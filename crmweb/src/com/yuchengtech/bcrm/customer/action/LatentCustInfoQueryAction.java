package com.yuchengtech.bcrm.customer.action;


import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;

import com.opensymphony.xwork2.ActionContext;
import com.yuchengtech.bob.common.CommonAction;
import com.yuchengtech.bob.common.DataType;
import com.yuchengtech.bob.vo.AuthUser;
/**
 * 
 * 潜在客户查询
 * @author hujun
 * @since 2014-7-16
 * 
 */
@SuppressWarnings("serial")
@ParentPackage("json-default")
@Action(value = "/latentCustInfoQueryAction", results = { @Result(name = "success", type = "json")})
public class LatentCustInfoQueryAction extends CommonAction {
	
	//数据源
	@Autowired
	@Qualifier("dsOracle")
	private DataSource ds;
   
	/**
	 *模块功能查询
	 */
	public void prepare() {
		AuthUser auth=(AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ActionContext ctx = ActionContext.getContext();
    	request = (HttpServletRequest)ctx.get(ServletActionContext.HTTP_REQUEST);   
    	String MAIN_BR_ID=request.getParameter("MAIN_BR_ID")==null?"":request.getParameter("MAIN_BR_ID");
		/*StringBuilder sb = new StringBuilder(
				"  select D.CUS_ID        CUST_ID," + 
				"         d.cus_name      CUST_NAME," + 
				"         d.CERT_TYPE     IDENT_TYPE," + 
				"         d.cert_code     IDENT_NO," + 
				"         d.cust_stat     CUST_STAT," + 
				"         d.CUST_TYPE," + 
				"         d.CUS_RESOURCE," + 
				"         d.short_name," + 
				"         d.ATTEN_NAME    LINKMAN_NAME," + 
				"         d.job_type," + 
				"         d.indust_type," + 
				"         d.CALL_NO," + 
				"         d.zipcode," + 
				"         d.contmeth_info," + 
				"         d.CUS_ADDR  ADDR,d.CUST_MGR,d.MAIN_BR_ID,d.STATE,d.CONCLUSION,d.MKT_ACTIVITIE,d.SOURCE_CHANNEL,d.CUS_NATIONALITY,d.REFEREES_ID" + 
				"    from ACRM_F_CI_POT_CUS_COM D where 1=1  " + 	
				"    and d.cust_type='2' ");*/
    	StringBuilder sb = new StringBuilder();
    	sb.append("select d.CUS_ID,"
    			+ " d.CUS_NAME,"
    			+ " d.CERT_TYPE,"
    			+ " d.CERT_CODE, "
    			+ " d.cust_stat, "
    			+ " d.CUST_TYPE,"
    			+ " d.ATTEN_NAME,"
    			+ " d.job_type,"
    			+ " d.indust_type,"
    			+ " d.CALL_NO,"
    			+ " d.zipcode,"
    			+ " d.contmeth_info,"
    			+ " d.CUS_ADDR,"
    			+ " d.CUST_MGR,"
    			+ " c.cust_name REFEREES_NAME,"
    			+ " ac.user_name CUST_MGR_NAME, "
    			+ " ao.org_name MAIN_BR_NAME,"
    			+ " d.MAIN_BR_ID,"
    			+ " d.STATE,d.DELETE_CUST_STATE,"
    			+ " d.MKT_ACTIVITIE,"
    			+ " FUN_TRANS_CODES(d.mkt_activitie) MKT_ACTIVITIE_V,"
    			+ " d.SOURCE_CHANNEL,"
    			+ " d.CUS_NATIONALITY,"
    			+ " d.REFEREES_ID,"
    			+ " a.USER_NAME MOVER_USER,"
    			+ " d.MOVER_DATE,"
    			+ " d.BACK_STATE, "
    			+ " d.MAIN_BR_ID TREE_STORE,"
    			+ " to_char(to_date(d.INPUT_DATE,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd') INPUT_DATE,"
    			+ " d.ACTUAL_RECEIVE_DATE,"
    			+ " d.DEFAULT_RECEIVE_DATE,"
    			+ " d.RECORD_SESSION,"
    			+ " c3.concust  SUMCOUNTS "
    			+ " ,c2.visit_date LASTVISITDATE,"
    			+ "d.CUS_EMAIL,"
    			+ "d.CUS_WECHATID, ");
    	sb.append(" case when (select count(c.cust_id)  from acrm_f_ci_customer c where c.cust_id in (select cust_id from ACRM_F_CI_CONTMETH co where co.contmeth_info=replace( d.CALL_NO,substr(d.CALL_NO,0,instr(d.CALL_NO,'-',1,2))))  and c.cust_name=d.cus_name group by c.cust_id)>=1 then 2 else 1 end FORMAL_CUST_FLAG ");
    	sb.append("  from ACRM_F_CI_POT_CUS_COM d ");
    	sb.append("  left join ACRM_F_CI_CUSTOMER c  on d.referees_id=c.cust_id ");
    	sb.append("  left join ADMIN_AUTH_ACCOUNT ac on d.CUST_MGR=ac.account_name ");
    	sb.append("  left join ADMIN_AUTH_ACCOUNT a on d.MOVER_USER=a.account_name ");
    	sb.append("  left join ADMIN_AUTH_ACCOUNT M on d.CUST_MGR=M.account_name");
    	sb.append("  left join ADMIN_AUTH_ORG ao on d.main_br_id=ao.org_id ");
    	sb.append("  left join (select count(cl.cust_id) as concust,cl.cust_id from OCRM_F_SE_CALLREPORT cl  group by cl.cust_id) c3 on c3.cust_id=d.cus_id ");
    	sb.append("  left join (select max(cl.visit_date) as visit_date ,cl.cust_id from OCRM_F_SE_CALLREPORT cl group by cl.cust_id )  c2 on c2.cust_id=d.cus_id ");
    	sb.append("  where 1=1 and d.cust_type='2' and d.STATE='0' and d.formal_cust_flag='1' ");
        for (String key : getJson().keySet()){
            String value = getJson().get(key).toString();
            if (! "".equals(value)) {
                if("CUS_ID".equals(key))
                    sb.append(" and d." + key + " like " + "'%" + value + "%'");
                else if("CUS_NAME".equals(key))
                    sb.append(" and d." + key + " like " + "'%" + value +"%'");
                else if("CERT_TYPE".equals(key))
                    sb.append(" and d.CERT_TYPE='"+value+"' ");  
                else if("SOURCE_CHANNEL".equals(key))
                    sb.append(" and d." + key + " like " + "'%" + value +"%'");
                else if("CUS_NATIONALITY".equals(key))
                    sb.append(" and d.CUS_NATIONALITY = '" + value + "' "); 
                else if("CUST_TYPE".equals(key))
                    sb.append(" and d.CUST_TYPE ='" + value + "'"); 
                else if("BACK_STATE".equals(key))
                    sb.append(" and d.BACK_STATE ='" + value + "'"); 
              /*  else if("STATE".equals(key))
                    sb.append(" and d.STATE = '" + value + "' "); */
                /* else if("BELONG_ORG_ID".equals(key))
                    sb.append(" and o.INSTITUTION_CODE = '" + value + "' "); */
                else if("CERT_CODE".equals(key))
                    sb.append(" and d.CERT_CODE = '" + value + "' ");
                else if("TREE_STORE".equals(key))
                    sb.append(" and d.MAIN_BR_ID = '" + value + "' ");
               
            }
        }
	   setPrimaryKey(" d.mover_date,d.input_date  desc ");
		SQL=sb.toString();
		datasource = ds;
		
		
	}
	
	
	public void query() {
		AuthUser auth=(AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ActionContext ctx = ActionContext.getContext();
    	request = (HttpServletRequest)ctx.get(ServletActionContext.HTTP_REQUEST);     	
    	String selectCustId="";
    	 for (String key : getJson().keySet()){
             String value = getJson().get(key).toString();
             if (! "".equals(value)) {
                 if("CUST_ID".equals(key)){              	 
                	 selectCustId=value;
                 }
           
             }
         }   	
		StringBuilder sb = new StringBuilder(
				"SELECT C.*," +
				"(select c.contmeth_info from ACRM_F_CI_CONTMETH c where  c.contmeth_type = '202' and c.cust_id='"+selectCustId+"') as CALL_NO, "+ 
	            "  a.ZIPCODE,"+  
	            " (select c.contmeth_info from ACRM_F_CI_CONTMETH c where  c.contmeth_type = '100' and c.cust_id='"+selectCustId+"') as CONTMETH_INFO,"+  	               
				"   a.ADDR" + 
				"  FROM ACRM_F_CI_customer C" + 
				"  left join OCRM_F_CI_BELONG_ORG o" + 
				"    on o.cust_id = c.cust_id" + 
				"  left join OCRM_F_CI_BELONG_CUSTMGR g" + 
				"    on g.cust_id = c.cust_id" + 
				"  left join ACRM_F_CI_ADDRESS a" + 
				"    on a.cust_id = c.cust_id" + 
				"  left join ACRM_F_CI_CONTMETH t" + 
				"    on t.cust_id = c.cust_id" + 
				" where 1 = 1" + 
				"   and C.POTENTIAL_FLAG = '1'" + 
				"   and a.ADDR_TYPE = '02'"+
				"  and c.cust_id='"+selectCustId+"'");
		SQL=sb.toString();
		datasource = ds;
	}	
}
