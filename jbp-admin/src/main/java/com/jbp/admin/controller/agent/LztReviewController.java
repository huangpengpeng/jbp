package com.jbp.admin.controller.agent;

import com.jbp.admin.service.LztReviewService;
import com.jbp.common.dto.LztReviewDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.admin.SystemMenu;
import com.jbp.common.model.agent.LztTransfer;
import com.jbp.common.model.agent.LztWithdrawal;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.service.service.SystemMenuService;
import com.jbp.service.service.agent.LztTransferService;
import com.jbp.service.service.agent.LztWithdrawalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/admin/agent/lzt/review")
@Api(tags = "来账通小程序接口")
public class LztReviewController {

    @Resource
    private SystemMenuService systemMenuService;
    @Resource
    private LztReviewService lztReviewService;
    @Resource
    private LztWithdrawalService lztWithdrawalService;
    @Resource
    private LztTransferService lztTransferService;


    @ApiOperation(value = "列表  查询状态  复核  成功 交易中 取消")
    @GetMapping(value = "/list")
    public CommonResult<List<LztReviewDto>> list(String status) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        List<SystemMenu> transferPerms = systemMenuService.findPermissionByUserIdAndPerms(systemAdmin.getId(), "agent:lzt:transfer:out:affrim");
        List<SystemMenu> withdrawalPerms = systemMenuService.findPermissionByUserIdAndPerms(systemAdmin.getId(), "agent:lzt:withdrawal:affrim");
        List<LztReviewDto> list = lztReviewService.list(systemAdmin.getMerId(), CollectionUtils.isNotEmpty(withdrawalPerms), CollectionUtils.isNotEmpty(transferPerms), status);
        return CommonResult.success(list);
    }

    @ApiOperation(value = "同意")
    @GetMapping(value = "/pass")
    public CommonResult audit(String txnSeqno) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        List<SystemMenu> transferPerms = systemMenuService.findPermissionByUserIdAndPerms(systemAdmin.getId(), "agent:lzt:transfer:out:affrim");
        List<SystemMenu> withdrawalPerms = systemMenuService.findPermissionByUserIdAndPerms(systemAdmin.getId(), "agent:lzt:withdrawal:affrim");
        audit(txnSeqno, withdrawalPerms, "ACCEPT", transferPerms, systemAdmin);
        return CommonResult.success();
    }

    @ApiOperation(value = "一键同意")
    @GetMapping(value = "/onePass")
    public CommonResult onePass() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        List<SystemMenu> transferPerms = systemMenuService.findPermissionByUserIdAndPerms(systemAdmin.getId(), "agent:lzt:transfer:out:affrim");
        List<SystemMenu> withdrawalPerms = systemMenuService.findPermissionByUserIdAndPerms(systemAdmin.getId(), "agent:lzt:withdrawal:affrim");
        List<LztReviewDto> list = lztReviewService.list(systemAdmin.getMerId(), CollectionUtils.isNotEmpty(withdrawalPerms), CollectionUtils.isNotEmpty(transferPerms), "复核");
        if (CollectionUtils.isNotEmpty(list)) {
            list = list.stream().filter(s -> "待确认".equals(s.getStatus())).collect(Collectors.toList());
            for (LztReviewDto lztReviewDto : list) {
                audit(lztReviewDto.getTxnSeqno(), withdrawalPerms, "ACCEPT", transferPerms, systemAdmin);
            }
        }
        return CommonResult.success();
    }

    @ApiOperation(value = "拒绝")
    @GetMapping(value = "/refuse")
    public CommonResult refuse(String txnSeqno) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        List<SystemMenu> transferPerms = systemMenuService.findPermissionByUserIdAndPerms(systemAdmin.getId(), "agent:lzt:transfer:out:affrim");
        List<SystemMenu> withdrawalPerms = systemMenuService.findPermissionByUserIdAndPerms(systemAdmin.getId(), "agent:lzt:withdrawal:affrim");
        audit(txnSeqno, withdrawalPerms, "CANCEL", transferPerms, systemAdmin);
        return CommonResult.success();
    }


    private void audit(String txnSeqno, List<SystemMenu> withdrawalPerms, String ACCEPT, List<SystemMenu> transferPerms, SystemAdmin systemAdmin) {
        LztWithdrawal lztWithdrawal = lztWithdrawalService.getByTxnSeqno(txnSeqno);
        LztTransfer lztTransfer = lztTransferService.getByTxnSeqno(txnSeqno);
        if (lztWithdrawal != null && systemAdmin.getMerId().intValue() != lztWithdrawal.getMerId().intValue()) {
            throw new CrmebException("操作权限不足");
        }
        if (lztTransfer != null && systemAdmin.getMerId().intValue() != lztTransfer.getMerId().intValue()) {
            throw new CrmebException("操作权限不足");
        }

        if (lztWithdrawal != null) {
            if (CollectionUtils.isEmpty(withdrawalPerms)) {
                throw new CrmebException("提现复核权限不足请联系管理员");
            }
            if ("待确认".equals(lztWithdrawal.getTxnStatus())) {
                lztWithdrawalService.check(lztWithdrawal.getId(), ACCEPT, "");
            }
        }
        if (lztTransfer != null) {
            if (CollectionUtils.isEmpty(transferPerms)) {
                throw new CrmebException("代付复核权限不足请联系管理员");
            }
            if ("待确认".equals(lztTransfer.getTxnStatus())) {
                lztTransferService.check(lztTransfer.getId(), ACCEPT, "");
            }
        }
    }

}
