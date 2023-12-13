package com.jbp.service.util;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.jbp.service.service.SystemConfigService;
import com.jbp.common.constants.Constants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.product.ProductAttr;
import com.jbp.common.request.ProductRequest;
import com.jbp.common.response.ProductResponseForCopyProduct;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.UrlUtil;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 商品工具类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class ProductUtils {
    private String baseUrl;

    String rightUrl;

    @Autowired
    private SystemConfigService systemConfigService;

//    @Autowired
//    private StoreSeckillService storeSeckillService;

//    @Autowired
//    private StoreSeckillMangerService storeSeckillMangerService;
//
//    @Autowired
//    private StoreBargainService storeBargainService;
//
//    @Autowired
//    private StoreCombinationService storeCombinationService;

    /**
     * 解析淘宝产品数据
     * @param url
     * @param tag
     * @throws IOException
     * @throws JSONException
     */
    public ProductRequest getTaobaoProductInfo(String url, int tag) throws JSONException, IOException {
        setConfig(url, tag);
        JSONObject tbJsonData = getRequestFromUrl(baseUrl + rightUrl);
//        JSONObject tbJsonData = new JSONObject(JSONExample.tbJson); // just Test
        JSONObject data = tbJsonData.getJSONObject("data");
        if (null == data) throw new CrmebException("复制商品失败--返回数据格式错误--未找到data");
        JSONObject item = data.getJSONObject("item");
        if (null == item) throw new CrmebException("复制商品失败--返回数据格式错误--未找到item");

        ProductRequest productRequest = new ProductRequest();
        Product product = new Product();
        product.setName(item.getString("title"));
        product.setIntro(item.getString("title"));
        product.setSliderImage(item.getString("images"));
        product.setImage(item.getString("images").split(",")[0]
                .replace("[", "").replace("\"", ""));
        product.setKeyword(item.getString("title"));
        BeanUtils.copyProperties(product, productRequest);
        productRequest.setContent(item.getString("desc"));

        productRequest.setSpecType(true);
        JSONArray props = item.getJSONArray("props");
//        if (null == props) throw new CrmebException("复制商品失败--返回数据格式错误--未找到props");
        if (null == props || props.length() < 1) {
            productRequest.setSpecType(false);
            return productRequest;
        }
        List<ProductAttr> spaAttes = new ArrayList<>();
        for (int i = 0; i < props.length(); i++) {
            JSONObject pItem = props.getJSONObject(i);
            ProductAttr spattr = new ProductAttr();
            spattr.setAttrName(pItem.getString("name"));
            JSONArray values = pItem.getJSONArray("values");
            List<String> attrValues = new ArrayList<>();
            for (int j = 0; j < values.length(); j++) {
                JSONObject value = values.getJSONObject(j);
                attrValues.add(value.getString("name"));
            }
            spattr.setAttrValues(JSON.toJSONString(attrValues));
            spaAttes.add(spattr);
        }
        productRequest.setAttr(spaAttes);
        return productRequest;
    }

    /**
     * 解析京东产品数据
     * @param url
     * @param tag
     * @return
     * @throws JSONException
     */
    public ProductRequest getJDProductInfo(String url, int tag) throws JSONException, IOException {
        setConfig(url,tag);
        JSONObject tbJsonData = getRequestFromUrl(baseUrl + rightUrl);
//        JSONObject tbJsonData = new JSONObject(JSONExample.jdJson); // just Test
        JSONObject data = tbJsonData.getJSONObject("data");
        if (null == data) throw new CrmebException("复制商品失败--返回数据格式错误--未找到data");
        JSONObject item = data.getJSONObject("item");
        if (null == item) throw new CrmebException("复制商品失败--返回数据格式错误--未找到item");

        ProductRequest productRequest = new ProductRequest();
        Product product = new Product();
        product.setName(item.getString("name"));
        product.setIntro(item.getString("name"));
        product.setSliderImage(item.getString("images"));
        product.setImage(item.getString("images").split(",")[0]
                .replace("[", "").replace("\"", ""));
        product.setPrice(BigDecimal.valueOf(item.getDouble("price")));
        BeanUtils.copyProperties(product, productRequest);
        productRequest.setContent(item.getString("desc"));

        JSONObject props = item.getJSONObject("skuProps");
        if (null == props) throw new CrmebException("复制商品失败--返回数据格式错误--未找到props");
        List<ProductAttr> spaAttes = new ArrayList<>();
        JSONObject saleJson = item.getJSONObject("saleProp");
        int attrValueIsNullCount = 0;
        Iterator<String> saleProps = saleJson.keys();
        while (saleProps.hasNext()) {
            ProductAttr spattr = new ProductAttr();
            String stepkey = saleProps.next();
            String stepValue = props.getString(stepkey);
            String stepValueValidLength = stepValue.replace("[","").replace("]","").replace("\"","");
            if(stepValueValidLength.length() > 0){
                com.alibaba.fastjson.JSONArray stepValues = JSON.parseArray(stepValue);
                int c = stepValues.get(0).toString().length();
                attrValueIsNullCount += c == 0 ? 1 : 0;
                spattr.setAttrName(saleJson.getString(stepkey));
                spattr.setAttrValues(props.getString(stepkey));
                spaAttes.add(spattr);
                productRequest.setAttr(spaAttes);
            }else{
                attrValueIsNullCount += 1;
            }
        }
        // 判断是否单属性
        productRequest.setSpecType(spaAttes.size() != attrValueIsNullCount);
        return productRequest;
    }

    /**
     * 解析天猫产品数据
     * @param url
     * @param tag
     * @return
     * @throws JSONException
     */
    public ProductRequest getTmallProductInfo(String url, int tag) throws JSONException, IOException {
        setConfig(url, tag);
        JSONObject tbJsonData = getRequestFromUrl(baseUrl + rightUrl);
//        JSONObject tbJsonData = new JSONObject(JSONExample.tmallJson); // just Test
        JSONObject data = tbJsonData.getJSONObject("data");
        if (null == data) throw new CrmebException("复制商品失败--返回数据格式错误--未找到data");
        JSONObject item = data.getJSONObject("item");
        if (null == item) throw new CrmebException("复制商品失败--返回数据格式错误--未找到item");

        ProductRequest productRequest = new ProductRequest();
        Product product = new Product();
        product.setName(item.getString("title"));
        product.setIntro(item.getString("subTitle"));
        product.setSliderImage(item.getString("images"));
        product.setImage(item.getString("images").split(",")[0]
                .replace("[", "").replace("\"", ""));
        product.setKeyword(item.getString("title"));
        BeanUtils.copyProperties(product, productRequest);
        productRequest.setContent(item.getString("descUrl"));

        productRequest.setSpecType(true);
        JSONArray props = item.getJSONArray("props");
//        if (null == props) throw new CrmebException("复制商品失败--返回数据格式错误--未找到props");
        if (null == props || props.length() < 1) {
            // 无规格商品
            productRequest.setSpecType(false);
            return productRequest;
        }
        List<ProductAttr> spaAttes = new ArrayList<>();
        for (int i = 0; i < props.length(); i++) {
            JSONObject pItem = props.getJSONObject(i);
            ProductAttr spattr = new ProductAttr();
            spattr.setAttrName(pItem.getString("name"));
            JSONArray values = pItem.getJSONArray("values");
            List<String> attrValues = new ArrayList<>();
            for (int j = 0; j < values.length(); j++) {
                JSONObject value = values.getJSONObject(j);
                attrValues.add(value.getString("name"));
            }
            spattr.setAttrValues(JSON.toJSONString(attrValues));
            spaAttes.add(spattr);
        }
        productRequest.setAttr(spaAttes);
        return productRequest;
    }

    /**
     * 解析拼多多产品数据
     * @param url
     * @param tag
     * @return
     * @throws JSONException
     */
    public ProductRequest getPddProductInfo(String url, int tag) throws JSONException, IOException {
        setConfig(url, tag);
        JSONObject tbJsonData = getRequestFromUrl(baseUrl + rightUrl);
//        JSONObject tbJsonData = new JSONObject(JSONExample.pddJson); // just Test
        JSONObject data = tbJsonData.getJSONObject("data");
        if (null == data) throw new CrmebException("复制商品失败--返回数据格式错误--未找到data");
        JSONObject item = data.getJSONObject("item");
        if (null == item) throw new CrmebException("复制商品失败--返回数据格式错误--未找到item");

        ProductRequest productRequest = new ProductRequest();
        Product product = new Product();
        product.setName(item.getString("goodsName"));
        product.setIntro(item.getString("goodsDesc"));
        product.setSliderImage(item.getString("thumbUrl"));
        product.setImage(item.getString("banner"));
        product.setVideoLink(item.getJSONArray("video").getJSONObject(0).getString("videoUrl"));
        product.setPrice(BigDecimal.valueOf(item.getDouble("maxNormalPrice")));
        product.setOtPrice(BigDecimal.valueOf(item.getDouble("marketPrice")));
        BeanUtils.copyProperties(product, productRequest);

        JSONArray props = item.getJSONArray("skus");
        if (null == props) throw new CrmebException("复制商品失败--返回数据格式错误--未找到props");
        if (props.length() > 0) {
            List<ProductAttr> spaAttes = new ArrayList<>();
            HashMap<String,List<String>> tempAttr = new HashMap<>();
            for (int i = 0; i < props.length(); i++) {
                JSONObject pItem = props.getJSONObject(i);
                JSONArray specArray = pItem.getJSONArray("specs");
                for (int j = 0; j < specArray.length(); j++) {
                    JSONObject specItem = specArray.getJSONObject(j);
                    String keyTemp = specItem.getString("spec_key");
                    String valueTemp = specItem.getString("spec_value");
                    if(tempAttr.containsKey(keyTemp)){
                        if(!tempAttr.get(keyTemp).contains(valueTemp)){
                            tempAttr.get(keyTemp).add(valueTemp);
                        }
                    }else{
                        List<String> tempList = new ArrayList<>();
                        tempList.add(valueTemp);
                        tempAttr.put(keyTemp, tempList);
                    }
                }

            }
            Iterator iterator = tempAttr.keySet().iterator();
            while (iterator.hasNext()){
                String key = (String)iterator.next();
                ProductAttr spattr = new ProductAttr();
                spattr.setAttrName(key);
                spattr.setAttrValues(tempAttr.get(key).toString());
                spaAttes.add(spattr);
            }
            productRequest.setAttr(spaAttes);
        }
        return productRequest;
    }

    /**
     * *** 苏宁返回的数据不一致，暂放
     * 解析苏宁产品数据
     * @param url
     * @param tag
     * @return
     * @throws JSONException
     */
    public ProductRequest getSuningProductInfo(String url, int tag) throws JSONException, IOException {
        setConfig(url,tag);
        JSONObject tbJsonData = getRequestFromUrl(baseUrl + rightUrl);
        System.out.println("tbJsonData:"+tbJsonData);
//        JSONObject tbJsonData = new JSONObject(JSONExample.snJson); // just Test
        JSONObject data = tbJsonData.getJSONObject("data");
        if (null == data) throw new CrmebException("复制商品失败--返回数据格式错误--未找到data");

        ProductRequest productRequest = new ProductRequest();
        Product product = new Product();
        product.setName(data.getString("title"));
        product.setIntro(data.getString("title"));
        product.setSliderImage(data.getString("images"));
        product.setImage(data.getString("images").split(",")[0]
                .replace("[", "").replace("\"", ""));
        Long priceS = data.getLong("price");
        product.setPrice(BigDecimal.valueOf(priceS));
        BeanUtils.copyProperties(product, productRequest);
        productRequest.setContent(data.getString("desc"));

        List<ProductAttr> spaAttes = new ArrayList<>();
        ProductAttr spattr = new ProductAttr();
        spattr.setAttrName("默认");
        List<String> attrValues = new ArrayList<>();
        attrValues.add("默认");
        spattr.setAttrValues(attrValues.toString());
        productRequest.setSpecType(false);
        productRequest.setAttr(spaAttes);
        return productRequest;
    }

    /**
     * 设置配置数据
     * @param tag
     */
    public void setConfig(String url, int tag){
        String rightEndUrl = "&itemid=";
        switch (tag){ // 导入平台1=淘宝，2=京东，3=苏宁，4=拼多多， 5=天猫
            case 1:
                baseUrl = systemConfigService.getValueByKey("importProductTB");
                rightEndUrl += UrlUtil.getParamsByKey(url, "id");
                break;
            case 2:
                baseUrl = systemConfigService.getValueByKey("importProductJD");
                rightEndUrl += url.substring(url.lastIndexOf("/")+1).replace(".html","");
                break;
            case 3:
                baseUrl = systemConfigService.getValueByKey("importProductSN");
                int start = url.indexOf(".com/") + 5;
                int end = url.indexOf(".html");
                String sp = url.substring(start,end);
                String[] shopProduct = sp.split("/");
                rightEndUrl += shopProduct[1]+"&shopid="+shopProduct[0];
                break;
            case 4:
                rightEndUrl += UrlUtil.getParamsByKey(url, "goods_id");
                baseUrl = systemConfigService.getValueByKey("importProductPDD");
                break;
            case 5:
                rightEndUrl += UrlUtil.getParamsByKey(url, "id");
                baseUrl = systemConfigService.getValueByKey("importProductTM");
                break;
        }
//        String token = systemConfigService.getValueByKey("importProductToken");
        String token = systemConfigService.getValueByKey("copy_product_apikey");
        if(StringUtils.isBlank(token)){
            throw new CrmebException("请配置复制产品平台的Token -- www.99api.com");
        }
        if(StringUtils.isBlank(baseUrl)){
            throw new CrmebException("请配置复制产品平台的Url-- www.99api.com");
        }
//        rightUrl = "?apikey="+systemConfigService.getValueByKey("importProductToken")+rightEndUrl;
        rightUrl = "?apikey="+systemConfigService.getValueByKey("copy_product_apikey")+rightEndUrl;

    }

    /**
     * 99api产品复制工具方法
     * @param rd
     * @return
     * @throws IOException
     */
    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /**
     * 根据url访问99api后返回对应的平台的产品json数据
     * @param url
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject getRequestFromUrl(String url) throws IOException, JSONException {
        URL realUrl = new URL(url);
        URLConnection conn = realUrl.openConnection();
        InputStream instream = conn.getInputStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(instream, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            instream.close();
        }
    }

//    /**
//     * 根据商品参加的活动次序查找对应活动明细
//     * @param productId 商品id
//     * @param activity 活动次序
//     * @return 活动结果
//     */
//    public HashMap<Integer,ProductActivityItemResponse> getActivityByProduct(Integer productId, String activity){
//        HashMap<Integer,ProductActivityItemResponse> result = null;
//        // 根据参与活动配置次序查找对应活动信息
//        if(StringUtils.isBlank(activity)){
//            return result;
//        }
//        result = new HashMap<>();
//
//        List<Integer> activitys = CrmebUtil.stringToArrayInt(activity);
//        for (Integer code : activitys) {
//            if(code == 1){ // 查找秒杀信息
//                List<StoreSeckill> currentSecKills = storeSeckillService.getCurrentSecKillByProductId(productId);
//                if(null != currentSecKills && currentSecKills.size() > 0){
//                    // 查询当前秒杀活动时间段配置信息
//                    StoreSeckillManger secKillManager = storeSeckillMangerService.getById(currentSecKills.get(0).getTimeId());
//                    // 将当前时间段转化成时间戳
//                    int secKillEndSecondTimestamp =
//                            DateUtil.getSecondTimestamp(DateUtil.nowDateTime("yyyy-MM-dd " + secKillManager.getEndTime() + ":00:00"));
//                    ProductActivityItemResponse secKillResponse = new ProductActivityItemResponse();
//                    secKillResponse.setId(currentSecKills.get(0).getId());
//                    secKillResponse.setTime(secKillEndSecondTimestamp);
//                    secKillResponse.setType(Constants.PRODUCT_TYPE_SECKILL+"");
//                    result.put(code,secKillResponse);
//                }
//            }
//            if(code == 2){ // 查找砍价信息
//                List<StoreBargain> currentBargains = storeBargainService.getCurrentBargainByProductId(productId);
//                if (CollUtil.isNotEmpty(currentBargains)) {
//                    ProductActivityItemResponse bargainResponse = new ProductActivityItemResponse();
//                    bargainResponse.setId(currentBargains.get(0).getId());
//                    bargainResponse.setTime(DateUtil.getSecondTimestamp(currentBargains.get(0).getStopTime()));
//                    bargainResponse.setType(Constants.PRODUCT_TYPE_BARGAIN +"");
//                    result.put(code, bargainResponse);
//                }
//            }
//            if(code == 3){ // 查找拼团信息
//                List<StoreCombination> currentCombinations = storeCombinationService.getCurrentBargainByProductId(productId);
//                if (CollUtil.isNotEmpty(currentCombinations)) {
//                    ProductActivityItemResponse bargainResponse = new ProductActivityItemResponse();
//                    bargainResponse.setId(currentCombinations.get(0).getId());
//                    bargainResponse.setTime(DateUtil.getSecondTimestamp(currentCombinations.get(0).getStopTime()));
//                    bargainResponse.setType(Constants.PRODUCT_TYPE_PINGTUAN +"");
//                    result.put(code, bargainResponse);
//                }
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 获取商品参与的全部活动
//     * @param storeProduct  当前商品
//     * @return  商品所参与的活动
//     */
//    public List<ProductActivityItemResponse> getProductAllActivity(StoreProduct storeProduct) {
//        HashMap<Integer, ProductActivityItemResponse> currentActivityList
//                = getActivityByProduct(storeProduct.getId(), storeProduct.getActivity());
//        if(StringUtils.isBlank(storeProduct.getActivity())) return new ArrayList<>();
//        List<ProductActivityItemResponse> activityH5 = new ArrayList<>();
//        List<Integer> activityList = CrmebUtil.stringToArrayInt(storeProduct.getActivity());
//        for (Integer code : activityList) {
//            if(null != currentActivityList.get(code)){
//                activityH5.add(currentActivityList.get(code));
//            }
//        }
//        return activityH5;
//    }
//
//    /**
//     * 获取当前商品参与的第一个活动
//     * @param storeProduct  当前商品信息
//     * @return 当前参与的商品活动
//     */
//    public ProductActivityItemResponse getProductCurrentActivity(StoreProduct storeProduct) {
//        HashMap<Integer, ProductActivityItemResponse> currentActivityList
//                = getActivityByProduct(storeProduct.getId(), storeProduct.getActivity());
//        if(StringUtils.isBlank(storeProduct.getActivity())) return null;
//        List<Integer> activityList = CrmebUtil.stringToArrayInt(storeProduct.getActivity());
//
//        return currentActivityList.get(activityList.get(0));
//    }

    /**
     * 一号通复制商品转公共商品参数
     * @param jsonObject 一号通复制商品
     *
     */
    public static ProductResponseForCopyProduct onePassCopyTransition(com.alibaba.fastjson.JSONObject jsonObject) {
        if (null == jsonObject) return null;

        ProductResponseForCopyProduct productRequestForCC = new ProductResponseForCopyProduct();
        productRequestForCC.setName(jsonObject.getString("store_name"));
        productRequestForCC.setIntro(jsonObject.getString("store_info"));
        productRequestForCC.setSliderImage(jsonObject.getString("slider_image"));
        productRequestForCC.setImage(jsonObject.getString("image").replace("[", "").replace("\"", ""));
        productRequestForCC.setKeyword(jsonObject.getString("store_name"));
        productRequestForCC.setCost(jsonObject.getBigDecimal("cost"));
        productRequestForCC.setPrice(jsonObject.getBigDecimal("price"));
        productRequestForCC.setOtPrice(jsonObject.getBigDecimal("ot_price"));
        productRequestForCC.setUnitName(jsonObject.getString("unit_name"));
        productRequestForCC.setContent(jsonObject.getString("description"));
        productRequestForCC.setSpecType(true);
        com.alibaba.fastjson.JSONArray props = jsonObject.getJSONArray("items");
        if (null == props || props.size() < 1) {
            // 无规格商品
            productRequestForCC.setSpecType(false);
            return productRequestForCC;
        }

        List<ProductAttr> spaAttes = new ArrayList<>();
        for (int i = 0; i < props.size(); i++) {
            com.alibaba.fastjson.JSONObject pItem = props.getJSONObject(i);
            ProductAttr spattr = new ProductAttr();
            spattr.setAttrName(pItem.getString("value"));
            com.alibaba.fastjson.JSONArray values = pItem.getJSONArray("detail");
            List<String> attrValues = new ArrayList<>();
            for (int j = 0; j < values.size(); j++) {
                String value = values.getString(j);
                attrValues.add(value);
            }
            spattr.setAttrValues(JSON.toJSONString(attrValues));
            spaAttes.add(spattr);
        }
        productRequestForCC.setAttr(spaAttes);
        return productRequestForCC;
    }

}
