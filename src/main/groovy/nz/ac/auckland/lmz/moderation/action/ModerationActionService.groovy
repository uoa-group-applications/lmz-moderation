package nz.ac.auckland.lmz.moderation.action

import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.lmz.ClassUtils
import nz.ac.auckland.lmz.LogUtils
import nz.ac.auckland.lmz.errors.PersistException
import nz.ac.auckland.lmz.moderation.*
import nz.ac.auckland.lmz.persist.Identifiable
import nz.ac.auckland.lmz.service.Saviour
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject

/**
 * This is the primary implementation of {@link ModerationAction}.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
//@CompileStatic
@UniversityComponent
public class ModerationActionService implements ModerationAction {
	private static final Logger LOG = LoggerFactory.getLogger(ModerationActionService);

	@Inject Saviour saviour;
	@Inject ModerationUtils utils;

	@Override
	public void approve(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String moderatorUpi,
			ModerationCallbacks callbacks = null
	) throws ModerationException {

		Moderatable candidate = findModeratable(targetType, targetId);

		verifyCandidate(candidate);
		updateApprove(candidate, moderatorUpi);
		persistCandidate(candidate, callbacks);

		LOG.info LogUtils.format('Moderation candidate has been approved', [
				candidateId: targetId
		]);

		Moderatable published = getPublished(candidate);

		updatePublished(candidate, published, callbacks);
		performPublish(candidate, published, callbacks);

		LOG.info LogUtils.format('Moderation candidate data has been published', [
				candidateId: ClassUtils.safeCast(candidate, Identifiable)?.id,
				publishedId: ClassUtils.safeCast(published, Identifiable)?.id
		]);

	}

	@Override
	public void decline(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String justification,
			String moderatorUpi,
			ModerationCallbacks callbacks = null
	) throws ModerationException {

		Moderatable candidate = findModeratable(targetType, targetId);

		verifyCandidate(candidate);
		updateDecline(candidate, justification, moderatorUpi);
		persistCandidate(candidate, callbacks);

		LOG.info LogUtils.format('Moderation candidate has been declined', [
				candidateId: targetId,
				comment: justification
		]);

	}

	protected Moderatable findModeratable(Class<? extends Moderatable> targetType, Serializable targetId) {
		return saviour.find(targetType, targetId);
	}

	protected void persistCandidate(Moderatable candidate, ModerationCallbacks callbacks) {
		callbacks?.beforePersist(candidate);

		saviour.update(candidate, true);

		callbacks?.afterPersist(candidate);
	}

	protected void persistPublished(Moderatable published, ModerationCallbacks callbacks) {
		callbacks?.beforePersist(published);

		if ((published as Identifiable).id) {
			saviour.update(published, true);
		} else {
			saviour.insert(published);
		}

		callbacks?.afterPersist(published);
	}

	/**
	 * Checks for issues that would prevent the moderation process from being possible, such as a missing candidate, or
	 * the candidate already having been moderated.
	 * @param candidate The candidate to verify
	 */
	protected void verifyCandidate(Moderatable candidate) throws ModerationException {

		// make sure the candidate isn't null or disabled
		saviour.ensureExists(candidate, true) { PersistException e ->
			return new ModerationException('moderation.verify.missing', [
					entityId: ClassUtils.safeCast(candidate, Identifiable)?.id,
					entityStatus: candidate ? 'disabled' : 'missing'
			], e);
		};

		// Make sure the entity has not already been moderated
		if (!ModerationStatus.PENDING.is(candidate)) {
			throw new ModerationException('moderation.verify.alreadydone', [
					entityId: ClassUtils.safeCast(candidate, Identifiable)?.id,
					entityStatus: candidate.modStatus
			]);
		}

	}

	protected void updateApprove(
			Moderatable candidate,
			String moderatorUpi
	) throws ModerationException {

		candidate.modAdmin = moderatorUpi;
		candidate.modComment = null;
		ModerationStatus.APPROVED.set(candidate);

	}

	protected void updateDecline(
			Moderatable candidate,
			String justification,
			String moderatorUpi
	) throws PersistException {

		candidate.modAdmin = moderatorUpi;
		candidate.modComment = justification;
		ModerationStatus.DECLINED.set(candidate);

	}

	/**
	 * This method just takes a moderation candidate, and returns it's reference to the published version, or a new
	 * instance of itself if the former does not exist.
	 *
	 * @param candidate The moderation candidate to get the published version for.
	 * @return The published version of the moderation candidate, or a new instance of the target class.
	 */
	protected Moderatable getPublished(Moderatable candidate) {
		return candidate.modReference ?: candidate.class.newInstance(
				modStatus: ModerationStatus.PUBLISHED.name()
		);
	}

	/**
	 * This method updates the published version with all the changes, then validates to see if there are any errors.
	 * If there are validation errors, the moderation is automatically declined, with the errors as the reason.
	 * @param candidate The moderation candidate.
	 * @param published The published version of the candidate.
	 * @param callbacks A bunch of callbacks for events that might fire.
	 * @return A response map if an error occurs, or null if everything is successful.
	 * @throws ModerationException
	 */
	protected void updatePublished(
			Moderatable candidate,
			Moderatable published,
			ModerationCallbacks callbacks
	) throws ModerationException {
		callbacks.beforeCopy(published, candidate);

		// Copy all the correct properties across.
		utils.publish(candidate, published);

		callbacks.afterCopy(published, candidate);
	}

	/**
	 * This method persists the published version to the database, and deletes the candidate if successful.
	 * @param candidate The moderation candidate to publish.
	 * @param published The published version of the candidate (auto-injected).
	 * @param callbacks A bunch of callbacks for events that might fire.
	 * @throws ModerationException
	 */
	protected void performPublish(
			Moderatable candidate,
			Moderatable published,
			ModerationCallbacks callbacks
	) throws ModerationException {

		try {
			persistPublished(published, callbacks);
		} catch (PersistException e) {
			failureCleanup(e, candidate, callbacks);
		}

		// Get rid of the successful candidate
		saviour.delete(candidate, true);

	}

	protected void failureCleanup(
			PersistException error,
			Moderatable candidate,
			ModerationCallbacks callbacks
	) throws ModerationException {

		// Make sure the candidate is up-to-date.
		saviour.refresh(candidate);

		// Automatically decline the moderation, citing the validation errors.
		candidate.modComment = 'moderation.publish.failed';
		ModerationStatus.DECLINED.set(candidate);

		// Save the validation errors as the decline reason.
		persistCandidate(candidate, callbacks);

		// Render the database persist failure message as a response
		throw new ModerationException('moderation.perform-copy.invalid', error.context, error);

	}

}
