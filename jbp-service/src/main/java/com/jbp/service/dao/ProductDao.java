package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.product.Product;
import com.jbp.common.response.PlatformProductListResponse;
import com.jbp.common.response.ProductFrontResponse;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
public interface ProductDao extends BaseMapper<Product> {
    /**
     * 平台端商品分页列表
     * @param map 查询参数
     */
    List<PlatformProductListResponse> getPlatformPageList(Map<String, Object> map);

    /**
     * 移动端商品列表
     * @param map 查询参数
     */
    List<ProductFrontResponse> findH5List(Map<String, Object> map);
}
