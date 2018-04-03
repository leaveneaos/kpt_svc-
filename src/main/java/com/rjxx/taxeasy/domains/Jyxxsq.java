package com.rjxx.taxeasy.domains;

import java.util.Date;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import com.rjxx.comm.json.JsonDateFormat;
import com.rjxx.comm.json.JsonDatetimeFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
/**
 * t_jyxxsq 实体类
 * 交易信息申请 该表用于记录全部交易申请信息。
 * 由GenEntityMysql类自动生成
 * Thu Apr 20 19:08:56 CST 2017
 * @ZhangBing
 */ 
@Entity
@Table(name="t_jyxxsq")
public class Jyxxsq  implements Serializable {

/**
 * 请求单据号 TaxEasy系统自动生成，每次请求唯一。
 */ 
	@Id    
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Integer sqlsh;

/**
 * 开票点代码
 */ 
@Column(name="kpddm")
	protected String kpddm;

/**
 * 交易流水号 业务系统或电商平台生成，包括所有需要开票和不需要开票的全部交易流水号。
 */ 
@Column(name="jylsh")
	protected String jylsh;

/**
 * 订单申请时间 业务系统或电商平台生成。
 */ 
@Column(name="ddrq")
@JsonSerialize(using = JsonDatetimeFormat.class)
@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	protected Date ddrq;

/**
 * 订单号 业务系统或电商平台生成。
 */ 
@Column(name="ddh")
	protected String ddh;

/**
 * 发票种类代码 业务系统或电商平台生成，或提供业务规则由TaxEasy生成。代码见t_fpzl。
 */ 
@Column(name="fpzldm")
	protected String fpzldm;

/**
 * 发票操作类型代码 业务系统或电商平台生成，或提供业务规则由TaxEasy生成。代码见t_fpczlx。
 */ 
@Column(name="fpczlxdm")
	protected String fpczlxdm;

/**
 * 销方ID
 */ 
@Column(name="xfid")
	protected Integer xfid;

/**
 * 销方税号
 */ 
@Column(name="xfsh")
	protected String xfsh;

/**
 * 销方名称
 */ 
@Column(name="xfmc")
	protected String xfmc;

/**
 * 销方银行
 */ 
@Column(name="xfyh")
	protected String xfyh;

/**
 * 销方银行账号
 */ 
@Column(name="xfyhzh")
	protected String xfyhzh;

/**
 * 销方联系人
 */ 
@Column(name="xflxr")
	protected String xflxr;

/**
 * 销方地址
 */ 
@Column(name="xfdz")
	protected String xfdz;

/**
 * 销方电话
 */ 
@Column(name="xfdh")
	protected String xfdh;

/**
 * 销方邮编
 */ 
@Column(name="xfyb")
	protected String xfyb;

/**
 * 购方ID
 */ 
@Column(name="gfid")
	protected Integer gfid;

/**
 * 购方税号
 */ 
@Column(name="gfsh")
	protected String gfsh;

/**
 * 购方类型（0不报销，1报销）
 */ 
@Column(name="gflx")
	protected String gflx;
/**
 * 购方名称
 */ 
@Column(name="gfmc")
	protected String gfmc;

/**
 * 购方银行
 */ 
@Column(name="gfyh")
	protected String gfyh;

/**
 * 购方银行账号
 */ 
@Column(name="gfyhzh")
	protected String gfyhzh;

/**
 * 购方联系人
 */ 
@Column(name="gflxr")
	protected String gflxr;

/**
 * 购方地址
 */ 
@Column(name="gfdz")
	protected String gfdz;

/**
 * 购方电话
 */ 
@Column(name="gfdh")
	protected String gfdh;

/**
 * 购方邮编
 */ 
@Column(name="gfyb")
	protected String gfyb;

/**
 * 邮箱地址
 */ 
@Column(name="gfemail")
	protected String gfemail;

/**
 * 开具类型(0、纸质开具；1、电子开具；2、部分退货暂不开具；3、部分折扣暂不开具
 */ 
@Column(name="sffsyj")
	protected String sffsyj;

/**
 * 电子发票处理状态代码
 */ 
@Column(name="clztdm")
	protected String clztdm;

/**
 * 备注
 */ 
@Column(name="bz")
	protected String bz;

/**
 * 收款人
 */ 
@Column(name="skr")
	protected String skr;

/**
 * 开票(红冲、作废)人
 */ 
@Column(name="kpr")
	protected String kpr;

/**
 * 复核人
 */ 
@Column(name="fhr")
	protected String fhr;

/**
 * 是否自动拆分？（1、拆分；0、不拆分）
 */ 
@Column(name="sfcp")
	protected String sfcp;

/**
 * 是否打印清单，1 打印清单 0 不打印清单
 */ 
@Column(name="sfdyqd")
	protected String sfdyqd;

/**
 * 征税方式：0-普通征税，1-减按征税，2-差额征税
 */ 
@Column(name="zsfs")
	protected String zsfs;

/**
 * 所属月份
 */ 
@Column(name="ssyf")
	protected String ssyf;

/**
 * 专票红字通知单号
 */ 
@Column(name="hztzdh")
	protected String hztzdh;

/**
 * 红冲(作废)对应原发票代码
 */ 
@Column(name="yfpdm")
	protected String yfpdm;

/**
 * 红冲(作废)对应原发票号码
 */ 
@Column(name="yfphm")
	protected String yfphm;

/**
 * 含税标志 0、不含税；1、含税
 */ 
@Column(name="hsbz")
	protected String hsbz;

/**
 * 价税合计 价税合计
 */ 
@Column(name="jshj")
	protected Double jshj;

/**
 * 已开票价税合计 已开票价税合计
 */ 
@Column(name="ykpjshj")
	protected Double ykpjshj;

/**
 * 有效标志 1、有效；0、无效
 */ 
@Column(name="yxbz")
	protected String yxbz;

/**
 * 录入时间 系统时间
 */ 
@Column(name="lrsj")
@JsonSerialize(using = JsonDatetimeFormat.class)
@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	protected Date lrsj;

/**
 * 录入人员
 */ 
@Column(name="lrry")
	protected Integer lrry;

/**
 * 修改时间
 */ 
@Column(name="xgsj")
@JsonSerialize(using = JsonDatetimeFormat.class)
@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	protected Date xgsj;

/**
 * 修改人员
 */ 
@Column(name="xgry")
	protected Integer xgry;

/**
 * 购方收件人
 */ 
@Column(name="gfsjr")
	protected String gfsjr;

/**
 * 购方收件人地址
 */ 
@Column(name="gfsjrdz")
	protected String gfsjrdz;

/**
 * 公司代码
 */ 
@Column(name="gsdm")
	protected String gsdm;

/**
 * 提取码
 */ 
@Column(name="tqm")
	protected String tqm;

@Column(name="skpid")
	protected Integer skpid;

/**
 * 购方手机号
 */ 
@Column(name="gfsjh")
	protected String gfsjh;

@Column(name="dxzt")
	protected String dxzt;

/**
 * 数据来源（0平台录入，1接口接入，2平台导入）
 */ 
@Column(name="sjly")
	protected String sjly;

/**
 * 状态标识 0 待提交,1已申请,2退回,3已处理,4删除,5部分处理,6待处理
 */ 
@Column(name="ztbz")
	protected String ztbz;

/**
 * 是否直连开票：1是，0否
 */ 
@Column(name="sfzlkp")
	protected String sfzlkp;

/**
 * 是否打印，1打印，0不打印
 */ 
@Column(name="sfdy")
	protected String sfdy;

