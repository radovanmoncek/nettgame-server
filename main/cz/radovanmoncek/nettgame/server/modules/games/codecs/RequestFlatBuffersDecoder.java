package cz.radovanmoncek.nettgame.server.modules.games.codecs;

import cz.radovanmoncek.nettgame.nettgame.ship.bay.parents.codecs.FlatBuffersDecoder;
import cz.radovanmoncek.nettgame.tables.Request;

import java.nio.ByteBuffer;

public final class RequestFlatBuffersDecoder extends FlatBuffersDecoder<Request> {

    @Override
    protected boolean decodeHeader(final ByteBuffer in) {

        return in.get() == 'g';
    }

    @Override
    protected Request decodeBodyAfterHeader(final ByteBuffer in) {

        return Request.getRootAsRequest(in);
    }
}
