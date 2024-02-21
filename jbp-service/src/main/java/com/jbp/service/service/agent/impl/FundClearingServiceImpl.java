package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.FundClearingDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.FundClearingService;
import com.jbp.service.service.agent.OrdersFundSummaryService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserCapaXsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class FundClearingServiceImpl extends ServiceImpl<FundClearingDao, FundClearing> implements FundClearingService {

    @Resource
    private OrdersFundSummaryService ordersFundSummaryService;
    @Resource
    private UserService userService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private UserCapaXsService userCapaXsService;

    @Override
    public PageInfo<FundClearing> pageList(String uniqueNo, String externalNo, Date startClearingTime, Date endClearingTime, Date starteCreateTime, Date endCreateTime, String status, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<FundClearing> lqw = new LambdaQueryWrapper<FundClearing>()
                .like(!ObjectUtil.isNull(uniqueNo) && !uniqueNo.equals(""), FundClearing::getUniqueNo, uniqueNo)
                .like(!ObjectUtil.isNull(externalNo) && !externalNo.equals(""), FundClearing::getExternalNo, externalNo)
                .eq(!ObjectUtil.isNull(status) && !status.equals(""), FundClearing::getStatus, status)
                .between(!ObjectUtil.isNull(startClearingTime) && !ObjectUtil.isNull(endClearingTime), FundClearing::getClearingTime, startClearingTime, endClearingTime)
                .between(!ObjectUtil.isNull(starteCreateTime) && !ObjectUtil.isNull(endCreateTime), FundClearing::getCreateTime, starteCreateTime, endCreateTime);
        Page<FundClearing> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<FundClearing> list = list(lqw);
        list.forEach(e -> {
            e.setAccount(userService.getById(e.getUid()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public FundClearing create(Integer uid, String externalNo, String commName, BigDecimal commAmt,
                               List<FundClearingItem> items, List<FundClearingProduct> productList, String description, String remark) {
        User user = userService.getById(uid);
        UserInfo userInfo = new UserInfo(user.getNickname(), user.getAccount());
        UserCapa userCapa = userCapaService.getByUser(uid);
        if (userCapa != null) {
            userInfo.setCapaId(userCapa.getCapaId());
            userInfo.setCapaName(userCapa.getCapaName());
        }
        UserCapaXs userCapaXs = userCapaXsService.getByUser(uid);
        if (userCapaXs != null) {
            userInfo.setCapaXsId(userCapaXs.getCapaId());
            userInfo.setCapaXsName(userCapaXs.getCapaName());
        }
        FundClearing fundClearing = new FundClearing(uid, externalNo, commName, commAmt, userInfo, items,
                productList, description, remark);
        save(fundClearing);

        // 更新概况
        ordersFundSummaryService.increaseCommAmt(externalNo, commAmt);
        return fundClearing;
    }

    @Override
    public List<FundClearing> getByUser(Integer uid, String commName, List<String> statusList) {
        QueryWrapper<FundClearing> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(FundClearing::getUid, uid)
                .eq(FundClearing::getCommName, commName)
                .in(FundClearing::getStatus, statusList);
        return list(queryWrapper);
    }

}
