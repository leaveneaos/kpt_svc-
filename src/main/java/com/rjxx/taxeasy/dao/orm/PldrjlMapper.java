package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Jyxxsq;
import com.rjxx.taxeasy.dao.bo.Pldrjl;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Thu Aug 03 10:28:01 CST 2017
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface PldrjlMapper {

    public Pldrjl findOneByParams(Map params);

    public List<Pldrjl> findAllByParams(Map params);

    public List<Pldrjl> findByPage(Pagination pagination);
    
    public List<Jyxxsq> findAllJyxxsqByParams(Map params);

}

