package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.WalletWithdraw;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.WalletWithdrawPageRequest;
import com.jbp.common.request.agent.WalletWithdrawRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.vo.WalletWithdrawExcelInfoVo;
import com.jbp.common.vo.WalletWithdrawVo;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.WalletWithdrawService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/wallet/withdraw")
@Api(tags = "钱包提现")
public class WalletWithdrawController {
    @Resource
    private WalletWithdrawService walletWithdrawService;
    @Resource
    private UserService userService;

    @PreAuthorize("hasAuthority('agent:wallet:withdraw:page')")
    @GetMapping("/page")
    @ApiOperation("钱包提现列表")
    public CommonResult<CommonPage<WalletWithdraw>> getList(WalletWithdrawPageRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getRealName())) {
            User user = userService.getByRealName(request.getRealName());
            if (user == null) {
                throw new CrmebException("姓名错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(walletWithdrawService.pageList(request.getAccount(), request.getWalletName(), request.getStatus(),request.getDateLimit(),uid, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('agent:wallet:withdraw:send')")
    @PostMapping("/send")
    @ApiOperation("钱包提现批量出款")
    public CommonResult send(@RequestBody List<WalletWithdrawRequest> requests) {
        walletWithdrawService.send(requests);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:wallet:withdraw:cancel')")
    @PostMapping("/cancel")
    @ApiOperation("钱包提现批量取消")
    public CommonResult cancel(@RequestBody List<WalletWithdrawRequest> requests) {
        walletWithdrawService.cancel(requests);
        return CommonResult.success();
    }
    @PreAuthorize("hasAuthority('agent:wallet:withdraw:excel')")
    @PostMapping("/excel")
    @ApiOperation("钱包导出数据")
    public CommonResult<WalletWithdrawExcelInfoVo> excel(WalletWithdrawPageRequest request) {
        if (ObjectUtil.isEmpty(request)) {
            throw new CrmebException("请选择一个过滤条件");
        }
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getRealName())) {
            User user = userService.getByRealName(request.getRealName());
            if (user == null) {
                throw new CrmebException("姓名错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(walletWithdrawService.excel(request.getAccount(), request.getWalletName(), request.getStatus(),request.getDateLimit(),uid));
    }


    @GetMapping("/status/enum")
    @ApiOperation("钱包提现状态")
    public CommonResult<Object> getStatusEnum() {
        return CommonResult.success(WalletWithdraw.StatusEnum.values());
    }
}
