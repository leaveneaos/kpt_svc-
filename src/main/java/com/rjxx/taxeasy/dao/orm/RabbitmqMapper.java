package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.taxeasy.dao.bo.Rabbitmq;
import com.rjxx.taxeasy.dao.vo.ShMqInfo;


import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/9.
 */
@MybatisRepository
public interface RabbitmqMapper {

    public Rabbitmq findByXfid(int xfid);
    
    public List<ShMqInfo> findAll(Map params);
    public List<ShMqInfo> findByParams(Map params);

}
