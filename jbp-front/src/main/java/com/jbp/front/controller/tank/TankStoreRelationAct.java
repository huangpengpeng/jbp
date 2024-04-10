package com.jbp.front.controller.tank;

import com.jbp.common.model.tank.TankStore;
import com.jbp.common.model.tank.TankStoreRelation;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TankStoreRelationService;
import com.jbp.service.service.TankStoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController("tank")
@RequestMapping("api/front/tankStoreRelation")
@Api(tags = "共享仓店主控制器")
public class TankStoreRelationAct {


    @Resource
    private TankStoreRelationService tankStoreRelationService;
    @Resource
    private TankStoreService tankStoreService;


    @ApiOperation(value = "删除店主", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult delete(Long id) {

        TankStoreRelation tankStoreRelation = tankStoreRelationService.getById(id);
        List<TankStore> tankStore = tankStoreService.getStoreUserId(tankStoreRelation.getStoreUserId().intValue());
        if (!tankStore.isEmpty()) {
            throw new RuntimeException("店主存在门店，无法删除");
        }
        tankStoreRelationService.removeById(id);
        return CommonResult.success();
    }

}
