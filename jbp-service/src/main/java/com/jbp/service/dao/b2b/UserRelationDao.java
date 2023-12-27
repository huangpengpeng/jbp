package com.jbp.service.dao.b2b;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.b2b.UserRelation;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserRelationDao extends BaseMapper<UserRelation> {
    @Select(" select t1.*, t2.node " +
            "        from( " +
            "                select @r as uId,  (select @r := pId  from UserRelation where uId = uId) as pId, @I := @I + 1 as level " +
            "        from (select @r := #{uId} ,@I := 0) vars, UserRelation h " +
            "            ) t1 " +
            "            join UserRelation t2 " +
            "        on t1.uId = t2.uId " +
            "        ORDER BY level ")
    List<UserUpperDto> getAllUpper(Integer uId);

    @Select("select u.* from b2b_user_relation u left join b2b_user_relation_flow f on f.uId = u.uId where f.id is null")
    List<UserRelation> getNoFlowList();
}
