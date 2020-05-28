package com.miya10kei.infrastructure.persistence.user;

import com.miya10kei.infrastructure.user.UserEntity;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class UserDao {
    private final ConnectionFactory factory;

    public UserDao(ConnectionFactory factory) {
        this.factory = factory;
    }

    public Flux<UserEntity> findAll() {
        return Mono.from(factory.create())
                .flatMap(conn -> Mono.from(conn.createStatement("SELECT * FROM user")
                        .execute())
                        .doFinally(st -> Mono.from(conn.close()).then(Mono.empty())))
                .flatMapMany(result -> result.map((row, meta) ->
                        new UserEntity(row.get("id", Long.class), row.get("name", String.class))));
    }

    public Mono<UserEntity> findById(long id) {
        return Mono.from(factory.create())
                .flatMap(conn -> Mono.from(conn.createStatement("SELECT * FROM user WHERE  id = $1")
                        .bind("$1", id)
                        .execute())
                        .doFinally(st -> Mono.just(conn.close()).then(Mono.empty())))
                .map(result -> result.map((row, meta) ->
                        new UserEntity(row.get("id", Long.class), row.get("name", String.class))))
                .flatMap(Mono::from);
    }

    public Mono<UserEntity> create(UserEntity entity) {
        return Mono.from(factory.create())
                .flatMap(conn -> Mono.from(conn.beginTransaction())
                        .then(Mono.from(conn.createStatement("INSERT INTO user(name) VALUES($1)")
                                .bind("$1", entity.getName())
                                .returnGeneratedValues("id")
                                .execute()))
                        .map(result -> result.map((row, meta) ->
                                new UserEntity(row.get("id", Long.class), entity.getName())))
                        .flatMap(Mono::from)
                        .delayUntil(r -> conn.commitTransaction())
                        .doFinally(st -> Mono.from(conn.close()).then(Mono.empty())));

    }
}
