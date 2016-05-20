package com.wsg.robot;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;


public class GetReplyFunction extends BrowserFunction {
	private static Map<String,String> handledMessage = new HashMap();
	private Browser browser;
	private String functionName ;
	
	public GetReplyFunction(Browser browser, String name) {
		super(browser, name);
		this.browser = browser;
		this.functionName = name;
	}
	
	public Object function(Object[] args) {
		String msg = (String) args[0];
		String from = (String) args[1];
		String time = (String) args[2];
		boolean inChatRoom = (boolean) args[3];
		long t = System.currentTimeMillis();
		//System.out.println(msg + "," + from + "," + time + "," + inChatRoom + "," + t);
		
		if (inChatRoom) {
			//判断是否是自己发出的信息
			//不处理群信息
			return null;
		} else {
			String key = msg+"@"+from+"@"+time;
			String send = handledMessage.get(key);
			if (send != null) {
				return null;//已经处理的不在处理
			} else {
				String reply = getReply(msg);
				handledMessage.put(key, reply);
			}
		}
		return null;
	}

	private String getReply(String msg) {
		String url = "http://www.xiaodoubi.com/simsimiapi.php?msg=" + URLEncoder.encode(msg);
		try {
			String answer = HttpUtil.doGet(url);
			return answer;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
