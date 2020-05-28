package com.miya10kei.infrastructure.user;

import com.miya10kei.domain.User;
import com.miya10kei.domain.UserRepository;
import com.miya10kei.infrastructure.persistence.user.UserDao;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class UserRepositoryImpl implements UserRepository {

    private final UserDao dao;

    public UserRepositoryImpl(UserDao dao) {
        this.dao = dao;
    }

    @Override
    public Flux<User> list() {
        return dao.findAll().map(e -> new User(e.getId(), e.getName()));
    }

    @Override
    public Mono<User> fetchById(long id) {
        return dao.findById(id).map(e -> new User(e.getId(), e.getName()));
    }

    @Override
    public Mono<User> create(String name) {
        return dao.create(new UserEntity(null, name))
                .map(e -> new User(e.getId(), e.getName()));
    }
}
