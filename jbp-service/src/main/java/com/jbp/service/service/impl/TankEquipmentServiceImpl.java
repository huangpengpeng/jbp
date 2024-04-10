package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.tank.TankEquipment;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.EquipmentListResponse;
import com.jbp.service.dao.TankEquipmentDao;
import com.jbp.service.service.TankEquipmentService;
import com.jbp.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class TankEquipmentServiceImpl extends ServiceImpl<TankEquipmentDao, TankEquipment> implements TankEquipmentService {

    @Resource
    private TankEquipmentDao dao;
    @Resource
    private UserService userService;

    @Override
    public TankEquipment getEquipmentSn(Long equipmentSn) {
        return  dao.selectOne(new QueryWrapper<TankEquipment>().lambda().eq(TankEquipment::getEquipmentSn,equipmentSn));
    }


    @Override
    public List<TankEquipment> getStoreId(Long storeId) {
         return  dao.selectList(new QueryWrapper<TankEquipment>().lambda().eq(TankEquipment::getStoreId,storeId));
    }

    @Override
    public PageInfo<EquipmentListResponse> getPageList(String type, PageParamRequest pageParamRequest) {


        Page<EquipmentListResponse> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<EquipmentListResponse> activateInfoResponses = dao.getPageList(userService.getInfo().getId(),type);

        activateInfoResponses.forEach(e ->{
            if(e.getStatus().equals(0) && e.getActivateStatus().equals(1) && e.getOnlineStatus().equals(1)  && e.getUseStatus().equals(1)){
                e.setEquipmentStatus("使用中");
            }else if (e.getStatus().equals(0) &&  e.getActivateStatus().equals(0) && e.getOnlineStatus().equals(1)  && e.getUseStatus().equals(1)){
                e.setEquipmentStatus("在线未用");
            }else {
                e.setEquipmentStatus("离线");
            }
        });


        return CommonPage.copyPageInfo(page, activateInfoResponses);
    }

    @Override
    public Map<String, Object> getInfo(String equipmentSn) {

        Map<String,Object> map =dao.getInfo(equipmentSn);

        return map;
    }

    @Override
    public Integer equipmentNumber() {
        return dao.equipmentNumber(userService.getInfo().getId());
    }

    @Override
    public Integer equipmentUseNumber() {
        return dao.equipmentUseNumber(userService.getInfo().getId());
    }

    @Override
    public Integer equipmentOnlineUnusedNumber() {
        return dao.equipmentOnlineUnusedNumber(userService.getInfo().getId());
    }

    @Override
    public Integer equipmentOfflinedNumber() {
        return dao.equipmentOfflinedNumber(userService.getInfo().getId());
    }

}
