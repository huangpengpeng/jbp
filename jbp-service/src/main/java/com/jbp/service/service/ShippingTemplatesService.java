package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.express.ShippingTemplates;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.ShippingTemplatesRequest;
import com.jbp.common.request.ShippingTemplatesSearchRequest;
import com.jbp.common.response.ShippingTemplatesInfoResponse;

import java.util.List;

/**
 * ShippingTemplatesService 接口
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
public interface ShippingTemplatesService extends IService<ShippingTemplates> {

    List<ShippingTemplates> getList(ShippingTemplatesSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增运费模板
     *
     * @param request 请求参数
     * @return 新增结果
     */
    Boolean create(ShippingTemplatesRequest request);

    Boolean edit(ShippingTemplatesRequest request);

    /**
     * 删除模板
     *
     * @param id 模板id
     * @return Boolean
     */
    Boolean remove(Integer id);

    /**
     * 获取模板信息
     *
     * @param id 模板id
     * @return ShippingTemplates
     */
    ShippingTemplatesInfoResponse getInfo(Integer id);
}
