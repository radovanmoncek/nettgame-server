package cz.radovanmoncek.nettgame.server.modules.games.tasks;

import cz.radovanmoncek.nettgame.nettgame.ship.bay.events.GameSessionContext;

import java.util.function.Consumer;

import static cz.radovanmoncek.nettgame.server.modules.games.handlers.GameStateGameSessionChannelGroupHandler.*;

public class GameStateStartTask implements Consumer<GameSessionContext> {

    @Override
    public void accept(GameSessionContext context) {

        context.performOnAllConnections(playerChannel -> {

            final var playerGameStateRequestQueue = playerChannel
                    .attr(PLAYER_STATE_QUEUE_ATTRIBUTE)
                    .get();

            if (playerGameStateRequestQueue == null)
                return;

            final var gameStateRequest = playerGameStateRequestQueue.poll();

            if (gameStateRequest == null)
                return;

            playerChannel
                    .attr(PLAYER_POSITION_ATTRIBUTE)
                    .set(new int[]{X_BOUND / 2, Y_BOUND / 2, 0});
            playerChannel
                    .attr(PLAYER_NAME_ATTRIBUTE)
                    .set(gameStateRequest
                            .player()
                            .name()
                    );
            playerChannel
                    .attr(PLAYER_CHARACTER_ATTRIBUTE)
                    .set(gameStateRequest
                            .player()
                            .character()
                    );
        });
    }
}
