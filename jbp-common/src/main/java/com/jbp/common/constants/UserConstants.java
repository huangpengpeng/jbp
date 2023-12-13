package com.jbp.common.constants;

/**
 * 用户常量表
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public class UserConstants {

    /** 注册类型——H5 */
    public static final String REGISTER_TYPE_H5 = "h5";
    /** 注册类型——公众号 */
    public static final String REGISTER_TYPE_WECHAT = "wechat";
    /** 注册类型——小程序 */
    public static final String REGISTER_TYPE_ROUTINE = "routine";
    /** 注册类型——微信ios */
    public static final String REGISTER_TYPE_IOS_WX = "iosWx";
    /** 注册类型——微信安卓 */
    public static final String REGISTER_TYPE_ANDROID_WX = "androidWx";
    /** 注册类型——ios */
    public static final String REGISTER_TYPE_IOS = "ios";

    /**
     * =========================================================
     * UserToken部分
     * =========================================================
     */
    /** 用户Token类型——公众号 */
    public static final Integer USER_TOKEN_TYPE_WECHAT = 1;
    /** 用户Token类型——小程序 */
    public static final Integer USER_TOKEN_TYPE_ROUTINE = 2;
    /** 用户Token类型——微信ios */
    public static final Integer USER_TOKEN_TYPE_IOS_WX  = 5;
    /** 用户Token类型——微信android */
    public static final Integer USER_TOKEN_TYPE_ANDROID_WX = 6;
    /** 用户Token类型——ios */
    public static final Integer USER_TOKEN_TYPE_IOS  = 7;
}
