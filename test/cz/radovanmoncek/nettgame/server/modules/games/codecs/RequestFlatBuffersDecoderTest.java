package cz.radovanmoncek.nettgame.server.modules.games.codecs;

import cz.radovanmoncek.nettgame.nettgame.ship.bay.utilities.reflection.ReflectionUtilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

public class RequestFlatBuffersDecoderTest {
    private static RequestFlatBuffersDecoder decoder;

    @BeforeAll
    static void setup() {

        decoder = new RequestFlatBuffersDecoder();
    }

    @Test
    void decodeHeader() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        ReflectionUtilities.invokeNonPublicMethod(decoder, "decodeHeader", new Class[]{ByteBuffer.class}, new Object[]{ByteBuffer.wrap(new byte[]{'g'})});
    }

    @Test
    void decodeBodyAfterHeader() {
    }
}
