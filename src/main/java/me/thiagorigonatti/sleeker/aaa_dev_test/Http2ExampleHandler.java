/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.aaa_dev_test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import me.thiagorigonatti.sleeker.core.http2.Http2Request;
import me.thiagorigonatti.sleeker.core.http2.Http2Response;
import me.thiagorigonatti.sleeker.core.http2.Http2SleekHandler;
import me.thiagorigonatti.sleeker.exception.HttpSleekException;
import me.thiagorigonatti.sleeker.util.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class Http2ExampleHandler extends Http2SleekHandler {

    private static final Logger LOGGER = LogManager.getLogger(Http2ExampleHandler.class);
    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void handleGET(Http2Request http2Request, Http2Response http2Response) {

        stringBuilder.setLength(0);

        stringBuilder
                .append("\r\n")
                .append("--------HTTP/2 REQUEST--------")
                .append("\r\n")
                .append("ip_port: ").append(http2Request.remoteAddress().getHostString())
                .append(":").append(http2Request.remoteAddress().getPort())
                .append("\r\n")
                .append("method: ").append(http2Request.method())
                .append("\r\n")
                .append("path: ").append(http2Request.path())
                .append("\r\n");

        for (Map.Entry<CharSequence, CharSequence> header : http2Request.headers()) {
            stringBuilder.append(header.getKey()).append(": ").append(header.getValue())
                    .append("\r\n");
        }

        http2Response.addHeader(HttpHeaderNames.CONTENT_TYPE, ContentType.TEXT_PLAIN_UTF8.getMimeType());
        http2Response.setBody("Hello from HTTP/2");
        http2Response.reply(HttpResponseStatus.OK);

        stringBuilder
                .append(http2Request.body())
                .append("\r\n")
                .append("--------------------------------")
                .append("\r\n");

        LOGGER.info(stringBuilder);
    }

    @Override
    protected void handlePOST(Http2Request http2Request, Http2Response http2Response) throws JsonProcessingException {

        if (http2Request.body().isEmpty() || http2Request.body().isBlank()) {

            throw new HttpSleekException.BaseBuilder<>()
                    .contentType(ContentType.APPLICATION_JSON_UTF8)
                    .httpResponseStatus(HttpResponseStatus.BAD_REQUEST)
                    .responseMessage(new ObjectMapper().writeValueAsString(Map.of("errorMessage", "Body cannot be empty or blank")))
                    .build();
        }

        stringBuilder.setLength(0);

        stringBuilder
                .append("\r\n")
                .append("--------HTTP/2 REQUEST--------")
                .append("\r\n")
                .append("ip_port: ").append(http2Request.remoteAddress().getHostString())
                .append(":").append(http2Request.remoteAddress().getPort())
                .append("\r\n")
                .append("method: ").append(http2Request.method())
                .append("\r\n")
                .append("path: ").append(http2Request.path())
                .append("\r\n");

        for (Map.Entry<CharSequence, CharSequence> header : http2Request.headers()) {
            stringBuilder.append(header.getKey()).append(": ").append(header.getValue())
                    .append("\r\n");
        }

        http2Response.addHeader(HttpHeaderNames.CONTENT_TYPE, ContentType.TEXT_PLAIN_UTF8.getMimeType());
        http2Response.setBody("Saved! (HTTP/2)");
        http2Response.reply(HttpResponseStatus.CREATED);

        stringBuilder
                .append(http2Request.body())
                .append("\r\n")
                .append("--------------------------------")
                .append("\r\n");

        LOGGER.info(stringBuilder);
    }
}
