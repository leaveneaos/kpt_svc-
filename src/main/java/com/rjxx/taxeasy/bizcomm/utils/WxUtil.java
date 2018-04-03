package com.rjxx.taxeasy.bizcomm.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjxx.comm.utils.ApplicationContextUtils;
import com.rjxx.taxeasy.domains.Csb;
import com.rjxx.taxeasy.domains.Gsxx;
import com.rjxx.taxeasy.domains.Wxfs;
import com.rjxx.taxeasy.domains.Wxkb;
import com.rjxx.taxeasy.service.CsbService;
import com.rjxx.taxeasy.service.GsxxService;
import com.rjxx.taxeasy.service.WxfsService;
import com.rjxx.taxeasy.service.WxkbService;

@Service
public class WxUtil {

	@Autowired
	private WxkbService wxkbService;

	@Autowired
	private CsbService csbService;

	@Autowired
	private GsxxService gsxxService;

	@Autowired
	private WxfsService wxfsService;

	public static final String APP_ID = "wx9abc729e2b4637ee";

	public static final String GET_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";// 获取access

	public static final String SECRET = "6415ee7a53601b6a0e8b4ac194b382eb";

	public static final String SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";

	public String getToken(Gsxx gsxx) {
		String token = null;
		Map<String, Object> params = new HashMap<>();
		params.put("gsdm", gsxx.getGsdm());
		Wxkb wxkb = wxkbService.findOneByParams(params);
		long crtTime = new Date().getTime();
		long lastTime = 0;
		long sub = 0;
		if (wxkb != null) {
			lastTime = wxkb.getScsj().getTime();
		}
		sub = crtTime - lastTime;
		if (wxkb != null && sub < Long.valueOf(wxkb.getExpiresIn()) * 1000) {
			return wxkb.getAccessToken();
		} else {
			if (wxkb == null) {
				wxkb = new Wxkb();
			}
			String turl = GET_TOKEN_URL + "?grant_type=client_credential&appid=" + gsxx.getWxappid() + "&secret=" + gsxx.getWxsecret();
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
						// appid"}

						return "获取微信token失败,错误代码为" + map.get("errcode");
					} else {// 正常情况下{"access_token":"ACCESS_TOKEN","expires_in":7200}
						wxkb.setAccessToken((String) map.get("access_token"));
						wxkb.setExpiresIn(map.get("expires_in").toString());
						wxkb.setScsj(new Date());
						wxkbService.save(wxkb);
						token = wxkb.getAccessToken();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// 关闭连接 ,释放资源
				client.getConnectionManager().shutdown();
			}
		}

		return token;
	}

	public String sendMessage(String openid, String xfmc, String gfmc, String spmc, double fpje, String kprq,
			Integer djh) {
		String msg = "";
		// String param = " {touser:" + openid
		// +
		// ",template_id:Rdatmf7JV1J-AJhSD1sdBpV1OWuTpwQ5QLmjFVLCHIU,url:data:{first:
		// {value:您的发票已开具，请在发票夹中查看,color:#173177},keynote1: {value:" + xfmc
		// + ",color:#173177},keynote2:{value:" + gfmc +
		// ",color:#173177},keynote3: {value:" + fplx
		// + "元,color:#173177},keynote4: {value:" + fpje +
		// ",color:#173177},keynote5: {value:" + kprq +
		// ",color:#173177},remark:{value:" + bz
		// + ",color:#173177 } }";
		// String result = WeixinUtil.httpRequest(SEND_URL + getToken(), "POST",
		// param);
		HttpClient client = new DefaultHttpClient();
		Map<String, Object> prms = new HashMap<>();
		prms.put("djh", djh);
		String gsdm = null;
		Gsxx gsxx = gsxxService.findOneByDjh(prms);
		if (gsxx == null) {
			gsxx = new Gsxx();
			gsxx.setWxappid(APP_ID);
			gsxx.setWxsecret(SECRET);
			gsxx.setGsdm("rjxx");
		} else if (gsxx != null && (gsxx.getWxappid() == null || gsxx.getWxsecret() == null
				|| "".equals(gsxx.getWxappid()) || "".equals(gsxx.getWxsecret()))) {
			gsdm = gsxx.getGsdm();
			gsxx.setWxappid(APP_ID);
			gsxx.setWxsecret(SECRET);
			gsxx.setGsdm("rjxx");
		}else{
			gsdm = gsxx.getGsdm();
		}
		Map map = new HashMap<>();
		HttpPost httpPost = new HttpPost(SEND_URL + getToken(gsxx));
		ObjectMapper mapper = new ObjectMapper();
		Map<Object, Object> data = new HashMap<>();
		data.put("touser", openid);
		Map<String, Object> params = new HashMap<>();
		params.put("csm", "mbxxurl");
		Csb cs = csbService.findOneByParams(params);
		params.put("csm", "mbxxmbid");
		Csb csb = csbService.findOneByParams(params);
		data.put("template_id", csb.getMrz());
		data.put("url", cs.getMrz() + "?djh=" + djh);
		Map<Object, Object> data1 = new HashMap<>();
		Map<Object, Object> data2 = new HashMap<>();
		data2.put("value", "您收到一笔由“泰易电子发票平台”开具的电子发票。详细信息如下：");
		data2.put("color", "#173177");
		data1.put("first", data2);
		data2 = new HashMap<>();
		data2.put("value", xfmc);
		data2.put("color", "#173177");
		data1.put("keyword1", data2);
		data2 = new HashMap<>();
		data2.put("value", gfmc);
		data2.put("color", "#173177");
		data1.put("keyword2", data2);
		data2 = new HashMap<>();
		data2.put("value", spmc);
		data2.put("color", "#173177");
		data1.put("keyword3", data2);
		data2 = new HashMap<>();
		DecimalFormat d1 = new DecimalFormat("#,##0.00");
		data2.put("value", "￥" + d1.format(fpje));
		data2.put("color", "#173177");
		data1.put("keyword4", data2);
		data2 = new HashMap<>();
		data2.put("value", kprq);
		data2.put("color", "#173177");
		data1.put("keyword5", data2);
		data2 = new HashMap<>();
		data2.put("value", "点击查看详情及发票预览");
		data2.put("color", "#173177");
		data1.put("remark", data2);
		data.put("data", data1);
		Wxfs wx = new Wxfs();
		try {
			String json = mapper.writeValueAsString(data);
			httpPost.setEntity(new StringEntity(json, "UTF-8"));
			HttpResponse response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String responseContent = EntityUtils.toString(entity, "UTF-8");
			ObjectMapper jsonparer = new ObjectMapper();
			map = jsonparer.readValue(responseContent, Map.class);
			if (map.get("errmsg").equals("ok")) {
				msg = "成功";
			} else {
				msg = "失败";
			}
			wx.setReturnmsg(String.valueOf(map.get("errmsg")));
		} catch (Exception e) {
			e.printStackTrace();
			msg = "失败";
		}
		wx.setDjh(djh);
		wx.setGsdm(gsdm);
		wx.setIssuccess(msg);
		wx.setOpenid(openid);
		wx.setLrsj(new Date());
		wxfsService.save(wx);
		// ObjectMapper map = new ObjectMapper();
		// try {
		// Map res = XmlUtil.xml2Map(result);
		// if (res.get("Status").equals("success")) {
		// msg = "成功";
		// }else if (res.get("Status").toString().contains("block")) {
		// msg = "拒绝接收";
		// }else{
		// msg = "失败";
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// msg = "失败";
		// }

		return msg;
	}
}
