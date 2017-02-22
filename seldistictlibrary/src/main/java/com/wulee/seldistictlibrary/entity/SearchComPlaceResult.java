package com.wulee.seldistictlibrary.entity;

import java.io.Serializable;

/*
 * 通用的搜索省市区的结果 类
 * */
public class SearchComPlaceResult implements Serializable {
	private int provinceID;
	private String provinceName;
	private int cityID;
	private String cityName;
	private int areaID;
	private String areaName;

	public SearchComPlaceResult() {
		provinceID = 0;
		provinceName = "";
		cityID = 0;
		cityName = "";
		areaID = 0;
		areaName = "";
	}

	public int getProvinceID() {
		return provinceID;
	}

	public void setProvinceID(int provinceID) {
		this.provinceID = provinceID;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public int getCityID() {
		return cityID;
	}

	public void setCityID(int cityID) {
		this.cityID = cityID;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public int getAreaID() {
		return areaID;
	}

	public void setAreaID(int areaID) {
		this.areaID = areaID;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getResult() {
		StringBuilder sb = new StringBuilder();
		if (provinceName != null && !provinceName.equalsIgnoreCase("")) {
			sb.append(provinceName);
		}
		if (cityName != null && !cityName.equalsIgnoreCase("")) {
			sb.append(" " + cityName);
		}
		if (areaName != null && !areaName.equalsIgnoreCase("")) {
			sb.append(" " + areaName);
		}
		return sb.toString();
	}
}
