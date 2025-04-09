package cz.radovanmoncek.nettgame.server.ship.creators;

import cz.radovanmoncek.nettgame.server.ship.engine.creators.GameStateGameSessionChannelHandlerCreator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateGameSessionChannelHandlerCreatorTest {

    @Test
    void newProduct() {

        final var handlerCreator = new GameStateGameSessionChannelHandlerCreator();
        final var handler = handlerCreator.newProduct();

        assertNotNull(handler);

        assertInstanceOf(handler.getClass(), handler);
    }
}
