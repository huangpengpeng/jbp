package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Message;
import com.jbp.common.model.agent.UserMessage;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserMessageService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/front/message")
@Api(tags = "用户消息")
public class MessageController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private UserService userService;

    @ApiOperation(value = "消息浏览量增加")
    @GetMapping("/view/add")
    public CommonResult viewAdd(@RequestParam("id") Long id){
        if (id==null) {
            throw new CrmebException("消息id不能为空!");
        }
        Message message = messageService.getById(id);
        if (message == null){
            throw new CrmebException("该消息不存在！");
        }
        message.setPageView(message.getPageView() + 1);
        messageService.updateById(message);
        return CommonResult.success();
    }

    @ApiOperation(value = "用户消息已读")
    @GetMapping("/message/read")
    public CommonResult messageUnread(@RequestParam("id") Long id){
        User user = userService.getInfo();
        if (user == null){
            throw new CrmebException("获取用户信息失败！");
        }
        if (id==null) {
            throw new CrmebException("消息id不能为空!");
        }
        UserMessage userMessage = userMessageService.getOne(new QueryWrapper<UserMessage>().lambda().eq(UserMessage::getMessageId, id).eq(UserMessage::getUid, user.getId()));
        if (userMessage != null){
            return CommonResult.success();
        }
        UserMessage newUserMessage = new UserMessage().setMessageId(id).setUid(user.getId());
        userMessageService.save(newUserMessage);
        return CommonResult.success();
    }

    @ApiOperation(value = "首页弹窗")
    @GetMapping("/home/popup")
    public CommonResult<Message> homePopup(){
        return CommonResult.success(messageService.homePopup());
    }

    @ApiOperation(value = "获取用户未读消息数量")
    @GetMapping("/unread/count")
    public CommonResult<Integer> unreadCount(){
        return CommonResult.success(messageService.unreadCount());
    }

    @ApiOperation(value = "获取用户消息列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<Message>> getList(PageParamRequest pageParamRequest){
        User user = userService.getInfo();
        if (user == null){
            throw new CrmebException("获取用户信息失败！");
        }
        return CommonResult.success(CommonPage.restPage(messageService.getList(user.getId(),pageParamRequest)));
    }

    @ApiOperation(value = "获取消息详情")
    @GetMapping("/detail")
    public CommonResult<Message> detail(@RequestParam("id") Long id){
        if (id==null) {
            throw new CrmebException("消息id不能为空!");
        }
        Message message = messageService.getById(id);
        if (message == null){
            throw new CrmebException("该消息不存在！");
        }
        return CommonResult.success(message);
    }

}
