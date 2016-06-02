package com.wsg.robot;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

public class GetImageReplyFunction extends BrowserFunction{
	private Logger logger = LogManager.getLogger(GetImageReplyFunction.class);
	private static Map<String,String> handledMessage = new HashMap<>();

	public GetImageReplyFunction(Browser browser, String name) {
		super(browser, name);
	}
	
	public Object function(Object[] args) {
		String msg = (String) args[0];
		String msgId = (String) args[1];
        String myself = (String) args[2];
        String sender = (String) args[3];
        boolean isRoomContact = Boolean.parseBoolean(args[4].toString());
        logger.debug(msg + "," + msgId + "," + myself + "," + sender + "," + isRoomContact);
        String send = handledMessage.get(msgId);
		if (send != null) {
			return null;//已经处理的不在处理
		} else {
			String reply = getReply(msg,sender);
			handledMessage.put(msgId, reply);
            return reply;
		}
	}
	
	private String getReply(String msg,String sender) {
		try {
			String answer = Application.replyImage(msg,sender);
			return answer;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
}
