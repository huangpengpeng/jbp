package com.jbp.common.jdpay.vo;


import java.io.Serializable;
import java.util.List;

/**
 * 分账业务字段
 */
public class JdPayDivisionAccount implements Serializable {

    /**
     * 版本号
     */
    private String version = "v2";
//    private String version;
    /**
     * 分账交易信息
     */
    private List<JdPayDivisionAccountTradeInfo> divisionAccountTradeInfoList;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<JdPayDivisionAccountTradeInfo> getDivisionAccountTradeInfoList() {
        return divisionAccountTradeInfoList;
    }

    public void setDivisionAccountTradeInfoList(List<JdPayDivisionAccountTradeInfo> divisionAccountTradeInfoList) {
        this.divisionAccountTradeInfoList = divisionAccountTradeInfoList;
    }

    @Override
    public String toString() {
        return "{\"JdPayDivisionAccount\":{"
                + "\"version\":\"" + version + "\""
                + ", \"divisionAccountTradeInfoList\":" + divisionAccountTradeInfoList
                + "}}";
    }
}
