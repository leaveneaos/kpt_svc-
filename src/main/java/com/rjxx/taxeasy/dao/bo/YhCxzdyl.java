package com.rjxx.taxeasy.dao.bo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rjxx.comm.json.JsonDatetimeFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * t_yh_cxzdyl 实体类
 * 用户查询自定义列保存
 * 由GenEntityMysql类自动生成
 * Wed Dec 21 18:09:11 CST 2016
 * @ZhangBing
 */ 
@Entity
@Table(name="t_yh_cxzdyl")
public class YhCxzdyl  implements Serializable {

/**
 * 主键
 */ 
@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Integer id;

@Column(name="yhid")
	protected Integer yhid;

/**
 * 对应的功能
 */ 
@Column(name="dygn")
	protected String dygn;

/**
 * 字段代码
 */ 
@Column(name="zddm")
	protected String zddm;

/**
 * 1-有效，0-无效
 */ 
@Column(name="yxbz")
	protected String yxbz;

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
 * 录入人员
 */ 
@Column(name="lrry")
	protected Integer lrry;

/**
 * 修改人员
 */ 
@Column(name="xgry")
	protected Integer xgry;


	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id=id;
	}

	public Integer getYhid(){
		return yhid;
	}

	public void setYhid(Integer yhid){
		this.yhid=yhid;
	}

	public String getDygn(){
		return dygn;
	}

	public void setDygn(String dygn){
		this.dygn=dygn;
	}

	public String getZddm(){
		return zddm;
	}

	public void setZddm(String zddm){
		this.zddm=zddm;
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

	public Date getXgsj(){
		return xgsj;
	}

	public void setXgsj(Date xgsj){
		this.xgsj=xgsj;
	}

	public Integer getLrry(){
		return lrry;
	}

	public void setLrry(Integer lrry){
		this.lrry=lrry;
	}

	public Integer getXgry(){
		return xgry;
	}

	public void setXgry(Integer xgry){
		this.xgry=xgry;
	}

}

