package cz.radovanmoncek.test.unit.suite;

import cz.radovanmoncek.test.unit.modules.games.models.GameHistoryEntityTest;
import cz.radovanmoncek.test.unit.modules.games.models.GameStateFlatBuffersSerializableTest;
import cz.radovanmoncek.test.unit.ship.creators.ExampleGameSessionHandlerCreatorTest;
import cz.radovanmoncek.test.unit.ship.creators.GameStateFlatBuffersEncoderCreatorTest;
import cz.radovanmoncek.test.unit.ship.creators.RequestFlatBuffersDecoderCreatorTest;
import cz.radovanmoncek.test.unit.ship.launcher.NettgameServerLauncherTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Example nettgame server test suite")
@SelectClasses({
        NettgameServerLauncherTest.class,
        ExampleGameSessionHandlerCreatorTest.class,
        GameStateFlatBuffersEncoderCreatorTest.class,
        RequestFlatBuffersDecoderCreatorTest.class,
        GameStateFlatBuffersSerializableTest.class,
        GameHistoryEntityTest.class
})
public class ExampleNettgameServerUnitTestSuite {}
