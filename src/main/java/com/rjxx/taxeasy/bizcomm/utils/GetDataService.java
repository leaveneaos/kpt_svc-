package com.rjxx.taxeasy.bizcomm.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rjxx.taxeasy.domains.*;
import com.rjxx.taxeasy.service.*;
import com.rjxx.taxeasy.vo.Spvo;
import com.rjxx.utils.CheckOrderUtil;
import com.rjxx.utils.weixin.HttpClientUtil;
import org.apache.axiom.om.OMElement;
import org.apache.commons.codec.digest.DigestUtils;
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
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by xlm on 2017/7/3.
 * 获取全家，绿地优鲜接口提供的数据
 */
@Service
public class GetDataService {
    @Autowired
    private YhService yhService;
    @Autowired
    private SkpService skpService;
    @Autowired
    private XfService xfService;
    @Autowired
    private JyxxsqService jyxxsqService;
    @Autowired
    private ZffsService zffsService;
    @Autowired
    private SpvoService spvoService;
    @Autowired
    private GsxxService gsxxService;
    @Autowired
    private CszbService cszbService;
    @Autowired
    private  CheckOrderUtil checkOrderUtil;
    @Autowired
    private  vSpbmService vSpbmService;

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private static String getSign(String QueryData, String key) {
        String signSourceData = "data=" + QueryData + "&key=" + key;
        String newSign = DigestUtils.md5Hex(signSourceData);
        return newSign;
    }
    public String  xmldata(){
        String xml3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "\t\t\t\t<Responese>\n" +
                "\t\t\t\t<ReturnCode>9002</ReturnCode>\n" +
                "\t\t\t\t<ReturnMessage>未提取到交易数据，请稍后再试</ReturnMessage>\n" +
                "\t\t\t\t</Responese>\n";
            String xml1="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<Responese>\n" +
                    "\t<ReturnCode>Family_01</ReturnCode>\n" +
                    "\t<ReturnMessage>Family_01</ReturnMessage>\n" +
                    "\t<ReturnData>\n" +
                    "\t<ExtractCode>061402100101123456765</ExtractCode>\n" +
                    "\t<MemberID></MemberID>\n" +
                    "\t<InvType>12</InvType>\n" +
                    "\t<Spbmbbh>13.0</Spbmbbh>\n" +
                    "\t<StoreNo></StoreNo>\n" +
                    "\t<Seller>\n" +
                    "\t\t<Identifier>9131000071785090X1</Identifier>\n" +
                    "\t\t<Name>上海福满家便利有限公司</Name>\n" +
                    "\t\t<Address>上海市普陀区真北路2167号上海鸿海大厦商铺2栋1层09号商铺</Address>\n" +
                    "\t\t<TelephoneNo>021-62723187</TelephoneNo>\n" +
                    "\t\t<Bank>中国民生银行上海分行陆家嘴支行</Bank>\n" +
                    "\t\t<BankAcc>0216014180000511\t</BankAcc>\n" +
                    "\t</Seller>\n" +
                    "\t\t<Orders>\n" +
                    "\t\t\t<OrderMain>\n" +
                    "\t\t\t\t<OrderNo>123456789</OrderNo>\n" +
                    "\t\t\t\t<InvoiceList>0</InvoiceList>\n" +
                    "\t\t\t\t<InvoiceSplit>1</InvoiceSplit>\n" +
                    "\t\t\t\t<InvoiceSfdy>1</InvoiceSfdy>\n" +
                    "\t\t\t\t<OrderDate>2016-06-22 23:59:59</OrderDate>\n" +
                    "\t\t\t\t<ChargeTaxWay>0</ChargeTaxWay>\n" +
                    "\t\t\t\t<TotalAmount>10.6</TotalAmount>\n" +
                    "\t\t\t\t<TaxMark>0</TaxMark>\n" +
                    "\t\t\t\t<Remark></Remark>\n" +
                    "\t\t\t</OrderMain>\n" +
                    "\t\t\t<OrderDetails count=\"1\">\n" +
                    "\t\t\t\t<ProductItem>\n" +
                    "\t\t\t\t\t<VenderOwnCode></VenderOwnCode>\n" +
                    "\t\t\t\t\t<ProductCode>1030201030000000000</ProductCode>\n" +
                    "\t\t\t\t\t<ProductName>饼干</ProductName>\n" +
                    "\t\t\t\t\t<RowType>0</RowType>\n" +
                    "\t\t\t\t\t<Spec></Spec>\n" +
                    "\t\t\t\t\t<Unit></Unit>\n" +
                    "\t\t\t\t\t<Quantity>1</Quantity>\n" +
                    "\t\t\t\t\t<UnitPrice>10.00</UnitPrice>\n" +
                    "\t\t\t\t\t<Amount>10.00</Amount>\n" +
                    "\t\t\t\t\t<DeductAmount></DeductAmount>\n" +
                    "\t\t\t\t\t<TaxRate>0.06</TaxRate>\n" +
                    "\t\t\t\t\t<TaxAmount>0.6</TaxAmount>\n" +
                    "\t\t\t\t\t<MxTotalAmount>10.6</MxTotalAmount>\n" +
                    "\t\t\t\t\t<PolicyMark></PolicyMark>\n" +
                    "\t\t\t\t\t<TaxRateMark></TaxRateMark>\n" +
                    "\t\t\t\t\t<PolicyName></PolicyName>\n" +
                    "\t\t\t\t</ProductItem>\n" +
                    "\t\t\t</OrderDetails>\n" +
                    "\t\t</Orders>\n" +
                    "\t\t</ReturnData>\n" +
                    "</Responese>\n";

        String xml2="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "\t\t\t\t<Responese>\n" +
                "\t\t\t\t\t<ReturnCode>0000</ReturnCode>\n" +
                "\t\t\t\t\t<ReturnMessage>成功</ReturnMessage>\n" +
                "\t\t\t\t\t<ReturnData>\n" +
                "\t\t\t\t\t\t<ExtractCode>38355712</ExtractCode>\n" +
                "\t\t\t\t\t\t<InvType>12</InvType>\n" +
                "\t\t\t\t\t\t<Spbmbbh>13.0</Spbmbbh>\n" +
                "\t\t\t\t\t\t<ClientNO>KP001</ClientNO>\n" +
                "\t\t\t\t\t\t<Seller>\n" +
                "\t\t\t\t\t\t\t<Identifier></Identifier>\n" +
                "\t\t\t\t\t\t\t<Name></Name>\n" +
                "\t\t\t\t\t\t\t<Address></Address>\n" +
                "\t\t\t\t\t\t\t<TelephoneNo></TelephoneNo>\n" +
                "\t\t\t\t\t\t\t<Bank></Bank>\n" +
                "\t\t\t\t\t\t\t<BankAcc></BankAcc>\n" +
                "\t\t\t\t\t\t</Seller>\n" +
                "\t\t\t\t\t\t<Orders>\n" +
                "\t\t\t\t\t\t\t<OrderMain>\n" +
                "\t\t\t\t\t\t\t\t<OrderNo>38355712</OrderNo>\n" +
                "\t\t\t\t\t\t\t\t<OrderDate>2017-08-14 14:07:24</OrderDate>\n" +
                "\t\t\t\t\t\t\t\t<ChargeTaxWay>0</ChargeTaxWay>\n" +
                "\t\t\t\t\t\t\t\t<TotalAmount>880.00</TotalAmount>\n" +
                "\t\t\t\t\t\t\t\t<TaxMark>1</TaxMark>\n" +
                "\t\t\t\t\t\t\t\t<Remark></Remark>\n" +
                "\t\t\t\t\t\t\t</OrderMain>\n" +
                "\t\t\t\t\t\t\t<OrderDetails>\n" +
                "\t\t\t\t\t\t\t\t<ProductItem>\n" +
                "\t\t\t\t\t\t\t\t<VenderOwnCode>101211</VenderOwnCode>\n" +
                "\t\t\t\t\t\t\t\t<ProductCode></ProductCode>\n" +
                "\t\t\t\t\t\t\t\t<ProductName><![CDATA[康多乐 幼犬粮活力幼犬发育成长配方狗粮1.4kg]]></ProductName>\n" +
                "\t\t\t\t\t\t\t\t<RowType>0</RowType>\n" +
                "\t\t\t\t\t\t\t\t<Spec><![CDATA[]]></Spec>\n" +
                "\t\t\t\t\t\t\t\t<Unit>件</Unit>\n" +
                "\t\t\t\t\t\t\t\t<Quantity>1</Quantity>\n" +
                "\t\t\t\t\t\t\t\t<UnitPrice>33.80</UnitPrice>\n" +
                "\t\t\t\t\t\t\t\t<Amount>33.8</Amount>\n" +
                "\t\t\t\t\t\t\t\t<DeductAmount></DeductAmount>\n" +
                "\t\t\t\t\t\t\t\t<TaxRate>0.11</TaxRate>\n" +
                "\t\t\t\t\t\t\t\t<TaxAmount></TaxAmount>\n" +
                "\t\t\t\t\t\t\t\t<MxTotalAmount>33.8</MxTotalAmount>\n" +
                "\t\t\t\t\t\t\t\t<PolicyMark></PolicyMark>\n" +
                "\t\t\t\t\t\t\t\t<TaxRateMark></TaxRateMark>\n" +
                "\t\t\t\t\t\t\t\t<PolicyName></PolicyName>\n" +
                "\t\t\t\t\t\t\t  </ProductItem><ProductItem>\n" +
                "\t\t\t\t\t\t\t\t<VenderOwnCode>100126</VenderOwnCode>\n" +
                "\t\t\t\t\t\t\t\t<ProductCode></ProductCode>\n" +
                "\t\t\t\t\t\t\t\t<ProductName><![CDATA[法国皇家ROYAL CANIN 成犬粮狗粮8kg CC]]></ProductName>\n" +
                "\t\t\t\t\t\t\t\t<RowType>0</RowType>\n" +
                "\t\t\t\t\t\t\t\t<Spec><![CDATA[]]></Spec>\n" +
                "\t\t\t\t\t\t\t\t<Unit>件</Unit>\n" +
                "\t\t\t\t\t\t\t\t<Quantity>2</Quantity>\n" +
                "\t\t\t\t\t\t\t\t<UnitPrice>166.00</UnitPrice>\n" +
                "\t\t\t\t\t\t\t\t<Amount>332</Amount>\n" +
                "\t\t\t\t\t\t\t\t<DeductAmount></DeductAmount>\n" +
                "\t\t\t\t\t\t\t\t<TaxRate>0.11</TaxRate>\n" +
                "\t\t\t\t\t\t\t\t<TaxAmount></TaxAmount>\n" +
                "\t\t\t\t\t\t\t\t<MxTotalAmount>332</MxTotalAmount>\n" +
                "\t\t\t\t\t\t\t\t<PolicyMark></PolicyMark>\n" +
                "\t\t\t\t\t\t\t\t<TaxRateMark></TaxRateMark>\n" +
                "\t\t\t\t\t\t\t\t<PolicyName></PolicyName>\n" +
                "\t\t\t\t\t\t\t  </ProductItem><ProductItem>\n" +
                "\t\t\t\t\t\t\t<VenderOwnCode>000000</VenderOwnCode>\n" +
                "\t\t\t\t\t\t\t<ProductCode></ProductCode>\n" +
                "\t\t\t\t\t\t\t<ProductName>运输服务</ProductName>\n" +
                "\t\t\t\t\t\t\t<RowType>0</RowType>\n" +
                "\t\t\t\t\t\t\t<Spec></Spec>\n" +
                "\t\t\t\t\t\t\t<Unit>件</Unit>\n" +
                "\t\t\t\t\t\t\t<Quantity>1</Quantity>\n" +
                "\t\t\t\t\t\t\t<UnitPrice>23.50</UnitPrice>\n" +
                "\t\t\t\t\t\t\t<Amount>23.50</Amount>\n" +
                "\t\t\t\t\t\t\t<DeductAmount></DeductAmount>\n" +
                "\t\t\t\t\t\t\t<TaxRate>0.06</TaxRate>\n" +
                "\t\t\t\t\t\t\t<TaxAmount></TaxAmount>\n" +
                "\t\t\t\t\t\t\t<MxTotalAmount>23.50</MxTotalAmount>\n" +
                "\t\t\t\t\t\t\t<PolicyMark></PolicyMark>\n" +
                "\t\t\t\t\t\t\t<TaxRateMark></TaxRateMark>\n" +
                "\t\t\t\t\t\t\t<PolicyName></PolicyName>\n" +
                "\t\t\t\t\t\t   </ProductItem><ProductItem>\n" +
                "\t\t\t\t\t\t\t\t\t\t<VenderOwnCode>3530401</VenderOwnCode>\n" +
                "\t\t\t\t\t\t\t\t\t\t<ProductCode></ProductCode>\n" +
                "\t\t\t\t\t\t\t\t\t\t<ProductName><![CDATA[闽江 玻璃水族箱鱼缸HR3-580 MJ-560 58cm长]]></ProductName>\n" +
                "\t\t\t\t\t\t\t\t\t\t<RowType>2</RowType>\n" +
                "\t\t\t\t\t\t\t\t\t\t<Spec><![CDATA[黑色]]></Spec>\n" +
                "\t\t\t\t\t\t\t\t\t\t<Unit>件</Unit>\n" +
                "\t\t\t\t\t\t\t\t\t\t<Quantity>2</Quantity>\n" +
                "\t\t\t\t\t\t\t\t\t\t<UnitPrice>249.00</UnitPrice>\n" +
                "\t\t\t\t\t\t\t\t\t\t<Amount>498</Amount>\n" +
                "\t\t\t\t\t\t\t\t\t\t<DeductAmount></DeductAmount>\n" +
                "\t\t\t\t\t\t\t\t\t\t<TaxRate>0.17</TaxRate>\n" +
                "\t\t\t\t\t\t\t\t\t\t<TaxAmount></TaxAmount>\n" +
                "\t\t\t\t\t\t\t\t\t\t<MxTotalAmount>498</MxTotalAmount>\n" +
                "\t\t\t\t\t\t\t\t\t\t<PolicyMark></PolicyMark>\n" +
                "\t\t\t\t\t\t\t\t\t\t<TaxRateMark></TaxRateMark>\n" +
                "\t\t\t\t\t\t\t\t\t\t<PolicyName></PolicyName>\n" +
                "\t\t\t\t\t\t\t\t\t  </ProductItem><ProductItem>\n" +
                "\t\t\t\t\t\t\t<VenderOwnCode>3530401</VenderOwnCode>\n" +
                "\t\t\t\t\t\t\t<ProductCode></ProductCode>\n" +
                "\t\t\t\t\t\t\t<ProductName><![CDATA[闽江 玻璃水族箱鱼缸HR3-580 MJ-560 58cm长]]></ProductName>\n" +
                "\t\t\t\t\t\t\t<RowType>1</RowType>\n" +
                "\t\t\t\t\t\t\t<Spec></Spec>\n" +
                "\t\t\t\t\t\t\t<Unit>件</Unit>\n" +
                "\t\t\t\t\t\t\t<Quantity></Quantity>\n" +
                "\t\t\t\t\t\t\t<UnitPrice></UnitPrice>\n" +
                "\t\t\t\t\t\t\t<Amount>-7.30</Amount>\n" +
                "\t\t\t\t\t\t\t<DeductAmount></DeductAmount>\n" +
                "\t\t\t\t\t\t\t<TaxRate>0.17</TaxRate>\n" +
                "\t\t\t\t\t\t\t<TaxAmount></TaxAmount>\n" +
                "\t\t\t\t\t\t\t<MxTotalAmount>-7.30</MxTotalAmount>\n" +
                "\t\t\t\t\t\t\t<PolicyMark></PolicyMark>\n" +
                "\t\t\t\t\t\t\t<TaxRateMark></TaxRateMark>\n" +
                "\t\t\t\t\t\t\t<PolicyName></PolicyName>\n" +
                "\t\t\t\t\t\t   </ProductItem>\n" +
                "\t\t\t\t\t\t\t</OrderDetails>\n" +
                "\t\t\t\t\t\t</Orders>\n" +
                "\t\t\t\t\t</ReturnData>\n" +
                "\t\t\t\t</Responese>\n";

            return xml3;
    }

