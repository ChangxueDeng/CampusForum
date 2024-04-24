package org.campusforum.backend.utils;


import org.campusforum.backend.entity.Result;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class ControllerUtils {

    /**
     * 统一处理返回消息
     * @param supplier 函数式接口
     * @return {@link String}
     */
    public <T> Result<T> messageHandler(Supplier<String> supplier) {
        String message = supplier.get();
        return message == null ? Result.success() : Result.failure(StatusUtils.STATUS_BAD_REQUEST, message);
    }
}
