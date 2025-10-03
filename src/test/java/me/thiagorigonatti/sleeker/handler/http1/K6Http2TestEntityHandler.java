/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.handler.http1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.*;
import me.thiagorigonatti.sleeker.core.http2.Http2SleekHandler;
import me.thiagorigonatti.sleeker.util.ContentType;

import java.nio.charset.StandardCharsets;

public class K6Http2TestEntityHandler extends Http2SleekHandler {
    @Override
    protected void handleGET(ChannelHandlerContext ctx, Http2Headers http2Headers, String requestBody, Http2FrameStream stream) {

        String e = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <title>Sleeker</title>
                  <style>
                    html, body {
                      height: 100%;
                      margin: 0;
                      padding: 0;
                      background: linear-gradient(45deg, #ff5f6d, #ffc371);
                      display: flex;
                      align-items: center;
                      justify-content: center;
                      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                      overflow: hidden;
                      transition: background 0.5s ease;
                    }
                
                    h1 {
                      font-size: 6rem;
                      color: white;
                      text-shadow: 0 0 20px rgba(0,0,0,0.3);
                      cursor: pointer;
                      transition: transform 0.3s ease;
                      user-select: none;
                    }
                
                    h1:hover {
                      transform: scale(1.1);
                    }
                
                    .burst {
                      position: absolute;
                      width: 100px;
                      height: 100px;
                      border-radius: 50%;
                      background: radial-gradient(circle, white 0%, transparent 70%);
                      animation: burst 0.6s ease-out forwards;
                      pointer-events: none;
                    }
                
                    @keyframes burst {
                      from {
                        opacity: 1;
                        transform: scale(0.5);
                      }
                      to {
                        opacity: 0;
                        transform: scale(5);
                      }
                    }
                  </style>
                </head>
                <body>
                
                  <h1 id="sleeker">Sleeker</h1>
                
                  <script>
                    const sleeker = document.getElementById('sleeker');
                    const body = document.body;
                
                    function getRandomColor() {
                      const r = Math.floor(Math.random() * 200 + 55);
                      const g = Math.floor(Math.random() * 200 + 55);
                      const b = Math.floor(Math.random() * 200 + 55);
                      return `rgb(${r}, ${g}, ${b})`;
                    }
                
                    function createBurst(x, y) {
                      const burst = document.createElement('div');
                      burst.className = 'burst';
                      burst.style.left = `${x - 50}px`;
                      burst.style.top = `${y - 50}px`;
                      document.body.appendChild(burst);
                
                      setTimeout(() => {
                        burst.remove();
                      }, 600);
                    }
                
                    sleeker.addEventListener('click', (e) => {
                      const color1 = getRandomColor();
                      const color2 = getRandomColor();
                      const textColor = getRandomColor();
                
                      // Change background
                      body.style.background = `linear-gradient(135deg, ${color1}, ${color2})`;
                
                      // Change text color
                      sleeker.style.color = textColor;
                
                      // Create burst effect
                      createBurst(e.clientX, e.clientY);
                    });
                  </script>
                
                </body>
                </html>
                """;

        Http2Headers responseHeaders = new DefaultHttp2Headers()
                .status(HttpResponseStatus.OK.codeAsText())
                .set(HttpHeaderNames.CONTENT_TYPE, ContentType.TEXT_PLAIN_UTF8.getMimeType());

        ctx.write(new DefaultHttp2HeadersFrame(responseHeaders, false).stream(stream));
        ByteBuf body = ctx.alloc().buffer();
        body.writeCharSequence(e, StandardCharsets.UTF_8);
        ctx.writeAndFlush(new DefaultHttp2DataFrame(body, true).stream(stream));
    }
}
