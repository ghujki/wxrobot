package com.microTimes.wechatRobot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Display;
import org.json.JSONObject;


public class NotifyMessageFunction extends BrowserFunction {
	private Logger logger = LogManager.getLogger(NotifyMessageFunction.class);
	private List <String> messages = new ArrayList<String>();
	private Browser browser;
	
	public NotifyMessageFunction(Browser browser, String name) {
		super(browser, name);
		this.browser = browser;
	}

	@Override
	public Object function(Object[] args) {
		String msg = (String) args[0];
		String msgId = (String) args[1];
        String myself = (String) args[2];
        final String sender = (String) args[3];
        boolean isRoomContact = Boolean.parseBoolean(args[4].toString());
        System.out.println(msg + "," + msgId + "," + myself + "," + sender + "," + isRoomContact);
        
        //判断是否已经处理
        if (!messages.contains(msgId) && RobotConfig.isAutoReply()) {
        	final ReplyConfig config = RobotConfig.getConfig(msg);
        	if (config != null) {
        		if (config.getReplyText() != null && config.getReplyText().length() > 0) {
        			browser.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(RobotConfig.getDelay() * 1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							MessageAPI.setBrowser(browser);
							String message = MessageAPI.sendMessage(config.getReplyText(), sender);
							if (message == null) {return ;}
							JSONObject obj = new JSONObject(message);
							String msgId = obj.getString("MsgID");
							if (msgId.length() == 0) {
								MainApplication.showMessage("发送消息出错");
							}
						}
        			});
        		}
        		
				if (config.getReplyPicture() != null && config.getReplyPicture().length() > 0) {
					browser.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(RobotConfig.getDelay() * 1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							MessageAPI.setBrowser(browser);
							String message = MessageAPI.sendImageMessage(config.getReplyPicture(), sender);
							if (message == null) {return;}
							JSONObject obj = new JSONObject(message);
							String msgId = obj.getString("MsgID");
							if (msgId.length() == 0) {
								// 重新获得
								String mediaId = MessageAPI.uploadFile(config.getPicturePath(), "filehelper");
								config.setReplyPicture(mediaId);
								message = MessageAPI.sendImageMessage(config.getReplyPicture(), sender);
								obj = new JSONObject(message);
								msgId = obj.getString("MsgID");
								if (msgId.length() == 0) {
									MainApplication.showMessage("发送消息出错");
								}
							}
						}
					});

				}
        	}
        	
        	messages.add(msgId);
        }
        return 0;
	}
	
	
}
