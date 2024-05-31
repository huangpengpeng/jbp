package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.UserRelationGplotVo;
import com.jbp.service.dao.agent.UserRelationFlowDao;
import com.jbp.service.service.TeamUserService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class UserRelationFlowServiceImpl extends ServiceImpl<UserRelationFlowDao, UserRelationFlow> implements UserRelationFlowService {

    @Resource
    private UserRelationService userRelationService;
    @Resource
    private UserService userService;
    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private TeamUserService teamUserService;
    @Resource
    private RelationScoreService relationScoreService;
    @Resource
    private CapaService capaService;


    @Override
    public void clear(Integer uid) {
        List<UserRelationFlow> list = list(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, uid));
        List<Integer> userIdList = list.stream().map(UserRelationFlow::getUId).collect(Collectors.toList());
        userIdList.add(uid);
        remove(new LambdaQueryWrapper<UserRelationFlow>().in(UserRelationFlow::getUId, userIdList));
    }

    @Override
    public void refresh(Integer uId) {
        // 查询出所有的上级
        List<UserUpperDto> upperList = userRelationService.getAllUpper(uId);
        // 如果上级没有就直接返回
        if (CollectionUtils.isEmpty(upperList)) {
            return;
        }
        // 获取所有的上级添加关系
        List<UserRelationFlow> list = Lists.newArrayList();
        int i = 1;
        for (UserUpperDto upper : upperList) {
            if (upper.getPId() != null && upper.getPId() > 0) {
                UserRelationFlow flow = new UserRelationFlow(uId, upper.getPId(), upper.getLevel(), upper.getNode());
                list.add(flow);
            }
            log.info("增在处理用户:{} 的服务层级关系:{}, 总关系:{}", uId, i++, upperList.size());
        }
        // 保存 list空 mybatis自带剔除
        if (CollectionUtils.isNotEmpty(list)) {
            List<List<UserRelationFlow>> partition = Lists.partition(list, 500);
            for (List<UserRelationFlow> userRelationFlows : partition) {
                saveBatch(userRelationFlows);
            }
        }

    }

    @Override
    public PageInfo<UserRelationFlow> pageList(Integer uid, Integer pid, Integer level, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserRelationFlow> lqw = new LambdaQueryWrapper<UserRelationFlow>()
                .eq(!ObjectUtil.isNull(uid), UserRelationFlow::getUId, uid)
                .eq(!ObjectUtil.isNull(pid), UserRelationFlow::getPId, pid)
                .eq(!ObjectUtil.isNull(level), UserRelationFlow::getLevel, level)
                .orderByDesc(UserRelationFlow::getId)
                .orderByAsc(UserRelationFlow::getNode);
        Page<UserRelationFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserRelationFlow> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(UserRelationFlow::getUId).collect(Collectors.toList());
        uIdList.addAll(list.stream().map(UserRelationFlow::getPId).collect(Collectors.toList()));
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
            UserCapa uUserCapa = capaMapList.get(e.getUId());
            e.setUCapaName(uUserCapa != null ? uUserCapa.getCapaName() : "");
            UserCapaXs uUserCapaXs = capaXsMapList.get(e.getUId());
            e.setUCapaXsName(uUserCapaXs != null ? uUserCapaXs.getCapaName() : "");

            User pUser = userMap.get(e.getPId());
            e.setPAccount(pUser != null ? pUser.getAccount() : "");
            e.setPNickName(pUser != null ? pUser.getNickname() : "");
            UserCapa pUserCapa = capaMapList.get(e.getPId());
            e.setPCapaName(pUserCapa != null ? pUserCapa.getCapaName() : "");
            UserCapaXs pUserCapaXs = capaXsMapList.get(e.getPId());
            e.setPCapaXsName(pUserCapaXs != null ? pUserCapaXs.getCapaName() : "");
            //团队
            TeamUser teamUser = teamUserMapList.get(e.getUId());
            e.setTeamName(teamUser!=null?teamUser.getName():"");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public UserRelationGplotVo gplot(Integer uid) {
        UserRelationGplotVo vo = new UserRelationGplotVo();
        UserRelationFlow userRelationFlow = getOne(new QueryWrapper<UserRelationFlow>().select("max(level) as level").eq("uid", uid));
        vo.setLevel(userRelationFlow != null ? userRelationFlow.getLevel() : 0);
        User user = userService.getById(uid);
        vo.setUAccount(user.getAccount());
        vo.setUNickName(user.getNickname());
        vo.setCreateTime(user.getCreateTime());
        UserCapa userCapa = userCapaService.getByUser(uid);
        vo.setUcapaId(userCapa.getCapaId());

        //左右区各等级人数
        UserRelationFlow userRelationFlow1 = getOne(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, uid).eq(UserRelationFlow::getLevel, 1).eq(UserRelationFlow::getNode, 0));
        vo.setCapaSumMap(userRelationFlow1 == null ? selectByCapa(0) : selectByCapa(userRelationFlow1.getUId()));
        UserRelationFlow userRelationFlow2 = getOne(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, uid).eq(UserRelationFlow::getLevel, 1).eq(UserRelationFlow::getNode, 1));
        vo.setCapaSumMap2(userRelationFlow2 == null ? selectByCapa(0) : selectByCapa(userRelationFlow2.getUId()));

        //当前用户左右区总业绩
        BigDecimal tenThousand = new BigDecimal("10000");
        RelationScore relationScore = relationScoreService.getOne(new QueryWrapper<RelationScore>().lambda().eq(RelationScore::getUid, uid).eq(RelationScore::getNode, 0));
        vo.setTotalScore(relationScore == null ? BigDecimal.ZERO : (relationScore.getUsableScore().add(relationScore.getUsedScore())).divide(tenThousand, 2, BigDecimal.ROUND_HALF_UP));
        RelationScore relationScore2 = relationScoreService.getOne(new QueryWrapper<RelationScore>().lambda().eq(RelationScore::getUid, uid).eq(RelationScore::getNode, 1));
        vo.setTotalScore2(relationScore2 == null ? BigDecimal.ZERO : (relationScore2.getUsableScore().add(relationScore2.getUsedScore())).divide(tenThousand, 2, BigDecimal.ROUND_HALF_UP));
        return vo;
    }

    @Override
    public UserRelationGplotVo gplotInfo(Integer uid) {
        if (uid == null) {
            return null;
        }
        //第一层
        UserRelationGplotVo top = gplot(uid);
        List<UserRelationGplotVo> topList = new ArrayList<>();
        UserRelationFlow userRelationFlow = getOne(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, uid).eq(UserRelationFlow::getLevel, 1).eq(UserRelationFlow::getNode, 0));
        if (userRelationFlow != null) {
            //第二层
            UserRelationGplotVo relation0 = gplot(userRelationFlow.getUId());
            relation0.setNode(0);
            topList.add(relation0);
            List<UserRelationGplotVo> topList1 = new ArrayList<>();
            UserRelationFlow userRelationFlow00 = getOne(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, userRelationFlow.getUId()).eq(UserRelationFlow::getLevel, 1).eq(UserRelationFlow::getNode, 0));
            if (userRelationFlow00 != null) {
                //第三层
                UserRelationGplotVo relation00 = gplot(userRelationFlow00.getUId());
                relation00.setNode(0);
                topList1.add(relation00);
                List<UserRelationGplotVo> topList21 = new ArrayList<>();
                UserRelationFlow userRelationFlow000 = getOne(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, userRelationFlow00.getUId()).eq(UserRelationFlow::getLevel, 1).eq(UserRelationFlow::getNode, 0));
                if (userRelationFlow000 != null) {
                    //第四层
                    UserRelationGplotVo relation000 = gplot(userRelationFlow000.getUId());
                    relation000.setNode(0);
                    topList21.add(relation000);
                } else {
                    UserRelationGplotVo relation000 = new UserRelationGplotVo();
                    topList21.add(relation000);
                }
                UserRelationFlow userRelationFlow001 = getOne(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, userRelationFlow00.getUId()).eq(UserRelationFlow::getLevel, 1).eq(UserRelationFlow::getNode, 1));
                if (userRelationFlow001 != null) {
                    //第四层
                    UserRelationGplotVo relation001 = gplot(userRelationFlow001.getUId());
                    relation001.setNode(1);
                    topList21.add(relation001);
                } else {
                    UserRelationGplotVo relation001 = new UserRelationGplotVo();
                    topList21.add(relation001);
                }
                relation00.setChildren(topList21);
            } else {
                UserRelationGplotVo relation10 = new UserRelationGplotVo();
                topList1.add(relation10);
            }
            UserRelationFlow userRelationFlow01 = getOne(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, userRelationFlow.getUId()).eq(UserRelationFlow::getLevel, 1).eq(UserRelationFlow::getNode, 1));
            if (userRelationFlow01 != null) {
                //第三层
                UserRelationGplotVo relation01 = gplot(userRelationFlow01.getUId());
                relation01.setNode(1);
                topList1.add(relation01);
                List<UserRelationGplotVo> topList22 = new ArrayList<>();
                UserRelationFlow userRelationFlow010 = getOne(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, userRelationFlow01.getUId()).eq(UserRelationFlow::getLevel, 1).eq(UserRelationFlow::getNode, 0));
                if (userRelationFlow010 != null) {
                    //第四层
                    UserRelationGplotVo relation010 = gplot(userRelationFlow010.getUId());
                    relation010.setNode(0);
                    topList22.add(relation010);
                } else {
                    UserRelationGplotVo relation010 = new UserRelationGplotVo();
                    topList22.add(relation010);
                }
                UserRelationFlow userRelationFlow011 = getOne(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, userRelationFlow01.getUId()).eq(UserRelationFlow::getLevel, 1).eq(UserRelationFlow::getNode, 1));
                if (userRelationFlow011 != null) {
                    //第四层
                    UserRelationGplotVo relation011 = gplot(userRelationFlow011.getUId());
                    relation011.setNode(1);
                    topList22.add(relation011);
                } else {
                    UserRelationGplotVo relation011 = new UserRelationGplotVo();
                    topList22.add(relation011);
                }
                relation01.setChildren(topList22);
            } else {
                UserRelationGplotVo relation01 = new UserRelationGplotVo();
                topList1.add(relation01);
            }
            relation0.setChildren(topList1);
        } else {
            UserRelationGplotVo relation0 = new UserRelationGplotVo();
            topList.add(relation0);
        }
        UserRelationFlow userRelationFlow1 = getOne(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, uid).eq(UserRelationFlow::getLevel, 1).eq(UserRelationFlow::getNode, 1));
        if (userRelationFlow1 != null) {
            //第二层
            UserRelationGplotVo relation1 = gplot(userRelationFlow1.getUId());
            relation1.setNode(1);
            topList.add(relation1);
            List<UserRelationGplotVo> topList2 = new ArrayList<>();
            UserRelationFlow userRelationFlow10 = getOne(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, userRelationFlow1.getUId()).eq(UserRelationFlow::getLevel, 1).eq(UserRelationFlow::getNode, 0));
            if (userRelationFlow10 != null) {
                //第三层
                UserRelationGplotVo relation10 = gplot(userRelationFlow10.getUId());
                relation10.setNode(0);
                topList2.add(relation10);
                List<UserRelationGplotVo> topList23 = new ArrayList<>();
                UserRelationFlow userRelationFlow100 = getOne(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, userRelationFlow10.getUId()).eq(UserRelationFlow::getLevel, 1).eq(UserRelationFlow::getNode, 0));
                if (userRelationFlow100 != null) {
                    //第四层
                    UserRelationGplotVo relation100 = gplot(userRelationFlow100.getUId());
                    relation100.setNode(0);
                    topList23.add(relation100);
                } else {
                    UserRelationGplotVo relation100 = new UserRelationGplotVo();
                    topList23.add(relation100);
                }
                UserRelationFlow userRelationFlow101 = getOne(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, userRelationFlow10.getUId()).eq(UserRelationFlow::getLevel, 1).eq(UserRelationFlow::getNode, 1));
                if (userRelationFlow101 != null) {
                    //第四层
                    UserRelationGplotVo relation101 = gplot(userRelationFlow101.getUId());
                    relation101.setNode(1);
                    topList23.add(relation101);
                } else {
                    UserRelationGplotVo relation101 = new UserRelationGplotVo();
                    topList23.add(relation101);
                }
                relation10.setChildren(topList23);
            } else {
                UserRelationGplotVo relation20 = new UserRelationGplotVo();
                topList2.add(relation20);
            }
            UserRelationFlow userRelationFlow11 = getOne(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, userRelationFlow1.getUId()).eq(UserRelationFlow::getLevel, 1).eq(UserRelationFlow::getNode, 1));
            if (userRelationFlow11 != null) {
                //第三层
                UserRelationGplotVo relation11 = gplot(userRelationFlow11.getUId());
                relation11.setNode(1);
                topList2.add(relation11);
                List<UserRelationGplotVo> topList24 = new ArrayList<>();
                UserRelationFlow userRelationFlow110 = getOne(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, userRelationFlow11.getUId()).eq(UserRelationFlow::getLevel, 1).eq(UserRelationFlow::getNode, 0));
                if (userRelationFlow110 != null) {
                    //第四层
                    UserRelationGplotVo relation110 = gplot(userRelationFlow110.getUId());
                    relation110.setNode(0);
                    topList24.add(relation110);
                } else {
                    UserRelationGplotVo relation110 = new UserRelationGplotVo();
                    topList24.add(relation110);
                }
                UserRelationFlow userRelationFlow111 = getOne(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, userRelationFlow11.getUId()).eq(UserRelationFlow::getLevel, 1).eq(UserRelationFlow::getNode, 1));
                if (userRelationFlow111 != null) {
                    //第四层
                    UserRelationGplotVo relation111 = gplot(userRelationFlow111.getUId());
                    relation111.setNode(1);
                    topList24.add(relation111);
                } else {
                    UserRelationGplotVo relation111 = new UserRelationGplotVo();
                    topList24.add(relation111);
                }
                relation11.setChildren(topList24);
            } else {
                UserRelationGplotVo relation11 = new UserRelationGplotVo();
                topList2.add(relation11);
            }
            relation1.setChildren(topList2);
        } else {
            UserRelationGplotVo relation1 = new UserRelationGplotVo();
            topList.add(relation1);
        }
        top.setChildren(topList);
        return top;
    }

    @Override
    public List<Integer> selectByCapa(Integer uId) {
        List<Integer> countList = new ArrayList<>();
        List<Capa> list = capaService.getList();
        UserCapa userCapa = userCapaService.getOne(new QueryWrapper<UserCapa>().eq("uid", uId));
        list.forEach(e->{
            if (userCapa != null && Objects.equals(userCapa.getCapaId(), e.getId())){
                //加上自己
                List<UserRelationFlow> userRelationFlowList = list(new LambdaQueryWrapper<UserRelationFlow>().eq(UserRelationFlow::getPId, uId).apply("uid in(select uid from eb_user_capa where capa_id=" + e.getId() + ")"));
                countList.add(userRelationFlowList.size()+1);
            }else {
                List<UserRelationFlow> userRelationFlowList = list(new LambdaQueryWrapper<UserRelationFlow>().eq(UserRelationFlow::getPId, uId).apply("uid in(select uid from eb_user_capa where capa_id=" + e.getId() + ")"));
                countList.add(userRelationFlowList.size());
            }
        });
        return countList;
    }
}
