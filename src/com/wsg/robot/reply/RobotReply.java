package com.wsg.robot.reply;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wsg.robot.IReply;
import com.wsg.robot.tools.HttpRequester;
import com.wsg.robot.tools.HttpRespons;

public class RobotReply implements IReply {
	private static String url = "http://www.xiaodoubi.com/simsimiapi.php";
	private Logger logger = LogManager.getLogger(RobotReply.class);
	@Override
	public String reply(String msg,String sender) {
        try {
            Map<String ,String> params = new HashMap<>();
            params.put("msg",URLEncoder.encode(msg,"utf-8"));
            HttpRequester request = new HttpRequester();
            request.setDefaultContentEncoding("utf-8");
            HttpRespons hr = request.sendGet(url,params);
            return new String(hr.getContent().getBytes(),"utf-8");
        } catch (Exception e) {
        	logger.error(e.getMessage(),e);
        }
        return null;
	}
}
