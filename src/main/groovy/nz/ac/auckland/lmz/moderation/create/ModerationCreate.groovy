package nz.ac.auckland.lmz.moderation.create

import groovy.transform.CompileStatic
import nz.ac.auckland.lmz.moderation.Moderatable
import nz.ac.auckland.lmz.moderation.ModerationCallbacks
import nz.ac.auckland.lmz.moderation.ModerationException

/**
 * This interface defines the methods required to perform a creation operation on a {@link Moderatable} entity.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
@CompileStatic
public interface ModerationCreate {

    public <Entity extends Moderatable<Entity>> Entity create(
            Entity target,
            String submitterUpi
    ) throws ModerationException;

    public <Entity extends Moderatable<Entity>> Entity create(
            Entity target,
            String submitterUpi,
            ModerationCallbacks callbacks
    ) throws ModerationException;

}