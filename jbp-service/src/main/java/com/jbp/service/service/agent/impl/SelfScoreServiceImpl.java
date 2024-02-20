package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.InvitationScoreFlow;
import com.jbp.common.model.agent.SelfScore;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.SelfScoreDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.SelfScoreService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SelfScoreServiceImpl extends ServiceImpl<SelfScoreDao, SelfScore> implements SelfScoreService {
    @Resource
    private UserService userService;
    @Override
    public PageInfo<SelfScore> pageList(Integer uid, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<SelfScore> lqw = new LambdaQueryWrapper<SelfScore>()
                .eq(!ObjectUtil.isNull(uid), SelfScore::getUid, uid);
        Page<SelfScore> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<SelfScore> list = list(lqw);
        list.forEach(e -> {
            e.setAccount(userService.getById(e.getUid()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
