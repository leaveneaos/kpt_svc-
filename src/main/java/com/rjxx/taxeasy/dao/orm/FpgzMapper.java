package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Fpgz;
import com.rjxx.taxeasy.dao.vo.FpgzVo;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Fri Feb 17 11:59:38 CST 2017
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface FpgzMapper {

    public Fpgz findOneByParams(Map params);

    public List<Fpgz> findAllByParams(Map params);

    public List<FpgzVo> findByPage(Pagination pagination);

}

