package com.jbp.service.service.agent.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.excel.OrderShipmentExcel;
import com.jbp.common.excel.WalletExcel;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Team;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.model.product.ProductDeduction;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.WalletRequest;
import com.jbp.common.response.WalletExtResponse;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.common.vo.FileResultVo;
import com.jbp.service.dao.agent.WalletDao;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.PlatformWalletService;
import com.jbp.service.service.agent.WalletFlowService;
import com.jbp.service.service.agent.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class WalletServiceImpl extends ServiceImpl<WalletDao, Wallet> implements WalletService {
    @Resource
    private WalletFlowService walletFlowService;
    @Resource
    private PlatformWalletService platformWalletService;
    @Resource
    private WalletConfigService walletConfigService;
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;
    @Resource
    private WalletDao walletDao;
    @Resource
    private UploadService uploadService;


    @Override
    public Wallet add(Integer uId, Integer type) {
        Wallet wallet = new Wallet(uId, type);
        save(wallet);
        return wallet;
    }

    @Override
    public Wallet getByUser(Integer uid, Integer type) {
        LambdaQueryWrapper<Wallet> wrapper = new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUId, uid)
                .eq(Wallet::getType, type);
        return getOne(wrapper);
    }


    @Override
    public Boolean increase(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript) {
        if (amt == null || ArithmeticUtils.lessEquals(amt, BigDecimal.ZERO)) {
            throw new CrmebException(type + "增加用户积分金额不能小于0:" + amt);
        }
        Wallet wallet = getByUser(uid, type);
        if (wallet == null) {
            wallet = add(uid, type);
        }
        BigDecimal orgBalance = wallet.getBalance();
        wallet.setBalance(wallet.getBalance().add(amt));
        boolean ifSuccess = updateById(wallet);
        if (BooleanUtils.isNotTrue(ifSuccess)) {
            throw new CrmebException("当前操作人数过多");
        }
        walletFlowService.add(uid, type, amt, operate, WalletFlow.ActionEnum.收入.name(), externalNo, orgBalance, wallet.getBalance(), postscript);
        return ifSuccess;
    }

    @Override
    public Boolean reduce(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript) {
        if (amt == null || ArithmeticUtils.lessEquals(amt, BigDecimal.ZERO)) {
            throw new CrmebException(type + "减少用户积分金额不能小于0:" + amt);
        }
        Wallet wallet = getByUser(uid, type);
        if (wallet == null || ArithmeticUtils.less(wallet.getBalance(), amt)) {
            User user = userService.getById(uid);
            BigDecimal balance = wallet == null ? BigDecimal.ZERO : wallet.getBalance();
            throw new CrmebException(user.getAccount() + "用户余额不足" + "应减少:" + amt + "可用:" + balance);
        }
        BigDecimal orgBalance = wallet.getBalance();
        wallet.setBalance(wallet.getBalance().subtract(amt));
        boolean ifSuccess = updateById(wallet);
        if (BooleanUtils.isNotTrue(ifSuccess)) {
            throw new CrmebException("当前操作人数过多");
        }
        walletFlowService.add(uid, type, amt, operate, WalletFlow.ActionEnum.支出.name(), externalNo, orgBalance, wallet.getBalance(), postscript);
        return ifSuccess;
    }

    @Override
    public Boolean transferToPlatform(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript) {
        reduce(uid, type, amt, operate, externalNo, postscript);
        platformWalletService.increase(type, amt, operate, externalNo, postscript);
        return true;
    }

    @Override
    public void deduction(Integer uid, List<ProductDeduction> deductionList, String externalNo, String postscript) {
        if (CollUtil.isEmpty(deductionList)) {
            return;
        }
        List<WalletFlow> walletFlows = walletFlowService.getByUser(uid, externalNo, WalletFlow.OperateEnum.抵扣.toString(), WalletFlow.ActionEnum.支出.name());
        if (CollUtil.isNotEmpty(walletFlows)) {
            return;
        }
        for (ProductDeduction deduction : deductionList) {
            if (deduction.getDeductionFee() != null && ArithmeticUtils.gt(deduction.getDeductionFee(), BigDecimal.ZERO)) {
                transferToPlatform(uid, deduction.getWalletType(), deduction.getDeductionFee(),
                        WalletFlow.OperateEnum.抵扣.toString(), externalNo, postscript);
            }
        }
    }

    @Override
    public void refundDeduction(Integer uid, String externalNo, String postscript) {
        List<WalletFlow> walletFlows = walletFlowService.getByUser(uid, externalNo, WalletFlow.OperateEnum.抵扣.toString(),
                WalletFlow.ActionEnum.收入.name());
        if (CollectionUtils.isNotEmpty(walletFlows)) {
            return;
        }
        walletFlows = walletFlowService.getByUser(uid, externalNo, WalletFlow.OperateEnum.抵扣.toString(), WalletFlow.ActionEnum.支出.name());
        if (CollUtil.isEmpty(walletFlows)) {
            return;
        }
        for (WalletFlow flow : walletFlows) {
            platformWalletService.transferToUser(flow.getUid(), flow.getWalletType(), flow.getAmt(),
                    WalletFlow.OperateEnum.抵扣.toString(), externalNo, postscript);
        }
    }

    @Override
    public Boolean transfer(Integer uid, Integer receiveUserId, BigDecimal amt, Integer type, String postscript) {
        String externalNo = StringUtils.N_TO_10("ZZ_");
        User receiveUser = userService.getById(receiveUserId);
        User user = userService.getById(uid);
        reduce(uid, type, amt, WalletFlow.OperateEnum.转账.name(), externalNo, postscript + "【对手账户:" + receiveUser.getAccount() + " | 昵称:" + receiveUser.getNickname() + "】");
        increase(receiveUserId, type, amt, WalletFlow.OperateEnum.转账.name(), externalNo, postscript + "【对手账户:" + user.getAccount() + " | 昵称:" + user.getNickname() + "】");
        return true;
    }

    @Override
    public Boolean change(Integer uid, BigDecimal amt, Integer type, Integer changeType, String postscript) {
        //扣除用户
        String externalNo = StringUtils.N_TO_10("DH_");
        reduce(uid, type, amt, WalletFlow.OperateEnum.兑换.name(), externalNo, postscript);
        //类型兑换比例积分
        WalletConfig walletConfig = walletConfigService.getByType(type);
        BigDecimal amtIntegral = amt.multiply(walletConfig.getChangeScale());
        increase(uid, changeType, amtIntegral, WalletFlow.OperateEnum.兑换.name(), externalNo, postscript);
        return true;
    }


    @Override
    public PageInfo<WalletExtResponse> pageList(Integer uid, Integer type, String teamId, String nickname, PageParamRequest pageParamRequest) {

        Page<Wallet> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<WalletExtResponse> list =   walletDao.getList(uid,type,teamId,nickname);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(WalletExtResponse::getUId).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            WalletConfig walletConfig = walletConfigService.getByType(e.getType());
            e.setTypeName(walletConfig != null ? walletConfig.getName() : "");
            User user = uidMapList.get(e.getUId());
            e.setAccount(user != null ? user.getAccount() : "");
            e.setNickname(user != null ? user.getNickname() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Wallet getCanPayByUser(Integer uid) {
        WalletConfig walletConfig = walletConfigService.getCanPay();
        if (walletConfig == null) {
            throw new CrmebException("平台未配置可付款积分类型");
        }
        return getByUser(uid, walletConfig.getType());
    }

    @Override
    public void init() {

        List<Map<String, Object>> maps = SqlRunner.db().selectList("select * from tmp_score where action='增加' and ifSuccess is false ");

       int i = 0;
        String externalNo = "CS_202405052001";
        for (Map<String, Object> map : maps) {
            Integer id = MapUtils.getInteger(map, "id");
            Integer uid = MapUtils.getInteger(map, "uid");
            String scoreType = MapUtils.getString(map, "scoreType");
            BigDecimal score = BigDecimal.valueOf(MapUtils.getDouble(map, "score"));
            Integer walletType = null;
            if ("购物积分".equals(scoreType)) {
                walletType = 1;
            }
            if ("奖励积分".equals(scoreType)) {
                walletType = 2;
            }
            if ("换购积分".equals(scoreType)) {
                walletType = 3;
            }
            if ("福券积分".equals(scoreType)) {
                walletType = 4;
            }
            if (walletType == null) {
                continue;
            }
            platformWalletService.transferToUser(uid, walletType, score, WalletFlow.OperateEnum.调账.name(), externalNo, "CS");
            SqlRunner.db().update("update tmp_score set ifSuccess = {0} where id= {1}", true, id);
            i++;
            log.info("增在执行增加积分操作，总数:{}, 当前条数:{}", maps.size(), i);
        }


    }

    @Override
    public void init2() {
        List<Map<String, Object>> maps = SqlRunner.db().selectList("select * from tmp_score where action='减少' and ifSuccess is false ");
        int i = 0;
        String externalNo = "CS_202405052001";
        for (Map<String, Object> map : maps) {
            Integer id = MapUtils.getInteger(map, "id");
            Integer uid = MapUtils.getInteger(map, "uid");
            String scoreType = MapUtils.getString(map, "scoreType");
            BigDecimal score = BigDecimal.valueOf(MapUtils.getDouble(map, "score"));
            Integer walletType = null;
            if ("购物积分".equals(scoreType)) {
                walletType = 1;
            }
            if ("奖励积分".equals(scoreType)) {
                walletType = 2;
            }
            if ("换购积分".equals(scoreType)) {
                walletType = 3;
            }
            if ("福券积分".equals(scoreType)) {
                walletType = 4;
            }
            i++;
            if (walletType != null) {
                Wallet wallet = getByUser(uid, walletType);
                if(wallet != null && ArithmeticUtils.gte(wallet.getBalance(), score)){
                    transferToPlatform(uid, walletType, score, WalletFlow.OperateEnum.调账.name(),  externalNo, "CS");
                    SqlRunner.db().update("update tmp_score set ifSuccess = {0} where id= {1}", true, id);
                }
            }
            log.info("增在执行减少积分操作，总数:{}, 当前条数:{}", maps.size(), i);
        }
    }

    @Override
    public void init3() {
        List<Map<String, Object>> maps = SqlRunner.db().selectList("select * from tmp_score where 1=1 ");
        int i = 0;
        String externalNo = "CS_202405132120";
        for (Map<String, Object> map : maps) {
            Integer id = MapUtils.getInteger(map, "id");
            Integer uid = MapUtils.getInteger(map, "uid");
            String scoreType = MapUtils.getString(map, "scoreType");
            BigDecimal score = BigDecimal.valueOf(MapUtils.getDouble(map, "score"));
            Integer walletType = null;
            if ("购物积分".equals(scoreType)) {
                walletType = 1;
            }
            if ("奖励积分".equals(scoreType)) {
                walletType = 2;
            }
            if ("换购积分".equals(scoreType)) {
                walletType = 3;
            }
            if ("福券积分".equals(scoreType)) {
                walletType = 4;
            }
            i++;
            if (walletType != null) {
                Wallet wallet = getByUser(uid, walletType);
                if (wallet != null && ArithmeticUtils.gte(wallet.getBalance(), BigDecimal.ZERO)) {
                    transferToPlatform(uid, walletType, wallet.getBalance(), WalletFlow.OperateEnum.调账.name(), externalNo, "CS");
                }
            }
            log.info("增在执行减少积分操作，总数:{}, 当前条数:{}", maps.size(), i);
        }

        init();
    }
    @Override
    public String export(WalletRequest request) {
        Integer uid = null;
        if (com.jbp.service.util.StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        List<WalletExtResponse> list = walletDao.getList(uid, request.getType(), request.getTeamId(), request.getNickname());
        if (CollectionUtils.isEmpty(list)) {
            throw new CrmebException("未查询到订单数据");
        }
        log.info("用户积分导出用户数据查询完成...");
        List<Integer> uIdList = list.stream().map(WalletExtResponse::getUId).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        Map<Integer, WalletConfig> walletMap = walletConfigService.getWalletMap();
        LinkedList<WalletExcel> result = new LinkedList<>();
        for (WalletExtResponse e : list) {
            WalletExcel vo = new WalletExcel();
            User userVo = uidMapList.get(e.getUId());
            vo.setNickname(userVo != null ? userVo.getNickname() : "");
            vo.setAccount(userVo != null ? userVo.getAccount() : "");
            WalletConfig walletConfig = walletMap.get(e.getType());
            vo.setTypeName(walletConfig != null ? walletConfig.getName() : "");

            vo.setName(e.getName());
            vo.setBalance(e.getBalance());
            vo.setFreeze(e.getFreeze());
            result.add(vo);
        }

        FileResultVo fileResultVo = uploadService.excelLocalUpload(result, WalletExcel.class);
        log.info("用户积分列表导出下载地址:" + fileResultVo.getUrl());
        return fileResultVo.getUrl();
    }
}
