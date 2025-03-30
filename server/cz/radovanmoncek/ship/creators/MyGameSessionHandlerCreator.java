package cz.radovanmoncek.ship.creators;

import cz.radovanmoncek.modules.games.handlers.MyGameSessionChannelGroupHandler;
import cz.radovanmoncek.ship.parents.creators.ChannelHandlerCreator;
import io.netty.channel.ChannelHandler;

public class MyGameSessionHandlerCreator extends ChannelHandlerCreator {

    @Override
    public ChannelHandler newProduct() {

        return new MyGameSessionChannelGroupHandler();
    }
}
