package com.jbp.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.OrderConstants;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.tank.TankActivate;
import com.jbp.common.model.tank.TankEquipment;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.ActivateAdminListResponse;
import com.jbp.common.response.ActivateInfoResponse;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.service.dao.TankActivateDao;
import com.jbp.service.service.TankActivateService;
import com.jbp.service.service.TankEquipmentNumberService;
import com.jbp.service.service.TankEquipmentService;
import com.jbp.service.service.UserService;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;


@Slf4j
@Service
public class TankActivateServiceImpl extends ServiceImpl<TankActivateDao, TankActivate> implements TankActivateService {

    @Resource
    private UserService userService;
    @Resource
    private TankActivateService tankActivateService;
    @Resource
    private TankActivateDao dao;

    @Resource
    private TankEquipmentService tankEquipmentService;

    @Resource
    private TankEquipmentNumberService tankEquipmentNumberService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public Boolean activateEquipment(Integer worktime, Long equipment_id, String token) {

        return transactionTemplate.execute(e -> {
            User user = userService.getInfo();
            TankEquipment tankEquipment = tankEquipmentService.getEquipmentSn(equipment_id);
            if (ifStart(equipment_id, token)) {
                throw new IllegalStateException("设备正在运行中");
            }

            tankEquipmentNumberService.reduce(tankEquipment.getStoreUserId(), user.getId().longValue());
            Map<String, Object> params = new TreeMap<String, Object>();
            HttpRequest request = HttpRequest.post("https://system.swgzsb.com/api/external_service/equipment/start");
            request.contentType("application/json");
            request.charset("utf-8");
            request.header("token", token);
            params.put("equipment_id", equipment_id);
            params.put("worktime", worktime);
            request.body(JSON.toJSONString(params));
            HttpResponse response1 = request.send();
            String respJson = response1.bodyText();
            JSONObject goodsJson = JSONObject.parseObject(respJson).getJSONObject("data");
            if (goodsJson == null) {
                throw new IllegalStateException("启动失败" + respJson);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.SECOND, worktime);

            TankActivate tankActivate = new TankActivate();
            tankActivate.setOperationSn(CrmebUtil.getOrderNo(OrderConstants.C_GXC_ORDER_PREFIX));
            tankActivate.setEquipmentId(tankEquipment.getId());
            tankActivate.setActivateUserId(user.getId().longValue());
            tankActivate.setCreatedTime(new Date());
            tankActivate.setStatus("成功");
            tankActivate.setEndTime(calendar.getTime());
            tankActivateService.save(tankActivate);
            return Boolean.TRUE;
        });

    }

    @Override
    public Integer activateDay(Long storeId) {
        return dao.activateDay(storeId);
    }

    @Override
    public Integer activateWeeks(Long storeId) {
        return dao.activateWeeks(storeId);
    }

    @Override
    public Integer activateMonth(Long storeId) {
        return dao.activateMonth(storeId);
    }

    @Override
    public Integer activateTotal(Long storeId) {
        return dao.activateTotal(storeId);
    }

    @Override
    public List<Integer> activateRecent(Long storeId) {
        return dao.activateRecent(storeId);
    }

    @Override
    public PageInfo<ActivateInfoResponse> getactivateList(PageParamRequest pageParamRequest) {

        Page<ActivateInfoResponse> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ActivateInfoResponse> activateInfoResponses = dao.getactivateList(userService.getInfo().getId());
        return CommonPage.copyPageInfo(page, activateInfoResponses);
    }

    @Override
    public PageInfo<ActivateAdminListResponse> getadminActivateList(String username,  String name,  String status,PageParamRequest pageParamRequest) {
        Page<ActivateAdminListResponse> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ActivateAdminListResponse> activateInfoResponses = dao.getadminActivateList(username,name,status);


        return CommonPage.copyPageInfo(page, activateInfoResponses);
    }


    public Boolean ifStart(Long equipment_id, String token) {

        HttpRequest request = HttpRequest.get("https://system.swgzsb.com/api/external_service/equipment/getEquipmentInfo?equipment_id=" + equipment_id);
        request.contentType("application/json");
        request.charset("utf-8");
        request.header("token", token);

        HttpResponse response1 = request.send();
        String respJson = response1.bodyText();
        JSONObject goodsJson = JSONObject.parseObject(respJson).getJSONObject("data");
        log.info("gxcinfo{}", goodsJson);
        if (goodsJson == null) {
            return false;
        }
        if (goodsJson.getString("start_status").equals("1")) {
            return true;
        }
        return false;
    }
}
