package cz.radovanmoncek.test.integration.suite;

import cz.radovanmoncek.test.integration.modules.games.codecs.GameStateFlatBuffersEncoderTest;
import cz.radovanmoncek.test.integration.modules.games.codecs.RequestFlatBuffersDecoderTest;
import cz.radovanmoncek.test.integration.modules.games.handlers.ExampleGameSessionChannelGroupHandlerTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Integration")
@SelectClasses({
        GameStateFlatBuffersEncoderTest.class,
        RequestFlatBuffersDecoderTest.class,
        ExampleGameSessionChannelGroupHandlerTest.class
})
public class ExampleNettgameServerIntegrationTestSuite {
}
