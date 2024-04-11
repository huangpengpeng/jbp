package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.tank.TankOrders;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.EquipmentAdminListResponse;
import com.jbp.common.response.EquipmentNumberInfoAdminListResponse;
import com.jbp.common.response.TankOrderAdminListResponse;
import com.jbp.service.dao.TankOrdersDao;
import com.jbp.service.service.TankOrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Service
public class TankOrdersServiceImpl extends ServiceImpl<TankOrdersDao, TankOrders> implements TankOrdersService {

    @Resource
    private TankOrdersDao dao;

    @Override
    public TankOrders getOrderSn(String orderSn) {
        return  dao.selectOne(new QueryWrapper<TankOrders>().lambda().eq(TankOrders::getOrderSn,orderSn));
    }

    @Override
    public PageInfo<TankOrderAdminListResponse> getAdminPageList(String username, String status, String startCreateTime, String endCreateTime, PageParamRequest pageParamRequest) {
        Page<TankOrderAdminListResponse> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<TankOrderAdminListResponse> activateInfoResponses = dao.getAdminPageList(username,status,startCreateTime,endCreateTime);

        return CommonPage.copyPageInfo(page, activateInfoResponses);
    }


}
