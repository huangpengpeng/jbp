package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.BrokerageRecordConstants;
import com.jbp.common.constants.ClosingConstant;
import com.jbp.common.constants.Constants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserBrokerageRecord;
import com.jbp.common.model.user.UserClosing;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.service.dao.UserClosingDao;
import com.jbp.service.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserExtractServiceImpl 接口实现
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
public class UserClosingServiceImpl extends ServiceImpl<UserClosingDao, UserClosing> implements UserClosingService {

    @Resource
    private UserClosingDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    /**
     * 根据uid获取分页结算记录
     *
     * @param uid              用户id
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserClosing> getPageByUid(Integer uid, PageParamRequest pageParamRequest) {
        Page<UserClosing> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserClosing> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserClosing::getUid, uid);
        lqw.orderByDesc(UserClosing::getId);
        List<UserClosing> closingList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, closingList);
    }

    /**
     * 用户结算分页列表
     *
     * @param request          搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserClosing> getPlatformPage(UserClosingSearchRequest request, PageParamRequest pageParamRequest) {
        Page<UserClosing> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        //带 UserExtract 类的多条件查询
        LambdaQueryWrapper<UserClosing> lqw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lqw.and(i -> i.
                    or().like(UserClosing::getWechatNo, keywords).   //微信号
                    or().like(UserClosing::getCardholder, keywords). //名称
                    or().like(UserClosing::getBankCardNo, keywords). //银行卡号
                    or().like(UserClosing::getAlipayAccount, keywords) //支付宝
            );
        }
        //提现状态
        if (ObjectUtil.isNotNull(request.getAuditStatus())) {
            lqw.eq(UserClosing::getAuditStatus, request.getAuditStatus());
        }
        //提现方式
        if (StrUtil.isNotBlank(request.getClosingType())) {
            lqw.eq(UserClosing::getClosingType, request.getClosingType());
        }
        //时间范围
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            lqw.between(UserClosing::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        if (ObjectUtil.isNotNull(request.getAccountStatus())) {
            lqw.eq(UserClosing::getAccountStatus, request.getAccountStatus());
        }
        //按创建时间降序排列
        lqw.orderByDesc(UserClosing::getId);
        List<UserClosing> closingList = dao.selectList(lqw);
        if (CollUtil.isNotEmpty(closingList)) {
            List<Integer> uidList = closingList.stream().map(UserClosing::getUid).distinct().collect(Collectors.toList());
            Map<Integer, User> userMap = userService.getUidMapList(uidList);
            for (UserClosing userClosing : closingList) {
                userClosing.setNickName(Optional.ofNullable(userMap.get(userClosing.getUid()).getNickname()).orElse(""));
                userClosing.setIsLogoff(Optional.ofNullable(userMap.get(userClosing.getUid()).getIsLogoff()).orElse(false));
            }
        }
        return CommonPage.copyPageInfo(page, closingList);
    }

    /**
     * 用户结算申请审核
     *
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean userClosingAudit(ClosingAuditRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        UserClosing userClosing = getByClosingNo(request.getClosingNo());
        if (!userClosing.getAuditStatus().equals(ClosingConstant.CLOSING_AUDIT_STATUS_AUDIT)) {
            throw new CrmebException("提现申请已经处理");
        }
        User user = userService.getById(userClosing.getUid());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户数据异常");
        }
        userClosing.setAuditId(admin.getId());
        userClosing.setAuditTime(DateUtil.date());

        UserBrokerageRecord brokerageRecord = userBrokerageRecordService.getOneByLinkNo(userClosing.getClosingNo());
        // 拒绝
        if (request.getAuditStatus().equals(ClosingConstant.CLOSING_AUDIT_STATUS_FAIL)) {//未通过时恢复用户总金额
            userClosing.setRefusalReason(request.getRefusalReason());
            userClosing.setAuditStatus(ClosingConstant.CLOSING_AUDIT_STATUS_FAIL);
            // 添加提现申请拒绝佣金记录
            brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_INVALIDATION);
            return transactionTemplate.execute(e -> {
                updateById(userClosing);
                userBrokerageRecordService.updateById(brokerageRecord);
                return Boolean.TRUE;
            });
        }
        // 同意
        userClosing.setAuditStatus(ClosingConstant.CLOSING_AUDIT_STATUS_SUCCESS);
        if (user.getBrokeragePrice().compareTo(userClosing.getClosingPrice()) < 0) {
            throw new CrmebException("用户佣金不足，审核无法成功");
        }
        brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        return transactionTemplate.execute(e -> {
            updateById(userClosing);
            // 修改用户佣金
            Boolean result = userService.updateBrokerage(user.getId(), userClosing.getClosingPrice(), Constants.OPERATION_TYPE_SUBTRACT);
            if (!result) {
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            userBrokerageRecordService.updateById(brokerageRecord);
            return Boolean.TRUE;
        });
    }

    /**
     * 根据结算单号查询
     *
     * @param closingNo 结算单号
     * @return UserClosing
     */
    @Override
    public UserClosing getByClosingNo(String closingNo) {
        LambdaQueryWrapper<UserClosing> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserClosing::getClosingNo, closingNo);
        lqw.last(" limit 1");
        UserClosing userClosing = dao.selectOne(lqw);
        if (ObjectUtil.isNull(userClosing)) {
            throw new CrmebException("用户结算单不存在");
        }
        return userClosing;
    }

    /**
     * 用户结算到账凭证
     */
    @Override
    public Boolean proof(ClosingProofRequest request) {
        UserClosing userClosing = getByClosingNo(request.getClosingNo());
        if (!userClosing.getAuditStatus().equals(ClosingConstant.CLOSING_AUDIT_STATUS_SUCCESS)) {
            throw new CrmebException("结算单审核状态异常!");
        }
        userClosing.setClosingProof(systemAttachmentService.clearPrefix(request.getClosingProof()));
        userClosing.setAccountStatus(ClosingConstant.CLOSING_ACCOUNT_STATUS_SUCCESS);
        userClosing.setClosingTime(DateUtil.date());
        return updateById(userClosing);
    }

    /**
     * 用户结算备注
     */
    @Override
    public Boolean remark(ClosingRemarkRequest request) {
        UserClosing userClosing = getByClosingNo(request.getClosingNo());
        userClosing.setMark(request.getRemark());
        return updateById(userClosing);
    }


