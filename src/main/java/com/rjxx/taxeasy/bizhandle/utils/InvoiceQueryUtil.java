package com.rjxx.taxeasy.bizhandle.utils;


import com.rjxx.taxeasy.dal.KplsService;
import com.rjxx.taxeasy.dao.vo.Fpcxvo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *@ClassName InvoiceQueryUtil
 *@Description TODO
 *@Author xlm
 *@Date 2018/3/29.
 *@Version 1.0
 **/
@Service
public class InvoiceQueryUtil {

    @Autowired
    private KplsService kplsService;

    /**
     *  @param gsdm,khh
     * @return list
     * 通过客户号，公司代码获取发票信息list
     */
    public List<Fpcxvo> getInvoiceListByKhh(String gsdm, String khh){
        List<Fpcxvo> list = new ArrayList<Fpcxvo>();
        Map params = new HashMap<String,Object>();
        params.put("gsdm",gsdm);
        params.put("khh",khh);
        params.put("fpczlx","11");
        params.put("fpzldm","12");
        list = kplsService.findFpListByParams(params);
        return list;
    }

    /**
     *  @param gsdm,tqm
     * @return list
     * 通过客户号，公司代码获取发票信息list
     */
    public List<Fpcxvo> getInvoiceListBytqm(String gsdm,String tqm){
        List<Fpcxvo> list = new ArrayList<Fpcxvo>();
        Map params = new HashMap<String,Object>();
        params.put("gsdm",gsdm);
        params.put("tqm",tqm);
        params.put("fpczlx","11");
        params.put("fpzldm","12");
        list = kplsService.findFpListByParams(params);
        return list;
    }

    /**
     *  @param gsdm,ddh
     * @return list
     * 通过订单号，公司代码获取发票信息list
     */
    public List<Fpcxvo> getInvoiceListByDdh(String gsdm,String ddh){
        List<Fpcxvo> list = new ArrayList<Fpcxvo>();
        Map params = new HashMap<String,Object>();
        params.put("gsdm",gsdm);
        params.put("ddh",ddh);
        params.put("fpczlx","11");
        params.put("fpzldm","12");
        list = kplsService.findFpListByParams(params);
        return list;
    }
}
