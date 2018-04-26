package com.rjxx.taxeasy.config.password;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 *@ClassName PasswordConfig
 *@Description 生成pdf配置签名证书用户，密码类
 *@Author xlm
 *@Date 2017/11/27.
 *@Version 1.0
 **/
@Component
public class PasswordConfig {
    public static  String FTP_URL;

    @Value("${ftp_url}")
    public void setFtp_url(String ftp_url) {
        FTP_URL = ftp_url;
    }

    public static  int FTP_PORT;

    @Value("${ftp_port}")
    public void setFTP_PORT(int ftp_port) {
        FTP_PORT = ftp_port;
    }


    public static  String FTP_USERNAME;

    @Value("${ftp_username}")
    public void setFTP_USERNAME(String ftp_username) {
        FTP_USERNAME = ftp_username;
    }

    public static  String FTP_PASSWORD;

    @Value("${ftp_password}")
    public void setFTP_PASSWORD(String ftp_password) {
        FTP_PASSWORD = ftp_password;
    }

    public static  String FTP_BASEPATH;

    @Value("${ftp_basepath}")
    public void setFTP_BASEPATH(String ftp_basepath) {
        FTP_BASEPATH = ftp_basepath;
    }

    public static  String FTP_FILEPATH;

    @Value("${ftp_filepath}")
    public void setFTP_FILEPATH(String ftp_filepath) {
        FTP_FILEPATH = ftp_filepath;
    }

    /**
     * pdf签名证书别名
     */
    public static  String PDF_SIGNUSER;

    @Value("${pdf_signuser}")
    public void setPDF_SIGNUSER(String pdf_signuser){
        PDF_SIGNUSER = pdf_signuser;
    }

    /**
     * pdf签名证书密码
     */
    public static  String PDF_SIGNPASSWORD;

    @Value("${pdf_signpassword}")
    public void setPDF_SIGNPASSWORD(String pdf_signpassword) {
        PDF_SIGNPASSWORD = pdf_signpassword;
    }

    /**
     * pdf证书路径
     */
    public static  String keyStorePath;

    @Value("${pdf_keyStorePath}")
    public void  setKeyStorePath(String pdf_keyStorePath){
        keyStorePath=pdf_keyStorePath;
    }

    /**
     * 凯盈AppID唯一标识
     */
    public static  String AppID;

    @Value("${CRESTV.AppID}")
    public void  setAppID(String appID){
        AppID=appID;
    }

    /**
     * 凯盈Appkey唯一加密key
     */
    public   String AppKey;

    @Value("${CRESTV.AppKey}")
    public void  setAppKey(String appKey){
        AppKey=appKey;
    }


    public static int port;
    @Value("${CRESTV.port}")
    public void  setPort(int Socketport){
        port=Socketport;
    }

    public static String  ip;

    @Value("${CRESTV.server}")
    public void  setIp(String SocketServer){
        ip=SocketServer;
    }

}
