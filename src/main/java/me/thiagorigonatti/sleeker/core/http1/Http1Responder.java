/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.core.http1;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class Http1Responder {

    public static void reply(ChannelHandlerContext ctx, FullHttpRequest msg, HttpResponseStatus httpResponseStatus) {
        String responseBody = httpResponseStatus.reasonPhrase();
        boolean end = msg.method().equals(HttpMethod.HEAD);

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                httpResponseStatus,
                end ? Unpooled.EMPTY_BUFFER : Unpooled.copiedBuffer(responseBody, CharsetUtil.UTF_8)
        );

        response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8")
                .setInt(HttpHeaderNames.CONTENT_LENGTH, end ? 0 : responseBody.getBytes(CharsetUtil.UTF_8).length);

        ctx.writeAndFlush(response);
    }
}
