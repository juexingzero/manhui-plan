package com.manhui.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.manhui.model.TemplateType;
import com.manhui.model.User;
import com.manhui.service.TemplateTypeService;
import com.manhui.util.PageUtil;

/**
 * @ClassName: TemplateTypeController
 * @Description: 模板类型控制器 
 * @author jiangxiaosong
 * @date: 2017年9月29日19:20:02
 */
@Controller
public class TemplateTypeController {
	
	//引入模板分类类型
	@Autowired
	private TemplateTypeService templateTypeService;


	/**
	 * 查询模板库
	 * @param tt
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @param model
	 * @return
	 */
	@GetMapping("/toFindTemplateType_{pageCurrent}_{pageSize}_{pageCount}")
	public String toFindTemplate(Model model,TemplateType tt,@PathVariable Integer pageCurrent,
								@PathVariable Integer pageSize,@PathVariable Integer pageCount) {
		//转换模糊查询
		String typeName = tt.getTypeName();
		if(typeName != null){
			typeName = "%"+typeName+"%";
			tt.setTypeName(typeName);
		}
		
		//判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = templateTypeService.count(tt);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		//查询所有计划模板
		tt.setStart((pageCurrent - 1)*pageSize);
		tt.setEnd(pageSize);
		List<TemplateType> templateTypeList = templateTypeService.findAllTemplateType(tt);
		//将下拉框内容传入页面
		model.addAttribute("templateTypeList",templateTypeList);
		model.addAttribute("rows", rows);
		model.addAttribute("pageCurrent", pageCurrent);
		model.addAttribute("pageSize", pageSize);
		return "pages/planTemplate/templatetype";
	}
	
	
	/**
	 * 跳转到新增画面
	 * @param model
	 * @return
	 */
	@GetMapping("/toTemplateTypeSave")
	public String toTemplateTypeSave(Model model){
		return "pages/planTemplate/savetemplatetype";
	}
	
	
	/**
	 * 新增模板分类
	 * @param model
	 * @param tt
	 * @return
	 */
	@PostMapping("/templateTypeSave")
	public String templateTypeSave(Model model,TemplateType tt,HttpSession httpSession){
		User user = (User)httpSession.getAttribute("user");
		Date createDate = new Date();
		if(user != null){
			tt.setCreateName(user.getUserName());
		}
		tt.setCreateDate(createDate);
		tt.setState(1);
		templateTypeService.insert(tt);
		return "redirect:toFindTemplateType_0_0_0";
	}
	
	
	/**
	 * 跳转到编辑页面
	 * @param model
	 * @param tt
	 * @return
	 */
	@GetMapping("/toTemplateTypeEdit")
	public String toTemplateTypeEdit(Model model,TemplateType tt) {
		TemplateType tType = templateTypeService.findTemplateType(tt);
		model.addAttribute("tType",tType);
		return "pages/planTemplate/edittemplatetype";
	}

	
	/**
	 * 保存模板编辑
	 * @param model
	 * @param pt
	 * @return
	 */
	@PostMapping("/templateTypeEdit")
	public String templateTypeEdit(Model model,TemplateType tt) {
		if(tt.getTtId() != 0){
			templateTypeService.update(tt);
		}else{
			model.addAttribute("error","不存在该条记录");
		}
		return "redirect:toFindTemplateType_0_0_0";
	}
	
	
	/**
	 * 删除模板
	 * @param model
	 * @param pt
	 * @return
	 */
	@PostMapping("/templateTypeDel")
	public String logout(Model model,Integer id) {
		templateTypeService.delete(id);
		return "redirect:toFindTemplate_0_0_0";
	}
}
