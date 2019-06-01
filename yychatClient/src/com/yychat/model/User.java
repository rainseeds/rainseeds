package com.yychat.model;

import java.io.Serializable;

public class User implements Serializable{
	private String userName;//成员变量
	private String passWord;
	private String userMessageType;
	//注册新用户步骤3："USER-REGISTER"和"USER-LOGIN"
	
	public String getuserMessageType() {
		return userName;
	}
	public void setuserMessageType(String userMessageType) {
		this.userName = userMessageType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;//局部变量给成员变量赋值
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	
	
}
