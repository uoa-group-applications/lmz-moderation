package nz.ac.auckland.lmz.moderation

import groovy.transform.CompileStatic
import nz.ac.auckland.common.stereotypes.UniversityComponent
import org.springframework.beans.factory.annotation.Value;

/**
 * This class centralises configuration property access for the module.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
@CompileStatic
@UniversityComponent
public class ModerationConfig {

    @Value('${moderation.disabled:false}')
    Boolean moderationDisabled;

}
