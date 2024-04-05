package org.campusforum.backend.entity.vo.response;

import lombok.Data;

/**
 * @author ChangxueDeng
 * @date 2024/04/05
 */
@Data
public class AccountPrivacyVO {
    private boolean gender;
    private boolean phone;
    private boolean qq;
    private boolean wx;
    private boolean email;
}
