/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.aaa_dev_test;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import me.thiagorigonatti.sleeker.core.http1.Http1SleekHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class Http1ExampleHandler extends Http1SleekHandler {

    private static final Logger LOGGER = LogManager.getLogger(Http1ExampleHandler.class);
    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void handleGET(ChannelHandlerContext ctx, FullHttpRequest msg) throws IOException {

        stringBuilder.setLength(0);

        stringBuilder
                .append("\r\n")
                .append("--------HTTP/1.1 REQUEST--------")
                .append("\r\n")
                .append("method: ").append(msg.method())
                .append("\r\n")
                .append("path: ").append(URI.create(msg.uri()).getPath())
                .append("\r\n");

        for (Map.Entry<String, String> header : msg.headers()) {
            stringBuilder.append(header.getKey()).append(": ").append(header.getValue())
                    .append("\r\n");
        }

        String body = "Hello from HTTP/1.1";
        FullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK,
                Unpooled.copiedBuffer(body, CharsetUtil.UTF_8));
        response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8")
                .set(HttpHeaderNames.CONTENT_LENGTH, body.length());
        ctx.writeAndFlush(response);

        stringBuilder
                .append(msg.content().toString(CharsetUtil.UTF_8))
                .append("\r\n")
                .append("--------------------------------")
                .append("\r\n");

        LOGGER.info(stringBuilder.toString());
    }

    @Override
    protected void handlePOST(ChannelHandlerContext ctx, FullHttpRequest msg) throws IOException {

        stringBuilder.setLength(0);

        stringBuilder
                .append("\r\n")
                .append("--------HTTP/1.1 REQUEST--------")
                .append("\r\n")
                .append("method: ").append(msg.method())
                .append("\r\n")
                .append("path: ").append(URI.create(msg.uri()).getPath())
                .append("\r\n");

        for (Map.Entry<String, String> header : msg.headers()) {
            stringBuilder.append(header.getKey()).append(": ").append(header.getValue())
                    .append("\r\n");
        }

        String body = "Saved! (HTTP/1.1)";
        FullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.CREATED,
                Unpooled.copiedBuffer(body, CharsetUtil.UTF_8));
        response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8")
                .set(HttpHeaderNames.CONTENT_LENGTH, body.length());
        ctx.writeAndFlush(response);

        stringBuilder
                .append(msg.content().toString(CharsetUtil.UTF_8))
                .append("\r\n")
                .append("--------------------------------")
                .append("\r\n");

        LOGGER.info(stringBuilder.toString());
    }
}
