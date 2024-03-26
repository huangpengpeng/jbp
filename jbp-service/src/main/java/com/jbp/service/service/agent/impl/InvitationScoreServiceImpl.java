package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.InvitationScore;
import com.jbp.common.model.agent.InvitationScoreFlow;
import com.jbp.common.model.agent.SelfScore;
import com.jbp.common.model.agent.SelfScoreFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.service.dao.agent.InvitationScoreDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.InvitationScoreFlowService;
import com.jbp.service.service.agent.InvitationScoreService;
import com.jbp.service.service.agent.SelfScoreService;
import com.jbp.service.service.agent.UserInvitationService;
import com.jbp.service.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class InvitationScoreServiceImpl extends ServiceImpl<InvitationScoreDao, InvitationScore> implements InvitationScoreService {
    @Resource
    private UserService userService;
    @Resource
    private InvitationScoreFlowService invitationScoreFlowService;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private SelfScoreService selfScoreService;


    @Override
    public void init() {
        InvitationScoreFlow one = invitationScoreFlowService.getOne(new QueryWrapper<InvitationScoreFlow>().lambda().
                like(InvitationScoreFlow::getOperate, "初始化").last("limit 1"));
        if (one != null) {
            return;
        }
        List<SelfScore> list = selfScoreService.list();
        for (SelfScore selfScore : list) {
            List<UserUpperDto> allUpper = userInvitationService.getAllUpper(selfScore.getUid());
            if (CollectionUtils.isEmpty(allUpper)) {
                continue;
            }
            for (UserUpperDto upperDto : allUpper) {
                if (upperDto.getPId() != null) {
                    InvitationScore invitationScore = getByUser(upperDto.getPId());
                    if (invitationScore == null) {
                        invitationScore = add(upperDto.getPId());
                    }
                    invitationScore.setScore(invitationScore.getScore().add(selfScore.getScore()));
                    updateById(invitationScore);
                    invitationScoreFlowService.add(upperDto.getPId(), selfScore.getUid(), selfScore.getScore(), "增加",
                            "初始化", CrmebUtil.getOrderNo("INIT_"), DateTimeUtils.getNow(), null, "初始化");
                }
            }
        }
    }


    @Override
    public PageInfo<InvitationScore> pageList(Integer uid,String dateLimit,  PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<InvitationScore> lqw = new LambdaQueryWrapper<InvitationScore>()
                .eq(!ObjectUtil.isNull(uid), InvitationScore::getUid, uid)
                .orderByDesc(InvitationScore::getId);
        getRequestTimeWhere(lqw,dateLimit);
        Page<InvitationScore> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<InvitationScore> list = list(lqw);
        if(CollectionUtils.isEmpty(list)){
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(InvitationScore::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }
    private void getRequestTimeWhere(LambdaQueryWrapper<InvitationScore> lqw, String dateLimit) {
        DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(dateLimit);
        lqw.between(StringUtils.isNotEmpty(dateLimit), InvitationScore::getGmtCreated, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
    }
    @Override
    public InvitationScore add(Integer uid) {
        InvitationScore invitationScore = new InvitationScore(uid);
        save(invitationScore);
        return invitationScore;
    }

    @Override
    public InvitationScore getByUser(Integer uid) {
        return getOne(new QueryWrapper<InvitationScore>().lambda().eq(InvitationScore::getUid, uid));
    }

    @Override
    public BigDecimal getInvitationScore(Integer uid, Boolean containsSelf) {
        InvitationScore nextInvitationScore = getByUser(uid);
        BigDecimal score = nextInvitationScore == null ? BigDecimal.ZERO : nextInvitationScore.getScore();
        if (BooleanUtils.isNotTrue(containsSelf)) {
            return score;
        }
        // 自己业绩
        SelfScore self = selfScoreService.getByUser(uid);
        BigDecimal selfScore = self == null ? BigDecimal.ZERO : self.getScore();
        score.add(selfScore);
        return score;
    }

    @Override
    public void orderSuccess(Integer uid, BigDecimal score, String ordersSn, Date payTime, List<ProductInfoDto> productInfo) {
        InvitationScoreFlow one = invitationScoreFlowService.getOne(new QueryWrapper<InvitationScoreFlow>().lambda()
                .eq(InvitationScoreFlow::getOrdersSn, ordersSn).last(" limit 1"));
        if (one != null) {
            return;
        }
        List<UserUpperDto> allUpper = userInvitationService.getAllUpper(uid);
        if (CollectionUtils.isEmpty(allUpper)) {
            return;
        }
        for (UserUpperDto upperDto : allUpper) {
            if (upperDto.getPId() != null) {
                InvitationScore invitationScore = getByUser(upperDto.getPId());
                if (invitationScore == null) {
                    invitationScore = add(upperDto.getPId());
                }
                invitationScore.setScore(invitationScore.getScore().add(score));
                updateById(invitationScore);
                invitationScoreFlowService.add(upperDto.getPId(), uid, score, "增加",
                        "下单", ordersSn, payTime, productInfo, "");
            }
        }
    }
}
