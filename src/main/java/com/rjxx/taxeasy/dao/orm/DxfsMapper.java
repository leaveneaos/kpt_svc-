package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Dxfs;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Wed Dec 14 15:19:28 CST 2016
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface DxfsMapper {

    public Dxfs findOneByParams(Map params);

    public List<Dxfs> findAllByParams(Map params);

    public List<Dxfs> findByPage(Pagination pagination);

}

