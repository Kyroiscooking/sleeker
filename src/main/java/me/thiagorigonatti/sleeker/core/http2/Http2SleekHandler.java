/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.core.http2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2Headers;

public abstract class Http2SleekHandler {
    protected void handleGET(ChannelHandlerContext ctx, Http2Headers http2Headers, String requestBody, Http2FrameStream stream) throws Exception {
        Http2Responder.reply(ctx, http2Headers, stream, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handlePOST(ChannelHandlerContext ctx, Http2Headers http2Headers, String requestBody, Http2FrameStream stream) throws Exception {
        Http2Responder.reply(ctx, http2Headers, stream, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handlePUT(ChannelHandlerContext ctx, Http2Headers http2Headers, String requestBody, Http2FrameStream stream) throws Exception {
        Http2Responder.reply(ctx, http2Headers, stream, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handlePATCH(ChannelHandlerContext ctx, Http2Headers http2Headers, String requestBody, Http2FrameStream stream) throws Exception {
        Http2Responder.reply(ctx, http2Headers, stream, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handleDELETE(ChannelHandlerContext ctx, Http2Headers http2Headers, String requestBody, Http2FrameStream stream) throws Exception {
        Http2Responder.reply(ctx, http2Headers, stream, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handleHEAD(ChannelHandlerContext ctx, Http2Headers http2Headers, String requestBody, Http2FrameStream stream) throws Exception {
        Http2Responder.reply(ctx, http2Headers, stream, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handleOPTIONS(ChannelHandlerContext ctx, Http2Headers http2Headers, String requestBody, Http2FrameStream stream) throws Exception {
        Http2Responder.reply(ctx, http2Headers, stream, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handleTRACE(ChannelHandlerContext ctx, Http2Headers http2Headers, String requestBody, Http2FrameStream stream) throws Exception {
        Http2Responder.reply(ctx, http2Headers, stream, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handleCONNECT(ChannelHandlerContext ctx, Http2Headers http2Headers, String requestBody, Http2FrameStream stream) throws Exception {
        Http2Responder.reply(ctx, http2Headers, stream, HttpResponseStatus.NOT_IMPLEMENTED);
    }
}
