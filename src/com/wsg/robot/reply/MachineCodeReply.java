package com.wsg.robot.reply;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.wsg.robot.IReply;
import com.wsg.robot.tools.HttpUtil;

public class MachineCodeReply implements IReply {
	private String url1 = "https://open.leguanzhu.net/user?username=%s&password=%s";
	private String url2 = "https://open.leguanzhu.net/psw/%s/%s?access_token=%s";
	private Logger logger = LogManager.getLogger(MachineCodeReply.class);
	private static String accessToken;
	private static long expiresIn;
	private static String username;
	private static String password;
	private static int count = 1;
	private static String configFile = "config/config.ini";

	public MachineCodeReply() {
		if (username == null) {
			Properties p = new Properties();
			try {
				InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile),"utf8");
				p.load(reader);
				username = p.getProperty("Account");
				password = p.getProperty("Password");
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public boolean checkExists(String sender, String date,String machineCode) {
		try {
			String filePath = "gamecode/" + date;
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileReader f = new FileReader(file);
			BufferedReader reader = new BufferedReader(f);
			String line = reader.readLine();
			String token = sender+":"+machineCode;
			while (line != null) {
				if (line.contains(token)) {
					reader.close();
					return true;
				}
				line = reader.readLine();
			}
			reader.close();
			f.close();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return false;
	}

	public void recordCode(String sender, String date,String machineCode) throws IOException {
		String filePath = "gamecode/" + date;
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter f = new FileWriter(file,true);
		f.write(sender+":"+machineCode+"\n");
		f.flush();
		f.close();
	}

	@Override
	public String reply(String msg, String sender) {
		Date today = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String dt_str = df.format(today);
		if (checkExists(sender, dt_str,msg)) {
			return "您已经领过游戏码了,一天只能领一次哦!";
		}
		Long now = System.currentTimeMillis();
		if (now > expiresIn) {
			this.initPeerTime();
		}
		String password = getGameCode(msg);
		try {
			recordCode(sender,dt_str,msg);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return "您本次的游戏码是:" + password + ",一天内使用有效";
	}

	private void initPeerTime() {
		try {
			String uri = String.format(url1, URLEncoder.encode(username,"utf8"),  password);
			logger.debug(uri);
			String tokenStr = HttpUtil.httpsGet(uri, null);
			JSONObject json = new JSONObject(tokenStr);
			String token = json.getString("access_token");
			int in = json.getInt("expires_in");
			if (token != null) {
				accessToken = token;
				expiresIn = System.currentTimeMillis() + 1000 * in;
			} else {
				logger.error("error when getting token :" + tokenStr);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private String getGameCode(String deviceId) {
		try {
			String str = HttpUtil.httpsGet(String.format(url2, deviceId, count, accessToken), null);
			JSONObject json = new JSONObject(str);
			JSONArray arr = json.getJSONArray("device_password");
			String password = arr.get(0).toString();
			return password;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static void main(String args[]) throws UnsupportedEncodingException {
		MachineCodeReply reply = new MachineCodeReply();
		String name = reply.username;
		String encode = "%E5%BE%AE%E6%97%B6%E5%85%89%E4%BC%A0%E5%AA%92";
		System.out.println(name + "," + URLEncoder.encode(name,"utf8").equals(encode));
	}
}
