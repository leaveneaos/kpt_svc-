package com.rjxx.taxeasy.dal;

import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Group;
import com.rjxx.taxeasy.dao.orm.GroupJpaDao;
import com.rjxx.taxeasy.dao.orm.GroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Thu Oct 13 14:35:28 GMT+08:00 2016
 *
 * @author 许黎明
 */ 
@Service
public class GroupService {

    @Autowired
    private GroupJpaDao groupJpaDao;

    @Autowired
    private GroupMapper groupMapper;

    public Group findOne(int id) {
        return groupJpaDao.findOne(id);
    }

    public void save(Group group) {
        groupJpaDao.save(group);
    }

    public void save(List<Group> groupList) {
        groupJpaDao.save(groupList);
    }

    public void delete(List<Group> groupList) {
    	for (Group group : groupList) {
			group.setYxbz("0");
		}
        groupJpaDao.save(groupList);
    }

    public Group findOneByParams(Group group) {
        return groupMapper.findOneByParams(group);
    }

    public List<Group> findAllByParams(Group group) {
        return groupMapper.findAllByParams(group);
    }

    public List<Group> findByPage(Pagination pagination) {
        return groupMapper.findByPage(pagination);
    }

}

