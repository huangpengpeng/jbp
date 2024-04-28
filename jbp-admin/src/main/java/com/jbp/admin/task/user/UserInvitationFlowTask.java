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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    public void refreshFlowAndTeam() {
        // cron : 0 0 1 * * ?
        logger.info("---UserInvitationFlowTask refreshFlowAndTeam------produce Data with fixed rate task: Execution Time - {}", DateUtil.date());
        if (StringUtils.isNotEmpty(stringRedisTemplate.opsForValue().get("refreshFlowAndTeam"))){
            logger.info("---UserInvitationFlowTask refreshFlowAndTeam-----未执行完成忽略本次", DateUtil.date());
            return;
        }
        stringRedisTemplate.opsForValue().set("refreshFlowAndTeam","1");
        try {
            List<UserInvitation> noFlowList = userInvitationService.getNoFlowList();
            for (UserInvitation userInvitation : noFlowList) {
                userInvitationFlowService.refreshFlowAndTeam(userInvitation.getUId());
            }

            // 查询所有没团队的
            String sql = " select * from eb_user where id not in(select uid from eb_team_user) ";
            List<Map<String, Object>> maps = SqlRunner.db().selectList(sql);
            for (Map<String, Object> map : maps) {

                UserInvitation userInvitation = userInvitationService.getByUser(MapUtils.getInteger(map, "id"));
                if(userInvitation != null && userInvitation.getPId() != null){
                     TeamUser teamUser = teamUserService.getByUser(userInvitation.getPId());
                     if(teamUser != null){
                         teamUserService.save(MapUtils.getInteger(map, "id"), teamUser.getTid());
                     }
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UserInvitationFlowTask.refreshFlowAndTeam" + " | msg : " + e.getMessage());
        }finally {
            stringRedisTemplate.delete("refreshFlowAndTeam");
        }
    }
}
