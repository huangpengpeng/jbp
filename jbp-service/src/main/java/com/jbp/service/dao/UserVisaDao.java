package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.UserVisa;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.UserVisaRecordResponse;
import com.jbp.common.response.UserVisaResponse;
import org.apache.ibatis.annotations.Update;

import java.util.List;


public interface UserVisaDao extends BaseMapper<UserVisa> {

    @Update("update ${platform}.eb_user_visa set visa=true where id = ${id} ")
     void updateVisa(Integer id, String platform);


    UserVisaResponse getVisaTask(String signTaskId);


    List<UserVisaRecordResponse> getAdminPageList(String account);

}
