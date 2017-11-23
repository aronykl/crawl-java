package com.netease.crawl;

import java.util.List;

/**
 * 省份
 * @Author: hzzhouwen
 * @Date: 2017/11/21 10:38
 */
public class Province {

	private String name;

	private String url;

	private List<City> citys;

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

	public List<City> getCitys() {
		return citys;
	}

	public void setCitys(List<City> citys) {
		this.citys = citys;
	}
}
