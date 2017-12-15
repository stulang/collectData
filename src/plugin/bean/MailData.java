package com.inspur.plugin.bean;

import java.util.ArrayList;
import java.util.List;

import com.inspur.plugin.bean.MultiMailsender.MultiMailSenderInfo;
 
public class MailData extends BaseDataBean{
 MultiMailSenderInfo mailInfo; 
 public List<String > dataFileList=new ArrayList<String >();
 public List<String > SDFileList=new ArrayList<String >();
public MultiMailSenderInfo getMailInfo() {
	return mailInfo;
}
public void setMailInfo(MultiMailSenderInfo mailInfo) {
	this.mailInfo = mailInfo;
}

}