    /**
     * 波奇网--调用接口获取数据
     * @param code
     * @param gsdm
     * @param url
     * @return
     */
    public Map getDataForBqw(String code,String gsdm,String url){
        logger.info("拉取数据参数值code"+code+"公司代码"+gsdm+"url地址"+url);
        Map parmsMap=new HashMap();
        Map parms=new HashMap();
        parms.put("gsdm",gsdm);
        Gsxx gsxx=gsxxService.findOneByParams(parms);
        try {
            String Secret = getSign(code,gsxx.getSecretKey());
            Map map = new HashMap();
            map.put("method","getOrderInfo");
            map.put("ExtractCode",code);
            map.put("sign",Secret);
            String response = HttpClientUtil.doPost(url, map);
            logger.info("波奇网---接收返回值:" + response);
            parmsMap=interpretingForBqw(gsdm,response);
            String error = (String) parmsMap.get("error");
            if(error==null) {
                List<Jyxxsq> jyxxsqList = (List) parmsMap.get("jyxxsqList");
                List<Jymxsq> jymxsqList = (List) parmsMap.get("jymxsqList");
                List<Jyzfmx> jyzfmxList = (List) parmsMap.get("jyzfmxList");
                String msg = checkOrderUtil.checkOrders(jyxxsqList,jymxsqList,jyzfmxList,gsdm,"");
                if(null!=msg&& !"".equals(msg)){
                    parmsMap.put("msg",msg);
                }
            }
        }catch (Exception e){
            logger.info("msg=" + e.getMessage());
            e.printStackTrace();
        }
        return parmsMap;
    }
    /**
     * 波奇网-- 解析数据
     * @param gsdm
     * @param data
     * @return
     * @throws Exception
     */
    public Map interpretingForBqw(String gsdm,String data)throws Exception {
        Map rsMap= null;
        try {
            Map params1 = new HashMap();
            params1.put("gsdm", gsdm);
            Yh yh = yhService.findOneByParams(params1);
            int lrry = yh.getId();
            List<Jyxxsq> jyxxsqList = new ArrayList();
            List<Jymxsq> jymxsqList = new ArrayList();
            List<Jyzfmx> jyzfmxList = new ArrayList<Jyzfmx>();
            rsMap = new HashMap();
            Document xmlDoc = null;
            OMElement root = null;
            try {
                xmlDoc = DocumentHelper.parseText(data);
                root = XmlMapUtils.xml2OMElement(data);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Map rootMap = XmlMapUtils.xml2Map(root, "Responese");
            String ReturnCode=rootMap.get("ReturnCode").toString();
            String ReturnMessage=rootMap.get("ReturnMessage").toString();
            if(!ReturnCode.equals("0000")){
                rsMap.put("jyxxsqList", jyxxsqList);
                rsMap.put("jymxsqList", jymxsqList);
                rsMap.put("jyzfmxList", jyzfmxList);
                rsMap.put("error",ReturnCode+":"+ReturnMessage);
                logger.info("------错误信息--------"+ReturnCode+":"+ReturnMessage);
                return rsMap;
            }
            Element ReturnData  = (Element) xmlDoc.selectSingleNode("Responese/ReturnData");
            // 提取码
            String ExtractCode = "";
            if (null != ReturnData.selectSingleNode("ExtractCode")
                    && !ReturnData.selectSingleNode("ExtractCode").equals("")) {
                ExtractCode = ReturnData.selectSingleNode("ExtractCode").getText();
            }
            // 发票种类
            String InvType = "";
            if (null != ReturnData.selectSingleNode("InvType")
                    && !ReturnData.selectSingleNode("InvType").equals("")) {
                InvType = ReturnData.selectSingleNode("InvType").getText();
            }
            // 商品编码版本号
            String Spbmbbh = "";
            if (null != ReturnData.selectSingleNode("Spbmbbh")
                    && !ReturnData.selectSingleNode("Spbmbbh").equals("")) {
                Spbmbbh = ReturnData.selectSingleNode("Spbmbbh").getText();
            }
            // 开票点编码
            String ClientNO = "";
            if (null != ReturnData.selectSingleNode("ClientNO")
                    && !ReturnData.selectSingleNode("ClientNO").equals("")) {
                ClientNO = ReturnData.selectSingleNode("ClientNO").getText();
            }

            //二级节点--销方信息
            Element Seller  = (Element) xmlDoc.selectSingleNode("Responese/ReturnData/Seller");
            // 销方税号
            String Identifier = "";
            if (null != Seller.selectSingleNode("Identifier")
                    && !Seller.selectSingleNode("Identifier").equals("")) {
                Identifier = Seller.selectSingleNode("Identifier").getText();
            }
            // 销方名称
            String Name = "";
            if (null != Seller.selectSingleNode("Name")
                    && !Seller.selectSingleNode("Name").equals("")) {
                Name = Seller.selectSingleNode("Name").getText();
            }
            // 销方地址
            String Address = "";
            if (null != Seller.selectSingleNode("Address")
                    && !Seller.selectSingleNode("Address").equals("")) {
                Address = Seller.selectSingleNode("Address").getText();
            }
            // 销方电话
            String TelephoneNo = "";
            if (null != Seller.selectSingleNode("TelephoneNo")
                    && !Seller.selectSingleNode("TelephoneNo").equals("")) {
                TelephoneNo = Seller.selectSingleNode("TelephoneNo").getText();
            }
            // 销方银行
            String Bank = "";
            if (null != Seller.selectSingleNode("Bank")
                    && !Seller.selectSingleNode("Bank").equals("")) {
                Bank = Seller.selectSingleNode("Bank").getText();
            }
            // 销方银行账号
            String BankAcc = "";
            if (null != Seller.selectSingleNode("BankAcc")
                    && !Seller.selectSingleNode("BankAcc").equals("")) {
                BankAcc = Seller.selectSingleNode("BankAcc").getText();
            }
            //二级节点--待开票信息
            List<Element> xnList = xmlDoc.selectNodes("Responese/ReturnData/Orders");
            if (null != xnList && xnList.size() > 0) {
                for (Element xn : xnList) {
                    Jyxxsq jyxxsq = new Jyxxsq();
                    //三级节点--待开票交易主信息
                    Element orderMainMap = (Element) xn.selectSingleNode("OrderMain");
                    // 订单号
                    String orderNo = "";
                    if (null != orderMainMap.selectSingleNode("OrderNo")
                            && !orderMainMap.selectSingleNode("OrderNo").equals("")) {
                        orderNo = orderMainMap.selectSingleNode("OrderNo").getText();
                    }
                    // 订单时间
                    String orderDate = "";
                    if (null != orderMainMap.selectSingleNode("OrderDate")
                            && !orderMainMap.selectSingleNode("OrderDate").equals("")) {
                        orderDate = orderMainMap.selectSingleNode("OrderDate").getText();
                    }
                    // 征税方式
                    String chargeTaxWay = "";
                    if (null != orderMainMap.selectSingleNode("ChargeTaxWay")
                            && !orderMainMap.selectSingleNode("ChargeTaxWay").equals("")) {
                        chargeTaxWay = orderMainMap.selectSingleNode("ChargeTaxWay").getText();
                    }
                    // 价税合计
                    String totalAmount = "";
                    if (null != orderMainMap.selectSingleNode("TotalAmount")
                            && !orderMainMap.selectSingleNode("TotalAmount").equals("")) {
                        totalAmount = orderMainMap.selectSingleNode("TotalAmount").getText();
                    }
                    // 含税标志
                    String taxMark = "";
                    if (null != orderMainMap.selectSingleNode("TaxMark")
                            && !orderMainMap.selectSingleNode("TaxMark").equals("")) {
                        taxMark = orderMainMap.selectSingleNode("TaxMark").getText();
                    }

                    // 备注
                    String remark = "";
                    if (null != orderMainMap.selectSingleNode("Remark")
                            && !orderMainMap.selectSingleNode("Remark").equals("")) {
                        remark = orderMainMap.selectSingleNode("Remark").getText();
                    }
                    jyxxsq.setTqm(ExtractCode);
                    jyxxsq.setDdh(orderNo);
                    SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        jyxxsq.setDdrq(orderDate == null ? new Date() : sim.parse(orderDate));
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Xf x = new Xf();
                    x.setGsdm(gsdm);
                    x.setXfsh(Identifier);
                    //测试销方
                    //x.setXfsh("500102010003643");
                    Xf xf = xfService.findOneByParams(x);
                    if(null==xf){
                        rsMap.put("jyxxsqList", jyxxsqList);
                        rsMap.put("jymxsqList", jymxsqList);
                        rsMap.put("jyzfmxList", jyzfmxList);
                        rsMap.put("error","9003:销方信息未维护，请联系商家");
                        return rsMap;
                    }
                    Map params=new HashMap();
                    params.put("xfid",xf.getId());
                    Skp skp=skpService.findOneByParams(params);
                    if(skp==null){
                        rsMap.put("jyxxsqList", jyxxsqList);
                        rsMap.put("jymxsqList", jymxsqList);
                        rsMap.put("jyzfmxList", jyzfmxList);
                        rsMap.put("error", "开票点信息未维护，请联系商家!");
                        return rsMap;
                    }
                    jyxxsq.setXfid(xf.getId());
                    jyxxsq.setJylsh(ExtractCode);
                    //测试
                    jyxxsq.setJshj(Double.valueOf(totalAmount));
                    jyxxsq.setHsbz(taxMark);
                    jyxxsq.setBz(remark);
                    jyxxsq.setFpzldm(InvType);
                    jyxxsq.setZsfs(chargeTaxWay);
                    jyxxsq.setKpr(xf.getKpr());
                    jyxxsq.setSkr(xf.getSkr());
                    jyxxsq.setFhr(xf.getFhr());
                    jyxxsq.setKpddm(ClientNO);
                    jyxxsq.setXfmc(Name);
                    jyxxsq.setXfsh(Identifier);
                    jyxxsq.setXfdz(Address);
                    jyxxsq.setXfdh(TelephoneNo);
                    jyxxsq.setXfyh(Bank);
                    jyxxsq.setXfyhzh(BankAcc);
                    jyxxsq.setYkpjshj(Double.valueOf("0.00"));
                    jyxxsq.setYxbz("1");
                    jyxxsq.setLrsj(new Date());
                    jyxxsq.setLrry(lrry);
                    jyxxsq.setXgry(lrry);
                    jyxxsq.setFpczlxdm("11");
                    jyxxsq.setXgsj(new Date());
                    jyxxsq.setGsdm(gsdm);
                    jyxxsq.setSjly("1");
                    jyxxsq.setClztdm("00");
                    jyxxsq.setQjzk(0d);
                    jyxxsqList.add(jyxxsq);
                    //三级节点--待开票交易明细商品信息
                    Element OrderDetails = (Element) xn.selectSingleNode("OrderDetails");
                    //四级节点
                    List<Element> orderDetailsList = (List<Element>) OrderDetails.elements("ProductItem");
                    if (null != orderDetailsList && orderDetailsList.size() > 0) {
                        int spmxxh = 0;
                        for (Element orderDetails : orderDetailsList) {
                            Jymxsq jymxsq = new Jymxsq();
                            // Map ProductItem = (Map) orderDetailsList.get(j);
                            spmxxh++;

                            // 商品编码
                            String ProductCode = "";
                            if (null != orderDetails.selectSingleNode("ProductCode")
                                    && !orderDetails.selectSingleNode("ProductCode").equals("")) {
                                ProductCode = orderDetails.selectSingleNode("ProductCode").getText();
                            }

                            jymxsq.setDdh(jyxxsq.getDdh());
                           // jymxsq.setSpdm(ProductCode);
                            // 商品名称
                            String ProductName = "";
                            if (null != orderDetails.selectSingleNode("ProductName")
                                    && !orderDetails.selectSingleNode("ProductName").equals("")) {
                                ProductName = orderDetails.selectSingleNode("ProductName").getText();
                            }

                            jymxsq.setSpmc(ProductName);
                            jymxsq.setDdh(jyxxsq.getDdh());
                            jymxsq.setHsbz(jyxxsq.getHsbz());
                            // 发票行性质
                            String RowType = "";
                            if (null != orderDetails.selectSingleNode("RowType")
                                    && !orderDetails.selectSingleNode("RowType").equals("")) {
                                RowType = orderDetails.selectSingleNode("RowType").getText();
                            }

                            jymxsq.setFphxz(RowType);
                            // 商品规格型号
                            String Spec = "";
                            if (null != orderDetails.selectSingleNode("Spec")
                                    && !orderDetails.selectSingleNode("Spec").equals("")) {
                                Spec = orderDetails.selectSingleNode("Spec").getText();
                            }

                            jymxsq.setSpggxh(Spec);
                            // 商品单位
                            String Unit = "";
                            if (null != orderDetails.selectSingleNode("Unit")
                                    && !orderDetails.selectSingleNode("Unit").equals("")) {
                                Unit = orderDetails.selectSingleNode("Unit").getText();
                            }

                            jymxsq.setSpdw(Unit);
                            // 商品数量
                            String Quantity = "";
                            if (null != orderDetails.selectSingleNode("Quantity")
                                    && !orderDetails.selectSingleNode("Quantity").equals("")) {
                                Quantity = orderDetails.selectSingleNode("Quantity").getText();
                                try{jymxsq.setSps(Double.valueOf(Quantity));}catch (Exception e){jymxsq.setSps(null);}
                            }
                            // 商品单价
                            String UnitPrice = "";
                            if (null != orderDetails.selectSingleNode("UnitPrice")
                                    && !orderDetails.selectSingleNode("UnitPrice").equals("")) {
                                UnitPrice = orderDetails.selectSingleNode("UnitPrice").getText();
                                try{jymxsq.setSpdj(Double.valueOf(UnitPrice));}catch (Exception e){jymxsq.setSpdj(null);}
                            }
                            // 商品金额
                            String Amount = "";
                            if (null != orderDetails.selectSingleNode("Amount")
                                    && !orderDetails.selectSingleNode("Amount").equals("")) {
                                Amount = orderDetails.selectSingleNode("Amount").getText();
                                try{jymxsq.setSpje(Double.valueOf(Amount));}catch (Exception e){jymxsq.setSpje(null);}

                            }
                            // 扣除金额
                            String DeductAmount = "";
                            if (null != orderDetails.selectSingleNode("DeductAmount")
                                    && !orderDetails.selectSingleNode("DeductAmount").equals("")) {
                                DeductAmount = orderDetails.selectSingleNode("DeductAmount").getText();
                                jymxsq.setKce((null == DeductAmount || DeductAmount.equals("")) ? Double.valueOf("0.00")
                                        : Double.valueOf(DeductAmount));
                            }
                            //商品税率
                            String TaxRate = "";
                            if (null != orderDetails.selectSingleNode("TaxRate")
                                    && !orderDetails.selectSingleNode("TaxRate").equals("")) {
                                TaxRate = orderDetails.selectSingleNode("TaxRate").getText();
                                jymxsq.setSpsl(Double.valueOf(TaxRate));
                            }
                            //商品税额
                            String TaxAmount = "";
                            if (null != orderDetails.selectSingleNode("TaxAmount")
                                    && !orderDetails.selectSingleNode("TaxAmount").equals("")) {
                                TaxAmount = orderDetails.selectSingleNode("TaxAmount").getText();
                                if(TaxAmount!=null&&!"".equals(TaxAmount)){
                                    jymxsq.setSpse(Double.valueOf(TaxAmount));
                                }
                            }
                            //价税合计
                            String MxTotalAmount = "";
                            if (null != orderDetails.selectSingleNode("MxTotalAmount")
                                    && !orderDetails.selectSingleNode("MxTotalAmount").equals("")) {
                                MxTotalAmount = orderDetails.selectSingleNode("MxTotalAmount").getText();
                                jymxsq.setJshj(Double.valueOf(MxTotalAmount));
                            }
                            //商品明细序号
                            jymxsq.setSpmxxh(spmxxh);
                            //可开具金额
                            jymxsq.setKkjje(Double.valueOf(MxTotalAmount));
                            //已开具金额
                            jymxsq.setYkjje(0d);
                            //商品自行编码
                            String VenderOwnCode = "";
                            if (null != orderDetails.selectSingleNode("VenderOwnCode")
                                    && !orderDetails.selectSingleNode("VenderOwnCode").equals("")) {
                                VenderOwnCode = orderDetails.selectSingleNode("VenderOwnCode").getText();
                            }
                            jymxsq.setSpzxbm(VenderOwnCode);

                            Map spvoMap = new HashMap();
                            spvoMap.put("gsdm",gsdm);
                            spvoMap.put("spdm",VenderOwnCode);
                            Spvo spvo = spvoService.findOneSpvo(spvoMap);
                            if(null==spvo){
                                rsMap.put("jyxxsqList", jyxxsqList);
                                rsMap.put("jymxsqList", jymxsqList);
                                rsMap.put("jyzfmxList", jyzfmxList);
                                rsMap.put("error", "商品信息未维护，请联系商家!");
                                return rsMap;
                            }
                            jymxsq.setSpdm(spvo.getSpbm());
                            jymxsq.setYhzcbs(spvo.getYhzcbs());
                            jymxsq.setLslbz(spvo.getLslbz());
                            jymxsq.setYhzcmc(spvo.getYhzcmc());
                            //优惠政策标识
                            String PolicyMark = "";
                            if (null != orderDetails.selectSingleNode("PolicyMark")
                                    && !orderDetails.selectSingleNode("PolicyMark").equals("")) {
                                PolicyMark = orderDetails.selectSingleNode("PolicyMark").getText();
                            }
                            //零税率标志
                            String TaxRateMark = "";
                            if (null != orderDetails.selectSingleNode("TaxRateMark")
                                    && !orderDetails.selectSingleNode("TaxRateMark").equals("")) {
                                TaxRateMark = orderDetails.selectSingleNode("TaxRateMark").getText();
                            }
                            //优惠政策名称
                            String PolicyName = "";
                            if (null != orderDetails.selectSingleNode("PolicyName")
                                    && !orderDetails.selectSingleNode("PolicyName").equals("")) {
                                PolicyName = orderDetails.selectSingleNode("PolicyName").getText();
                            }
                            jymxsq.setGsdm(gsdm);
                            jymxsq.setLrry(lrry);
                            jymxsq.setLrsj(new Date());
                            jymxsq.setXgry(lrry);
                            jymxsq.setXgsj(new Date());
                            jymxsq.setYxbz("1");
                            jymxsqList.add(jymxsq);
                        }
                    }
                }
            }
            rsMap.put("jyxxsqList", jyxxsqList);
            rsMap.put("jymxsqList", jymxsqList);
            rsMap.put("jyzfmxList", jyzfmxList);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return rsMap;
    }

    /**
     * 全家--调用接口获取数据
     * @param ExtractCode
     * @param gsdm
     * @return
     */
    public Map getData(String ExtractCode,String gsdm){
            Map parmsMap=new HashMap();
            String strMessage = "";
            BufferedReader reader = null;
            StringBuffer buffer = new StringBuffer();
            Map parms=new HashMap();
            parms.put("gsdm",gsdm);
            Gsxx gsxx=gsxxService.findOneByParams(parms);
            Map resultMap = null;
//            HttpPost httpPost = new HttpPost("http://103.13.247.68:6180/EinvoiceWebvoiceWeb/strMessageervice/EInvoiceWS/QueryOrder");
            HttpPost httpPost = new HttpPost("http://172.16.0.221:6180/EinvoiceWeb/service/EInvoiceWS/QueryOrder");
            CloseableHttpResponse response = null;
            RequestConfig requestConfig = RequestConfig.custom().
                    setSocketTimeout(120*1000).setConnectionRequestTimeout(120*1000).setConnectTimeout(120*1000).build();
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build();
            //httpPost.setConfig(requestConfig);
            httpPost.addHeader("Content-Type", "application/json");
            try {
                Map nvps = new HashMap();
                String Secret=getSign(ExtractCode,gsxx.getSecretKey());
                logger.info("-------------"+ExtractCode+"----------"+gsxx.getSecretKey());
                nvps.put("ExtractCode", ExtractCode);
                nvps.put("sign", Secret);
                StringEntity requestEntity = new StringEntity(JSON.toJSONString(nvps), "utf-8");
                httpPost.setEntity(requestEntity);
                response = httpClient.execute(httpPost, new BasicHttpContext());
                if (response.getStatusLine().getStatusCode() != 200) {
                    System.out.println("request url failed, http code=" + response.getStatusLine().getStatusCode()
                            + ", url=" + "");
                }
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    reader = new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8"));
                    while ((strMessage = reader.readLine()) != null) {
                        buffer.append(strMessage);
                    }
                }
                System.out.println("接收返回值:" + buffer.toString());
                parmsMap=interpreting(gsdm,buffer.toString());
                String error = (String) parmsMap.get("error");
                if(error==null) {
                    List<Jyxxsq> jyxxsqList = (List) parmsMap.get("jyxxsqList");
                    List<Jymxsq> jymxsqList = (List) parmsMap.get("jymxsqList");
                    List<Jyzfmx> jyzfmxList = (List) parmsMap.get("jyzfmxList");
                   // String tmp = this.checkAll(jyxxsqList, jymxsqList, jyzfmxList, gsdm);
                    String msg = checkOrderUtil.checkOrders(jyxxsqList,jymxsqList,jyzfmxList,gsdm,"");
                    parmsMap.put("tmp", msg);
                }
            }catch (Exception e){
                System.out.println("request url=" + "" + ", exception, msg=" + e.getMessage());
                e.printStackTrace();
                e.printStackTrace();
            }
            return parmsMap;
    }

    /**
     * 校验方法
     * @param jyxxsqList
     * @param jymxsqList
     * @param jyzfmxList
     * @param gsdm
     * @return
     */
    public String checkAll(List<Jyxxsq> jyxxsqList, List<Jymxsq> jymxsqList, List<Jyzfmx> jyzfmxList, String gsdm) {
        String result = "";
        String ddh = "";
        String ddh2 = "";
        List tqmList = new ArrayList();
        List jylshList = new ArrayList();
        Map tqmMap = new HashMap();
        Map jylshMap = new HashMap();
        Jyxxsq jyxxsq = new Jyxxsq();
        Jymxsq jymxsq = new Jymxsq();
        Jyzfmx jyzfmx = new Jyzfmx();

        for (int i = 0; i < jyxxsqList.size(); i++) {
            BigDecimal ajshj;
            BigDecimal jshj = new BigDecimal("0");
            BigDecimal jshj2 = new BigDecimal("0");
            for (int j = 0; j < jymxsqList.size(); j++) {
                jymxsq = (Jymxsq) jymxsqList.get(j);
                ddh = jymxsq.getDdh();
                if (jyxxsqList.get(i).getDdh().equals(jymxsq.getDdh())) {
                    jyxxsq = jyxxsqList.get(i);
                    if (ddh != null && !ddh.equals("")) {
                        if (ddh.length() > 20) {
                            result += "明细数据" + ddh + ":订单号太长;";
                        }
                    } else {
                        result += "明细数据订单号不能为空;";
                    }
                    String ProductCode = (String) jymxsq.getSpdm();
                    if (ProductCode == null) {
                        result += "订单号为" + ddh + "的订单ProductCode为空";
                    } else if (ProductCode.length() != 19) {
                        result += "订单号为" + ddh + "的订单ProductCode不等于19位;";
                    }
                    // 商品名称
                    String ProductName = (String) jymxsq.getSpmc();
                    if (ProductName == null) {
                        result += "订单号为" + ddh + "的订单ProductName为空！";
                    } else if (ProductName.length() > 50) {
                        result += "订单号为" + ddh + "的订单ProductName太长！";
                    }
                    // 发票行性质
                    String RowType = (String) jymxsq.getFphxz();
                    if (RowType == null) {
                        result += "订单号为" + ddh + "的订单RowType为空;";
                    } else if (!("0".equals(RowType) || "1".equals(RowType) || "2".equals(RowType))) {
                        result += "订单号为" + ddh + "的订单RowType只能填写0，1或2;";
                    }


                    // 商品金额
                    String Amount = String.valueOf(jymxsq.getSpje());
                    if (Amount == null) {
                        result += "订单号为" + ddh + "的订单商品Amount为空;";
                    } else if (!Amount.matches("^\\-?[0-9]{0,15}+(.[0-9]{0,2})?$")) {
                        result += "订单号为" + ddh + "的订单Amount格式不正确！";
                    }
                    // 商品税率
                    String TaxRate = String.valueOf(jymxsq.getSpsl());
                    if (TaxRate == null) {
                        result = "订单号为" + ddh + "的订单TaxRate为空;";
                    } else {
                        double taxRate = Double.valueOf(TaxRate);
                        if (!(taxRate == 0 || taxRate == 0.03 || taxRate == 0.04
                                || taxRate == 0.06 || taxRate == 0.11 || taxRate == 0.13
                                || taxRate == 0.17)) {
                            result += "订单号为" + ddh + "的订单TaxRate格式有误;";
                        }
                    }
                    if((jymxsq.getSpdj()==null&&jymxsq.getSps()!=null)||(jymxsq.getSps()==null&&jymxsq.getSpdj()!=null)){
                        result += "订单号为" + ddh + "的订单第" + i+1+ "行商品单价，商品数量必须全部为空或者全部不为空！";
                    }
                    if (jymxsq.getSpdj() != null && jymxsq.getSps() != null && jymxsq.getSpje() != null) {
                        double res = jymxsq.getSpdj() * jymxsq.getSps();
                        BigDecimal big1 = new BigDecimal(res);
                        big1 = big1.setScale(2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal big2 = new BigDecimal(jymxsq.getSpje());
                        big2 = big2.setScale(2, BigDecimal.ROUND_HALF_UP);
                        if (big1.compareTo(big2) != 0) {
                            result += "订单号为" + ddh + "的订单第" + i+1+ "行商品单价，商品数量，商品金额之间的计算校验不通过，请检查！";
                        }
                    }
                    if(jymxsq.getSpdj() != null && jymxsq.getSps() != null && jymxsq.getSpje() != null){
                        double spdj = jymxsq.getSpdj();
                        double sps = jymxsq.getSps();
                        BigDecimal big1 = new BigDecimal(spdj);
                        BigDecimal big2 = new BigDecimal(sps);
                        if((big1.compareTo(new BigDecimal(0))==0)||(big2.compareTo(new BigDecimal(0))==0)){
                            result += "订单号为" + ddh + "的订单第" + i+1+ "行商品单价或商品数量不能为零！";
                        }
                    }
                    // 商品税额
                    String TaxAmount = String.valueOf(jymxsq.getSpse());
                    if (TaxAmount != null && TaxAmount.equals("^\\-?[0-9]{0,15}+(.[0-9]{0,2})?$")) {
                        result += "订单号为" + ddh + "的订单第" + i +1+ "条商品TaxAmount格式不正确！";
                    }

                    // 校验金额误差
                    String TaxMark = jyxxsq.getHsbz();
                    double je = Double.valueOf(Amount);
                    double se = 0;
                    //含税时，忽略税额
                    if (TaxMark.equals("0")) {
                        if (TaxAmount != null && !"".equals(TaxAmount)) {
                            se = Double.valueOf(TaxAmount);
                        }
                    }
                    double sl = Double.valueOf(TaxRate);
                    if (TaxMark.equals("0") && je * sl - se >= 0.0625) {
                        result += "订单号为" + ddh + "的订单(Amount，TaxRate，TaxAmount)之间的校验不通过";
                    }

                    BigDecimal bd = new BigDecimal(je);
                    BigDecimal bd1 = new BigDecimal(se);
                    ajshj = bd.add(bd1);
                    jshj = jshj.add(ajshj);
                    String ChargeTaxWay = jyxxsq.getZsfs();
                    String DeductAmount = String.valueOf(jymxsq.getKce());
                    if (ChargeTaxWay.equals("2") && (null == DeductAmount || DeductAmount.equals(""))) {
                        result += "订单号为" + ddh + "的订单DeductAmount不能为空";
                    }
                }
            }
            // 价税合计
            Double  TotalAmount = jyxxsq.getJshj();
            if (TotalAmount == null) {
                result += ddh + ":价税合计为空;";
            }
            // 订单号
            ddh = jyxxsq.getDdh();
            if (ddh != null && !ddh.equals("")) {
                if (ddh.length() > 100) {
                    result += "交易数据" + ddh + ":订单号太长;";
                }
            } else {
                result += "交易数据订单号不能为空;";
            }
            // 订单时间
            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            if (null != jyxxsq.getDdrq() && !jyxxsq.getDdrq().equals("")) {
                String OrderDate = sim.format(jyxxsq.getDdrq());
                Pattern p = Pattern.compile(
                        "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s((([0-1][0-9])|(2?[0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
                if (OrderDate != null && !p.matcher(OrderDate).matches()) {
                    result += ddh + ":订单时间格式不正确;";
                }
            }
            BigDecimal bd2 = new BigDecimal(jyxxsq.getJshj());
            if (bd2.setScale(2, BigDecimal.ROUND_HALF_UP).subtract(jshj.setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue() != 0.0) {
                result += "订单号为" + ddh + "的订单TotalAmount，Amount，TaxAmount计算校验不通过";
            }
            // 提取码校验
            String tqm = jyxxsq.getTqm();
            if (null !=tqm && !tqm.equals("")) {
                tqmList.add(tqm);
            }
            // 交易流水号校验
            String jylsh = jyxxsq.getJylsh();
            jylshList.add(jylsh);
            // 一次性校验提取码和交易流水号是否重复上传，每笔交易流水号必须唯一，提取码也唯一,订单号也必须唯一
            if (null != tqmList && !tqmList.isEmpty()) {
                tqmMap.put("tqmList", tqmList);
                tqmMap.put("gsdm", gsdm);
                List<Jyxxsq> t1 = jyxxsqService.findAllByTqms(tqmMap);
                if (null != t1 && !t1.isEmpty()) {
                    for (int a = 0; a < t1.size(); a++) {
                        Jyxxsq jy1 = (Jyxxsq) t1.get(a);
                        result += "提取码" + jy1.getTqm() + "已存在;";
                    }
                }
            }
            jylshMap.put("jylshList", jylshList);
            jylshMap.put("gsdm", gsdm);
            List<Jyxxsq> t2 = jyxxsqService.findAllByJylshs(jylshMap);
            if (null != t2 && !t2.isEmpty()) {
                for (int b = 0; b < t2.size(); b++) {
                    Jyxxsq jy2 = (Jyxxsq) t2.get(b);
                    result += "交易流水号" + jy2.getJylsh() + "已存在;";
                }
            }
            if (null != jyzfmxList && !jyzfmxList.isEmpty()) {
                List kpfsList = new ArrayList();
                Map params = new HashMap();
                params.put("gsdm", gsdm);
                params.put("kpfsList", kpfsList);
                List<Zffs> zffsList = zffsService.findAllByParams(params);
                if(null == zffsList ||zffsList.isEmpty()){
                    result += "请去平台支付方式管理维护对应的支付方式;";
                }
                String flag ="0";
                for (int j = 0; j < jyzfmxList.size(); j++) {
                    jyzfmx = (Jyzfmx) jyzfmxList.get(j);
                    if(null != zffsList && !zffsList.isEmpty()){
                        for(int k=0;k<zffsList.size();k++){
                            Zffs  zffs = zffsList.get(k);
                            if(jyzfmx.getZffsDm().equals(zffs.getZffsDm())){
                                flag = "1";
                            }
                        }
                        if(flag.equals("0")){
                            result += "订单号为" + ddh + "的订单,支付方式代码"+jyzfmx.getZffsDm()+"未在平台维护;";
                        }
                    }
                    ddh2 = jyzfmx.getDdh();
                    if (ddh.equals(ddh2)) {
                        BigDecimal zfje = new BigDecimal(jyzfmx.getZfje());
                        jshj2 = jshj2.add(zfje);
                    }
                }
                if (jshj2.compareTo(bd2) !=0) {
                    result += "订单号为" + ddh + "的订单PayPrice合计与TotalAmount不等;";
                }
            }
        }
        return result;
    }
    /**
     * 全家 -- 解析数据
     * @param gsdm
     * @param data
     * @return
     * @throws Exception
     */
    public Map interpreting(String gsdm,String data)throws Exception {
        Map params1 = new HashMap();
        params1.put("gsdm", gsdm);
        Yh yh = yhService.findOneByParams(params1);
        int lrry = yh.getId();
        List<Jyxxsq> jyxxsqList = new ArrayList();
        List<Jymxsq> jymxsqList = new ArrayList();
        List<Jyzfmx> jyzfmxList = new ArrayList<Jyzfmx>();
        Map rsMap=new HashMap();
        Document xmlDoc = null;
        OMElement root = null;
        try {
            xmlDoc = DocumentHelper.parseText(data);
            root = XmlMapUtils.xml2OMElement(data);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Map rootMap = XmlMapUtils.xml2Map(root, "Responese");
        String ReturnCode=rootMap.get("ReturnCode").toString();
        String ReturnMessage=rootMap.get("ReturnMessage").toString();
        if(!ReturnCode.equals("0000")){
            rsMap.put("jyxxsqList", jyxxsqList);
            rsMap.put("jymxsqList", jymxsqList);
            rsMap.put("jyzfmxList", jyzfmxList);
            rsMap.put("error",ReturnCode+":"+ReturnMessage);
            logger.info("------错误信息--------"+ReturnCode+":"+ReturnMessage);
            return rsMap;
        }
        Element ReturnData  = (Element) xmlDoc.selectSingleNode("Responese/ReturnData");
        // 提取码
        String ExtractCode = "";
        if (null != ReturnData.selectSingleNode("ExtractCode")
                && !ReturnData.selectSingleNode("ExtractCode").equals("")) {
            ExtractCode = ReturnData.selectSingleNode("ExtractCode").getText();
        }
        // 会员号
        String MemberID = "";
        if (null != ReturnData.selectSingleNode("MemberID")
                && !ReturnData.selectSingleNode("MemberID").equals("")) {
            MemberID = ReturnData.selectSingleNode("MemberID").getText();
        }
        // 发票种类
        String InvType = "";
        if (null != ReturnData.selectSingleNode("InvType")
                && !ReturnData.selectSingleNode("InvType").equals("")) {
            InvType = ReturnData.selectSingleNode("InvType").getText();
        }
        // 商品编码版本号
        String Spbmbbh = "";
        if (null != ReturnData.selectSingleNode("Spbmbbh")
                && !ReturnData.selectSingleNode("Spbmbbh").equals("")) {
            Spbmbbh = ReturnData.selectSingleNode("Spbmbbh").getText();
        }
        // 门店号
        String StoreNo = "";
        if (null != ReturnData.selectSingleNode("StoreNo")
                && !ReturnData.selectSingleNode("StoreNo").equals("")) {
            StoreNo = ReturnData.selectSingleNode("StoreNo").getText();
        }
        Element Seller  = (Element) xmlDoc.selectSingleNode("Responese/ReturnData/Seller");
        // 销方税号
        String Identifier = "";
        if (null != Seller.selectSingleNode("Identifier")
                && !Seller.selectSingleNode("Identifier").equals("")) {
            Identifier = Seller.selectSingleNode("Identifier").getText();
        }
        // 销方名称
        String Name = "";
        if (null != Seller.selectSingleNode("Name")
                && !Seller.selectSingleNode("Name").equals("")) {
            Name = Seller.selectSingleNode("Name").getText();
        }
        // 销方地址
        String Address = "";
        if (null != Seller.selectSingleNode("Address")
                && !Seller.selectSingleNode("Address").equals("")) {
            Address = Seller.selectSingleNode("Address").getText();
        }
        // 销方电话
        String TelephoneNo = "";
        if (null != Seller.selectSingleNode("TelephoneNo")
                && !Seller.selectSingleNode("TelephoneNo").equals("")) {
            TelephoneNo = Seller.selectSingleNode("TelephoneNo").getText();
        }
        // 销方银行
        String Bank = "";
        if (null != Seller.selectSingleNode("Bank")
                && !Seller.selectSingleNode("Bank").equals("")) {
            Bank = Seller.selectSingleNode("Bank").getText();
        }
        // 销方银行账号
        String BankAcc = "";
        if (null != Seller.selectSingleNode("BankAcc")
                && !Seller.selectSingleNode("BankAcc").equals("")) {
            BankAcc = Seller.selectSingleNode("BankAcc").getText();
        }
        List<Element> xnList = xmlDoc.selectNodes("Responese/ReturnData/Orders");
        if (null != xnList && xnList.size() > 0) {
            for (Element xn : xnList) {
                Jyxxsq jyxxsq = new Jyxxsq();
                Element orderMainMap = (Element) xn.selectSingleNode("OrderMain");
                // 订单号
                String orderNo = "";
                if (null != orderMainMap.selectSingleNode("OrderNo")
                        && !orderMainMap.selectSingleNode("OrderNo").equals("")) {
                    orderNo = orderMainMap.selectSingleNode("OrderNo").getText();
                }
                // 订单时间
                String orderDate = "";
                if (null != orderMainMap.selectSingleNode("OrderDate")
                        && !orderMainMap.selectSingleNode("OrderDate").equals("")) {
                    orderDate = orderMainMap.selectSingleNode("OrderDate").getText();
                }
                // 征税方式
                String chargeTaxWay = "";
                if (null != orderMainMap.selectSingleNode("ChargeTaxWay")
                        && !orderMainMap.selectSingleNode("ChargeTaxWay").equals("")) {
                    chargeTaxWay = orderMainMap.selectSingleNode("ChargeTaxWay").getText();
                }
                // 价税合计
                String totalAmount = "";
                if (null != orderMainMap.selectSingleNode("TotalAmount")
                        && !orderMainMap.selectSingleNode("TotalAmount").equals("")) {
                    totalAmount = orderMainMap.selectSingleNode("TotalAmount").getText();
                }
                // 含税标志
                String taxMark = "";
                if (null != orderMainMap.selectSingleNode("TaxMark")
                        && !orderMainMap.selectSingleNode("TaxMark").equals("")) {
                    taxMark = orderMainMap.selectSingleNode("TaxMark").getText();
                }

                // 备注
                String remark = "";
                if (null != orderMainMap.selectSingleNode("Remark")
                        && !orderMainMap.selectSingleNode("Remark").equals("")) {
                    remark = orderMainMap.selectSingleNode("Remark").getText();
                }
                jyxxsq.setTqm(ExtractCode);
                jyxxsq.setDdh(orderNo);
                SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    jyxxsq.setDdrq(orderDate == null ? new Date() : sim.parse(orderDate));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Xf x = new Xf();
                x.setGsdm(gsdm);
                x.setXfsh(Identifier);
                Xf xf = xfService.findOneByParams(x);
                if(null==xf){
                    rsMap.put("jyxxsqList", jyxxsqList);
                    rsMap.put("jymxsqList", jymxsqList);
                    rsMap.put("jyzfmxList", jyzfmxList);
                    rsMap.put("error","9003:开票信息有误，请联系商家");
                    logger.info("------错误信息--------"+"9003:开票信息有误，请联系商家");
                    return rsMap;
                }
                Map params=new HashMap();
                params.put("xfid",xf.getId());
                Skp skp=skpService.findOneByParams(params);
                jyxxsq.setXfid(xf.getId());
                jyxxsq.setJylsh(ExtractCode);
                //String kpddm=ExtractCode.substring(4,10);
                jyxxsq.setKpddm(skp.getKpddm());
                jyxxsq.setJshj(Double.valueOf(totalAmount));
                jyxxsq.setHsbz(taxMark);
                jyxxsq.setBz(remark);
                jyxxsq.setFpzldm(InvType);
                jyxxsq.setZsfs(chargeTaxWay);
                jyxxsq.setKpr(xf.getKpr());
                jyxxsq.setSkr(xf.getSkr());
                jyxxsq.setFhr(xf.getFhr());
                jyxxsq.setXfsh(Identifier);
                jyxxsq.setXfmc(Name);
                jyxxsq.setXfdz(Address);
                jyxxsq.setXfdh(TelephoneNo);
                jyxxsq.setXfyh(Bank);
                jyxxsq.setXfyhzh(BankAcc);
                jyxxsq.setYkpjshj(Double.valueOf("0.00"));
                jyxxsq.setYxbz("1");
                jyxxsq.setLrsj(new Date());
                jyxxsq.setLrry(lrry);
                jyxxsq.setXgry(lrry);
                jyxxsq.setFpczlxdm("11");
                jyxxsq.setXgsj(new Date());
                jyxxsq.setGsdm(gsdm);
                jyxxsq.setSjly("1");
                jyxxsq.setClztdm("00");
                jyxxsq.setQjzk(0d);
                jyxxsqList.add(jyxxsq);
                Element OrderDetails = (Element) xn.selectSingleNode("OrderDetails");
                List<Element> orderDetailsList = (List<Element>) OrderDetails.elements("ProductItem");
                if (null != orderDetailsList && orderDetailsList.size() > 0) {
                    int spmxxh = 0;
                    for (Element orderDetails : orderDetailsList) {
                        Jymxsq jymxsq = new Jymxsq();
                        // Map ProductItem = (Map) orderDetailsList.get(j);
                        spmxxh++;
                        // 商品编码
                        String ProductCode = "";
                        if (null != orderDetails.selectSingleNode("ProductCode")
                                && !orderDetails.selectSingleNode("ProductCode").equals("")) {
                            ProductCode = orderDetails.selectSingleNode("ProductCode").getText();
                        }
                        if(ProductCode==null||ProductCode.equals("")){
                            rsMap.put("jyxxsqList", jyxxsqList);
                            rsMap.put("jymxsqList", jymxsqList);
                            rsMap.put("jyzfmxList", jyzfmxList);
                            rsMap.put("error","9003:开票信息有误，请联系商家");
                            logger.info("------错误信息--------"+"9003:开票信息有误，请联系商家");
                            return rsMap;
                        }
                        jymxsq.setDdh(jyxxsq.getDdh());
                        jymxsq.setSpdm(ProductCode);
                        // 商品名称
                        String ProductName = "";
                        if (null != orderDetails.selectSingleNode("ProductName")
                                && !orderDetails.selectSingleNode("ProductName").equals("")) {
                            ProductName = orderDetails.selectSingleNode("ProductName").getText();
                        }

                        jymxsq.setSpmc(ProductName);
                        jymxsq.setDdh(jyxxsq.getDdh());
                        jymxsq.setHsbz(jyxxsq.getHsbz());
                        // 发票行性质
                        String RowType = "";
                        if (null != orderDetails.selectSingleNode("RowType")
                                && !orderDetails.selectSingleNode("RowType").equals("")) {
                            RowType = orderDetails.selectSingleNode("RowType").getText();
                        }

                        jymxsq.setFphxz(RowType);
                        // 商品规格型号
                        String Spec = "";
                        if (null != orderDetails.selectSingleNode("Spec")
                                && !orderDetails.selectSingleNode("Spec").equals("")) {
                            Spec = orderDetails.selectSingleNode("Spec").getText();
                        }

                        jymxsq.setSpggxh(Spec);
                        // 商品单位
                        String Unit = "";
                        if (null != orderDetails.selectSingleNode("Unit")
                                && !orderDetails.selectSingleNode("Unit").equals("")) {
                            Unit = orderDetails.selectSingleNode("Unit").getText();
                        }

                        jymxsq.setSpdw(Unit);
                        // 商品数量
                        String Quantity = "";
                        if (null != orderDetails.selectSingleNode("Quantity")
                                && !orderDetails.selectSingleNode("Quantity").equals("")) {
                            Quantity = orderDetails.selectSingleNode("Quantity").getText();
                            try{jymxsq.setSps(Double.valueOf(Quantity));}catch (Exception e){jymxsq.setSps(null);}
                        }

                        // 商品单价
                        String UnitPrice = "";
                        if (null != orderDetails.selectSingleNode("UnitPrice")
                                && !orderDetails.selectSingleNode("UnitPrice").equals("")) {
                            UnitPrice = orderDetails.selectSingleNode("UnitPrice").getText();
                            try{jymxsq.setSpdj(Double.valueOf(UnitPrice));}catch (Exception e){jymxsq.setSpdj(null);}
                        }

                        // 商品金额
                        String Amount = "";
                        if (null != orderDetails.selectSingleNode("Amount")
                                && !orderDetails.selectSingleNode("Amount").equals("")) {
                            Amount = orderDetails.selectSingleNode("Amount").getText();
                            try{jymxsq.setSpje(Double.valueOf(Amount));}catch (Exception e){jymxsq.setSpje(null);}

                        }

                        // 扣除金额
                        String DeductAmount = "";
                        if (null != orderDetails.selectSingleNode("DeductAmount")
                                && !orderDetails.selectSingleNode("DeductAmount").equals("")) {
                            DeductAmount = orderDetails.selectSingleNode("DeductAmount").getText();
                            jymxsq.setKce((null == DeductAmount || DeductAmount.equals("")) ? Double.valueOf("0.00")
                                    : Double.valueOf(DeductAmount));
                        }
                        //商品税率
                        String TaxRate = "";
                        if (null != orderDetails.selectSingleNode("TaxRate")
                                && !orderDetails.selectSingleNode("TaxRate").equals("")) {
                            TaxRate = orderDetails.selectSingleNode("TaxRate").getText();
                            jymxsq.setSpsl(Double.valueOf(TaxRate));
                        }
                        //商品税额
                        String TaxAmount = "";
                        if (null != orderDetails.selectSingleNode("TaxAmount")
                                && !orderDetails.selectSingleNode("TaxAmount").equals("")) {
                            TaxAmount = orderDetails.selectSingleNode("TaxAmount").getText();
                            if(TaxAmount!=null&&!"".equals(TaxAmount)){
                                jymxsq.setSpse(Double.valueOf(TaxAmount));
                            }
                        }
                        //价税合计
                        String MxTotalAmount = "";
                        if (null != orderDetails.selectSingleNode("MxTotalAmount")
                                && !orderDetails.selectSingleNode("MxTotalAmount").equals("")) {
                            MxTotalAmount = orderDetails.selectSingleNode("MxTotalAmount").getText();
                            jymxsq.setJshj(Double.valueOf(MxTotalAmount));
                        }
                        //商品明细序号
                        jymxsq.setSpmxxh(spmxxh);
                        //可开具金额
                        jymxsq.setKkjje(Double.valueOf(MxTotalAmount));
                        //已开具金额
                        jymxsq.setYkjje(0d);
                        //商品自行编码
                        String VenderOwnCode = "";
                        if (null != orderDetails.selectSingleNode("VenderOwnCode")
                                && !orderDetails.selectSingleNode("VenderOwnCode").equals("")) {
                            VenderOwnCode = orderDetails.selectSingleNode("VenderOwnCode").getText();
                        }
                        jymxsq.setSpzxbm(VenderOwnCode);
                        Map spbmMap=new HashMap();
                        spbmMap.put("spbm",ProductCode);
                        spbmMap.put("gsdm",gsdm);
                        vSpbm spbm=vSpbmService.findOneByParams(spbmMap);
                        if(spbm!=null){
                            if(Double.valueOf(TaxRate)>0){
                                jymxsq.setYhzcbs("0");
                                jymxsq.setLslbz("");
                                jymxsq.setYhzcmc("");
                            }else{
                                jymxsq.setYhzcbs(spbm.getYhzcbs().toString());
                                jymxsq.setLslbz(spbm.getLslbz());
                                jymxsq.setYhzcmc(spbm.getYhzcmc());
                            }
                        }
                        //优惠政策标识
                        String PolicyMark = "";
                        if (null != orderDetails.selectSingleNode("PolicyMark")
                                && !orderDetails.selectSingleNode("PolicyMark").equals("")) {
                            PolicyMark = orderDetails.selectSingleNode("PolicyMark").getText();
                        }
                        //零税率标志
                        String TaxRateMark = "";
                        if (null != orderDetails.selectSingleNode("TaxRateMark")
                                && !orderDetails.selectSingleNode("TaxRateMark").equals("")) {
                            TaxRateMark = orderDetails.selectSingleNode("TaxRateMark").getText();
                        }
                        //优惠政策名称
                        String PolicyName = "";
                        if (null != orderDetails.selectSingleNode("PolicyName")
                                && !orderDetails.selectSingleNode("PolicyName").equals("")) {
                            PolicyName = orderDetails.selectSingleNode("PolicyName").getText();
                        }
                        jymxsq.setGsdm(gsdm);
                        jymxsq.setLrry(lrry);
                        jymxsq.setLrsj(new Date());
                        jymxsq.setXgry(lrry);
                        jymxsq.setXgsj(new Date());
                        jymxsq.setYxbz("1");
                        jymxsqList.add(jymxsq);
                    }
                }
                // 获取参数中对应的支付信息

                /*Element payments = (Element) xn.selectSingleNode("Payments");
                if (null != payments && !payments.equals("")) {
                    List<Element> paymentItemList = (List<Element>) payments.elements("PaymentItem");
                    if (null != paymentItemList && paymentItemList.size() > 0) {
                        for (Element PaymentItem : paymentItemList) {
                            Jyzfmx jyzfmx = new Jyzfmx();
                            //支付方式代码
                            String zffsDm = "";
                            if (null != PaymentItem.selectSingleNode("PayCode")
                                    && !PaymentItem.selectSingleNode("PayCode").equals("")) {
                                zffsDm = PaymentItem.selectSingleNode("PayCode").getText();
                                jyzfmx.setZffsDm(zffsDm);
                            }
                            //支付总金额
                            String zfje = "";
                            if (null != PaymentItem.selectSingleNode("PayPrice")
                                    && !PaymentItem.selectSingleNode("PayPrice").equals("")) {
                                zfje = PaymentItem.selectSingleNode("PayPrice").getText();
                                jyzfmx.setZfje(Double.valueOf(zfje));
                            }
                            //支付序列号
                            String PayNumber = "";
                            if (null != PaymentItem.selectSingleNode("PayNumber")
                                    && !PaymentItem.selectSingleNode("PayNumber").equals("")) {
                                PayNumber = PaymentItem.selectSingleNode("PayNumber").getText();
                                jyzfmx.setPaynumber(PayNumber);
                            }
                            jyzfmx.setGsdm(gsdm);
                            jyzfmx.setDdh(jyxxsq.getDdh());
                            jyzfmx.setLrry(lrry);
                            jyzfmx.setLrsj(new Date());
                            jyzfmx.setXgry(lrry);
                            jyzfmx.setXgsj(new Date());
                            jyzfmxList.add(jyzfmx);
                        }
                    }
                }*/
            }
        }

        rsMap.put("jyxxsqList", jyxxsqList);
        rsMap.put("jymxsqList", jymxsqList);
        rsMap.put("jyzfmxList", jyzfmxList);
        return rsMap;
    }

    /**
     * 绿地优鲜--获取验签接口
     * @param ExtractCode
     * @param gsdm
     * @return
     */
    public Map getldyxFirData(String ExtractCode,String gsdm){

            Map parmsMap=new HashMap();
            String strMessage = "";
            BufferedReader reader = null;
            StringBuffer buffer = new StringBuffer();
            Map parms=new HashMap();
            parms.put("gsdm",gsdm);
            //查询参数总表url
            Cszb zb1 = cszbService.getSpbmbbh(gsdm, null,null, "shhqtokenurl");
            Map resultMap = null;
            HttpPost httpPost = new HttpPost(zb1.getCsz());
            CloseableHttpResponse response = null;
            RequestConfig requestConfig = RequestConfig.custom().
                    setSocketTimeout(120*1000).setConnectionRequestTimeout(120*1000).setConnectTimeout(120*1000).build();
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build();
            //httpPost.setConfig(requestConfig);
            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("Authorization","Basic aWZpZWxkOmlmaWVsZDEyMzQ=");
            //传递数据验证码为json格式
             //Map nvps = new HashMap();
            try {
                //nvps.put("Authorization", "Basic aWZpZWxkOmlmaWVsZDEyMzQ=");
                StringEntity requestEntity = new StringEntity(JSON.toJSONString(""), "utf-8");
                httpPost.setEntity(requestEntity);
                response = httpClient.execute(httpPost, new BasicHttpContext());
                if (response.getStatusLine().getStatusCode() != 200) {
                    System.out.println("request url failed, http code=" + response.getStatusLine().getStatusCode()
                            + ", url=" + "");
                }
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    reader = new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8"));
                    while ((strMessage = reader.readLine()) != null) {
                        buffer.append(strMessage);
                    }
                }
                System.out.println("接收返回值:" + buffer.toString());
                //解析json获取token
                parmsMap = interpretFirstForJson(gsdm, buffer.toString());
        }catch (Exception e){
            logger.info("request url=" +zb1.getCsz()+ ", exception, msg=" + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return parmsMap;
    }

    /**
     * 绿地优鲜 --调用接口获取获取数据
     * @param ExtractCode
     * @param gsdm
     * @param token
     * @return
     */
    public Map getldyxSecData(String ExtractCode,String gsdm,String token){

        Map parmsMap=new HashMap();
        String strMessage = "";
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        Map parms=new HashMap();
        parms.put("gsdm",gsdm);

        //查询参数总表第二次url
        Cszb zb2 = cszbService.getSpbmbbh(gsdm, null,null, "sfhhurl");
        String uri = zb2.getCsz()+"?access_token="+token;
        //String uri = zb2.getCsz();
        //System.out.println("jkdz"+uri);
        System.out.println("two  url"+uri);
        Map resultMap = null;
        HttpPost httpPost = new HttpPost(uri);
        CloseableHttpResponse response = null;
        RequestConfig requestConfig = RequestConfig.custom().
                setSocketTimeout(120*1000).setConnectionRequestTimeout(120*1000).setConnectTimeout(120*1000).build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
        //httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-Type", "application/json");
        //传递数据验证码为json格式
        Map nvps = new HashMap();
        try {
            nvps.put("channel","rongjin");
            nvps.put("listno", ExtractCode);
            System.out.println("kaipiaohao"+nvps.get("listno").toString());
            StringEntity requestEntity = new StringEntity(JSON.toJSONString(nvps), "utf-8");
            httpPost.setEntity(requestEntity);
            response = httpClient.execute(httpPost, new BasicHttpContext());
            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println("request url failed, http code=" + response.getStatusLine().getStatusCode()
                        + ", url=" + "");
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                reader = new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8"));
                while ((strMessage = reader.readLine()) != null) {
                    buffer.append(strMessage);
                }
            }
            System.out.println("接收返回值:" + buffer.toString());
            //解析json获取购物小票数据 封装数据
            parmsMap = interpretSecForJson(gsdm, buffer.toString(),ExtractCode);
            List<Jyxxsq> jyxxsqList = (List) parmsMap.get("jyxxsqList");
            List<Jymxsq> jymxsqList = (List) parmsMap.get("jymxsqList");
            List<Jyzfmx> jyzfmxList = (List) parmsMap.get("jyzfmxList");
            if(null!=jyxxsqList &&!"".equals(jyxxsqList)&& null!=jymxsqList && !"".equals(jymxsqList)){
                String msg = checkOrderUtil.checkOrders(jyxxsqList,jymxsqList,jyzfmxList,gsdm,"");
                if(null!=msg&& !"".equals(msg)){
                    parmsMap.put("msg",msg);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return parmsMap;
    }

    public static void main(String[] args) {
        String json=" {\n" +
                "    \"code\": 0,\n" +
                "    \"data\": [{\n" +
                "      \"billno\": \"001170701000423096\",\n" +
                "      \"tradeDate\": \"2017-07-01 00:00:00\",\n" +
                "      \"tradeTime\": \"20:00:00\",\n" +
                "      \"voidbillno\":\"\",\n" +
                "      \"shopid\": \"001\" ,\n" +
                "      \"shopname\": \"XXXX店\",\n" +
                "      \"posid\": \"004\",\n" +
                "      \"listno\": \"23096\",\n" +
                "      \"cardno\": \"77777777\",\n" +
                "      \"payamount\": 20.00,\n" +
                "      \"addcodev\": \"V1.0.0\",\n" +
                "      \"salelist\":[{\n" +
                "          \"goodsid\": \"1\",\n" +
                "          \"goodsname\": \"可乐\",\n" +
                "          \"qty\": 1.000,\n" +
                "          \"amount\": 10.00,\n" +
                "          \"discamount\": 0.00\n" +
                "      },\n" +
                "      {\n" +
                "          \"goodsid\": \"2\",\n" +
                "          \"goodsname\": \"雪碧\",\n" +
                "          \"qty\": 1.000,\n" +
                "          \"amount\": 10.00,\n" +
                "          \"discamount\": 0.00\n" +
                "      }],\n" +
                "      \"paylist\":[{\n" +
                "          \"paytype\": \"现金\",\n" +
                "          \"payamount\": 20.00,\n" +
                "          \"cardno\":\"\"\n" +
                "      },\n" +
                "      {\n" +
                "          \"paytype\": \"会员卡\",\n" +
                "          \"payamount\": 0.00,\n" +
                "          \"cardno\":\"77777777\"\n" +
                "      }]\n" +
                "    }]\n" +
                "  }";
        String json1="{\n" +
                "    \"storeNo\": \"S017\",\n" +
                "    \"total\": \"1689.00000000\",\n" +
                "    \"orderNo\": \"S017-05144911\",\n" +
                "    \"totalDiscounts\": \"337.80000000\",\n" +
                "    \"details\": [\n" +
                "        {\n" +
                "            \"unitPrice\": 790,\n" +
                "            \"quantity\": 1,\n" +
                "            \"priceDiscounts\": 100,\n" +
                "            \"name\": \"FACNM1705\",\n" +
                "            \"afterDiscountPrice\": 690\n" +
                "        },\n" +
                "        {\n" +
                "            \"unitPrice\": 999,\n" +
                "            \"quantity\": 1,\n" +
                "            \"priceDiscounts\": 0,\n" +
                "            \"name\": \"HOYA金级镜片\",\n" +
                "            \"afterDiscountPrice\": 999\n" +
                "        }\n" +
                "    ],\n" +
                "    \"payment\": [\n" +
                "        {\n" +
                "            \"pay_amout\": \"248.0\",\n" +
                "            \"pay_code\": \"PT_06\",\n" +
                "            \"pay_name\": \"巍康卡\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"pay_amout\": \"1103.2\",\n" +
                "            \"pay_code\": \"PT_15\",\n" +
                "            \"pay_name\": \"国内银行卡\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"orderDate\": \"2017-10-17 22:10:09.0\",\n" +
                "    \"afterDiscountTotal\": \"1351.20000000\",\n" +
                "    \"status\": \"COMPLETED\"\n" +
                "}";
          GetDataService getDataService=new GetDataService();
        try {
            getDataService.interpretForGVC("gvc",json1,"S017-05144911");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 绿地优鲜--解析json数据,获取token
     * @param gsdm
     * @param data
     * @return
     * @throws Exception
     */
    public Map interpretFirstForJson(String gsdm,String data)throws Exception {

        Map resultMap=new HashMap();
        //传入数据
        JSONObject jsonObj = JSONObject.parseObject(data);
        //获取accessToken
        String accessToken ="";
        if (null!=jsonObj.getString("access_token")&&!jsonObj.getString("access_token").equals("")){
            accessToken =  jsonObj.get("access_token").toString();
        }
        //获取	token类型
        String tokenType ="";
        if (null!=jsonObj.getString("token_type")&&!jsonObj.getString("token_type").equals("")){
            tokenType =  jsonObj.get("token_type").toString();
        }
        //获取
        String refreshToken ="";
        if (null!=jsonObj.getString("refresh_token")&&!jsonObj.getString("refresh_token").equals("")){
            refreshToken =  jsonObj.get("refresh_token").toString();
        }
        //获取过期时间
        Integer expiresIn =null;
        if (null!=jsonObj.getInteger("expires_in")&&!jsonObj.getInteger("expires_in").equals("")){
            expiresIn =   jsonObj.getInteger("expires_in");
        }
        //获取权限
        String scope ="";
        if (null!=jsonObj.getString("scope")&&!jsonObj.getString("scope").equals("")){
            scope =  jsonObj.getString("scope").toString();
        }
        //获取组织
        String organization ="";
        if (null!=jsonObj.getString("organization")&&!jsonObj.getString("organization").equals("")){
            organization =  jsonObj.getString("organization").toString();
        }
        resultMap.put("accessToken",accessToken);
        resultMap.put("tokenType",tokenType);
        resultMap.put("expiresIn",expiresIn);
        resultMap.put("scope",scope);
        resultMap.put("organization",organization);
        return  resultMap;
    }

    /**
     * 绿地优鲜--解析json数据,封装
     * @param gsdm
     * @param data
     * @return
     * @throws Exception
     */
    public Map interpretSecForJson(String gsdm,String data,String tqm)throws Exception {

        Map rsMap = new HashMap();
        Map params1 = new HashMap();
        params1.put("gsdm", gsdm);//公司代码
        Yh yh = yhService.findOneByParams(params1);
        int lrry = yh.getId();
        List<Jyxxsq> jyxxsqList = new ArrayList();//交易信息申请
        List<Jymxsq> jymxsqList = new ArrayList();//交易明细申请
        List<Jyzfmx> jyzfmxList = new ArrayList<Jyzfmx>();//交易支付明细
        //传入数据
        JSONObject jsonObj = JSONObject.parseObject(data);
        String code = jsonObj.getString("code"); //code值为0 表示数据正常
        //根据data获取购物小票信息
        if(null!=code&&code.equals("0")) {
            JSONArray jsondata = jsonObj.getJSONArray("data");
            if (jsondata.size() > 0) {
                //System.out.println("进入data数据");
                for (int i = 0; i < jsondata.size(); i++) {
                    //基本信息获取
                    JSONObject jo = jsondata.getJSONObject(i);
                    //获取购物小票号(ld)
                    String ExtractCode = "";
                    if (null != jo.getString("billno") && !jo.getString("billno").equals("")) {
                        ExtractCode = jo.getString("billno").toString();
                    }
                    //获取发生日期
                    Date tradeDate = null;
                    if (null != jo.getDate("tradedate") && !jo.getDate("tradedate").equals("")) {
                        tradeDate = jo.getDate("tradedate");
                    }
                    //System.out.println("获取发生日期"+tradeDate);
                    //获取发生时间
                    String tradeTime = "";
                    if (null != jo.getString("tradetime") && !jo.getString("tradetime").equals("")) {
                        tradeTime = jo.getString("tradetime").toString();
                    }
                    //获取	退货时，原购物小票号
                    String voidbillno = "";
                    if (null != jo.getString("voidbillno") && !jo.getString("voidbillno").equals("")) {
                        voidbillno = jo.getString("voidbillno").toString();
                    }
                    //获取		门店编码
                    String shopid = "";
                    if (null != jo.getString("shopid") && !jo.getString("shopid").equals("")) {
                        shopid = jo.getString("shopid").toString();
                    }
                    //获取		门店名称
                    String shopname = "";
                    if (null != jo.getString("shopname") && !jo.getString("shopname").equals("")) {
                        shopname = jo.getString("shopname").toString();
                    }
                    //获取		收银机号
                    String posid = "";
                    if (null != jo.getString("posid") && !jo.getString("posid").equals("")) {
                        posid = jo.getString("posid").toString();
                    }
                    //获取		小票流水号
                    Integer listno = null;
                    if (null != jo.getInteger("listno") && !jo.getInteger("listno").equals("")) {
                        listno = jo.getInteger("listno");
                    }
                    //获取		会员卡号(ld)
                    String MemberID = "";
                    if (null != jo.getString("cardno") && !jo.getString("cardno").equals("")) {
                        MemberID = jo.getString("cardno").toString();
                    }
                    //获取		顾额应付总金额
                    Double payamount = null;
                    if (null != jo.getDouble("payamount") && !jo.getDouble("payamount").equals("")) {
                        payamount = jo.getDouble("payamount");
                    }
                    //获取		附码版本
                    String addcodev = "";
                    if (null != jo.getString("addcodev") && !jo.getString("addcodev").equals("")) {
                        addcodev = jo.getString("addcodev").toString();
                    }
                    //基本数据封装进交易信息申请
                    Jyxxsq jyxxsq = new Jyxxsq();
                    jyxxsq.setDdh(ExtractCode);//订单编号 对应小票流水号
                    jyxxsq.setTqm(tqm);// 提取码  对应购物小票流水号
                    jyxxsq.setJylsh("JY" + new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date()));//交易流水号
                    //String kpddm = "A" + tqm.substring(0, 3);
                    String kpddm=tqm.substring(0,3);
                    jyxxsq.setKpddm(kpddm);
                    logger.info("开票点代码为----" + jyxxsq.getKpddm());
                    //根据公司代码、开票点代码查询税控盘
                    Map skpmap = new HashMap();
                    skpmap.put("gsdm", gsdm);
                    skpmap.put("kpddm", kpddm);
                    Skp skpdata = skpService.findOneByParams(skpmap);
                    if(skpdata==null){
                        rsMap.put("jyxxsqList", jyxxsqList);
                        rsMap.put("jymxsqList", jymxsqList);
                        rsMap.put("jyzfmxList", jyzfmxList);
                        rsMap.put("msg","开票点信息未维护，请联系商家");
                        return rsMap;
                    }
                    //根据销方id  查询
                    Xf x = new Xf();
                    x.setId(skpdata.getXfid());
                    Xf xf = xfService.findOneByParams(x);
                    if(xf==null){
                        rsMap.put("msg","销方信息未维护，请联系商家");
                        rsMap.put("jyxxsqList", jyxxsqList);
                        rsMap.put("jymxsqList", jymxsqList);
                        rsMap.put("jyzfmxList", jyzfmxList);
                        return rsMap;
                    }
                    jyxxsq.setXfid(xf.getId());//销方id
                    jyxxsq.setFpzldm("12"); //发票种类
                    jyxxsq.setJshj(Double.valueOf(payamount));//价税合计
                    jyxxsq.setHsbz("1");//含税标志 1含税
                    jyxxsq.setBz("");//备注
                    jyxxsq.setZsfs("");//征税方式
                    jyxxsq.setKpr(xf.getKpr());
                    jyxxsq.setSkr(xf.getSkr());
                    jyxxsq.setFhr(xf.getFhr());
                    jyxxsq.setDdrq(new Date());
                    jyxxsq.setXfsh(xf.getXfsh());
                    jyxxsq.setXfmc(xf.getXfmc());
                    jyxxsq.setXfdz(xf.getXfdz());
                    jyxxsq.setXfdh(xf.getXfdh());
                    jyxxsq.setXfyh(xf.getXfyh());
                    jyxxsq.setXfyhzh(xf.getXfyhzh());
                    jyxxsq.setYkpjshj(Double.valueOf("0.00"));
                    jyxxsq.setYxbz("1");
                    jyxxsq.setLrsj(new Date());
                    jyxxsq.setLrry(lrry);
                    jyxxsq.setXgry(lrry);
                    jyxxsq.setFpczlxdm("11");
                    jyxxsq.setXgsj(new Date());
                    jyxxsq.setGsdm(gsdm);
                    jyxxsq.setSjly("1");
                    jyxxsq.setClztdm("00");
                    //全局折扣
                    jyxxsq.setQjzk(0d);
                    jyxxsqList.add(jyxxsq);
                    JSONArray salelist = jo.getJSONArray("salelist");
                    if (null != salelist && salelist.size() > 0) {
                        //商品明细获取
                        int spmxxh = 0;
                        for (int s = 0; s < salelist.size(); s++) {

                            Jymxsq jymxsq = new Jymxsq();
                            JSONObject saleData = salelist.getJSONObject(s);
                            //获取     商品税务附码
                            String goodsid = "";
                            if (null != saleData.getString("goodsid") && !saleData.getString("goodsid").equals("")) {
                                goodsid = saleData.getString("goodsid").toString();
                                //String spdm = goodsid.replaceAll("\r\n");
                                jymxsq.setSpdm(goodsid);
                            }
                            logger.info("获取salelist成功,数据商品税务附码gooid" + goodsid);
                            //获取     	商品名称
                            String goodsname = "";
                            if (null != saleData.getString("goodsname") && !saleData.getString("goodsname").equals("")) {
                                String goodsna = saleData.getString("goodsname").toString();
                                goodsname = goodsna.replaceAll("\n", "");
                                jymxsq.setSpmc(goodsname.trim());
                            }
                            //获取     	数量，负数为退货数量
                            Double qty = null;
                            if (null != saleData.getDouble("qty") && !saleData.getDouble("qty").equals("")) {
                                qty = saleData.getDouble("qty");
                                jymxsq.setSps(Double.valueOf(qty));//商品数量
                            }

                            //获取      顾客应付金额，负数为退货金额
                            BigDecimal amount = null;
                            if (null != saleData.getBigDecimal("amount") && !saleData.getBigDecimal("amount").equals("")) {
                                amount = saleData.getBigDecimal("amount");
                            }

                            //获取      销售税率
                            BigDecimal taxrate = null;
                            if (null != saleData.getBigDecimal("taxrate") && !saleData.getBigDecimal("taxrate").equals("")) {
                                BigDecimal taxrates = saleData.getBigDecimal("taxrate");
                                if (taxrates.compareTo(new BigDecimal(1)) > 0) {
                                    taxrate = taxrates.multiply(new BigDecimal(0.01));
                                } else {
                                    taxrate = taxrates;
                                }
                                jymxsq.setSpsl(taxrate.doubleValue());// 商品税率
                            }

                            //获取     	实际单价,顾客应付金额 / 数量
                            Double price = null;
                            if (null != saleData.getDouble("price") && !saleData.getDouble("price").equals("")) {
                                price = saleData.getDouble("price");
                                jymxsq.setSpdj(price);//商品单价
                            }
                            //获取      	促销金额
                            Double discamount = null;
                            if (null != saleData.getDouble("discamount") && !saleData.getDouble("discamount").equals("")) {
                                discamount = saleData.getDouble("discamount");
                            }
                            //商品明细 封装进交易明细申请
                            spmxxh++;
                            jymxsq.setSpmxxh(spmxxh);//商品明细序号
                            jymxsq.setDdh(jyxxsq.getDdh());//订单号
                            jymxsq.setHsbz(jyxxsq.getHsbz());
                            jymxsq.setFphxz("0");//发票行性质 0：正常行
                            //jymxsq.setSpggxh("");//商品规格型号
                            //jymxsq.setSpdw("");//商品单位
                            //计算不含税金额
                            BigDecimal big = new BigDecimal("1");
                            //BigDecimal bhsamount =   amount.divide(big.add(taxrate));
                            BigDecimal bhsamount = InvoiceSplitUtils.div(amount, big.add(taxrate), 6);
                            jymxsq.setSpje(amount.doubleValue());//商品金额
                            //计算商品税额
                            BigDecimal spseAmount = bhsamount.multiply(taxrate);
                            //jymxsq.setSpse(spseAmount.doubleValue());
                            jymxsq.setJshj(amount.doubleValue());//税价合计为绿地传进的金额
                            //可开具金额  = amount
                            //jymxsq.setKkjje(amount.doubleValue());
                            //已开具金额  = 0
                            jymxsq.setYkjje(0d);
                            Map spbmMap=new HashMap();
                            spbmMap.put("spbm",goodsid);
                            spbmMap.put("gsdm",gsdm);
                            vSpbm spbm=vSpbmService.findOneByParams(spbmMap);
                            if(spbm!=null){
                                if(Double.valueOf(taxrate.toString())>0){
                                    jymxsq.setYhzcbs("0");
                                    jymxsq.setLslbz("");
                                    jymxsq.setYhzcmc("");
                                }else{
                                    jymxsq.setYhzcbs(spbm.getYhzcbs().toString());
                                    jymxsq.setLslbz(spbm.getLslbz());
                                    jymxsq.setYhzcmc(spbm.getYhzcmc());
                                }
                            }
                           /* Map spbmMap = new HashMap();
                            spbmMap.put("spbm", goodsid);
                            spbmMap.put("gsdm", gsdm);
                            Spvo spvo = spvoService.findOneSpvo(spbmMap);
                            if (spvo != null) {
                                jymxsq.setYhzcbs(spvo.getYhzcbs());
                                jymxsq.setLslbz(spvo.getLslbz());
                                jymxsq.setYhzcmc(spvo.getYhzcmc());
                            }*/
                            jymxsq.setGsdm(gsdm);
                            jymxsq.setLrry(lrry);
                            jymxsq.setLrsj(new Date());
                            jymxsq.setXgry(lrry);
                            jymxsq.setXgsj(new Date());
                            jymxsq.setYxbz("1");
                            jymxsqList.add(jymxsq);
                        }

                    }

                    Double bkkjje = 0.00;
                    JSONArray paylist = jo.getJSONArray("paylist");
                    if (null != paylist && paylist.size() > 0) {
                        // 获取支付明细
                        for (int p = 0; p < paylist.size(); p++) {
                            Jyzfmx jyzfmx = new Jyzfmx();
                            JSONObject payData = paylist.getJSONObject(p);
                            //获取     支付方式代码
                            String paytype = "";
                            if (null != payData.getString("paytype") && !payData.getString("paytype").equals("")) {
                                paytype = payData.getString("paytype");
                                jyzfmx.setZffsDm(paytype);
                            }
                            //获取     顾客支付方式支付实际金额，负数为退款
                            Double zfje = null;
                            if (null != payData.getDouble("payamount") && !payData.getDouble("payamount").equals("")) {
                                zfje = payData.getDouble("payamount");
                                jyzfmx.setZfje(Double.valueOf(zfje));//支付金额
                            }
                            //获取     支付方式是储值卡或会员卡时，记录卡号
                            String paycardno = "";
                            if (null != payData.getString("cardno") && !payData.getString("cardno").equals("")) {
                                paycardno = payData.getString("cardno").toString();
                            }
                            //支付明细封装交易支付明细
                            jyzfmx.setGsdm(gsdm);
                            jyzfmx.setDdh(jyxxsq.getDdh());
                            jyzfmx.setLrry(lrry);
                            jyzfmx.setLrsj(new Date());
                            jyzfmx.setXgry(lrry);
                            jyzfmx.setXgsj(new Date());
                            jyzfmxList.add(jyzfmx);
                        }
                    }
                }
            }else {
                String msg ="获取数据为空，请稍后再试！";
                rsMap.put("msg",msg);
            }
            rsMap.put("jyxxsqList", jyxxsqList);
            rsMap.put("jymxsqList", jymxsqList);
            rsMap.put("jyzfmxList", jyzfmxList);
            return rsMap;
        }else {
            String msg = jsonObj.getString("msg");
            if(null!=msg && !"".equals(msg)){
                rsMap.put("msg",msg);
                rsMap.put("jyxxsqList", jyxxsqList);
                rsMap.put("jymxsqList", jymxsqList);
                rsMap.put("jyzfmxList", jyzfmxList);
            }else {
                 msg = "获取数据失败，请重试！";
                rsMap.put("msg", msg);
                rsMap.put("jyxxsqList", jyxxsqList);
                rsMap.put("jymxsqList", jymxsqList);
                rsMap.put("jyzfmxList", jyzfmxList);
            }
        }
        return rsMap;
    }

    /**
     * 光唯尚--调用接口获取数据
     * @param orderNo
     * @param gsdm
     * @param url
     * @return
     */
    public Map getDataForGvc(String orderNo,String gsdm,String url){
        Map parmsMap=new HashMap();
        try {
            Map map = new HashMap();
            map.put("orderNo",orderNo);
            //map.put("gsdm",gsdm);
            logger.info("光唯尚---传入参数"+JSON.toJSONString(map));
            String response = HttpClientUtil.doGet(url, map);
            logger.info("光唯尚---接收返回值:" + response);
            //解析返回数据
            parmsMap=interpretForGVC(gsdm,response,orderNo);
            String msg = (String) parmsMap.get("msg");
                List<Jyxxsq> jyxxsqList = (List) parmsMap.get("jyxxsqList");
                List<Jymxsq> jymxsqList = (List) parmsMap.get("jymxsqList");
                List<Jyzfmx> jyzfmxList = (List) parmsMap.get("jyzfmxList");
                /*if(null!=jyxxsqList &&!"".equals(jyxxsqList)&& null!=jymxsqList && !"".equals(jymxsqList)){
                    String msgss = checkOrderUtil.checkOrders(jyxxsqList,jymxsqList,jyzfmxList,gsdm,"");
                    if(null!=msgss&& !"".equals(msgss)){
                        parmsMap.put("msg",msgss);
                    }
                }*/
            logger.info("-----封装好的数据"+JSON.toJSON(parmsMap));
        }catch (Exception e){
            logger.info("msg=" + e.getMessage());
            e.printStackTrace();
        }
        return parmsMap;
    }

    /**
     * 光唯尚--解析数据并封装
     * @param gsdm
     * @param data
     * @param orderNo
     * @return
     * @throws Exception
     */
    public Map interpretForGVC(String gsdm,String data,String orderNo)throws Exception {

        Map rsMap = new HashMap();
        Map params1 = new HashMap();
        params1.put("gsdm", gsdm);//公司代码
        Yh yh = yhService.findOneByParams(params1);
        int lrry = yh.getId();
        List<Jyxxsq> jyxxsqList = new ArrayList();//交易信息申请
        List<Jymxsq> jymxsqList = new ArrayList();//交易明细申请
        List<Jyzfmx> jyzfmxList = new ArrayList<Jyzfmx>();//交易支付明细
        String nowdate ="";
        String storeno ="";
        Double zkjine = 0d;
        String kpqssj="";
        String kpjssj="";
        //传入数据
        JSONObject jsonObj = JSONObject.parseObject(data);
        String code = jsonObj.getString("code"); //code值为0 表示数据正常
        if(null!=code&&code.equals("0000")) {
            JSONObject jsondata = jsonObj.getJSONObject("data");
            if (jsondata!=null) {
                //for (int i = 0; i < jsondata.size(); i++) {
                    JSONObject jo = jsondata;
                    String storeNo = "";
                    if (null != jo.getString("storeNo") && !jo.getString("storeNo").equals("")) {
                        storeNo = jo.getString("storeNo").toString();
                    }
                    Date orderDate = null;
                    if (null != jo.getDate("orderDate") && !jo.getDate("orderDate").equals("")) {
                        orderDate = jo.getDate("orderDate");
                    }
                    Double total = null;
                    if (null != jo.getDouble("total") && !jo.getDouble("total").equals("")) {
                        total = jo.getDouble("total");
                    }
                    Double totalDiscounts = null;
                    if (null != jo.getDouble("totalDiscounts") && !jo.getDouble("totalDiscounts").equals("")) {
                        totalDiscounts = jo.getDouble("totalDiscounts");
                    }
                    Double afterDiscountTotal = null;
                    if (null != jo.getString("afterDiscountTotal") && !jo.getDouble("afterDiscountTotal").equals("")) {
                        afterDiscountTotal = jo.getDouble("afterDiscountTotal");
                    }
                    String status = "";
                    if (null != jo.getString("status") && !jo.getString("status").equals("")) {
                        status = jo.getString("status").toString();
                    }
                    String remark = null;
                    if (null != jo.getString("remark") && !jo.getString("remark").equals("")) {
                        remark = jo.getString("remark").toString();
                    }
                    String nowDate = null;
                    if (null != jo.getString("nowDate") && !jo.getString("nowDate").equals("")) {
                        nowDate = jo.getString("nowDate").toString();
                    }
                    nowdate= nowDate;
                    storeno =storeNo;
                    zkjine=afterDiscountTotal;
                    //基本数据封装进交易信息申请
                    Jyxxsq jyxxsq = new Jyxxsq();
                    jyxxsq.setDdh(orderNo);//订单编号 对应小票流水号
                    jyxxsq.setTqm(orderNo);// 提取码  对应购物小票流水号
                    jyxxsq.setJylsh("JY" + new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date()));//交易流水号
                    jyxxsq.setKpddm(storeNo);
//                    jyxxsq.setKpddm("gvc_01");
                //根据公司代码、开票点代码查询税控盘
                Map skpmap = new HashMap();
                    skpmap.put("gsdm", gsdm);
                    skpmap.put("kpddm", storeNo);
//                    skpmap.put("kpddm", "gvc_01");
                    Skp skpdata = skpService.findOneByParams(skpmap);
                    if(skpdata==null){
                        rsMap.put("jyxxsqList", jyxxsqList);
                        rsMap.put("jymxsqList", jymxsqList);
                        rsMap.put("jyzfmxList", jyzfmxList);
                        rsMap.put("nowdate",nowdate);
                        rsMap.put("storeno",storeno);
                        rsMap.put("zkjine",zkjine);
                        rsMap.put("msg","开票点信息未维护，请联系商家！");
                        return rsMap;
                    }
                    if(skpdata.getKpqssj()!=null){
                        kpqssj =skpdata.getKpqssj().toString();
                    }
                    if(skpdata.getKpjssj()!=null){
                        kpjssj =skpdata.getKpjssj().toString();
                    }
                    //根据销方id  查询
                    Xf x = new Xf();
                    x.setId(skpdata.getXfid());
                    Xf xf = xfService.findOneByParams(x);
                    if(xf==null){
                        rsMap.put("msg","销方信息未维护，请联系商家！");
                        rsMap.put("jyxxsqList", jyxxsqList);
                        rsMap.put("jymxsqList", jymxsqList);
                        rsMap.put("jyzfmxList", jyzfmxList);
                        rsMap.put("nowdate",nowdate);
                        rsMap.put("storeno",storeno);
                        rsMap.put("zkjine",zkjine);
                        rsMap.put("kpqssj",kpqssj);
                        rsMap.put("kpjssj",kpjssj);
                        return rsMap;
                    }
                    jyxxsq.setXfid(xf.getId());//销方id
                    jyxxsq.setFpzldm("12"); //发票种类
                    jyxxsq.setJshj(Double.valueOf(total));//价税合计
                    //全局折扣
                    jyxxsq.setQjzk(Double.valueOf(totalDiscounts));
                    jyxxsq.setHsbz("1");//含税标志 1含税
                    jyxxsq.setZsfs("");//征税方式
                    jyxxsq.setKpr(xf.getKpr());
                    jyxxsq.setSkr(xf.getSkr());
                    jyxxsq.setFhr(xf.getFhr());
                    jyxxsq.setDdrq(orderDate);
                    jyxxsq.setXfsh(xf.getXfsh());
                    jyxxsq.setXfmc(xf.getXfmc());
                    jyxxsq.setXfdz(xf.getXfdz());
                    jyxxsq.setXfdh(xf.getXfdh());
                    jyxxsq.setXfyh(xf.getXfyh());
                    jyxxsq.setXfyhzh(xf.getXfyhzh());
                    jyxxsq.setYkpjshj(Double.valueOf("0.00"));
                    jyxxsq.setYxbz("1");
                    jyxxsq.setLrsj(new Date());
                    jyxxsq.setLrry(lrry);
                    jyxxsq.setXgry(lrry);
                    jyxxsq.setFpczlxdm("11");
                    jyxxsq.setXgsj(new Date());
                    jyxxsq.setGsdm(gsdm);
                    jyxxsq.setSjly("1");
                    jyxxsq.setClztdm("00");
                    jyxxsq.setBz(remark);
                    jyxxsqList.add(jyxxsq);
                    JSONArray salelist = jo.getJSONArray("details");
                    if (null != salelist && salelist.size() > 0) {
                        //商品明细获取
                        int spmxxh = 1;
                        for (int s = 0; s < salelist.size(); s++) {

                            JSONObject saleData = salelist.getJSONObject(s);
                            String name = "";//名称
                            if (null != saleData.getString("name") && !saleData.getString("name").equals("")) {
                                String goodsna = saleData.getString("name").toString();
                                name = goodsna.replaceAll("\n", "");
                            }
                            BigDecimal quantity = null;//数量
                            if (null != saleData.getBigDecimal("quantity") && saleData.getBigDecimal("quantity").compareTo(BigDecimal.ZERO)!=0) {
                                quantity = saleData.getBigDecimal("quantity");
                            }
                            BigDecimal unitPrice = null;//单价
                            if (null != saleData.getBigDecimal("unitPrice") && saleData.getBigDecimal("unitPrice").compareTo(BigDecimal.ZERO)!=0) {
                                unitPrice = saleData.getBigDecimal("unitPrice");
                            }
                            BigDecimal priceDiscounts = null;
                            if (null != saleData.getBigDecimal("priceDiscounts") && !saleData.getBigDecimal("priceDiscounts").equals("")) {
                                priceDiscounts = saleData.getBigDecimal("priceDiscounts");
                            }
                            BigDecimal afterDiscountPrice = null;
                            if (null != saleData.getBigDecimal("afterDiscountPrice") && !saleData.getBigDecimal("afterDiscountPrice").equals("")) {
                                afterDiscountPrice = saleData.getBigDecimal("afterDiscountPrice");
                            }

                            //商品明细 封装进交易明细申请
                            Cszb cszb = cszbService.getSpbmbbh("gvc",xf.getId(), skpdata.getId(), "dyspbmb");
                            int b = priceDiscounts.compareTo(new BigDecimal("0"));
                            if(b==0){
                                Jymxsq jymxsq = new Jymxsq();
                                jymxsq.setSpmc(name.trim());
                                jymxsq.setFphxz("0");//发票行性质 0：正常行
                                jymxsq.setSpmc(name);
                                jymxsq.setSpdj(new Double(unitPrice.toString()));
                                jymxsq.setSps(new Double(quantity.toString()));
                                BigDecimal zch = unitPrice.multiply(quantity);
                                Double zchSpje = new Double(zch.toString());
                                jymxsq.setSpje(zchSpje);
                                jymxsq.setJshj(zchSpje);
                                jymxsq.setSpmxxh(spmxxh);//商品明细序号
                                spmxxh++;
                                jymxsq.setDdh(jyxxsq.getDdh());//订单号
                                jymxsq.setHsbz(jyxxsq.getHsbz());
                                jymxsq.setYkjje(0d);
                                Map mapoo = new HashMap();
                                mapoo.put("gsdm", "gvc");
                                if (cszb.getCsz() != null) {
                                    mapoo.put("spdm", cszb.getCsz());
                                }
                                Spvo spvo = spvoService.findOneSpvo(mapoo);
                                if (spvo == null) {
                                    rsMap.put("msg","商品信息未维护，请联系商家！");
                                    rsMap.put("jyxxsqList", jyxxsqList);
                                    rsMap.put("jymxsqList", jymxsqList);
                                    rsMap.put("jyzfmxList", jyzfmxList);
                                    rsMap.put("nowdate",nowdate);
                                    rsMap.put("storeno",storeno);
                                    rsMap.put("zkjine",zkjine);
                                    rsMap.put("kpqssj",kpqssj);
                                    rsMap.put("kpjssj",kpjssj);
                                    return rsMap;
                                }
                                jymxsq.setSpsl(spvo.getSl());
                                jymxsq.setSpdm(spvo.getSpbm());
                                jymxsq.setSpse(0d);
                                jymxsq.setYhzcbs(spvo.getYhzcbs());
                                jymxsq.setLslbz(spvo.getLslbz());
                                jymxsq.setYhzcmc(spvo.getYhzcmc());
                                jymxsq.setGsdm(gsdm);
                                jymxsq.setLrry(lrry);
                                jymxsq.setLrsj(new Date());
                                jymxsq.setXgry(lrry);
                                jymxsq.setXgsj(new Date());
                                jymxsq.setYxbz("1");
                                jymxsqList.add(jymxsq);
                            }else {
                                Jymxsq jymxsq2 = new Jymxsq(); //被折扣行
                                jymxsq2.setSpmc(name.trim());
                                jymxsq2.setFphxz("2");//发票行性质 2：被折扣行
                                jymxsq2.setHsbz(jyxxsq.getHsbz());
                                jymxsq2.setSpdj(new Double(unitPrice.toString()));
                                jymxsq2.setSps(new Double(quantity.toString()));
                                //计算被折扣行  单价乘以数量
                                BigDecimal bzkh = unitPrice.multiply(quantity);
                                Double spje2 = new Double(bzkh.toString());
                                jymxsq2.setSpje(spje2);//商品金额
                                //计算商品税额
                                jymxsq2.setJshj(spje2);//税价合计
                                jymxsq2.setSpmxxh(spmxxh);//商品明细序号
                                spmxxh++;
                                jymxsq2.setDdh(jyxxsq.getDdh());//订单号
                                //已开具金额  = 0
                                jymxsq2.setYkjje(0d);
                                Map mapoo = new HashMap();
                                mapoo.put("gsdm", "gvc");
                                if (cszb.getCsz() != null) {
                                    mapoo.put("spdm", cszb.getCsz());
                                }
                                Spvo spvo = spvoService.findOneSpvo(mapoo);
                                if (spvo == null) {
                                    rsMap.put("msg","商品信息未维护，请联系商家！");
                                    rsMap.put("jyxxsqList", jyxxsqList);
                                    rsMap.put("jymxsqList", jymxsqList);
                                    rsMap.put("jyzfmxList", jyzfmxList);
                                    rsMap.put("nowdate",nowdate);
                                    rsMap.put("storeno",storeno);
                                    rsMap.put("zkjine",zkjine);
                                    rsMap.put("kpqssj",kpqssj);
                                    rsMap.put("kpjssj",kpjssj);
                                    return rsMap;
                                }
                                jymxsq2.setSpsl(spvo.getSl());
                                jymxsq2.setSpdm(spvo.getSpbm());
                                jymxsq2.setSpse(0d);
                                jymxsq2.setYhzcbs(spvo.getYhzcbs());
                                jymxsq2.setLslbz(spvo.getLslbz());
                                jymxsq2.setYhzcmc(spvo.getYhzcmc());
                                jymxsq2.setGsdm(gsdm);
                                jymxsq2.setLrry(lrry);
                                jymxsq2.setLrsj(new Date());
                                jymxsq2.setXgry(lrry);
                                jymxsq2.setXgsj(new Date());
                                jymxsq2.setYxbz("1");
                                jymxsqList.add(jymxsq2);

                                //折扣行
                                Jymxsq jymxsq1 = new Jymxsq();
                                jymxsq1.setSpmc(name.trim());
                                jymxsq1.setFphxz("1");
                                //jymxsq1.setSpdj(new Double(unitPrice.toString()));
                                //jymxsq1.setSps(new Double(quantity.toString()));
                                BigDecimal zkh = priceDiscounts.multiply(new BigDecimal(-1));
                                Double spje1 = new Double(zkh.toString());
                                jymxsq1.setSpje(spje1);//商品金额
                                //计算商品税额
                                jymxsq1.setJshj(spje1);//税价合计
                                jymxsq1.setSpmxxh(spmxxh);//商品明细序号
                                spmxxh++;
                                jymxsq1.setDdh(jyxxsq.getDdh());//订单号
                                jymxsq1.setHsbz(jyxxsq.getHsbz());
                                //已开具金额  = 0
                                jymxsq1.setYkjje(0d);
                                if (spvo == null) {
                                    rsMap.put("msg","商品信息未维护，请联系商家！");
                                    rsMap.put("jyxxsqList", jyxxsqList);
                                    rsMap.put("jymxsqList", jymxsqList);
                                    rsMap.put("jyzfmxList", jyzfmxList);
                                    rsMap.put("nowdate",nowdate);
                                    rsMap.put("storeno",storeno);
                                    rsMap.put("zkjine",zkjine);
                                    rsMap.put("kpqssj",kpqssj);
                                    rsMap.put("kpjssj",kpjssj);
                                    return rsMap;
                                }
                                jymxsq1.setSpsl(spvo.getSl());
                                jymxsq1.setSpdm(spvo.getSpbm());
                                jymxsq1.setSpse(0d);
                                jymxsq1.setYhzcbs(spvo.getYhzcbs());
                                jymxsq1.setLslbz(spvo.getLslbz());
                                jymxsq1.setYhzcmc(spvo.getYhzcmc());
                                jymxsq1.setGsdm(gsdm);
                                jymxsq1.setLrry(lrry);
                                jymxsq1.setLrsj(new Date());
                                jymxsq1.setXgry(lrry);
                                jymxsq1.setXgsj(new Date());
                                jymxsq1.setYxbz("1");
                                jymxsqList.add(jymxsq1);
                            }
                        }
                    }
                    JSONArray paylist = jo.getJSONArray("payment");
                    if (null != paylist && paylist.size() > 0) {
                        // 获取支付明细
                        for (int p = 0; p < paylist.size(); p++) {
                            Jyzfmx jyzfmx = new Jyzfmx();
                            JSONObject payData = paylist.getJSONObject(p);
                            String pay_code = "";
                            if (null != payData.getString("pay_code") && !payData.getString("pay_code").equals("")) {
                                pay_code = payData.getString("pay_code");
                                jyzfmx.setZffsDm(pay_code);
                            }
                            Double pay_amount = null;
                            if (null != payData.getDouble("pay_amount") && !payData.getDouble("pay_amount").equals("")) {
                                pay_amount = payData.getDouble("pay_amount");
                                jyzfmx.setZfje(pay_amount);//支付金额
                            }
                            String pay_name = "";
                            if (null != payData.getString("pay_name") && !payData.getString("pay_name").equals("")) {
                                pay_name = payData.getString("pay_name").toString();
                            }
                            //支付明细封装交易支付明细
                            jyzfmx.setGsdm(gsdm);
                            jyzfmx.setDdh(jyxxsq.getDdh());
                            jyzfmx.setLrry(lrry);
                            jyzfmx.setLrsj(new Date());
                            jyzfmx.setXgry(lrry);
                            jyzfmx.setXgsj(new Date());
                            jyzfmxList.add(jyzfmx);
                        }
                    }
            }else {
                String msg ="获取数据为空，请稍后再试！";
                rsMap.put("msg",msg);
            }
            rsMap.put("jyxxsqList", jyxxsqList);
            rsMap.put("jymxsqList", jymxsqList);
            rsMap.put("jyzfmxList", jyzfmxList);
            rsMap.put("nowdate",nowdate);
            rsMap.put("storeno",storeno);
            rsMap.put("zkjine",zkjine);
            rsMap.put("kpqssj",kpqssj);
            rsMap.put("kpjssj",kpjssj);
            return rsMap;
        }else {
            String msg = jsonObj.getString("msg");
            if(null!=msg && !"".equals(msg)){
                rsMap.put("msg",msg);
                rsMap.put("jyxxsqList", jyxxsqList);
                rsMap.put("jymxsqList", jymxsqList);
                rsMap.put("jyzfmxList", jyzfmxList);
                rsMap.put("nowdate",nowdate);
                rsMap.put("storeno",storeno);
                rsMap.put("zkjine",zkjine);
                rsMap.put("kpqssj",kpqssj);
                rsMap.put("kpjssj",kpjssj);
            }else {
                msg = "获取数据失败，请重试！";
                rsMap.put("msg", msg);
                rsMap.put("jyxxsqList", jyxxsqList);
                rsMap.put("jymxsqList", jymxsqList);
                rsMap.put("jyzfmxList", jyzfmxList);
                rsMap.put("nowdate",nowdate);
                rsMap.put("storeno",storeno);
                rsMap.put("zkjine",zkjine);
                rsMap.put("kpqssj",kpqssj);
                rsMap.put("kpjssj",kpjssj);
            }
        }
        return rsMap;
    }
}
