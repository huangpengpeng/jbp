package com.jbp.admin.controller.agent;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztSalaryPayer;
import com.jbp.common.model.agent.LztSalaryTransfer;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.LztSalaryTransferRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.LztSalaryTransferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/admin/agent/lzt/salary/transfer")
@Api(tags = "来账通薪资代发接口")
public class LztSalaryTransferController {


    @Resource
    private LztSalaryTransferService lztSalaryTransferService;
    @Resource
    private LztAcctService lztAcctService;

    @PreAuthorize("hasAuthority('agent:lzt:salary:transfer:page')")
    @ApiOperation(value = "分页")
    @GetMapping(value = "/page")
    public CommonResult<PageInfo<LztSalaryTransfer>> page(String payerId, String txnSeqno, String status, String bankAcctNo, String bankAcctName, String time,
                                                          PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        PageInfo<LztSalaryTransfer> page = lztSalaryTransferService.pageList(merId, payerId, txnSeqno,
                bankAcctNo, bankAcctName, status, time, pageParamRequest);
        return CommonResult.success(page);
    }


    @PreAuthorize("hasAuthority('agent:lzt:salary:transfer:edit')")
    @ApiOperation(value = "导入")
    @PostMapping(value = "/import")
    public CommonResult<Boolean> importExcel(@RequestBody List<LztSalaryTransferRequest> requests) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LztSalaryPayer salaryPayer = lztSalaryTransferService.getSalaryPayer(merId);
        if (salaryPayer == null) {
            throw new RuntimeException("出款账户未设置请联系管理员");
        }
        lztSalaryTransferService.create(lztAcctService.getByUserId(salaryPayer.getPayerId()), requests);
        return CommonResult.success(true);
    }


    @PreAuthorize("hasAuthority('agent:lzt:salary:transfer:edit')")
    @ApiOperation(value = "删除")
    @PostMapping(value = "/del")
    public CommonResult<Boolean> del(@RequestBody JSONArray idList) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LztSalaryPayer salaryPayer = lztSalaryTransferService.getSalaryPayer(merId);
        if (salaryPayer == null) {
            throw new RuntimeException("出款账户未设置请联系管理员");
        }
        if(idList == null || idList.isEmpty()){
            throw new RuntimeException("请选择需要删除的发放记录");
        }

        lztSalaryTransferService.del(lztAcctService.getByUserId(salaryPayer.getPayerId()), idList.toJavaList(Long.class));
        return CommonResult.success(true);
    }


    @PreAuthorize("hasAuthority('agent:lzt:salary:transfer:send')")
    @ApiOperation(value = "发放")
    @GetMapping(value = "/send")
    public CommonResult<LztSalaryTransfer> send(HttpServletRequest request, String pwd, String randomKey, Long id, String payCode) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LztSalaryPayer salaryPayer = lztSalaryTransferService.getSalaryPayer(merId);
        if (salaryPayer == null) {
            throw new RuntimeException("出款账户未设置请联系管理员");
        }
        String ip = CrmebUtil.getClientIp(request);
        LztAcct acct = lztAcctService.getByUserId(salaryPayer.getPayerId());
        LztSalaryTransfer lztSalaryTransfer = lztSalaryTransferService.send(acct, id, pwd, randomKey, payCode, ip);
        if (StringUtils.isNotEmpty(acct.getPhone())) {
            String phone = acct.getPhone();
            lztSalaryTransfer.setRegMsg("短信已发送至: " + phone.substring(0, 3) + "****" + phone.substring(7, phone.length()) + " 请注意查收");
        } else {
            lztSalaryTransfer.setRegMsg("短信已发送请注意查收");
        }
        return CommonResult.success(lztSalaryTransfer);
    }

    @PreAuthorize("hasAuthority('agent:lzt:salary:transfer:send')")
    @ApiOperation(value = "自动发放")
    @GetMapping(value = "/autoSend")
    public CommonResult<LztSalaryTransfer> autoSend(HttpServletRequest request, Long id, String pwd, String randomKey) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LztSalaryPayer salaryPayer = lztSalaryTransferService.getSalaryPayer(merId);
        if (salaryPayer == null) {
            throw new RuntimeException("出款账户未设置请联系管理员");
        }
        String ip = CrmebUtil.getClientIp(request);
        LztAcct acct = lztAcctService.getByUserId(salaryPayer.getPayerId());
        LztSalaryTransfer lztSalaryTransfer = lztSalaryTransferService.autoSend(acct, id, ip, pwd, randomKey);
        return CommonResult.success(lztSalaryTransfer);
    }

}
