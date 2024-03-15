package org.campusforum.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.campusforum.backend.entity.Result;
import org.campusforum.backend.entity.vo.request.RegisterVO;
import org.campusforum.backend.entity.vo.request.ResetConfirmVO;
import org.campusforum.backend.entity.vo.request.ResetPasswordVO;
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
        return messageHandler(()-> accountServiceImpl.emailVerify(email, type, request.getRemoteAddr()));
    }
    //注册
    @PostMapping("/register")
    public String register(@RequestBody RegisterVO registerVO) {
        return messageHandler(()-> accountServiceImpl.registerAccount(registerVO));
    }
    //重置
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordVO resetPasswordVO) {
        return messageHandler(()-> accountServiceImpl.resetPassword(resetPasswordVO));
    }

    //验证
    @PostMapping("/reset-confirm")
    public String resetConfirm(@RequestBody ResetConfirmVO resetConfirmVO) {
        return messageHandler(()-> accountServiceImpl.resetConfirm(resetConfirmVO));
    }

    //返回消息统一处理
    private String messageHandler(Supplier<String> supplier) {
        String message = supplier.get();
        return message == null ? Result.success().toJsonString() : Result.failure(StatusUtils.STATUS_BAD_REQUEST, message).toJsonString();
    }
    @GetMapping("/test")
    public String test() {
        return messageHandler(() -> "123");
    }
}