	/**
	 * 客户号
	 */
	@Column(name="khh")
	protected String khh;

	/**
	 * 数据来源对应终端的openid
	 */
	@Column(name="openid")
	protected String openid;

	/**
	 * 全局折扣qjzk
	 */
	@Column(name="qjzk")
	protected Double qjzk;
	/**
	 * 销售渠道xsqd
	 */
	@Column(name="xsqd")
	protected String xsqd;

	public Double getQjzk() {
		return qjzk;
	}

	public void setQjzk(Double qjzk) {
		this.qjzk = qjzk;
	}

	public Integer getSqlsh(){
		return sqlsh;
	}

	public void setSqlsh(Integer sqlsh){
		this.sqlsh=sqlsh;
	}

	public String getKpddm(){
		return kpddm;
	}

	public void setKpddm(String kpddm){
		this.kpddm=kpddm;
	}

	public String getJylsh(){
		return jylsh;
	}

	public void setJylsh(String jylsh){
		this.jylsh=jylsh;
	}

	public Date getDdrq(){
		return ddrq;
	}

	public void setDdrq(Date ddrq){
		this.ddrq=ddrq;
	}

	public String getDdh(){
		return ddh;
	}

	public void setDdh(String ddh){
		this.ddh=ddh;
	}

	public String getFpzldm(){
		return fpzldm;
	}

