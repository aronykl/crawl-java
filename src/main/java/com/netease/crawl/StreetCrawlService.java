package com.netease.crawl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * 国家统计局地域信息
 * @Author: hzzhouwen
 * @Date: 2017/11/21 10:37
 */
public class StreetCrawlService {

	// url前缀
	private static final String URL_PREFIX = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2016/";

	public static void main(String[] args) throws Exception {
		List<Province> provinces = new ArrayList<>();
		String initUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2016/index.html";
//		String initUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2016/53/07/22/530722105.html";
		// 初始页面 解析省份
		Document doc = null;
		try {
			HttpURLConnection connection = (HttpURLConnection)new URL(initUrl).openConnection();
			connection.setConnectTimeout(80000);
			connection.addRequestProperty("Connection", "close");
			connection.setReadTimeout(8000);
			connection.setUseCaches(Boolean.FALSE);
			doc = Jsoup.parse(connection.getInputStream(), "GBK", initUrl);
//			doc = HttpUtils.httpGet(initUrl);
//			doc = Jsoup.connect(initUrl).timeout(100000).get();
//			doc.charset(Charset.forName("utf-8"));
			Thread.sleep(2000);
		} catch (Exception e) {
//			e.printStackTrace();
//			doc = Jsoup.parse(new URL(initUrl), 100000);
//			doc = HttpUtils.httpGet(initUrl);
//			doc = Jsoup.connect(initUrl).timeout(100000).get();
//			doc.charset(Charset.forName("utf-8"));
			HttpURLConnection connection = (HttpURLConnection)new URL(initUrl).openConnection();
			connection.setConnectTimeout(80000);
			connection.addRequestProperty("Connection", "close");
			connection.setReadTimeout(8000);
			connection.setUseCaches(Boolean.FALSE);
			doc = Jsoup.parse(connection.getInputStream(), "GBK", initUrl);
		}
//		String content = HttpUtils.httpGet(initUrl);
//		System.out.println(doc.html());

//		Elements elements = Jsoup.parse(doc.html()).select("tr .provincetr");
		Element element = Jsoup.parse(doc.html()).select("tr .provincetr").select("td").get(7);
//		System.out.println(elements.html());
//		if (null != elements) {
//			for (Element element : elements) {
				Province prov = new Province();
				prov.setName(element.select("a").html().replace("<br>", ""));
				prov.setUrl(URL_PREFIX + element.select("a").attr("href"));
				fillCities(prov);
				provinces.add(prov);
//			}
//		}
//		System.out.println(JSON.toJSONString(provinces));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:/government.txt", true)));
//		writer.write(JSON.toJSONString(provinces));
//		writer.close();
		if (CollectionUtils.isNotEmpty(provinces)) {
			for (Province province : provinces) {
				String provinceName = province.getName();
				if (CollectionUtils.isNotEmpty(province.getCitys())) {
					for (City city : province.getCitys()) {
						String cityName = city.getName();
						String cityCode = city.getCode();
						if (CollectionUtils.isNotEmpty(city.getCounties())) {
							for (County county : city.getCounties()) {
								String countyName = county.getName();
								String countyCode = county.getCode();
								if (CollectionUtils.isNotEmpty(county.getTowns())) {
									for (Town town : county.getTowns()) {
										String townName = town.getName();
										String townCode = town.getCode();
										if (CollectionUtils.isNotEmpty(town.getVillages())) {
											for (Village village : town.getVillages()) {
												String villageName = village.getName();
												String villageCode = village.getCode();
												String villageCateCode = village.getCategoryCode();

												String line = villageName + "\t" + villageCode + "\t" + villageCateCode + "\t"
														+ townName + "\t" + townCode + "\t"
														+ countyName + "\t" + countyCode + "\t"
														+ cityName + "\t" + cityCode + "\t" + provinceName + "\n";
												writer.write(line);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		writer.close();
	}

	private static void fillCities(Province province) throws Exception {
		if (!StringUtil.isBlank(province.getUrl())) {
			List<City> cities = new ArrayList<>();
			String[] str = province.getUrl().split("/");
			String prefix = province.getUrl().replace(str[str.length - 1], "");
			Document doc = null;
			try {
//				doc = Jsoup.parse(new URL(province.getUrl()), 100000);
//				doc = HttpUtils.httpGet(province.getUrl());
//				doc = Jsoup.connect(province.getUrl()).timeout(100000).get();
//				doc.charset(Charset.forName("utf-8"));
				HttpURLConnection connection = (HttpURLConnection)new URL(province.getUrl()).openConnection();
				connection.setConnectTimeout(80000);
				connection.addRequestProperty("Connection", "close");
				connection.setReadTimeout(8000);
				connection.setUseCaches(Boolean.FALSE);
				doc = Jsoup.parse(connection.getInputStream(), "GBK", province.getUrl());

				Thread.sleep(2000);
			} catch (Exception e) {
//				e.printStackTrace();
				System.out.println(province.getUrl());
//				doc = Jsoup.parse(new URL(province.getUrl()), 100000);
//				doc = HttpUtils.httpGet(province.getUrl());
//				doc = Jsoup.connect(province.getUrl()).timeout(100000).get();
//				doc.charset(Charset.forName("utf-8"));
				HttpURLConnection connection = (HttpURLConnection)new URL(province.getUrl()).openConnection();
				connection.setConnectTimeout(80000);
				connection.addRequestProperty("Connection", "close");
				connection.setReadTimeout(8000);
				connection.setUseCaches(Boolean.FALSE);
				doc = Jsoup.parse(connection.getInputStream(), "GBK", province.getUrl());
			}
//			String content = HttpUtils.httpGet(province.getUrl());

			if (null != doc) {
				Elements elements = Jsoup.parse(doc.html()).select("tr .citytr");
				if (null != elements) {
					for (Element element : elements) {
						City city = new City();
						if (element.select("td a").size() > 1) {
							city.setCode(element.select("td a").get(0).html());
							city.setName(element.select("td a").get(1).html());
							city.setUrl(prefix + element.select("td a").get(0).attr("href"));
						} else if (element.select("td").size() > 1) {
							city.setCode(element.select("td").get(0).html());
							city.setName(element.select("td").get(1).html());
						}

						fillCounties(city);
						cities.add(city);
					}
				}
			}
			province.setCitys(cities);
		} else {
			province.setCitys(new ArrayList<>());
		}
	}

	private static void fillCounties(City city) throws Exception {
		if (!StringUtil.isBlank(city.getUrl())) {
			List<County> counties = new ArrayList<>();
			String[] str = city.getUrl().split("/");
			String prefix = city.getUrl().replace(str[str.length - 1], "");
			Document doc = null;
			try {
//				doc = Jsoup.parse(new URL(city.getUrl()), 100000);
//				doc = HttpUtils.httpGet(city.getUrl());
//				doc = Jsoup.connect(city.getUrl()).timeout(100000).get();
//				doc.charset(Charset.forName("utf-8"));
				HttpURLConnection connection = (HttpURLConnection)new URL(city.getUrl()).openConnection();
				connection.setConnectTimeout(80000);
				connection.addRequestProperty("Connection", "close");
				connection.setReadTimeout(8000);
				connection.setUseCaches(Boolean.FALSE);
				doc = Jsoup.parse(connection.getInputStream(), "GBK", city.getUrl());
				Thread.sleep(2000);
			} catch (Exception e) {
//				e.printStackTrace();
				System.out.println(city.getUrl());
//				doc = Jsoup.parse(new URL(city.getUrl()), 100000);
//				doc = HttpUtils.httpGet(city.getUrl());
//				doc = Jsoup.connect(city.getUrl()).timeout(100000).get();
//				doc.charset(Charset.forName("utf-8"));
				HttpURLConnection connection = (HttpURLConnection)new URL(city.getUrl()).openConnection();
				connection.setConnectTimeout(80000);
				connection.addRequestProperty("Connection", "close");
				connection.setReadTimeout(8000);
				connection.setUseCaches(Boolean.FALSE);
				doc = Jsoup.parse(connection.getInputStream(), "GBK", city.getUrl());
			}
//				String content = HttpUtils.httpGet(city.getUrl());

				if (null != doc) {
					Elements elements = Jsoup.parse(doc.html()).select("tr .countytr");
					if (null != elements) {
						for (Element element : elements) {
							County county = new County();
							if (element.select("td a").size() > 1) {
								county.setCode(element.select("td a").get(0).html());
								county.setName(element.select("td a").get(1).html());
								county.setUrl(prefix + element.select("td a").get(0).attr("href"));
							} else if (element.select("td").size() > 1) {
								county.setCode(element.select("td").get(0).html());
								county.setName(element.select("td").get(1).html());
							}
							fillTowns(county);
							counties.add(county);
						}
					}
				}

			city.setCounties(counties);
		} else {
			city.setCounties(new ArrayList<>());
		}
	}

	private static void fillTowns(County county) throws Exception {
		if (!StringUtil.isBlank(county.getUrl())) {
			List<Town> towns = new ArrayList<>();
			String[] str = county.getUrl().split("/");
			String prefix = county.getUrl().replace(str[str.length - 1], "");
			Document doc = null;
			try {
//				doc = Jsoup.parse(new URL(county.getUrl()), 100000);
//				doc = HttpUtils.httpGet(county.getUrl());
//				doc = Jsoup.connect(county.getUrl()).timeout(100000).get();
//				doc.charset(Charset.forName("utf-8"));
				HttpURLConnection connection = (HttpURLConnection)new URL(county.getUrl()).openConnection();
				connection.setConnectTimeout(80000);
				connection.addRequestProperty("Connection", "close");
				connection.setReadTimeout(8000);
				connection.setUseCaches(Boolean.FALSE);
				doc = Jsoup.parse(connection.getInputStream(), "GBK", county.getUrl());
				Thread.sleep(2000);
			} catch (Exception e) {
//				e.printStackTrace();
				System.out.println(county.getUrl());
//				doc = Jsoup.parse(new URL(county.getUrl()), 100000);
//				doc = HttpUtils.httpGet(county.getUrl());
//				doc = Jsoup.connect(county.getUrl()).timeout(100000).get();
//				doc.charset(Charset.forName("utf-8"));
				HttpURLConnection connection = (HttpURLConnection)new URL(county.getUrl()).openConnection();
				connection.setConnectTimeout(80000);
				connection.addRequestProperty("Connection", "close");
				connection.setReadTimeout(8000);
				connection.setUseCaches(Boolean.FALSE);
				doc = Jsoup.parse(connection.getInputStream(), "GBK", county.getUrl());
			}
//			String content = HttpUtils.httpGet(county.getUrl());
			if (null != doc) {
				Elements elements = Jsoup.parse(doc.html()).select("tr .towntr");
				if (null != elements) {
					for (Element element : elements) {
						Town town = new Town();
						if (element.select("td a").size() > 1) {
							town.setCode(element.select("td a").get(0).html());
							town.setName(element.select("td a").get(1).html());
							town.setUrl(prefix + element.select("td a").get(0).attr("href"));
						} else if (element.select("td").size() > 1) {
							town.setCode(element.select("td").get(0).html());
							town.setName(element.select("td").get(1).html());
						}
						fillVillages(town);
						towns.add(town);
					}
				}
			}
			county.setTowns(towns);
		} else {
			county.setTowns(new ArrayList<>());
		}
	}

	private static void fillVillages(Town town) throws Exception {
		if (!StringUtil.isBlank(town.getUrl())) {
			List<Village> villages = new ArrayList<>();
			Document doc = null;
			try {
//				doc = Jsoup.parse(new URL(town.getUrl()), 100000);
//				doc = HttpUtils.httpGet(town.getUrl());
//				doc = Jsoup.connect(town.getUrl()).timeout(100000).get();
//				doc.charset(Charset.forName("utf-8"));
				HttpURLConnection connection = (HttpURLConnection)new URL(town.getUrl()).openConnection();
				connection.setConnectTimeout(80000);
				connection.addRequestProperty("Connection", "close");
				connection.setReadTimeout(8000);
				connection.setUseCaches(Boolean.FALSE);
				doc = Jsoup.parse(connection.getInputStream(), "GBK", town.getUrl());
				Thread.sleep(2000);
			} catch (Exception e) {
//				e.printStackTrace();
				System.out.println(town.getUrl());
				try {
//					doc = Jsoup.parse(new URL(town.getUrl()), 100000);
//					doc = HttpUtils.httpGet(town.getUrl());
//					doc = Jsoup.connect(town.getUrl()).timeout(100000).get();
//					doc.charset(Charset.forName("utf-8"));
					HttpURLConnection connection = (HttpURLConnection)new URL(town.getUrl()).openConnection();
					connection.setConnectTimeout(80000);
					connection.addRequestProperty("Connection", "close");
					connection.setReadTimeout(8000);
					connection.setUseCaches(Boolean.FALSE);
					doc = Jsoup.parse(connection.getInputStream(), "GBK", town.getUrl());
					Thread.sleep(2000);
				} catch (Exception e1) {
//					e1.printStackTrace();
//					doc = Jsoup.parse(new URL(town.getUrl()), 100000);
//					doc = HttpUtils.httpGet(town.getUrl());
//					doc = Jsoup.connect(town.getUrl()).timeout(100000).get();
//					doc.charset(Charset.forName("utf-8"));
					HttpURLConnection connection = (HttpURLConnection)new URL(town.getUrl()).openConnection();
					connection.setConnectTimeout(80000);
					connection.addRequestProperty("Connection", "close");
					connection.setReadTimeout(8000);
					connection.setUseCaches(Boolean.FALSE);
					doc = Jsoup.parse(connection.getInputStream(), "GBK", town.getUrl());
				}
			}
			if (null != doc) {
				Elements elements = Jsoup.parse(doc.html()).select("tr .villagetr");
				if (null != elements) {
					for (Element element : elements) {
						Village village = new Village();
						if (element.select("td").size() > 2) {
							village.setCode(element.select("td").get(0).html());
							village.setName(element.select("td").get(2).html());
							village.setCategoryCode(element.select("td").get(1).html());
						}

						villages.add(village);
					}
				}
			}
			town.setVillages(villages);
		} else {
			town.setVillages(new ArrayList<>());
		}
	}
}
