package com.jbp.service.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.excel.EasyExcelUtils;
import com.jbp.common.excel.FundClearingExcel;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.vo.CloudVo;
import com.jbp.service.service.OssService;

import com.jbp.service.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * AsyncServiceImpl 同步到云服务
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class OssServiceImpl implements OssService {

    private static final Logger logger = LoggerFactory.getLogger(OssServiceImpl.class);

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public void upload(CloudVo cloudVo, String webPth, String localFile, File file) {
        logger.info("上传文件开始：" + localFile);
        OSS ossClient = new OSSClientBuilder().build(cloudVo.getRegion(), cloudVo.getAccessKey(), cloudVo.getSecretKey());
        try {
            //判断bucket是否存在
            if (!ossClient.doesBucketExist(cloudVo.getBucketName())) {
                ossClient.createBucket(cloudVo.getBucketName());
            }

            if (!file.exists()) {
                logger.info("上传文件" + localFile + "不存在：");
                return;
            }
            PutObjectRequest putObjectRequest = new PutObjectRequest(cloudVo.getBucketName(), webPth, file);
            // 上传文件。
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
            logger.info("上传文件 -- 结束：" + putObjectResult.getETag());

        } catch (Exception e) {
            throw new CrmebException(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public String upload(InputStream is, String fileName) {
        CloudVo cloudVo = new CloudVo();
        cloudVo.setDomain(systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_AL_UPLOAD_URL));
        cloudVo.setAccessKey(systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_AL_ACCESS_KEY));
        cloudVo.setSecretKey(systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_AL_SECRET_KEY));
        cloudVo.setBucketName(systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_AL_STORAGE_NAME));
        cloudVo.setRegion(systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_AL_STORAGE_REGION));

        String webPth = "jbp/file/" + System.currentTimeMillis() + "/" + fileName;
        logger.info("上传文件开始：" + fileName);
        OSS ossClient = new OSSClientBuilder().build(cloudVo.getRegion(), cloudVo.getAccessKey(), cloudVo.getSecretKey());
        try {
            //判断bucket是否存在
            if (!ossClient.doesBucketExist(cloudVo.getBucketName())) {
                ossClient.createBucket(cloudVo.getBucketName());
            }
            if (is == null) {
                logger.info("上传文件" + fileName + "不存在：");
                return null;
            }
            PutObjectRequest putObjectRequest = new PutObjectRequest(cloudVo.getBucketName(), webPth, is);
            // 上传文件。
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
            logger.info("上传文件 -- 结束：" + putObjectResult.getETag());

        } catch (Exception e) {
            throw new CrmebException(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return cloudVo.getDomain() + "/" + webPth;
    }

}

