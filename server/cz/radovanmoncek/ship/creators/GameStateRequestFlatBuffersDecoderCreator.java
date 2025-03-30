package cz.radovanmoncek.ship.creators;

import cz.radovanmoncek.modules.games.codecs.RequestFlatBuffersDecoder;
import cz.radovanmoncek.ship.parents.creators.ChannelHandlerCreator;
import io.netty.channel.ChannelHandler;

import java.util.Scanner;

public class GameStateRequestFlatBuffersDecoderCreator extends ChannelHandlerCreator {

    @Override
    public ChannelHandler newProduct() {

        return new RequestFlatBuffersDecoder();
    }
}
