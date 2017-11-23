package com.netease.crawl;

import java.util.List;

/**
 * 区县
 * @Author: hzzhouwen
 * @Date: 2017/11/21 10:39
 */
public class County {

	private String code;

	private String name;

	private String url;

	private List<Town> towns;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Town> getTowns() {
		return towns;
	}

	public void setTowns(List<Town> towns) {
		this.towns = towns;
	}
}
