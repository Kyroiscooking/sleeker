/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.tls;

import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.ssl.*;

import java.nio.file.Path;

public final class ServerSsl {

    private ServerSsl() {
        throw new AssertionError("Instantiation of an utility class");
    }

    public static SslContext create(Path certOrChainFilePath, Path privKeyFilePath) throws Exception {
        return SslContextBuilder.forServer(certOrChainFilePath.toFile(), privKeyFilePath.toFile())
                .sslProvider(SslProvider.OPENSSL)
                .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                .applicationProtocolConfig(new ApplicationProtocolConfig(
                        ApplicationProtocolConfig.Protocol.ALPN,
                        ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                        ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                        ApplicationProtocolNames.HTTP_1_1,
                        ApplicationProtocolNames.HTTP_2))
                .build();
    }
}
