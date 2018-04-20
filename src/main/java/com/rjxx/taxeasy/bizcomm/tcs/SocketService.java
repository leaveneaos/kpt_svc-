package com.rjxx.taxeasy.bizcomm.tcs;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.rjxx.taxeasy.bizhandle.invoicehandling.FpclService;
import com.rjxx.taxeasy.bizhandle.invoicehandling.GeneratePdfService;
import com.rjxx.taxeasy.bizhandle.invoicehandling.SkService;
import com.rjxx.taxeasy.bizhandle.maindata.command.SendCommand;
import com.rjxx.taxeasy.bizhandle.utils.AESUtils;
import com.rjxx.taxeasy.bizhandle.utils.HttpUtils;
import com.rjxx.taxeasy.bizhandle.utils.InvoiceResponseUtils;
import com.rjxx.taxeasy.bizhandle.utils.SeperateInvoiceUtils;
import com.rjxx.taxeasy.config.rabbitmq.RabbitmqUtils;
import com.rjxx.taxeasy.dal.*;
import com.rjxx.taxeasy.dao.bo.*;
import com.rjxx.taxeasy.dao.dto.InvoicePendingData;
import com.rjxx.taxeasy.dao.dto.InvoiceResponse;
import com.rjxx.utils.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.support.PublisherCallbackChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *@ClassName SocketService
 *@Description 税控向socket发送封装好的数据的数据操作的service
 *@Author 许黎明
 *@Date 2018/3/28.
 *@Version 1.0
 **/
@Service
public class SocketService {

    @Autowired
    private SkService skService;

    @Autowired
    private SkpService skpService;

    @Autowired
    private RabbitmqUtils rabbitmqUtils;

    @Autowired
    private CszbService cszbService;

    @Autowired
    private KplsService kplsService;

    @Autowired
    private KpspmxService kpspmxService;

    @Autowired
    private GeneratePdfService generatePdfService;

    @Autowired
    private FpclService fpclService;

    @Autowired
    private GsxxService gsxxService;

    @Autowired
    private XfService xfService;



    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取待开票数据
     * @param kpdid
     * @return
     */
    public InvoicePendingData GetPendingData(String kpdid) {
        String skph=null;
        InvoicePendingData result = new InvoicePendingData();
        Map parms=new HashMap();
        parms.put("kpdid",kpdid);
        List<Skp> skpList=skpService.findSkpbySkph(parms);
        Skp skp=skpList.get(0);
        Cszb cszb = cszbService.getSpbmbbh(skp.getGsdm(), skp.getXfid(), null, "sfzcdkpdkp");
        String sfzcdkpdkp = cszb.getCsz();
        if(sfzcdkpdkp.equals("是")){
            skph=kpdid;
        }else{
            skph = skpService.findOne(Integer.parseInt(kpdid)).getSkph();
            if(null==skph||"".equals(skph)){
                skph=skpService.findOne(Integer.parseInt(kpdid)).getId().toString();
            }
        }
        try {
            Channel channel = ((PublisherCallbackChannel) rabbitmqUtils.getChannel()).getDelegate();
            String zpQueueName = rabbitmqUtils.getQueueName(skph, "01");
            int zpkjsl = (int) channel.messageCount(zpQueueName);
            result.setZpkjsl(zpkjsl);
            String ppQueueName = rabbitmqUtils.getQueueName(skph, "02");
            int ppkjsl = (int) channel.messageCount(ppQueueName);
            result.setPpkjsl(ppkjsl);
            String dzpQueueName = rabbitmqUtils.getQueueName(skph, "12");
            int dzpkjsl = (int) channel.messageCount(dzpQueueName);
            result.setDzpkjsl(dzpkjsl);
            channel.close();
            logger.debug(kpdid + "-----" + "zp:" + zpkjsl + ",pp:" + ppkjsl + ",dzp:" + dzpkjsl);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("", e);
        }
        result.setKpdid(123);
        result.setSuccess("true");
        return result;
    }