	public void setFpzldm(String fpzldm){
		this.fpzldm=fpzldm;
	}

	public String getFpczlxdm(){
		return fpczlxdm;
	}

	public void setFpczlxdm(String fpczlxdm){
		this.fpczlxdm=fpczlxdm;
	}

	public Integer getXfid(){
		return xfid;
	}

	public void setXfid(Integer xfid){
		this.xfid=xfid;
	}

	public String getXfsh(){
		return xfsh;
	}

	public void setXfsh(String xfsh){
		this.xfsh=xfsh;
	}

	public String getXfmc(){
		return xfmc;
	}

	public void setXfmc(String xfmc){
		this.xfmc=xfmc;
	}

	public String getXfyh(){
		return xfyh;
	}

	public void setXfyh(String xfyh){
		this.xfyh=xfyh;
	}

	public String getXfyhzh(){
		return xfyhzh;
	}

	public void setXfyhzh(String xfyhzh){
		this.xfyhzh=xfyhzh;
	}

	public String getXflxr(){
		return xflxr;
	}

	public void setXflxr(String xflxr){
		this.xflxr=xflxr;
	}

	public String getXfdz(){
		return xfdz;
	}

	public void setXfdz(String xfdz){
		this.xfdz=xfdz;
	}

	public String getXfdh(){
		return xfdh;
	}

	public void setXfdh(String xfdh){
		this.xfdh=xfdh;
	}

	public String getXfyb(){
		return xfyb;
	}

	public void setXfyb(String xfyb){
		this.xfyb=xfyb;
	}

	public Integer getGfid(){
		return gfid;
	}

	public void setGfid(Integer gfid){
		this.gfid=gfid;
	}

	public String getGfsh(){
		return gfsh;
	}

	public void setGfsh(String gfsh){
		this.gfsh=gfsh;
	}

	public String getGflx() {
		return gflx;
	}

	public void setGflx(String gflx) {
		this.gflx = gflx;
	}

	public String getGfmc(){
		return gfmc;
	}

	public void setGfmc(String gfmc){
		this.gfmc=gfmc;
	}

	public String getGfyh(){
		return gfyh;
	}

	public void setGfyh(String gfyh){
		this.gfyh=gfyh;
	}

	public String getGfyhzh(){
		return gfyhzh;
	}

	public void setGfyhzh(String gfyhzh){
		this.gfyhzh=gfyhzh;
	}

	public String getGflxr(){
		return gflxr;
	}

	public void setGflxr(String gflxr){
		this.gflxr=gflxr;
	}

	public String getGfdz(){
		return gfdz;
	}

	public void setGfdz(String gfdz){
		this.gfdz=gfdz;
	}

	public String getGfdh(){
		return gfdh;
	}

	public void setGfdh(String gfdh){
		this.gfdh=gfdh;
	}

	public String getGfyb(){
		return gfyb;
	}

	public void setGfyb(String gfyb){
		this.gfyb=gfyb;
	}

	public String getGfemail(){
		return gfemail;
	}

	public void setGfemail(String gfemail){
		this.gfemail=gfemail;
	}

	public String getSffsyj(){
		return sffsyj;
	}

	public void setSffsyj(String sffsyj){
		this.sffsyj=sffsyj;
	}

	public String getClztdm(){
		return clztdm;
	}

	public void setClztdm(String clztdm){
		this.clztdm=clztdm;
	}

	public String getBz(){
		return bz;
	}

	public void setBz(String bz){
		this.bz=bz;
	}

	public String getSkr(){
		return skr;
	}

	public void setSkr(String skr){
		this.skr=skr;
	}

	public String getKpr(){
		return kpr;
	}

