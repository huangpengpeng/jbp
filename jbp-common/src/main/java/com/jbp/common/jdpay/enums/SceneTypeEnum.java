package com.jbp.common.jdpay.enums;


/**
 * 三方聚合场景类型
 */
public enum SceneTypeEnum {

	ONLINE_APP("ONLINE_APP","线上移动端支付"),
	ONLINE_PC("ONLINE_PC","线上PC支付"),
	OFFLINE("OFFLINE","线下支付"),
	OFFLINE_WX("OFFLINE_WX","线下微信支付"),
	OFFLINE_JD("OFFLINE_JD","线下京东支付"),
	OFFLINE_ALI("OFFLINE_ALI","线下支付宝支付"),
	;

	private String code;
    private String desc;

    SceneTypeEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public static SceneTypeEnum getEnum(String code) {
        for (SceneTypeEnum t : values()){
            if (t.code.equals(code)) {
				return t;
			}
        }
        throw new IllegalArgumentException("SceneTyepEnum is illegal");
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
