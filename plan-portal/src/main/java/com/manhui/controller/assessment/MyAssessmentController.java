package com.manhui.controller.assessment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

/**
 * @ClassName: MyAssessmentController
 * @description: 我的考核相关控制器
 * @author:	HeJiayan
 * @date Create in 下午4:15:22 2017年12月11日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class MyAssessmentController {
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
	
	@RequestMapping("/my_examine_record_{pageCurrent}_{pageSize}_{pageCount}")
	public ModelAndView myExamineRecord(@PathVariable Integer pageCurrent, @PathVariable Integer pageSize,@PathVariable Integer pageCount,
			Assessment ass, String startTime, String endTime, String flag){
		ModelAndView mv = new ModelAndView("pages/assessment/myAssessment/my_assessment_list");
		User user = (User) CacheManager.getCacheInfo("user").getValue();
		Map<String,Object> criteria = new HashMap<String, Object>();
		
		criteria.put("assedPerson", "%"+user.getName()+"%");
		if(ass.getAssContent() != null && !"".equals(ass.getAssContent()))
			criteria.put("assContent", "%"+ass.getAssContent()+"%");
		if(ass.getStatus() != null && !"".equals(ass.getStatus()))
			criteria.put("status", ass.getStatus());
		if(ass.getAssType() != null && !"".equals(ass.getAssType()))
			criteria.put("assType", ass.getAssType());
		if(startTime != null && !"".equals(startTime) && endTime!= null && !"".equals(endTime)){
			criteria.put("startTime", startTime);
			criteria.put("endTime", endTime);
		}/*else{
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
		}*/
		
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
		if(flag != null && "submit".equals(flag)){
			flag = "提交申诉成功！";
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
	 * @description: 提起申诉或者查看考核详情
	 * @param assId
	 * @param flag
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/complain_or_check_assessment")
	public ModelAndView ComplainOrCheckAssessment(String assId, String flag){
		ModelAndView mv = new ModelAndView("pages/assessment/myAssessment/assessment_complain_or_check");
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
	 * @description: 提交申诉请求
	 * @param assId
	 * @param complainReason
	 * @param
	 * @return 
	 * @throws
	 */
	@RequestMapping("/submit_complain")
	public String submitComplain(String assId, String complainReason){
		User loginUser = (User) CacheManager.getCacheInfo("user").getValue();
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(date);
        	calendar.add(Calendar.HOUR_OF_DAY, -2);
        	date = calendar.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
		Accessory acce = new Accessory();
		acce.setModel("assessment-complain");
		acce.setModelId(Integer.parseInt(assId));
		acce.setUpTime(date);
		acceService.updateByModelAndId(acce);
		
		Assessment ass = assService.selectByPrimaryKey(Integer.parseInt(assId));
		ass.setComplainant(loginUser.getName());
		ass.setComplainReason(complainReason);
		ass.setComplainTime(new Date());
		ass.setRebutPerson(null);
		ass.setRebutReason(null);
		ass.setRebutTime(null);
		ass.setStatus(1);
		assService.updateByPrimaryKey(ass);
		return "redirect:/my_examine_record_0_10_0.action?flag=submit";
	}
	
	/**
	 * @description: 取消申诉，删除已上传的附件
	 * @param assId
	 * @param
	 * @return 
	 * @throws
	 */
	@RequestMapping("/cancel_complain")
	public String cancelComplain(String assId){
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(date);
        	calendar.add(Calendar.HOUR_OF_DAY, -2);
        	date = calendar.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
		Accessory acce = new Accessory();
		acce.setModel("assessment-complain");
		acce.setModelId(Integer.parseInt(assId));
		acce.setUpTime(date);
		acceService.deleteByModelAndId(acce);
		return "redirect:/my_examine_record_0_10_0.action";
	}
}
