package com.jbp.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbp.admin.copyright.CopyrightInfoResponse;
import com.jbp.admin.copyright.CopyrightUpdateInfoRequest;
import com.jbp.common.config.CrmebConfig;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.utils.RestTemplateUtil;
import com.jbp.service.service.SystemAttachmentService;
import com.jbp.service.service.SystemConfigService;

import cn.hutool.core.util.StrUtil;

/**
 * 版权服务实现类
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
public class CopyrightServiceImpl implements CopyrightService {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private CrmebConfig crmebConfig;
    @Autowired
    private RestTemplateUtil restTemplateUtil;
    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 获取版权信息
     */
    @Override
    public CopyrightInfoResponse getInfo() {
        CopyrightInfoResponse response = new CopyrightInfoResponse();
        String domainName = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_API_URL);
        if (StrUtil.isBlank(domainName)) {
            response.setStatus(-2);
            return response;
        }
        String label = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_LABEL);
        String version = crmebConfig.getVersion();
        if (StrUtil.isBlank(version)) {
            throw new CrmebException("请先在yml中配置版本号");
        }
        response.setDomainUrl(domainName);
        response.setLabel(Integer.parseInt(label));
        response.setVersion(version);
        response.setCompanyName(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_INFO));
        response.setCompanyImage(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_IMAGE));
        return response;
    }

    /**
     * 编辑公司版权信息
     */
    @Override
    @Transactional
    public Boolean updateCompanyInfo(CopyrightUpdateInfoRequest request) {
        Boolean update = systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_INFO, request.getCompanyName());
        Boolean update1 = systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_IMAGE, systemAttachmentService.clearPrefix(request.getCompanyImage()));
        return update && update1;
    }

    /**
     * 获取商户版权信息
     */
    @Override
    public String getCompanyInfo() {
        return systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_INFO);
    }
}