//    /**
//    * 列表
//    * @param request 请求参数
//    * @param pageParamRequest 分页类参数
//    * @author Mr.
//    * @since 2020-05-11
//    * @return List<UserExtract>
//    */
//    @Override
//    public List<UserExtract> getList(UserExtractSearchRequest request, PageParamRequest pageParamRequest) {
//        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
//
//        //带 UserExtract 类的多条件查询
//        LambdaQueryWrapper<UserExtract> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if (!StringUtils.isBlank(request.getKeywords())) {
//            lambdaQueryWrapper.and(i -> i.
//                    or().like(UserExtract::getWechat, request.getKeywords()).   //微信号
//                    or().like(UserExtract::getRealName, request.getKeywords()). //名称
//                    or().like(UserExtract::getBankCode, request.getKeywords()). //银行卡
//                    or().like(UserExtract::getBankAddress, request.getKeywords()). //开户行
//                    or().like(UserExtract::getAlipayCode, request.getKeywords()). //支付宝
//                    or().like(UserExtract::getFailMsg, request.getKeywords()) //失败原因
//            );
//        }
//
//        //提现状态
//        if (request.getStatus() != null) {
//            lambdaQueryWrapper.eq(UserExtract::getStatus, request.getStatus());
//        }
//
//        //提现方式
//        if (!StringUtils.isBlank(request.getExtractType())) {
//            lambdaQueryWrapper.eq(UserExtract::getExtractType, request.getExtractType());
//        }
//
//        //时间范围
//        if (StringUtils.isNotBlank(request.getDateLimit())) {
//            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
//            lambdaQueryWrapper.between(UserExtract::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
//        }
//
//        //按创建时间降序排列
//        lambdaQueryWrapper.orderByDesc(UserExtract::getCreateTime, UserExtract::getId);
//
//        List<UserExtract> extractList = dao.selectList(lambdaQueryWrapper);
//        if (CollUtil.isEmpty(extractList)) {
//            return extractList;
//        }
//        List<Integer> uidList = extractList.stream().map(o -> o.getUid()).distinct().collect(Collectors.toList());
//        HashMap<Integer, User> userMap = userService.getMapListInUid(uidList);
//        for (UserExtract userExtract : extractList) {
//            userExtract.setNickName(Optional.ofNullable(userMap.get(userExtract.getUid()).getNickname()).orElse(""));
//        }
//        return extractList;
//    }
//
//    /**
//     * 提现总金额
//     * 总佣金 = 已提现佣金 + 未提现佣金
//     * 已提现佣金 = 用户成功提现的金额
//     * 未提现佣金 = 用户未提现的佣金 = 可提现佣金 + 冻结佣金 = 用户佣金
//     * 可提现佣金 = 包括解冻佣金、提现未通过的佣金 = 用户佣金 - 冻结期佣金
//     * 待提现佣金 = 待审核状态的佣金
//     * 冻结佣金 = 用户在冻结期的佣金，不包括退回佣金
//     * 退回佣金 = 因退款导致的冻结佣金退回
//     */
//    @Override
//    public BalanceResponse getBalance(String dateLimit) {
//        String startTime = "";
//        String endTime = "";
//        if (StringUtils.isNotBlank(dateLimit)) {
//            dateLimitUtilVo dateRage = DateUtil.getDateLimit(dateLimit);
//            startTime = dateRage.getStartTime();
//            endTime = dateRage.getEndTime();
//        }
//
//        // 已提现
//        BigDecimal withdrawn = getWithdrawn(startTime, endTime);
//        // 待提现(审核中)
//        BigDecimal toBeWithdrawn = getWithdrawning(startTime, endTime);
//
//        // 佣金总金额（单位时间）
//        BigDecimal commissionTotal = userBrokerageRecordService.getTotalSpreadPriceBydateLimit(dateLimit);
//        // 单位时间消耗的佣金
//        BigDecimal subWithdarw = userBrokerageRecordService.getSubSpreadPriceByDateLimit(dateLimit);
//        // 未提现
//        BigDecimal unDrawn = commissionTotal.subtract(subWithdarw);
//        return new BalanceResponse(withdrawn, unDrawn, commissionTotal, toBeWithdrawn);
//    }
//
//
//    /**
//     * 提现总金额
//
//     * @since 2020-05-11
//     * @return BalanceResponse
//     */
//    @Override
//    public BigDecimal getWithdrawn(String startTime, String endTime) {
//        return getSum(null, 1, startTime, endTime);
//    }
//
//    /**
//     * 审核中总金额
//
//     * @since 2020-05-11
//     * @return BalanceResponse
//     */
//    private BigDecimal getWithdrawning(String startTime, String endTime) {
//        return getSum(null, 0, startTime, endTime);
//    }
//
//    /**
//     * 根据状态获取总额
//     * @return BigDecimal
//     */
//    private BigDecimal getSum(Integer userId, int status, String startTime, String endTime) {
//        LambdaQueryWrapper<UserExtract> lqw = Wrappers.lambdaQuery();
//        if (null != userId) {
//            lqw.eq(UserExtract::getUid,userId);
//        }
//        lqw.eq(UserExtract::getStatus,status);
//        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
//            lqw.between(UserExtract::getCreateTime, startTime, endTime);
//        }
//        List<UserExtract> userExtracts = dao.selectList(lqw);
//        BigDecimal sum = ZERO;
//        if (CollUtil.isNotEmpty(userExtracts)) {
//            sum = userExtracts.stream().map(UserExtract::getExtractPrice).reduce(ZERO, BigDecimal::add);
//        }
//        return sum;
//    }

    /**
     * 获取用户对应的提现数据
     *
     * @param userId 用户id
     * @return 提现数据
     */
    @Override
    public UserClosing getUserExtractByUserId(Integer userId) {
        QueryWrapper<UserClosing> qw = new QueryWrapper<>();
        qw.select("SUM(closing_price) as closing_price,count(id) as id, uid");
        qw.eq("audit_status", ClosingConstant.CLOSING_AUDIT_STATUS_SUCCESS);
        qw.eq("account_status", ClosingConstant.CLOSING_ACCOUNT_STATUS_SUCCESS);
        qw.eq("uid", userId);
        qw.groupBy("uid");
        UserClosing userClosing = dao.selectOne(qw);
        if (ObjectUtil.isNull(userClosing)) {
            userClosing = new UserClosing();
            userClosing.setId(0);
            userClosing.setClosingPrice(BigDecimal.ZERO);
        }
        return userClosing;
    }

    /**
     * 获取某一天的所有数据
     * @param date 日期：年-月-日
     * @return List
     */
    public List<UserClosing> findByDate(String date) {
        LambdaQueryWrapper<UserClosing> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserClosing::getAuditStatus, ClosingConstant.CLOSING_AUDIT_STATUS_SUCCESS);
        lqw.eq(UserClosing::getAccountStatus, ClosingConstant.CLOSING_ACCOUNT_STATUS_SUCCESS);
        lqw.apply("date_format(update_time, '%Y-%m-%d') = {0}", date);
        return dao.selectList(lqw);
    }

