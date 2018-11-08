package com.manhui.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Accessory;
import com.manhui.model.Plan;
import com.manhui.model.Question;
import com.manhui.model.QuestionList;
import com.manhui.model.Report;
import com.manhui.model.User;
import com.manhui.service.AccessoryService;
import com.manhui.service.PlanService;
import com.manhui.service.QuestionService;
import com.manhui.service.ReportService;
import com.manhui.util.FileUploadUtil;

import net.minidev.json.JSONObject;

/**
 * @ClassName: PlanReportController
 * @description: 进度汇报控制层
 * @author:	RanMaoKun
 * @date Create in 2017年12月8日10:39:20
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class PlanReportController {
	
	
	//引入汇报记录
	@Autowired
	private ReportService reportService;
			
	//引入计划分类类型
	@Autowired
	private PlanService planService;
	
	//引入问题类型
	@Autowired
	private QuestionService questionService;
	
	@Autowired
    private AccessoryService acceService;
	
	public Date getTime(String time) throws ParseException{
		 Date datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
		return datetime;
	}
	
	
	 @RequestMapping("/schedule_report")
	 public ModelAndView myReport(){
	    ModelAndView mv = new ModelAndView("pages/lookreport/myReport");
		return mv;
	 }
	 
	
	
	/**
	 * 查询我需要汇报的汇报
	 * @param model
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @param planName
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws ParseException
	 */
	@GetMapping("/my_report_list_{pageCurrent}_{pageSize}_{pageCount}")
	public String findMyReport(Model model,@PathVariable Integer pageCurrent,@PathVariable Integer pageSize,
			@PathVariable Integer pageCount,String planName,String startTime,String endTime,Integer status) throws ParseException{
		Map<String, Object> map=new HashMap<>();
		User user = (User) CacheManager.getCacheInfo("user").getValue(); //取到缓存中的登录用户
		map.put("reportPerson", user.getName());
		if(null!=planName&&""!=planName){
			planName="%"+planName+"%";
			map.put("planName", planName);
		}
		if(startTime!=null&&(!"".equals(startTime))){
			startTime=startTime+" 00:00:00";
			getTime(startTime);
			map.put("startTime", startTime);
		}
		if(null!=endTime&&(!"".equals(endTime))){
			endTime=endTime+" 59:59:59";
			getTime(endTime);
			map.put("endTime", endTime);
		}
		if(null !=status&&0!=status){
			map.put("status", status);
		}
		//判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows=this.reportService.findMyReportCount(map);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		map.put("start", (pageCurrent - 1)*pageSize);
		map.put("end", pageSize);
		List<Report> reports=this.reportService.findMyReport(map);
		if(null!=reports&&reports.size()>0){
			model.addAttribute("reports", reports);
		}
		model.addAttribute("pageCurrent", pageCurrent);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("rows", rows);
		model.addAttribute("pageCount", pageCount);
		return "pages/lookreport/myReportList";
	}
	 
	
	/**
	 * 查询我查看的汇报
	 * @param model
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @param planName
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws ParseException
	 */
	@GetMapping("/toSeeReport_{pageCurrent}_{pageSize}_{pageCount}")
	public String findLookReport(Model model,@PathVariable Integer pageCurrent,
				@PathVariable Integer pageSize,@PathVariable Integer pageCount,String planName,String startTime,String endTime) throws ParseException{
			Map<String, Object> map=new HashMap<>();
			User user = (User) CacheManager.getCacheInfo("user").getValue(); //取到缓存中的登录用户
			map.put("reportObject", user.getName());
			if(null != planName&&""!=planName){
				planName="%"+planName+"%";
				map.put("planName", planName);
			}
			if(null != startTime&&""!=startTime){
				startTime=startTime+" 00:00:00";
				getTime(startTime);
				map.put("startTime", startTime);
			}
			if(null != endTime&&""!=endTime){
				endTime=endTime+" 59:59:59";
				getTime(endTime);
				map.put("endTime", endTime);
			}
			//判断
			if(pageSize == 0) pageSize = 10;
			if(pageCurrent == 0) pageCurrent = 1;
			int rows=this.reportService.findLookReportCount(map);
			if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
			map.put("start", (pageCurrent - 1)*pageSize);
			map.put("end", pageSize);
			List<Report> reports=this.reportService.findLookReport(map);
			if(null!=reports&&reports.size()>0){
				model.addAttribute("reports", reports);
			}
			model.addAttribute("reports", reports);
			model.addAttribute("pageCurrent", pageCurrent);
			model.addAttribute("pageSize", pageSize);
			model.addAttribute("rows", rows);
			model.addAttribute("pageCount", pageCount);
			return "pages/lookreport/lookReportList";
	}
	
	
	/**
	 *  汇报页面 、查看页面
	 * @param reportId
	 * @param model
	 * @return
	 */
	@GetMapping("/planReport")
	public String planReport(Integer reportId,Model model,String type){
		//type=1 为汇报  2为查看
		Report report=this.reportService.selectByReportId(reportId);
		Plan plan=this.planService.findById(report.getPlanId());
		plan.setLoopMode(report.getFinishDegree());
		Plan mPlan=new Plan();
		if(plan.getParentId()==null){
			mPlan=plan;
		}else{
			mPlan=this.planService.findById(plan.getParentId());
			if(mPlan.getParentId()!=null){
				mPlan=this.planService.findById(mPlan.getParentId());
				if(mPlan.getParentId()!=null){
					mPlan=this.planService.findById(mPlan.getParentId());
					if(mPlan.getParentId()!=null){
						mPlan=this.planService.findById(mPlan.getParentId());
						if(mPlan.getParentId()!=null){
							mPlan=this.planService.findById(mPlan.getParentId());
						}
					}
				}
			}
		}
		List<Question> questions=this.questionService.findByReportId(report.getReportId());
		List<Plan> plans=this.planService.findSubPlans(plan.getPlanId());
		if(null!=plans&&plans.size()>0){
			for(Plan p:plans){
				Integer planreportId=this.reportService.maxPlanReportId(p.getPlanId());
				if(null!=planreportId&&planreportId!=0){		//说明下面的计划汇报过
    				Report rp=this.reportService.selectByReportId(planreportId);
    				p.setLoopMode(rp.getFinishDegree());		//暂时用来保存进度
    				p.setNodeStatus(planreportId);				//暂时用来保存最近汇报id
				}else{
					p.setLoopMode(0);
				}
			}
		}
		Accessory acce = new Accessory();
		acce.setModel("report");
		acce.setModelId(reportId);
		acce.setStatus(0);
		List<Accessory> acceList = acceService.selectByAccessorySelective(acce);
		
		if(acceList.size() > 0)
			model.addAttribute("acceList", acceList);
		model.addAttribute("report", report);
		model.addAttribute("type", type);
		model.addAttribute("mPlan", mPlan);
		model.addAttribute("plan", plan);
		model.addAttribute("plans", plans);
		model.addAttribute("questions", questions);
		return "pages/lookreport/planReport";
	}
	 
	/**
	 * 提交汇报
	 * @param reporId
	 * @param finishDegree
	 * @param question
	 * @param remark
	 * @return
	 */
	@GetMapping("/saveReport")
	@ResponseBody
	public Integer saveReport(Integer reportId,Integer finishDegree,QuestionList question,String remark){
		Report report=this.reportService.selectByReportId(reportId);
		report.setFinishDegree(finishDegree);
		report.setRemark(remark);
		report.setStatus(3);
		report.setRealityReportTime(new Date());
		this.reportService.updateReport(report);
		if(null!=question){
			for(Question qt:question.getQuestion()){
				if(null!=qt.getQuestionContent()&&""!=qt.getQuestionContent()){
					qt.setReportId(reportId);
					this.questionService.saveQuestion(qt);
				}
			}
		}
		return 1;
	}
	
	/**
	 * 汇报等页面点击查看的详情
	 * @param reportId
	 * @param model
	 * @return
	 */
	@GetMapping("/reportDetail")
	public String reportDetail(Integer reportId,Model model){
		Report report=this.reportService.selectByReportId(reportId);
		Plan plan=this.planService.findById(report.getPlanId());
		List<Plan> list=this.planService.findSubPlans(plan.getPlanId());
		if(null!=list&&list.size()>0){
			for(Plan p:list){
				Integer planreportId=this.reportService.maxPlanReportId(p.getPlanId());
				if(null!=planreportId&&0!=planreportId){		//说明有汇报
					Report rp=this.reportService.selectByReportId(planreportId);
					p.setLoopMode(rp.getFinishDegree());		//暂时用来保存进度
				}else{
					p.setLoopMode(0);		//暂时用来保存进度
				}
			}
		}
		model.addAttribute("report", report);
		model.addAttribute("plan", plan);
		model.addAttribute("plans", list);
		return "pages/lookreport/reportDetail";
	}
	
	
	
	/**
	 * @description: 取消完成，删除已上传的附件
	 * @param coopId
	 * @param
	 * @return 
	 * @throws
	 */
	@RequestMapping("/cancel_report")
	public String cancelComplete(String coopId){
		Accessory acce = new Accessory();
		acce.setModel("report");
		acce.setModelId(Integer.parseInt(coopId));
		acceService.deleteByModelAndId(acce);
		return "redirect:/schedule_report.action";
	}
	
	/**
	 * @description: 上传计划协作附件
	 * @param request
	 * @param coopId
	 * @param remark
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping(value = "/upload/reportAccessory", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject completeCooperation(HttpServletRequest request, MultipartFile file, String planId){
		JSONObject json = new JSONObject();
		String uploadDir = "upload/reportAccessory/";  //本地测试用地址
		//String uploadDir = "/manhui-plan/upload/reportAccessory/";     //部署到linux服务器上时，使用这个上传地址
		//上传文件，转存到服务器
		boolean flag = FileUploadUtil.upload(request, file, uploadDir);
		if(flag == true){
			Date upTime = new Date();
			String fileName = file.getOriginalFilename();
			String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);  
			String size = FileUploadUtil.FormetFileSize(file.getSize());
			
			Accessory acce = new Accessory();
			acce.setModel("report");
			acce.setModelId(Integer.parseInt(planId));
			acce.setName(fileName);
			acce.setSize(size);
			acce.setSuffix("."+suffix);
			acce.setUpTime(upTime);
			acce.setPath(uploadDir.substring(uploadDir.lastIndexOf("upload"), uploadDir.length()));
			acceService.insertSelective(acce);
			
			json.put("code", 0);
			json.put("msg", "上传成功！");
		}else{
			json.put("code", 1);
			json.put("msg", "上传失败！");
		}
		return json;
	}
	
	/**
	 * @description: 下载计划协作的附件文件
	 * @param request
	 * @param response
	 * @param id
	 * @return 
	 * @throws
	 */
	@RequestMapping("/download_report_accessory")
	public void downloadAccessory(HttpServletRequest request, HttpServletResponse response, String id){
		Accessory acce = acceService.selectByPrimaryKey(Integer.parseInt(id));
		String filePath = request.getSession().getServletContext().getRealPath(acce.getPath());     //本地测试用地址
		String fileName = acce.getName();
		FileUploadUtil.download(request, response, fileName, filePath);	
	}
	
	
	
	
	/**
	 * 删除问题
	 * @param questionId
	 * @return
	 */
	@GetMapping("/deleteQuestion")
	@ResponseBody
	public String deleteQuestion(Integer questionId){
		int i=this.questionService.deleteQuestion(questionId);
		if(i!=1){
			return "1";
		}
		return "2";
	}
}
