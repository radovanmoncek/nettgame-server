package cz.radovanmoncek.nettgame.server.modules.games.actions;

import cz.radovanmoncek.nettgame.nettgame.ship.bay.events.GameSessionContext;
import cz.radovanmoncek.nettgame.server.modules.games.tasks.GameStateServerTickTask;

import java.util.function.Consumer;

public class GameStateServerTickAction implements Consumer<GameSessionContext> {

    @Override
    public void accept(final GameSessionContext gameSessionContext) {

        new GameStateServerTickTask()
                .accept(gameSessionContext);
    }
}
