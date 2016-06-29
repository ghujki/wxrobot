package com.microTimes.wechatRobot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class RobotConfig {
	private boolean autoReply;
	private static boolean initialized = false;

	private List <ReplyConfig> replyConfigs = new ArrayList<>();
	private static String filePath = "resources\\robot.conf";
	private static RobotConfig config;
	private static int delay;

	public static boolean isAutoReply() {
		return config.autoReply;
	}
	
	public static void setAutoReply(boolean autoReply) {
		config.autoReply = autoReply;
	}
	
	public static List <ReplyConfig> getConfigs () {
		return config.replyConfigs;
	}
	
	public static void addConfig (ReplyConfig cf) {
		config.getConfigs().add(cf);
	}
	
	public static void removeConfig (ReplyConfig cf) {
		config.getConfigs().remove(config);
	}
	
	public static void setDelay (int i) {
		delay = i;
	}
	
	public static int getDelay() {
		return delay;
	}
	
	private RobotConfig () {}
	
	public static ReplyConfig getConfig (String msg) {
		for (ReplyConfig config : getConfigs()) {
			if (msg.matches(config.getKeywords())) {
				return config;
			}
		}
		return null;
	}
	public static void init () throws IOException {
		if (initialized) {
			return ;
		}
		if (config == null) {
			config = new RobotConfig();
		}
		File file = new File (filePath);
		BufferedReader in = new BufferedReader(new FileReader(file));
		String str;
		StringBuffer sb = new StringBuffer();
		while ((str = in.readLine()) != null) {
			sb.append(str);
		}
		in.close();
		if (sb.length() > 0) {
			JSONObject obj = new JSONObject(sb.toString());
			config.autoReply = obj.getBoolean("autoReply");
			config.delay = obj.getInt("delay");
			JSONArray arr = obj.getJSONArray("replyConfigs");
			for (int i = 0; i < arr.length(); i++ ) {
				JSONObject jsonConfigObj = arr.getJSONObject(i);
				ReplyConfig config = new ReplyConfig();
				config.setConfigName(jsonConfigObj.getString("configName"));
				config.setKeywords(jsonConfigObj.getString("keywords"));
				config.setReplyText(jsonConfigObj.getString("replyText"));
				config.setReplyPicture(jsonConfigObj.getString("replyPicture"));
				config.setPicturePath(jsonConfigObj.getString("picturePath"));
				addConfig(config);
			}
		}
		initialized = true;
	}
	
	public static void save () throws IOException {
		File file = new File (filePath);
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		JSONObject obj = new JSONObject();
		obj.put("autoReply", config.autoReply);
		obj.put("delay", config.delay);
		JSONArray arr = new JSONArray();
		for (ReplyConfig r : config.replyConfigs) {
			JSONObject o = new JSONObject();
			o.put("configName", r.getConfigName());
			o.put("keywords", r.getKeywords());
			o.put("replyText", r.getReplyText());
			o.put("replyPicture", r.getReplyPicture());
			o.put("picturePath", r.getPicturePath());
			arr.put(o);
		}
		obj.put("replyConfigs", arr);
		writer.write(obj.toString());
		writer.flush();
		writer.close();
	}
}
