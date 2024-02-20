package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.InvitationScore;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.InvitationScoreDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.InvitationScoreService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class InvitationScoreServiceImpl extends ServiceImpl<InvitationScoreDao, InvitationScore> implements InvitationScoreService {
    @Resource
    private UserService userService;

    @Override
    public PageInfo<InvitationScore> pageList(Integer uid, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<InvitationScore> lqw = new LambdaQueryWrapper<InvitationScore>()
                .eq(!ObjectUtil.isNull(uid), InvitationScore::getUid, uid);
        Page<InvitationScore> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<InvitationScore> list = list(lqw);
        list.forEach(e -> {
            e.setAccount(userService.getById(e.getUid()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
