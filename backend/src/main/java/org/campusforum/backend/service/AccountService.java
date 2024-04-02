package org.campusforum.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.campusforum.backend.entity.Account;
import org.campusforum.backend.entity.dto.AccountDTO;
import org.campusforum.backend.entity.vo.request.RegisterVO;
import org.campusforum.backend.entity.vo.request.ResetConfirmVO;
import org.campusforum.backend.entity.vo.request.ResetPasswordVO;

/**
 * 用户的方法
 * @author ChangxueDeng
 */
public interface AccountService extends IService<Account> {
    public Account findAccountByUsernameOrEmail(String text);
    public Account findAccountById(int id);
    public AccountDTO coverDTO(Account account);
    public String emailVerify(String email, String type, String ip);
    public String registerAccount(RegisterVO registerVO);
    public String resetConfirm(ResetConfirmVO resetConfirmVO);
    public String resetPassword(ResetPasswordVO resetPasswordVO);
}
