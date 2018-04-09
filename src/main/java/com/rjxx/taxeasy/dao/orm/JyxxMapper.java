package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Jyxx;
import com.rjxx.taxeasy.dao.bo.Smtq;
import com.rjxx.taxeasy.dao.vo.TqmtqVo;


import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Wed Nov 16 10:21:21 CST 2016
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface JyxxMapper {

    public Jyxx findOneByParams(Map params);

    public List<Smtq> findAllByParams(Map params);
    public List<Smtq> findAllByParams1(Map params);
    public List<TqmtqVo> findAllByParams2(Map params);
    public List<Jyxx> findByPage(Pagination pagination);
    public void update(Map params);
}

