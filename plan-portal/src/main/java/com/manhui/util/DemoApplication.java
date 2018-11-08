package com.manhui.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Office;
import com.manhui.model.Plan;
import com.manhui.model.Report;
import com.manhui.model.User;
import com.manhui.model.assessment.Assessment;
import com.manhui.service.PlanService;
import com.manhui.service.ReportService;
import com.manhui.service.assessment.AssessmentService;
import com.manhui.service.planCooperation.CooperationService;

import net.minidev.json.JSONObject;


/**
 * @ClassName: DemoApplication
 * @description: 定时器
 * @author:	RanMaoKun
 * @date Create in 2017年10月30日11:15:05
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Configuration
@EnableScheduling  
public class DemoApplication {
	
	//引入汇报记录
	@Autowired
	private ReportService reportService;
		
	//引入计划分类类型
	@Autowired
	private PlanService planService;
	
	@Autowired
	private CooperationService coopService;
	
	@Autowired
    private AssessmentService assService;
	
	/**
	 * 定时器总开关
	 * @param args
	 */
    public static void main(String[] args) {  
       // SpringApplication.run(DemoApplication.class, args);  
    }
    
    @SuppressWarnings("unchecked")
	@Scheduled(cron = "0 0 18 * * ?")		//每天下午6点执行  
    public void timers() throws ParseException{
    	List<User> UList = (List<User>) CacheManager.getCacheInfo("allUser").getValue();
    	Date date =new Date();
    	List<Report> rep=this.reportService.findReport();	//查询出未汇报的汇报  变更为超时
    	for(Report rp:rep){
    		rp.setStatus(2);
    		this.reportService.updateReport(rp); //将没有汇报的汇报改为已超时
    		Assessment ass = new Assessment();
			ass.setPlanId(rp.getPlanId());
			ass.setAssType(2);
			ass.setAssTime(date);
			ass.setAssContent(rp.getPlanName());
			Plan plan = this.planService.findById(rp.getPlanId());
			ass.setAssedPerson(plan.getBlamePerson());
			ass.setPersonCompany(plan.getBlameOrganization());
			ass.setPersonOffice(plan.getBlameSection());
			for (User user : UList) {
				if(user.getName().equals(plan.getBlamePerson()))
					ass.setPersonId(user.getUserId());
			}
			ass.setAssAmount(50.0);
			ass.setCreateTime(date);
			ass.setStatus(3);
			this.assService.insert(ass);
    	}
    	generateAssessment();
    }
    
    
    @SuppressWarnings("unchecked")
	@Scheduled(cron = "0 0 0 * * ?")   													//定时器每天0点执行
    public void timer() throws ParseException{  
    	
    	
    	Plan plan=new Plan();
    	List<Plan> plans=this.planService.findCoreMainPlan(plan);
    	List<User> users=(List<User>) CacheManager.getCacheInfo("allUser").getValue();
    	List<Office> offices=(List<Office>) CacheManager.getCacheInfo("allOffice").getValue();
    	String dates=getDates();
		if(plans.size()>0&&plans!=null){
			for(Plan p:plans){
	    		if(p.getPlanType()==1){			//项目类计划才有汇报
	    			if(p.getStatus()!=5&&p.getStatus()!=6&&p.getStatus()!=7){	//先判断计划状态是否需要创建汇报  这里暂时先判断已经完成的 还有已经中止，暂停 的
	    				boolean flg=false;
	    				String username=p.getBlamePerson();
	    				for(User user:users){
	    					if(user.getName().equals(username)){
	    						JSONObject json = new JSONObject();
	    						json.put("date", dates);
	    						for(Office office:offices){
	    							if(office.getName().equals(user.getOffice())){
	    								json.put("deptId", office.getId());
	    							}
	    						}
	    						json.put("userId", user.getUserId());
	    						String re=PostWorkDay.post(json);
	    						if(re.equals(0)){
	    							flg=true;
	    						}else if(re.equals(1)){
	    							System.out.println("不在工作时间");
	    						}else{
	    							System.out.println("系统错误");
	    						}
	    					}
	    				}
	        			 //这里调用人力资源接口判断当天是否是工作时间且没请假    是就创建汇报   
	            		if(flg){
	            			Report report =new Report();
	                		report.setPlanId(p.getPlanId());
	                		report.setPlanName(p.getPlanName());
	                		report.setMainPlanName("--");
	                		report.setReportObject("--");
	                		report.setReportPerson(p.getBlamePerson());
	                		report.setShouldReportTime(getDateTime());
	                		report.setStatus(1);
	            			List<Report> reports=this.reportService.selectByplanId(p.getPlanId());
	            			if(null!=reports&&reports.size()>0){	//说明已经生成过汇报
	            				Integer planreportId=this.reportService.maxPlanReportId(p.getPlanId());
	            				Report rp=this.reportService.selectByReportId(planreportId);
	            				report.setFinishDegree(rp.getFinishDegree());
	            			}
	            			this.reportService.insertReport(report);
	            		}
	        		}
	        		List<Plan> plans2=this.planService.findSubPlans(p.getPlanId());
	        		if(null!=plans2&&plans2.size()>0){
	        			for(Plan pp:plans2){
	        				if(pp.getStatus()!=5&&pp.getStatus()!=6&&pp.getStatus()!=7){	//先判断计划状态是否需要创建汇报  这里暂时先判断已经完成的 还有已经中止，暂停 的
	        	    			boolean flg=false; //这里调用人力资源接口判断当天是否是工作时间且没请假    是就创建汇报   
	        	    			String username=pp.getBlamePerson();
	            				for(User user:users){
	            					if(user.getName().equals(username)){
	            						JSONObject json = new JSONObject();
	            						json.put("date", dates);
	            						for(Office office:offices){
	            							if(office.getName().equals(user.getOffice())){
	            								json.put("deptId", office.getId());
	            							}
	            						}
	            						json.put("userId", user.getUserId());
	            						String re=PostWorkDay.post(json);
	            						if(re.equals(0)){
	            							flg=true;
	            						}else if(re.equals(1)){
	            							System.out.println("不在工作时间");
	            						}else{
	            							System.out.println("系统错误");
	            						}
	            					}
	            				}
	        	        		if(flg){
	        	        			Report report =new Report();
	        	            		report.setPlanId(pp.getPlanId());
	        	            		report.setPlanName(pp.getPlanName());
	        	            		report.setMainPlanName(p.getPlanName());
	        	            		report.setReportObject(p.getBlamePerson());
	        	            		report.setReportPerson(pp.getBlamePerson());
	        	            		report.setShouldReportTime(getDateTime());
	        	            		report.setStatus(1);
	        	        			List<Report> reports=this.reportService.selectByplanId(pp.getPlanId());
	        	        			if(null!=reports&&reports.size()>0){	//说明已经生成过汇报
	        	        				Integer planreportId=this.reportService.maxPlanReportId(pp.getPlanId());
	        	        				Report rp=this.reportService.selectByReportId(planreportId);
	        	        				report.setFinishDegree(rp.getFinishDegree());
	        	        			}
	        	        			this.reportService.insertReport(report);
	        	        		}
	        	    		}
	        				List<Plan> plans3 =this.planService.findSubPlans(pp.getPlanId());
	        				if(null!=plans3&&plans3.size()>0){
	        					for(Plan ppp:plans3){
	        						if(ppp.getStatus()!=5&&ppp.getStatus()!=6&&ppp.getStatus()!=7){	//先判断计划状态是否需要创建汇报  这里暂时先判断已经完成的 还有已经中止，暂停 的
	        	    	    			boolean flg=false; //这里调用人力资源接口判断当天是否是工作时间且没请假    是就创建汇报   
	        	    	    			String username=ppp.getBlamePerson();
	                    				for(User user:users){
	                    					if(user.getName().equals(username)){
	                    						JSONObject json = new JSONObject();
	                    						json.put("date", dates);
	                    						for(Office office:offices){
	                    							if(office.getName().equals(user.getOffice())){
	                    								json.put("deptId", office.getId());
	                    							}
	                    						}
	                    						json.put("userId", user.getUserId());
	                    						String re=PostWorkDay.post(json);
	                    						if(re.equals(0)){
	                    							flg=true;
	                    						}else if(re.equals(1)){
	                    							System.out.println("不在工作时间");
	                    						}else{
	                    							System.out.println("系统错误");
	                    						}
	                    					}
	                    				}
	        	    	        		if(flg){
	        	    	        			Report report =new Report();
	        	    	            		report.setPlanId(ppp.getPlanId());
	        	    	            		report.setPlanName(ppp.getPlanName());
	        	    	            		report.setMainPlanName(pp.getPlanName());
	        	    	            		report.setReportObject(pp.getBlamePerson());
	        	    	            		report.setReportPerson(ppp.getBlamePerson());
	        	    	            		report.setShouldReportTime(getDateTime());
	        	    	            		report.setStatus(1);
	        	    	        			List<Report> reports=this.reportService.selectByplanId(ppp.getPlanId());
	        	    	        			if(null!=reports&&reports.size()>0){	//说明已经生成过汇报
	        	    	        				Integer planreportId=this.reportService.maxPlanReportId(ppp.getPlanId());
	        	    	        				Report rp=this.reportService.selectByReportId(planreportId);
	        	    	        				report.setFinishDegree(rp.getFinishDegree());
	        	    	        			}
	        	    	        			this.reportService.insertReport(report);
	        	    	        		}
	        	    	    		}
	        						List<Plan> plans4=this.planService.findSubPlans(ppp.getPlanId());
	        						if(null!=plans4&&plans4.size()>0){
	        							for(Plan pppp:plans4){
	        								if(pppp.getStatus()!=5&&pppp.getStatus()!=6&&pppp.getStatus()!=7){	//先判断计划状态是否需要创建汇报  这里暂时先判断已经完成的 还有已经中止，暂停 的
	        	    	    	    			boolean flg=false; //这里调用人力资源接口判断当天是否是工作时间且没请假    是就创建汇报  
	        	    	    	    			String username=pppp.getBlamePerson();
	                            				for(User user:users){
	                            					if(user.getName().equals(username)){
	                            						JSONObject json = new JSONObject();
	                            						json.put("date", dates);
	                            						for(Office office:offices){
	                            							if(office.getName().equals(user.getOffice())){
	                            								json.put("deptId", office.getId());
	                            							}
	                            						}
	                            						json.put("userId", user.getUserId());
	                            						String re=PostWorkDay.post(json);
	                            						if(re.equals(0)){
	                            							flg=true;
	                            						}else if(re.equals(1)){
	                            							System.out.println("不在工作时间");
	                            						}else{
	                            							System.out.println("系统错误");
	                            						}
	                            					}
	                            				}
	        	    	    	        		if(flg){
	        	    	    	        			Report report =new Report();
	        	    	    	            		report.setPlanId(pppp.getPlanId());
	        	    	    	            		report.setPlanName(pppp.getPlanName());
	        	    	    	            		report.setMainPlanName(ppp.getPlanName());
	        	    	    	            		report.setReportObject(ppp.getBlamePerson());
	        	    	    	            		report.setReportPerson(pppp.getBlamePerson());
	        	    	    	            		report.setShouldReportTime(getDateTime());
	        	    	    	            		report.setStatus(1);
	        	    	    	        			List<Report> reports=this.reportService.selectByplanId(pppp.getPlanId());
	        	    	    	        			if(null!=reports&&reports.size()>0){	//说明已经生成过汇报
	        	    	    	        				Integer planreportId=this.reportService.maxPlanReportId(pppp.getPlanId());
	        	    	    	        				Report rp=this.reportService.selectByReportId(planreportId);
	        	    	    	        				report.setFinishDegree(rp.getFinishDegree());
	        	    	    	        			}
	        	    	    	        			this.reportService.insertReport(report);
	        	    	    	        		}
	        	    	    	    		}
	        								List<Plan> plans5=this.planService.findSubPlans(pppp.getPlanId());
	        								if(null!=plans5&&plans5.size()>0){
	        									for(Plan ppppp:plans5){
	        										if(ppppp.getStatus()!=5&&ppppp.getStatus()!=6&&ppppp.getStatus()!=7){	//先判断计划状态是否需要创建汇报  这里暂时先判断已经完成的 还有已经中止，暂停 的
	        	    	    	    	    			boolean flg=false; //这里调用人力资源接口判断当天是否是工作时间且没请假    是就创建汇报   
	        	    	    	    	    			String username=ppppp.getBlamePerson();
	        	                        				for(User user:users){
	        	                        					if(user.getName().equals(username)){
	        	                        						JSONObject json = new JSONObject();
	        	                        						json.put("date", dates);
	        	                        						for(Office office:offices){
	        	                        							if(office.getName().equals(user.getOffice())){
	        	                        								json.put("deptId", office.getId());
	        	                        							}
	        	                        						}
	        	                        						json.put("userId", user.getUserId());
	        	                        						String re=PostWorkDay.post(json);
	        	                        						if(re.equals(0)){
	        	                        							flg=true;
	        	                        						}else if(re.equals(1)){
	        	                        							System.out.println("不在工作时间");
	        	                        						}else{
	        	                        							System.out.println("系统错误");
	        	                        						}
	        	                        					}
	        	                        				}
	        	    	    	    	        		if(flg){
	        	    	    	    	        			Report report =new Report();
	        	    	    	    	            		report.setPlanId(ppppp.getPlanId());
	        	    	    	    	            		report.setPlanName(ppppp.getPlanName());
	        	    	    	    	            		report.setMainPlanName(pppp.getPlanName());
	        	    	    	    	            		report.setReportObject(pppp.getBlamePerson());
	        	    	    	    	            		report.setReportPerson(ppppp.getBlamePerson());
	        	    	    	    	            		report.setShouldReportTime(getDateTime());
	        	    	    	    	            		report.setStatus(1);
	        	    	    	    	        			List<Report> reports=this.reportService.selectByplanId(ppppp.getPlanId());
	        	    	    	    	        			if(null!=reports&&reports.size()>0){	//说明已经生成过汇报
	        	    	    	    	        				Integer planreportId=this.reportService.maxPlanReportId(ppppp.getPlanId());
	        	    	    	    	        				Report rp=this.reportService.selectByReportId(planreportId);
	        	    	    	    	        				report.setFinishDegree(rp.getFinishDegree());
	        	    	    	    	        			}
	        	    	    	    	        			this.reportService.insertReport(report);
	        	    	    	    	        		}
	        	    	    	    	    		}
	        									}
	        								}
	        							}
	        						}
	        					}
	        				}
	        			}
	        		}
	    		}
	    	}
		}
    }
    
	/**
     * 得到当天日期
     * @return
     * @throws ParseException
     */
    public Date getDateTime() throws ParseException{
    	Date date =new Date();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
	    String time = format.format(date);
	    time =time+" 00:00:00";
	    Date datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
		return datetime;
    	
    }
    
    
    public String getDates() throws ParseException{
    	Date date =new Date();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
	    String time = format.format(date);
		return time;
    	
    }
     
    /**
	 * @description: 生成考核
	 * @param 
	 * @return 
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	private void generateAssessment() throws ParseException {
		List<User> UList = (List<User>) CacheManager.getCacheInfo("allUser").getValue();
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
		Date date = new Date();
		String today = sdf.format(date);
		List<Plan> planList = this.planService.findAllPlan();
		for (Plan plan : planList) {
			if(plan.getPlanType() == 1 && plan.getStartTime() != null && !"".equals(plan.getStartTime())){
				if(DateUtil.daysBetween(today, plan.getStartTime()) < 0 && plan.getStatus() == 3){
					Assessment ass = new Assessment();
					ass.setPlanId(plan.getPlanId());
					ass.setAssType(3);
					ass.setAssTime(date);
					ass.setAssContent(plan.getPlanName());
					ass.setAssedPerson(plan.getBlamePerson());
					ass.setPersonCompany(plan.getBlameOrganization());
					ass.setPersonOffice(plan.getBlameSection());
					for (User user : UList) {
						if(user.getName().equals(plan.getBlamePerson()))
							ass.setPersonId(user.getUserId());
					}
					ass.setAssAmount(100.0);
					ass.setCreateTime(date);
					ass.setStatus(3);
					this.assService.insert(ass);
				}
				
				if(DateUtil.daysBetween(today, plan.getEndTime()) < 0 && (plan.getStatus() != 5 || plan.getStatus() != 6)){
					Assessment ass = new Assessment();
					ass.setPlanId(plan.getPlanId());
					ass.setAssType(4);
					ass.setAssTime(date);
					ass.setAssContent(plan.getPlanName());
					ass.setAssedPerson(plan.getBlamePerson());
					ass.setPersonCompany(plan.getBlameOrganization());
					ass.setPersonOffice(plan.getBlameSection());
					for (User user : UList) {
						if(user.getName().equals(plan.getBlamePerson()))
							ass.setPersonId(user.getUserId());
					}
					ass.setAssAmount(100.0);
					ass.setCreateTime(date);
					ass.setStatus(3);
					this.assService.insert(ass);
				}
			}
		}
	}
} 
