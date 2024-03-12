package org.campusforum.backend.listenter;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "email-campus")
public class RabbitmqListener {
    @Resource
    JavaMailSender sender;
    @Value("${spring.mail.username}")
    String username;
    @RabbitHandler
    public void sendEmail(Map<String, Object> map){
        //取出消息队列中的信息
        String email = map.get("email").toString();
        String type = map.get("type").toString();
        String code = map.get("code").toString();
        //根据类型创建邮件消息
        SimpleMailMessage message = switch (type){
            case "register"->
                createMessage("注册", "验证码为：" + code + "，有效时间3分钟", email);
            case "reset"->
                createMessage("重置密码", "验证码为：" + code + "，有效时间3分钟", email);
            default-> null;
        };
        //发送
        if(message == null) return;
        sender.send(message);

    }
    private SimpleMailMessage createMessage(String title, String content, String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setFrom(username);
        message.setTo(email);
        return message;
    }

}
