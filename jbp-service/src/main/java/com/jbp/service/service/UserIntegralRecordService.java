package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.UserIntegralRecord;
import com.jbp.common.request.IntegralPageSearchRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.IntegralRecordPageResponse;
import com.jbp.common.vo.IntegralRecordVo;

import java.math.BigDecimal;

/**
 * 用户积分记录Service
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface UserIntegralRecordService extends IService<UserIntegralRecord> {

    /**
     * 新增用户积分明细
     * @param uid  用户ID
     * @param integralType  积分类型
     * @param externalNo  三方单号
     * @param type  1 增加  2 减少
     * @param title 标题
     * @param integral  积分
     * @param balance  余额
     * @param mark  备注【后台】
     * @param postscript  附言
     * @return
     */
    UserIntegralRecord add(Integer uid, String integralType, String externalNo, Integer type, String title, BigDecimal integral,
                           BigDecimal balance, String mark, String postscript);

    /**
     * 积分明细分页查询
     */
    PageInfo<IntegralRecordVo> page(IntegralPageSearchRequest request, PageParamRequest pageRequest);



}
