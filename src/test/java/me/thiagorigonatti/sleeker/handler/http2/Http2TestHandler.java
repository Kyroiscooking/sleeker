/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.handler.http2;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.*;
import io.netty.util.CharsetUtil;
import me.thiagorigonatti.sleeker.core.http2.Http2SleekHandler;

public class Http2TestHandler extends Http2SleekHandler {

    @Override
    protected void handleGET(ChannelHandlerContext ctx, Http2Headers http2Headers, String requestBody, Http2FrameStream stream) {

        Http2Headers responseHeaders = new DefaultHttp2Headers()
                .status(HttpResponseStatus.OK.codeAsText())
                .set(HttpHeaderNames.CONTENT_TYPE, "text/plain");

        HttpMethod httpMethod = HttpMethod.valueOf(http2Headers.method().toString());
        boolean end = httpMethod.equals(HttpMethod.HEAD);
        ctx.write(new DefaultHttp2HeadersFrame(responseHeaders, end).stream(stream));

        if (!end)
            ctx.write(new DefaultHttp2DataFrame(
                    Unpooled.copiedBuffer("Hello from HTTP/2", CharsetUtil.UTF_8), true
            ).stream(stream));
        ctx.flush();
    }
}
