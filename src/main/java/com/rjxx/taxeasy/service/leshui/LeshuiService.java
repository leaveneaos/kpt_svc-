package com.rjxx.taxeasy.service.leshui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rjxx.taxeasy.dao.leshui.*;
import com.rjxx.taxeasy.domains.leshui.*;
import com.rjxx.utils.leshui.LeShuiUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * Created by wangyahui on 2018/1/26 0026.
 */
@Service
public class LeshuiService {
    @Autowired
    private FpcyJpaDao fpcyJpaDao;
    @Autowired
    private FpcyjlJpaDao fpcyjlJpaDao;
    @Autowired
    private FpcymxJpaDao fpcymxJpaDao;
    @Autowired
    private JxfpxxJpaDao jxfpxxJpaDao;
    @Autowired
    private JxfpmxJpaDao jxfpmxJpaDao;
    @Autowired
    private JxywjlJpaDao jxywjlJpaDao;
    @Autowired
    private JxdyjlJpaDao jxdyjlJpaDao;
    @Autowired
    private JxhdjlJpaDao jxhdjlJpaDao;
    @Autowired
    private JxdymxjlJpaDao jxdymxjlJpaDao;

    private final static String INVOICE_INFO_SUCCESS = "00";
    private final static String INVOICE_QUERY_SUCCESS = "000";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 发票查验
     *
     * @param invoiceCode   发票代码
     * @param invoiceNumber 发票号码
     * @param billTime      开票日期
     * @param checkCode     校验码
     * @param invoiceAmount 不含税金额
     * @param sjly          数据来源
     * @param gsdm          公司代码
     * @return 乐税接口返回数据
     */
    public String fpcy(String invoiceCode, String invoiceNumber, Date billTime,
                              String checkCode, String invoiceAmount, String sjly, String gsdm,String bxry) {
        //调发票查验接口
        String result = LeShuiUtil.invoiceInfoForCom(invoiceCode, invoiceNumber, new SimpleDateFormat("yyyy-MM-dd").format(billTime), checkCode, invoiceAmount);
        //解析返回值
        JSONObject resultJson = JSON.parseObject(result);
        String rtnCode_r = resultJson.getString("RtnCode");//服务器是否正常标志
        String resultCode_r = resultJson.getString("resultCode");//查询发票状态码
        String resultMsg_r = resultJson.getString("resultMsg");//查验结果
        String invoiceName_r = resultJson.getString("invoiceName");//发票名称
        String invoiceResultString = resultJson.getString("invoiceResult");//发票主体信息
        JSONObject invoiceResult_r = JSON.parseObject(invoiceResultString);
        String invoicefalseCode_r = resultJson.getString("invoicefalseCode");//错误码;

        //创建记录表对象
        Fpcyjl fpcyjl = new Fpcyjl();
        fpcyjl.setLrsj(new Date());
        fpcyjl.setInvoicename(invoiceName_r);
        fpcyjl.setFalsecode(invoicefalseCode_r);
        fpcyjl.setResultcode(resultCode_r);
        fpcyjl.setResultmsg(resultMsg_r);
        fpcyjl.setReturncode(rtnCode_r);
        fpcyjl.setInvoicecode(invoiceCode);
        fpcyjl.setInvoicenumber(invoiceNumber);
        fpcyjl.setBilltime(billTime);
        fpcyjl.setCheckcode(checkCode);
        fpcyjl.setInvoiceamount(invoiceAmount);
        fpcyjl.setSjly(sjly);
        fpcyjl.setGsdm(gsdm);
        fpcyjl.setBxry(bxry);

        Fpcy oldFpcy = fpcyJpaDao.findOneByFpdmAndFphm(invoiceCode, invoiceNumber);
        //如果库中没有记录
        if (oldFpcy == null) {
            //保存结果
            Fpcy newFpcy = new Fpcy();
            List<Fpcymx> mxs = new ArrayList<>();
            newFpcy.setLrsj(new Date());
            newFpcy.setXgsj(newFpcy.getLrsj());
            newFpcy.setSjly(sjly);
            newFpcy.setGsdm(gsdm);
            newFpcy.setYxbz("1");
            newFpcy.setFpdm(invoiceCode);
            newFpcy.setFphm(invoiceNumber);
            newFpcy.setJym(checkCode);
            newFpcy.setKprq(billTime);
            newFpcy.setBxry(bxry);
            if(StringUtils.isNotBlank(invoiceAmount)){
                newFpcy.setHjje(new BigDecimal(invoiceAmount));
            }
            if (INVOICE_INFO_SUCCESS.equals(rtnCode_r)) {
                if (invoiceResult_r != null) {
                    String invoiceDataCode_r = invoiceResult_r.getString("invoiceDataCode").trim();//发票代码
                    String invoiceNumber_r = invoiceResult_r.getString("invoiceNumber").trim();//发票号码
                    String invoiceTypeName_r = invoiceResult_r.getString("invoiceTypeName").trim();//发票类型名称
                    String invoiceTypeCode_r = invoiceResult_r.getString("invoiceTypeCode").trim();//发票类型  01:增值税专票,02:货物运输业增值税专用发票,04:增值税普通发票,03:机动车销售统一发票,10:电子发票,11:卷式普通发票,20:国税,30:地税
                    Date billingTime_r = invoiceResult_r.getDate("billingTime");//开票时间
                    Date checkDate_r = invoiceResult_r.getDate("checkDate");//查询时间
                    String checkCode_r = invoiceResult_r.getString("checkCode").trim();// 校验码
                    String taxDiskCode_r = invoiceResult_r.getString("taxDiskCode").trim();//机器码
                    String purchaserName_r = invoiceResult_r.getString("purchaserName").trim();//购方名称
                    String taxpayerNumber_r = invoiceResult_r.getString("taxpayerNumber").trim();//购方识别号
                    String taxpayerBankAccount_r = invoiceResult_r.getString("taxpayerBankAccount").trim();//购方银行账号
                    String taxpayerAddressOrId_r = invoiceResult_r.getString("taxpayerAddressOrId").trim();//购方地址，电话
                    String salesName_r = invoiceResult_r.getString("salesName").trim();//销方名称
                    String salesTaxpayerNum_r = invoiceResult_r.getString("salesTaxpayerNum").trim();//销方纳税人识别号
                    String salesTaxpayerBankAccount_r = invoiceResult_r.getString("salesTaxpayerBankAccount").trim();//销方银行，账号
                    String salesTaxpayerAddress_r = invoiceResult_r.getString("salesTaxpayerAddress").trim();//销方地址，电话
                    BigDecimal totalTaxSum_r = invoiceResult_r.getBigDecimal("totalTaxSum");//价税合计
                    BigDecimal totalTaxNum_r = invoiceResult_r.getBigDecimal("totalTaxNum");//税额
                    BigDecimal totalAmount_r = invoiceResult_r.getBigDecimal("totalAmount");//不含税价（金额）
                    String invoiceRemarks_r = invoiceResult_r.getString("invoiceRemarks").trim();//备注
                    String isBillMark_r = invoiceResult_r.getString("isBillMark").trim();//是否为清单票，Y：是，N：否
                    String voidMark_r = invoiceResult_r.getString("voidMark").trim();//作废标志，0：正常，1：作废
                    Integer checkNum_r = invoiceResult_r.getInteger("checkNum");//查询次数
                    newFpcy.setCycs(checkNum_r);
                    newFpcy.setFpzt(voidMark_r);
                    newFpcy.setQdbz(isBillMark_r);
                    newFpcy.setBz(invoiceRemarks_r);
                    newFpcy.setHjje(totalAmount_r);
                    newFpcy.setHjse(totalTaxNum_r);
                    newFpcy.setJshj(totalTaxSum_r);
                    newFpcy.setXfdzdh(salesTaxpayerAddress_r);
                    newFpcy.setXfyhyhzh(salesTaxpayerBankAccount_r);
                    newFpcy.setXfsh(salesTaxpayerNum_r);
                    newFpcy.setXfmc(salesName_r);
                    newFpcy.setGfdzdh(taxpayerAddressOrId_r);
                    newFpcy.setGfyhyhzh(taxpayerBankAccount_r);
                    newFpcy.setGfsh(taxpayerNumber_r);
                    newFpcy.setGfmc(purchaserName_r);
                    newFpcy.setJqm(taxDiskCode_r);
                    newFpcy.setCyrq(checkDate_r);
                    newFpcy.setFpdm(invoiceDataCode_r);
                    newFpcy.setFphm(invoiceNumber_r);
                    newFpcy.setJym(checkCode_r);
                    newFpcy.setKprq(billingTime_r);
                    newFpcy.setFpzldm(invoiceTypeCode_r);
                    newFpcy.setFpzlmc(invoiceTypeName_r);

                    JSONArray invoiceDetailData_r = invoiceResult_r.getJSONArray("invoiceDetailData");//发票明细
                    for (int i = 0; i < invoiceDetailData_r.size(); i++) {
                        Fpcymx fpcymx = new Fpcymx();
                        JSONObject o = (JSONObject) invoiceDetailData_r.get(i);
                        String unit = o.getString("unit").trim();//单位
                        String model = o.getString("model").trim();//型号
                        String isBillLine = o.getString("isBillLine").trim();//是否是清单行
                        String price = o.getString("price").trim();//单价
                        BigDecimal tax = o.getBigDecimal("tax");//税额
                        String taxRate = o.getString("taxRate").trim();//税率
                        String goodserviceName = o.getString("goodserviceName");//货劳务名称
                        BigDecimal sum = o.getBigDecimal("sum");//金额
                        String number = o.getString("number").trim();//数量
                        fpcymx.setSpdw(unit);
                        if(StringUtils.isBlank(price) || "0".equals(price)){
                            fpcymx.setSpdj(null);
                        }else{
                            fpcymx.setSpdj(new BigDecimal(price));
                        }
                        fpcymx.setSpggxh(model);
                        fpcymx.setSpmc(goodserviceName);
                        if(StringUtils.isBlank(number) || "0".equals(number)){
                            fpcymx.setSps(null);
                        }else{
                            fpcymx.setSps(new BigDecimal(number));
                        }
                        fpcymx.setSpje(sum);
                        if(StringUtils.isNotBlank(taxRate)){
                            String stringTaxRate = taxRate.replaceAll("%", "");
                            BigDecimal rate = new BigDecimal(stringTaxRate);
                            fpcymx.setSpsl(rate.divide(new BigDecimal("100")));
                        }else{
                            fpcymx.setSpsl(null);
                        }
                        fpcymx.setSpse(tax);
                        fpcymx.setSpmxxh(i + 1);
                        fpcymx.setQdhbz(isBillLine);
                        mxs.add(fpcymx);
                    }
                }else{
                    fpcyjl.setFpzt("9");
//                    newFpcy.setFphm("9");
                }
            }else{
                fpcyjl.setFpzt("9");
//                newFpcy.setFphm("9");
            }
            //保存主表
            Fpcy newSave = fpcyJpaDao.save(newFpcy);
            Integer fpcyid = newSave.getId();
            //保存明细表
            if (mxs != null && mxs.size() > 0) {
                for (Fpcymx fpcymx : mxs) {
                    fpcymx.setFpcyid(fpcyid);
                }
                fpcymxJpaDao.save(mxs);
            }
            //保存记录表
            fpcyjl.setFpcyid(fpcyid);
            fpcyjl.setCycs(newSave.getCycs());
            fpcyjl.setCyrq(newSave.getCyrq());
            fpcyjl.setFpzt(newSave.getFpzt());

            //库中如果有记录
        } else {
            List<Fpcymx> mxs = new ArrayList<>();
            if (INVOICE_INFO_SUCCESS.equals(rtnCode_r)) {
                if (invoiceResult_r != null) {
                    String invoiceDataCode_r = invoiceResult_r.getString("invoiceDataCode").trim();//发票代码
                    String invoiceNumber_r = invoiceResult_r.getString("invoiceNumber").trim();//发票号码
                    String invoiceTypeName_r = invoiceResult_r.getString("invoiceTypeName").trim();//发票类型名称
                    String invoiceTypeCode_r = invoiceResult_r.getString("invoiceTypeCode").trim();//发票类型  01:增值税专票,02:货物运输业增值税专用发票,04:增值税普通发票,03:机动车销售统一发票,10:电子发票,11:卷式普通发票,20:国税,30:地税
                    Date billingTime_r = invoiceResult_r.getDate("billingTime");//开票时间
                    Date checkDate_r = invoiceResult_r.getDate("checkDate");//查询时间
                    String checkCode_r = invoiceResult_r.getString("checkCode").trim();// 校验码
                    String taxDiskCode_r = invoiceResult_r.getString("taxDiskCode").trim();//机器码
                    String purchaserName_r = invoiceResult_r.getString("purchaserName").trim();//购方名称
                    String taxpayerNumber_r = invoiceResult_r.getString("taxpayerNumber").trim();//购方识别号
                    String taxpayerBankAccount_r = invoiceResult_r.getString("taxpayerBankAccount").trim();//购方银行账号
                    String taxpayerAddressOrId_r = invoiceResult_r.getString("taxpayerAddressOrId").trim();//购方地址，电话
                    String salesName_r = invoiceResult_r.getString("salesName").trim();//销方名称
                    String salesTaxpayerNum_r = invoiceResult_r.getString("salesTaxpayerNum").trim();//销方纳税人识别号
                    String salesTaxpayerBankAccount_r = invoiceResult_r.getString("salesTaxpayerBankAccount").trim();//销方银行，账号
                    String salesTaxpayerAddress_r = invoiceResult_r.getString("salesTaxpayerAddress").trim();//销方地址，电话
                    BigDecimal totalTaxSum_r = invoiceResult_r.getBigDecimal("totalTaxSum");//价税合计
                    BigDecimal totalTaxNum_r = invoiceResult_r.getBigDecimal("totalTaxNum");//税额
                    BigDecimal totalAmount_r = invoiceResult_r.getBigDecimal("totalAmount");//不含税价（金额）
                    String invoiceRemarks_r = invoiceResult_r.getString("invoiceRemarks").trim();//备注
                    String isBillMark_r = invoiceResult_r.getString("isBillMark").trim();//是否为清单票，Y：是，N：否
                    String voidMark_r = invoiceResult_r.getString("voidMark").trim();//作废标志，0：正常，1：作废
                    Integer checkNum_r = invoiceResult_r.getInteger("checkNum");//查询次数
                    if(StringUtils.isNotBlank(bxry)){
                        oldFpcy.setBxry(bxry);
                    }
                    oldFpcy.setSjly(sjly);
                    oldFpcy.setGsdm(gsdm);
                    oldFpcy.setXgsj(new Date());
                    oldFpcy.setCycs(checkNum_r);
                    oldFpcy.setFpzt(voidMark_r);
                    oldFpcy.setQdbz(isBillMark_r);
                    oldFpcy.setBz(invoiceRemarks_r);
                    oldFpcy.setHjje(totalAmount_r);
                    oldFpcy.setHjse(totalTaxNum_r);
                    oldFpcy.setJshj(totalTaxSum_r);
                    oldFpcy.setXfdzdh(salesTaxpayerAddress_r);
                    oldFpcy.setXfyhyhzh(salesTaxpayerBankAccount_r);
                    oldFpcy.setXfsh(salesTaxpayerNum_r);
                    oldFpcy.setXfmc(salesName_r);
                    oldFpcy.setGfdzdh(taxpayerAddressOrId_r);
                    oldFpcy.setGfyhyhzh(taxpayerBankAccount_r);
                    oldFpcy.setGfsh(taxpayerNumber_r);
                    oldFpcy.setGfmc(purchaserName_r);
                    oldFpcy.setJqm(taxDiskCode_r);
                    oldFpcy.setCyrq(checkDate_r);
                    oldFpcy.setFpdm(invoiceDataCode_r);
                    oldFpcy.setFphm(invoiceNumber_r);
                    oldFpcy.setJym(checkCode_r);
                    oldFpcy.setKprq(billingTime_r);
                    oldFpcy.setFpzldm(invoiceTypeCode_r);
                    oldFpcy.setFpzlmc(invoiceTypeName_r);

                    JSONArray invoiceDetailData_r = invoiceResult_r.getJSONArray("invoiceDetailData");//发票明细
                    for (int i = 0; i < invoiceDetailData_r.size(); i++) {
                        Fpcymx fpcymx = new Fpcymx();
                        JSONObject o = (JSONObject) invoiceDetailData_r.get(i);
                        String unit = o.getString("unit").trim();//单位
                        String model = o.getString("model").trim();//型号
                        String isBillLine = o.getString("isBillLine").trim();//是否是清单行
                        String price = o.getString("price").trim();//单价
                        BigDecimal tax = o.getBigDecimal("tax");//税额
                        String taxRate = o.getString("taxRate").trim();//税率
                        String goodserviceName = o.getString("goodserviceName");//货劳务名称
                        BigDecimal sum = o.getBigDecimal("sum");//金额
                        String number = o.getString("number").trim();//数量
                        fpcymx.setSpdw(unit);
                        if(StringUtils.isBlank(price) || "0".equals(price)){
                            fpcymx.setSpdj(null);
                        }else{
                            fpcymx.setSpdj(new BigDecimal(price));
                        }
                        fpcymx.setSpggxh(model);
                        fpcymx.setSpmc(goodserviceName);
                        if(StringUtils.isBlank(number) || "0".equals(number)){
                            fpcymx.setSps(null);
                        }else{
                            fpcymx.setSps(new BigDecimal(number));
                        }
                        fpcymx.setSpje(sum);
                        if(StringUtils.isNotBlank(taxRate)){
                            String stringTaxRate = taxRate.replaceAll("%", "");
                            BigDecimal rate = new BigDecimal(stringTaxRate);
                            fpcymx.setSpsl(rate.divide(new BigDecimal("100")));
                        }else{
                            fpcymx.setSpsl(null);
                        }
                        fpcymx.setSpse(tax);
                        fpcymx.setSpmxxh(i + 1);
                        fpcymx.setQdhbz(isBillLine);
                        mxs.add(fpcymx);
                    }
                    fpcyjl.setCycs(checkNum_r);
                    fpcyjl.setCyrq(checkDate_r);
                    fpcyjl.setFpzt(voidMark_r);
                }else{
//                    oldFpcy.setFpzt("9");
                    fpcyjl.setFpzt("9");
                }
            }else{
//                oldFpcy.setFpzt("9");
                fpcyjl.setFpzt("9");
            }
            fpcyjl.setFpcyid(oldFpcy.getId());
            fpcyJpaDao.save(oldFpcy);
            //保存明细表
            if (mxs != null && mxs.size() > 0) {
                //如果库中没有
                List<Fpcymx> mxlist=fpcymxJpaDao.findOneByFpcyId(oldFpcy.getId());
                if(mxlist==null && mxlist.size()==0){
                    for (Fpcymx fpcymx : mxs) {
                        fpcymx.setFpcyid(oldFpcy.getId());
                    }
                    fpcymxJpaDao.save(mxs);
                }
            }
        }
        fpcyjlJpaDao.save(fpcyjl);
        return result;
    }


