/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.core.http2;

import io.netty.handler.codec.http.HttpMethod;

import java.util.List;

public record Http2Setup(Http2SleekHandler http2SleekHandler, List<HttpMethod> httpMethodList) {
}
