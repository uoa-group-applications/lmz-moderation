package nz.ac.auckland.lmz.moderation.update

import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.lmz.ClassUtils
import nz.ac.auckland.lmz.errors.PersistException
import nz.ac.auckland.lmz.moderation.*
import nz.ac.auckland.lmz.persist.Identifiable
import nz.ac.auckland.lmz.service.Saviour

import javax.inject.Inject

/**
 * This is the primary implementation of {@link ModerationUpdate}.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
//@CompileStatic
@UniversityComponent
public class ModerationUpdateService implements ModerationUpdate {

    @Inject Saviour saviour;
    @Inject ModerationUtils utils;
    @Inject ModerationConfig config;

    @Override
    public void update(
            Moderatable published,
            Moderatable candidate,
            String editorUpi,
            ModerationCallbacks callbacks = null
    ) throws ModerationException {

	    // Make sure the update target is valid.
	    verifyTargetExists(published);
	    verifyTargetDraft(published);

	    // Make sure the draft candidate is valid.
        verifyCandidate(candidate);

        // Make sure the published is up-to-date.
	    refreshEntity(published);

	    // Generate the draft entity from the two other entities
        Moderatable draft = createDraft(published, candidate, editorUpi, callbacks);

        callbacks?.beforePersist(draft);

        // Persist the changes
	    persistEntity(draft);

        callbacks?.afterPersist(draft);
    }

	protected void persistEntity(Moderatable draft) {
		if (moderationDisabled) {
			saviour.update(draft, true);
		} else {
			saviour.insert(draft, true);
		}
	}

	protected void refreshEntity(Moderatable published) {
		saviour.refresh(published)
	}

	protected Moderatable createDraft(
            Moderatable published,
            Moderatable candidate,
            String editorUpi,
            ModerationCallbacks callbacks
    ) {
        final Moderatable draft = moderationDisabled ? published : published.class.newInstance();

        callbacks?.beforeCopy(draft, candidate);

        // Copy the data across
        utils.publish(published, draft);
        utils.publish(candidate, draft);

        callbacks?.afterCopy(draft, candidate);

        // Update relevant properties
        draft.modSubmitter = editorUpi;
        draft.modAdmin = null;

        if (moderationDisabled) {
            draft.modComment = ModerationUtils.MSG_BYPASS;
            ModerationStatus.PUBLISHED.set(draft);
        } else {
            draft.modComment = null;
            draft.modReference = published;
            ModerationStatus.PENDING.set(draft);
        }

        return draft;
    }

    protected boolean isModerationDisabled() {
        return config.moderationDisabled;
    }

	protected void verifyTargetExists(Moderatable target) {
		saviour.ensureExists(target, false) { PersistException ex ->
			return new ModerationException('moderation.update.missing', ex.context, ex);
		}
	}

	protected void verifyCandidate(Moderatable target) throws ModerationException {

	    if (!target) {
		    throw new ModerationException('moderation.update.candidate.missing', [:]);
	    }

	    if (retrieveId(target)) {
		    throw new ModerationException('moderation.update.candidate.existing', [
				    targetType: target.class.simpleName,
				    targetId: retrieveId(target)
		    ]);
	    }

    }

	protected void verifyTargetDraft(Moderatable target) throws ModerationException {
		utils.verifyDraftStatus(target);
	}

	protected Serializable retrieveId(Moderatable moderatable) {
		return ClassUtils.safeCast(moderatable, Identifiable)?.id;
	}

}
