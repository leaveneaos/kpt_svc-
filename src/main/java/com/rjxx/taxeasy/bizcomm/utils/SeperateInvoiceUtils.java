package com.rjxx.taxeasy.bizcomm.utils;


import com.rjxx.taxeasy.domains.Kpls;
import com.rjxx.taxeasy.domains.Kpspmx;
import com.rjxx.taxeasy.vo.JyspmxDecimal;
import com.rjxx.taxeasy.vo.JyspmxDecimal2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/11.
 */
public class SeperateInvoiceUtils {

    // 默认除法运算精度
    private static final Integer DEF_DIV_SCALE = 30;

    public static final int detailsNumber = 100;

    /**
     * 重新对kpspmx的金额进行价税分离，并保留6位小数
     *
     * @param kpspmxList
     * @return
     * @throws Exception
     */
    public static List<Kpspmx> repeatSeparatePrice(Kpls kpls, List<Kpspmx> kpspmxList) throws Exception {
        List<Kpspmx> finalList = new ArrayList<>(kpspmxList.size());
        for (int i = 0; i < kpspmxList.size(); i++) {
            Kpspmx mx = kpspmxList.get(i);
            BigDecimal jshj = new BigDecimal(mx.getSpje() + mx.getSpse());
            jshj = jshj.setScale(2,BigDecimal.ROUND_HALF_UP);
            if(!kpls.getZsfs().equals("2")){
                BigDecimal spsl = new BigDecimal(mx.getSpsl());
                spsl = spsl.setScale(2,BigDecimal.ROUND_HALF_UP);
                BigDecimal jeWithoutTax = div(jshj, spsl.add(new BigDecimal(1))).setScale(6, BigDecimal.ROUND_HALF_UP);
                mx.setSpje(jeWithoutTax.doubleValue());// 商品金额不含税
            }
            if(mx.getFphxz().equals("1")){
                mx.setSpdw(null);
                mx.setSpggxh(null);
            }
            finalList.add(mx);
        }
        return finalList;
    }

    /*
     * 价税分离
     */
    public static List<JyspmxDecimal> separatePrice(List<JyspmxDecimal> jyspmxs) throws Exception {
        List<JyspmxDecimal> sepJyspmxs = new ArrayList<JyspmxDecimal>();// 价税分离后的list
        for (int i = 0; i < jyspmxs.size(); i++) {
            JyspmxDecimal mx = jyspmxs.get(i);
            BigDecimal jshj = mx.getJshj();
//            BigDecimal spje = mx.getSpje();
            BigDecimal spsl = mx.getSpsl();
            BigDecimal spdj = mx.getSpdj();
            BigDecimal jeWithoutTax = div(jshj, spsl.add(new BigDecimal(1))).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal jeTax = sub(jshj, jeWithoutTax);
            // 判断单价是否为空！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！todo
            //Double djWithoutTax = div(spdj, 1 + spsl, 6);
            BigDecimal djWithoutTax;
            if (spdj == null) {
                djWithoutTax = null;// 单价不含税
            } else {
                djWithoutTax = div(spdj, spsl.add(new BigDecimal(1)));
            }
            mx.setSpje(jeWithoutTax);// 商品金额不含税
            mx.setSpse(jeTax);// 税额
//            mx.setJshj(spje);// 价税合计
            mx.setSpdj(djWithoutTax);// 单价不含税
            sepJyspmxs.add(mx);
        }
        return sepJyspmxs;
    }

    /**
     * 价税分离
     * @param jyspmxs
     * @return
     * @throws Exception
     */
    public static List<JyspmxDecimal2> separatePrice2(List<JyspmxDecimal2> jyspmxs) throws Exception {
        List<JyspmxDecimal2> sepJyspmxs = new ArrayList<JyspmxDecimal2>();// 价税分离后的list
        for (int i = 0; i < jyspmxs.size(); i++) {
            JyspmxDecimal2 mx = jyspmxs.get(i);
            BigDecimal jshj = mx.getJshj();
//            BigDecimal spje = mx.getSpje();
            BigDecimal spsl = mx.getSpsl();
            BigDecimal spdj = mx.getSpdj();
            BigDecimal jeWithoutTax;
            BigDecimal jeTax;
            BigDecimal kce = mx.getKce() == null?BigDecimal.ZERO:mx.getKce();
            if(kce.compareTo(BigDecimal.ZERO)>0){
                 jeTax =  mul(div(sub(jshj,mx.getKce()), spsl.add(new BigDecimal(1))),spsl).setScale(2, BigDecimal.ROUND_HALF_UP).compareTo(BigDecimal.ZERO)<0?
                        BigDecimal.ZERO:mul(div(sub(jshj,mx.getKce()), spsl.add(new BigDecimal(1))),spsl).setScale(2, BigDecimal.ROUND_HALF_UP);

                jeWithoutTax =  sub(jshj, jeTax);
            }else{
                jeWithoutTax = div(jshj, spsl.add(new BigDecimal(1))).setScale(2, BigDecimal.ROUND_HALF_UP);
                jeTax = sub(jshj, jeWithoutTax);
            }

            // 判断单价是否为空！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！todo
            //Double djWithoutTax = div(spdj, 1 + spsl, 6);
            BigDecimal djWithoutTax;
            if (spdj == null) {
                djWithoutTax = null;// 单价不含税
            } else {
                //djWithoutTax = div(spdj, spsl.add(new BigDecimal(1)));
                djWithoutTax = div(jeWithoutTax,mx.getSps());
            }
            mx.setSpje(jeWithoutTax);// 商品金额不含税
            mx.setSpse(jeTax);// 税额
//            mx.setJshj(spje);// 价税合计
            mx.setSpdj(djWithoutTax);// 单价不含税
            sepJyspmxs.add(mx);
        }
        return sepJyspmxs;
    }

    /**
     * 提供精确的加法运算。
     *
     * @param value1 被加数
     * @param value2 加数
     * @return 两个参数的和
     */
    public static BigDecimal add(BigDecimal value1, BigDecimal value2) {
        if (value1 == null) {
            return null;
        }
        if (value2 == null) {
            return null;
        }
        return value1.add(value2);
    }

