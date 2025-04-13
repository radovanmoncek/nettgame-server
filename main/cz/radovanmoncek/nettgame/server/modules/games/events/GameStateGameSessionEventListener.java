package cz.radovanmoncek.nettgame.server.modules.games.events;

import cz.radovanmoncek.nettgame.nettgame.ship.bay.events.GameSessionContext;
import cz.radovanmoncek.nettgame.nettgame.ship.bay.utilities.logging.LoggingUtilities;
import cz.radovanmoncek.nettgame.nettgame.ship.deck.events.GameSessionEventListener;
import cz.radovanmoncek.nettgame.server.modules.games.actions.GameStateEndAction;
import cz.radovanmoncek.nettgame.server.modules.games.actions.GameStateServerTickAction;
import cz.radovanmoncek.nettgame.server.modules.games.actions.GameStateStartAction;
import cz.radovanmoncek.nettgame.server.modules.games.models.GameHistoryEntity;
import cz.radovanmoncek.nettgame.server.modules.games.models.GameStateFlatBuffersSerializable;
import cz.radovanmoncek.nettgame.server.modules.games.repositories.GameHistories;
import cz.radovanmoncek.nettgame.tables.GameStatus;
import io.netty.channel.Channel;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cz.radovanmoncek.nettgame.server.modules.games.handlers.GameStateGameSessionChannelGroupHandler.*;

public class GameStateGameSessionEventListener implements GameSessionEventListener {

    private static final Logger logger = Logger.getLogger(GameStateGameSessionEventListener.class.getName());

    private final UUID gameUUID;
    private final Channel hostChannel;
    private final GameHistories gameHistories;
    private final long start;
    private final String[] playerNames;

    private int lastGameHash = Integer.MIN_VALUE;
    private long length;

    //https://stackoverflow.com/questions/6307648/change-global-setting-for-logger-instances

    static {

        LoggingUtilities.enableGlobalLoggingLevel(Level.ALL);
    }

    public GameStateGameSessionEventListener(Channel hostChannel, GameHistories gameHistories) {

        this.gameHistories = gameHistories;
        this.hostChannel = hostChannel;

        playerNames = new String[] {"", ""};
        gameUUID = UUID.randomUUID();
        length = 0;
        start = System.currentTimeMillis();
    }

    @Override
    public void onErrorThrown(final GameSessionContext context, Throwable throwable) {

        logger.throwing(getClass().getName(), "onErrorThrown", throwable);
    }

    @Override
    public void onStart(final GameSessionContext context) {

        new GameStateStartAction()
                .accept(context);

        if (hostChannel.attr(PLAYER_POSITION_ATTRIBUTE).get() == null) {

            return;
        }

        lastGameHash = Integer.hashCode(Arrays.stream(hostChannel
                .attr(PLAYER_POSITION_ATTRIBUTE)
                .get()
        ).sum());

        playerNames[0] = hostChannel
                .attr(PLAYER_NAME_ATTRIBUTE)
                .get();

        sendGameStateToOrderedPlayerChannels(GameStatus.START_SESSION, context);
    }

    @Override
    public void onGlobalConnectionEvent(GameSessionContext context, Channel playerChannel) {

        final var playerGameStateRequestQueue = playerChannel
                .attr(PLAYER_STATE_QUEUE_ATTRIBUTE)
                .get();

        if (playerGameStateRequestQueue == null) {

            return;
        }

        if (playerGameStateRequestQueue.isEmpty()) {

            return;
        }

        final var gameStateRequest = playerGameStateRequestQueue.peek();
        final var gameCode = Objects.requireNonNullElse(gameStateRequest.gameCode(), "");

        if (!gameUUID.toString().substring(0, 8).equals(gameCode.substring(0, 8))) {

            logger.log(Level.FINEST, "Player sent non-matching game code: {0}", gameCode);

            return;
        }

        playerGameStateRequestQueue.poll();

        playerChannel
                .attr(PLAYER_POSITION_ATTRIBUTE)
                .set(new int[]{X_BOUND / 2, Y_BOUND / 2, 0});
        playerChannel
                .attr(PLAYER_NAME_ATTRIBUTE)
                .set(gameStateRequest.player().name());
        playerChannel
                .attr(PLAYER_CHARACTER_ATTRIBUTE)
                .set(gameStateRequest.player().character());

        context.registerPlayerConnection(playerChannel);

        logger.info("A player has joined");
    }

    @Override
    public void onInitialize(GameSessionContext context) {

        context.registerPlayerConnection(hostChannel);
    }

