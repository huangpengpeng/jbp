package com.jbp.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.jbp.front.service.IndexService;
import com.jbp.common.constants.GroupDataConstants;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.constants.VisitRecordConstants;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.system.SystemConfig;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.IndexInfoResponse;
import com.jbp.common.response.IndexMerchantResponse;
import com.jbp.common.response.ProductCommonResponse;
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
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
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
        indexInfoResponse.setLogoUrl(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MOBILE_TOP_LOGO));// 移动端顶部logo
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
    public List<IndexMerchantResponse> findIndexMerchantList() {
        return merchantService.findIndexList();
    }

    /**
     * 获取公司版权图片
     */
    @Override
    public String getCopyrightCompanyImage() {
        return systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_IMAGE);
    }

}

