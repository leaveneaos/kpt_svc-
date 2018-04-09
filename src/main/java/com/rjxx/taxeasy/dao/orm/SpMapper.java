package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Sp;

import java.util.List;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Mon Oct 10 21:51:53 CST 2016
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface SpMapper {

    public Sp findOneByParams(Sp sp);

    public List<Sp> findAllByParams(Sp sp);

    public List<Sp> findByPage(Pagination pagination);

}

