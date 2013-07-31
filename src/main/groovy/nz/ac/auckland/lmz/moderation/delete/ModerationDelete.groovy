package nz.ac.auckland.lmz.moderation.delete

import groovy.transform.CompileStatic
import nz.ac.auckland.lmz.moderation.Moderatable
import nz.ac.auckland.lmz.moderation.ModerationCallbacks
import nz.ac.auckland.lmz.moderation.ModerationException

/**
 * This interface defines the methods required to perform a deletion operation on a {@link Moderatable} entity.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
@CompileStatic
public interface ModerationDelete {

	/**
	 * Exactly the same as {@link #delete(Class, Serializable, String, ModerationCallbacks)}, but the callbacks are
	 * omitted.
	 * @throws ModerationException
	 */
	public void delete(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String submitterUpi
	) throws ModerationException;

	/**
	 * Will enter the provided entity into a moderation workflow that determines whether it gets soft-deleted.
	 * @param targetType the type of the target to delete.
	 * @param targetId The persistence id of the target to delete.
	 * @param submitterUpi The UPI of the user who submitted the request.
	 * @param callbacks A set of potential callback hooks that can be used to manipulate app-specific data.
	 * @throws ModerationException
	 */
	public void delete(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String submitterUpi,
			ModerationCallbacks callbacks
	) throws ModerationException;

}
