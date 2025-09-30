/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.database.postgres;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import me.thiagorigonatti.sleeker.model.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public class PostgresTest {

    private static final Logger LOGGER = LogManager.getLogger(PostgresTest.class);

    public static Mono<Void> createTableIfNotExists() {
        ConnectionPool pool = R2DBCPool.getPool();

        return Mono.usingWhen(
                pool.create(),
                conn ->
                        Mono.from(conn.createStatement("""
                                            SELECT EXISTS (
                                                SELECT FROM information_schema.tables
                                                WHERE table_schema = 'public' AND table_name = 'entity'
                                            )
                                        """).execute()
                                ).flatMap(result -> Mono.from(result.map((row, metadata) -> row.get(0, Boolean.class))))
                                .flatMap(exists -> {
                                    if (Boolean.TRUE.equals(exists)) {
                                        LOGGER.info("Table 'entity' already exists.");
                                        return Mono.empty();
                                    } else {
                                        return Mono.from(conn.createStatement("""
                                                            CREATE UNLOGGED TABLE entity (
                                                                id VARCHAR(36) PRIMARY KEY,
                                                                level INTEGER NOT NULL)
                                                        """).execute())
                                                .flatMap(res -> Mono.from(res.getRowsUpdated()))
                                                .doOnSuccess(v -> LOGGER.info("Table 'entity' created successfully!"))
                                                .then();
                                    }
                                }),
                Connection::close
        );
    }


    public static Mono<Long> saveEntity(String id, int level) {
        ConnectionPool pool = R2DBCPool.getPool();

        return Mono.usingWhen(
                pool.create(),
                conn -> Mono
                        .from(conn.createStatement("INSERT INTO entity VALUES ($1, $2)")
                                .bind(0, id)
                                .bind(1, level)
                                .execute()
                        ).flatMap(r -> Mono.from(r.getRowsUpdated())),
                Connection::close
        );
    }

    public static Mono<Void> truncateEntityTable() {
        ConnectionPool pool = R2DBCPool.getPool();

        return Mono.usingWhen(
                pool.create(),
                conn -> Mono.from(conn.createStatement("TRUNCATE TABLE entity").execute())
                        .then(),
                Connection::close
        );
    }

    public static Mono<Entity> findEntityById(String id) {
        ConnectionPool pool = R2DBCPool.getPool();

        return Mono.usingWhen(
                pool.create(),
                conn -> Mono.from(
                        conn.createStatement("SELECT id, level FROM entity WHERE id = $1")
                                .bind(0, id)
                                .execute()
                ).flatMap(result -> Mono.from(result.map((Row row, RowMetadata metadata) ->
                        new Entity(
                                row.get("id", String.class),
                                row.get("level", Integer.class)
                        )
                ))),
                Connection::close
        ).switchIfEmpty(Mono.error(new IOException("Entity not found with id: " + id)));
    }

    public static Mono<List<Entity>> findAll() {
        ConnectionPool pool = R2DBCPool.getPool();

        return Mono.usingWhen(
                pool.create(),
                conn -> Flux.from(
                                conn.createStatement("SELECT id, level FROM entity")
                                        .execute()
                        )
                        .flatMap(result -> result.map((Row row, RowMetadata metadata) ->
                                new Entity(
                                        row.get("id", String.class),
                                        row.get("level", Integer.class)
                                )
                        ))
                        .collectList(),
                Connection::close
        ).switchIfEmpty(Mono.error(new IOException("Entities not found")));
    }
}
