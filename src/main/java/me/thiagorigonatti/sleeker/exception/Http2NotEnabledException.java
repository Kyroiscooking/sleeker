/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.exception;

import io.netty.handler.codec.http2.Http2Frame;

public class Http2NotEnabledException extends SleekException {

    public Http2Frame http2Frame;
    public final String responseMessage = "HTTP/2 version not enabled, there wasn't http2 context added in the server.";

    public Http2NotEnabledException(Http2Frame http2Frame) {
        this.http2Frame = http2Frame;
    }
}
