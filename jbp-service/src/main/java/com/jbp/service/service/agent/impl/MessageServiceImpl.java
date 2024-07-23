package com.jbp.service.service.agent.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.MessageAddRequest;
import com.jbp.common.request.agent.MessageUpdateRequest;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.agent.MessageDao;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)

public class MessageServiceImpl extends ServiceImpl<MessageDao, Message> implements MessageService {

    @Autowired
    private UserService userService;
    @Autowired
    private WhiteUserService whiteUserService;
    @Autowired
    private TeamUserService teamUserService;
    @Autowired
    private UserInvitationService userInvitationService;
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private LimitTempService limitTempService;
    @Autowired
    private UserCapaService userCapaService;
    @Autowired
    private UserCapaXsService userCapaXsService;
    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Override
    public PageInfo<Message> pageList(String title, Boolean status, Boolean isPop, Boolean isTop, PageParamRequest pageParamRequest) {
        Page<Message> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Message> lqw = new LambdaQueryWrapper<>();
        lqw.like(!StringUtils.isEmpty(title), Message::getTitle, title);
        lqw.eq(!Objects.isNull(status), Message::getStatus, status);
        lqw.eq(!Objects.isNull(isPop), Message::getIsPop, isPop);
        lqw.eq(!Objects.isNull(isTop), Message::getIsTop, isTop);
        lqw.orderByDesc(Message::getId);
        List<Message> list = list(lqw);
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Boolean add(MessageAddRequest request) {

        Message message=Message.builder().title(request.getTitle()).digest(request.getDigest()).skipUrl(request.getSkipUrl())
                .windowImage(request.getWindowImage()).showImage(request.getShowImage()).content(request.getContent()).readLimitTempId(request.getReadLimitTempId())
                .issueTime(request.getIssueTime()).number(request.getNumber()).isPop(request.getIsPop()).isTop(request.getIsTop())
                .pageView(0L).status(true).build();
        String cdnUrl = systemAttachmentService.getCdnUrl();
        message.setWindowImage(systemAttachmentService.clearPrefix(message.getWindowImage(), cdnUrl));
        message.setShowImage(systemAttachmentService.clearPrefix(message.getShowImage(), cdnUrl));
        message.setContent(systemAttachmentService.clearPrefix(message.getContent(), cdnUrl));
        return  save(message);
    }

    @Override
    public Boolean edit(MessageUpdateRequest request) {
        Message message = getById(request.getId());
        if (message == null) {
            throw new CrmebException("该消息不存在");
        }
        BeanUtils.copyProperties(request, message);
        String cdnUrl = systemAttachmentService.getCdnUrl();
        message.setWindowImage(systemAttachmentService.clearPrefix(message.getWindowImage(), cdnUrl));
        message.setShowImage(systemAttachmentService.clearPrefix(message.getShowImage(), cdnUrl));
        message.setContent(systemAttachmentService.clearPrefix(message.getContent(), cdnUrl));
        return updateById(message);
    }

    @Override
    public List<Long> getTempIds(Integer uid) {
        Integer pId = null, rId = null;
        Long capaId = null,capaXsId = null;
        List<Long> whiteIdList = Lists.newArrayList(), teamIdList = Lists.newArrayList();
        if (uid != null) {
            whiteIdList = whiteUserService.getByUser(uid);
            TeamUser teamUser = teamUserService.getByUser(uid);
            if (teamUser != null) {
                teamIdList.add(Long.valueOf(teamUser.getTid()));
            }
            pId = userInvitationService.getPid(uid);
            rId = userRelationService.getPid(uid);
            UserCapa userCapa = userCapaService.getByUser(uid);
            capaId = userCapa != null ? userCapa.getCapaId() : null;
            UserCapaXs userCapaXs = userCapaXsService.getByUser(uid);
            capaXsId = userCapaXs != null ? userCapaXs.getCapaId() : null;
        }
        List<Long> tempIds = limitTempService.hasLimits(capaId, capaXsId, whiteIdList, teamIdList, pId, rId);
        return tempIds;
    }

    @Override
    public Message homePopup() {
        User user = userService.getInfo();
        if (user == null) {
            throw new CrmebException("获取用户信息失败！");
        }
        List<Long> tempIds = getTempIds(user.getId());
        Message message = getOne(new QueryWrapper<Message>().lambda().eq(Message::getStatus, true).eq(Message::getIsPop, true)
                .in(Message::getReadLimitTempId, tempIds).lt(Message::getIssueTime,new Date()).orderByDesc(Message::getIssueTime).last("limit 1"));
        if (message != null) {
            UserMessage userMessage = userMessageService.getOne(new QueryWrapper<UserMessage>().lambda().eq(UserMessage::getMessageId, message.getId()).eq(UserMessage::getUid,user.getId()));
            if (userMessage != null) {
                return null;
            }
        }
        return message;
    }

    @Override
    public Integer unreadCount() {
        User user = userService.getInfo();
        if (user == null) {
            throw new CrmebException("获取用户信息失败！");
        }
        List<Long> tempIds = getTempIds(user.getId());
        LambdaQueryWrapper<Message> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Message::getStatus, true);
        lqw.lt(Message::getIssueTime,new Date());
        lqw.in(Message::getReadLimitTempId, tempIds);
        List<Message> list = list(lqw);
        if (list.isEmpty()) {
            return null;
        }
        List<UserMessage> userMessages = userMessageService.list(new QueryWrapper<UserMessage>().lambda().eq(UserMessage::getUid, user.getId()));
        if (userMessages.isEmpty()) {
            return list.size();
        }
        List<Long> idList = list.stream().map(Message::getId).collect(Collectors.toList());
        List<Long> midList = userMessages.stream().map(UserMessage::getMessageId).collect(Collectors.toList());
        idList.removeAll(midList);
        return idList.size();
    }

    @Override
    public PageInfo<Message> getList(Integer uid,PageParamRequest pageParamRequest) {
        List<Long> tempIds = getTempIds(uid);
        Page<Message> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Message> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Message::getStatus, true);
        lqw.lt(Message::getIssueTime,new Date());
        lqw.in(Message::getReadLimitTempId, tempIds);
        lqw.orderByDesc(Message::getIsTop);
        lqw.orderByDesc(Message::getNumber);
        lqw.orderByDesc(Message::getIssueTime);
        List<Message> list = list(lqw);
        if (list.isEmpty()) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        List<UserMessage> userMessages = userMessageService.list(new QueryWrapper<UserMessage>().lambda().eq(UserMessage::getUid, uid));
        if (userMessages.isEmpty()) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Long> midList = userMessages.stream().map(UserMessage::getMessageId).collect(Collectors.toList());
        list.forEach(e->{
            if (midList.contains(e.getId())){
                e.setIsRead(true);
            }
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
