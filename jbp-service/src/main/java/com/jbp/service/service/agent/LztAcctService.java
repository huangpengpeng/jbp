package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.lianlian.result.AcctInfoResult;
import com.jbp.common.lianlian.result.LztQueryAcctInfoResult;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztAcctApply;

import java.util.List;

public interface LztAcctService extends IService<LztAcct> {

    LztAcct getByLianLianAcct(String lianLianAcct);

    LztAcct create(Integer merId, String lianLianAcct);

    LztAcct queryAcctInfo(String userId);

    List<LztAcct> getByMerId(Integer merId);
}
