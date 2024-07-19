package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.Message;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.MessageAddRequest;
import com.jbp.common.request.agent.MessageUpdateRequest;

import java.util.List;

public interface MessageService extends IService<Message> {
    PageInfo<Message> pageList(String title, Boolean status, Boolean isPop, Boolean isTop, PageParamRequest pageParamRequest);

    Boolean add(MessageAddRequest request);

    Boolean edit(MessageUpdateRequest request);

    List<Long> getTempIds(Integer uid);
    Message homePopup();

    Integer unreadCount();

    PageInfo<Message> getList(Integer id,PageParamRequest pageParamRequest);
}
