package com.manhui.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.manhui.model.Plan;

/**
 * @ClassName: DateUtil
 * @description: 时间控制器
 * @author:	HeJiayan
 * @date Create in 下午5:11:34 2017年10月11日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class DateUtil {
	
    /**  
     * 计算两个日期之间相差的天数  
     * @param smdate 较小的时间 
     * @param bdate  较大的时间 
     * @return 相差天数 
     * @throws ParseException  
     */    
    public static Integer daysBetween(Date smdate,Date bdate) throws ParseException    
    {    
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        smdate=sdf.parse(sdf.format(smdate));  
        bdate=sdf.parse(sdf.format(bdate));  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));           
    }    
      
	/** 
	*字符串的日期格式的计算 
	*/  
    public static Integer daysBetween(String smdate,String bdate) throws ParseException{  
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(sdf.parse(smdate));    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(sdf.parse(bdate));    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));     
    }

	/**
	 * @description: 通过模板计划时间偏移量计算生成后计划的开始时间以及结束时间
	 * @param @param planList
	 * @param @param startTime
	 * @param @return
	 * @return 
	 * @throws 
	 */
	public static Plan getPlanTime(Plan plan, String startTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
			
		try {
			calendar.setTime(sdf.parse(startTime));
	        if(plan.getTimeOffset() != null && !"".equals(plan.getTimeOffset())){
	        	calendar.add(Calendar.DATE, plan.getTimeOffset());
	            String newStartTime = sdf.format(calendar.getTime());
	            plan.setStartTime(newStartTime);
	        }    
	        if(plan.getTimeLimit() != null && !"".equals(plan.getTimeLimit())){
	        	calendar.add(Calendar.DATE, plan.getTimeLimit());
	            String newEndTime = sdf.format(calendar.getTime());
	            plan.setEndTime(newEndTime);
	        }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return plan;
	}  
}
