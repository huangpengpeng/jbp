package com.jbp.service.service.agent.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.RelationScore;
import com.jbp.common.model.agent.RelationScoreFlow;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserRelation;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.RelationScoreResponse;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.vo.RelationScoreVo;
import com.jbp.service.dao.agent.RelationScoreDao;
import com.jbp.service.product.comm.CollisionCommHandler;
import com.jbp.service.product.comm.DepthCommHandler;
import com.jbp.service.product.comm.ProductCommEnum;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class RelationScoreServiceImpl extends ServiceImpl<RelationScoreDao, RelationScore> implements RelationScoreService {
    @Resource
    private UserService userService;
    @Resource
    private RelationScoreFlowService relationScoreFlowService;
    @Resource
    private UserRelationService userRelationService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private CollisionCommHandler collisionCommHandler;
    @Resource
    private DepthCommHandler depthCommHandler;
    @Resource
    private FundClearingService fundClearingService;

    @Override
    public RelationScore getByUser(Integer uId, Integer node) {
        LambdaQueryWrapper<RelationScore> query = new LambdaQueryWrapper<>();
        query.eq(RelationScore::getUid, uId).eq(RelationScore::getNode, node);
        return getOne(query);
    }

    @Override
    public PageInfo<RelationScore> pageList(Integer uid, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<RelationScore> lqw = new LambdaQueryWrapper<RelationScore>()
                .eq(!ObjectUtil.isNull(uid), RelationScore::getUid, uid);
        lqw.last("order by uid, id desc ");
        Page<RelationScore> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<RelationScore> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(RelationScore::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public List<RelationScoreVo> excel(Integer uid) {
        Long id = 0L;
        List<RelationScoreVo> voList = CollUtil.newArrayList();
        do {
            LambdaQueryWrapper<RelationScore> lqw = new LambdaQueryWrapper<RelationScore>()
                    .eq(!ObjectUtil.isNull(uid), RelationScore::getUid, uid)
                    .orderByAsc(RelationScore::getId);
            lqw.gt(RelationScore::getId, id).last("LIMIT 1000");
            List<RelationScore> fundClearingVos = list(lqw);
            if (CollectionUtils.isEmpty(fundClearingVos)) {
                break;
            }
            List<Integer> uIdList = fundClearingVos.stream().map(RelationScore::getUid).collect(Collectors.toList());
            Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
            fundClearingVos.forEach(e -> {
                User user = uidMapList.get(e.getUid());
                e.setAccount(user != null ? user.getAccount() : "");
                RelationScoreVo relationScoreVo=new RelationScoreVo();
                BeanUtils.copyProperties(e,relationScoreVo);
                voList.add(relationScoreVo);
            });
            id = fundClearingVos.get(fundClearingVos.size()-1).getId();
        } while (true);
        return voList;
    }


    @Override
    public RelationScoreFlow orderSuccessIncrease(Integer uid, Integer orderUid, BigDecimal score, int node,
                                                  String ordersSn, Date payTime, List<ProductInfoDto> productInfo, Integer level) {
        RelationScore relationScore = getByUser(uid, node);
        if (relationScore == null) {
            relationScore = new RelationScore(uid, node);
            save(relationScore);
        }
        // 更新可用
        relationScore.setUsableScore(relationScore.getUsableScore().add(score));
        updateById(relationScore);
        // 增加明细
        RelationScoreFlow flow = new RelationScoreFlow(uid, orderUid, score, node,
                "下单", "增加", ordersSn, payTime, productInfo, "", level, BigDecimal.ZERO, BigDecimal.ZERO);
        return flow;
    }

    @Override
    public RelationScoreFlow orderSuccessReduce(Integer uid, Integer orderUid, BigDecimal score, int node, String ordersSn,
                                                Date payTime, Integer level, BigDecimal amt, BigDecimal ratio, String remark) {
        RelationScore relationScore = getByUser(uid, node);
        if (relationScore == null) {
            throw new CrmebException("对碰减少积分不存在，用户:" + uid + ",点位:" + node);
        }
        relationScore.setUsableScore(relationScore.getUsableScore().subtract(score));
        relationScore.setUsedScore(relationScore.getUsedScore().add(score));
        updateById(relationScore);
        if (relationScore.hasError()) {
            throw new CrmebException("对碰减少积分错误，用户:" + uid + ",点位:" + node + ",减少积分:" + score);
        }
        // 增加明细
        RelationScoreFlow flow = new RelationScoreFlow(uid, orderUid, score, node,
                "对碰", "减少", ordersSn, payTime, null, remark, level, amt, ratio);
        relationScoreFlowService.save(flow);
        return flow;
    }

    @Override
    public void operateUsable(Integer uid, BigDecimal score, int node, String ordersSn, Date payTime, String remark, Boolean ifAdd) {
        RelationScore relationScore = getByUser(uid, node);
        // 减少
        if (BooleanUtils.isFalse(ifAdd)) {
            if (relationScore == null || ArithmeticUtils.less(relationScore.getUsableScore(), score)) {
                throw new CrmebException("可用积分不足");
            }
            relationScore.setUsableScore(relationScore.getUsableScore().subtract(score));
            updateById(relationScore);
            Boolean ifSuccess = updateById(relationScore);
            if (BooleanUtils.isNotTrue(ifSuccess)) {
                throw new CrmebException("当前操作人数过多");
            }
            RelationScoreFlow flow = new RelationScoreFlow(uid, null, score, node,
                    "调分可用", "减少", ordersSn, payTime, null, remark, 0, BigDecimal.ZERO, BigDecimal.ZERO);
            relationScoreFlowService.save(flow);
            return;
        }
        // 增加
        if (BooleanUtils.isTrue(ifAdd) && relationScore == null) {
            relationScore = new RelationScore(uid, node);
            save(relationScore);
        }
        int backNode = node == 0 ? 1 : 0;
        final RelationScore backRelationScore = getByUser(uid, backNode);
        if (backRelationScore != null && ArithmeticUtils.gt(backRelationScore.getUsableScore(), BigDecimal.ZERO)) {
            throw new CrmebException("反方向存在可用积分不允许新增");
        }
        relationScore.setUsableScore(relationScore.getUsableScore().add(score));
        Boolean ifSuccess = updateById(relationScore);
        if (BooleanUtils.isNotTrue(ifSuccess)) {
            throw new CrmebException("当前操作人数过多");
        }
        // 增加明细
        RelationScoreFlow flow = new RelationScoreFlow(uid, null, score, node,
                "调分可用", "增加", ordersSn, payTime, null, remark, 0, BigDecimal.ZERO, BigDecimal.ZERO);
        relationScoreFlowService.save(flow);
    }

    @Override
    public void operateUsed(Integer uid, BigDecimal score, int node, String ordersSn, Date payTime, String remark, Boolean ifAdd) {
        RelationScore relationScore = getByUser(uid, node);
        // 减少
        if (BooleanUtils.isFalse(ifAdd)) {
            if (relationScore == null || ArithmeticUtils.less(relationScore.getUsedScore(), score)) {
                throw new CrmebException("已用积分不足");
            }
            relationScore.setUsedScore(relationScore.getUsedScore().subtract(score));
            updateById(relationScore);
            Boolean ifSuccess = updateById(relationScore);
            if (BooleanUtils.isNotTrue(ifSuccess)) {
                throw new CrmebException("当前操作人数过多");
            }
            RelationScoreFlow flow = new RelationScoreFlow(uid, null, score, node,
                    "调分已用", "减少", ordersSn, payTime, null, remark, 0, BigDecimal.ZERO, BigDecimal.ZERO);
            relationScoreFlowService.save(flow);
            return;
        }
        // 增加
        if (BooleanUtils.isTrue(ifAdd) && relationScore == null) {
            relationScore = new RelationScore(uid, node);
            save(relationScore);
        }
        relationScore.setUsedScore(relationScore.getUsedScore().add(score));
        Boolean ifSuccess = updateById(relationScore);
        if (BooleanUtils.isNotTrue(ifSuccess)) {
            throw new CrmebException("当前操作人数过多");
        }
        // 增加明细
        RelationScoreFlow flow = new RelationScoreFlow(uid, null, score, node,
                "调分已用", "增加", ordersSn, payTime, null, remark, 0, BigDecimal.ZERO, BigDecimal.ZERO);
        relationScoreFlowService.save(flow);
    }

        @Override
        public RelationScoreResponse getUserResult() {
            RelationScoreResponse relationScoreResponse = new RelationScoreResponse();
            User user = userService.getInfo();

            UserRelation userRelation = userRelationService.getByPid(user.getId(), 0);
            UserRelation userRelation2 = userRelationService.getByPid(user.getId(), 1);

            if (userRelation != null) {
                User user1 = userService.getById(userRelation.getUId());
                UserCapa userCapa = userCapaService.getByUser(userRelation.getUId());

                relationScoreResponse.setNickname(user1.getNickname());
                relationScoreResponse.setAccount(user1.getAccount());
                relationScoreResponse.setUserUrl(user1.getAvatar());
                relationScoreResponse.setCapaImg(userCapa.getCapaUrl());
            }
            if (userRelation2 != null) {
                User user1 = userService.getById(userRelation2.getUId());
                UserCapa userCapa = userCapaService.getByUser(userRelation2.getUId());
                relationScoreResponse.setNickname2(user1.getNickname());
                relationScoreResponse.setAccount2(user1.getAccount());
                relationScoreResponse.setUserUrl2(user1.getAvatar());
                relationScoreResponse.setCapaImg2(userCapa.getCapaUrl());
            }

            RelationScore relationScore = getOne(new QueryWrapper<RelationScore>().lambda().eq(RelationScore::getUid, user.getId()).eq(RelationScore::getNode, 0));

            relationScoreResponse.setTotalScore(relationScore == null ? BigDecimal.ZERO : relationScore.getUsableScore().add(relationScore.getUsedScore()));
            relationScoreResponse.setUsableScore(relationScore == null ? BigDecimal.ZERO : relationScore.getUsableScore());

            RelationScore relationScore2 = getOne(new QueryWrapper<RelationScore>().lambda().eq(RelationScore::getUid, user.getId()).eq(RelationScore::getNode, 1));
            relationScoreResponse.setTotalScore2(relationScore2 == null ? BigDecimal.ZERO : relationScore2.getUsableScore().add(relationScore2.getUsedScore()));
            relationScoreResponse.setUsableScore2(relationScore2 == null ? BigDecimal.ZERO : relationScore2.getUsableScore());
            return relationScoreResponse;
        }

    @Override
    public void orderRefund(String platOrderNo) {
        // 下单增加的明细
        List<RelationScoreFlow> addList = relationScoreFlowService.getByOrders(platOrderNo, "下单", "增加");
        if (CollectionUtils.isEmpty(addList)) {
            return;
        }
        Set<Long> idSet = new HashSet<>();
        // 对碰减少
        List<RelationScoreFlow> reduceList = relationScoreFlowService.getByOrders(platOrderNo, "对碰", "减少");
        // 先把减少增加
        for (RelationScoreFlow flow : reduceList) {
            RelationScore relationScore = getByUser(flow.getUid(), flow.getNode());
            relationScore.setUsableScore(relationScore.getUsableScore().add(flow.getScore()));
            relationScore.setUsedScore(relationScore.getUsedScore().subtract(flow.getScore()));
            if (ArithmeticUtils.less(relationScore.getUsedScore(), BigDecimal.ZERO)) {
                relationScore.setUsedScore(BigDecimal.ZERO);
            }
            updateById(relationScore);
            idSet.add(flow.getId());
        }
        // 在将增加减少
        for (RelationScoreFlow flow : addList) {
            RelationScore relationScore = getByUser(flow.getUid(), flow.getNode());
            relationScore.setUsableScore(relationScore.getUsableScore().subtract(flow.getScore()));
            if (ArithmeticUtils.less(relationScore.getUsableScore(), BigDecimal.ZERO)) {
                relationScore.setUsedScore(BigDecimal.ZERO);
            }
            updateById(relationScore);
            idSet.add(flow.getId());
        }
        // 检查对碰
        for (RelationScoreFlow flow : addList) {
            int backNode = flow.getNode() == 0 ? 1 : 0; // 反方向
            RelationScore frontRelationScore = getByUser(flow.getUid(), flow.getNode());
            RelationScore backRelationScore = getByUser(flow.getUid(), backNode);
            // 正
            BigDecimal frontScore = frontRelationScore == null ? BigDecimal.ZERO : frontRelationScore.getUsableScore();
            // 反
            BigDecimal backScore = backRelationScore == null ? BigDecimal.ZERO : backRelationScore.getUsableScore();
            // 对碰积分
            BigDecimal minScore = BigDecimal.valueOf(Math.min(frontScore.doubleValue(), backScore.doubleValue()));

            if (ArithmeticUtils.lessEquals(minScore, BigDecimal.ZERO)) {
                continue;
            }

            // 对碰规则
            CollisionCommHandler.Rule rule = collisionCommHandler.getRule(null);
            Map<Long, CollisionCommHandler.CapaRatio> capaRatioMap = FunctionUtil.keyValueMap(rule.getCapaRatioList(), CollisionCommHandler.CapaRatio::getCapaId);
            Map<Integer, CollisionCommHandler.LevelRatio> levelRatioMap = FunctionUtil.keyValueMap(rule.getLevelRatioList(), CollisionCommHandler.LevelRatio::getLevel);
            // 深度规则
            List<DepthCommHandler.Rule> depthRuleList = depthCommHandler.getRule(null);
            Map<Long, DepthCommHandler.Rule> depthRuleMap = FunctionUtil.keyValueMap(depthRuleList, DepthCommHandler.Rule::getCapaId);

            // 等级比例
            BigDecimal ratio = BigDecimal.ZERO;
            UserCapa userCapa = userCapaService.getByUser(flow.getUid());
            if (userCapa != null) {
                CollisionCommHandler.CapaRatio capaRatio = capaRatioMap.get(userCapa.getCapaId());
                ratio = capaRatio == null ? BigDecimal.ZERO : capaRatio.getRatio();
            }
            // 层级比例
            BigDecimal lRatio = BigDecimal.ZERO;
            CollisionCommHandler.LevelRatio levelRatio = levelRatioMap.get(flow.getLevel());
            if (levelRatio != null) {
                lRatio = levelRatio.getRatio();
            }
            // 奖金比例
            ratio = ratio.multiply(lRatio);
            // 奖励金额[保留2为小数]
            BigDecimal amt = ratio.multiply(minScore).setScale(2, BigDecimal.ROUND_DOWN);

            String remark = "";
            List<CollisionCommHandler.LimitAmt> limitList = rule.getLimitList();
            if (CollectionUtils.isNotEmpty(limitList)) {
                Map<Long, BigDecimal> limitMap = FunctionUtil.keyValueMap(limitList, CollisionCommHandler.LimitAmt::getCapaId, CollisionCommHandler.LimitAmt::getMaxAmt);
                BigDecimal maxAmt = limitMap.get(userCapa.getCapaId());
                if (maxAmt != null) {
                    BigDecimal orgAmt = amt;
                    // 增加奖励
                    Date now = DateTimeUtils.getNow();
                    Date start = DateTimeUtils.getStartDate(now);
                    Date end = DateTimeUtils.getFinallyDate(now);
                    BigDecimal usedAmt = fundClearingService.getSendCommAmt(flow.getUid(), start, end, ProductCommEnum.渠道佣金.getName());
                    BigDecimal usableAmt = maxAmt.subtract(usedAmt);
                    if (ArithmeticUtils.lessEquals(usableAmt, BigDecimal.ZERO)) {
                        amt = BigDecimal.ZERO;
                        remark = "当日已得金额:" + usedAmt + ",最大金额限制:" + maxAmt + ",本次应得金额:" + orgAmt + ",实得金额:" + amt;
                    } else {
                        amt = BigDecimal.valueOf(Math.min(usableAmt.doubleValue(), amt.doubleValue()));
                        remark = "当日已得金额:" + usedAmt + ",最大金额限制:" + maxAmt + ",本次应得金额:" + orgAmt + ",实得金额:" + amt;
                    }
                }
            }
            // 减少反方向
            orderSuccessReduce(backRelationScore.getUid(), flow.getOrderUid(), minScore, backRelationScore.getNode(), "RE_" + flow.getOrdersSn(),
                    flow.getPayTime(), flow.getLevel(), amt, ratio, remark);
            // 减少正方向
            orderSuccessReduce(frontRelationScore.getUid(), flow.getOrderUid(), minScore, frontRelationScore.getNode(), "RE_" + flow.getOrdersSn(),
                    flow.getPayTime(), flow.getLevel(), amt, ratio, remark);

            User orderUser = userService.getById(flow.getOrderUid());
            if (ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {
                fundClearingService.create(flow.getUid(), "RE_" + flow.getOrdersSn(), ProductCommEnum.渠道佣金.getName(), amt,
                        null, orderUser.getAccount() + "退款重碰获得" + ProductCommEnum.渠道佣金.getName(), "");

                DepthCommHandler.Rule depthRule = userCapa == null ? null : depthRuleMap.get(userCapa.getCapaId());
                BigDecimal depthRatio = rule == null ? BigDecimal.ZERO : depthRule.getRatio();
                BigDecimal depthAmt = minScore.multiply(depthRatio).setScale(2, BigDecimal.ROUND_DOWN);
                if (ArithmeticUtils.gt(depthAmt, BigDecimal.ZERO)) {
                    fundClearingService.create(flow.getUid(), "RE_" + flow.getOrdersSn(), ProductCommEnum.深度佣金.getName(), amt,
                            null, orderUser.getAccount() + "退款重碰，获得对碰佣金，奖励" + ProductCommEnum.深度佣金.getName(), "");
                }
            }
        }

        // 删除明细
        relationScoreFlowService.removeByIds(idSet);
    }
}
