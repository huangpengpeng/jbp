package com.jbp.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.jbp.front.service.FrontOrderService;
import com.jbp.front.service.UserCenterService;
import com.jbp.common.constants.Constants;
import com.jbp.common.constants.DateConstants;
import com.jbp.common.constants.GroupDataConstants;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.record.BrowseRecord;
import com.jbp.common.model.system.SystemUserLevel;
import com.jbp.common.model.user.*;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.*;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.service.dao.UserDao;
import com.jbp.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户中心 服务实现类
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
public class UserCenterServiceImpl extends ServiceImpl<UserDao, User> implements UserCenterService {

    private final Logger logger = LoggerFactory.getLogger(UserCenterServiceImpl.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserBalanceRecordService userBalanceRecordService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private SystemUserLevelService systemUserLevelService;
    @Autowired
    private SystemGroupDataService systemGroupDataService;
    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;
    @Autowired
    private UserExperienceRecordService userExperienceRecordService;
    @Autowired
    private FrontOrderService frontOrderService;
    @Autowired
    private BrowseRecordService browseRecordService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserVisitRecordService userVisitRecordService;
    @Autowired
    private AsyncService asyncService;


    /**
     * 获取个人中心详情
     * @return 个人中心数据
     */
    @Override
    public UserCenterResponse getUserCenterInfo() {
        Integer uid = userService.getUserId();
        UserCenterResponse response = new UserCenterResponse();
        response.setCenterBanner(systemGroupDataService.getListMapByGid(GroupDataConstants.GROUP_DATA_ID_USER_CENTER_BANNER));
        response.setCenterMenu(systemGroupDataService.getListMapByGid(GroupDataConstants.GROUP_DATA_ID_USER_CENTER_MENU));
        if (uid <= 0) {
            response.setId(0);
            return response;
        }
        User user = getById(uid);
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("登录信息已过期，请重新登录！");
        }
        BeanUtils.copyProperties(user, response);
        response.setPhone(CrmebUtil.maskMobile(response.getPhone()));
        // 优惠券数量
        response.setCouponCount(couponUserService.getUseCount(user.getId()));
        // 收藏数量
        response.setCollectCount(productRelationService.getCollectCountByUid(user.getId()));
        // 足迹
        response.setBrowseNum(browseRecordService.getCountByUid(uid));

        response.setIsVip(false);
        if (response.getLevel() > 0) {
            SystemUserLevel systemUserLevel = systemUserLevelService.getByLevelId(user.getId());
            if (ObjectUtil.isNotNull(systemUserLevel)) {
                response.setIsVip(true);
                response.setVipIcon(systemUserLevel.getIcon());
                response.setVipName(systemUserLevel.getName());
            }
        }
        // 判断是否展示我的推广，1.分销模式是否开启
        response.setIsPromoter(false);
        String retailStoreSwitch = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_SWITCH);
        if (retailStoreSwitch.equals(Constants.COMMON_SWITCH_OPEN) && user.getIsPromoter()) {
            response.setIsPromoter(true);
        }

        // 保存用户访问记录
        asyncService.visitUserCenter(user.getId());
        return response;
    }

    /**
     * 我的推广(冻结的佣金 累计提现金额 当前佣金)
     */
    @Override
    public UserMyPromotionResponse getMyPromotion() {
        User user = userService.getInfo();
        // 冻结的佣金
        BigDecimal freezePrice = userBrokerageRecordService.getFreezePrice(user.getId());
        //累计已提取佣金
        BigDecimal settledCommissionPrice = userBrokerageRecordService.getSettledCommission(user.getId());
        UserMyPromotionResponse response = new UserMyPromotionResponse();
        response.setFreezePrice(freezePrice);
        response.setSettledCommissionPrice(settledCommissionPrice);
        response.setBrokeragePrice(user.getBrokeragePrice());
        return response;
    }

//    /**
//     * 推广佣金/提现总和
//     * @return BigDecimal
//     */
//    @Override
//    public BigDecimal getSpreadCountByType(Integer type) {
//        //推广佣金/提现总和
//        Integer userId = userService.getUserIdException();
//        if (type == 3) {
//            BigDecimal count = userBillService.getSumBigDecimal(null, userId, Constants.USER_BILL_CATEGORY_MONEY, null, Constants.USER_BILL_TYPE_BROKERAGE);
//            BigDecimal withdraw = userBillService.getSumBigDecimal(1, userId, Constants.USER_BILL_CATEGORY_MONEY, null, Constants.USER_BILL_TYPE_BROKERAGE); //提现
//            return count.subtract(withdraw);
//        }
//
//        //累计提现
//        if (type == 4) {
//            return userExtractService.getWithdrawn(null,null);
//        }
//
//        return BigDecimal.ZERO;
//    }
//
//    /**
//     * 提现申请
//     * @return Boolean
//     */
//    @Override
//    public Boolean extractCash(UserExtractRequest request) {
//        switch (request.getExtractType()) {
//            case "weixin":
//                if (StrUtil.isBlank(request.getWechat())) {
//                    throw new CrmebException("请填写微信号！");
//                }
//                request.setAlipayCode(null);
//                request.setBankCode(null);
//                request.setBankName(null);
//                break;
//            case "alipay":
//                if (StrUtil.isBlank(request.getAlipayCode())) {
//                    throw new CrmebException("请填写支付宝账号！");
//                }
//                request.setWechat(null);
//                request.setBankCode(null);
//                request.setBankName(null);
//                break;
//            case "bank":
//                if (StrUtil.isBlank(request.getBankName())) {
//                    throw new CrmebException("请填写银行名称！");
//                }
//                if (StrUtil.isBlank(request.getBankCode())) {
//                    throw new CrmebException("请填写银行卡号！");
//                }
//                request.setWechat(null);
//                request.setAlipayCode(null);
//                break;
//            default:
//                throw new CrmebException("请选择支付方式");
//        }
//        return userExtractService.extractApply(request);
//    }
//
//    /**
//     * 提现银行/提现最低金额
//     * @return UserExtractCashResponse
//     */
//    @Override
//    public List<String> getExtractBank() {
//        // 获取提现银行
//        String bank = systemConfigService.getValueByKeyException(Constants.CONFIG_BANK_LIST).replace("\r\n", "\n");
//        List<String> bankArr = new ArrayList<>();
//        if (bank.indexOf("\n") > 0) {
//            bankArr.addAll(Arrays.asList(bank.split("\n")));
//        }else{
//            bankArr.add(bank);
//        }
//        return bankArr;
//    }

    /**
     * 会员等级列表
     * @return List<UserLevel>
     */
    @Override
    public List<SystemUserLevel> getUserLevelList() {
        return systemUserLevelService.getH5LevelList();
    }

