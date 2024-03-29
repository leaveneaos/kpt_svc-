package com.rjxx.taxeasy.dao.bo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rjxx.comm.json.JsonDatetimeFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
/**
 * t_seqnumber 实体类
 * 由GenEntityMysql类自动生成
 * Fri May 11 14:39:37 CST 2018
 * @administrator
 */ 
@Entity
@Table(name="t_seqnumber")
public class Seqnumber  implements Serializable {

/**
 * 凯盈交易流水号自动递增
 */
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Integer seqnumber;

/**
 * 平台开票流水号或者税控盘ID
 */ 
@Column(name="jylsh")
	protected String jylsh;

/**
 * 业务类型
 */ 
@Column(name="optype")
	protected String optype;

/**
 * 录入时间
 */ 
@Column(name="lrsj")
@JsonSerialize(using = JsonDatetimeFormat.class)
@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	protected Date lrsj;

/**
 * 修改时间
 */ 
@Column(name="xgsj")
@JsonSerialize(using = JsonDatetimeFormat.class)
@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	protected Date xgsj;

/**
 * 有效标志（1：有效；0：无效）
 */ 
@Column(name="yxbz")
	protected Integer yxbz;

/**
 * 业务类型名称
 */ 
@Column(name="optypemc")
	protected String optypemc;


	public Integer getSeqnumber(){
		return seqnumber;
	}

	public void setSeqnumber(Integer seqnumber){
		this.seqnumber=seqnumber;
	}

	public String getJylsh(){
		return jylsh;
	}

	public void setJylsh(String jylsh){
		this.jylsh=jylsh;
	}

	public String getOptype(){
		return optype;
	}

	public void setOptype(String optype){
		this.optype=optype;
	}

	public Date getLrsj(){
		return lrsj;
	}

	public void setLrsj(Date lrsj){
		this.lrsj=lrsj;
	}

	public Date getXgsj(){
		return xgsj;
	}

	public void setXgsj(Date xgsj){
		this.xgsj=xgsj;
	}

	public Integer getYxbz(){
		return yxbz;
	}

	public void setYxbz(Integer yxbz){
		this.yxbz=yxbz;
	}

	public String getOptypemc(){
		return optypemc;
	}

	public void setOptypemc(String optypemc){
		this.optypemc=optypemc;
	}

}

