package org.campusforum.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.campusforum.backend.entity.Account;
import org.campusforum.backend.entity.dto.AccountDTO;

/**
 * 用户的方法
 * @author ChangxueDeng
 */
public interface AccountService extends IService<Account> {
    public Account findAccountByUsernameOrEmail(String text);
    public AccountDTO coverDTO(Account account);
    public String EmailVerify(String email, String type, String ip);
}
