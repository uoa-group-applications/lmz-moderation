package nz.ac.auckland.lmz.moderation.create

import nz.ac.auckland.lmz.errors.PersistException
import nz.ac.auckland.lmz.moderation.*
import nz.ac.auckland.lmz.persist.Identifiable
import nz.ac.auckland.lmz.service.Saviour
import org.junit.After
import org.junit.Test

/**
 * Unit tests for {@link ModerationCreateService}.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
public class ModerationCreateServiceTest {

    ModerationCreateService target;
    List<String> expectedMethodCalls;

    @Test(expected = ModerationException)
    public void createWontCatchVerificationError() throws Exception {
        final ModeratableTest inputTarget = new ModeratableTest();

        target = [

                verifyTarget: { Moderatable target ->
                    assert expectedMethodCalls.remove('verifyTarget');
                    assert target == inputTarget;
                    throw new ModerationException('', [:]);
                }

        ] as ModerationCreateService;

        expectedMethodCalls = [ 'verifyTarget' ];

        target.create(inputTarget, '');
    }

    @Test(expected = PersistException)
    public void createWontCatchPersistError() throws Exception {
        final ModeratableTest inputTarget = new ModeratableTest();
        final String inputEditorUpi = '';

        target = [

                verifyTarget: { Moderatable target ->
                    assert expectedMethodCalls.remove('verifyTarget');
                    assert target == inputTarget;
                },

                updateTarget: { Moderatable target, String editorUpi ->
                    assert expectedMethodCalls.remove('updateTarget');
                    assert target == inputTarget;
                    assert editorUpi == inputEditorUpi;
                },

                entityInsert: { Moderatable target ->
                    assert expectedMethodCalls.remove('entityInsert');
                    assert target == inputTarget;
                    throw new PersistException('', [:]);
                }

        ] as ModerationCreateService;

        expectedMethodCalls = [
                'verifyTarget',
                'updateTarget',
                'entityInsert'
        ];

        target.create(inputTarget, inputEditorUpi);
    }

    @Test
    public void createReturnsCreationResult() throws Exception {
        final ModeratableTest inputTarget = new ModeratableTest();
        final String inputEditorUpi = '';
        final ModerationCallbacks callbacks = [

                beforePersist: { Moderatable target ->
                    assert expectedMethodCalls.remove('beforePersist');
                    assert target == inputTarget;
                },

                afterPersist: { Moderatable target ->
                    assert expectedMethodCalls.remove('afterPersist');
                    assert target == inputTarget;
                }

        ] as ModerationCallbacks;

        target = [

                verifyTarget: { Moderatable target ->
                    assert expectedMethodCalls.remove('verifyTarget');
                    assert target == inputTarget;
                },

                updateTarget: { Moderatable target, String editorUpi ->
                    assert expectedMethodCalls.remove('updateTarget');
                    assert target == inputTarget;
                    assert editorUpi == inputEditorUpi;
                },

		        entityInsert: { Moderatable target ->
                    assert expectedMethodCalls.remove('entityInsert');
                    assert target == inputTarget;
                }

        ] as ModerationCreateService;

        expectedMethodCalls = [
                'verifyTarget',
                'updateTarget',
                'beforePersist',
                'entityInsert',
                'afterPersist'
        ];

        assert target.create(inputTarget, inputEditorUpi, callbacks) == inputTarget;
    }

    @Test(expected = ModerationException)
    public void verifyTargetForbidsNullInput() throws Exception {
        target = new ModerationCreateService();

        expectedMethodCalls = [];

        target.verifyTarget(null as Moderatable);
    }

    @Test(expected = ModerationException)
    public void verifyTargetForbidsNonIdentifiableInput() throws Exception {
        target = new ModerationCreateService();

        expectedMethodCalls = [];

        target.verifyTarget([:] as Moderatable);
    }

    @Test(expected = ModerationException)
    public void verifyTargetForbidsEntityWithId() throws Exception {
        final ModeratableTest inputTarget = new ModeratableTest(id: 1L);

        target = new ModerationCreateService();

        expectedMethodCalls = [];

        target.verifyTarget(inputTarget);
    }

    @Test
    public void verifyTargetAllowsCompliantInput() throws Exception {
        final ModeratableTest inputTarget = new ModeratableTest();

        target = new ModerationCreateService();

        expectedMethodCalls = [];

        target.verifyTarget(inputTarget);
    }

    @Test
    public void updateTargetPrepsObject() throws Exception {
        final Moderatable inputTarget = new ModeratableTest(
                modStatus: null,
                modSubmitter: null,
                modAdmin: 'something',
                modComment: 'something',
                modReference: new ModeratableTest()
        );
        final String inputEditorUpi = '';
        final boolean outputModerationDisabled = false;

        target = [

                isModerationDisabled: { ->
                    assert expectedMethodCalls.remove('isModerationDisabled');
                    return outputModerationDisabled;
                }

        ] as ModerationCreateService;

        expectedMethodCalls = [ 'isModerationDisabled' ];

        target.updateTarget(inputTarget, inputEditorUpi);

        assert inputTarget.modStatus == ModerationStatus.PENDING.name();
        assert inputTarget.modSubmitter == inputEditorUpi;
        assert inputTarget.modAdmin == null;
        assert inputTarget.modComment == null;
        assert inputTarget.modReference == null;
    }

    @Test(expected = PersistException)
    public void entityInsertWontCatchPersistenceException() throws Exception {

        final Moderatable inputTarget = [:] as Moderatable;

        target = new ModerationCreateService(saviour: [

		        insert: { def item, boolean flush ->
			        assert expectedMethodCalls.remove('insert');
			        assert item == inputTarget;
			        assert flush;
			        throw new PersistException('', [:]);
		        }

        ] as Saviour);

        expectedMethodCalls = [ 'insert' ];

        target.entityInsert(inputTarget);
    }

    @Test
    public void isModerationDisabledReturnsConfigValue() throws Exception {
        final boolean outputDisabled = true;

        target = new ModerationCreateService(config: [

                getModerationDisabled: { ->
                    expectedMethodCalls.remove('getModerationDisabled');
                    return outputDisabled;
                }

        ] as ModerationConfig);

        expectedMethodCalls = [ 'getModerationDisabled' ];

        assert target.moderationDisabled == outputDisabled;
    }

    @After
    public void tearDown() throws Exception {
        assert expectedMethodCalls.empty;
    }

    private class ModeratableTest implements Identifiable<Long>, Moderatable<ModeratableTest> {
        Long id;
        String modSubmitter;
        String modAdmin;
        String modComment;
        ModeratableTest modReference;
        String modStatus;
    }
}
