package nz.ac.auckland.lmz.moderation

/**
 * A list of all the possible statuses a {@link Moderatable} entity can be in.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
public enum ModerationStatus {
    PENDING,
    DECLINED,
    APPROVED,
    PUBLISHED,
    UNPUBLISHED;

    public boolean is(Moderatable entity) {
        return is(entity, this);
    }

    public void set(Moderatable entity) {
        set(entity, this);
    }

    public static ModerationStatus get(Moderatable entity) {
        return entity.modStatus ?
                    ModerationStatus.valueOf(entity.modStatus?.toUpperCase())
                :
                    UNPUBLISHED;
    }

    public static void set(Moderatable entity, ModerationStatus status) {
        entity.modStatus = status.name();
    }

    public static boolean is(Moderatable entity, ModerationStatus status) {
        return get(entity) == status;
    }

}