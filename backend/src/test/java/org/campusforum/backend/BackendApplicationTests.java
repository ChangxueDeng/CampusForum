package org.campusforum.backend;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;

@SpringBootTest
class BackendApplicationTests {
    @Resource
    RabbitTemplate rabbitTemplate;
    @Test
    void contextLoads() {
        System.out.println(rabbitTemplate.getExchange());
        rabbitTemplate.convertAndSend( "send-email", "hi");
        rabbitTemplate.setExchange("amq.direct");
        System.out.println(rabbitTemplate.getExchange());
        rabbitTemplate.convertAndSend( "send-email", "hi");
    }

}
