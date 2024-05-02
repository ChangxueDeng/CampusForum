package org.campusforum.backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.campusforum.backend.entity.Result;
import org.campusforum.backend.entity.dto.Interact;
import org.campusforum.backend.entity.dto.TopicType;
import org.campusforum.backend.entity.vo.request.AddCommentVO;
import org.campusforum.backend.entity.vo.request.CreateTopicVO;
import org.campusforum.backend.entity.vo.request.UpdateTopicVO;
import org.campusforum.backend.entity.vo.response.*;
import org.campusforum.backend.service.TopicService;
import org.campusforum.backend.service.WeatherService;
import org.campusforum.backend.utils.Const;
import org.campusforum.backend.utils.ControllerUtils;
import org.campusforum.backend.utils.StatusUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 论坛主体功能Controller
 * @author ChangxueDeng
 * @date 2024/04/06
 */
@RestController
@ResponseBody
@Tag(name = "论坛主体功能接口")
@RequestMapping("/api/forum")
public class ForumController {
    @Resource
    WeatherService weatherService;
    @Resource
    TopicService topicService;
    @Resource
    ControllerUtils controllerUtils;
    /**
     * 获取天气信息
     * @param longitude 经度
     * @param latitude 纬度
     * @return {@link Result}<{@link WeatherVO}>
     */
    @Operation(summary = "获取天气信息", description = "通过经纬度，从和风天气api获取天气信息")
    @GetMapping("/weather")
    public Result<WeatherVO> getWeather(
            @Parameter(description = "经度", content = @Content(schema = @Schema(implementation = double.class)))
            @RequestParam("longitude") double longitude,
            @Parameter(description = "纬度", content = @Content(schema = @Schema(implementation = double.class)))
            @RequestParam("latitude") double latitude) {
        WeatherVO weather = weatherService.fetchWeather(longitude, latitude);
        if (weather == null) {
            return Result.failure(StatusUtils.STATUS_BAD_REQUEST, "获取天气和地理位置信息失败，请联系管理员");
        }
        return Result.success(weather);
    }

    /**
     * 获取所有帖子类型
     * @return {@link Result}<{@link List}<{@link TopicTypeVO}>>
     */
    @GetMapping("/types")
    public Result<List<TopicTypeVO>> topicList() {
        List<TopicTypeVO> vo = new ArrayList<>();
        for (TopicType t : topicService.typeList()) {
            TopicTypeVO type = new TopicTypeVO();
            BeanUtils.copyProperties(t, type);
            vo.add(type);
        }
        return Result.success(vo);
    }

    /**
     * 创建贴子
     * @param vo 创建帖子参数
     * @param id 用户id
     * @return {@link Result}<{@link Void}>
     */
    @PostMapping("/create-topic")
    public Result<Void> createTopic(@Valid @RequestBody CreateTopicVO vo,
                                    @RequestAttribute(Const.USER_ID) int id) {
        return controllerUtils.messageHandler(() -> topicService.createTopic(vo, id));
    }

    /**
     * 获取指定类型的帖子列表，并进行分页
     * @param page 页面
     * @param type 帖子类型
     * @return {@link Result}<{@link List}<{@link TopicPreviewVO}>>
     */
    @GetMapping("/list-topic")
    public Result<List<TopicPreviewVO>> listTopic(@RequestParam @Min(0) int page,
                                                  @RequestParam  @Min(0) int type) {
        return Result.success(topicService.listTopicByPage(page + 1, type));
    }

    /**
     * 获取热门话题列表
     * 本接口不需要接收任何参数，仅返回一个热门话题的列表。
     *
     * @return {@link Result}<{@link List}<{@link TopicTopVO}>>
     *         返回一个包含热门话题列表的结果对象，其中Result对象封装了操作的成功与否及数据。
     */
    @GetMapping("/top-topic")
    public Result<List<TopicTopVO>> topTopic() {
        return Result.success(topicService.listTopicTop());
    }