    /**
     * 从mq中获取数据
     *
     * @param kpdid
     * @param fpzldms
     * @return
     */
    public Kpls getDataFromMq(String kpdid, String fpzldms) throws Exception {
        String[] fpzldmArr = fpzldms.split(",");
        String skph=null;
        Map parms=new HashMap();
        parms.put("kpdid",kpdid);
        List<Skp> skpList=skpService.findSkpbySkph(parms);
        Skp skp=skpList.get(0);
        Cszb cszb = cszbService.getSpbmbbh(skp.getGsdm(), skp.getXfid(), null, "sfzcdkpdkp");
        String sfzcdkpdkp = cszb.getCsz();
        if(sfzcdkpdkp.equals("是")){
            skph=kpdid;
        }else{
            skph = skpService.findOne(Integer.parseInt(kpdid)).getSkph();
            if(null==skph||"".equals(skph)){
                skph=skpService.findOne(Integer.parseInt(kpdid)).getId().toString();
            }
        }
        for (String fpzldm : fpzldmArr) {
            do {
                String kplshStr = (String) rabbitmqUtils.receiveMsg(skph, fpzldm);
                if (StringUtils.isNotBlank(kplshStr)) {
                    int kplsh = Integer.valueOf(kplshStr);
                    Map params = new HashMap();
                    params.put("kplsh", kplsh);
                    Kpls kpls = kplsService.findOneByParams(params);
                    if (kpls != null) {
                        return kpls;
                    }
                } else {
                    break;
                }
            } while (true);
        }
        return null;
    }


