package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.IsvSuiteToken;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Thu Apr 13 17:45:29 CST 2017
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface IsvSuiteTokenMapper {

    public IsvSuiteToken findOneByParams(Map params);

    public List<IsvSuiteToken> findAllByParams(Map params);

    public List<IsvSuiteToken> findByPage(Pagination pagination);

}

