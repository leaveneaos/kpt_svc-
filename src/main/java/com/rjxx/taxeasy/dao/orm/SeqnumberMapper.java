package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Seqnumber;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Fri May 11 14:40:00 CST 2018
 *
 * @administrator
 */ 
@MybatisRepository
public interface SeqnumberMapper {

    public Seqnumber findOneByParams(Map params);

    public List<Seqnumber> findAllByParams(Map params);

    public List<Seqnumber> findByPage(Pagination pagination);

}

