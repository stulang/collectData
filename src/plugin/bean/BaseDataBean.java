package com.inspur.plugin.bean;

 
import com.google.gson.Gson;
import com.inspur.utils.iLog;


public class BaseDataBean {

public BaseDataBean conver(String data) {
	try {
		Gson gson = new Gson();
		return gson.fromJson(data, this.getClass());
	} catch (Exception e) {
		iLog.d("GSON", e.getMessage(), e); 
	}
	return null;

}
	
}
