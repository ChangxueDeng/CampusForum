package org.campusforum.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.campusforum.backend.entity.dto.AccountPrivacy;
import org.campusforum.backend.entity.vo.request.PrivacySaveVO;
import org.campusforum.backend.mapper.AccountPrivacyMapper;
import org.campusforum.backend.service.AccountPrivacyService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 用户隐私
 * @author ChangxueDeng
 * @date 2024/04/05
 */
@Service
public class AccountPrivacyServiceImpl extends ServiceImpl<AccountPrivacyMapper, AccountPrivacy> implements AccountPrivacyService {
    @Override
    public void savePrivacy(int id, PrivacySaveVO vo) {
        AccountPrivacy accountPrivacy = Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacy(id));
        boolean status = vo.getStatus();
        switch (vo.getType()) {
            case "gender" -> accountPrivacy.setGender(status);
            case "phone" -> accountPrivacy.setPhone(status);
            case "qq" -> accountPrivacy.setQq(status);
            case "wx" -> accountPrivacy.setWx(status);
            case "email" -> accountPrivacy.setEmail(status);
            default -> {}
        }
        this.saveOrUpdate(accountPrivacy);
    }
    @Override
    public AccountPrivacy findAccountPrivacyById(int id) {
        AccountPrivacy accountPrivacy = getById(id);
        //如果为空时写入
        if (accountPrivacy == null) {
            AccountPrivacy creatPrivacy = new AccountPrivacy(id);
            this.save(creatPrivacy);
            return creatPrivacy;
        } else {
            return accountPrivacy;
        }
    }
}
