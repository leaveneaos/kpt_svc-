package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Mkgl;
import com.rjxx.taxeasy.dao.vo.Mkvo;


import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Thu Oct 13 10:51:59 CST 2016
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface MkglMapper {

    public Mkgl findOneByParams(Map params);
    
    public void update(Map params);
    
    public void destory(Map params);
    
    public void updateSort(Map params);
    
    public List<Mkgl> findAllByParams(Map params);
    
    public List<Mkvo> findAllConnect(Map params);

    public List<Mkgl> findByPage(Pagination pagination);
  //新增时查询区块内容是否存在
    public Mkgl findQkExist(Map params);
    //修改时查询区块内容是否存在
    public Mkgl findQkExistUpdate(Map params);

}

