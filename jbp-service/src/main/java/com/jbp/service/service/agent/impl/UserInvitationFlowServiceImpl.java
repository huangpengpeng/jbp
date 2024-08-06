package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.UserInvitationGplotVo;
import com.jbp.service.dao.agent.UserInvitationFlowDao;
import com.jbp.service.service.TeamService;
import com.jbp.service.service.TeamUserService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.service.agent.UserInvitationFlowService;
import com.jbp.service.service.agent.UserInvitationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class UserInvitationFlowServiceImpl extends ServiceImpl<UserInvitationFlowDao, UserInvitationFlow> implements UserInvitationFlowService {

    @Resource
    private TeamService teamService;
    @Resource
    private TeamUserService teamUserService;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private UserService userService;
    @Resource
    private UserInvitationFlowDao dao;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private UserCapaXsService userCapaXsService;

    @Override
    public List<UserInvitationFlow> getUnderList(Integer pId, Long minCapaId) {
        return dao.getUnderCapaList(pId, minCapaId);
    }

    @Override
    public List<UserInvitationFlow> getXsUnderList(Integer pId, Long minXsCapaId) {
        return dao.getUnderXsCapaList(pId, minXsCapaId);
    }

    @Override
    public List<UserInvitationFlow> getUnderList(Integer pId) {
        LambdaQueryWrapper<UserInvitationFlow> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserInvitationFlow::getPId, pId);
        return list(lqw);
    }

    /**
     * 用户关系如果变更需要将更当前用户有关的的记录全部删除重新生成
     */
    @Override
    public void clear(Integer uId) {
        List<UserInvitationFlow> list = list(new QueryWrapper<UserInvitationFlow>().lambda().eq(UserInvitationFlow::getPId, uId));
        List<Integer> userIdList = list.stream().map(UserInvitationFlow::getUId).collect(Collectors.toList());
        userIdList.add(uId);
        remove(new LambdaQueryWrapper<UserInvitationFlow>().in(UserInvitationFlow::getUId, userIdList));
    }

    /**
     * 刷新用户关系+团队
     */
    @Override
    public void refreshFlowAndTeam(Integer uId) {
        // 查询出所有的上级
        List<UserUpperDto> upperList = userInvitationService.getAllUpper(uId);
        // 如果上级没有就直接返回
        if (CollectionUtils.isEmpty(upperList)) {
            return;
        }
        // 更新团队
        teamUserService.deleteByUid(uId);
        Team team = teamService.getLastTeam(uId, upperList);
        if (team != null) {
            teamUserService.save(uId, team.getId());
        }
        // 获取所有的上级添加关系
        List<UserInvitationFlow> list = Lists.newArrayList();
        int i=0;
        for (UserUpperDto upper : upperList) {
            if (upper.getPId() != null && upper.getPId() > 0) {
                UserInvitationFlow flow = new UserInvitationFlow(uId, upper.getPId(), upper.getLevel());
                list.add(flow);
            }
            log.info("增在处理用户:{} 的邀请层级关系:{}, 总关系:{}", uId, i++, list.size());
        }
        // 保存 list空 mybatis自带剔除
        if (CollectionUtils.isNotEmpty(list)) {
            dao.insertBatch(list);
        }
    }

    @Override
    public PageInfo<UserInvitationFlow> pageList(Integer uid, Integer pid, Integer level, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserInvitationFlow> lqw = new LambdaQueryWrapper<UserInvitationFlow>()
                .eq(!ObjectUtil.isNull(uid), UserInvitationFlow::getUId, uid)
                .eq(!ObjectUtil.isNull(pid), UserInvitationFlow::getPId, pid)
                .eq(!ObjectUtil.isNull(level), UserInvitationFlow::getLevel, level)
                .orderByDesc(UserInvitationFlow::getId);
        Page<UserInvitationFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserInvitationFlow> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(UserInvitationFlow::getUId).collect(Collectors.toList());
        uIdList.addAll(list.stream().map(UserInvitationFlow::getPId).collect(Collectors.toList()));
        Map<Integer, User> userMap = userService.getUidMapList(uIdList);
        //等级
        Map<Integer, UserCapa> capaMapList = userCapaService.getUidMap(uIdList);
        Map<Integer, UserCapaXs> capaXsMapList = userCapaXsService.getUidMap(uIdList);
        //团队
        Map<Integer, TeamUser> teamUserMapList = teamUserService.getUidMapList(uIdList);
        list.forEach(e -> {
            User uUser = userMap.get(e.getUId());
            e.setUAccount(uUser != null ? uUser.getAccount() : "");
            e.setUNickName(uUser != null ? uUser.getNickname() : "");
            //等级
            UserCapa uUserCapa = capaMapList.get(e.getUId());
            e.setUCapaName(uUserCapa != null ? uUserCapa.getCapaName() : "");
            UserCapaXs uUserCapaXs = capaXsMapList.get(e.getUId());
            e.setUCapaXsName(uUserCapaXs!=null?uUserCapaXs.getCapaName():"");

            User pUser = userMap.get(e.getPId());
            e.setPAccount(pUser != null ? pUser.getAccount() : "");
            e.setPNickName(pUser != null ? pUser.getNickname() : "");
            UserCapa pUserCapa = capaMapList.get(e.getPId());
            e.setPCapaName(pUserCapa != null ? pUserCapa.getCapaName() : "");
            UserCapaXs pUserCapaXs = capaXsMapList.get(e.getPId());
            e.setPCapaXsName(pUserCapaXs!=null?pUserCapaXs.getCapaName():"");
            //团队
            TeamUser teamUser = teamUserMapList.get(e.getUId());
            e.setTeamName(teamUser != null ? teamUser.getName() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public List<UserInvitationGplotVo> gplotInfo(Integer uid) {
        UserInvitationGplotVo vo = new UserInvitationGplotVo();
        //当前搜索用户信息
        User user = userService.getById(uid);
        UserCapa userCapa = userCapaService.getByUser(uid);
        UserCapaXs userCapaXs = userCapaXsService.getByUser(uid);
        vo.setUAccount(user.getAccount());
        vo.setUNickName(user.getNickname());
        //等级
        vo.setCapaName(userCapa != null ? userCapa.getCapaName() : "");
        vo.setUcapaId(userCapa != null ? userCapa.getCapaId() : null);
        //星级
        vo.setCapaXsName(userCapaXs != null ? userCapaXs.getCapaName() : "");
        vo.setUcapaXsId(userCapaXs != null ? userCapaXs.getCapaId() : null);
        int count = count(new QueryWrapper<UserInvitationFlow>().lambda().eq(UserInvitationFlow::getPId, uid));
        vo.setCount(count);

        List<UserInvitationGplotVo> gplotVoList = new ArrayList<>();
        List<UserInvitation> userInvitationList = userInvitationService.list(new QueryWrapper<UserInvitation>().lambda().eq(UserInvitation::getPId, uid));
        if (CollectionUtils.isNotEmpty(userInvitationList)) {
            vo.setIsParent(true);
            List<Integer> uidList = userInvitationList.stream().map(UserInvitation::getUId).collect(Collectors.toList());
            Map<Integer, User> userMap = userService.getUidMapList(uidList);
            //等级
            Map<Integer, UserCapa> capaMapList = userCapaService.getUidMap(uidList);
            Map<Integer, UserCapaXs> capaXsMapList = userCapaXsService.getUidMap(uidList);
            userInvitationList.forEach(e -> {
                int uCount = count(new QueryWrapper<UserInvitationFlow>().lambda().eq(UserInvitationFlow::getPId, e.getUId()));
                UserInvitationGplotVo cvo = new UserInvitationGplotVo();
                cvo.setIsParent(uCount > 0);
                User cUser = userMap.get(e.getUId());
                cvo.setUAccount(cUser != null ? cUser.getAccount() : "");
                cvo.setUNickName(cUser != null ? cUser.getNickname() : "");
                //等级
                UserCapa cUserCapa = capaMapList.get(e.getUId());
                cvo.setCapaName(cUserCapa != null ? cUserCapa.getCapaName() : "");
                cvo.setUcapaId(cUserCapa != null ? cUserCapa.getCapaId() : null);
                //星级
                UserCapaXs cUserCapaXs = capaXsMapList.get(e.getUId());
                cvo.setCapaXsName(cUserCapaXs != null ? cUserCapaXs.getCapaName() : "");
                cvo.setUcapaXsId(cUserCapaXs != null ? cUserCapaXs.getCapaId() : null);
                //下级人数
                cvo.setCount(uCount);
                if (uCount != 0){
                    List<UserInvitationGplotVo> svoList= new ArrayList<>();
                    UserInvitationGplotVo svo = new UserInvitationGplotVo();
                    svoList.add(svo);
                    cvo.setChildren(svoList);
                }
                gplotVoList.add(cvo);
            });
            vo.setChildren(gplotVoList);
        } else {
            vo.setIsParent(false);
            vo.setChildren(null);
        }
        List<UserInvitationGplotVo> list1 = new ArrayList<>();
        list1.add(vo);
        return list1;
    }
}
