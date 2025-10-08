/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.core;

import io.netty.channel.uring.IoUringIoHandlerConfig;
import me.thiagorigonatti.sleeker.config.Yml;
import me.thiagorigonatti.sleeker.io.EpollIo;
import me.thiagorigonatti.sleeker.io.IoUringIo;
import me.thiagorigonatti.sleeker.io.ServerIo;
import me.thiagorigonatti.sleeker.io.SleekIo;
import me.thiagorigonatti.sleeker.tls.ServerSsl;

import java.io.File;
import java.net.URL;
import java.util.Map;

public class Config {

    private static Map<String, Object> sleekerYml;
    private static boolean http2Priority;

    private Config() {
        throw new AssertionError("Instantiation of an utility class");
    }

    public static boolean isHttp2Priority() {
        return http2Priority;
    }

    public static void init() {

        URL url = ServerSsl.class.getClassLoader().getResource("sleeker.yml");
        File file;

        if (url != null && (file = new File(url.getPath())).exists()) {
            sleekerYml = new Yml().read(file);
            http2Priority = (boolean) sleekerYml.get("sleeker.server.http2_priority");
        } else {
            http2Priority = true;
        }
    }

    public static SleekIo getSleekIo(final ServerIo serverIo) {

        switch (serverIo) {
            case TYPE_IOURING -> {
                final IoUringIoHandlerConfig handlerConfig = new IoUringIoHandlerConfig();
                final int ringSize = sleekerYml.get("sleeker.server.io.io_uring.ring_size") != null
                        ? (int) sleekerYml.get("sleeker.server.io.io_uring.ring_size")
                        : handlerConfig.getRingSize();

                final int cqSize = sleekerYml.get("sleeker.server.io.io_uring.cq_size") != null
                        ? (int) sleekerYml.get("sleeker.server.io.io_uring.cq_size")
                        : handlerConfig.getCqSize();

                handlerConfig
                        .setRingSize(ringSize)
                        .setCqSize(cqSize);

                return new IoUringIo(handlerConfig);
            }
            default -> {
                return new EpollIo();
            }
        }
    }
}
