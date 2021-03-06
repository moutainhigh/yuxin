package com.yuchengtech.bcrm.customer.potentialMkt.action;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;

import com.opensymphony.xwork2.ActionContext;
import com.yuchengtech.bcrm.customer.potentialMkt.model.OcrmFCiMktProspectC;
import com.yuchengtech.bcrm.customer.potentialMkt.service.OcrmFCiMktProspectCService;
import com.yuchengtech.bob.common.CommonAction;
import com.yuchengtech.bob.common.DataType;
import com.yuchengtech.bob.core.QueryHelper;
import com.yuchengtech.bob.vo.AuthUser;
/**
 * 企商金客户营销流程 -  prospect处理  luyy  2014-07-22
 * @modify dongyi 2014-12-01
 */

@SuppressWarnings("serial")
@Action("/mktprospectC")
public class MktProspectCAction  extends CommonAction{
	@Autowired
	@Qualifier("dsOracle")
	private DataSource ds; //声明数据源
	private HttpServletRequest request;
	
    @Autowired
    private OcrmFCiMktProspectCService service ;
    
    AuthUser auth = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    @Autowired
	public void init(){
        	model = new OcrmFCiMktProspectC();  
        	setCommonService(service);
		//新增修改删除记录是否记录日志,默认为false，不记录日志
		needLog=true;
	}
    /**
	 * 设置查询SQL并为父类相关属性赋值
	 */
	public void prepare() {
		ActionContext ctx = ActionContext.getContext();
    	request = (HttpServletRequest)ctx.get(ServletActionContext.HTTP_REQUEST);
    	String sqlapp = " select c.*,a.USER_NAME," +
    			"(trunc(sysdate, 'DD') - c.record_date) TREAMENT_DAYS " +
    			"from OCRM_F_CI_MKT_PROSPECT_C c " +
			    			" left join admin_auth_account a on c.user_id = a.account_name " +
			    				" where 1=1 ";	
        
    	String id = request.getParameter("id");
    	StringBuilder sb  = new StringBuilder(sqlapp);
    	if( id != null && !"".equals(id)){//流程查询使用
    		sb.append(" and c.id = '"+id+"'");
//    		addOracleLookup("CUST_SOURCE", "CUST_SOURCE");
//    		addOracleLookup("VISIT_WAY", "VISIT_TYPE");
//    		addOracleLookup("IF_TRANS_CUST", "IF_FLAG");
//    		addOracleLookup("IF_FILES", "IF_FLAG");
//    		addOracleLookup("IF_PIPELINE", "IF_FLAG");
    	}else{
    		//sb.append("and user_id='"+auth.getUserId()+"' ");
    		sb.append("and (IF_PIPELINE='0' or IF_PIPELINE='2') ");
    		setPrimaryKey("c.ID desc ");
    	}
        	SQL=sb.toString();
        	datasource = ds;
        	setPrimaryKey("c.IF_PIPELINE desc, c.RECORD_DATE asc");
        	configCondition("CUST_ID","=","CUST_ID",DataType.String);
    		configCondition("CUST_NAME","like","CUST_NAME",DataType.String);
    		configCondition("CALL_DATE","=","CALL_DATE",DataType.Date);
    		configCondition("AREA_NAME","like","AREA_NAME",DataType.String);
    		configCondition("DEPT_NAME","=","DEPT_NAME",DataType.String);
    		configCondition("CUST_SOURCE","=","CUST_SOURCE",DataType.String);
	}

    
    
