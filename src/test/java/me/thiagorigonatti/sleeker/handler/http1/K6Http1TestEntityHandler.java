/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.handler.http1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import me.thiagorigonatti.sleeker.core.http1.Http1Responder;
import me.thiagorigonatti.sleeker.core.http1.Http1SleekHandler;
import me.thiagorigonatti.sleeker.database.postgres.PostgresTest;
import me.thiagorigonatti.sleeker.model.Entity;

import java.io.IOException;

public class K6Http1TestEntityHandler extends Http1SleekHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handlePOST(ChannelHandlerContext ctx, FullHttpRequest msg) throws IOException {

        String requestBody = msg.content().toString(CharsetUtil.UTF_8);

        FullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.CREATED,
                Unpooled.copiedBuffer(requestBody, CharsetUtil.UTF_8));

        response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8")
                .set(HttpHeaderNames.CONTENT_LENGTH, requestBody.length());
        ctx.writeAndFlush(response);

        Entity entity = objectMapper.readValue(requestBody, Entity.class);

        PostgresTest.saveEntity(entity.id(), entity.level()).subscribe();
    }

    @Override
    protected void handleGET(ChannelHandlerContext ctx, FullHttpRequest msg) {

        PostgresTest.findAll().subscribe(entityList -> {
            try {
                String e = objectMapper.writeValueAsString(entityList);

                FullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK,
                        Unpooled.copiedBuffer(e, CharsetUtil.UTF_8));

                response.headers()
                        .set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8")
                        .set(HttpHeaderNames.CONTENT_LENGTH, e.length());
                ctx.writeAndFlush(response);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, error -> Http1Responder.reply(ctx, msg, HttpResponseStatus.NOT_FOUND));
    }
}
