package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.LztAcctApply;
import com.jbp.common.model.agent.LztAcctOpen;
import com.jbp.common.model.agent.LztPayChannel;
import com.jbp.common.request.PageParamRequest;

import java.io.File;

public interface LztAcctOpenService extends IService<LztAcctOpen> {

    LztAcctOpen apply( String userId, String userType, String returnUrl, String businessScope, LztPayChannel lztPayChannel);

    LztAcctOpen apply2( String userId, String userType, String returnUrl, String businessScope,
                        LztPayChannel lztPayChannel, String sync_open_lzt, String open_bank);

    LztAcctOpen yopApply(String signName, String id_card, String frontUrl, String backUrl, String mobile, String province,
                         String city, String district, String address, String bankCardNo, String bankCode, LztPayChannel lztPayChannel);

    void refresh(String accpTxno);

    void del(Long id);

    LztAcctOpen getByTxnSeqno(String txnSeqno);

    LztAcctOpen getByAccpTxno(String accpTxno);

    Boolean has(String userId);

    PageInfo<LztAcctOpen> pageList(Integer merId, String userId, String status, PageParamRequest pageParamRequest);

}
