package cz.radovanmoncek.nettgame.server.modules.games.models;

import cz.radovanmoncek.nettgame.nettgame.ship.bay.utilities.reflection.ReflectionUtilities;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameHistoryEntityTest {
    private static GameHistoryEntity gameHistoryEntity;

    @BeforeAll
    static void setup() {

        gameHistoryEntity = new GameHistoryEntity();
    }

    @Test
    void setGameSessionUUID() throws NoSuchFieldException, IllegalAccessException {

        gameHistoryEntity.withGameSessionUUID("veryRealUUIDThat36CharactersInLength");

        assertNotNull(ReflectionUtilities.returnValueOnFieldReflectively(gameHistoryEntity, "gameSessionUUID"));
    }

    @Test
    void setEndTime() {
    }

    @Test
    void setPlayer1Name() {

        gameHistoryEntity.withPlayer1Name("llllslkdalskdlaskdlaksdlaskdlasdlaksdlaksdlaksd");
    }

    @Test
    void setPlayer2Name() {
    }

    @Test
    void setLength() {
    }

    @Test
    @Order(Integer.MAX_VALUE / 2 + 1)
    void testToString() {

        assertNotNull(gameHistoryEntity.toString());

        System.out.println(gameHistoryEntity);
    }
}
