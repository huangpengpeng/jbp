package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jbp.common.constants.*;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.bill.Bill;
import com.jbp.common.model.bill.UserBill;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderExt;
import com.jbp.common.model.order.OrderRegister;
import com.jbp.common.model.system.SystemUserLevel;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserBalanceRecord;
import com.jbp.common.model.user.UserIntegralRecord;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.request.merchant.MerchantUserSearchRequest;
import com.jbp.common.response.*;
import com.jbp.common.token.FrontTokenComponent;
import com.jbp.common.utils.*;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.common.vo.DeclUserInfoResultVo;
import com.jbp.common.vo.MyRecord;
import com.jbp.service.dao.UserDao;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 用户表 服务实现类
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
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserDao dao;
    @Autowired
    private FrontTokenComponent tokenComponent;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;
    @Autowired
    private UserBillService userBillService;
    @Autowired
    private BillService billService;
    @Autowired
    private UserBalanceRecordService userBalanceRecordService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private UserMerchantCollectService userMerchantCollectService;
    @Autowired
    private CommunityNotesService communityNotesService;
    @Autowired
    private CommunityNotesRelationService communityNotesRelationService;
    @Autowired
    private SystemUserLevelService systemUserLevelService;
    @Resource
    private CapaService capaService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private CapaXsService capaXsService;
    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private TeamService teamService;
    @Resource
    private TeamUserService teamUserService;
    @Resource
    private UserInvitationService invitationService;
    @Resource
    private UserRelationService relationService;
    @Resource
    private OrderExtService orderExtService;
    @Resource
    private UserService userService;
    @Resource
    private WalletConfigService walletConfigService;
    @Resource
    private RelationScoreService relationScoreService;
    @Resource
    private PlatformWalletService platformWalletService;
    @Resource
    private ChannelIdentityService channelIdentityService;
    @Resource
    private UserCapaXsSnapshotService userCapaXsSnapshotService;

    public static void main(String[] args) {

    }
    /**
     * 手机号注册用户
     *
     * @param phone     手机号
     * @param spreadUid 推广人编号
     * @return User
     */
    @Override
    public User registerPhone(String username, String phone, Integer spreadUid) {
        User user = new User();
        user.setAccount(getAccount());
        user.setPwd(CommonUtil.createPwd(phone));
        if (isUnique4Phone() && CollectionUtils.isNotEmpty(getByPhone(phone))) {
            throw new CrmebException("手机号重复");
        }
        user.setPhone(phone);
        user.setRegisterType(UserConstants.REGISTER_TYPE_H5);
        user.setNickname(com.jbp.common.utils.StringUtils.filterEmoji(username));
        user.setAvatar(systemConfigService.getValueByKey(SysConfigConstants.USER_DEFAULT_AVATAR_CONFIG_KEY));
        Date nowDate = CrmebDateUtil.nowDateTime();
        user.setCreateTime(nowDate);
        user.setLastLoginTime(nowDate);

         String mobileDefaultPwd = systemConfigService.getValueByKey(SysConfigConstants.MOBILE_DEFAULT_PWD);

         if(!org.apache.commons.lang3.StringUtils.equals(Constants.CONFIG_FORM_SWITCH_OPEN,mobileDefaultPwd)){
             user.setPwd(CrmebUtil.encryptPassword("123456"));
             if(!ObjectUtil.isNull(phone)){
                 user.setPayPwd(CrmebUtil.encryptPassword(phone.substring(phone.length() - 6)));
             }
         }


        user.setLevel(1);
        // 设置活跃时间
        setActiveTime(user);
        // 推广人
        user.setSpreadUid(0);
        if (spreadUid != null && spreadUid > 0) {
            Boolean check = checkBingSpread(user, spreadUid, "new");
            if (check) {
                user.setSpreadUid(spreadUid);
                user.setSpreadTime(nowDate);
            }
        }
        Boolean execute = transactionTemplate.execute(e -> {
            save(user);
            // 推广人处理
            if (user.getSpreadUid() > 0) {
                updateSpreadCountByUid(spreadUid, Constants.OPERATION_TYPE_ADD);
            }
            // 增加代理等级
            userCapaService.saveOrUpdateCapa(user.getId(), capaService.getMinCapa().getId(), "", "手机号验证码注册");
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("创建用户失败!");
        }
        //绑定用户账号
        if (spreadUid != null && spreadUid > 0) {

            String ifOpen =  systemConfigService.getValueByKey("ifOpen");
            String capaId =  systemConfigService.getValueByKey("capaId");
            //邀请配置 配置关闭时默认强绑定
             invitationService.band(user.getId(), spreadUid, false, ifOpen.equals("2")?true: Long.valueOf(capaId).equals(capaService.getMinCapa().getId()), false);

        }
        return user;
    }

    @Override
    public User registerNoBandPater(String username, String phone, String remark, Long capaId) {
        User user = new User();
        user.setAccount(getAccount().toUpperCase());
        user.setPwd(CommonUtil.createPwd(phone));
        if (isUnique4Phone() && CollectionUtils.isNotEmpty(getByPhone(phone))) {
            throw new CrmebException("手机号重复");
        }
        user.setPhone(phone);
        user.setRegisterType(UserConstants.REGISTER_TYPE_H5);
        user.setNickname(com.jbp.common.utils.StringUtils.filterEmoji(username));
        user.setAvatar(systemConfigService.getValueByKey(SysConfigConstants.USER_DEFAULT_AVATAR_CONFIG_KEY));
        Date nowDate = CrmebDateUtil.nowDateTime();
        user.setCreateTime(nowDate);
        user.setLastLoginTime(nowDate);
        user.setPwd(CrmebUtil.encryptPassword("123456"));
        if (!ObjectUtil.isNull(phone)) {
            user.setPayPwd(CrmebUtil.encryptPassword(phone.substring(phone.length() - 6)));
        }
        user.setLevel(1);
        // 设置活跃时间
        setActiveTime(user);
        // 推广人
        user.setSpreadUid(0);
        Boolean execute = transactionTemplate.execute(e -> {
            save(user);
            // 增加代理等级
            if(capaId == null){
                userCapaService.saveOrUpdateCapa(user.getId(), capaService.getMinCapa().getId(), remark, remark);
            }else{
                userCapaService.saveOrUpdateCapa(user.getId(), capaId, remark, remark);
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("创建用户失败!");
        }
        return user;
    }

    @Override
    public void registerPhone(String username, String phone, String account, UserCapaTemplateRequest userCapaTemplateRequest, String regionPAccount, Integer regionPNode, String invitationPAccount, String pwd) {
        User user = new User();
        user.setAccount(account.toUpperCase());
        user.setPwd(CrmebUtil.encryptPassword(pwd));
        if (isUnique4Phone() && CollectionUtils.isNotEmpty(getByPhone(phone))) {
            throw new CrmebException("手机号重复");
        }
        user.setPhone(phone);
        user.setRegisterType(UserConstants.REGISTER_TYPE_H5);
        user.setNickname(com.jbp.common.utils.StringUtils.filterEmoji(username));
        user.setAvatar(systemConfigService.getValueByKey(SysConfigConstants.USER_DEFAULT_AVATAR_CONFIG_KEY));
        Date nowDate = CrmebDateUtil.nowDateTime();
        user.setCreateTime(nowDate);
        user.setLastLoginTime(nowDate);
        user.setLevel(1);
        // 设置活跃时间
        setActiveTime(user);
        // 推广人
        user.setSpreadUid(0);

        Boolean execute = transactionTemplate.execute(e -> {
            save(user);
            //设置服务上级
            if (ObjectUtils.isNotEmpty(regionPAccount)) {
                User regionPid = userService.getByAccount(regionPAccount);
                if (regionPid == null) {
                    throw new CrmebException("上级账户不存在");
                }
                relationService.band(user.getId(), regionPid.getId(), null, regionPNode);

            }
//        设置邀请上级账号
            if (ObjectUtils.isNotEmpty(invitationPAccount)) {
                Integer pid = null;
                if (com.jbp.service.util.StringUtils.isNotEmpty(invitationPAccount)) {
                    User invitationPid = userService.getByAccount(invitationPAccount);
                    if (invitationPid == null) {
                        throw new CrmebException("邀请上级账号信息错误");
                    }
                    pid = invitationPid.getId();
                }
                invitationService.band(user.getId(), pid, false, true, false);
            }
            if (userCapaTemplateRequest != null && userCapaTemplateRequest.getCapaId() != 0) {
                userCapaService.saveOrUpdateCapa(user.getId(), userCapaTemplateRequest.getCapaId(),
                        userCapaTemplateRequest.getRemark(), userCapaTemplateRequest.getDescription());
            } else {
                userCapaService.saveOrUpdateCapa(user.getId(), capaService.getMinCapa().getId(), "", "手机号验证码注册");
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("创建用户失败!");
        }
    }

    @Override
    public void setActiveTime(User user) {
        // 注册新增活跃
        List<String> activeKeyList = Lists.newArrayList(SysConfigConstants.CONFIG_REGISTER_ACTIVE_OPEN,
                SysConfigConstants.CONFIG_REGISTER_ACTIVE_TYPE,
                SysConfigConstants.CONFIG_REGISTER_ACTIVE_VALUE);
        MyRecord myRecord = systemConfigService.getValuesByKeyList(activeKeyList);

        int ifOpen = myRecord.getInt(SysConfigConstants.CONFIG_REGISTER_ACTIVE_OPEN);
        if (1 == ifOpen) {
            user.setActiveTime(DateTimeUtils.getNow());
        } else {
            String activeType = myRecord.getStr(SysConfigConstants.CONFIG_REGISTER_ACTIVE_TYPE);
            Integer activeValue = myRecord.getInt(SysConfigConstants.CONFIG_REGISTER_ACTIVE_VALUE);
            activeValue = activeValue - 1;
            if ("month".equals(activeType)) {
                user.setActiveTime(DateTimeUtils.getMonthEnd(DateTimeUtils.getNow()));
                if (activeValue > 0) {
                    user.setActiveTime(DateTimeUtils.getMonthEnd(DateTimeUtils.addMonths(DateTimeUtils.getNow(), activeValue)));
                }
            } else {
                user.setActiveTime(DateTimeUtils.getFinallyDate(DateTimeUtils.getNow()));
                if (activeValue > 0) {
                    user.setActiveTime(DateTimeUtils.getFinallyDate(DateTimeUtils.addDays(DateTimeUtils.getNow(), activeValue)));
                }
            }
        }
    }

    @Override
    public HelpRegisterResponse helpRegisterValid(String username, String phone, String pAccount,
                                                  String rAccount, Integer node, Long userLevel) {
        if (com.jbp.common.utils.StringUtils.isAnyBlank(username, phone, pAccount, rAccount)) {
            throw new CrmebException("注册信息不能为空");
        }
        if (node == null || (node != 0 && node != 1)) {
            throw new CrmebException("注册节点错误");
        }
        if (userLevel == null) {
            throw new CrmebException("注册等级不能为空");
        }
        User pUser = getByAccount(pAccount);
        if (pUser == null) {
            throw new CrmebException("邀请账号错误");
        }
        User rUser = getByAccount(rAccount);
        if (rUser == null) {
            throw new CrmebException("服务账号错误");
        }
        if (relationService.getByPid(rUser.getId(), node) != null) {
            throw new CrmebException("服务节点被占用");
        }
        if (capaService.getById(userLevel) == null) {
            throw new CrmebException("等级编号错误");
        }
        return new HelpRegisterResponse(pUser.getId(), rUser.getId(), node);
    }

    @Override
    public User helpRegister(String username, String phone, Integer pid, Integer rId, Integer node) {
        // 开启事务
        AtomicReference<User> result = new AtomicReference<>();
        Boolean execute = transactionTemplate.execute(e -> {
            // 注册用户
            User user = registerPhone(username, phone, 0);
            // 绑定上级
            invitationService.band(user.getId(), pid, false, true, false);
            // 绑定节点
            relationService.band(user.getId(), rId, null, node);
            result.set(user);
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("创建用户失败!");
        }
        return result.get();
    }

    /**
     * 根据手机号查询用户
     *
     * @param phone 用户手机号
     * @return 用户信息
     */
    @Override
    public List<User> getByPhone(String phone) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getPhone, phone);
        lqw.eq(User::getIsLogoff, 0);
        return dao.selectList(lqw);
    }

    @Override
    public User getByAccount(String account) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getAccount, account);
        return getOne(lqw);
    }

    @Override
    public String getAccount() {
        String accountPrefix = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_REGISTER_ACCOUNT_PREFIX);
        String accountNum = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_REGISTER_ACCOUNT_NUM);
        String account = StringUtils.EMPTY;
        do {
            account = CrmebUtil.getAccount(accountPrefix, accountNum); // 默认是A 开头 8为数字
            if (getByAccount(account) == null) {
                return account;
            }
        } while (true);
    }

    @Override
    public boolean isUnique4Phone() {
        String value = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_REGISTER_PHONE_IS_UNIQUE);
        return StringUtils.isNotBlank(value) && value.equals("'true'");
    }

    /**
     * 检测能否绑定关系
     *
     * @param user      当前用户
     * @param spreadUid 推广员Uid
     * @param type      用户类型:new-新用户，old—老用户
     * @return Boolean
     * 1.判断分销功能是否启用
     * 2.判断分销模式
     * 3.根据不同的分销模式校验
     * 4.指定分销，只有分销员才可以分销，需要spreadUid是推广员才可以绑定
     * 5.人人分销，可以直接绑定
     * *推广关系绑定，下级不能绑定自己的上级为下级，A->B->A(❌)
     */
    public Boolean checkBingSpread(User user, Integer spreadUid, String type) {
        if (ObjectUtil.isNull(spreadUid) || spreadUid <= 0) {
            return false;
        }
        if (ObjectUtil.isNull(user)) {
            return false;
        }
        if (user.getSpreadUid() > 0) {
            return false;
        }
        if (ObjectUtil.isNotNull(user.getId()) && user.getId().equals(spreadUid)) {
            return false;
        }
        // 判断分销功能是否启用
        String isOpen = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_SWITCH);
        if (StrUtil.isBlank(isOpen) || isOpen.equals("0")) {
            return false;
        }
        if (type.equals("old")) {
            // 判断分销关系绑定类型（所有、新用户）
            String bindType = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_BINDING_TYPE);
            if (StrUtil.isBlank(bindType) || bindType.equals("1")) {
                return false;
            }
        }
        // 查询推广员
        User spreadUser = getById(spreadUid);
        if (ObjectUtil.isNull(spreadUser) || !spreadUser.getStatus()) {
            return false;
        }
        // 指定分销不是推广员不绑定
        if (!spreadUser.getIsPromoter()) {
            return false;
        }
        // 下级不能绑定自己的上级为自己的下级
        return !ObjectUtil.isNotNull(user.getId()) || !user.getId().equals(spreadUser.getSpreadUid());
    }

    /**
     * 更新推广员推广数
     *
     * @param uid  uid
     * @param type add or sub
     */
    public Boolean updateSpreadCountByUid(Integer uid, String type) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            updateWrapper.setSql("spread_count = spread_count + 1");
        } else {
            updateWrapper.setSql("spread_count = spread_count - 1");
        }
        updateWrapper.eq("id", uid);
        return update(updateWrapper);
    }

    /**
     * 修改密码
     *
     * @param request PasswordRequest 密码
     * @return boolean
     */
    @Override
    public Boolean password(PasswordRequest request) {
        User user = getInfo();
        //检测验证码
        checkValidateCode(user.getPhone(), request.getValidateCode());
        //密码
        user.setPwd(CrmebUtil.encryptPassword(request.getPassword()));
        return updateById(user);
    }

    /**
     * 获取当前用户ID，带异常
     */
    @Override
    public Integer getUserIdException() {
        Integer id = tokenComponent.getUserId();
        if (null == id) {
            throw new CrmebException("登录信息已过期，请重新登录！");
        }
        return id;
    }

    /**
     * 获取当前用户id
     */
    @Override
    public Integer getUserId() {
        Integer id = tokenComponent.getUserId();
        if (null == id) {
            return 0;
        }
        return id;
    }

    /**
     * 获取个人资料
     */
    @Override
    public User getInfo() {
        Integer userId = getUserIdException();
        User user = getById(userId);
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("登录信息已过期，请重新登录！");
        }
        if (user.getIsLogoff()) {
            throw new CrmebException("登录信息已过期，请重新登录！");
        }
        return user;
    }

    /**
     * 移动端当前用户信息
     *
     * @return UserInfoResponse
     */
    @Override
    public UserInfoResponse getUserInfo() {
        User currentUser = getInfo();
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        BeanUtils.copyProperties(currentUser, userInfoResponse);
        userInfoResponse.setPhone(CrmebUtil.maskMobile(userInfoResponse.getPhone()));
        UserCapa userCapa = userCapaService.getByUser(currentUser.getId());
        if (userCapa != null) {
            userInfoResponse.setCapa(capaService.getById(userCapa.getCapaId()));
        }
        UserCapaXs userCapaXs = userCapaXsService.getByUser(currentUser.getId());
        if (userCapaXs != null) {
            userInfoResponse.setCapaXs(capaXsService.getById(userCapaXs.getCapaId()));
        }
        return userInfoResponse;
    }

    /**
     * 修改个人信息
     *
     * @param request 修改信息
     */
    @Override
    public Boolean editUser(UserEditInfoRequest request) {
        User user = getInfo();
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(User::getNickname, request.getNickname());
        wrapper.set(User::getAvatar, systemAttachmentService.clearPrefix(request.getAvatar()));
        wrapper.set(User::getProvince, request.getProvince());
        wrapper.set(User::getCity, request.getCity());
        wrapper.set(User::getSex, request.getSex());
        wrapper.set(User::getBirthday, request.getBirthday());
        wrapper.eq(User::getId, user.getId());
        return update(wrapper);
    }

    /**
     * 获取当前用户手机号验证码
     */
    @Override
    public Boolean getCurrentPhoneCode() {
        User user = getInfo();
        return smsService.sendCommonCode(user.getPhone());
    }

    /**
     * 换绑手机号获取验证码
     *
     * @param request 请求参数
     *                captcha 为用户原手机号验证码
     */
    @Override
    public Boolean updatePhoneCode(UserBindingPhoneUpdateRequest request) {
        // 校验老手机号验证码
        User user = getInfo();
        checkValidateCode(user.getPhone(), request.getCaptcha());
        List<User> tempUsers = getByPhone(request.getPhone());
        if (isUnique4Phone() && CollectionUtils.isNotEmpty(tempUsers)) {
            List<User> collect = tempUsers.stream().filter(u -> u.getId() != user.getId()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(collect)) {
                throw new CrmebException("手机号已被占用");
            }
        }
        // 新手机发送验证码
        return smsService.sendCommonCode(request.getPhone());
    }

    /**
     * 换绑手机号
     *
     * @param request 请求参数
     */
    @Override
    public Boolean updatePhone(UserBindingPhoneUpdateRequest request) {
        String changePhoneNosign = systemConfigService.getValueByKey(SysConfigConstants.CHANGE_PHONE_NOSIGN);
        if(!"'true'".equals(changePhoneNosign)) {
            checkValidateCode(request.getPhone(), request.getCaptcha());
        }
        User user = getInfo();
        if (request.getPhone().equals(user.getPhone())) {
            return true;
        }
        // 如果手机号唯一
        List<User> tempUsers = getByPhone(request.getPhone());
        if (isUnique4Phone() && CollectionUtils.isNotEmpty(tempUsers)) {
            List<User> collect = tempUsers.stream().filter(u -> u.getId() != user.getId()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(collect)) {
                throw new CrmebException("手机号已被占用");
            }
        }
//        String password = "";
//        try {
//            password = CrmebUtil.decryptPassowrd(user.getPwd(), user.getPhone());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(User::getPhone, request.getPhone());
     //   wrapper.set(User::getPwd, CrmebUtil.encryptPassword(user.getPwd()));
        wrapper.eq(User::getId, user.getId());
        return update(wrapper);
    }

    /**
     * 分页显示用户表
     *
     * @param request          搜索条件
     * @param pageParamRequest 分页参数
     */
    @Override
    public PageInfo<UserResponse> getPlatformPage(UserSearchRequest request, PageParamRequest pageParamRequest) {
        Page<User> pageUser = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        Map<String, Object> map = CollUtil.newHashMap();
        if (StrUtil.isNotEmpty(request.getAccount())) {
            map.put("account", request.getAccount());
        }
        if (request.getOpenShop() != null) {
            map.put("openShop", request.getOpenShop());
        }
        if (StrUtil.isNotEmpty(request.getSpreadAccount())) {
            User user = userService.getByAccount(request.getSpreadAccount());
            if (user == null) {
                throw new CrmebException("推荐人用户账号信息错误");
            }
            map.put("spreadid", user.getId());
        }
        if (ObjectUtils.isNotEmpty(request.getUid())) {
            map.put("uid",request.getUid());
        }
        if (StrUtil.isNotEmpty(request.getNikename())) {
            String nikeName = URLUtil.decode(request.getNikename());
            map.put("nikename", nikeName);
        }
        if (StrUtil.isNotEmpty(request.getPhone())) {
            map.put("phone", request.getPhone());
        }
        if (StrUtil.isNotBlank(request.getTagIds())) {
            String tagIdSql = CrmebUtil.getFindInSetSql("u.tag_id", request.getTagIds());
            map.put("tagIdSql", tagIdSql);
        }
        if (ObjectUtil.isNotNull(request.getSex())) {
            map.put("sex", request.getSex());
        }
        if (StrUtil.isNotBlank(request.getRegisterType())) {
            map.put("registerType", request.getRegisterType());
        }
        if (StrUtil.isNotBlank(request.getPayCount())) {
            map.put("payCount", Integer.valueOf(request.getPayCount()));
        }
        if (ObjectUtil.isNotNull(request.getIsPromoter())) {
            map.put("isPromoter", request.getIsPromoter().equals(1) ? 1 : 0);
        }
        if (ObjectUtil.isNotNull(request.getIsLogoff())) {
            map.put("isLogoff", request.getIsLogoff());
        }
        DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
        if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
            map.put("startTime", dateLimit.getStartTime());
            map.put("endTime", dateLimit.getEndTime());
            map.put("accessType", request.getAccessType());
        }
        List<User> userList = dao.findAdminList(map);
        List<UserResponse> userResponses = new ArrayList<>();
        if (CollUtil.isEmpty(userList)) {
            return CommonPage.copyPageInfo(pageUser, userResponses);
        }
        List<Integer> spreadUidList = userList.stream().filter(e -> e.getSpreadUid() > 0).map(User::getSpreadUid).distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = CollUtil.newHashMap();
        if (CollUtil.isNotEmpty(spreadUidList)) {
            userMap = getUidMapList(spreadUidList);
        }
        for (User user : userList) {
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(user, userResponse);
            if (StrUtil.isNotEmpty(userResponse.getPhone())) {
                userResponse.setPhone(CrmebUtil.maskMobile(userResponse.getPhone()));
            }
            if (user.getSpreadUid() > 0) {
                userResponse.setSpreadName(userMap.get(user.getSpreadUid()).getNickname());
            }
            //获取等级名称
            UserCapa userCapa = userCapaService.getByUser(user.getId());
            if (!ObjectUtil.isNull(userCapa)) {
                Capa capa = capaService.getById(userCapa.getCapaId());
                userResponse.setCapaName(capa != null ? capa.getName() : "");
            }
            //获取星级名称
            UserCapaXs userCapaXs = userCapaXsService.getByUser(user.getId());
            if (!ObjectUtil.isNull(userCapaXs)) {
                CapaXs capaXs = capaXsService.getById(userCapaXs.getCapaId());
                userResponse.setCapaXsName(capaXs != null ? capaXs.getName() : "");
            }
            //获取团队名称
            TeamUser teamUser = teamUserService.getByUser(user.getId());
            if (!ObjectUtil.isNull(teamUser)) {
                Team team = teamService.getById(teamUser.getTid());
                userResponse.setTeamName(team != null ? team.getName() : "");
            }
            userResponses.add(userResponse);
        }
        return CommonPage.copyPageInfo(pageUser, userResponses);
    }

    /**
     * 商户端用户分页列表
     *
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserResponse> getMerchantPage(MerchantUserSearchRequest request, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Page<User> pageUser = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("merId", systemAdmin.getMerId());
        if (StrUtil.isNotEmpty(request.getNikename())) {
            String nikeName = URLUtil.decode(request.getNikename());
            map.put("nikename", nikeName);
        }
        if (StrUtil.isNotEmpty(request.getPhone())) {
            map.put("phone", request.getPhone());
        }
        if (StrUtil.isNotBlank(request.getRegisterType())) {
            map.put("registerType", request.getRegisterType());
        }
        if (ObjectUtil.isNotNull(request.getSex())) {
            map.put("sex", request.getSex());
        }
        DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
        if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
            map.put("startTime", dateLimit.getStartTime());
            map.put("endTime", dateLimit.getEndTime());
        }
        List<User> userList = dao.findMerchantList(map);
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : userList) {
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(user, userResponse);
            userResponse.setPhone(CrmebUtil.maskMobile(userResponse.getPhone()));
            userResponses.add(userResponse);
        }
        return CommonPage.copyPageInfo(pageUser, userResponses);
    }

    /**
     * 更新用户
     *
     * @param userRequest 用户参数
     * @return Boolean
     */
    @Override
    public Boolean updateUser(UserUpdateRequest userRequest) {
        User tempUser = getById(userRequest.getId());
        if (ObjectUtil.isNull(tempUser)) {
            throw new CrmebException("用户不存在");
        }
        if (tempUser.getIsLogoff()) {
            throw new CrmebException("已注销用户不可操作");
        }
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        if (StrUtil.isNotBlank(userRequest.getTagId())) {
            wrapper.set(User::getTagId, userRequest.getTagId());
        } else {
            wrapper.set(User::getTagId, "");
        }
        if (StrUtil.isNotBlank(userRequest.getBirthday())) {
            wrapper.set(User::getBirthday, userRequest.getBirthday());
        }
        if (StrUtil.isNotBlank(userRequest.getProvince())) {
            wrapper.set(User::getProvince, userRequest.getProvince());
        }
        if (StrUtil.isNotBlank(userRequest.getCity())) {
            wrapper.set(User::getCity, userRequest.getCity());
        }
        if (StrUtil.isNotBlank(userRequest.getMark())) {
            wrapper.set(User::getMark, userRequest.getMark());
        }
        if (ObjectUtil.isNotNull(userRequest.getStatus())) {
            wrapper.set(User::getStatus, userRequest.getStatus());
        }
        if (ObjectUtil.isNotNull(userRequest.getIsPromoter())) {
            wrapper.set(User::getIsPromoter, userRequest.getIsPromoter());
            if (userRequest.getIsPromoter() && !tempUser.getIsPromoter()) {
                wrapper.set(User::getPromoterTime, DateUtil.date());
            }
        }
        wrapper.eq(User::getId, tempUser.getId());
        return update(wrapper);
    }

    /**
     * 检测验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    public void checkValidateCode(String phone, String code) {
        if(StringUtils.isBlank(code)){
            throw new CrmebException("请输入验证码");
        }
        Object validateCode = redisUtil.get(getValidateCodeRedisKey(phone));
        String walletPayOpenPassword = systemConfigService.getValueByKey(SysConfigConstants.IPHON_CODE_CARD);
        Boolean ifBooleand = Constants.CONFIG_FORM_SWITCH_OPEN.equals(walletPayOpenPassword);

        List<User> user =  userService.getByPhone(phone);
        String channelName = systemConfigService.getValueByKey("pay_channel_name");
        channelName = com.jbp.service.util.StringUtils.isEmpty(channelName) ? "平台" : channelName;

        if (validateCode == null && !ifBooleand) {
            throw new CrmebException("请先获取验证码");
        }
        if (validateCode == null) {
            validateCode= "";
        }

        if(ifBooleand){
            if(user.isEmpty() && !validateCode.toString().equals(code)) {
              throw new CrmebException("验证码错误");
            }
            if(!user.isEmpty()){
               ChannelIdentity channelIdentity = channelIdentityService.getByUser(user.get(0).getId(), channelName);
                if(channelIdentity == null && !validateCode.toString().equals(code)){
                    throw new CrmebException("验证码错误");
                }
                //验证码兼容身份证后6位
                if(channelIdentity != null && !validateCode.toString().equals(code) && !channelIdentity.getIdCardNo().substring(channelIdentity.getIdCardNo().length() - 6).equals(code)){
                   throw new CrmebException("验证码错误");
                }
            }
        }

        if(!ifBooleand && !validateCode.toString().equals(code)){
          throw new CrmebException("验证码错误");
        }
        redisUtil.delete(getValidateCodeRedisKey(phone));
    }

    @Override
    public void checkAccountTeamCode(Integer uid, Integer receiveUserId) {

        //验证是否同一个顶点账号，不是同一个不允许转账
        List<UserUpperDto> list =  invitationService.getAllUpper(uid);
        List<UserUpperDto>  receiveList =  invitationService.getAllUpper(receiveUserId);
        if(list.isEmpty() || receiveList.isEmpty() ){
            throw new CrmebException("转让失败。积分转让仅限同市场团队内进行，请核对接收人信息");
        }
        if(list.get(list.size() - 1).getPId().intValue() != receiveList.get(receiveList.size() - 1).getPId().intValue() ){
            throw new CrmebException("转让失败。积分转让仅限同市场团队内进行，请核对接收人信息");
        }

    }

    @Override
    public void checkTeamAccountTeamCode(Integer uid, Integer receiveUserId) {
        //验证是否同一个团队，不是同一个不允许转账

       Boolean ifInvit =  invitationService.hasChild(uid,receiveUserId);
        Boolean ifInvit2 =  invitationService.hasChild(receiveUserId,uid);

        if(!ifInvit && !ifInvit2){
            throw new CrmebException("转让失败。积分转让仅限同市场团队内进行，请核对接收人信息");
        }

    }

    /**
     * 检测手机验证码key
     *
     * @param phone String 手机号
     * @return String
     */
    private String getValidateCodeRedisKey(String phone) {
        return SmsConstants.SMS_VALIDATE_PHONE + phone;
    }

    /**
     * 是否用户使用标签
     *
     * @param tagId 标签id
     * @return Boolean
     */
    @Override
    public Boolean isUsedTag(Integer tagId) {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.select(User::getId);
        lqw.apply(StrUtil.format("find_in_set('{}', tag_id)", tagId));
        lqw.last(" limit 1");
        User user = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(user);
    }

    @Override
    public String getPhone(String account) {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.eq(User::getAccount,account);
        User one = getOne(lqw);
        return one.getPhone();
    }

    /**
     * 用户分配标签
     *
     * @param request 标签参数
     */
    @Override
    public Boolean tag(UserAssignTagRequest request) {
        //循环id处理
        List<Integer> idList = CrmebUtil.stringToArray(request.getIds());
        idList = idList.stream().distinct().collect(Collectors.toList());
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(User::getTagId, request.getTagIds());
        wrapper.in(User::getId, idList);
        wrapper.eq(User::getIsLogoff, 0);
        return update(wrapper);
    }

    /**
     * 清除对应的用户等级
     *
     * @param levelId 等级id
     */
    @Override
    public Boolean removeLevelByLevelId(Integer levelId) {
        LambdaUpdateWrapper<User> luw = Wrappers.lambdaUpdate();
        luw.set(User::getLevel, 1);
        luw.eq(User::getLevel, levelId);
        return update(luw);
    }

    /**
     * 获取uidMap
     *
     * @param uidList uid列表
     * @return Map
     */
    @Override
    public Map<Integer, User> getUidMapList(List<Integer> uidList) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.select(User::getId, User::getAccount, User::getNickname, User::getPhone, User::getAvatar, User::getIsLogoff, User::getLevel);
        lqw.in(User::getId, uidList);
        List<User> userList = dao.selectList(lqw);
        Map<Integer, User> userMap = new HashMap<>();
        userList.forEach(user -> {
            userMap.put(user.getId(), user);
        });
        return userMap;
    }

    @Override
    public void validPayPwd(Integer uid, String pwd) {
        User user = getById(uid);
        if (walletConfigService.hasPwd()) {
            if (com.jbp.service.util.StringUtils.isEmpty(user.getPayPwd())) {
                throw new CrmebException("请设置交易密码");
            }
            if (!CrmebUtil.encryptPassword(pwd).equals(user.getPayPwd())) {
                throw new CrmebException("交易密码不正确");
            }
        }
    }

    @Override
    public UserPlatformInfoResponse getUserPlatfromInfo(String dbName,String mobile) {
        return   dao.getUserPlatfromInfo(mobile,dbName);
    }

    @Override
    public UserPlatformInfoResponse getUserPlatfromInfo(String mobile) {
        return   dao.getUserPlatfromInfo2(mobile);
    }

    @Override
    public UserPlatformInfoResponse getPlatfromInfo() {
        return   dao.getPlatfromInfo();
    }

    /**
     * 更新余额
     *
     * @param uid   用户ID
     * @param price 金额
     * @param type  增加add、扣减sub
     * @return Boolean
     */
    @Override
    public Boolean updateNowMoney(Integer uid, BigDecimal price, String type) {
        UpdateWrapper<User> wrapper = Wrappers.update();
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            wrapper.setSql(StrUtil.format("now_money = now_money + {}", price));
        } else {
            wrapper.setSql(StrUtil.format("now_money = now_money - {}", price));
        }
        wrapper.eq("id", uid);
        if (type.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            wrapper.apply(StrUtil.format(" now_money - {} >= 0", price));
        }
        return update(wrapper);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public User register(String username, String phone, String account, Boolean ifOpenShop) {
        User user = new User();
        user.setAccount(account);
        if (isUnique4Phone() && CollectionUtils.isNotEmpty(getByPhone(phone))) {
            throw new CrmebException("手机号重复");
        }
        user.setPhone(phone);
        user.setOpenShop(ifOpenShop);
        user.setRegisterType(UserConstants.REGISTER_TYPE_H5);
        user.setNickname(com.jbp.common.utils.StringUtils.filterEmoji(username));
        user.setAvatar(systemConfigService.getValueByKey(SysConfigConstants.USER_DEFAULT_AVATAR_CONFIG_KEY));
        Date nowDate = CrmebDateUtil.nowDateTime();
        user.setCreateTime(nowDate);
        user.setLastLoginTime(nowDate);
        user.setPwd(CrmebUtil.encryptPassword("123456"));
        if (!ObjectUtil.isNull(phone)) {
            user.setPayPwd(CrmebUtil.encryptPassword(phone.substring(phone.length() - 6)));
        }
        user.setLevel(1);
        // 设置活跃时间
        setActiveTime(user);
        // 推广人
        user.setSpreadUid(0);
        save(user);
        // 增加代理等级
        userCapaService.saveOrUpdateCapa(user.getId(), capaService.getMinCapa().getId(), "", "手机号验证码注册");
        return user;
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public Boolean importUser(List<UserImportRequest> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new CrmebException("导入用户数据不能为空");
        }
        // 校验
        int i = 1;
        Map<String, UserImportRequest> userMap = Maps.newConcurrentMap();
        for (UserImportRequest importUser : list) {
            String account = importUser.getAccount();
            if (com.jbp.common.utils.StringUtils.isEmpty(account)) {
                throw new CrmebException("账号不能为空");
            }
            account = com.jbp.common.utils.StringUtils.trim(account);
            if (userMap.get(account) != null) {
                throw new CrmebException("导入表格账号重复:" + account);
            }
            userMap.put(account, importUser);
            if (getByAccount(importUser.getAccount()) != null) {
                throw new CrmebException(importUser.getAccount() + ":账号已经存在");
            }
            if (com.jbp.common.utils.StringUtils.isEmpty(importUser.getNickname())) {
                throw new CrmebException("昵称不能为空"+ importUser.getAccount());
            }
            if (com.jbp.common.utils.StringUtils.isEmpty(importUser.getMobile())) {
                throw new CrmebException("手机号不能为空"+ importUser.getAccount());
            }
            if (com.jbp.common.utils.StringUtils.isEmpty(importUser.getPaccount())) {
                throw new CrmebException("销售账号不能为空"+ importUser.getAccount());
            }
            if(importUser.getAccount().equals(importUser.getPaccount())){
                throw new CrmebException("销售账号不能是注册账号"+ importUser.getPaccount());
            }
            if (com.jbp.common.utils.StringUtils.isEmpty(importUser.getOpenShop())) {
                throw new CrmebException("是否开店不能为空"+ importUser.getAccount());
            }
            if (!("是".equals(importUser.getOpenShop()) || "否".equals(importUser.getOpenShop()))) {
                throw new CrmebException("是否开店只能填写是|否"+ importUser.getAccount());
            }
            if (importUser.getCapaId() == null) {
                throw new CrmebException("等级编号不能为空"+ importUser.getAccount());
            }
            if (capaService.getById(importUser.getCapaId()) == null) {
                throw new CrmebException("等级编号不存在"+ importUser.getAccount());
            }
            if (importUser.getCapaXsId() != null) {
                if (capaXsService.getById(importUser.getCapaXsId()) == null) {
                    throw new CrmebException("星级编号不存在"+ importUser.getAccount());
                }
            }
            if (com.jbp.common.utils.StringUtils.isNotEmpty(importUser.getRaccount())) {
                if(importUser.getAccount().equals(importUser.getRaccount())){
                    throw new CrmebException("服务账号不能是注册账号:"+ importUser.getRaccount());
                }
                if (importUser.getNode() == null) {
                    throw new CrmebException("存在服务上级必须填写安置节点"+ importUser.getAccount());
                }
                if (importUser.getNode().intValue() != 0 && importUser.getNode().intValue() != 1) {
                    throw new CrmebException("存在服务上级必须填写安置节点且只能填写0或1"+ importUser.getAccount());
                }
            }
            if (ObjectUtils.anyNull(importUser.getUsableScore(), importUser.getUsedScore(), importUser.getGouWu(), importUser.getJiangLi(), importUser.getHuangGou(), importUser.getFuQuan())) {
                throw new CrmebException("积分数字不能为空，没有则录入0:"+ importUser.getAccount());
            }
            logger.info("正在检查导入数据基础信息:" + i + "###总条数:" + list.size());
            i++;
        }

        // 保存用户
        i = 1;
        for (UserImportRequest importUser : list) {
            register(importUser.getNickname(), importUser.getMobile(), importUser.getAccount(), importUser.getOpenShop().equals("是"));
            logger.info("正在注册新用户信息:" + i + "###总条数:" + list.size());
            i++;
        }
        i = 1;
        for (UserImportRequest importUser : list) {
            User user = getByAccount(importUser.getAccount());
            userCapaService.saveOrUpdateCapa(user.getId(), importUser.getCapaId(), "导入", "导入");
            if (importUser.getCapaXsId() != null) {
                userCapaXsService.saveOrUpdateCapa(user.getId(), importUser.getCapaXsId(), false, "导入", "导入");
            }
            logger.info("正在处理用户等级信息:" + i + "###总条数:" + list.size());
            i++;
        }
        // 绑定销售上级
        i = 1;
        for (UserImportRequest importUser : list) {
            User pUser = getByAccount(importUser.getPaccount());
            if (pUser == null) {
                throw new RuntimeException("销售上级账户不存在"+ importUser.getPaccount());
            }
            String account = com.jbp.common.utils.StringUtils.trim(importUser.getAccount());
            User user = getByAccount(account);
            invitationService.band(user.getId(), pUser.getId(), false, true, true);
            logger.info("正在绑定销售上级信息:" + i + "###总条数:" + list.size());
            i++;
        }
        // 绑定服务上级
        i = 1;
        for (UserImportRequest importUser : list) {
            if (com.jbp.common.utils.StringUtils.isEmpty(importUser.getRaccount())) {
                continue;
            }
            User rUser = getByAccount(importUser.getRaccount());
            if (rUser == null) {
                throw new RuntimeException("服务上级账户不存在"+importUser.getRaccount());
            }
            String account = com.jbp.common.utils.StringUtils.trim(importUser.getAccount());
            User user = getByAccount(account);
            if (relationService.getByPid(rUser.getId(), importUser.getNode()) != null) {
                throw new CrmebException(importUser.getRaccount() + "位置:" + importUser.getNode() + "安置重复，请检查表格是否存在重复或者已经安置过");
            }
            relationService.band(user.getId(), rUser.getId(), null, importUser.getNode());
            logger.info("正在绑定销售服务信息:" + i + "###总条数:" + list.size());
            i++;
        }

        // 处理业绩 自己的业绩+给安置上级
        i=1;
        for (UserImportRequest importUser : list) {
            if (com.jbp.common.utils.StringUtils.isEmpty(importUser.getRaccount())) {
                continue;
            }
            User rUser = getByAccount(importUser.getRaccount());
            if (ArithmeticUtils.gt(importUser.getUsableScore(), BigDecimal.ZERO)) {
                relationScoreService.operateUsable(rUser.getId(), importUser.getUsableScore(),
                        importUser.getNode(), com.jbp.common.utils.StringUtils.N_TO_10("DR_"), DateTimeUtils.getNow(), "导入初始化", true);
            }
            if (ArithmeticUtils.gt(importUser.getUsedScore(), BigDecimal.ZERO)) {
                relationScoreService.operateUsed(rUser.getId(), importUser.getUsedScore(),
                        importUser.getNode(), com.jbp.common.utils.StringUtils.N_TO_10("DR_"), DateTimeUtils.getNow(), "导入初始化", true);
            }
            logger.info("正在处理业绩信息:" + i + "###总条数:" + list.size());
            i++;
        }

        // 处理自己的积分
        i = 1;
        for (UserImportRequest importUser : list) {
            String account = com.jbp.common.utils.StringUtils.trim(importUser.getAccount());
            User user = getByAccount(account);
            if (ArithmeticUtils.gt(importUser.getGouWu(), BigDecimal.ZERO)) {
                WalletConfig walletConfig = walletConfigService.getByName(WalletConfig.NameEnum.购物积分.name());
                if (walletConfig != null) {
                    platformWalletService.transferToUser(user.getId(), walletConfig.getType(), importUser.getGouWu(), WalletFlow.OperateEnum.调账.name(), com.jbp.common.utils.StringUtils.N_TO_10("DR_"), "后台导入名单初始化");
                }
            }
            if (ArithmeticUtils.gt(importUser.getJiangLi(), BigDecimal.ZERO)) {
                WalletConfig walletConfig = walletConfigService.getByName(WalletConfig.NameEnum.奖励积分.name());
                if (walletConfig != null) {
                    platformWalletService.transferToUser(user.getId(), walletConfig.getType(), importUser.getJiangLi(), WalletFlow.OperateEnum.调账.name(), com.jbp.common.utils.StringUtils.N_TO_10("DR_"), "后台导入名单初始化");
                }
            }
            if (ArithmeticUtils.gt(importUser.getHuangGou(), BigDecimal.ZERO)) {
                WalletConfig walletConfig = walletConfigService.getByName(WalletConfig.NameEnum.换购积分.name());
                if (walletConfig != null) {
                    platformWalletService.transferToUser(user.getId(), walletConfig.getType(), importUser.getHuangGou(), WalletFlow.OperateEnum.调账.name(), com.jbp.common.utils.StringUtils.N_TO_10("DR_"), "后台导入名单初始化");
                }
            }
            if (ArithmeticUtils.gt(importUser.getFuQuan(), BigDecimal.ZERO)) {
                WalletConfig walletConfig = walletConfigService.getByName(WalletConfig.NameEnum.福券积分.name());
                if (walletConfig != null) {
                    platformWalletService.transferToUser(user.getId(), walletConfig.getType(), importUser.getFuQuan(), WalletFlow.OperateEnum.调账.name(), com.jbp.common.utils.StringUtils.N_TO_10("DR_"), "后台导入名单初始化");
                }
            }
            logger.info("正在处理积分信息:" + i + "###总条数:" + list.size());
            i++;
        }
        return true;
    }

    /**
     * 更新用户积分
     *
     * @param uid      用户ID
     * @param integral 积分
     * @param type     增加add、扣减sub
     * @return Boolean
     */
    @Override
    public Boolean updateIntegral(Integer uid, Integer integral, String type) {
        UpdateWrapper<User> wrapper = Wrappers.update();
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            wrapper.setSql(StrUtil.format("integral = integral + {}", integral));
        } else {
            wrapper.setSql(StrUtil.format("integral = integral - {}", integral));
        }
        wrapper.eq("id", uid);
        if (type.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            wrapper.apply(StrUtil.format(" integral - {} >= 0", integral));
        }
        return update(wrapper);
    }

    /**
     * 更新用户佣金
     *
     * @param uid   用户ID
     * @param price 金额
     * @param type  增加add、扣减sub
     * @return Boolean
     */
    @Override
    public Boolean updateBrokerage(Integer uid, BigDecimal price, String type) {
        UpdateWrapper<User> wrapper = Wrappers.update();
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            wrapper.setSql(StrUtil.format("brokerage_price = brokerage_price + {}", price));
        } else {
            wrapper.setSql(StrUtil.format("brokerage_price = brokerage_price - {}", price));
        }
        wrapper.eq("id", uid);
        if (type.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            wrapper.apply(StrUtil.format(" brokerage_price - {} >= 0", price));
        }
        return update(wrapper);
    }

    /**
     * 更新用户经验
     *
     * @param uid        用户ID
     * @param experience 经验
     * @param type       增加add、扣减sub
     * @return Boolean
     */
    @Override
    public Boolean updateExperience(Integer uid, Integer experience, String type) {
        UpdateWrapper<User> wrapper = Wrappers.update();
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            wrapper.setSql(StrUtil.format("experience = experience + {}", experience));
        } else {
            wrapper.setSql(StrUtil.format("experience = experience - {}", experience));
        }
        wrapper.eq("id", uid);
        if (type.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            wrapper.apply(StrUtil.format(" experience - {} >= 0", experience));
        }
        return update(wrapper);
    }

    /**
     * 佣金转余额
     *
     * @param uid   用户ID
     * @param price 转入金额
     * @return Boolean
     */
    @Override
    public Boolean brokerageToYue(Integer uid, BigDecimal price) {
        UpdateWrapper<User> wrapper = Wrappers.update();
        wrapper.setSql(StrUtil.format("now_money = now_money + {}", price));
        wrapper.setSql(StrUtil.format("brokerage_price = brokerage_price - {}", price));
        wrapper.eq("id", uid);
        wrapper.apply(StrUtil.format(" brokerage_price - {} >= 0", price));
        return update(wrapper);
    }

    /**
     * 操作用户积分
     *
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean operateUserInteger(UserOperateIntegralRequest request) {
        User user = getById(request.getUid());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户不存在");
        }
        if (user.getIsLogoff()) {
            throw new CrmebException("已注销用户不可操作");
        }
        if (request.getOperateType().equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            if (user.getIntegral() - request.getIntegral() < 0) {
                throw new CrmebException("积分扣减后不能小于0");
            }
        }
        if (request.getOperateType().equals(Constants.OPERATION_TYPE_ADD)) {
            if ((user.getIntegral() + request.getIntegral()) > 99999999) {
                throw new CrmebException("积分添加后不能大于99999999");
            }
        }
        return transactionTemplate.execute(e -> {
            // 生成记录
            UserIntegralRecord integralRecord = new UserIntegralRecord();
            integralRecord.setUid(user.getId());
            integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_SYSTEM);
            integralRecord.setTitle(IntegralRecordConstants.INTEGRAL_RECORD_TITLE_SYSTEM);
            integralRecord.setIntegral(request.getIntegral());
            integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
            if (request.getOperateType().equals(Constants.OPERATION_TYPE_ADD)) {// 增加
                integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
                integralRecord.setBalance(user.getIntegral() + request.getIntegral());
                integralRecord.setMark(StrUtil.format("后台操作增加了{}积分", request.getIntegral()));
                updateIntegral(user.getId(), request.getIntegral(), Constants.OPERATION_TYPE_ADD);
            } else {
                integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB);
                integralRecord.setBalance(user.getIntegral() - request.getIntegral());
                integralRecord.setMark(StrUtil.format("后台操作减少了{}积分", request.getIntegral()));
                updateIntegral(user.getId(), request.getIntegral(), Constants.OPERATION_TYPE_SUBTRACT);
            }
            userIntegralRecordService.save(integralRecord);
            return Boolean.TRUE;
        });
    }

    /**
     * 操作用户余额
     *
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean operateUserBalance(UserOperateBalanceRequest request) {
        User user = getById(request.getUid());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户不存在");
        }
        if (user.getIsLogoff()) {
            throw new CrmebException("已注销用户不可操作");
        }
        // 减少时要判断小于0的情况,添加时判断是否超过数据限制
        if (request.getOperateType().equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            if (user.getNowMoney().subtract(request.getMoney()).compareTo(BigDecimal.ZERO) < 0) {
                throw new CrmebException("余额扣减后不能小于0");
            }
        }
        if (request.getOperateType().equals(Constants.OPERATION_TYPE_ADD)) {
            if (user.getNowMoney().add(request.getMoney()).compareTo(new BigDecimal("99999999.99")) > 0) {
                throw new CrmebException("余额添加后后不能大于99999999.99");
            }
        }
        // 生成UserBill
        UserBill userBill = new UserBill();
        userBill.setUid(user.getId());
        userBill.setLinkId("0");
        userBill.setTitle("后台操作");
        userBill.setCategory(BillConstants.BILL_TYPE_SYSTEM);
        userBill.setNumber(request.getMoney());
        userBill.setStatus(1);
        userBill.setCreateTime(CrmebDateUtil.nowDateTime());

        Bill bill = new Bill();
        bill.setUid(user.getId());
        bill.setAmount(request.getMoney());
        bill.setType(BillConstants.BILL_TYPE_SYSTEM);

        UserBalanceRecord balanceRecord = new UserBalanceRecord();
        balanceRecord.setUid(user.getId());
        balanceRecord.setAmount(request.getMoney());
        balanceRecord.setLinkId("0");
        balanceRecord.setLinkType(BalanceRecordConstants.BALANCE_RECORD_LINK_TYPE_SYSTEM);
        return transactionTemplate.execute(e -> {
            if (request.getOperateType().equals(Constants.OPERATION_TYPE_ADD)) {// 增加
                userBill.setPm(1);
                userBill.setType(BillConstants.USER_BILL_TYPE_SYSTEM_ADD);
                userBill.setBalance(user.getNowMoney().add(request.getMoney()));
                userBill.setMark(StrUtil.format("后台操作增加了{}余额", request.getMoney()));

                bill.setPm(BillConstants.BILL_PM_SUB);
                bill.setMark(StrUtil.format("后台操作给用户增加余额{}元", request.getMoney()));

                balanceRecord.setType(BalanceRecordConstants.BALANCE_RECORD_TYPE_ADD);
                balanceRecord.setBalance(user.getNowMoney().add(request.getMoney()));
                balanceRecord.setRemark(StrUtil.format(BalanceRecordConstants.BALANCE_RECORD_REMARK_SYSTEM_ADD, request.getMoney().setScale(2).toString()));

                updateNowMoney(user.getId(), request.getMoney(), Constants.OPERATION_TYPE_ADD);
            } else {
                userBill.setPm(0);
                userBill.setType(BillConstants.USER_BILL_TYPE_SYSTEM_SUB);
                userBill.setBalance(user.getNowMoney().subtract(request.getMoney()));
                userBill.setMark(StrUtil.format("后台操作减少了{}余额", request.getMoney()));

                bill.setPm(BillConstants.BILL_PM_ADD);
                bill.setMark(StrUtil.format("后台操作给用户扣减余额{}元", request.getMoney()));

                balanceRecord.setType(BalanceRecordConstants.BALANCE_RECORD_TYPE_SUB);
                balanceRecord.setBalance(user.getNowMoney().subtract(request.getMoney()));
                balanceRecord.setRemark(StrUtil.format(BalanceRecordConstants.BALANCE_RECORD_REMARK_SYSTEM_SUB, request.getMoney().setScale(2).toString()));

                updateNowMoney(user.getId(), request.getMoney(), Constants.OPERATION_TYPE_SUBTRACT);
            }
            billService.save(bill);
            userBillService.save(userBill);
            userBalanceRecordService.save(balanceRecord);
            return Boolean.TRUE;
        });
    }

    /**
     * 支付成功，用户信息变更
     *
     * @param id         用户id
     * @param isPromoter 是否成为推广员
     */
    @Override
    public Boolean paySuccessChange(Integer id, Boolean isPromoter) {
        UpdateWrapper<User> wrapper = Wrappers.update();
        wrapper.setSql("pay_count = pay_count + 1");
        if (isPromoter) {
            wrapper.set("is_promoter", 1);
            wrapper.set("promoter_time", DateUtil.date());
        }
        wrapper.eq("id", id);
        return update(wrapper);
    }

    /**
     * 根据用户id获取自己本身的推广用户
     *
     * @param userIdList List<Integer> 用户id集合
     * @return List<User>
     */
    @Override
    public List<Integer> getSpreadPeopleIdList(List<Integer> userIdList) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(User::getId); //查询用户id
        lambdaQueryWrapper.in(User::getSpreadUid, userIdList); //xx的下线集合
        List<User> list = dao.selectList(lambdaQueryWrapper);

        if (null == list || list.size() < 1) {
            return new ArrayList<>();
        }
        return list.stream().map(User::getId).distinct().collect(Collectors.toList());
    }

    /**
     * 根据用户id获取自己本身的推广用户
     *
     * @param userId           自己的id
     * @param spreadUserIdList 自己推广用户的id列表
     * @param sortKey          排序, 排序|childCount=团队排序,amountCount=金额排序,orderCount=订单排序
     * @param isAsc            排序值 DESC ASC
     */
    @Override
    public List<UserSpreadPeopleItemResponse> getSpreadPeopleList(Integer userId, List<Integer> spreadUserIdList, String keywords, String sortKey,
                                                                  String isAsc, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        Map<String, Object> map = new HashMap<>();
        map.put("uid", userId);
        map.put("userIdList", spreadUserIdList.stream().distinct().map(String::valueOf).collect(Collectors.joining(",")));
        if (StrUtil.isNotBlank(keywords)) {
            keywords = URLUtil.decode(keywords);
            map.put("keywords", "%" + keywords + "%");
        }
        map.put("sortKey", "create_time");
        if (StrUtil.isNotBlank(sortKey)) {
            map.put("sortKey", sortKey);
        }
        map.put("sortValue", Constants.SORT_DESC);
        if (isAsc.equalsIgnoreCase(Constants.SORT_ASC)) {
            map.put("sortValue", Constants.SORT_ASC);
        }

        return dao.getSpreadPeopleList(map);
    }

    /**
     * 推广人排行(取前50)
     *
     * @param type 时间范围(week-周，month-月)
     * @return List<User>
     */
    @Override
    public List<User> getSpreadPeopleTopByDate(String type) {
        QueryWrapper<User> queryWrapper = Wrappers.query();
        queryWrapper.select("count(id) as spread_count, spread_uid")
                .gt("spread_uid", 0)
                .eq("status", true);
        if (StrUtil.isNotBlank(type)) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(type);
            queryWrapper.between("spread_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        queryWrapper.groupBy("spread_uid").orderByDesc("spread_count");
        queryWrapper.last(" limit 50");
        List<User> spreadVoList = dao.selectList(queryWrapper);
        if (CollUtil.isEmpty(spreadVoList)) {
            return spreadVoList;
        }
        List<Integer> spreadIdList = spreadVoList.stream().map(User::getSpreadUid).collect(Collectors.toList());
        Map<Integer, User> userMap = getUidMapList(spreadIdList);
        for (User spreadVo : spreadVoList) {
            User user = userMap.get(spreadVo.getSpreadUid());
            spreadVo.setId(spreadVo.getSpreadUid());
            spreadVo.setAvatar(user.getAvatar());
            spreadVo.setNickname(user.getNickname());
        }
        return spreadVoList;
    }

    /**
     * 绑定推广关系（登录状态）
     *
     * @param spreadUid 推广人id
     */
    @Override
    public void bindSpread(Integer spreadUid) {
        //新用户会在注册的时候单独绑定，此处只处理登录用户
        if (ObjectUtil.isNull(spreadUid) || spreadUid <= 0) {
            return;
        }
        User user = getInfo();
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("当前用户未登录,请先登录");
        }
        Boolean checkBingSpread = checkBingSpread(user, spreadUid, "old");
        if (!checkBingSpread) return;

        user.setSpreadUid(spreadUid);
        user.setSpreadTime(CrmebDateUtil.nowDateTime());
        Boolean execute = transactionTemplate.execute(e -> {
            updateById(user);
            updateSpreadCountByUid(spreadUid, Constants.OPERATION_TYPE_ADD);
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("绑定推广人时出错，userUid = {}, spreadUid = {}", user.getId(), spreadUid));
        }
    }

    /**
     * 获取用户总人数
     */
    @Override
    public Integer getTotalNum() {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.select(User::getId);
        lqw.eq(User::getIsLogoff, false);
        return dao.selectCount(lqw);
    }

    /**
     * 获取日期新增用户数
     *
     * @param date 日期 yyyy-MM-dd
     * @return 新增用户数
     */
    @Override
    public Integer getRegisterNumByDate(String date) {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(lqw);
    }

    /**
     * 获取用户渠道数据
     *
     * @return List
     */
    @Override
    public List<User> getChannelData() {
        QueryWrapper<User> wrapper = Wrappers.query();
        wrapper.select("register_type", "count(id) as pay_count");
        wrapper.eq("is_logoff", 0);
        wrapper.groupBy("register_type");
        return dao.selectList(wrapper);
    }

    /**
     * 更新用户推广人
     *
     * @param userId    用户ID
     * @param spreadUid 推广人ID
     */
    @Override
    public Boolean updateSpreadByUid(Integer userId, Integer spreadUid) {
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(User::getSpreadUid, spreadUid);
        wrapper.set(User::getSpreadTime, DateUtil.date());
        wrapper.eq(User::getId, userId);
        return update(wrapper);
    }

    /**
     * PC后台分销员列表
     *
     * @param keywords    搜索参数
     * @param dateLimit   时间参数
     * @param pageRequest 分页参数
     */
    @Override
    public PageInfo<User> getRetailStorePeoplePage(String keywords, String dateLimit, PageParamRequest pageRequest) {
        Page<User> pageUser = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        // id,头像，昵称，姓名，电话，推广用户数，推广订单数，推广订单额，佣金总金额，已提现金额，提现次数，未提现金额，上级推广人
        lqw.select(User::getId, User::getNickname, User::getRealName, User::getPhone, User::getAvatar,
                User::getSpreadCount, User::getBrokeragePrice, User::getSpreadUid, User::getPromoterTime);
        lqw.eq(User::getIsPromoter, true);
        if (StrUtil.isNotBlank(keywords)) {
            String decode = URLUtil.decode(keywords);
            lqw.and(i -> i.eq(User::getId, decode) //用户账号
                    .or().like(User::getNickname, decode) //昵称
                    .or().like(User::getPhone, decode)); //手机号码
        }
        if (StrUtil.isNotBlank(dateLimit)) {
            DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(dateLimit);
            lqw.between(User::getPromoterTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        lqw.eq(User::getIsLogoff, 0);
        lqw.orderByDesc(User::getId);
        List<User> userList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(pageUser, userList);
    }

    /**
     * 根据推广级别和其他参数当前用户下的推广列表
     *
     * @param request 推广列表参数
     * @return 当前用户的推广人列表
     */
    @Override
    public PageInfo<User> getRetailStoreSubUserList(RetailStoreSubUserSearchRequest request, PageParamRequest pageRequest) {
        if (request.getType().equals(1)) {// 一级推广人
            return getFirstSpreadUserListPage(request, pageRequest);
        }
        if (request.getType().equals(2)) {// 二级推广人
            return getSecondSpreadUserListPage(request, pageRequest);
        }
        return getAllSpreadUserListPage(request, pageRequest);
    }

    /**
     * 更新用户连续签到天数
     *
     * @param day 连续签到天数
     * @param id  用户ID
     * @return Boolean
     */
    @Override
    public Boolean updateSignNumByUid(Integer day, Integer id) {
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(User::getSignNum, day);
        wrapper.eq(User::getId, id);
        return update(wrapper);
    }

    /**
     * 用户注销数据前置
     * 判断用户还存在积分、余额、佣金
     * 判断用户是否存在待发货订单
     * 判断用户是否存在退款申请中的退款单
     */
    @Override
    public UserLogoffBeforeResponse logoffBefore() {
        User user = getInfo();
        if (user.getIsLogoff()) {
            throw new CrmebException("用户已注销");
        }
        UserLogoffBeforeResponse response = new UserLogoffBeforeResponse();
        response.setIsTip(true);
        if (user.getIntegral() > 0 || user.getNowMoney().compareTo(BigDecimal.ZERO) > 0
                || user.getBrokeragePrice().compareTo(BigDecimal.ZERO) > 0) {
            return response;
        }
        if (orderService.isExistPendingOrderByUid(user.getId())) {
            return response;
        }
        if (refundOrderService.getRefundingCount(user.getId()) > 0) {
            return response;
        }
        response.setIsTip(false);
        return response;
    }

    /**
     * 用户注销
     * 1.用户置为注销状态
     * 2.user_token软删除
     * 3.清除购物车数据
     * 4.清除关注、收藏数据
     * 5.接触分销关系
     * 6.清空用户登录状态
     * 7.清除用户标签
     * 8.关闭推广人按钮
     * 9.删除用户社区笔记
     * 10.删除用户社区笔记关系
     */
    @Override
    public Boolean logoff() {
        User user = getInfo();
        user.setIsLogoff(true);
        user.setLogoffTime(DateUtil.date());
        user.setTagId("");
        user.setIsPromoter(false);

        Boolean execute = transactionTemplate.execute(e -> {
            userTokenService.deleteByUid(user.getId());
            cartService.deleteByUid(user.getId());
            productRelationService.deleteByUid(user.getId());
            userMerchantCollectService.deleteByUid(user.getId());
            if (user.getSpreadUid() > 0) {
                updateSpreadCountByUid(user.getSpreadUid(), Constants.OPERATION_TYPE_SUBTRACT);
                user.setSpreadUid(0);
            }
            if (user.getSpreadCount() > 0) {
                batchRemoveSpreadUid(user.getId());
                user.setSpreadCount(0);
            }
            communityNotesService.deleteByUid(user.getId());
            communityNotesRelationService.deleteByAuthorId(user.getId());
            boolean update = updateById(user);
            if (!update) {
                logger.error("更新用户注销状态失败，用户id： {}", user.getId());
                e.setRollbackOnly();
                return update;
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("用户注销失败");
        }
        tokenComponent.logout(RequestUtil.getRequest());
        return execute;
    }

    /**
     * 管理端用户详情
     *
     * @param id 用户ID
     */
    @Override
    public UserAdminDetailResponse getAdminDetail(Integer id) {
        User user = getById(id);
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户不存在");
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (admin.getMerId() > 0) {
            if (!userMerchantCollectService.isCollect(user.getId(), admin.getMerId())) {
                throw new CrmebException("用户未收藏商户，无权查看用户信息");
            }
        }
        UserAdminDetailResponse response = new UserAdminDetailResponse();
        BeanUtils.copyProperties(user, response);
        if (user.getSpreadUid() > 0) {
            User spreadUser = getById(user.getSpreadUid());
            response.setSpreadName(spreadUser.getNickname());
        }
        SystemUserLevel systemUserLevel = systemUserLevelService.getByLevelId(user.getLevel());
        response.setGrade(ObjectUtil.isNotNull(systemUserLevel) ? systemUserLevel.getGrade() : 0);
        return response;
    }

    /**
     * 更新用户等级
     *
     * @param userId 用户ID
     * @param level  用户等级
     */
    @Override
    public Boolean updateUserLevel(Integer userId, Integer level) {
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(User::getLevel, level);
        wrapper.eq(User::getId, userId);
        return update(wrapper);
    }

    /**
     * 通过生日获取用户列表
     *
     * @param birthday 生日日期
     */
    @Override
    public List<User> findByBirthday(String birthday) {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.select(User::getId, User::getPhone);
        lqw.eq(User::getIsLogoff, 0);
        lqw.eq(User::getStatus, 1);
//        lqw.eq(User::getBirthday, birthday);
        lqw.apply("date_format(birthday, '%m-%d') = {0}", birthday);
        return dao.selectList(lqw);
    }

    @Override
    public void tradePassword( String code, String tradePassword) {
        if (StrUtil.isBlank(code)) {
            throw new CrmebException("手机号码验证码不能为空");
        }
        User user = getInfo();

        //检测验证码
        checkValidateCode(user.getPhone(), code);
        //获取当前用户信息
        user.setPayPwd(CrmebUtil.encryptPassword(tradePassword));
        updateById(user);

    }

    @Override
    public List<User> getNoChild() {
        return dao.getNoChild();
    }

    @Override
    public DeclUserInfoResultVo getOrderDealUser(String orderNo) {

        DeclUserInfoResultVo declUserInfoResultVo = new DeclUserInfoResultVo();
        OrderExt orderExt = orderExtService.getByOrder(orderNo);

        Order order = orderService.getOne(new QueryWrapper<Order>().lambda()
                .eq(Order::getOrderNo, orderNo));
        if (orderExt != null) {
            OrderRegister orderRegister = orderExt.getOrderRegister();
            User user = dao.selectOne(new QueryWrapper<User>().lambda()
                    .eq(User::getId, order.getUid()));
            //升级下单后重新赋值
            if (orderRegister == null) {
                orderRegister = new OrderRegister();
                UserCapa userCapa = userCapaService.getByUser(user.getId());
                orderRegister.setUsername(user.getNickname());
                orderRegister.setCapaId(userCapa.getCapaId());
            }

            declUserInfoResultVo.setUserUame(orderRegister.getUsername());
            declUserInfoResultVo.setAccount(user.getAccount());
            declUserInfoResultVo.setCapa(capaService.getById(orderRegister.getCapaId()).getName());
            declUserInfoResultVo.setNode(orderRegister.getNode());
            declUserInfoResultVo.setRaccount(orderRegister.getRaccount());
        }

        return declUserInfoResultVo;
    }

    @Override
    public PageInfo<UserInviteResponse> getUserInvite(UserInviteRequest request) {
        User currentUser = getInfo();
        List<UserInviteResponse> userInviteResponseList = invitationService.getUserNextList(currentUser.getId(), request.getKeywords());
        Page<Object> page = PageHelper.startPage(request.getPage(), request.getLimit());
        return CommonPage.copyPageInfo(page, userInviteResponseList);
    }

    public void updateUser(Integer id, String pwd, Integer sex, String nickname, String phone, String country, String province, String city, String district, String address, String payPwd, Boolean openShop) {
        if(StringUtils.isNotBlank(phone) && phone.contains("***")){
            throw new CrmebException("手机号格式错误");
        }
        User user = getById(id);
        LambdaUpdateWrapper<User> lqw = new LambdaUpdateWrapper<User>()
                .eq(User::getId, id)
                .set(ObjectUtil.isNotEmpty(sex), User::getSex, sex)
                .set(ObjectUtil.isNotEmpty(nickname) && !nickname.equals(""), User::getNickname, nickname)
                .set(ObjectUtil.isNotEmpty(phone) && !phone.equals(""), User::getPhone, phone)
                .set(ObjectUtil.isNotEmpty(country) && !country.equals(""), User::getCountry, country)
                .set(ObjectUtil.isNotEmpty(province) && !province.equals(""), User::getProvince, province)
                .set(ObjectUtil.isNotEmpty(city) && !city.equals(""), User::getCity, city)
                .set(ObjectUtil.isNotEmpty(district) && !district.equals(""), User::getDistrict, district)
                .set(ObjectUtil.isNotEmpty(address) && !address.equals(""), User::getAddress, address)
                .set(ObjectUtil.isNotEmpty(openShop), User::getOpenShop, openShop);
        if (ObjectUtils.isNotEmpty(pwd) && !pwd.equals("")) {
            lqw.set(User::getPwd, CrmebUtil.encryptPassword(pwd));
        }
        if (ObjectUtils.isNotEmpty(payPwd) && !payPwd.equals("")) {
            lqw.set(User::getPayPwd, CrmebUtil.encryptPassword(payPwd));
        }
        update(lqw);

    }

    @Override
    public Boolean verifyPayPwd(String payPwd) throws Exception {
        User user = getInfo();
        if (user.getPayPwd() == null) {
            throw new CrmebException("用户没有设置交易密码");
        }

        return user.getPayPwd().equals(CrmebUtil.encryptPassword(payPwd));
    }

    @Override
    public PageInfo<UserInviteInfoResponse> getUserInviteInfo(UserInviteRequest request) {
        User currentUser = getInfo();
        List<UserInviteInfoResponse> userInviteResponseList = invitationService.getUserNextInfoList(currentUser.getId(), request.getKeywords());


        userInviteResponseList.forEach(e -> {
            List<UserCapaXsSnapshot> userCapaXsSnapshotList =  userCapaXsSnapshotService.list(new QueryWrapper<UserCapaXsSnapshot>().lambda().eq(UserCapaXsSnapshot::getUid,e.getUid()));
            Date xsCreateTime = null;
              if(!userCapaXsSnapshotList.isEmpty()){
                  if(userCapaXsSnapshotList.size() == 1){
                      xsCreateTime = userCapaXsSnapshotList.get(0).getGmtCreated();
                  }
              }

            Date createTime =xsCreateTime == null ? e.getCreateTime(): xsCreateTime;
            createTime = DateTimeUtils.addHours(createTime, 24);
            e.setOneCount(invitationService.getInviteNumber(e.getUid()));
            e.setIfMonth(createTime.getTime() >= DateTimeUtils.getNow().getTime());
        });

        Page<Object> page = PageHelper.startPage(request.getPage(), request.getLimit());
        return CommonPage.copyPageInfo(page, userInviteResponseList);


    }

    /**
     * 批量清除用户推广人
     *
     * @param spreadUid 推广人id
     */
    private Boolean batchRemoveSpreadUid(Integer spreadUid) {
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(User::getSpreadUid, 0);
        wrapper.eq(User::getSpreadUid, spreadUid);
        return update(wrapper);
    }

    /**
     * 分页获取所有推广员
     */
    private PageInfo<User> getAllSpreadUserListPage(RetailStoreSubUserSearchRequest request, PageParamRequest pageRequest) {
        // 先所有一级推广员
        List<User> firstUserList = getSpreadListBySpreadIdAndType(request.getUid(), 0);
        if (CollUtil.isEmpty(firstUserList)) {
            return new PageInfo<>(CollUtil.newArrayList());
        }
        List<Integer> userIds = firstUserList.stream().map(User::getId).distinct().collect(Collectors.toList());
        Page<User> userPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getId, User::getAvatar, User::getNickname, User::getIsPromoter, User::getSpreadCount, User::getPayCount);
        queryWrapper.in(User::getId, userIds);
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String decode = URLUtil.decode(request.getKeywords());
            queryWrapper.and(e -> e.like(User::getNickname, decode).or().eq(User::getId, decode)
                    .or().eq(User::getPhone, decode));
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            queryWrapper.between(User::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        List<User> userList = dao.selectList(queryWrapper);
        return CommonPage.copyPageInfo(userPage, userList);
    }

    /**
     * 分页获取二级推广员
     */
    private PageInfo<User> getSecondSpreadUserListPage(RetailStoreSubUserSearchRequest request, PageParamRequest pageRequest) {
        // 先获取一级推广员
        List<User> firstUserList = getSpreadListBySpreadIdAndType(request.getUid(), 1);
        if (CollUtil.isEmpty(firstUserList)) {
            return new PageInfo<>(CollUtil.newArrayList());
        }
        List<Integer> userIds = firstUserList.stream().map(User::getId).distinct().collect(Collectors.toList());
        Page<User> userPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getId, User::getAvatar, User::getNickname, User::getIsPromoter, User::getSpreadCount, User::getPayCount);
        queryWrapper.in(User::getSpreadUid, userIds);
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String decode = URLUtil.decode(request.getKeywords());
            queryWrapper.and(e -> e.like(User::getNickname, decode).or().eq(User::getId, decode)
                    .or().eq(User::getPhone, decode));
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            queryWrapper.between(User::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        List<User> userList = dao.selectList(queryWrapper);
        return CommonPage.copyPageInfo(userPage, userList);
    }

    /**
     * 获取推广人列表
     *
     * @param spreadUid 父Uid
     * @param type      类型 0 = 全部 1=一级推广人 2=二级推广人
     */
    private List<User> getSpreadListBySpreadIdAndType(Integer spreadUid, Integer type) {
        // 获取一级推广人
        List<User> userList = getSpreadListBySpreadId(spreadUid);
        if (CollUtil.isEmpty(userList)) return userList;
        if (type.equals(1)) return userList;
        // 获取二级推广人
        List<User> userSecondList = CollUtil.newArrayList();
        userList.forEach(user -> {
            List<User> childUserList = getSpreadListBySpreadId(user.getId());
            if (CollUtil.isNotEmpty(childUserList)) {
                userSecondList.addAll(childUserList);
            }
        });
        if (type.equals(2)) {
            return userSecondList;
        }
        userList.addAll(userSecondList);
        return userList;
    }

    /**
     * 获取推广人列表
     *
     * @param spreadUid 父Uid
     */
    private List<User> getSpreadListBySpreadId(Integer spreadUid) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getSpreadUid, spreadUid);
        return dao.selectList(queryWrapper);
    }

    /**
     * 分页获取一级推广员
     */
    private PageInfo<User> getFirstSpreadUserListPage(RetailStoreSubUserSearchRequest request, PageParamRequest pageRequest) {
        Page<User> userPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.select(User::getId, User::getAvatar, User::getNickname, User::getIsPromoter, User::getSpreadCount, User::getPayCount);
        lqw.eq(User::getSpreadUid, request.getUid());
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String decode = URLUtil.decode(request.getKeywords());
            lqw.and(e -> e.like(User::getNickname, decode).or().eq(User::getId, decode)
                    .or().eq(User::getPhone, decode));
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            lqw.between(User::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        List<User> userList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(userPage, userList);
    }

}
