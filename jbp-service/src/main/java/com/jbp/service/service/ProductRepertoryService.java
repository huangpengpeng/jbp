package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.product.ProductRepertory;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.ProductRepertoryRequest;
import com.jbp.common.vo.MyRecord;
import com.jbp.common.vo.ProductRepertoryVo;

import java.util.List;


public interface ProductRepertoryService extends IService<ProductRepertory> {


    public Boolean increase(Integer productId,Integer count,Integer uId,String description ,String orderSn,String type);

    public ProductRepertory add(Integer productId, Integer count, Integer uId);

    public Boolean reduce(Integer productId,Integer count,Integer uId,String description ,String orderSn,String type);

    PageInfo<ProductRepertory> getList(Integer uid, String nickname, String productNameOrCode, PageParamRequest pageParamRequest);

    Boolean allot(Integer fUid, Integer tUid, Integer productId, Integer count);

    String export(Integer uid, String nickname, String productNameOrCode);

    List<ProductRepertory> getUserRepertory(Integer uid);


    List<ProductRepertoryVo>  getProductList(Integer uid);


    void allot(List<ProductRepertoryRequest> request);

    Boolean company(Integer uid, Integer productId, Integer count, String description);

    Boolean edit(Integer id, Integer count, String kind, String description);
}
