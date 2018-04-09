package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.vo.Cxtjvo;
import com.rjxx.taxeasy.dao.vo.Fpnum;
import com.rjxx.taxeasy.dao.vo.KplsVO;
import com.rjxx.taxeasy.dao.vo.Slcxvo;


import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Thu Dec 01 11:29:47 GMT+08:00 2016
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface KplsvoMapper {

    public KplsVO findOneByParams(Map params);

    public List<KplsVO> findAllByParams(Map params);

    public List<KplsVO> findByPage(Pagination pagination);
    
    public List<Slcxvo> Sltjcx(Map params);
    
    public Fpnum findFps(Map params);
    
    public List<Cxtjvo> findYypl(Map params);
    
    public List<Cxtjvo> findYtql(Map params);

}

