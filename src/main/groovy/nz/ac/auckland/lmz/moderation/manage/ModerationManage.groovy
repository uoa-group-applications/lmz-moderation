package nz.ac.auckland.lmz.moderation.manage

import groovy.transform.CompileStatic
import nz.ac.auckland.lmz.moderation.Moderatable
import nz.ac.auckland.lmz.moderation.ModerationCallbacks
import nz.ac.auckland.lmz.moderation.ModerationException;

/**
 * This interface defines the methods required to manage a {@link Moderatable} entity's public visibility.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
@CompileStatic
public interface ModerationManage {

	public void publish(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String submitterUpi
	) throws ModerationException;

	public void publish(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String submitterUpi,
			ModerationCallbacks callbacks
	) throws ModerationException;

	public void unpublish(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String submitterUpi
	) throws ModerationException;

	public void unpublish(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String submitterUpi,
			ModerationCallbacks callbacks
	) throws ModerationException;

}
