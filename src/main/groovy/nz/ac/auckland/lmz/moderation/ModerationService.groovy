package nz.ac.auckland.lmz.moderation

import groovy.transform.CompileStatic
import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.lmz.moderation.action.ModerationAction
import nz.ac.auckland.lmz.moderation.create.ModerationCreate
import nz.ac.auckland.lmz.moderation.delete.ModerationDelete
import nz.ac.auckland.lmz.moderation.manage.ModerationManage
import nz.ac.auckland.lmz.moderation.update.ModerationUpdate

import javax.inject.Inject

/**
 * This service handles all moderation-related operations on {@link Moderatable Moderatable} entities. It also simply
 * passes-through to the individual operation service implementations.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 * @see ModerationCreate
 * @see ModerationUpdate
 * @see ModerationDelete
 * @see ModerationAction
 */
@CompileStatic
@UniversityComponent
public class ModerationService implements Moderation {

    @Inject ModerationCreate create;
    @Inject ModerationUpdate update;
    @Inject ModerationDelete delete;
    @Inject ModerationAction action;
    @Inject ModerationManage manage;

    @Override
    public <Entity extends Moderatable<Entity>> Entity create(
            Entity target,
            String submitterUpi,
            ModerationCallbacks callbacks = null
    ) throws ModerationException {
        create.create(target, submitterUpi, callbacks);
    }

    @Override
    public void update(
            Moderatable target,
            Moderatable candidate,
            String submitterUpi,
            ModerationCallbacks callbacks = null
    ) throws ModerationException {
        update.update(target, candidate, submitterUpi, callbacks);
    }

    @Override
    public void delete(
		    Class<? extends Moderatable> targetType,
		    Serializable targetId,
            String submitterUpi,
            ModerationCallbacks callbacks = null
    ) throws ModerationException {
        delete.delete(targetType, targetId, submitterUpi, callbacks);
    }

	@Override
	public void approve(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String moderatorUpi,
			ModerationCallbacks callbacks = null
	) throws ModerationException {
		action.approve(targetType, targetId, moderatorUpi, callbacks);
	}

	@Override
	public void decline(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String justification,
			String moderatorUpi,
			ModerationCallbacks callbacks = null
	) throws ModerationException {
		action.decline(targetType, targetId, justification, moderatorUpi, callbacks);
	}

	@Override
	public void publish(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String adminUpi,
			ModerationCallbacks callbacks = null
	) throws ModerationException {
		manage.publish(targetType, targetId, adminUpi, callbacks);
	}

	@Override
	public void unpublish(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String submitterUpi,
			ModerationCallbacks callbacks = null
	) throws ModerationException {
		manage.unpublish(targetType, targetId, submitterUpi, callbacks);
	}

}
