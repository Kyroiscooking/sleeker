/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.database.postgres;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;

import java.time.Duration;

public class R2DBCPool {

    private static final ConnectionPool pool;

    static {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.parse("r2dbc:postgresql://localhost:5432/sleeker_test")
                .mutate()
                .option(ConnectionFactoryOptions.USER, "admin")
                .option(ConnectionFactoryOptions.PASSWORD, "123")
                .option(ConnectionFactoryOptions.DATABASE, "sleeker_test")
                .build();

        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(ConnectionFactories.get(options))
                .initialSize(2)
                .maxSize(10)
                .maxIdleTime(Duration.ofMinutes(30))
                .build();

        pool = new ConnectionPool(configuration);
    }

    public static ConnectionPool getPool() {
        return pool;
    }

}
