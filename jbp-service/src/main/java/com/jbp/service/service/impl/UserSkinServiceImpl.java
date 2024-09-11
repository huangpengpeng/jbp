package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.excel.UserSkinExcel;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.user.UserSkin;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.FileResultVo;
import com.jbp.service.dao.UserSkinDao;
import com.jbp.service.service.UploadService;
import com.jbp.service.service.UserSkinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class UserSkinServiceImpl extends ServiceImpl<UserSkinDao, UserSkin> implements UserSkinService {

    @Autowired
    private UserSkinDao dao;
    @Autowired
    private UploadService uploadService;

    @Override
    public UserSkin getByNo(String number,Integer uid) {
        return getOne(new QueryWrapper<UserSkin>().lambda().eq(UserSkin::getRecordListNo, number).eq(UserSkin::getUid,uid));
    }

    @Override
    public PageInfo<UserSkin> getList(Integer uid, String nickname, String phone, Date startCreateTime, Date endCreateTime, PageParamRequest pageParamRequest) {
        Page<UserSkin> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserSkin> list = dao.getList(uid, nickname, phone, startCreateTime, endCreateTime);
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public String export(Integer uid, String nickname, String phone, Date startCreateTime, Date endCreateTime) {
        List<UserSkin> list = dao.getList(uid, nickname, phone, startCreateTime, endCreateTime);
        if (CollUtil.isEmpty(list)) {
            throw new CrmebException("未查询到皮肤检测管理数据！");
        }
        List<UserSkinExcel> result = new LinkedList<>();
        list.forEach(e -> {
            UserSkinExcel vo = new UserSkinExcel();
            BeanUtils.copyProperties(e, vo);
            result.add(vo);
        });
        FileResultVo fileResultVo = uploadService.excelLocalUpload(result, UserSkinExcel.class);
        log.info("导出皮肤检测管理下载地址:" + fileResultVo.getUrl());
        return fileResultVo.getUrl();
    }
}
