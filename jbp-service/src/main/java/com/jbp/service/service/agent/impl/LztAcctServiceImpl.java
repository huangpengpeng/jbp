package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.lianlian.result.*;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.service.dao.agent.LztAcctDao;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.agent.LztAcctService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LztAcctServiceImpl extends ServiceImpl<LztAcctDao, LztAcct> implements LztAcctService {

    @Resource
    private LianLianPayService lianLianPayService;

    @Override
    public LztAcct getByLianLianAcct(String lianLianAcct) {
        return getOne(new QueryWrapper<LztAcct>().lambda().eq(LztAcct::getLianLianAcct, lianLianAcct));
    }

    @Override
    public LztAcct create(Integer merId, String lianLianAcct) {
        LztAcct lztAcct = getByLianLianAcct(lianLianAcct);
        if (lztAcct == null) {
            UserInfoResult userInfoResult = lianLianPayService.lztQueryUserInfo(lianLianAcct);
            lztAcct = new LztAcct(merId, lianLianAcct, userInfoResult.getUser_name());
            save(lztAcct);
        }
        return lztAcct;
    }

    @Override
    public LztAcct queryAcctInfo(String userId) {
        LztAcct lztAcct = getByLianLianAcct(userId);
        AcctInfoResult acctInfoResult = lianLianPayService.lztLianLianQueryAcctInfo(userId);
        LztQueryAcctInfoResult lztQueryAcctInfoResult = lianLianPayService.lztQueryAcctInfo(userId);
        lztAcct.setAcctInfoList(acctInfoResult.getAcctinfo_list());
        lztAcct.setBankAcctInfoList(lztQueryAcctInfoResult.getList());
        return lztAcct;
    }

    @Override
    public List<LztAcct> getByMerId(Integer merId) {
        List<LztAcct> list = list(new QueryWrapper<LztAcct>().lambda().eq(LztAcct::getMerId, merId));
        for (LztAcct lztAcct : list) {
            AcctInfoResult acctInfoResult = lianLianPayService.lztLianLianQueryAcctInfo(lztAcct.getLianLianAcct());
            LztQueryAcctInfoResult lztQueryAcctInfoResult = lianLianPayService.lztQueryAcctInfo(lztAcct.getLianLianAcct());
            List<AcctInfo> acctinfoList = acctInfoResult.getAcctinfo_list();
            for (AcctInfo acctInfo : acctinfoList) {
                acctInfo.setAcct_type(LianLianPayConfig.AcctType.getName(acctInfo.getAcct_type()));
                acctInfo.setAcct_state(LianLianPayConfig.AcctState.valueOf(acctInfo.getAcct_state()).getName());
            }
            lztAcct.setAcctInfoList(acctinfoList);
            List<LztQueryAcctInfo> LztQueryAcctInfoList = lztQueryAcctInfoResult.getList();
            for (LztQueryAcctInfo lztQueryAcctInfo : LztQueryAcctInfoList) {
                lztQueryAcctInfo.setAcct_stat(LianLianPayConfig.AcctState.valueOf(lztQueryAcctInfo.getAcct_stat()).getName());
            }
            lztAcct.setBankAcctInfoList(LztQueryAcctInfoList);
        }
        return list;
    }
}
