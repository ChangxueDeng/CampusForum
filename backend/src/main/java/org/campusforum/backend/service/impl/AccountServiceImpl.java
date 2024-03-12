package org.campusforum.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.campusforum.backend.entity.Account;
import org.campusforum.backend.entity.dto.AccountDTO;
import org.campusforum.backend.mapper.AccountMapper;
import org.campusforum.backend.service.AccountService;
import org.campusforum.backend.utils.Const;
import org.campusforum.backend.utils.FlowUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 用户方法的实现类
 * @author ChangxueDeng
 */

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService, UserDetailsService {

    @Resource
    AccountMapper accountMapper;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    RabbitTemplate rabbitTemplate;
    @Resource
    FlowUtils flowUtils;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = findAccountByUsernameOrEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User.withUsername(account.getUsername())
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    @Override
    public Account findAccountByUsernameOrEmail(String text) {
        return accountMapper.selectOne(new QueryWrapper<Account>()
                .eq("username", text)
                .or().eq("email", text));
    }

    @Override
    public AccountDTO coverDTO(Account account) {
        AccountDTO accountDTO = new AccountDTO();
        BeanUtils.copyProperties(account, accountDTO);
        return accountDTO;
    }

    @Override
    public String EmailVerify(String email, String type, String ip) {
        //使用ip作为锁
        synchronized (ip.intern()) {
            if (isLimitedEmail(email)) return "请求频繁";

            //生成验证码
            Random random = new Random();
            int code = random.nextInt(900000) + 100000;
            //存入消息队列
            Map<String, Object> map = Map.of("email", email, "type", type, "code", code);
            //rabbitTemplate.convertAndSend("send-email", map);
            rabbitTemplate.convertAndSend("amq.direct","send-email", map);
            //存入redis,验证码失效实际为3分钟
            stringRedisTemplate.opsForValue().set(Const.LIMIT_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    private boolean isLimitedEmail(String ip){
        String key = Const.LIMIT_EMAIL + ip;
        if (!flowUtils.limitOnceCheck(key)) return true;
        return false;
    }

}
