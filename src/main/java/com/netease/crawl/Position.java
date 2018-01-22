package com.netease.crawl;

/**
 * @Description: Created with IntelliJ IDEA.
 * @Author: hzzhouwen
 * @Date: 2017/12/14 16:55
 */
public class Position {

	private String name;//名称

	private int isUnique;// 是否唯一， 1表示唯一  0表示不唯一  -1表示百度地图搜不到 -2表示查询没有结果返回的

	private String belongTo;// 如果唯一  所属区域

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIsUnique() {
		return isUnique;
	}

	public void setIsUnique(int isUnique) {
		this.isUnique = isUnique;
	}

	public String getBelongTo() {
		return belongTo;
	}

	public void setBelongTo(String belongTo) {
		this.belongTo = belongTo;
	}
}
