package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.jbp.common.enums.RoleEnum;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.admin.SystemRole;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.SystemAdminAddRequest;
import com.jbp.common.request.SystemAdminRequest;
import com.jbp.common.request.SystemAdminUpdateRequest;
import com.jbp.common.response.SystemAdminResponse;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.service.dao.SystemAdminDao;
import com.jbp.service.service.SystemAdminService;
import com.jbp.service.service.SystemRoleService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SystemAdminServiceImpl 接口实现
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
public class SystemAdminServiceImpl extends ServiceImpl<SystemAdminDao, SystemAdmin> implements SystemAdminService {

    @Resource
    private SystemAdminDao dao;

    @Autowired
    private SystemRoleService systemRoleService;

    /**
     * 通过用户名获取用户
     *
     * @param username 用户名
     * @param type     用户类型
     * @return SystemAdmin
     */
    @Override
    public SystemAdmin selectUserByUserNameAndType(String username, Integer type) {
        List<Integer> types = addSuperRoleType(type);
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemAdmin::getAccount, username);
        lqw.in(SystemAdmin::getType, types);
        lqw.eq(SystemAdmin::getIsDel, false);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    @Override
    public SystemAdmin selectUserByPhoneAndType(String phone, Integer adminType) {
        List<Integer> types = addSuperRoleType(adminType);
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemAdmin::getPhone, phone);
        lqw.in(SystemAdmin::getType, types);
        lqw.eq(SystemAdmin::getIsDel, false);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 后台管理员列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public List<SystemAdminResponse> getList(SystemAdminRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        //带SystemAdminRequest类的多条件查询
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lqw.eq(SystemAdmin::getStatus, request.getStatus());
        }
        if (StrUtil.isNotBlank(request.getRealName())) {
            String decode = URLUtil.decode(request.getRealName());
            lqw.and(i -> i.like(SystemAdmin::getRealName, decode)
                    .or().like(SystemAdmin::getAccount, decode));
        }
        SystemAdmin currentAdmin = SecurityUtil.getLoginUserVo().getUser();
        lqw.eq(SystemAdmin::getMerId, currentAdmin.getMerId());
        // 超级管理员查看平台账户 商户超级管理员查看自己和商户管理员
        // 1=1，2,3 2=2，4
        List<Integer> types = new ArrayList<>();
        if (currentAdmin.getType().equals(RoleEnum.SUPER_MERCHANT.getValue())) {
            types.add(RoleEnum.MERCHANT_ADMIN.getValue());
            types.add(RoleEnum.SUPER_MERCHANT.getValue());
            lqw.in(SystemAdmin::getType, types);
        } else if (currentAdmin.getType().equals(RoleEnum.SUPER_ADMIN.getValue())) {
            types.add(RoleEnum.PLATFORM_ADMIN.getValue());
            types.add(RoleEnum.SUPER_ADMIN.getValue());
            lqw.in(SystemAdmin::getType, types);
        } else {
            lqw.eq(SystemAdmin::getType, systemRoleService.getRoleTypeByCurrentAdmin());
        }
        if (ObjectUtil.isNotNull(request.getRoles()) && request.getRoles() > 0) {
//            lqw.apply(" find_in_set('{0}', roles)", request.getRoles());
            lqw.apply(StrUtil.format(" find_in_set('{}', roles)", request.getRoles()));
        }
        List<SystemAdmin> systemAdmins = dao.selectList(lqw);
        if (CollUtil.isEmpty(systemAdmins)) {
            return CollUtil.newArrayList();
        }
        List<SystemAdminResponse> adminResponseList = new ArrayList<>();
        List<SystemRole> roleList = systemRoleService.getListByMerId(currentAdmin.getMerId());
        for (SystemAdmin admin : systemAdmins) {
            SystemAdminResponse sar = new SystemAdminResponse();
            BeanUtils.copyProperties(admin, sar);
            sar.setLastTime(admin.getUpdateTime());
            List<Integer> roleIds = CrmebUtil.stringToArrayInt(admin.getRoles());
            List<String> roleNames = new ArrayList<>();
            for (Integer roleId : roleIds) {
                for (SystemRole role : roleList) {
                    if (role.getId().equals(roleId)) {
                        roleNames.add(role.getRoleName());
                    }
                }
            }
            sar.setRoleNames(StringUtils.join(roleNames, ","));
            adminResponseList.add(sar);
        }
        return adminResponseList;
    }

    /**
     * 是否存在角色
     *
     * @param roleId 角色id
     * @return Boolean
     */
    @Override
    public Boolean isExistRole(Integer roleId) {
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.select(SystemAdmin::getId);
        lqw.apply(StrUtil.format(" find_in_set('{}', roles)", roleId));
        lqw.last(" limit 1");
        SystemAdmin systemAdmin = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(systemAdmin);
    }

    /**
     * 新增管理员
     * @param systemAdminAddRequest 新增参数
     * @return Boolean
     */
    @Override
    public Boolean saveAdmin(SystemAdminAddRequest systemAdminAddRequest) {
        SystemAdmin currentUser = SecurityUtil.getLoginUserVo().getUser();

        // 管理员名称唯一校验
        if (checkAccount(systemAdminAddRequest.getAccount())) {
            throw new CrmebException("管理员已存在");
        }

        SystemAdmin systemAdmin = new SystemAdmin();
        BeanUtils.copyProperties(systemAdminAddRequest, systemAdmin);

        String pwd = CrmebUtil.encryptPassword(systemAdmin.getPwd());
        systemAdmin.setPwd(pwd);
        systemAdmin.setMerId(currentUser.getMerId());
        systemAdmin.setType(systemRoleService.getRoleTypeByCurrentAdmin());
        return save(systemAdmin);
    }

    /**
     * 管理员名称唯一校验
     * @param account 管理员账号
     * @return Boolean
     */
    @Override
    public Boolean checkAccount(String account) {
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemAdmin::getAccount, account);
        lqw.eq(SystemAdmin::getIsDel, false);
        return dao.selectCount(lqw) > 0;
    }

    /**
     * 更新管理员
     */
    @Override
    public Boolean updateAdmin(SystemAdminUpdateRequest systemAdminRequest) {
        SystemAdmin currentUser = SecurityUtil.getLoginUserVo().getUser();

        SystemAdmin adminDetail = getDetail(systemAdminRequest.getId());
        // 超级管理员不允许编辑
        if (adminDetail.getType().equals(RoleEnum.SUPER_ADMIN.getValue())
                || adminDetail.getType().equals(RoleEnum.SUPER_MERCHANT.getValue())) {
            throw new CrmebException("系统内置权限，不允许编辑");
        }
        if (!currentUser.getMerId().equals(adminDetail.getMerId())) {
            throw new CrmebException("不能操作非自己商户的数据");
        }
        verifyAccount(systemAdminRequest.getId(), systemAdminRequest.getAccount());
        SystemAdmin systemAdmin = new SystemAdmin();
        BeanUtils.copyProperties(systemAdminRequest, systemAdmin);
        systemAdmin.setPwd(null);
        if (StrUtil.isNotBlank(systemAdminRequest.getPwd())) {
            String pwd = CrmebUtil.encryptPassword(systemAdminRequest.getPwd());
            systemAdmin.setPwd(pwd);
        }
        systemAdmin.setMerId(currentUser.getMerId());
        return updateById(systemAdmin);
    }

    /**
     * 删除管理员
     * @param id 管理员id
     * @return 删除结果
     */
    @Override
    public Boolean removeAdmin(Integer id) {
        SystemAdmin perDelAdmin = getDetail(id);
        if (perDelAdmin.getType().equals(RoleEnum.SUPER_ADMIN.getValue())
                || perDelAdmin.getType().equals(RoleEnum.SUPER_MERCHANT.getValue())) {
            throw new CrmebException("系统内置权限，不允许删除");
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(perDelAdmin.getMerId())) {
            throw new CrmebException("不能操作非自己商户的数据");
        }
        return removeById(id);
    }

    /**
     * 修改后台管理员状态
     * @param id 管理员id
     * @param status 状态
     * @return Boolean
     */
    @Override
    public Boolean updateStatus(Integer id, Boolean status) {
        SystemAdmin currentUser = SecurityUtil.getLoginUserVo().getUser();
        // 超级管理员不允许编辑
        SystemAdmin systemAdmin = getDetail(id);
        if (systemAdmin.getType().equals(RoleEnum.SUPER_ADMIN.getValue())
                || systemAdmin.getType().equals(RoleEnum.SUPER_MERCHANT.getValue())) {
            throw new CrmebException("系统内置权限，不允许编辑");
        }
        if (!currentUser.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException("不能操作非自己商户的数据");
        }
        if (systemAdmin.getStatus().equals(status)) {
            return true;
        }
        systemAdmin.setStatus(status);
        return updateById(systemAdmin);
    }

    /**
     * 管理员详情
     * @param id 管理员id
     * @return SystemAdmin
     */
    @Override
    public SystemAdmin getDetail(Integer id) {
        SystemAdmin systemAdmin = getById(id);
        if (ObjectUtil.isNull(systemAdmin) || systemAdmin.getIsDel()) {
            throw new CrmebException("管理员不存在");
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (admin.getMerId() > 0) {
            if (!admin.getMerId().equals(systemAdmin.getMerId())) {
                throw new CrmebException("不能操作非自己商户的数据");
            }
        }
        return systemAdmin;
    }

    /**
     * 获取管理员名称map
     * @param idList id列表
     * @return Map
     */
    @Override
    public Map<Integer, String> getNameMapByIdList(List<Integer> idList) {
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.select(SystemAdmin::getId, SystemAdmin::getRealName);
        lqw.in(SystemAdmin::getId, idList);
        List<SystemAdmin> adminList = dao.selectList(lqw);
        Map<Integer, String> map = CollUtil.newHashMap();
        adminList.forEach(admin -> {
            map.put(admin.getId(), admin.getRealName());
        });
        return map;
    }

    @Override
    public Map<Integer, SystemAdmin> getMapByMerIdList(List<Integer> idList) {
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.in(SystemAdmin::getMerId, idList);
        List<SystemAdmin> adminList = dao.selectList(lqw);
        return FunctionUtil.keyValueMap(adminList, SystemAdmin::getMerId);
    }

    /**
     * 校验账号唯一性（管理员更新时）
     * @param id 管理员id
     * @param account 管理员账号
     */
    private void verifyAccount(Integer id, String account) {
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.ne(SystemAdmin::getId, id);
        lqw.eq(SystemAdmin::getAccount, account);
        SystemAdmin systemAdmin = dao.selectOne(lqw);
        if (ObjectUtil.isNotNull(systemAdmin)) {
            throw new CrmebException("账号已存在");
        }
    }

    /**
     * 登录之前查询权限带上超管标识
     *
     * @param type 用户类型
     */
    private List<Integer> addSuperRoleType(Integer type) {
        List<Integer> types = new ArrayList<>();
        if (type.equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
            types.add(RoleEnum.SUPER_ADMIN.getValue());
            types.add(RoleEnum.PLATFORM_ADMIN.getValue());
        }
        if (type.equals(RoleEnum.MERCHANT_ADMIN.getValue())) {
            types.add(RoleEnum.SUPER_MERCHANT.getValue());
            types.add(RoleEnum.MERCHANT_ADMIN.getValue());
        }
        return types;
    }

}

