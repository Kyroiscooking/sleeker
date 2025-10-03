/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.core.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.*;
import io.netty.util.CharsetUtil;
import me.thiagorigonatti.sleeker.core.HttpRouterRunnable;
import me.thiagorigonatti.sleeker.core.SleekerServer;
import me.thiagorigonatti.sleeker.exception.Http2SleekException;
import me.thiagorigonatti.sleeker.util.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
public class Http2RouterHandler extends SimpleChannelInboundHandler<Http2Frame> {

    private static final Logger LOGGER = LogManager.getLogger(Http2RouterHandler.class);

    public final Map<String, Http2Setup> handlers = new HashMap<>();
    private final Map<String, Http2Request> http2RequestMap = new ConcurrentHashMap<>();
    private boolean useHttp2;

    public boolean isUseHttp2() {
        return useHttp2;
    }

    public void setUseHttp2(boolean useHttp2) {
        this.useHttp2 = useHttp2;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        if (cause instanceof Http2SleekException http2SleekException) {

            Http2Headers http2Headers = http2SleekException.getHttp2Headers();
            Http2FrameStream http2FrameStream = http2SleekException.getHttp2FrameStream();

            HttpResponseStatus httpResponseStatus = http2SleekException.getHttpResponseStatus();
            CharSequence responseMessage = http2SleekException.getResponseMessage();
            ContentType contentType = http2SleekException.getContentType();

            if (ctx.channel().isActive()) {
                Http2Headers headers = new DefaultHttp2Headers()
                        .status(httpResponseStatus.codeAsText())
                        .set(HttpHeaderNames.CONTENT_TYPE, contentType.getMimeType());

                HttpMethod httpMethod = HttpMethod.valueOf(http2Headers.method().toString());
                boolean end = httpMethod.equals(HttpMethod.HEAD);

                ctx.write(new DefaultHttp2HeadersFrame(headers, end).stream(http2FrameStream));

                if (!end) {
                    ByteBuf body = ctx.alloc().buffer();
                    body.writeCharSequence(responseMessage, CharsetUtil.UTF_8);
                    ctx.write(new DefaultHttp2DataFrame(body, true).stream(http2FrameStream));
                }
                ctx.flush();
            } else {
                ctx.close();
            }
        }
    }

    private void toRun(ChannelHandlerContext ctx, HttpRouterRunnable handler) {
        try {
            handler.run();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            exceptionCaught(ctx, e);
        }
    }

    private Runnable getTask(ChannelHandlerContext ctx, Http2Setup setup, Http2Headers headers, String body, Http2Frame http2Frame) {

        Http2FrameStream stream = http2Frame instanceof Http2HeadersFrame
                ? ((Http2HeadersFrame) http2Frame).stream()
                : ((Http2DataFrame) http2Frame).stream();

        return switch (headers.method().toString()) {
            case "GET" -> () -> toRun(ctx, () -> setup.http2SleekHandler().handleGET(ctx, headers, body, stream));
            case "POST" -> () -> toRun(ctx, () -> setup.http2SleekHandler().handlePOST(ctx, headers, body, stream));
            case "PUT" -> () -> toRun(ctx, () -> setup.http2SleekHandler().handlePUT(ctx, headers, body, stream));
            case "PATCH" -> () -> toRun(ctx, () -> setup.http2SleekHandler().handlePATCH(ctx, headers, body, stream));
            case "DELETE" -> () -> toRun(ctx, () -> setup.http2SleekHandler().handleDELETE(ctx, headers, body, stream));
            case "HEAD" -> () -> toRun(ctx, () -> setup.http2SleekHandler().handleHEAD(ctx, headers, body, stream));
            case "OPTIONS" ->
                    () -> toRun(ctx, () -> setup.http2SleekHandler().handleOPTIONS(ctx, headers, body, stream));
            case "TRACE" -> () -> toRun(ctx, () -> setup.http2SleekHandler().handleTRACE(ctx, headers, body, stream));
            case "CONNECT" ->
                    () -> toRun(ctx, () -> setup.http2SleekHandler().handleCONNECT(ctx, headers, body, stream));
            default -> () -> Http2Responder.reply(ctx, headers, stream, HttpResponseStatus.METHOD_NOT_ALLOWED);
        };
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Http2Frame msg) {

        if (msg instanceof Http2HeadersFrame headersFrame) {

            if (!this.isUseHttp2()) {
                throw new Http2SleekException.Builder(headersFrame.headers(), headersFrame.stream())
                        .responseMessage("HTTP/2 version not enabled, there wasn't HTTP/2 context added in the server.")
                        .httpResponseStatus(HttpResponseStatus.HTTP_VERSION_NOT_SUPPORTED)
                        .contentType(ContentType.TEXT_PLAIN_UTF8)
                        .build();
            }

            String id = ctx.channel().id().asShortText() + "_" + headersFrame.stream().id();

            String path = headersFrame.headers().path().toString();
            Http2Setup http2Setup = handlers.get(path);

            if (http2Setup == null) {
                SleekerServer.EXECUTOR_SERVICE.execute(() -> Http2Responder.reply(ctx, headersFrame.headers(), headersFrame.stream(), HttpResponseStatus.NOT_FOUND));
                return;
            }

            if (headersFrame.isEndStream()) {
                if (http2Setup.httpMethodList().contains(HttpMethod.valueOf(headersFrame.headers().method().toString()))) {
                    SleekerServer.EXECUTOR_SERVICE.execute(getTask(ctx, http2Setup, headersFrame.headers(), "", headersFrame));

                } else {
                    SleekerServer.EXECUTOR_SERVICE.execute(() ->
                            Http2Responder.reply(ctx, headersFrame.headers(), headersFrame.stream(), HttpResponseStatus.METHOD_NOT_ALLOWED));
                }
            } else {
                ByteBuf body = ctx.alloc().buffer();
                http2RequestMap.put(id, new Http2Request(headersFrame.headers(), body));
            }


        } else if (msg instanceof Http2DataFrame dataFrame) {

            String id = ctx.channel().id().asShortText() + "_" + dataFrame.stream().id();
            Http2Request context = http2RequestMap.get(id);

            if (context == null) {
                return;
            }

            context.body().writeBytes(dataFrame.content());

            String body = context.body().toString(CharsetUtil.UTF_8);
            Http2Headers headers = context.headers();
            Http2Setup http2Setup = handlers.get(headers.path().toString());

            if (dataFrame.isEndStream()) {
                if (http2Setup.httpMethodList().contains(HttpMethod.valueOf(headers.method().toString()))) {
                    SleekerServer.EXECUTOR_SERVICE.execute(getTask(ctx, http2Setup, headers, body, dataFrame));

                } else {
                    SleekerServer.EXECUTOR_SERVICE.execute(() ->
                            Http2Responder.reply(ctx, headers, dataFrame.stream(), HttpResponseStatus.METHOD_NOT_ALLOWED));
                }

                context.body().release();
                http2RequestMap.remove(id);
            }

        } else if (msg instanceof Http2ResetFrame resetFrame) {
            String id = ctx.channel().id().asShortText() + "_" + resetFrame.stream().id();
            Http2Request req = http2RequestMap.remove(id);
            if (req != null) {
                req.body().release();
            }
        }
    }
}
