package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.sgin.UserSignRecord;
import com.jbp.common.request.PageParamRequest;

import java.util.List;

/**
*  UserSignRecordService 接口
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
public interface UserSignRecordService extends IService<UserSignRecord> {

    /**
     * 获取用户签到记录
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserSignRecord> pageRecordList(PageParamRequest pageParamRequest);

    /**
     * 获取用户最后一条签到记录
     * @param uid 用户id
     * @return UserSignRecord
     */
    UserSignRecord getLastByUid(Integer uid);

    /**
     * 获取某个月的签到记录
     * @param uid 用户id
     * @param month 月份 yyyy-MM
     * @return 签到记录
     */
    List<UserSignRecord> findByMonth(Integer uid, String month);

    /**
     * 获取用户签到记录列表
     * @param uid 用户ID
     * @param pageParamRequest 分页参数
     * @return 记录列表
     */
    PageInfo<UserSignRecord> findPageByUid(Integer uid, PageParamRequest pageParamRequest);
}
