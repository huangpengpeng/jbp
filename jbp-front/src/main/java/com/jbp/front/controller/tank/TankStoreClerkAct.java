package com.jbp.front.controller.tank;

import com.Jwebmall.tank.entity.TankStoreClerkRelation;
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
public class TankStoreClerkAct {


    @Resource
    private TankStoreClerkRelationMng tankStoreClerkRelationMng;
    @Resource
    private CapacityMng capacityMng;

    @Resource
    private UnifiedJDBCMng unifiedJDBCMng;
    @Autowired
    private TxMng txMng;
    @Resource
    private UserMng userMng;
    @Resource
    private TankStoreMng tankStoreMng;
    @Resource
    private TankStoreRelationMng tankStoreRelationMng;


    @ApiOperation(value = "店主绑定店员", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/tankStoreClerk/addClerkRelation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseForT addClerkRelation(String name,String phone,Long storeId , @ApiIgnore ResponseForT response) {

      User user =  userMng.getByPhone(phone);

      if(user == null){
          String secrt = UUID.randomUUID().toString().replace("-", "");
          user = userMng.add(name, null, null,
                  capacityMng.getDefault().getId(), phone,null,
               null, 0, null, secrt);
      }

        TankStoreClerkRelation tankStoreClerkRelation = tankStoreClerkRelationMng.getClerkUserId(user.getId());
        if(tankStoreClerkRelation != null){
            throw new IllegalStateException("手机号已被绑定，请输入其他");
        }

       if(!tankStoreMng.getStoreUserId(user.getId()).isEmpty()){
           throw new IllegalStateException("用户为店主，不能成为店员");
       }

        if(!tankStoreRelationMng.getTankUserId(user.getId()).isEmpty()){
            throw new IllegalStateException("用户为舱主，不能成为店员");
        }

        tankStoreClerkRelationMng.add(WebUtils.getIdForLogin(),user.getId(),new Date(),storeId);

       return response.SUCCESS();
    }


    @ApiOperation(value = "店员管理列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/tankStoreClerk/getClerkList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseForT getClerkList(@ApiIgnore ResponseForT response) {

        List<?> list = unifiedJDBCMng.query(new String[] { "userId"},
                new Object[] { WebUtils.getIdForLogin() }, "店员管理列表");

        return response.SUCCESS(list);
    }

    @ApiOperation(value = "门店列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/tankStoreClerk/getStoreList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseForT getStoreList(@ApiIgnore ResponseForT response) {

        List<?> list = unifiedJDBCMng.query(new String[] { "userId"},
                new Object[] { WebUtils.getIdForLogin() }, "共享仓门店列表");

        return response.SUCCESS(list);
    }




    @ApiOperation(value = "删除店员", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/tankStoreClerk/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseForT delete(Long clerkUserId, @ApiIgnore ResponseForT response) {

        TankStoreClerkRelation tankStoreClerkRelation = tankStoreClerkRelationMng.getClerkUserId(clerkUserId);

        tankStoreClerkRelationMng.delete(tankStoreClerkRelation.getId());

        return response.SUCCESS();
    }


}
