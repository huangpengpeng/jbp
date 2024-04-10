package com.jbp.front.controller.tank;

import com.Jwebmall.tank.entity.TankEquipment;
import com.Jwebmall.tank.entity.TankStore;
import com.Jwebmall.tank.entity.TankStoreClerkRelation;
import com.Jwebmall.tank.entity.TankStoreRelation;
import com.Jwebmall.tank.manager.TankEquipmentMng;
import com.Jwebmall.tank.manager.TankStoreClerkRelationMng;
import com.Jwebmall.tank.manager.TankStoreMng;
import com.Jwebmall.tank.manager.TankStoreRelationMng;
import com.Jwebmall.user.entity.User;
import com.Jwebmall.user.manager.CapacityMng;
import com.Jwebmall.user.manager.UserMng;
import com.common.api.ResponseForT;
import com.common.jdbc.template.TxMng;
import com.common.jdbc.template.UnifiedJDBCMng;
import com.common.web.util.WebUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Slf4j
@Controller
public class TankStoreAct {


    @Resource
    private TankStoreMng tankStoreMng;

    @Resource
    private TankStoreRelationMng tankStoreRelationMng;
    @Resource
    private CapacityMng capacityMng;

    @Resource
    private TankStoreClerkRelationMng tankStoreClerkRelationMng;

    @Resource
    private TankEquipmentMng tankEquipmentMng;

    @Resource
    private UnifiedJDBCMng unifiedJDBCMng;
    @Autowired
    private TxMng txMng;
    @Resource
    private UserMng userMng;

    @ApiOperation(value = "舱主绑定店主", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/tankStore/addStoreRelation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseForT addOrder(String name,String phone,@ApiIgnore ResponseForT response) {

      User user =  userMng.getByPhone(phone);

      if(user == null){
          String secrt = UUID.randomUUID().toString().replace("-", "");
          user = userMng.add(name, null, null,
                  capacityMng.getDefault().getId(), phone,null,
               null, 0, null, secrt);
      }

        TankStoreRelation tankStoreRelation = tankStoreRelationMng.getStoreUserId(user.getId());
        if(tankStoreRelation != null){
            throw new IllegalStateException("店主已被绑定，请选择其他店主");
        }

        tankStoreRelationMng.add(WebUtils.getIdForLogin(),user.getId(),new Date());

       return response.SUCCESS();
    }


    @ApiOperation(value = "门店管理列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/tankStore/getStoreList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseForT getStoreList(@ApiIgnore ResponseForT response) {

        List<?> list = unifiedJDBCMng.query(new String[] { "userId"},
                new Object[] { WebUtils.getIdForLogin() }, "门店管理列表");

        return response.SUCCESS(list);
    }


    @ApiOperation(value = "删除门店", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/tankStore/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseForT delete(Long id, @ApiIgnore ResponseForT response) {


        TankStore tankStore =  tankStoreMng.getId(id);

        List<TankEquipment> tankEquipment =  tankEquipmentMng.getStoreId(tankStore.getId());

        if(!tankEquipment.isEmpty()){
            throw new RuntimeException("门店存在设备，无法删除");
        }

        TankStoreRelation tankStoreRelation = tankStoreRelationMng.getStoreUserId(tankStore.getUserId());
        if(tankStoreRelation != null) {
            tankStoreRelationMng.delete(tankStoreRelation.getId());
        }

        List<TankStoreClerkRelation>  list =  tankStoreClerkRelationMng.getStoreUserId(tankStore.getUserId());
        for(TankStoreClerkRelation tankStoreClerkRelation :list){
            tankStoreClerkRelationMng.delete(tankStoreClerkRelation.getId());
        }
        tankStoreMng.delete(id);

        return response.SUCCESS();
    }



    @ApiOperation(value = "用户身份", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/tankStore/getUserStanding", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseForT getUserStanding(@ApiIgnore ResponseForT response) {

        String standing = "";
       TankStoreRelation tankStoreRelation =  tankStoreRelationMng.getStoreUserId( WebUtils.getIdForLogin());
        List<TankStoreRelation> list =  tankStoreRelationMng.getTankUserId( WebUtils.getIdForLogin());
       if(tankStoreRelation != null){
           standing = "店主";

       }
        if(!list.isEmpty()){
            standing =standing+ ",舱主";

        }

        TankStoreClerkRelation tankStoreClerkRelation =  tankStoreClerkRelationMng.getClerkUserId( WebUtils.getIdForLogin());
        if(tankStoreClerkRelation != null){
            standing =standing+ ",店员";
        }

        return response.SUCCESS(standing);
    }

}