    @Override
    public void onServerTick(GameSessionContext context) {

        final var currentGameStateSum = new AtomicInteger(0);

        length = System.currentTimeMillis() - start; //todo: framework feature fix !!!! !!!!

        new GameStateServerTickAction()
                .accept(context);

        context.performOnAllConnections(playerChannel -> {

            final var currentPlayerState = playerChannel
                    .attr(PLAYER_POSITION_ATTRIBUTE)
                    .get();

            if (Objects.isNull(currentPlayerState)) {

                logger.log(Level.WARNING, "Player with no state {0}", playerChannel);

                currentGameStateSum.set(-1);

                return;
            }

            currentGameStateSum.set(currentGameStateSum.addAndGet(Arrays.stream(currentPlayerState).sum()));
        });

        if (currentGameStateSum.get() < 0 || lastGameHash == Integer.hashCode(currentGameStateSum.get())) {

            return;
        }

        lastGameHash = Integer.hashCode(currentGameStateSum.get());

        sendGameStateToOrderedPlayerChannels(GameStatus.STATE_CHANGE, context);
    }

    @Override
    public void onEnded(GameSessionContext context) {

        new GameStateEndAction(gameHistories)
                .accept(
                        new GameHistoryEntity()
                                .withGameSessionUUID(gameUUID.toString())
                                .withEndTime(Timestamp.from(Instant.now()))
                                .withPlayer1Name(playerNames[0])
                                .withPlayer2Name(playerNames[1])
                                .withLength(length)
                );
    }

    @Override
    public void onContextConnectionsEmpty(GameSessionContext context) {

        logger.fine("Context connections empty, sending STOP_SESSION");

        context.broadcast(new GameStateFlatBuffersSerializable().ofStoppedStatus());
    }

    @Override
    public void onContextConnection(GameSessionContext context, Channel playerChannel) {

        playerNames[1] = playerChannel
                .attr(PLAYER_NAME_ATTRIBUTE)
                .get();

        sendGameStateToOrderedPlayerChannels(GameStatus.JOIN_SESSION, context);
    }

    @Override
    public void onContextConnectionClosed(GameSessionContext context) {

        logger.finest("Got context connection closed");
    }

    @Override
    public void onContextConnectionCountChanged(GameSessionContext context) {

        logger.finest("Got context connection count changed");
    }

    private void sendInvalid(Channel channel) {

        channel.writeAndFlush(
                new GameStateFlatBuffersSerializable()
                        .ofInvalidStatus()
        );
    }

    private void sendGameStateToOrderedPlayerChannels(byte gameStatus, GameSessionContext context) {

        // TODO: invalid state check, that means, to check the integrity of the current game state

        logger.log(Level.FINE, "Sending game state to all channels");

        final var secondPlayerChannel = new AtomicReference<Channel>();

        context.performOnAllConnections(connection -> {

            if (connection.equals(hostChannel))
                return;

            secondPlayerChannel.set(connection);
        });

        if (secondPlayerChannel.get() == null) {

            hostChannel.writeAndFlush(new GameStateFlatBuffersSerializable()
                    .withGameState(length, gameStatus, gameUUID.toString().substring(0, 8))
                    .withPlayer(
                            hostChannel.attr(PLAYER_POSITION_ATTRIBUTE).get(),
                            hostChannel.attr(PLAYER_NAME_ATTRIBUTE).get(),
                            hostChannel.attr(PLAYER_CHARACTER_ATTRIBUTE).get()
                    ));

            return;
        }

        hostChannel.writeAndFlush(new GameStateFlatBuffersSerializable()
                .withGameState(length, gameStatus, gameUUID.toString().substring(0, 8))
                .withPlayer(
                        hostChannel.attr(PLAYER_POSITION_ATTRIBUTE).get(),
                        hostChannel.attr(PLAYER_NAME_ATTRIBUTE).get(),
                        hostChannel.attr(PLAYER_CHARACTER_ATTRIBUTE).get()
                )
                .withPlayer(
                        secondPlayerChannel.get().attr(PLAYER_POSITION_ATTRIBUTE).get(),
                        secondPlayerChannel.get().attr(PLAYER_NAME_ATTRIBUTE).get(),
                        secondPlayerChannel.get().attr(PLAYER_CHARACTER_ATTRIBUTE).get()
                )
        );

        secondPlayerChannel.get().writeAndFlush(new GameStateFlatBuffersSerializable()
                .withGameState(length, gameStatus, gameUUID.toString().substring(0, 8))
                .withPlayer(
                        secondPlayerChannel.get().attr(PLAYER_POSITION_ATTRIBUTE).get(),
                        secondPlayerChannel.get().attr(PLAYER_NAME_ATTRIBUTE).get(),
                        secondPlayerChannel.get().attr(PLAYER_CHARACTER_ATTRIBUTE).get()
                )
                .withPlayer(
                        hostChannel.attr(PLAYER_POSITION_ATTRIBUTE).get(),
                        hostChannel.attr(PLAYER_NAME_ATTRIBUTE).get(),
                        hostChannel.attr(PLAYER_CHARACTER_ATTRIBUTE).get()
                )
        );
    }
}
