package org.campusforum.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.campusforum.backend.entity.dto.Account;
import org.campusforum.backend.entity.vo.request.ChangePasswordVO;
import org.campusforum.backend.entity.vo.request.RegisterVO;
import org.campusforum.backend.entity.vo.request.ResetConfirmVO;
import org.campusforum.backend.entity.vo.request.ResetPasswordVO;
import org.campusforum.backend.entity.vo.response.AccountListVO;
import org.campusforum.backend.entity.vo.response.FollowVO;
import org.campusforum.backend.entity.vo.response.SpaceVO;

import java.util.List;

/**
 * 用户的方法
 * @author ChangxueDeng
 */
public interface AccountService extends IService<Account> {
     /**
      * 通过用户名或邮箱查找用户
      * @param text 用户名或邮箱
      * @return {@link Account}
      */
     Account findAccountByUsernameOrEmail(String text);

     /**
      * 通过Id查找用户
      * @param id 用户id
      * @return {@link Account}
      */
     Account findAccountById(int id);

     /**
      * 校验邮箱是否有效并发送验证码
      * @param email 邮箱
      * @param type 验证码类型
      * @param ip 用户ip
      * @return {@link String}
      */
     String emailVerify(String email, String type, String ip);

     /**
      * 进行用户注册
      * @param registerVO 注册参数
      * @return {@link String}
      */
     String registerAccount(RegisterVO registerVO);

     /**
      * 进行重置密码时进行邮箱校验
      * @param resetConfirmVO 校验参数
      * @return {@link String}
      */
     String resetConfirm(ResetConfirmVO resetConfirmVO);

     /**
      * 重置用户密码
      * @param resetPasswordVO 重置密码参数
      * @return {@link String}
      */
     String resetPassword(ResetPasswordVO resetPasswordVO);

     /**
      *
      * 修改用户绑定邮箱
      * @param id 用户id
      * @param vo 用户邮箱修改参数
      * @return {@link String}
      */
     String modifyEmail(int id, ResetConfirmVO vo);

     /**
      * 修改用户密码
      * @param id 用户id
      * @param vo 用户密码修改参数
      * @return {@link String}
      */
     String changePassword(int id, ChangePasswordVO vo);

}
