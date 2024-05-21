package com.jbp.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionDTO implements Serializable {

    private static final long serialVersionUID = 7194390322936295902L;

    private String bizId;

    private String channelName;

    private String mobile;

    private String noceStr;

    private Long timestamp;

    private String accessToken;

    private String refreshToken;
}
