package cz.radovanmoncek.nettgame.server.ship.engine.launcher;

import cz.radovanmoncek.nettgame.nettgame.ship.engine.bootstrap.exceptions.BootstrapInvalidException;
import cz.radovanmoncek.nettgame.server.modules.games.repositories.GameHistories;
import cz.radovanmoncek.nettgame.server.ship.engine.creators.GameStateFlatBuffersEncoderCreator;
import cz.radovanmoncek.nettgame.server.ship.engine.creators.GameStateGameSessionChannelHandlerCreator;
import cz.radovanmoncek.nettgame.server.ship.engine.creators.RequestFlatBuffersDecoderCreator;
import cz.radovanmoncek.nettgame.nettgame.ship.engine.builders.NettgameServerBootstrapBuilder;
import cz.radovanmoncek.nettgame.nettgame.ship.engine.directors.NettgameServerBootstrapDirector;

import java.net.UnknownHostException;

public final class NettgameServerLauncher {

    public static void main(String[] args) throws UnknownHostException, BootstrapInvalidException {

        System.setProperty("containerized", "false");

        final var director = new NettgameServerBootstrapDirector(new NettgameServerBootstrapBuilder());

        NettgameServerBootstrapBuilder builder = null;

        if (args.length != 0 && (args[0].equals("--help") || args[0].equals("-h"))) {

            System.out.println("NettgameServerLauncher [--help] [-h]");
            System.out.println("Usage: NettgameServerLauncher --mode <local|containerized>");
            System.out.println("'--mode local' launches the game server with localhost address, this mode should only be used for testing outside of the Docker container");
            System.out.println("'--mode containerized' launches the game server with host address, this mode should be used for normal operation inside of the Docker container");

            System.exit(0);
        }

        if (args.length != 2 || !args[0].equals("--mode")) {

            System.out.println("Usage: NettgameServerLauncher --mode <local|containerized>");
            System.out.println("for more information, please issue the --help command.");

            System.exit(1);
        }

        if (!args[1].equals("local") && !args[1].equals("containerized")) {

            System.out.println("Usage: NettgameServerLauncher --mode <local|containerized>");
            System.out.println("for more information, please issue the --help command.");

            System.exit(1);
        }

        switch (args[1]) {

            case "local" -> builder = director.makeLoopbackGameServerBootstrap();

            case "containerized" -> {

                builder = director.makeDefaultGameServerBootstrap();

                System.setProperty("containerized", "true");
            }
        }

        if (builder == null) {

            System.err.println("Fatal -> builder failed to initialize");
            System.exit(1);
        }

        builder
                .buildChannelHandlerCreator(new RequestFlatBuffersDecoderCreator())
                .buildChannelHandlerCreator(new GameStateGameSessionChannelHandlerCreator())
                .buildChannelHandlerCreator(new GameStateFlatBuffersEncoderCreator())
                .buildRepository(new GameHistories())
                .build()
                .run();
    }
}
