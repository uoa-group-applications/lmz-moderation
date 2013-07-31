package nz.ac.auckland.lmz.moderation

import nz.ac.auckland.common.testrunner.GroupAppsSpringTestRunner
import nz.ac.auckland.lmz.moderation.action.ModerationActionService
import nz.ac.auckland.lmz.moderation.create.ModerationCreateService
import nz.ac.auckland.lmz.moderation.delete.ModerationDeleteService
import nz.ac.auckland.lmz.moderation.manage.ModerationManageService
import nz.ac.auckland.lmz.moderation.update.ModerationUpdateService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration

import javax.inject.Inject;

/**
 * This test is specifically designed to run up the application context, make sure Spring is doing everything it's
 * supposed to, then stop. It shouldn't be asserting anything more than this module won't blow up dependent modules
 * when trying to wire up spring components.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
@RunWith(GroupAppsSpringTestRunner)
@ContextConfiguration('classpath:context-springonly.xml')
public class SpringContextTest {

    // nz.ac.auckland.lmz.moderation.action
    @Inject ModerationActionService moderationActionService;

    // nz.ac.auckland.lmz.moderation.create
    @Inject ModerationCreateService moderationCreateService;

    // nz.ac.auckland.lmz.moderation.delete
    @Inject ModerationDeleteService moderationDeleteService;

    // nz.ac.auckland.lmz.moderation.manage
    @Inject ModerationManageService moderationManageService;

    // nz.ac.auckland.lmz.moderation.update
    @Inject ModerationUpdateService moderationUpdateService;

    // nz.ac.auckland.lmz.moderation
    @Inject ModerationService moderationService;
    @Inject ModerationConfig moderationConfig;
    @Inject ModerationUtils moderationUtils;

    @Test
    public void everythingGetsInjected() throws Exception {
        assert true;
    }
}
