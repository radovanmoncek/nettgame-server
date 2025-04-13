package cz.radovanmoncek.nettgame.server.modules.games.tasks;

import cz.radovanmoncek.nettgame.tables.GameStatus;
import cz.radovanmoncek.nettgame.nettgame.ship.bay.events.GameSessionContext;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cz.radovanmoncek.nettgame.server.modules.games.handlers.GameStateGameSessionChannelGroupHandler.*;

public class GameStateServerTickTask implements Consumer<GameSessionContext> {

    private static final Logger logger = Logger.getLogger(GameStateServerTickTask.class.getName());

    @Override
    public void accept(GameSessionContext context) {

        final var i = new AtomicInteger(0);

        context.performOnAllConnections(playerChannel -> {

            final var playerGameStateRequestQueue = playerChannel
                    .attr(PLAYER_STATE_QUEUE_ATTRIBUTE)
                    .get();

            if (playerGameStateRequestQueue == null) {

                return;
            }

            final var gameStateRequest = playerGameStateRequestQueue.poll();

            if (gameStateRequest == null) {

                i.set(i.incrementAndGet());

                return;
            }

            switch (gameStateRequest.gameStatus()) {

                case GameStatus.STOP_SESSION -> {

                    playerChannel
                            .attr(PLAYER_POSITION_ATTRIBUTE)
                            .set(null);

                    playerChannel
                            .attr(PLAYER_NAME_ATTRIBUTE)
                            .set(null);

                    playerChannel
                            .attr(PLAYER_STATE_QUEUE_ATTRIBUTE)
                            .set(null);

                    context.unregisterPlayerConnection(playerChannel);

                    logger.info("A player has left");
                }

                case GameStatus.STATE_CHANGE -> {

                    final var currentPlayerState = playerChannel
                            .attr(PLAYER_POSITION_ATTRIBUTE)
                            .get();

                    if (Objects.isNull(currentPlayerState)) {

                        logger.log(Level.WARNING, "Player with no state {0}", playerChannel);

                        return;
                    }

                    if (gameStateRequest.player().position().x() > X_BOUND || gameStateRequest.player().position().y() > Y_BOUND) {

                        return;
                    }

                    if (gameStateRequest.player().position().x() < 0 || gameStateRequest.player().position().y() < 0) {

                        return;
                    }

                    if (
                            gameStateRequest.player().position().rotation() != 0
                                    && gameStateRequest.player().position().rotation() != 90
                                    && gameStateRequest.player().position().rotation() != 180
                                    && gameStateRequest.player().position().rotation() != 270
                    ) {

                        return;
                    }

                    final var xDelta = Math.abs(gameStateRequest.player().position().x() - currentPlayerState[0]);
                    final var yDelta = Math.abs(gameStateRequest.player().position().y() - currentPlayerState[1]);

                    if ((xDelta != MOVE_DELTA && xDelta != 0) || (yDelta != 0 && yDelta != MOVE_DELTA)) {

                        return;
                    }

                    playerChannel
                            .attr(PLAYER_POSITION_ATTRIBUTE)
                            .set(new int[]{(int) gameStateRequest.player().position().x(), (int) gameStateRequest.player().position().y(), gameStateRequest.player().position().rotation()});

                    i.set(i.incrementAndGet());
                }
            }
        });
    }
}
