package com.rjxx.taxeasy.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rjxx.comm.json.JsonDatetimeFormat;

public class KplsVO2  {

     
    protected Integer kplsh;
     
    protected String jylsh;
     
    protected String gfmc;
    @JsonSerialize(using = JsonDatetimeFormat.class)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    protected Date jylssj;
     
    protected String fpdm;
     
    protected String fphm;
     
    protected Double je;
     
    protected Double se;
     
    protected Double jshj;
     
    protected String yx;//邮箱
     
    protected String sj;//购方电话
    @JsonSerialize(using = JsonDatetimeFormat.class)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    protected Date kprq;
     
    protected String pdfurl;
     
    protected String id;//kplsh
    //查询、红冲所用   
     
    protected String ddh;//订单号
     
    protected String gfsh;//税号
     
    protected String gfyh;//
     
    protected String gfyhzh;//
     
    protected Integer djh;
     
    protected String hkbz;
     
    protected String fpzldm;
     
    protected String fpczlxdm;
     
    protected String hzyfpdm;
     
    protected String hzyfphm;
     
    protected String zfr;
     
    protected String kpr;
     
    protected String fpzlmc;
     
    protected String fpzt;
     
    protected String fpczlxmc;
     
    private String spmc;

    public String getSpmc() {
		return spmc;
	}

	public void setSpmc(String spmc) {
		this.spmc = spmc;
	}

	public Integer getKplsh() {
        return kplsh;
    }

    public void setKplsh(Integer kplsh) {
        this.kplsh = kplsh;
    }

    public String getJylsh() {
        return jylsh;
    }

    public void setJylsh(String jylsh) {
        this.jylsh = jylsh;
    }

    public String getGfmc() {
        return gfmc;
    }

    public void setGfmc(String gfmc) {
        this.gfmc = gfmc;
    }



    public Date getJylssj() {
		return jylssj;
	}

	public void setJylssj(Date jylssj) {
		this.jylssj = jylssj;
	}

	public void setKprq(Date kprq) {
		this.kprq = kprq;
	}

	public String getFpdm() {
        return fpdm;
    }

    public void setFpdm(String fpdm) {
        this.fpdm = fpdm;
    }

    public String getFphm() {
        return fphm;
    }

    public void setFphm(String fphm) {
        this.fphm = fphm;
    }

    public Double getJe() {
        return je;
    }

    public void setJe(Double je) {
        this.je = je;
    }

    public Double getSe() {
        return se;
    }

    public void setSe(Double se) {
        this.se = se;
    }

    public Double getJshj() {
        return jshj;
    }

    public void setJshj(Double jshj) {
        this.jshj = jshj;
    }

    public String getYx() {
        return yx;
    }

    public void setYx(String yx) {
        this.yx = yx;
    }

    public String getSj() {
        return sj;
    }

    public void setSj(String sj) {
        this.sj = sj;
    }

    public String getPdfurl() {
        return pdfurl;
    }

    public void setPdfurl(String pdfurl) {
        this.pdfurl = pdfurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDdh() {
        return ddh;
    }

    public void setDdh(String ddh) {
        this.ddh = ddh;
    }

    public String getGfsh() {
        return gfsh;
    }

    public void setGfsh(String gfsh) {
        this.gfsh = gfsh;
    }

    public String getGfyh() {
        return gfyh;
    }

    public void setGfyh(String gfyh) {
        this.gfyh = gfyh;
    }

    public String getGfyhzh() {
        return gfyhzh;
    }

    public void setGfyhzh(String gfyhzh) {
        this.gfyhzh = gfyhzh;
    }

    public Integer getDjh() {
        return djh;
    }

    public void setDjh(Integer djh) {
        this.djh = djh;
    }

    public String getHkbz() {
        return hkbz;
    }

    public void setHkbz(String hkbz) {
        this.hkbz = hkbz;
    }

    public String getFpzldm() {
        return fpzldm;
    }

    public void setFpzldm(String fpzldm) {
        this.fpzldm = fpzldm;
    }

    public String getFpczlxdm() {
        return fpczlxdm;
    }

    public void setFpczlxdm(String fpczlxdm) {
        this.fpczlxdm = fpczlxdm;
    }

    public String getHzyfpdm() {
        return hzyfpdm;
    }

    public void setHzyfpdm(String hzyfpdm) {
        this.hzyfpdm = hzyfpdm;
    }

    public String getHzyfphm() {
        return hzyfphm;
    }

    public void setHzyfphm(String hzyfphm) {
        this.hzyfphm = hzyfphm;
    }

    public String getZfr() {
        return zfr;
    }

    public void setZfr(String zfr) {
        this.zfr = zfr;
    }

    public String getKpr() {
        return kpr;
    }

    public void setKpr(String kpr) {
        this.kpr = kpr;
    }

    public String getFpzlmc() {
        return fpzlmc;
    }

    public void setFpzlmc(String fpzlmc) {
        this.fpzlmc = fpzlmc;
    }

    public String getFpzt() {
        return fpzt;
    }

    public void setFpzt(String fpzt) {
        this.fpzt = fpzt;
    }

    public String getFpczlxmc() {
        return fpczlxmc;
    }

    public void setFpczlxmc(String fpczlxmc) {
        this.fpczlxmc = fpczlxmc;
    }
}