    /**
     * 执行开票
     *
     * @param kplsh
     * @param wait    等待开票结果
     * @param timeout 等待超时时间
     */
    public InvoiceResponse doKp(int kplsh, boolean wait, long timeout) throws Exception {
        Kpls kpls = kplsService.findOne(kplsh);
        if (kpls == null) {
            InvoiceResponse response = InvoiceResponseUtils.responseError("开票流水号：" + kplsh + "没有该数据");
            return response;
        }
        String xml = "";
        if ("11".equals(kpls.getFpczlxdm()) || "12".equals(kpls.getFpczlxdm()) || "13".equals(kpls.getFpczlxdm())||"14".equals(kpls.getFpczlxdm())) {
            xml = getInvoiceXml(kpls);
            logger.debug("kplsh:" + kplsh + " xml:");
            logger.debug(xml);
            xml = Base64.encodeBase64String(xml.getBytes("UTF-8"));
        }
        Map params = new HashMap();
        params.put("xml", xml);
        params.put("kpls", kpls);
        String lsh = kpls.getKplsh() + "$" + System.currentTimeMillis();
        params.put("lsh", lsh);
        String content = TemplateUtils.generateContent("invoice-request.ftl", params);
        logger.debug(content);
        String result = null;
        String kpdid=null;
        try {
            Skp skp = skpService.findOne(kpls.getSkpid());
            Cszb cszb = cszbService.getSpbmbbh(skp.getGsdm(), skp.getXfid(), null, "sfzcdkpdkp");
            String sfzcdkpdkp = cszb.getCsz();
            if(sfzcdkpdkp.equals("是")){
                kpdid=skp.getSkph();
            }else{
                kpdid=kpls.getSkpid().toString();
            }
            Map map=new HashMap();
            map.put("kpdid",kpdid);
            map.put("SendCommand", SendCommand.Invoice);
            map.put("content",content);
            map.put("lsh",lsh);
            map.put("wait",wait);
            map.put("timeout",timeout);
            result = skService.sendMessage(JSON.toJSONString(map));
            logger.debug("----------客户端返回结果-------------"+result);
        } catch (Exception e) {
            result = e.getMessage();
        }
        if (StringUtils.isBlank(result)) {
            InvoiceResponse response = InvoiceResponseUtils.responseSuccess("成功发送客户端");
            return response;
        } else if (result.contains("开票点：") && result.contains("没有连上服务器")) {
            kpls.setFpztdm("04");
            kplsService.save(kpls);
        }
        if (result.contains("<Response>")) {
            InvoiceResponse invoiceResponse = XmlJaxbUtils.convertXmlStrToObject(InvoiceResponse.class, result);
            invoiceResponse.setKpddm(kpls.getKpddm());
            invoiceResponse.setJylsh(kpls.getJylsh());
            return invoiceResponse;
        } else {
            kpls = kplsService.findOne(kplsh);
            InvoiceResponse invoiceResponse = new InvoiceResponse();
            if ("00".equals(kpls.getFpztdm())) {
                invoiceResponse.setReturnCode("0000");
                invoiceResponse.setFpdm(kpls.getFpdm());
                invoiceResponse.setFphm(kpls.getFphm());
                invoiceResponse.setKprq(DateFormatUtils.format(kpls.getKprq(), "yyyy-MM-dd HH:mm:ss"));
                invoiceResponse.setKpddm(kpls.getKpddm());
                invoiceResponse.setJylsh(kpls.getJylsh());
                invoiceResponse.setPrintFlag(Integer.valueOf(kpls.getPrintflag()));
            } else if ("05".equals(kpls.getFpztdm())) {
                invoiceResponse.setReturnCode("9999");
                invoiceResponse.setReturnMessage(kpls.getErrorReason());
            } else {
                if (result.contains("开票点：") && result.contains("没有连上服务器")&&!kpls.getFpztdm().equals("04")) {
                    kpls.setFpztdm("04");
                    kplsService.save(kpls);
                }
                invoiceResponse.setReturnCode("9999");
                invoiceResponse.setReturnMessage("未知异常，发送结果为"+result);
            }
            return invoiceResponse;
        }
    }
    /**
     * 获取发票的xml数据
     *
     * @param kpls
     * @return
     * @throws Exception
     */
    private String getInvoiceXml(Kpls kpls) throws Exception {
        Map params = new HashMap();
        params.put("kplsh", kpls.getKplsh());
        List<Kpspmx> kpspmxList = kpspmxService.findMxList(params);
        if (kpspmxList == null || kpspmxList.isEmpty()) {
            throw new Exception("没有商品明细");
        }
        int skpid = kpls.getSkpid();
        Skp skp = skpService.findOne(skpid);
        //文本方式，需要重新进行价税分离
        List<Kpspmx> kpspmxListnew= SeperateInvoiceUtils.repeatSeparatePrice(kpls,kpspmxList);
        int xfid = skp.getXfid();
        int kpdid = skp.getId();
        Cszb cszb = cszbService.getSpbmbbh(kpls.getGsdm(), xfid, kpdid, "spbmbbh");
        String spbmbbh = cszb.getCsz();
        params.put("spbmbbh",spbmbbh);
        params.put("kpls", kpls);
        params.put("kpspmxList", kpspmxListnew);
        String gfyhzh = (kpls.getGfyh() == null ? "" : kpls.getGfyh()) + (kpls.getGfyhzh() == null ? "" : kpls.getGfyhzh());
        String gfdzdh = (kpls.getGfdz() == null ? "" : kpls.getGfdz()) + (kpls.getGfdh() == null ? "" : kpls.getGfdh());
        String xfyhzh =  (kpls.getXfyh() == null ? "" : kpls.getXfyh()) + (kpls.getXfyhzh() == null ? "" : kpls.getXfyhzh());
        String xfdzdh = (kpls.getXfdz() == null ? "" : kpls.getXfdz()) + (kpls.getXfdh() == null ? "" : kpls.getXfdh());
        gfyhzh = gfyhzh.trim();
        gfdzdh = gfdzdh.trim();
        xfyhzh = xfyhzh.trim();
        xfdzdh = xfdzdh.trim();
        if (StringUtils.isBlank(gfyhzh)) {
            gfyhzh = "　";
        }
        if (StringUtils.isBlank(gfdzdh)) {
            gfdzdh = "　";
        }
        if (StringUtils.isBlank(xfyhzh)) {
            xfyhzh = "　";
        }
        if (StringUtils.isBlank(xfdzdh)) {
            xfdzdh = "　";
        }
        params.put("gfyhzh", gfyhzh);
        params.put("gfdzdh", gfdzdh);
        params.put("xfyhzh", xfyhzh);
        params.put("xfdzdh", xfdzdh);
        String templateName = "invoice-xml.ftl";
        if ("12".equals(kpls.getFpzldm())) {
            templateName = "dzfp-xml.ftl";
            if ("11".equals(kpls.getFpczlxdm())) {
                params.put("kplx", "0");
            } else if ("12".equals(kpls.getFpczlxdm()) || "13".equals(kpls.getFpczlxdm())) {
                params.put("kplx", "1");
            }
        }
        String content = TemplateUtils.generateContent(templateName, params);
        return content;
    }

