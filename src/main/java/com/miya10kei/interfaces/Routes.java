package com.miya10kei.interfaces;

import com.miya10kei.infrastructure.persistence.user.UserDao;
import com.miya10kei.infrastructure.user.UserRepositoryImpl;
import com.miya10kei.interfaces.user.UserRouter;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import java.util.function.Consumer;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRoutes;

import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PROTOCOL;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

public class Routes implements Consumer<HttpServerRoutes> {

    private final UserRouter userRouter;

    public Routes() {
        var factory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "h2")
                .option(PROTOCOL, "mem")
                .option(DATABASE, "r2dbc:h2:mem//test")
                .option(USER, "sa")
                .option(PASSWORD, "")
                .build());
        Mono.from(factory.create())
                .flatMap(conn -> Mono.from(conn.createStatement("CREATE TABLE IF NOT EXISTS user(id IDENTITY PRIMARY KEY , name VARCHAR(255))")
                        .execute())
                        .doFinally(st -> Mono.from(conn.close()).then(Mono.empty())))
                .subscribe();
        userRouter = new UserRouter(new UserRepositoryImpl(new UserDao(factory)));

    }

    @Override
    public void accept(HttpServerRoutes routes) {
        userRouter.configureRoutes(routes);
    }


}
