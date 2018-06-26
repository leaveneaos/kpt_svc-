package com.rjxx.taxeasy.bizhandle.utils;


import com.rjxx.taxeasy.dal.DxfsService;
import com.rjxx.taxeasy.dao.bo.Dxfs;
import com.rjxx.utils.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 *@ClassName SaveMessage
 *@Description 保存短信方法
 *@Author 未知
 *@Date 2017/1/4.
 *@Version 1.0
 **/
@Service
public class SaveMessage {
	@Autowired
	DxfsService dxfsService;

	public boolean saveMessage(String gsdm, Integer djh, String sjhm, Map<String, String> dxnr, String mbdm,
			String qmmc) {
		try {
			String returnid = SendMessage.sendSms(qmmc, mbdm, dxnr, sjhm);
			Dxfs dxfs = new Dxfs();
			dxfs.setGsdm(gsdm);
			dxfs.setDjh(djh);
			dxfs.setSjhm(sjhm);
			dxfs.setMbdm(mbdm);
			dxfs.setLrsj(new Date());
			dxfs.setReturnid(returnid);
			dxfsService.save(dxfs);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
