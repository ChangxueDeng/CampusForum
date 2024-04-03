package org.campusforum.backend.controller;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.campusforum.backend.entity.Result;
import org.campusforum.backend.entity.dto.Account;
import org.campusforum.backend.entity.dto.AccountDetails;
import org.campusforum.backend.entity.vo.request.DetailsSaveVO;
import org.campusforum.backend.entity.vo.response.AccountDetailsVO;
import org.campusforum.backend.entity.vo.response.AccountVO;
import org.campusforum.backend.service.AccountDetailsService;
import org.campusforum.backend.service.AccountService;
import org.campusforum.backend.utils.Const;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@ResponseBody
@RequestMapping("/api/user")
public class AccountController {
    @Resource
    AccountService accountService;
    @Resource
    AccountDetailsService accountDetailsService;
    @GetMapping("/info")
    public String info(@RequestAttribute(Const.USER_ID) int id) {
        Account account = accountService.findAccountById(id);
        AccountVO accountVO = new AccountVO();
        BeanUtils.copyProperties(account, accountVO);
        return Result.success(accountVO).toJsonString();
    }

    @GetMapping("/details")
    public Result<AccountDetailsVO> details(@RequestAttribute(Const.USER_ID) int id) {
        //避免为空
        AccountDetails details = Optional.ofNullable(accountDetailsService.findAccountDetailsById(id)).orElse(new AccountDetails());
        AccountDetailsVO accountDetailsVO = new AccountDetailsVO();
        BeanUtils.copyProperties(details, accountDetailsVO);
        return Result.success(accountDetailsVO);
    }
    @PostMapping("/sava-details")
    public Result<Void> savaDetails(@RequestAttribute(Const.USER_ID) int id,
                                    @RequestBody @Valid DetailsSaveVO vo) {
        boolean success = accountDetailsService.saveAccountDetails(id, vo);
        return success ? Result.success() : Result.failure(HttpServletResponse.SC_BAD_REQUEST, "此用户名已被其他用户注册，请更换！");
    }

}
