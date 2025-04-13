package cz.radovanmoncek.nettgame.server.modules.games.actions;

import cz.radovanmoncek.nettgame.server.modules.games.tasks.GameStateStartTask;
import cz.radovanmoncek.nettgame.nettgame.ship.bay.events.GameSessionContext;

import java.util.function.Consumer;

public class GameStateStartAction implements Consumer<GameSessionContext> {

    @Override
    public void accept(final GameSessionContext context) {

        new GameStateStartTask()
                .accept(context);
    }
}
