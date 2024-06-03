package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.beust.jcommander.internal.Sets;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.excel.FundClearingExcel;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.tank.TankOrders;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.FundClearingImportRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.agent.FundClearingDao;
import com.jbp.service.product.comm.*;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WalletConfigService;
import com.jbp.service.service.agent.*;
import com.jbp.service.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class FundClearingServiceImpl extends ServiceImpl<FundClearingDao, FundClearing> implements FundClearingService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


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
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private OrderService orderService;
    @Resource
    private CapaXsService capaXsService;
    @Resource
    private ProductCommConfigService productCommConfigService;



    @Override
    public PageInfo<FundClearing> pageList(String uniqueNo, String externalNo, Date startClearingTime, Date endClearingTime,
                                           Date starteCreateTime, Date endCreateTime, String status, Integer uid,
                                           String teamName, String description, String commName, Boolean ifRefund, List<String> orderList, PageParamRequest pageParamRequest) {
        Page<FundClearing> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<FundClearing> list = fundClearingDao.pageList(uniqueNo, externalNo, startClearingTime, endClearingTime, starteCreateTime, endCreateTime, status, uid, teamName, description, commName, ifRefund, orderList);
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
                .in(FundClearing::getStatus, FundClearing.Constants.已创建.toString(), FundClearing.Constants.已拦截.toString());
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
        List<FundClearing> list = list(new QueryWrapper<FundClearing>().lambda().in(FundClearing::getId, ids)
                .in(FundClearing::getStatus, FundClearing.Constants.已创建.toString(), FundClearing.Constants.已拦截.toString()));
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

        Boolean ifHdf = false;
        String name = environment.getProperty("spring.profiles.active");
        if(name.contains("hdf")){
            ifHdf = true;
        }
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
                        if (ifHdf) {
                            Map<String, Object> walletDebt = SqlRunner.db().selectOne(" select * from eb_wallet_debt where uid= {0} and type={1}", fundClearing.getUid(), walletConfig.getType());
                            BigDecimal debtAmt = walletDebt == null ? BigDecimal.ZERO : BigDecimal.valueOf(MapUtils.getDouble(walletDebt, "usable_amt"));
                            if (ArithmeticUtils.gt(debtAmt, BigDecimal.ZERO)) {
                                BigDecimal min = BigDecimal.valueOf(Math.min(debtAmt.doubleValue(), item.getAmt().doubleValue()));
                                SqlRunner.db().update("update eb_wallet_debt set usable_amt = usable_amt - {0} where id= {1} ", min, walletDebt.get("id"));
                                BigDecimal realAmt = item.getAmt().subtract(min);
                                if (ArithmeticUtils.gt(realAmt, BigDecimal.ZERO)) {
                                    platformWalletService.transferToUser(fundClearing.getUid(), item.getWalletType(), realAmt, WalletFlow.OperateEnum.奖励.toString(), fundClearing.getUniqueNo(), fundClearing.getDescription() + "[发放金额:" + item.getAmt() + " 补缴金额:" + min + "]");
                                }
                                fundClearing.setDescription(fundClearing.getDescription()+ "[发放金额:" + item.getAmt() + " 补缴金额:" + min + "]");
                            } else {
                                platformWalletService.transferToUser(fundClearing.getUid(), item.getWalletType(), item.getAmt(), WalletFlow.OperateEnum.奖励.toString(), fundClearing.getUniqueNo(), fundClearing.getDescription());
                            }
                        } else {
                            platformWalletService.transferToUser(fundClearing.getUid(), item.getWalletType(), item.getAmt(), WalletFlow.OperateEnum.奖励.toString(), fundClearing.getUniqueNo(), fundClearing.getDescription());
                        }

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

        StringBuilder msg = new StringBuilder();
        Set<Integer> uidSet = new HashSet<>();
        Table<Integer, Integer, BigDecimal> tab = HashBasedTable.create();
        for (FundClearing fundClearing : list) {
            if (fundClearing.getIfRefund() != null && fundClearing.getIfRefund()) {
                msg.append(fundClearing.getUniqueNo()).append(",");
            }
            uidSet.add(fundClearing.getUid());
            List<WalletFlow> walletFlows = walletFlowService.getByUser(fundClearing.getUid(), fundClearing.getUniqueNo(), WalletFlow.OperateEnum.奖励.toString(), WalletFlow.ActionEnum.收入.name());
            if (CollectionUtils.isNotEmpty(walletFlows)) {
                for (WalletFlow walletFlow : walletFlows) {
                    BigDecimal amt = tab.get(walletFlow.getUid(), walletFlow.getWalletType());
                    amt = amt == null ? BigDecimal.ZERO : amt;// 需要回退金额
                    amt = walletFlow.getAmt().add(amt);
                    tab.put(walletFlow.getUid(), walletFlow.getWalletType(), amt);
                }
            }
        }

        if(StringUtils.isNotEmpty(msg.toString())){
            throw new RuntimeException(msg.toString() + "已经退回请勿重复操作");
        }

        Map<Integer, User> userMap = userService.getUidMapList(uidSet.stream().collect(Collectors.toList()));
        Map<Integer, WalletConfig> walletMap = walletConfigService.getWalletMap();
        // 遍历
        Set<Table.Cell<Integer, Integer, BigDecimal>> cells = tab.cellSet();
        for (Table.Cell<Integer, Integer, BigDecimal> cell : cells) {
            Integer uid = cell.getRowKey();
            Integer walletType = cell.getColumnKey();
            BigDecimal amt = cell.getValue();
            Wallet wallet = walletService.getByUser(uid, walletType);
            BigDecimal balance = wallet == null ? BigDecimal.ZERO : wallet.getBalance();
            if (ArithmeticUtils.less(balance, amt)) {
                WalletConfig walletConfig = walletMap.get(walletType);
                BigDecimal subtract = amt.subtract(balance);
                msg.append("[").append(userMap.get(uid).getAccount()).append("|").append(walletConfig.getName()).append("积分不足差额:").append(subtract).append("]");
            }
        }
        if(StringUtils.isNotEmpty(msg.toString())){
            throw new RuntimeException(msg.toString());
        }

        for (FundClearing fundClearing : list) {
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
    public List<FundClearingExcel> exportFundClearing(String uniqueNo, String externalNo, Date startClearingTime, Date endClearingTime, Date starteCreateTime, Date endCreateTime, String status, Integer uid, String teamName, String description, String commName, Boolean ifRefund , List<String> orderList) {
        String channelName = systemConfigService.getValueByKey("pay_channel_name");
        List<FundClearingExcel> fundClearingVos = fundClearingDao.exportFundClearing(uniqueNo, externalNo, startClearingTime, endClearingTime, starteCreateTime, endCreateTime, status, uid, teamName, description, 0L, channelName, commName, ifRefund, orderList);
        String name = environment.getProperty("spring.profiles.active");
        if(name.contains("sm") || name.contains("yk")  || name.contains("tf") || name.contains("qy") ){
            for (FundClearingExcel fundClearingVo : fundClearingVos) {
                fundClearingVo.setIdCardNo("");
                fundClearingVo.setPhone("");
                fundClearingVo.setBankName("");
                fundClearingVo.setBankCode("");
            }
        }
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
        if (StringUtils.isNotEmpty(openWaitStatus)) {
            issue.in(FundClearing::getStatus, FundClearing.Constants.待审核, FundClearing.Constants.待出款, FundClearing.Constants.已创建);
        } else {
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
        String name = environment.getProperty("spring.profiles.active");

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
            e.setSendAmt(new BigDecimal((e.getSendAmt().multiply(wallet_pay_integral)).stripTrailingZeros().toPlainString()));
            if(name.contains("sm") || name.contains("yk")  || name.contains("tf") ){
                e.setDescription(CommAliasNameSmEnum.getAliasNameReplaceName(e.getDescription()));
            }else if (name.contains("hdf") ){
                e.setDescription(CommAliasNameHdfEnum.getAliasNameReplaceName(e.getDescription()));
            }else{
                e.setDescription(CommAliasNameEnum.getAliasNameByName(e.getCommName()));
            }
            for (FundClearingItem item : e.getItems()) {
                WalletConfig walletConfig = walletMap.get(item.getWalletType());
                item.setWalletName(walletConfig != null ? walletConfig.getName() : "");
                item.setAmt(new BigDecimal((item.getAmt().multiply(wallet_pay_integral)).stripTrailingZeros().toPlainString()));
            }
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public boolean hasCreate(String orderNo, String commName) {
        return getOne(new QueryWrapper<FundClearing>().lambda().eq(FundClearing::getExternalNo, orderNo).eq(FundClearing::getCommName, commName).last(" limit 1")) != null;
    }

    @Override
    public BigDecimal getSendCommAmt(Integer uid, Date start, Date end, String... commName) {
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
    public BigDecimal getUserTotalMonth(Integer uid, String month) {
        return fundClearingDao.getUserTotalMonth(uid, month);
    }

    @Override
    public List<Map<String, Object>> getUserTotalMonthList(Integer uid, String month) {
        return fundClearingDao.getUserTotalMonthList(uid, month);
    }

    @Override
    public BigDecimal getUserTotalContMonth(Integer uid, String month) {
        return fundClearingDao.getUserTotalContMonth(uid, month);
    }

    @Override
    public BigDecimal getUserTotalDay(Integer uid, String day) {
        return fundClearingDao.getUserTotalDay(uid, day);
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

    @Override
    public void addFgComm(String month) {

        String repetition = systemConfigService.getValueByKey("goods_repetition");

        if (repetition.equals("0")) {
            return;
        }
        // 新系统复销奖统计
        //复销奖奖励商品
           String repetitionId =  systemConfigService.getValueByKey("goods_repetition_id");

        //复销奖资格
        String goodsRepetitionIdQua =  systemConfigService.getValueByKey("goods_repetition_id_qua");


        StringBuilder stringBuilder = new StringBuilder();

        String name = environment.getProperty("historyOrder.name");
        if (name.equals("jymall")) {
            stringBuilder = new StringBuilder("\n" +
                    "\t\t\t\t\t\t\t\t\t \n" +
                    "\t\t\t\t\t\t\t\t\t    SELECT  o.userId,g.goodsId ,o.orderSn ,g.goodsSn ,g.price,g.number FROM jymall.ordergoods AS g \n" +
                    "\t\t\t\t\t\t\t\t\t\t\tleft join jymall.orders o on o.id  = g.orderId\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t\n" +
                    "\t\t\t\t\t\t\t\t\t\t\tWHERE 1=1 AND ( g.goodsId IN(190,224,228,276,277,278,279,280,2010,2028,2035,2061,2062,2063,2064,2065,2066,2068,2069,2070,2071,2074,2077,2078,2079,2080,2090,2089,2081,2095,2096,2085,2097,2098,2103,2114,2116,2117,2119,2120,2124,2125) OR g.`refGoodsId` IN (190,224,228,276,277,278,279,280,2010,2028,2035,2061,2062,2063,2064,2065,2066,2068,2069,2070,2071,2074,2077,2078,2079,2080,2090,2089,2081,2095,2096,2085,2097,2098,2103,2114,2116,2117,2119,2120,2124,2125) )\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t       and   DATE_FORMAT( o.`payTime`,'%Y-%m')  = '2024-04'\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t\t\tand\t  o.`payTime` IS NOT NULL\n" +
                    "                    and o.`status` IN ( 201,301,401,402,501 )\n" +
                    "                   AND o.sellerId IS NULL\n" +
                    "                   AND o.platform in('商城', '订货') ");
        } else {
            stringBuilder = new StringBuilder("\n" +
                    "\t\t\t\t\t\t\t\t\t \n" +
                    "\t\t\t\t\t\t\t\t\t    SELECT o.userId, g.goodsId ,o.orderSn ,g.goodsSn ,g.price,g.number FROM wkp42271043176625.ordergoods AS g \n" +
                    "\t\t\t\t\t\t\t\t\t\t\tleft join wkp42271043176625.orders o on o.id  = g.orderId\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t\n" +
                    "\t\t\t\t\t\t\t\t\t\t\tWHERE 1=1 AND ( g.goodsId IN(190,224,228,276,277,278,279,280,316,332,339,360,371,372,373,374,375,376,378,379,380,381,386,395,396,397,398,407,408,399,403,421,422,415,418,429,444,446,447,449,450,454,455) OR g.`refGoodsId` IN (190,224,228,276,277,278,279,280,316,332,339,360,371,372,373,374,375,376,378,379,380,381,386,395,396,397,398,407,408,399,403,421,422,415,418,429,444,446,447,449,450,454,455) )\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t       and   DATE_FORMAT( o.`payTime`,'%Y-%m')  = '2024-04'\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t\t\tand\t  o.`payTime` IS NOT NULL\n" +
                    "                    and o.`status` IN ( 201,301,401,402,501 )\n" +
                    "                   AND o.sellerId IS NULL\n" +
                    "                   AND o.platform in('商城', '订货')");
        }


        List<Map<String, Object>> maps = SqlRunner.db().selectList(stringBuilder.toString());


        //查询新系统复销奖订单

        List<Map<String,Object>>  orderList =   orderService.getFgGoodsOrder(repetitionId,month);
         for(Map<String,Object> order : orderList){
            Map<String,Object> map =new HashMap<>();
             map.put("userId" , order.get("uid"));
             map.put("goodsId" , order.get("product_id"));
             map.put("orderSn" , order.get("plat_order_no"));
             map.put("goodsSn" , order.get("bar_code"));
             map.put("price" , order.get("pay_price"));
             map.put("number" , 1);
             maps.add(map);
         }



        for (Map<String, Object> map : maps) {

            BigDecimal pv = new BigDecimal(0.8);
            //商品pv
            if(StringUtils.equals(map.get("goodsSn").toString(),"TZ2023092813398")){
                pv = new BigDecimal(0.7);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"TZ2023092866543")){
                pv = new BigDecimal(0.7);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"2023121288666774")){
                pv = new BigDecimal(0.4);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"TZ2023122112338999")){
                pv = new BigDecimal(0.79);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"TZ20231221123381156")){
                pv = new BigDecimal(0.79);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"TZ2024013088325639")){
                pv = new BigDecimal(0.5);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"TZ2024013077229933")){
                pv = new BigDecimal(0.5);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"2023122583649936")){
                pv = new BigDecimal(0.4);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"202401048822334")){
                pv = new BigDecimal(0.79);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"2024010488639116")){
                pv = new BigDecimal(0.79);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"TZ2024013055829421")){
                pv = new BigDecimal(0.5);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"TZ2024013062816492")){
                pv = new BigDecimal(0.5);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"TZ2024013161812376")){
                pv = new BigDecimal(0.79);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"TZ2024013161823616")){
                pv = new BigDecimal(0.79);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"TZ2024031486238116")){
                pv = new BigDecimal(0.5);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"TZ2024040266881156")){
                pv = new BigDecimal(0.5);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"TZ2024040372615986")){
                pv = new BigDecimal(1);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"6944247206532")){
                pv = new BigDecimal(1);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"6944247204156")){
                pv = new BigDecimal(1);
            }else if(StringUtils.equals(map.get("goodsSn").toString(),"TZ2024041922336611")){
                pv = new BigDecimal(1);
            }




