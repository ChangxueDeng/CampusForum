package org.campusforum.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.campusforum.backend.entity.Result;
import org.campusforum.backend.service.AccountService;
import org.campusforum.backend.utils.StatusUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;
import java.util.function.Supplier;

@RestController
@ResponseBody
@Validated
@RequestMapping("/api/auth")
public class AuthorizeController {
    @Resource
    AccountService accountServiceImpl;
    @PostMapping("/ask-code")
    //发送验证码
    public String sendEmailVerify(@Email @RequestParam String email,
                                  @Pattern (regexp = "register|reset")@RequestParam String type,
                                  HttpServletRequest request) {
        return MessageHandler(()-> accountServiceImpl.EmailVerify(email, type, request.getRemoteAddr()));
    }
    //注册

    //重置

    //验证

    //返回消息统一处理
    private String MessageHandler(Supplier<String> supplier){
        String message = supplier.get();
        return message == null ? Result.success().toJsonString() : Result.failure(StatusUtils.STATUS_BAD_REQUEST, message).toString();
    }
}
