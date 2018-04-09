package com.rjxx.taxeasy.dao.orm;

import com.rjxx.taxeasy.dao.bo.Privileges;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Administrator on 2016/10/9.
 */
public interface PrivilegesJpaDao extends CrudRepository<Privileges, Integer> {
}
