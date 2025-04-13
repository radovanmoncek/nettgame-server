package cz.radovanmoncek.nettgame.server.modules.games.handlers;

import com.google.flatbuffers.FlatBufferBuilder;
import cz.radovanmoncek.nettgame.nettgame.ship.bay.utilities.logging.LoggingUtilities;
import cz.radovanmoncek.nettgame.nettgame.ship.bay.utilities.reflection.ReflectionUtilities;
import cz.radovanmoncek.nettgame.server.modules.games.models.GameStateFlatBuffersSerializable;
import cz.radovanmoncek.nettgame.server.modules.games.repositories.GameHistories;
import cz.radovanmoncek.nettgame.tables.Character;
import cz.radovanmoncek.nettgame.tables.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionDelegatorBaseImpl;
import org.hibernate.engine.spi.SessionFactoryDelegatingImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GameStateGameSessionChannelGroupHandlerTest {
    private EmbeddedChannel channel;

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {

        final var handler = new GameStateGameSessionChannelGroupHandler();
        final var gameHistories = new GameHistories();

        ReflectionUtilities.setValueOnFieldReflectively(handler, "gameHistories", gameHistories);
        ReflectionUtilities.setValueOnFieldReflectively(gameHistories, "sessionFactory", new SessionFactoryDelegatingImpl(null){

            @Override
            public <R> R fromTransaction(Function<? super Session, R> action) {
                return action.apply(new SessionDelegatorBaseImpl(null){

                    @Override
                    public <T> T merge(T object) {
                        return object;
                    }
                });
            }
        });

        (channel = new EmbeddedChannel())
                .pipeline()
                .addLast(handler);

        LoggingUtilities.enableGlobalLoggingLevel(Level.ALL);
    }

    @Test
    void playerChannelRead() throws InterruptedException, NoSuchFieldException, IllegalAccessException {

        writeStartGame();
        writeGameState();
        writeStopGame();
    }

    void writeStartGame() throws InterruptedException, NoSuchFieldException, IllegalAccessException {

        final var builder = new FlatBufferBuilder(1024);
        final var name = builder.createString("Test");

        Player.startPlayer(builder);
        Player.addName(builder, name);
        Player.addCharacter(builder, Character.BLUE);
        Player.addPosition(builder, Position.createPosition(builder, 0, 0, 0));

        final var player = Player.endPlayer(builder);

        final var gameStatusRequest = Request.createRequest(builder, player, 0, GameStatus.START_SESSION);

        builder.finish(gameStatusRequest);

        final var encodedGameStatusRequest = ByteBuffer.wrap(builder.sizedByteArray());

        channel.writeInbound(Request.getRootAsRequest(encodedGameStatusRequest));

        TimeUnit.MILLISECONDS.sleep(20);

        channel.advanceTimeBy(20, TimeUnit.SECONDS);

        var gameStateFlatBuffersSerializable = (GameStateFlatBuffersSerializable) channel.readOutbound();

        assertNotNull(gameStateFlatBuffersSerializable);

        var player1Position = ((int[][]) ReflectionUtilities.returnValueOnFieldReflectively(gameStateFlatBuffersSerializable, "playerPositions"))[0];
        var name1 = (String[]) ReflectionUtilities.returnValueOnFieldReflectively(gameStateFlatBuffersSerializable, "playerNames");

        assertNotNull(player1Position);
        assertNotNull(name1);

        assertEquals(400, player1Position[0]);
        assertEquals(300, player1Position[1]);
        assertEquals(0, player1Position[2]);
        assertEquals("Test", name1[0]);
        assertEquals(GameStatus.START_SESSION, ReflectionUtilities.returnValueOnFieldReflectively(gameStateFlatBuffersSerializable, "gameStatus"));
    }

    void writeGameState() throws InterruptedException, NoSuchFieldException, IllegalAccessException {

        final var builder = new FlatBufferBuilder(1024);
        final var position = Position.createPosition(builder, 402, 300, 0);

        Player.startPlayer(builder);
        Player.addPosition(builder, position);

        final var player = Player.endPlayer(builder);
        final var request = Request.createRequest(builder, player, 0, GameStatus.STATE_CHANGE);

        builder.finish(request);

        channel.writeInbound(Request.getRootAsRequest(ByteBuffer.wrap(builder.sizedByteArray())));

        TimeUnit.MILLISECONDS.sleep(20);

        channel.advanceTimeBy(20, TimeUnit.SECONDS);

        var gameStateFlatBuffersSerializable = (GameStateFlatBuffersSerializable) channel.readOutbound();

        assertNotNull(gameStateFlatBuffersSerializable);

        var player1Position = (int[][]) ReflectionUtilities.returnValueOnFieldReflectively(gameStateFlatBuffersSerializable, "playerPositions");
        var name1 = (String[]) ReflectionUtilities.returnValueOnFieldReflectively(gameStateFlatBuffersSerializable, "playerNames");

        assertNotNull(player1Position);
        assertNotNull(name1);
        assertEquals("Test", name1[0]);
        assertEquals(GameStatus.STATE_CHANGE, ReflectionUtilities.returnValueOnFieldReflectively(gameStateFlatBuffersSerializable, "gameStatus"));
    }

    void writeStopGame() throws InterruptedException, NoSuchFieldException, IllegalAccessException {

        final var builder = new FlatBufferBuilder(1024);

        builder.finish(Request.createRequest(builder, 0, 0, GameStatus.STOP_SESSION));

        channel.writeInbound(Request.getRootAsRequest(ByteBuffer.wrap(builder.sizedByteArray())));

        //sleeping to wait for session end
        TimeUnit.MILLISECONDS.sleep(100);

        channel.advanceTimeBy(100, TimeUnit.MILLISECONDS);

        final var gameStateFlatBuffersSerializable = channel.readOutbound();

        assertNotNull(gameStateFlatBuffersSerializable);

        assertEquals(GameStatus.STOP_SESSION, ReflectionUtilities.returnValueOnFieldReflectively(gameStateFlatBuffersSerializable, "gameStatus"));
    }
}
