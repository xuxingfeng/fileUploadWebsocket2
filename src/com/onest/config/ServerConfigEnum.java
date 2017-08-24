package com.onest.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Administrator on 2017/8/23.
 */
public enum ServerConfigEnum {
	config;
	private String savePath;
	private int blobSize;
	private int textSize;
	private String suFix;
	private ServerConfigEnum(){
		Properties properties=new Properties();
		InputStream inStream;
		try {
			inStream = this.getClass().getResourceAsStream("config.properties");
			properties.load(inStream);
			inStream.close();
			this.savePath=properties.getProperty("savePath");
			this.blobSize=Integer.valueOf(properties.getProperty("blobSize"));
			this.textSize=Integer.valueOf(properties.getProperty("textSize"));
			this.suFix=".si";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	public int getBlobSize() {
		return blobSize;
	}
	public void setBlobSize(int blobSize) {
		this.blobSize = blobSize;
	}
	public String getSuFix() {
		return suFix;
	}
	public void setSuFix(String suFix) {
		this.suFix = suFix;
	}
}
