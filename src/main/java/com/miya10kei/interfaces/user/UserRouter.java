package com.miya10kei.interfaces.user;

import com.jsoniter.output.JsonStream;
import com.miya10kei.domain.UserRepository;
import java.util.Objects;
import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;

import static com.jsoniter.JsonIterator.deserialize;
import static java.lang.Long.parseLong;

public class UserRouter {

    private final UserRepository repository;

    public UserRouter(UserRepository repository) {
        this.repository = repository;
    }

    public void configureRoutes(HttpServerRoutes routes) {
        routes.get("/users", this::getUsers)
                .get("/users/{id}", this::getUser)
                .post("/users", this::createUsers);
    }

    private Publisher<Void> getUsers(HttpServerRequest request, HttpServerResponse response) {
        return response.sse().sendString(repository.list().map(JsonStream::serialize));
    }

    private Publisher<Void> getUser(HttpServerRequest request, HttpServerResponse response) {
        return response.sendString(repository
                .fetchById(parseLong(Objects.requireNonNull(request.param("id"))))
                .map(JsonStream::serialize));
    }

    private Publisher<Void> createUsers(HttpServerRequest request, HttpServerResponse response) {
        return response.sendString(
                request.receive().asString()
                        .map(body -> deserialize(body, UserRequest.class))
                        .flatMap(ur -> repository.create(ur.getName()))
                        .map(user -> new UserResponse(user.getId(), user.getName()))
                        .map(JsonStream::serialize));

    }
}
