package com.manhui.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.manhui.model.Plan;

/**
 * @ClassName: ExcelUtil
 * @description: Excel模板数据读取
 * @author:	HeJiayan
 * @date Create in 下午10:47:59 2017年10月23日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class ExcelUtil {
	public static Logger logger = Logger.getLogger(ExcelUtil.class);
	
	//总行数  
    private static int totalRows = 0;    
    //总条数  
    private static int totalCells = 0;   
    //错误信息接收器  
    private static String errorMsg;  
    //构造方法  
    public ExcelUtil(){}  
    //获取总行数  
    public int getTotalRows()  { return totalRows;}   
    //获取总列数  
    public int getTotalCells() {  return totalCells;}   
    //获取错误信息  
    public String getErrorInfo() { return errorMsg; } 
	
	/** 
     * 读EXCEL文件，获取信息集合 
	 * @param planType 
     * @param fielName 
     * @return 
     */  
    public static List<Plan> readExcelFile(MultipartFile mFile, Integer planType) {  
    	List<Plan> planList = new ArrayList<Plan>();
    	//获取文件名  
    	String fileName = mFile.getOriginalFilename();
        try {  
        	// 验证文件名是否合格
            if (!validateExcel(fileName)) {  
                return null;  
            }  
            //根据文件名判断文件是2003版本还是2007版本
            boolean isExcel2003 = true;  
            if (isExcel2007(fileName)) {  
                isExcel2003 = false;  
            }  
            planList = createExcel(mFile.getInputStream(), isExcel2003, planType);  
        } catch (Exception e) {  
        	logger.error(e.getMessage()); 
        }  
        return planList;  
    } 
    
    /** 
     * 根据excel里面的内容读取客户信息 
     * @param is 输入流 
     * @param isExcel2003 excel是2003还是2007版本 
     * @param planType 
     * @return 
     * @throws IOException 
     */  
    public static List<Plan> createExcel(InputStream is, boolean isExcel2003, Integer planType) {  
    	List<Plan> planList = new ArrayList<Plan>();
    	try{  
             Workbook wb = null;  
             if (isExcel2003) {
            	 //当excel是2003时,创建excel2003  
                 wb = new HSSFWorkbook(is);  
             } else {
            	 //当excel是2007时,创建excel2007  
                 wb = new XSSFWorkbook(is);  
             }  
             //读取Excel里面客户的信息
             planList = readExcelValue(wb,planType);  
        } catch (IOException e) {  
        	logger.error(e.getMessage()); 
        }  
        return planList;  
    } 
    
    /** 
     * 读取Excel里面客户的信息 
     * @param wb 
     * @param planType 
     * @return 
     */  
    private static List<Plan> readExcelValue(Workbook wb, Integer planType) {  
    	List<Plan> planList = new ArrayList<Plan>();
    	
    	//得到第一个shell  
        Sheet sheet = wb.getSheetAt(0);  
        //得到Excel的行数  
        totalRows = sheet.getPhysicalNumberOfRows();
        //得到Excel的列数(前提是有行数)  
        if (totalRows > 5 && sheet.getRow(5) != null) {  
            totalCells = sheet.getRow(3).getPhysicalNumberOfCells();
            if(totalCells == 17 || "17".equals(totalCells)){
            	totalCells = totalCells - 1;
            }
        }  
        //循环Excel行数  
        for (int r = 5; r < totalRows; r++) {  
            Row row = sheet.getRow(r);  
            if (row == null){  
                continue;  
            }
            if(row.getCell(1).getStringCellValue() != null && !"".equals(row.getCell(1).getStringCellValue())){
            	Plan plan = new Plan();  
	            //循环Excel的列  
	            for (int c = 0; c < totalCells; c++) {  
	            	//将每一行的计划信息封装成一个 Plan 实体类
	            	if(totalCells == 14 || "14".equals(totalCells)){
	            		//项目类，临时类，日常类按天循环的计划
	            		plan = getNotDailyPlanInfo(row,plan,c); 
	            	}else if(totalCells == 16 || "16".equals(totalCells)){
	            		//日常类按周，按月循环的计划
	            		plan = getDailyPlanInfo(row,plan,c);
	            	}
	            }
	            plan.setPlanType(planType);
	            //添加到list  
	            planList.add(plan);
            }
        }  
        return planList;  
    }
    
    /**
	 * @description: 将每一行的计划信息封装成一个 Plan 实体类（日常类按周，按月循环的计划）
	 * @param  row
	 * @param  c 列数
     * @param  plan  新建的 Plan 对象
	 * @return 
	 * @throws 
	 */
	private static Plan getDailyPlanInfo(Row row, Plan plan, int c) {
		Cell cell = row.getCell(c);  
        //如果是纯数字,比如你写的是25,cell.getNumericCellValue()获得是25.0,通过截取字符串去掉.0获得25 
        if (cell.getStringCellValue() != null && !"".equals(cell.getStringCellValue())) {  
            if (c == 0) {  
                //编号
                plan.setPlanId(Integer.parseInt(cell.getStringCellValue()));
            } else if (c == 1) {  
                //计划名称
            	plan.setPlanName(cell.getStringCellValue());
            } else if (c == 2){  
            	//计划描述
            	plan.setPlanDescribe(cell.getStringCellValue());
            } else if (c == 3){  
            	//父级ID
                plan.setParentId(Integer.parseInt(cell.getStringCellValue()));
            } else if (c == 4){  
            	//负责机构
            	plan.setBlameOrganization(cell.getStringCellValue());
            } else if (c == 5){  
            	//负责部门
            	plan.setBlameSection(cell.getStringCellValue());
            } else if (c == 6){  
            	//负责人
            	plan.setBlamePerson(cell.getStringCellValue());
            } else if (c == 7){  
                //工号
            	plan.setBlamePerson(plan.getBlamePerson()+","+cell.getStringCellValue());
            } else if (c == 8){  
            	//开始时间的周数/月数
            	plan.setStartTime(cell.getStringCellValue());
            } else if (c == 9){  
            	//开始时间的周几/第几天
            	plan.setStartTimeDaily(cell.getStringCellValue());
            } else if (c == 10){  
            	//结束时间的周数/月数
            	plan.setEndTime(cell.getStringCellValue());
            } else if (c == 11){  
            	//结束时间的周几/第几天
            	plan.setEndTimeDaily(cell.getStringCellValue());
            } else if (c == 12){
            	//完成标准
            	plan.setFinishStandard(cell.getStringCellValue());
            } else if (c == 13){  
            	//考核项
            	plan.setCheckItem(cell.getStringCellValue());
            } else if (c == 14){ 
            	//考核方式
            	String checkManner = cell.getStringCellValue();
            	if(checkManner == "算入绩效" || "算入绩效".equals(checkManner))
            		plan.setCheckManner("1");
            	else if(checkManner == "单次考核" || "单次考核".equals(checkManner))
            		plan.setCheckManner("2");
            	else if(checkManner == "每天考核" || "每天考核".equals(checkManner))
            		plan.setCheckManner("3");
            	else if(checkManner == "算入绩效+单次考核" || "算入绩效+单次考核".equals(checkManner))
            		plan.setCheckManner("1,2");
            	else if(checkManner == "算入绩效+每天考核" || "算入绩效+每天考核".equals(checkManner))
            		plan.setCheckManner("1,3");
            	
            } else if (c == 15){
            	//考核金额
                plan.setPenaltyAmount(Integer.parseInt(cell.getStringCellValue()));
            }
        }
		return plan;
	}
	/**
	 * @description: 将每一行的计划信息封装成一个 Plan 实体类（项目类，临时类，日常类按天循环的计划）
	 * @param  row
	 * @param  c 列数
     * @param  plan  新建的 Plan 对象
	 * @return 
	 * @throws 
	 */
	private static Plan getNotDailyPlanInfo(Row row, Plan plan, int c) {
		Cell cell = row.getCell(c);  
        //如果是纯数字,比如你写的是25,cell.getNumericCellValue()获得是25.0,通过截取字符串去掉.0获得25 
        if (cell.getStringCellValue() != null && !"".equals(cell.getStringCellValue())) {  
            if (c == 0) {  
                //编号
                plan.setPlanId(Integer.parseInt(cell.getStringCellValue()));
            } else if (c == 1) {  
                //计划名称
            	plan.setPlanName(cell.getStringCellValue());
            } else if (c == 2){  
            	//计划描述
            	plan.setPlanDescribe(cell.getStringCellValue());
            } else if (c == 3){  
            	//父级ID
                plan.setParentId(Integer.parseInt(cell.getStringCellValue()));
            } else if (c == 4){  
            	//负责机构
            	plan.setBlameOrganization(cell.getStringCellValue());
            } else if (c == 5){  
            	//负责部门
            	plan.setBlameSection(cell.getStringCellValue());
            } else if (c == 6){  
            	//负责人
            	plan.setBlamePerson(cell.getStringCellValue());
            } else if (c == 7){  
                //工号
            	plan.setBlamePerson(plan.getBlamePerson()+","+cell.getStringCellValue());
            } else if (c == 8){  
            	//开始时间
            	plan.setStartTime(cell.getStringCellValue());
            } else if (c == 9){  
            	//结束时间
            	plan.setEndTime(cell.getStringCellValue());
            } else if (c == 10){
            	//完成标准
            	plan.setFinishStandard(cell.getStringCellValue());
            } else if (c == 11){  
            	//考核项
            	plan.setCheckItem(cell.getStringCellValue());
            } else if (c == 12){ 
            	//考核方式
            	String checkManner = cell.getStringCellValue();
            	if(checkManner == "算入绩效" || "算入绩效".equals(checkManner))
            		plan.setCheckManner("1");
            	else if(checkManner == "单次考核" || "单次考核".equals(checkManner))
            		plan.setCheckManner("2");
            	else if(checkManner == "每天考核" || "每天考核".equals(checkManner))
            		plan.setCheckManner("3");
            	else if(checkManner == "算入绩效+单次考核" || "算入绩效+单次考核".equals(checkManner))
            		plan.setCheckManner("1,2");
            	else if(checkManner == "算入绩效+每天考核" || "算入绩效+每天考核".equals(checkManner))
            		plan.setCheckManner("1,3");
            	
            } else if (c == 13){
            	//考核金额
                plan.setPenaltyAmount(Integer.parseInt(cell.getStringCellValue()));
            }
        }
		return plan;
	}
	/** 
     * 验证EXCEL文件 
     *  
     * @param filePath 
     * @return 
     */  
    public static boolean validateExcel(String filePath) {  
        if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath))) {  
            errorMsg = "文件名不是excel格式";  
            return false;  
        }  
        return true;  
    } 
    
    // @描述：是否是2003的excel，返回true是2003   
    public static boolean isExcel2003(String filePath)  {    
        return filePath.matches("^.+\\.(?i)(xls)$");    
    }    
     
    //@描述：是否是2007的excel，返回true是2007   
    public static boolean isExcel2007(String filePath)  {    
        return filePath.matches("^.+\\.(?i)(xlsx)$");    
    } 
	
}