/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.handler.http1;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import me.thiagorigonatti.sleeker.core.http1.Http1SleekHandler;
import me.thiagorigonatti.sleeker.database.postgres.PostgresTest;
import me.thiagorigonatti.sleeker.model.Entity;

import java.io.IOException;

public class K6Http1TestEntityHandler extends Http1SleekHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handlePOST(ChannelHandlerContext ctx, FullHttpRequest msg) throws IOException {

        String requestBody = msg.content().toString(CharsetUtil.UTF_8);

        FullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.CREATED,
                Unpooled.copiedBuffer(requestBody, CharsetUtil.UTF_8));

        response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8")
                .set(HttpHeaderNames.CONTENT_LENGTH, requestBody.length());
        ctx.writeAndFlush(response);

        Entity entity = objectMapper.readValue(requestBody, Entity.class);

        PostgresTest.saveEntity(entity.id(), entity.level()).subscribe();
    }

    @Override
    protected void handleGET(ChannelHandlerContext ctx, FullHttpRequest msg) {


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

            FullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(e, CharsetUtil.UTF_8));

            response.headers()
                    .set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8")
                    .set(HttpHeaderNames.CONTENT_LENGTH, e.length());
            ctx.writeAndFlush(response);

    }
}
