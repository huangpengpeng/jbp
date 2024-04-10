package com.jbp.front.controller.tank;

import com.Jwebmall.tank.entity.TankEquipment;
import com.Jwebmall.tank.manager.TankEquipmentMng;
import com.Jwebmall.tank.manager.TankEquipmentNumberMng;
import com.Jwebmall.tank.manager.TankStoreClerkRelationMng;
import com.Jwebmall.user.manager.CapacityMng;
import com.Jwebmall.user.manager.UserMng;
import com.common.api.ResponseForT;
import com.common.jdbc.JdbcDirect;
import com.common.jdbc.page.Pagination;
import com.common.jdbc.template.TxMng;
import com.common.jdbc.template.UnifiedJDBCMng;
import com.common.web.util.WebUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;


@Slf4j
@Controller
public class TankEquipmentNumberAct {


    @Resource
    private TankStoreClerkRelationMng tankStoreClerkRelationMng;
    @Resource
    private CapacityMng capacityMng;

    @Resource
    private TankEquipmentNumberMng tankEquipmentNumberMng;
    @Resource
    private UnifiedJDBCMng unifiedJDBCMng;
    @Autowired
    private TxMng txMng;
    @Resource
    private UserMng userMng;
    @Resource
    private TankEquipmentMng tankEquipmentMng;


    @ApiOperation(value = "共享仓剩余次数", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/tankEquipmentNumber/getNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseForT getNumber( @ApiIgnore ResponseForT response) {

        Number total = unifiedJDBCMng.getNum(new String[] { "userId"},
                new Object[] {  WebUtils.getIdForLogin()}, "共享仓剩余次数");

        return response.SUCCESS(total);
    }



    @ApiOperation(value = "共享仓扣减次数", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/tankEquipmentNumber/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseForT edit( Long equipmentId,Long userId,@ApiIgnore ResponseForT response) {


      TankEquipment tankEquipment =  tankEquipmentMng.getEquipmentSn(equipmentId);

        tankEquipmentNumberMng.reduce( tankEquipment.getStoreUserId(),userId);


        return response.SUCCESS();
    }



    @ApiOperation(value = "共享仓次数明细", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/tankEquipmentNumber/getInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseForT getInfo(String type ,@RequestParam("page") Integer pageNo, Integer limit, @ApiIgnore ResponseForT response) {

        Pagination page = unifiedJDBCMng.getNoQueryCountPageMap(new String[] { "userId","type" },
                new Object[] {  WebUtils.getIdForLogin(),type}, "共享仓次数明细", pageNo, limit);

        JdbcDirect jdbc = new JdbcDirect(page.getList());
        jdbc.NATIVE(new JdbcDirect.Native() {
            @Override
            public void handle(JdbcDirect direct) {

                if(!StringUtils.isBlank(direct.getString("activateId"))) {
                    direct.add("username", userMng.get(Long.valueOf(direct.getString("activateId"))).getUsername());
                }
            }
        });



        return response.SUCCESS(page);
    }


}
