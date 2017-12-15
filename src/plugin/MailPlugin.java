package com.inspur.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.inspur.moa.plugin.ImpPlugin;
import com.inspur.plugin.bean.MailData;
import com.inspur.plugin.bean.MultiMailsender;
import com.inspur.plugin.bean.MultiMailsender.MultiMailSenderInfo;
import com.inspur.utils.FileUtils;
import com.inspur.utils.ZipFileUtils;
import com.inspur.utils.iLog;

public class MailPlugin extends ImpPlugin {

	private static final String TAG = "MailPlugin";

	@Override
	public void execute(String action, JSONObject paramsObject) {
 		if("collectData".equals(action)){
 			collectData(paramsObject.toString());
		}
	}

	MailData maildata;
	String path;
	
	
	//
	public void collectData(String json) {
		maildata = new MailData();
		maildata = (MailData) maildata.conver(json);
 		final MultiMailSenderInfo mailInfo = maildata.getMailInfo();
		path = context.getFilesDir().getParent();
		try {
			File[] files  ;
			if(maildata.dataFileList.size()==0){
			files = new File[6];
			files[0] = new File(path + "/app_database");
			files[1] = new File(path + "/databases");
			files[2] = new File(path + "/app_cache");
			files[3] = new File(path + "/files");
			files[4] = new File(path + "/shared_prefs");
			files[5] = new File(path + "/app_webview");
			}else{
				int m=maildata.dataFileList.size();
				int n=maildata.SDFileList.size();
				files = new File[m+n];
				int i=0;
				while(m>0){
					files[i++]=new File(path +maildata.dataFileList.get(--m)); 
				}
				while(n>0){
					path   = FileUtils.getSDPath();
					files[i++]=new File(path +maildata.SDFileList.get(--n)); ;
				}
			}
			String res=ZipFileUtils.zip(files, path + "/data.zip");
			if(res!=null){
				jsCallback("0");
				iLog.e(TAG, res);
				return;
			}
			List<File> fileList = new ArrayList<File>();
			fileList.add(new File(path + "/data.zip"));
			mailInfo.setFileList(fileList);
		} catch (Exception e) {
			iLog.e("sendMail", e.getMessage(), e);
			e.printStackTrace();
			jsCallback("0");
			return;
		}

		new Thread() {
			@Override
			public void run() {
				// 这个类主要来发送邮件
				MultiMailsender sms = new MultiMailsender();
				boolean isComplete = sms.sendTextMail(mailInfo);// 发送文体格式
				Message msg = new Message();

				if (isComplete) {
					// int n =fileList.size();
					// while (n>0){
					// fileList.get(n-1).delete();
					// n--;
					// }
					FileUtils.delFile(path + "/data.zip");
					msg.what = 1;
				} else {
					msg.what = 0;
				}
				handler.sendMessage(msg);
			}
		}.start();

	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			jsCallback(msg.what + "");
		}
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

	}
}
