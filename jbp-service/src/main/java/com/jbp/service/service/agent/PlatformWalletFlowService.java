package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.PlatformWallet;
import com.jbp.common.model.agent.PlatformWalletFlow;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.PlatformWalletFlowVo;

import java.math.BigDecimal;
import java.util.List;

public interface PlatformWalletFlowService extends IService<PlatformWalletFlow> {

    PlatformWalletFlow add(Integer type, String operate, String action, String externalNo, String postscript, BigDecimal amt, BigDecimal orgBalance, BigDecimal tagBalance);

    PageInfo<PlatformWalletFlow> pageList(Integer type,String dateLimit,String externalNo,String action, PageParamRequest pageParamRequest);

    List<PlatformWalletFlowVo> excel(Integer type, String dateLimit, String externalNo,String action);

}
