package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.product.ProductRepertory;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface ProductRepertoryDao extends BaseMapper<ProductRepertory> {

    List<ProductRepertory> getList(@Param("uid") Integer uid,@Param("nickname") String nickname,@Param("productNameOrCode") String productNameOrCode);
}
