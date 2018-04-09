package com.rjxx.taxeasy.dao.bo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * t_fpzt 实体类
 * 由GenEntityMysql类自动生成
 * Mon Oct 31 17:41:25 CST 2016
 * @ZhangBing
 */ 
@Entity
@Table(name="t_fpzt")
public class Fpzt  implements Serializable {

@Column(name="fpztdm")
	protected String fpztdm;

@Column(name="fpztmc")
	protected String fpztmc;

/**
 * 主键
 */ 
@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Integer id;


	public String getFpztdm(){
		return fpztdm;
	}

	public void setFpztdm(String fpztdm){
		this.fpztdm=fpztdm;
	}

	public String getFpztmc(){
		return fpztmc;
	}

	public void setFpztmc(String fpztmc){
		this.fpztmc=fpztmc;
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id=id;
	}

}

