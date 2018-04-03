package com.rjxx.taxeasy.dao;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.domains.Jkpzb;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Tue Mar 20 16:06:49 CST 2018
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface JkpzbMapper {

    public Jkpzb findOneByParams(Map params);

    public List<Jkpzb> findAllByParams(Map params);

    public List<Jkpzb> findByPage(Pagination pagination);

}