    /**
     * 提供精确的减法运算。
     *
     * @param value1 被减数
     * @param value2 减数
     * @return 两个参数的差
     */
    public static BigDecimal sub(BigDecimal value1, BigDecimal value2) {
        if (value1 == null) {
            return null;
        }
        if (value2 == null) {
            return null;
        }
        return value1.subtract(value2);
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal mul(BigDecimal value1, BigDecimal value2) {
        if (value1 == null) {
            return null;
        }
        if (value2 == null) {
            return null;
        }
        return value1.multiply(value2);
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时， 精确到小数点以后指定位，以后的数字四舍五入。
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @return 两个参数的商
     */
    public static BigDecimal div(BigDecimal dividend, BigDecimal divisor) {
        if (dividend == null) {
            return null;
        }
        if (divisor == null || divisor.doubleValue() == 0) {
            return null;
        }
        return div(dividend, divisor, DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。 当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @param scale    表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static BigDecimal div(BigDecimal dividend, BigDecimal divisor, Integer scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        if (dividend == null) {
            return null;
        }
        if (divisor == null || divisor.doubleValue() == 0) {
            return null;
        }
        return dividend.divide(divisor, scale, BigDecimal.ROUND_HALF_UP);
    }

    public static void main(String[] args) throws Exception {
        List<JyspmxDecimal2> list = new ArrayList<>();
        JyspmxDecimal2 jymx = new JyspmxDecimal2();
        jymx.setJshj(new BigDecimal(23456));
        jymx.setSpsl(new BigDecimal(0.03));
        jymx.setSpje(new BigDecimal(22772.82));
        jymx.setSpse(new BigDecimal(683.18));
        jymx.setSqlsh(1);
        jymx.setSpmxxh(1);
        for (int i = 0; i < 10; i++) {
            list.add(jymx);
            jymx.setSpmxxh(i + 2);
        }
        System.out.println("2312");
        List<JyspmxDecimal2> res = splitInvoicesbhs(list, new BigDecimal(9999.99), new BigDecimal(10000), 8, false,false);
        for (JyspmxDecimal2 jyspmx : res) {
            System.out.println(jyspmx.getJshj() + "\t" + jyspmx.getFpnum() + "\t" + jyspmx.getSpje() + "\t" + jyspmx.getSpse() + "\t" + jyspmx.getSpsl() + "\t");
        }

    }


    //不含税拆分方法

    public static List<JyspmxDecimal2> splitInvoicesbhs(List<JyspmxDecimal2> jyspmxs, BigDecimal maxje, BigDecimal fpje, int mxsl, boolean qzfp,boolean spzsfp) throws Exception {
        int mxnum = detailsNumber;
        if (mxsl != 0 && mxsl <= detailsNumber) {
            mxnum = mxsl;
        }else if(mxsl != 0 && mxsl > detailsNumber){
            mxnum = mxsl;
        }
        boolean fp = false;
        List<JyspmxDecimal2> tempJyspmxs = new ArrayList<JyspmxDecimal2>();// 缓存商品明细表
        List<JyspmxDecimal2> splitKpspmxs = new ArrayList<JyspmxDecimal2>();// 拆分发票后的list
        BigDecimal zje = new BigDecimal(0);// 汇总金额不含税
        BigDecimal zje1 = new BigDecimal(0);// 汇总金额含税
        BigDecimal total = new BigDecimal(0);//商品明细金额总和（不含税）
        for (JyspmxDecimal2 jyspmx : jyspmxs) {
            total = total.add(jyspmx.getSpje());
        }
        if (!qzfp) { //如果不是强制分票
            if (maxje.compareTo(total) > 0) { //开票限额（不含税）是否大于商品明细总和（不含税）
                fp = true;
            }
        }
        int fpnum = 1;
        int sqlsh;
        int spmxxh;
        String fphxz;
        String spdm;
        for (int i = 0; i < jyspmxs.size(); i++) {
            JyspmxDecimal2 jyspmx = jyspmxs.get(i);
            sqlsh = jyspmx.getsqlsh();
            fphxz = jyspmx.getFphxz();
            spmxxh = jyspmx.getSpmxxh();
            tempJyspmxs.add(jyspmx);
            zje = zje.add(jyspmx.getSpje());//不含税商品总金额
            zje1 = zje1.add(jyspmx.getJshj());//含税商品总金额
            spdm = jyspmx.getSpdm();
            if (zje.compareTo(maxje) >= 0 || tempJyspmxs.size() == mxnum || (zje1.compareTo(fpje) >= 0 && qzfp)) {
                if (tempJyspmxs.size() == mxnum && zje.compareTo(maxje) <= 0 && zje1.compareTo(fpje) <= 0) {
                    //达到每张发票开具最大条数，并且总金额未超出上限。
                    jyspmx.setFpnum(fpnum);
                    splitKpspmxs.add(jyspmx);
                    tempJyspmxs.clear();
                    fpnum++;
                    zje = BigDecimal.ZERO;
                    zje1 = BigDecimal.ZERO;
                } else if (fpje.divide(new BigDecimal(1).add(jyspmx.getSpsl()), 30, BigDecimal.ROUND_HALF_UP).compareTo(maxje) > 0) {
                    /**
                     * 当分票金额(不含税)大于开票限额时，按照开票限额来分票
                     */
                    // Jyspmx ccjyspmx = new Jyspmx();//超出金额对象
                    JyspmxDecimal2 cfjyspmx = new JyspmxDecimal2();// 拆分金额对象
                    // ccjyspmx = jyspmx;//超出金额对象
                    // cfjyspmx = jyspmx;//拆分金额对象
                    // 商品名称
                    String spmc = jyspmx.getSpmc();
                    // 规格型号
                    String spggxh = jyspmx.getSpggxh();
                    // 单位
                    String spdw = jyspmx.getSpdw();
                    // 单价
                    BigDecimal spdj = jyspmx.getSpdj();
                    // 税率
                    BigDecimal spsl = jyspmx.getSpsl();
                    BigDecimal spje = jyspmx.getSpje();// 原商品金额
                    BigDecimal yjshj = jyspmx.getJshj();// 原商品金额
                    BigDecimal spsm = jyspmx.getSps();// 原商品数量
                    BigDecimal spse = jyspmx.getSpse();// 原商品税额
                    BigDecimal ccje = sub(zje, maxje);// 超出金额

                    BigDecimal cfje = sub(spje, ccje);// 拆分金额
                    /**
                     * 是否按商品整数分票
                     */
                    BigDecimal cfsm;
                    BigDecimal cfse;
                    BigDecimal jshj;
                    BigDecimal ccjshj;
                    if(spzsfp&&null!=spdj&&!"".equals(spdj)&&null!=spsm&&!"".equals(spsm)){
                        cfsm = div(spsm, div(spje, cfje));// 拆分数量

                        BigDecimal cfsm1=cfsm;//过度数据，计算下一次数据。

                        cfsm=new BigDecimal(Math.floor(cfsm.doubleValue()));//向下取整
                        if(cfsm.compareTo(new  BigDecimal(0))!=0){
                            BigDecimal cfje1=mul(spdj,sub(cfsm1,cfsm));//多出数量的金额(不含税)

                            cfje=sub(cfje,cfje1);

                            cfse = div(spse, div(spje, cfje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额

                            jshj = add(cfje, cfse);

                            ccjshj = sub(jyspmx.getJshj(), jshj);// 超出价税合计

                            ccje=add(ccje,cfje1);
                        }else{
                            cfsm = div(spsm, div(spje, cfje));// 拆分数量
                            cfse = div(spse, div(spje, cfje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                            jshj = add(cfje, cfse);
                            ccjshj = sub(jyspmx.getJshj(), jshj);// 超出价税合计
                        }
                    }else{
                         cfsm = div(spsm, div(spje, cfje));// 拆分数量
                         cfse = div(spse, div(spje, cfje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                         jshj = add(cfje, cfse);
                         ccjshj = sub(jyspmx.getJshj(), jshj);// 超出价税合计
                    }

                    cfjyspmx.setFphxz(fphxz);
                    cfjyspmx.setSqlsh(sqlsh);
                    cfjyspmx.setSpmxxh(spmxxh);
                    cfjyspmx.setSpje(cfje);
                    cfjyspmx.setSps(cfsm);
                    cfjyspmx.setSpse(cfse);
                    cfjyspmx.setFpnum(fpnum);
                    cfjyspmx.setSpmc(spmc);
                    cfjyspmx.setYhzcbs(jyspmx.getYhzcbs());
                    cfjyspmx.setYhzcmc(jyspmx.getYhzcmc());
                    cfjyspmx.setKce(jyspmx.getKce());
                    cfjyspmx.setLslbz(jyspmx.getLslbz());
                    cfjyspmx.setSpggxh(spggxh);
                    cfjyspmx.setSpdw(spdw);
                    cfjyspmx.setSpdj(spdj);
                    cfjyspmx.setSpsl(spsl);
                    cfjyspmx.setJshj(jshj);
                    cfjyspmx.setYkphj(new BigDecimal(0));
                    cfjyspmx.setSpdm(spdm);
                    cfjyspmx.setGsdm(jyspmx.getGsdm());
                    splitKpspmxs.add(cfjyspmx);
                    if (fpje.divide(new BigDecimal(1).add(jyspmx.getSpsl()), 30, BigDecimal.ROUND_HALF_UP).compareTo(maxje) > 0) {
                        // Jyspmx ccjyspmx = new Jyspmx();//超出金额对象){
                        int n = (int) Math.floor(div(ccje, maxje).doubleValue());
                        BigDecimal cfsm1 = new BigDecimal(0.00);
                        BigDecimal cfse1 = new BigDecimal(0.00);
                        if (n > 0) {

                            /**
                             * 按商品整数来分票
                             */
                            if(spzsfp&&null!=spdj&&!"".equals(spdj)&&null!=spsm&&!"".equals(spsm)){

                                cfsm1 = div(spsm, div(spje, maxje));// 拆分数量

                                BigDecimal cfsm2=cfsm1;//过度数据，计算下一次数据。

                                cfsm1=new BigDecimal(Math.floor(cfsm1.doubleValue()));//向下取整
                                if(cfsm1.compareTo(new BigDecimal(0))!=0){
                                    BigDecimal cfje1=mul(spdj,sub(cfsm2,cfsm1));//多出数量的金额(不含税)

                                    maxje=sub(maxje,cfje1);

                                    cfse1 = div(spse, div(spje, maxje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                                }else{
                                    cfsm1 = div(spsm, div(spje, maxje));// 拆分数量
                                    cfse1 = div(spse, div(spje, maxje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                                }
                            }else{
                                cfsm1 = div(spsm, div(spje, maxje));// 拆分数量
                                cfse1 = div(spse, div(spje, maxje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                            }
                            for (int j = 0; j < n; j++) {
                                JyspmxDecimal2 ccjyspmx1 = new JyspmxDecimal2();
                                // ccjyspmx1 = ccjyspmx;
                                fpnum++;
                                BigDecimal jshj1 = add(maxje, cfse1);
                                ccjyspmx1.setFphxz(fphxz);
                                ccjyspmx1.setSqlsh(sqlsh);
                                ccjyspmx1.setSpmxxh(spmxxh);
                                ccjyspmx1.setSpje(maxje);
                                ccjyspmx1.setSps(cfsm1);
                                ccjyspmx1.setSpse(cfse1);
                                ccjyspmx1.setFpnum(fpnum);
                                ccjyspmx1.setYhzcbs(jyspmx.getYhzcbs());
                                ccjyspmx1.setYhzcmc(jyspmx.getYhzcmc());
                                ccjyspmx1.setKce(jyspmx.getKce());
                                ccjyspmx1.setLslbz(jyspmx.getLslbz());
                                ccjyspmx1.setSpmc(spmc);
                                ccjyspmx1.setSpggxh(spggxh);
                                ccjyspmx1.setSpdw(spdw);
                                ccjyspmx1.setSpdj(spdj);
                                ccjyspmx1.setSpsl(spsl);
                                ccjyspmx1.setJshj(jshj1);
                                ccjyspmx1.setYkphj(new BigDecimal(0));
                                ccjyspmx1.setSpdm(spdm);
                                ccjyspmx1.setGsdm(jyspmx.getGsdm());
                                splitKpspmxs.add(ccjyspmx1);
                            }
                        }
                        ccje = sub(ccje, mul(new BigDecimal(n), maxje));
                        JyspmxDecimal2 ccjyspmx2 = new JyspmxDecimal2();
                        ccjyspmx2.setFphxz(fphxz);
                        ccjyspmx2.setSqlsh(sqlsh);
                        ccjyspmx2.setSpmxxh(spmxxh);
                        ccjyspmx2.setSpje(ccje);
                        ccjyspmx2.setSpmc(spmc);
                        ccjyspmx2.setSpggxh(spggxh);
                        ccjyspmx2.setSpdj(spdj);
                        ccjyspmx2.setYhzcbs(jyspmx.getYhzcbs());
                        ccjyspmx2.setYhzcmc(jyspmx.getYhzcmc());
                        ccjyspmx2.setKce(jyspmx.getKce());
                        ccjyspmx2.setLslbz(jyspmx.getLslbz());
                        ccjyspmx2.setSpsl(spsl);
                        ccjyspmx2.setSpdw(spdw);
                        ccjyspmx2.setSps(sub(sub(spsm, cfsm), mul(new BigDecimal(n), cfsm1)));
                        ccjyspmx2.setSpse(sub(sub(spse, cfse), mul(new BigDecimal(n), cfse1)));
                        ccjyspmx2.setJshj(add(ccjyspmx2.getSpje(), ccjyspmx2.getSpse()));
                        ccjyspmx2.setYkphj(new BigDecimal(0));
                        ccjyspmx2.setSpdm(spdm);
                        ccjyspmx2.setGsdm(jyspmx.getGsdm());
                        fpnum++;
                        ccjyspmx2.setFpnum(fpnum);
                        tempJyspmxs.clear();
                        if (ccje.doubleValue() != 0) {
                            splitKpspmxs.add(ccjyspmx2);
                            tempJyspmxs.add(ccjyspmx2);
                        }
                        zje = ccje;
                        zje1 = ccjyspmx2.getJshj();
                    } else {
                        //按照含税金额拆分
                        int n = (int) Math.floor(div(ccjshj, fpje).doubleValue());
                        BigDecimal cfsm1 = new BigDecimal(0.00);
                        BigDecimal cfse1 = new BigDecimal(0.00);
                        if (n > 0) {

                            /**
                             * 按商品整数来分票
                             */
                            if(spzsfp&&null!=spdj&&!"".equals(spdj)&&null!=spsm&&!"".equals(spsm)){

                                cfsm1 = div(spsm, div(yjshj, fpje));// 拆分数量

                                BigDecimal cfsm2=cfsm1;//过度数据，计算下一次数据。

                                cfsm1=new BigDecimal(Math.floor(cfsm1.doubleValue()));//向下取整
                                if(cfsm1.compareTo(new BigDecimal(0))!=0){
                                    BigDecimal cfjshj1=mul(mul(spdj,new BigDecimal(1).add(jyspmx.getSpsl())),sub(cfsm2,cfsm1));//多出数量的价税合计
                                    fpje=sub(fpje,cfjshj1);
                                    cfse1 = div(spse, div(yjshj, fpje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                                }else{
                                    cfsm1 = div(spsm, div(yjshj, fpje));// 拆分数量
                                    cfse1 = div(spse, div(yjshj, fpje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                                }
                            }else{
                                cfsm1 = div(spsm, div(yjshj, fpje));// 拆分数量
                                cfse1 = div(spse, div(yjshj, fpje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                            }


                            for (int j = 0; j < n; j++) {
                                JyspmxDecimal2 ccjyspmx1 = new JyspmxDecimal2();
                                // ccjyspmx1 = ccjyspmx;
                                fpnum++;
                                BigDecimal jshj1 = fpje;
                                ccjyspmx1.setFphxz(fphxz);
                                ccjyspmx1.setSqlsh(sqlsh);
                                ccjyspmx1.setSpmxxh(spmxxh);
                                ccjyspmx1.setSpje(sub(fpje, cfse1));
                                ccjyspmx1.setSps(cfsm1);
                                ccjyspmx1.setSpse(cfse1);
                                ccjyspmx1.setFpnum(fpnum);
                                ccjyspmx1.setSpmc(spmc);
                                ccjyspmx1.setSpggxh(spggxh);
                                ccjyspmx1.setSpdw(spdw);
                                ccjyspmx1.setYhzcbs(jyspmx.getYhzcbs());
                                ccjyspmx1.setYhzcmc(jyspmx.getYhzcmc());
                                ccjyspmx1.setKce(jyspmx.getKce());
                                ccjyspmx1.setLslbz(jyspmx.getLslbz());
                                ccjyspmx1.setSpdj(spdj);
                                ccjyspmx1.setSpsl(spsl);
                                ccjyspmx1.setJshj(jshj1);
                                ccjyspmx1.setYkphj(new BigDecimal(0));
                                ccjyspmx1.setSpdm(spdm);
                                ccjyspmx1.setGsdm(jyspmx.getGsdm());
                                splitKpspmxs.add(ccjyspmx1);
                            }
                        }
                        ccjshj = sub(ccjshj, mul(new BigDecimal(n), fpje));
                        JyspmxDecimal2 ccjyspmx2 = new JyspmxDecimal2();
                        ccjyspmx2.setFphxz(fphxz);
                        ccjyspmx2.setSqlsh(sqlsh);
                        ccjyspmx2.setSpmxxh(spmxxh);
                        ccjyspmx2.setSpmc(spmc);
                        ccjyspmx2.setSpggxh(spggxh);
                        ccjyspmx2.setSpdj(spdj);
                        ccjyspmx2.setSpsl(spsl);
                        ccjyspmx2.setYhzcbs(jyspmx.getYhzcbs());
                        ccjyspmx2.setYhzcmc(jyspmx.getYhzcmc());
                        ccjyspmx2.setKce(jyspmx.getKce());
                        ccjyspmx2.setLslbz(jyspmx.getLslbz());
                        ccjyspmx2.setSpdw(spdw);
                        ccjyspmx2.setSps(sub(sub(spsm, cfsm), mul(new BigDecimal(n), cfsm1)));
                        ccjyspmx2.setSpse(sub(sub(spse, cfse), mul(new BigDecimal(n), cfse1)));
                        ccjyspmx2.setSpje(sub(ccjshj, ccjyspmx2.getSpse()));
                        ccjyspmx2.setJshj(ccjshj);
                        ccjyspmx2.setYkphj(new BigDecimal(0));
                        ccjyspmx2.setSpdm(spdm);
                        ccjyspmx2.setGsdm(jyspmx.getGsdm());
                        fpnum++;
                        ccjyspmx2.setFpnum(fpnum);
                        tempJyspmxs.clear();
                        if (ccjshj.doubleValue() != 0) {
                            splitKpspmxs.add(ccjyspmx2);
                            tempJyspmxs.add(ccjyspmx2);
                        }
                        zje = ccjyspmx2.getSpje();
                        zje1 = ccjshj;
                    }
                } else {
                    /**
                     * 分票金额不大于开票限额
                     */
                    if (fp) {
                        /**
                         * 开票限额大于商品明细金额（不含税）总和时，按照开票限额分票
                         */
                        // Jyspmx ccjyspmx = new Jyspmx();//超出金额对象
                        JyspmxDecimal2 cfjyspmx = new JyspmxDecimal2();// 拆分金额对象
                        // ccjyspmx = jyspmx;//超出金额对象
                        // cfjyspmx = jyspmx;//拆分金额对象
                        // 商品名称
                        String spmc = jyspmx.getSpmc();
                        // 规格型号
                        String spggxh = jyspmx.getSpggxh();
                        // 单位
                        String spdw = jyspmx.getSpdw();
                        // 单价
                        BigDecimal spdj = jyspmx.getSpdj();
                        // 税率
                        BigDecimal spsl = jyspmx.getSpsl();
                        BigDecimal spje = jyspmx.getSpje();// 原商品金额
                        BigDecimal yjshj = jyspmx.getJshj();// 原商品金额
                        BigDecimal spsm = jyspmx.getSps();// 原商品数量
                        BigDecimal spse = jyspmx.getSpse();// 原商品税额

                        BigDecimal ccje = sub(zje, maxje);// 超出金额

                        BigDecimal cfje = sub(spje, ccje);// 拆分金额
                        /**
                         * //是否按商品整数分票
                         */
                        BigDecimal cfsm;
                        BigDecimal cfse;
                        BigDecimal jshj;
                        if(spzsfp&&null!=spdj&&!"".equals(spdj)&&null!=spsm&&!"".equals(spsm)){
                            cfsm = div(spsm, div(spje, cfje));// 拆分数量
                            BigDecimal cfsm1=cfsm;//过度数据，计算下一次数据。

                            cfsm=new BigDecimal(Math.floor(cfsm.doubleValue()));//向下取整
                            if(cfsm.compareTo(new BigDecimal(0))!=0){
                                BigDecimal cfje1=mul(spdj,sub(cfsm1,cfsm));//多出数量的金额(不含税)
                                cfje=sub(cfje,cfje1);
                                cfse = div(spse, div(spje, cfje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                                jshj = add(cfje, cfse);
                                BigDecimal ccjshj = sub(jyspmx.getJshj(), jshj);// 超出价税合计
                                ccje=add(ccje,cfje1);
                            }else{
                                cfsm = div(spsm, div(spje, cfje));// 拆分数量
                                cfse = div(spse, div(spje, cfje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                                jshj = add(cfje, cfse);
                                BigDecimal ccjshj = sub(jyspmx.getJshj(), jshj);// 超出价税合计
                            }
                        }else{
                             cfsm = div(spsm, div(spje, cfje));// 拆分数量
                             cfse = div(spse, div(spje, cfje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                             jshj = add(cfje, cfse);
                             BigDecimal ccjshj = sub(jyspmx.getJshj(), jshj);// 超出价税合计
                        }
                        cfjyspmx.setFphxz(fphxz);
                        cfjyspmx.setSqlsh(sqlsh);
                        cfjyspmx.setSpmxxh(spmxxh);
                        cfjyspmx.setSpje(cfje);
                        cfjyspmx.setSps(cfsm);
                        cfjyspmx.setSpse(cfse);
                        cfjyspmx.setFpnum(fpnum);
                        cfjyspmx.setSpmc(spmc);
                        cfjyspmx.setSpggxh(spggxh);
                        cfjyspmx.setYhzcbs(jyspmx.getYhzcbs());
                        cfjyspmx.setYhzcmc(jyspmx.getYhzcmc());
                        cfjyspmx.setKce(jyspmx.getKce());
                        cfjyspmx.setLslbz(jyspmx.getLslbz());
                        cfjyspmx.setSpdw(spdw);
                        cfjyspmx.setSpdj(spdj);
                        cfjyspmx.setSpsl(spsl);
                        cfjyspmx.setJshj(jshj);
                        cfjyspmx.setYkphj(new BigDecimal(0));
                        cfjyspmx.setSpdm(spdm);
                        cfjyspmx.setGsdm(jyspmx.getGsdm());
                        splitKpspmxs.add(cfjyspmx);
                        // Jyspmx ccjyspmx = new Jyspmx();//超出金额对象){
                        int n = (int) Math.floor(div(ccje, maxje).doubleValue());
                        BigDecimal cfsm1 = new BigDecimal(0.00);
                        BigDecimal cfse1 = new BigDecimal(0.00);
                        if (n > 0) {
                            /**
                             * 是否按整数开票
                             */
                            if(spzsfp&&null!=spdj&&!"".equals(spdj)&&null!=spsm&&!"".equals(spsm)){
                                cfsm1 = div(spsm, div(spje, maxje));// 拆分数量

                                BigDecimal cfsm2=cfsm1;//过度数据，计算下一次数据。

                                cfsm1=new BigDecimal(Math.floor(cfsm1.doubleValue()));//向下取整
                                if(cfsm1.compareTo(new BigDecimal(0))!=0){
                                    BigDecimal cfje1=mul(spdj,sub(cfsm2,cfsm1));//多出数量的金额(不含税)

                                    maxje=sub(maxje,cfje1);

                                    cfse1 = div(spse, div(spje, maxje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额

                                    //ccje=add(ccje,cfje1);
                                }else{
                                    cfsm1 = div(spsm, div(spje, maxje));// 拆分数量
                                    cfse1 = div(spse, div(spje, maxje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                                }
                            }else{
                                cfsm1 = div(spsm, div(spje, maxje));// 拆分数量
                                cfse1 = div(spse, div(spje, maxje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                            }
                            for (int j = 0; j < n; j++) {
                                JyspmxDecimal2 ccjyspmx1 = new JyspmxDecimal2();
                                // ccjyspmx1 = ccjyspmx;
                                fpnum++;
                                BigDecimal jshj1 = add(maxje, cfse1);
                                ccjyspmx1.setFphxz(fphxz);
                                ccjyspmx1.setSqlsh(sqlsh);
                                ccjyspmx1.setSpmxxh(spmxxh);
                                ccjyspmx1.setSpje(maxje);
                                ccjyspmx1.setSps(cfsm1);
                                ccjyspmx1.setSpse(cfse1);
                                ccjyspmx1.setFpnum(fpnum);
                                ccjyspmx1.setSpmc(spmc);
                                ccjyspmx1.setSpggxh(spggxh);
                                ccjyspmx1.setSpdw(spdw);
                                ccjyspmx1.setYhzcbs(jyspmx.getYhzcbs());
                                ccjyspmx1.setYhzcmc(jyspmx.getYhzcmc());
                                ccjyspmx1.setKce(jyspmx.getKce());
                                ccjyspmx1.setLslbz(jyspmx.getLslbz());
                                ccjyspmx1.setSpdj(spdj);
                                ccjyspmx1.setSpsl(spsl);
                                ccjyspmx1.setJshj(jshj1);
                                ccjyspmx1.setYkphj(new BigDecimal(0));
                                ccjyspmx1.setSpdm(spdm);
                                ccjyspmx1.setGsdm(jyspmx.getGsdm());
                                splitKpspmxs.add(ccjyspmx1);
                            }
                        }
                        ccje = sub(ccje, mul(new BigDecimal(n), maxje));
                        JyspmxDecimal2 ccjyspmx2 = new JyspmxDecimal2();
                        ccjyspmx2.setFphxz(fphxz);
                        ccjyspmx2.setSqlsh(sqlsh);
                        ccjyspmx2.setSpmxxh(spmxxh);
                        ccjyspmx2.setSpje(ccje);
                        ccjyspmx2.setSpmc(spmc);
                        ccjyspmx2.setSpggxh(spggxh);
                        ccjyspmx2.setSpdj(spdj);
                        ccjyspmx2.setSpsl(spsl);
                        ccjyspmx2.setYhzcbs(jyspmx.getYhzcbs());
                        ccjyspmx2.setYhzcmc(jyspmx.getYhzcmc());
                        ccjyspmx2.setKce(jyspmx.getKce());
                        ccjyspmx2.setLslbz(jyspmx.getLslbz());
                        ccjyspmx2.setSpdw(spdw);
                        ccjyspmx2.setSps(sub(sub(spsm, cfsm), mul(new BigDecimal(n), cfsm1)));
                        ccjyspmx2.setSpse(sub(sub(spse, cfse), mul(new BigDecimal(n), cfse1)));
                        ccjyspmx2.setJshj(add(ccjyspmx2.getSpje(), ccjyspmx2.getSpse()));
                        ccjyspmx2.setYkphj(new BigDecimal(0));
                        ccjyspmx2.setSpdm(spdm);
                        ccjyspmx2.setGsdm(jyspmx.getGsdm());
                        fpnum++;
                        ccjyspmx2.setFpnum(fpnum);
                        tempJyspmxs.clear();
                        if (ccje.doubleValue() != 0) {
                            splitKpspmxs.add(ccjyspmx2);
                            tempJyspmxs.add(ccjyspmx2);
                        }
                        zje = ccje;
                        zje1 = ccjyspmx2.getJshj();
                    } else {
                        /**
                         * 按照页面上填写的分票金额来分票
                         */
                        // Jyspmx ccjyspmx = new Jyspmx();//超出金额对象
                        JyspmxDecimal2 cfjyspmx = new JyspmxDecimal2();// 拆分金额对象
                        // ccjyspmx = jyspmx;//超出金额对象
                        // cfjyspmx = jyspmx;//拆分金额对象
                        // 商品名称
                        String spmc = jyspmx.getSpmc();
                        // 规格型号
                        String spggxh = jyspmx.getSpggxh();
                        // 单位
                        String spdw = jyspmx.getSpdw();
                        // 单价
                        BigDecimal spdj = jyspmx.getSpdj();
                        // 税率
                        BigDecimal spsl = jyspmx.getSpsl();
                        BigDecimal spje = jyspmx.getSpje();// 原商品金额
                        BigDecimal yjshj = jyspmx.getJshj();// 原商品价税合计
                        BigDecimal spsm = jyspmx.getSps();// 原商品数量
                        BigDecimal spse = jyspmx.getSpse();// 原商品税额
                        BigDecimal ccjshj = sub(zje1, fpje);// 超出价税合计
                        BigDecimal cfjshj = sub(yjshj, ccjshj);// 拆分价税合计

                        // BigDecimal cfje = sub(spje, ccje);// 拆分金额
                        BigDecimal cfsm;
                        BigDecimal cfse;
                        BigDecimal cfje;
                        BigDecimal ccje;
                        /**
                         *  是否按商品整数分票
                         */
                        if(spzsfp&&null!=spdj&&!"".equals(spdj)&&null!=spsm&&!"".equals(spsm)){
                             cfsm = div(spsm, div(yjshj, cfjshj));// 拆分数量
                             BigDecimal cfsm1=cfsm;//过度数据，计算下一次数据。

                             cfsm=new BigDecimal(Math.floor(cfsm.doubleValue()));//向下取整
                             if(cfsm.compareTo(new BigDecimal(0))!=0){
                                 BigDecimal cfjshj1=mul(mul(spdj,new BigDecimal(1).add(jyspmx.getSpsl())),sub(cfsm1,cfsm));//多出数量的价税合计

                                 cfjshj=sub(cfjshj,cfjshj1);//把多余的价税合计去掉

                                 ccjshj=add(ccjshj,cfjshj1);

                                 cfse = div(spse, div(yjshj, cfjshj)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额

                                 cfje = sub(cfjshj, cfse);
                                 ccje = sub(spje, cfje);// 超出金额
                             }else{
                                 cfsm = div(spsm, div(yjshj, cfjshj));// 拆分数量
                                 cfse = div(spse, div(yjshj, cfjshj)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                                 cfje = sub(cfjshj, cfse);
                                 ccje = sub(spje, cfje);// 超出金额
                             }
                        }else{
                             cfsm = div(spsm, div(yjshj, cfjshj));// 拆分数量
                             cfse = div(spse, div(yjshj, cfjshj)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                             cfje = sub(cfjshj, cfse);
                             ccje = sub(spje, cfje);// 超出金额
                        }

                        cfjyspmx.setFphxz(fphxz);
                        cfjyspmx.setSqlsh(sqlsh);
                        cfjyspmx.setSpmxxh(spmxxh);
                        cfjyspmx.setSpje(cfje);
                        cfjyspmx.setSps(cfsm);
                        cfjyspmx.setSpse(cfse);
                        cfjyspmx.setFpnum(fpnum);
                        cfjyspmx.setSpmc(spmc);
                        cfjyspmx.setSpggxh(spggxh);
                        cfjyspmx.setSpdw(spdw);
                        cfjyspmx.setSpdj(spdj);
                        cfjyspmx.setSpsl(spsl);
                        cfjyspmx.setYhzcbs(jyspmx.getYhzcbs());
                        cfjyspmx.setYhzcmc(jyspmx.getYhzcmc());
                        cfjyspmx.setKce(jyspmx.getKce());
                        cfjyspmx.setLslbz(jyspmx.getLslbz());
                        cfjyspmx.setJshj(cfjshj);
                        cfjyspmx.setYkphj(new BigDecimal(0));
                        cfjyspmx.setSpdm(spdm);
                        cfjyspmx.setGsdm(jyspmx.getGsdm());
                        splitKpspmxs.add(cfjyspmx);
                        if (fpje.divide(new BigDecimal(1).add(jyspmx.getSpsl()), 30, BigDecimal.ROUND_HALF_UP).compareTo(maxje) > 0) {
                            int n = (int) Math.floor(div(ccje, maxje).doubleValue());
                            BigDecimal cfsm1 = new BigDecimal(0.00);
                            BigDecimal cfse1 = new BigDecimal(0.00);
                            if (n > 0) {
                                /**
                                 * //是否按商品整数分票
                                 */
                                if(spzsfp&&null!=spdj&&!"".equals(spdj)&&null!=spsm&&!"".equals(spsm)){
                                    cfsm1 = div(spsm, div(spje, maxje));// 拆分数量
                                    BigDecimal cfsm2=cfsm1;//过度数据，计算下一次数据。
                                    cfsm1=new BigDecimal(Math.floor(cfsm1.doubleValue()));//向下取整
                                    if(cfsm1.compareTo(new BigDecimal(0))!=0){
                                        BigDecimal ccje2=mul(spdj,sub(cfsm2,cfsm1));//多出数量的金额(不含税)
                                        //ccje=add(ccje,ccje2);//把多出数量的加到超出金额里面
                                        maxje=sub(maxje,ccje2);//把最大金额减去多出数量的金额
                                        cfse1 = div(spse, div(spje, maxje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                                    }else{
                                        cfsm1 = div(spsm, div(spje, maxje));// 拆分数量
                                        cfse1 = div(spse, div(spje, maxje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                                    }
                                }else{
                                    cfsm1 = div(spsm, div(spje, maxje));// 拆分数量
                                    cfse1 = div(spse, div(spje, maxje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                                }
                                for (int j = 0; j < n; j++) {
                                    JyspmxDecimal2 ccjyspmx1 = new JyspmxDecimal2();
                                    // ccjyspmx1 = ccjyspmx;
                                    fpnum++;
                                    BigDecimal jshj1 = add(maxje, cfse1);
                                    ccjyspmx1.setFphxz(fphxz);
                                    ccjyspmx1.setSqlsh(sqlsh);
                                    ccjyspmx1.setSpmxxh(spmxxh);
                                    ccjyspmx1.setSpje(maxje);
                                    ccjyspmx1.setSps(cfsm1);
                                    ccjyspmx1.setSpse(cfse1);
                                    ccjyspmx1.setFpnum(fpnum);
                                    ccjyspmx1.setSpmc(spmc);
                                    ccjyspmx1.setSpggxh(spggxh);
                                    ccjyspmx1.setSpdw(spdw);
                                    ccjyspmx1.setYhzcbs(jyspmx.getYhzcbs());
                                    ccjyspmx1.setYhzcmc(jyspmx.getYhzcmc());
                                    ccjyspmx1.setKce(jyspmx.getKce());
                                    ccjyspmx1.setLslbz(jyspmx.getLslbz());
                                    ccjyspmx1.setSpdj(spdj);
                                    ccjyspmx1.setSpsl(spsl);
                                    ccjyspmx1.setJshj(jshj1);
                                    ccjyspmx1.setYkphj(new BigDecimal(0));
                                    ccjyspmx1.setSpdm(spdm);
                                    ccjyspmx1.setGsdm(jyspmx.getGsdm());
                                    splitKpspmxs.add(ccjyspmx1);
                                }
                            }
                            ccje = sub(ccje, mul(new BigDecimal(n), maxje));
                            JyspmxDecimal2 ccjyspmx2 = new JyspmxDecimal2();
                            ccjyspmx2.setFphxz(fphxz);
                            ccjyspmx2.setSqlsh(sqlsh);
                            ccjyspmx2.setSpmxxh(spmxxh);
                            ccjyspmx2.setSpje(ccje);
                            ccjyspmx2.setSpmc(spmc);
                            ccjyspmx2.setSpggxh(spggxh);
                            ccjyspmx2.setSpdj(spdj);
                            ccjyspmx2.setSpsl(spsl);
                            ccjyspmx2.setSpdw(spdw);
                            ccjyspmx2.setYhzcbs(jyspmx.getYhzcbs());
                            ccjyspmx2.setYhzcmc(jyspmx.getYhzcmc());
                            ccjyspmx2.setKce(jyspmx.getKce());
                            ccjyspmx2.setLslbz(jyspmx.getLslbz());
                            ccjyspmx2.setSps(sub(sub(spsm, cfsm), mul(new BigDecimal(n), cfsm1)));
                            ccjyspmx2.setSpse(sub(sub(spse, cfse), mul(new BigDecimal(n), cfse1)));
                            ccjyspmx2.setJshj(add(ccjyspmx2.getSpje(), ccjyspmx2.getSpse()));
                            ccjyspmx2.setYkphj(new BigDecimal(0));
                            ccjyspmx2.setSpdm(spdm);
                            ccjyspmx2.setGsdm(jyspmx.getGsdm());
                            fpnum++;
                            ccjyspmx2.setFpnum(fpnum);
                            tempJyspmxs.clear();
                            if (ccje.doubleValue() != 0) {
                                splitKpspmxs.add(ccjyspmx2);
                                tempJyspmxs.add(ccjyspmx2);
                            }
                            zje = ccje;
                            zje1 = ccjyspmx2.getJshj();
                        } else {
                            //按照含税金额拆分
                            int n = (int) Math.floor(div(ccjshj, fpje).doubleValue());
                            BigDecimal cfsm1 = new BigDecimal(0.00);
                            BigDecimal cfse1 = new BigDecimal(0.00);
                            if (n > 0) {
                                /**
                                 * //是否按商品整数分票
                                 */
                                if(spzsfp&&null!=spdj&&!"".equals(spdj)&&null!=spsm&&!"".equals(spsm)){
                                    cfsm1 = div(spsm, div(yjshj, fpje));// 拆分数量
                                    BigDecimal cfsm2=cfsm1;//过度数据，计算下一次数据。
                                    cfsm1=new BigDecimal(Math.floor(cfsm1.doubleValue()));//向下取整
                                    if(cfsm1.compareTo(new BigDecimal(0))!=0){
                                        BigDecimal fpje1=mul(mul(spdj,new BigDecimal(1).add(jyspmx.getSpsl())),sub(cfsm2,cfsm1));//多出数量的价税合计

                                        fpje=sub(fpje,fpje1);//把多余的分票金额去掉

                                        //ccjshj=add(ccjshj,fpje1);//把多余的分票金额加到超出的价税合计上面

                                        cfse1 = div(spse, div(yjshj, fpje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                                    }else{
                                        cfsm1 = div(spsm, div(yjshj, fpje));// 拆分数量
                                        cfse1 = div(spse, div(yjshj, fpje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                                    }
                                }else{
                                    cfsm1 = div(spsm, div(yjshj, fpje));// 拆分数量
                                    cfse1 = div(spse, div(yjshj, fpje)).setScale(2, BigDecimal.ROUND_HALF_UP);// 拆分税额
                                }
                                for (int j = 0; j < n; j++) {
                                    JyspmxDecimal2 ccjyspmx1 = new JyspmxDecimal2();
                                    // ccjyspmx1 = ccjyspmx;
                                    fpnum++;
                                    BigDecimal jshj1 = fpje;
                                    ccjyspmx1.setFphxz(fphxz);
                                    ccjyspmx1.setSqlsh(sqlsh);
                                    ccjyspmx1.setSpmxxh(spmxxh);
                                    ccjyspmx1.setSpje(sub(fpje, cfse1));
                                    ccjyspmx1.setSps(cfsm1);
                                    ccjyspmx1.setSpse(cfse1);
                                    ccjyspmx1.setFpnum(fpnum);
                                    ccjyspmx1.setSpmc(spmc);
                                    ccjyspmx1.setSpggxh(spggxh);
                                    ccjyspmx1.setSpdw(spdw);
                                    ccjyspmx1.setYhzcbs(jyspmx.getYhzcbs());
                                    ccjyspmx1.setYhzcmc(jyspmx.getYhzcmc());
                                    ccjyspmx1.setKce(jyspmx.getKce());
                                    ccjyspmx1.setLslbz(jyspmx.getLslbz());
                                    ccjyspmx1.setSpdj(spdj);
                                    ccjyspmx1.setSpsl(spsl);
                                    ccjyspmx1.setJshj(jshj1);
                                    ccjyspmx1.setYkphj(new BigDecimal(0));
                                    ccjyspmx1.setSpdm(spdm);
                                    ccjyspmx1.setGsdm(jyspmx.getGsdm());
                                    splitKpspmxs.add(ccjyspmx1);
                                }
                            }
                            ccjshj = sub(ccjshj, mul(new BigDecimal(n), fpje));
                            JyspmxDecimal2 ccjyspmx2 = new JyspmxDecimal2();
                            ccjyspmx2.setFphxz(fphxz);
                            ccjyspmx2.setSqlsh(sqlsh);
                            ccjyspmx2.setSpmxxh(spmxxh);
                            ccjyspmx2.setSpmc(spmc);
                            ccjyspmx2.setSpggxh(spggxh);
                            ccjyspmx2.setSpdj(spdj);
                            ccjyspmx2.setSpsl(spsl);
                            ccjyspmx2.setSpdw(spdw);
                            ccjyspmx2.setYhzcbs(jyspmx.getYhzcbs());
                            ccjyspmx2.setYhzcmc(jyspmx.getYhzcmc());
                            ccjyspmx2.setKce(jyspmx.getKce());
                            ccjyspmx2.setLslbz(jyspmx.getLslbz());
                            ccjyspmx2.setSps(sub(sub(spsm, cfsm), mul(new BigDecimal(n), cfsm1)));
                            ccjyspmx2.setSpse(sub(sub(spse, cfse), mul(new BigDecimal(n), cfse1)));
                            ccjyspmx2.setSpje(sub(ccjshj, ccjyspmx2.getSpse()));
                            ccjyspmx2.setJshj(ccjshj);
                            ccjyspmx2.setYkphj(new BigDecimal(0));
                            ccjyspmx2.setSpdm(spdm);
                            ccjyspmx2.setGsdm(jyspmx.getGsdm());
                            fpnum++;
                            ccjyspmx2.setFpnum(fpnum);
                            tempJyspmxs.clear();
                            if (ccjshj.doubleValue() != 0) {
                                splitKpspmxs.add(ccjyspmx2);
                                tempJyspmxs.add(ccjyspmx2);
                            }
                            zje = ccjyspmx2.getSpje();
                            zje1 = ccjshj;
                        }
                    }
                }
            } else {
                jyspmx.setFpnum(fpnum);
                splitKpspmxs.add(jyspmx);
            }
        }
        return splitKpspmxs;
    }
    /**
     * 按商品整数来分票
     */
    private static Map<String, BigDecimal> cfsl_byInt(BigDecimal spje, BigDecimal spdj) {

        Map<String, BigDecimal> resultMap = new HashMap<String, BigDecimal>();
        BigDecimal cfsm = div(spje, spdj);
        BigDecimal cfsm1 = cfsm;// 未取整的数量
        BigDecimal cfje_int;// 取整后的金额
        BigDecimal ccje;// 取整后多出的金额 cfje = cfje_int,ccje
        cfsm = new BigDecimal(Math.floor(cfsm.doubleValue()));// 整数数量，向下取整
        cfje_int = mul(spdj, cfsm);// 整数数量对应的金额
        ccje = sub(spje, cfje_int);// 多出数量的金额(不含税)
        if (cfsm.equals(BigDecimal.ZERO)) {
            resultMap.put("cfsm", cfsm1);
            resultMap.put("cfje", spje);
            resultMap.put("ccje", BigDecimal.ZERO);
        } else {
            resultMap.put("cfsm", cfsm);
            resultMap.put("cfje", cfje_int);
            resultMap.put("ccje", ccje);
        }
        return resultMap;
    }
}
