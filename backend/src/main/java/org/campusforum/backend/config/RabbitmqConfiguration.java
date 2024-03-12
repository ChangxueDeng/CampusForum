package org.campusforum.backend.config;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ消息队列配置类
 * @author ChangxueDeng
 *
 */
@Configuration
public class RabbitmqConfiguration {
    //创建队列
    @Bean(name = "q")
    public Queue emailCampus(){
        return QueueBuilder.nonDurable("email-campus").build();
    }
    //创建交换机
    @Bean(name = "e")
    public Exchange exchange(){
        return ExchangeBuilder.directExchange("amq.direct").build();
    }
    //绑定队列和通道
    @Bean
    public Binding binding(@Qualifier("q") Queue queue, @Qualifier("e") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("send-email").noargs();
    }
    //消息转换器
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}
