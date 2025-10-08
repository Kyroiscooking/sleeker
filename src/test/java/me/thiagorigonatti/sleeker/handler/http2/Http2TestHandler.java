/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.handler.http2;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import me.thiagorigonatti.sleeker.core.http2.Http2Request;
import me.thiagorigonatti.sleeker.core.http2.Http2Response;
import me.thiagorigonatti.sleeker.core.http2.Http2SleekHandler;
import me.thiagorigonatti.sleeker.util.ContentType;

public class Http2TestHandler extends Http2SleekHandler {

    @Override
    protected void handleGET(Http2Request http2Request, Http2Response http2Response) {
        http2Response.addHeader(HttpHeaderNames.CONTENT_TYPE, ContentType.TEXT_PLAIN_UTF8.getMimeType());
        http2Response.setBody("Hello from HTTP/2 test");
        http2Response.reply(HttpResponseStatus.OK);
    }
}
