/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.core;

import me.thiagorigonatti.sleeker.config.Yml;
import me.thiagorigonatti.sleeker.tls.ServerSsl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

public class Config {

    private static final Logger LOGGER = LogManager.getLogger(Config.class);

    public static Boolean HTTP2_PRIORITY;

    public static void init() {
        URL url = ServerSsl.class.getClassLoader().getResource("sleeker.yml");

        try {
            File file;
            if (url != null && (file = new File(url.toURI())).exists()) {
                Map<String, Object> config = new Yml().read(file);
                HTTP2_PRIORITY = (Boolean) config.get("sleeker.http2-priority");
            } else {
                LOGGER.warn("Could not find sleeker.yml, now using default values.");
                HTTP2_PRIORITY = true;
            }

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
