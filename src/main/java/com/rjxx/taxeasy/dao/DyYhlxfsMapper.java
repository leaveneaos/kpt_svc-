package com.rjxx.taxeasy.dao;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.domains.DyYhlxfs;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Thu Mar 09 14:09:59 CST 2017
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface DyYhlxfsMapper {

    public DyYhlxfs findOneByParams(Map params);

    public List<DyYhlxfs> findAllByParams(Map params);

    public List<DyYhlxfs> findByPage(Pagination pagination);
    
    public void updateYxbz(Map params);

}

