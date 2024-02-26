package com.jbp.service.service.agent.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.config.CrmebConfig;
import com.jbp.common.constants.DateConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.*;
import com.jbp.common.vo.FundClearingVo;
import com.jbp.service.dao.agent.FundClearingDao;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WalletConfigService;
import com.jbp.service.service.agent.*;
import com.jbp.service.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class FundClearingServiceImpl extends ServiceImpl<FundClearingDao, FundClearing> implements FundClearingService {

    @Resource
    private OrdersFundSummaryService ordersFundSummaryService;
    @Resource
    private FundClearingItemConfigService fundClearingItemConfigService;
    @Resource
    private UserService userService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private PlatformWalletService platformWalletService;
    @Resource
    private WalletConfigService walletConfigService;
    @Resource
    private CrmebConfig crmebConfig;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private ChannelIdentityService channelIdentityService;
    @Resource
    private ChannelCardService channelCardService;

    @Override
    public PageInfo<FundClearing> pageList(String uniqueNo, String externalNo, Date startClearingTime, Date endClearingTime, Date starteCreateTime, Date endCreateTime, String status, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<FundClearing> lqw = new LambdaQueryWrapper<FundClearing>()
                .like(StringUtils.isNotEmpty(uniqueNo), FundClearing::getUniqueNo, uniqueNo)
                .like(StringUtils.isNotEmpty(externalNo), FundClearing::getExternalNo, externalNo)
                .eq(StringUtils.isNotEmpty(status), FundClearing::getStatus, status)
                .between(!ObjectUtil.isNull(startClearingTime) && !ObjectUtil.isNull(endClearingTime), FundClearing::getClearingTime, startClearingTime, endClearingTime)
                .between(!ObjectUtil.isNull(starteCreateTime) && !ObjectUtil.isNull(endCreateTime), FundClearing::getCreateTime, starteCreateTime, endCreateTime);
        Page<FundClearing> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<FundClearing> list = list(lqw);
        list.forEach(e -> {
            e.setAccount(e.getUserInfo().getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public FundClearing create(Integer uid, String externalNo, String commName, BigDecimal commAmt,
                               List<FundClearingItem> items, List<FundClearingProduct> productList, String description, String remark) {
        User user = userService.getById(uid);
        UserInfo userInfo = new UserInfo(user.getNickname(), user.getAccount());
        UserCapa userCapa = userCapaService.getByUser(uid);
        if (userCapa != null) {
            userInfo.setCapaId(userCapa.getCapaId());
            userInfo.setCapaName(userCapa.getCapaName());
        }
        UserCapaXs userCapaXs = userCapaXsService.getByUser(uid);
        if (userCapaXs != null) {
            userInfo.setCapaXsId(userCapaXs.getCapaId());
            userInfo.setCapaXsName(userCapaXs.getCapaName());
        }
        FundClearing fundClearing = new FundClearing(uid, externalNo, commName, commAmt, userInfo, items,
                productList, description, remark);
        save(fundClearing);

        // 更新概况
        ordersFundSummaryService.increaseCommAmt(externalNo, commAmt);
        return fundClearing;
    }

    @Override
    public List<FundClearing> getByUser(Integer uid, String commName, List<String> statusList) {
        QueryWrapper<FundClearing> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(FundClearing::getUid, uid)
                .eq(FundClearing::getCommName, commName)
                .in(FundClearing::getStatus, statusList);
        return list(queryWrapper);
    }

    @Override
    public void updateSendAmt(Long id, BigDecimal sendAmt, String remark) {
        FundClearing fundClearing = getById(id);
        if (fundClearing == null) {
            throw new CrmebException("发放记录不存在");
        }
        if (!Lists.newArrayList("已创建", "待审核").contains(fundClearing.getStatus())) {
            throw new CrmebException("只允许在 已创建  待审核 状态下修改发放金额");
        }
        BigDecimal orgSendAmt = fundClearing.getSendAmt();
        fundClearing.setSendAmt(sendAmt);
        fundClearing.setRemark(remark);
        Boolean ifSuccess = updateById(fundClearing);
        if (BooleanUtils.isNotTrue(ifSuccess)) {
            throw new CrmebException("当前操作人数过多");
        }

        if (ArithmeticUtils.gt(sendAmt, orgSendAmt)) {
            ordersFundSummaryService.increaseCommAmt(fundClearing.getExternalNo(), sendAmt.subtract(orgSendAmt));
        }
        if (ArithmeticUtils.less(sendAmt, orgSendAmt)) {
            ordersFundSummaryService.reduceCommAmt(fundClearing.getExternalNo(), orgSendAmt.subtract(sendAmt));
        }
    }

    @Override
    public void updateWaitAudit(String externalNo, String remark) {
        QueryWrapper<FundClearing> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(FundClearing::getExternalNo, externalNo)
                .eq(FundClearing::getStatus, FundClearing.Constants.已创建.toString());
        List<FundClearing> list = list(queryWrapper);
        for (FundClearing fundClearing : list) {
            fundClearing.setStatus(FundClearing.Constants.待审核.toString());
            fundClearing.setRemark(remark);
        }
        Boolean ifSuccess = updateBatchById(list);
        if (BooleanUtils.isNotTrue(ifSuccess)) {
            throw new CrmebException("当前操作人数过多");
        }
    }

    @Override
    public void updateWaitAudit(List<Long> ids, String remark) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new CrmebException("请选择佣金发放记录");
        }
        List<FundClearing> list = list(new QueryWrapper<FundClearing>().lambda().in(FundClearing::getId, ids).eq(FundClearing::getStatus, FundClearing.Constants.已创建.toString()));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<List<FundClearing>> partition = Lists.partition(list, 100);
        for (List<FundClearing> fundClearingList : partition) {
            for (FundClearing fundClearing : fundClearingList) {
                fundClearing.setStatus(FundClearing.Constants.待审核.toString());
                fundClearing.setRemark(remark);
            }
            Boolean ifSuccess = updateBatchById(fundClearingList);
            if (BooleanUtils.isNotTrue(ifSuccess)) {
                throw new CrmebException("当前操作人数过多");
            }
        }
    }

    @Override
    public void updateWaitSend(List<Long> ids, String remark) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new CrmebException("请选择佣金发放记录");
        }
        List<FundClearing> list = list(new QueryWrapper<FundClearing>().lambda().in(FundClearing::getId, ids).eq(FundClearing::getStatus, FundClearing.Constants.待审核.toString()));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<FundClearingItemConfig> configAllList = fundClearingItemConfigService.list();
        Map<String, List<FundClearingItemConfig>> configListMap = FunctionUtil.valueMap(configAllList, FundClearingItemConfig::getCommName);

        List<List<FundClearing>> partition = Lists.partition(list, 100);
        for (List<FundClearing> fundClearingList : partition) {
            for (FundClearing fundClearing : fundClearingList) {
                fundClearing.setStatus(FundClearing.Constants.待出款.toString());
                fundClearing.setRemark(remark);
                List<FundClearingItem> items = Lists.newArrayList();
                List<FundClearingItemConfig> configList = configListMap.get(fundClearing.getCommName());
                if (CollectionUtils.isEmpty(configList)) {
                    throw new CrmebException(fundClearing.getCommName() + "未配置出款规则" + fundClearing.getUniqueNo());
                }
                for (FundClearingItemConfig clearingItemConfig : configList) {
                    BigDecimal amt = clearingItemConfig.getScale().multiply(fundClearing.getSendAmt()).setScale(2, BigDecimal.ROUND_DOWN);
                    FundClearingItem item = new FundClearingItem(clearingItemConfig.getName(), clearingItemConfig.getWalletType(), amt);
                    items.add(item);
                }
                fundClearing.setItems(items);
            }
            updateBatchById(fundClearingList);
        }
    }

    @Override
    public void updateSend(List<Long> ids, String remark) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new CrmebException("请选择佣金发放记录");
        }
        List<FundClearing> list = list(new QueryWrapper<FundClearing>().lambda().in(FundClearing::getId, ids)
                .eq(FundClearing::getStatus, FundClearing.Constants.待出款.toString()));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        Map<Integer, WalletConfig> walletMap = walletConfigService.getWalletMap();
        Date now = DateTimeUtils.getNow();
        List<List<FundClearing>> partition = Lists.partition(list, 100);
        for (List<FundClearing> fundClearingList : partition) {
            for (FundClearing fundClearing : fundClearingList) {
                fundClearing.setStatus(FundClearing.Constants.已出款.toString());
                fundClearing.setClearingTime(now);
                fundClearing.setRemark(remark);
                List<FundClearingItem> items = fundClearing.getItems();
                if (CollectionUtils.isEmpty(items)) {
                    throw new CrmebException(fundClearing.getCommName() + "未配置出款规则" + fundClearing.getUniqueNo());
                }
                for (FundClearingItem item : items) {
                    WalletConfig walletConfig = walletMap.get(item.getWalletType());
                    if (walletConfig != null) {
                        platformWalletService.transferToUser(fundClearing.getUid(), item.getWalletType(), item.getAmt(), WalletFlow.OperateEnum.奖励.toString(), fundClearing.getUniqueNo(), fundClearing.getDescription());
                    }
                }
            }
            updateBatchById(fundClearingList);
        }
    }

    @Override
    public void updateCancel(List<Long> ids, String remark) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new CrmebException("请选择佣金发放记录");
        }
        List<String> status = Lists.newArrayList("已取消", "已出款");
        List<FundClearing> list = list(new QueryWrapper<FundClearing>().lambda().in(FundClearing::getId, ids).notIn(FundClearing::getStatus, status));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<List<FundClearing>> partition = Lists.partition(list, 100);
        for (List<FundClearing> fundClearingList : partition) {
            for (FundClearing fundClearing : fundClearingList) {
                fundClearing.setStatus(FundClearing.Constants.已取消.toString());
                fundClearing.setRemark(remark);
                // 减少金额
                ordersFundSummaryService.reduceCommAmt(fundClearing.getExternalNo(), fundClearing.getSendAmt());
            }
            updateBatchById(fundClearingList);
        }
    }

    @Override
    public void updateIntercept(List<Long> ids, String remark) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new CrmebException("请选择佣金发放记录");
        }
        List<FundClearing> list = list(new QueryWrapper<FundClearing>().lambda().in(FundClearing::getId, ids).in(FundClearing::getStatus, FundClearing.interceptStatus()));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<List<FundClearing>> partition = Lists.partition(list, 100);
        for (List<FundClearing> fundClearingList : partition) {
            for (FundClearing fundClearing : fundClearingList) {
                fundClearing.setStatus(FundClearing.Constants.已拦截.toString());
                fundClearing.setRemark(remark);
            }
            updateBatchById(fundClearingList);
        }
    }

    @Override
    public String exportFundClearing(String uniqueNo, String externalNo, Date startClearingTime, Date endClearingTime, Date startCreateTime, Date endCreateTime, String status) {
        String channelName = systemConfigService.getValueByKey("pay_channel_name");
        Long id = 0L;
        List<FundClearingVo> result = Lists.newArrayList();
        do {
            LambdaQueryWrapper<FundClearing> lqw = new LambdaQueryWrapper<FundClearing>()
                    .like(StringUtils.isNotEmpty(uniqueNo), FundClearing::getUniqueNo, uniqueNo)
                    .like(StringUtils.isNotEmpty(externalNo), FundClearing::getExternalNo, externalNo)
                    .eq(StringUtils.isNotEmpty(status), FundClearing::getStatus, status)
                    .between(!ObjectUtil.isNull(startClearingTime) && !ObjectUtil.isNull(endClearingTime), FundClearing::getClearingTime, startClearingTime, endClearingTime)
                    .between(!ObjectUtil.isNull(startCreateTime) && !ObjectUtil.isNull(endCreateTime), FundClearing::getCreateTime, startCreateTime, endCreateTime)
                    .gt(FundClearing::getId, id).last("LIMIT 1000");
            List<FundClearing> fundClearingList = list(lqw);
            if (CollectionUtils.isEmpty(fundClearingList)) {
                break;
            }
            List<Integer> uIdList = fundClearingList.stream().map(FundClearing::getUid).collect(Collectors.toList());

            Map<Integer, ChannelIdentity> channelIdentityMap = channelIdentityService.getChannelIdentityMap(uIdList, channelName);
            Map<Integer, ChannelCard> channelCardMap = channelCardService.getChannelCardMap(uIdList, channelName);
            Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
            fundClearingList.forEach(e -> {
                User user = uidMapList.get(e.getUid());
                e.setAccount(user != null ? user.getAccount() : "");
                FundClearingVo fundClearingVo = new FundClearingVo();
                BeanUtils.copyProperties(e, fundClearingVo);
                ChannelIdentity channelIdentity = channelIdentityMap.get(e.getUid());
                if(channelIdentity != null){
                    fundClearingVo.setRealName(channelIdentity.getRealName());
                    fundClearingVo.setIdCardNo(channelIdentity.getIdCardNo());
                }
                ChannelCard channelCard = channelCardMap.get(e.getUid());
                if(channelCard != null){
                    fundClearingVo.setPhone(channelCard.getPhone());
                    fundClearingVo.setBankName(channelCard.getBankName());
                    fundClearingVo.setBankCode(channelCard.getBankCardNo());
                }
                result.add(fundClearingVo);
            });
            id = fundClearingList.get(fundClearingList.size() - 1).getId();
        } while (true);

//       以下为存储部分
        //上传设置
        UploadUtil.setHzwServerPath((crmebConfig.getImagePath() + "/").replace(" ", "").replace("//", "/"));
        //文件名
        String fileName = "佣金发放记录导出".concat(CrmebDateUtil.nowDateTime(DateConstants.DATE_TIME_FORMAT_NUM)).concat(CrmebUtil.randomCount(111111111, 999999999).toString()).concat(".xlsx");
        //自定义标题别名
        LinkedHashMap<String, String> aliasMap = new LinkedHashMap<>();
        aliasMap.put("account", "用户账户");
        aliasMap.put("realName", "真实姓名");
        aliasMap.put("idCardNo", "身份证号");
        aliasMap.put("bankName", "银行名称");
        aliasMap.put("bankCode", "银行卡号");
        aliasMap.put("phone", "预留手机");
        aliasMap.put("uniqueNo", "流水单号");
        aliasMap.put("externalNo", "外部单号");
        aliasMap.put("commName", "佣金名称");
        aliasMap.put("commAmt", "结算佣金");
        aliasMap.put("sendAmt", "实发金额");
        aliasMap.put("description", "描述");
        aliasMap.put("status", "结算状态");
        aliasMap.put("remark", "备注");
        aliasMap.put("clearingTime", "结算时间");
        aliasMap.put("createTime", "创建时间");
        return ExportUtil.exportExcel(fileName, "佣金发放记录导出", result, aliasMap);
    }

    @Override
    public void updateRemark(Long id, String remark) {
        LambdaUpdateWrapper<FundClearing> luw = new LambdaUpdateWrapper<FundClearing>()
                .eq(FundClearing::getId,id)
                .set(FundClearing::getRemark, remark);
        update(luw);
    }
}
