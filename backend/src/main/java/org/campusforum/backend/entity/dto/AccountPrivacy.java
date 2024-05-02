package org.campusforum.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户隐私设置
 * @author ChangxueDeng
 * @date 2024/04/05
 */
@Data
@TableName("account_privacy")
public class AccountPrivacy {
    @TableId()
    private final Integer id;
    @TableField
    private boolean gender = true;
    @TableField
    private boolean phone = true;
    @TableField
    private boolean qq = true;
    @TableField
    private boolean wx = true;
    @TableField
    private boolean email = true;
    public String[] hiddenFields() {
        List<String> strings = new ArrayList<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try{
                if (field.getType().equals(boolean.class) && !field.getBoolean(this)) {
                    strings.add(field.getName());
                }
            }catch (Exception ignore) {}
        }
        return strings.toArray(String[]::new);
    }
}
