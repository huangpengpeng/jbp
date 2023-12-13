package com.jbp.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.merchant.MerchantPrint;
import com.jbp.common.model.order.MerchantOrder;
import com.jbp.common.request.PageParamRequest;

import java.util.List;

/**
* @author dazongzi
* @description MerchantPrintService 接口
* @date 2023-09-20
*/
public interface MerchantPrintService extends IService<MerchantPrint> {

    /**
     * 小票打印机列表
     * @param pageParamRequest 分页参数
     * @return 小票打印机配置列表
     */
    List<MerchantPrint> getList(PageParamRequest pageParamRequest);

    /**
     * 获取商户打印配置
     * @param merId 商户id
     * @return 开启的打印配置
     */
    List<MerchantPrint> getByMerIdAndStatusOn(Integer merId);

    /**
     * 新增小票打印机配置
     *
     * @param merchantPrint 小票打印机对象
     * @return 新增结果
     */
    Boolean savePrintConfig(MerchantPrint merchantPrint);

    /**
     * 编辑小票打印机配置
     */
    Boolean updatePrintConfig(MerchantPrint merchantPrint);

    /**
     * 更新打印机状态
     * @param id 更改id
     * @param status 具体状态值 0关闭，1开启
     * @return 结果
     */
    Boolean updateStatus(Integer id, Integer status);

    /**
     *  执行具体打印
     * @param merchantOrderList 当前待打印的订单
     * @param op 小票打印开关：0关闭，1=自动打印，2=手动打印，3=自动和手动
     */
    void printReceipt(List<MerchantOrder> merchantOrderList, Integer op);

    /**
     * 删除打印
     * @param id 打印ID
     */
    Boolean deleteById(Integer id);

    /**
     * 获取打印机配置详情
     * @param id 打印机ID
     */
    MerchantPrint getPrintInfo(Integer id);
}
