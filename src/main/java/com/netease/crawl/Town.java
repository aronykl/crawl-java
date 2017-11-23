package com.netease.crawl;

import java.util.List;

/**
 * 街道城镇
 * @Author: hzzhouwen
 * @Date: 2017/11/21 10:39
 */
public class Town {

	private String code;

	private String name;

	private String url;

	private List<Village> villages;

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

	public List<Village> getVillages() {
		return villages;
	}

	public void setVillages(List<Village> villages) {
		this.villages = villages;
	}
}
