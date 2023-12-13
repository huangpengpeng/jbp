package com.jbp.front.service;

import com.github.pagehelper.PageInfo;
import com.jbp.common.model.coupon.Coupon;
import com.jbp.common.model.seckill.SeckillProduct;
import com.jbp.common.model.system.SystemConfig;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.*;
import com.jbp.common.vo.MyRecord;

import java.util.HashMap;
import java.util.List;

/**
* IndexService 接口
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
public interface IndexService{

    /**
     * 首页信息
     * @return IndexInfoResponse
     */
    IndexInfoResponse getIndexInfo();

    /**
     * 热门搜索
     * @return List
     */
    List<HashMap<String, Object>> hotKeywords();

    /**
     * 获取首页商品列表
     * @param pageParamRequest 分页参数
     * @param cid 一级商品分类id，全部传0
     * @return List
     */
    PageInfo<ProductCommonResponse> findIndexProductList(Integer cid, PageParamRequest pageParamRequest);

    /**
     * 获取颜色配置
     * @return SystemConfig
     */
    SystemConfig getColorConfig();

    /**
     * 获取全局本地图片域名
     * @return String
     */
    String getImageDomain();

    /**
     * 首页商户列表
     * @param recomdProdsNum 推荐商品数量
     */
    List<IndexMerchantResponse> findIndexMerchantListByRecomdNum(Integer recomdProdsNum);

    /**
     * 根据商户id集合查询对应商户信息
     * @param ids id集合
     * @return 商户id集合
     */
    List<IndexMerchantResponse> findIndexMerchantListByIds(String ids);

    /**
     * 获取公司版权图片
     */
    String getCopyrightCompanyImage();

    /**
     * 获取首页秒杀信息
     */
    List<SeckillProduct> getIndexSeckillInfo();

    /**
     * 获取首页优惠券信息
     * @param limit 优惠券数量
     */
    List<Coupon> getIndexCouponInfo(Integer limit);

    /**
     * 获取底部导航信息
     */
    PageLayoutBottomNavigationResponse getBottomNavigationInfo();

    /**
     * 获取版本信息
     * @return AppVersionResponse
     */
    AppVersionResponse getVersion();

    /**
     * 获取公司版权图片
     */
    CopyrightConfigInfoResponse getCopyrightInfo();

    /**
     * 获取移动端域名
     */
    String getFrontDomain();

    /**
     * 获取平台客服
     */
    CustomerServiceResponse getPlatCustomerService();
}
