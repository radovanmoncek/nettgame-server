package cz.radovanmoncek.nettgame.server.modules.games.handlers;

import cz.radovanmoncek.nettgame.server.modules.games.events.GameStateGameSessionEventListener;
import cz.radovanmoncek.nettgame.server.modules.games.models.GameStateFlatBuffersSerializable;
import cz.radovanmoncek.nettgame.server.modules.games.repositories.GameHistories;
import cz.radovanmoncek.nettgame.nettgame.ship.bay.models.GameSessionConfigurationOption;
import cz.radovanmoncek.nettgame.nettgame.ship.bay.parents.handlers.GameSessionChannelGroupHandler;
import cz.radovanmoncek.nettgame.nettgame.ship.engine.injection.annotations.ChannelHandlerAttributeInjectee;
import cz.radovanmoncek.nettgame.tables.GameStatus;
import cz.radovanmoncek.nettgame.tables.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.AbstractMap;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameStateGameSessionChannelGroupHandler extends GameSessionChannelGroupHandler<Request> {

    public static final AttributeKey<Queue<Request>> PLAYER_STATE_QUEUE_ATTRIBUTE = AttributeKey.valueOf("gameStateRequestQueue");
    public static final int MAX_NICK_NAME_LENGTH = 8;
    public static final AttributeKey<int[]> PLAYER_POSITION_ATTRIBUTE = AttributeKey.valueOf("playerPosition");
    public static final AttributeKey<String> PLAYER_NAME_ATTRIBUTE = AttributeKey.valueOf("playerName");
    public static final AttributeKey<Byte> PLAYER_CHARACTER_ATTRIBUTE = AttributeKey.valueOf("playerCharacter");
    public static final int X_BOUND = 800,
                            Y_BOUND = 600,
                            MOVE_DELTA = 2;

    private static final Logger logger = Logger.getLogger(GameStateGameSessionChannelGroupHandler.class.getName());

    @SuppressWarnings("unused")
    @ChannelHandlerAttributeInjectee
    private GameHistories gameHistories;

    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final Request gameStateRequest) {

        final var playerChannel = channelHandlerContext.channel();

        switch (gameStateRequest.gameStatus()) {

            case GameStatus.START_SESSION -> {

                if (playerChannel
                        .attr(PLAYER_STATE_QUEUE_ATTRIBUTE)
                        .get() != null
                )
                    return;

                logger.log(Level.INFO, "Player {0} wants to start the game", playerChannel);

                final var requestedName = Objects.requireNonNullElse(gameStateRequest.player().name(), "");

                if (requestedName.length() > MAX_NICK_NAME_LENGTH || requestedName.isBlank()) { //todo: auth module

                    playerChannel.writeAndFlush(
                            new GameStateFlatBuffersSerializable()
                                    .ofInvalidStatus()
                    );

                    logger.log(Level.INFO, "Invalid nickname {0} length {1} or format", new Object[]{requestedName, requestedName.length()});

                    return;
                }

                playerChannel
                        .attr(PLAYER_STATE_QUEUE_ATTRIBUTE)
                        .set(new ConcurrentLinkedQueue<>(List.of(gameStateRequest)));

                startGameSession(
                        new GameStateGameSessionEventListener(playerChannel, gameHistories),
                        List.of(new AbstractMap.SimpleEntry<>(GameSessionConfigurationOption.MAX_PLAYERS, 2))
                );
            }

            case GameStatus.STOP_SESSION, GameStatus.STATE_CHANGE -> {

                final var currentPlayerStateQueueAttribute = playerChannel
                        .attr(PLAYER_STATE_QUEUE_ATTRIBUTE);

                final var currentPlayerStateQueue = currentPlayerStateQueueAttribute.get();

                if(currentPlayerStateQueue == null)
                    return;

                currentPlayerStateQueue.offer(gameStateRequest);

                logger.fine("Game state request enqueued");
            }

            case GameStatus.JOIN_SESSION -> {

                final var currentPlayerStateQueueAttribute = playerChannel
                        .attr(PLAYER_STATE_QUEUE_ATTRIBUTE);

                final var currentPlayerStateQueue = currentPlayerStateQueueAttribute.get();

                if(currentPlayerStateQueue == null) {

                    currentPlayerStateQueueAttribute.set(new ConcurrentLinkedQueue<>(List.of(gameStateRequest)));

                    broadcastGlobalEvent(playerChannel);

                    return;
                }

                currentPlayerStateQueue.offer(gameStateRequest);

                broadcastGlobalEvent(playerChannel);
            }
        }
    }
}
