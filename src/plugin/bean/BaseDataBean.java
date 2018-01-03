package com.inspur.plugin.bean;

 
import com.google.gson.Gson;
import android.util.Log;

public class BaseDataBean {

public BaseDataBean conver(String data) {
	try {
		Gson gson = new Gson();
		return gson.fromJson(data, this.getClass());
	} catch (Exception e) {
		Log.d("GSON", e.getMessage(), e); 
	}
	return null;

}
	
}
