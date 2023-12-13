package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.community.CommunityReplyLike;
import com.jbp.service.dao.community.CommunityReplyLikeDao;
import com.jbp.service.service.CommunityReplyLikeService;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* CommunityReplyLike 接口实现
* +----------------------------------------------------------------------
* | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
* +----------------------------------------------------------------------
* | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
* +----------------------------------------------------------------------
* | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
* +----------------------------------------------------------------------
* | Author: CRMEB Team <admin@crmeb.com>
* +----------------------------------------------------------------------
*/
@Service
public class CommunityReplyLikeServiceImpl extends ServiceImpl<CommunityReplyLikeDao, CommunityReplyLike> implements CommunityReplyLikeService {

    @Resource
    private CommunityReplyLikeDao dao;

    /**
     * 获取点赞列表，通过笔记ID和用户ID
     * @param noteId 笔记ID
     * @param userId 用户ID
     */
    @Override
    public List<CommunityReplyLike> findListByNoteIdAndUid(Integer noteId, Integer userId) {
        LambdaQueryWrapper<CommunityReplyLike> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommunityReplyLike::getNoteId, noteId);
        lqw.eq(CommunityReplyLike::getUid, userId);
        return dao.selectList(lqw);
    }

    /**
     * 获取点赞详情
     * @param replyId 评论ID
     * @param userId 用户ID
     */
    @Override
    public CommunityReplyLike getDetail(Integer replyId, Integer userId) {
        LambdaQueryWrapper<CommunityReplyLike> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommunityReplyLike::getReplyId, replyId);
        lqw.eq(CommunityReplyLike::getUid, userId);
        lqw.last("limit 1");
        return getOne(lqw);
    }
}

