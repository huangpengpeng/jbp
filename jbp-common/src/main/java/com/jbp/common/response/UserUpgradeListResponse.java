package com.jbp.common.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class UserUpgradeListResponse implements Serializable {

    public UserUpgradeListResponse(Long id, String username, String numberCode, String rankName, BigDecimal teamAmt, BigDecimal differenceAmt, BigDecimal selfAmt) {
        this.id = id;
        this.username = username;
        this.numberCode = numberCode;
        this.rankName = rankName;
        this.teamAmt = teamAmt;
        this.differenceAmt = differenceAmt;
        this.selfAmt = selfAmt;
    }

    private Long id;

    private String username;

    private String numberCode;

    private String rankName;

    private BigDecimal teamAmt;

    private BigDecimal differenceAmt;

    private BigDecimal selfAmt;
}
