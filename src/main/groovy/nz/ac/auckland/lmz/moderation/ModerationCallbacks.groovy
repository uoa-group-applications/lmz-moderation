package nz.ac.auckland.lmz.moderation;

/**
 * This class represents all the potential event hooks that can be called during various stages of the moderation
 * process.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
public abstract class ModerationCallbacks<Entity extends Moderatable<Entity>> {

	/**
	 * Called just before data is copied across from the entity requested to be changed, to the empty draft.
	 * @param copyTarget The empty draft to have data copied to it.
	 * @param copySource The entity requested to be deleted.
	 */
	public void beforeCopy(Entity copyTarget, Entity copySource) {
		// Do nothing unless overridden
	}

	/**
	 * Called immediately after data has been copied from the entity requested to be changed to the empty draft.
	 * @param copyTarget The draft, with freshly copied data from the source.
	 * @param copySource The source that provided the data copied to the target.
	 */
	public void afterCopy(Entity copyTarget, Entity copySource) {
		// Do nothing unless overridden
	}

	/**
	 * Called just before the draft is to be persisted to the database.
	 * @param toPersist The entity to be persisted.
	 */
	public void beforePersist(Entity toPersist) {
		// Do nothing unless overridden
	}

	/**
	 * Called immediately after the draft has been successfully persisted to the database.
	 * @param persisted The entity that has just been persisted.
	 */
	public void afterPersist(Entity persisted) {
		// Do nothing unless overridden
	}

}
