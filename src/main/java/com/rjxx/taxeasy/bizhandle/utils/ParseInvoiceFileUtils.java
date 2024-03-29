package com.rjxx.taxeasy.bizhandle.utils;

import com.alibaba.fastjson.JSON;
import com.rjxx.taxeasy.bizhandle.invoicehandling.GeneratePdfService;
import com.rjxx.taxeasy.dal.*;
import com.rjxx.taxeasy.dao.bo.Cszb;
import com.rjxx.taxeasy.dao.bo.Gsxx;
import com.rjxx.taxeasy.dao.bo.Jyls;
import com.rjxx.taxeasy.dao.bo.Kpls;
import com.rjxx.taxeasy.dao.dto.InvoiceResponse;
import com.rjxx.taxeasy.dao.vo.Kpspmxvo;
import com.rjxx.utils.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析发票文件的utils
 * Created by Administrator on 2017-04-14.
 */
@Service("parseInvoiceFileUtils")
public class ParseInvoiceFileUtils {

    @Autowired
    private KplsService kplsService;

    @Autowired
    private JylsService jylsService;

    @Autowired
    private KpspmxService kpspmxService;

    @Autowired
    private GeneratePdfService generatePdfService;

    @Autowired
    private CszbService cszbService;

    @Autowired
    private GsxxService gsxxService;

    @Autowired
    private FphxUtil fphxUtil;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 解析纸质票批量导入结果的文本<br>
     * kjjg：开具结果，0-失败，1-成功，
     * fpdm：发票代码，fphm：发票号码，
     * sbyy：失败原因，kplsh：开票流水号
     * kprq：开票日期
     *
     * @param content
     * @return
     */
    public Map<String, String> parseZZPBulkImportText(String content) {
        Map<String, String> retMap = new HashMap<>();
        if (StringUtils.isBlank(content)) {
            retMap.put("kjjg", "0");
            retMap.put("sbyy", "开票软件没有返回结果，请去开票软件确认本次开具结果");
            return retMap;
        }
        //开票日期
        int pos11 = content.indexOf("[");
        int pos12 = content.indexOf("]");
        if (pos11 != -1 && pos12 != -1) {
            String kprq = content.substring(pos11 + 1, pos12);
            retMap.put("kprq", kprq);
        }
        content = content.replace("：", ":").replace("，", ",");
        int pos1 = content.indexOf("单据号:");
        int pos2 = content.indexOf("开具结果:");
        int startIndex = pos1 + "单据号:".length();
        //开票流水号
        String resultdjh = content.substring(startIndex, pos2);
        if (resultdjh.endsWith(",")) {
            resultdjh = resultdjh.substring(0, resultdjh.length() - 1);
        }
        int pos111 = resultdjh.indexOf("-");
        if (pos111 != -1) {
            resultdjh = resultdjh.substring(0, pos111);
        }
        retMap.put("kplsh", resultdjh);
        int pos3 = content.indexOf(",", pos2);
        startIndex = pos2 + "开具结果:".length();
        String kjjg = content.substring(startIndex, pos3);
        retMap.put("kjjg", kjjg);
        if ("0".equals(kjjg)) {
            //开具失败
            int pos4 = content.indexOf("失败原因:", pos3);
            startIndex = pos4 + "失败原因:".length();
            String sbyy = content.substring(startIndex).trim();
            retMap.put("sbyy", sbyy);
            return retMap;
        }
        int pos5 = content.indexOf("发票信息:", pos3);
        int pos6 = content.indexOf(",", pos5);
        startIndex = pos5 + "发票信息:".length();
        String fpzl = content.substring(startIndex, pos6);
        String fpinfo = content.substring(pos6 + 1).trim();
        String[] fpInfoArr = fpinfo.split(",");
        String fpdm = fpInfoArr[0];
        String fphm = fpInfoArr[1];
        retMap.put("fpdm", fpdm);
        retMap.put("fphm", fphm);
        return retMap;
    }

