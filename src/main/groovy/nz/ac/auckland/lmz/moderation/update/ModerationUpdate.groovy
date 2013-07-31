package nz.ac.auckland.lmz.moderation.update

import groovy.transform.CompileStatic
import nz.ac.auckland.lmz.moderation.Moderatable
import nz.ac.auckland.lmz.moderation.ModerationCallbacks
import nz.ac.auckland.lmz.moderation.ModerationException

/**
 * This interface defines the methods required to perform an update operation on a {@link Moderatable} entity.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
@CompileStatic
public interface ModerationUpdate {

	public void update(
			Moderatable target,
			Moderatable candidate,
			String submitterUpi
	) throws ModerationException;

	public void update(
			Moderatable target,
			Moderatable candidate,
			String submitterUpi,
			ModerationCallbacks callbacks
	) throws ModerationException;

}