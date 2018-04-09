package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Group;

import java.util.List;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Thu Oct 13 14:35:28 GMT+08:00 2016
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface GroupMapper {

    public Group findOneByParams(Group group);

    public List<Group> findAllByParams(Group group);

    public List<Group> findByPage(Pagination pagination);

}

