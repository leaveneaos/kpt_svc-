package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.DmCrestv;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Wed Jul 18 09:47:04 CST 2018
 *
 * @administrator
 */ 
@MybatisRepository
public interface DmCrestvMapper {

    public DmCrestv findOneByParams(Map params);

    public List<DmCrestv> findAllByParams(Map params);

    public List<DmCrestv> findByPage(Pagination pagination);

}

