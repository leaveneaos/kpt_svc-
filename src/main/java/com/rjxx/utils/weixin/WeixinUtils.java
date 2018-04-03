package com.rjxx.utils.weixin;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjxx.taxeasy.dao.PpJpaDao;
import com.rjxx.taxeasy.dao.WxTokenJpaDao;
import com.rjxx.taxeasy.dao.WxfpxxJpaDao;
import com.rjxx.taxeasy.dao.XfJpaDao;
import com.rjxx.taxeasy.domains.*;
import com.rjxx.taxeasy.service.SkpService;
import com.rjxx.utils.StringUtils;
import com.rjxx.utils.TimeUtil;
import com.rjxx.utils.WeixinUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 微信工具类
 * Created by zsq on 2017-08-03.
 */
@Service
public class WeixinUtils {
    private static Logger logger = LoggerFactory.getLogger(WeixinUtils.class);

    @Autowired
    private WxfpxxJpaDao wxfpxxJpaDao;

    @Autowired
    private SkpService skpService;

    @Autowired
    private PpJpaDao ppJpaDao;

    @Autowired
    private XfJpaDao xfJpaDao;

    @Autowired
    private WxTokenJpaDao wxTokenJpaDao;

    /**
     * 判断是否微信浏览器
     * @param
     * @return
     */
     public static boolean isWeiXinBrowser(HttpServletRequest request) {
        String ua = request.getHeader("user-agent").toLowerCase();
        boolean res = ua.contains("micromessenger");
        return res;
     }

