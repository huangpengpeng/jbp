package com.jbp.admin.controller.agent;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Message;
import com.jbp.common.model.agent.TeamItem;
import com.jbp.common.model.agent.UserMessage;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.MessageAddRequest;
import com.jbp.common.request.agent.MessagePageRequest;
import com.jbp.common.request.agent.MessageUpdateRequest;
import com.jbp.common.request.agent.TeamItemUpdateRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserMessageService;
import com.jbp.service.service.agent.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/agent/message")
@Api(tags = "系统消息管理")
public class MessageController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserMessageService userMessageService;

    @PreAuthorize("hasAuthority('agent:message:page')")
    @GetMapping("/page")
    @ApiOperation("系统消息列表分页查询")
    public CommonResult<CommonPage<Message>> getList(MessagePageRequest request, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(messageService.pageList(request.getTitle(), request.getStatus(),
                request.getIsPop(), request.getIsTop(), pageParamRequest)));
    }

    @GetMapping("/detail")
    @ApiOperation("系统消息详情")
    public CommonResult<Message> getDetail(Integer id) {
        if (id == null) {
            throw new CrmebException("消息id不能为空!");
        }
        Message message = messageService.getById(id);
        if (message == null) {
            throw new CrmebException("该消息不存在!");
        }
        return CommonResult.success(message);
    }



    @PreAuthorize("hasAuthority('agent:message:add')")
    @PostMapping("/add")
    @ApiOperation("新增系统消息")
    public CommonResult add(@RequestBody @Validated MessageAddRequest request) {
        return CommonResult.success(messageService.add(request));
    }

    @PreAuthorize("hasAuthority('agent:message:update')")
    @PostMapping("/update")
    @ApiOperation("修改系统消息")
    public CommonResult update(@RequestBody @Validated MessageUpdateRequest request) {
        return CommonResult.success(messageService.edit(request));
    }

    @PreAuthorize("hasAuthority('agent:message:delete')")
    @GetMapping("/delete")
    @ApiOperation("删除系统消息")
    public CommonResult delete(Integer id) {
        if (id == null) {
            throw new CrmebException("消息id不能为空!");
        }
        Message message = messageService.getById(id);
        if (message == null) {
            throw new CrmebException("该消息不存在!");
        }
        messageService.removeById(id);
        userMessageService.remove(new QueryWrapper<UserMessage>().lambda().eq(UserMessage::getMessageId, id));
        return CommonResult.success();

    }

    @GetMapping("/switch")
    @ApiOperation("系统消息开关")
    public CommonResult messageSwitch(Integer id) {
        if (id == null) {
            throw new CrmebException("消息id不能为空!");
        }
        Message message = messageService.getById(id);
        if (message == null) {
            throw new CrmebException("该消息不存在!");
        }
        message.setStatus(!message.getStatus());
        messageService.updateById(message);
        return CommonResult.success();
    }
}
