package com.rjxx.taxeasy.dal;

import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Group;
import com.rjxx.taxeasy.dao.bo.Xf;
import com.rjxx.taxeasy.dao.orm.XfJpaDao;
import com.rjxx.taxeasy.dao.orm.XfMapper;
import com.rjxx.taxeasy.dao.vo.XfKzVo;
import com.rjxx.taxeasy.dao.vo.XfVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/9.
 * @author 许黎明
 */
@Service
public class XfService {

    @Autowired
    private XfJpaDao xfJpaDao;

    @Autowired
    private XfMapper xfMapper;

    @Autowired
    private GroupService groupService;

    @Autowired
    private SkpService skpService;

    public Xf findOne(Integer id) {
        return xfJpaDao.findOne(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveNew(Xf xf) {

        /**
         * 保存销方
         */
        xfJpaDao.save(xf);
        /**
         * 保存默认税控盘
         */
        Group g = new Group();
        g.setYhid(xf.getLrry());
        g.setXfid(xf.getId());
        g.setYxbz("1");
        g.setLrry(xf.getLrry());
        g.setXgry(xf.getLrry());
        g.setLrsj(new Date());
        g.setXgsj(new Date());
        groupService.save(g);
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(Iterable<Xf> xfList) {
        xfJpaDao.save(xfList);
        Group g = null;
        for (Xf xf : xfList) {
            g = new Group();
            g.setYhid(xf.getLrry());
            g.setXfid(xf.getId());
            g.setYxbz("1");
            g.setLrry(xf.getLrry());
            g.setXgry(xf.getLrry());
            g.setLrsj(new Date());
            g.setXgsj(new Date());
            groupService.save(g);
        }
    }

    public void update(Xf xf) {
        xfJpaDao.save(xf);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Xf xf) {
        xfJpaDao.save(xf);
        Group g = new Group();
        g.setXfid(xf.getId());
        List<Group> list = groupService.findAllByParams(g);
        if (!list.isEmpty()) {
            for (Group group : list) {
                group.setYxbz("0");
                g.setXgry(xf.getLrry());
                g.setXgsj(new Date());
                groupService.save(group);
            }
        }
    }

    /**
     * 根据yhid查找该用户下的所有管理的销方
     *
     * @param yhId
     * @return
     */
    public List<Xf> getXfListByYhId(int yhId) {
        return xfMapper.getXfListByYhId(yhId);
    }

    public Xf findOneByParams(Xf xf) {
        return xfMapper.findOneByParams(xf);
    }

    public List<Xf> findAllByParams(Xf xf) {
        return xfMapper.findAllByParams(xf);
    }

    /**
     * 根据销方id获取销方信息
     *
     * @param map
     * @return
     */
    public List<Xf> findAllByMap(Map<String, Object> map) {
        return xfMapper.findAllByMap(map);
    }

    public List<Xf> findByPage(Pagination pagination) {
        return xfMapper.findByPage(pagination);
    }

    public List<XfVo> findByPages(Pagination pagination) {
        return xfMapper.findByPages(pagination);
    }

	public XfVo findAllByXfxx(Map map) {
		return xfMapper.findAllByXfxx(map);
	}

    public List<XfKzVo> findXfkzListByXfid(Map<String, Object> map) {
        return xfMapper.findXfkzListByXfid(map);
    }


}
