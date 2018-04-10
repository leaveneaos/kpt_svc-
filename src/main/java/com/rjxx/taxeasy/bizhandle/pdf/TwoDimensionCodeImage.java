package com.rjxx.taxeasy.bizhandle.pdf;

import jp.sourceforge.qrcode.data.QRCodeImage;

import java.awt.image.BufferedImage;


/**
 *@ClassName TwoDimensionCodeImage
 *@Description 生成二维码主类
 *@Author Him
 *@Date 2015-09-22.
 *@Version 1.0
 **/
public class TwoDimensionCodeImage implements QRCodeImage {
    BufferedImage bufImg;

    public TwoDimensionCodeImage(BufferedImage bufImg) {
        this.bufImg = bufImg;
    }
    @Override
    public int getHeight() {
        return bufImg.getHeight();
    }
    @Override
    public int getPixel(int x, int y) {
        return bufImg.getRGB(x, y);
    }
    @Override
    public int getWidth() {
        return bufImg.getWidth();
    }
}