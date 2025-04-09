package cz.radovanmoncek.nettgame.server.ship.engine.creators;

import cz.radovanmoncek.nettgame.server.modules.games.codecs.GameStateFlatBuffersEncoder;
import cz.radovanmoncek.nettgame.nettgame.ship.deck.creators.ChannelHandlerCreator;
import io.netty.channel.ChannelHandler;

public class GameStateFlatBuffersEncoderCreator implements ChannelHandlerCreator {

    @Override
    public ChannelHandler newProduct() {

        return new GameStateFlatBuffersEncoder();
    }
}
