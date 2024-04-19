package com.jbp.service.service;

import com.jbp.common.lianlian.result.AcctInfoResult;
import com.jbp.common.lianlian.result.LztQueryAcctInfoResult;
import com.jbp.common.model.agent.LztAcct;

public interface DegreePayService {

    AcctInfoResult queryAcct(LztAcct lztAcct);
    LztQueryAcctInfoResult queryBankAcct(LztAcct lztAcct);
}
