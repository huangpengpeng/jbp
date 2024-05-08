package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.order.Order;
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
import com.jbp.service.product.comm.CommAliasNameSmEnum;
import com.jbp.service.product.comm.ProductCommEnum;
import com.jbp.service.service.OrderService;
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
import java.util.*;

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
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private OrderService orderService;


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
            if (fundClearing.getIfRefund() != null && fundClearing.getIfRefund()) {
                throw new RuntimeException(fundClearing.getUniqueNo() + "已经退回请勿重复操作");
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
    public List<FundClearingVo> exportFundClearing(String uniqueNo, String externalNo, Date startClearingTime, Date endClearingTime, Date starteCreateTime, Date endCreateTime, String status, Integer uid, String teamName, String description, String commName, Boolean ifRefund , List<String> orderList) {
        String channelName = systemConfigService.getValueByKey("pay_channel_name");
        List<FundClearingVo> fundClearingVos = fundClearingDao.exportFundClearing(uniqueNo, externalNo, startClearingTime, endClearingTime, starteCreateTime, endCreateTime, status, uid, teamName, description, 0L, channelName, commName, ifRefund, orderList);
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
            e.setSendAmt(e.getSendAmt().multiply(wallet_pay_integral));
            if(name.equals("sm") || name.equals("yk")  || name.equals("tf") ){
                e.setDescription(CommAliasNameSmEnum.getAliasNameReplaceName(e.getDescription()));
            }else{
                e.setDescription(CommAliasNameEnum.getAliasNameByName(e.getCommName()));
            }
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
}
