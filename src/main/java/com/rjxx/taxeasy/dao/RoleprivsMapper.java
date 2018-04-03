package com.rjxx.taxeasy.dao;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.domains.Roleprivs;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Thu Dec 01 15:36:47 CST 2016
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface RoleprivsMapper {

    public Roleprivs findOneByParams(Map params);

    public List<Roleprivs> findAllByParams(Map params);
    public List<Roleprivs> findBySql(Map params);
    public List<Roleprivs> findBySql1(Map params);
    public List<Roleprivs> findByPage(Pagination pagination);

}