    /**
     * 获取平台accessToken
     * @return
     */
    public Map hqtk() {
        Map<String, Object> result = new HashMap<String, Object>();
        // 获取token
        String turl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + WeiXinConstants.APP_ID + "&secret=" + WeiXinConstants.APP_SECRET;
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(turl);
        ObjectMapper jsonparer = new ObjectMapper();// 初始化解析json格式的对象
        try {
            HttpResponse res = client.execute(get);
            String responseContent = null; // 响应内容
            HttpEntity entity = res.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
            Map map = jsonparer.readValue(responseContent, Map.class);
            // 将json字符串转换为json对象
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (map.get("errcode") != null) {// 错误时微信会返回错误码等信息，{"errcode":40013,"errmsg":"invalid
                    result.put("success", false);
                    result.put("msg", "获取微信token失败,错误代码为" + map.get("errcode"));
                    return result;
                } else {// 正常情况下{"access_token":"ACCESS_TOKEN","expires_in":7200}
                    map.put("success", true);
                    return map;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("msg", "获取微信token失败" + e.getMessage());
        } finally {
            // 关闭连接 ,释放资源
            client.getConnectionManager().shutdown();
        }
        return result;
    }
    /**
     * 获取ticket
     * @return
     */
    public String getTicket(String accessToken) {
        String ticket = "";
        // String accessToken = (String) weixinUtils.hqtk().get("access_token");
        String ticketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=wx_card";
        String jsonStr = WeixinUtil.httpRequest(ticketUrl, "GET", null);
        if (null != jsonStr) {
            ObjectMapper jsonparer = new ObjectMapper();// 初始化解析json格式的对象
            try {
                Map map = jsonparer.readValue(jsonStr, Map.class);
                Integer errcode = (Integer) map.get("errcode");
                if(null!= errcode && errcode == 0){
                    ticket = (String) map.get("ticket");
                    logger.info("ticket获取成功" + ticket);
                    return ticket;
                }else{
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取平台sappid
     * @return
     */
    public String getSpappid(String accessToken) {

        String invoice_url = "";
        String spappid = "";
        String url = "https://api.weixin.qq.com/card/invoice/seturl?access_token=" + accessToken;
        String jsonStr = WeixinUtil.httpRequest(url, "POST", JSON.toJSONString(""));
        if (jsonStr != null) {
            ObjectMapper jsonparer = new ObjectMapper();// 初始化解析json格式的对象
            try {
                Map map = jsonparer.readValue(jsonStr, Map.class);
                Integer errcode = (Integer) map.get("errcode");
                if(null!= errcode && errcode == 0){
                    invoice_url = (String) map.get("invoice_url");
                    spappid = invoice_url.split("&")[1].split("=")[1];
                    return spappid;
                }else{
                    return null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取微信授权页，跳转链接
     * @param orderid
     * @param money
     * @param timestamp
     * @param menDianId
     * @param type
     * @return
     * @throws Exception
     */
    public String getTiaoURL(String gsdm,String orderid, String money, String timestamp, String menDianId,String type,String access_token,String ticket,String spappid) throws Exception {

        String auth_url = "";
        logger.info("拉取授权订单编号" + orderid + "金额" + money + "时间" + timestamp + "门店号" + menDianId+"--------------"+gsdm);
        if (null == orderid || null == money || timestamp == null || spappid == null || access_token == null || ticket == null) {
            return null;
        }
        BigDecimal big = new BigDecimal(money);
        BigDecimal newbig = big.multiply(new BigDecimal(100));
        Double doumoney = new Double(newbig.toString());
        Date dateTime = null;
        if (null != timestamp && !timestamp.equals("")) {
            String[] s = timestamp.split("-");
            if (s.length > 1) {
                dateTime = TimeUtil.getSysDateInDate(timestamp, "yyyy-MM-dd HH:mm:ss");
            } else {
                dateTime = TimeUtil.getSysDateInDate(timestamp, "yyyyMMddHHmmss");
            }
        }
        String source = "web";
        String redirect_url ="";
        if(gsdm.equals("dicos")){
            redirect_url = WeiXinConstants.DICOS_REDIRECT_URL;
        }else if(gsdm.equals("gvc")){
            redirect_url = WeiXinConstants.GVC_REDIRECT_URL;
        }else if(gsdm.equals("chamate")&&menDianId.equals("chamate_test")){
            logger.info("进入一茶一坐测试开票----等待页面跳转");
            redirect_url=WeiXinConstants.YCYZ_REDIRECT_URL;
        }else {
            redirect_url = WeiXinConstants.SUCCESS_REDIRECT_URL;
        }
        Map nvps = new HashMap();
        nvps.put("s_pappid", spappid);
        nvps.put("order_id", orderid);
        nvps.put("money", doumoney);
        nvps.put("timestamp", dateTime.getTime() / 1000);
        nvps.put("source", source);
        if(type.equals("1")){
            nvps.put("redirect_url", redirect_url);//正式跳转url
        }
        nvps.put("ticket", ticket);
        nvps.put("type", type);

        String sj = JSON.toJSONString(nvps);
        String urls = "https://api.weixin.qq.com/card/invoice/getauthurl?access_token=" + access_token;
        String jsonStr3 = WeixinUtil.httpRequest(urls, "POST", sj);
        if (null != jsonStr3) {
            ObjectMapper jsonparer = new ObjectMapper();// 初始化解析json格式的对象
            try {
                Map map = jsonparer.readValue(jsonStr3, Map.class);

                int errcode = (int) map.get("errcode");
                if (errcode == 0) {
                    auth_url = (String) map.get("auth_url");
                    logger.info("跳转url" + auth_url);
                    return auth_url;
                } else {
                    logger.info("获取微信授权链接失败!");
                    return null;
                }
            } catch (Exception e) {
                //处理异常
                logger.error("Get Ali Access_token error", e);
                return null;
            }
        }
        return auth_url;
    }


    /**
     * 主动查询order_id授权状态
     * @param order_id
     * @param access_token
     * @return
     */
    public Map zdcxstatus(String order_id, String access_token) {

        Map resultMap = new HashMap();
        WeixinUtils weixinUtils = new WeixinUtils();
        String s_pappid = weixinUtils.getSpappid(access_token);

        if(s_pappid == null){
            resultMap.put("msg","72038");
            return resultMap;
        }
        String URL = "https://api.weixin.qq.com/card/invoice/getauthdata?access_token=" + access_token;
        Map nvps = new HashMap();
        nvps.put("s_pappid", s_pappid);
        nvps.put("order_id", order_id);
        String sj = JSON.toJSONString(nvps);
        String jsonStr3 = WeixinUtil.httpRequest(URL, "POST", sj);
        if (null != jsonStr3) {
            ObjectMapper jsonparer = new ObjectMapper();// 初始化解析json格式的对象
            try {
                Map map = jsonparer.readValue(jsonStr3, Map.class);
                Integer errcode = (Integer) map.get("errcode");
                System.out.println("code" + errcode);
                if (null != errcode && errcode==0) {
                    System.out.println("返回数据成功！解析json数据");
                    System.out.println("返回数据" + map.toString());
                    String invoice_status = (String) map.get("invoice_status");
                    int auth_time = (int) map.get("auth_time");
                    Map user_auth_info = (Map) map.get("user_auth_info");
                    Date date = new Date(auth_time);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String xdsj = sdf.format(date);//下单时间
                    System.out.println("" + xdsj.toString());
                    if (null != user_auth_info.get("user_field")) {
                        //个人抬头
                        Map user_field = (Map) user_auth_info.get("user_field");
                        System.out.println("个人抬头" + user_auth_info.toString());
                        String title = (String) user_field.get("title");
                        String phone = (String) user_field.get("phone");
                        String email = (String) user_field.get("email");
                        List custom_field = (List) user_field.get("custom_field");
                        String key = "";
                        String value = "";
                        if (custom_field.size() > 0) {
                            for (int i = 0; i < custom_field.size(); i++) {
                                System.out.println("个人中的其他数据" + custom_field.get(i));
                                Map map1 = (Map) custom_field.get(i);
                                key = (String) map1.get("key");
                                value = (String) map1.get("value");
                                //System.out.println("key"+key);
                                //System.out.println("value"+value);
                            }
                        }
                        resultMap.put("xdsj", xdsj);//下单时间
                        resultMap.put("title", title);//发票抬头名称
                        resultMap.put("phone", phone);//电话
                        resultMap.put("email", email);//邮箱
                        resultMap.put("key", key);
                        resultMap.put("value", value);
                        resultMap.put("msg","0");
                        return resultMap;
                    }
                    if (null != user_auth_info.get("biz_field")) {
                        //单位抬头
                        Map biz_field = (Map) user_auth_info.get("biz_field");
                        System.out.println("个人抬头" + user_auth_info.toString());
                        String title = (String) biz_field.get("title");
                        String tax_no = (String) biz_field.get("tax_no");
                        String addr = (String) biz_field.get("addr");
                        String phone = (String) biz_field.get("phone");
                        String bank_type = (String) biz_field.get("bank_type");
                        String bank_no = (String) biz_field.get("bank_no");
                        List custom_field = (List) biz_field.get("custom_field");
                        String key = "";
                        String value = "";
                        if (custom_field.size() > 0) {
                            for (int i = 0; i < custom_field.size(); i++) {
                                System.out.println("个人中的其他数据" + custom_field.get(i));
                                Map map1 = (Map) custom_field.get(i);
                                key = (String) map1.get("key");
                                value = (String) map1.get("value");
                                System.out.println("key" + key);
                                System.out.println("value" + value);

                            }
                        }
                        resultMap.put("title", title);//抬头
                        resultMap.put("tax_no", tax_no);//税号
                        resultMap.put("addr", addr);//地址
                        resultMap.put("phone", phone);//电话
                        resultMap.put("bank_type", bank_type);//开户类型
                        resultMap.put("bank_no", bank_no);//银行账号
                        resultMap.put("key", key);//邮箱
                        resultMap.put("value", value);//
                        if (null != key && key.equals("邮箱")) {
                            resultMap.put("email", value);
                        }
                        System.out.println("封装的数据" + resultMap.toString());
                        resultMap.put("msg","0");
                        return resultMap;
                    }

                } else{
                    logger.info( order_id + "授权状态"+errcode+",错误代码" + errcode);
                    resultMap.put("msg","72038");
                    return resultMap;
                }

            } catch (Exception e) {
                e.printStackTrace();
                resultMap.put("msg","72038");
                return resultMap;
            }
        }
        resultMap.put("msg","0");
        return resultMap;
    }

    /**
     * 微信授权页面 设置字段
     */
    public void sqzd() {
        WeixinUtils weixinUtils = new WeixinUtils();
        //String access_token = (String) weixinUtils.hqtk().get("access_token");
        String access_token ="";
        WxToken wxToken = wxTokenJpaDao.findByFlag("01");
        if(wxToken==null){
            access_token= (String) weixinUtils.hqtk().get("access_token");
        }else {
            access_token = wxToken.getAccessToken();
        }
        Map sjss = new HashMap();
        Map auth_field = new HashMap();
        Map user_field = new HashMap();
        Map biz_field = new HashMap();
        List custom_field1 = new ArrayList();
        List custom_field2 = new ArrayList();

        Map cus1 = new HashMap();
        Map cus2 = new HashMap();
        cus1.put("key", "其他");
        cus2.put("key", "邮箱");
        custom_field1.add(cus1);
        custom_field2.add(cus2);

        auth_field.put("user_field", user_field);
        auth_field.put("biz_field", biz_field);

        user_field.put("show_title", 1);
        user_field.put("show_phone", 1);
        user_field.put("show_email", 1);
        user_field.put("require_email", 1);
        user_field.put("custom_field", custom_field1);

        biz_field.put("show_title", 1);
        biz_field.put("show_tax_no", 1);
        biz_field.put("show_addr", 1);

        biz_field.put("show_phone", 1);
        biz_field.put("show_bank_type", 1);
        biz_field.put("show_bank_no", 1);
        biz_field.put("require_tax_no", 1);
        biz_field.put("require_addr", 0);
        biz_field.put("custom_field", custom_field2);
        biz_field.put("is_require", 1);

        sjss.put("auth_field", auth_field);
        String sj = JSON.toJSONString(sjss);
        String urls = "https://api.weixin.qq.com/card/invoice/setbizattr?action=set_auth_field&access_token=" + access_token;
        String jsonStr3 = WeixinUtil.httpRequest(urls, "POST", sj);
        if (null != jsonStr3) {
            ObjectMapper jsonparer = new ObjectMapper();// 初始化解析json格式的对象
            try {
                Map map = jsonparer.readValue(jsonStr3, Map.class);
                String errmsg = (String) map.get("errmsg");
                System.out.println("错误类型" + errmsg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询授权页面字段
     */
    public void cksqzd() {
        WeixinUtils weixinUtils = new WeixinUtils();
        String access_token ="";

        WxToken wxToken = wxTokenJpaDao.findByFlag("01");
        if(wxToken==null){
            access_token= (String) weixinUtils.hqtk().get("access_token");
        }else {
            access_token = wxToken.getAccessToken();
        }

        String ckUrl = "https://api.weixin.qq.com/card/invoice/setbizattr?action=get_auth_field&access_token=" + access_token;
        String jsonStr3 = WeixinUtil.httpRequest(ckUrl, "POST", JSON.toJSONString(""));
        System.out.println("返回信息" + jsonStr3.toString());
        if (null != jsonStr3) {
            ObjectMapper jsonparer = new ObjectMapper();// 初始化解析json格式的对象
            try {
                Map map = jsonparer.readValue(jsonStr3, Map.class);
                String errmsg = (String) map.get("errmsg");
                System.out.println("错误类型" + errmsg);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 拒绝开票
     * @param order_id
     * @param reason
     * @param access_token
     * @return
     */
    public String jujuekp(String order_id, String reason, String access_token) {
        logger.info("拒绝---开票时传入的"+order_id);
        String msg = "";
        WeixinUtils weixinUtils = new WeixinUtils();
        String jjkpURL = "https://api.weixin.qq.com/card/invoice/rejectinsert?access_token=" + access_token;
        Map mapInfo = new HashMap();
        String s_pappid = weixinUtils.getSpappid(access_token);
        //原始订单 ----- 开票平台的订单
//        String orderno_old="";
//        int i = order_id.indexOf("-");
//        if(i<0){
//            logger.info("没有-，表示没有拒绝过开票");
//            orderno_old=order_id;
//        }else {
//            logger.info("表示拒绝过开票");
//            String[] split = order_id.split("-");
//            orderno_old=split[0];
//        }
        //String url = WeiXinConstants.RJXX_REDIRECT_URL;
        if(s_pappid==null){
            return null;
        }
        mapInfo.put("s_pappid", s_pappid);
        mapInfo.put("order_id", order_id);
        mapInfo.put("reason", reason);
        //mapInfo.put("url", url);
        String info = JSON.toJSONString(mapInfo);
        String jsonStr3 = WeixinUtil.httpRequest(jjkpURL, "POST", info);
        System.out.println("返回信息" + jsonStr3.toString());
        if (null != jsonStr3) {
            ObjectMapper jsonparer = new ObjectMapper();// 初始化解析json格式的对象
            try {
                Map map = jsonparer.readValue(jsonStr3, Map.class);
                int errcode = (int) map.get("errcode");
                String errmsg = (String) map.get("errmsg");
                System.out.println("错误码" + errcode);
                if (errcode == 0) {
                    logger.info("拒绝开票成功");
                     msg = "1";
                     WxFpxx wxFpxx = wxfpxxJpaDao.selectByWeiXinOrderNo(order_id);
                     int coun = wxFpxx.getCount()+ 1;
                     wxFpxx.setCount(coun);
                     wxfpxxJpaDao.save(wxFpxx);
                     logger.info("拒绝开票----更新计数"+coun);
                } else if(errcode == 72035) {
                    msg = "1";
                    WxFpxx wxFpxx = wxfpxxJpaDao.selectByWeiXinOrderNo(order_id);
                    int coun = wxFpxx.getCount()+ 1;
                    wxFpxx.setCount(coun);
                    wxfpxxJpaDao.save(wxFpxx);
                    logger.info("发票已被拒绝---72035----更新计数"+coun);
                }else {
                    logger.info("其他错误信息为" + errmsg);
                    msg = "0";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return msg;
    }

    /**
     * 创建发票卡券模板
     * @param gsmc
     * @param payee
     * @param logo_url
     * @return
     */
    public String creatMb(String gsmc, String payee, String logo_url,String access_token) {
        String card_id = "";
        logger.info("进入创建卡券模板----logo_url" + logo_url);

        String creatURL = WeiXinConstants.CREAT_MUBAN_URL + access_token;
        System.out.println("创建卡卷模板url地址" + creatURL);
        Map paraInfo = new HashMap();
        Map invoice_info = new HashMap();
        Map base_info = new HashMap();
        WeiXinMuBan weiXinMuBan = new WeiXinMuBan();
        weiXinMuBan.setPayee(payee);
        weiXinMuBan.setType("增值税普通发票");
        weiXinMuBan.setTitle(gsmc);
        weiXinMuBan.setLogo_url(logo_url);
        //weiXinMuBan.setLogo_url("http://mmbiz.qpic.cn/mmbiz_jpg/l249Gu1JJaJjZiauO138MD1d6dnglRlj1bicFTaNyyDGcAOgxMd2WoXLKvEn8icJiaiaibRJkgeBcsCODI4AYP7V6vPg/0");
        weiXinMuBan.setCustom_url_name("查看发票");
        weiXinMuBan.setCustom_url_sub_title("电子发票");
        //weiXinMuBan.setCustom_url(WeiXinConstants.fpInfoURL);//发票详情--正式
        //weiXinMuBan.setCustom_url(WeiXinConstants.testfpInfoURL);//发票详情--测试
        weiXinMuBan.setPromotion_url_name("电票简介");
        weiXinMuBan.setPromotion_url_sub_title("看懂电子发票");
        weiXinMuBan.setPromotion_url("");
        weiXinMuBan.setDescription("自己看流程");
        invoice_info.put("base_info", base_info);
        invoice_info.put("payee", weiXinMuBan.getPayee());
        invoice_info.put("type", weiXinMuBan.getType());
        invoice_info.put("detail", "测试-detail");
        base_info.put("logo_url", weiXinMuBan.getLogo_url());
        base_info.put("title", weiXinMuBan.getTitle());
        base_info.put("description", weiXinMuBan.getDescription());
        base_info.put("custom_url_name", weiXinMuBan.getCustom_url_name());
        base_info.put("custom_url", weiXinMuBan.getCustom_url());
        base_info.put("custom_url_sub_title", weiXinMuBan.getCustom_url_sub_title());
        base_info.put("promotion_url_name", weiXinMuBan.getPromotion_url_name());
        base_info.put("promotion_url", weiXinMuBan.getPromotion_url());
        base_info.put("promotion_url_sub_title", weiXinMuBan.getPromotion_url_sub_title());
        paraInfo.put("invoice_info", invoice_info);

        System.out.println("参数" + JSON.toJSONString(paraInfo));
        String jsonStr = WeixinUtil.httpRequest(creatURL, "POST", JSON.toJSONString(paraInfo));
        if (null != jsonStr) {
            ObjectMapper jsonparer = new ObjectMapper();// 初始化解析json格式的对象
            try {
                Map map = jsonparer.readValue(jsonStr, Map.class);
                int errcode = (int) map.get("errcode");
                String errmsg = (String) map.get("errmsg");
                System.out.println("错误码" + errcode);
                if (errcode == 0) {
                    card_id = (String) map.get("card_id");
                    // resultMap.put("card_id",card_id);
                    // resultMap.put("msg","发票卡券设置成功");
                    System.out.println("创建卡券模板成功" + card_id);

                } else {
                    //resultMap.put("msg","发票卡券模板设置错误");
                    logger.info("返回的错误信息为" + errmsg);
                    System.out.println("卡券模板设置失败" + errmsg);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return card_id;
    }

    /**
     * 上传图片
     *
     * @return ImageURL
     */

    public String uploadImage() {

        String ImageURL = "";
        WeixinUtils weixinUtils = new WeixinUtils();
        //String access_token = (String) weixinUtils.hqtk().get("access_token");
        String access_token ="";

        WxToken wxToken = wxTokenJpaDao.findByFlag("01");
        if(wxToken==null){
            access_token= (String) weixinUtils.hqtk().get("access_token");
        }else {
            access_token = wxToken.getAccessToken();
        }

        String url = "//api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=" + access_token;
        HttpPost httpPost = new HttpPost(url);
        HttpClient httpClient = new DefaultHttpClient();
        ObjectMapper jsonparer = new ObjectMapper();// 初始化解析json格式的对象

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        FileBody fileBody = new FileBody(new File("D:/family.jpg"));//上传图片路径
        builder.addPart("media", fileBody);
        HttpEntity entit = builder.build();
        httpPost.setEntity(entit);
        try {
            HttpResponse res = httpClient.execute(httpPost);
            String responseContent = null; // 响应内容
            HttpEntity entityRes = res.getEntity();
            responseContent = EntityUtils.toString(entityRes, "UTF-8");
            Map map = jsonparer.readValue(responseContent, Map.class);

            // 将json字符串转换为json对象
            System.out.println("返回的数据" + map.toString());
            ImageURL = (String) map.get("url");
            if (ImageURL != null) {
                System.out.println("图片上传成功");
                System.out.println("url路径为" + url);
            } else {
                System.out.println("图片上传失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ImageURL;
    }

    /**
     * @param order_id
     * @param pdf_file_url
     * @param kpspmxList
     * @param kpls
     * @return
     */

    public String fpInsertCardBox(String order_id, String pdf_file_url, List<Kpspmx>
            kpspmxList, Kpls kpls) {
        logger.info("发票插入卡包开始");
        logger.info("开票商品明细为---"+JSON.toJSONString(kpspmxList));
        //主动查询授权状态
        //String access_token = (String) this.hqtk().get("access_token");
        String access_token ="";
        WxToken wxToken = wxTokenJpaDao.findByFlag("01");
        if(wxToken==null){
            WeixinUtils weixinUtils = new WeixinUtils();
            access_token= (String) weixinUtils.hqtk().get("access_token");
        }else {
            access_token = wxToken.getAccessToken();
        }
        Map weiXinData = this.zdcxstatus(order_id, access_token);
        if (null == weiXinData) {
            //logger.info("主动查询授权没有数据++++++++++++");
            //logger.info("开票流水为---"+JSON.toJSONString(kpls));
           // logger.info("====="+JSON.toJSONString(weiXinData));
           // logger.info("-------------"+kpls.getGfmc());
            Map resultMap = new HashMap();
            resultMap.put("title",kpls.getGfmc());
            resultMap.put("tax_no",kpls.getGfsh());
            resultMap.put("email",kpls.getGfemail());
            resultMap.put("bank_type",kpls.getGfyh());
            resultMap.put("bank_no",kpls.getGfyhzh());
            resultMap.put("addr",kpls.getGfdz());
            resultMap.put("phone",kpls.getGfdh());
            weiXinData = resultMap;
        }else if(weiXinData!=null && weiXinData.get("msg").equals("72038")){
            logger.info("没有授权,不插卡");
            return null;
        }else if(weiXinData!=null && weiXinData.get("msg").equals("0")){
            Map resultMap = new HashMap();
            resultMap.put("title",kpls.getGfmc());
            resultMap.put("tax_no",kpls.getGfsh());
            resultMap.put("email",kpls.getGfemail());
            resultMap.put("bank_type",kpls.getGfyh());
            resultMap.put("bank_no",kpls.getGfyhzh());
            resultMap.put("addr",kpls.getGfdz());
            resultMap.put("phone",kpls.getGfdh());
            weiXinData = resultMap;
        }
        //公司简称 品牌t_pp kpddm->skp->pid->ppmc
        if (null == kpls.getSkpid()) {
            logger.info("税控盘id为空！");
            return null;
        }
        Map skpMap = new HashMap();
        logger.info("根据税控盘id查询税控盘-----");
        skpMap.put("kpdid", kpls.getSkpid());
        Skp skp = skpService.findOneByParams(skpMap);
        if (null == skp.getPid()) {
            logger.info("pid 为空----");
            return null;
        }
        logger.info("根据品牌代码查询品牌表---");
        Pp pp = ppJpaDao.findOneById(skp.getPid());
        logger.info("品牌详情---"+pp.toString());
        Xf xf = xfJpaDao.findOneById(skp.getXfid());
        logger.info("销方详情----"+JSON.toJSONString(xf));
        logger.info("wechatCardId----"+xf.getWechatCardId());
        String card_id = "";
        if(null==xf.getWechatCardId()||xf.getWechatCardId().equals("")){
            //销方表没有值，调用生成卡券模板
            card_id = this.creatMb(pp.getPpmc(), kpls.getXfmc(), pp.getWechatLogoUrl(),access_token);
            logger.info("公司简称---" + pp.getPpmc());
            logger.info("logourl---" + pp.getWechatLogoUrl());
            logger.info("插入卡包的模板id-------" + card_id);
            //保存卡券模板id进xf表
            try {
                xf.setWechatCardId(card_id);
                xfJpaDao.save(xf);
                //防止生成卡包模板和插卡时间间隔过短
                //Thread.sleep(300000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            card_id=xf.getWechatCardId();
            logger.info("直接获取到卡券模板id---------"+card_id);
        }
            //调用dzfpInCard方法将发票放入卡包
        return dzfpInCard(order_id, card_id, pdf_file_url, weiXinData, kpspmxList, kpls, access_token);
    }

    /**
     * 发票插入卡包
     * @param order_id
     * @param card_id
     * @param pdf_file_url
     * @param weiXinData
     * @param kpspmxList
     * @param kpls
     * @param access_token
     * @return
     */
    public String dzfpInCard(String order_id, String card_id, String pdf_file_url, Map weiXinData, List<Kpspmx> kpspmxList, Kpls kpls, String access_token) {
        String appid = WeiXinConstants.APP_ID;
        WeiXinInfo weiXinInfo = new WeiXinInfo();
        WeixinUtils weixinUtils = new WeixinUtils();

        Map sj = new HashMap();
        Map card_ext = new HashMap();
        Map user_card = new HashMap();
        List<Map> info = new ArrayList<>();
        Map invoice_user_data = new HashMap();
        sj.put("order_id", order_id);   //订单编号     必填
        sj.put("card_id", card_id);     //发票模板id   必填
        sj.put("appid", appid);         //公众号APPid  必填
        sj.put("card_ext", card_ext);

        String nonce_str = System.currentTimeMillis() + "";//随机字符串，防止重复  必填
        card_ext.put("nonce_str", nonce_str);
        card_ext.put("user_card", user_card);

        user_card.put("invoice_user_data", invoice_user_data);

        weiXinInfo.setTitle((String) weiXinData.get("title"));//发票抬头    必填

        BigDecimal bigjshj= new BigDecimal(kpls.getJshj().toString());
        BigDecimal bigzh = bigjshj.multiply(new BigDecimal(100));
        //Double doujshj= new Double(bigzh.toString());
        //weiXinInfo.setFee(doujshj);//卡包开票金额,价税合计  必填
        //weiXinInfo.setFee(kpls.getJshj()*100);//卡包开票金额,价税合计  必填

        logger.info("数据库开票录入时间----"+kpls.getKprq());
        logger.info("插入卡包时间-----"+String.valueOf(kpls.getKprq().getTime() / 1000));
        weiXinInfo.setBilling_time(String.valueOf(kpls.getKprq().getTime() / 1000));//开票时间  必填
        weiXinInfo.setBilling_no(kpls.getFpdm());//发票代码      必填
        weiXinInfo.setBilling_code(kpls.getFphm());//发票号码    必填

        BigDecimal bighjje = new BigDecimal(kpls.getHjje().toString());
        BigDecimal bigzzhjje = bighjje.multiply(new BigDecimal(100));
        //Double douhjje = new Double(bigzzhjje.toString());
        //weiXinInfo.setFee_without_tax(douhjje);//不含税金额  必填
        //weiXinInfo.setFee_without_tax(kpls.getHjje()* 100);//不含税金额  必填

        BigDecimal bighjse = new BigDecimal(kpls.getHjse().toString());
        BigDecimal bigzzhjse = bighjse.multiply(new BigDecimal(100));
        //Double douhjse = new Double(bigzzhjse.toString());
        //weiXinInfo.setTax(douhjse);//税额        必填
        // weiXinInfo.setTax(kpls.getHjse() * 100);//税额        必填
        weiXinInfo.setCheck_code(kpls.getJym());//校验码    必填
//        weiXinInfo.setFee(20.00);//发票金额
//        weiXinInfo.setTitle("测试");
//        weiXinInfo.setBilling_time("1480342498");//发票开票时间
//        weiXinInfo.setBilling_no("150003521088");//发票代码
//        weiXinInfo.setBilling_code("36984009");//发票号码
//        weiXinInfo.setFee_without_tax(10.00);//不含税金额
//        weiXinInfo.setTax(11.00);//税额
//        weiXinInfo.setCheck_code("737806869");
//        weiXinInfo.setS_pdf_media_id("75542938824475128");
//        Map ma = new HashMap();
//        ma.put("name", "饼干");//商品名称 必填
//        ma.put("num",2);//商品数量    必填
//        ma.put("unit","包");//商品单位  必填
//        ma.put("price",20);//商品单价 必填

        //info.add(ma);
        if (kpspmxList.size() > 0) {
            for (Kpspmx kpspmx : kpspmxList) {
                Map ma = new HashMap();
                ma.put("name", kpspmx.getSpmc());//商品名称 必填
                ma.put("num", kpspmx.getSps());//商品数量    必填
                ma.put("unit", kpspmx.getSpdw());//商品单位  必填
                ma.put("price", kpspmx.getSpdj());//商品单价 必填
                info.add(ma);
                //break;
            }
        }
        //上传PDF生成的一个发票s_media_id   关联发票PDF和发票卡券  必填
        String pdfUrl = kpls.getPdfurl();
        String s_media_id_pdf = weixinUtils.creatPDF(pdfUrl, pdf_file_url,access_token);
        if (null != s_media_id_pdf && StringUtils.isNotBlank(s_media_id_pdf)) {
            weiXinInfo.setS_pdf_media_id(s_media_id_pdf);
        }
        invoice_user_data.put("fee", bigzh.intValue());
        invoice_user_data.put("title", weiXinInfo.getTitle());
        invoice_user_data.put("billing_time", weiXinInfo.getBilling_time());
        invoice_user_data.put("billing_no", weiXinInfo.getBilling_no());
        invoice_user_data.put("billing_code", weiXinInfo.getBilling_code());
        invoice_user_data.put("info", info);
        invoice_user_data.put("fee_without_tax", bigzzhjje.intValue());
        invoice_user_data.put("tax", bigzzhjse.intValue());
        invoice_user_data.put("s_pdf_media_id", weiXinInfo.getS_pdf_media_id());
        invoice_user_data.put("s_trip_pdf_media_id", weiXinInfo.getS_trip_pdf_media_id());
        invoice_user_data.put("check_code", weiXinInfo.getCheck_code());
        invoice_user_data.put("buyer_number", weiXinInfo.getBuyer_number());
        invoice_user_data.put("buyer_address_and_phone", weiXinInfo.getBuyer_address_and_phone());
        invoice_user_data.put("buyer_bank_account", weiXinInfo.getBuyer_bank_account());
        invoice_user_data.put("seller_number", weiXinInfo.getSeller_number());
        invoice_user_data.put("seller_address_and_phone", weiXinInfo.getSeller_address_and_phone());
        invoice_user_data.put("seller_bank_account", weiXinInfo.getSeller_bank_account());
        invoice_user_data.put("remarks", weiXinInfo.getRemarks());
        invoice_user_data.put("cashier", weiXinInfo.getCashier());
        invoice_user_data.put("maker", weiXinInfo.getMaker());


        if (null == sj.get("order_id")) {
            logger.info("订单order_id为空");
            return null;
        }
        if (null == sj.get("card_id")) {
            logger.info("发票card_id为null");
            return null;
        }
        if (null == sj.get("appid")) {
            logger.info("商户appid为null");
            return null;
        }
        if (null == card_ext.get("nonce_str")) {
            logger.info("随机字符串nonce_str为null");
            return null;
        }
        if (null == invoice_user_data.get("fee")) {
            logger.info("发票金额fee为null");
            return null;
        }
        if (null == invoice_user_data.get("title")) {
            logger.info("发票抬头title为null");
            return null;
        }
        if (null == invoice_user_data.get("billing_time")) {
            logger.info("发票的开票时间billing_time为null");
            return null;
        }
        if (null == invoice_user_data.get("billing_no")) {
            logger.info("发票的发票代码billing_no为null");
            return null;
        }
        if (null == invoice_user_data.get("billing_code")) {
            logger.info("发票的发票号码billing_code为null");
            return null;
        }
        if (null == invoice_user_data.get("fee_without_tax")) {
            logger.info("不含税金额fee_without_tax为null");
            return null;
        }
        if (null == invoice_user_data.get("s_pdf_media_id")) {
            logger.info("上传PDF的s_pdf_media_id为null");
            return null;
        }
        if (null == invoice_user_data.get("check_code")) {
            logger.info("校验码check_code为null");
            return null;
        }
        if (null == access_token) {
            logger.info("获取token错误");
            return null;
        }
        String URL = WeiXinConstants.dzfpInCard_url + access_token;
        String jsonStr = WeixinUtil.httpRequest(URL, "POST", JSON.toJSONString(sj));
        if (null != jsonStr) {
            ObjectMapper jsonparer = new ObjectMapper();// 初始化解析json格式的对象
            try {
                Map map = jsonparer.readValue(jsonStr, Map.class);
                int errcode = (int) map.get("errcode");
                String errmsg = (String) map.get("errmsg");
                System.out.println("错误码" + errcode);
                if (errcode == 0) {
                    String openid = (String) map.get("openid");
                    String code = (String) map.get("code");
                    WxFpxx wxFpxx = wxfpxxJpaDao.selectByWeiXinOrderNo(order_id);
                    wxFpxx.setCode(code);
                    wxfpxxJpaDao.save(wxFpxx);
                    logger.info("发票插入卡包成功-------------------------");
                    return code;
                } else {
                    logger.info("返回的错误信息为" + errmsg);
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 上传PDF
     * @param pdfurl
     * @param pdf_file_url
     * @return
     */
    public String creatPDF(String pdfurl, String pdf_file_url,String access_token ) {
        String pdfUrlPath = "";

        if (null == pdf_file_url) {
            logger.info("上传PDF路径pdf_file_url为null");
            return null;
        }
        if (null == pdfurl) {
            logger.info("上传PDF的url路径pdfurl为null");
            return null;
        }
        if (null != pdfurl && StringUtils.isNotBlank(pdfurl)) {
            String p = pdfurl.split("//")[1];
            if (null != p && StringUtils.isNoneEmpty(p)) {
                String p1 = pdfurl.split("//")[1].split("/")[1];
                String p2 = pdfurl.split("//")[1].split("/")[2];
                String p3 = pdfurl.split("//")[1].split("/")[3];
                String p4 = pdfurl.split("//")[1].split("/")[4];
                pdfUrlPath = pdf_file_url + "/" + p1 + "/" + p2 + "/" + p3 + "/" + p4;
            }
        }

        System.out.println("pdf路径问题" + pdfUrlPath);
        String s_media_id = "";

        String pdfURL = WeiXinConstants.CREAT_PDF_URL + access_token;

        HttpPost httpPost = new HttpPost(pdfURL);
        HttpClient httpClient = new DefaultHttpClient();

        ObjectMapper jsonparer = new ObjectMapper();// 初始化解析json格式的对象
        // Map nvps = new HashMap();
        // nvps.put("pdf", file);
        // StringEntity requestEntity = new StringEntity(nvps.toString(), ContentType.MULTIPART_FORM_DATA);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        FileBody fileBody = new FileBody(new File(pdfUrlPath));
        /// FileBody fileBody = new FileBody(new File("D:/5001020100036432.pdf"));
        // FileBody fileBody = new FileBody(new File("D:/111111.pdf"));
        builder.addPart("pdf", fileBody);
        HttpEntity entit = builder.build();
        httpPost.setEntity(entit);
        try {
            HttpResponse res = httpClient.execute(httpPost);
            String responseContent = null; // 响应内容
            HttpEntity entityRes = res.getEntity();
            responseContent = EntityUtils.toString(entityRes, "UTF-8");
            Map map = jsonparer.readValue(responseContent, Map.class);
            int errcode = (int) map.get("errcode");
            // 将json字符串转换为json对象
            System.out.println("返回的数据" + map.toString());
            if (errcode == 0) {
                System.out.println("上传PDF成功");
                s_media_id = (String) map.get("s_media_id");
                return s_media_id;
            } else {
                String errmsg = (String) map.get("errmsg");
                System.out.println("上传PDF失败,失败原因" + errmsg);
                return "上传PDF失败";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新发票卡券状态
     * @param card_id
     * @param code
     * @param reimburse_status
     * @return
     */
    public String updateStatus(String card_id, String code, String reimburse_status) {
        String msg = "";
        WeixinUtils weixinUtils = new WeixinUtils();
        //String reimburse_status = "";
        Map map = new HashMap();
        map.put("card_id", card_id);
        map.put("code", code);
        map.put("reimburse_status", reimburse_status);
        System.out.println("封装数据" + JSON.toJSONString(map));

        //String access_token = (String) weixinUtils.hqtk().get("access_token");
        String access_token ="";
        WxToken wxToken = wxTokenJpaDao.findByFlag("01");
        if(wxToken==null){
            access_token= (String) weixinUtils.hqtk().get("access_token");
        }else {
            access_token = wxToken.getAccessToken();
        }
        String updeatStatusURL = "https://api.weixin.qq.com/card/invoice/platform/updatestatus?access_token=" + access_token;
        System.out.println("电子发票插入卡包url为++++" + updeatStatusURL);

        String jsonStr = WeixinUtil.httpRequest(updeatStatusURL, "POST", JSON.toJSONString(map));
        if (null != jsonStr) {
            ObjectMapper jsonparer = new ObjectMapper();// 初始化解析json格式的对象
            try {
                Map maps = jsonparer.readValue(jsonStr, Map.class);
                int errcode = (int) maps.get("errcode");
                String errmsg = (String) maps.get("errmsg");
                if (errcode == 0) {
                    msg = "1";
                    System.out.println("错误码" + errmsg);

                } else {
                    msg = "0";
                    logger.info("返回的错误信息为" + errmsg);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return msg;
    }

    /**
     * 微信解码code
     * @param encrypt_code
     * @return
     */
    public String decode(String encrypt_code,String access_token) {
        String code = "";
        if (null == encrypt_code) {
            return null;
        }
        String URL = WeiXinConstants.decodeURL + access_token;
        Map map = new HashMap();
        map.put("encrypt_code", encrypt_code);
        System.out.println("数据" + JSON.toJSONString(map));
        String jsonStr = WeixinUtil.httpRequest(URL, "POST", JSON.toJSONString(map));
        if (null != jsonStr) {
            ObjectMapper jsonparer = new ObjectMapper();// 初始化解析json格式的对象
            try {
                Map maps = jsonparer.readValue(jsonStr, Map.class);
                int errcode = (int) maps.get("errcode");
                String errmsg = (String) maps.get("errmsg");
                if (errcode == 0) {
                    code = (String) maps.get("code");
                    System.out.println("错误码" + errmsg);
                } else {
                    logger.info("解码code错误，返回的错误信息为" + errmsg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return code;
    }

    public String queryPDF(String s_media_id,String access_token) {
        String pdf_url = "";
        if (null == s_media_id || s_media_id.equals("")) {
            return null;
        }
        String URL = WeiXinConstants.QUERY_PDF_URL + access_token;
        Map map = new HashMap();
        map.put("s_media_id", s_media_id);
        map.put("action","get_url");
        logger.info("数据" + JSON.toJSONString(map));
        String jsonStr = WeixinUtil.httpRequest(URL, "POST", JSON.toJSONString(map));
        if (null != jsonStr) {
            ObjectMapper jsonparer = new ObjectMapper();// 初始化解析json格式的对象
            try {
                Map maps = jsonparer.readValue(jsonStr, Map.class);
                int errcode = (int) maps.get("errcode");
                String errmsg = (String) maps.get("errmsg");
                if (errcode == 0) {
                    pdf_url = (String) maps.get("pdf_url");
                    return pdf_url;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