//    /**
//     * 推广用户， 我自己推广了哪些用户
//     * @return List<UserSpreadPeopleItemResponse>
//     */
//    @Override
//    public List<UserSpreadPeopleItemResponse> getSpreadPeopleList(UserSpreadPeopleRequest request, PageParamRequest pageParamRequest) {
//        //查询当前用户名下的一级推广员
//        Integer userId = userService.getUserIdException();
//        List<Integer> userIdList = new ArrayList<>();
//        userIdList.add(userId);
//        userIdList = userService.getSpreadPeopleIdList(userIdList); //我推广的一级用户id集合
//
//        if (CollUtil.isEmpty(userIdList)) {//如果没有一级推广人，直接返回
//            return new ArrayList<>();
//        }
//        if (request.getGrade().equals(1)) {// 二级推广人
//            //查询二级推广人
//            List<Integer> secondSpreadIdList = userService.getSpreadPeopleIdList(userIdList);
//            if (CollUtil.isEmpty(secondSpreadIdList)) {
//                return new ArrayList<>();
//            }
//            //二级推广人
//            userIdList.clear();
//            userIdList.addAll(secondSpreadIdList);
//        }
//        List<UserSpreadPeopleItemResponse> spreadPeopleList = userService.getSpreadPeopleList(userIdList, request.getKeyword(), request.getSortKey(), request.getIsAsc(), pageParamRequest);
////        spreadPeopleList.forEach(e -> {
////            OrderBrokerageData brokerageData = storeOrderService.getBrokerageData(e.getUid(), userId);
////            e.setOrderCount(brokerageData.getNum());
////            e.setNumberCount(brokerageData.getPrice());
////        });
//        return spreadPeopleList;
//    }

    /**
     * 我的账户
     * @return UserMyAccountResponse
     */
    @Override
    public UserMyAccountResponse getMyAccount() {
        User user = userService.getInfo();
        UserMyAccountResponse response = new UserMyAccountResponse();
        response.setNowMoney(user.getNowMoney());
        response.setMonetary(BigDecimal.ZERO);
        response.setRecharge(BigDecimal.ZERO);
        response.setRechargeSwitch(false);
        List<UserBalanceRecord> monetaryRecordList = userBalanceRecordService.getMonetaryRecordByUid(user.getId());
        if (CollUtil.isNotEmpty(monetaryRecordList)) {
            response.setMonetary(monetaryRecordList.stream().map(UserBalanceRecord::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        List<UserBalanceRecord> rechargeRecordList = userBalanceRecordService.getRechargeRecordByUid(user.getId());
        if (CollUtil.isNotEmpty(rechargeRecordList)) {
            response.setRecharge(rechargeRecordList.stream().map(UserBalanceRecord::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        String rechargeSwitch = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_RECHARGE_SWITCH);
        if (StrUtil.isNotBlank(rechargeSwitch) && rechargeSwitch.equals(Constants.CONFIG_FORM_SWITCH_OPEN)) {
            response.setRechargeSwitch(true);
        }
        return response;
    }

    /**
     * 用户余额记录
     * @param recordType 记录类型：all-全部，expenditure-支出，income-收入，recharge-充值
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserBalanceRecordMonthResponse> getUserBalanceRecord(String recordType, PageParamRequest pageRequest) {
        if (StrUtil.isBlank(recordType)) {
            throw new CrmebException("记录类型不能为空");
        }
        if (!recordType.equals("all") && !recordType.equals("expenditure") && !recordType.equals("income") && !recordType.equals("recharge")) {
            throw new CrmebException("未知的记录类型");
        }
        Integer uid = userService.getUserIdException();
        PageInfo<UserBalanceRecord> pageInfo = userBalanceRecordService.getUserBalanceRecord(uid, recordType, pageRequest);
        List<UserBalanceRecord> recordList = pageInfo.getList();
        if (CollUtil.isEmpty(recordList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        // 获取年-月
        Map<String, List<UserBalanceRecord>> map = CollUtil.newHashMap();
        recordList.forEach(i -> {
            String month = StrUtil.subPre(CrmebDateUtil.dateToStr(i.getCreateTime(), DateConstants.DATE_FORMAT), 7);
            if (map.containsKey(month)) {
                map.get(month).add(i);
            } else {
                List<UserBalanceRecord> list = CollUtil.newArrayList();
                list.add(i);
                map.put(month, list);
            }
        });
        List<UserBalanceRecordMonthResponse> responseList = CollUtil.newArrayList();
        map.forEach((key, value) -> {
            UserBalanceRecordMonthResponse response = new UserBalanceRecordMonthResponse();
            response.setMonth(key);
            response.setList(value);
            responseList.add(response);
        });
        List<UserBalanceRecordMonthResponse> collect = responseList.stream().sorted(Comparator.comparing(s -> DateUtil.parse(s.getMonth(), "yyyy-MM").getTime(), Comparator.reverseOrder())).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, collect);
    }

    /**
     * 我的积分(当前积分 累计积分 累计消费 冻结中积分)
     *
     * @return 我的积分
     */
    @Override
    public UserMyIntegralResponse getMyIntegral() {
        User user = userService.getInfo();
        Integer settledIntegral = userIntegralRecordService.getSettledIntegralByUid(user.getId());
        Integer freezeIntegral = userIntegralRecordService.getFreezeIntegralByUid(user.getId());

        UserMyIntegralResponse response = new UserMyIntegralResponse();
        response.setIntegral(user.getIntegral());
        response.setSettledIntegral(settledIntegral);
        response.setUseIntegral(settledIntegral - user.getIntegral());
        response.setFreezeIntegral(freezeIntegral);
        return response;
    }

//    /**
//     * 推广订单
//     * @return UserSpreadOrderResponse;
//     */
//    @Override
//    public UserSpreadOrderResponse getSpreadOrder(PageParamRequest pageParamRequest) {
//        User user = userService.getInfo();
//        if (ObjectUtil.isNull(user)) {
//            throw new CrmebException("用户数据异常");
//        }
//        UserSpreadOrderResponse spreadOrderResponse = new UserSpreadOrderResponse();
//        // 获取累计推广条数
//        Integer spreadCount = userBrokerageRecordService.getSpreadCountByUid(user.getUid());
//        spreadOrderResponse.setCount(spreadCount.longValue());
//        if (spreadCount.equals(0)) {
//            return spreadOrderResponse;
//        }
//
//        // 获取推广订单记录，分页
//        List<UserBrokerageRecord> recordList = userBrokerageRecordService.findSpreadListByUid(user.getUid(), pageParamRequest);
//        // 获取对应的订单信息
//        List<String> orderNoList = recordList.stream().map(UserBrokerageRecord::getLinkId).collect(Collectors.toList());
//        Map<String, StoreOrder> orderMap = storeOrderService.getMapInOrderNo(orderNoList);
//        // 获取对应的用户信息
//        List<StoreOrder> storeOrderList = new ArrayList<>(orderMap.values());
//        List<Integer> uidList = storeOrderList.stream().map(StoreOrder::getUid).distinct().collect(Collectors.toList());
//        HashMap<Integer, User> userMap = userService.getMapListInUid(uidList);
//
//        List<UserSpreadOrderItemResponse> userSpreadOrderItemResponseList = new ArrayList<>();
//        List<String> monthList = CollUtil.newArrayList();
//        recordList.forEach(record -> {
//            UserSpreadOrderItemChildResponse userSpreadOrderItemChildResponse = new UserSpreadOrderItemChildResponse();
//            userSpreadOrderItemChildResponse.setOrderId(record.getLinkId());
//            userSpreadOrderItemChildResponse.setTime(record.getUpdateTime());
//            userSpreadOrderItemChildResponse.setNumber(record.getPrice());
//            Integer orderUid = orderMap.get(record.getLinkId()).getUid();
//            userSpreadOrderItemChildResponse.setAvatar(userMap.get(orderUid).getAvatar());
//            userSpreadOrderItemChildResponse.setNickname(userMap.get(orderUid).getNickname());
//            userSpreadOrderItemChildResponse.setType("返佣");
//
//            String month = DateUtil.dateToStr(record.getUpdateTime(), DateConstants.DATE_FORMAT_MONTH);
//            if (monthList.contains(month)) {
//                //如果在已有的数据中找到当前月份数据则追加
//                for (UserSpreadOrderItemResponse userSpreadOrderItemResponse : userSpreadOrderItemResponseList) {
//                    if (userSpreadOrderItemResponse.getTime().equals(month)) {
//                        userSpreadOrderItemResponse.getChild().add(userSpreadOrderItemChildResponse);
//                        break;
//                    }
//                }
//            } else {// 不包含此月份
//                //创建一个
//                UserSpreadOrderItemResponse userSpreadOrderItemResponse = new UserSpreadOrderItemResponse();
//                userSpreadOrderItemResponse.setTime(month);
//                userSpreadOrderItemResponse.getChild().add(userSpreadOrderItemChildResponse);
//                userSpreadOrderItemResponseList.add(userSpreadOrderItemResponse);
//                monthList.add(month);
//            }
//        });
//
//        // 获取月份总订单数
//        Map<String, Integer> countMap = userBrokerageRecordService.getSpreadCountByUidAndMonth(user.getUid(), monthList);
//        for (UserSpreadOrderItemResponse userSpreadOrderItemResponse: userSpreadOrderItemResponseList) {
//            userSpreadOrderItemResponse.setCount(countMap.get(userSpreadOrderItemResponse.getTime()));
//        }
//
//        spreadOrderResponse.setList(userSpreadOrderItemResponseList);
//        return spreadOrderResponse;
//    }
//
//    /**
//     * 充值
//     * @return UserSpreadOrderResponse;
//     */
//    @Override
//    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
//    public OrderPayResultResponse recharge(UserRechargeRequest request) {
//        request.setPayType(Constants.PAY_TYPE_WE_CHAT);
//
//        //验证金额是否为最低金额
//        String rechargeMinAmountStr = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_RECHARGE_MIN_AMOUNT);
//        BigDecimal rechargeMinAmount = new BigDecimal(rechargeMinAmountStr);
//        int compareResult = rechargeMinAmount.compareTo(request.getPrice());
//        if (compareResult > 0) {
//            throw new CrmebException("充值金额不能低于" + rechargeMinAmountStr);
//        }
//
//        request.setGivePrice(BigDecimal.ZERO);
//
//        if (request.getGroupDataId() > 0) {
//            SystemGroupDataRechargeConfigVo systemGroupData = systemGroupDataService.getNormalInfo(request.getGroupDataId(), SystemGroupDataRechargeConfigVo.class);
//            if (ObjectUtil.isNull(systemGroupData)) {
//                throw new CrmebException("您选择的充值方式已下架");
//            }
//            //售价和赠送
//            request.setPrice(systemGroupData.getPrice());
//            request.setGivePrice(systemGroupData.getGiveMoney());
//        }
//        User currentUser = userService.getInfoException();
//        //生成系统订单
//        UserRecharge userRecharge = new UserRecharge();
//        userRecharge.setUid(currentUser.getUid());
//        userRecharge.setOrderId(CrmebUtil.getOrderNo("recharge"));
//        userRecharge.setPrice(request.getPrice());
//        userRecharge.setGivePrice(request.getGivePrice());
//        userRecharge.setRechargeType(request.getFromType());
//        boolean save = userRechargeService.save(userRecharge);
//        if (!save) {
//            throw new CrmebException("生成充值订单失败!");
//        }
//
//        OrderPayResultResponse response = new OrderPayResultResponse();
//        MyRecord record = new MyRecord();
//        Map<String, String> unifiedorder = weChatPayService.unifiedRecharge(userRecharge, request.getClientIp());
//        record.set("status", true);
//        response.setStatus(true);
//        WxPayJsResultVo vo = new WxPayJsResultVo();
//        vo.setAppId(unifiedorder.get("appId"));
//        vo.setNonceStr(unifiedorder.get("nonceStr"));
//        vo.setPackages(unifiedorder.get("package"));
//        vo.setSignType(unifiedorder.get("signType"));
//        vo.setTimeStamp(unifiedorder.get("timeStamp"));
//        vo.setPaySign(unifiedorder.get("paySign"));
//        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_H5)) {
//            vo.setMwebUrl(unifiedorder.get("mweb_url"));
//            response.setPayType(PayConstants.PAY_CHANNEL_WE_CHAT_H5);
//        }
//        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS) || userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID)) {//
//            vo.setPartnerid(unifiedorder.get("partnerid"));
//        }
//        response.setJsConfig(vo);
//        response.setOrderNo(userRecharge.getOrderId());
//        return response;
//    }
//
//    /**
//     * 微信登录
//     * @return LoginResponse;
//     */
//    @Override
//    public LoginResponse weChatAuthorizeLogin(String code, Integer spreadUid) {
//        // 通过code获取获取公众号授权信息
//        WeChatOauthToken oauthToken = wechatNewService.getOauth2AccessToken(code);
//        //检测是否存在
//        UserToken userToken = userTokenService.getByOpenidAndType(oauthToken.getOpenId(),  Constants.THIRD_LOGIN_TOKEN_TYPE_PUBLIC);
//        LoginResponse loginResponse = new LoginResponse();
//        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
//            User user = userService.getById(userToken.getUid());
//            if (!user.getStatus()) {
//                throw new CrmebException("当前账户已禁用，请联系管理员！");
//            }
//
//            // 记录最后一次登录时间
//            user.setLastLoginTime(DateUtil.nowDateTime());
//            Boolean execute = transactionTemplate.execute(e -> {
//                // 分销绑定
//                if (userService.checkBingSpread(user, spreadUid, "old")) {
//                    user.setSpreadUid(spreadUid);
//                    user.setSpreadTime(DateUtil.nowDateTime());
//                    // 处理新旧推广人数据
//                    userService.updateSpreadCountByUid(spreadUid, "add");
//                }
//                userService.updateById(user);
//                return Boolean.TRUE;
//            });
//            if (!execute) {
//                logger.error(StrUtil.format("公众号登录绑定分销关系失败，uid={},spreadUid={}", user.getUid(), spreadUid));
//            }
//            try {
//                String token = tokenComponent.createToken(user);
//                loginResponse.setToken(token);
//            } catch (Exception e) {
//                logger.error(StrUtil.format("公众号登录生成token失败，uid={}", user.getUid()));
//                e.printStackTrace();
//            }
//            loginResponse.setType("login");
//            loginResponse.setUid(user.getUid());
//            loginResponse.setNikeName(user.getNickname());
//            loginResponse.setPhone(user.getPhone());
//            return loginResponse;
//        }
//        // 没有用户，走创建用户流程
//        // 从微信获取用户信息，存入Redis中，将key返回给前端，前端在下一步绑定手机号的时候下发
//        WeChatAuthorizeLoginUserInfoVo userInfo = wechatNewService.getSnsUserInfo(oauthToken.getAccessToken(), oauthToken.getOpenId());
//        RegisterThirdUserRequest registerThirdUserRequest = new RegisterThirdUserRequest();
//        BeanUtils.copyProperties(userInfo, registerThirdUserRequest);
//        registerThirdUserRequest.setSpreadPid(spreadUid);
//        registerThirdUserRequest.setType(Constants.USER_LOGIN_TYPE_PUBLIC);
//        registerThirdUserRequest.setOpenId(oauthToken.getOpenId());
//        String key = SecureUtil.md5(oauthToken.getOpenId());
//        redisUtil.set(key, JSONObject.toJSONString(registerThirdUserRequest), (long) (60 * 2), TimeUnit.MINUTES);
//
//        loginResponse.setType("register");
//        loginResponse.setKey(key);
//        return loginResponse;
//    }
//
//    /**
//     * 获取微信授权logo
//     * @return String;
//     */
//    @Override
//    public String getLogo() {
//        return systemConfigService.getValueByKey(Constants.CONFIG_KEY_MOBILE_LOGIN_LOGO);
//    }
//
//    /**
//     * 小程序登录
//     * @param code String 前端临时授权code
//     * @param request RegisterThirdUserRequest 用户信息
//     * @return LoginResponse
//     */
//    @Override
//    public LoginResponse weChatAuthorizeProgramLogin(String code, RegisterThirdUserRequest request) {
//        WeChatMiniAuthorizeVo response = wechatNewService.miniAuthCode(code);
//        System.out.println("小程序登陆成功 = " + JSON.toJSONString(response));
//
//        //检测是否存在
//        UserToken userToken = userTokenService.getByOpenidAndType(response.getOpenId(), Constants.THIRD_LOGIN_TOKEN_TYPE_PROGRAM);
//        LoginResponse loginResponse = new LoginResponse();
//        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
//            User user = userService.getById(userToken.getUid());
//            if (!user.getStatus()) {
//                throw new CrmebException("当前账户已禁用，请联系管理员！");
//            }
//            // 记录最后一次登录时间
//            user.setLastLoginTime(DateUtil.nowDateTime());
//            Boolean execute = transactionTemplate.execute(e -> {
//                // 分销绑定
//                if (userService.checkBingSpread(user, request.getSpreadPid(), "old")) {
//                    user.setSpreadUid(request.getSpreadPid());
//                    user.setSpreadTime(DateUtil.nowDateTime());
//                    // 处理新旧推广人数据
//                    userService.updateSpreadCountByUid(request.getSpreadPid(), "add");
//                }
//                userService.updateById(user);
//                return Boolean.TRUE;
//            });
//            if (!execute) {
//                logger.error(StrUtil.format("小程序登录绑定分销关系失败，uid={},spreadUid={}", user.getUid(), request.getSpreadPid()));
//            }
//
//            try {
//                String token = tokenComponent.createToken(user);
//                loginResponse.setToken(token);
//            } catch (Exception e) {
//                logger.error(StrUtil.format("小程序登录生成token失败，uid={}", user.getUid()));
//                e.printStackTrace();
//            }
//            loginResponse.setType("login");
//            loginResponse.setUid(user.getUid());
//            loginResponse.setNikeName(user.getNickname());
//            loginResponse.setPhone(user.getPhone());
//            return loginResponse;
//        }
//
//        if (StrUtil.isBlank(request.getNickName()) && StrUtil.isBlank(request.getAvatar()) && StrUtil.isBlank(request.getHeadimgurl())) {
//            // 返回后，前端去走注册起始页
//            loginResponse.setType("start");
//            return loginResponse;
//        }
//
//        request.setType(Constants.USER_LOGIN_TYPE_PROGRAM);
//        request.setOpenId(response.getOpenId());
//        String key = SecureUtil.md5(response.getOpenId());
//        redisUtil.set(key, JSONObject.toJSONString(request), (long) (60 * 2), TimeUnit.MINUTES);
//        loginResponse.setType("register");
//        loginResponse.setKey(key);
//        return loginResponse;
//    }
//
//    /**
//     * 推广人排行榜
//     * @param type  String 时间范围(week-周，month-月)
//     * @param pageParamRequest PageParamRequest 分页
//     * @return List<LoginResponse>
//     */
//    @Override
//    public List<User> getTopSpreadPeopleListByDate(String type, PageParamRequest pageParamRequest) {
//        return userService.getTopSpreadPeopleListByDate(type, pageParamRequest);
//    }
//
//    /**
//     * 佣金排行榜
//     * @param type  String 时间范围
//     * @param pageParamRequest PageParamRequest 分页
//     * @return List<User>
//     */
//    @Override
//    public List<User> getTopBrokerageListByDate(String type, PageParamRequest pageParamRequest) {
//        // 获取佣金排行榜（周、月）
//        List<UserBrokerageRecord> recordList = userBrokerageRecordService.getBrokerageTopByDate(type);
//        if (CollUtil.isEmpty(recordList)) {
//            return null;
//        }
//        // 解决0元排行问题
//        for (int i = 0; i < recordList.size();) {
//            UserBrokerageRecord userBrokerageRecord = recordList.get(i);
//            if (userBrokerageRecord.getPrice().compareTo(BigDecimal.ZERO) < 1) {
//                recordList.remove(i);
//                continue;
//            }
//            i++;
//        }
//        if (CollUtil.isEmpty(recordList)) {
//            return null;
//        }
//
//        List<Integer> uidList = recordList.stream().map(UserBrokerageRecord::getUid).collect(Collectors.toList());
//        //查询用户
//        HashMap<Integer, User> userVoList = userService.getMapListInUid(uidList);
//
//        //解决排序问题
//        List<User> userList = CollUtil.newArrayList();
//        for (UserBrokerageRecord record: recordList) {
//            User user = new User();
//            User userVo = userVoList.get(record.getUid());
//
//            user.setUid(record.getUid());
//            user.setAvatar(userVo.getAvatar());
//            user.setBrokeragePrice(record.getPrice());
//            if (StrUtil.isBlank(userVo.getNickname())) {
//                user.setNickname(userVo.getPhone().substring(0, 2) + "****" + userVo.getPhone().substring(7));
//            }else{
//                user.setNickname(userVo.getNickname());
//            }
//            userList.add(user);
//        }
//        return userList;
//    }
//
//    /**
//     * 推广海报图
//     * @return List<SystemGroupData>
//     */
//    @Override
//    public List<UserSpreadBannerResponse> getSpreadBannerList() {
//        return systemGroupDataService.getListByGid(Constants.GROUP_DATA_ID_SPREAD_BANNER_LIST, UserSpreadBannerResponse.class);
//    }
//
//    /**
//     * 当前用户在佣金排行第几名
//     * @param type  String 时间范围
//     * @return 优惠券集合
//     */
//    @Override
//    public Integer getNumberByTop(String type) {
//        int number = 0;
//        Integer userId = userService.getUserIdException();
//        PageParamRequest pageParamRequest = new PageParamRequest();
//        pageParamRequest.setLimit(100);
//
//        List<UserBrokerageRecord> recordList = userBrokerageRecordService.getBrokerageTopByDate(type);
//        if (CollUtil.isEmpty(recordList)) {
//            return number;
//        }
//
//        for (int i = 0; i < recordList.size(); i++) {
//            if (recordList.get(i).getUid().equals(userId)) {
//                number = i + 1;
//                break ;
//            }
//        }
//        return number;
//    }
//
//    /**
//     * 佣金转入余额
//     * @return Boolean
//     */
//    @Override
//    public Boolean transferIn(BigDecimal price) {
//        if (price.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new CrmebException("转入金额不能为0");
//        }
//        //当前可提现佣金
//        User user = userService.getInfo();
//        if (ObjectUtil.isNull(user)) {
//            throw new CrmebException("用户数据异常");
//        }
//        BigDecimal subtract = user.getBrokeragePrice();
//        if (subtract.compareTo(price) < 0) {
//            throw new CrmebException("您当前可充值余额为 " + subtract + "元");
//        }
//        // userBill现金增加记录
//        UserBill userBill = new UserBill();
//        userBill.setUid(user.getUid());
//        userBill.setLinkId("0");
//        userBill.setPm(1);
//        userBill.setTitle("佣金转余额");
//        userBill.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
//        userBill.setType(Constants.USER_BILL_TYPE_TRANSFER_IN);
//        userBill.setNumber(price);
//        userBill.setBalance(user.getNowMoney().add(price));
//        userBill.setMark(StrUtil.format("佣金转余额,增加{}", price));
//        userBill.setStatus(1);
//        userBill.setCreateTime(DateUtil.nowDateTime());
//
//        // userBrokerage转出记录
//        UserBrokerageRecord brokerageRecord = new UserBrokerageRecord();
//        brokerageRecord.setUid(user.getUid());
//        brokerageRecord.setLinkId("0");
//        brokerageRecord.setLinkType(BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_YUE);
//        brokerageRecord.setType(BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_SUB);
//        brokerageRecord.setTitle(BrokerageRecordConstants.BROKERAGE_RECORD_TITLE_BROKERAGE_YUE);
//        brokerageRecord.setPrice(price);
//        brokerageRecord.setBalance(user.getNowMoney().add(price));
//        brokerageRecord.setMark(StrUtil.format("佣金转余额，减少{}", price));
//        brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
//        brokerageRecord.setCreateTime(DateUtil.nowDateTime());
//
//        Boolean execute = transactionTemplate.execute(e -> {
//            // 扣佣金
//            userService.operationBrokerage(user.getUid(), price, user.getBrokeragePrice(), "sub");
//            // 加余额
//            userService.operationNowMoney(user.getUid(), price, user.getNowMoney(), "add");
//            userBillService.save(userBill);
//            userBrokerageRecordService.save(brokerageRecord);
//            return Boolean.TRUE;
//        });
//        return execute;
//    }
//
//    /**
//     * 提现记录
//     */
//    @Override
//    public PageInfo<UserExtractRecordResponse> getExtractRecord(PageParamRequest pageParamRequest) {
//        Integer userId = userService.getUserIdException();
//        return userExtractService.getExtractRecord(userId, pageParamRequest);
//    }
//
//    /**
//     * 推广佣金明细
//     * @param pageParamRequest 分页参数
//     */
//    @Override
//    public PageInfo<SpreadCommissionDetailResponse> getSpreadCommissionDetail(PageParamRequest pageParamRequest) {
//        User user = userService.getInfoException();
//        return userBrokerageRecordService.findDetailListByUid(user.getUid(), pageParamRequest);
//    }
//
//    /**
//     * 用户账单记录（现金）
//     * @param type 记录类型：all-全部，expenditure-支出，income-收入
//     * @return CommonPage
//     */
//    @Override
//    public CommonPage<UserRechargeBillRecordResponse> nowMoneyBillRecord(String type, PageParamRequest pageRequest) {
//        User user = userService.getInfo();
//        if (ObjectUtil.isNull(user)) {
//            throw new CrmebException("用户数据异常");
//        }
//        PageInfo<UserBill> billPageInfo = userBillService.nowMoneyBillRecord(user.getUid(), type, pageRequest);
//        List<UserBill> list = billPageInfo.getList();
//
//        // 获取年-月
//        Map<String, List<UserBill>> map = CollUtil.newHashMap();
//        list.forEach(i -> {
//            String month = StrUtil.subPre(DateUtil.dateToStr(i.getCreateTime(), DateConstants.DATE_FORMAT), 7);
//            if (map.containsKey(month)) {
//                map.get(month).add(i);
//            } else {
//                List<UserBill> billList = CollUtil.newArrayList();
//                billList.add(i);
//                map.put(month, billList);
//            }
//        });
//        List<UserRechargeBillRecordResponse> responseList = CollUtil.newArrayList();
//        map.forEach((key, value) -> {
//            UserRechargeBillRecordResponse response = new UserRechargeBillRecordResponse();
//            response.setDate(key);
//            response.setList(value);
//            responseList.add(response);
//        });
//
//        List<UserRechargeBillRecordResponse> collect = responseList.stream().sorted(Comparator.comparing(s -> cn.hutool.core.date.DateUtil.parse(s.getDate(), "yyyy-MM").getTime(), Comparator.reverseOrder())).collect(Collectors.toList());
//        PageInfo<UserRechargeBillRecordResponse> pageInfo = CommonPage.copyPageInfo(billPageInfo, collect);
//        return CommonPage.restPage(pageInfo);
//    }
//
//    /**
//     * 微信注册绑定手机号
//     * @param request 请求参数
//     * @return 登录信息
//     */
//    @Override
//    public LoginResponse registerBindingPhone(WxBindingPhoneRequest request) {
//        checkBindingPhone(request);
//
//        // 进入创建用户绑定手机号流程
//        Object o = redisUtil.get(request.getKey());
//        if (ObjectUtil.isNull(o)) {
//            throw new CrmebException("用户缓存已过期，请清除缓存重新登录");
//        }
//        RegisterThirdUserRequest registerThirdUserRequest = JSONObject.parseObject(o.toString(), RegisterThirdUserRequest.class);
//
//        boolean isNew = true;
//
//        User user = userService.getByPhone(request.getPhone());
//        if (ObjectUtil.isNull(user)) {
//            user = userService.registerByThird(registerThirdUserRequest);
//            user.setPhone(request.getPhone());
//            user.setAccount(request.getPhone());
//            user.setSpreadUid(0);
//            user.setPwd(CommonUtil.createPwd(request.getPhone()));
//        } else {// 已有账户，关联到之前得账户即可
//            // 查询是否用对应得token
//            int type = 0;
//            switch (request.getType()) {
//                case "public":
//                    type = Constants.THIRD_LOGIN_TOKEN_TYPE_PUBLIC;
//                    break;
//                case "routine":
//                    type = Constants.THIRD_LOGIN_TOKEN_TYPE_PROGRAM;
//                    break;
//                case "iosWx":
//                    type = Constants.THIRD_LOGIN_TOKEN_TYPE_IOS_WX;
//                    break;
//                case "androidWx":
//                    type = Constants.THIRD_LOGIN_TOKEN_TYPE_ANDROID_WX;
//                    break;
//            }
//
//            UserToken userToken = userTokenService.getTokenByUserId(user.getUid(), type);
//            if (ObjectUtil.isNotNull(userToken)) {
//                throw new CrmebException("该手机号已被注册");
//            }
//            isNew = false;
//        }
//
//        User finalUser = user;
//        boolean finalIsNew = isNew;
//        Boolean execute = transactionTemplate.execute(e -> {
//            if (finalIsNew) {// 新用户
//                // 分销绑定
//                if (userService.checkBingSpread(finalUser, registerThirdUserRequest.getSpreadPid(), "new")) {
//                    finalUser.setSpreadUid(registerThirdUserRequest.getSpreadPid());
//                    finalUser.setSpreadTime(DateUtil.nowDateTime());
//                    userService.updateSpreadCountByUid(registerThirdUserRequest.getSpreadPid(), "add");
//                }
//                userService.save(finalUser);
//                // 赠送新人券
//                giveNewPeopleCoupon(finalUser.getUid());
//            }
//            switch (request.getType()) {
//                case "public":
//                    userTokenService.bind(registerThirdUserRequest.getOpenId(), Constants.THIRD_LOGIN_TOKEN_TYPE_PUBLIC, finalUser.getUid());
//                    break;
//                case "routine":
//                    userTokenService.bind(registerThirdUserRequest.getOpenId(), Constants.THIRD_LOGIN_TOKEN_TYPE_PROGRAM, finalUser.getUid());
//                    break;
//                case "iosWx":
//                    userTokenService.bind(registerThirdUserRequest.getOpenId(), Constants.THIRD_LOGIN_TOKEN_TYPE_IOS_WX, finalUser.getUid());
//                    break;
//                default:
//                    userTokenService.bind(registerThirdUserRequest.getOpenId(), Constants.THIRD_LOGIN_TOKEN_TYPE_ANDROID_WX, finalUser.getUid());
//                    break;
//            }
//            return Boolean.TRUE;
//        });
//        if (!execute) {
//            logger.error("微信用户注册生成失败，nickName = " + registerThirdUserRequest.getNickName());
//        } else if (!isNew) {// 老用户绑定推广人
//            if (ObjectUtil.isNotNull(registerThirdUserRequest.getSpreadPid()) && registerThirdUserRequest.getSpreadPid() > 0) {
//                loginService.bindSpread(finalUser, registerThirdUserRequest.getSpreadPid());
//            }
//        }
//        LoginResponse loginResponse = new LoginResponse();
//        try {
//            String token = tokenComponent.createToken(finalUser);
//            loginResponse.setToken(token);
//        } catch (Exception e) {
//            logger.error(StrUtil.format("绑定手机号，自动登录生成token失败，uid={}", finalUser.getUid()));
//            e.printStackTrace();
//        }
//        loginResponse.setType("login");
//        loginResponse.setUid(user.getUid());
//        loginResponse.setNikeName(user.getNickname());
//        loginResponse.setPhone(user.getPhone());
//        return loginResponse;
//    }

    /**
     * 用户积分记录列表
     * @param pageParamRequest 分页参数
     * @return PageInfo<UserIntegralRecord>
     */
    @Override
    public PageInfo<UserIntegralRecord> getUserIntegralRecordList(PageParamRequest pageParamRequest) {
        Integer uid = userService.getUserIdException();
        return userIntegralRecordService.findUserIntegralRecordList(uid, pageParamRequest);
    }

//    /**
//     * 微信app登录
//     * @param request 请求参数
//     * @return 登录响应体
//     */
//    @Override
//    public LoginResponse appLogin(RegisterAppWxRequest request) {
//        //检测是否存在
//        UserToken userToken = null;
//        if (request.getType().equals(Constants.USER_LOGIN_TYPE_IOS_WX)) {
//            userToken = userTokenService.getByOpenidAndType(request.getOpenId(),  Constants.THIRD_LOGIN_TOKEN_TYPE_IOS_WX);
//        }
//        if (request.getType().equals(Constants.USER_LOGIN_TYPE_ANDROID_WX)) {
//            userToken = userTokenService.getByOpenidAndType(request.getOpenId(),  Constants.THIRD_LOGIN_TOKEN_TYPE_ANDROID_WX);
//        }
//        LoginResponse loginResponse = new LoginResponse();
//        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
//            User user = userService.getById(userToken.getUid());
//            if (!user.getStatus()) {
//                throw new CrmebException("当前账户已禁用，请联系管理员！");
//            }
//
//            // 记录最后一次登录时间
//            user.setLastLoginTime(DateUtil.nowDateTime());
//            Boolean execute = transactionTemplate.execute(e -> {
//                userService.updateById(user);
//                return Boolean.TRUE;
//            });
//            if (!execute) {
//                logger.error(StrUtil.format("APP微信登录记录最后一次登录时间失败，uid={}", user.getUid()));
//            }
//            try {
//                String token = tokenComponent.createToken(user);
//                loginResponse.setToken(token);
//            } catch (Exception e) {
//                logger.error(StrUtil.format("APP微信登录生成token失败，uid={}", user.getUid()));
//                e.printStackTrace();
//            }
//            loginResponse.setType("login");
//            loginResponse.setUid(user.getUid());
//            return loginResponse;
//        }
//        // 没有用户，走创建用户流程
//        // 从微信获取用户信息，存入Redis中，将key返回给前端，前端在下一步绑定手机号的时候下发
//        RegisterThirdUserRequest registerThirdUserRequest = new RegisterThirdUserRequest();
//        registerThirdUserRequest.setSpreadPid(0);
//        registerThirdUserRequest.setType(request.getType());
//        registerThirdUserRequest.setOpenId(request.getOpenId());
//        registerThirdUserRequest.setNickName(request.getNickName());
//        registerThirdUserRequest.setSex(request.getGender());
//        registerThirdUserRequest.setProvince(request.getProvince());
//        registerThirdUserRequest.setCity(request.getCity());
//        registerThirdUserRequest.setCountry(request.getCountry());
//        registerThirdUserRequest.setAvatar(request.getAvatarUrl());
//        String key = SecureUtil.md5(request.getOpenId());
//        redisUtil.set(key, JSONObject.toJSONString(registerThirdUserRequest), (long) (60 * 2), TimeUnit.MINUTES);
//
//        loginResponse.setType("register");
//        loginResponse.setKey(key);
//        return loginResponse;
//    }
//
//    /**
//     * 获取用户积分信息
//     * @return IntegralUserResponse
//     */
//    @Override
//    public IntegralUserResponse getIntegralUser() {
//        User user = userService.getInfoException();
//        IntegralUserResponse userSignInfoResponse = new IntegralUserResponse();
//
//        //签到
//        Integer sumIntegral = userIntegralRecordService.getSumIntegral(user.getUid(), IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD, "", null);
//        Integer deductionIntegral = userIntegralRecordService.getSumIntegral(user.getUid(), IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB, "", IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
//        userSignInfoResponse.setSumIntegral(sumIntegral);
//        userSignInfoResponse.setDeductionIntegral(deductionIntegral);
//        // 冻结积分
//        Integer frozenIntegral = userIntegralRecordService.getFrozenIntegralByUid(user.getUid());
//        userSignInfoResponse.setFrozenIntegral(frozenIntegral);
//        userSignInfoResponse.setIntegral(user.getIntegral());
//        return userSignInfoResponse;
//    }

    /**
     * 获取用户经验记录
     * @param pageParamRequest 分页参数
     * @return List<UserExperienceRecord>
     */
    @Override
    public List<UserExperienceRecord> getUserExperienceList(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();
        return userExperienceRecordService.getH5List(userId, pageParamRequest);
    }

    /**
     * 个人中心-订单头部数量
     */
    @Override
    public OrderCenterNumResponse getUserCenterOrderNum() {
        return frontOrderService.userCenterNum();
    }

    /**
     * 获取用户浏览足迹
     * @return 用户浏览足迹
     */
    @Override
    public List<UserBrowseRecordDateResponse> getUserBrowseRecord() {
        Integer userId = userService.getUserIdException();
        List<BrowseRecord> browseRecordList = browseRecordService.findAllByUid(userId);
        if (CollUtil.isEmpty(browseRecordList)) {
            return new ArrayList<>();
        }
        List<Integer> proIdList = browseRecordList.stream().map(BrowseRecord::getProductId).collect(Collectors.toList());
        Map<Integer, Product> productMap = productService.getMapByIdList(proIdList);
        Map<String, List<BrowseResponse>> map = browseRecordList.stream().map(browseRecord -> {
            BrowseResponse browseResponse = new BrowseResponse();
            BeanUtils.copyProperties(browseRecord, browseResponse);
            browseResponse.setName(productMap.get(browseRecord.getProductId()).getName());
            browseResponse.setImage(productMap.get(browseRecord.getProductId()).getImage());
            browseResponse.setIsShow(productMap.get(browseRecord.getProductId()).getIsShow());
            browseResponse.setIsDel(productMap.get(browseRecord.getProductId()).getIsDel());
            browseResponse.setPrice(productMap.get(browseRecord.getProductId()).getPrice());
            return browseResponse;
        }).collect(Collectors.groupingBy(BrowseResponse::getDate));

        List<UserBrowseRecordDateResponse> responseList = CollUtil.newArrayList();
        map.forEach((key, value) -> {
            UserBrowseRecordDateResponse response = new UserBrowseRecordDateResponse();
            response.setDate(key);
            response.setList(value);
            responseList.add(response);
        });
        return responseList.stream().sorted(Comparator.comparing(s -> DateUtil.parse(s.getDate(), "yyyy-MM-dd").getTime(), Comparator.reverseOrder())).collect(Collectors.toList());
    }

    /**
     * 我的经验
     */
    @Override
    public UserMyExpResponse getMyExp() {
        User user = userService.getInfo();
        // 会员等级列表
        List<SystemUserLevel> levelList = systemUserLevelService.getH5LevelList();
        UserMyExpResponse response = new UserMyExpResponse();
        response.setAvatar(user.getAvatar());
        response.setNickname(user.getNickname());
        response.setExperience(user.getExperience());
        response.setLevel(user.getLevel());
        response.setSystemLevelList(levelList);
        return response;
    }

//    /**
//     * 提现用户信息
//     * @return UserExtractCashResponse
//     */
//    @Override
//    public UserExtractCashResponse getExtractUser() {
//        User user = userService.getInfoException();
//        // 提现最低金额
//        String minPrice = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_EXTRACT_MIN_PRICE);
//        // 冻结天数
//        String extractTime = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_EXTRACT_FREEZING_TIME);
//        // 可提现佣金
//        BigDecimal brokeragePrice = user.getBrokeragePrice();
//        // 冻结佣金
//        BigDecimal freeze = userBrokerageRecordService.getFreezePrice(user.getUid());
//        return new UserExtractCashResponse(minPrice, brokeragePrice, freeze, extractTime);
//    }
//
//    /**
//     * 推广人列表统计
//     * @return UserSpreadPeopleResponse
//     */
//    @Override
//    public UserSpreadPeopleResponse getSpreadPeopleCount() {
//        //查询当前用户名下的一级推广员
//        UserSpreadPeopleResponse userSpreadPeopleResponse = new UserSpreadPeopleResponse();
//        List<Integer> userIdList = new ArrayList<>();
//        Integer userId = userService.getUserIdException();
//        userIdList.add(userId);
//        userIdList = userService.getSpreadPeopleIdList(userIdList); //我推广的一级用户id集合
//
//        if (CollUtil.isEmpty(userIdList)) {//如果没有一级推广人，直接返回
//            userSpreadPeopleResponse.setCount(0);
//            userSpreadPeopleResponse.setTotal(0);
//            userSpreadPeopleResponse.setTotalLevel(0);
//            return userSpreadPeopleResponse;
//        }
//
//        userSpreadPeopleResponse.setTotal(userIdList.size()); //一级推广人
//        //查询二级推广人
//        List<Integer> secondSpreadIdList = userService.getSpreadPeopleIdList(userIdList);
//        if (CollUtil.isEmpty(secondSpreadIdList)) {
//            userSpreadPeopleResponse.setTotalLevel(0);
//            userSpreadPeopleResponse.setCount(userSpreadPeopleResponse.getTotal());
//            return userSpreadPeopleResponse;
//        }
//        userSpreadPeopleResponse.setTotalLevel(secondSpreadIdList.size());
//        userSpreadPeopleResponse.setCount(userIdList.size() + secondSpreadIdList.size());
//        return userSpreadPeopleResponse;
//    }
//
//    /**
//     * 支付宝充值
//     */
//    @Override
//    public OrderPayResultResponse aliPayRecharge(UserRechargeRequest request) {
//        request.setPayType(Constants.PAY_TYPE_ALI_PAY);
//
//        //验证金额是否为最低金额
//        String rechargeMinAmountStr = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_RECHARGE_MIN_AMOUNT);
//        BigDecimal rechargeMinAmount = new BigDecimal(rechargeMinAmountStr);
//        int compareResult = rechargeMinAmount.compareTo(request.getPrice());
//        if (compareResult > 0) {
//            throw new CrmebException("充值金额不能低于" + rechargeMinAmountStr);
//        }
//
//        request.setGivePrice(BigDecimal.ZERO);
//
//        if (request.getGroupDataId() > 0) {
//            SystemGroupDataRechargeConfigVo systemGroupData = systemGroupDataService.getNormalInfo(request.getGroupDataId(), SystemGroupDataRechargeConfigVo.class);
//            if (ObjectUtil.isNull(systemGroupData)) {
//                throw new CrmebException("您选择的充值方式已下架");
//            }
//            //售价和赠送
//            request.setPrice(systemGroupData.getPrice());
//            request.setGivePrice(systemGroupData.getGiveMoney());
//
//        }
//        User currentUser = userService.getInfoException();
//        //生成系统订单
//        UserRecharge userRecharge = new UserRecharge();
//        userRecharge.setUid(currentUser.getUid());
//        userRecharge.setOrderId(CrmebUtil.getOrderNo("recharge"));
//        userRecharge.setPrice(request.getPrice());
//        userRecharge.setGivePrice(request.getGivePrice());
//        userRecharge.setRechargeType(request.getFromType());
//        boolean save = userRechargeService.save(userRecharge);
//        if (!save) {
//            throw new CrmebException("生成充值订单失败!");
//        }
//
//        //获得初始化的AlipayClient
//        String aliPayAppid = systemConfigService.getValueByKey(AlipayConfig.APPID);
//        String aliPayPrivateKey = systemConfigService.getValueByKey(AlipayConfig.RSA_PRIVATE_KEY);
//        String aliPayPublicKey = systemConfigService.getValueByKey(AlipayConfig.ALIPAY_PUBLIC_KEY);
//        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey, AlipayConfig.SIGNTYPE);
//
//        OrderPayResultResponse response = new OrderPayResultResponse();
//        //商户订单号，商户网站订单系统中唯一订单号，必填
//        String out_trade_no = userRecharge.getOrderId();
//        //付款金额，必填
//        String total_amount = userRecharge.getPrice().toString();
//        //订单名称，必填
//        String subject = "crmeb商城订单";
//        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
//        String timeout_express = "30m";
//
//        if (request.getFromType().equals("appAliPay")) {
//            //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
//            AlipayTradeAppPayRequest payRequest = new AlipayTradeAppPayRequest();
//            //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
//            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
//            model.setSubject(subject);
//            model.setOutTradeNo(out_trade_no);
//            model.setTimeoutExpress(timeout_express);
//            model.setTotalAmount(total_amount);
//            model.setProductCode("QUICK_MSECURITY_PAY");
//
////            HashMap<String, String> map = CollUtil.newHashMap();
////            map.put("type", Constants.SERVICE_PAY_TYPE_RECHARGE);
////            String jsonString = JSONObject.toJSONString(map);
////            String encode;
//            String encode = "type=" + Constants.SERVICE_PAY_TYPE_RECHARGE;
//            try {
//                encode = URLEncoder.encode(encode, "utf-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//                throw new CrmebException("支付宝参数UrlEncode异常");
//            }
//            model.setPassbackParams(encode);
//
//            payRequest.setBizModel(model);
//            payRequest.setNotifyUrl(systemConfigService.getValueByKey(AlipayConfig.notify_url));
//
//            //请求
//            String result;
//            try {
//                //这里和普通的接口调用不同，使用的是sdkExecute
//                AlipayTradeAppPayResponse aaa = alipayClient.sdkExecute(payRequest);
//                result = aaa.getBody();
//            } catch (AlipayApiException e) {
//                logger.error("生成支付宝app支付请求异常," + e.getErrMsg());
//                throw new CrmebException(e.getErrMsg());
//            }
//            logger.info("支付宝app result = " + result);
//            response.setAlipayRequest(result);
//            response.setOrderNo(userRecharge.getOrderId());
//            return response;
//        }
//
//        //设置请求参数
//        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
//        alipayRequest.setReturnUrl(systemConfigService.getValueByKey(AlipayConfig.recharge_return_url));
//        alipayRequest.setNotifyUrl(systemConfigService.getValueByKey(AlipayConfig.notify_url));
//
//        AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
//        model.setOutTradeNo(out_trade_no);
//        model.setSubject(subject);
//        model.setTotalAmount(total_amount);
////            model.setBody(body);
//        model.setTimeoutExpress(timeout_express);
//        model.setProductCode("QUICK_WAP_PAY");
//        model.setQuitUrl(systemConfigService.getValueByKey(AlipayConfig.recharge_quit_url));
//
////        HashMap<String, String> map = CollUtil.newHashMap();
////        map.put("type", Constants.SERVICE_PAY_TYPE_RECHARGE);
////        String jsonString = JSONObject.toJSONString(map);
////        String encode;
//        String encode = "type=" + Constants.SERVICE_PAY_TYPE_RECHARGE;
//        try {
//            encode = URLEncoder.encode(encode, "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            throw new CrmebException("支付宝参数UrlEncode异常");
//        }
//        model.setPassbackParams(encode);
//        alipayRequest.setBizModel(model);
//
//        logger.info("alipayRequest = " + alipayRequest);
//        //请求
//        String result = null;
//        try {
//            result = alipayClient.pageExecute(alipayRequest).getBody();
//        } catch (AlipayApiException e) {
//            logger.error("支付宝订单生成失败," + e.getErrMsg());
//            throw new CrmebException(e.getErrMsg());
//        }
//        logger.info("result = " + result);
//        response.setAlipayRequest(result);
//        response.setOrderNo(userRecharge.getOrderId());
//        return response;
//    }
//
//    /**
//     * 绑定手机号数据校验
//     */
//    private void checkBindingPhone(WxBindingPhoneRequest request) {
//        if (!request.getType().equals("public") && !request.getType().equals("routine") && !request.getType().equals("iosWx") && !request.getType().equals("androidWx")) {
//            throw new CrmebException("未知的用户类型");
//        }
//        if (request.getType().equals("public") || request.getType().equals("iosWx") || request.getType().equals("androidWx")) {
//            if (StrUtil.isBlank(request.getCaptcha())) {
//                throw new CrmebException("验证码不能为空");
//            }
//            boolean matchPhone = ReUtil.isMatch(RegularConstants.PHONE_TWO, request.getPhone());
//            if (!matchPhone) {
//                throw new CrmebException("手机号格式错误，请输入正确得手机号");
//            }
//            // 公众号用户校验验证码
//            boolean match = ReUtil.isMatch(RegularConstants.VALIDATE_CODE_NUM_SIX, request.getCaptcha());
//            if (!match) {
//                throw new CrmebException("验证码格式错误，验证码必须为6位数字");
//            }
//            checkValidateCode(request.getPhone(), request.getCaptcha());
//        } else {
//            // 参数校验
//            if (StrUtil.isBlank(request.getCode())) {
//                throw new CrmebException("小程序获取手机号code不能为空");
//            }
//            if (StrUtil.isBlank(request.getEncryptedData())) {
//                throw new CrmebException("小程序获取手机号加密数据不能为空");
//            }
//            if (StrUtil.isBlank(request.getIv())) {
//                throw new CrmebException("小程序获取手机号加密算法的初始向量不能为空");
//            }
//            // 获取appid
//            String programAppId = systemConfigService.getValueByKey(WeChatConstants.WECHAT_MINI_APPID);
//            if (StrUtil.isBlank(programAppId)) {
//                throw new CrmebException("微信小程序appId未设置");
//            }
//
//            WeChatMiniAuthorizeVo response = wechatNewService.miniAuthCode(request.getCode());
////            WeChatMiniAuthorizeVo response = weChatService.programAuthorizeLogin(request.getCode());
//            System.out.println("小程序登陆成功 = " + JSON.toJSONString(response));
//            String decrypt = WxUtil.decrypt(programAppId, request.getEncryptedData(), response.getSessionKey(), request.getIv());
//            if (StrUtil.isBlank(decrypt)) {
//                throw new CrmebException("微信小程序获取手机号解密失败");
//            }
//            JSONObject jsonObject = JSONObject.parseObject(decrypt);
//            if (StrUtil.isBlank(jsonObject.getString("phoneNumber"))) {
//                throw new CrmebException("微信小程序获取手机号没有有效的手机号");
//            }
//            request.setPhone(jsonObject.getString("phoneNumber"));
//        }
//    }
//
//    /**
//     * 赠送新人券
//     * @param uid 用户uid
//     */
//    private void giveNewPeopleCoupon(Integer uid) {
//        // 查询是否有新人注册赠送优惠券
//        List<StoreCouponUser> couponUserList = CollUtil.newArrayList();
//        List<StoreCoupon> couponList = storeCouponService.findRegisterList();
//        if (CollUtil.isNotEmpty(couponList)) {
//            couponList.forEach(storeCoupon -> {
//                //是否有固定的使用时间
//                if (!storeCoupon.getIsFixedTime()) {
//                    String endTime = DateUtil.addDay(DateUtil.nowDate(DateConstants.DATE_FORMAT), storeCoupon.getDay(), DateConstants.DATE_FORMAT);
//                    storeCoupon.setUseEndTime(DateUtil.strToDate(endTime, DateConstants.DATE_FORMAT));
//                    storeCoupon.setUseStartTime(DateUtil.nowDateTimeReturnDate(DateConstants.DATE_FORMAT));
//                }
//
//                StoreCouponUser storeCouponUser = new StoreCouponUser();
//                storeCouponUser.setCouponId(storeCoupon.getId());
//                storeCouponUser.setName(storeCoupon.getName());
//                storeCouponUser.setMoney(storeCoupon.getMoney());
//                storeCouponUser.setMinPrice(storeCoupon.getMinPrice());
//                storeCouponUser.setUseType(storeCoupon.getUseType());
//                if (storeCoupon.getIsFixedTime()) {// 使用固定时间
//                    storeCouponUser.setStartTime(storeCoupon.getUseStartTime());
//                    storeCouponUser.setEndTime(storeCoupon.getUseEndTime());
//                } else {// 没有固定使用时间
//                    Date nowDate = DateUtil.nowDateTime();
//                    storeCouponUser.setStartTime(nowDate);
//                    DateTime dateTime = cn.hutool.core.date.DateUtil.offsetDay(nowDate, storeCoupon.getDay());
//                    storeCouponUser.setEndTime(dateTime);
//                }
//                storeCouponUser.setType("register");
//                if (storeCoupon.getUseType() > 1) {
//                    storeCouponUser.setPrimaryKey(storeCoupon.getPrimaryKey());
//                }
//                storeCouponUser.setType(CouponConstants.STORE_COUPON_USER_TYPE_REGISTER);
//                couponUserList.add(storeCouponUser);
//            });
//        }
//
//        // 赠送客户优惠券
//        if (CollUtil.isNotEmpty(couponUserList)) {
//            couponUserList.forEach(couponUser -> couponUser.setUid(uid));
//            storeCouponUserService.saveBatch(couponUserList);
//            couponList.forEach(coupon -> storeCouponService.deduction(coupon.getId(), 1, coupon.getIsLimited()));
//        }
//    }
//
//    /**
//     * 检测手机验证码
//     * @param phone 手机号
//     * @param code 验证码
//     */
//    private void checkValidateCode(String phone, String code) {
//        Object validateCode = redisUtil.get(SmsConstants.SMS_VALIDATE_PHONE + phone);
//        if (validateCode == null) {
//            throw new CrmebException("验证码已过期");
//        }
//        if (!validateCode.toString().equals(code)) {
//            throw new CrmebException("验证码错误");
//        }
//        //删除验证码
//        redisUtil.delete(SmsConstants.SMS_VALIDATE_PHONE + phone);
//    }
}
