package com.rjxx.taxeasy.dao.bo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * t_dyfs 实体类
 * 由GenEntityMysql类自动生成
 * Wed Jan 18 09:13:53 CST 2017
 * @ZhangBing
 */ 
@Entity
@Table(name="t_dyfs")
public class Dyfs  implements Serializable {

@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Integer id;

@Column(name="dyfsdm")
	protected String dyfsdm;

@Column(name="dyfsmc")
	protected String dyfsmc;


	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id=id;
	}

	public String getDyfsdm(){
		return dyfsdm;
	}

	public void setDyfsdm(String dyfsdm){
		this.dyfsdm=dyfsdm;
	}

	public String getDyfsmc(){
		return dyfsmc;
	}

	public void setDyfsmc(String dyfsmc){
		this.dyfsmc=dyfsmc;
	}

}