    /**
     * 单张发票查询
     *
     * @param invoiceCode 发票代码
     * @param invoiceNo   发票号码
     * @param taxCode     税号
     * @param gsdm        公司代码
     * @return 乐税接口返回结果
     */
    public String fpcx(String invoiceCode, String invoiceNo,
                       String taxCode, String gsdm,Integer gfid) {
        //调单张发票查询接口
        String uniqueId = "QBI" + new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date())
                + new Random().nextInt(9)
                + new Random().nextInt(9)
                + LeShuiUtil.getRandomLetter();
        String result = LeShuiUtil.invoiceQuery(uniqueId, invoiceCode, invoiceNo, taxCode);
        JSONObject resultJson = JSON.parseObject(result);
        JSONObject head = resultJson.getJSONObject("head");
        String rtnMsg = head.getString("rtnMsg");
        String rtnCode = head.getString("rtnCode");
        JSONObject body = resultJson.getJSONObject("body");

        //创建业务记录对象
        Jxywjl jxywjl = new Jxywjl();
        jxywjl.setYwlx(1);//1.单张发票查询 2.批量查询 3.发票认证 4.发票查询回调 5.认证结果回调
        jxywjl.setGsdm(gsdm);
        jxywjl.setGfid(gfid);
        jxywjl.setLrsj(new Date());
        //创建调用记录对象
        Jxdyjl jxdyjl = new Jxdyjl();
        jxdyjl.setLrsj(new Date());
        jxdyjl.setDyxh(1);
        jxdyjl.setMxbz(0);//明细标志 0没有 1有
        jxdyjl.setGsdm(gsdm);
        jxdyjl.setUniqueid(uniqueId);
        jxdyjl.setInvoicecode(invoiceCode);
        jxdyjl.setInvoiceno(invoiceNo);
        jxdyjl.setTaxcode(taxCode);
        //创建回调记录对象
        Jxhdjl jxhdjl = new Jxhdjl();
        jxhdjl.setLrsj(new Date());
        jxhdjl.setGsdm(gsdm);
        jxhdjl.setRtncode(rtnCode);
        jxhdjl.setRtnmsg(rtnMsg);

