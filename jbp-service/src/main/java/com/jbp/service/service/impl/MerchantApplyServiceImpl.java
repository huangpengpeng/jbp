package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.MerchantConstants;
import com.jbp.common.constants.NotifyConstants;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.merchant.MerchantApply;
import com.jbp.common.model.system.SystemNotification;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.merchant.*;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.common.vo.LoginUserVo;
import com.jbp.service.dao.MerchantApplyDao;
import com.jbp.service.service.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * StoreApplyServiceImpl 接口实现
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
public class MerchantApplyServiceImpl extends ServiceImpl<MerchantApplyDao, MerchantApply> implements MerchantApplyService {

    @Resource
    private MerchantApplyDao dao;

    private final Logger logger = LoggerFactory.getLogger(MerchantApplyServiceImpl.class);

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private SystemAdminService adminService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private SystemNotificationService systemNotificationService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 分页列表
     * @param searchRequest 查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantApply> getAdminPage(MerchantApplySearchRequest searchRequest, PageParamRequest pageParamRequest) {
        Page<MerchantApply> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<MerchantApply> lqw = Wrappers.lambdaQuery();
        if (ObjectUtil.isNotNull(searchRequest.getCategoryId())) {
            lqw.eq(MerchantApply::getCategoryId, searchRequest.getCategoryId());
        }
        if (ObjectUtil.isNotNull(searchRequest.getTypeId())) {
            lqw.eq(MerchantApply::getTypeId, searchRequest.getTypeId());
        }
        if (ObjectUtil.isNotNull(searchRequest.getAuditStatus())) {
            lqw.eq(MerchantApply::getAuditStatus, searchRequest.getAuditStatus());
        }
        if (StrUtil.isNotBlank(searchRequest.getKeywords())) {
            String keywords = URLUtil.decode(searchRequest.getKeywords());
            lqw.and(i -> i.like(MerchantApply::getName, keywords).or().like(MerchantApply::getKeywords, keywords));
        }
        if (StrUtil.isNotBlank(searchRequest.getDateLimit())) {
            DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(searchRequest.getDateLimit());
            lqw.between(MerchantApply::getCreateTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        lqw.orderByDesc(MerchantApply::getId);
        List<MerchantApply> applyList = dao.selectList(lqw);
        if (CollUtil.isEmpty(applyList)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        return CommonPage.copyPageInfo(page, applyList);
    }

    /**
     * 入驻审核
     * @param request 审核参数
     * @return Boolean
     */
    @Override
    public Boolean audit(MerchantApplyAuditRequest request) {
        if (request.getAuditStatus().equals(MerchantConstants.AUDIT_STATUS_FAIL) && StrUtil.isEmpty(request.getDenialReason())) {
            throw new CrmebException("审核拒绝，拒绝原因不能为空");
        }
        MerchantApply merchantApply = getByIdException(request.getId());
        if (!merchantApply.getAuditStatus().equals(MerchantConstants.AUDIT_STATUS_WAIT)) {
            throw new CrmebException("申请已审核");
        }
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        // 审核拒绝
        if (request.getAuditStatus().equals(MerchantConstants.AUDIT_STATUS_FAIL)) {
            merchantApply.setAuditStatus(MerchantConstants.AUDIT_STATUS_FAIL);
            merchantApply.setDenialReason(request.getDenialReason());
            merchantApply.setAuditorId(loginUserVo.getUser().getId());
            Boolean update = dao.updateById(merchantApply) > 0;
            if (update) {
                SystemNotification payNotification = systemNotificationService.getByMark(NotifyConstants.AUDIT_FAIL_MARK);
                // 发送短信
                if (StrUtil.isNotBlank(merchantApply.getPhone()) && payNotification.getIsSms().equals(1)) {
                    try {
                        smsService.sendMerchantFileSuccessNotice(merchantApply.getPhone(), DateUtil.date().toString(),
                                merchantApply.getName(),"");
                    } catch (Exception e) {
                        logger.error("商户入驻审核失败短信发送异常，{}", e.getMessage());
                    }
                }
            }
            return update;
        }
        // 审核成功，初始化商户
        merchantApply.setAuditStatus(MerchantConstants.AUDIT_STATUS_SUCCESS);
        merchantApply.setAuditorId(loginUserVo.getUser().getId());
        Boolean execute = transactionTemplate.execute(e -> {
            int update = dao.updateById(merchantApply);
            if (update <= 0) {
                logger.error("商户入驻审核成功事务失败！准备回滚");
                e.setRollbackOnly();
            }
            MerchantAddRequest merchantAddRequest = merchantInit(merchantApply);
            Boolean auditSuccess = merchantService.auditSuccess(merchantAddRequest, merchantApply.getAuditorId());
            if (!auditSuccess) {
                logger.error("商户入驻审核成功事务失败！准备回滚");
                e.setRollbackOnly();
            }
            return Boolean.TRUE;
        });
        if (execute) {
            SystemNotification payNotification = systemNotificationService.getByMark(NotifyConstants.AUDIT_SUCCESS_MARK);
            // 发送短信
            if (StrUtil.isNotBlank(merchantApply.getPhone()) && payNotification.getIsSms().equals(1)) {
                String merSiteUrl = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_SITE_URL);
                if (StrUtil.isBlank(merSiteUrl)) {
                    merSiteUrl = "";
                }
                try {
                    smsService.sendMerchantAuditSuccessNotice(merchantApply.getPhone(), DateUtil.date().toString(),
                            merchantApply.getName(), merchantApply.getPhone(),
                            "000000",
                            merSiteUrl);
                } catch (Exception e) {
                    logger.error("商户入驻审核成功短信发送异常，{}", e.getMessage());
                }
            }
        }
        return execute;
    }

    /**
     * 备注
     * @param request 备注参数
     * @return Boolean
     */
    @Override
    public Boolean remark(MerchantApplyRemarkRequest request) {
        MerchantApply merchantApply = getByIdException(request.getId());
        merchantApply.setRemark(request.getRemark());
        return dao.updateById(merchantApply) > 0;
    }

    /**
     * 商户入驻申请
     * @param request 申请参数
     * @return Boolean
     */
    @Override
    public Boolean settledApply(MerchantSettledApplyRequest request) {
        Integer uid = userService.getUserIdException();
        // 检查商户名or商户账号or商户邮箱是否重复
        if (checkMerchantName(request.getName())) {
            throw new CrmebException("商户名称已存在");
        }
        if (checkMerchantPhone(request.getPhone())) {
            throw new CrmebException("商户手机号已存在");
        }
        if (adminService.checkAccount(request.getPhone())) {
            throw new CrmebException("商户手机号已存在");
        }
        MerchantApply merchantApply = new MerchantApply();
        BeanUtils.copyProperties(request, merchantApply);
        merchantApply.setQualificationPicture(systemAttachmentService.clearPrefix(request.getQualificationPicture()));
        merchantApply.setUid(uid);
        return save(merchantApply);
    }

    /**
     * 商户入驻申请记录
     * @param uid 申请用户id
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public PageInfo<MerchantApply> findSettledRecord(Integer uid, PageParamRequest pageParamRequest) {
        Page<MerchantApply> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<MerchantApply> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantApply::getUid, uid);
        lqw.orderByDesc(MerchantApply::getId);
        List<MerchantApply> applyList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, applyList);
    }

    /**
     * 商户名称是否存在
     * @param name 商户名
     * @return Boolean
     */
    @Override
    public Boolean checkMerchantName(String name) {
        LambdaQueryWrapper<MerchantApply> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantApply::getName, name);
        lqw.eq(MerchantApply::getAuditStatus, MerchantConstants.AUDIT_STATUS_WAIT);
        lqw.last(" limit 1");
        MerchantApply merchantApply = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(merchantApply);
    }

    /**
     * 商户手机号是否存在
     * @param phone 手机号
     * @return Boolean
     */
    @Override
    public Boolean checkMerchantPhone(String phone) {
        LambdaQueryWrapper<MerchantApply> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantApply::getPhone, phone);
        lqw.eq(MerchantApply::getAuditStatus, MerchantConstants.AUDIT_STATUS_WAIT);
        lqw.last(" limit 1");
        MerchantApply merchantApply = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(merchantApply);
    }

    private MerchantAddRequest merchantInit(MerchantApply merchantApply) {
        MerchantAddRequest merchant = new MerchantAddRequest();
        BeanUtils.copyProperties(merchantApply, merchant);
        return merchant;
    }

    private MerchantApply getByIdException(Integer id) {
        MerchantApply merchantApply = getById(id);
        if (ObjectUtil.isNull(merchantApply)) {
            throw new CrmebException("审核单不存在");
        }
        return merchantApply;
    }
}

