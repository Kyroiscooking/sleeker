/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.core.http1;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public abstract class Http1SleekHandler {

    protected void handleGET(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        Http1Responder.reply(ctx, request, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handlePOST(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        Http1Responder.reply(ctx, request, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handlePUT(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        Http1Responder.reply(ctx, request, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handlePATCH(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        Http1Responder.reply(ctx, request, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handleDELETE(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        Http1Responder.reply(ctx, request, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handleHEAD(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        Http1Responder.reply(ctx, request, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handleOPTIONS(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        Http1Responder.reply(ctx, request, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handleTRACE(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        Http1Responder.reply(ctx, request, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handleCONNECT(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        Http1Responder.reply(ctx, request, HttpResponseStatus.NOT_IMPLEMENTED);
    }
}
