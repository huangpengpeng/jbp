package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.admin.SystemAdminRef;
import com.jbp.service.dao.SystemAdminRefDao;
import com.jbp.service.service.SystemAdminRefService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemAdminRefServiceImpl extends ServiceImpl<SystemAdminRefDao, SystemAdminRef> implements SystemAdminRefService {
    @Override
    public List<SystemAdminRef> getList(Integer mId) {
        return list(new QueryWrapper<SystemAdminRef>().lambda().eq(SystemAdminRef::getMId, mId));
    }

}
