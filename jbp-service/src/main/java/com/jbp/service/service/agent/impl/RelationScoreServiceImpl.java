package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.RelationScore;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.RelationScoreDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.RelationScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class RelationScoreServiceImpl extends ServiceImpl<RelationScoreDao, RelationScore> implements RelationScoreService {
    @Resource
    private UserService userService;

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
        Page<RelationScore> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<RelationScore> list = list(lqw);
        list.forEach(e -> {
            e.setAccount(userService.getById(e.getUid()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Boolean edit(Long id, BigDecimal usableScore, BigDecimal usedScore, int node) {
        RelationScore relationScore = getById(id);
        relationScore.setUsableScore(usableScore);
        relationScore.setUsedScore(usedScore);
        relationScore.setNode(node);
        updateById(relationScore);
        return true;
    }

    @Override
    public Boolean save(Integer uid, int node) {
        LambdaQueryWrapper<RelationScore> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RelationScore::getUid, uid);
        lqw.eq(RelationScore::getNode, node);
        List<RelationScore> relationScoreList = list(lqw);
        if (relationScoreList.size() > 0) {
            throw new CrmebException("服务业绩汇总已存在");
        }
        RelationScore relationScore = new RelationScore(uid, node);
        save(relationScore);
        return true;
    }


}
