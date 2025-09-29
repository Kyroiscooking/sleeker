/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.io;

import io.netty.channel.IoHandler;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueIoHandler;
import io.netty.channel.kqueue.KQueueServerDomainSocketChannel;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.local.LocalIoHandler;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerDomainSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.uring.IoUring;
import io.netty.channel.uring.IoUringIoHandler;
import io.netty.channel.uring.IoUringServerSocketChannel;

public enum ServerIO {

    TypeIoUring(IoUring.isAvailable(), IoUringIoHandler.class, IoUringServerSocketChannel.class),
    TypeEpoll(Epoll.isAvailable(), EpollIoHandler.class, EpollServerSocketChannel.class),
    TypeKQueue(KQueue.isAvailable(), KQueueIoHandler.class, KQueueServerSocketChannel.class),
    TypeLocal(true, LocalIoHandler.class, LocalServerChannel.class),
    TypeNio(true, NioIoHandler.class, NioServerSocketChannel.class),

    TypeEpollUnixDomainSocket(Epoll.isAvailable(), EpollIoHandler.class, EpollServerDomainSocketChannel.class),
    TypeKQueueUnixDomainSocket(KQueue.isAvailable(), KQueueIoHandler.class, KQueueServerDomainSocketChannel.class),
    TypeNioUnixDomainSocket(true, NioIoHandler.class, NioServerDomainSocketChannel.class);

    private final boolean available;
    private final Class<? extends IoHandler> ioHandlerClass;
    private final Class<? extends ServerChannel> serverChannelClass;

    ServerIO(boolean available, Class<? extends IoHandler> ioHandlerClass, Class<? extends ServerChannel> serverChannelClass) {
        this.available = available;
        this.ioHandlerClass = ioHandlerClass;
        this.serverChannelClass = serverChannelClass;
    }

    public boolean isAvailable() {
        return available;
    }

    public Class<? extends IoHandler> getIoHandlerClass() {
        return ioHandlerClass;
    }

    public Class<? extends ServerChannel> getServerChannelClass() {
        return serverChannelClass;
    }
}
