package org.campusforum.backend.entity.vo.response;

import lombok.Data;

/**
 * 获取账户详细信息响应
 * @author ChangxueDeng
 * @date 2024/04/02
 */
@Data
public class AccountDetailsVO {
    Integer gender;
    String phone;
    String qq;
    String wx;
    String desc;
}
