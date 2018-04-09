package com.rjxx.taxeasy.dao.orm;

import com.rjxx.taxeasy.dao.bo.Rabbitmq;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Administrator on 2016/10/9.
 */
public interface RabbitmqJpaDao extends CrudRepository<Rabbitmq, Integer> {
}
