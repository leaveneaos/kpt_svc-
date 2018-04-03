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
 * t_smtq 实体类
 * 由GenEntityMysql类自动生成
 * Mon Nov 07 16:40:04 CST 2016
 * @WangYong
 */ 
@Entity
@Table(name="t_smtq")
public class Smtq  implements Serializable {

@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Integer id;

@Column(name="ddh")
	protected String ddh;

@Column(name="jylssj")
@JsonSerialize(using = JsonDatetimeFormat.class)
@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	protected Date jylssj;

@Column(name="zje")
	protected Double zje;

@Column(name="gfmc")
	protected String gfmc;

@Column(name="nsrsbh")
	protected String nsrsbh;

@Column(name="dz")
	protected String dz;

@Column(name="dh")
	protected String dh;

@Column(name="khh")
	protected String khh;

@Column(name="khhzh")
	protected String khhzh;

@Column(name="yx")
	protected String yx;

@Column(name="sj")
	protected String sj;
@Column(name="kpddm")
protected String kpddm;
@Column(name="openid")
protected String openid;
@Column(name="lrsj")
@JsonSerialize(using = JsonDatetimeFormat.class)
@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	protected Date lrsj;

/**
 * 请查看代码表
 */ 
@Column(name="fpzt")
	protected String fpzt;

@Column(name="yxbz")
	protected String yxbz;

@Column(name="gsdm")
	protected String gsdm;

@Column(name="llqxx")
protected String llqxx;

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id=id;
	}

	public String getDdh(){
		return ddh;
	}

	public void setDdh(String ddh){
		this.ddh=ddh;
	}

	public Date getJylssj(){
		return jylssj;
	}

	public void setJylssj(Date jylssj){
		this.jylssj=jylssj;
	}

	public Double getZje(){
		return zje;
	}

	public void setZje(Double zje){
		this.zje=zje;
	}

	public String getGfmc(){
		return gfmc;
	}

	public void setGfmc(String gfmc){
		this.gfmc=gfmc;
	}

	public String getNsrsbh(){
		return nsrsbh;
	}

	public void setNsrsbh(String nsrsbh){
		this.nsrsbh=nsrsbh;
	}

	public String getDz(){
		return dz;
	}

	public void setDz(String dz){
		this.dz=dz;
	}

	public String getDh(){
		return dh;
	}

	public void setDh(String dh){
		this.dh=dh;
	}

	public String getKhh(){
		return khh;
	}

	public void setKhh(String khh){
		this.khh=khh;
	}

	public String getKhhzh(){
		return khhzh;
	}

	public void setKhhzh(String khhzh){
		this.khhzh=khhzh;
	}

	public String getYx(){
		return yx;
	}

	public void setYx(String yx){
		this.yx=yx;
	}

	public String getSj(){
		return sj;
	}

	public void setSj(String sj){
		this.sj=sj;
	}

	public Date getLrsj(){
		return lrsj;
	}

	public void setLrsj(Date lrsj){
		this.lrsj=lrsj;
	}

	public String getFpzt(){
		return fpzt;
	}

	public void setFpzt(String fpzt){
		this.fpzt=fpzt;
	}

	public String getYxbz(){
		return yxbz;
	}

	public void setYxbz(String yxbz){
		this.yxbz=yxbz;
	}

	public String getGsdm(){
		return gsdm;
	}

	public void setGsdm(String gsdm){
		this.gsdm=gsdm;
	}

	public String getKpddm() {
		return kpddm;
	}

	public void setKpddm(String kpddm) {
		this.kpddm = kpddm;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getLlqxx() {
		return llqxx;
	}

	public void setLlqxx(String llqxx) {
		this.llqxx = llqxx;
	}

}

