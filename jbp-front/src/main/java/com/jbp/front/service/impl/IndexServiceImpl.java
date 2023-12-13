package com.jbp.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.Constants;
import com.jbp.common.constants.GroupDataConstants;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.constants.VisitRecordConstants;
import com.jbp.common.model.coupon.Coupon;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.seckill.SeckillProduct;
import com.jbp.common.model.system.SystemConfig;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.*;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.front.service.IndexService;
import com.jbp.front.service.SeckillService;
import com.jbp.service.service.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* IndexServiceImpl 接口实现
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
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private SystemGroupDataService systemGroupDataService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private ProductTagService productTagService;

    /**
     * 首页数据
     * 首页banner
     * 新闻头条-文章标题列表
     * 金刚区
     * 秒杀
     * 店铺街
     * 推荐商品
     */
    @Override
    public IndexInfoResponse getIndexInfo() {
        IndexInfoResponse indexInfoResponse = new IndexInfoResponse();
        indexInfoResponse.setBanner(systemGroupDataService.getListMapByGid(GroupDataConstants.GROUP_DATA_ID_INDEX_BANNER)); //首页banner滚动图
        indexInfoResponse.setMenus(systemGroupDataService.getListMapByGid(GroupDataConstants.GROUP_DATA_ID_INDEX_MENU)); //首页金刚区
        indexInfoResponse.setLogoUrl(systemAttachmentService.getCdnUrl());// 移动端顶部logo 1.3版本 DIY 已经替代
        indexInfoResponse.setWechatBrowserVisit(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_WECHAT_BROWSER_VISIT));// 是否开启微信公众号授权登录
        // 客服部分
        indexInfoResponse.setConsumerType(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_CONSUMER_TYPE));
        switch (indexInfoResponse.getConsumerType()) {
            case SysConfigConstants.CONSUMER_TYPE_H5:
                indexInfoResponse.setConsumerH5Url(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_CONSUMER_H5_URL));
            case SysConfigConstants.CONSUMER_TYPE_HOTLINE:
                indexInfoResponse.setConsumerHotline(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_CONSUMER_HOTLINE));
        }
        // 新闻头条
        indexInfoResponse.setHeadline(articleService.getIndexHeadline());

        // 店铺街开关
        indexInfoResponse.setShopStreetSwitch(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_SHOP_STREET_SWITCH));

        // 保存用户访问记录
        Integer userId = userService.getUserId();
        if (userId > 0) {
            asyncService.saveUserVisit(userId, VisitRecordConstants.VISIT_TYPE_INDEX);
        }
        return indexInfoResponse;
    }

    /**
     * 热门搜索
     * @return List<HashMap<String, String>>
     */
    @Override
    public List<HashMap<String, Object>> hotKeywords() {
        return systemGroupDataService.getListMapByGid(GroupDataConstants.GROUP_DATA_ID_INDEX_KEYWORDS);
    }

    /**
     * 获取首页商品列表
     * @param pageParamRequest 分页参数
     * @param cid 一级商品分类id，全部传0
     * @return List
     */
    @Override
    public PageInfo<ProductCommonResponse> findIndexProductList(Integer cid, PageParamRequest pageParamRequest) {
        PageInfo<Product> pageInfo = productService.getIndexProduct(cid, pageParamRequest);
        List<Product> productList = pageInfo.getList();
        if(CollUtil.isEmpty(productList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        List<Integer> merIdList = productList.stream().map(Product::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        List<ProductCommonResponse> productResponseArrayList = new ArrayList<>();
        for (Product product : productList) {
            ProductCommonResponse productResponse = new ProductCommonResponse();
            BeanUtils.copyProperties(product, productResponse);
            productResponse.setIsSelf(merchantMap.get(product.getMerId()).getIsSelf());
            // 根据条件加载商品标签
            ProductTagsFrontResponse productTagsFrontResponse = productTagService.setProductTagByProductTagsRules(product.getId(), product.getBrandId(), product.getMerId(), product.getCategoryId(), productResponse.getProductTags());
            productResponse.setProductTags(productTagsFrontResponse);
            productResponseArrayList.add(productResponse);
        }
        return CommonPage.copyPageInfo(pageInfo, productResponseArrayList);
    }

    /**
     * 获取颜色配置
     * @return SystemConfig
     */
    @Override
    public SystemConfig getColorConfig() {
        return systemConfigService.getColorConfig();
    }

    /**
     * 获取全局本地图片域名
     * @return String
     */
    @Override
    public String getImageDomain() {
        String localUploadUrl = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_LOCAL_UPLOAD_URL);
        return StrUtil.isBlank(localUploadUrl) ? "" : localUploadUrl;
    }

    /**
     * 首页商户列表
     */
    @Override
    public List<IndexMerchantResponse> findIndexMerchantListByRecomdNum(Integer recomdProdsNum) {
        return merchantService.findIndexList(recomdProdsNum);
    }

    /**
     * 根据商户id集合查询对应商户信息
     * @param ids id集合
     * @return 商户id集合
     */
    @Override
    public List<IndexMerchantResponse> findIndexMerchantListByIds(String ids) {
        List<Merchant> listByIdList = merchantService.getListByIdList(CrmebUtil.stringToArray(ids));
        List<IndexMerchantResponse> responseList = new ArrayList<>();
        for (Merchant merchant : listByIdList) {
            IndexMerchantResponse response = new IndexMerchantResponse();
            BeanUtils.copyProperties(merchant, response);
            // 获取商户推荐商品
            List<ProMerchantProductResponse> merchantProductResponseList = productService.getRecommendedProductsByMerId(merchant.getId(), 3);
            response.setProList(merchantProductResponseList);
            responseList.add(response);
        }
        return responseList;
    }

    /**
     * 获取公司版权图片
     */
    @Override
    public String getCopyrightCompanyImage() {
        return systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_IMAGE);
    }

    /**
     * 获取首页秒杀信息
     */
    @Override
    public List<SeckillProduct> getIndexSeckillInfo() {
        return seckillService.getIndexInfo();
    }

    /**
     * 获取首页优惠券信息
     *
     * @param limit 优惠券数量
     */
    @Override
    public List<Coupon> getIndexCouponInfo(Integer limit) {
        return couponService.getCouponListForDiyPageHome(limit);
    }

    /**
     * 获取底部导航信息
     */
    @Override
    public PageLayoutBottomNavigationResponse getBottomNavigationInfo() {
        String isCustom = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_BOTTOM_NAVIGATION_IS_CUSTOM);
        List<HashMap<String, Object>> bnList = systemGroupDataService.getListMapByGid(GroupDataConstants.GROUP_DATA_ID_BOTTOM_NAVIGATION);
        PageLayoutBottomNavigationResponse response = new PageLayoutBottomNavigationResponse();
        response.setIsCustom(isCustom);
        response.setBottomNavigationList(bnList);
        return response;
    }

    /**
     * 获取版本信息
     * @return AppVersionResponse
     */
    @Override
    public AppVersionResponse getVersion() {
        AppVersionResponse response = new AppVersionResponse();
        response.setAppVersion(systemConfigService.getValueByKey(Constants.CONFIG_APP_VERSION));
        response.setAndroidAddress(systemConfigService.getValueByKey(Constants.CONFIG_APP_ANDROID_ADDRESS));
        response.setIosAddress(systemConfigService.getValueByKey(Constants.CONFIG_APP_IOS_ADDRESS));
        response.setOpenUpgrade(systemConfigService.getValueByKey(Constants.CONFIG_APP_OPEN_UPGRADE));
        return response;
    }

    /**
     * 获取公司版权图片
     */
    @Override
    public CopyrightConfigInfoResponse getCopyrightInfo() {
        String copyrightCompanyImage = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_IMAGE);
        String copyrightCompanyName = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_INFO);
        CopyrightConfigInfoResponse response = new CopyrightConfigInfoResponse();
        response.setCompanyName(copyrightCompanyName);
        response.setCompanyImage(copyrightCompanyImage);
        return response;
    }

    /**
     * 获取移动端域名
     */
    @Override
    public String getFrontDomain() {
        return systemConfigService.getFrontDomain();
    }

    /**
     * 获取平台客服
     */
    @Override
    public CustomerServiceResponse getPlatCustomerService() {
        CustomerServiceResponse response = new CustomerServiceResponse();
        response.setConsumerType(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_CONSUMER_TYPE));
        switch (response.getConsumerType()) {
            case SysConfigConstants.CONSUMER_TYPE_H5:
                response.setConsumerH5Url(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_CONSUMER_H5_URL));
            case SysConfigConstants.CONSUMER_TYPE_HOTLINE:
                response.setConsumerHotline(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_CONSUMER_HOTLINE));
        }
        return response;
    }
}

