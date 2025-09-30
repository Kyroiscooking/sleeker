/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.core.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.*;
import io.netty.util.CharsetUtil;
import me.thiagorigonatti.sleeker.core.SleekerServer;
import me.thiagorigonatti.sleeker.exception.Http2NotEnabledException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
public class Http2RouterHandler extends SimpleChannelInboundHandler<Http2Frame> {

    private static final Logger LOGGER = LogManager.getLogger(Http2RouterHandler.class);

    public final Map<String, Http2Setup> handlers = new HashMap<>();
    private final Map<String, Http2Request> http2RequestMap = new ConcurrentHashMap<>();
    private boolean useHttp2;

    public boolean isUseHttp2() {
        return useHttp2;
    }

    public void setUseHttp2(boolean useHttp2) {
        this.useHttp2 = useHttp2;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        if (cause instanceof Http2NotEnabledException http2NotEnabledException) {
            Http2Frame http2Frame = http2NotEnabledException.http2Frame;
            HttpResponseStatus status = http2NotEnabledException.status;
            String responseBody = http2NotEnabledException.responseMessage;

            LOGGER.warn(responseBody);
            if (ctx.channel().isActive()) {
                Http2Headers headers = new DefaultHttp2Headers()
                        .status(status.codeAsText())
                        .set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

                Http2HeadersFrame http2HeadersFrame = (Http2HeadersFrame) http2Frame;
                HttpMethod httpMethod = HttpMethod.valueOf(http2HeadersFrame.headers().method().toString());
                boolean end = httpMethod.equals(HttpMethod.HEAD);

                ctx.write(new DefaultHttp2HeadersFrame(headers, end).stream(http2HeadersFrame.stream()));

                if (!end)
                    ctx.write(new DefaultHttp2DataFrame(
                            Unpooled.copiedBuffer(responseBody, CharsetUtil.UTF_8), true)
                            .stream(http2HeadersFrame.stream()));

                ctx.flush();
            } else {
                ctx.close();
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Http2Frame msg) {

        if (msg instanceof Http2HeadersFrame headersFrame) {

            if (!this.isUseHttp2()) {
                throw new Http2NotEnabledException(msg);
            }

            String id = ctx.channel().id().asShortText() + "_" + headersFrame.stream().id();

            String path = headersFrame.headers().path().toString();
            Http2Setup http2Setup = handlers.get(path);
            HttpMethod httpMethod = HttpMethod.valueOf(headersFrame.headers().method().toString());

            if (http2Setup == null) {
                SleekerServer.EXECUTOR_SERVICE.execute(() -> Http2Responder.reply(ctx, headersFrame.headers(), headersFrame.stream(), HttpResponseStatus.NOT_FOUND));
                return;
            }

            if (headersFrame.isEndStream()) {
                if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.GET) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handleGET(ctx, headersFrame.headers(), "", headersFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.POST) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handlePOST(ctx, headersFrame.headers(), "", headersFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.PUT) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handlePUT(ctx, headersFrame.headers(), "", headersFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.PATCH) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handlePATCH(ctx, headersFrame.headers(), "", headersFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.DELETE) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handleDELETE(ctx, headersFrame.headers(), "", headersFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.HEAD) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handleHEAD(ctx, headersFrame.headers(), "", headersFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.OPTIONS) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handleOPTIONS(ctx, headersFrame.headers(), "", headersFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.TRACE) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handleTRACE(ctx, headersFrame.headers(), "", headersFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.CONNECT) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handleCONNECT(ctx, headersFrame.headers(), "", headersFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else {
                    SleekerServer.EXECUTOR_SERVICE.execute(() ->
                            Http2Responder.reply(ctx, headersFrame.headers(), headersFrame.stream(), HttpResponseStatus.METHOD_NOT_ALLOWED));
                }
            } else {
                ByteBuf bodyBuffer = Unpooled.buffer();
                http2RequestMap.put(id, new Http2Request(headersFrame.headers(), bodyBuffer));
            }


        } else if (msg instanceof Http2DataFrame dataFrame) {

            String id = ctx.channel().id().asShortText() + "_" + dataFrame.stream().id();
            Http2Request context = http2RequestMap.get(id);

            if (context == null) {
                return;
            }

            context.body().writeBytes(dataFrame.content());

            String body = context.body().toString(CharsetUtil.UTF_8);
            Http2Headers headers = context.headers();
            Http2Setup http2Setup = handlers.get(headers.path().toString());
            HttpMethod httpMethod = HttpMethod.valueOf(headers.method().toString());

            if (dataFrame.isEndStream()) {

                if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.GET) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handleGET(ctx, headers, body, dataFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.POST) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handlePOST(ctx, headers, body, dataFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.PUT) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handlePUT(ctx, headers, body, dataFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.PATCH) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handlePATCH(ctx, headers, body, dataFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.DELETE) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handleDELETE(ctx, headers, body, dataFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.HEAD) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handleHEAD(ctx, headers, body, dataFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.OPTIONS) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handleOPTIONS(ctx, headers, body, dataFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.TRACE) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handleTRACE(ctx, headers, body, dataFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (http2Setup.httpMethodList().contains(httpMethod) && httpMethod == HttpMethod.CONNECT) {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> {
                        try {
                            http2Setup.http2SleekHandler().handleCONNECT(ctx, headers, body, dataFrame.stream());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else {
                    SleekerServer.EXECUTOR_SERVICE.execute(() -> Http2Responder.reply(ctx, headers, dataFrame.stream(), HttpResponseStatus.METHOD_NOT_ALLOWED));
                }

                context.body().release();
                http2RequestMap.remove(id);
            }

        } else if (msg instanceof Http2ResetFrame resetFrame) {
            String id = ctx.channel().id().asShortText() + "_" + resetFrame.stream().id();
            Http2Request req = http2RequestMap.remove(id);
            if (req != null) {
                req.body().release();
            }
        }
    }
}
