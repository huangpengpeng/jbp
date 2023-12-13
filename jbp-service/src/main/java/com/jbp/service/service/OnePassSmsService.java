package com.jbp.service.service;

import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.SmsApplyTempRequest;
import com.jbp.common.request.SmsModifySignRequest;
import com.jbp.common.vo.OnePassSmsTempsListVo;

import java.math.BigDecimal;

/**
 * 一号通短信服务service
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
public interface OnePassSmsService {

    /**
     * 修改签名
     */
    Boolean modifySign(SmsModifySignRequest request);

    /**
     * 短信模板
     *
     * @return OnePassSmsTempsListVo
     */
    OnePassSmsTempsListVo temps(PageParamRequest pageParamRequest);

    /**
     * 申请模板消息
     */
    Boolean applyTempMessage(SmsApplyTempRequest request);

}
