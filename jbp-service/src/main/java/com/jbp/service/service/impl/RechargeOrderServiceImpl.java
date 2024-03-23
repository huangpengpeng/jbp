package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.*;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.lianlian.result.CashierPayCreateResult;
import com.jbp.common.lianlian.result.LianLianPayInfoResult;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.model.bill.Bill;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.RechargeOrder;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserBalanceRecord;
import com.jbp.common.model.user.UserToken;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.RechargeOrderSearchRequest;
import com.jbp.common.request.UserRechargeRequest;
import com.jbp.common.response.OrderPayResultResponse;
import com.jbp.common.response.RechargePackageResponse;
import com.jbp.common.response.UserRechargeItemResponse;
import com.jbp.common.utils.*;
import com.jbp.common.vo.*;
import com.jbp.service.dao.RechargeOrderDao;
import com.jbp.service.service.*;

import com.jbp.service.service.agent.PlatformWalletService;
import com.jbp.service.service.agent.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RechargeOrderServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class RechargeOrderServiceImpl extends ServiceImpl<RechargeOrderDao, RechargeOrder> implements RechargeOrderService {

    @Resource
    private RechargeOrderDao dao;

    private static final Logger logger = LoggerFactory.getLogger(RechargeOrderServiceImpl.class);

    @Autowired
    private UserService userService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private SystemGroupDataService systemGroupDataService;
    @Autowired
    private WechatService wechatService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserBalanceRecordService userBalanceRecordService;
    @Autowired
    private BillService billService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private AliPayService aliPayService;
    @Autowired
    private LianLianPayService lianLianPayService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private PlatformWalletService platformWalletService;

    /**
     * 列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     * @return List<RechargeOrder>
     */
    @Override
    public PageInfo<RechargeOrder> getAdminPage(RechargeOrderSearchRequest request, PageParamRequest pageParamRequest) {
        Page<RechargeOrder> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        //带 UserExtract 类的多条件查询
        LambdaQueryWrapper<RechargeOrder> lqw = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(request.getUid()) && request.getUid() > 0) {
            lqw.eq(RechargeOrder::getUid, request.getUid());
        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lqw.like(RechargeOrder::getOrderNo, keywords); //订单号
        }
        //是否充值
        lqw.eq(RechargeOrder::getPaid, true);
        //时间范围
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            if (StrUtil.isNotBlank(dateLimit.getStartTime()) && StrUtil.isNotBlank(dateLimit.getEndTime())) {
                //判断时间
                int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT);
                if (compareDateResult == -1) {
                    throw new CrmebException("开始时间不能大于结束时间！");
                }
                lqw.between(RechargeOrder::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
            }
        }
        lqw.orderByDesc(RechargeOrder::getId);
        List<RechargeOrder> rechargeOrderList = dao.selectList(lqw);
        if (CollUtil.isEmpty(rechargeOrderList)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        List<Integer> userIds = rechargeOrderList.stream().map(RechargeOrder::getUid).collect(Collectors.toList());
        Map<Integer, User> userHashMap = userService.getUidMapList(userIds);
        rechargeOrderList.forEach(e -> {
            User user = userHashMap.get(e.getUid());
            e.setAvatar(user.getAvatar());
            e.setNickname(user.getNickname());
        });
        return CommonPage.copyPageInfo(page, rechargeOrderList);
    }

    /**
     * 充值额度选择
     *
     * @return UserRechargeResponse
     */
    @Override
    public RechargePackageResponse getRechargePackage() {
        RechargePackageResponse userRechargeResponse = new RechargePackageResponse();
        userRechargeResponse.setPackageList(systemGroupDataService.getListByGid(GroupDataConstants.GROUP_DATA_ID_RECHARGE_PACKAGE, UserRechargeItemResponse.class));
        String rechargeAttention = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_RECHARGE_ATTENTION);
        List<String> rechargeAttentionList = new ArrayList<>();
        if (StrUtil.isNotBlank(rechargeAttention)) {
            rechargeAttentionList = CrmebUtil.stringToArrayStrRegex(rechargeAttention, "\n");
        }
        userRechargeResponse.setNoticeList(rechargeAttentionList);
        return userRechargeResponse;
    }

    /**
     * 创建用户充值订单
     *
     * @param request 用户下单参数
     */
    @Override
    public OrderPayResultResponse userRechargeOrderCreate(UserRechargeRequest request) {
        User user = userService.getInfo();
        if (ObjectUtil.isNull(request.getPrice()) && ObjectUtil.isNull(request.getGroupDataId())) {
            throw new CrmebException("请选择充值套餐或填写自定义充值金额");
        }
        BigDecimal rechargePrice = BigDecimal.ZERO;
        BigDecimal gainPrice = BigDecimal.ZERO;
        String rechargeMinAmountStr = systemConfigService.getValueByKey(SysConfigConstants.USER_RECHARGE_MIN_AMOUNT);
        BigDecimal rechargeMinAmount = StrUtil.isBlank(rechargeMinAmountStr) ? BigDecimal.ZERO : new BigDecimal(rechargeMinAmountStr);
        if (ObjectUtil.isNotNull(request.getPrice())) {
            if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new CrmebException("充值金额必须大于0");
            }
            rechargePrice = request.getPrice();
        } else {
            UserRechargeItemResponse rechargePackage = systemGroupDataService.getNormalInfo(request.getGroupDataId(), UserRechargeItemResponse.class);
            if (ObjectUtil.isNull(rechargePackage)) {
                throw new CrmebException("您选择的充值方式已下架");
            }
            //售价和赠送
            rechargePrice = new BigDecimal(rechargePackage.getPrice());
            gainPrice = new BigDecimal(rechargePackage.getGiveMoney());
        }
        if (rechargePrice.compareTo(rechargeMinAmount) < 0) {
            throw new CrmebException("充值金额小于最低充值金额");
        }
        String rechargeNo = CrmebUtil.getOrderNo(OrderConstants.RECHARGE_ORDER_PREFIX);
        OrderPayResultResponse response = new OrderPayResultResponse();
        RechargeOrder rechargeOrder = new RechargeOrder();
        if (request.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            MyRecord record = wechatPayment(rechargePrice, request.getPayChannel(), user.getId());
            WxPayJsResultVo vo = record.get("vo");
            String outTradeNo = record.getStr("outTradeNo");
            response.setJsConfig(vo);
            response.setPayType(PayConstants.PAY_TYPE_WE_CHAT);
            rechargeOrder.setOutTradeNo(outTradeNo);
        }
        if (request.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY)) {
            String result = aliPayService.pay(rechargeNo, rechargePrice, "recharge", request.getPayChannel());
            response.setPayType(PayConstants.PAY_TYPE_ALI_PAY);
            response.setAlipayRequest(result);
            rechargeOrder.setOutTradeNo(rechargeNo);
        }
        if (request.getPayType().equals(PayConstants.PAY_TYPE_LIANLIAN)) {
            CashierPayCreateResult cashier = lianLianPayService.cashier(user.getAccount(), rechargeNo, rechargePrice, "补差", request.getIp());
            response.setStatus(true);
            response.setLianLianCashierConfig(cashier);
            response.setPayType(PayConstants.PAY_TYPE_LIANLIAN);
            rechargeOrder.setOutTradeNo(cashier.getAccp_txno());
        }
        rechargeOrder.setUid(user.getId());
        rechargeOrder.setOrderNo(rechargeNo);
        rechargeOrder.setPrice(rechargePrice);
        rechargeOrder.setGivePrice(gainPrice);
        rechargeOrder.setPayType(request.getPayType());
        rechargeOrder.setType(request.getType());
        rechargeOrder.setPayChannel(request.getPayChannel());
        boolean save = save(rechargeOrder);
        if (!save) {
            throw new CrmebException("生成充值订单失败!");
        }
        return response;
    }

    /**
     * 微信支付
     *
     * @param rechargePrice 充值金额
     * @param payChannel    支付渠道
     * @param uid           用户id
     * @return
     */
    private MyRecord wechatPayment(BigDecimal rechargePrice, String payChannel, Integer uid) {
        // 预下单
        Map<String, String> unifiedorder = wechatUnifiedorder(rechargePrice, payChannel, uid);
        WxPayJsResultVo vo = new WxPayJsResultVo();
        vo.setAppId(unifiedorder.get("appId"));
        vo.setNonceStr(unifiedorder.get("nonceStr"));
        vo.setPackages(unifiedorder.get("package"));
        vo.setSignType(unifiedorder.get("signType"));
        vo.setTimeStamp(unifiedorder.get("timeStamp"));
        vo.setPaySign(unifiedorder.get("paySign"));
        if (payChannel.equals(PayConstants.PAY_CHANNEL_H5)) {
            vo.setMwebUrl(unifiedorder.get("mweb_url"));
        }
        if (payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_APP_IOS) ||
                payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID)) {// App
            vo.setPartnerid(unifiedorder.get("partnerid"));
        }
        MyRecord record = new MyRecord();
        record.set("vo", vo);
        record.set("outTradeNo", unifiedorder.get("outTradeNo"));
        return record;
    }

    /**
     * 微信预下单
     *
     * @param rechargePrice 充值金额
     * @param payChannel    支付渠道
     * @return 预下单返回对象
     */
    private Map<String, String> wechatUnifiedorder(BigDecimal rechargePrice, String payChannel, Integer uid) {
        // 获取用户openId
        // 根据订单支付类型来判断获取公众号openId还是小程序openId
        UserToken userToken = new UserToken();
        userToken.setToken("");
        if (payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_PUBLIC)) {// 公众号
            userToken = userTokenService.getTokenByUserId(uid, UserConstants.USER_TOKEN_TYPE_WECHAT);
        }
        if (payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_MINI)) {// 小程序
            userToken = userTokenService.getTokenByUserId(uid, UserConstants.USER_TOKEN_TYPE_ROUTINE);
        }

        // 获取appid、mch_id、微信签名key
        String appId = "";
        String mchId = "";
        String signKey = "";
        switch (payChannel) {
            case PayConstants.PAY_CHANNEL_WECHAT_PUBLIC:
            case PayConstants.PAY_CHANNEL_H5:// H5使用公众号的信息
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PUBLIC_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_PUBLIC_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_PUBLIC_KEY);
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_MINI:
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_MINI_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_KEY);
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_APP_IOS:
            case PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID:
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_APP_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_KEY);
                break;
        }
        // 获取微信预下单对象
        CreateOrderRequestVo unifiedorderVo = getUnifiedorderVo(rechargePrice, uid, userToken.getToken(), appId, mchId, signKey, payChannel);
        // 预下单
        CreateOrderResponseVo responseVo = wechatService.payUnifiedorder(unifiedorderVo);
        // 组装前端预下单参数
        Map<String, String> map = new HashMap<>();
        map.put("appId", unifiedorderVo.getAppid());
        map.put("nonceStr", unifiedorderVo.getNonce_str());
        map.put("package", "prepay_id=".concat(responseVo.getPrepayId()));
        map.put("signType", unifiedorderVo.getSign_type());
        Long currentTimestamp = WxPayUtil.getCurrentTimestamp();
        map.put("timeStamp", Long.toString(currentTimestamp));
        String paySign = WxPayUtil.getSign(map, signKey);
        map.put("paySign", paySign);
        map.put("prepayId", responseVo.getPrepayId());
        map.put("prepayTime", CrmebDateUtil.nowDateTimeStr());
        map.put("outTradeNo", unifiedorderVo.getOut_trade_no());
        if (payChannel.equals(PayConstants.PAY_CHANNEL_H5)) {
            map.put("mweb_url", responseVo.getMWebUrl());
        }
        if (payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_APP_IOS) ||
                payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID)) {// App
            map.put("partnerid", mchId);
            map.put("package", responseVo.getPrepayId());
            Map<String, Object> appMap = new HashMap<>();
            appMap.put("appid", unifiedorderVo.getAppid());
            appMap.put("partnerid", mchId);
            appMap.put("prepayid", responseVo.getPrepayId());
            appMap.put("package", "Sign=WXPay");
            appMap.put("noncestr", unifiedorderVo.getNonce_str());
            appMap.put("timestamp", currentTimestamp);
            logger.info("================================================app支付签名，map = " + appMap);
            String sign = WxPayUtil.getSignObject(appMap, signKey);
            logger.info("================================================app支付签名，sign = " + sign);
            map.put("paySign", sign);
        }
        return map;
    }

    /**
     * 获取微信预下单对象
     */
    private CreateOrderRequestVo getUnifiedorderVo(BigDecimal rechargePrice, Integer uid, String openid, String appId, String mchId, String signKey, String payChannel) {
        // 获取域名
        String domain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_URL);
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);

        AttachVo attachVo = new AttachVo(PayConstants.PAY_SERVICE_TYPE_RECHARGE, uid);

        CreateOrderRequestVo vo = new CreateOrderRequestVo();
        vo.setAppid(appId);
        vo.setMch_id(mchId);
        vo.setNonce_str(WxPayUtil.getNonceStr());
        vo.setSign_type(PayConstants.WX_PAY_SIGN_TYPE_MD5);
        String siteName = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME);
        // 因商品名称在微信侧超长更换为网站名称
        vo.setBody(siteName);
        vo.setAttach(JSONObject.toJSONString(attachVo));
        vo.setOut_trade_no(CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_WECHAT));
        // 订单中使用的是BigDecimal,这里要转为Integer类型
        vo.setTotal_fee(rechargePrice.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        vo.setSpbill_create_ip(RequestUtil.getClientIp());
        vo.setNotify_url(apiDomain + PayConstants.WX_PAY_NOTIFY_API_URI);
        switch (payChannel) {
            case PayConstants.PAY_CHANNEL_H5:
                vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_H5);
                vo.setOpenid(null);
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_APP_IOS:
            case PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID:
                vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_APP);
                vo.setOpenid(null);
                break;
            default:
                vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_JS);
                vo.setOpenid(openid);
        }
        CreateOrderH5SceneInfoVo createOrderH5SceneInfoVo = new CreateOrderH5SceneInfoVo(
                new CreateOrderH5SceneInfoDetailVo(
                        domain,
                        systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME)
                )
        );
        vo.setScene_info(JSONObject.toJSONString(createOrderH5SceneInfoVo));
        String sign = WxPayUtil.getSign(vo, signKey);
        vo.setSign(sign);
        return vo;
    }

    /**
     * 获取订单
     *
     * @param outTradeNo 商户系统内部的订单号
     */
    @Override
    public RechargeOrder getByOutTradeNo(String outTradeNo) {
        LambdaQueryWrapper<RechargeOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(RechargeOrder::getOutTradeNo, outTradeNo);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    @Override
    public RechargeOrder getByOrderNo(String orderNo) {
        return getOne(new QueryWrapper<RechargeOrder>().lambda().eq(RechargeOrder::getOrderNo, orderNo));
    }

    /**
     * 支付成功后置处理
     *
     * @param rechargeOrder 支付订单
     */
    @Async
    @Override
    public Boolean paySuccessAfter(RechargeOrder rechargeOrder) {
        User user = userService.getById(rechargeOrder.getUid());
        BigDecimal addPrice = rechargeOrder.getPrice().add(rechargeOrder.getGivePrice());
        BigDecimal balance = BigDecimal.ZERO;
        if(rechargeOrder.getType() == 0){
            balance = user.getNowMoney().add(addPrice);
        }
        UserBalanceRecord record = new UserBalanceRecord();
        if(rechargeOrder.getType() == 0){
            // 余额变动对象
            record.setUid(rechargeOrder.getUid());
            record.setLinkId(rechargeOrder.getOrderNo());
            record.setLinkType(BalanceRecordConstants.BALANCE_RECORD_LINK_TYPE_RECHARGE);
            record.setType(BalanceRecordConstants.BALANCE_RECORD_TYPE_ADD);
            record.setAmount(addPrice);
            record.setBalance(balance);
            record.setRemark(StrUtil.format(BalanceRecordConstants.BALANCE_RECORD_REMARK_RECHARGE, addPrice));
        }

        Bill bill = new Bill();
        bill.setOrderNo(rechargeOrder.getOrderNo());
        bill.setUid(rechargeOrder.getUid());
        bill.setPm(BillConstants.BILL_PM_ADD);
        bill.setAmount(rechargeOrder.getPrice());
        bill.setType(BillConstants.BILL_TYPE_RECHARGE_USER);
        bill.setMark(StrUtil.format("充值订单，用户充值金额{}元", rechargeOrder.getPrice()));
        Boolean execute = transactionTemplate.execute(e -> {
            // 订单变动
            boolean updatePaid = updatePaid(rechargeOrder.getId(), rechargeOrder.getOrderNo());
            if (!updatePaid) {
                logger.warn("充值订单更新支付状态失败，orderNo = {}", rechargeOrder.getOrderNo());
                e.setRollbackOnly();
            }
            // 创建记录
            if (rechargeOrder.getType() == 0) {
                // 余额变动
                userService.updateNowMoney(user.getId(), addPrice, Constants.OPERATION_TYPE_ADD);
                userBalanceRecordService.save(record);
            } else {
                platformWalletService.transferToUser(user.getId(), rechargeOrder.getType(), addPrice, WalletFlow.OperateEnum.充值.toString(),
                        rechargeOrder.getOrderNo(), StrUtil.format("充值订单，用户充值金额{}元", rechargeOrder.getPrice()));
            }
            billService.save(bill);
            return Boolean.TRUE;
        });
        if (execute) {
            // 发送充值成功通知
            asyncService.sendRechargeSuccessNotification(rechargeOrder, user);
        }
        return execute;
    }

    /**
     * 支付完成变动
     */
    private boolean updatePaid(Integer id, String orderNo) {
        LambdaUpdateWrapper<RechargeOrder> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(RechargeOrder::getPaid, true);
        wrapper.set(RechargeOrder::getPayTime, CrmebDateUtil.nowDateTime());
        wrapper.eq(RechargeOrder::getId, id);
        wrapper.eq(RechargeOrder::getOrderNo, orderNo);
        wrapper.eq(RechargeOrder::getPaid, false);
        return update(wrapper);
    }

    /**
     * 获取某一天的充值记录
     *
     * @param date 日期 yyyy-MM-dd
     * @return 充值记录
     */
    @Override
    public List<RechargeOrder> findByDate(String date) {
        LambdaQueryWrapper<RechargeOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(RechargeOrder::getPaid, true);
        lqw.apply("date_format(pay_time, '%Y-%m-%d') = {0}", date);
        return dao.selectList(lqw);
    }

    /**
     * 获取某一月的充值记录
     *
     * @param month 日期 yyyy-MM
     * @return 充值记录
     */
    @Override
    public List<RechargeOrder> findByMonth(String month) {
        LambdaQueryWrapper<RechargeOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(RechargeOrder::getPaid, true);
        lqw.apply("date_format(pay_time, '%Y-%m') = {0}", month);
        return dao.selectList(lqw);
    }

    @Override
    public List<RechargeOrder> getWaitPayList(int intervalMinutes) {
        Date now = DateTimeUtils.getNow();
        Date start = DateTimeUtils.addMinutes(now, -intervalMinutes);
        LambdaQueryWrapper<RechargeOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(RechargeOrder::getPaid, false);
        lqw.ge(RechargeOrder::getCreateTime, start);
        lqw.le(RechargeOrder::getCreateTime, now);
        return list(lqw);
    }
}

