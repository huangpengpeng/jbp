package com.jbp.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.jbp.service.dao.SystemRoleDao;
import com.jbp.service.service.SystemAdminService;
import com.jbp.service.service.SystemMenuService;
import com.jbp.service.service.SystemRoleMenuService;
import com.jbp.service.service.SystemRoleService;
import com.jbp.common.enums.RoleEnum;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.admin.SystemMenu;
import com.jbp.common.model.admin.SystemRole;
import com.jbp.common.model.admin.SystemRoleMenu;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.SystemRoleRequest;
import com.jbp.common.request.SystemRoleSearchRequest;
import com.jbp.common.request.SystemRoleStatusRequest;
import com.jbp.common.response.RoleInfoResponse;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.vo.MenuCheckTree;
import com.jbp.common.vo.MenuCheckVo;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SystemRoleServiceImpl 接口实现
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
public class SystemRoleServiceImpl extends ServiceImpl<SystemRoleDao, SystemRole> implements SystemRoleService {

    @Resource
    private SystemRoleDao dao;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private SystemRoleMenuService systemRoleMenuService;

    @Autowired
    private SystemMenuService systemMenuService;

    @Autowired
    private SystemAdminService systemAdminService;

    /**
     * 列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     * @return List<SystemRole>
     */
    @Override
    public List<SystemRole> getList(SystemRoleSearchRequest request, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<SystemRole> lqw = Wrappers.lambdaQuery();
        lqw.select(SystemRole::getId, SystemRole::getRoleName, SystemRole::getStatus,
                SystemRole::getCreateTime, SystemRole::getUpdateTime);
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lqw.eq(SystemRole::getStatus, request.getStatus());
        }
        if (StrUtil.isNotBlank(request.getRoleName())) {
            String decode = URLUtil.decode(request.getRoleName());
            lqw.like(SystemRole::getRoleName, decode);
        }
        lqw.eq(SystemRole::getMerId, systemAdmin.getMerId());
        lqw.eq(SystemRole::getType, getRoleTypeByCurrentAdmin(systemAdmin.getType()));
        lqw.orderByAsc(SystemRole::getId);
        return dao.selectList(lqw);
    }

    /**
     * 修改身份状态
     */
    @Override
    public Boolean updateStatus(SystemRoleStatusRequest request) {
        SystemRole role = getById(request.getId());
        if (ObjectUtil.isNull(role)) {
            throw new CrmebException("身份不存在");
        }
        // 超级管理员不允许编辑
        if (role.getType().equals(RoleEnum.SUPER_ADMIN.getValue())
                || role.getType().equals(RoleEnum.SUPER_MERCHANT.getValue())) {
            throw new CrmebException("系统内置权限，不允许编辑");
        }
        if (role.getStatus().equals(request.getStatus())) {
            return true;
        }
        role.setStatus(request.getStatus());
        return updateById(role);
    }

    /**
     * 添加身份
     *
     * @param systemRoleRequest 身份参数
     * @return Boolean
     */
    @Override
    public Boolean add(SystemRoleRequest systemRoleRequest) {
        SystemAdmin currentAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (existName(systemRoleRequest.getRoleName(), null)) {
            throw new CrmebException("角色名称重复");
        }
        SystemRole systemRole = new SystemRole();
        BeanUtils.copyProperties(systemRoleRequest, systemRole);
        List<Integer> ruleList = Stream.of(systemRoleRequest.getRules().split(",")).map(Integer::valueOf).distinct().collect(Collectors.toList());
        systemRole.setId(null);
        systemRole.setRules("");
        systemRole.setMerId(currentAdmin.getMerId());
        systemRole.setType(getRoleTypeByCurrentAdmin(currentAdmin.getType()));
        return transactionTemplate.execute(e -> {
            boolean save = save(systemRole);
            if (!save) {
                return Boolean.FALSE;
            }
            List<SystemRoleMenu> roleMenuList = ruleList.stream().map(rule -> {
                SystemRoleMenu roleMenu = new SystemRoleMenu();
                roleMenu.setRid(systemRole.getId());
                roleMenu.setMenuId(rule);
                return roleMenu;
            }).collect(Collectors.toList());
            systemRoleMenuService.saveBatch(roleMenuList, 100);
            return Boolean.TRUE;
        });
    }

    /**
     * 判断角色名称是否存在
     *
     * @param roleName 角色名称
     * @param id       角色id
     * @return Boolean
     */
    private Boolean existName(String roleName, Integer id) {
        LambdaQueryWrapper<SystemRole> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemRole::getRoleName, roleName);
        if (ObjectUtil.isNotNull(id)) {
            lqw.ne(SystemRole::getId, id);
        }
        lqw.last(" limit 1");
        Integer count = dao.selectCount(lqw);
        return count > 0;
    }

    /**
     * 修改身份管理表
     *
     * @param request 修改参数
     */
    @Override
    public Boolean edit(SystemRoleRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException("角色ID不能为空");
        }
        SystemRole role = getById(request.getId());
        if (ObjectUtil.isNull(role)) {
            throw new CrmebException("角色不存在");
        }
        SystemAdmin currentAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (!currentAdmin.getMerId().equals(role.getMerId())) {
            throw new CrmebException("非自己管理的角色不能修改");
        }
        // 超级管理员不允许编辑
        if (role.getType().equals(RoleEnum.SUPER_ADMIN.getValue())
                || role.getType().equals(RoleEnum.SUPER_MERCHANT.getValue())) {
            throw new CrmebException("系统内置权限，不允许编辑");
        }
        if (!role.getRoleName().equals(request.getRoleName())) {
            if (existName(request.getRoleName(), request.getId())) {
                throw new CrmebException("角色名称重复");
            }
        }
        List<Integer> ruleList = Stream.of(request.getRules().split(",")).map(Integer::valueOf).distinct().collect(Collectors.toList());
        List<SystemRoleMenu> roleMenuList = ruleList.stream().map(rule -> {
            SystemRoleMenu roleMenu = new SystemRoleMenu();
            roleMenu.setRid(request.getId());
            roleMenu.setMenuId(rule);
            return roleMenu;
        }).collect(Collectors.toList());

        SystemRole systemRole = new SystemRole();
        BeanUtils.copyProperties(request, systemRole);
        systemRole.setRules("");
        return transactionTemplate.execute(e -> {
            updateById(systemRole);
            systemRoleMenuService.deleteByRid(systemRole.getId());
            systemRoleMenuService.saveBatch(roleMenuList, 100);
            return Boolean.TRUE;
        });
    }

    /**
     * 删除角色
     *
     * @param id 角色id
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        SystemRole systemRole = getById(id);
        if (ObjectUtil.isNull(systemRole)) {
            throw new CrmebException("角色已删除");
        }
        if (systemRole.getType().equals(RoleEnum.SUPER_ADMIN.getValue()) ||
                systemRole.getType().equals(RoleEnum.SUPER_MERCHANT.getValue())) {
            throw new CrmebException("系统内置权限，不允许删除");
        }
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (!systemAdmin.getMerId().equals(systemRole.getMerId())) {
            throw new CrmebException("非自己平台管理的角色不能删除");
        }
        if (systemAdminService.isExistRole(id)) {
            throw new CrmebException("有管理员使用角色，无法删除");
        }
        return transactionTemplate.execute(e -> {
            dao.deleteById(id);
            systemRoleMenuService.deleteByRid(id);
            return Boolean.TRUE;
        });
    }

    /**
     * 获取角色详情
     *
     * @param id 角色id
     * @return RoleInfoResponse
     */
    @Override
    public RoleInfoResponse getInfo(Integer id) {
        SystemRole systemRole = getById(id);
        if (ObjectUtil.isNull(systemRole)) {
            throw new CrmebException("角色不存在");
        }
        // 查询角色对应的菜单(权限)
        List<Integer> menuIdList = systemRoleMenuService.getMenuListByRid(id);
        List<SystemMenu> menuList;
        if (systemRole.getType().equals(RoleEnum.SUPER_ADMIN.getValue()) || systemRole.getType().equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
            menuList = systemMenuService.getMenuCacheList(RoleEnum.PLATFORM_ADMIN.getValue());
        } else {// 商户端
            menuList = systemMenuService.getMenuCacheList(RoleEnum.MERCHANT_ADMIN.getValue());
        }

        List<MenuCheckVo> menuCheckVoList = menuList.stream().map(menu -> {
            MenuCheckVo menuCheckVo = new MenuCheckVo();
            BeanUtils.copyProperties(menu, menuCheckVo);
            menuCheckVo.setChecked(menuIdList.contains(menu.getId()));
            return menuCheckVo;
        }).collect(Collectors.toList());

        RoleInfoResponse response = new RoleInfoResponse();
        BeanUtils.copyProperties(systemRole, response);
        response.setMenuList(new MenuCheckTree(menuCheckVoList).buildTree());
        return response;
    }

    /**
     * 根据当前登录人判断非超管的权限 一般使用到区分平台和平台的位置
     *
     * @return 当前登录人的所属权限
     */
    @Override
    public Integer getRoleTypeByCurrentAdmin() {
        SystemAdmin currentAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (currentAdmin.getType().equals(RoleEnum.SUPER_MERCHANT.getValue()) ||
                currentAdmin.getType().equals(RoleEnum.MERCHANT_ADMIN.getValue())) {
            return RoleEnum.MERCHANT_ADMIN.getValue();
        }
        if (currentAdmin.getType().equals(RoleEnum.SUPER_ADMIN.getValue()) ||
                currentAdmin.getType().equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
            return RoleEnum.PLATFORM_ADMIN.getValue();
        }
        return null;
    }

    /**
     * @param type 管理员类型：1= 平台超管, 2=商户超管, 3=系统管理员，4=商户管理员
     * @return Integer
     */
    private Integer getRoleTypeByCurrentAdmin(Integer type) {
        if (type.equals(RoleEnum.SUPER_MERCHANT.getValue()) || type.equals(RoleEnum.MERCHANT_ADMIN.getValue())) {
            return RoleEnum.MERCHANT_ADMIN.getValue();
        }
        if (type.equals(RoleEnum.SUPER_ADMIN.getValue()) || type.equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
            return RoleEnum.PLATFORM_ADMIN.getValue();
        }
        return null;
    }

    /**
     * 根据当前登录人获取素材资源归属人 平台的是权限 其他的都是商户id
     *
     * @return 资源归属方
     */
    @Override
    public Integer getOwnerByCurrentAdmin() {
        SystemAdmin currentAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (currentAdmin.getType().equals(RoleEnum.SUPER_MERCHANT.getValue()) ||
                currentAdmin.getType().equals(RoleEnum.MERCHANT_ADMIN.getValue())) {
            return currentAdmin.getMerId();
        }
        if (currentAdmin.getType().equals(RoleEnum.SUPER_ADMIN.getValue()) ||
                currentAdmin.getType().equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
            return -1;
        }
        return -1;
    }

    /**
     * 获取所有角色
     *
     * @param merId 平台-0，商户id
     * @return List
     */
    @Override
    public List<SystemRole> getListByMerId(Integer merId) {
        LambdaQueryWrapper<SystemRole> lqw = Wrappers.lambdaQuery();
        if (merId.equals(0)) {
            lqw.eq(SystemRole::getMerId, merId);
            lqw.ne(SystemRole::getType, RoleEnum.SUPER_ADMIN.getValue());
        } else {
            lqw.eq(SystemRole::getMerId, merId).or().eq(SystemRole::getType, RoleEnum.SUPER_MERCHANT.getValue());
        }
        lqw.orderByAsc(SystemRole::getId);
        return dao.selectList(lqw);
    }

}

