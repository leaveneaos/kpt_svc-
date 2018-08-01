package com.rjxx.taxeasy.dal;

import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.config.rabbitmq.RabbitmqSend;
import com.rjxx.taxeasy.config.rabbitmq.RabbitmqUtils;
import com.rjxx.taxeasy.dao.bo.Cszb;
import com.rjxx.taxeasy.dao.bo.Kpls;
import com.rjxx.taxeasy.dao.orm.KplsJpaDao;
import com.rjxx.taxeasy.dao.orm.KplsMapper;
import com.rjxx.taxeasy.dao.vo.Fpcxvo;
import com.rjxx.taxeasy.dao.vo.FptjVo;
import com.rjxx.taxeasy.dao.vo.KpcxjgVo;
import com.rjxx.taxeasy.dao.vo.KplsVO3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Mon Oct 17 14:37:57 GMT+08:00 2016
 *
 * @author 许黎明
 */
@Service
public class KplsService {

    @Autowired
    private KplsJpaDao kplsJpaDao;

    @Autowired
    private KplsMapper kplsMapper;

    @Autowired
    private RabbitmqUtils rabbitmqUtils;

    @Autowired
    private RabbitmqSend rabbitmqSend;

    @Autowired
    private SkpService skpService;

    @Autowired
    private CszbService cszbService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public Kpls findOne(int id) {
        return kplsJpaDao.findOne(id);
    }

