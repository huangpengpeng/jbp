package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.excel.FundClearingExcel;
import com.jbp.common.excel.ScoreDownLoadExcel;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ScoreDownloadRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.vo.FileResultVo;
import com.jbp.service.dao.agent.InvitationScoreDao;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class InvitationScoreServiceImpl extends ServiceImpl<InvitationScoreDao, InvitationScore> implements InvitationScoreService {

    @Resource
    private OssService ossService;
    @Resource
    private UploadService uploadService;
    @Resource
    private TeamUserService teamUserService;
    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private OrderService orderService;
    @Resource
    private UserService userService;
    @Resource
    private InvitationScoreFlowService invitationScoreFlowService;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private SelfScoreService selfScoreService;
    @Resource
    private InvitationScoreDao dao;


    @Override
    public void init() {
        InvitationScoreFlow one = invitationScoreFlowService.getOne(new QueryWrapper<InvitationScoreFlow>().lambda().
                like(InvitationScoreFlow::getOperate, "初始化").last("limit 1"));
        if (one != null) {
            return;
        }
        List<SelfScore> list = selfScoreService.list();
        for (SelfScore selfScore : list) {
            List<UserUpperDto> allUpper = userInvitationService.getAllUpper(selfScore.getUid());
            if (CollectionUtils.isEmpty(allUpper)) {
                continue;
            }
            for (UserUpperDto upperDto : allUpper) {
                if (upperDto.getPId() != null) {
                    InvitationScore invitationScore = getByUser(upperDto.getPId());
                    if (invitationScore == null) {
                        invitationScore = add(upperDto.getPId());
                    }
                    invitationScore.setScore(invitationScore.getScore().add(selfScore.getScore()));
                    updateById(invitationScore);
                    invitationScoreFlowService.add(upperDto.getPId(), selfScore.getUid(), selfScore.getScore(), "增加",
                            "初始化", CrmebUtil.getOrderNo("INIT_"), DateTimeUtils.getNow(), null, "初始化");
                }
            }
        }
    }


    @Override
    public PageInfo<InvitationScore> pageList(Integer uid, String nickname,PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<InvitationScore> lqw = new LambdaQueryWrapper<InvitationScore>()
                .eq(!ObjectUtil.isNull(uid), InvitationScore::getUid, uid)
                .orderByDesc(InvitationScore::getId);
        if (StrUtil.isNotBlank(nickname)){
            lqw.apply("1=1 and uid in (select id from eb_user where nickname like '%" + nickname + "%')");
        }
        Page<InvitationScore> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<InvitationScore> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(InvitationScore::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        List<SelfScore> selfScoreList = selfScoreService.list(new QueryWrapper<SelfScore>().lambda().in(SelfScore::getUid, uIdList));
        Map<Integer, SelfScore> selfScoreMap = FunctionUtil.keyValueMap(selfScoreList, SelfScore::getUid);
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
            e.setNickname(user != null ? user.getNickname() : "");
            SelfScore selfScore = selfScoreMap.get(e.getUid());
            e.setSelfScore(selfScore != null ? selfScore.getScore() : BigDecimal.ZERO);
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public InvitationScore add(Integer uid) {
        InvitationScore invitationScore = new InvitationScore(uid);
        save(invitationScore);
        return invitationScore;
    }

    @Override
    public InvitationScore getByUser(Integer uid) {
        return getOne(new QueryWrapper<InvitationScore>().lambda().eq(InvitationScore::getUid, uid));
    }

    @Override
    public BigDecimal getInvitationScore(Integer uid, Boolean containsSelf) {
        InvitationScore nextInvitationScore = getByUser(uid);
        BigDecimal score = nextInvitationScore == null ? BigDecimal.ZERO : nextInvitationScore.getScore();
        if (BooleanUtils.isNotTrue(containsSelf)) {
            return score;
        }
        // 自己业绩
        SelfScore self = selfScoreService.getByUser(uid);
        BigDecimal selfScore = self == null ? BigDecimal.ZERO : self.getScore();
        score.add(selfScore);
        return score;
    }

    @Override
    public void orderSuccess(Integer uid, BigDecimal score, String ordersSn, Date payTime, List<ProductInfoDto> productInfo) {
        InvitationScoreFlow one = invitationScoreFlowService.getOne(new QueryWrapper<InvitationScoreFlow>().lambda()
                .eq(InvitationScoreFlow::getOrdersSn, ordersSn).last(" limit 1"));
        if (one != null) {
            return;
        }
        List<UserUpperDto> allUpper = userInvitationService.getAllUpper(uid);
        if (CollectionUtils.isEmpty(allUpper)) {
            return;
        }
        List<Integer> pIdList = allUpper.stream().filter(u -> u.getPId() != null).map(UserUpperDto::getPId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(pIdList)) {
            return;
        }
        List<InvitationScore> invitationScores = list(new QueryWrapper<InvitationScore>().lambda().in(InvitationScore::getUid, pIdList));
        Map<Integer, InvitationScore> invitationScoreMap = FunctionUtil.keyValueMap(invitationScores, InvitationScore::getUid);
        LinkedList<InvitationScoreFlow> list = Lists.newLinkedList();
        for (UserUpperDto upperDto : allUpper) {
            if (upperDto.getPId() != null) {
                InvitationScore invitationScore = invitationScoreMap.get(upperDto.getPId());
                if (invitationScore == null) {
                    invitationScore = add(upperDto.getPId());
                }
                // 汇总
                invitationScore.setScore(invitationScore.getScore().add(score));
                updateById(invitationScore);
                // 明细
                InvitationScoreFlow flow = new InvitationScoreFlow(upperDto.getPId(), uid, score, "增加", "下单", ordersSn, payTime, productInfo, "");
                list.add(flow);
            }
        }
        // 增加明细
        if (CollectionUtils.isNotEmpty(list)) {
            invitationScoreFlowService.saveBatch(list);
        }
    }

    @Override
    public void orderRefund(String orderSn) {
        List<InvitationScoreFlow> list = invitationScoreFlowService.list(new QueryWrapper<InvitationScoreFlow>().lambda().eq(InvitationScoreFlow::getOrdersSn, orderSn));
        invitationScoreFlowService.remove(new QueryWrapper<InvitationScoreFlow>().lambda().eq(InvitationScoreFlow::getOrdersSn, orderSn));
        if (!list.isEmpty()) {
            List<InvitationScore> updateList = Lists.newArrayList();
            for (InvitationScoreFlow invitationScoreFlow : list) {
                InvitationScore invitationScore = getByUser(invitationScoreFlow.getUid());
                if (invitationScore != null && ArithmeticUtils.gte(invitationScore.getScore(), invitationScoreFlow.getScore())) {
                    BigDecimal score = invitationScore.getScore().subtract(invitationScoreFlow.getScore());
                    invitationScore.setScore(score);
                    updateList.add(invitationScore);
                }
            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                dao.updateBatch(updateList);
            }
        }
    }

    @Override
    public String download(ScoreDownloadRequest request) {
        if (CollectionUtils.isEmpty(request.getAccountList())) {
            if (request.getStartTime() == null || request.getEndTime() == null) {
                throw new RuntimeException("未指定固定账户,业绩开始时间-结束时间不能为空");
            }
            if (DateTimeUtils.addMonths(request.getStartTime(), 3).before(request.getEndTime())) {
                throw new RuntimeException("未指定固定账户,时间跨度不能超过3个月");
            }
        }
        // 查询订单指定时间成功的订单
        List<Order> successList = orderService.getSuccessList(request.getStartTime(), request.getEndTime());
        if (CollectionUtils.isEmpty(successList)) {
            throw new RuntimeException("没查到支付成功的订单");
        }
        // 查询账户
        List<Integer> uidList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(request.getAccountList())) {
            List<User> userList = userService.list(new LambdaQueryWrapper<User>().in(User::getAccount, request.getAccountList()));
            if (CollectionUtils.isNotEmpty(userList)) {
                uidList = userList.stream().map(User::getId).collect(Collectors.toList());
            }
        }

        // 查询所有的用户上级
        Map<Integer, LinkedList<Integer>> pidListMap = Maps.newConcurrentMap();
        Map<Integer, UserCapa> capaMap = Maps.newConcurrentMap();
        Map<Integer, UserCapaXs> capaXsMap = Maps.newConcurrentMap();

       int i = 1;
        for (Order order : successList) {
            log.info("总数:{},当前条数:{}", successList.size(), i++);
            LinkedList<Integer> pidList = pidListMap.get(order.getUid());
            if (CollectionUtils.isNotEmpty(pidList)) {
                continue;
            }
            List<UserUpperDto> allUpper = userInvitationService.getAllUpper(order.getUid());
            if (CollectionUtils.isEmpty(allUpper) || allUpper.get(0).getPId() == null) {
                continue;
            }
            pidList = new LinkedList<>();
            for (UserUpperDto upperDto : allUpper) {
                if(upperDto.getPId() == null){
                    continue;
                }
                if (CollectionUtils.isNotEmpty(uidList)) {
                    if (!uidList.contains(upperDto.getPId())) {
                        continue;
                    }
                }
                if (CollectionUtils.isNotEmpty(request.getCapaIdList())) {
                    UserCapa userCapa = capaMap.get(upperDto.getPId());
                    if (userCapa == null) {
                        userCapa = userCapaService.getByUser(upperDto.getPId());
                        if (userCapa != null) {
                            capaMap.put(upperDto.getPId(), userCapa);
                        }
                    }

                    if (userCapa == null || !request.getCapaIdList().contains(userCapa.getCapaId())) {
                        continue;
                    }
                }
                if (CollectionUtils.isNotEmpty(request.getCapaIdXsList())) {
                    UserCapaXs userCapaXs = capaXsMap.get(upperDto.getPId());
                    if (userCapaXs == null) {
                        userCapaXs = userCapaXsService.getByUser(upperDto.getPId());
                        if(userCapaXs != null){
                            capaXsMap.put(upperDto.getPId(), userCapaXs);
                        }
                    }
                    if (userCapaXs == null || !request.getCapaIdXsList().contains(userCapaXs.getCapaId())) {
                        continue;
                    }
                }
                pidList.add(upperDto.getPId());
            }
            pidListMap.put(order.getUid(), pidList);
        }

        List<String> orderNoList = successList.stream().map(Order::getOrderNo).collect(Collectors.toList());
        Map<String, List<OrderDetail>> orderDetailMap = orderDetailService.getMapByOrderNoList(orderNoList);
        List<ScoreDownLoadExcel> list = Lists.newArrayList();

        Map<Integer, User> userMap = Maps.newConcurrentMap();
        Map<Integer, TeamUser> userTeamMap = Maps.newConcurrentMap();
        i = 1;
        for (Order order : successList) {
            log.info("组装数据，总数:{},当前条数:{}", successList.size(), i++);
            LinkedList<Integer> pidList = pidListMap.get(order.getUid());
            if (CollectionUtils.isEmpty(pidList)) {
                continue;
            }
            List<OrderDetail> orderDetailList = orderDetailMap.get(order.getOrderNo());
            for (OrderDetail orderDetail : orderDetailList) {
                BigDecimal score = orderDetailService.getRealScore(orderDetail);
                BigDecimal level = BigDecimal.ONE;
                for (Integer pid : pidList) {
                    ScoreDownLoadExcel excel = new ScoreDownLoadExcel();
                    User puser = userMap.get(pid);
                    if (puser == null) {
                        puser = userService.getById(pid);
                        if(puser != null){
                            userMap.put(pid, puser);
                        }
                    }
                    excel.setUid(pid);
                    excel.setAccount(puser.getAccount());
                    excel.setNickName(puser.getNickname());
                    TeamUser pUserTeam = userTeamMap.get(pid);
                    if (pUserTeam == null) {
                        pUserTeam = teamUserService.getByUser(pid);
                        if(pUserTeam != null){
                            userTeamMap.put(pid, pUserTeam);
                        }
                    }
                    if (pUserTeam != null) {
                        excel.setTeamName(pUserTeam.getName());
                    }
                    UserCapa pUserCapa = capaMap.get(pid);
                    if (pUserCapa == null) {
                        pUserCapa = userCapaService.getByUser(pid);
                        if(pUserCapa != null){
                            capaMap.put(pid, pUserCapa);
                        }
                    }
                    if (pUserCapa != null) {
                        excel.setCapaName(pUserCapa.getCapaName());
                    }
                    UserCapaXs pUserCapaXs = capaXsMap.get(pid);
                    if (pUserCapaXs == null) {
                        pUserCapaXs = userCapaXsService.getByUser(pid);
                        if(pUserCapaXs != null){
                            capaXsMap.put(pid, pUserCapaXs);
                        }
                    }
                    if (pUserCapaXs != null) {
                        excel.setCapaXsName(pUserCapaXs.getCapaName());
                    }
                    excel.setScore(score);
                    excel.setScore2(score.divide(level, 2, BigDecimal.ROUND_DOWN));
                    excel.setOrderSn(order.getOrderNo());
                    excel.setProductName(orderDetail.getProductName());
                    excel.setBarCode(orderDetail.getBarCode());
                    excel.setPayNum(orderDetail.getPayNum());
                    excel.setPayPrice(orderDetail.getPayPrice().subtract(orderDetail.getFreightFee()));
                    excel.setOrderUid(order.getUid());

                    User user = userMap.get(order.getUid());
                    if (user == null) {
                        user = userService.getById(order.getUid());
                        userMap.put(order.getUid(), user);
                    }
                    excel.setOrderAccount(user.getAccount());
                    excel.setPayTime(order.getPayTime());
                    excel.setStartTime(request.getStartTime());
                    excel.setEndTime(request.getEndTime());
                    list.add(excel);
                    level  = level.add(BigDecimal.ONE);
                }
            }
        }
        FileResultVo fileResultVo = uploadService.excelLocalUpload(list, ScoreDownLoadExcel.class);
        return fileResultVo.getUrl();
    }


}
