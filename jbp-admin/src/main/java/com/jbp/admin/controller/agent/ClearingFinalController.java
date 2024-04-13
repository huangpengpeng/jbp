package com.jbp.admin.controller.agent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ClearingFinal;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ClearingPreUserRequest;
import com.jbp.common.request.agent.ClearingRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.RedisUtil;
import com.jbp.service.product.comm.ProductCommEnum;
import com.jbp.service.service.agent.ClearingFinalService;
import com.jbp.service.service.agent.ClearingUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/admin/agent/clearing/final")
@Api(tags = "结算管理")
public class ClearingFinalController {

    @Resource
    private ClearingFinalService clearingFinalService;
    @Resource
    private ClearingUserService clearingUserService;
    @Resource
    private RedisUtil redisUtil;

    @PreAuthorize("hasAuthority('agent:clearing:final:page')")
    @GetMapping("/page")
    @ApiOperation("结算任务列表")
    public CommonResult<CommonPage<ClearingFinal>> getList(Integer commType, String status, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(clearingFinalService.pageList(commType, status, pageParamRequest)));
    }

    @GetMapping("/baseInfo")
    @ApiOperation("结算任务信息")
    public CommonResult baseInfo() {
        // 返回佣金类型
        JSONArray commArray = new JSONArray();
        List<ProductCommEnum> commEnumList = ProductCommEnum.getByNames(ProductCommEnum.拓展佣金.getName(), ProductCommEnum.培育佣金.getName());
        for (ProductCommEnum commEnum : commEnumList) {
            JSONObject json = new JSONObject();
            json.put("name", commEnum.getName());
            json.put("type", commEnum.getType());
            commArray.add(json);
        }
        // 返回状态
        List<String> statusList = Lists.newArrayList(ClearingFinal.Constants.待结算.toString(),
                ClearingFinal.Constants.待出款.toString(), ClearingFinal.Constants.已出款.toString());
        // 名单生成方式
        List<Integer> createUserTypeList = Lists.newArrayList(0, 1);
        JSONObject result = new JSONObject();
        result.put("commList", commArray);
        result.put("statusList", statusList);
        result.put("createUserTypeList", createUserTypeList);
        return CommonResult.success(result);
    }

    @PostMapping("/oneKeyClearing")
    @ApiOperation("一键结算")
    public CommonResult<ClearingFinal> create(@RequestBody ClearingRequest request) {
        Set<Object> set = redisUtil.sGet("clearing_final");
        if(set != null && set.size() > 0){
            new CrmebException("正在进行结算不允许重复操作");
        }
        try{
            clearingFinalService.oneKeyClearing(request);
        }catch (Exception e){
            redisUtil.delete("clearing_final");
            return CommonResult.failed(e.getMessage());
        }
        return CommonResult.success();
    }

    @GetMapping("/oneKeyDel")
    @ApiOperation("一键删除")
    public CommonResult<Boolean> del(Long clearingId) {
        Set<Object> set = redisUtil.sGet("clearing_final");
        if(set != null && set.size() > 0){
            new CrmebException("正在进行结算不允许删除");
        }
        return CommonResult.success(clearingFinalService.oneKeyDel(clearingId));
    }

    @PostMapping("/preImportUser")
    @ApiOperation("预设名单")
    public CommonResult<Boolean> preImportUser(@RequestBody ClearingPreUserRequest request) {
        Set<Object> set = redisUtil.sGet("clearing_final");
        if(set != null && set.size() > 0){
            new CrmebException("正在进行结算不允许设置");
        }
        return CommonResult.success(clearingUserService.preImportUser(request));
    }

    @GetMapping("/preDelUser")
    @ApiOperation("预设名单删除")
    public CommonResult<Boolean> preDelUser() {
        Set<Object> set = redisUtil.sGet("clearing_final");
        if(set != null && set.size() > 0){
            new CrmebException("正在进行结算不允许删除");
        }
        return CommonResult.success(clearingUserService.delPerUser());
    }

    @GetMapping("/send")
    @ApiOperation("一键出款")
    public CommonResult<Boolean> send(Long clearingId) {
        return CommonResult.success(clearingFinalService.oneKeySend(clearingId));
    }

    @GetMapping("/detail")
    @ApiOperation("结算任务详情")
    public CommonResult<ClearingFinal> detail(Long clearingId) {
        ClearingFinal clearingFinal = clearingFinalService.getById(clearingId);
        Set<Object> set = redisUtil.sGet("clearing_final");
        if(set != null && set.size() > 0){
            clearingFinal.setLogs(JSONArray.toJSONString(set));
        }
        return CommonResult.success(clearingFinal);
    }

    @ApiOperation("结算任务下拉选")
    public CommonResult<List<ClearingFinal>> list() {
        return CommonResult.success(clearingFinalService.list());
    }

}
