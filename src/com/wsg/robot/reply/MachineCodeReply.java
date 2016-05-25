package com.wsg.robot.reply;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
				p.load(new FileReader(configFile));
				username = p.getProperty("Account");
				password = p.getProperty("Password");
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public boolean checkExists(String sender, String date) {
		try {
			String filePath = "gamecode/" + date;
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileReader f = new FileReader(file);
			BufferedReader reader = new BufferedReader(f);
			String line = reader.readLine();
			while (line != null) {
				line = reader.readLine();
				if (line.contains(sender)) {
					reader.close();
					return true;
				}
			}
			reader.close();
			f.close();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return false;
	}

	public void recordCode(String sender, String date) throws IOException {
		String filePath = "gamecode/" + date;
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter f = new FileWriter(file);
		BufferedWriter write = new BufferedWriter(f);

		write.newLine();
		write.write(sender);
		write.close();
		f.close();
	}

	@Override
	public String reply(String msg, String sender) {
		Date today = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String dt_str = df.format(today);
		if (checkExists(sender, dt_str)) {
			return "您已经领过游戏码了,一天只能领一次哦!";
		}
		Long now = System.currentTimeMillis();
		if (now > expiresIn) {
			this.initPeerTime();
		}
		String password = getGameCode(msg);
		return "您本次的游戏码是:" + password + ",一天内使用有效";
	}

	private void initPeerTime() {
		try {
			String uri = String.format(url1, URLEncoder.encode(username, "utf-8"), password);
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
}