    public List<Kpls> findFpExist(Map params) {
        return kplsMapper.findFpExist(params);
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(Kpls kpls) throws Exception{
        kplsJpaDao.save(kpls);
        Cszb cszb = cszbService.getSpbmbbh(kpls.getGsdm(), kpls.getXfid(), kpls.getSkpid(), "kpfs");

        if ("04".equals(kpls.getFpztdm())) {
            if(("01").equals(cszb.getCsz())) {
                //如果状态是04，发送的mq中
                try {
                    String sksbh = skpService.findOne(kpls.getSkpid()).getSkph();
                    if(!"".equals(sksbh)&&null!=sksbh){
                        rabbitmqUtils.sendMsg(sksbh, kpls.getFpzldm(), kpls.getKplsh() + "");
                    }else{
                        rabbitmqUtils.sendMsg(skpService.findOne(kpls.getSkpid()).getId().toString(), kpls.getFpzldm(), kpls.getKplsh() + "");
                    }
                } catch (Exception e) {
                    throw new RuntimeException("发送队列失败，请联系管理员");
                }
            }else if(("03").equals(cszb.getCsz())){
                try {
                    rabbitmqSend.send(kpls.getKplsh() + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(("04").equals(cszb.getCsz())){
                /**
                 * 凯盈开票放队列
                 */
                try {
                    logger.info("KplsService.save，kpt-svc更新或保存开票流水放入凯盈队列，kplsh="+kpls.getKplsh());
                    rabbitmqSend.sendbox(kpls.getKplsh() + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(List<Kpls> kplsList) throws Exception{
        kplsJpaDao.save(kplsList);
        for (Kpls kpls : kplsList) {
            if ("04".equals(kpls.getFpztdm())) {
                //如果状态是04，发送的mq中
                try {
                    String sksbh = skpService.findOne(kpls.getSkpid()).getSkph();
                    if(!"".equals(sksbh)&&null!=sksbh){
                        rabbitmqUtils.sendMsg(sksbh, kpls.getFpzldm(), kpls.getKplsh() + "");
                    }else{
                        rabbitmqUtils.sendMsg(skpService.findOne(kpls.getSkpid()).getId().toString(), kpls.getFpzldm(), kpls.getKplsh() + "");
                    }
                } catch (Exception e) {
                    throw new RuntimeException("发送队列失败，请联系管理员");
                }
            }
        }
    }

    public Kpls findOneByParams(Map params) {
        return kplsMapper.findOneByParams(params);
    }

    public List<Fpcxvo> findAllByParams(Map params) {
        return kplsMapper.findAllByParams(params);
    }

    public List<Fpcxvo> findAllByParams2(Map params){ return kplsMapper.findAllByParams2(params);}

    public List<Fpcxvo> findFpListByParams(Map params){ return kplsMapper.findFpListByParams(params);}


    public List<Fpcxvo> findByPage(Pagination pagination) {
        return kplsMapper.findByPage(pagination);
    }

    public List<Fpcxvo> findByPage2(Map params) {
        return kplsMapper.findByPage2(params);
    }


    public Integer findTotal(Map map){return kplsMapper.findTotal(map);}

    public void update(Map params) {
        kplsMapper.update(params);
    }

    /**
     * 更新换开标志
     */
    public void updateHkbz(Map params) {
        kplsMapper.updateHkbz(params);
    }

    public List<Kpls> printSingle(Map params) {
        return kplsMapper.printSingle(params);
    }

    /**
     * 批量打印查询
     */
    public List<Fpcxvo> printmany(Map params) {
        return kplsMapper.printmany(params);
    }

    /**
     * 发票红冲数据查询
     */
    public List<Fpcxvo> findKhcfpByPage(Pagination pagination) {
        return kplsMapper.findKhcfpByPage(pagination);
    }
    /**
     * 重新生成PDF查询
     */
    public List<Fpcxvo> findPdf(Pagination pagination){
        return  kplsMapper.findPdf(pagination);
    }
    /**
     * 红冲后更新发票状态
     */
    public void updateFpczlx(Map params) {
        kplsMapper.updateFpczlx(params);
    }

    public List<Kpls> findAllByKpls(Kpls kpls) {
        return kplsMapper.findAllByKpls(kpls);
    }

    /**
     * 查询明细合计金额
     */
    public Kpls findHjje(Map params) {
        return kplsMapper.findHjje(params);
    }

    /**
     * 更新主表的合计金额
     */
    public void updateHjje(Map params) {
        kplsMapper.updateHjje(params);
    }

    /**
     * 红冲·查询相差月份
     */
    public Fpcxvo selectMonth(Map params) {
        return kplsMapper.selectMonth(params);
    }

    /**
     * 查询发票换开数据
     */
    public List<Fpcxvo> findFphkByPage(Pagination pagination) {
        return kplsMapper.findFphkByPage(pagination);
    }

    /**
     * 发票重开数据查询
     */
    public List<Fpcxvo> findFpcksqKpls(Map params) {
        return kplsMapper.findFpcksqKpls(params);
    }

    /**
     * 发票换开申请数据查询
     */
    public List<Fpcxvo> findHkfpsqByPage(Pagination pagination) {
        return kplsMapper.findHkfpsqByPage(pagination);
    }

    /**
     * 发票归档查询
     */
    public List<Fpcxvo> fpgdcxdb(Map params) {
        return kplsMapper.fpgdcxdb(params);
    }

    public List<Kpls> findKplsByDjh(Map params) {
        return kplsMapper.findKplsByDjh(params);
    }

    public List<Kpls> findKplsByPms(Map params) {
        return kplsMapper.findKplsByPms(params);
    }

    public List<Kpls> findListByPagination(Pagination pagination) {
        return kplsMapper.findListByPagination(pagination);
    }

    public List<KplsVO3> findList2ByPagination(Map map) {
        return kplsMapper.findList2ByPagination(map);
    }

    public List<Kpls> findByDjh(Kpls kpls) {
        return kplsMapper.findByDjh(kpls);
    }

    /**
     * 根据原发票号码查询原开票流水号
     */
    public Kpls findByyfphm(Kpls kpls) {
        return kplsMapper.findByyfphm(kpls);
    }


    public List<Kpls> findAll(Map params) {
        return kplsMapper.findAll(params);
    }

    public List<Kpls> findBySerialorder(Map params) {
        return kplsMapper.findBySerialorder(params);
    }


    public List<Kpls> findAllKpInfo(Map params) {
        return kplsMapper.findAllKpInfo(params);
    }

    public List<Kpls> findAllNeedRegeneratePdfKpls(Map params) {
        return kplsMapper.findAllNeedRegeneratePdfKpls(params);
    }

    /**
     * 更新调用接口返回数据
     */
    public void updateReturnMes(Map params) {
        kplsMapper.updateReturnMes(params);
    }

    /**
     * 查询需要生成pdf文件的记录
     */
    public List<Kpls> findKplsNoPdf() {
        return kplsMapper.findKplsNoPdf();
    }

    public Integer findCountByDjh(Kpls kpls) {
        return kplsMapper.findCountByDjh(kpls);
    }

    public List<Fpcxvo> findKhcfpByPage1(Pagination pagination) {
        // TODO Auto-generated method stub
        return kplsMapper.findKhcfpByPage1(pagination);
    }

    public List<Fpcxvo> findKzffpByPage(Pagination pagination) {
        // TODO Auto-generated method stub
        return kplsMapper.findKzffpByPage(pagination);
    }

    public List<Fpcxvo> findKzffpByPage1(Pagination pagination) {
        // TODO Auto-generated method stub
        return kplsMapper.findKzffpByPage1(pagination);
    }

    /**
     * 获取发票待办统计结果
     *
     * @param kpdid
     * @return
     */
    public List<FptjVo> findFpdbtjjgByKpdid(int kpdid) {
        return kplsMapper.findFpdbtjjgByKpdid(kpdid);
    }

    public void deleteAll(List<Kpls> list) {
        kplsJpaDao.delete(list);
    }

    public void delete(Kpls kpls) {
        kplsJpaDao.delete(kpls.getKplsh());
    }

    /**
     * 发票重打数据查询
     */
    public List<Fpcxvo> findKcdfpByPage(Pagination pagination) {
        // TODO Auto-generated method stub
        return kplsMapper.findKcdfpByPage(pagination);
    }

    /**
     * 发票重打数据查询1
     */
    public List<Fpcxvo> findKcdfpByPage1(Pagination pagination) {
        // TODO Auto-generated method stub
        return kplsMapper.findKcdfpByPage1(pagination);
    }

    public Fpcxvo findykpCount(Map map) {
        return kplsMapper.findykpCount(map);

    }

    /**
     * 根据map参数返回List<Kpls>
     *
     * @param map
     * @return
     */
    public List<Kpls> findAllByMapParams(Map map) {
        return kplsMapper.findAllByMapParams(map);
    }

    public List<Kpls> findDingdingTsInfo(Map map) {
        return kplsMapper.findDingdingTsInfo(map);
    }

    /**
     * 根据销方id获取省份名称
     *
     * @param xfid
     * @return
     */
    public String findSfmcByXfid(int xfid) {
        return kplsMapper.findSfmcByXfid(xfid);
    }

    /**
     * 查询开票结果
     *
     * @param params
     * @return
     */
    public List<KpcxjgVo> findAllKpjgByMap(Map params) {
        return kplsMapper.findAllKpjgByMap(params);
    }


    public Kpls findByfphm(Kpls parms) {
        return kplsMapper.findByfphm(parms);
    }

    public Kpls findByhzfphm(Kpls kpls) {
        return kplsMapper.findByhzfphm(kpls);
    }

    public List<Kpls> findKplsBySerialOrder(Map params) {
        return kplsMapper.findKplsBySerialOrder(params);
    }

    public List<Kpls> findFphc(Map parms) {
        return kplsMapper.findFphc(parms);
    }
}

