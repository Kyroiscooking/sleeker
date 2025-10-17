/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.core.http1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import jakarta.validation.constraints.NotNull;
import me.thiagorigonatti.sleeker.core.HeaderAddeable;

public class Http1Response implements HeaderAddeable {

    private final ChannelHandlerContext ctx;
    private final ByteBuf buf;
    private final HttpVersion httpVersion;
    private final HttpHeaders httpHeaders;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public Http1Response(@NotNull ChannelHandlerContext ctx, @NotNull HttpVersion httpVersion) {
        this.ctx = ctx;
        this.buf = ctx.alloc().buffer();
        this.httpVersion = httpVersion;
        this.httpHeaders = new DefaultHttpHeaders();
    }

    public void setBody(@NotNull String body) {
        this.buf.writeCharSequence(body, CharsetUtil.UTF_8);
    }

    public void addHeader(@NotNull CharSequence httpHeaderName, @NotNull CharSequence httpHeaderValue) {
        this.httpHeaders.add(httpHeaderName, httpHeaderValue);
    }

    public void reply(@NotNull HttpResponseStatus httpResponseStatus) {
        final FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(this.httpVersion, httpResponseStatus, this.buf);
        fullHttpResponse.headers().add(HttpHeaderNames.CONTENT_LENGTH, this.buf.readableBytes());
        fullHttpResponse.headers().add(this.httpHeaders);
        ctx.writeAndFlush(fullHttpResponse);
    }
}
