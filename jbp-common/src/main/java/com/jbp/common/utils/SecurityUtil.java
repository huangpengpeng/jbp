package com.jbp.common.utils;

import cn.hutool.core.util.ObjectUtil;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.vo.LoginUserVo;

/**
 * security工具类
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
public class SecurityUtil {

    /**
     * 获取管理员信息（从security中）
     */
    public static LoginUserVo getLoginUserVo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUserVo loginUserVo = (LoginUserVo) authentication.getPrincipal();
        if (ObjectUtil.isNull(loginUserVo)) {
            throw new CrmebException("登录信息已过期，请重新登录");
        }
        return loginUserVo;
    }

}
