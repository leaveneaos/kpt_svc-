package com.rjxx.taxeasy.dao;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.domains.Gszc;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Tue Dec 20 15:51:26 CST 2016
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface GszcMapper {

    public Gszc findOneByParams(Map params);

    public List<Gszc> findAllByParams(Map params);

    public List<Gszc> findByPage(Pagination pagination);

}

