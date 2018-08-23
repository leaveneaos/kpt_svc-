package com.rjxx.taxeasy.config.mina;

import com.alibaba.fastjson.JSON;
import com.rjxx.comm.utils.ApplicationContextUtils;
import com.rjxx.taxeasy.bizhandle.invoicehandling.FpclService;
import com.rjxx.taxeasy.config.password.PasswordConfig;
import com.rjxx.taxeasy.config.rabbitmq.RabbitmqSend;
import com.rjxx.taxeasy.dal.*;
import com.rjxx.taxeasy.dao.bo.*;
import com.rjxx.taxeasy.dao.dto.InvoiceResponse;
import com.rjxx.taxeasy.dao.dto.crestvinvoice.PacketBody;
import com.rjxx.time.TimeUtil;
import com.rjxx.utils.*;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *@ClassName SocketService
 *@Description 连接凯盈平台mina 客户端业务处理
 *@Author 许黎明
 *@Date 2018/4/25.
 *@Version 1.0
 **/
public class ServerHandler extends IoHandlerAdapter {



    private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    private static Map<String, SocketRequest> cachedRequestMap = new ConcurrentHashMap<>();

    /**
     * 线程池执行任务
     */
    private static ThreadPoolTaskExecutor taskExecutor = null;



    /**
     * 发送消息
     * @param commandId
     * @param message
     * @param wait
     * @param timeout
     * @param lsh
     */
    public static String sendMessage(String commandId, Object message,boolean wait, long timeout,int lsh)throws Exception {
        IoSession session=SocketSession.getInstance().getSession();
        if(session.isConnected()){
            sendMessage(session,message,lsh,commandId);
        }else{
            logger.info("##########The socket connection has been disconnected.#####");
            return "连接断开";
        }
        if (wait && timeout > 0) {
            logger.info("####ServerHandler sendMessage waiting for timeout !!####,commandId="+commandId);
            SocketRequest socketRequest = new SocketRequest();
            socketRequest.setCommandId(commandId);
            cachedRequestMap.put(commandId, socketRequest);
            synchronized (socketRequest) {
                socketRequest.wait(timeout);
            }
            if (socketRequest.getException() != null) {
                throw socketRequest.getException();
            }
            return socketRequest.getReturnMessage();
        }
        return null;
    }



