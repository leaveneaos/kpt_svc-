package com.rjxx.taxeasy.dao.dto.crestvinvoice;

import com.alibaba.fastjson.JSON;
import com.rjxx.taxeasy.dao.bo.Jyls;
import com.rjxx.taxeasy.dao.bo.Kpls;
import com.rjxx.taxeasy.dao.bo.Skp;
import com.rjxx.taxeasy.dao.vo.Kpspmxvo;
import com.rjxx.utils.AESUtils;
import com.rjxx.utils.StringUtils;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @ClassName PacketBody
 * @Description 封装凯盈开票指令报文
 * @Author 许黎明
 * @Date 2018-04-24 19:50
 * @Version 1.0
 **/
public class PacketBody {

    /**
     * 静态内部类懒汉式单列模式
     */
    private static class SingletonPacketBody {
        private static final PacketBody INSTANCE = new PacketBody();
    }
    public static final PacketBody getInstance() {
        return PacketBody.SingletonPacketBody.INSTANCE;
    }

    public String Packet_Invoice_Json(Kpls kpls, Jyls jyls, List<Kpspmxvo>kpspmxList, Skp skp,String spbmbbh)throws Exception{
        Packet.NewInvoice newInvoice=Packet_NewInvoice(kpls,jyls,kpspmxList,spbmbbh);
        String Invoice_Json= JSON.toJSONString(newInvoice);
        return AESUtils.aesEncrypt(Invoice_Json,skp.getDevicekey());
    }

    public  Packet.NewInvoice Packet_NewInvoice(Kpls kpls, Jyls jyls,List<Kpspmxvo>kpspmxList,String spbmbbh){

        Packet.NewInvoice newInvoice=new Packet().new NewInvoice();
        newInvoice.MerchantAddress=kpls.getXfdz();
        newInvoice.MerchantPhone=kpls.getXfdh();
        newInvoice.MerchantBank=kpls.getXfyh();
        newInvoice.MerchantBankAccount=kpls.getXfyhzh();
        if(kpls.getGfsh()==null){
            newInvoice.PurchaserTaxID="";
        }else{
            newInvoice.PurchaserTaxID=kpls.getGfsh();
        }
        newInvoice.PurchaserName=kpls.getGfmc();
        newInvoice.PurchaserPhone=kpls.getGfdh();
        newInvoice.PurchaserMobile=jyls.getGfsjh();
        newInvoice.PurchaserEmail=kpls.getGfemail();
        newInvoice.PurchaserAddr=kpls.getGfdh();
        newInvoice.PurchaserBank=kpls.getGfyh();
        newInvoice.PurchaserAccount=kpls.getGfyhzh();
        newInvoice.Drawee=kpls.getKpr();
        String fpzldm="";
        if("01".equals(kpls.getFpczlxdm())){
            fpzldm="1";
        }else if("02".equals(kpls.getFpzldm())){
            fpzldm="2";
        }else if("12".equals(kpls.getFpzldm())){
            fpzldm="4";
        }else if("03".equals(kpls.getFpzldm())){
            fpzldm="3";
        }
        newInvoice.InvoiceType=fpzldm;
        newInvoice.CatalogVer=spbmbbh;
        String kplx="";
        if("11".equals(kpls.getFpczlxdm())){
            kplx="0";
        }else if("12".equals(kpls.getFpczlxdm())){
            kplx="1";
        }
        newInvoice.IssueType=kplx;
        if(kpls.getSfdyqd()==null){
            newInvoice.IsPrintList="";
        }else{
            newInvoice.IsPrintList=kpls.getSfdyqd();
        }
        newInvoice.NegSpecialInvoicePermitNum=kpls.getHztzdh();
        newInvoice.OriginInvoiceCode=kpls.getHzyfpdm();
        newInvoice.OriginInvoiceNum=kpls.getHzyfphm();
        newInvoice.Payee=kpls.getSkr();
        if(kpls.getBz()==null){
            newInvoice.Remarks="";
        }else{
            newInvoice.Remarks=kpls.getBz();
        }
        newInvoice.Reviewer=kpls.getFhr();
        newInvoice.TotalAmount=kpls.getJshj().toString();
        newInvoice.TotalAmountTaxExcluded=kpls.getHjje().toString();
        newInvoice.TotalTax=kpls.getHjse().toString();
        newInvoice.ItemList=Packet_itemList(kpspmxList);
        return newInvoice;
    }

