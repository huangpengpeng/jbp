package com.jbp.common.kqbill.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * 快钱查询头
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class KqHeadParams {

    public KqHeadParams(String version, String messageType, String memberCode, String externalRefNumber) {
        this.version = version;
        this.messageType = messageType;
        this.memberCode = memberCode;
        this.externalRefNumber = externalRefNumber;
    }

    private String version;

    private String messageType;

    private String memberCode;

    private String externalRefNumber;

}
