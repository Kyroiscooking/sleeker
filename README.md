# SLEEKER v0.0.9
![](assets/png/logo-v0.0.9.png)

[TEST](TEST.md)
```java
public class Test {
    public static void main(String[] args) throws Exception {

        // Creating instances of Http1 and Http2 handler classes.
        final Http1ExampleHandler http1ExampleHandler = new Http1ExampleHandler();
        final Http2ExampleHandler http2ExampleHandler = new Http2ExampleHandler();

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
```
```md
2025-09-29 10:00:01 [INFO ] [main] m.t.s.c.SleekerServer: Sleeker server running at: http://localhost:8080
```
### HTTP1.1 HANDLER
```java
public class Http1ExampleHandler extends Http1SleekHandler {

    private static final Logger LOGGER = LogManager.getLogger(Http1ExampleHandler.class);
    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void handleGET(Http1Request http1Request, Http1Response http1Response) {

        stringBuilder.setLength(0);

        stringBuilder
                .append("\r\n")
                .append("--------HTTP/1.1 REQUEST--------")
                .append("\r\n")
                .append("ip_port: ")
                .append(http1Request.remoteAddress().getHostString())
                .append(":")
                .append(http1Request.remoteAddress().getPort())
                .append("\r\n")
                .append("method: ").append(http1Request.method())
                .append("\r\n")
                .append("path: ").append(http1Request.path())
                .append("\r\n");

        for (Map.Entry<String, String> header : http1Request.headers()) {
            stringBuilder.append(header.getKey()).append(": ").append(header.getValue())
                    .append("\r\n");
        }

        http1Response.addHeader(HttpHeaderNames.CONTENT_TYPE, ContentType.TEXT_PLAIN_UTF8.getMimeType());
        http1Response.setBody("Hello from HTTP/1.1");
        http1Response.reply(HttpResponseStatus.OK);

        stringBuilder
                .append(http1Request.body())
                .append("\r\n")
                .append("--------------------------------")
                .append("\r\n");

        LOGGER.info(stringBuilder);
    }

    @Override
    protected void handlePOST(Http1Request http1Request, Http1Response http1Response) throws JsonProcessingException {

        if (http1Request.body().isEmpty() || http1Request.body().isBlank()) {

            throw new HttpSleekException.BaseBuilder<>()
                    .contentType(ContentType.APPLICATION_JSON_UTF8)
                    .httpResponseStatus(HttpResponseStatus.BAD_REQUEST)
                    .responseMessage(new ObjectMapper().writeValueAsString(Map.of("errorMessage", "Body cannot be empty or blank")))
                    .build();
        }

        stringBuilder.setLength(0);

        stringBuilder
                .append("\r\n")
                .append("--------HTTP/1.1 REQUEST--------")
                .append("\r\n")
                .append("ip_port: ")
                .append(http1Request.remoteAddress().getHostString())
                .append(":")
                .append(http1Request.remoteAddress().getPort())
                .append("\r\n")
                .append("method: ").append(http1Request.method())
                .append("\r\n")
                .append("path: ").append(http1Request.path())
                .append("\r\n");

        for (Map.Entry<String, String> header : http1Request.headers()) {
            stringBuilder.append(header.getKey()).append(": ").append(header.getValue())
                    .append("\r\n");
        }

        http1Response.addHeader(HttpHeaderNames.CONTENT_TYPE, ContentType.TEXT_PLAIN_UTF8.getMimeType());
        http1Response.setBody("Saved! (HTTP/1.1)");
        http1Response.reply(HttpResponseStatus.CREATED);

        stringBuilder
                .append(http1Request.body())
                .append("\r\n")
                .append("--------------------------------")
                .append("\r\n");

        LOGGER.info(stringBuilder);
    }
}
```
### HTTP1.1 REQUEST
```md
2025-10-08 20:18:31 [INFO ] [pool-2-thread-1] m.t.s.a.Http1ExampleHandler:
--------HTTP/1.1 REQUEST--------
ip_port: 127.0.0.1:34492
method: POST
path: /http1_get_post
Content-Type: application/json
User-Agent: PostmanRuntime/7.48.0
Accept: */*
Postman-Token: 01688100-405d-4c89-b7ad-0747e6b01b98
Host: localhost:8080
Accept-Encoding: gzip, deflate, br
Connection: keep-alive
Content-Length: 37
{
"id": "abc",
"level": 123
}
--------------------------------
```
### HTTP2 HANDLER
```java
public class Http2ExampleHandler extends Http2SleekHandler {

    private static final Logger LOGGER = LogManager.getLogger(Http2ExampleHandler.class);
    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void handleGET(Http2Request http2Request, Http2Response http2Response) {

        stringBuilder.setLength(0);

        stringBuilder
                .append("\r\n")
                .append("--------HTTP/2 REQUEST--------")
                .append("\r\n")
                .append("ip_port: ")
                .append(http2Request.remoteAddress().getHostString())
                .append(":")
                .append(http2Request.remoteAddress().getPort())
                .append("\r\n")
                .append("method: ").append(http2Request.method())
                .append("\r\n")
                .append("path: ").append(http2Request.path())
                .append("\r\n");

        for (Map.Entry<CharSequence, CharSequence> header : http2Request.headers()) {
            stringBuilder.append(header.getKey()).append(": ").append(header.getValue())
                    .append("\r\n");
        }

        http2Response.addHeader(HttpHeaderNames.CONTENT_TYPE, ContentType.TEXT_PLAIN_UTF8.getMimeType());
        http2Response.setBody("Hello from HTTP/2");
        http2Response.reply(HttpResponseStatus.OK);

        stringBuilder
                .append(http2Request.body())
                .append("\r\n")
                .append("--------------------------------")
                .append("\r\n");

        LOGGER.info(stringBuilder);
    }

    @Override
    protected void handlePOST(Http2Request http2Request, Http2Response http2Response) throws JsonProcessingException {

        if (http2Request.body().isEmpty() || http2Request.body().isBlank()) {

            throw new HttpSleekException.BaseBuilder<>()
                    .contentType(ContentType.APPLICATION_JSON_UTF8)
                    .httpResponseStatus(HttpResponseStatus.BAD_REQUEST)
                    .responseMessage(new ObjectMapper().writeValueAsString(Map.of("errorMessage", "Body cannot be empty or blank")))
                    .build();
        }

        stringBuilder.setLength(0);

        stringBuilder
                .append("\r\n")
                .append("--------HTTP/2 REQUEST--------")
                .append("\r\n")
                .append("ip_port: ")
                .append(http2Request.remoteAddress().getHostString())
                .append(":")
                .append(http2Request.remoteAddress().getPort())
                .append("\r\n")
                .append("method: ").append(http2Request.method())
                .append("\r\n")
                .append("path: ").append(http2Request.path())
                .append("\r\n");

        for (Map.Entry<CharSequence, CharSequence> header : http2Request.headers()) {
            stringBuilder.append(header.getKey()).append(": ").append(header.getValue())
                    .append("\r\n");
        }

        http2Response.addHeader(HttpHeaderNames.CONTENT_TYPE, ContentType.TEXT_PLAIN_UTF8.getMimeType());
        http2Response.setBody("Saved! (HTTP/2)");
        http2Response.reply(HttpResponseStatus.CREATED);

        stringBuilder
                .append(http2Request.body())
                .append("\r\n")
                .append("--------------------------------")
                .append("\r\n");

        LOGGER.info(stringBuilder);
    }
}
```
### HTTP2 REQUEST
```md
2025-10-08 20:19:26 [INFO ] [pool-2-thread-1] m.t.s.a.Http2ExampleHandler:
--------HTTP/2 REQUEST--------
ip_port: 127.0.0.1:36578
method: POST
path: /http2_post
:path: /http2_post
:method: POST
:authority: localhost:8080
:scheme: https
content-type: application/json
user-agent: PostmanRuntime/7.48.0
accept: */*
postman-token: 6ac23e59-403b-4387-8973-71189cb47647
accept-encoding: gzip, deflate, br
content-length: 37
{
"id": "abc",
"level": 123
}
--------------------------------
```