    public  List<Packet.Item> Packet_itemList(List<Kpspmxvo>kpspmxList){
        List <Packet.Item> itemList=new ArrayList<>();
        for(Kpspmxvo kpspmx:kpspmxList){
            Packet.Item item=new Packet().new Item();
            item.EntryType=kpspmx.getFphxz();
            item.ItemName=kpspmx.getSpmc();
            /**
             * 含税单价 3位小数 开具卷票时提供
             */
            item.UnitPrice=null;
            if(null!=kpspmx.getSpdj()){
                item.UnitPriceTaxExcluded=new BigDecimal(kpspmx.getSpdj()).setScale(8,BigDecimal.ROUND_HALF_UP).toString();
            }else{
                item.UnitPriceTaxExcluded="";
            }
            item.Amount=new BigDecimal(kpspmx.getSpje() + kpspmx.getSpse()).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
            item.AmountTaxExcluded=new BigDecimal(kpspmx.getSpje()).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
            if(null!=kpspmx.getSps()){
                item.Quantity =new BigDecimal(kpspmx.getSps()).setScale(3,BigDecimal.ROUND_HALF_UP).toString();
            }else{
                item.Quantity="";
            }
            item.TaxRate=kpspmx.getSpsl().toString();
            item.Tax=kpspmx.getSpse().toString();
            item.Specifications=kpspmx.getSpggxh();
            item.Unit=kpspmx.getSpdw();
            item.TaxExcludingAmount=kpspmx.getKce().toString();
            /**
             * 商品分类名称
             */
            item.CatalogName=kpspmx.getSpflmc();
            /**
             * 商品分类简称
             */
            item.CatalogShortName=kpspmx.getSpfljc();
            item.CatalogCode=kpspmx.getSpdm();
            item.HasPreferentialPolicy=kpspmx.getYhzcbs();
            if(kpspmx.getYhzcmc()==null){
                item.PreferentialPolicy="";
            }else{
                item.PreferentialPolicy=kpspmx.getYhzcmc();
            }

            if(kpspmx.getLslbz()==null){
                item.TaxFreeType="";
            }else{
                item.TaxFreeType=kpspmx.getLslbz();
            }
            itemList.add(item);
        }
            return itemList;
    }

    public  String Packet_DeviceCmd (String kplsh,String OpType,String Data,Skp skp,String Appkey)throws Exception{
        Packet.Terminal terminal=new Packet().new Terminal();
        terminal.ProtocolVer=1;
        terminal.SeqNumber=kplsh;
        terminal.ZipType=0;
        terminal.EncryptType=1;
        terminal.DeviceSN=skp.getDevicesn();
        terminal.UserID="rjxx";
        terminal.OpType=OpType;
        terminal.Data=Data;
        System.out.println("----终端指令------"+JSON.toJSONString(terminal));
        return AESUtils.aesEncrypt(JSON.toJSONString(terminal),Appkey) ;
    }

    public String  Packet_Ruquest(String AppID,String ReqType,String ReqData)throws Exception{
        Packet.RequestLayer requestLayer=new Packet().new RequestLayer();
        byte[] newbyte=null;
        try {
            requestLayer.AppID=AppID;
            requestLayer.ReqType=ReqType;
            requestLayer.ReqData=ReqData;
            byte[] old= StringUtils.hexString2Bytes("1A");
            byte []temp= JSON.toJSONString(requestLayer).getBytes("utf-8");
            int total=temp.length+(old==null?0:old.length);
            newbyte = new byte[total];
            System.arraycopy(temp, 0, newbyte, 0, temp.length);
            System.arraycopy(old, 0, newbyte, temp.length, (old==null?0:old.length));
        }catch (Exception e){
            e.printStackTrace();
        }
         return JSON.toJSONString(requestLayer);
    }

    public String Packet_DeviceAuth(Skp skp,String AppKey)throws Exception{
        Packet.DeviceAuth deviceAuth=new Packet().new DeviceAuth();
        deviceAuth.ReqType="DeviceAuth";
        deviceAuth.DeviceSN=skp.getDevicesn();
        deviceAuth.DevicePassword=skp.getDevicepassword();
        System.out.println(JSON.toJSONString(deviceAuth));
        return AESUtils.aesEncrypt(JSON.toJSONString(deviceAuth),AppKey);
    }
    public String Packet_DeviceState(Skp skp,String AppKey)throws Exception{
        Packet.DeviceState deviceState=new Packet().new DeviceState();
        deviceState.ReqType="DeviceState";
        deviceState.DeviceSN=skp.getDevicesn();
        deviceState.DeviceKey=skp.getDevicekey();
        System.out.println(JSON.toJSONString(deviceState));
        return AESUtils.aesEncrypt(JSON.toJSONString(deviceState),AppKey);
    }
    public String Packet_InputUDiskPassword(Skp skp)throws Exception{
        Packet.InputUDiskPassword inputUDiskPassword=new Packet().new InputUDiskPassword();
        inputUDiskPassword.UDiskSn=skp.getSkph();
        inputUDiskPassword.UDiskPwd=skp.getSkpmm();
        inputUDiskPassword.CaPwd=skp.getZsmm();
        inputUDiskPassword.TaxDiskPwd="";
        List UDiskList=new ArrayList();
        UDiskList.add(inputUDiskPassword);
        Map  UDiskMap=new HashMap(1);
        UDiskMap.put("UDiskList",UDiskList);
        return AESUtils.aesEncrypt(JSON.toJSONString(UDiskMap),skp.getDevicekey());
    }
    /***
     * 解压GZip
     *
     * @param data
     * @return
     */
    public static byte[] unGZip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            GZIPInputStream gzip = new GZIPInputStream(bis);
            byte[] buf = new byte[1024];
            int num = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }
            b = baos.toByteArray();
            baos.flush();
            baos.close();
            gzip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }
    /***
     * 压缩GZip
     *
     * @param data
     * @return
     */
    public static byte[] gZip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(data);
            gzip.finish();
            gzip.close();
            b = bos.toByteArray();
            bos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

    /**
     * 解密业务包文
     * @param str
     * @param DeviceKey
     * @return
     */
    public static String jiemiData(String str,String DeviceKey){
        String s="";
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] str2=decoder.decodeBuffer(str);
            s=new String(PacketBody.unGZip(AESUtils.aesDecrypt(str2,DeviceKey)),"utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return s;
    }
}
