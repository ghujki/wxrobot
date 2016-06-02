package com.wsg.robot.reply;

import com.wsg.robot.IReply;

public class TextReply implements IReply {

	@Override
	public String reply(String msg,String sender) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String args[] ) {
		String exp = "[\\w\\W]*刚刚把你添加到通讯录[\\w\\W]*";
		String str = "萌宝大赛客服-小艾刚刚把你添加到通讯录，现在可以开始聊天了。";
		System.out.println(str.matches(exp));
	}
}
