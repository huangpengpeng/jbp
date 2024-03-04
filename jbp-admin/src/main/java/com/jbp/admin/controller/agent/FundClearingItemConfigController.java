package com.jbp.admin.controller.agent;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import com.jbp.common.model.agent.FundClearingItemConfig;
import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.FundClearingItemConfigPageRequest;
import com.jbp.common.request.agent.FundClearingItemConfigRequest;
import com.jbp.common.request.agent.FundClearingItemConfigUpdateRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.product.comm.ProductCommEnum;
import com.jbp.service.service.WalletConfigService;
import com.jbp.service.service.agent.FundClearingItemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/fund/clearing/item/config")
@Api(tags = "佣金发放配置")
public class FundClearingItemConfigController {

    @Resource
    private WalletConfigService walletConfigService;
    @Resource
    private FundClearingItemConfigService fundClearingItemConfigService;

    @PreAuthorize("hasAuthority('agent:fund:clearing:item:config:page')")
    @GetMapping("/page")
    @ApiOperation("佣金发放配置列表")
    public CommonResult<CommonPage<FundClearingItemConfig>> getList(FundClearingItemConfigPageRequest request, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(fundClearingItemConfigService.pageList(request.getCommName(), pageParamRequest)));
    }

    @ApiOperation("发放类型")
    @PostMapping("/type/list")
    public CommonResult typeList() {
        List<WalletConfig> list = walletConfigService.list();
        Map<Integer, String> map = Maps.newConcurrentMap();
        for (WalletConfig walletConfig : list) {
            map.put(walletConfig.getType(), walletConfig.getName());
        }
        map.put(-1, "管理费");
        map.put(-2, "手续费");
        return CommonResult.success(map);
    }

    @ApiOperation("佣金名称")
    @PostMapping("/comm/list")
    public CommonResult commList() {
        List<String> list = Lists.newArrayList();
        for (ProductCommEnum value : ProductCommEnum.values()) {
            list.add(value.getName());
        }
        return CommonResult.success(list);
    }

    @PreAuthorize("hasAuthority('agent:fund:clearing:item:config:add')")
    @ApiOperation("头部配置")
    @PostMapping("/add")
    public CommonResult add(@RequestBody @Validated List<FundClearingItemConfigRequest> request) {
        if (CollectionUtils.isEmpty(request)) {
            return CommonResult.failed("发放配置不能为空");
        }
        Set<String> set = request.stream().map(FundClearingItemConfigRequest::getCommName).collect(Collectors.toSet());
        if (set.size() != 1) {
            return CommonResult.failed("一次只能配置一类佣金");
        }
        BigDecimal scale = BigDecimal.ZERO;
        for (FundClearingItemConfigRequest config : request) {
            scale = scale.add(config.getScale());
        }
        if (!ArithmeticUtils.equals(scale, BigDecimal.ONE)) {
            return CommonResult.failed("佣金方法的总比例必须等于1");
        }
        fundClearingItemConfigService.save(request);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:fund:clearing:item:config:update')")
    @ApiOperation("修改")
    @PostMapping("/update")
    public CommonResult update(@RequestBody @Validated List<FundClearingItemConfigUpdateRequest> request) {
        if (CollectionUtils.isEmpty(request)) {
            return CommonResult.failed("发放配置不能为空");
        }
        Set<String> set = request.stream().map(FundClearingItemConfigUpdateRequest::getCommName).collect(Collectors.toSet());
        if (set.size() != 1) {
            return CommonResult.failed("一次只能配置一类佣金");
        }
        BigDecimal scale = BigDecimal.ZERO;
        for (FundClearingItemConfigUpdateRequest config : request) {
            scale = scale.add(config.getScale());
        }
        if (!ArithmeticUtils.equals(scale, BigDecimal.ONE)) {
            return CommonResult.failed("佣金方法的总比例必须等于1");
        }
        fundClearingItemConfigService.update(request);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:fund:clearing:item:config:delect')")
    @ApiOperation("删除")
    @GetMapping("/delect/{id}")
    public CommonResult delect(@PathVariable("id") Integer id) {
        fundClearingItemConfigService.removeById(id);
        return CommonResult.success();
    }


}