    /**
     * 作废发票
     *
     * @param kplsh
     * @param wait
     * @param timeout
     * @return
     * @throws Exception
     */
    public String voidInvoice(int kplsh, boolean wait, long timeout) throws Exception {
        try {
            logger.debug("receive void invoice request:" + kplsh);
            Kpls kpls = kplsService.findOne(kplsh);
            String  xml = getInvoiceXml(kpls);
            logger.debug("kplsh:" + kplsh + " xml:");
            logger.debug(xml);
            xml = Base64.encodeBase64String(xml.getBytes("UTF-8"));
            if (kpls == null) {
                InvoiceResponse response = InvoiceResponseUtils.responseError("开票流水号：" + kplsh + "没有该数据");
                return XmlJaxbUtils.toXml(response);
            }
            if (StringUtils.isBlank(kpls.getFpdm()) || StringUtils.isBlank(kpls.getFphm())) {
                InvoiceResponse response = InvoiceResponseUtils.responseError("开票流水号：" + kplsh + "没有发票代码或号码，无法作废");
                return XmlJaxbUtils.toXml(response);
            }
            Map params = new HashMap();
            params.put("kpls", kpls);
            params.put("xml", xml);
            String commandId = kpls.getKplsh() + "$" + System.currentTimeMillis();
            params.put("lsh", kpls.getKplsh() + "");
            String content = TemplateUtils.generateContent("invoice-request.ftl", params);
            logger.debug(content);
            String result = null;
            String kpdid=null;
            try {
                Skp skp = skpService.findOne(kpls.getSkpid());
                Cszb cszb = cszbService.getSpbmbbh(skp.getGsdm(), skp.getXfid(), null, "sfzcdkpdkp");
                String sfzcdkpdkp = cszb.getCsz();
                if(sfzcdkpdkp.equals("是")){
                    kpdid=skp.getSkph();
                }else{
                    kpdid=kpls.getSkpid().toString();
                }
                Map map=new HashMap();
                map.put("kpdid",kpdid);
                map.put("SendCommand", SendCommand.VoidInvoice);
                map.put("content",content);
                map.put("lsh",commandId);
                map.put("wait",wait);
                map.put("timeout",timeout);
                result = skService.sendMessage(JSON.toJSONString(map));
            } catch (Exception e) {
                result = e.getMessage();
            }
            if (StringUtils.isBlank(result)) {
                InvoiceResponse response = InvoiceResponseUtils.responseSuccess("成功发送客户端");
            } else if (result.contains("开票点：") && result.contains("没有连上服务器")) {
                kpls.setFpztdm("04");
                kplsService.save(kpls);
            }
            logger.debug(result);
            return result;
        } catch (Exception e) {
            logger.error("", e);
            InvoiceResponse response = InvoiceResponseUtils.responseError(e.getMessage());
            return XmlJaxbUtils.toXml(response);
        }
    }

    /**
     * 重新生成pdf
     * @param kplsh
     * @return
     */
    public  InvoiceResponse generatePdf(int kplsh) {
        InvoiceResponse invoiceResponse=new InvoiceResponse();
        try {
            generatePdfService.generatePdf(kplsh);
            Kpls kpls = kplsService.findOne(kplsh);
            kpls.setFpztdm("00");
            kpls.setErrorReason("成功");
            kplsService.save(kpls);
            invoiceResponse.setReturnCode("0000");
        }catch (Exception e){
            invoiceResponse.setReturnCode("9999");
            invoiceResponse.setReturnMessage(e.getMessage());
            e.printStackTrace();
        }
        return invoiceResponse;
    }

    /**
     * 税控服务器开票
     * @param kplsh
     * @return
     */
    public InvoiceResponse skServerKP(int kplsh) {
        InvoiceResponse invoiceResponse=new InvoiceResponse();
        try{
            fpclService.skServerKP(kplsh);
            invoiceResponse.setReturnCode("0000");
        }catch (Exception e){
            invoiceResponse.setReturnCode("9999");
            invoiceResponse.setReturnMessage(e.getMessage());
            e.printStackTrace();
        }
        return invoiceResponse;
    }

