package com.jbp.common.encryptapi;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.jbp.common.exception.CrmebException;

import cn.hutool.crypto.SecureUtil;

/**
 * Author:Bobby
 * DateTime:2019/4/9
 **/
@ConfigurationProperties(prefix = "rsa.encrypt")
@Configuration
public class SecretKeyConfig{

    private String key;

    private String charset = "UTF-8";

    private boolean open = true;

    private boolean showLog = false;

    /**
     * 请求数据时间戳校验时间差
     * 超过指定时间的数据认定为伪造
     */
    private boolean timestampCheck = false;

  
	public String getKey() {
		return key;
	}
	
	public String getSecureKey() {
		return afterCutAndappend(this.key, "0", 32);
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isShowLog() {
        return showLog;
    }

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }

    public boolean isTimestampCheck() {
        return timestampCheck;
    }

    public void setTimestampCheck(boolean timestampCheck) {
        this.timestampCheck = timestampCheck;
    }
    
	public String decryptStr(String textString) {
		try {
			textString = textString.replaceAll("＼", "");
			textString = SecureUtil.des(getSecureKey().getBytes()).decryptStr(Base64Util.decode(textString));
			return textString;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CrmebException(e.getMessage());
		}
	}
    
    public String encryptStr(String textString) {
    	try {
			textString= SecureUtil.des(getSecureKey().getBytes()).encryptBase64(textString.getBytes());
			return textString;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CrmebException(e.getMessage());
		}
    }
    
	/**
	 * 字符串过长 截取 不满足追加
	 * @param textString
	 * @param append
	 * @param len
	 * @return
	 */
	public String afterCutAndappend(String textString,String append,int len) {
		if (textString.length() > len) {
			textString = textString.substring(0, len);
		}
		else {
			for (int i = 0; i < len; i++) {
				if (textString.length() < len) {
					textString = textString + append;
				}
			}
		}
		return textString;
	}
	
	public String afterCutAndappend(String textString,String append,int start,int len) {
		if (textString.length() > len) {
			textString = textString.substring(start, start+len);
		}
		else {
			for (int i = 0; i < len; i++) {
				if (textString.length() < len) {
					textString = textString + append;
				}
			}
		}
		return textString;
	}
}