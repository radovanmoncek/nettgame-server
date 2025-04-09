package cz.radovanmoncek.nettgame.server.ship.creators;

import cz.radovanmoncek.nettgame.server.modules.games.codecs.RequestFlatBuffersDecoder;
import cz.radovanmoncek.nettgame.server.ship.engine.creators.RequestFlatBuffersDecoderCreator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RequestFlatBuffersDecoderCreatorTest {

    @Test
    void newProduct() {

        assertInstanceOf(RequestFlatBuffersDecoder.class, new RequestFlatBuffersDecoderCreator().newProduct());
    }
}
