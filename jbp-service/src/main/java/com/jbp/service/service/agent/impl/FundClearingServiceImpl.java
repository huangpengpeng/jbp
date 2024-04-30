package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.common.vo.FundClearingVo;
import com.jbp.service.dao.agent.FundClearingDao;
import com.jbp.service.product.comm.CommAliasNameEnum;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WalletConfigService;
import com.jbp.service.service.agent.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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
    private SystemConfigService systemConfigService;
    @Resource
    private FundClearingDao fundClearingDao;
    @Resource
    private WalletFlowService walletFlowService;
    @Resource
    private WalletService walletService;
    @Resource
    private Environment environment;

    @Override
    public PageInfo<FundClearing> pageList(String uniqueNo, String externalNo, Date startClearingTime, Date endClearingTime,
                                           Date starteCreateTime, Date endCreateTime, String status, Integer uid,
                                           String teamName, String description, String commName, Boolean ifRefund, PageParamRequest pageParamRequest) {
        Page<FundClearing> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<FundClearing> list = fundClearingDao.pageList(uniqueNo, externalNo, startClearingTime, endClearingTime, starteCreateTime, endCreateTime, status, uid, teamName, description, commName, ifRefund);
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public FundClearing create(Integer uid, String externalNo, String commName, BigDecimal commAmt, List<FundClearingProduct> productList, String description, String remark) {
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
        FundClearing fundClearing = new FundClearing(uid, externalNo, commName, commAmt, userInfo, null,
                productList, description, remark);
        // 获取发放信息
        List<FundClearingItem> items = createItemList(fundClearing, fundClearingItemConfigService.getMap());
        fundClearing.setItems(items);
        save(fundClearing);
        String dsh = systemConfigService.getValueByKey("fund_clearing_status_dsh");
        if (StringUtils.isNotEmpty(dsh) && "1".equals(dsh)) {
            updateWaitAudit(externalNo, "平台开启自动待审核");
        }
        // 更新概况
        ordersFundSummaryService.increaseCommAmt(externalNo, commAmt);
        return fundClearing;
    }

    @Override
    public List<FundClearing> getByExternalNo(String externalNo, List<String> statusList) {
        QueryWrapper<FundClearing> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(FundClearing::getExternalNo, externalNo)
                .in(FundClearing::getStatus, statusList);
        return list(queryWrapper);
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
        if (!Lists.newArrayList("已创建", "待审核", "待发放").contains(fundClearing.getStatus())) {
            throw new CrmebException("只允许在 已创建  待审核 状态下修改发放金额");
        }
        BigDecimal orgSendAmt = fundClearing.getSendAmt();
        fundClearing.setSendAmt(sendAmt);
        fundClearing.setRemark(remark);
        List<FundClearingItem> items = getItemList(fundClearing, fundClearingItemConfigService.getMap());
        fundClearing.setItems(items);

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
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        Map<String, List<FundClearingItemConfig>> configListMap = fundClearingItemConfigService.getMap();

        for (FundClearing fundClearing : list) {
            fundClearing.setStatus(FundClearing.Constants.待审核.toString());
            fundClearing.setRemark(remark);
            fundClearing.setItems(getItemList(fundClearing, configListMap));
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
        List<FundClearingItemConfig> configAllList = fundClearingItemConfigService.list();
        Map<String, List<FundClearingItemConfig>> configListMap = FunctionUtil.valueMap(configAllList, FundClearingItemConfig::getCommName);

        List<List<FundClearing>> partition = Lists.partition(list, 100);

        for (List<FundClearing> fundClearingList : partition) {
            for (FundClearing fundClearing : fundClearingList) {
                fundClearing.setStatus(FundClearing.Constants.待审核.toString());
                fundClearing.setRemark(remark);
                final List<FundClearingItem> items = getItemList(fundClearing, configListMap);
                fundClearing.setItems(items);
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
        Map<String, List<FundClearingItemConfig>> configListMap = fundClearingItemConfigService.getMap();

        List<List<FundClearing>> partition = Lists.partition(list, 100);
        for (List<FundClearing> fundClearingList : partition) {
            for (FundClearing fundClearing : fundClearingList) {
                fundClearing.setStatus(FundClearing.Constants.待出款.toString());
                fundClearing.setRemark(remark);
                List<FundClearingItem> items = getItemList(fundClearing, configListMap);
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
        Map<String, List<FundClearingItemConfig>> configListMap = fundClearingItemConfigService.getMap();

        Map<Integer, WalletConfig> walletMap = walletConfigService.getWalletMap();
        Date now = DateTimeUtils.getNow();
        List<List<FundClearing>> partition = Lists.partition(list, 100);
        for (List<FundClearing> fundClearingList : partition) {
            for (FundClearing fundClearing : fundClearingList) {
                fundClearing.setStatus(FundClearing.Constants.已出款.toString());
                fundClearing.setClearingTime(now);
                fundClearing.setRemark(remark);
                List<FundClearingItem> items = getItemList(fundClearing, configListMap);
                fundClearing.setItems(items);
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
    public void updateIfRefund(List<Long> ids, String remark) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new CrmebException("请选择佣金发放记录");
        }
        List<FundClearing> list = list(new QueryWrapper<FundClearing>().lambda().
                in(FundClearing::getId, ids).eq(FundClearing::getStatus, FundClearing.Constants.已出款).eq(FundClearing::getIfRefund, false));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (FundClearing fundClearing : list) {
            if(fundClearing.getIfRefund() != null && fundClearing.getIfRefund()){
                throw new RuntimeException(fundClearing.getUniqueNo()+"已经退回请勿重复操作");
            }
            List<WalletFlow> walletFlows = walletFlowService.getByUser(fundClearing.getUid(), fundClearing.getUniqueNo(), WalletFlow.OperateEnum.奖励.toString(), WalletFlow.ActionEnum.收入.name());
            if (CollectionUtils.isNotEmpty(walletFlows)) {
                for (WalletFlow walletFlow : walletFlows) {
                    walletService.transferToPlatform(walletFlow.getUid(), walletFlow.getWalletType(), walletFlow.getAmt(),
                            WalletFlow.OperateEnum.退款.toString(), fundClearing.getUniqueNo(), "订单退款回退");
                }
            }
            fundClearing.setIfRefund(true);
            fundClearing.setRemark(remark);
            updateById(fundClearing);
        }

    }

    @Override
    public List<FundClearingVo> exportFundClearing(String uniqueNo, String externalNo, Date startClearingTime, Date endClearingTime, Date starteCreateTime, Date endCreateTime, String status, Integer uid, String teamName, String description, String commName, Boolean ifRefund) {
        String channelName = systemConfigService.getValueByKey("pay_channel_name");
        List<FundClearingVo> fundClearingVos = fundClearingDao.exportFundClearing(uniqueNo, externalNo, startClearingTime, endClearingTime, starteCreateTime, endCreateTime, status, uid, teamName, description, 0L, channelName, commName, ifRefund);
        return fundClearingVos;
    }

    @Override
    public void updateRemark(Long id, String remark) {
        LambdaUpdateWrapper<FundClearing> luw = new LambdaUpdateWrapper<FundClearing>()
                .eq(FundClearing::getId, id)
                .set(FundClearing::getRemark, remark);
        update(luw);
    }

    @Override
    public Map<String, Object> totalGet(Integer uid) {

        String openWaitStatus = environment.getProperty("platform.openWaitStatus");

        BigDecimal wallet_pay_integral = new BigDecimal(systemConfigService.getValueByKey(SysConfigConstants.WALLET_PAY_INTEGRAl));
        Map<String, Object> map = new HashMap<>();
        LambdaQueryWrapper<FundClearing> issue = new LambdaQueryWrapper<FundClearing>()
                .eq(FundClearing::getUid, uid);
        if(StringUtils.isNotEmpty(openWaitStatus)){
            issue.in(FundClearing::getStatus, FundClearing.Constants.待审核, FundClearing.Constants.待出款, FundClearing.Constants.已创建);
        }else{
            issue.in(FundClearing::getStatus, FundClearing.Constants.待审核, FundClearing.Constants.待出款);
        }
        BigDecimal count = list(issue).stream().map(FundClearing::getSendAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
        map.put("issue", count.multiply(wallet_pay_integral));
        LambdaQueryWrapper<FundClearing> completed = new LambdaQueryWrapper<FundClearing>()
                .eq(FundClearing::getUid, uid)
                .eq(FundClearing::getStatus, FundClearing.Constants.已出款);
        BigDecimal completedCount = list(completed).stream().map(FundClearing::getSendAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
        map.put("completed", completedCount.multiply(wallet_pay_integral));
        return map;
    }

    @Override
    public PageInfo<FundClearing> flowGet(Integer uid, Integer headerStatus, PageParamRequest pageParamRequest) {
        BigDecimal wallet_pay_integral = new BigDecimal(systemConfigService.getValueByKey(SysConfigConstants.WALLET_PAY_INTEGRAl));

        LambdaQueryWrapper<FundClearing> lqw = new LambdaQueryWrapper<>();
        setFundClearingWrapperByStatus(lqw, uid, headerStatus);
        lqw.orderByDesc(FundClearing::getId);
        Page<FundClearing> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<FundClearing> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, Lists.newArrayList());
        }
        Map<Integer, WalletConfig> walletMap = walletConfigService.getWalletMap();
        walletMap.put(-1, new WalletConfig().setName("管理费"));
        walletMap.put(-2, new WalletConfig().setName("手续费"));
        list.forEach(e -> {
            e.setSendAmt(e.getSendAmt().multiply(wallet_pay_integral));
            e.setDescription(CommAliasNameEnum.getAliasNameByName(e.getCommName()));
            for (FundClearingItem item : e.getItems()) {
                WalletConfig walletConfig = walletMap.get(item.getWalletType());
                item.setWalletName(walletConfig != null ? walletConfig.getName() : "");
                item.setAmt(item.getAmt().multiply(wallet_pay_integral));
            }
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public boolean hasCreate(String orderNo, String commName) {
        return getOne(new QueryWrapper<FundClearing>().lambda().eq(FundClearing::getExternalNo, orderNo).eq(FundClearing::getCommName, commName).last(" limit 1")) != null;
    }

    @Override
    public BigDecimal getSendCommAmt(Integer uid, Date start, Date end, String ...commName) {
        QueryWrapper<FundClearing> lw = new QueryWrapper<>();
        lw.select("SUM(comm_amt) AS send_amt ");
        lw.lambda()
                .eq(FundClearing::getUid, uid)
                .eq(FundClearing::getIfRefund, false)
                .ge(start != null, FundClearing::getCreateTime, start)
                .le(end != null, FundClearing::getCreateTime, end)
                .in(FundClearing::getCommName, commName)
                .notIn(FundClearing::getStatus, Lists.newArrayList(FundClearing.Constants.已取消.toString(), FundClearing.Constants.已拦截.toString()))
        ;
        FundClearing one = getOne(lw);
        return one == null ? BigDecimal.ZERO : one.getSendAmt();
    }

    @Override
    public BigDecimal getUserTotal(Integer uid) {
        return fundClearingDao.getUserTotal(uid);
    }
    @Override
    public BigDecimal getUserTotalMonth(Integer uid,String month) {
        return fundClearingDao.getUserTotalMonth(uid,month);
    }

    @Override
    public   List<Map<String,Object>>  getUserTotalMonthList(Integer uid,String month) {
        return fundClearingDao.getUserTotalMonthList(uid,month);
    }

    @Override
    public BigDecimal getUserTotalContMonth(Integer uid, String month) {
        return fundClearingDao.getUserTotalContMonth(uid,month);
    }

    @Override
    public BigDecimal getUserTotalDay(Integer uid,String day) {
        return fundClearingDao.getUserTotalDay(uid,day);
    }

    @Override
    public void init() {
        QueryWrapper<FundClearing> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(FundClearing::getStatus, FundClearing.Constants.已创建.toString());
        List<FundClearing> list = list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        Map<String, List<FundClearingItemConfig>> configListMap = fundClearingItemConfigService.getMap();
        int i = 0;
        for (FundClearing fundClearing : list) {
            i++;
            if (fundClearing.getUid() != null) {
                fundClearing.setItems(getItemList(fundClearing, configListMap));
                Boolean ifSuccess = updateById(fundClearing);
                if (BooleanUtils.isNotTrue(ifSuccess)) {
                    throw new CrmebException("当前操作人数过多");
                }
            }

            log.info("当前执行条数:{}, 总条数:{}", i, list.size());
        }


    }

    private static List<FundClearingItem> createItemList(FundClearing fundClearing, Map<String, List<FundClearingItemConfig>> configListMap) {
        List<FundClearingItem> items = Lists.newArrayList();
        List<FundClearingItemConfig> configList = configListMap.get(fundClearing.getCommName());
        if (CollectionUtils.isEmpty(configList)) {
            return items;
        }
        for (FundClearingItemConfig clearingItemConfig : configList) {
            BigDecimal amt = clearingItemConfig.getScale().multiply(fundClearing.getSendAmt()).setScale(2, BigDecimal.ROUND_DOWN);
            if (ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {
                FundClearingItem item = new FundClearingItem(clearingItemConfig.getName(), clearingItemConfig.getWalletType(), amt);
                items.add(item);
            }
        }
        return items;
    }

    private static List<FundClearingItem> getItemList(FundClearing fundClearing, Map<String, List<FundClearingItemConfig>> configListMap) {
        List<FundClearingItem> items = Lists.newArrayList();
        List<FundClearingItemConfig> configList = configListMap.get(fundClearing.getCommName());
        if (CollectionUtils.isEmpty(configList)) {
            throw new CrmebException(fundClearing.getCommName() + "未配置出款规则" + fundClearing.getUniqueNo());
        }
        for (FundClearingItemConfig clearingItemConfig : configList) {
            BigDecimal amt = clearingItemConfig.getScale().multiply(fundClearing.getSendAmt()).setScale(2, BigDecimal.ROUND_DOWN);
            if (ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {
                FundClearingItem item = new FundClearingItem(clearingItemConfig.getName(), clearingItemConfig.getWalletType(), amt);
                items.add(item);
            }
        }
        return items;
    }

    public void setFundClearingWrapperByStatus(LambdaQueryWrapper<FundClearing> lqw, Integer uid, Integer headerStatus) {
        lqw.eq(FundClearing::getUid, uid);
        String openWaitStatus = environment.getProperty("platform.openWaitStatus");

        switch (headerStatus) {
            case 0:
                if(StringUtils.isNotEmpty(openWaitStatus)){
                    lqw.in(FundClearing::getStatus, FundClearing.Constants.已创建, FundClearing.Constants.待审核, FundClearing.Constants.待出款, FundClearing.Constants.已出款);
                }else{
                    lqw.in(FundClearing::getStatus, FundClearing.Constants.待审核, FundClearing.Constants.待出款, FundClearing.Constants.已出款);
                }
                break;
            case 1:
                if(StringUtils.isNotEmpty(openWaitStatus)){
                    lqw.in(FundClearing::getStatus, FundClearing.Constants.已创建, FundClearing.Constants.待审核, FundClearing.Constants.待出款);
                }else{
                    lqw.in(FundClearing::getStatus, FundClearing.Constants.待审核, FundClearing.Constants.待出款);
                }
                break;
            case 2:
                lqw.eq(FundClearing::getStatus, FundClearing.Constants.已出款);
                break;
            default:
                break;
        }
    }
}
