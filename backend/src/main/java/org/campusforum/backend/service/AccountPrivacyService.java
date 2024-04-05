package org.campusforum.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.campusforum.backend.entity.dto.AccountPrivacy;
import org.campusforum.backend.entity.vo.request.PrivacySaveVO;

/**
 * @author ChangxueDeng
 * @date 2024/04/05
 */
public interface AccountPrivacyService extends IService<AccountPrivacy> {
    /**
     * 保存隐私设置
     * @param id 用户id
     * @param vo 隐私设置参数
     */
    void savePrivacy(int id, PrivacySaveVO vo);

    /**
     * 获取获取隐私设置
     * @param id 用户id
     * @return {@link AccountPrivacy}
     */
    AccountPrivacy findAccountPrivacyById(int id);
}
