package com.jbp.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.White;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.WhiteRequest;
import com.jbp.service.dao.WhiteDao;
import com.jbp.service.service.WhiteService;
import com.jbp.service.service.WhiteUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class WhiteServiceImpl extends ServiceImpl<WhiteDao, White> implements WhiteService {
    @Resource
    WhiteDao whiteDao;
    @Resource
    WhiteUserService whiteUserService;


    @Override
    public PageInfo<White> pageList(WhiteRequest request, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<White> lambdaQueryWrapper = new LambdaQueryWrapper<White>()
                .in(!Objects.isNull(request.getName()) && request.getName() != "", White::getName, request.getName());
        Page<White> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<White> whites = whiteDao.selectList(lambdaQueryWrapper);
        return CommonPage.copyPageInfo(page, whites);
    }

    @Override
    public Boolean addWhiten(WhiteRequest white) {
        LambdaQueryWrapper<White> whiteLambda = new LambdaQueryWrapper<White>()
                .eq(!Objects.isNull(white.getName()), White::getName, white.getName());
        White one = whiteDao.selectOne(whiteLambda);
        if (one == null) {
            one = new White();
            one.setName(white.getName());
            one.setGmtCreated(new Date());
            one.setGmtModify(new Date());
            whiteDao.insert(one);
            return true;
        }
        return false;

    }

    @Override
    public void delete(Long id) {
        // 删除关联用户
        whiteUserService.deleteByWhite(id);
        // 删除白名单
        whiteDao.deleteById(id);
    }

    @Override
    public White getByName(String whiteName) {
        LambdaQueryWrapper<White> wrapper = new LambdaQueryWrapper<White>();
        wrapper.eq(White::getName, whiteName);
        return getOne(wrapper);
    }

    @Override
    public List<White> getByNameList(String name) {
        LambdaQueryWrapper<White> wrapper = new LambdaQueryWrapper<White>();
        wrapper.like(!ObjectUtil.isNull(name)&&!name.equals(""), White::getName, name);
        return list(wrapper);
    }
}
