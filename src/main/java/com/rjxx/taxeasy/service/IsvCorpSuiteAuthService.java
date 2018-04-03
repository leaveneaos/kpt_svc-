package com.rjxx.taxeasy.service;

import com.alibaba.fastjson.JSON;
import com.dingtalk.open.client.api.model.isv.CorpAgent;
import com.dingtalk.open.client.api.model.isv.CorpAuthInfo;
import com.dingtalk.open.client.api.model.isv.CorpAuthInfo.Agent;
import com.dingtalk.open.client.api.model.isv.CorpAuthInfo.AuthCorpInfo;
import com.dingtalk.open.client.api.model.isv.CorpAuthInfo.AuthInfo;
import com.dingtalk.open.client.api.model.isv.CorpAuthSuiteCode;
import com.google.common.eventbus.EventBus;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.IsvCorpSuiteAuthJpaDao;
import com.rjxx.taxeasy.dao.IsvCorpSuiteAuthMapper;
import com.rjxx.taxeasy.dingding.Helper.AuthHelper;

import com.rjxx.taxeasy.dingding.Helper.ServiceHelper;
import com.rjxx.taxeasy.domains.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Thu Apr 13 17:36:36 CST 2017
 *
 * @ZhangBing
 */ 
@Service
public class IsvCorpSuiteAuthService {
    
	private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IsvCorpSuiteAuthJpaDao isvCorpSuiteAuthJpaDao;

    @Autowired
    private IsvCorpSuiteAuthMapper isvCorpSuiteAuthMapper;
    @Autowired
    private IsvSuiteService isvSuiteService;
    @Autowired
    private IsvAppService isvappservice;
    @Autowired
    private IsvCorpService isvcorpservice;
    @Autowired
    private IsvCorpAppService IsvCorpAppService;
    @Autowired
    private IsvSuiteTokenService isvsuitetokenservice;
    @Autowired
    private IsvCorpTokenService IsvCorpTokenService;
    @Autowired
    private IsvCorpSuiteJsapiTicketService isvCorpSuiteJsapiTicketService;

    public IsvCorpSuiteAuth findOne(int id) {
        return isvCorpSuiteAuthJpaDao.findOne(id);
    }

    public void save(IsvCorpSuiteAuth isvCorpSuiteAuth) {
        isvCorpSuiteAuthJpaDao.save(isvCorpSuiteAuth);
    }

    public void save(List<IsvCorpSuiteAuth> isvCorpSuiteAuthList) {
        isvCorpSuiteAuthJpaDao.save(isvCorpSuiteAuthList);
    }

    public IsvCorpSuiteAuth findOneByParams(Map params) {
        return isvCorpSuiteAuthMapper.findOneByParams(params);
    }

    public List<IsvCorpSuiteAuth> findAllByParams(Map params) {
        return isvCorpSuiteAuthMapper.findAllByParams(params);
    }

