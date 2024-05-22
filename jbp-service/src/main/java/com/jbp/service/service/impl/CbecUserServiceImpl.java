package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.user.CbecUser;
import com.jbp.service.dao.CbecUserDao;
import com.jbp.service.service.CbecUserService;
import org.springframework.stereotype.Service;


@Service
public class CbecUserServiceImpl extends ServiceImpl<CbecUserDao, CbecUser> implements CbecUserService {

}

