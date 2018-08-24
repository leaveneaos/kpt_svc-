package com.rjxx.taxeasy.bizhandle.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rjxx.taxeasy.config.rabbitmq.RabbitmqUtils;
import com.rjxx.taxeasy.dal.*;
import com.rjxx.taxeasy.dao.bo.*;
import com.rjxx.taxeasy.dao.dto.*;
import com.rjxx.taxeasy.dao.dto.adapter.*;
import com.rjxx.taxeasy.dao.orm.KpspmxJpaDao;
import com.rjxx.taxeasy.dao.orm.XfJpaDao;
import com.rjxx.utils.SignUtils;
import com.rjxx.utils.StringUtils;
import com.rjxx.utils.XmlJaxbUtils;
import com.rjxx.utils.XmltoJson;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: zsq
 * @date: 2018/7/24 18:20
 * @describe: 发票回写util
 */
@Service
public class FphxUtil {

    private static Logger logger = LoggerFactory.getLogger(FphxUtil.class);
    @Autowired
    private JyxxsqService jyxxsqService;
    @Autowired
    private CszbService cszbService;
    @Autowired
    private FphxwsjlService fphxwsjlService;
    @Autowired
    private RabbitmqUtils rabbitmqSend;
    @Autowired
    private KplsService kplsService;
    @Autowired
    private JylsService jylsService;
    @Autowired
    private GsxxService gsxxService;
    @Autowired
    private KpspmxJpaDao kpspmxJpaDao;
    @Autowired
    private XfJpaDao xfJpaDao;

