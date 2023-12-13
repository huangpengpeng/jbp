package com.jbp.front.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.jbp.front.service.QrCodeService;
import com.jbp.common.constants.Constants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.QRCodeUtil;
import com.jbp.common.utils.RestTemplateUtil;
import com.jbp.common.vo.QrCodeVo;
import com.jbp.service.service.WechatService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * QrCodeServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class QrCodeServiceImpl implements QrCodeService {

    @Autowired
    private RestTemplateUtil restTemplateUtil;
    @Autowired
    private WechatService wechatService;

    /**
     * 二维码
     *
     * @return QrCodeVo
     */
    @Override
    public QrCodeVo getWecahtQrCode(JSONObject data) {
        StringBuilder scene = new StringBuilder();
        String page = "";
        try {
            if (ObjectUtil.isNotNull(data)) {
                Map<Object, Object> dataMap = JSONObject.toJavaObject(data, Map.class);

                for (Map.Entry<Object, Object> m : dataMap.entrySet()) {
                    if (m.getKey().equals("path")) {
                        //前端路由， 不需要拼参数
                        page = m.getValue().toString();
                        continue;
                    }
                    if (scene.length() > 0) {
                        scene.append(",");
                    }
                    scene.append(m.getKey()).append(":").append(m.getValue());
                }
            }
        } catch (Exception e) {
            throw new CrmebException("url参数错误 " + e.getMessage());
        }
        QrCodeVo vo = new QrCodeVo();
        vo.setCode(wechatService.createQrCode(page, scene.length() > 0 ? scene.toString() : ""));
        return vo;
    }

    /**
     * 远程图片转base64
     *
     * @param url 图片链接地址
     * @return QrCodeVo
     */
    @Override
    public QrCodeVo urlToBase64(String url) {
        byte[] bytes = restTemplateUtil.getBuffer(url);
        String base64Image = CrmebUtil.getBase64Image(Base64.encodeBase64String(bytes));
        QrCodeVo vo = new QrCodeVo();
        vo.setCode(base64Image);
        return vo;
    }

    /**
     * 字符串转base64
     *
     * @param text 待转换字符串
     * @return QrCodeVo base64格式
     */
    @Override
    public QrCodeVo strToBase64(String text, Integer width, Integer height) {
        if ((width < 50 || height < 50) && (width > 500 || height > 500) && text.length() >= 999) {
            throw new CrmebException("生成二维码参数不合法");
        }
        String base64Image;
        try {
            base64Image = QRCodeUtil.crateQRCode(text, width, height);
        } catch (Exception e) {
            throw new CrmebException("生成二维码异常");
        }
        QrCodeVo vo = new QrCodeVo();
        vo.setCode(base64Image);
        return vo;
    }
}

