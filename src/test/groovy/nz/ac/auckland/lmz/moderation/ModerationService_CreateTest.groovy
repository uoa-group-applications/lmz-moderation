package nz.ac.auckland.lmz.moderation

import groovy.transform.CompileStatic
import nz.ac.auckland.lmz.moderation.ModerationException
import nz.ac.auckland.lmz.moderation.create.ModerationCreate
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

/**
 * Tests for {@link ModerationService#create}.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
@CompileStatic
public class ModerationService_CreateTest {

    ModerationService target;

    Moderatable input1;
    String input2;
    ModerationCallbacks input3;
    ModerationException exception;

    @Before
    public void setUp() throws Exception {
        target = new nz.ac.auckland.lmz.moderation.ModerationService();

        input1 = Mockito.mock(Moderatable);
        input2 = 'submitterUPI';
        input3 = Mockito.mock(ModerationCallbacks);
        exception = Mockito.mock(ModerationException);
    }

    @Test
    public void passesCreateThroughWithoutCallbacks() throws Exception {
        mockCreate { Moderatable target, String submitterUpi, ModerationCallbacks callbacks = null ->
            assert target == input1;
            assert submitterUpi == input2;
            assert callbacks == null;
        }

        target.create(input1, input2);
    }

    @Test
    public void passesCreateThroughWithCallbacks() throws Exception {
        mockCreate { Moderatable target, String submitterUpi, ModerationCallbacks callbacks = null ->
            assert target == input1;
            assert submitterUpi == input2;
            assert callbacks == input3;
        }

        target.create(input1, input2, input3);
    }

    @Test(expected = ModerationException)
    public void passesThroughThrownExceptions() throws Exception {
        mockCreate { Moderatable target, String submitterUpi, ModerationCallbacks callbacks = null ->
            throw exception;
        }

        target.create(input1, input2);
    }

    private void mockCreate(Closure closure) {
        target.create = [ create: closure ] as ModerationCreate;
    }

}
