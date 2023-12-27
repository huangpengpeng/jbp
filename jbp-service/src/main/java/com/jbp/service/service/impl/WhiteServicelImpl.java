package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.merchant.MerchantApply;
import com.jbp.common.model.user.UserWhite;
import com.jbp.common.model.user.White;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.UserWhiteRequest;
import com.jbp.common.request.WhiteRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.dao.UserWhiteDao;
import com.jbp.service.dao.WhiteDao;
import com.jbp.service.service.WhiteServicel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataUnit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class WhiteServicelImpl extends ServiceImpl<WhiteDao, White> implements WhiteServicel {
    @Autowired
    WhiteDao whiteDao;
    @Autowired
    UserWhiteDao userWhiteDao;

    @Override
    public  PageInfo<White>  pageList(WhiteRequest request, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<White> lambdaQueryWrapper=new LambdaQueryWrapper<White>()
                .in(request.getName()!=null&&request.getName()!="",White::getName,request.getName());
        Page<White> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<White> whites = whiteDao.selectList(lambdaQueryWrapper);
       return CommonPage.copyPageInfo(page, whites);
    }

    @Override
    public Boolean addWhiten(WhiteRequest white) {
        LambdaQueryWrapper<White> whiteLambda=new LambdaQueryWrapper<White>()
                .eq(white.getName()!=null,White::getName,white.getName());
        White one = whiteDao.selectOne(whiteLambda);
       if (one==null){
           one=new White();
           one.setName(white.getName());
           one.setGmtCreated(new Date());
           whiteDao.insert(one);
           return true;
       }
       return false;

    }

    @Override
    public Boolean cascadingDelte(Long id) {
        LambdaQueryWrapper<UserWhite> userWhiteQeryWrappper=new LambdaQueryWrapper<UserWhite>()
                .eq(id!=0&&id!=null,UserWhite::getWhiteId,id);
        List<UserWhite> userWhites = userWhiteDao.selectList(userWhiteQeryWrappper);
        List<Long> userWhiteid = new ArrayList<>();
        userWhites.forEach(e->{userWhiteid.add(e.getId());});
        userWhiteDao.deleteBatchIds(userWhiteid);
        whiteDao.deleteById(id);
        return true;

    }
}
