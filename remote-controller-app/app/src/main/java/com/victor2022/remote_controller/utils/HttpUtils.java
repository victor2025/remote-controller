package com.victor2022.remote_controller.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * @author wangfei
 * <p>
 * 以同步方式发送Http请求
 */
public class HttpUtils {

    public static final String HTTP_PREFIX = "http://";

    public static String httpGet(String urlStr, int connTimeout, int readTimeout){
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(urlStr);
            // 1，得到HttpURLConnection 实例
            connection = (HttpURLConnection) url.openConnection();
            // 2，设置请求方法
            connection.setRequestMethod("GET");
            // 3，自由定制:连接超时、读取超时等
            connection.setConnectTimeout(connTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            // 4,向服务器发送数据
            // 5,得到服务器返回数据
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String httpPost(String address, Map<String,String> params, int connTimeout, int readTimeout){
        URL url = null;
        HttpURLConnection connection = null;

        try {
            url = new URL(address);
            // 1，得到HttpURLConnection 实例
            connection = (HttpURLConnection) url.openConnection();
            // 2，设置请求方法
            connection.setRequestMethod("POST");
            // 3，自由定制:连接超时、读取超时等
            if(params!=null){
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    connection.addRequestProperty(entry.getKey(),entry.getValue());
                }
            }
            connection.setConnectTimeout(connTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            // 4,向服务器发送数据
            // 5,得到服务器返回数据
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