    /**
     * 获取指定帖子的详细信息。
     *
     * @param tid 帖子ID，必须为非负整数。
     * @param id 用户ID，用于标识请求的用户。
     * @return 返回一个结果对象，其中包含帖子的详细信息。如果操作成功，结果对象的状态为成功；如果操作失败，结果对象的状态为失败，并提供错误信息。
     */
    @GetMapping("/topic")
    public Result<TopicDetailVo> topic(@RequestParam @Min(0) int tid, @RequestAttribute(Const.USER_ID) int id) {
        // 调用topicService获取指定主题的详细信息，并将结果封装成Result对象返回
        return Result.success(topicService.getTopicDetail(tid, id));
    }


    /**
     * 用户互动接口。
     * 用于处理用户对主题帖子的点赞或收藏等互动行为。
     *
     * @param tid 帖子ID，标识互动对象。
     * @param type 互动类型，只能是"like"或"collect"。
     * @param state 互动状态，用于表示用户是否取消了之前的互动行为。
     * @param id 用户ID，标识进行互动的用户。
     * @return 返回一个结果对象，当前场景下返回的是一个空结果对象Void，表示操作成功。
     */
    @GetMapping("/interact")
    public Result<Void> interact(@RequestParam @Min(0) int tid,
                                 @RequestParam @Pattern(regexp = "(like|collect)") String type,
                                 @RequestParam boolean state,
                                 @RequestAttribute(Const.USER_ID) int id) {
        // 调用主题服务，处理用户的互动行为
        topicService.interact(new Interact(tid, id, type, new Date()), state);
        // 返回操作成功的结果
        return Result.success();
    }


    /**
     * 获取用户收藏的主题预览列表
     *
     * @param id 用户ID，用于识别请求的用户
     * @return 返回一个结果对象，其中包含用户收藏的主题预览列表。列表中每个项都是一个{@link TopicPreviewVO}对象。
     *         结果对象由{@link Result}表示，它包含了操作是否成功的信息以及相应的数据。
     */
    @GetMapping("/collects")
    public Result<List<TopicPreviewVO>> collects(@RequestAttribute(Const.USER_ID) int id) {
        // 调用topicService中的listCollects方法，根据用户ID获取其收藏的主题列表，并封装成成功的结果返回
        return Result.success(topicService.listCollects(id));
    }


    /**
     * 更新主题信息。
     *
     * @param vo 更新主题的视图对象，包含主题的详细信息，通过RequestBody接收。
     * @param id 用户ID，通过RequestAttribute获取，用于权限验证或记录操作用户。
     * @return 返回一个Result<Void>对象，表示操作的结果，如果成功则返回null。
     */
    @PostMapping("/update-topic")

    public Result<Void> updateTopic(@Valid @RequestBody UpdateTopicVO vo, @RequestAttribute(Const.USER_ID) int id) {
        // 通过controllerUtils的messageHandler方法处理更新主题逻辑，返回操作结果
        return controllerUtils.messageHandler(() -> topicService.updateTopic(vo, id));
    }

    @GetMapping("/ip")
    public Result<String> getIp(HttpServletRequest request) {
        return Result.success(request.getRemoteAddr());
    }

    @PostMapping("/add-comment")
    public Result<Void> addComment(@Valid @RequestBody AddCommentVO vo,
                                   @RequestAttribute(Const.USER_ID) int id) {
        return controllerUtils.messageHandler(()-> topicService.createComment(vo, id));
    }
    @GetMapping("/comments")
    public Result<List<CommentVO>> comments(@RequestParam @Min(0) int tid,
                                            @RequestParam @Min(0) int page) {
        return Result.success(topicService.listComments(tid, page));
    }
    @GetMapping("/delete-comment")
    public Result<Void> deleteComment(@RequestParam @Min(0) int cid,
                                      @RequestAttribute(Const.USER_ID) int id) {
        return controllerUtils.messageHandler(() -> topicService.deleteComment(cid, id));
    }
}
