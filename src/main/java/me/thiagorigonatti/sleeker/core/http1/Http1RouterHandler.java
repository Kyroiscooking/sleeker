/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.core.http1;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import me.thiagorigonatti.sleeker.core.SleekerServer;
import me.thiagorigonatti.sleeker.exception.Http1NotEnabledException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@ChannelHandler.Sharable
public class Http1RouterHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger LOGGER = LogManager.getLogger(Http1RouterHandler.class);
    public final Map<String, Http1Setup> handlers = new HashMap<>();

    private boolean useHttp1;

    public boolean isUseHttp1() {
        return useHttp1;
    }

    public void setUseHttp1(boolean useHttp1) {
        this.useHttp1 = useHttp1;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        if (cause instanceof Http1NotEnabledException http1NotEnabledException) {
            HttpResponseStatus status = http1NotEnabledException.status;
            String responseBody = http1NotEnabledException.responseMessage;
            LOGGER.warn(responseBody);

            if (ctx.channel().isActive()) {
                FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        status,
                        Unpooled.copiedBuffer(responseBody, CharsetUtil.UTF_8)
                );
                ctx.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
            } else {
                ctx.close();
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {

        if (!this.isUseHttp1()) {
            throw new Http1NotEnabledException();
        }

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
