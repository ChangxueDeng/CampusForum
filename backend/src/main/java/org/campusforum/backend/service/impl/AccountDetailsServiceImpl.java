package org.campusforum.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.campusforum.backend.entity.dto.Account;
import org.campusforum.backend.entity.dto.AccountDetails;
import org.campusforum.backend.entity.vo.request.DetailsSaveVO;
import org.campusforum.backend.mapper.AccountDetailsMapper;
import org.campusforum.backend.service.AccountDetailsService;
import org.campusforum.backend.service.AccountService;
import org.springframework.stereotype.Service;


/**
 * AccountDetailsService实现类
 * @author ChangxueDeng
 * @date 2024/04/02
 */
@Service
public class AccountDetailsServiceImpl extends ServiceImpl<AccountDetailsMapper, AccountDetails> implements AccountDetailsService {

    @Resource
    AccountService accountService;


    @Override
    public AccountDetails findAccountDetailsById(int id) {
        return this.getById(id);
    }

    @Override
    public synchronized boolean saveAccountDetails(int id, DetailsSaveVO saveVO) {
        Account account = accountService.findAccountByUsernameOrEmail(saveVO.getUsername());
        if (account == null || account.getId() == id) {
            accountService.update().eq("id", id).set("username", saveVO.getUsername()).update();
            this.saveOrUpdate(new AccountDetails(
                    id, saveVO.getGender(), saveVO.getPhone(),saveVO.getQq(), saveVO.getWx(), saveVO.getDesc()));
            return true;
        }
        return false;
    }
}
