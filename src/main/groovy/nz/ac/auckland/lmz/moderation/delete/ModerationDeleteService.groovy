package nz.ac.auckland.lmz.moderation.delete

import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.lmz.errors.PersistException
import nz.ac.auckland.lmz.moderation.*
import nz.ac.auckland.lmz.persist.Enableable
import nz.ac.auckland.lmz.service.Saviour

import javax.inject.Inject

/**
 * This is the primary implementation of {@link ModerationDelete}.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
//@CompileStatic
@UniversityComponent
public class ModerationDeleteService implements ModerationDelete {

    @Inject ModerationConfig config;
    @Inject ModerationUtils utils;
    @Inject Saviour saviour;

    @Override
    public void delete(
		    Class<? extends Moderatable> targetType,
		    Serializable targetId,
            String submitterUpi,
            ModerationCallbacks callbacks = null
    ) throws ModerationException {
		withTarget(targetType, targetId) { Moderatable target ->

			// Validate the target entity.
			verifyTargetExists(target);
			verifyTargetDraft(target);

			// Generate the draft submission with changes.
			Moderatable draft = generateDraft(target, submitterUpi, callbacks);

			callbacks?.beforePersist(draft);

			// Save the draft to the database.
			persistEntity(draft);

			callbacks?.afterPersist(draft);

		}
    }

	protected void verifyTargetExists(Moderatable target) throws ModerationException {
		saviour.ensureExists(target, false) { PersistException ex ->
			return new ModerationException('moderate.delete.missing', ex.context, ex);
		}
	}

	protected void verifyTargetDraft(Moderatable target) throws ModerationException {
		utils.verifyDraftStatus(target);
	}

	protected Moderatable generateDraft(Moderatable target, String editorUpi, ModerationCallbacks callbacks) {

        final Moderatable draft;

        if (moderationDisabled) {

            // Just use the target entity.
            draft = target;

            // Always leave a note.
            draft.modComment = ModerationUtils.MSG_BYPASS;

        } else {

            // construct a new draft entity instance.
            draft = target.class.newInstance();

            callbacks?.beforeCopy(draft, target);

            // Copy across the publishable data from the target.
            performPublish(target, draft);

            callbacks?.afterCopy(draft, target);

            // Update relevant properties
            draft.modReference = target;

            ModerationStatus.PENDING.set(draft);
        }

        (draft as Enableable).enabled = false;

        draft.modAdmin = null;
        draft.modSubmitter = editorUpi;

        return draft;
    }

	protected boolean isModerationDisabled() {
		return config.moderationDisabled;
	}

	protected void persistEntity(Moderatable entity) {
		if (moderationDisabled) {
			saviour.update(entity, true);
		} else {
			saviour.insert(entity, true);
		}
	}

	protected void performPublish(Moderatable source, Moderatable target) {
		utils.publish(source, target);
	}

	protected void withTarget(Class<? extends Moderatable> type, Serializable id, Closure callback) {
		callback.call(saviour.find(type, id));
	}

}
