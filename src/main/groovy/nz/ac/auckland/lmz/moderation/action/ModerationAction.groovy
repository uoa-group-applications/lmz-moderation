package nz.ac.auckland.lmz.moderation.action

import groovy.transform.CompileStatic
import nz.ac.auckland.lmz.moderation.Moderatable
import nz.ac.auckland.lmz.moderation.ModerationCallbacks
import nz.ac.auckland.lmz.moderation.ModerationException

/**
 * This interface defines the methods required to perform a workflow action on a {@link Moderatable} entity.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
@CompileStatic
public interface ModerationAction {

	/**
	 * Finds the specified {@link Moderatable} entity and approves the requested change.
	 * @param targetType The class of the entity to moderate.
	 * @param targetId The id of the entity to moderate.
	 * @param moderatorUpi The UPI of the moderator performing this operation.
	 * @param callbacks A set of event hooks which may be called during the process.
	 * @throws ModerationException
	 */
	public void approve(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String moderatorUpi,
			ModerationCallbacks callbacks
	) throws ModerationException;

	/** Short form of {@link #approve(Class, Serializable, String, ModerationCallbacks)}. */
	public void approve(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String moderatorUpi
	) throws ModerationException;

	/**
	 * Finds the specified {@link Moderatable} entity and declines the requested change.
	 * @param targetType The class of the entity to moderate.
	 * @param targetId The id of the entity to moderate.
	 * @param justification The justification for declining the requested change.
	 * @param moderatorUpi The UPI of the moderator performing this operation.
	 * @param callbacks A set of event hooks which may be called during the process.
	 * @throws ModerationException
	 */
	public void decline(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String justification,
			String moderatorUpi,
			ModerationCallbacks callbacks
	) throws ModerationException;

	/** Short form of {@link #decline(Class, Serializable, String, String, ModerationCallbacks)}. */
	public void decline(
			Class<? extends Moderatable> targetType,
			Serializable targetId,
			String justification,
			String moderatorUpi
	) throws ModerationException;

}
