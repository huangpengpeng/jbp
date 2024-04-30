package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.OrderConstants;
import com.jbp.common.model.tank.TankEquipmentNumber;
import com.jbp.common.model.tank.TankEquipmentNumberInfo;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.ActivateAdminListResponse;
import com.jbp.common.response.EquipmentNumberAdminListResponse;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.service.dao.TankEquipmentNumberDao;
import com.jbp.service.service.TankEquipmentNumberInfoService;
import com.jbp.service.service.TankEquipmentNumberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class TankEquipmentNumberServiceImpl extends ServiceImpl<TankEquipmentNumberDao, TankEquipmentNumber> implements TankEquipmentNumberService {

    @Resource
    private TankEquipmentNumberDao dao;
    @Resource
    private TankEquipmentNumberInfoService tankEquipmentNumberInfoService;

    @Override
    public TankEquipmentNumber reduce(Long storeUserId,Long userId) {
        TankEquipmentNumber tankEquipmentNumber =    getStoreUserId(storeUserId);

        if(tankEquipmentNumber == null ){
            throw new RuntimeException("次数不足");
        }
        if(tankEquipmentNumber.getNumber()-1 <0 ){
            throw new RuntimeException("次数不足");
        }
        tankEquipmentNumber.setNumber(tankEquipmentNumber.getNumber()-1);
        updateById(tankEquipmentNumber);

        TankEquipmentNumberInfo tankEquipmentNumberInfo = new TankEquipmentNumberInfo();
        tankEquipmentNumberInfo.setStoreUserId(storeUserId);
        tankEquipmentNumberInfo.setType("消耗");
        tankEquipmentNumberInfo.setNumber(-1);
        tankEquipmentNumberInfo.setActivateId(userId);
        tankEquipmentNumberInfo.setCreatedTime(new Date());
        tankEquipmentNumberInfo.setOrderSn(CrmebUtil.getOrderNo(OrderConstants.GXC_ORDER_PREFIX));
        tankEquipmentNumberInfo.setCount(tankEquipmentNumber.getNumber());
        tankEquipmentNumberInfoService.save(tankEquipmentNumberInfo);
        return tankEquipmentNumber;
    }

    @Override
    public TankEquipmentNumber getStoreUserId(Long storeUserId) {

        TankEquipmentNumber tankEquipmentNumber = dao.selectOne(new QueryWrapper<TankEquipmentNumber>().lambda().eq(TankEquipmentNumber::getStoreUserId,storeUserId));

        return tankEquipmentNumber;
    }

    @Override
    public TankEquipmentNumber increase(Long storeUserId,Integer number,String orderSn,String remark) {

        TankEquipmentNumber tankEquipmentNumber =  getStoreUserId(storeUserId);
        if(tankEquipmentNumber == null ){
            tankEquipmentNumber = new TankEquipmentNumber();
            tankEquipmentNumber.setStoreUserId(storeUserId);
            tankEquipmentNumber.setNumber(number);
            save(tankEquipmentNumber);
        }else{
            tankEquipmentNumber.setNumber(tankEquipmentNumber.getNumber()+ number);
            updateById(tankEquipmentNumber);
        }

        TankEquipmentNumberInfo tankEquipmentNumberInfo = new TankEquipmentNumberInfo();
        tankEquipmentNumberInfo.setStoreUserId(storeUserId);
        tankEquipmentNumberInfo.setActivateId(storeUserId);
        tankEquipmentNumberInfo.setType("充值");
        tankEquipmentNumberInfo.setNumber(number);
        tankEquipmentNumberInfo.setCreatedTime(new Date());
        tankEquipmentNumberInfo.setOrderSn(CrmebUtil.getOrderNo(OrderConstants.GXC_ORDER_PREFIX));
        tankEquipmentNumberInfo.setCount(tankEquipmentNumber.getNumber());
        tankEquipmentNumberInfoService.save(tankEquipmentNumberInfo);

        return tankEquipmentNumber;
    }

    @Override
    public PageInfo<EquipmentNumberAdminListResponse> getAdminPageList(String username, PageParamRequest pageParamRequest) {
        Page<EquipmentNumberAdminListResponse> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<EquipmentNumberAdminListResponse> activateInfoResponses = dao.getAdminPageList(username);


        return CommonPage.copyPageInfo(page, activateInfoResponses);
    }

}
