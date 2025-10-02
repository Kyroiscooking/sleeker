/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.io;

import io.netty.channel.IoHandlerFactory;
import io.netty.channel.ServerChannel;
import io.netty.channel.uring.IoUring;
import io.netty.channel.uring.IoUringIoHandler;
import io.netty.channel.uring.IoUringIoHandlerConfig;
import io.netty.channel.uring.IoUringServerSocketChannel;

public class IoUringIo implements SleekIo {

    private final IoUringIoHandlerConfig config;

    public IoUringIo(IoUringIoHandlerConfig config) {
        this.config = config;
    }

    @Override
    public Class<? extends ServerChannel> getServerChannelClass() {
        return IoUringServerSocketChannel.class;
    }

    @Override
    public IoHandlerFactory getIoHandlerFactory() {
        return IoUringIoHandler.newFactory(config);
    }

    @Override
    public boolean isAvailable() {
        return IoUring.isAvailable();
    }
}
