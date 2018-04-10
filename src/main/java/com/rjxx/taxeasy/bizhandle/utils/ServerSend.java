package com.rjxx.taxeasy.bizhandle.utils;

import com.rjxx.taxeasy.bizhandle.invoicehandling.DataOperate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.CoreConnectionPNames;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *@ClassName SendalEmail
 *@Description 发送xml数据的方法
 *@Author xlm
 *@Date 2017/1/4.
 *@Version 1.0
 **/
@Service
public class ServerSend {
    @Autowired
    private DataOperate dataOperate;
	/**
	 * 发送到税控服务器
	 *
	 * @param sendMes
	 * @param key
	 * @param xfsh
	 * @param jylsh
	 * @throws Exception
	 */
	public Map send(String sendMes, String url, String key, String xfsh, String jylsh) throws Exception {
		SingleClientConnManager sccm = new SingleClientConnManager();
		HttpClient httpclient = new DefaultHttpClient(sccm);
		HttpPost httppost = new HttpPost(url);
		StringBuffer buffer = new StringBuffer();
		StringEntity myEntity = new StringEntity(sendMes, "GBK");
		httppost.addHeader("Content-Type", "text/xml");
		httppost.setEntity(myEntity);
		HttpResponse response;
		String strMessage = "";
		BufferedReader reader = null;
		Map resultMap = null;
		try {
			//连接超时时间
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,1000*60);
			//数据传输时间
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,1000*60);
			response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			reader = new BufferedReader(new InputStreamReader(resEntity.getContent(), "gbk"));
			while ((strMessage = reader.readLine()) != null) {
				buffer.append(strMessage);
			}
			System.out.println("接收返回值:" + buffer.toString());
			resultMap = handerReturnMes(buffer.toString(),key);
			if (null != resultMap && !resultMap.isEmpty()) {

				int pos = key.indexOf("$");
				if (pos != -1) {
					key = key.substring(0, pos);
				}

				if (resultMap.get("returncode").equals("0000")) {
					dataOperate.saveLog(Integer.valueOf(key), "91", "1", "Send:send",
							"(服务端)发送服务器成功" + resultMap.get("returnmsg").toString(), 2, xfsh, jylsh);
				} else {
					dataOperate.saveLog(Integer.valueOf(key), "92", "1", "Send:send",
							"(服务端)发送服务器失败" + resultMap.get("returnmsg").toString(), 2, xfsh, jylsh);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			int pos = key.indexOf("$");
			if (pos != -1) {
				key = key.substring(0, pos);
			}
			dataOperate.saveLog(Integer.valueOf(key), "92", "1", "Send:send", "(服务端)发送服务器异常" + e.getMessage(), 2, xfsh,
					jylsh);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			int pos = key.indexOf("$");
			if (pos != -1) {
				key = key.substring(0, pos);
			}
			dataOperate.saveLog(Integer.valueOf(key), "92", "1", "Send:send", "(服务端)发送服务器异常" + e.getMessage(), 2, xfsh,
					jylsh);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}

		return resultMap;
	}

	/**
	 * 接收返回报文并做后续处理
	 *
	 * @param returnMes
	 * 
	 * @throws Exception
	 */
	public Map handerReturnMes(String returnMes,String key) throws Exception {

		// 将放回报文字符串转成Document对象
		Document document = DocumentHelper.parseText(returnMes);
		Element root = document.getRootElement();
		List<Element> childElements = root.elements();
		// List resultList = new ArrayList();
		Map resultMap = new HashMap();

		for (Element child : childElements) {
			int pos = key.indexOf("$");
			if (pos != -1) {
				key = key.substring(pos+1);
				System.out.println("传入开票流水号:" + key);
			}
			if (child.elementText("RETURNCODE").equals("0000")) {
				// 返回结果，发票代码
				resultMap.put("FP_DM", child.elementText("FP_DM"));
				// 发票号码
				resultMap.put("FP_HM", child.elementText("FP_HM"));
				// 发票密文
				resultMap.put("FP_MW", child.element("FP_MW").getText());
				// 校验码
				resultMap.put("JYM", child.element("JYM").getText());
				// 二维码
				resultMap.put("EWM", child.element("EWM").getText());
				// 机器编号
				resultMap.put("JQBH", child.element("JQBH").getText());
				resultMap.put("KPRQ", child.element("KPRQ").getText());
				resultMap.put("returncode", child.elementText("RETURNCODE"));
				resultMap.put("returnmsg", child.elementText("RETURNMSG"));
				resultMap.put("KPLSH", key);
			}else{
				resultMap.put("returncode", child.elementText("RETURNCODE"));
				resultMap.put("returnmsg", child.elementText("RETURNMSG"));
				resultMap.put("KPLSH", key);
			}
		}
		return resultMap;
	}
}