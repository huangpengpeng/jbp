package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.agent.UserRegion;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.UserInvitationDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserInvitationFlowService;
import com.jbp.service.service.agent.UserInvitationService;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.ibatis.annotations.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户销售关系网
 */
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class UserInvitationServiceImpl extends ServiceImpl<UserInvitationDao, UserInvitation> implements UserInvitationService {

    @Resource
    private UserInvitationDao dao;
    @Resource
    private UserInvitationFlowService userInvitationFlowService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private UserService userService;

    @Override
    public UserInvitation getByUser(Integer uId) {
        LambdaQueryWrapper<UserInvitation> wrapper = new LambdaQueryWrapper();
        wrapper.and((w)-> {
            w.eq(UserInvitation::getPId, uId)
                    .or()
                    .eq(UserInvitation::getMId, uId);
        });
        return getOne(wrapper);
    }

    @Override
    public List<UserInvitation> getNextList(Integer uid) {
        LambdaQueryWrapper<UserInvitation> wrapper = new LambdaQueryWrapper();
        wrapper.eq(UserInvitation::getPId, uid);
        return list(wrapper);
    }

    @Override
    public Integer getPid(Integer uId) {
        UserInvitation userInvitation = getByUser(uId);
        return userInvitation == null ? null : userInvitation.getRealPid();
    }

    @Override
    public List<UserUpperDto> getAllUpper(Integer uId) {
        return dao.getAllUpper(uId);
    }

    @Override
    public List<UserUpperDto> getNoMountAllUpper(Integer uId) {
        return dao.getNoMountAllUpper(uId);
    }

    @Override
    public Boolean hasChild(Integer uId, Integer pId) {
        List<UserUpperDto> allUpper = getAllUpper(uId);
        return !ListUtils.emptyIfNull(allUpper).stream().filter(u -> pId == u.getPId()).collect(Collectors.toList()).isEmpty();
    }

    @Override
    public void validBand(Integer uId, Integer pId) {
        // 没有上级直接通过
        if (pId == null) {
            throw new RuntimeException("上级不能为空");
        }
        if (uId == pId) {
            throw new RuntimeException("自己不能绑定自己");
        }
        if (hasChild(pId, uId)) {
            throw new RuntimeException("关系链条的上级不能绑定给自己");
        }
    }

    /**
     * 更新关系统一入口
     *
     * @param uId     当前用户ID
     * @param pId     需要绑定的销售人员
     * @param ifM     是否转挂
     * @param ifForce 是否强制绑定
     */
    @Override
    public UserInvitation band(Integer uId, Integer pId, Boolean ifM, Boolean ifForce) {
        validBand(uId, pId);
        UserInvitation userInvitation = getByUser(uId);
        if (userInvitation == null) {
            userInvitation = new UserInvitation();
        }
        if (BooleanUtils.isTrue(ifM)) {
            userInvitation.setMId(pId);
        } else {
            userInvitation.setPId(pId);
        }
        userInvitation.setUId(uId);
        userInvitation.setIfForce(ifForce);

        // 执行更新
        UserInvitation finalUserInvitation = userInvitation;
        transactionTemplate.execute(s->{
            saveOrUpdate(finalUserInvitation);
            // 删除关系留影
            userInvitationFlowService.clear(uId);
            return Boolean.TRUE;
        });
        return userInvitation;
    }

    @Override
    public List<UserInvitation> getNoFlowList() {
        return dao.getNoFlowList();
    }

    @Override
    public PageInfo<UserInvitation> pageList(Integer uid, Integer pid, Integer mid, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserInvitation> lwq=new LambdaQueryWrapper<UserInvitation>()
                .eq(!ObjectUtil.isNull(uid),UserInvitation::getUId,uid)
                .eq(!ObjectUtil.isNull(pid),UserInvitation::getPId,pid)
                .eq(!ObjectUtil.isNull(mid),UserInvitation::getMId,mid);
        Page<UserInvitation> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserInvitation> list = list(lwq);
        list.forEach(e -> {
            e.setUAccount(userService.getById(e.getUId()).getAccount());
            e.setPAccount(userService.getById(e.getMId()).getAccount());
            e.setMAccount(userService.getById(e.getPId()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
