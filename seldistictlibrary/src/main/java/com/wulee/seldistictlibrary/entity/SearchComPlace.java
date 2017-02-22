package com.wulee.seldistictlibrary.entity;

import java.io.Serializable;

/*
 * 通用的搜索省市区类
 * */
public class SearchComPlace implements Serializable {
	private int type;
	private String id;
	private String name;

	/**
	 * @return 0为省 1为市 2为区
	 */
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
