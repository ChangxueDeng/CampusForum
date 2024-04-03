package org.campusforum.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.campusforum.backend.entity.dto.AccountDetails;
import org.campusforum.backend.entity.vo.request.DetailsSaveVO;

/**
 * 用户详细信息
 * @author ChangxueDeng
 * @date 2024/04/02
 */
public interface AccountDetailsService extends IService<AccountDetails> {
    /**
     * 通过用户id查找对应用户的详细信息
     * @param id 用户id
     * @return {@link AccountDetails}
     */
    AccountDetails findAccountDetailsById(int id);

    /**
     * 生成或更改用户详细信息
     * @param id 用户id
     * @param saveVO 详细信息
     * @return boolean
     */
    boolean saveAccountDetails(int id, DetailsSaveVO saveVO);
}
