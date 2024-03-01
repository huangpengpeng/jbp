package com.jbp.admin.controller.agent;

import com.beust.jcommander.internal.Lists;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.lianlian.result.ApplyPasswordElementResult;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztAcctApply;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantPayInfo;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.LztService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.LztAcctApplyService;
import com.jbp.service.service.agent.LztAcctService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/lzt/acct")
@Api(tags = "来账通-账户")
public class LztAcctController {

    @Resource
    private LztAcctApplyService lztAcctApplyService;
    @Resource
    private LztAcctService lztAcctService;
    @Resource
    private LztService lztService;
    @Resource
    private MerchantService merchantService;

    @ApiOperation(value = "来账通账户分页")
    @GetMapping(value = "/page")
    public CommonResult<PageInfo<LztAcct>> page(String userId, String username, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        PageInfo<LztAcct> page = lztAcctService.pageList(merId, userId, username, pageParamRequest);
        return CommonResult.success(page);
    }

    @ApiOperation(value = "来账通账户详情[余额信息]")
    @GetMapping(value = "/details")
    public CommonResult<LztAcct> details(String userId) {
        return CommonResult.success(lztAcctService.details(userId));
    }




    @ApiOperation(value = "银行虚拟户申请")
    @GetMapping(value = "/bank/apply")
    public CommonResult<LztAcctApply> apply(Integer merId, String userId, String shopId, String shopName, String province,
                                            String city, String area, String address) {
        LztAcctApply apply = lztAcctApplyService.apply(merId, userId, shopId, shopName, province, city, area, address);
        return CommonResult.success(apply);
    }


    @ApiOperation(value = "来账通账户密码控件")
    @GetMapping(value = "/pwd")
    public CommonResult<ApplyPasswordElementResult> pwd(String userId, BigDecimal amt, String scan) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LztAcct lztAcct = lztAcctService.getByUserId(userId);
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
        Merchant merchant = merchantService.getById(lztAcct.getMerId());
        MerchantPayInfo payInfo = merchant.getPayInfo();
        ApplyPasswordElementResult result = lztService.getPasswordToken(payInfo.getOidPartner(), payInfo.getPriKey(),
                userId, payCode, lztAcct.getUsername(), amt, scan);
        result.setPasswordScene(scan);
        result.setPayCode(payCode);
        return CommonResult.success(result);
    }

    @ApiOperation(value = "来账通账户代发资金用途")
    @GetMapping(value = "/purposeList")
    public CommonResult<List<String>> purposeList() {
        return CommonResult.success(Lists.newArrayList("服务费", "信息费", "修理费", "佣金支付", "贷款", "其他"));
    }

    @ApiOperation(value = "来账通用户类型")
    @GetMapping(value = "/userTypeList")
    public CommonResult<List<String>> userTypeList() {
        List<String> list = Lists.newArrayList();
        for (LianLianPayConfig.UserType value : LianLianPayConfig.UserType.values()) {
            list.add(value.name());
        }
        return CommonResult.success(list);
    }


}
