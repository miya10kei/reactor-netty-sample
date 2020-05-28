package com.miya10kei.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Flux<User> list();

    Mono<User> fetchById(long id);

    Mono<User> create(String name);
}
