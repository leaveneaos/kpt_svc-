package com.rjxx.taxeasy.config.mina;

import com.rjxx.comm.utils.ApplicationContextUtils;
import com.rjxx.taxeasy.bizhandle.invoicehandling.FpclService;
import com.rjxx.taxeasy.config.password.PasswordConfig;
import com.rjxx.taxeasy.dal.KplsService;
import com.rjxx.taxeasy.dal.SkpService;
import com.rjxx.taxeasy.dao.bo.Kpls;
import com.rjxx.taxeasy.dao.bo.Skp;
import com.rjxx.taxeasy.dao.dto.crestvinvoice.PacketBody;
import com.rjxx.utils.AESUtils;
import com.rjxx.utils.DesUtils;
import com.rjxx.utils.XmltoJson;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import sun.misc.BASE64Decoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *@ClassName SocketService
 *@Description 连接凯盈平台mina 客户端业务处理
 *@Author 许黎明
 *@Date 2018/4/25.
 *@Version 1.0
 **/
public class ServerHandler extends IoHandlerAdapter {



    private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    /**
     * 线程池执行任务
     */
    private static ThreadPoolTaskExecutor taskExecutor = null;
    /**
     * 发送消息
     * @param message
     */
    public static void sendMessage( Object message) {
        IoSession session=SocketSession.getInstance().getSession();
        sendMessage(session,message);
    }
    /**
     * 发送消息
     *
     * @param session
     * @param message
     */
    public static void sendMessage(IoSession session, Object message) {
        session.write(message);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {

    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        ReceiveTask receiveTask = new ReceiveTask();
        receiveTask.setMsg((String)message);
        receiveTask.setSession(session);
        logger.info("-------消息---------"+message);
        if (taskExecutor == null) {
            taskExecutor = ApplicationContextUtils.getBean(ThreadPoolTaskExecutor.class);
        }
        taskExecutor.execute(receiveTask);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        logger.info("客户端发送信息成功  message:" + message);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        String kpdid = (String) session.getAttribute("kpdid");
        if (kpdid != null) {
            logger.info("kpd:" + kpdid + " " + session + " idle time out!!!");
        }
        logger.info("Client will interrupt the connection with the client");
        session.closeNow();
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        String kpdid = (String) session.getAttribute("kpdid");
        if (kpdid != null) {
            logger.error("kpd:" + kpdid + " " + session + " exception caught!!!", cause);
        }
    }

    /**
     * 接收任务
     */
    class ReceiveTask implements Runnable {

        private Logger logger = LoggerFactory.getLogger(this.getClass());

        private String msg;

        private IoSession session;

        @Override
        public void run() {
            logger.info("--------服务器应答:--------"+msg);
            try {
                Map requestMap= XmltoJson.strJson2Map(msg);
                String ReqType=requestMap.get("ReqType").toString();
                String ReqData=new String(AESUtils.aesDecrypt(new BASE64Decoder().decodeBuffer(requestMap.get("ReqData").toString()), PasswordConfig.AppKey),"utf-8");
                logger.info("解密后字符"+ReqData);
                switch (ReqType){
                    case "DeviceAuth":
                        OnReceive_DeviceAuth(ReqData,ReqType);
                    case "DeviceCmd":
                        OnReceive_DeviceCmd(ReqData,ReqType);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        public void setMsg(String msg) {
            this.msg = msg;
        }

        public void setSession(IoSession session) {
            this.session = session;
        }
    }
    private static void OnReceive_DeviceCmd(String reqData, String reqType) {
        Map DeviceCmdMap=XmltoJson.strJson2Map(reqData);
        String ProtocolVer=DeviceCmdMap.get("ProtocolVer").toString();
        String SeqNumber=DeviceCmdMap.get("SeqNumber").toString();
        KplsService kplsService = ApplicationContextUtils.getBean(KplsService.class);
        SkpService skpService = ApplicationContextUtils.getBean(SkpService.class);
        Kpls kpls=kplsService.findOne(Integer.valueOf(SeqNumber));
        Skp skp=skpService.findOne(kpls.getSkpid());
        String ZipType=DeviceCmdMap.get("ZipType").toString();
        String EncryptType=DeviceCmdMap.get("EncryptType").toString();
        String DeviceSN=DeviceCmdMap.get("DeviceSN").toString();
        String UserID=DeviceCmdMap.get("UserID").toString();
        String OpType=DeviceCmdMap.get("OpType").toString();
        String Data=PacketBody.jiemiData(DeviceCmdMap.get("Data").toString(),skp.getDevicekey());
        switch (OpType){
            case "NewInvoice":
                OnReceive_NewInvoice(Data,OpType,SeqNumber);
        }
    }

    private static void OnReceive_NewInvoice(String data, String opType, String seqNumber) {

        try {
            KplsService kplsService = ApplicationContextUtils.getBean(KplsService.class);
            SkpService skpService = ApplicationContextUtils.getBean(SkpService.class);
            FpclService fpclService = ApplicationContextUtils.getBean(FpclService.class);
            Kpls kpls=kplsService.findOne(Integer.valueOf(seqNumber));
            Skp skp=skpService.findOne(kpls.getSkpid());
            String result= data;
            Map resultMap=XmltoJson.strJson2Map(result);
            String Code=resultMap.get("Code").toString();
            String Msg=resultMap.get("Msg").toString();
            List<Map> InvoiceList=(List)resultMap.get("InvoiceList");
            logger.info("-----开票数据-----"+result);
            for(Map invoiceMap:InvoiceList){
                String  UDiskSn=invoiceMap.get("UDiskSn").toString();
                String  InvoiceType=invoiceMap.get("InvoiceType").toString();
                String  InvoiceCode=invoiceMap.get("InvoiceCode").toString();
                String  InvoiceNum=invoiceMap.get("InvoiceNum").toString();
                String  InvoiceTime=invoiceMap.get("InvoiceTime").toString();
                String  VerifyCode=invoiceMap.get("VerifyCode").toString();
                String  Ciphertext=invoiceMap.get("Ciphertext").toString();
                String  QrCode=invoiceMap.get("QrCode").toString();
                Map kpMap=new HashMap(10);
                // 返回结果，发票代码
                kpMap.put("FP_DM", InvoiceCode);
                // 发票号码
                kpMap.put("FP_HM", InvoiceNum);
                // 发票密文
                kpMap.put("FP_MW", Ciphertext);
                // 校验码
                kpMap.put("JYM", VerifyCode);
                // 二维码
                if(null!=QrCode){
                    kpMap.put("EWM", QrCode);
                }
                // 机器编号
                kpMap.put("JQBH", UDiskSn);
                kpMap.put("KPRQ", InvoiceTime);
                if("0".equals(Code)){
                    kpMap.put("RETURNCODE", "0000");
                }
                kpMap.put("RETURNMSG", Msg);
                kpMap.put("KPLSH", kpls.getKplsh());
                fpclService.updateKpls(kpMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String OnReceive_DeviceAuth(String ReqData,String ReqType)throws Exception{

        String ResultCode=null;
        try {
            Map DeviceAuthMap=XmltoJson.strJson2Map(ReqData);
            String DeviceSN=DeviceAuthMap.get("DeviceSN").toString();
            String DeviceKey=DeviceAuthMap.get("DeviceKey").toString();
            ResultCode=DeviceAuthMap.get("ResultCode").toString();
            String ResultMsg=DeviceAuthMap.get("ResultMsg").toString();
            BASE64Decoder decoder = new BASE64Decoder();
            DeviceKey=DesUtils.bytesToHexString(decoder.decodeBuffer(DeviceKey)).toUpperCase();
            logger.info("------终端密钥--------"+DeviceKey);
            SkpService skpService = ApplicationContextUtils.getBean(SkpService.class);
            Map skpMap =new HashMap(1);
            skpMap.put("devicesn",DeviceSN);
            Skp skp= skpService.findOneByParams(skpMap);
            skp.setDevicekey(DeviceKey);
            skpService.save(skp);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResultCode;
    }

    public static void main(String[] args) throws Exception{
        //String ReqData=AESUtils.aesDecrypt(new String(new BASE64Decoder().decodeBuffer("yacLMdwy4ZWZUMETWpcHkzHXJwXziakpDBjmv50H1PqWLUc6/Kx6+k5ijloBRrqYO+4zJnSu4GZe1vll63YMCK+9TAoHfwmPCFSx7E2bcEdz6aiGmb8CV6tfpjfB235BlbNcuSpmPm/ugfnYp5TAevDO+Rq9sXcVZ+tLH2uPrzni8ILgN/BKbBsoNEkQIEPx"), "UTF-8"),"1BE2E4DECA4C6EF2B0DB1455FD859C607EF55EE43B95204D22DFE29957A46AEA");
        //logger.info(new String(new BASE64Decoder().decodeBuffer("yacLMdwy4ZWZUMETWpcHkzHXJwXziakpDBjmv50H1PqWLUc6/Kx6+k5ijloBRrqYO+4zJnSu4GZe1vll63YMCK+9TAoHfwmPCFSx7E2bcEdz6aiGmb8CV6tfpjfB235BlbNcuSpmPm/ugfnYp5TAevDO+Rq9sXcVZ+tLH2uPrzni8ILgN/BKbBsoNEkQIEPx"), "UTF-8"),"UTF-8");
        //String key="1BE2E4DECA4C6EF2B0DB1455FD859C607EF55EE43B95204D22DFE29957A46AEA";

       // String ReqData=AESUtils.aesDecrypt(new BASE64Decoder().decodeBuffer("yacLMdwy4ZWZUMETWpcHkzHXJwXziakpDBjmv50H1PqWLUc6/Kx6+k5ijloBRrqYO+4zJnSu4GZe1vll63YMCK+9TAoHfwmPCFSx7E2bcEdz6aiGmb8CV6tfpjfB235BlbNcuSpmPm/ugfnYp5TAevDO+Rq9sXcVZ+tLH2uPrzni8ILgN/BKbBsoNEkQIEPx"),"1BE2E4DECA4C6EF2B0DB1455FD859C607EF55EE43B95204D22DFE29957A46AEA");

       // logger.info("解密后字符"+ReqData);
        //logger.info("-------DeviceKey----------"+new String((new BASE64Decoder().decodeBuffer("tZNLzJUsDBCR3fgV+6uinIjhJ3Bb3g0bS9RLHYZIgJ4="))));
        BASE64Decoder decoder = new BASE64Decoder();
        String str="MiwEGYOEPSr1rb4ceasRAIB16qwiIyAiBFjAo4beGhZIOEJyZ5NIZI7E1wjjz9i6tUvJFwafaiZbNcMo/kbiZ+9uYPjo+cj8EOXbFouPiq4=";
        byte[] str2=decoder.decodeBuffer(str);
        //logger.info(new String(PacketBody.unGZip(str2)),"utf-8");
        String DeviceKey="B5934BCC952C0C1091DDF815FBABA29C88E127705BDE0D1B4BD44B1D8648809E";
        logger.info(new String(PacketBody.unGZip(AESUtils.aesDecrypt(str2,DeviceKey)),"utf-8"));
    }
}
