package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.UserInviteInfoResponse;
import com.jbp.common.response.UserInviteResponse;
import com.jbp.service.dao.agent.UserInvitationDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.service.agent.UserInvitationFlowService;
import com.jbp.service.service.agent.UserInvitationService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private UserCapaXsService userCapaXsService;

    @Override
    public UserInvitation getByUser(Integer uId) {
        LambdaQueryWrapper<UserInvitation> wrapper = new LambdaQueryWrapper();
        wrapper.and((w) -> {
            w.eq(UserInvitation::getUId, uId);
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
    public List<UserInvitation> getNextList(List<Integer> uid) {
        LambdaQueryWrapper<UserInvitation> wrapper = new LambdaQueryWrapper();
        wrapper.in(UserInvitation::getPId, uid);
        return list(wrapper);
    }

    @Override
    public LinkedList<List<UserInvitation>> getLevelList(Integer uid, int level) {
        LinkedList<List<UserInvitation>> linkedList = Lists.newLinkedList();
        List<Integer> uidList = Lists.newArrayList(uid);
        for (int i = 0; i < level; i++) {
            List<UserInvitation> nextList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(uidList)) {
                nextList = getNextList(uidList);
            }
            linkedList.add(nextList);
            if (CollectionUtils.isEmpty(nextList)) {
                uidList.clear();
            } else {
                uidList = nextList.stream().map(UserInvitation::getUId).collect(Collectors.toList());
            }
        }
        return linkedList;
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
        if(allUpper.isEmpty()){
            return false;
        }
        return !ListUtils.emptyIfNull(allUpper).stream().filter(u -> pId.intValue() == u.getPId().intValue()).collect(Collectors.toList()).isEmpty();
    }

    @Override
    public void validBand(Integer uId, Integer pId) {
        // 没有上级直接通过
        if (pId == null) {
            throw new RuntimeException("上级不能为空");
        }
        if (uId.intValue() == pId.intValue()) {
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
    public UserInvitation band(Integer uId, Integer pId, Boolean ifM, Boolean ifForce, Boolean ifPlatOperate) {
        validBand(uId, pId);
        UserInvitation userInvitation = getByUser(uId);
        if (userInvitation == null) {
            userInvitation = new UserInvitation();
        }
        //强绑定用户无法换绑
        if(BooleanUtils.isNotTrue(ifPlatOperate)){
            if(BooleanUtils.isTrue(userInvitation.getIfForce())){
                return userInvitation ;
            }
        }
        if (BooleanUtils.isTrue(ifM)) {
            userInvitation.setMId(pId);
        } else {
            userInvitation.setPId(pId);
        }
        userInvitation.setUId(uId);
        userInvitation.setIfForce(ifForce== null ? false : ifForce);

        // 执行更新
        UserInvitation finalUserInvitation = userInvitation;
        transactionTemplate.execute(s -> {
            saveOrUpdate(finalUserInvitation);
            // 删除关系留影
            userInvitationFlowService.clear(uId);
            return Boolean.TRUE;
        });
        return userInvitation;
    }

    @Override
    public void del(Integer uId) {
        UserInvitation userInvitation = getByUser(uId);
        if (userInvitation != null) {
            removeById(userInvitation.getId());
            userInvitationFlowService.clear(uId);
        }
    }

    @Override
    public List<UserInvitation> getNoFlowList() {
        return dao.getNoFlowList();
    }

    @Override
    public PageInfo<UserInvitation> pageList(Integer uid, Integer pid, Integer mid, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserInvitation> lwq = new LambdaQueryWrapper<UserInvitation>()
                .eq(!ObjectUtil.isNull(uid), UserInvitation::getUId, uid)
                .eq(!ObjectUtil.isNull(pid), UserInvitation::getPId, pid)
                .eq(!ObjectUtil.isNull(mid), UserInvitation::getMId, mid)
                .orderByDesc(UserInvitation::getId);
        Page<UserInvitation> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserInvitation> list = list(lwq);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(UserInvitation::getUId).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        List<Integer> pIdList = list.stream().map(UserInvitation::getPId).collect(Collectors.toList());
        Map<Integer, User> pidMapList = userService.getUidMapList(pIdList);
        List<Integer> mIdList = list.stream().map(UserInvitation::getMId).collect(Collectors.toList());
        Map<Integer, User> midMapList = userService.getUidMapList(mIdList);
        //等级
        Map<Integer, UserCapa> capaUidMapList = userCapaService.getUidMap(uIdList);
        Map<Integer, UserCapa> capaPidMapList = userCapaService.getUidMap(pIdList);
        Map<Integer, UserCapa> capaMidMapList = userCapaService.getUidMap(mIdList);
        //星级
        Map<Integer, UserCapaXs> capaXsUidMapList = userCapaXsService.getUidMap(uIdList);
        Map<Integer, UserCapaXs> capaXsPidMapList = userCapaXsService.getUidMap(pIdList);
        Map<Integer, UserCapaXs> capaXsMidMapList = userCapaXsService.getUidMap(mIdList);

        list.forEach(e -> {
            User uUser = uidMapList.get(e.getUId());
            e.setUAccount(uUser != null ? uUser.getAccount() : "");
            e.setUNickName(uUser != null ? uUser.getNickname() : "");
            //等级
            UserCapa uUserCapa = capaUidMapList.get(e.getUId());
            e.setUCapaName(uUserCapa != null ? uUserCapa.getCapaName() : "");
            UserCapa pUserCapa = capaPidMapList.get(e.getPId());
            e.setPCapaName(pUserCapa != null ? pUserCapa.getCapaName() : "");
            UserCapa mUserCap = capaMidMapList.get(e.getMId());
            e.setMCapaName(mUserCap != null ? mUserCap.getCapaName() : "");
            //星级
            UserCapaXs uUserCapaXs = capaXsUidMapList.get(e.getUId());
            e.setUCapaXsName(uUserCapaXs!=null?uUserCapaXs.getCapaName():"");
            UserCapaXs pUserCapaXs = capaXsPidMapList.get(e.getPId());
            e.setPCapaXsName(pUserCapaXs!=null?pUserCapaXs.getCapaName():"");
            UserCapaXs mUserCapaXs = capaXsMidMapList.get(e.getMId());
            e.setMCapaXsName(mUserCapaXs!=null?mUserCapaXs.getCapaName():"");

            User pUser = pidMapList.get(e.getPId());
            e.setPAccount(pUser != null ? pUser.getAccount() : "");
            e.setPNickName(pUser != null ? pUser.getNickname() : "");
            User mUser = midMapList.get(e.getMId());
            e.setMAccount(mUser != null ? mUser.getAccount() : "");
            e.setMNickNamee(mUser != null ? mUser.getNickname() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public List<UserInviteResponse> getUserNextList(Integer uid, String account) {
        return dao.getUserNextList(uid, account);
    }

    @Override
    public List<UserInviteInfoResponse> getUserNextInfoList(Integer uid, String account) {
        return dao.getUserNextInfoList(uid, account);
    }

    @Override
    public Integer getInviteNumber(Integer pId) {
        LambdaQueryWrapper<UserInvitation> wrapper = new LambdaQueryWrapper();
        wrapper.and((w) -> {
            w.eq(UserInvitation::getPId, pId);
        });
        return list(wrapper).size();
    }
}