    /**
     * 获取发票代码、发票号码
     * @param p
     * @return
     * @throws Exception
     */
    public String getCodeAndNo(String p)throws Exception {
        try {
            if (StringUtils.isBlank(p)) {
                throw new Exception("参数不能为空");
            }
            String params = skService.decryptSkServerParameter(p);
            Map<String, String> map = HtmlUtils.parseQueryString(params);
            String kpdid = map.get("kpdid");
            logger.debug("------传输数据-------"+params);
            Skp skp = skpService.findOne(Integer.valueOf(map.get("kpdid")));
            Cszb cszb = cszbService.getSpbmbbh(skp.getGsdm(), skp.getXfid(), skp.getId(), "sfzcdkpdkp");
            String sfzcdkpdkp = cszb.getCsz();
            if(sfzcdkpdkp.equals("是")){
                kpdid=skp.getSkph();
            }
            String fplxdm = map.get("fplxdm");
            Map resultMap=new HashMap(6);
            resultMap.put("kpdid",kpdid);
            resultMap.put("SendCommand", SendCommand.GetCodeAndNo);
            resultMap.put("content",fplxdm);
            resultMap.put("lsh","00000");
            resultMap.put("wait",false);
            resultMap.put("timeout",0);
            String result = skService.sendMessage(JSON.toJSONString(resultMap));
            logger.debug(result);
            return result;
        } catch (Exception e) {
            logger.error("", e);
            InvoiceResponse response = InvoiceResponseUtils.responseError(e.getMessage());
            return XmlJaxbUtils.toXml(response);
        }
    }

    public String reprintInvoice(String p) throws Exception{

        try {
            if (StringUtils.isBlank(p)) {
                throw new Exception("参数不能为空");
            }
            String kplshStr = skService.decryptSkServerParameter(p);
            int kplsh = Integer.valueOf(kplshStr);
            logger.debug("receive void invoice request:" + kplsh);
            Kpls kpls = kplsService.findOne(kplsh);
            if (kpls == null) {
                InvoiceResponse response = InvoiceResponseUtils.responseError("开票流水号：" + kplsh + "没有该数据");
                return XmlJaxbUtils.toXml(response);
            }
            if (StringUtils.isBlank(kpls.getFpdm()) || StringUtils.isBlank(kpls.getFphm())) {
                InvoiceResponse response = InvoiceResponseUtils.responseError("开票流水号：" + kplsh + "没有发票代码或号码，无法重打");
                return XmlJaxbUtils.toXml(response);
            }
            Map params = new HashMap();
            params.put("kpls", kpls);
            String lsh = kpls.getKplsh() + "$" + System.currentTimeMillis();
            params.put("lsh", lsh);
            String content = TemplateUtils.generateContent("invoice-request.ftl", params);
            logger.debug(content);
            Skp skp = skpService.findOne(kpls.getSkpid());
            Cszb cszb = cszbService.getSpbmbbh(skp.getGsdm(), skp.getXfid(), null, "sfzcdkpdkp");
            String sfzcdkpdkp = cszb.getCsz();
            String kpdid=null;
            if(sfzcdkpdkp.equals("是")){
                kpdid=skp.getSkph();
            }else{
                kpdid=kpls.getSkpid().toString();
            }
            Map resultMap=new HashMap(6);
            resultMap.put("kpdid",kpdid);
            resultMap.put("SendCommand", SendCommand.ReprintInvoice);
            resultMap.put("content",content);
            resultMap.put("lsh",kpls.getKplsh() + "");
            resultMap.put("wait",true);
            resultMap.put("timeout",60000);
            String result = skService.sendMessage(JSON.toJSONString(resultMap));
            InvoiceResponse invoiceResponse = XmlJaxbUtils.convertXmlStrToObject(InvoiceResponse.class, result);
            invoiceResponse.setKpddm(kpls.getKpddm());
            invoiceResponse.setJylsh(kpls.getJylsh());
            result = XmlJaxbUtils.toXml(invoiceResponse);
            logger.debug(result);
            return result;
        } catch (Exception e) {
            logger.error("", e);
            InvoiceResponse response = InvoiceResponseUtils.responseError(e.getMessage());
            return XmlJaxbUtils.toXml(response);
        }
    }

