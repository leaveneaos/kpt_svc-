package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.taxeasy.dao.bo.Privileges;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/9.
 */
@MybatisRepository
public interface PrivilegesMapper {

    public List<Privileges> findByRoleIds(Map params);
    public List<Privileges> findOneByParams(Map params);

}
