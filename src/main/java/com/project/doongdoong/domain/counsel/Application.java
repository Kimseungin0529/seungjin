package com.project.doongdoong.domain.counsel;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class Application implements CommandLineRunner {
    private final RedisRebuilder redisRebuilder;

    @Override
    public void run(String... args) throws Exception {
        redisRebuilder.rebuildRedisFromRdb();
    }
}