//    /**
//     * 提现审核
//     *
//     * @param id          提现申请id
//     * @param status      审核状态 -1 未通过 0 审核中 1 已提现
//     * @param backMessage 驳回原因
//     * @return 审核结果
//     */
//    @Override
//    public Boolean updateStatus(Integer id, Integer status, String backMessage) {
//        if (status == -1 && StringUtils.isBlank(backMessage))
//            throw new CrmebException("驳回时请填写驳回原因");
//
//        UserExtract userExtract = getById(id);
//        if (ObjectUtil.isNull(userExtract)) {
//            throw new CrmebException("提现申请记录不存在");
//        }
//        if (userExtract.getStatus() != 0) {
//            throw new CrmebException("提现申请已处理过");
//        }
//        userExtract.setStatus(status);
//
//        User user = userService.getById(userExtract.getUid());
//        if (ObjectUtil.isNull(user)) {
//            throw new CrmebException("提现用户数据异常");
//        }
//
//        Boolean execute = false;
//
//        userExtract.setUpdateTime(cn.hutool.core.date.DateUtil.date());
//        // 拒绝
//        if (status == -1) {//未通过时恢复用户总金额
//            userExtract.setFailMsg(backMessage);
//            // 添加提现申请拒绝佣金记录
//            UserBrokerageRecord brokerageRecord = new UserBrokerageRecord();
//            brokerageRecord.setUid(user.getUid());
//            brokerageRecord.setLinkId(userExtract.getId().toString());
//            brokerageRecord.setLinkType(BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_WITHDRAW);
//            brokerageRecord.setType(BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD);
//            brokerageRecord.setTitle(BrokerageRecordConstants.BROKERAGE_RECORD_TITLE_WITHDRAW_FAIL);
//            brokerageRecord.setPrice(userExtract.getExtractPrice());
//            brokerageRecord.setBalance(user.getBrokeragePrice().add(userExtract.getExtractPrice()));
//            brokerageRecord.setMark(StrUtil.format("提现申请拒绝返还佣金{}", userExtract.getExtractPrice()));
//            brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
//            brokerageRecord.setCreateTime(DateUtil.nowDateTime());
//
//            execute = transactionTemplate.execute(e -> {
//                // 返还佣金
//                userService.operationBrokerage(userExtract.getUid(), userExtract.getExtractPrice(), user.getBrokeragePrice(), "add");
//                updateById(userExtract);
//                userBrokerageRecordService.save(brokerageRecord);
//                return Boolean.TRUE;
//            });
//        }
//
//        // 同意
//        if (status == 1) {
//            // 获取佣金提现记录
//            UserBrokerageRecord brokerageRecord = userBrokerageRecordService.getByLinkIdAndLinkType(userExtract.getId().toString(), BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_WITHDRAW);
//            if (ObjectUtil.isNull(brokerageRecord)) {
//                throw new CrmebException("对应的佣金记录不存在");
//            }
//            execute = transactionTemplate.execute(e -> {
//                updateById(userExtract);
//                brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
//                userBrokerageRecordService.updateById(brokerageRecord);
//                return Boolean.TRUE;
//            });
//        }
//        return execute;
//    }
//
//    /**
//     * 获取提现记录列表
//     * @param userId 用户uid
//     * @param pageParamRequest 分页参数
//     * @return PageInfo
//     */
//    @Override
//    public PageInfo<UserExtractRecordResponse> getExtractRecord(Integer userId, PageParamRequest pageParamRequest) {
//        Page<UserExtract> userExtractPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
//        QueryWrapper<UserExtract> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("uid", userId);
//
//        queryWrapper.groupBy("left(create_time, 7)");
//        queryWrapper.orderByDesc("left(create_time, 7)");
//        List<UserExtract> list = dao.selectList(queryWrapper);
//        if (CollUtil.isEmpty(list)) {
//            return new PageInfo<>();
//        }
//        ArrayList<UserExtractRecordResponse> userExtractRecordResponseList = CollectionUtil.newArrayList();
//        for (UserExtract userExtract : list) {
//            String date = DateUtil.dateToStr(userExtract.getCreateTime(), DateConstants.DATE_FORMAT_MONTH);
//            userExtractRecordResponseList.add(new UserExtractRecordResponse(date, getListByMonth(userId, date)));
//        }
//
//        return CommonPage.copyPageInfo(userExtractPage, userExtractRecordResponseList);
//    }
//
//    private List<UserExtract> getListByMonth(Integer userId, String date) {
//        QueryWrapper<UserExtract> queryWrapper = new QueryWrapper<>();
//        queryWrapper.select("id", "extract_price", "status", "create_time", "update_time");
//        queryWrapper.eq("uid", userId);
//        queryWrapper.apply(StrUtil.format(" left(create_time, 7) = '{}'", date));
//        queryWrapper.orderByDesc("create_time");
//        return dao.selectList(queryWrapper);
//    }
//
//    /**
//     * 获取用户提现总金额
//     * @param userId 用户uid
//     * @return BigDecimal
//     */
//    @Override
//    public BigDecimal getExtractTotalMoney(Integer userId) {
//        return getSum(userId, 1, null, null);
//    }
//
//
//    /**
//     * 提现申请
//     * @return Boolean
//     */
//    @Override
//    public Boolean extractApply(UserExtractRequest request) {
//        //添加判断，提现金额不能后台配置金额
//        String value = systemConfigService.getValueByKeyException(Constants.CONFIG_EXTRACT_MIN_PRICE);
//        BigDecimal ten = new BigDecimal(value);
//        if (request.getExtractPrice().compareTo(ten) < 0) {
//            throw new CrmebException(StrUtil.format("最低提现金额{}元", ten));
//        }
//
//        User user = userService.getInfo();
//        if (ObjectUtil.isNull(user)) {
//            throw new CrmebException("提现用户信息异常");
//        }
//        BigDecimal money = user.getBrokeragePrice();//可提现总金额
//        if (money.compareTo(ZERO) < 1) {
//            throw new CrmebException("您当前没有金额可以提现");
//        }
//
//        if (money.compareTo(request.getExtractPrice()) < 0) {
//            throw new CrmebException("你当前最多可提现 " + money + "元");
//        }
//
//        UserExtract userExtract = new UserExtract();
//        BeanUtils.copyProperties(request, userExtract);
//        userExtract.setUid(user.getUid());
//        userExtract.setBalance(money.subtract(request.getExtractPrice()));
//        //存入银行名称
//        if (StrUtil.isNotBlank(userExtract.getQrcodeUrl())) {
//            userExtract.setQrcodeUrl(systemAttachmentService.clearPrefix(userExtract.getQrcodeUrl()));
//        }
//
//        // 添加佣金记录
//        UserBrokerageRecord brokerageRecord = new UserBrokerageRecord();
//        brokerageRecord.setUid(user.getUid());
//        brokerageRecord.setLinkType(BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_WITHDRAW);
//        brokerageRecord.setType(BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_SUB);
//        brokerageRecord.setTitle(BrokerageRecordConstants.BROKERAGE_RECORD_TITLE_WITHDRAW_APPLY);
//        brokerageRecord.setPrice(userExtract.getExtractPrice());
//        brokerageRecord.setBalance(money.subtract(userExtract.getExtractPrice()));
//        brokerageRecord.setMark(StrUtil.format("提现申请扣除佣金{}", userExtract.getExtractPrice()));
//        brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_WITHDRAW);
//        brokerageRecord.setCreateTime(DateUtil.nowDateTime());
//
//        Boolean execute = transactionTemplate.execute(e -> {
//            // 保存提现记录
//            save(userExtract);
//            // 修改用户佣金
//            userService.operationBrokerage(user.getUid(), userExtract.getExtractPrice(), money, "sub");
//            // 添加佣金记录
//            brokerageRecord.setLinkId(userExtract.getId().toString());
//            userBrokerageRecordService.save(brokerageRecord);
//            return Boolean.TRUE;
//        });
//        // 此处可添加提现申请通知
//
//        return execute;
//    }
//
//    /**
//     * 修改提现申请
//     * @param id 申请id
//     * @param userExtractRequest 具体参数
//     */
//    @Override
//    public Boolean updateExtract(Integer id, UserExtractRequest userExtractRequest) {
//        UserExtract userExtract = new UserExtract();
//        BeanUtils.copyProperties(userExtractRequest, userExtract);
//        userExtract.setId(id);
//        return updateById(userExtract);
//    }
//
//    /**
//     * 提现申请待审核数量
//     * @return Integer
//     */
//    @Override
//    public Integer getNotAuditNum() {
//        LambdaQueryWrapper<UserExtract> lqw = Wrappers.lambdaQuery();
//        lqw.eq(UserExtract::getStatus, 0);
//        return dao.selectCount(lqw);
//    }
}

