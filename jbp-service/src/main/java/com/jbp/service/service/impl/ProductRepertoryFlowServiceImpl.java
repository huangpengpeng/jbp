package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.product.ProductRepertoryFlow;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.ProductRepertoryFlowDao;
import com.jbp.service.service.ProductRepertoryFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


@Service
public class ProductRepertoryFlowServiceImpl extends ServiceImpl<ProductRepertoryFlowDao, ProductRepertoryFlow> implements ProductRepertoryFlowService {

    private static final Logger logger = LoggerFactory.getLogger(ProductRepertoryFlowService.class);

    @Resource
    private ProductRepertoryFlowDao dao;


    @Override
    public void  add(Integer uId, Integer productId, Integer count, String description, String orderSn, Date time, String type, Integer surplusCount, String kind){
        ProductRepertoryFlow productRepertoryFlow = new ProductRepertoryFlow();
        productRepertoryFlow.setUid(uId);
        productRepertoryFlow.setProductId(productId);
        productRepertoryFlow.setCount(count);
        productRepertoryFlow.setDescription(description);
        productRepertoryFlow.setOrderSn(orderSn);
        productRepertoryFlow.setTime(time);
        productRepertoryFlow.setType(type);
        productRepertoryFlow.setSurplusCount(surplusCount);
        productRepertoryFlow.setKind(kind);

        save(productRepertoryFlow);
    }

    @Override
    public PageInfo<ProductRepertoryFlow> getList(Integer uid, String nickname, Integer productId, PageParamRequest pageParamRequest) {
        Page<ProductRepertoryFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ProductRepertoryFlow> list = dao.getList(uid, nickname,productId);
        return CommonPage.copyPageInfo(page, list);
    }
}

