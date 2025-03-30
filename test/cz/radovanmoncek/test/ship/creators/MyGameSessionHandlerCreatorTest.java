package cz.radovanmoncek.test.ship.creators;

import cz.radovanmoncek.ship.creators.MyGameSessionHandlerCreator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyGameSessionHandlerCreatorTest {

    @Test
    void newProduct() {

        final var handlerCreator = new MyGameSessionHandlerCreator();
        final var handler = handlerCreator.newProduct();

        assertNotNull(handler);

        assertInstanceOf(handler.getClass(), handler);
    }
}
