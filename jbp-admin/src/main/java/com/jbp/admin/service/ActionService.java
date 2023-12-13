package com.jbp.admin.service;

import com.jbp.common.model.record.SensitiveMethodLog;

/**
 * 行为service
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
public interface ActionService {

    /**
     * 添加敏感记录
     *
     * @param methodLog 记录信息
     */
    void addSensitiveLog(SensitiveMethodLog methodLog);
}
