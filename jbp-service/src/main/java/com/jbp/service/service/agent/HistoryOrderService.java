package com.jbp.service.service.agent;

import com.github.pagehelper.PageInfo;
import com.jbp.common.request.HistoryOrderEditRequest;
import com.jbp.common.request.HistoryOrderRequest;
import com.jbp.common.request.HistoryOrderShipRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.HistoryOrderResponse;

public interface HistoryOrderService {

    PageInfo<HistoryOrderResponse> pageList(HistoryOrderRequest request, PageParamRequest pageParamRequest);

    void edit(HistoryOrderEditRequest request);

    Boolean jstCall(String dbName, String orderSn, String shipName, String shipNo);

    void ship(HistoryOrderShipRequest request);

}
