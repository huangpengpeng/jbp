package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.express.UserWhiteExpress;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.model.user.White;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.UserWhiteRequest;
import com.jbp.service.dao.UserDao;
import com.jbp.service.dao.UserWhiteDao;
import com.jbp.service.dao.WhiteDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.UserWhiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class UserWhiteServiceImpl extends ServiceImpl<UserWhiteDao, WhiteUser> implements UserWhiteService {
   @Resource
   private UserWhiteDao userWhiteDao;
    @Resource
    private UserService userService;
    @Autowired
    WhiteDao whiteDao;
    @Override
    public PageInfo<WhiteUser> pageList(UserWhiteRequest request, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<WhiteUser> lambdaQueryWrapper=new LambdaQueryWrapper<WhiteUser>()
                .eq(request.getUserId()!=null&&request.getUserId()!=0, WhiteUser::getUserId,request.getUserId())
                .eq(!Objects.isNull(request.getWhiteId()), WhiteUser::getWhiteId,request.getWhiteId());
        Page<WhiteUser> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<WhiteUser> whites = userWhiteDao.selectList(lambdaQueryWrapper);
        whites.forEach(e->{
           e.setAccountNo(userDao.selectById(e.getUserId()).getAccount());
           e.setWhiteName(whiteDao.selectById(e.getWhiteId()).getName());
        });
        return CommonPage.copyPageInfo(page, whites);
    }
    @Override
    public WhiteUser add(UserWhiteRequest userWhiteRequest) {
        WhiteUser userWhite = new WhiteUser();
        userWhite.setWhiteId(Long.valueOf(userWhiteRequest.getWhiteId()));
        userWhite.setUserId(Long.valueOf(userWhiteRequest.getUserId()));
        userWhite.setGmtCreated(new Date());
        userWhiteDao.insert(userWhite);
        userWhite.setWhiteName(whiteDao.selectById(userWhite.getWhiteId()).getName());
        userWhite.setAccountNo(userDao.selectById(userWhite.getUserId()).getAccount());
        return userWhite;
    }

    @Override
    public List<WhiteUser> batchSave(List<UserWhiteExpress> userWhiteExpresses) {

        List<WhiteUser> userWhiteList=new ArrayList<>();
        for (int i = 0; i < userWhiteExpresses.size(); i++) {
            WhiteUser userWhite = new WhiteUser();
            try {
                userWhite.setUserId(Long.valueOf(userDao.selectOne(new LambdaQueryWrapper<User>().eq(userWhiteExpresses.get(i).getAccountNo() != null &&
                        userWhiteExpresses.get(i).getAccountNo() != "", User::getAccount, userWhiteExpresses.get(i).getAccountNo()).last("LIMIT 1")).getId()));

            }catch (NullPointerException e){
                throw new RuntimeException("白名单:"+userWhiteExpresses.get(i).getWhiteName()+"不存在");
            }
            try {
                userWhite.setWhiteId(Long.valueOf(whiteDao.selectOne(new LambdaQueryWrapper<White>()
                        .eq(userWhiteExpresses.get(i).getWhiteName() != null && userWhiteExpresses.get(i).getWhiteName() != "", White::getName, userWhiteExpresses.get(i).getWhiteName()).last("LIMIT 1")).getId()));
            }catch (NullPointerException e){
                throw new RuntimeException("账号:"+userWhiteExpresses.get(i).getAccountNo()+"不存在");
            }
            userWhite.setGmtCreated(new Date());
            userWhiteList.add(userWhite);
        }
        saveBatch(userWhiteList);
        userWhiteList.forEach(e->{
            e.setWhiteName(whiteDao.selectById(e.getWhiteId()).getName());
            e.setAccountNo(userDao.selectById(e.getUserId()).getAccount());
        });
        return userWhiteList;

    }


}