        //查询库中是否已有，如果没有，则插入主表与明细
        Jxfpxx oldJxfpxx = jxfpxxJpaDao.findByFpdmAndFphm(invoiceCode, invoiceNo);
        if (oldJxfpxx == null) {
            List<Jxfpmx> jxfpmxList = new ArrayList<>();
            Jxfpxx newJxfpxx = new Jxfpxx();
            newJxfpxx.setLrsj(new Date());
            newJxfpxx.setXgsj(newJxfpxx.getLrsj());
            newJxfpxx.setUniqueid(uniqueId);
            newJxfpxx.setGsdm(gsdm);
            newJxfpxx.setYxbz("1");
            newJxfpxx.setFphm(invoiceNo);
            newJxfpxx.setFpdm(invoiceCode);
            newJxfpxx.setGfsh(taxCode);
            //如果返回成功
            if (INVOICE_QUERY_SUCCESS.equals(rtnCode)) {
                jxywjl.setZt("0000");//0000 成功 9999失败 5555部分成功
                jxdyjl.setZt("0000");
                String invoiceCode_r = body.getString("invoiceCode");
                String invoiceNo_r = body.getString("invoiceNo");
                String type = body.getString("type");
                String status = body.getString("status");
                BigDecimal amount = body.getBigDecimal("amount");
                BigDecimal taxAmount = body.getBigDecimal("taxAmount");
                BigDecimal totalAmount = body.getBigDecimal("totalAmount");
                String salerCompany = body.getString("salerCompany");
                String salerCode = body.getString("salerCode");
                String salerAddress = body.getString("salerAddress");
                String salerBankAccount = body.getString("salerBankAccount");
                String buyerCompany = body.getString("buyerCompany");
                String buyerCode = body.getString("buyerCode");
                String buyerAddress = body.getString("buyerAddress");
                String buyerBankAccount = body.getString("buyerBankAccount");
                Date createDate = body.getDate("createDate");
                String verifyCode = body.getString("verifyCode");
                String machineCode = body.getString("machineCode");
                String invoicesStatus = body.getString("invoicesStatus");
                String isAuth = body.getString("isAuth");
                Date authTime = body.getDate("authTime");
                String authType = body.getString("authType");
                String remark = body.getString("remark");
                newJxfpxx.setBz(remark);
                newJxfpxx.setRzlx(authType);
                newJxfpxx.setRzsj(authTime);
                newJxfpxx.setRzbz(isAuth);
                newJxfpxx.setFpzt(invoicesStatus);
                newJxfpxx.setJqm(machineCode);
                newJxfpxx.setJym(verifyCode);
                newJxfpxx.setKprq(createDate);
                newJxfpxx.setGfyhyhzh(buyerBankAccount);
                newJxfpxx.setGfdzdh(buyerAddress);
                newJxfpxx.setGfsh(buyerCode);
                newJxfpxx.setGfmc(buyerCompany);
                newJxfpxx.setXfyhyhzh(salerBankAccount);
                newJxfpxx.setXfdzdh(salerAddress);
                newJxfpxx.setXfsh(salerCode);
                newJxfpxx.setXfmc(salerCompany);
                newJxfpxx.setJshj(totalAmount);
                newJxfpxx.setHjse(taxAmount);
                newJxfpxx.setHjje(amount);
                newJxfpxx.setFpzldm(type);
                newJxfpxx.setFpdm(invoiceCode_r);
                newJxfpxx.setFphm(invoiceNo_r);
                JSONArray goods = body.getJSONArray("goods");
                for (int j = 0; j < goods.size(); j++) {
                    Jxfpmx jxfpmx = new Jxfpmx();
                    JSONObject good = (JSONObject) goods.get(j);
                    String goodName = good.getString("goodName").trim();
                    String unit = good.getString("unit").trim();
                    String rate = good.getString("rate").trim();
                    BigDecimal taxAmountLine = good.getBigDecimal("taxAmount");
                    BigDecimal amountLine = good.getBigDecimal("amount");
                    String price = good.getString("price").trim();
                    String model = good.getString("model").trim();
                    String count = good.getString("count").trim();
                    String lineNum = good.getString("lineNum").trim();
                    //税率
                    if(StringUtils.isNotBlank(rate)){
                        String stringTaxRate = rate.replaceAll("%", "");
                        BigDecimal sl = new BigDecimal(stringTaxRate);
                        jxfpmx.setSpsl(sl.divide(new BigDecimal("100")));
                    }else{
                        jxfpmx.setSpsl(null);
                    }
                    //单价
                    if(StringUtils.isBlank(price) || "0".equals(price)){
                        jxfpmx.setSpdj(null);
                    }else{
                        jxfpmx.setSpdj(new BigDecimal(price));
                    }
                    //数量
                    if(StringUtils.isBlank(count) || "0".equals(count)){
                        jxfpmx.setSps(null);
                    }else{
                        jxfpmx.setSps(new BigDecimal(count));
                    }
                    jxfpmx.setSpdw(unit);
                    jxfpmx.setSpggxh(model);
                    jxfpmx.setSpje(amountLine);
                    jxfpmx.setSpse(taxAmountLine);
                    jxfpmx.setSpmc(goodName);
                    jxfpmx.setHh(lineNum);
                    jxfpmx.setSpmxxh(j + 1);
                    jxfpmxList.add(jxfpmx);
                }

                jxhdjl.setFpzt(invoicesStatus);
                jxhdjl.setRzbz(isAuth);
                jxhdjl.setRzlx(authType);
                jxhdjl.setRzsj(authTime);
            }else{
//                newJxfpxx.setFpzt("9");
                jxhdjl.setFpzt("9");
                jxywjl.setZt("9999");
                jxdyjl.setZt("9999");
            }
            Jxfpxx newSave = jxfpxxJpaDao.save(newJxfpxx);
            Integer fplsh = newSave.getFplsh();
            //保存明细
            if(jxfpmxList!=null && jxfpmxList.size()>0){
                for (Jxfpmx jxfpmx : jxfpmxList) {
                    jxfpmx.setFplsh(fplsh);
                }
            }
            jxfpmxJpaDao.save(jxfpmxList);
            jxhdjl.setFplsh(fplsh);

            //如果库中存在,更新主表
        } else {
            List<Jxfpmx> jxfpmxList = new ArrayList<>();
            if (INVOICE_QUERY_SUCCESS.equals(rtnCode)) {
                jxywjl.setZt("0000");//0000 成功 9999失败 5555部分成功
                jxdyjl.setZt("0000");
                String invoiceCode_r = body.getString("invoiceCode");
                String invoiceNo_r = body.getString("invoiceNo");
                String type = body.getString("type");
                String status = body.getString("status");
                BigDecimal amount = body.getBigDecimal("amount");
                BigDecimal taxAmount = body.getBigDecimal("taxAmount");
                BigDecimal totalAmount = body.getBigDecimal("totalAmount");
                String salerCompany = body.getString("salerCompany");
                String salerCode = body.getString("salerCode");
                String salerAddress = body.getString("salerAddress");
                String salerBankAccount = body.getString("salerBankAccount");
                String buyerCompany = body.getString("buyerCompany");
                String buyerCode = body.getString("buyerCode");
                String buyerAddress = body.getString("buyerAddress");
                String buyerBankAccount = body.getString("buyerBankAccount");
                Date createDate = body.getDate("createDate");
                String verifyCode = body.getString("verifyCode");
                String machineCode = body.getString("machineCode");
                String invoicesStatus = body.getString("invoicesStatus");
                String isAuth = body.getString("isAuth");
                Date authTime = body.getDate("authTime");
                String authType = body.getString("authType");
                String remark = body.getString("remark");
                oldJxfpxx.setGsdm(gsdm);
                oldJxfpxx.setXgsj(new Date());
                oldJxfpxx.setUniqueid(uniqueId);
                oldJxfpxx.setBz(remark);
                oldJxfpxx.setRzlx(authType);
                oldJxfpxx.setRzsj(authTime);
                oldJxfpxx.setRzbz(isAuth);
                oldJxfpxx.setFpzt(invoicesStatus);
                oldJxfpxx.setJqm(machineCode);
                oldJxfpxx.setJym(verifyCode);
                oldJxfpxx.setKprq(createDate);
                oldJxfpxx.setGfyhyhzh(buyerBankAccount);
                oldJxfpxx.setGfdzdh(buyerAddress);
                oldJxfpxx.setGfsh(buyerCode);
                oldJxfpxx.setGfmc(buyerCompany);
                oldJxfpxx.setXfyhyhzh(salerBankAccount);
                oldJxfpxx.setXfdzdh(salerAddress);
                oldJxfpxx.setXfsh(salerCode);
                oldJxfpxx.setXfmc(salerCompany);
                oldJxfpxx.setJshj(totalAmount);
                oldJxfpxx.setHjse(taxAmount);
                oldJxfpxx.setHjje(amount);
                oldJxfpxx.setFpzldm(type);
                oldJxfpxx.setFpdm(invoiceCode_r);
                oldJxfpxx.setFphm(invoiceNo_r);
                JSONArray goods = body.getJSONArray("goods");
                for (int j = 0; j < goods.size(); j++) {
                    Jxfpmx jxfpmx = new Jxfpmx();
                    JSONObject good = (JSONObject) goods.get(j);
                    String goodName = good.getString("goodName").trim();
                    String unit = good.getString("unit").trim();
                    String rate = good.getString("rate").trim();
                    BigDecimal taxAmountLine = good.getBigDecimal("taxAmount");
                    BigDecimal amountLine = good.getBigDecimal("amount");
                    String price = good.getString("price").trim();
                    String model = good.getString("model").trim();
                    String count = good.getString("count").trim();
                    String lineNum = good.getString("lineNum").trim();
                    //税率
                    if(StringUtils.isNotBlank(rate)){
                        String stringTaxRate = rate.replaceAll("%", "");
                        BigDecimal sl = new BigDecimal(stringTaxRate);
                        jxfpmx.setSpsl(sl.divide(new BigDecimal("100")));
                    }else{
                        jxfpmx.setSpsl(null);
                    }
                    //单价
                    if(StringUtils.isBlank(price) || "0".equals(price)){
                        jxfpmx.setSpdj(null);
                    }else{
                        jxfpmx.setSpdj(new BigDecimal(price));
                    }
                    //数量
                    if(StringUtils.isBlank(count) || "0".equals(count)){
                        jxfpmx.setSps(null);
                    }else{
                        jxfpmx.setSps(new BigDecimal(count));
                    }
                    jxfpmx.setSpdw(unit);
                    jxfpmx.setSpggxh(model);
                    jxfpmx.setSpje(amountLine);
                    jxfpmx.setSpse(taxAmountLine);
                    jxfpmx.setSpmc(goodName);
                    jxfpmx.setHh(lineNum);
                    jxfpmx.setSpmxxh(j + 1);
                    jxfpmxList.add(jxfpmx);
                }
                jxhdjl.setFpzt(invoicesStatus);
                jxhdjl.setRzbz(isAuth);
                jxhdjl.setRzlx(authType);
                jxhdjl.setRzsj(authTime);
            }else{
//                oldJxfpxx.setFpzt("9");
                jxhdjl.setFpzt("9");
                jxdyjl.setZt("9999");
                jxywjl.setZt("9999");
            }
            //更新
            jxfpxxJpaDao.save(oldJxfpxx);
            //保存明细
            if(jxfpmxList!=null && jxfpmxList.size()>0){
                //如果库中没有
                List<Jxfpmx> mxlist=jxfpmxJpaDao.findOneByFplsh(oldJxfpxx.getFplsh());
                if(mxlist==null && mxlist.size()==0){
                    for (Jxfpmx jxfpmx : jxfpmxList) {
                        jxfpmx.setFplsh(oldJxfpxx.getFplsh());
                    }
                    jxfpmxJpaDao.save(jxfpmxList);
                }
            }
            jxhdjl.setFplsh(oldJxfpxx.getFplsh());
        }
        Jxywjl saveJxyw = jxywjlJpaDao.save(jxywjl);
        jxdyjl.setYwid(saveJxyw.getId());
        Jxdyjl saveJxdyjl = jxdyjlJpaDao.save(jxdyjl);
        jxhdjl.setDyid(saveJxdyjl.getId());
        jxhdjlJpaDao.save(jxhdjl);
        return result;
    }

    /**
     * 发票批量查询
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param taxCode   税号
     * @return 本次业务记录的状态
     */
    public String fpcxBatch(Date startTime, Date endTime,
                            String taxCode, String gsdm, Integer gfid) {
        //创建业务记录对象
        Jxywjl jxywjl = new Jxywjl();
        jxywjl.setLrsj(new Date());
        jxywjl.setYwlx(2);//1.单张发票查询 2.批量查询 3.发票认证 4.发票查询回调 5.认证结果回调
        jxywjl.setGsdm(gsdm);
        jxywjl.setGfid(gfid);
        jxywjl.setKssj(startTime);
        jxywjl.setJssj(endTime);
        Jxywjl saveJxywjl = jxywjlJpaDao.save(jxywjl);

        //总页数(调用次数)初始化
        int countPage=0;

        //调用成功次数
        int successNum=0;

        //开启死循环
        for(int num=1;num>0;num++){
            //如果当前页数超过总页数而且不是第一次，跳出循环
            if(num>countPage && countPage!=0){
                break;
            }
            String uniqueId = "QBI" + new SimpleDateFormat("yyyyMMddHHmmss")
                    .format(new Date())
                    + new Random().nextInt(9)
                    + new Random().nextInt(9)
                    + LeShuiUtil.getRandomLetter();
            //调接口
            String result = LeShuiUtil.invoiceBatchQuery(uniqueId, sdf.format(startTime), sdf.format(endTime), taxCode, num);
            JSONObject resultJson = JSON.parseObject(result);
            JSONObject head = resultJson.getJSONObject("head");
            String rtnMsg = head.getString("rtnMsg");
            String rtnCode = head.getString("rtnCode");
            JSONObject body = resultJson.getJSONObject("body");
            Integer pageNo_r = body.getInteger("pageNo");
            Integer pageSize = body.getInteger("pageSize");
            Integer totalSum = body.getInteger("totalSum");
            JSONArray invoices = body.getJSONArray("invoices");

            //创建调用记录对象
            Jxdyjl jxdyjl = new Jxdyjl();
            jxdyjl.setLrsj(new Date());
            jxdyjl.setDyxh(num);
            jxdyjl.setMxbz(0);//明细标志 0没有 1有
            jxdyjl.setGsdm(gsdm);
            jxdyjl.setUniqueid(uniqueId);
            jxdyjl.setTaxcode(taxCode);
            jxdyjl.setStarttime(startTime);
            jxdyjl.setEndtime(endTime);
            jxdyjl.setPageno(pageNo_r);
            if(INVOICE_QUERY_SUCCESS.equals(rtnCode)) {
                jxdyjl.setZt("0000");
                successNum += 1;
            }else{
                jxdyjl.setZt("9999");
            }
            jxdyjl.setYwid(saveJxywjl.getId());
            Jxdyjl saveJxdyjl=jxdyjlJpaDao.save(jxdyjl);

            //创建回调记录对象
            Jxhdjl jxhdjl = new Jxhdjl();
            jxhdjl.setLrsj(new Date());
            jxhdjl.setGsdm(gsdm);
            jxhdjl.setRtncode(rtnCode);
            jxhdjl.setRtnmsg(rtnMsg);
            jxhdjl.setTotalsum(totalSum);
            jxhdjl.setPagesize(pageSize);
            jxhdjl.setPageno(pageNo_r);
            jxhdjl.setDyid(saveJxdyjl.getId());

            //获取总数除以每页数量的余数
            if(totalSum!=0){
                if(totalSum>pageSize){
                    Integer ys = totalSum % pageSize;
                    if(ys!=0){
                        countPage = (totalSum / pageSize) + 1;
                    }else{
                        countPage = totalSum / pageSize;
                    }
                }else{
                    countPage = 1;
                }
            }else{
                jxhdjlJpaDao.save(jxhdjl);
                break;
            }

            //成功
            if(INVOICE_QUERY_SUCCESS.equals(rtnCode)){
                //如果发票信息不为空
                if (invoices != null && invoices.size() > 0) {
                    for (int i = 0; i < invoices.size(); i++) {
                        JSONObject fpcxBatch = (JSONObject) invoices.get(i);
                        String invoiceCode = fpcxBatch.getString("invoiceCode");
                        String invoiceNo = fpcxBatch.getString("invoiceNo");
                        String type = fpcxBatch.getString("type");
                        String status = fpcxBatch.getString("status");
                        BigDecimal amount = fpcxBatch.getBigDecimal("amount");
                        BigDecimal taxAmount = fpcxBatch.getBigDecimal("taxAmount");
                        BigDecimal totalAmount = fpcxBatch.getBigDecimal("totalAmount");
                        String salerCompany = fpcxBatch.getString("salerCompany");
                        String salerCode = fpcxBatch.getString("salerCode");
                        String salerAddress = fpcxBatch.getString("salerAddress");
                        String salerBankAccount = fpcxBatch.getString("salerBankAccount");
                        String buyerCompany = fpcxBatch.getString("buyerCompany");
                        String buyerCode = fpcxBatch.getString("buyerCode");
                        String buyerAddress = fpcxBatch.getString("buyerAddress");
                        String buyerBankAccount = fpcxBatch.getString("buyerBankAccount");
                        Date createDate = fpcxBatch.getDate("createDate");
                        String verifyCode = fpcxBatch.getString("verifyCode");
                        String machineCode = fpcxBatch.getString("machineCode");
                        String invoicesStatus = fpcxBatch.getString("invoicesStatus");
                        String isAuth = fpcxBatch.getString("isAuth");
                        Date authTime = fpcxBatch.getDate("authTime");
                        String authType = fpcxBatch.getString("authType");
                        String remark = fpcxBatch.getString("remark");
                        JSONArray goods = fpcxBatch.getJSONArray("goods");
                        List<Jxfpmx> jxfpmxList = new ArrayList<>();
                        for (int j = 0; j < goods.size(); j++) {
                            Jxfpmx jxfpmx = new Jxfpmx();
                            JSONObject good = (JSONObject) goods.get(j);
                            String goodName = good.getString("goodName").trim();
                            String unit = good.getString("unit").trim();
                            String rate = good.getString("rate").trim();
                            BigDecimal taxAmountLine = good.getBigDecimal("taxAmount");
                            BigDecimal amountLine = good.getBigDecimal("amount");
                            String  price = good.getString("price").trim();
                            String model = good.getString("model").trim();
                            String count = good.getString("count").trim();
                            String lineNum = good.getString("lineNum").trim();
                            //税率
                            if(StringUtils.isNotBlank(rate)){
                                String stringTaxRate = rate.replaceAll("%", "");
                                BigDecimal sl = new BigDecimal(stringTaxRate);
                                jxfpmx.setSpsl(sl.divide(new BigDecimal("100")));
                            }else{
                                jxfpmx.setSpsl(null);
                            }
                            //单价
                            if(StringUtils.isBlank(price) || "0".equals(price)){
                                jxfpmx.setSpdj(null);
                            }else{
                                jxfpmx.setSpdj(new BigDecimal(price));
                            }
                            //数量
                            if(StringUtils.isBlank(count) || "0".equals(count)){
                                jxfpmx.setSps(null);
                            }else{
                                jxfpmx.setSps(new BigDecimal(count));
                            }
                            jxfpmx.setSpdw(unit);
                            jxfpmx.setSpggxh(model);
                            jxfpmx.setSpje(amountLine);
                            jxfpmx.setSpse(taxAmountLine);
                            jxfpmx.setSpmc(goodName);
                            jxfpmx.setHh(lineNum);
                            jxfpmx.setSpmxxh(j + 1);
                            jxfpmxList.add(jxfpmx);
                        }

                        jxhdjl.setFpzt(invoicesStatus);
                        jxhdjl.setRzsj(authTime);
                        jxhdjl.setRzlx(authType);
                        jxhdjl.setRzbz(isAuth);

                        //查询表中是否已存在
                        Jxfpxx oldJxfpxx = jxfpxxJpaDao.findByFpdmAndFphm(invoiceCode, invoiceNo);
                        if(oldJxfpxx==null){
                            Jxfpxx newJxfpxx = new Jxfpxx();
                            newJxfpxx.setLrsj(new Date());
                            newJxfpxx.setXgsj(newJxfpxx.getLrsj());
                            newJxfpxx.setUniqueid(uniqueId);
                            newJxfpxx.setGsdm(gsdm);
                            newJxfpxx.setYxbz("1");
                            newJxfpxx.setBz(remark);
                            newJxfpxx.setRzlx(authType);
                            newJxfpxx.setRzsj(authTime);
                            newJxfpxx.setRzbz(isAuth);
                            newJxfpxx.setFpzt(invoicesStatus);
                            newJxfpxx.setJqm(machineCode);
                            newJxfpxx.setJym(verifyCode);
                            newJxfpxx.setKprq(createDate);
                            newJxfpxx.setGfyhyhzh(buyerBankAccount);
                            newJxfpxx.setGfdzdh(buyerAddress);
                            newJxfpxx.setGfsh(buyerCode);
                            newJxfpxx.setGfmc(buyerCompany);
                            newJxfpxx.setXfyhyhzh(salerBankAccount);
                            newJxfpxx.setXfdzdh(salerAddress);
                            newJxfpxx.setXfsh(salerCode);
                            newJxfpxx.setXfmc(salerCompany);
                            newJxfpxx.setJshj(totalAmount);
                            newJxfpxx.setHjse(taxAmount);
                            newJxfpxx.setHjje(amount);
                            newJxfpxx.setFpzldm(type);
                            newJxfpxx.setFpdm(invoiceCode);
                            newJxfpxx.setFphm(invoiceNo);
                            Jxfpxx newSave = jxfpxxJpaDao.save(newJxfpxx);
                            Integer fplsh = newSave.getFplsh();
                            //保存明细
                            for (Jxfpmx jxfpmx : jxfpmxList) {
                                jxfpmx.setFplsh(fplsh);
                            }
                            jxfpmxJpaDao.save(jxfpmxList);
                            jxhdjl.setFplsh(fplsh);
                            //存在
                        }else{
                            oldJxfpxx.setXgsj(new Date());
                            oldJxfpxx.setUniqueid(uniqueId);
                            oldJxfpxx.setGsdm(gsdm);
                            oldJxfpxx.setBz(remark);
                            oldJxfpxx.setRzlx(authType);
                            oldJxfpxx.setRzsj(authTime);
                            oldJxfpxx.setRzbz(isAuth);
                            oldJxfpxx.setFpzt(invoicesStatus);
                            oldJxfpxx.setJqm(machineCode);
                            oldJxfpxx.setJym(verifyCode);
                            oldJxfpxx.setKprq(createDate);
                            oldJxfpxx.setGfyhyhzh(buyerBankAccount);
                            oldJxfpxx.setGfdzdh(buyerAddress);
                            oldJxfpxx.setGfsh(buyerCode);
                            oldJxfpxx.setGfmc(buyerCompany);
                            oldJxfpxx.setXfyhyhzh(salerBankAccount);
                            oldJxfpxx.setXfdzdh(salerAddress);
                            oldJxfpxx.setXfsh(salerCode);
                            oldJxfpxx.setXfmc(salerCompany);
                            oldJxfpxx.setJshj(totalAmount);
                            oldJxfpxx.setHjse(taxAmount);
                            oldJxfpxx.setHjje(amount);
                            oldJxfpxx.setFpzldm(type);
                            oldJxfpxx.setFpdm(invoiceCode);
                            oldJxfpxx.setFphm(invoiceNo);
                            //保存明细
                            List<Jxfpmx> mxlist=jxfpmxJpaDao.findOneByFplsh(oldJxfpxx.getFplsh());
                            if(mxlist==null&&mxlist.size()==0){
                                for (Jxfpmx jxfpmx : jxfpmxList) {
                                    jxfpmx.setFplsh(oldJxfpxx.getFplsh());
                                }
                                jxfpmxJpaDao.save(jxfpmxList);
                            }
                            jxfpxxJpaDao.save(oldJxfpxx);
                            jxhdjl.setFplsh(oldJxfpxx.getFplsh());
                        }
                    }
                }else{
                    //如果系统异常
                    saveJxdyjl.setZt("4444");
                    saveJxywjl.setZt("4444");
                    jxhdjl.setFpzt("9");
                    jxdyjlJpaDao.save(saveJxdyjl);
                }
            }else{
                jxhdjl.setFpzt("9");
            }
            jxhdjlJpaDao.save(jxhdjl);
        }
        if(saveJxywjl.getZt()==null){
            //根据成功次数与总页数的关系来更新业务记录表的状态
            if(successNum==countPage && countPage!=0){
                saveJxywjl.setZt("0000");
            }else if(successNum==0 || countPage==0){
                saveJxywjl.setZt("9999");
            }else{
                saveJxywjl.setZt("5555");
            }
        }
        jxywjlJpaDao.save(saveJxywjl);
        return saveJxywjl.getZt();
    }

    /**
     * 发票认证
     * @param taxCode 税号
     * @param body 发票信息
     * @return 乐税接口返回数据
     */
    public String fprz(String taxCode,String period,
                     List<InvoiceAuth> body,Integer gfid,String gsdm){
        String batchId = "QBI" + new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date())
                + new Random().nextInt(9)
                + new Random().nextInt(9)
                + LeShuiUtil.getRandomLetter();
        String result = LeShuiUtil.invoiceAuthorize(batchId, taxCode,period,body);
        JSONObject resultJson = JSON.parseObject(result);
        JSONObject head = resultJson.getJSONObject("head");
        JSONObject body_r = resultJson.getJSONObject("body");
        String rtnMsg = head.getString("rtnMsg");
        String rtnCode = head.getString("rtnCode");
        String resultCode = body_r.getString("resultCode");
        String resultMsg = body_r.getString("resultMsg");

        //创建业务记录对象
        Jxywjl jxywjl = new Jxywjl();
        jxywjl.setLrsj(new Date());
        jxywjl.setYwlx(3);//1.单张发票查询 2.批量查询 3.发票认证 4.发票查询回调 5.认证结果回调
        jxywjl.setGsdm(gsdm);
        jxywjl.setGfid(gfid);

        //创建调用记录对象
        Jxdyjl jxdyjl = new Jxdyjl();
        jxdyjl.setLrsj(new Date());
        jxdyjl.setDyxh(1);
        jxdyjl.setBatchid(batchId);
        jxdyjl.setTaxcode(taxCode);
        jxdyjl.setMxbz(1);//有调用明细
        List<Jxdymxjl> jxdymxjls = new ArrayList<>();

        for(InvoiceAuth auth:body){
            Jxdymxjl jxdymxjl = new Jxdymxjl();
            String fpdm = auth.getInvoiceCode();
            String fphm = auth.getInvoiceNo();
            Jxfpxx jxfpxx = jxfpxxJpaDao.findByFpdmAndFphm(fpdm, fphm);
            jxfpxx.setRzzt("2");//已发送乐税
            //更新
            jxfpxxJpaDao.save(jxfpxx);
            jxdymxjl.setInvoceno(fphm);
            jxdymxjl.setInvoicecode(fpdm);
            jxdymxjls.add(jxdymxjl);
        }

        //创建回调记录对象
        Jxhdjl jxhdjl = new Jxhdjl();
        jxhdjl.setLrsj(new Date());
        jxhdjl.setRtnmsg(rtnMsg);
        jxhdjl.setRtncode(rtnCode);
        jxhdjl.setResultcode(resultCode);
        jxhdjl.setResultmsg(resultMsg);

        if(!INVOICE_QUERY_SUCCESS.equals(rtnCode)){
            jxywjl.setZt("9999");
            jxdyjl.setZt("9999");
        }else{
            if(resultCode==null){
                jxywjl.setZt("0000");
                jxdyjl.setZt("0000");
            }else{
                jxywjl.setZt("9999");
                jxdyjl.setZt("9999");
            }
        }
        Jxywjl saveJxywjl = jxywjlJpaDao.save(jxywjl);
        jxdyjl.setYwid(saveJxywjl.getId());
        Jxdyjl saveJxdyjl = jxdyjlJpaDao.save(jxdyjl);
        for(Jxdymxjl jxdymxjl:jxdymxjls){
            jxdymxjl.setDyid(saveJxdyjl.getId());
        }
        jxdymxjlJpaDao.save(jxdymxjls);
        jxhdjl.setDyid(saveJxdyjl.getId());
        jxhdjlJpaDao.save(jxhdjl);
        return saveJxywjl.getZt();
    }
}
