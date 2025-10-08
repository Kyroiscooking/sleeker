/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.io;

public enum ServerIo {

    TYPE_IOURING,
    TYPE_EPOLL,
    TYPE_KQUEUE,
    TYPE_LOCAL,
    TYPE_NIO,

    TYPE_EPOLL_UNIX_DOMAIN_SOCKET,
    TYPE_KQUEUE_UNIX_DOMAIN_SOCKET,
    TYPE_NIO_UNIX_DOMAIN_SOCKET
}
