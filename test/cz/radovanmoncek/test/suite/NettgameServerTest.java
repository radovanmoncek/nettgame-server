package cz.radovanmoncek.test.suite;

import cz.radovanmoncek.test.modules.games.codecs.GameStateFlatBuffersEncoderTest;
import cz.radovanmoncek.test.modules.games.codecs.RequestFlatBuffersDecoderTest;
import cz.radovanmoncek.test.modules.games.handlers.MyGameSessionChannelGroupHandlerTest;
import cz.radovanmoncek.test.modules.games.models.GameHistoryEntityTest;
import cz.radovanmoncek.test.modules.games.models.GameStateFlatBuffersSerializableTest;
import cz.radovanmoncek.test.ship.creators.MyGameSessionHandlerCreatorTest;
import cz.radovanmoncek.test.ship.creators.GameStateFlatBuffersEncoderCreatorTest;
import cz.radovanmoncek.test.ship.creators.RequestFlatBuffersDecoderCreatorTest;
import cz.radovanmoncek.test.ship.launcher.NettgameServerLauncherTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Integration")
@SelectClasses({
        GameStateFlatBuffersEncoderTest.class,
        RequestFlatBuffersDecoderTest.class,
        MyGameSessionChannelGroupHandlerTest.class,
            NettgameServerLauncherTest.class,
            MyGameSessionHandlerCreatorTest.class,
            GameStateFlatBuffersEncoderCreatorTest.class,
            RequestFlatBuffersDecoderCreatorTest.class,
            GameStateFlatBuffersSerializableTest.class,
            GameHistoryEntityTest.class
    })
    public  class NettgameServerTest {
}
