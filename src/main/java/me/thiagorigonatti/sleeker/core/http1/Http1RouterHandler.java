/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.core.http1;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import me.thiagorigonatti.sleeker.core.SleekerServer;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@ChannelHandler.Sharable
public class Http1RouterHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public final Map<String, Http1Setup> handlers = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {

        Http1Setup http1Setup = handlers.get(URI.create(msg.uri()).getPath());

        msg.retain();

        SleekerServer.EXECUTOR_SERVICE.execute(() -> {
            if (http1Setup == null) {
                Http1Responder.reply(ctx, msg, HttpResponseStatus.NOT_FOUND);
                msg.release();
                return;
            }
            try {
                if (http1Setup.httpMethodList().contains(msg.method()) && msg.method() == HttpMethod.GET) {
                    http1Setup.http1SleekHandler().handleGET(ctx, msg);

                } else if (http1Setup.httpMethodList().contains(msg.method()) && msg.method() == HttpMethod.POST) {
                    http1Setup.http1SleekHandler().handlePOST(ctx, msg);

                } else if (http1Setup.httpMethodList().contains(msg.method()) && msg.method() == HttpMethod.PUT) {
                    http1Setup.http1SleekHandler().handlePUT(ctx, msg);

                } else if (http1Setup.httpMethodList().contains(msg.method()) && msg.method() == HttpMethod.PATCH) {
                    http1Setup.http1SleekHandler().handlePATCH(ctx, msg);

                } else if (http1Setup.httpMethodList().contains(msg.method()) && msg.method() == HttpMethod.DELETE) {
                    http1Setup.http1SleekHandler().handleDELETE(ctx, msg);

                } else if (http1Setup.httpMethodList().contains(msg.method()) && msg.method() == HttpMethod.HEAD) {
                    http1Setup.http1SleekHandler().handleHEAD(ctx, msg);

                } else if (http1Setup.httpMethodList().contains(msg.method()) && msg.method() == HttpMethod.OPTIONS) {
                    http1Setup.http1SleekHandler().handleOPTIONS(ctx, msg);

                } else if (http1Setup.httpMethodList().contains(msg.method()) && msg.method() == HttpMethod.TRACE) {
                    http1Setup.http1SleekHandler().handleTRACE(ctx, msg);

                } else if (http1Setup.httpMethodList().contains(msg.method()) && msg.method() == HttpMethod.CONNECT) {
                    http1Setup.http1SleekHandler().handleCONNECT(ctx, msg);

                } else {
                    Http1Responder.reply(ctx, msg, HttpResponseStatus.METHOD_NOT_ALLOWED);
                }

                msg.release();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
