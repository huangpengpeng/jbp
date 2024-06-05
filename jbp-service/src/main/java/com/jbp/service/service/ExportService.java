package com.jbp.service.service;


import com.jbp.common.request.OrderSearchRequest;
import com.jbp.common.request.ProductDayRecordRequest;
import com.jbp.common.vo.OrderExcelInfoVo;
import com.jbp.common.vo.OrderExcelVo;
import com.jbp.common.vo.OrderShipmentExcelInfoVo;

import java.util.List;

/**
 * StoreProductService 接口
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
public interface ExportService {

    /**
     * 订单导出
     *
     * @param request 查询条件
     * @return 文件名称
     */
    String exportOrderShipment(OrderSearchRequest request);

    String exportOrder(OrderSearchRequest request);

    String exportProductStatement(ProductDayRecordRequest request);
}
