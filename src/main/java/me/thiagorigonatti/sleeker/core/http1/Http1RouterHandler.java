/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.core.http1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import me.thiagorigonatti.sleeker.core.HttpRouterRunnable;
import me.thiagorigonatti.sleeker.core.SleekerServer;
import me.thiagorigonatti.sleeker.exception.HttpSleekException;
import me.thiagorigonatti.sleeker.util.ContentType;
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

        if (cause instanceof HttpSleekException httpSleekException) {
            HttpResponseStatus status = httpSleekException.getHttpResponseStatus();
            CharSequence responseBody = httpSleekException.getResponseMessage();

            if (ctx.channel().isActive()) {

                ByteBuf body = ctx.alloc().buffer();
                body.writeCharSequence(responseBody, CharsetUtil.UTF_8);

                FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, body);
                ctx.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
            } else {
                ctx.close();
            }
        }
    }

    private Runnable getTask(ChannelHandlerContext ctx, Http1Setup setup, FullHttpRequest msg) {
        return switch (msg.method().name()) {
            case "GET" -> () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handleGET(ctx, msg));
            case "POST" -> () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handlePOST(ctx, msg));
            case "PUT" -> () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handlePUT(ctx, msg));
            case "PATCH" -> () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handlePATCH(ctx, msg));
            case "DELETE" -> () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handleDELETE(ctx, msg));
            case "HEAD" -> () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handleHEAD(ctx, msg));
            case "OPTIONS" -> () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handleOPTIONS(ctx, msg));
            case "TRACE" -> () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handleTRACE(ctx, msg));
            case "CONNECT" -> () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handleCONNECT(ctx, msg));
            default -> () -> Http1Responder.reply(ctx, msg, HttpResponseStatus.METHOD_NOT_ALLOWED);
        };
    }

    private void toRun(ChannelHandlerContext ctx, FullHttpRequest msg, HttpRouterRunnable handler) {

        try {
            handler.run();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            exceptionCaught(ctx, e);
        }finally {
            msg.release();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {

        if (!this.isUseHttp1()) {
            throw new HttpSleekException.BaseBuilder<>()
                    .responseMessage("HTTP/1.1 version not enabled, there wasn't HTTP/1.1 context added in the server.")
                    .httpResponseStatus(HttpResponseStatus.HTTP_VERSION_NOT_SUPPORTED)
                    .contentType(ContentType.TEXT_PLAIN_UTF8)
                    .build();
        }

        Http1Setup http1Setup = handlers.get(URI.create(msg.uri()).getPath());

        msg.retain();

        if (http1Setup == null) {
            Http1Responder.reply(ctx, msg, HttpResponseStatus.NOT_FOUND);
            msg.release();
            return;
        }

        if (http1Setup.httpMethodList().contains(msg.method())) {
            SleekerServer.EXECUTOR_SERVICE.execute(getTask(ctx, http1Setup, msg));
        } else {
            Http1Responder.reply(ctx, msg, HttpResponseStatus.METHOD_NOT_ALLOWED);
            msg.release();
        }
    }
}
