/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.aaa_dev_test;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import me.thiagorigonatti.sleeker.core.SleekerServer;
import me.thiagorigonatti.sleeker.guard.Cors;
import me.thiagorigonatti.sleeker.io.ServerIo;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Set;

public class Test {
    public static void main(String[] args) throws Exception {

        // Creating instances of Http1 and Http2 handler classes.
        final Http1ExampleHandler http1ExampleHandler = new Http1ExampleHandler();
        final Http2ExampleHandler http2ExampleHandler = new Http2ExampleHandler();

        //Creating Cors instance with origin, allowed methods, allowed headers, sending cookies, and cache time.
        final Cors cors = new Cors("http://localhost:54321",
                Set.of(HttpMethod.GET, HttpMethod.POST),
                Set.of(HttpHeaderNames.AUTHORIZATION), true, 3600L);

        // Creates a builder object for SleekerServer.
        new SleekerServer.Builder()
                // Adds an HTTP context, with an endpoint, a handler that will process the request,
                // and supported HTTP methods.
                .addHttp1Context("/http1_get_post", http1ExampleHandler,
                        HttpMethod.GET,
                        HttpMethod.POST)

                .addHttp1Context("/http1_put_patch_delete", http1ExampleHandler,
                        HttpMethod.PUT,
                        HttpMethod.PATCH,
                        HttpMethod.DELETE)

                .addHttp1Context("/http1_head", http1ExampleHandler, HttpMethod.HEAD)

                // Adds CORS for both http1 and http2
                .withCors(cors)

                // Configures SSL with cert file and private key.
                .withSsl(Path.of("localhost-cert.pem"), Path.of("localhost-key.pem"))

                .addHttp2Context("/http2_get", http2ExampleHandler, HttpMethod.GET)
                .addHttp2Context("/http2_post", http2ExampleHandler, HttpMethod.POST)

                // Builds a SleekerServer object.
                .build()

                // Starts the server with the address and port, as well as the type of I/O used.
                .startServer(new InetSocketAddress("localhost", 8080), ServerIo.TYPE_IOURING);
    }
}
