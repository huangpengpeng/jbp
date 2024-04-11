//package com.jbp.admin.controller.tank;
//
//import com.Jwebmall.tank.entity.TankStoreRelation;
//import com.Jwebmall.tank.manager.TankStoreRelationMng;
//import com.Jwebmall.tools.RequestParamsUtils;
//import com.common.api.ResponseForT;
//import com.common.jdbc.page.Pagination;
//import com.common.jdbc.template.UnifiedJDBCMng;
//import com.common.web.ResponseUtil;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import springfox.documentation.annotations.ApiIgnore;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.util.Date;
//import java.util.Map;
//
///**
// * 店主管理
// */
//@Controller
//public class TankStoreRelationAct {
//
//    @Resource
//    private UnifiedJDBCMng unifiedJDBCMng;
//
//
//
//    @Resource
//    private TankStoreRelationMng tankStoreRelationMng;
//
//
//    @ApiOperation(value = "店主管理", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    @RequestMapping(value = "/tankStoreRelation/list", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Object list(HttpServletRequest request, ModelMap model, @RequestParam Map<String, Object> params) {
//        Pagination page = unifiedJDBCMng.getPageMap(null, "共享舱店主列表", RequestParamsUtils.fillParams(params),
//                ResponseUtil.getPageNO(params), ResponseUtil.getPageSize(params));
//        return new ResponseUtil<>(ResponseUtil.SUCCESS, ResponseUtil.MESSAGE, page.totalCount, page.getList());
//    }
//
//
//
//    @ApiOperation(value = "增加舱主", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    @RequestMapping(value = "/tankStoreRelation/save", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseForT delete(HttpServletRequest request, ModelMap model, Long userId , Long storeUserId, @ApiIgnore ResponseForT response ) {
//
//
//       TankStoreRelation tankStoreRelation =  tankStoreRelationMng.getStoreUserId(storeUserId);
//       if(tankStoreRelation != null){
//           throw new RuntimeException("店主已经被绑定，增加失败");
//       }
//        tankStoreRelationMng.add(userId,storeUserId,new Date());
//
//        return response.SUCCESS();
//    }
//
//
//
//
//
//
//    @ApiOperation(value = "删除舱主店主", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    @RequestMapping(value = "/tankStoreRelation/delete", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseForT delete(HttpServletRequest request, ModelMap model, Long id ,  @ApiIgnore ResponseForT response ) {
//
//        tankStoreRelationMng.delete(id);
//
//        return response.SUCCESS();
//    }
//
//
//}
