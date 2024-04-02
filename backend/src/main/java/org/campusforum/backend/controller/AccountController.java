package org.campusforum.backend.controller;


import jakarta.annotation.Resource;
import org.campusforum.backend.entity.Account;
import org.campusforum.backend.entity.Result;
import org.campusforum.backend.entity.dto.AccountDTO;
import org.campusforum.backend.entity.vo.response.AccountVO;
import org.campusforum.backend.service.AccountService;
import org.campusforum.backend.utils.Const;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;


@RestController
@ResponseBody
@RequestMapping("/api/user")
public class AccountController {
    @Resource
    AccountService accountService;
    @GetMapping("/info")
    public String info(@RequestAttribute(Const.USER_ID) int id) {
        AccountDTO accountDTO = accountService.coverDTO(accountService.findAccountById(id));
        AccountVO accountVO = new AccountVO();
        BeanUtils.copyProperties(accountDTO, accountVO);
        return Result.success(accountVO).toJsonString();
    }
}
