package com.wsg.robot.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


public class HttpUtil {

    public static String doHttpGet(String msg) {
        String url = "http://www.xiaodoubi.com/simsimiapi.php";
        Map<String ,String> params = new HashMap<>();
        params.put("msg",URLEncoder.encode(msg));
        try {
            HttpRequester request = new HttpRequester();
            request.setDefaultContentEncoding("utf-8");
            HttpRespons hr = request.sendGet(url,params);
            return hr.getContent();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String httpGet(String url,Map params) throws IOException {
    	HttpRequester request = new HttpRequester();
        request.setDefaultContentEncoding("utf-8");
        HttpRespons hr = request.sendGet(url,params);
        return hr.getContent();
    }
    
    public static void main(String args[]) throws UnsupportedEncodingException {
    	String url = "https://open.leguanzhu.net/user?username=" +URLEncoder.encode("微时光传媒", "utf-8")+ "&password=123456";
    	Map<String ,String> params = new HashMap<>();
    
    	
		try {
			String content = HttpUtil.httpsGet(url, params);
			System.out.println(content);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    
    public static String httpsGet(String url,Map<String,String> params) throws Exception {
    	HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                System.out.println("Warning: URL Host: " + urlHostName + " vs. "
                                   + session.getPeerHost());
                return true;
            }
        };
        trustAllHttpsCertificates();
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
    	URL uri = new URL(url);
    	HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
    	connection.setRequestMethod("GET");
    	connection.setDoOutput(true);
    	connection.setDoInput(true);
    	connection.setUseCaches(false);
    	if (params != null) {
    	for (String key : params.keySet()) {
    		connection.addRequestProperty(key, params.get(key));
    	}}
    	
		final BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		String line;
		final StringBuffer stringBuffer = new StringBuffer(255);

		synchronized (stringBuffer) {
			while ((line = in.readLine()) != null) {
				stringBuffer.append(line);
				stringBuffer.append("\n");
			}
			return stringBuffer.toString();
		}
    }
    
    private static void trustAllHttpsCertificates() throws Exception {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
				.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
				.getSocketFactory());
	}

	static class miTM implements javax.net.ssl.TrustManager,
			javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}
}