    /**
     * 发送消息
     *
     * @param session
     * @param message
     */
    public static void sendMessage(IoSession session, Object message,int lsh,String commandId) throws Exception{
        if("NewInvoice".equals(commandId)){
            session.setAttribute("lsh",lsh);
        }

        WriteFuture writeFuture = session.write(message);
        writeFuture.addListener(new IoFutureListener<WriteFuture>() {
            @Override
            public void operationComplete(WriteFuture future) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean flag = future.isWritten();
                if(!flag){
                    logger.info("################Mina send message failed!!!");
                }else{
                    logger.info("----发送数据成功！------kplsh="+lsh);
                }
            }
        });
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        logger.info("客户端连接是否开启:" + session.isConnected());
        logger.info("客户端连接数:" +session.getService().getManagedSessionCount());
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        logger.info("客户端连接是否关闭:" + session.isClosing());
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        ReceiveTask receiveTask = new ReceiveTask();
        receiveTask.setMsg((String)message);
        receiveTask.setSession(session);
        if (taskExecutor == null) {
            taskExecutor = ApplicationContextUtils.getBean(ThreadPoolTaskExecutor.class);
        }
        taskExecutor.execute(receiveTask);
        //session.write(new String(StringUtils.hexString2Bytes("1A")));
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        if(session.isConnected()){
            logger.info("客户端发送信息成功  message:" + message);
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        logger.info("客户端连接空闲:" + status.toString()+session.isConnected());
        session.closeNow();
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            logger.info("客户端连接IO异常信息:" + cause.getMessage());
            if(session.getAttribute("lsh")!=null){
                String lsh=session.getAttribute("lsh").toString();
                //logger.info("----发送异常流水号----"+lsh);
                RabbitmqSend rabbitmqSend = ApplicationContextUtils.getBean(RabbitmqSend.class);
                logger.info("ServerHandler.exceptionCaught,捕捉socket通道异常，重新放入凯盈开票队列，kplsh="+lsh);
                rabbitmqSend.sendbox(lsh + "");
            }
            session.closeNow();
        }
        else if (cause instanceof ProtocolDecoderException) {
            logger.info("客户端连接编码与解码异常信息:" + cause.getMessage());
            session.closeNow();
        }
        else {
            logger.info("客户端连接其他异常信息:" + cause.getMessage());
            session.closeNow();
        }
    }

    /**
     * 接收任务
     */
    static class ReceiveTask implements Runnable {

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
                        break;
                    case "DeviceState":
                        OnReceive_DeviceState(ReqData,ReqType);
                        break;
                    case "DeviceCmd":
                        OnReceive_DeviceCmd(ReqData,ReqType);
                        break;
                    default:
                        break;
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
    public static void setSocketRequest(String reqType,String reqData){
        if (StringUtils.isNotBlank(reqType)) {
            SocketRequest socketRequest = cachedRequestMap.remove(reqType);
            if (socketRequest != null) {
                if (StringUtils.isNotBlank(reqData)) {
                    socketRequest.setReturnMessage(reqData);
                } else {
                    socketRequest.setReturnMessage("");
                }
                synchronized (socketRequest) {
                    socketRequest.notifyAll();
                }
            }
        }
    }
    private static void OnReceive_DeviceState(String reqData, String reqType) {

        String ResultCode=null;
        Map DeviceAuthMap=null;
        try {
            DeviceAuthMap=XmltoJson.strJson2Map(reqData);
            ResultCode=DeviceAuthMap.get("ResultCode").toString();
            String ResultMsg=DeviceAuthMap.get("ResultMsg").toString();
            if("0".equals(ResultCode)){
                String DeviceSN=DeviceAuthMap.get("DeviceSN").toString();
                String LatestOnlineTime=DeviceAuthMap.get("LatestOnlineTime").toString();
                BASE64Decoder decoder = new BASE64Decoder();
                SkpService skpService = ApplicationContextUtils.getBean(SkpService.class);
                Map skpMap =new HashMap(1);
                skpMap.put("devicesn",DeviceSN);
                Skp skp= skpService.findOneByParams(skpMap);
                skpService.save(skp);
            }
            setSocketRequest(reqType,reqData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static void OnReceive_DeviceCmd(String reqData, String reqType) {
        Map DeviceCmdMap=XmltoJson.strJson2Map(reqData);
        String ProtocolVer=DeviceCmdMap.get("ProtocolVer")==null?"":DeviceCmdMap.get("ProtocolVer").toString();
        String SeqNumber=DeviceCmdMap.get("SeqNumber")==null?"":DeviceCmdMap.get("SeqNumber").toString();
        String OpType=DeviceCmdMap.get("OpType")==null?"":DeviceCmdMap.get("OpType").toString();
        String ZipType=DeviceCmdMap.get("ZipType")==null?"":DeviceCmdMap.get("ZipType").toString();
        String EncryptType=DeviceCmdMap.get("EncryptType")==null?"":DeviceCmdMap.get("EncryptType").toString();
        String DeviceSN=DeviceCmdMap.get("DeviceSN")==null?"":DeviceCmdMap.get("DeviceSN").toString();
        String UserID=DeviceCmdMap.get("UserID")==null?"":DeviceCmdMap.get("UserID").toString();
        KplsService kplsService = ApplicationContextUtils.getBean(KplsService.class);
        SkpService skpService = ApplicationContextUtils.getBean(SkpService.class);
        SeqnumberService seqnumberService = ApplicationContextUtils.getBean(SeqnumberService.class);
        Seqnumber seqNumber=seqnumberService.findOne(Integer.valueOf(SeqNumber));
        if("".equals(OpType)){
            OpType=seqNumber.getOptype();
        }
        Kpls kpls=null;
        Skp skp=null;
        if("InvalidateInvoice".equals(OpType)||"NewInvoice".equals(OpType)){
            kpls=kplsService.findOne(Integer.valueOf(seqNumber.getJylsh()));
            skp=skpService.findOne(kpls.getSkpid());
        }else{
            skp=skpService.findOne(Integer.valueOf(seqNumber.getJylsh()));
        }
        String Data=null;
        if(null!=DeviceCmdMap.get("Data")){
            Data=PacketBody.jiemiData(DeviceCmdMap.get("Data").toString(),skp.getDevicekey());
        }else{
            Map errorMap=new HashMap();
            errorMap.put("Code",DeviceCmdMap.get("ResultCode")==null?"":DeviceCmdMap.get("ResultCode").toString());
            errorMap.put("Msg",DeviceCmdMap.get("ResultMsg")==null?"":DeviceCmdMap.get("ResultMsg").toString());
            Data= JSON.toJSONString(errorMap);
        }
        switch (OpType){
            case "NewInvoice":
                OnReceive_NewInvoice(Data,OpType,kpls.getKplsh().toString());
                break;
            case "InputUDiskPassword":
                OnReceive_InputUDiskPassword(Data,OpType,skp.getId().toString());
                break;
            case "InvalidateInvoice":
                OnReceive_InvalidateInvoice(Data,OpType,kpls.getKplsh().toString());
                break;
            case "GetUploadStates":
                OnReceive_GetUploadStates(Data,OpType,skp.getId().toString());
                break;
            case "TriggerUpload":
                OnReceive_TriggerUpload(Data,OpType,skp.getId().toString());
                break;
            case "GetDeclareTaxStates":
                OnReceive_GetDeclareTaxStates(Data,OpType,skp.getId().toString());
                break;
            case "TriggerDeclareTax":
                OnReceive_TriggerDeclareTax(Data,OpType,skp.getId().toString());
                break;
            case "UDiskInfo":
                OnReceive_UDiskInfo(Data,OpType,skp.getId().toString());
                break;
            case "InvoiceControlInfo":
                OnReceive_InvoiceControlInfo(Data,OpType,skp.getId().toString());
                break;
            case "GetCurrentInvoiceInfo":
                OnReceive_GetCurrentInvoiceInfo(Data,OpType,skp.getId().toString());
                break;
            case "GetAllInvoiceSections":
                OnReceive_GetAllInvoiceSections(Data,OpType,skp.getId().toString());
                break;
            case "InvoiceDistribute":
                OnReceive_InvoiceDistribute(Data,OpType,skp.getId().toString());
                break;
            case "UDiskBinding":
                OnReceive_UDiskBinding(Data,OpType,skp.getId().toString());
                break;
            case "SwitchUDisk":
                OnReceive_SwitchUDisk(Data,OpType,skp.getId().toString());
                break;
            case "DeviceInfo":
                OnReceive_DeviceInfo(Data,OpType,skp.getId().toString());
                break;
            case "FactoryReset":
                OnReceive_FactoryReset(Data,OpType,skp.getId().toString());
                break;
            default:
                break;
        }
    }

    private static void OnReceive_FactoryReset(String data, String opType, String s) {
        String ResultCode=null;
        Map FactoryResetMap=null;
        try {
            FactoryResetMap=XmltoJson.strJson2Map(data);
            ResultCode=FactoryResetMap.get("Code").toString();
            if("0".equals(ResultCode)){
                String ResultMsg=FactoryResetMap.get("Msg").toString();
            }
            setSocketRequest(opType,data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void OnReceive_DeviceInfo(String data, String opType, String s) {
        String ResultCode=null;
        Map DeviceInfoMap=null;
        try {
            DeviceInfoMap=XmltoJson.strJson2Map(data);
            ResultCode=DeviceInfoMap.get("Code").toString();
            if("0".equals(ResultCode)){
                String ResultMsg=DeviceInfoMap.get("Msg").toString();
            }
            setSocketRequest(opType,data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void OnReceive_SwitchUDisk(String data, String opType, String s) {
        String ResultCode=null;
        Map SwitchUDiskMap=null;
        try {
            SwitchUDiskMap=XmltoJson.strJson2Map(data);
            ResultCode=SwitchUDiskMap.get("Code").toString();
            if("0".equals(ResultCode)){
                String ResultMsg=SwitchUDiskMap.get("Msg").toString();
            }
            setSocketRequest(opType,data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void OnReceive_UDiskBinding(String data, String opType, String s) {
        String ResultCode=null;
        Map UDiskBindingMap=null;
        try {
            UDiskBindingMap=XmltoJson.strJson2Map(data);
            ResultCode=UDiskBindingMap.get("Code").toString();
            if("0".equals(ResultCode)){
                String ResultMsg=UDiskBindingMap.get("Msg").toString();
            }
            setSocketRequest(opType,data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void OnReceive_InvoiceDistribute(String data, String opType, String s) {
        String ResultCode=null;
        Map InvoiceDistributeMap=null;
        try {
            InvoiceDistributeMap=XmltoJson.strJson2Map(data);
            ResultCode=InvoiceDistributeMap.get("Code").toString();
            if("0".equals(ResultCode)){
                String ResultMsg=InvoiceDistributeMap.get("Msg").toString();
            }
            setSocketRequest(opType,data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void OnReceive_GetAllInvoiceSections(String data, String opType, String s) {
        String ResultCode=null;
        Map GetAllInvoiceSectionsMap=null;
        try {
            GetAllInvoiceSectionsMap=XmltoJson.strJson2Map(data);
            ResultCode=GetAllInvoiceSectionsMap.get("Code").toString();
            if("0".equals(ResultCode)){
                String ResultMsg=GetAllInvoiceSectionsMap.get("Msg").toString();
            }
            setSocketRequest(opType,data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void OnReceive_GetCurrentInvoiceInfo(String data, String opType, String s) {
        String ResultCode=null;
        Map GetCurrentInvoiceInfoMap=null;
        try {
            GetCurrentInvoiceInfoMap=XmltoJson.strJson2Map(data);
            ResultCode=GetCurrentInvoiceInfoMap.get("Code").toString();
            InvoiceResponse invoiceResponse=new InvoiceResponse();
            if("0".equals(ResultCode)){
                String ResultMsg=GetCurrentInvoiceInfoMap.get("Msg").toString();
                if(null!=GetCurrentInvoiceInfoMap.get("InvoiceList")){
                    List InvoiceList=(List)GetCurrentInvoiceInfoMap.get("InvoiceList");
                    Map InvoiceMap=(Map)InvoiceList.get(0);
                    String InvoiceType=(String)InvoiceMap.get("InvoiceType");
                    String InvoiceCode=(String)InvoiceMap.get("InvoiceCode");
                    String CurrentInvoiceNum=(String)InvoiceMap.get("CurrentInvoiceNum");
                    invoiceResponse.setFpdm(InvoiceCode);
                    invoiceResponse.setFphm(CurrentInvoiceNum);
                    invoiceResponse.setFplxdm(InvoiceType);
                    invoiceResponse.setReturnCode("0000");
                    invoiceResponse.setReturnMessage("获取号码成功");
                }
            }
            setSocketRequest(opType, XmlJaxbUtils.toXml(invoiceResponse));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void OnReceive_InvoiceControlInfo(String data, String opType, String s) {
        String ResultCode=null;
        Map InvoiceControlInfoMap=null;
        try {
            InvoiceControlInfoMap=XmltoJson.strJson2Map(data);
            ResultCode=InvoiceControlInfoMap.get("Code").toString();
            if("0".equals(ResultCode)){
                String ResultMsg=InvoiceControlInfoMap.get("Msg").toString();
            }
            setSocketRequest(opType,data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void OnReceive_UDiskInfo(String data, String opType, String s) {
        String ResultCode=null;
        Map UDiskInfoMap=null;
        try {
            UDiskInfoMap=XmltoJson.strJson2Map(data);
            ResultCode=UDiskInfoMap.get("Code").toString();
            if("0".equals(ResultCode)){
                String ResultMsg=UDiskInfoMap.get("Msg").toString();
            }
            setSocketRequest(opType,data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void OnReceive_TriggerDeclareTax(String data, String opType, String s) {
        String ResultCode=null;
        Map TriggerDeclareTaxMap=null;
        try {
            TriggerDeclareTaxMap=XmltoJson.strJson2Map(data);
            ResultCode=TriggerDeclareTaxMap.get("Code").toString();
            if("0".equals(ResultCode)){
                String ResultMsg=TriggerDeclareTaxMap.get("Msg").toString();
            }
            setSocketRequest(opType,data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void OnReceive_GetDeclareTaxStates(String data, String opType, String s) {
        String ResultCode=null;
        Map GetDeclareTaxStatesMap=null;
        try {
            GetDeclareTaxStatesMap=XmltoJson.strJson2Map(data);
            ResultCode=GetDeclareTaxStatesMap.get("Code").toString();
            if("0".equals(ResultCode)){
                String ResultMsg=GetDeclareTaxStatesMap.get("Msg").toString();
            }
            setSocketRequest(opType,data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void OnReceive_TriggerUpload(String data, String opType, String s) {
        String ResultCode=null;
        Map TriggerUploadMap=null;
        try {
            TriggerUploadMap=XmltoJson.strJson2Map(data);
            ResultCode=TriggerUploadMap.get("Code").toString();
            if("0".equals(ResultCode)){
                String ResultMsg=TriggerUploadMap.get("Msg").toString();
            }
            setSocketRequest(opType,data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void OnReceive_GetUploadStates(String data, String opType, String s) {
        String ResultCode=null;
        Map GetUploadStatesMap=null;
        try {
            GetUploadStatesMap=XmltoJson.strJson2Map(data);
            ResultCode=GetUploadStatesMap.get("Code").toString();
            if("0".equals(ResultCode)){
                String ResultMsg=GetUploadStatesMap.get("Msg").toString();
            }
            setSocketRequest(opType,data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void OnReceive_InvalidateInvoice(String data, String opType, String seqNumber) {
        String ResultCode=null;
        Map InvalidateInvoiceMap=null;
        try {
            InvalidateInvoiceMap=XmltoJson.strJson2Map(data);
            ResultCode=InvalidateInvoiceMap.get("Code").toString();
            String ResultMsg=InvalidateInvoiceMap.get("Msg").toString();
            if("0".equals(ResultCode)){
                String InvoiceType=InvalidateInvoiceMap.get("InvoiceType").toString();
                String InvoiceCode=InvalidateInvoiceMap.get("InvoiceCode").toString();
                String InvoiceNum=InvalidateInvoiceMap.get("InvoiceNum").toString();
                String InvalidateTime=InvalidateInvoiceMap.get("InvalidateTime").toString();
                KplsService kplsService = ApplicationContextUtils.getBean(KplsService.class);
                Kpls kpls=kplsService.findOne(Integer.valueOf(seqNumber));
                kpls.setZfrq(TimeUtil.getSysDateInDate(InvalidateTime, null));
                kplsService.save(kpls);
            }
            setSocketRequest(opType,data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void OnReceive_InputUDiskPassword(String data, String opType, String seqNumber) {
        String ResultCode=null;
        Map DeviceAuthMap=null;
        try {
            DeviceAuthMap=XmltoJson.strJson2Map(data);
            ResultCode=DeviceAuthMap.get("Code").toString();
            if("0".equals(ResultCode)){
                String ResultMsg=DeviceAuthMap.get("Msg").toString();
            }
            setSocketRequest(opType,data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void OnReceive_NewInvoice(String data, String opType, String seqNumber) {

        try {
            KplsService kplsService = ApplicationContextUtils.getBean(KplsService.class);
            //SkpService skpService = ApplicationContextUtils.getBean(SkpService.class);
            FpclService fpclService = ApplicationContextUtils.getBean(FpclService.class);
            CrestvbusinessService crestvbusinessService = ApplicationContextUtils.getBean(CrestvbusinessService.class);
            Kpls kpls=kplsService.findOne(Integer.valueOf(seqNumber));
            //Skp skp=skpService.findOne(kpls.getSkpid());

            String result= data;
            Map resultMap=XmltoJson.strJson2Map(result);
            String Code=resultMap.get("Code").toString();
            String Msg=resultMap.get("Msg").toString();

            Map parmsMap=new HashMap(1);
            parmsMap.put("kplsh",kpls.getKplsh());
            Crestvbusiness crestvbusiness=crestvbusinessService.findOneByParams(parmsMap);
            DmCrestvService dmCrestvService = ApplicationContextUtils.getBean(DmCrestvService.class);
            Map crestvMap = new HashMap();
            crestvMap.put("code",Code);
            DmCrestv dmCrestv = dmCrestvService.findOneByParams(crestvMap);
            //20180718重发数据表中有数据，且凯盈返回代码查询代码表中repeatSend的值不为1时，删除重发表数据.
            if(null!=crestvbusiness && (null == dmCrestv || (null != dmCrestv && !dmCrestv.getRepeatSend().equals("1")))){
                crestvbusinessService.delete(crestvbusiness);
            }
            if("0".equals(Code)){
                if(resultMap.get("InvoiceList")!=null){
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
                            kpMap.put("EWM", "");
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
                }
            }else{
                Map kpMap=new HashMap(2);
                kpMap.put("RETURNCODE", Code);
                kpMap.put("RETURNMSG", Msg);
                if(Msg.contains("相同请求处理中")){
                    kpls.setFpztdm("14");
                    kpls.setErrorReason("相同请求处理中");
                    kplsService.save(kpls);
                }else if(Code.equals("1")){
                    kpls.setFpztdm("14");
                    kpls.setErrorReason("请求已接收");
                    kplsService.save(kpls);
                }else{
                    kpMap.put("KPLSH", kpls.getKplsh());
                    fpclService.updateKpls(kpMap);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void OnReceive_DeviceAuth(String ReqData,String ReqType)throws Exception{

        String ResultCode=null;
        try {
            Map DeviceAuthMap=XmltoJson.strJson2Map(ReqData);
            ResultCode=DeviceAuthMap.get("ResultCode").toString();
            String ResultMsg=DeviceAuthMap.get("ResultMsg").toString();
            if("0".equals(ResultCode)){
                String DeviceSN=DeviceAuthMap.get("DeviceSN").toString();
                String DeviceKey=DeviceAuthMap.get("DeviceKey").toString();
                BASE64Decoder decoder = new BASE64Decoder();
                DeviceKey=DesUtils.bytesToHexString(decoder.decodeBuffer(DeviceKey)).toUpperCase();
                logger.info("------终端密钥--------"+DeviceKey);
                SkpService skpService = ApplicationContextUtils.getBean(SkpService.class);
                Map skpMap =new HashMap(1);
                skpMap.put("devicesn",DeviceSN);
                Skp skp= skpService.findOneByParams(skpMap);
                skp.setDevicekey(DeviceKey);
                skpService.save(skp);
            }
            setSocketRequest(ReqType,ReqData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        BASE64Decoder decoder = new BASE64Decoder();
        String Appkey="1BE2E4DECA4C6EF2B0DB1455FD859C607EF55EE43B95204D22DFE29957A46AEA";
        String miwen="LodVKHBP6PfYeYgWr4qeEnSsUVPAaZTCFLkYAQtFTjw=";
        byte[] ReqData=new BASE64Decoder().decodeBuffer(miwen);
        String DeviceKey=DesUtils.bytesToHexString(ReqData).toUpperCase();
        logger.info("-------解密后字符DeviceKey----------"+DeviceKey);
        //BASE64Decoder decoder = new BASE64Decoder();
        // String str="MiwEGYOEPSr1rb4ceasRAIB16qwiIyAiBFjAo4beGhZIOEJyZ5NIZI7E1wjjz9i6tUvJFwafaiZbNcMo/kbiZ+9uYPjo+cj8EOXbFouPiq4=";
        // byte[] str2=decoder.decodeBuffer(str);
        // //logger.info(new String(PacketBody.unGZip(str2)),"utf-8");
        // String DeviceKey="B5934BCC952C0C1091DDF815FBABA29C88E127705BDE0D1B4BD44B1D8648809E";
        // logger.info(new String(PacketBody.unGZip(AESUtils.aesDecrypt(str2,DeviceKey)),"utf-8"));
    }
}
