package com.a502.backend.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(classes = {RedisTestContainerConfig.class})
@EnableAutoConfiguration
public class RedisServerTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String TEST_KEY = "test-key";
    private static final String TEST_VALUE = "test-value";

    @BeforeAll
    static void setup() {
    }

    @AfterAll
    static void tearDown() {
    }

    @Test
    public void testRedisServer() {
        redisTemplate.opsForValue().set(TEST_KEY, TEST_VALUE);
        String value = redisTemplate.opsForValue().get(TEST_KEY);
        assertEquals(TEST_VALUE, value);
    }
}

