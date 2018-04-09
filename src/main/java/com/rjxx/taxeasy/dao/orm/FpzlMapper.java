package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Fpzl;
import com.rjxx.taxeasy.dao.bo.Jyxxsq;
import com.rjxx.taxeasy.dao.bo.Kpls;
import com.rjxx.taxeasy.dao.bo.Kpspmx;
import com.rjxx.taxeasy.dao.vo.Fpnum;
import com.rjxx.taxeasy.dao.vo.Slcxvo;


import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Tue Jan 17 11:22:01 CST 2017
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface FpzlMapper {

    public Fpzl findOneByParams(Map params);

    public List<Fpzl> findAllByParams(Map params);

    public List<Fpzl> findByPage(Pagination pagination);
    
    public List<Fpnum> findGfpsl(Map params);
    
    public List<Kpspmx> findSpsl(Map params);
    
    public List<Slcxvo> findSpje(Map params);
    
    public List<Jyxxsq> findDbsx(Map params);
    
    public Kpls findDkpsj(Map params);

}

