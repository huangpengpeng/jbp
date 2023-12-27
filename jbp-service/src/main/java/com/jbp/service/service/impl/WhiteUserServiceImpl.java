package com.jbp.service.service.impl;

import com.alipay.service.schema.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.express.UserWhiteExpress;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.White;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.WhiteUserRequest;
import com.jbp.service.dao.WhiteUserDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WhiteService;
import com.jbp.service.service.WhiteUserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
@Service
public class WhiteUserServiceImpl extends ServiceImpl<WhiteUserDao, WhiteUser> implements WhiteUserService {
    @Resource
    private WhiteUserDao whiteUserDao;
    @Resource
    private UserService userService;
    @Resource
    WhiteService whiteService;

    @Override
    public PageInfo<WhiteUser> pageList(WhiteUserRequest request, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<WhiteUser> lambdaQueryWrapper = new LambdaQueryWrapper<WhiteUser>()
                .eq(!Objects.isNull(request.getUserId()), WhiteUser::getUid, request.getUserId())
                .eq(!Objects.isNull(request.getWhiteId()), WhiteUser::getWhiteId, request.getWhiteId());
        Page<WhiteUser> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<WhiteUser> whites = whiteUserDao.selectList(lambdaQueryWrapper);
        whites.forEach(e -> {
            e.setAccount(userService.getById(e.getUid()).getAccount());
            e.setWhiteName(whiteService.getById(e.getWhiteId()).getName());
        });
        return CommonPage.copyPageInfo(page, whites);
    }

    @Override
    public Boolean add(WhiteUserRequest userWhiteRequest) {
        WhiteUser userWhite = new WhiteUser();
        userWhite.setWhiteId(Long.valueOf(userWhiteRequest.getWhiteId()));
        userWhite.setUid(userWhiteRequest.getUserId());
        userWhite.setGmtCreated(new Date());
        userWhite.setGmtModify(new Date());
        whiteUserDao.insert(userWhite);
        userWhite.setWhiteName(whiteService.getById(userWhite.getWhiteId()).getName());
        userWhite.setAccount(userService.getById(userWhite.getUid()).getAccount());
        return true;
    }

    @Override
    public Boolean batchSave(List<UserWhiteExpress> userWhiteExpresses) {

        List<WhiteUser> userWhiteList = new ArrayList<>();
        for (int i = 0; i < userWhiteExpresses.size(); i++) {
            WhiteUser userWhite = new WhiteUser();
            try {
                LambdaQueryWrapper<User> userLambdaQueryWrapper=new LambdaQueryWrapper<User>()
                        .eq(!Objects.isNull(userWhiteExpresses.get(i).getAccount()),User::getAccount,userWhiteExpresses.get(i).getAccount());
                userWhite.setUid(userService.getOne(userLambdaQueryWrapper).getId());

            } catch (NullPointerException e) {
                throw new RuntimeException("白名单:" + userWhiteExpresses.get(i).getWhiteName() + "不存在");
            }
            try {
                LambdaQueryWrapper<White> whiteUserLambdaQueryWrapper=new LambdaQueryWrapper<White>()
                        .eq(!Objects.isNull(userWhiteExpresses.get(i).getWhiteName()),White::getName,userWhiteExpresses.get(i).getWhiteName());
                userWhite.setWhiteId(Long.valueOf(whiteService.getOne(whiteUserLambdaQueryWrapper).getId()));
            } catch (NullPointerException e) {
                throw new RuntimeException("账号:" + userWhiteExpresses.get(i).getAccount() + "不存在");
            }
            userWhite.setGmtCreated(new Date());
            userWhite.setGmtModify(new Date());
            userWhiteList.add(userWhite);
        }
        /*批量添加*/
        saveBatch(userWhiteList);

        return true;

    }


}
