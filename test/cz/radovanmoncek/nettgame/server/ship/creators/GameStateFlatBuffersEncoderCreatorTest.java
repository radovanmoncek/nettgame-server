package cz.radovanmoncek.nettgame.server.ship.creators;

import cz.radovanmoncek.nettgame.server.modules.games.codecs.GameStateFlatBuffersEncoder;
import cz.radovanmoncek.nettgame.server.ship.engine.creators.GameStateFlatBuffersEncoderCreator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateFlatBuffersEncoderCreatorTest {

    @Test
    void newProduct() {

        assertInstanceOf(GameStateFlatBuffersEncoder.class, new GameStateFlatBuffersEncoderCreator().newProduct());
    }
}