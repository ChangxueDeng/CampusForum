package org.campusforum.backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.campusforum.backend.entity.Result;
import org.campusforum.backend.entity.dto.Account;
import org.campusforum.backend.entity.dto.AccountDetails;
import org.campusforum.backend.entity.dto.AccountPrivacy;
import org.campusforum.backend.entity.vo.request.ChangePasswordVO;
import org.campusforum.backend.entity.vo.request.DetailsSaveVO;
import org.campusforum.backend.entity.vo.request.PrivacySaveVO;
import org.campusforum.backend.entity.vo.request.ResetConfirmVO;
import org.campusforum.backend.entity.vo.response.AccountDetailsVO;
import org.campusforum.backend.entity.vo.response.AccountPrivacyVO;
import org.campusforum.backend.entity.vo.response.AccountVO;
import org.campusforum.backend.service.AccountDetailsService;
import org.campusforum.backend.service.AccountPrivacyService;
import org.campusforum.backend.service.AccountService;
import org.campusforum.backend.utils.Const;
import org.campusforum.backend.utils.ControllerUtils;
import org.campusforum.backend.utils.StatusUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.function.Supplier;


/**
 * @author ChangxueDeng
 * @date 2024/04/04
 */
@RestController
@ResponseBody
@Validated
@Tag(name = "用户接口")
@RequestMapping("/api/user")
public class AccountController {
    @Resource
    AccountService accountService;
    @Resource
    AccountDetailsService accountDetailsService;
    @Resource
    AccountPrivacyService accountPrivacyService;
    @Resource
    ControllerUtils controllerUtils;
    /**
     * 获取用户信息
     * @param id 用户id
     * @return {@link String}
     */
    @Operation(summary = "用户信息获取", description = "获取用户信息")
    @GetMapping("/info")
    public Result<AccountVO> info(
            @Parameter(description = "用户id",
            content = @Content(schema = @Schema(implementation = int.class)))
            @RequestAttribute(Const.USER_ID) int id) {
        Account account = accountService.findAccountById(id);
        AccountVO accountVO = new AccountVO();
        BeanUtils.copyProperties(account, accountVO);
        return Result.success(accountVO);
    }

    /**
     * 获取用户详细信息
     * @param id 用户id
     * @return {@link Result}<{@link AccountDetailsVO}>
     */
    @Operation(summary = "用户详细信息", description = "获取用户详细信息用于展示")
    @GetMapping("/details")
    public Result<AccountDetailsVO> details(
            @Parameter(description = "用户id",
                    content = @Content(schema = @Schema(implementation = int.class)))
            @RequestAttribute(Const.USER_ID) int id) {
        //避免为空
        AccountDetails details = Optional.ofNullable(accountDetailsService.findAccountDetailsById(id)).orElse(new AccountDetails());
        AccountDetailsVO accountDetailsVO = new AccountDetailsVO();
        BeanUtils.copyProperties(details, accountDetailsVO);
        return Result.success(accountDetailsVO);
    }

    /**
     * 保存用户详细信息
     * @param vo 用户信息参数
     * @param id 用户id
     * @return {@link Result}<{@link Void}>
     */
    @Operation(summary = "保存用户详细信息", description = "保存或生成（用户详细为空时）用户详细信息，通过用户id")
    @PostMapping("/sava-details")
    public Result<Void> savaDetails(
            @Parameter(description = "用户id",
                    content = @Content(schema = @Schema(implementation = int.class)))
            @RequestAttribute(Const.USER_ID) int id,
            @Parameter(description = "用户详细信息",
                    content = @Content(schema = @Schema(implementation = DetailsSaveVO.class)))
            @RequestBody @Valid DetailsSaveVO vo) {
        boolean success = accountDetailsService.saveAccountDetails(id, vo);
        return success ? Result.success() : Result.failure(HttpServletResponse.SC_BAD_REQUEST, "此用户名已被其他用户注册，请更换！");
    }


    /**
     * 修改用户绑定邮箱
     * @param id 用户id
     * @param vo 修改邮箱参数
     * @return {@link Result}<{@link Void}>
     */
    @Operation(summary = "修改邮箱", description = "修改用户绑定的邮箱")
    @PostMapping("/modify-email")
    public Result<Void> modifyEmail(
            @Parameter(description = "用户id",
                    content = @Content(schema = @Schema(implementation = int.class)))
            @RequestAttribute(Const.USER_ID) int id,
            @Parameter(description = "邮箱修改参数",
                    content = @Content(schema = @Schema(implementation = ResetConfirmVO.class)))
            @RequestBody @Valid ResetConfirmVO vo) {
        return controllerUtils.messageHandler(() -> accountService.modifyEmail(id, vo));
    }

    /**
     * 修改用户密码
     * @param id 用户id
     * @param vo 修改密码参数
     * @return {@link Result}<{@link Void}>
     */
    @Operation(summary = "修改用户密码", description = "修改用户登录时的密码")
    @PostMapping("/change-password")
    public Result<Void> changePassword(
            @Parameter(description = "用户id",
                    content = @Content(schema = @Schema(implementation = int.class)))
            @RequestAttribute(Const.USER_ID) int id,
            @Parameter(description = "修改密码参数",
                    content = @Content(schema = @Schema(implementation = ChangePasswordVO.class)))
            @RequestBody @Validated ChangePasswordVO vo) {
        return controllerUtils.messageHandler(() -> accountService.changePassword(id, vo));
    }

    /**
     * 保存用户隐私设置
     * @param id 用户id
     * @param vo 隐私设置参赛
     * @return {@link Result}<{@link Void}>
     */
    @PostMapping("/sava-privacy")
    public Result<Void> savaPrivacy(
            @Parameter(description = "用户id",
                    content = @Content(schema = @Schema(implementation = int.class)))
            @RequestAttribute(Const.USER_ID) int id,
            @Parameter(description = "隐私设置参数",
                    content = @Content(schema = @Schema(implementation = PrivacySaveVO.class)))
            @RequestBody @Valid PrivacySaveVO vo){
        accountPrivacyService.savePrivacy(id, vo);
        return Result.success();
    }

    /**
     * 获取用户隐私设置
     * @param id 用户id
     * @return {@link Result}<{@link AccountPrivacyVO}>
     */
    @GetMapping("/privacy")
    public Result<AccountPrivacyVO> privacy(
            @Parameter(description = "用户id",
                    content = @Content(schema = @Schema(implementation = int.class)))
            @RequestAttribute(Const.USER_ID) int id) {
        AccountPrivacy accountPrivacy = accountPrivacyService.findAccountPrivacyById(id);
        AccountPrivacyVO accountPrivacyVO = new AccountPrivacyVO();
        BeanUtils.copyProperties(accountPrivacy, accountPrivacyVO);
        return Result.success(accountPrivacyVO);
    }

    @GetMapping("/test")
    public String test() {
        return "测试";
    }
}
