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

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ChannelHandler.Sharable
public class Http1RouterHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger LOGGER = LogManager.getLogger(Http1RouterHandler.class);
    public final Map<String, Http1Setup> handlers = new HashMap<>();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        if (cause instanceof HttpSleekException httpSleekException) {
            HttpResponseStatus status = httpSleekException.getHttpResponseStatus();
            CharSequence responseBody = httpSleekException.getResponseMessage();
            if (ctx.channel().isActive()) {
                ByteBuf body = ctx.alloc().buffer();
                body.writeCharSequence(responseBody, CharsetUtil.UTF_8);
                FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, body);
                fullHttpResponse.headers().add(HttpHeaderNames.CONTENT_TYPE, httpSleekException.getContentType().getMimeType());
                ctx.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
            } else {
                ctx.close();
            }
        } else {
            ByteBuf body = ctx.alloc().buffer();
            body.writeCharSequence(cause.getMessage(), CharsetUtil.UTF_8);
            FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR, body);
            fullHttpResponse.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.TEXT_PLAIN_UTF8.getMimeType());
            ctx.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
        }
        ctx.close();
    }

    private Runnable getTask(ChannelHandlerContext ctx, Http1Setup setup, FullHttpRequest msg) {

        final QueryStringDecoder decoder = new QueryStringDecoder(msg.uri());
        final Map<String, List<String>> params = decoder.parameters();

        final Http1Request http1Request = new Http1Request(
                (InetSocketAddress) ctx.channel().localAddress(),
                (InetSocketAddress) ctx.channel().remoteAddress(),
                msg.method(),
                msg.headers(),
                URI.create(msg.uri()).getPath(),
                params,
                msg.content().toString(CharsetUtil.UTF_8));

        final Http1Response http1Response = new Http1Response(ctx, msg.protocolVersion());

        return switch (http1Request.method().name()) {
            case "GET" -> () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handleGET(http1Request, http1Response));
            case "POST" ->
                    () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handlePOST(http1Request, http1Response));
            case "PUT" -> () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handlePUT(http1Request, http1Response));
            case "PATCH" ->
                    () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handlePATCH(http1Request, http1Response));
            case "DELETE" ->
                    () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handleDELETE(http1Request, http1Response));
            case "HEAD" ->
                    () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handleHEAD(http1Request, http1Response));
            case "OPTIONS" ->
                    () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handleOPTIONS(http1Request, http1Response));
            case "TRACE" ->
                    () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handleTRACE(http1Request, http1Response));
            case "CONNECT" ->
                    () -> toRun(ctx, msg, () -> setup.http1SleekHandler().handleCONNECT(http1Request, http1Response));
            default -> () -> Http1Responder.reply(ctx, msg, HttpResponseStatus.METHOD_NOT_ALLOWED);
        };
    }

    private void toRun(ChannelHandlerContext ctx, FullHttpRequest msg, HttpRouterRunnable handler) {
        try {
            handler.run();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            exceptionCaught(ctx, e);
        } finally {
            msg.release();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {

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
