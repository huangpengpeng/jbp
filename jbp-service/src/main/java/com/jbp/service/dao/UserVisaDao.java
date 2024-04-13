package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.user.UserVisa;
import com.jbp.common.response.UserVisaResponse;
import org.apache.ibatis.annotations.Update;


public interface UserVisaDao extends BaseMapper<UserVisa> {

    @Update("update #{platform,jdbcType=VARCHAR}.eb_user_visa set visa=true where id = #{id,jdbcType=VARCHAR} ")
     void updateVisa(Integer id, String platform);


    UserVisaResponse getVisaTask(String signTaskId);
}
