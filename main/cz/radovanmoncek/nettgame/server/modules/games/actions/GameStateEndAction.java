package cz.radovanmoncek.nettgame.server.modules.games.actions;

import cz.radovanmoncek.nettgame.server.modules.games.models.GameHistoryEntity;
import cz.radovanmoncek.nettgame.server.modules.games.repositories.GameHistories;
import cz.radovanmoncek.nettgame.server.modules.games.tasks.GameStateEndTask;

import java.util.function.Consumer;

public class GameStateEndAction implements Consumer<GameHistoryEntity> {

    private final GameHistories gameHistories;

    public GameStateEndAction(GameHistories gameHistories) {

        this.gameHistories = gameHistories;
    }

    @Override
    public void accept(GameHistoryEntity gameHistoryEntity) {

        new GameStateEndTask(gameHistories)
                .accept(gameHistoryEntity);
    }
}
