package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.Message;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.MessageAddRequest;
import com.jbp.common.request.agent.MessageUpdateRequest;

import java.util.List;

public interface MessageService extends IService<Message> {

    /**
     *获取消息列表
     */
    PageInfo<Message> pageList(String title, Boolean status, Boolean isPop, Boolean isTop, PageParamRequest pageParamRequest);

    /**
     * 后台消息新增
     *
     * @param request 消息新增请求对象
     * @return Boolean
     */
    Boolean add(MessageAddRequest request);

    /**
     * 后台消息修改
     *
     * @param request 消息修改请求对象
     * @return Boolean
     */
    Boolean edit(MessageUpdateRequest request);

    /**
     * 获取用户满足条件的权益模板
     *
     * @param uid 用户id
     * @return List<Long> 权益模板id集合
     */
    List<Long> getTempIds(Integer uid);

    /**
     * 首页弹窗
     *
     * @return Message
     */
    Message homePopup();

    /**
     * 获取用户未读消息数量
     *
     * @return Integer
     */
    Integer unreadCount();

    /**
     * 用户消息列表
     *
     * @param uid 用户id
     * @param pageParamRequest 分页参数
     * @return PageInfo<Message>
     */
    PageInfo<Message> getList(Integer uid,PageParamRequest pageParamRequest);
}
