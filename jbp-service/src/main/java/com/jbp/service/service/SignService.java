package com.jbp.service.service;

import com.github.pagehelper.PageInfo;
import com.jbp.common.model.sgin.UserSignRecord;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.SignConfigRequest;
import com.jbp.common.response.SignConfigResponse;
import com.jbp.common.response.SignPageInfoResponse;
import com.jbp.common.response.UserSignRecordResponse;

/**
 * 签到服务
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public interface SignService {

    /**
     * 获取签到配置
     * @return SignConfigResponse
     */
    SignConfigResponse getConfig();

    /**
     * 新增连续签到配置
     * @param request 配置参数
     * @return Boolean
     */
    Boolean addConfig(SignConfigRequest request);

    /**
     * 删除连续签到配置
     * @param id 签到配置id
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 编辑基础签到配置
     * @param request 配置参数
     * @return Boolean
     */
    Boolean editBaseConfig(SignConfigRequest request);

    /**
     * 编辑连续签到配置
     * @param request 配置参数
     * @return Boolean
     */
    Boolean editAwardConfig(SignConfigRequest request);

    /**
     * 获取用户签到记录
     * @param pageParamRequest 分页参数
     */
    PageInfo<UserSignRecordResponse> getSignRecordList(PageParamRequest pageParamRequest);

    /**
     * 获取签到页信息
     * @param month 月份 yyyy-MM
     * @return 签到页信息
     */
    SignPageInfoResponse getPageInfo(String month);

    /**
     * 获取移动端签到记录列表
     * @param pageParamRequest 分页参数
     * @return 记录列表
     */
    PageInfo<UserSignRecord> findFrontSignRecordList(PageParamRequest pageParamRequest);
}
