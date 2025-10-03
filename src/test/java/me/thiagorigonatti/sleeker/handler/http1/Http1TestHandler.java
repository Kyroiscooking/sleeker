/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.handler.http1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import me.thiagorigonatti.sleeker.core.http1.Http1SleekHandler;
import me.thiagorigonatti.sleeker.util.ContentType;

public class Http1TestHandler extends Http1SleekHandler {

    @Override
    protected void handleGET(ChannelHandlerContext ctx, FullHttpRequest msg) {

        ByteBuf body = ctx.alloc().buffer();
        body.writeCharSequence("Hello from HTTP/1.1 test", CharsetUtil.UTF_8);

        FullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK, body);

        response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, ContentType.TEXT_PLAIN_UTF8.getMimeType())
                .set(HttpHeaderNames.CONTENT_LENGTH, body.readableBytes());
        ctx.writeAndFlush(response);
    }
}
