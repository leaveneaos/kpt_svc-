package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Fpj;
import com.rjxx.taxeasy.dao.vo.FpjVo;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Thu Nov 03 14:27:31 GMT+08:00 2016
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface FpjMapper {

    public Fpj findOneByParams(Map params);

    public List<FpjVo> findAllByParams(Map params);
    
    public List<FpjVo> findAllByParam(Map params);

    public List<FpjVo> findByPage(Pagination pagination);
    
    public List<Fpj> findAllByDjh(Map params);

}

