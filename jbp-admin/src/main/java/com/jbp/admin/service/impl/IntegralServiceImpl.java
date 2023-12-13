package com.jbp.admin.service.impl;

import com.github.pagehelper.PageInfo;
import com.jbp.admin.service.IntegralService;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.request.IntegralPageSearchRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.IntegralConfigResponse;
import com.jbp.common.response.IntegralRecordPageResponse;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserIntegralRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

/**
 * 积分服务实现类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class IntegralServiceImpl implements IntegralService {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    /**
     * 获取积分配置
     * @return IntegralConfigResponse
     */
    @Override
    public IntegralConfigResponse getConfig() {
        String integralDeductionSwitch = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_SWITCH);
        String integralDeductionStartMoney = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_START_MONEY);
        String integralDeductionMoney = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_MONEY);
        String integralDeductionRatio = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_RATIO);
        String orderGiveIntegral = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE);
        String freezeIntegralDay = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_STORE_INTEGRAL_EXTRACT_TIME);
        IntegralConfigResponse configResponse = new IntegralConfigResponse();
        configResponse.setIntegralDeductionSwitch(Boolean.valueOf(integralDeductionSwitch));
        configResponse.setIntegralDeductionStartMoney(Integer.valueOf(integralDeductionStartMoney));
        configResponse.setIntegralDeductionMoney(new BigDecimal(integralDeductionMoney));
        configResponse.setIntegralDeductionRatio(Integer.valueOf(integralDeductionRatio));
        configResponse.setOrderGiveIntegral(Integer.valueOf(orderGiveIntegral));
        configResponse.setFreezeIntegralDay(Integer.valueOf(freezeIntegralDay));
        return configResponse;
    }

    /**
     * 编辑积分配置
     * @param request 积分配置请求对象
     * @return Boolean
     */
    @Override
    public Boolean updateConfig(IntegralConfigResponse request) {
        return transactionTemplate.execute(e -> {
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_SWITCH,
                    request.getIntegralDeductionSwitch().toString());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_START_MONEY,
                    request.getIntegralDeductionStartMoney().toString());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_MONEY,
                    request.getIntegralDeductionMoney().toString());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_RATIO,
                    request.getIntegralDeductionRatio().toString());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE,
                    request.getOrderGiveIntegral().toString());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_STORE_INTEGRAL_EXTRACT_TIME,
                    request.getFreezeIntegralDay().toString());
            return Boolean.TRUE;
        });
    }

    /**
     * 积分记录分页列表
     * @param request 搜索参数
     * @param pageRequest 分页参数
     */
    @Override
    public PageInfo<IntegralRecordPageResponse> findRecordPageList(IntegralPageSearchRequest request, PageParamRequest pageRequest) {
        return userIntegralRecordService.findRecordPageListByPlat(request, pageRequest);
    }
}
