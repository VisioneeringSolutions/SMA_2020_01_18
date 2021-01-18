package com.vspl.music.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.tools.ant.types.CommandlineJava.SysProperties;

import com.vspl.music.model.eo.EOObject;
import com.vspl.music.util.DataUtil;
import com.vspl.music.util.DateUtil;

public class SlotCreation extends TimerTask{
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//System.out.println("Timer task started at:" + new Date());
		completeTask();
		//System.out.println("Timer task finished at:" + new Date());
	}
	
	public synchronized void completeTask(){
		try{
			HashMap<String, Object> timeslotData = DataUtil.factory().timeSlotMap;
			String str = "SELECT max(date) FROM eotimeslot";
			List<HashMap<String, Object>> maxDateList = DBServices.getNativeQueryResult(str);
			
			if(maxDateList != null && maxDateList.size() > 0){
				if(maxDateList.get(0).get("max") != null){
					//if data base has entry
					String sourceDate = maxDateList.get(0).get("max")+"";
					int dateDiff = DateUtil.getDiffOfTwoDate(sourceDate, DateUtil.formatedCurrentDate());
					if(dateDiff <= 7){
						for(int m = 0; m < 1; m++){
							for(Map.Entry<String, Object>entry1 : timeslotData.entrySet()) {
								Object valueMap = entry1.getValue();
							        
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
								Date myDate = format.parse(sourceDate);
								myDate = DateUtil.addDay(myDate);
								SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
								String dateValue = formatDate.format(myDate);
								sourceDate = "";
								sourceDate = dateValue;
								String day = DateUtil.getDayNameFromDate(myDate);
								
								HashMap<String, Object> timeSlotMap = new HashMap<>();
								for(Map.Entry<String, Object>entry2 : ((HashMap<String,Object>) valueMap).entrySet()) {
									if(((HashMap<String,Object>)entry2.getValue()).get("primaryKey") == null){
										String status = (String) ((HashMap<String,Object>)entry2.getValue()).get("status");
										String timeString = (String) entry2.getKey(); 
									    String[] timeStringArray = timeString.split("-"); 
									    String startTime = timeStringArray[0];
									    String endTime = timeStringArray[1];
									    timeSlotMap.put("day", day);
									    timeSlotMap.put("date", dateValue);
									    timeSlotMap.put("startTime", startTime);
									    timeSlotMap.put("endTime", endTime);
									    timeSlotMap.put("status", status);
									    timeSlotMap.put("className", "EOTimeSlot");
									    timeSlotMap.put("createdDate", DateUtil.formatedCurrentDate());
									    //System.out.println("timeSlotMap::"+timeSlotMap);
									    DBServices.create(EOObject.createObject(timeSlotMap));
						
									}
								}	
							}
						}
					}
				}else{
					//if data base has no entry
					
					String sourceDate = DateUtil.dateOfSunday(DateUtil.formatedCurrentDate());
					for(int m = 0; m < 1; m++){
						for(Map.Entry<String, Object>entry1 : timeslotData.entrySet()) {
							Object valueMap = entry1.getValue();
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
							Date myDate = format.parse(sourceDate);
							myDate = DateUtil.addDay(myDate);
							SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
							String dateValue = formatDate.format(myDate);
							sourceDate = "";
							sourceDate = dateValue;
							String day = DateUtil.getDayNameFromDate(myDate);
							
							HashMap<String, Object> timeSlotMap = new HashMap<>();
							for(Map.Entry<String, Object>entry2 : ((HashMap<String,Object>) valueMap).entrySet()) {
								if(((HashMap<String,Object>)entry2.getValue()).get("primaryKey") == null){
									String status = (String) ((HashMap<String,Object>)entry2.getValue()).get("status");
									String timeString = (String) entry2.getKey(); 
								    String[] timeStringArray = timeString.split("-"); 
								    String startTime = timeStringArray[0];
								    String endTime = timeStringArray[1];
								    timeSlotMap.put("day", day);
								    timeSlotMap.put("date", dateValue);
								    timeSlotMap.put("startTime", startTime);
								    timeSlotMap.put("endTime", endTime);
								    timeSlotMap.put("status", status);
								    timeSlotMap.put("className", "EOTimeSlot");
								    timeSlotMap.put("createdDate", DateUtil.formatedCurrentDate());
								    //System.out.println("timeSlotMap::"+timeSlotMap);
								    DBServices.create(EOObject.createObject(timeSlotMap));
					
								}
							}	
						}
					}
				}
			}else{
				System.out.println("else");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