    public boolean fphx(Kpls kpls ,Jyls jyls,Gsxx gsxx) throws Exception{
        boolean b =true;
        Integer kplsh = kpls.getKplsh();
        String url = gsxx.getCallbackurl();
        if (!("").equals(url) && url != null) {
            String returnmessage = "";
            if (kpls.getFpzldm().equals("12") && kpls.getGsdm().equals("Family")) {
                returnmessage = this.CreateReturnMessage2(kpls.getKplsh());
            } else if (kpls.getFpzldm().equals("12") && kpls.getGsdm().equals("mcake")) {
                returnmessage = this.CreateReturnMessage(kpls.getKplsh());
            }else if (kpls.getFpzldm().equals("12") && (kpls.getGsdm().equals("fwk")||kpls.getGsdm().equals("bqw"))) {
                returnmessage = this.CreateReturnMessage3(kpls.getKplsh());
            }else {
//                        returnmessage = this.CreateReturnMessage(kpls.getKplsh());
                Cszb cszb = cszbService.getSpbmbbh(gsxx.getGsdm(), null, null, "callBackType");
                String callbacktype = cszb.getCsz();
                if(callbacktype!=null){
                    if("9".equals(callbacktype)){
                        returnmessage = this.createJsonMsg(kpls.getKplsh());
                    }
                }else{
                    returnmessage = this.CreateReturnMessage(kpls.getKplsh());
                }
            }
            //输出调用结果
            logger.info("回写报文" + returnmessage);
            if (returnmessage != null && !"".equals(returnmessage)) {
                if(kpls.getGsdm().equals("afb")){
                    Map returnMap = this.httpPostNoSign(returnmessage, kpls);
                    logger.info("返回报文" + JSON.toJSONString(returnMap));
                }if(kpls.getGsdm().equals("fwk")){
                    try {
                        String ss = this.netWebService(url, "CallBack", returnmessage, gsxx.getAppKey(), gsxx.getSecretKey());
                        String fwkReturnMessageStr = fwkReturnMessage(kpls);
                        logger.info("----------sap回写报文----------" + fwkReturnMessageStr);
                        String Data = HttpUtils.doPostSoap1_2(gsxx.getSapcallbackurl(), fwkReturnMessageStr, null, "clouder", "Welcome5");
                        logger.info("----------fwk平台回写返回报文--------" + ss);
                        logger.info("----------sap回写返回报文----------" + Data);
                        if(StringUtils.isBlank(ss) || StringUtils.isBlank(Data)){
                            logger.info("sap、fwk--回写返回为空，放入mq---"+kpls.getKplsh() + "_1");
                            Map map = new HashMap();
                            map.put("kplsh", kplsh);
                            map.put("gsdm", kpls.getGsdm());
                            //存在就更新
                            Fphxwsjl fphxwsjl1 = fphxwsjlService.findOneByParams(map);
                            if (fphxwsjl1 != null) {
                                fphxwsjl1.setEnddate(new Date());
                                fphxwsjl1.setReturncode("9999");
                                fphxwsjl1.setReturnmessage("回写失败，返回数据为空");
                                fphxwsjlService.save(fphxwsjl1);
                            } else {
                                Fphxwsjl fphxwsjl2 = new Fphxwsjl();
                                fphxwsjl2.setGsdm("fwk");
                                fphxwsjl2.setXfid(kpls.getXfid());
                                fphxwsjl2.setSkpid(kpls.getSkpid());
                                fphxwsjl2.setKplsh(kplsh);
                                fphxwsjl2.setDdh(jyls.getDdh());
                                fphxwsjl2.setEnddate(new Date());
                                fphxwsjl2.setReturncode("9999");
                                fphxwsjl2.setStartdate(new Date());
                                fphxwsjl2.setSecretKey("");
                                fphxwsjl2.setSign("");
                                fphxwsjl2.setWsurl(gsxx.getSapcallbackurl());
                                fphxwsjl2.setReturncontent(fwkReturnMessageStr);
                                fphxwsjl2.setReturnmessage(fwkReturnMessageStr);
                                fphxwsjlService.save(fphxwsjl2);
                            }
                            rabbitmqSend.sendMsg("ErrorException_Callback", kpls.getFpzldm(), kpls.getKplsh() + "_1");
                        }else {
                            Map resultMap = handerReturnMes(ss);
                            String returnCode = resultMap.get("ReturnCode").toString();
                            //解析sap返回值
                            String note = "";
                            try {
                                String jsonString= XmltoJson.xml2json(Data);
                                Map dataMap=XmltoJson.strJson2Map(jsonString);
                                Map Envelope=(Map)dataMap.get("env:Envelope");
                                Map Body=(Map)Envelope.get("env:Body");
                                Map GoldenTaxGoldenTaxCreateConfirmation_sync=(Map)Body.get("n0:GoldenTaxGoldenTaxCreateConfirmation_sync");
                                Map Log=(Map)GoldenTaxGoldenTaxCreateConfirmation_sync.get("Log");
                                Map item = (Map)Log.get("Item");
                                note = (String) item.get("Note");
                            } catch (Exception e) {
                                logger.info("解析sap失败");
                            }
                            //fwk 、sap 不成功
                            if((StringUtils.isBlank(note)|| !"Create operation was successful".equals(note))||(StringUtils.isBlank(returnCode)|| !"0000".equals(returnCode))){
                                logger.info("sap--回写返回不成功或者 fwk回写返回不成功，放入mq---"+kpls.getKplsh() + "_1");
                                rabbitmqSend.sendMsg("ErrorException_Callback", kpls.getFpzldm(), kpls.getKplsh() + "_1");
                            }
                            Map map = new HashMap();
                            map.put("kplsh", kplsh);
                            map.put("gsdm", kpls.getGsdm());
                            Fphxwsjl fphxwsjl3 = fphxwsjlService.findOneByParams(map);
                            //更新
                            if (fphxwsjl3 != null) {
                                if ((StringUtils.isBlank(note) || !"Create operation was successful".equals(note)) || (StringUtils.isBlank(returnCode) || !"0000".equals(returnCode))) {
                                    fphxwsjl3.setReturncode("9999");
                                } else {
                                    fphxwsjl3.setReturncode("0000");
                                }
                                fphxwsjl3.setEnddate(new Date());
                                fphxwsjl3.setReturnmessage(Data);
                                fphxwsjl3.setReturncontent(fwkReturnMessageStr);
                                fphxwsjlService.save(fphxwsjl3);
                            } else {
                                Fphxwsjl fphxwsjl4 = new Fphxwsjl();
                                fphxwsjl4.setGsdm("fwk");
                                fphxwsjl4.setXfid(kpls.getXfid());
                                fphxwsjl4.setSkpid(kpls.getSkpid());
                                fphxwsjl4.setKplsh(kplsh);
                                fphxwsjl4.setDdh(jyls.getDdh());
                                fphxwsjl4.setEnddate(new Date());
                                if ((StringUtils.isBlank(returnCode) || !"0000".equals(returnCode)) || (StringUtils.isBlank(note) || !"Create operation was successful".equals(note))) {
                                    fphxwsjl4.setReturncode("9999");
                                } else {
                                    fphxwsjl4.setReturncode("0000");
                                }
                                fphxwsjl4.setStartdate(new Date());
                                fphxwsjl4.setSecretKey("");
                                fphxwsjl4.setSign("");
                                fphxwsjl4.setWsurl(gsxx.getSapcallbackurl());
                                fphxwsjl4.setReturncontent(fwkReturnMessageStr);
                                fphxwsjl4.setReturnmessage(Data);
                                fphxwsjlService.save(fphxwsjl4);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        rabbitmqSend.sendMsg("ErrorException_Callback", kpls.getFpzldm(), kpls.getKplsh() + "");
                    }
                } else{
                    try{
                        Map returnMap = this.httpPost(returnmessage, kpls);
                        logger.info("返回报文" + JSON.toJSONString(returnMap));
                        String Secret = getSign(returnmessage, gsxx.getSecretKey());
                        if(returnMap==null || returnMap.get("ReturnCode")==null){
                            logger.info("回写返回为空，放入mq---"+kpls.getKplsh() + "_1");
                            Map map = new HashMap();
                            map.put("kplsh", kplsh);
                            map.put("gsdm", kpls.getGsdm());
                            Fphxwsjl fphxwsjl5 = fphxwsjlService.findOneByParams(map);
                            if (fphxwsjl5 != null) {
                                fphxwsjl5.setEnddate(new Date());
                                fphxwsjl5.setReturncode("9999");
                                fphxwsjl5.setReturnmessage("回写失败，返回数据为空");
                                fphxwsjlService.save(fphxwsjl5);
                            } else {
                                Fphxwsjl fphxwsj6 = new Fphxwsjl();
                                fphxwsj6.setGsdm(kpls.getGsdm());
                                fphxwsj6.setKplsh(kplsh);
                                fphxwsj6.setXfid(kpls.getXfid());
                                fphxwsj6.setSkpid(kpls.getSkpid());
                                fphxwsj6.setDdh(jyls.getDdh());
                                fphxwsj6.setEnddate(new Date());
                                fphxwsj6.setReturncode("9999");
                                fphxwsj6.setStartdate(new Date());
                                fphxwsj6.setSecretKey(gsxx.getSecretKey());
                                fphxwsj6.setSign(Secret);
                                fphxwsj6.setWsurl(gsxx.getCallbackurl());
                                fphxwsj6.setReturncontent(returnmessage);
                                fphxwsj6.setReturnmessage("回写失败，返回数据为空");
                                fphxwsjlService.save(fphxwsj6);
                            }
                            rabbitmqSend.sendMsg("ErrorException_Callback", kpls.getFpzldm(), kpls.getKplsh() + "_1");
                        }else {
                            String returnCode = returnMap.get("ReturnCode").toString();
                            String returnMessage = returnMap.get("ReturnMessage").toString();
                            //回写失败放入mq
                            if(StringUtils.isBlank(returnCode)|| (!"0000".equals(returnCode) && !"0".equals(returnCode))){
                                logger.info("回写返回不成功，放入mq---"+kpls.getKplsh() + "_1");
                                rabbitmqSend.sendMsg("ErrorException_Callback", kpls.getFpzldm(), kpls.getKplsh() + "_1");
                            }
                            Map map = new HashMap();
                            map.put("kplsh", kplsh);
                            map.put("gsdm", kpls.getGsdm());
                            Fphxwsjl fphxwsjl7 = fphxwsjlService.findOneByParams(map);
                            if (fphxwsjl7 != null) {
                                if (StringUtils.isBlank(returnCode) || (!"0000".equals(returnCode) && !"0".equals(returnCode))) {
                                    fphxwsjl7.setReturncode("9999");
                                } else {
                                    fphxwsjl7.setReturncode("0000");
                                }
                                fphxwsjl7.setEnddate(new Date());
                                fphxwsjl7.setReturnmessage(returnmessage);
                                fphxwsjlService.save(fphxwsjl7);
                            } else {
                                Fphxwsjl fphxwsjl8 = new Fphxwsjl();
                                fphxwsjl8.setGsdm(kpls.getGsdm());
                                fphxwsjl8.setKplsh(kplsh);
                                fphxwsjl8.setXfid(kpls.getXfid());
                                fphxwsjl8.setSkpid(kpls.getSkpid());
                                fphxwsjl8.setDdh(jyls.getDdh());
                                fphxwsjl8.setEnddate(new Date());
                                if (StringUtils.isBlank(returnCode) || (!"0000".equals(returnCode) && !"0".equals(returnCode))) {
                                    fphxwsjl8.setReturncode("9999");
                                } else {
                                    fphxwsjl8.setReturncode("0000");
                                }
                                fphxwsjl8.setStartdate(new Date());
                                fphxwsjl8.setSecretKey(gsxx.getSecretKey());
                                fphxwsjl8.setSign(Secret);
                                fphxwsjl8.setWsurl(gsxx.getCallbackurl());
                                fphxwsjl8.setReturncontent(returnmessage);
                                fphxwsjl8.setReturnmessage(returnMessage);
                                fphxwsjlService.save(fphxwsjl8);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        rabbitmqSend.sendMsg("ErrorException_Callback", kpls.getFpzldm(), kpls.getKplsh() + "_1");
                    }
                }
            }
        }
        return b;
    }



    public Map httpPostNoSign(String returnmessage, Kpls kpls) {
        Map parms=new HashMap();
        parms.put("gsdm",kpls.getGsdm());
        Gsxx gsxx=gsxxService.findOneByParams(parms);
        String url=gsxx.getCallbackurl();
        String strMessage = "";
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        Map resultMap = null;
        if(!("").equals(url)&&url!=null){
            HttpPost httpPost = new HttpPost(url);
            CloseableHttpResponse response = null;
            RequestConfig requestConfig = RequestConfig.custom().
                    setSocketTimeout(120*1000).setConnectionRequestTimeout(120*1000).setConnectTimeout(120*1000).build();
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build();
            httpPost.addHeader("Content-Type", "application/json");
            try {
                Map nvps = new HashMap();
                nvps.put("invoiceData", returnmessage);
                StringEntity requestEntity = new StringEntity(JSON.toJSONString(nvps), "utf-8");
                logger.info("-------数据-----"+JSON.toJSONString(nvps));
                httpPost.setEntity(requestEntity);
                response = httpClient.execute(httpPost, new BasicHttpContext());
                if (response.getStatusLine().getStatusCode() != 200) {
                    System.out.println("request url failed, http code=" + response.getStatusLine().getStatusCode()
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
                System.out.println("接收返回值:" + buffer.toString());
                resultMap = handerReturnMes(buffer.toString());
                String returnCode=resultMap.get("ReturnCode").toString();
                String ReturnMessage=resultMap.get("ReturnMessage").toString();
                Fphxwsjl fphxwsjl=new Fphxwsjl();
                fphxwsjl.setGsdm(kpls.getGsdm());
                fphxwsjl.setEnddate(new Date());
                fphxwsjl.setReturncode(returnCode);
                fphxwsjl.setStartdate(new Date());
                fphxwsjl.setSecretKey(gsxx.getSecretKey());
                fphxwsjl.setSign("");
                fphxwsjl.setWsurl(gsxx.getCallbackurl());
                fphxwsjl.setReturncontent(returnmessage);
                fphxwsjl.setReturnmessage(ReturnMessage);
                fphxwsjlService.save(fphxwsjl);
            } catch (IOException e) {
                System.out.println("request url=" + url + ", exception, msg=" + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (response != null){
                    try {
                        response.close();
                        httpClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return resultMap;
    }

    public static void main(String[] args) {
        String s= getSign("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Request>\n" +
                "    <ExtractCode>081820888801000000837</ExtractCode>\n" +
                "    <InvoiceItems count=\"1\">\n" +
                "        <InvoiceItem>\n" +
                "            <InvoiceCode>031001700111</InvoiceCode>\n" +
                "            <InvoiceNumber>97369803</InvoiceNumber>\n" +
                "            <InvoiceDate>20170818155605</InvoiceDate>\n" +
                "            <InvoiceStatus>正常发票</InvoiceStatus>\n" +
                "            <Amount>4.27</Amount>\n" +
                "            <TaxAmount>0.73</TaxAmount>\n" +
                "            <PdfUrl>http://test.datarj.com/e-invoice-file/9131000071785090X1/20170818/bc9f7e58-e5f6-46b3-822a-e13e2c04ff19.pdf</PdfUrl>\n" +
                "        </InvoiceItem>\n" +
                "    </InvoiceItems>\n" +
                "</Request>\n","bd79b66f566b5e2de07f1807c56b2469");
        System.out.println(s);

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
    public String netWebService (String url,String methodName,String QueryData,String AppId,String key){
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
//            Map resultMap=new HashMap();
//            resultMap = handerReturnMes(result.toString());
//            String returnCode=resultMap.get("ReturnCode").toString();
//            String ReturnMessage=resultMap.get("ReturnMessage").toString();
//            Fphxwsjl fphxwsjl=new Fphxwsjl();
//            fphxwsjl.setGsdm("fwk");
//            fphxwsjl.setEnddate(new Date());
//            fphxwsjl.setReturncode(returnCode);
//            fphxwsjl.setStartdate(new Date());
//            fphxwsjl.setSecretKey(key);
//            fphxwsjl.setSign(sign);
//            fphxwsjl.setWsurl(url);
//            fphxwsjl.setReturncontent(QueryData);
//            fphxwsjl.setReturnmessage(ReturnMessage);
//            fphxwsjlService.save(fphxwsjl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public String   fwkReturnMessage(Kpls kpls) {
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
        String result="Succeed";
        if(kpls.getFpczlxdm().equals("12")){
            result="CancelSucceed";
        }
        String ss="\n" +
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:glob=\"http://sap.com/xi/SAPGlobal20/Global\">\n" +
                "   <soap:Header/>\n" +
                "   <soap:Body>\n" +
                "      <glob:GoldenTaxGoldenTaxCreateRequest_sync>\n" +
                "         <BasicMessageHeader></BasicMessageHeader>\n" +
                "         <GoldenTax>\n" +
                "            <CutInvID>"+kpls.getJylsh()+"</CutInvID>\n" +
                "            <GoldenTaxID>"+kpls.getFphm()+"</GoldenTaxID>\n" +
                "            <GoldenTaxDate>"+sim.format(kpls.getKprq())+"</GoldenTaxDate>\n"+
                "            <GoldenTaxResult>"+result+"</GoldenTaxResult>\n" +
                "            <GoldenTaxCode>"+kpls.getFpdm()+"</GoldenTaxCode>\n" +
                "         </GoldenTax>\n" +
                "      </glob:GoldenTaxGoldenTaxCreateRequest_sync>\n" +
                "   </soap:Body>\n" +
                "</soap:Envelope>";
        return ss;
    }
    public String CreateReturnMessage(Integer kplsh) {

        String Message="";
        Kpls kpls=kplsService.findOne(kplsh);
        Integer djh = kpls.getDjh();
        Map param4 = new HashMap<>();
        param4.put("djh", djh);
        Jyls jyls = jylsService.findJylsByDjh(param4);
        /**
         * 查询原交易流水得ddh
         */
        String ddh = jyls.getDdh();
        Map params=new HashMap();
        params.put("gsdm",kpls.getGsdm());
        params.put("serialorder",kpls.getSerialorder());
        List<Kpls> kplsList=kplsService.findKplsBySerialOrder(params);
        if(kpls.getFpczlxdm().equals("11")){
            ReturnData returnData=new ReturnData();
            OperationItem operationItem=new OperationItem();
            operationItem.setSerialNumber(kpls.getJylsh());
            operationItem.setOrderNumber(ddh);
            operationItem.setOperationType(kpls.getFpczlxdm());
            InvoiceItems invoiceItems=new InvoiceItems();
            List<InvoiceItem> invoiceItemList=new ArrayList<>();
            boolean f=true;
            for(int i=0;i<kplsList.size();i++){
                Kpls kpls2=kplsList.get(i);
                if(kpls2.getFpztdm().equals("00")||kpls2.getFpztdm().equals("05")){
                    InvoiceItem invoiceItem=new InvoiceItem();
                    if(kpls2.getFpztdm().equals("00")){
                        invoiceItem.setReturnCode("0000");
                        invoiceItem.setInvoiceStatus("正常发票");
                        invoiceItem.setReturnMessage("");
                    }else if(kpls2.getFpztdm().equals("05")){
                        invoiceItem.setReturnCode("9999");
                        invoiceItem.setInvoiceStatus("开具失败");
                        invoiceItem.setReturnMessage(kpls2.getErrorReason());
                    }
                    invoiceItem.setInvoiceCode(kpls2.getFpdm());
                    invoiceItem.setInvoiceNumber(kpls2.getFphm());
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
                    if(kpls2.getKprq()==null){
                        invoiceItem.setInvoiceDate(sdf.format(new Date()));
                    }else{
                        invoiceItem.setInvoiceDate(sdf.format(kpls2.getKprq()));
                    }
                    invoiceItem.setAmount(kpls2.getHjje().toString());
                    invoiceItem.setTaxAmount(kpls2.getHjse().toString());
                    invoiceItem.setPdfUrl(kpls2.getPdfurl());
                    invoiceItemList.add(invoiceItem);
                }else{
                    f=false;
                    break;
                }
            }
            invoiceItems.setCount(invoiceItemList.size());
            invoiceItems.setInvoiceItem(invoiceItemList);
            returnData.setOperationItem(operationItem);
            returnData.setInvoiceItems(invoiceItems);
            if(f){
                Message= XmlJaxbUtils.toXml(returnData);
            }else{
                Message="";
            }
        }else if(kpls.getFpczlxdm().equals("12")){
            ReturnData returnData=new ReturnData();
            OperationItem operationItem=new OperationItem();
            operationItem.setSerialNumber(kpls.getJylsh());
            operationItem.setOrderNumber(ddh);
            operationItem.setOperationType(kpls.getFpczlxdm());
            InvoiceItems invoiceItems=new InvoiceItems();
            List<InvoiceItem> invoiceItemList=new ArrayList<>();

            InvoiceItem invoiceItem=new InvoiceItem();
            if(kpls.getFpztdm().equals("00")){
                invoiceItem.setReturnCode("0000");
                invoiceItem.setInvoiceStatus("正常发票");
                invoiceItem.setReturnMessage("");
            }else if(kpls.getFpztdm().equals("05")){
                invoiceItem.setReturnCode("9999");
                invoiceItem.setInvoiceStatus("开具失败");
                invoiceItem.setReturnMessage(kpls.getErrorReason());
            }
            invoiceItem.setInvoiceCode(kpls.getFpdm());
            invoiceItem.setInvoiceNumber(kpls.getFphm());
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
            if(kpls.getKprq()==null){
                invoiceItem.setInvoiceDate(sdf.format(new Date()));
            }else{
                invoiceItem.setInvoiceDate(sdf.format(kpls.getKprq()));
            }
            invoiceItem.setAmount(kpls.getHjje().toString());
            invoiceItem.setTaxAmount(kpls.getHjse().toString());
            invoiceItem.setPdfUrl(kpls.getPdfurl());
            Kpls parms=new Kpls();
            parms.setFpdm(kpls.getHzyfpdm());
            parms.setFphm(kpls.getHzyfphm());
            Kpls ypkpls = kplsService.findByfphm(parms);
            InvoiceItem ypinvoiceItem=new InvoiceItem();
            if(ypkpls.getFpztdm().equals("02")){
                ypinvoiceItem.setReturnCode("0000");
                ypinvoiceItem.setInvoiceStatus("已红冲");
                ypinvoiceItem.setReturnMessage("");
            }else if(ypkpls.getFpztdm().equals("00")){
                ypinvoiceItem.setReturnCode("0000");
                ypinvoiceItem.setInvoiceStatus("正常发票");
                ypinvoiceItem.setReturnMessage("");
            }
            ypinvoiceItem.setInvoiceCode(ypkpls.getFpdm());
            ypinvoiceItem.setInvoiceNumber(ypkpls.getFphm());
            if(ypkpls.getKprq()==null){
                ypinvoiceItem.setInvoiceDate(sdf.format(new Date()));
            }else{
                ypinvoiceItem.setInvoiceDate(sdf.format(ypkpls.getKprq()));
            }
            ypinvoiceItem.setAmount(ypkpls.getHjje().toString());
            ypinvoiceItem.setTaxAmount(ypkpls.getHjse().toString());
            ypinvoiceItem.setPdfUrl(ypkpls.getPdfurl());
            invoiceItemList.add(invoiceItem);
            invoiceItemList.add(ypinvoiceItem);
            invoiceItems.setCount(invoiceItemList.size());
            invoiceItems.setInvoiceItem(invoiceItemList);
            returnData.setOperationItem(operationItem);
            returnData.setInvoiceItems(invoiceItems);
            Message=XmlJaxbUtils.toXml(returnData);

        }else if(kpls.getFpczlxdm().equals("14")){

            Map parameter =new HashMap();
            parameter.put("fpdm",kpls.getFpdm());
            parameter.put("fphm",kpls.getFphm());
            Jyxxsq jyxxsq=jyxxsqService.findOneByParams(parameter);

            ReturnData returnData=new ReturnData();
            OperationItem operationItem=new OperationItem();
            operationItem.setSerialNumber(jyxxsq.getJylsh());
            operationItem.setOrderNumber(jyxxsq.getDdh());
            operationItem.setOperationType(jyxxsq.getFpczlxdm());
            InvoiceItems invoiceItems=new InvoiceItems();
            List<InvoiceItem> invoiceItemList=new ArrayList<>();

            InvoiceItem invoiceItem=new InvoiceItem();
            if(kpls.getFpztdm().equals("08")){
                invoiceItem.setReturnCode("0000");
                invoiceItem.setInvoiceStatus("已作废");
                invoiceItem.setReturnMessage("");
            }else {
                invoiceItem.setReturnCode("9999");
                invoiceItem.setInvoiceStatus("开具失败");
                invoiceItem.setReturnMessage(kpls.getErrorReason());
            }
            invoiceItem.setInvoiceCode(kpls.getFpdm());
            invoiceItem.setInvoiceNumber(kpls.getFphm());
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");

            if(kpls.getZfrq()==null){
                invoiceItem.setInvoiceDate(sdf.format(new Date()));
            }else{
                invoiceItem.setInvoiceDate(sdf.format(kpls.getZfrq()));
            }
            invoiceItem.setAmount(kpls.getHjje().toString());
            invoiceItem.setTaxAmount(kpls.getHjse().toString());
            invoiceItem.setPdfUrl(kpls.getPdfurl());
            invoiceItemList.add(invoiceItem);

            invoiceItems.setCount(invoiceItemList.size());
            invoiceItems.setInvoiceItem(invoiceItemList);
            returnData.setOperationItem(operationItem);
            returnData.setInvoiceItems(invoiceItems);
            Message=XmlJaxbUtils.toXml(returnData);

        }
        return Message;
    }
    public String CreateReturnMessage3(Integer kplsh) {

        String Message="";
        Kpls kpls=kplsService.findOne(kplsh);
        Integer djh = kpls.getDjh();
        Map param4 = new HashMap<>();
        param4.put("djh", djh);
        Jyls jyls = jylsService.findJylsByDjh(param4);
        /**
         * 查询原交易流水得ddh
         */
        String ddh = jyls.getDdh();
        Map params=new HashMap(2);
        params.put("gsdm",kpls.getGsdm());
        params.put("serialorder",kpls.getSerialorder());
        List<Kpls> kplsList=kplsService.findKplsBySerialOrder(params);
        if(kpls.getFpczlxdm().equals("11")){
            ReturnData3 returnData=new ReturnData3();
            if(kpls.getGsdm().equals("bqw")){
                Buyer buyer=new Buyer();
                buyer.setIdentifier(kpls.getGfsh());
                buyer.setName(kpls.getGfmc());
                buyer.setAddress(kpls.getGfdz());
                buyer.setTelephoneNo(kpls.getGfdh());
                buyer.setBank(kpls.getGfyh());
                buyer.setBankAcc(kpls.getGfyhzh());
                Seller seller=new Seller();
                seller.setIdentifier(kpls.getXfsh());
                seller.setName(kpls.getXfmc());
                seller.setAddress(kpls.getXfdz());
                seller.setTelephoneNo(kpls.getXfdh());
                seller.setBank(kpls.getXfyh());
                seller.setBankAcc(kpls.getXfyhzh());
                returnData.setBuyer(buyer);
                returnData.setSeller(seller);
            }
            OperationItem3 operationItem=new OperationItem3();
            operationItem.setSerialNumber(kpls.getJylsh());
            operationItem.setOrderNumber(ddh);
            operationItem.setOperationType(kpls.getFpczlxdm());
            if(kpls.getGsdm().equals("fwk")){
                operationItem.setBuyerID(jyls.getBz());
            }
            InvoiceItems3 invoiceItems=new InvoiceItems3();
            List<InvoiceItem3> invoiceItemList=new ArrayList<>();
            boolean f=true;
            for(int i=0;i<kplsList.size();i++){
                Kpls kpls2=kplsList.get(i);
                if(kpls2.getFpztdm().equals("00")||kpls2.getFpztdm().equals("05")){
                    InvoiceItem3 invoiceItem=new InvoiceItem3();
                    if(kpls2.getFpztdm().equals("00")){
                        invoiceItem.setReturnCode("0000");
                        invoiceItem.setInvoiceStatus("00");
                        invoiceItem.setReturnMessage("");
                    }else if(kpls2.getFpztdm().equals("05")){
                        invoiceItem.setReturnCode("9999");
                        invoiceItem.setInvoiceStatus("05");
                        invoiceItem.setReturnMessage(kpls2.getErrorReason());
                    }
                    invoiceItem.setInvoiceCode(kpls2.getFpdm());
                    invoiceItem.setInvoiceNumber(kpls2.getFphm());
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
                    if(kpls2.getKprq()==null){
                        invoiceItem.setInvoiceDate(sdf.format(new Date()));
                    }else{
                        invoiceItem.setInvoiceDate(sdf.format(kpls2.getKprq()));
                    }
                    invoiceItem.setAmount(kpls2.getHjje().toString());
                    invoiceItem.setTaxAmount(kpls2.getHjse().toString());
                    invoiceItem.setPdfUrl(kpls2.getPdfurl());
                    invoiceItem.setExtractCode(jyls.getTqm());
                    invoiceItemList.add(invoiceItem);
                }else{
                    f=false;
                    break;
                }
            }
            invoiceItems.setCount(invoiceItemList.size());
            invoiceItems.setInvoiceItem(invoiceItemList);
            returnData.setOperationItem(operationItem);
            returnData.setInvoiceItems(invoiceItems);
            if(f){
                Message=XmlJaxbUtils.toXml(returnData);
            }else{
                Message="";
            }
        }else if(kpls.getFpczlxdm().equals("12")){
            ReturnData3 returnData=new ReturnData3();
            OperationItem3 operationItem=new OperationItem3();
            operationItem.setSerialNumber(kpls.getJylsh());
            operationItem.setOrderNumber(ddh);
            operationItem.setOperationType(kpls.getFpczlxdm());
            operationItem.setBuyerID(jyls.getBz());
            InvoiceItems3 invoiceItems=new InvoiceItems3();
            List<InvoiceItem3> invoiceItemList=new ArrayList<>();

            InvoiceItem3 invoiceItem=new InvoiceItem3();
            if(kpls.getFpztdm().equals("00")){
                invoiceItem.setReturnCode("0000");
                invoiceItem.setInvoiceStatus("00");
                invoiceItem.setReturnMessage("");
            }else if(kpls.getFpztdm().equals("05")){
                invoiceItem.setReturnCode("9999");
                invoiceItem.setInvoiceStatus("05");
                invoiceItem.setReturnMessage(kpls.getErrorReason());
            }
            invoiceItem.setInvoiceCode(kpls.getFpdm());
            invoiceItem.setInvoiceNumber(kpls.getFphm());
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
            if(kpls.getKprq()==null){
                invoiceItem.setInvoiceDate(sdf.format(new Date()));
            }else{
                invoiceItem.setInvoiceDate(sdf.format(kpls.getKprq()));
            }
            invoiceItem.setAmount(kpls.getHjje().toString());
            invoiceItem.setTaxAmount(kpls.getHjse().toString());
            invoiceItem.setPdfUrl(kpls.getPdfurl());
            invoiceItem.setExtractCode(jyls.getTqm());
            Kpls parms=new Kpls();
            parms.setFphm(kpls.getHzyfphm());
            parms.setFpdm(kpls.getHzyfpdm());
            Kpls ypkpls=kplsService.findByfphm(parms);
            InvoiceItem3 ypinvoiceItem=new InvoiceItem3();
            if(ypkpls.getFpztdm().equals("02")){
                ypinvoiceItem.setReturnCode("0000");
                ypinvoiceItem.setInvoiceStatus("02");
                ypinvoiceItem.setReturnMessage("");
            }else if(ypkpls.getFpztdm().equals("00")){
                ypinvoiceItem.setReturnCode("0000");
                ypinvoiceItem.setInvoiceStatus("00");
                ypinvoiceItem.setReturnMessage("");
            }
            ypinvoiceItem.setInvoiceCode(ypkpls.getFpdm());
            ypinvoiceItem.setInvoiceNumber(ypkpls.getFphm());
            if(ypkpls.getKprq()==null){
                ypinvoiceItem.setInvoiceDate(sdf.format(new Date()));
            }else{
                ypinvoiceItem.setInvoiceDate(sdf.format(ypkpls.getKprq()));
            }
            ypinvoiceItem.setAmount(ypkpls.getHjje().toString());
            ypinvoiceItem.setTaxAmount(ypkpls.getHjse().toString());
            ypinvoiceItem.setPdfUrl(ypkpls.getPdfurl());
            ypinvoiceItem.setExtractCode(jyls.getTqm());
            invoiceItemList.add(invoiceItem);
            invoiceItemList.add(ypinvoiceItem);
            invoiceItems.setCount(invoiceItemList.size());
            invoiceItems.setInvoiceItem(invoiceItemList);
            returnData.setOperationItem(operationItem);
            returnData.setInvoiceItems(invoiceItems);
            Message=XmlJaxbUtils.toXml(returnData);

        }else if(kpls.getFpczlxdm().equals("14")){

            Map parameter =new HashMap();
            parameter.put("fpdm",kpls.getFpdm());
            parameter.put("fphm",kpls.getFphm());
            Jyxxsq jyxxsq=jyxxsqService.findOneByParams(parameter);

            ReturnData returnData=new ReturnData();
            OperationItem operationItem=new OperationItem();
            operationItem.setSerialNumber(jyxxsq.getJylsh());
            operationItem.setOrderNumber(jyxxsq.getDdh());
            operationItem.setOperationType(jyxxsq.getFpczlxdm());
            InvoiceItems invoiceItems=new InvoiceItems();
            List<InvoiceItem> invoiceItemList=new ArrayList<>();

            InvoiceItem invoiceItem=new InvoiceItem();
            if(kpls.getFpztdm().equals("08")){
                invoiceItem.setReturnCode("0000");
                invoiceItem.setInvoiceStatus("已作废");
                invoiceItem.setReturnMessage("");
            }else {
                invoiceItem.setReturnCode("9999");
                invoiceItem.setInvoiceStatus("开具失败");
                invoiceItem.setReturnMessage(kpls.getErrorReason());
            }
            invoiceItem.setInvoiceCode(kpls.getFpdm());
            invoiceItem.setInvoiceNumber(kpls.getFphm());
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");

            if(kpls.getZfrq()==null){
                invoiceItem.setInvoiceDate(sdf.format(new Date()));
            }else{
                invoiceItem.setInvoiceDate(sdf.format(kpls.getZfrq()));
            }
            invoiceItem.setAmount(kpls.getHjje().toString());
            invoiceItem.setTaxAmount(kpls.getHjse().toString());
            invoiceItem.setPdfUrl(kpls.getPdfurl());
            invoiceItemList.add(invoiceItem);

            invoiceItems.setCount(invoiceItemList.size());
            invoiceItems.setInvoiceItem(invoiceItemList);
            returnData.setOperationItem(operationItem);
            returnData.setInvoiceItems(invoiceItems);
            Message=XmlJaxbUtils.toXml(returnData);

        }
        return Message;
    }

    public String createJsonMsg(Integer kplsh) {
        String Message="";
        Map rtn = new HashMap();
        List<Map> resultList = new ArrayList<>();
        Kpls kpls=kplsService.findOne(kplsh);
        Integer djh = kpls.getDjh();
        Map param4 = new HashMap<>();
        param4.put("djh", djh);
        Jyls jyls = jylsService.findJylsByDjh(param4);
        String ddh = jyls.getDdh(); // 查询原交易流水得ddh
        Map params=new HashMap();
        params.put("gsdm",kpls.getGsdm());
        params.put("serialorder",kpls.getSerialorder());
        List<Kpls> kplsList=kplsService.findKplsBySerialOrder(params);
        if(kpls.getFpczlxdm().equals("11")){
            rtn.put("serialNumber", kpls.getJylsh());
            rtn.put("orderNumber", ddh);
            rtn.put("operationType", kpls.getFpczlxdm());
            for (Kpls one : kplsList) {
                List<Kpspmx> kpspmxs = kpspmxJpaDao.findByKplsh(one.getKplsh());
                AdapterData data = new AdapterData();
                AdapterDataSeller seller = new AdapterDataSeller();
                AdapterDataOrder order = new AdapterDataOrder();
                AdapterDataOrderBuyer buyer = new AdapterDataOrderBuyer();
                List<AdapterDataOrderDetails> details = new ArrayList<>();
                data.setSerialNumber(one.getJylsh());
                data.setPayee(one.getSkr());
                data.setReviewer(one.getFhr());
                data.setDrawer(one.getKpr());
                data.setSeller(seller);
                data.setOrder(order);

                Xf xf = xfJpaDao.findOneById(one.getXfid());
                seller.setBankAcc(xf.getXfyhzh());
                seller.setBank(xf.getXfyh());
                seller.setAddress(xf.getXfdz());
                seller.setTelephoneNo(xf.getXfdh());
                seller.setIdentifier(xf.getXfsh());
                seller.setName(xf.getXfmc());

                Jyls oneJyls = jylsService.findJylsByDjh(param4);
                order.setOrderNo(oneJyls.getDdh());
                order.setRemark(one.getBz());
                order.setTotalAmount(one.getJshj());
                order.setOrderDate(one.getKprq());
                order.setBuyer(buyer);
                order.setOrderDetails(details);

                buyer.setBank(one.getGfyh());
                buyer.setBankAcc(one.getGfyhzh());
                buyer.setIdentifier(one.getGfsh());
                buyer.setName(one.getGfmc());
                buyer.setAddress(one.getGfdz());
                buyer.setTelephoneNo(one.getGfdh());
                buyer.setEmail(one.getGfemail());

                for (Kpspmx kpspmx : kpspmxs) {
                    AdapterDataOrderDetails detail = new AdapterDataOrderDetails();
                    detail.setUnitPrice(kpspmx.getSpdj());
                    detail.setTaxRate(kpspmx.getSpsl());
                    detail.setAmount(kpspmx.getSpje());
                    detail.setTaxAmount(kpspmx.getSpse());
                    detail.setSpec(kpspmx.getSpggxh());
                    detail.setUnit(kpspmx.getSpdw());
                    detail.setQuantity(kpspmx.getSps());
                    detail.setProductName(kpspmx.getSpmc());
                    detail.setProductCode(kpspmx.getSpdm());
                    details.add(detail);
                }
                Map result = new HashMap();
                result.put("invoices", data);
                result.put("pdfurl", one.getPdfurl());
                result.put("invoiceCode", one.getFpdm());
                result.put("invoiceNumber", one.getFphm());
                SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
                if(one.getKprq()==null){
                    result.put("invoiceDate", sdf.format(new Date()));
                }else{
                    result.put("invoiceDate", sdf.format(one.getKprq()));
                }
                if(one.getFpztdm().equals("00")||one.getFpztdm().equals("05")){
                    if(one.getFpztdm().equals("00")){
                        result.put("returnCode", "0000");
                        result.put("invoiceStatus", "正常发票");
                        result.put("returnMsg", "");
                    }else {
                        result.put("returnCode", "9999");
                        result.put("invoiceStatus", "开具失败");
                        result.put("returnMsg", one.getErrorReason());
                    }
                    resultList.add(result);
                }
            }
            rtn.put("data", resultList);
            Message = JSON.toJSONString(rtn);
        }else if(kpls.getFpczlxdm().equals("12")){
            ReturnData returnData=new ReturnData();
            OperationItem operationItem=new OperationItem();
            operationItem.setSerialNumber(kpls.getJylsh());
            operationItem.setOrderNumber(ddh);
            operationItem.setOperationType(kpls.getFpczlxdm());
            InvoiceItems invoiceItems=new InvoiceItems();
            List<InvoiceItem> invoiceItemList=new ArrayList<>();

            InvoiceItem invoiceItem=new InvoiceItem();
            if(kpls.getFpztdm().equals("00")){
                invoiceItem.setReturnCode("0000");
                invoiceItem.setInvoiceStatus("正常发票");
                invoiceItem.setReturnMessage("");
            }else if(kpls.getFpztdm().equals("05")){
                invoiceItem.setReturnCode("9999");
                invoiceItem.setInvoiceStatus("开具失败");
                invoiceItem.setReturnMessage(kpls.getErrorReason());
            }
            invoiceItem.setInvoiceCode(kpls.getFpdm());
            invoiceItem.setInvoiceNumber(kpls.getFphm());
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
            if(kpls.getKprq()==null){
                invoiceItem.setInvoiceDate(sdf.format(new Date()));
            }else{
                invoiceItem.setInvoiceDate(sdf.format(kpls.getKprq()));
            }
            invoiceItem.setAmount(kpls.getHjje().toString());
            invoiceItem.setTaxAmount(kpls.getHjse().toString());
            invoiceItem.setPdfUrl(kpls.getPdfurl());
            Kpls parms=new Kpls();
            parms.setFpdm(kpls.getHzyfpdm());
            parms.setFphm(kpls.getHzyfphm());
            Kpls ypkpls = kplsService.findByfphm(parms);
            InvoiceItem ypinvoiceItem=new InvoiceItem();
            if(ypkpls.getFpztdm().equals("02")){
                ypinvoiceItem.setReturnCode("0000");
                ypinvoiceItem.setInvoiceStatus("已红冲");
                ypinvoiceItem.setReturnMessage("");
            }else if(ypkpls.getFpztdm().equals("00")){
                ypinvoiceItem.setReturnCode("0000");
                ypinvoiceItem.setInvoiceStatus("正常发票");
                ypinvoiceItem.setReturnMessage("");
            }
            ypinvoiceItem.setInvoiceCode(ypkpls.getFpdm());
            ypinvoiceItem.setInvoiceNumber(ypkpls.getFphm());
            if(ypkpls.getKprq()==null){
                ypinvoiceItem.setInvoiceDate(sdf.format(new Date()));
            }else{
                ypinvoiceItem.setInvoiceDate(sdf.format(ypkpls.getKprq()));
            }
            ypinvoiceItem.setAmount(ypkpls.getHjje().toString());
            ypinvoiceItem.setTaxAmount(ypkpls.getHjse().toString());
            ypinvoiceItem.setPdfUrl(ypkpls.getPdfurl());
            invoiceItemList.add(invoiceItem);
            invoiceItemList.add(ypinvoiceItem);
            invoiceItems.setCount(invoiceItemList.size());
            invoiceItems.setInvoiceItem(invoiceItemList);
            returnData.setOperationItem(operationItem);
            returnData.setInvoiceItems(invoiceItems);
            Message=JSON.toJSONString(returnData);

        }else if(kpls.getFpczlxdm().equals("14")){

            Map parameter =new HashMap();
            parameter.put("fpdm",kpls.getFpdm());
            parameter.put("fphm",kpls.getFphm());
            Jyxxsq jyxxsq=jyxxsqService.findOneByParams(parameter);

            ReturnData returnData=new ReturnData();
            OperationItem operationItem=new OperationItem();
            operationItem.setSerialNumber(jyxxsq.getJylsh());
            operationItem.setOrderNumber(jyxxsq.getDdh());
            operationItem.setOperationType(jyxxsq.getFpczlxdm());
            InvoiceItems invoiceItems=new InvoiceItems();
            List<InvoiceItem> invoiceItemList=new ArrayList<>();

            InvoiceItem invoiceItem=new InvoiceItem();
            if(kpls.getFpztdm().equals("08")){
                invoiceItem.setReturnCode("0000");
                invoiceItem.setInvoiceStatus("已作废");
                invoiceItem.setReturnMessage("");
            }else {
                invoiceItem.setReturnCode("9999");
                invoiceItem.setInvoiceStatus("开具失败");
                invoiceItem.setReturnMessage(kpls.getErrorReason());
            }
            invoiceItem.setInvoiceCode(kpls.getFpdm());
            invoiceItem.setInvoiceNumber(kpls.getFphm());
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");

            if(kpls.getZfrq()==null){
                invoiceItem.setInvoiceDate(sdf.format(new Date()));
            }else{
                invoiceItem.setInvoiceDate(sdf.format(kpls.getZfrq()));
            }
            invoiceItem.setAmount(kpls.getHjje().toString());
            invoiceItem.setTaxAmount(kpls.getHjse().toString());
            invoiceItem.setPdfUrl(kpls.getPdfurl());
            invoiceItemList.add(invoiceItem);

            invoiceItems.setCount(invoiceItemList.size());
            invoiceItems.setInvoiceItem(invoiceItemList);
            returnData.setOperationItem(operationItem);
            returnData.setInvoiceItems(invoiceItems);
            Message=JSON.toJSONString(returnData);

        }
        return Message;

    }
    private static String getSign(String QueryData, String key) {
        String signSourceData = "data=" + QueryData + "&key=" + key;
        String newSign = DigestUtils.md5Hex(signSourceData);
        return newSign;
    }
    public  Map httpPost(String sendMes, Kpls kpls) throws Exception {
        Map parms=new HashMap();
        parms.put("gsdm",kpls.getGsdm());
        Gsxx gsxx=gsxxService.findOneByParams(parms);
        Map resultMap = null;
        try {
            String url = gsxx.getCallbackurl();
            String strMessage = "";
            BufferedReader reader = null;
            StringBuffer buffer = new StringBuffer();
            if (!("").equals(url) && url != null) {
                String Secret = getSign(sendMes, gsxx.getSecretKey());
                logger.info("-----数据------" + sendMes + "-----key------" + gsxx.getSecretKey() + "-----签名-----" + Secret);
                HttpPost httpPost = new HttpPost(url);
                CloseableHttpResponse response = null;
                RequestConfig requestConfig = RequestConfig.custom().
                        setSocketTimeout(120 * 1000).setConnectionRequestTimeout(120 * 1000).setConnectTimeout(120 * 1000).build();
                CloseableHttpClient httpClient = HttpClients.custom()
                        .setDefaultRequestConfig(requestConfig)
                        .build();
                httpPost.addHeader("Content-Type", "application/json");
                try {
                    Map nvps = new HashMap();
                    nvps.put("invoiceData", sendMes);
                    nvps.put("sign", Secret);
                    StringEntity requestEntity = new StringEntity(JSON.toJSONString(nvps), "utf-8");
                    logger.info("-------数据-----" + JSON.toJSONString(nvps));
                    httpPost.setEntity(requestEntity);
                    response = httpClient.execute(httpPost, new BasicHttpContext());
                    if (response.getStatusLine().getStatusCode() != 200) {
                        System.out.println("request url failed, http code=" + response.getStatusLine().getStatusCode()
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
                    System.out.println("接收返回值:" + buffer.toString());
//                    resultMap = handerReturnMes(buffer.toString());
                    Cszb cszbForCallBackType = cszbService.getSpbmbbh(gsxx.getGsdm(), null, null, "callBackType");
                    String callbacktype = cszbForCallBackType.getCsz();
                    if(callbacktype!=null){
                        if("9".equals(callbacktype)){
                            resultMap = handerReturnMesJson(buffer.toString());
                        }
                    }else{
                        resultMap = handerReturnMes(buffer.toString());
                    }
//                    String returnCode = resultMap.get("ReturnCode").toString();
//                    String ReturnMessage = resultMap.get("ReturnMessage").toString();
//                    Fphxwsjl fphxwsjl = new Fphxwsjl();
//                    fphxwsjl.setGsdm(kpls.getGsdm());
//                    fphxwsjl.setEnddate(new Date());
//                    fphxwsjl.setReturncode(returnCode);
//                    fphxwsjl.setStartdate(new Date());
//                    fphxwsjl.setSecretKey(gsxx.getSecretKey());
//                    fphxwsjl.setSign(Secret);
//                    fphxwsjl.setWsurl(gsxx.getCallbackurl());
//                    fphxwsjl.setReturncontent(sendMes);
//                    fphxwsjl.setReturnmessage(ReturnMessage);
//                    fphxwsjlService.save(fphxwsjl);
                } catch (IOException e) {
                    System.out.println("request url=" + url + ", exception, msg=" + e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (response != null) {
                        try {
                            response.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
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
    public  Map handerReturnMes(String returnMes) throws Exception {

        Document document = DocumentHelper.parseText(returnMes);
        Element root = document.getRootElement();
        List<Element> childElements = root.elements();
        Map resultMap = new HashMap();
        for (Element child : childElements) {
            /**
             * 返回结果
             */
            resultMap.put(child.getName(), child.getText());
        }
        return resultMap;
    }

    public Map handerReturnMesJson(String returnMes) throws Exception {
        JSONObject jsonObject = JSON.parseObject(returnMes);
        Map map = new HashMap();
        map.put("ReturnCode", jsonObject.getString("ReturnCode"));
        map.put("ReturnMessage", jsonObject.getString("ReturnMessage"));
        return map;
    }

    public String CreateReturnMessage2(Integer kplsh) {

        String Message="";
        Kpls kpls=kplsService.findOne(kplsh);
        Integer djh = kpls.getDjh();
        logger.info("-----------PDFURL--------------"+kpls.getPdfurl()+"-----kplsh--------"+kpls.getKplsh());
        Map param4 = new HashMap<>();
        param4.put("djh", djh);
        Jyls jyls = jylsService.findJylsByDjh(param4);
        /**
         * 查询原交易流水得ddh
         */
        String ddh = jyls.getDdh();
        Map params=new HashMap();
        params.put("gsdm",kpls.getGsdm());
        params.put("serialorder",kpls.getSerialorder());
        List<Kpls> kplsList=kplsService.findKplsBySerialOrder(params);
        if(kpls.getFpczlxdm().equals("11")){
            RetrunData2 returnData=new RetrunData2();
            returnData.setExtractCode(jyls.getTqm());
            InvoiceItems2 invoiceItems=new InvoiceItems2();
            List<InvoiceItem2> invoiceItemList=new ArrayList<>();
            boolean f=true;
            for(int i=0;i<kplsList.size();i++){
                Kpls kpls2=kplsList.get(i);
                if(kpls2.getFpztdm().equals("00")||kpls2.getFpztdm().equals("05")){
                    InvoiceItem2 invoiceItem=new InvoiceItem2();
                    if(kpls2.getFpztdm().equals("00")){
                        invoiceItem.setInvoiceStatus("00");
                    }else if(kpls2.getFpztdm().equals("05")){
                        invoiceItem.setInvoiceStatus("05");
                    }
                    invoiceItem.setInvoiceCode(kpls2.getFpdm());
                    invoiceItem.setInvoiceNumber(kpls2.getFphm());
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
                    if(kpls2.getKprq()==null){
                        invoiceItem.setInvoiceDate(sdf.format(new Date()));
                    }else{
                        invoiceItem.setInvoiceDate(sdf.format(kpls2.getKprq()));
                    }
                    invoiceItem.setAmount(kpls2.getHjje().toString());
                    invoiceItem.setTaxAmount(kpls2.getHjse().toString());
                    invoiceItem.setPdfUrl(kpls2.getPdfurl());
                    invoiceItemList.add(invoiceItem);
                }else{
                    f=false;
                    break;
                }
            }
            if(invoiceItemList.size()!=kplsList.size()){
                f=false;
            }
            invoiceItems.setCount(invoiceItemList.size());
            invoiceItems.setInvoiceItem(invoiceItemList);
            returnData.setInvoiceItems(invoiceItems);
            if(f){
                Message=XmlJaxbUtils.toXml(returnData);
            }else{
                Message="";
            }
        }else if(kpls.getFpczlxdm().equals("12")){
            RetrunData2 returnData=new RetrunData2();
            returnData.setExtractCode(jyls.getTqm());
            InvoiceItems2 invoiceItems=new InvoiceItems2();
            List<InvoiceItem2> invoiceItemList=new ArrayList<>();
            boolean f=true;
            Kpls kpls2=kpls;
            if(kpls2.getFpztdm().equals("00")||kpls2.getFpztdm().equals("05")){
                InvoiceItem2 invoiceItem=new InvoiceItem2();
                if(kpls2.getFpztdm().equals("00")){
                    invoiceItem.setInvoiceStatus("02");
                }else if(kpls2.getFpztdm().equals("05")){
                    invoiceItem.setInvoiceStatus("05");
                }
                invoiceItem.setInvoiceCode(kpls2.getFpdm());
                invoiceItem.setInvoiceNumber(kpls2.getFphm());
                SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
                if(kpls2.getKprq()==null){
                    invoiceItem.setInvoiceDate(sdf.format(new Date()));
                }else{
                    invoiceItem.setInvoiceDate(sdf.format(kpls2.getKprq()));
                }
                invoiceItem.setAmount(kpls2.getHjje().toString());
                invoiceItem.setTaxAmount(kpls2.getHjse().toString());
                invoiceItem.setPdfUrl(kpls2.getPdfurl());
                invoiceItemList.add(invoiceItem);
            }else{
                f=false;
                // break;
            }
            invoiceItems.setCount(invoiceItemList.size());
            invoiceItems.setInvoiceItem(invoiceItemList);
            returnData.setInvoiceItems(invoiceItems);
            if(f){
                Message=XmlJaxbUtils.toXml(returnData);
            }else{
                Message="";
            }

        }else if(kpls.getFpczlxdm().equals("14")){
            Map parameter =new HashMap();
            parameter.put("fpdm",kpls.getFpdm());
            parameter.put("fphm",kpls.getFphm());
            Jyxxsq jyxxsq=jyxxsqService.findOneByParams(parameter);
            RetrunData2 returnData=new RetrunData2();
            returnData.setExtractCode(jyls.getTqm());
            InvoiceItems2 invoiceItems=new InvoiceItems2();
            List<InvoiceItem2> invoiceItemList=new ArrayList<>();

            InvoiceItem2 invoiceItem=new InvoiceItem2();
            if(kpls.getFpztdm().equals("08")){
                invoiceItem.setInvoiceStatus("08");
            }else {
                invoiceItem.setInvoiceStatus("05");
            }
            invoiceItem.setInvoiceCode(kpls.getFpdm());
            invoiceItem.setInvoiceNumber(kpls.getFphm());
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");

            if(kpls.getZfrq()==null){
                invoiceItem.setInvoiceDate(sdf.format(new Date()));
            }else{
                invoiceItem.setInvoiceDate(sdf.format(kpls.getZfrq()));
            }
            invoiceItem.setAmount(kpls.getHjje().toString());
            invoiceItem.setTaxAmount(kpls.getHjse().toString());
            invoiceItem.setPdfUrl(kpls.getPdfurl());
            invoiceItemList.add(invoiceItem);

            invoiceItems.setCount(invoiceItemList.size());
            invoiceItems.setInvoiceItem(invoiceItemList);
            returnData.setInvoiceItems(invoiceItems);
            Message=XmlJaxbUtils.toXml(returnData);
        }
        return Message;
    }
}
