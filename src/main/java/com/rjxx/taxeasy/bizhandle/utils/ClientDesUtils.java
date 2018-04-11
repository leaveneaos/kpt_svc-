package com.rjxx.taxeasy.bizhandle.utils;

import com.rjxx.taxeasy.bizhandle.invoicehandling.DataOperate;
import com.rjxx.taxeasy.dal.FphxwsjlService;
import com.rjxx.taxeasy.dal.GsxxService;
import com.rjxx.utils.DesUtils;
import com.rjxx.utils.HtmlUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 解析客户端加密
 * Created by ZhangBing on 2017-03-09.
 */
@Service("clientDesUtils")
public class ClientDesUtils {


    @Autowired
    private DataOperate dataOperate;

    @Autowired
    private GsxxService gsxxService;
    @Autowired
    private FphxwsjlService fphxwsjlService;
    /**
     * 解密客户端的请求参数
     *
     * @param queryString
     * @return
     */
    public static Map<String, String> decryptClientQueryString(String queryString) throws Exception {
        if (StringUtils.isBlank(queryString)) {
            throw new Exception("参数不能为空");
        }
        try {
            String result = DesUtils.DESDecrypt(queryString, DesUtils.GLOBAL_DES_KEY);
            return HtmlUtils.parseQueryString(result);
        } catch (Exception e) {
            throw new Exception("非法请求");
        }
    }
    private static String getSign(String QueryData, String key) {
        String signSourceData = "data=" + QueryData + "&key=" + key;
        String newSign = DigestUtils.md5Hex(signSourceData);
        return newSign;
    }
}
