package com.jbp.admin.task.user;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.TeamUserService;
import com.jbp.service.service.agent.UserInvitationFlowService;
import com.jbp.service.service.agent.UserInvitationService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 刷新用户销售层级关系和等级
 */
@Component("UserInvitationFlowTask")
public class UserInvitationFlowTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(UserInvitationFlowTask.class);

    @Autowired
    private TeamUserService teamUserService;
    @Autowired
    private UserInvitationService userInvitationService;
    @Autowired
    private UserInvitationFlowService userInvitationFlowService;
    @Autowired
    private RedisTemplate redisTemplate;


    public void refreshFlowAndTeam() {
        // cron : 0 0 1 * * ?
        logger.info("---UserInvitationFlowTask refresh------produce Data with fixed rate task: Execution Time - {}", DateUtil.date());
        // 1.加锁成功
        Boolean task = redisTemplate.opsForValue().setIfAbsent("UserInvitationFlowTask.refresh", 1);
        if(!task){
            //没有争抢(设置)到锁
            logger.info("上一次任务未执行完成退出");
            return;//方法结束
        }
        redisTemplate.expire("UserInvitationFlowTask.refresh",3, TimeUnit.MINUTES);
        try {
            List<UserInvitation> noFlowList = userInvitationService.getNoFlowList();
            for (UserInvitation userInvitation : noFlowList) {
                userInvitationFlowService.refreshFlowAndTeam(userInvitation.getUId());
            }

            // 查询所有没团队的
//            String sql = " select * from eb_user where id not in(select uid from eb_team_user) ";
//            List<Map<String, Object>> maps = SqlRunner.db().selectList(sql);
//            for (Map<String, Object> map : maps) {
//
//                UserInvitation userInvitation = userInvitationService.getByUser(MapUtils.getInteger(map, "id"));
//                if(userInvitation != null && userInvitation.getPId() != null){
//                     TeamUser teamUser = teamUserService.getByUser(userInvitation.getPId());
//                     if(teamUser != null){
//                         teamUserService.save(MapUtils.getInteger(map, "id"), teamUser.getTid());
//                     }
//                }
//            }

            redisTemplate.delete("UserInvitationFlowTask.refresh");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UserInvitationFlowTask.refreshFlowAndTeam" + " | msg : " + e.getMessage());
        }finally {
            redisTemplate.delete("UserInvitationFlowTask.refresh");
        }
    }
}
