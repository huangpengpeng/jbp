package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.model.user.White;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.WhiteRequest;
import com.jbp.service.dao.WhiteDao;
import com.jbp.service.service.WhiteService;
import com.jbp.service.service.WhiteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class WhiteServicelImpl extends ServiceImpl<WhiteDao, White> implements WhiteService {
    @Resource
    WhiteDao whiteDao;
    @Resource
    WhiteUserService whiteUserService;

    @Override
    public  PageInfo<White>  pageList(WhiteRequest request, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<White> lambdaQueryWrapper=new LambdaQueryWrapper<White>()
                .in(!Objects.isNull(request.getName())&&request.getName()!="",White::getName,request.getName());
        Page<White> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<White> whites = whiteDao.selectList(lambdaQueryWrapper);
       return CommonPage.copyPageInfo(page, whites);
    }

    @Override
    public Boolean addWhiten(WhiteRequest white) {
        LambdaQueryWrapper<White> whiteLambda=new LambdaQueryWrapper<White>()
                .eq(!Objects.isNull(white.getName()),White::getName,white.getName());
        White one = whiteDao.selectOne(whiteLambda);
       if (one==null){
           one=new White();
           one.setName(white.getName());
           one.setGmtCreated(new Date());
           one.setGmtModify(new Date());
           whiteDao.insert(one);
           return true;
       }
       return false;

    }

    @Override
    public Boolean cascadingDelte(Long id) {
        whiteUserService.remove(new LambdaQueryWrapper<WhiteUser>().eq(!Objects.isNull(id),WhiteUser::getWhiteId,id));
        whiteDao.deleteById(id);
        return true;

    }
}
