package com.jbp.admin.controller.agent;

import com.jbp.common.lianlian.result.ReceiptDownloadResult;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.LztReceipt;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.service.service.agent.LztReceiptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/lzt/receipt")
@Api(tags = "来账通-回执单下单")
public class LztReceiptAct {

    @Resource
    private LztReceiptService lztReceiptService;

    @PreAuthorize("hasAuthority('agent:lzt:receipt:list')")
    @GetMapping("/list")
    @ApiOperation("回执单列表")
    public CommonResult<List<LztReceipt>> list(String tradeTxnSeqno, String memo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return CommonResult.success(lztReceiptService.getList(systemAdmin.getMerId(), tradeTxnSeqno, memo));
    }


    @PreAuthorize("hasAuthority('agent:lzt:receipt:add')")
    @GetMapping("/add")
    @ApiOperation("申请")
    public CommonResult<LztReceipt> add(String tradeTxnSeqno, String tradeBillType, String totalAmount, String memo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        LztReceipt lztReceipt = lztReceiptService.add(systemAdmin.getMerId(), tradeTxnSeqno, memo, tradeBillType, totalAmount);
        return CommonResult.success(lztReceipt);
    }

    @PreAuthorize("hasAuthority('agent:lzt:receipt:download')")
    @GetMapping("/down")
    @ApiOperation("下载")
    public CommonResult<ReceiptDownloadResult> down(Long id) {
        ReceiptDownloadResult download = lztReceiptService.download(id);
        return CommonResult.success(download);
    }
}
