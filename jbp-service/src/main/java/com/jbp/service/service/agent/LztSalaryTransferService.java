package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztSalaryTransfer;
import com.jbp.common.request.agent.LztSalaryTransferRequest;

import java.util.List;

public interface LztSalaryTransferService extends IService<LztSalaryTransfer> {

    void create(LztAcct payer, List<LztSalaryTransferRequest> requests);






}