    public List<IsvCorpSuiteAuth> findByPage(Pagination pagination) {
        return isvCorpSuiteAuthMapper.findByPage(pagination);
    }
    /**
     * 用临时授权码换取企业授权信息
     *
     * @param suiteKey  套件key
     * @param tmpAuthCode  临时授权码
     * @return
     */
	public boolean saveOrUpdateCorpSuiteAuth(String suiteKey, String tmpAuthCode) {
		// TODO Auto-generated method stub
		
		logger.info("suiteKey:{},tmpAuthCode:{}",suiteKey,tmpAuthCode);
		
		
        try{
        Map map=new HashMap();
        map.put("suiteKey",suiteKey);
        //获取套件access_token
        IsvSuiteToken IsvSuiteToken=isvsuitetokenservice.findOneByParams(map);
        
        String suiteToken = IsvSuiteToken.getSuiteToken();
       
      
        CorpAuthSuiteCode corpAuthSuiteCode = ServiceHelper.getPermanentCode(tmpAuthCode, suiteToken);
        
        String corpId = corpAuthSuiteCode.getAuth_corp_info().getCorpid();
        
		String permanent_code = corpAuthSuiteCode.getPermanent_code();
        
		/*
		 * 对企业授权的套件发起激活，
		 */
		ServiceHelper.getActivateSuite(suiteToken, suiteKey, corpId, permanent_code);
		/*
		 * 将corpId（企业id）和permanent_code（永久授权码）做持久化存储
		 * 之后在获取企业的access_token时需要使用
		 */
        IsvCorpSuiteAuth IsvCorpSuiteAuthsave=new IsvCorpSuiteAuth();
        IsvCorpSuiteAuthsave.setChPermanentCode("");
        IsvCorpSuiteAuthsave.setCorpId(corpId);
        IsvCorpSuiteAuthsave.setPermanentCode(permanent_code);
        IsvCorpSuiteAuthsave.setSuiteKey(suiteKey);
        IsvCorpSuiteAuthsave.setGmtCreate(new Date());
        IsvCorpSuiteAuthsave.setGmtModified(new Date());
        
    	String corptoken=AuthHelper.getCorpAccessToken(corpId,IsvSuiteToken.getSuiteToken(),permanent_code);

        IsvCorpToken  isvcorptoken=new IsvCorpToken();
        isvcorptoken.setCorpId(corpId);
        isvcorptoken.setSuiteKey(suiteKey);
        isvcorptoken.setGmtCreate(new Date());
        isvcorptoken.setGmtModified(new Date());
        isvcorptoken.setCorpToken(corptoken);
        Calendar ca=Calendar.getInstance();
    	ca.setTime(new Date());
    	ca.add(Calendar.HOUR_OF_DAY, 2);
        isvcorptoken.setExpiredTime(ca.getTime());
        IsvCorpTokenService.save(isvcorptoken);
        
        this.save(IsvCorpSuiteAuthsave);
        
        CorpAuthInfo corpauthinfo=ServiceHelper.getAuthInfo(suiteToken,suiteKey,corpId,permanent_code);
        
        System.out.println(JSON.toJSON(corpauthinfo));
        
        AuthCorpInfo authcorpinfo= corpauthinfo.getAuth_corp_info();//获取授权微应用信息
        
        System.out.println(JSON.toJSON(authcorpinfo));
        map.put("corpId", corpId);
        IsvCorp isvcorp= new IsvCorp();
        isvcorp.setCorpLogoUrl(authcorpinfo.getCorp_logo_url());
        isvcorp.setCorpName(authcorpinfo.getCorp_name());
        isvcorp.setGmtCreate(new Date());
        isvcorp.setGmtModified(new Date());
        isvcorp.setIndustry(authcorpinfo.getIndustry());
        isvcorp.setInviteCode(authcorpinfo.getInvite_code());
        isvcorp.setInviteUrl(authcorpinfo.getInvite_url());
        isvcorp.setCorpId(corpId);
        isvcorpservice.save(isvcorp);
        
        AuthInfo  authinfo= corpauthinfo.getAuth_info();
        
        System.out.println(JSON.toJSON(authinfo));

        List<Agent> agentlist=authinfo.getAgent();
        
        System.out.println(JSON.toJSON(agentlist));
        
        for(Agent agent : agentlist){
       	CorpAgent CorpAgent= ServiceHelper.getAgent(suiteToken, suiteKey, corpId, permanent_code, agent.getAgentid().toString());
       	
       	IsvCorpApp IsvCorpApp=new IsvCorpApp();
       	
       	IsvCorpApp.setAgentId(CorpAgent.getAgentid());
       	
       	IsvCorpApp.setAgentName(CorpAgent.getName());
       	
       	IsvCorpApp.setAppId(agent.getAppid());
       	
       	IsvCorpApp.setLogoUrl(CorpAgent.getLogo_url());
       	
        IsvCorpApp.setCorpId(corpId);
        
        IsvCorpApp.setGmtCreate(new Date());
        
        IsvCorpApp.setGmtModified(new Date());

       	
       	IsvCorpAppService.save(IsvCorpApp);
        }
		    return true;
		}catch(Exception e){
			return false;
		}
	}
	/**
     * 处理权限变更事件
     *
     * @param suiteKey
     * @param corpId
     * @return
     */
	public boolean handleChangeAuth(String suiteKey, String corpId) {
		
		// TODO Auto-generated method stub
		logger.info("suiteKey:{},corpId:{}",suiteKey,corpId);
		String suiteToken = "";
        String permanentCode = "";
        try {
        	
        	 Map map=new HashMap();
        	 
             map.put("suiteKey",suiteKey);
             
             map.put("corpId", corpId);
             
             //获取套件access_token
             IsvSuiteToken IsvSuiteToken=isvsuitetokenservice.findOneByParams(map);
             
             IsvCorpSuiteAuth isvcorpsuiteauth=this.findOneByParams(map);
             if(isvcorpsuiteauth==null){
            	 logger.info("授权关系不存在或者已经解除,  suiteKey:{},corpId:{}",suiteKey,corpId);
                 return false;
             }
             suiteToken=IsvSuiteToken.getSuiteToken();
             
             permanentCode=isvcorpsuiteauth.getPermanentCode();
             
             CorpAuthInfo corpauthinfo=ServiceHelper.getAuthInfo(suiteToken,suiteKey,corpId,permanentCode);
             
             System.out.println(JSON.toJSON(corpauthinfo));
             
             AuthCorpInfo authcorpinfo= corpauthinfo.getAuth_corp_info();//获取授权微应用信息
             
             System.out.println(JSON.toJSON(authcorpinfo));
             
             IsvCorp isvcorp= isvcorpservice.findOneByParams(map);
             
             isvcorp.setId(isvcorp.getId());
             isvcorp.setCorpLogoUrl(authcorpinfo.getCorp_logo_url());
             isvcorp.setCorpName(authcorpinfo.getCorp_name());
             isvcorp.setGmtCreate(new Date());
             isvcorp.setGmtModified(new Date());
             isvcorp.setIndustry(authcorpinfo.getIndustry());
             isvcorp.setInviteCode(authcorpinfo.getInvite_code());
             isvcorp.setInviteUrl(authcorpinfo.getInvite_url());
             isvcorpservice.save(isvcorp);
             
             AuthInfo  authinfo= corpauthinfo.getAuth_info();
             
             System.out.println(JSON.toJSON(authinfo));

             List<Agent> agentlist=authinfo.getAgent();
             
             System.out.println(JSON.toJSON(agentlist));
             
             for(Agent agent : agentlist){
            	CorpAgent CorpAgent= ServiceHelper.getAgent(suiteToken, suiteKey, corpId, permanentCode, agent.getAgentid().toString());
            	
            	IsvCorpApp IsvCorpApp=IsvCorpAppService.findOneByParams(map);
            	
            	IsvCorpApp.setAgentId(CorpAgent.getAgentid());
            	
            	IsvCorpApp.setAgentName(CorpAgent.getName());
            	
            	IsvCorpApp.setAppId(agent.getAppid());
            	
            	IsvCorpApp.setLogoUrl(CorpAgent.getLogo_url());
            	
            	
            	IsvCorpAppService.save(IsvCorpApp);
             }
             
        }catch(Exception e){
        	e.printStackTrace();
        	return false;
        }
      
		return true;
	}
	 /**
     * 处理解除授权事件
     *
     * @param suiteKey
     * @param
     * @return
     */
	public boolean handleRelieveAuth(String suiteKey, String receiveCorpId) {
		// TODO Auto-generated method stub
		logger.info("suiteKey:{},corpId:{}",suiteKey,receiveCorpId);
		try{
		 //1.删除掉企业对套件的授权信息
		this.deleteCorpSuiteAuth(receiveCorpId, suiteKey);
		  //2.删除掉企业使用的微应用
		Map params=new HashMap<>();
		params.put("suiteKey", suiteKey);
		params.put("corpId", receiveCorpId);
        List<IsvCorp> isvcorplist = isvcorpservice.findAllByParams(params);
        for (IsvCorp isvcorp : isvcorplist) {
        	isvcorpservice.deleteCorp(isvcorp);
        }
        IsvCorpApp isvCorpApp=IsvCorpAppService.findOneByParams(params);
        IsvCorpAppService.deleteCorpApp(isvCorpApp);

            //4.删除企业token.这个必须删除,一旦出现解除授权立即授权的情况,之前的token是不可用的
        IsvCorpToken isvCorpToken= IsvCorpTokenService.findOneByParams(params);
        IsvCorpSuiteJsapiTicket isvCorpSuiteJsapiTicket=isvCorpSuiteJsapiTicketService.findOneByParams(params);
        isvCorpSuiteJsapiTicketService.deleteisvCorpSuiteJsapiTicket(isvCorpSuiteJsapiTicket);
        IsvCorpTokenService.deleteCorpToken(isvCorpToken);
		    return true;
		}catch(Exception e){
			return false;
		}
	}
    /**
     * //1.删除掉企业对套件的授权信息
     *
     * @param receiveCorpId
     * @param suiteKey
     */
	private void deleteCorpSuiteAuth(String receiveCorpId, String suiteKey) {
		// TODO Auto-generated method stub
		Map map=new HashMap();
        map.put("suiteKey",suiteKey);
        map.put("corpId", receiveCorpId);
        IsvCorpSuiteAuth isvcorpsuiteauth=this.findOneByParams(map);

        //删除套件授权码
		isvCorpSuiteAuthJpaDao.delete(isvcorpsuiteauth);
	}

}

