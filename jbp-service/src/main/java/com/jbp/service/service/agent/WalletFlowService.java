package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.WalletFlowVo;

import java.math.BigDecimal;
import java.util.List;

public interface WalletFlowService extends IService<WalletFlow> {
    WalletFlow add(Integer uid, Integer type, BigDecimal amt, String operate, String action, String externalNo,
                   BigDecimal orgBalance, BigDecimal tagBalance, String postscript);

    PageInfo<WalletFlow> pageList(Integer uid, Integer type, String dateLimit, String externalNo,String action, PageParamRequest pageParamRequest);

    List<WalletFlow> getByUser(Integer uid, String externalNo, String operate, String action);

    PageInfo<WalletFlow> pageWalletList(Integer uid, Integer type, String action, PageParamRequest pageParamRequest);

    List<WalletFlow> details(Integer uid, String action);

    List<WalletFlowVo> excel(Integer uid, Integer type, String dateLimit, String externalNo,String action);
}
