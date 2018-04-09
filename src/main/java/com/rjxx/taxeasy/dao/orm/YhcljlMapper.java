package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Yhcljl;
import com.rjxx.taxeasy.dao.vo.Yhcljlvo;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Wed Feb 22 13:59:07 CST 2017
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface YhcljlMapper {

    public Yhcljl findOneByParams(Map params);

    public List<Yhcljl> findAllByParams(Map params);

    public List<Yhcljl> findByPage(Pagination pagination);
    
    public List<Yhcljlvo> findYhcljl(Map params);

}