    /**
     * 更新开票结果
     *
     * @param response
     * @return
     */
    public void updateInvoiceResult(InvoiceResponse response) throws Exception {
        String returnCode = response.getReturnCode();
        if ("0000".equals(returnCode)) {
            String lsh = response.getLsh();
            int pos = lsh.indexOf("$");
            int kplsh;
            if (pos != -1) {
                kplsh = Integer.valueOf(lsh.substring(0, pos));
            } else {
                kplsh = Integer.valueOf(lsh);
            }
            Kpls kpls = kplsService.findOne(kplsh);
            kpls.setFpdm(response.getFpdm());
            kpls.setFphm(response.getFphm());
            kpls.setFpztdm("00");
            kpls.setErrorReason(null);
            kpls.setPrintflag("" + response.getPrintFlag());
            kpls.setKprq(DateUtils.parseDate(response.getKprq(), "yyyy-MM-dd HH:mm:ss"));
            kpls.setXgsj(new Date());
            kpls.setXgry(1);
            if (StringUtils.isNotBlank(response.getReturnMessage())) {
                kpls.setErrorReason(response.getReturnMessage());
            } else {
                kpls.setErrorReason(null);
            }

            Cszb cszb1 = cszbService.getSpbmbbh(kpls.getGsdm(),kpls.getXfid(),kpls.getSkpid(),"zpsfscpdf");
            if(null !=cszb1 && cszb1.getCsz().equals("是")){
                kpls.setJym("10497438135598948527");
                kpls.setSksbm("499000134531");
                kpls.setMwq("03*6<7-4937->9/1-544>0*1<76-</+0<<**87>-+>6+462+4145-1<+86*6<7-4937->9/1-538/0*>>687-44/8>4/*>010/17196-70/2>*81");
            }

            kplsService.save(kpls);
            Jyls jyls = jylsService.findOne(kpls.getDjh());
            jyls.setClztdm("91");
            jylsService.save(jyls);
            String czlxdm = kpls.getFpczlxdm();
            if ("12".equals(czlxdm) || "13".equals(czlxdm)) {
                if (kpls.getHkFphm() != null && kpls.getHkFpdm() != null) {
                    kpls.setJylsh("");
                    Kpls ykpls = kplsService.findByhzfphm(kpls);
                    Map param2 = new HashMap<>();
                    param2.put("kplsh", ykpls.getKplsh());
                    // 全部红冲后修改
                    Kpspmxvo mxvo = kpspmxService.findKhcje(param2);
                    if (mxvo.getKhcje() == 0) {
                        param2.put("fpztdm", "02");
                        kplsService.updateFpczlx(param2);
                    } else {
                        param2.put("fpztdm", "01");
                        kplsService.updateFpczlx(param2);
                    }
                }
            }
            
            //回写
            if(null !=cszb1 && cszb1.getCsz().equals("是")){
                generatePdfService.generatePdf(kplsh);
            }else {
                //回写
                Map parms=new HashMap();
                parms.put("gsdm",kpls.getGsdm());
                Gsxx gsxx=gsxxService.findOneByParams(parms);
                //Jyls jyls = jylsService.findOne(kpls.getDjh());
                fphxUtil.fphx(kpls,jyls,gsxx);
            }
            //String url="https://vrapi.fvt.tujia.com/Invoice/CallBack";
//            String url=gsxx.getCallbackurl();
//            if(!("").equals(url)&&url!=null){
//                String returnmessage=null;
//                if(!kpls.getGsdm().equals("Family")&&!kpls.getGsdm().equals("fwk")) {
//                    returnmessage = fphxUtil.CreateReturnMessage(kpls.getKplsh());
//                    //输出调用结果
//                    logger.info("回写报文" + returnmessage);
//                    if (returnmessage != null && !"".equals(returnmessage)) {
//                        Map returnMap = fphxUtil.httpPost(returnmessage, kpls);
//                        logger.info("返回报文" + JSON.toJSONString(returnMap));
//                    }
//                }else if(kpls.getGsdm().equals("fwk")){
//                    returnmessage = fphxUtil.CreateReturnMessage3(kpls.getKplsh());
//                    logger.info("回写报文" + returnmessage);
//                    if (returnmessage != null && !"".equals(returnmessage)) {
//                        String ss= HttpUtils.netWebService(url,"CallBack",returnmessage,gsxx.getAppKey(),gsxx.getSecretKey());
//                    }
//                }
//            }
        } else {
            String lsh = response.getLsh();
            int pos = lsh.indexOf("$");
            int kplsh;
            if (pos != -1) {
                kplsh = Integer.valueOf(lsh.substring(0, pos));
            } else {
                kplsh = Integer.valueOf(lsh);
            }
            Kpls kpls = kplsService.findOne(kplsh);
            kpls.setFpztdm("05");
            kpls.setErrorReason(response.getReturnMessage());
            kpls.setXgsj(new Date());
            kpls.setXgry(1);
            kplsService.save(kpls);
            Jyls jyls = jylsService.findOne(kpls.getDjh());
            jyls.setClztdm("92");
            jylsService.save(jyls);
            //失败不回写
            //Map parms=new HashMap();
            //parms.put("gsdm",kpls.getGsdm());
            //Gsxx gsxx=gsxxService.findOneByParams(parms);
            //String url="https://vrapi.fvt.tujia.com/Invoice/CallBack";
//            String url=gsxx.getCallbackurl();
//            if(!("").equals(url)&&url!=null){
//                String returnmessage=null;
//                if(!kpls.getGsdm().equals("Family")&&!kpls.getGsdm().equals("fwk")) {
//                    returnmessage = fphxUtil.CreateReturnMessage(kpls.getKplsh());
//                    //输出调用结果
//                    logger.info("回写报文" + returnmessage);
//                    if (returnmessage != null && !"".equals(returnmessage)) {
//                        Map returnMap = fphxUtil.httpPost(returnmessage, kpls);
//                        logger.info("返回报文" + JSON.toJSONString(returnMap));
//                    }
//                }else if(kpls.getGsdm().equals("fwk")){
//                    returnmessage = fphxUtil.CreateReturnMessage3(kpls.getKplsh());
//                    logger.info("回写报文" + returnmessage);
//                    if (returnmessage != null && !"".equals(returnmessage)) {
//                        String ss= HttpUtils.netWebService(url,"CallBack",returnmessage,gsxx.getAppKey(),gsxx.getSecretKey());
//                    }
//                }
//            }
        }

    }


}
