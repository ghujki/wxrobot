package com.microTimes.wechatRobot;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import org.eclipse.swt.browser.Browser;
import org.json.JSONObject;

public class MessageAPI {
	private static Browser browser;
	
	public static void setBrowser (Browser b) {
		if (browser == null) {
			browser = b;
		}
	}

	public static String sendMessage(String message,String toUser) {
		String result  = null;
		try {
			initEnv();
			result = (String) browser.evaluate("return window.sendTextMessage('" + message + "','" + toUser + "');");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String uploadFile(String filepath,String toUser) {
		String result = null;
		try {
			File file = new File(filepath);
			String md5 = getMd5ByFile(file);
			if (!file.exists() || !file.isFile()) {
				throw new IOException("文件不存在");
			}
			initEnv();
			browser.execute("var $scope1 = angular.element('.chat_item').scope();");
			Double uin = (Double) browser.evaluate("return $scope1.account.Uin;");
			String sid = (String) browser.evaluate("return getCookie(\"wxsid\");");
			String skey = (String) browser.evaluate("return $scope1.account.HeadImgUrl.substring($scope1.account.HeadImgUrl.lastIndexOf(\"=\") + 1);");
			String deviceId = (String) browser.evaluate("return getDeviceID();");
			String localId = (String) browser.evaluate("return getMsgId();");
			String fromUser = (String) browser.evaluate("return $scope1.account.UserName;");
			String webticket = (String) browser.evaluate("return getCookie('webwx_data_ticket');");
			String apiPath = (String) browser.evaluate("return $scope1.CONF.API_webwxuploadmedia;");
			URL url = new URL(apiPath+"?f=json");
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("POST");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.73 Safari/537.36");
			String boundary = "----WebKitFormBoundary" + getRandomString(16);
			con.setRequestProperty("Content-Type", "multipart/form-data; boundary="+ boundary);
			
			String requestString = "{\"UploadType\":2,\"BaseRequest\":{\"Uin\":" + uin.intValue() + ",\"Sid\":\"" + sid +  "\",\"Skey\":\"" + 
					skey + "\",\"DeviceID\":\"" + deviceId + "\"},\"ClientMediaId\":" + localId + ",\"TotalLen\":" + file.length() + 
					",\"StartPos\":0,\"DataLen\":"+ file.length() +",\"MediaType\":4,\"FromUserName\":\"" + fromUser + "\",\"ToUserName\":\"" + toUser + "\",\"FileMd5\":\"" + md5 + "\"}";
			StringBuilder sb = new StringBuilder();
			sb.append("--").append(boundary).append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"id\"\r\n\r\n");
			sb.append("WU_FILE_0\r\n");
			sb.append("--").append(boundary).append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"name\"\r\n\r\n");
			sb.append(file.getName()+ "\r\n");//debug
			sb.append("--").append(boundary).append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"type\"\r\n\r\n");
			sb.append("image/jpeg\r\n");
			sb.append("--").append(boundary).append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"lastModifiedDate\"\r\n\r\n");
			sb.append(getGMT(new Date(file.lastModified())) + "\r\n");//debug
			sb.append("--").append(boundary).append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"size\"\r\n\r\n");
			sb.append(file.length() + "\r\n");
			sb.append("--").append(boundary).append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"mediatype\"\r\n\r\n");
			sb.append("pic\r\n");
			sb.append("--").append(boundary).append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"uploadmediarequest\"\r\n\r\n");
			sb.append(requestString + "\r\n");//debug
			sb.append("--").append(boundary).append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"webwx_data_ticket\"\r\n\r\n");
			sb.append(webticket + "\r\n");
			sb.append("--").append(boundary).append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"pass_ticket\"\r\n\r\n");
			sb.append("undefined\r\n");
			sb.append("--").append(boundary).append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"filename\"; filename=\"" + file.getName() + "\"\r\n");
			sb.append("Content-Type: image/jpeg\r\n\r\n");
			
			byte[] head = sb.toString().getBytes("utf-8");
			OutputStream out = new DataOutputStream(con.getOutputStream());
			out.write(head);
			
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
			}
			in.close();
			byte[] foot = ("\r\n--" + boundary + "--\r\n").getBytes("utf-8");
			out.write(foot);

			out.flush();
			out.close();
			
			StringBuffer buffer = new StringBuffer();
			BufferedReader reader = null;
			
			try {
				// 定义BufferedReader输入流来读取URL的响应
				reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					// System.out.println(line);
					buffer.append(line);
				}
				if (result == null) {
					result = buffer.toString();
				}
			} catch (IOException e) {
				System.out.println("发送POST请求出现异常！" + e);
				e.printStackTrace();
				throw new IOException("数据读取异常");
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(result);
		JSONObject obj = new JSONObject(result);
		return obj.getString("MediaId");
	}
	
	public static String getMd5ByFile(File file) throws FileNotFoundException {
		String value = null;
		FileInputStream in = new FileInputStream(file);
		try {
			MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(byteBuffer);
			BigInteger bi = new BigInteger(1, md5.digest());
			value = bi.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	} 
	
	public static String getGMT(Date dateCST) { 
		   DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z+0800 (中国标准时间)", Locale.ENGLISH); 
		   df.setTimeZone(TimeZone.getTimeZone("GMT")); // modify Time Zone. 
		   return(df.format(dateCST)); 
	} 
	
	private static String getRandomString(int length) { //length表示生成字符串的长度
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";   
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();   
	    for (int i = 0; i < length; i++) {   
	        int number = random.nextInt(base.length());   
	        sb.append(base.charAt(number));   
	    }   
	    return sb.toString();   
	}
	
	public static String sendImageMessage(String mediaId,String toUser) {
		String result  = null;
		try {
			initEnv();
			result = (String) browser.evaluate("return window.sendImageMessage('" + mediaId + "','" + toUser + "');");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void initEnv() throws IOException {
		boolean initialized = (boolean) browser.evaluate("return (typeof(now) == 'function');");
		if (!initialized) {
			File f = new File("resources\\test.js");
			FileReader r = new FileReader(f);
			BufferedReader reader = new BufferedReader(r);
			String line = null;
			StringBuffer buffer = new StringBuffer();
			String result = null;
			while ((line = reader.readLine()) != null) {
				// System.out.println(line);
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
			reader.close();
			browser.execute(result);
		}
	}
}
