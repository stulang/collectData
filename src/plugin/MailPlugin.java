package com.inspur.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.inspur.plugin.bean.MailData;
import com.inspur.plugin.bean.MultiMailsender;
import com.inspur.plugin.bean.MultiMailsender.MultiMailSenderInfo;
import com.inspur.utils.FileUtils;
import com.inspur.utils.ZipFileUtils;

public class MailPlugin extends CordovaPlugin {

	private static final String TAG = "MailPlugin";

  @Override
  public void initialize(final CordovaInterface cordova, final CordovaWebView webView) {
    super.initialize(cordova, webView);
    Log.d("MailPlugin", "initialize"  );
   }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onResume(boolean multitasking) {
    super.onResume(multitasking);
  }

  @Override
  public void onStop() {

    super.onStop();
  }


  @Override
  public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
    boolean cmdProcessed = true;
    if ("collectData".equals(action)) {
      collectData(callbackContext, args);
     } else {
      cmdProcessed = false;
    }

    return cmdProcessed;
  }

	MailData maildata;
	String path;
  CallbackContext callback;

	public void collectData(CallbackContext callbackContext, CordovaArgs args) {
    callback=  callbackContext;
		maildata = new MailData();
		maildata = (MailData) maildata.conver(args.optJSONObject(0).toString());
 		final MultiMailSenderInfo mailInfo = maildata.getMailInfo();
		path = this.cordova.getActivity().getFilesDir().getParent();
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
        if (callbackContext != null) {
           callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION,"压缩文件失败"));
        }
				Log.e(TAG, res);
				return;
			}
			List<File> fileList = new ArrayList<File>();
			fileList.add(new File(path + "/data.zip"));
			mailInfo.setFileList(fileList);
		} catch (Exception e) {
			Log.e("sendMail", e.getMessage(), e);
			e.printStackTrace();
      if (callbackContext != null) {
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION,"压缩文件失败"));
      }			return;
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
      if (callback != null) {
        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK,msg.what));
      }		}
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

	}
}
