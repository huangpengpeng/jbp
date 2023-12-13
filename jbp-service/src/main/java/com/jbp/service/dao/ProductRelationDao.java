package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.product.ProductRelation;
import com.jbp.common.response.UserProductRelationResponse;

import java.util.List;

/**
 * <p>
 * 商品点赞和收藏表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
public interface ProductRelationDao extends BaseMapper<ProductRelation> {

    /**
     * 用户收藏列表
     * @param uid 用户uid
     */
    List<UserProductRelationResponse> getUserList(Integer uid);
}
