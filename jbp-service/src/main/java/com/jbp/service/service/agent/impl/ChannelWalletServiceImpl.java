package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.ChannelWallet;
import com.jbp.service.dao.agent.ChannelWalletDao;
import com.jbp.service.service.agent.ChannelWalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ChannelWalletServiceImpl extends ServiceImpl<ChannelWalletDao, ChannelWallet> implements ChannelWalletService {
}
