package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.UserRegion;
import org.apache.ibatis.annotations.Param;
import com.github.pagehelper.Page;

import java.util.Map;

public interface UserRegionMapper extends BaseMapper<UserRegion> {
    PageInfo<UserRegion> adminPage(Page<UserRegion> page, @Param("map") Map map);
}
