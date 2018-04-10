package com.rjxx.taxeasy.bizhandle.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 *@ClassName URLUtils
 *@Description 配置url参数
 *@Author xlm
 *@Date 2017/8/8.
 *@Version 1.0
 **/
@Component
public class URLUtils {

    public static String WS_URL;


    @Value("${ws_url}")
    public void setWsurl(String ws_url) {
        WS_URL = ws_url;
    }
}
