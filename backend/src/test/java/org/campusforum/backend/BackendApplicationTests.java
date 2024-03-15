package org.campusforum.backend;

import jakarta.annotation.Resource;
import org.campusforum.backend.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;

@SpringBootTest
class BackendApplicationTests {
    @Resource
    JwtUtils utils;
    @Test
    void contextLoads() {
        utils.createExpireTime();
    }

}
