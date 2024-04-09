package org.campusforum.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.campusforum.backend.entity.dto.Account;
import org.campusforum.backend.entity.vo.request.ChangePasswordVO;
import org.campusforum.backend.entity.vo.request.RegisterVO;
import org.campusforum.backend.entity.vo.request.ResetConfirmVO;
import org.campusforum.backend.entity.vo.request.ResetPasswordVO;
import org.campusforum.backend.mapper.AccountMapper;
import org.campusforum.backend.service.AccountService;
import org.campusforum.backend.utils.Const;
import org.campusforum.backend.utils.FlowUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 用户方法的实现类
 * @author ChangxueDeng
 */

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService, UserDetailsService {

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    RabbitTemplate rabbitTemplate;
    @Resource
    FlowUtils flowUtils;

    @Resource
    PasswordEncoder passwordEncoder;

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
        return this.query()
                .eq("username", text)
                .or()
                .eq("email", text)
                .one();
    }

    @Override
    public Account findAccountById(int id) {
        return this.query().eq("id", id).one();
    }


    @Override
    public String emailVerify(String email, String type, String ip) {
        //使用ip作为锁
        synchronized (ip.intern()) {
            if (isLimitedEmail(email)) return "请求频繁";
            //生成验证码
            Random random = new Random();
            int code = random.nextInt(900000) + 100000;
            //存入消息队列
            Map<String, Object> map = Map.of("email", email, "type", type, "code", code);
            rabbitTemplate.convertAndSend("amq.direct","send-email", map);
            //存入redis,验证码失效实际为3分钟
            storeEmailVerify(email, String.valueOf(code));
            return null;
        }
    }

    /**
     * 发送邮件是否已经被限流
     * @param ip 请求ip
     * @return boolean
     */
    private boolean isLimitedEmail(String ip){
        String key = Const.LIMIT_EMAIL + ip;
        if (!flowUtils.limitOnce(key, 60)) return true;
        return false;
    }

    @Override
    public String registerAccount(RegisterVO registerVO) {
        String username = registerVO.getUsername();
        String email = registerVO.getEmail();
        String password = passwordEncoder.encode(registerVO.getPassword());
        String code = registerVO.getCode();
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(Const.LIMIT_EMAIL_DATA + email)))
            return "验证码失效，请重新发送";
        if (!Objects.equals(stringRedisTemplate.opsForValue().get(Const.LIMIT_EMAIL_DATA + email), code))
            return "验证码错误";
        if (existAccountByEmail(email))
            return "电子邮件地址已存在";
        if (existAccountByUsername(username))
            return "用户名已被占用";
        Account account = new Account(null, username, password, email, "user", new Date(),null);
        this.save(account);
        stringRedisTemplate.delete(Const.LIMIT_EMAIL_DATA + email);
        return null;
    }

    private boolean existAccountByUsername(String username) {
        return this.query().eq("username", username).exists();
    }

    private boolean existAccountByEmail(String email) {
        return this.query().eq("email", email).exists();
    }

    @Override
    public String resetPassword(ResetPasswordVO resetPasswordVO) {
        if(resetConfirm(new ResetConfirmVO(resetPasswordVO.getEmail(), resetPasswordVO.getCode())) != null)
            return "验证状态已失效";
        String password = passwordEncoder.encode(resetPasswordVO.getPassword());
        boolean update = this.update().eq("email", resetPasswordVO.getEmail()).set("password", password).update();
        if (update) {
            deleteEmailVerify(Const.LIMIT_EMAIL_DATA + resetPasswordVO.getEmail());
        }
        return null;
    }

    @Override
    public String modifyEmail(int id, ResetConfirmVO vo) {
        String key = Const.LIMIT_EMAIL_DATA + vo.getEmail();
        String code = getEmailVerify(key);
        if (code == null) {
            return "请先获取验证码";
        } else if (!code.equals(vo.getCode())) {
            return "验证码错误，请重试";
        } else {
            this.deleteEmailVerify(key);
            Account account = this.findAccountByUsernameOrEmail(vo.getEmail());
            if (account != null && account.getId() != id) {
                return "该邮箱已被使用";
            }
            this.update().eq("id", id).set("email", vo.getEmail()).update();
        }
        return null;
    }

    @Override
    public String resetConfirm(ResetConfirmVO resetConfirmVO) {
        String email = resetConfirmVO.getEmail();
        String code = resetConfirmVO.getCode();
        if (!existAccountByEmail(email)) return "账户不存在";
        String verify = getEmailVerify(Const.LIMIT_EMAIL_DATA + email);
        if (verify == null) return "验证码未发送";
        if (!verify.equals(code)) return "验证码错误";
        return null;
    }

    @Override
    public String changePassword(int id, ChangePasswordVO vo) {
        String password = this.query().eq("id", id).one().getPassword();
        //比较原密码是否填写正确
        if (!passwordEncoder.matches(vo.getPassword(), password)) {
            return "原密码错误，请重新错误";
        }
        boolean success = this.update().eq("id", id).set("password", passwordEncoder.encode(vo.getNewPassword()))
                .update();
        return success ? null : "未知错误，请联系管理员";
    }

    /**
     * 将邮箱验证码存入redis，避免重复获取
     * @param email 邮箱地址
     * @param code 验证码
     */
    private void storeEmailVerify(String email, String code) {
        String key = Const.LIMIT_EMAIL_DATA + email;
        stringRedisTemplate.opsForValue().set(key, code, 3, TimeUnit.MINUTES);
    }

    /**
     * 删除redis中的验证码
     * @param key 邮箱key
     */
    private void deleteEmailVerify(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 通过指定key获取redis中的验证码
     * @param key 邮箱key
     * @return {@link String}
     */
    private String getEmailVerify(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

}
