/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.handler.http1;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.*;
import io.netty.util.CharsetUtil;
import me.thiagorigonatti.sleeker.core.http2.Http2SleekHandler;

public class K6Http2TestEntityHandler extends Http2SleekHandler {
    @Override
    protected void handleGET(ChannelHandlerContext ctx, Http2Headers http2Headers, String requestBody, Http2FrameStream stream) throws Exception {

        String e = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n" +
                "    <title>xbox360-retitler</title>\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />\n" +
                "    <script type=\"text/javascript\" src=\"sha1-generator.js\"></script>\n" +
                "    <script type=\"text/javascript\" src=\"xbox360-retitler.js\"></script>\n" +
                "    <script type=\"text/javascript\" src=\"toast.js\"></script>\n" +
                "    <script type=\"text/javascript\" src=\"app.js\"></script>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div id=\"root\">\n" +
                "      <div id=\"app-heading\"><span>xbox360-retitler</span></div>\n" +
                "      <div class=\"files-container\">\n" +
                "        <div id=\"files\">\n" +
                "          <div id=\"files-head\" class=\"grid-container\">\n" +
                "            <span>#</span>\n" +
                "            <div class=\"checkbox-head-container\">\n" +
                "              <input\n" +
                "                id=\"checkbox-head\"\n" +
                "                name=\"checkbox-head\"\n" +
                "                type=\"checkbox\"\n" +
                "              /><label for=\"checkbox-head\"> select all</label>\n" +
                "            </div>\n" +
                "            <span>file name</span>\n" +
                "            <span>old title</span>\n" +
                "            <span>new title</span>\n" +
                "            <span>delete</span>\n" +
                "          </div>\n" +
                "          <div id=\"files-body\">\n" +
                "            <div id=\"file-upload\" class=\"grid-container\">\n" +
                "              <span>drag and drop file(s) or</span>\n" +
                "              <input\n" +
                "                id=\"file-input\"\n" +
                "                type=\"file\"\n" +
                "                multiple\n" +
                "                onchange=\"handleUploadFiles(this.files)\"\n" +
                "              />\n" +
                "              <label for=\"file-input\">select file(s) to upload</label>\n" +
                "            </div>\n" +
                "            <div id=\"blank-filler\"></div>\n" +
                "          </div>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "      <div class=\"download-container\">\n" +
                "        <button\n" +
                "          id=\"download-button\"\n" +
                "          type=\"button\"\n" +
                "          onclick=\"handleDownload()\"\n" +
                "          disabled\n" +
                "        >\n" +
                "          download selected file(s)\n" +
                "        </button>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </body>\n" +
                "</html>\n";

        Http2Headers responseHeaders = new DefaultHttp2Headers()
                .status(HttpResponseStatus.OK.codeAsText())
                .set(HttpHeaderNames.CONTENT_TYPE, "text/html");

        HttpMethod httpMethod = HttpMethod.valueOf(http2Headers.method().toString());
        boolean end = httpMethod.equals(HttpMethod.HEAD);
        ctx.write(new DefaultHttp2HeadersFrame(responseHeaders, end).stream(stream));

        if (!end)
            ctx.write(new DefaultHttp2DataFrame(
                    Unpooled.copiedBuffer(e, CharsetUtil.UTF_8), true
            ).stream(stream));
        ctx.flush();
    }
}
