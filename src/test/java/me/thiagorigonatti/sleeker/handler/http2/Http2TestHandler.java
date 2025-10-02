/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.handler.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.*;
import me.thiagorigonatti.sleeker.core.http2.Http2SleekHandler;

import static io.netty.util.CharsetUtil.UTF_8;

public class Http2TestHandler extends Http2SleekHandler {

    @Override
    protected void handleGET(ChannelHandlerContext ctx, Http2Headers http2Headers, String requestBody, Http2FrameStream stream) {

        Http2Headers responseHeaders = new DefaultHttp2Headers()
                .status(HttpResponseStatus.OK.codeAsText())
                .set(HttpHeaderNames.CONTENT_TYPE, "text/plain");

        ctx.write(new DefaultHttp2HeadersFrame(responseHeaders, false).stream(stream));
        ByteBuf body = ctx.alloc().buffer();
        body.writeCharSequence("Hello from HTTP/2", UTF_8);
        ctx.writeAndFlush(new DefaultHttp2DataFrame(body, true).stream(stream));
    }
}
