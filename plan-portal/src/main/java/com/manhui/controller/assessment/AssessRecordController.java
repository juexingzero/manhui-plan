package com.manhui.controller.assessment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Accessory;
import com.manhui.model.Plan;
import com.manhui.model.User;
import com.manhui.model.assessment.Assessment;
import com.manhui.model.planCooperation.Cooperation;
import com.manhui.model.planCooperation.SkipInfo;
import com.manhui.service.AccessoryService;
import com.manhui.service.PlanService;
import com.manhui.service.assessment.AssessmentService;
import com.manhui.service.planCooperation.CooperationService;
import com.manhui.service.planCooperation.SkipInfoService;
import com.manhui.util.FileUploadUtil;

import net.minidev.json.JSONObject;

/**
 * @ClassName: AssessRecordController
 * @description: 考核记录控制器
 * @author:	HeJiayan
 * @date Create in 上午11:42:21 2017年12月9日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class AssessRecordController {
	
	@Autowired
    private AssessmentService assService;
	@Autowired
    private CooperationService coopService;
	@Autowired
    private SkipInfoService skipService;
	@Autowired
    private PlanService planService;
	@Autowired
    private AccessoryService acceService;
	
	/**
	 * @description: 分页查询所有考核信息
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @param status
	 * @param assType
	 * @param assContent
	 * @return 
	 * @throws
	 */
	@RequestMapping("/examine_record_{pageCurrent}_{pageSize}_{pageCount}")
	public ModelAndView examineRecord(@PathVariable Integer pageCurrent, @PathVariable Integer pageSize,@PathVariable Integer pageCount,
			Assessment ass, String startTime, String endTime, String flag){
		ModelAndView mv = new ModelAndView("pages/assessment/assessRecord/assessment_list");
		//User user = (User) CacheManager.getCacheInfo("user").getValue();
		Map<String,Object> criteria = new HashMap<String, Object>();
		
		if(ass.getAssContent() != null && !"".equals(ass.getAssContent()))
			criteria.put("assContent", "%"+ass.getAssContent()+"%");
		if(ass.getStatus() != null && !"".equals(ass.getStatus()))
			criteria.put("status", ass.getStatus());
		if(ass.getAssType() != null && !"".equals(ass.getAssType()))
			criteria.put("assType", ass.getAssType());
		if(ass.getAssedPerson() != null && !"".equals(ass.getAssedPerson()))
			criteria.put("assedPerson", "%"+ass.getAssedPerson()+"%");
		if(startTime != null && !"".equals(startTime) && endTime!= null && !"".equals(endTime)){
			criteria.put("startTime", startTime);
			criteria.put("endTime", endTime);
		}else{
			SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			try {
				calendar.setTime(date);
	        	calendar.add(Calendar.MONTH, -1);
	        	calendar.set(Calendar.DAY_OF_MONTH, 1);
	        	calendar.set(Calendar.HOUR_OF_DAY, 0);
	        	calendar.set(Calendar.MINUTE, 0);
	        	calendar.set(Calendar.SECOND, 0);
	            String newStartTime = sdf.format(calendar.getTime());
	            criteria.put("startTime", newStartTime);
	            
	            calendar.setTime(date);
	            calendar.add(Calendar.MONTH, +1);
	        	calendar.set(Calendar.DAY_OF_MONTH, 1);
	        	calendar.set(Calendar.HOUR_OF_DAY, 0);
	        	calendar.set(Calendar.MINUTE, 0);
	        	calendar.set(Calendar.SECOND, 0);
	            String newEndTime = sdf.format(calendar.getTime());
	            criteria.put("endTime", newEndTime);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		
		//分页判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = assService.countAllAssessment(criteria);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		
		//条件查询所有协作申请列表
		criteria.put("start", (pageCurrent - 1)*pageSize);
		criteria.put("end", pageSize);
		List<Assessment> assList = assService.findAllAssessment(criteria);
		
		if(assList != null && assList.size() > 0)
			mv.addObject("assList", assList);
		if(flag != null && "rebut".equals(flag)){
			flag = "驳回成功！";
		}else if(flag != null && "revoke".equals(flag)){
			flag = "撤销成功！";
		}
		mv.addObject("flag", flag);
		mv.addObject("ass", ass);
		mv.addObject("startTime", startTime);
		mv.addObject("endTime", endTime);
		mv.addObject("pageCurrent",pageCurrent);
		mv.addObject("pageSize",pageSize);
		mv.addObject("pageCount",pageCount);
		mv.addObject("rows",rows);
		return mv;
	}
	
	/**
	 * @description: 处理或者查看申诉信息
	 * @param assId
	 * @param flag
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/deal_or_check_assessment")
	public ModelAndView dealOrCheckAssessment(String assId, String flag){
		ModelAndView mv = new ModelAndView("pages/assessment/assessRecord/assessment_deal_or_check");
		Assessment ass = new Assessment();
		Cooperation coop = new Cooperation();
		SkipInfo skip = new SkipInfo();
		Plan plan = new Plan();
		if(assId != null && !"".equals(assId)){
			ass = assService.selectByPrimaryKey(Integer.parseInt(assId));
			if(ass.getCoopId() != null && !"".equals(ass.getCoopId())){
				coop = coopService.selectByPrimaryKey(ass.getCoopId());
				List<SkipInfo> skipInfos = skipService.findAllSkipInfosByCoopId(coop.getCoopId());
				if(skipInfos.size() > 0){
					if(coop.getCollaborator().equals(ass.getAssedPerson())){
						skip.setSkipTime(skipInfos.get(0).getSkipTime());
					}else{
						for (int i = 0; i < skipInfos.size(); i++) {
							if(skipInfos.get(i).getSkipHandler().equals(ass.getAssedPerson())){
								skip = skipInfos.get(i);
								skip.setSkipTime(skipInfos.get(i+1).getSkipTime());
							}
						}
					}
				}
			}
			if(ass.getPlanId() != null && !"".equals(ass.getPlanId()))
				plan = planService.findById(ass.getPlanId());
		}
		Accessory acce = new Accessory();
		//查询申诉附件
		acce.setModel("assessment-complain");
		acce.setModelId(Integer.parseInt(assId));
		acce.setStatus(0);
		List<Accessory> complainAcceList = acceService.selectByAccessorySelective(acce);
		//查询撤消附件
		acce = new Accessory();
		acce.setModel("assessment-revoke");
		acce.setModelId(Integer.parseInt(assId));
		acce.setStatus(0);
		List<Accessory> revokeAcceList = acceService.selectByAccessorySelective(acce);
		
		if(complainAcceList.size() > 0)
			mv.addObject("complainAcceList", complainAcceList);
		if(revokeAcceList.size() > 0)
			mv.addObject("revokeAcceList", revokeAcceList);
		mv.addObject("ass", ass);
		mv.addObject("coop", coop);
		mv.addObject("skip", skip);
		mv.addObject("plan", plan);
		mv.addObject("flag", flag);
		return mv;
	}
	
	/**
	 * @description: 驳回申诉
	 * @param coopId
	 * @param rejectReason
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/rebut_ass_complain")
	public String rebutAssComplain(String assId, String rebutReason){
		User loginUser = (User) CacheManager.getCacheInfo("user").getValue();
		Date rebutTime = new Date();
		if(assId != null && !"".equals(assId)){
			Assessment ass = assService.selectByPrimaryKey(Integer.parseInt(assId));
			ass.setRebutPerson(loginUser.getName());
			ass.setRebutTime(rebutTime);
			ass.setRebutReason(rebutReason);
			ass.setStatus(2);
			assService.updateByPrimaryKeySelective(ass);
		}
		return "redirect:/examine_record_0_10_0?flag=rebut";
	}
	
	/**
	 * @description: 撤销考核页面跳转
	 * @param assId
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/revoke_ass")
	public ModelAndView revokeAss(String assId){
		ModelAndView mv = new ModelAndView("pages/assessment/assessRecord/revoke_assessment");
		mv.addObject("assId", assId);
		return mv;
	}
	
	/**
	 * @description: 撤销考核
	 * @param assId
	 * @param revokeReason
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/revoke_ass_complain")
	public String revokeAssComplain(String assId, String revokeReason){
		User loginUser = (User) CacheManager.getCacheInfo("user").getValue();
		Date revokeTime = new Date();
		if(assId != null && !"".equals(assId)){
			Assessment ass = assService.selectByPrimaryKey(Integer.parseInt(assId));
			ass.setRevokePerson(loginUser.getName());
			ass.setRevokeTime(revokeTime);
			ass.setRevokeReason(revokeReason);
			ass.setStatus(5);
			assService.updateByPrimaryKeySelective(ass);
		}
		return "redirect:/examine_record_0_10_0?flag=revoke";
	}
	
	/**
	 * @description: 取消撤销，删除已上传的附件
	 * @param assId
	 * @param
	 * @return 
	 * @throws
	 */
	@RequestMapping("/cancel_revoke")
	public String cancelRevoke(String assId){
		Accessory acce = new Accessory();
		acce.setModel("assessment-revoke");
		acce.setModelId(Integer.parseInt(assId));
		acceService.deleteByModelAndId(acce);
		return "redirect:/examine_record_0_10_0.action";
	}
	
	/**
	 * @description: 上传考核相关附件
	 * @param request
	 * @param coopId
	 * @param remark
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping(value = "/upload/assessmentAccessory", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject assessmentAccessory(HttpServletRequest request, MultipartFile file, String assId, String upFlag){
		JSONObject json = new JSONObject();
		String uploadDir = "upload/assessmentAccessory/";  
		
		//上传文件，转存到服务器
		boolean flag = FileUploadUtil.upload(request, file, uploadDir);
		if(flag == true){
			Date upTime = new Date();
			String fileName = file.getOriginalFilename();
			String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);  
			String size = FileUploadUtil.FormetFileSize(file.getSize());
			
			Accessory acce = new Accessory();
			acce.setModel("assessment-"+upFlag);
			acce.setModelId(Integer.parseInt(assId));
			acce.setName(fileName);
			acce.setSize(size);
			acce.setSuffix("."+suffix);
			acce.setUpTime(upTime);
			acce.setPath(uploadDir);
			acceService.insertSelective(acce);
			
			json.put("code", 0);
			json.put("msg", "上传成功！");
		}else{
			json.put("code", 1);
			json.put("msg", "上传失败！");
		}
		return json;
	}
}
