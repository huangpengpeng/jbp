package com.jbp.admin.controller.agent;

import com.beust.jcommander.internal.Lists;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.lianlian.result.ApplyPasswordElementResult;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztAcctApply;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.agent.LztAcctApplyService;
import com.jbp.service.service.agent.LztAcctService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.Bidi;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/lzt")
@Api(tags = "来账通账户接口")
public class LztController {

    @Resource
    private LztAcctApplyService lztAcctApplyService;
    @Resource
    private LztAcctService lztAcctService;
    @Resource
    private LianLianPayService lianLianPayService;

    @ApiOperation(value = "来账通开户申请")
    @GetMapping(value = "/apply")
    public CommonResult<LztAcctApply> apply(String userId, String shopId, String shopName, String province,
                                            String city, String area, String address) {
        LztAcctApply apply = lztAcctApplyService.apply(4, userId, shopId, shopName, province, city, area, address);
        return CommonResult.success(apply);
    }

    @ApiOperation(value = "来账通开户申请刷新")
    @GetMapping(value = "/apply/refresh")
    public CommonResult<LztAcctApply> apply(String userId) {
        LztAcctApply apply = lztAcctApplyService.refresh(userId);
        return CommonResult.success(apply);
    }

    @ApiOperation(value = "来账通账户信息")
    @GetMapping(value = "/acct")
    public CommonResult<List<LztAcct>> acct() {
//        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
//       Integer merId = systemAdmin.getMerId();
        List<LztAcct> list = lztAcctService.getByMerId(4);
        return CommonResult.success(list);
    }

    @ApiOperation(value = "来账通账户密码控件")
    @GetMapping(value = "/pwd")
    public CommonResult<ApplyPasswordElementResult> pwd(String lianLianAcct, BigDecimal amt, String scan) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LztAcct lztAcct = lztAcctService.getByLianLianAcct(lianLianAcct);
        if (lztAcct == null || lztAcct.getMerId() != merId) {
            throw new CrmebException("账户不存在");
        }
        if (StringUtils.isEmpty(scan)) {
            throw new CrmebException("场景不能为空");
        }
        if (amt == null || !ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {
            throw new CrmebException("金额错误");
        }
        String payCode = "";
        switch (scan) {
            case "转账":
                scan = "pay_password";
                payCode = com.jbp.service.util.StringUtils.N_TO_10(LianLianPayConfig.TxnSeqnoPrefix.来账通内部代发.getPrefix());
                break;
            case "提现":
                scan = "cashout_password";
                payCode = com.jbp.service.util.StringUtils.N_TO_10(LianLianPayConfig.TxnSeqnoPrefix.来账通提现.getPrefix());
                break;
        }
        ApplyPasswordElementResult result = lianLianPayService.getLztPasswordElementToken(lianLianAcct, payCode,
                lztAcct.getLianLianAcctName(), amt, scan);
        result.setPasswordScene(scan);
        result.setPayCode(payCode);
        return CommonResult.success(result);
    }

    @ApiOperation(value = "来账通账户代发资金用途")
    @GetMapping(value = "/purposeList")
    public CommonResult<List<String>> purposeList() {
        return CommonResult.success(Lists.newArrayList("服务费", "信息费", "修理费", "佣金支付", "贷款", "其他"));

    }


}
