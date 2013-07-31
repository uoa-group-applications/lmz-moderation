package nz.ac.auckland.lmz.moderation.manage

import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.lmz.ClassUtils
import nz.ac.auckland.lmz.errors.PersistException
import nz.ac.auckland.lmz.moderation.*
import nz.ac.auckland.lmz.persist.Identifiable
import nz.ac.auckland.lmz.service.Saviour

import javax.inject.Inject

/**
 * This is the primary implementation of {@link ModerationManage}.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
//@CompileStatic
@UniversityComponent
public class ModerationManageService implements ModerationManage {

	@Inject Saviour saviour;
	@Inject ModerationConfig config;
	@Inject ModerationUtils utils;

	@Override
	public void publish(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String adminUpi,
			ModerationCallbacks callbacks = null
	) throws ModerationException {
		withTarget(targetType, targetId) { Moderatable target ->
			verifyTarget(target, true);
			updateTarget(target, adminUpi, true);
			persistTarget(target, callbacks);
		}
	}

	@Override
	public void unpublish(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String adminUpi,
			ModerationCallbacks callbacks = null
	) throws ModerationException {
		withTarget(targetType, targetId) { Moderatable target ->
			verifyTarget(target, false);
			updateTarget(target, adminUpi, false);
			persistTarget(target, callbacks);
		}
	}

	protected void withTarget(Class<? extends Moderatable> type, Serializable id, Closure callback) {
		callback.call(saviour.find(type, id));
	}

	protected void verifyTarget(Moderatable target, boolean publish) {

		// Start off the error context with the operation being performed.
		Map context = [operation: publish ? 'publish' : 'unpublish'];

		// Make sure that the target exists, and isn't disabled.
		saviour.ensureExists(target, false) { PersistException ex ->
			return new ModerationException('moderation.manage.missing', context, ex);
		}

		verifyTargetStatus(publish, target);

		verifyTargetDraft(target, publish)

		// Everything appears to be fine.
	}

	protected void verifyTargetDraft(Moderatable target, boolean publish) throws ModerationException {
		Moderatable existingDraft = findExistingDraft(target);

		if (existingDraft) {
			throw new ModerationException('moderate.delete.verify.existing', [
					targetId: retrieveId(target),
					targetType: target.class.simpleName,
					operation: publish ? 'publish' : 'unpublish',
					targetDraftId: retrieveId(existingDraft)
			]);
		}
	}

	/** Make sure that the target is in the correct status for the given operation. */
	protected void verifyTargetStatus(boolean publish, Moderatable target) throws ModerationException {
		if (publish && !ModerationStatus.UNPUBLISHED.is(target)) {
			throw new ModerationException('moderation.manage.invalid', [
					targetId: retrieveId(target),
					targetType: target.class.simpleName,
					operation: publish ? 'publish' : 'unpublish',
					targetStatus: target.modStatus
			]);
		} else if (!publish && !ModerationStatus.PUBLISHED.is(target)) {
			throw new ModerationException('moderation.manage.invalid', [
					targetId: retrieveId(target),
					targetType: target.class.simpleName,
					operation: 'unpublish',
					targetStatus: target.modStatus
			]);
		}
	}

	protected void updateTarget(Moderatable target, String adminUpi, boolean publish) {

		if (publish) {
			ModerationStatus.PUBLISHED.set(target);
		} else {
			ModerationStatus.UNPUBLISHED.set(target);
		}

		// Admin is both submitter and approver.
		target.modAdmin = adminUpi;
		target.modSubmitter = adminUpi;

	}

	protected void persistTarget(Moderatable target, ModerationCallbacks callbacks) {
		callbacks?.beforePersist(target);

		saviour.update(target, true);

		callbacks?.afterPersist(target);
	}

	protected Serializable retrieveId(Moderatable moderatable) {
		return ClassUtils.safeCast(moderatable, Identifiable)?.id;
	}

	protected Moderatable findExistingDraft(Moderatable published) throws PersistException {
		return utils.findExistingDraft(published);
	}

}
