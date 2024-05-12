package org.campusforum.backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.campusforum.backend.entity.Result;
import org.campusforum.backend.entity.dto.Notification;
import org.campusforum.backend.entity.vo.request.AddAnnouncementVO;
import org.campusforum.backend.entity.vo.request.AddUserVO;
import org.campusforum.backend.entity.vo.request.UpdateAnnouncement;
import org.campusforum.backend.entity.vo.response.*;
import org.campusforum.backend.service.AdminService;
import org.campusforum.backend.utils.Const;
import org.campusforum.backend.utils.ControllerUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@Tag(name = "管理员功能接口")
@PreAuthorize("hasRole('admin')")
@RequestMapping("/api/admin")
public class AdminController {
    @Resource
    AdminService adminService;
    @Resource
    ControllerUtils controllerUtils;
    /*
    1. 发布论坛公告
    2. 查看所有用户
    3. 查看所有帖子
    4. 封禁或删除帖子
    5. 封禁或删除用户
     */

    @GetMapping("/all-user")
    public Result<List<AccountListVO>> allUser(@RequestParam("page") int page) {
        return Result.success(adminService.listAccounts(page));
    }
    @GetMapping("/topics")
    public Result<List<TopicListVO>> topics(@RequestParam("page") int page) {
        return Result.success(adminService.listTopicByTyPage(page));
    }
    @GetMapping("/overview")
    public Result<OverviewVO> overview() {
        return Result.success(adminService.getOverview());
    }
    @GetMapping("/notifications")
    public Result<List<Notification>> notifications(@RequestParam("page") int page) {
        return Result.success(adminService.listNotificationsByPage(page));
    }
    @GetMapping("/comments")
    public Result<List<CommentAdminVO>> comments(@RequestParam("page") int page) {
        return Result.success(adminService.listCommentsByPage(page));
    }
    @GetMapping("/announcements")
    public Result<List<AnnouncementAdminVO>> announcements(@RequestParam("page") int page) {
        return Result.success(adminService.listAnnouncementsByPage(page));
    }
    @PostMapping("/add-announcement")
    public Result<Void> addAnnouncement(@RequestBody AddAnnouncementVO vo,
                                        @RequestAttribute(Const.USER_ID) int id) {
        adminService.addAnnouncement(vo, id);
        return Result.success();
    }
    @GetMapping("/delete-announcement")
    public Result<Void> deleteAnnouncement(@RequestParam("id") int id) {
        adminService.deleteAnnouncement(id);
        return Result.success();
    }
    @PostMapping("/update-announcement")
    public Result<Void> updateAnnouncement(@RequestBody UpdateAnnouncement vo) {
        adminService.updateAnnouncement(vo);
        return Result.success();
    }
    @PostMapping("/add-User")
    public Result<Void> addUser(@RequestBody AddUserVO vo) {
        return controllerUtils.messageHandler(()-> adminService.addUser(vo));
    }
    @GetMapping("/ban-User")
    public Result<Void> banUser(@RequestParam("id") int id, @RequestParam("ban") boolean ban) {
        adminService.banUser(id, ban);
        return Result.success();
    }
    @GetMapping("/ban-topic")
    public Result<Void> banTopic(@RequestParam("id") int id, @RequestParam("ban") boolean ban) {
        adminService.banTopic(id, ban);
        return Result.success();
    }
    @GetMapping("/ban-comment")
    public Result<Void> banComment(@RequestParam("id") int id, @RequestParam("ban") boolean ban) {
        adminService.banComment(id, ban);
        return Result.success();
    }
    @GetMapping("/delete-topic")
    public Result<Void> deleteTopic(@RequestParam("id") int id) {
        adminService.deleteTopic(id);
        return Result.success();
    }
    @GetMapping("/delete-comment")
    public Result<Void> deleteComment(@RequestParam("id") int id) {
        adminService.deleteComment(id);
        return Result.success();
    }
    @GetMapping("top-topic")
    public Result<List<TopicTopVO>> topTopic(@RequestParam("id") int id, @RequestParam("top") boolean top) {
        adminService.topTopic(id, top);
        return Result.success();
    }
}
