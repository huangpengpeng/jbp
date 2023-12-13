package com.jbp.service.service.impl;

import com.jbp.service.service.QiNiuService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.jbp.common.exception.CrmebException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;


/**
 * AsyncServiceImpl 同步到云服务
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
public class QiNiuServiceImpl implements QiNiuService {
    private static final Logger logger = LoggerFactory.getLogger(QiNiuServiceImpl.class);

    @Override
    public void uploadFile(UploadManager uploadManager, String upToken, String webPth, String localFile, File file) {
        try {
            logger.info("上传文件开始：" + localFile);

//            File file = new File(localFile);
            if(!file.exists()){
                logger.info("上传文件" + localFile + "不存在：");
                return;
            }

            Response put = uploadManager.put(file, webPth, upToken);
            put.close();
            logger.info("上传文件 -- 结束：" + put.address);
        } catch (QiniuException ex) {
            throw new CrmebException(ex.getMessage());
        }
    }
}

