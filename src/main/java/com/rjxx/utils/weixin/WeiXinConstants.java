package com.rjxx.utils.weixin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017-08-03.
 */
@Component
public class WeiXinConstants {

    //微信AppSecret
    public static String APP_ID;

    public static String APP_SECRET;

    @Value("${wechatappid}")
    public  void setAppId(String wechatappid) {
        APP_ID = wechatappid;
    }
    @Value("${wechatappsecret}")
    public  void setAppSecret(String wechatappsecret) {
        APP_SECRET = wechatappsecret;
    }

//    沙箱appid
   //public static final String APP_ID = "wx8c2a4c2289e10ffb";
//    沙箱appSecret
    //public static  final  String APP_SECRET = "ad706ca065a0d384414ae3b568e030fb";
//    //微信appid
//    public static String APP_ID = "wx9abc729e2b4637ee";
//    //微信AppSecret
//    public static String APP_SECRET = "6415ee7a53601b6a0e8b4ac194b382eb";

    //微信回调地址
    public static final String AFTER_WEIXIN_REDIRECT_URL = "/getWeiXinURL";
    //微信授权成功后跳转url
    public static final String BEFORE_WEIXIN_REDIRECT_URL = "/getTiaoZhuanURL";
    //重定向url
    public static final String RJXX_REDIRECT_URL = "WWW.baidu.com";
    //创建发票卡卷模板
    public  static  final  String CREAT_MUBAN_URL = "https://api.weixin.qq.com/card/invoice/platform/createcard?access_token=";
    //将电子发票插入微信卡包
    public  static  final  String dzfpInCard_url = "https://api.weixin.qq.com/card/invoice/insert?access_token=";

    //全家发票模板card_id    一次性设置
   // public static  final  String FAMILY_CARD_ID ="pPyotwWZi4W2onmIlX_ohO23lr28";
    //一茶一坐  发票模板card_id
   // public static final String  CHAMATE_CARD_ID ="";
    //上传PDF地址
    public  static  final String CREAT_PDF_URL = "https://api.weixin.qq.com/card/invoice/platform/setpdf?access_token=";

    //查询已上传PDF
    public static final String QUERY_PDF_URL  ="https://api.weixin.qq.com/card/invoice/platform/getpdf?action=get_url&access_token=";
    //发票报销状态
    public  static  final String INVOICE_STATUS_INIT = "INVOICE_REIMBURSE_INIT";//发票初始状态，未锁定
    public  static  final String INVOICE_STATUS_LOCK = "INVOICE_REIMBURSE_LOCK";//发票已锁定
    public  static  final String INVOICE_STATUS_CLOUSE = "INVOICE_REIMBURSE_CLOSURE";//发票已核销

    //申请开票完成跳转url     http://fpj.datarj.com/einv/Family/witting.html
    public  static final String TEST_SUCCESS_REDIRECT_URL = "http://fpjtest.datarj.com/einv/QR/zzkj.html";//测试地址等待页面

    public  static final String SUCCESS_REDIRECT_URL = "http://fpj.datarj.com/einv/QR/zzkj.html";//正式地址等待页面

    //德克士开票完成跳转url
    public static final String DICOS_REDIRECT_URL ="http://fpjtest.datarj.com/einv/dicos/witting.html";

    //光唯尚开票完成跳转url
    public static final String GVC_REDIRECT_URL ="http://fpj.datarj.com/einv/GV/witting11.html";

    //一茶一坐营销等待页面
    public static final String YCYZ_REDIRECT_URL ="http://fpjtest.datarj.com/einv/dicos/witting22.html";

    // 测试  发票模板发票详情url
    public  static final String testfpInfoURL ="http://fpjtest.datarj.com/einv/common/fpInfo";
    //正式
    public  static final String fpInfoURL ="http://fpj.datarj.com/einv/common/fpInfo";
    //解码code  URL
    public  static final String decodeURL="https://api.weixin.qq.com/card/code/decrypt?access_token=";

    /**************一次授 权批量插卡************************/
    //获取授权链接
    public static final String HQ_SQ_URL = "https://api.weixin.qq.com/card/invoice/bizbatchgetauthurl?access_token=";
    //查询授权信息
    public static final String BATCH_SQ_STATUS = "https://api.weixin.qq.com/card/invoice/bizbatchgetauthdata?access_token=";
    //拒绝开票
    public static final String BATCH_REFUSE_KP = "Https://api.weixin.qq.com/card/invoice/batchrejectinsert?access_token=";
    //将发票插入用户卡包
    public static final String BATCH_DZFP_IN_CARD = "https://api.weixin.qq.com/card/invoice/batchinsert?access_token=";
    //查询插卡结果
    public static final String BATCH_IN_CARD_RESULT = "https://api.weixin.qq.com/card/invoice/ batchcheckinsertresult?access_token=";

    /**************门店、卡券***************/

    //创建门店url
    public static final String CREAT_STORE_URL="http://api.weixin.qq.com/cgi-bin/poi/addpoi?access_token=";

    //测试 开票平台标识
    public static final String TEST_SPAPPID ="d3g4YzJhNGMyMjg5ZTEwZmZiX8ykqqY-ekKLnDj3KRTgmFqFkztdBlnQH_M6UiEz47RY";

    //正式 容津公众号开票标识
    public static final String RJXX_SPAPPID ="d3g5YWJjNzI5ZTJiNDYzN2VlX0PARqxCKGk0d1fanZfCN3KxU5K6C-9JRLhQXmLzcptB";
}
