package com.cgcg.context.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;

/**
 * http请求类
 * @author xujinbang
 * @date 2019-8-27
 */
@Component
public class HttpUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    private static int socketTimeout = 10000;// 连接超时时间，默认10秒
    private static int connectTimeout = 30000;// 传输超时时间，默认30秒
    private static RequestConfig requestConfig;// 请求器的配置
    private static CloseableHttpClient httpClient;// HTTP请求器
    
    public static final String UTF8 = "UTF-8";
    public final static String APPLICATION_JSON = "application/json";
    public final static String APPLICATION_FOEM_URLENCODED = "application/x-www-form-urlencoded";

    public static String post(String urlString, String context) {
        return post(urlString, context, UTF8, null);
    }

    public static String post(String urlString, String context, boolean isJson) {
        if(isJson) {
            return post(urlString, context, UTF8, APPLICATION_JSON);
        } else {
            return post(urlString, context, UTF8, null);
        }
    }
    
    public static String post(String urlString, String context, String charset) {
        return post(urlString, context, charset, null);
    }

    public static String post(String urlString, String context, String charset, String contentType) {

        log.info("\n请求地址>>{}\n请求报文>>{}", urlString, context);
        HttpURLConnection httpURLConnection = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            if (StringUtils.equals(APPLICATION_JSON, contentType)) {
                httpURLConnection.setRequestProperty("Content-Type", APPLICATION_JSON);
                httpURLConnection.setRequestProperty("Accept-Charset", charset);
            } else if (StringUtils.equals(APPLICATION_FOEM_URLENCODED, contentType)) {
                httpURLConnection.setUseCaches(false);  
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            } else {
                httpURLConnection.setRequestProperty("contentType", charset);
            }
            out = new PrintWriter(httpURLConnection.getOutputStream());
            out.write(context);
            out.flush();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuffer content = new StringBuffer();
                String tempStr = null;
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), charset));
                while ((tempStr = in.readLine()) != null) {
                    content.append(tempStr);
                }

                log.info("\n响应报文>>{}", content.toString());
                return content.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Http Post Exception:", e);
            return null;
        } finally {
            if (out != null) {
                out.close();
            }
            httpURLConnection.disconnect();
        }
    }

    /**
     * post请求。带证书
     *
     * @param urlString
     * @param context
     * @param key
     * @param path
     * @return
     */
    public static String postData(String urlString, String context, String key, String path) {
        // 加载证书
        try {
            initCert(key, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("\n请求地址>>{}\n请求报文>>{}", urlString, context);
        HttpPost httpPost = new HttpPost(urlString);
        String result = null;

        StringEntity postEntity = new StringEntity(context, "UTF-8");
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.setEntity(postEntity);
        // 根据默认超时限制初始化requestConfig
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout)
                .build();

        httpPost.setConfig(requestConfig);
        try {
            HttpResponse response = null;
            try {
                response = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
            }
            HttpEntity entity = response.getEntity();
            try {
                result = EntityUtils.toString(entity, "UTF-8");
                log.info("\n响应报文>>{}", result);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            httpPost.abort();
        }
        return result;
    }

    /**
     * 加载证书
     *
     * @throws Exception
     */
    private static void initCert(String key, String path) throws Exception {
        // 指定读取证书格式为PKCS12
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        // 读取本机存放的PKCS12证书文件
        FileInputStream instream = new FileInputStream(new File(path));
        try {
            // 指定PKCS12的密码(商户ID)
            keyStore.load(instream, key.toCharArray());
        } finally {
            instream.close();
        }
        SSLContext sslcontext = SSLContexts
                .custom()
                .loadKeyMaterial(keyStore, key.toCharArray())
                .build();
        // 指定TLS版本
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext, new String[]{"TLSv1"}, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        // 设置httpclient的SSLSocketFactory
        httpClient = HttpClients
                .custom()
                .setSSLSocketFactory(sslsf)
                .build();
    }
}
