package org.campusforum.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
import java.util.function.Supplier;


/**
 * 登录验证接口
 * @author ChangxueDeng
 */
@RestController
@ResponseBody
@Validated
@RequestMapping("/api/auth")
@Tag(name = "登录验证接口")
public class AuthorizeController {
    @Resource
    AccountService accountServiceImpl;

    /**
     * 发送指定类型验证码到指定邮箱
     * @param email 收件邮箱
     * @param type 验证码类型
     * @param request 请求
     * @return {@link String}
     */

    @PostMapping("/ask-code")
    @Operation(summary = "发送验证码", description = "发送指定类型验证码到指定收件邮箱")
    public String sendEmailVerify(@Parameter(description = "验证码接收邮箱") @Email @RequestParam String email,
                                  @Parameter(description = "验证码类型，分为register和reset两种") @Pattern (regexp = "register|reset")@RequestParam String type,
                                  @Parameter(description = "HttpServletRequest") HttpServletRequest request) {
        return messageHandler(()-> accountServiceImpl.emailVerify(email, type, request.getRemoteAddr()));
    }

    /**
     * 进行用户注册
     * @param registerVO 接受注册参数
     * @return {@link String}
     */
    @Operation(summary = "用户注册", description = "进行新用户注册")
    @PostMapping("/register")
    public String register(
            @Parameter(description = "注册信息",
                    content = @Content(schema = @Schema(implementation = RegisterVO.class)))
            @Valid @RequestBody RegisterVO registerVO) {
        return messageHandler(()-> accountServiceImpl.registerAccount(registerVO));
    }

    /**
     * 进行用户密码重置
     * @param resetPasswordVO 接受重置密码参数
     * @return {@link String}
     */
    @Operation(summary = "用户密码重置", description = "通过邮箱地址对用户的密码进行重置")
    @PostMapping("/reset-password")
    public String resetPassword(
            @Parameter(description = "重置参数",
                    content = @Content(schema = @Schema(implementation = ResetPasswordVO.class)))
            @Valid @RequestBody ResetPasswordVO resetPasswordVO) {
        return messageHandler(()-> accountServiceImpl.resetPassword(resetPasswordVO));
    }

    /**
     * 进行重置验证参数
     * @param resetConfirmVO 接受重置验证参数
     * @return {@link String}
     */
    @Operation(summary = "重置验证", description = "重置密码时进行邮箱验证，查询是否存在对应用户")
    @PostMapping("/reset-confirm")
    public String resetConfirm(
            @Parameter(description = "验证参数",
                    content = @Content(schema = @Schema(implementation = ResetConfirmVO.class)))
            @Valid @RequestBody ResetConfirmVO resetConfirmVO) {
        return messageHandler(()-> accountServiceImpl.resetConfirm(resetConfirmVO));
    }

    /**
     * 统一处理返回消息
     * @param supplier 函数式接口
     * @return {@link String}
     */
    private String messageHandler(Supplier<String> supplier) {
        String message = supplier.get();
        return message == null ? Result.success().toJsonString() : Result.failure(StatusUtils.STATUS_BAD_REQUEST, message).toJsonString();
    }
}
