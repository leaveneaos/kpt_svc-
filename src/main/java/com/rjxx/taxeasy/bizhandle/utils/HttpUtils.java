package com.rjxx.taxeasy.bizhandle.utils;

import com.rjxx.utils.SignUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;


/**
 *@ClassName HttpUtils
 *@Description TODO
 *@Author xlm
 *@Date 2017/7/21.
 *@Version 1.0
 **/
@Service
public class HttpUtils {

    private  static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 调用2.0接口开票
     * @param QueryData
     * @param AppId
     * @param key
     * @return
     */
    public static String HttpUrlPost(String QueryData,String AppId,String key){
        String result="";
        try {
            logger.info("----------发送的报文------"+QueryData);
            JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
            Client client = dcf.createClient(URLUtils.WS_URL);
            String methodName = "UploadOrderData";
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = new HTTPClientPolicy();
            /**
             * 连接超时时间
             */
            policy.setConnectionTimeout(180000);
            /**
             * 请求超时时间.
             */
            policy.setReceiveTimeout(180000);
            conduit.setClient(policy);
            String sign= SignUtils.getSign(QueryData,key);
            Object[] objects = client.invoke(methodName, AppId, sign, "01", QueryData);
            /**
             * 输出调用结果
             */
            result = objects[0].toString();
            logger.info("----------接收返回值------"+result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String HttpUrlWebService(String QueryData,String AppId,String key,String kpff){
        String result="";
        try {
            logger.info("----------发送的报文------"+QueryData);
            JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
            Client client = dcf.createClient(URLUtils.WS_URL);
            String methodName = "UploadOrderData";
            String sign= SignUtils.getSign(QueryData,key);
            Object[] objects = client.invoke(methodName, AppId, sign, kpff, QueryData);
            /**
             * 输出调用结果
             */
            result = objects[0].toString();
            logger.info("----------接收返回值------"+result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 请求超时时间
     */
    static int socketTimeout = 180*1000;
    /**
     * 传输超时时间
     */
    static int connectTimeout = 180*1000;


    /**
     * 使用SOAP1.1发送消息
     *
     * @param postUrl
     * @param soapXml
     * @param soapAction
     * @return
     */
    public static String doPostSoap1_1(String postUrl, String soapXml,
                                       String soapAction,String username,String password) {
        String retStr = "";
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(postUrl);
        //  设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);
        try {
            httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
            httpPost.setHeader("SOAPAction", soapAction);
            String authString = username + ":" + password;
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            httpPost.addHeader("Authorization",  "Basic " + authStringEnc);
            StringEntity data = new StringEntity(soapXml,
                    Charset.forName("UTF-8"));
            httpPost.setEntity(data);
            CloseableHttpResponse response = closeableHttpClient
                    .execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                // 打印响应内容
                retStr = EntityUtils.toString(httpEntity, "UTF-8");
                //System.out.println("response:" + retStr);
            }
            //释放资源
            closeableHttpClient.close();
        } catch (Exception e) {
            System.out.println("exception in doPostSoap1_1" + e);
        }
        return retStr;
    }



    /**
     * 使用SOAP1.2发送消息
     *
     * @param postUrl
     * @param soapXml
     * @param soapAction
     * @return
     */
    public static String doPostSoap1_2(String postUrl, String soapXml,
                                       String soapAction,String username,String password) {
        String retStr = "";
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(postUrl);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);
        try {
            httpPost.setHeader("Content-Type",
                    "application/soap+xml;charset=UTF-8");
            httpPost.setHeader("SOAPAction", soapAction);
            String authString = username + ":" + password;
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            httpPost.addHeader("Authorization",  "Basic " + authStringEnc);
            StringEntity data = new StringEntity(soapXml,
                    Charset.forName("UTF-8"));
            httpPost.setEntity(data);
            CloseableHttpResponse response = closeableHttpClient
                    .execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                // 打印响应内容
                retStr = EntityUtils.toString(httpEntity, "UTF-8");
                System.out.println("response:" + retStr);
            }
            // 释放资源
            closeableHttpClient.close();
        } catch (Exception e) {
            System.out.println("exception in doPostSoap1_2" + e);
        }
        return retStr;
    }

    /**
     * .net webService
     * @param url
     * @param methodName
     * @param QueryData
     * @param AppId
     * @param key
     * @return
     */
    public static String netWebService (String url,String methodName,String QueryData,String AppId,String key){
        String result="";
        try {
            logger.info("----------发送的报文------"+QueryData);
            JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
            Client client = dcf.createClient(url);
            String sign= SignUtils.getSign(QueryData,key);
            Object[] objects = client.invoke(methodName, AppId,QueryData,sign);
            //输出调用结果
            result = objects[0].toString();
            logger.info("----------接收返回值------"+result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public static String HttpPost_Basic(String url,String data){
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        RequestConfig requestConfig = RequestConfig.custom().
                setSocketTimeout(120*1000).setConnectionRequestTimeout(120*1000).setConnectTimeout(120*1000).build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
        String strMessage = "";
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        httpPost.addHeader("Content-Type", "application/json");
        try {
            StringEntity requestEntity = new StringEntity(data, "utf-8");
            httpPost.setEntity(requestEntity);
            response = httpClient.execute(httpPost, new BasicHttpContext());
            if (response.getStatusLine().getStatusCode() != 200) {
                logger.info("request url failed, http code=" + response.getStatusLine().getStatusCode()
                        + ", url=" + url);
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                reader = new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8"));
                while ((strMessage = reader.readLine()) != null) {
                    buffer.append(strMessage);
                }
            }
            logger.info("接收返回值:" + buffer.toString());
        }catch (IOException e){
            logger.info("request url=" + url + ", exception, msg=" + e.getMessage());
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
