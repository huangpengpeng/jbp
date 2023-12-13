package com.jbp.front.service;

import com.github.pagehelper.PageInfo;
import com.jbp.common.model.seckill.SeckillProduct;
import com.jbp.common.model.user.User;
import com.jbp.common.request.CreateOrderRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.PreOrderDetailRequest;
import com.jbp.common.request.SeckillProductFrontSearchRequest;
import com.jbp.common.response.OrderNoResponse;
import com.jbp.common.response.ProductDetailResponse;
import com.jbp.common.response.SeckillFrontTimeResponse;
import com.jbp.common.response.SeckillProductFrontResponse;
import com.jbp.common.vo.MyRecord;
import com.jbp.common.vo.PreMerchantOrderVo;
import com.jbp.common.vo.PreOrderInfoVo;

import java.util.List;

/**
 * 秒杀服务
 *
 * @Author 指缝de阳光
 * @Date 2022/12/26 14:36
 * @Version 1.0
 */
public interface SeckillService {

    /**
     * 获取首页秒杀信息
     * @return
     */
    List<SeckillProduct> getIndexInfo();

    /**
     * 秒杀时段信息
     */
    List<SeckillFrontTimeResponse> activityTimeInfo();

    /**
     * 秒杀商品列表
     * @param request 搜索参数
     * @param pageRequest 分页参数
     * @return
     */
    PageInfo<SeckillProductFrontResponse> getProductList(SeckillProductFrontSearchRequest request, PageParamRequest pageRequest);

    /**
     * 获取秒杀商品详情
     * @param id 秒杀商品ID
     * @return 秒杀商品详情
     */
    ProductDetailResponse getProductDetail(Integer id);

    /**
     * 秒杀预下单校验
     * @param detailRequest 商品参数
     * @return PreMerchantOrderVo
     */
    PreMerchantOrderVo validatePreOrderSeckill(PreOrderDetailRequest detailRequest);

    /**
     * 秒杀创建订单库存校验
     * @param orderInfoVo 预下单信息
     * @return
     */
    MyRecord validateCreateOrderProductStock(PreOrderInfoVo orderInfoVo);

    /**
     * 秒杀下单扣减库存
     * @param skuRecord sku参数
     */
    void subStock(MyRecord skuRecord);

    /**
     * 创建秒杀订单
     * @param orderRequest 下单请求对象
     * @param orderInfoVo 预下单缓存对象
     * @param user 用户信息
     * @return
     */
    OrderNoResponse createOrder(CreateOrderRequest orderRequest, PreOrderInfoVo orderInfoVo, User user);
}