    public String skEkyunKP(String p) {
        try {
            if (StringUtils.isBlank(p)) {
                throw new Exception("参数不能为空");
            }
            String kplshStr = skService.decryptSkServerParameter(p);
            int kplsh = Integer.valueOf(kplshStr);
            logger.debug("receive void invoice request:" + kplsh);
            Kpls kpls = kplsService.findOne(kplsh);
            if (kpls == null) {
                InvoiceResponse response = InvoiceResponseUtils.responseError("开票流水号：" + kplsh + "没有该数据");
                return XmlJaxbUtils.toXml(response);
            }
            Map params = new HashMap();
            params.put("kplsh", kpls.getKplsh());
            List<Kpspmx> kpspmxList = kpspmxService.findMxList(params);
            if (kpspmxList == null || kpspmxList.isEmpty()) {
                throw new Exception("没有商品明细");
            }
            int skpid = kpls.getSkpid();
            Skp skp = skpService.findOne(skpid);
            //文本方式，需要重新进行价税分离
            List<Kpspmx> kpspmxListnew= SeperateInvoiceUtils.repeatSeparatePrice(kpls,kpspmxList);
            int xfid = skp.getXfid();
            int kpdid = skp.getId();
            Cszb cszb = cszbService.getSpbmbbh(kpls.getGsdm(), xfid, kpdid, "spbmbbh");
            String spbmbbh = cszb.getCsz();
            String fpzldm=kpls.getFpzldm();
            String fpzl=null;
            if (("01").equals(fpzldm)) {
                fpzl="s";
            } else if (("02").equals(fpzldm)) {
                fpzl="c";
            } else if (("12").equals(fpzldm)) {
                fpzl="t";
            }
            params.put("spbmbbh",spbmbbh);
            params.put("kpls", kpls);
            params.put("kpspmxList", kpspmxListnew);
            params.put("fpzl",fpzl);
            String gfyhzh = (kpls.getGfyh() == null ? "" : kpls.getGfyh()) + (kpls.getGfyhzh() == null ? "" : kpls.getGfyhzh());
            String gfdzdh = (kpls.getGfdz() == null ? "" : kpls.getGfdz()) + (kpls.getGfdh() == null ? "" : kpls.getGfdh());
            String xfyhzh =  (kpls.getXfyh() == null ? "" : kpls.getXfyh()) + (kpls.getXfyhzh() == null ? "" : kpls.getXfyhzh());
            String xfdzdh = (kpls.getXfdz() == null ? "" : kpls.getXfdz()) + (kpls.getXfdh() == null ? "" : kpls.getXfdh());
            gfyhzh = gfyhzh.trim();
            gfdzdh = gfdzdh.trim();
            xfyhzh = xfyhzh.trim();
            xfdzdh = xfdzdh.trim();
            if (StringUtils.isBlank(gfyhzh)) {
                gfyhzh = "　";
            }
            if (StringUtils.isBlank(gfdzdh)) {
                gfdzdh = "　";
            }
            if (StringUtils.isBlank(xfyhzh)) {
                xfyhzh = "　";
            }
            if (StringUtils.isBlank(xfdzdh)) {
                xfdzdh = "　";
            }
            params.put("gfyhzh", gfyhzh);
            params.put("gfdzdh", gfdzdh);
            params.put("xfyhzh", xfyhzh);
            params.put("xfdzdh", xfdzdh);
            params.put("mxCount", kpspmxListnew.size());
            String templateName = "skekyun.ftl";
            String content = TemplateUtils.generateContent(templateName, params);
            Map invoiceMap=new HashMap(7);
            invoiceMap.put("invoice",kpls.getXfsh());
            invoiceMap.put("kpjh",skp.getKpjh());
            invoiceMap.put("token",skp.getToken());
            invoiceMap.put("spxx", AESUtils.AESEncode(kpls.getXfsh(),content));
            invoiceMap.put("orderId",kplsh);
            String invoiceXml=JSON.toJSONString(invoiceMap);
            Map parmMap=new HashMap(1);
            parmMap.put("gsdm",kpls.getGsdm());
            Gsxx gsxx=gsxxService.findOneByParams(parmMap);
            /**
             * http://ek.caikaixin.cn/sendFpInfo
             */
            String returnJson= HttpUtils.HttpPost_Basic("http://ek.caikaixin.cn/sendFpInfo",invoiceXml);
            Map resultMap= XmltoJson.strJson2Map(returnJson);
            String errcode=resultMap.get("errcode").toString();
            String errmsg=resultMap.get("errmsg").toString();
            if("700".equals(errcode)){
                refreshToken(kpdid);
                skEkyunKP(p);
            }else if("200".equals(errcode)){
                skEkyunGetFpData(p);
            }
            return returnJson;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("", e);
            InvoiceResponse response = InvoiceResponseUtils.responseError(e.getMessage());
            return XmlJaxbUtils.toXml(response);
        }
    }

