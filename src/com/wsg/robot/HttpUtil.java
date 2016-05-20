package com.wsg.robot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class HttpUtil {
	//向服务器发送get请求
    public static String doGet(String uri){  
        try {  
        	URL url = new URL(uri);
        	
            HttpURLConnection uRLConnection = (HttpURLConnection)url.openConnection();  
            uRLConnection.setRequestProperty("Content-Type", "text/xml; charset=utf8"); 
            uRLConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"); 
            uRLConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
            uRLConnection.setRequestProperty("DNT", "1");
            uRLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
            uRLConnection.setRequestProperty("Connection", "keep-alive");
            uRLConnection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
            uRLConnection.setRequestProperty("Upgrade-Insecure-Requests","1");
            InputStream is = uRLConnection.getInputStream();  
            BufferedReader br = new BufferedReader(new InputStreamReader(is));  
            String response = "";  
            String readLine = null;  
            while((readLine =br.readLine()) != null){  
                response = response + readLine;  
            }  
            is.close();  
            br.close();  
            uRLConnection.disconnect();  
            return response;  
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
            return null;  
        } catch (IOException e) {  
            e.printStackTrace();  
            return null;  
        }  
    }   
    
	
	public static void main(String args[]) {
		String msg = "who is that";
		String url = "http://www.xiaodoubi.com/simsimiapi.php?msg=" + URLEncoder.encode(msg);
		try {
			String answer = HttpUtil.doGet(url);
			System.out.println(answer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
