package nz.ac.auckland.lmz.moderation

import groovy.transform.CompileStatic
import nz.ac.auckland.lmz.moderation.action.ModerationAction
import nz.ac.auckland.lmz.moderation.create.ModerationCreate
import nz.ac.auckland.lmz.moderation.delete.ModerationDelete
import nz.ac.auckland.lmz.moderation.manage.ModerationManage
import nz.ac.auckland.lmz.moderation.update.ModerationUpdate

/**
 * The Moderation service provides a method for safely processing {@link Moderatable} entities in a lmz
 * application.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
@CompileStatic
public interface Moderation extends
        ModerationCreate,
        ModerationUpdate,
        ModerationDelete,
        ModerationAction,
        ModerationManage {

    // no method definitions required here.

}