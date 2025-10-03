/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.util;

public enum ContentType {

    TEXT_PLAIN_UTF8("text/plain; charset=utf-8"),
    APPLICATION_JSON_UTF8("application/json; charset=utf-8");

    private final CharSequence mimeType;

    public CharSequence getMimeType() {
        return mimeType;
    }

    ContentType(CharSequence contentType) {
        this.mimeType = contentType;
    }
}