    public DefaultHttpHeaders save() throws Exception{
    	ActionContext ctx = ActionContext.getContext();
    	request = (HttpServletRequest)ctx.get(ServletActionContext.HTTP_REQUEST);
    	((OcrmFCiMktProspectC)model).setUserId(auth.getUserId());
    	((OcrmFCiMktProspectC)model).setCheckStat("2");
    	if(((OcrmFCiMktProspectC)model).getId()==null){//新增
    		((OcrmFCiMktProspectC)model).setRecordDate(new Date());//设置记录日期
    		((OcrmFCiMktProspectC)model).setPipelineId((new Date()).getTime());//设置PipelineId必须保证其唯一性
    		service.save(model);
    	}else{//修改
    		((OcrmFCiMktProspectC)model).setUpdateDate(new Date());
    		service.save(model);
    	}
    	 Map<String,Object> map=new HashMap<String,Object>();
    	//根据字段IF_PIPELINE判断是否转入PIPELINE
    	String flag=((OcrmFCiMktProspectC)model).getIfPipeline();
    	if("1".equals(flag)){
    		map.put("prospectId", ((OcrmFCiMktProspectC)model).getId());
    		map.put("pipelineId", ((OcrmFCiMktProspectC)model).getPipelineId());
    		map.put("custId", ((OcrmFCiMktProspectC)model).getCustId());
    		map.put("custName", ((OcrmFCiMktProspectC)model).getCustName());
    		map.put("areaName", ((OcrmFCiMktProspectC)model).getAreaName());
    		map.put("areaId", ((OcrmFCiMktProspectC)model).getAreaId());
    		map.put("deptId", ((OcrmFCiMktProspectC)model).getDeptId());
    		map.put("deptName", ((OcrmFCiMktProspectC)model).getDeptName());
    		map.put("rm", ((OcrmFCiMktProspectC)model).getRm());
    		map.put("rmId", ((OcrmFCiMktProspectC)model).getRmId());
    		this.setJson(map);
    		//save to PIPELINE
    	}
    	
    	
//    	String name = ((OcrmFCiMktProspectC)model).getCustName();
//    	
//    	String instanceid = "PMKT_"+id+"_21";//最后的字符串含义  第一位：1 个金  2商金    第二位：表示营销的步骤
//		String jobName = "PROSPECT复核_"+name;//自定义流程名称
//    	service.initWorkflowByWfidAndInstanceid("42", jobName, null, instanceid);//调用CommonService中的该方法发起工作流，第三个参数可以自定义一些变量，用于路由器条件等
		 
//			map.put("instanceid", instanceid);
//		    map.put("currNode", "42_a3");
//		    map.put("nextNode",  "42_a4");
		    
    	
    	
    	return new DefaultHttpHeaders("success");
    }
    //区域中心码值查询
    public  void searchArea(){
    	try {
			StringBuilder sb = new StringBuilder("");
				sb.append("SELECT UNIT.ORG_NAME as VALUE,UNIT.UNITID as KEY FROM SYS_UNITS UNIT WHERE UNITSEQ LIKE '500%'  and levelunit='2'  order by UNIT.levelunit,UNIT.id ");
			this.json = new QueryHelper(sb.toString(), ds.getConnection()).getJSON();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
  //客户预评级码值查询
    public  void searchYpj(){
    	try {
			StringBuilder sb = new StringBuilder("");
				sb.append("SELECT T.F_VALUE as VALUE,T.F_CODE as KEY FROM OCRM_SYS_LOOKUP_ITEM T WHERE T.F_LOOKUP_ID = 'GRADE_PERSECT_YU' ORDER BY T.F_CODE ASC");
			this.json = new QueryHelper(sb.toString(), ds.getConnection()).getJSON();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    //通过营业部门查询区域中心并返现在新增面板
    public DefaultHttpHeaders searchAreaBack(){
    	ActionContext ctx = ActionContext.getContext();
    	request = (HttpServletRequest)ctx.get(ServletActionContext.HTTP_REQUEST);
    	String unitId = request.getParameter("deptId");
    	try {
			StringBuilder sb = new StringBuilder("");
				sb.append("select t.org_name as area_name ," +
						" t.unitid as area_id " +
						" from  SYS_UNITS  t " +
						"where 1=1 " +
						"and ( t.unitid =(select  unit.superunitid FROM SYS_UNITS UNIT WHERE UNITSEQ LIKE '500%'and unitid ='"+unitId+"' )) ");
			this.json = new QueryHelper(sb.toString(), ds.getConnection()).getJSON();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	  return new DefaultHttpHeaders("success");
    }
   
    
    //查询电访相关信息
    public void getInfo() throws IOException{
    	ActionContext ctx = ActionContext.getContext();
    	request = (HttpServletRequest)ctx.get(ServletActionContext.HTTP_REQUEST);
    	String callId = request.getParameter("callId");
    	String info = "";
    	List<Object[]> list1 = service.getBaseDAO().findByNativeSQLWithIndexParam(" " +
    			"select a.CUST_ID,a.CUST_NAME,c.CUST_SOURCE,a.LINK_MAN,a.LINK_PHONE,o.institution_code from OCRM_F_CI_MKT_CALL_C a " +
    			"left join OCRM_F_CI_BELONG_ORG o on a.cust_id = o.cust_id and o.MAIN_TYPE='1',OCRM_F_CI_MKT_VISIT_C c  where a.id=c.call_id and a.id='"+callId+"'");
    	if (list1 != null && list1.size() > 0) {
			Object[] o = list1.get(0);
			info = o[0]+"#"+o[1]+"#"+o[2]+"#"+o[3]+"#"+o[4] ;
			String org = o[5]==null?"":o[5].toString();
			if(!"".equals(org)){//查询区域中心信息
				List<Object[]> list2 = service.getBaseDAO().findByNativeSQLWithIndexParam(" " +
		    			"select  org_id,org_name from admin_auth_org where (org_id = '"+org+"' and org_level='2') " +
		    			"or org_id = (select up_org_id from admin_auth_org where org_id = '"+org+"' and org_level='3')");
				if (list2 != null && list2.size() > 0) {
					Object[] oo = list2.get(0);
					info += "#"+oo[0]+"#"+oo[1];
				}else{
					info += "#null#null";
				} 
			}else{
				info += "#null#null";
			}
			
    	}
    	HttpServletResponse response =(HttpServletResponse) ctx.get(ServletActionContext.HTTP_RESPONSE);
    	response.setCharacterEncoding("UTF-8");
		response.getWriter().write(info);
		response.getWriter().flush();
    	
    }
    public void batchDel(){
    	ActionContext ctx = ActionContext.getContext();
    	request = (HttpServletRequest)ctx.get(ServletActionContext.HTTP_REQUEST);
    	String id = request.getParameter("id");
    	//	service.batchUpdateByName(" delete from OcrmFCiMktProspectC g where g.id='"+Long.parseLong(id)+"'", new HashMap());
    	List list=service.getBaseDAO().findByNativeSQLWithNameParam("select pipeline_id from OCRM_F_CI_MKT_PROSPECT_C WHERE ID='"+Long.parseLong(id)+"'", null);
		if(list!=null){
	    	for(int i=0;i<list.size();i++){
				String pId=list.get(i)+"";
				service.batchUpdateByName("UPDATE OcrmFCiMktProspectC  SET ifPipeline='99' WHERE pipelineId='"+Long.parseLong(pId)+"'", new HashMap());
				service.batchUpdateByName("UPDATE OcrmFCiMktIntentC SET ifSecondStep='99' WHERE pipelineId='"+Long.parseLong(pId)+"'", new HashMap());
				service.batchUpdateByName("UPDATE OcrmFCiMktCaC   SET ifThirdStep='99'  WHERE pipelineId='"+Long.parseLong(pId)+"'", new HashMap());
				service.batchUpdateByName("UPDATE OcrmFCiMktCheckC  SET ifFourthStep='99' WHERE pipelineId='"+Long.parseLong(pId)+"'", new HashMap());
				service.batchUpdateByName("UPDATE OcrmFCiMktApprovlC  SET ifFifthStep='99' WHERE pipelineId='"+Long.parseLong(pId)+"'", new HashMap());
				service.batchUpdateByName("UPDATE OcrmFCiMktApprovedC  SET lastSendStep='99' WHERE pipelineId='"+Long.parseLong(pId)+"'", new HashMap());
			}
		}
    }
}