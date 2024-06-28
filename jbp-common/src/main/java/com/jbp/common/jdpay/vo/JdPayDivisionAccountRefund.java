package com.jbp.common.jdpay.vo;


import java.io.Serializable;
import java.util.List;

/**
 * 分账业务字段
 * @author liulian115
 */
public class JdPayDivisionAccountRefund implements Serializable {

    /**
     * 版本号
     */
    private String version="V2";
    /**
     * 分账交易信息
     * @see JdPayDivisionAccountRefundInfo
     */
    private List<JdPayDivisionAccountRefundInfo> divisionAccountRefundInfoList;

    @Override
    public String toString() {
        return "{\"JdPayDivisionAccountRefund\":{"
                + "\"version\":\"" + version + "\""
                + ", \"divisionAccountRefundInfoList\":" + divisionAccountRefundInfoList
                + "}}";
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<JdPayDivisionAccountRefundInfo> getDivisionAccountRefundInfoList() {
        return divisionAccountRefundInfoList;
    }

    public void setDivisionAccountRefundInfoList(List<JdPayDivisionAccountRefundInfo> divisionAccountRefundInfoList) {
        this.divisionAccountRefundInfoList = divisionAccountRefundInfoList;
    }
}
