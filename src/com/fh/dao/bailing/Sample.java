package com.fh.dao.bailing;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;
import org.json.JSONObject;
public class Sample {
    private static final String serverURL = "http://vop.baidu.com/server_api";
    private static String token = "";     
    private static final String testFileName = "D:\\导出代码\\好的项目\\FH-WEB\\pcm\\pcss.pcm";//需要识别的音频文件
    //put your own params here
    private static final String apiKey = "cIcLYTeDv9dFV9P9mBhZw4RP";
    private static final String secretKey = "Qqi7QNpPFLsSuHFcHmwR0cmlu77fLpuv ";
    private static final String cuid = "84-EF-18-1F-5D-AB";
    public static void main(String[] args) throws Exception {
        Sample s = new Sample();
        s.shibie();
    }
    
    
    public String shibie() throws Exception{
    	 getToken();
         method1();
         method2();
         File f = new File(testFileName);
         f.delete();
		return information;
    }
    
    private static void getToken() throws Exception {
        String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" + 
            "&client_id=" + apiKey + "&client_secret=" + secretKey;
        HttpURLConnection conn = (HttpURLConnection) new URL(getTokenURL).openConnection();
        token = new JSONObject(printResponse(conn)).getString("access_token");
    }
    private static void method1() throws Exception {
        File wavFile = new File(testFileName);
        HttpURLConnection conn = (HttpURLConnection) new URL(serverURL).openConnection();
        // construct params
        JSONObject params = new JSONObject();
        params.put("format", "wav");
        params.put("rate", 16000);
        params.put("channel", "1");
        params.put("token", token);
        params.put("cuid", cuid);
        params.put("lan", "zh");
        params.put("len", wavFile.length());
        params.put("speech", DatatypeConverter.printBase64Binary(loadFile(wavFile)));
        // add request header
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        // send request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(params.toString());
        wr.flush();
        wr.close();
        printResponse(conn);
    }
    
    private static void method2() throws Exception {
        File wavFile = new File(testFileName);
        HttpURLConnection conn = (HttpURLConnection) new URL(serverURL
                + "?cuid=" + cuid + "&token=" + token).openConnection();
        // add request header
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "audio/wav; rate=16000");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        // send request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.write(loadFile(wavFile));
        wr.flush();
        wr.close();
       printResponse(conn);
    }
    static int i = 0;
    static String information;
    private static String printResponse(HttpURLConnection conn) throws Exception {
    	i++;
        if (conn.getResponseCode() != 200) {
            // request error
            return "";
        }
        InputStream is = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        String shi = new JSONObject(response.toString()).toString(4);
        judge j = new judge();
        information = j.result(shi);
        
        if(information!="") {
 
        information = information.replaceAll("[\\[\\]]", "");
        information = information.replaceAll("\"", "");
        information = information.replaceAll("，", "");
        information = information.replaceAll(" ", "");
        }
        return response.toString();
    }


    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);


        long length = file.length();
        byte[] bytes = new byte[(int) length];


        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }


        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }


        is.close();
        return bytes;
    }
    public static String getChinese(String paramValue) {
    String regex = "([\u4e00-\u9fa5]+)";
    String str = "";
    Matcher matcher = Pattern.compile(regex).matcher(paramValue);
    while (matcher.find()) {
    str+= matcher.group(0);
    }
    return str;
    }
}