    public String registerXf(int skpid) {
        String token=null;
        try {
            Skp skp=skpService.findOne(skpid);
            Xf xf=xfService.findOne(skp.getXfid());
            Map xfMap=new HashMap(6);
            xfMap.put("company",xf.getXfmc());
            xfMap.put("invoice",xf.getXfsh());
            xfMap.put("kpjh","1");
            xfMap.put("address",skp.getLxdz());
            xfMap.put("tel",skp.getLxdz());
            xfMap.put("contact",skp.getKpr());
            String xfJson= JSON.toJSONString(xfMap);
            String tokenjson=HttpUtils.HttpPost_Basic("http://ek.caikaixin.cn/register",xfJson);
            Map resultMap = XmltoJson.strJson2Map(tokenjson);
            String errcode=resultMap.get("errcode").toString();
            String errmsg=resultMap.get("errmsg").toString();
            List infoList=(List)resultMap.get("info");
            Map infoMap=(Map)infoList.get(0);
            token=infoMap.get("token").toString();
            skp.setToken(token);
            skp.setKpjh("1");
            skpService.save(skp);

        }catch (Exception e){
            e.printStackTrace();
        }
        return token;
    }

    public String refreshToken(int skpid) {
        String token=null;
        try {
            Skp skp = skpService.findOne(skpid);
            Xf xf = xfService.findOne(skp.getXfid());
            Map refreshtokenMap = new HashMap(6);
            refreshtokenMap.put("invoice", xf.getXfsh());
            refreshtokenMap.put("kpjh", skp.getKpjh());
            refreshtokenMap.put("token", skp.getToken());
            String tokenJson = JSON.toJSONString(refreshtokenMap);
            String resultTokenJson = HttpUtils.HttpPost_Basic("http://ek.caikaixin.cn/token", tokenJson);
            Map resultMap = XmltoJson.strJson2Map(resultTokenJson);
            String errcode = resultMap.get("errcode").toString();
            String errmsg = resultMap.get("errmsg").toString();
            List infoList=(List)resultMap.get("info");
            Map infoMap=(Map)infoList.get(0);
            token = infoMap.get("token").toString();
            skp.setToken(token);
            skpService.save(skp);

        }catch (Exception e){
            e.printStackTrace();
        }
        return token;
    }

    public String skEkyunGetFpData(String p) {
        try {
            if (StringUtils.isBlank(p)) {
                throw new Exception("参数不能为空");
            }
            String kplshStr = skService.decryptSkServerParameter(p);
            int kplsh = Integer.valueOf(kplshStr);
            logger.debug("receive void invoice request:" + kplsh);
            Kpls kpls = kplsService.findOne(kplsh);
            if (kpls == null) {
                InvoiceResponse response = InvoiceResponseUtils.responseError("开票流水号：" + kplsh + "没有该数据");
                return XmlJaxbUtils.toXml(response);
            }
            int skpid = kpls.getSkpid();
            Skp skp = skpService.findOne(skpid);
            Map invoiceMap=new HashMap(7);
            invoiceMap.put("invoice",kpls.getXfsh());
            invoiceMap.put("kpjh",skp.getKpjh());
            invoiceMap.put("token",skp.getToken());
            invoiceMap.put("orderId",kplsh);
            String invoiceXml=JSON.toJSONString(invoiceMap);
            String returnJson= HttpUtils.HttpPost_Basic("http://ek.caikaixin.cn/getFPData",invoiceXml);
            Map resultMap= XmltoJson.strJson2Map(returnJson);
            String errcode=resultMap.get("errcode").toString();
            String errmsg=resultMap.get("errmsg").toString();
            List infoList=(List)resultMap.get("info");
            Map infpMap=(Map)infoList.get(0);
            if(infpMap!=null){
                String orderId=infpMap.get("orderId").toString();
                String fpdm=infpMap.get("fpdm").toString();
                String fphm=infpMap.get("fphm").toString();
                String kprq=infpMap.get("kprq").toString();
                kpls.setKprq(com.rjxx.time.TimeUtil.getSysDateInDate(kprq, "yyyy-MM-dd HH:mm:ss"));
                kpls.setFpdm(fpdm);
                kpls.setFphm(fphm);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}