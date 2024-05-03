package org.campusforum.backend.controller;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import org.campusforum.backend.entity.Result;
import org.campusforum.backend.entity.vo.response.NotificationVO;
import org.campusforum.backend.service.NotificationService;
import org.campusforum.backend.utils.Const;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/api/notification")
public class NotificationController {
    @Resource
    NotificationService notificationService;
    @GetMapping("/list")
    public Result<List<NotificationVO>> listNotifications(@RequestAttribute(Const.USER_ID) int id) {
        return Result.success(notificationService.findUserNotifications(id));
    }
    @GetMapping("/delete")
    public Result<Void> deleteNotification(@RequestParam("id") @Min(0) int nid,
                                           @RequestAttribute(Const.USER_ID) int id) {
        notificationService.deleteUserNotification(nid, id);
        return Result.success();
    }
    @GetMapping("/delete-all")
    public Result<Void> deleteAllNotifications(@RequestAttribute(Const.USER_ID) int id) {
        notificationService.deleteUserAlNotifications(id);
        return Result.success();
    }
}
