/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

public class SleekException extends RuntimeException {

    public final HttpResponseStatus status = HttpResponseStatus.HTTP_VERSION_NOT_SUPPORTED;
}