	public void setKpr(String kpr){
		this.kpr=kpr;
	}

	public String getFhr(){
		return fhr;
	}

	public void setFhr(String fhr){
		this.fhr=fhr;
	}

	public String getSfcp(){
		return sfcp;
	}

	public void setSfcp(String sfcp){
		this.sfcp=sfcp;
	}

	public String getSfdyqd(){
		return sfdyqd;
	}

	public void setSfdyqd(String sfdyqd){
		this.sfdyqd=sfdyqd;
	}

	public String getZsfs(){
		return zsfs;
	}

	public void setZsfs(String zsfs){
		this.zsfs=zsfs;
	}

	public String getSsyf(){
		return ssyf;
	}

	public void setSsyf(String ssyf){
		this.ssyf=ssyf;
	}

	public String getHztzdh(){
		return hztzdh;
	}

	public void setHztzdh(String hztzdh){
		this.hztzdh=hztzdh;
	}

	public String getYfpdm(){
		return yfpdm;
	}

	public void setYfpdm(String yfpdm){
		this.yfpdm=yfpdm;
	}

	public String getYfphm(){
		return yfphm;
	}

	public void setYfphm(String yfphm){
		this.yfphm=yfphm;
	}

	public String getHsbz(){
		return hsbz;
	}

	public void setHsbz(String hsbz){
		this.hsbz=hsbz;
	}

	public Double getJshj(){
		return jshj;
	}

	public void setJshj(Double jshj){
		this.jshj=jshj;
	}

	public Double getYkpjshj(){
		return ykpjshj;
	}

	public void setYkpjshj(Double ykpjshj){
		this.ykpjshj=ykpjshj;
	}

	public String getYxbz(){
		return yxbz;
	}

	public void setYxbz(String yxbz){
		this.yxbz=yxbz;
	}

	public Date getLrsj(){
		return lrsj;
	}

	public void setLrsj(Date lrsj){
		this.lrsj=lrsj;
	}

	public Integer getLrry(){
		return lrry;
	}

	public void setLrry(Integer lrry){
		this.lrry=lrry;
	}

	public Date getXgsj(){
		return xgsj;
	}

	public void setXgsj(Date xgsj){
		this.xgsj=xgsj;
	}

	public Integer getXgry(){
		return xgry;
	}

	public void setXgry(Integer xgry){
		this.xgry=xgry;
	}

	public String getGfsjr(){
		return gfsjr;
	}

	public void setGfsjr(String gfsjr){
		this.gfsjr=gfsjr;
	}

	public String getGfsjrdz(){
		return gfsjrdz;
	}

	public void setGfsjrdz(String gfsjrdz){
		this.gfsjrdz=gfsjrdz;
	}

	public String getGsdm(){
		return gsdm;
	}

	public void setGsdm(String gsdm){
		this.gsdm=gsdm;
	}

	public String getTqm(){
		return tqm;
	}

	public void setTqm(String tqm){
		this.tqm=tqm;
	}

	public Integer getSkpid(){
		return skpid;
	}

	public void setSkpid(Integer skpid){
		this.skpid=skpid;
	}

	public String getGfsjh(){
		return gfsjh;
	}

	public void setGfsjh(String gfsjh){
		this.gfsjh=gfsjh;
	}

	public String getDxzt(){
		return dxzt;
	}

	public void setDxzt(String dxzt){
		this.dxzt=dxzt;
	}

	public String getSjly(){
		return sjly;
	}

	public void setSjly(String sjly){
		this.sjly=sjly;
	}

	public String getZtbz(){
		return ztbz;
	}

	public void setZtbz(String ztbz){
		this.ztbz=ztbz;
	}

	public String getSfzlkp(){
		return sfzlkp;
	}

	public void setSfzlkp(String sfzlkp){
		this.sfzlkp=sfzlkp;
	}

	public String getSfdy(){
		return sfdy;
	}

	public void setSfdy(String sfdy){
		this.sfdy=sfdy;
	}

	public String getKhh() {
		return khh;
	}

	public void setKhh(String khh) {
		this.khh = khh;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getXsqd() {
		return xsqd;
	}

	public void setXsqd(String xsqd) {
		this.xsqd = xsqd;
	}
}

