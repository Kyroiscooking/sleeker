/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.aaa_dev_test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.*;
import io.netty.util.CharsetUtil;
import me.thiagorigonatti.sleeker.core.http2.Http2SleekHandler;
import me.thiagorigonatti.sleeker.util.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

public class Http2ExampleHandler extends Http2SleekHandler {

    private static final Logger LOGGER = LogManager.getLogger(Http2ExampleHandler.class);
    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void handleGET(ChannelHandlerContext ctx, Http2Headers headers, String requestBody,
                             Http2FrameStream stream) throws IOException {

        stringBuilder.setLength(0);

        stringBuilder
                .append("\r\n")
                .append("---------HTTP/2 REQUEST---------")
                .append("\r\n");

        for (Map.Entry<CharSequence, CharSequence> header : headers) {
            stringBuilder.append(header.getKey()).append(": ").append(header.getValue())
                    .append("\r\n");
        }
        stringBuilder
                .append(requestBody)
                .append("\r\n")
                .append("--------------------------------")
                .append("\r\n");

        Http2Headers responseHeaders = new DefaultHttp2Headers()
                .status(HttpResponseStatus.OK.codeAsText())
                .set(HttpHeaderNames.CONTENT_TYPE, ContentType.TEXT_PLAIN_UTF8.getMimeType());

        ByteBuf body = ctx.alloc().buffer();
        body.writeCharSequence("Hello from HTTP/2", CharsetUtil.UTF_8);

        ctx.write(new DefaultHttp2HeadersFrame(responseHeaders, false).stream(stream));
        ctx.writeAndFlush(new DefaultHttp2DataFrame(body, true).stream(stream));

        LOGGER.info(stringBuilder.toString());
    }

    @Override
    protected void handlePOST(ChannelHandlerContext ctx, Http2Headers http2Headers, String requestBody,
                              Http2FrameStream stream) {

        stringBuilder.setLength(0);

        stringBuilder
                .append("\r\n")
                .append("---------HTTP/2 REQUEST---------")
                .append("\r\n");

        for (Map.Entry<CharSequence, CharSequence> header : http2Headers) {
            stringBuilder.append(header.getKey()).append(": ").append(header.getValue())
                    .append("\r\n");
        }
        stringBuilder
                .append(requestBody)
                .append("\r\n")
                .append("--------------------------------")
                .append("\r\n");

        Http2Headers responseHeaders = new DefaultHttp2Headers()
                .status(HttpResponseStatus.CREATED.codeAsText())
                .set(HttpHeaderNames.CONTENT_TYPE, ContentType.TEXT_PLAIN_UTF8.getMimeType());

        ByteBuf body = ctx.alloc().buffer();
        body.writeCharSequence("Saved! (HTTP/2)", CharsetUtil.UTF_8);

        ctx.write(new DefaultHttp2HeadersFrame(responseHeaders, false).stream(stream));
        ctx.writeAndFlush(new DefaultHttp2DataFrame(body, true).stream(stream));

        LOGGER.info(stringBuilder.toString());
    }
}
