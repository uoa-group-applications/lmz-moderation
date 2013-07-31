package nz.ac.auckland.lmz.moderation.create

import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.lmz.ClassUtils
import nz.ac.auckland.lmz.errors.PersistException
import nz.ac.auckland.lmz.moderation.*
import nz.ac.auckland.lmz.persist.Identifiable
import nz.ac.auckland.lmz.service.Saviour

import javax.inject.Inject

/**
 * This is the primary implementation of {@link ModerationCreate}.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
//@CompileStatic
@UniversityComponent
public class ModerationCreateService implements ModerationCreate {

    @Inject Saviour saviour;
    @Inject ModerationConfig config;

    @Override
    public <Entity extends Moderatable<Entity>> Entity create(
            Entity target,
            String submitterUpi,
            ModerationCallbacks callbacks = null
    ) throws ModerationException {

        verifyTarget(target);

        updateTarget(target, submitterUpi);

        callbacks?.beforePersist(target);

        entityInsert(target);

        callbacks?.afterPersist(target);

        return target;
    }

    protected void verifyTarget(Moderatable target) throws ModerationException {

        if (!target || !Identifiable.isAssignableFrom(target.class)) {
            throw new ModerationException('moderate.create.verify.missing', [
                    targetType: target?.class?.simpleName
            ]);
        }

        if (retrieveId(target)) {
            throw new ModerationException('moderate.create.verify.exists', [
                    targetId: retrieveId(target),
                    targetType: target.class.simpleName
            ]);
        }

    }

    protected void updateTarget(Moderatable target, String editorUpi) {
        target.modSubmitter = editorUpi;
        target.modAdmin = null;
        target.modReference = null;

        if (moderationDisabled) {
            ModerationStatus.PUBLISHED.set(target);
            target.modComment = 'Moderation overridden.';
        } else {
            ModerationStatus.PENDING.set(target);
            target.modComment = null;
        }
    }

    protected void entityInsert(Moderatable target) throws PersistException {
        saviour.insert(target, true);
    }

    protected boolean isModerationDisabled() {
        return config.moderationDisabled;
    }

	protected Serializable retrieveId(Moderatable target) {
		return ClassUtils.safeCast(target, Identifiable)?.id;
	}

}
