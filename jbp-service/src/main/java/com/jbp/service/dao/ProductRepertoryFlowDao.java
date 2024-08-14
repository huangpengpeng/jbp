package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.product.ProductRepertoryFlow;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface ProductRepertoryFlowDao extends BaseMapper<ProductRepertoryFlow> {

    List<ProductRepertoryFlow> getList(@Param("uid") Integer uid, @Param("nickname")String nickname);
}
