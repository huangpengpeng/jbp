package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductMaterials;
import com.jbp.common.model.agent.Team;
import com.jbp.common.request.PageParamRequest;

import java.math.BigDecimal;
import java.util.List;

public interface ProductMaterialsService extends IService<ProductMaterials> {
    PageInfo<ProductMaterials> pageList(Integer merId, String materialsName,String barCode, String supplyName,  PageParamRequest pageParamRequest);

    Boolean add(Integer merId, String barCode, String materialsName, Integer materialsQuantity, BigDecimal materialsPrice, String materialsCode, String supplyName);

    List<ProductMaterials> getByBarCode(Integer merId, String barCode);

    List<String> getBarCodeList4Supply(String supplyName);
}
