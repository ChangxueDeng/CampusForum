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
import org.campusforum.backend.utils.ControllerUtils;
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
    @Resource
    ControllerUtils controllerUtils;
    /**
     * 发送指定类型验证码到指定邮箱
     * @param email 收件邮箱
     * @param type 验证码类型
     * @param request 请求
     * @return {@link Result}<{@link String}
     */
    @PostMapping("/ask-code")
    @Operation(summary = "发送验证码", description = "发送指定类型验证码到指定收件邮箱")
    public Result<String> sendEmailVerify(@Parameter(description = "验证码接收邮箱") @Email @RequestParam String email,
                                  @Parameter(description = "验证码类型，分为register,reset和modify三种") @Pattern (regexp = "register|reset|modify")@RequestParam String type,
                                  @Parameter(description = "HttpServletRequest") HttpServletRequest request) {
        return controllerUtils.messageHandler(()-> accountServiceImpl.emailVerify(email, type, request.getRemoteAddr()));
    }

    /**
     * 进行用户注册
     * @param registerVO 接受注册参数
     * @return {@link Result}<{@link String}
     */
    @Operation(summary = "用户注册", description = "进行新用户注册")
    @PostMapping("/register")
    public Result<String> register(
            @Parameter(description = "注册信息",
                    content = @Content(schema = @Schema(implementation = RegisterVO.class)))
            @Valid @RequestBody RegisterVO registerVO) {
        return controllerUtils.messageHandler(()-> accountServiceImpl.registerAccount(registerVO));
    }

    /**
     * 进行用户密码重置
     * @param resetPasswordVO 接受重置密码参数
     * @return {@link Result}<{@link String}
     */
    @Operation(summary = "用户密码重置", description = "通过邮箱地址对用户的密码进行重置")
    @PostMapping("/reset-password")
    public Result<String> resetPassword(
            @Parameter(description = "重置参数",
                    content = @Content(schema = @Schema(implementation = ResetPasswordVO.class)))
            @Valid @RequestBody ResetPasswordVO resetPasswordVO) {
        return controllerUtils.messageHandler(()-> accountServiceImpl.resetPassword(resetPasswordVO));
    }

    /**
     * 进行重置验证参数
     * @param resetConfirmVO 接受重置验证参数
     * @return {@link Result}<{@link String}
     */
    @Operation(summary = "重置验证", description = "重置密码时进行邮箱验证，查询是否存在对应用户")
    @PostMapping("/reset-confirm")
    public Result<String> resetConfirm(
            @Parameter(description = "验证参数",
                    content = @Content(schema = @Schema(implementation = ResetConfirmVO.class)))
            @Valid @RequestBody ResetConfirmVO resetConfirmVO) {
        return controllerUtils.messageHandler(()-> accountServiceImpl.resetConfirm(resetConfirmVO));
    }


}
