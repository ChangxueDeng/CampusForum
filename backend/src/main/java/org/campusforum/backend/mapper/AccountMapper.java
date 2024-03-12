package org.campusforum.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.campusforum.backend.entity.Account;
@Mapper
/**
 * UserMapper 对用户表进行操作
 * @author ChangxueDeng
 */
public interface AccountMapper extends BaseMapper<Account> {
}
