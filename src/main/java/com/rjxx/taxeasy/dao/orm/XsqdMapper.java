package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Xsqd;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Thu Jul 05 09:30:30 CST 2018
 *
 * @administrator
 */ 
@MybatisRepository
public interface XsqdMapper {

    public Xsqd findOneByParams(Map params);

    public List<Xsqd> findAllByParams(Map params);

    public List<Xsqd> findByPage(Pagination pagination);

}

