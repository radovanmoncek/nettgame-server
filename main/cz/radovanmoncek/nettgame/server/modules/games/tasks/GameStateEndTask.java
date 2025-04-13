package cz.radovanmoncek.nettgame.server.modules.games.tasks;

import cz.radovanmoncek.nettgame.server.modules.games.models.GameHistoryEntity;
import cz.radovanmoncek.nettgame.nettgame.ship.bay.repositories.Repository;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameStateEndTask implements Consumer<GameHistoryEntity> {

    private static final Logger logger = Logger.getLogger(GameStateEndTask.class.getName());

    private final Repository<GameHistoryEntity> gameHistories;

    public GameStateEndTask(Repository<GameHistoryEntity> gameHistories) {

        this.gameHistories = gameHistories;
    }

    @Override
    public void accept(GameHistoryEntity gameHistoryEntity) {

        gameHistories
                .store(gameHistoryEntity)
                .ifPresent(storedGameHistory -> logger.log(Level.INFO, "Persisted game history, and ended game session, game history: \n{0}", storedGameHistory));
    }
}