//            //判断历史复销奖
            User createUser = userService.getById(Integer.valueOf(map.get("userId").toString()));
            if (createUser == null) {
                continue;
            }
            UserInvitation userInvitation = userInvitationService.getByUser(createUser.getId());
            if (userInvitation == null) {
                continue;
            }

            User user = userService.getById(userInvitation.getPId());
            Integer J = 1;
            // 增加复销奖
            do {
                if (user == null) {
                    break;
                }

                Boolean ifAddclearing = false;

                UserCapaXs xsUserCapa = userCapaXsService.getByUser(user.getId());
                if (xsUserCapa != null && xsUserCapa.getCapaId() >= 3) {
                    //大于1星直接增加佣金
                    ifAddclearing = true;
                } else {
                    UserInvitation userInvitation2 = userInvitationService.getByUser(user.getId());

                    if (userInvitation2 == null) {
                        break;
                    }
                    user = userService.getById(userInvitation2.getPId());
                    continue;
                }


                if (name.equals("jymall")) {
                    stringBuilder = new StringBuilder(" SELECT IFNULL(SUM(o.`payPrice`),0) as c FROM " + name + ".orders AS o\n" +
                            "        WHERE  o.`payTime` IS NOT NULL\n" +
                            "        and o.`status` IN ( 201,301,401,402,501 )\n" +
                            "        AND o.platform in('商城', '订货')\n" +
                            "        AND o.id IN (\n" +
                            "                SELECT g.orderId FROM " + name + ".ordergoods AS g WHERE 1=1  AND ( g.goodsId IN(190,207,228,237,276,279,280,2010,2016,2028,2032,2035,2044,2054,2059,2061,2062,2063,2064,2065,2066,2068,2069,2070,2071,2073,2074,2077,2079,2080,2081,2089,2090,2095,2096,2085,2097,2098,2100,2103,2114,2116,2117,2118,2119,2120,2124,2125) OR g.`refGoodsId` IN (190,207,228,237,276,279,280,2010,2016,2028,2032,2035,2044,2054,2059,2061,2062,2063,2064,2065,2066,2068,2069,2070,2071,2073,2074,2077,2079,2080,2081,2089,2090,2095,2096,2085,2097,2098,2100,2103,2114,2116,2117,2118,2119,2120,2124,2125) )\n" +
                            "\t\t)\n" +
                            "        and   DATE_FORMAT( o.`payTime`,'%Y-%m') ='2024-04'\n" +
                            "        and o.userId  = '" + user.getId() + "'");
                } else {
                    stringBuilder = new StringBuilder(" SELECT IFNULL(SUM(o.`payPrice`),0) as c FROM " + name + ".orders AS o\n" +
                            "        WHERE  o.`payTime` IS NOT NULL\n" +
                            "        and o.`status` IN ( 201,301,401,402,501 )\n" +
                            "        AND o.platform in('商城', '订货')\n" +
                            "        AND o.id IN (\n" +
                            "                SELECT g.orderId FROM " + name + ".ordergoods AS g WHERE 1=1  AND ( g.goodsId IN(190,207,228,236,237,276,279,280,316,322,332,336,339,350,365,368,371,372,373,374,375,376,378,379,380,381,382,385,386,394,395,397,398,399,407,408,415,418,403,421,422,424,429,436,444,446,447,449,450,454,455) OR g.`refGoodsId` IN (190,207,228,236,237,276,279,280,316,322,332,336,339,350,365,368,371,372,373,374,375,376,378,379,380,381,382,385,386,394,395,397,398,399,407,408,415,418,403,421,422,424,429,436,444,446,447,449,450,454,455) )\n" +
                            "\t\t)\n" +
                            "        and   DATE_FORMAT( o.`payTime`,'%Y-%m') ='2024-04'\n" +
                            "        and o.userId  = '" + user.getId() + "'"
                    );
                }


                Map<String, Object> map2 = SqlRunner.db().selectOne(stringBuilder.toString());

                BigDecimal salse = orderService.getGoodsPrice(goodsRepetitionIdQua,user.getId(), month+"-01 00:00:00");

                if (ifAddclearing && ((new BigDecimal(map2.get("c").toString())).add(salse)).compareTo(new BigDecimal(199)) == 1) {
                  BigDecimal amt = new BigDecimal( map.get("price").toString()).multiply(new BigDecimal( map.get("number").toString())).multiply(new BigDecimal("0.01")).multiply(pv).setScale(2, BigDecimal.ROUND_DOWN);

                    create(user.getId(), map.get("orderSn").toString(), "重复消费积分", amt,
                            null, month+"重复消费积分", "");

                    J++;
                }

                userInvitation = userInvitationService.getByUser(user.getId());
                if (userInvitation == null) {
                    break;
                }

                user = userService.getById(userInvitation.getPId());

            } while (J <= 18);
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
                if (StringUtils.isNotEmpty(openWaitStatus)) {
                    lqw.in(FundClearing::getStatus, FundClearing.Constants.已创建, FundClearing.Constants.待审核, FundClearing.Constants.待出款, FundClearing.Constants.已出款);
                } else {
                    lqw.in(FundClearing::getStatus, FundClearing.Constants.待审核, FundClearing.Constants.待出款, FundClearing.Constants.已出款);
                }
                break;
            case 1:
                if (StringUtils.isNotEmpty(openWaitStatus)) {
                    lqw.in(FundClearing::getStatus, FundClearing.Constants.已创建, FundClearing.Constants.待审核, FundClearing.Constants.待出款);
                } else {
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





    /**
     *  共享仓极差伯乐奖
     * @param order
     */
    @Override
    public void createTankOrder(TankOrders order) {

        // 查询所有上级
        List<UserUpperDto> allUpper = userInvitationService.getNoMountAllUpper(order.getUserId().intValue());
        if (CollectionUtils.isEmpty(allUpper)) {
            return;
        }
        Map<Integer, UserCapaXs> uidCapaXsMap = userCapaXsService.getUidMap(allUpper.stream().filter(u -> u.getPId() != null).map(UserUpperDto::getPId).collect(Collectors.toList()));
        // 最大星级
        CapaXs maxCapa = capaXsService.getMaxCapa();
        // 更高头衔的用户【拿钱用户】
        LinkedList<UserCapaXs> userList = Lists.newLinkedList();
        Long capaId = 0L;
        for (UserUpperDto upperDto : allUpper) {
            if (upperDto.getPId() != null) {
                UserCapaXs userCapaXs = uidCapaXsMap.get(upperDto.getPId());
                if(userCapaXs != null){
                    if (NumberUtils.compare(userCapaXs.getCapaId(), capaId) > 0) {
                        userList.add(userCapaXs);
                        capaId = userCapaXs.getCapaId();
                    }
                    if (NumberUtils.compare(maxCapa.getId(), capaId) == 0) {
                        break;
                    }
                }
            }
        }
        // 没人退出
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }

        // 分钱用户产品
        LinkedHashMap<Integer, List<FundClearingProduct>> productMap = Maps.newLinkedHashMap();
        // 分钱用户金额
        LinkedHashMap<Integer, Double> userAmtMap = Maps.newLinkedHashMap();


        // 真实业绩
        BigDecimal totalPv = order.getPayPrice();
        // 佣金规则
        List<CapaXsDifferentialCommHandler.Rule> rules =new ArrayList<>();

        CapaXsDifferentialCommHandler.Rule rule2= new  CapaXsDifferentialCommHandler.Rule();
        rule2.setCapaXsId(3L);
        rule2.setRatio(new BigDecimal(0.03));
        rules.add(rule2);
        CapaXsDifferentialCommHandler.Rule rule3= new  CapaXsDifferentialCommHandler.Rule();
        rule3.setCapaXsId(4L);
        rule3.setRatio(new BigDecimal(0.05));
        rules.add(rule3);
        CapaXsDifferentialCommHandler.Rule rule4= new  CapaXsDifferentialCommHandler.Rule();
        rule4.setCapaXsId(5L);
        rule4.setRatio(new BigDecimal(0.07));
        rules.add(rule4);
        CapaXsDifferentialCommHandler.Rule rule5= new  CapaXsDifferentialCommHandler.Rule();
        rule5.setCapaXsId(6L);
        rule5.setRatio(new BigDecimal(0.09));
        rules.add(rule5);
        CapaXsDifferentialCommHandler.Rule rule6= new  CapaXsDifferentialCommHandler.Rule();
        rule6.setCapaXsId(7L);
        rule6.setRatio(new BigDecimal(0.11));
        rules.add(rule6);
        CapaXsDifferentialCommHandler.Rule rule7= new  CapaXsDifferentialCommHandler.Rule();
        rule7.setCapaXsId(8L);
        rule7.setRatio(new BigDecimal(0.13));
        rules.add(rule7);
        CapaXsDifferentialCommHandler.Rule rule8= new  CapaXsDifferentialCommHandler.Rule();
        rule8.setCapaXsId(9L);
        rule8.setRatio(new BigDecimal(0.15));
        rules.add(rule8);


        Map<Long, CapaXsDifferentialCommHandler.Rule> ruleMap = FunctionUtil.keyValueMap(rules, CapaXsDifferentialCommHandler.Rule::getCapaXsId);
        // 已发比例
        BigDecimal usedRatio = BigDecimal.ZERO;
        // 每个人拿
        for (UserCapaXs userCapa : userList) {
            CapaXsDifferentialCommHandler.Rule rule = ruleMap.get(userCapa.getCapaId());
            BigDecimal ratio = BigDecimal.ZERO;
            if (rule != null) {
                ratio = rule.getRatio();
            }
            // 佣金
            if (ArithmeticUtils.gt(ratio, usedRatio)) {
                BigDecimal usableRatio = ratio.subtract(usedRatio);
                double amt = totalPv.multiply(usableRatio).setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
                usedRatio = ratio;

                userAmtMap.put(userCapa.getUid(), MapUtils.getDoubleValue(userAmtMap, userCapa.getUid(), 0d) + amt);
                FundClearingProduct clearingProduct = new FundClearingProduct(null, "共享仓充值", totalPv,
                        1, ratio, BigDecimal.valueOf(amt));

                List<FundClearingProduct> productList = productMap.get(userCapa.getUid());
                if (CollectionUtils.isEmpty(productList)) {
                    productList = Lists.newArrayList();
                }
                productList.add(clearingProduct);
                productMap.put(userCapa.getUid(), productList);
            }
        }

        if (userAmtMap.isEmpty()) {
            return;
        }
        // 分钱
        User orderUser = userService.getById(order.getUserId());
        LinkedList<CommCalculateResult> resultList =new  LinkedList<CommCalculateResult>();
        userAmtMap.forEach((uid, amt) -> {
            BigDecimal clearingFee = BigDecimal.valueOf(amt).setScale(2, BigDecimal.ROUND_DOWN);
            if (ArithmeticUtils.gt(clearingFee, BigDecimal.ZERO)) {
                List<FundClearingProduct> fundClearingProducts = productMap.get(uid);
                create(uid, order.getOrderSn(), ProductCommEnum.星级级差佣金.getName(), clearingFee,
                        fundClearingProducts, orderUser.getAccount() + "下单获得" + ProductCommEnum.星级级差佣金.getName(), "");

                int sort = resultList.size() + 1;
                CommCalculateResult calculateResult = new CommCalculateResult(uid, 13, ProductCommEnum.星级级差佣金.getName(),
                        null, null, null,
                        1, null, BigDecimal.ONE, null, clearingFee, sort);
                resultList.add(calculateResult);
            }
        });



        // 星级级差佣金
        List<CommCalculateResult> collisionFeeList = resultList.stream().filter(r -> r.getType().equals(ProductCommEnum.星级级差佣金.getType()))
                .sorted(Comparator.comparing(CommCalculateResult::getSort)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collisionFeeList)) {
            return;
        }
        // 下单用户的所有上级
        if (CollectionUtils.isEmpty(allUpper)) {
            return;
        }
        ProductCommConfig config = productCommConfigService.getByType(14);
        CapaXsBoleCommHandler.Rule rules2 = JSONObject.parseObject(config.getRatioJson(), CapaXsBoleCommHandler.Rule.class);
        CapaXsBoleCommHandler.Rule rule = rules2;
        List<CapaXsBoleCommHandler.LevelRatio> levelRatios = rule.getLevelRatios();
        Map<Integer, CapaXsBoleCommHandler.LevelRatio> ratioMap = FunctionUtil.keyValueMap(levelRatios, CapaXsBoleCommHandler.LevelRatio::getLevel);
        int maxLevel = levelRatios.size();
        Map<Integer, UserCapaXs> uidCapaXsMap2 = userCapaXsService.getUidMap(allUpper.stream().filter(u -> u.getPId() != null).map(UserUpperDto::getPId).collect(Collectors.toList()));
        // 往上找18个人获得佣金
        for (CommCalculateResult calculateResult : collisionFeeList) {
            int i = 1;
            // 得奖人
            Integer uid = calculateResult.getUid();
            User user = userService.getById(calculateResult.getUid());
            Boolean start = false;
            for (UserUpperDto upperDto : allUpper) {
                // 没有上级就空了
                if (upperDto.getPId() == null) {
                    break;
                }
                if (upperDto.getPId().intValue() == uid.intValue()) {
                    start = true;  // 找到自己的位置
                    continue;
                }
                // 自己后面的
                if (start && i <= maxLevel) {
                    UserCapaXs pCapaXs = uidCapaXsMap2.get(upperDto.getPId());
                    if (pCapaXs != null && pCapaXs.getCapaId().intValue() >= rule.getCapaXsId().intValue()) {
                        // 算钱
                        BigDecimal ratio = ratioMap.get(i).getRatio();
                        BigDecimal amt = ratio.multiply(calculateResult.getAmt()).multiply(rule.getScale()).setScale(2, BigDecimal.ROUND_DOWN);
                        if (ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {
                            create(upperDto.getPId(), order.getOrderSn(), ProductCommEnum.级差伯乐佣金.getName(), amt,
                                    null, user.getAccount() + "获得星级级差佣金奖励上级" + ProductCommEnum.级差伯乐佣金.getName(), "");
                        }
                        i++;
                    }
                }
            }


        }

    }

    /**
     * Excel导入佣金出款
     * @param list
     * @return
     */
    @Override
    public Boolean importFundClearing(List<FundClearingImportRequest> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new CrmebException("导入数据不能为空！");
        }
        //校验
        int i = 1;
        Map<String, User> userMap = Maps.newConcurrentMap();
        for (FundClearingImportRequest importFound : list) {
            String account = importFound.getAccount();
            account = StringUtils.trim(account);
            if (StringUtils.isEmpty(account)) {
                throw new CrmebException("账号不能为空！");
            }
            importFound.setAccount(account);
            User user = userService.getByAccount(account);
            if (user == null) {
                throw new CrmebException(importFound.getAccount() + ":账号不存在");
            }
            userMap.put(account, user);
            if (StringUtils.isEmpty(importFound.getExternalNo())) {
                throw new CrmebException("外部订单号不能为空！" + importFound.getAccount());
            }
            if (StringUtils.isEmpty(importFound.getCommName())) {
                throw new CrmebException("佣金名称不能为空！" + importFound.getAccount());
            }
            //获取佣金名称
            Set<String> set = Sets.newHashSet();
            List<ProductCommConfig> list1 = productCommConfigService.getOpenList();
            for (ProductCommConfig value : list1) {
                set.add(value.getName());
            }
            set.add("销售佣金");
            set.add("培育佣金");
            set.add("其他佣金");

            String commNameStr = environment.getProperty("fundClearing.name");
            if (StringUtils.isNotEmpty(commNameStr)) {
                String[] split = commNameStr.split(",");
                for (String s : split) {
                    set.add(s);
                }
            }
            //判断提供的佣金名称是否包含
            if (!set.contains(importFound.getCommName())) {
                throw new CrmebException("佣金名称不存在或状态未开启！" + importFound.getAccount());
            }

            if (ObjectUtil.isEmpty(importFound.getCommAmt()) && ArithmeticUtils.gt(importFound.getCommAmt(), BigDecimal.ZERO)) {
                throw new CrmebException("佣金金额不能为空！" + importFound.getAccount());
            }
            if (StringUtils.isEmpty(importFound.getDescription())) {
                throw new CrmebException("佣金描述不能为空！" + importFound.getAccount());
            }
            if (StringUtils.isEmpty(importFound.getRemark())) {
                throw new CrmebException("佣金备注不能为空！" + importFound.getAccount());
            }
            logger.info("正在检查导入数据基础信息:" + i + "###总条数:" + list.size());
            i++;
        }
        //保存数据
        i = 1;
        for (FundClearingImportRequest importFound : list) {
            User user = userMap.get(importFound.getAccount());
            create(user.getId(), importFound.getExternalNo(), importFound.getCommName(), importFound.getCommAmt(), null, importFound.getDescription(), importFound.getRemark());
            logger.info("正在保存佣金出款信息:" + i + "###总条数:" + list.size());
            i++;
        }
        return true;
    }

}
