package com.netease.crawl;

import com.alibaba.fastjson.JSON;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Description: Created with IntelliJ IDEA.
 * @Author: hzzhouwen
 * @Date: 2017/11/15 18:33
 */
public class CrawlService {

	public static void main(String[] args) throws IOException {
		// 存取抓取的结果，key：城市 ——》value: key:区域 value:商区列表
		Map<String, Map<String, Set<String>>> areaMap = new ConcurrentHashMap<>();
		// 存取抓取的结果，key：di ——》value: key:地铁线 value:商区列表
		Map<String, Map<String, Set<String>>> subwayMap = new ConcurrentHashMap<>();

		CrawlService service = new CrawlService();
		int count = 0;
		for (int i = 1; i <= 2500; i++) {
			String url = "http://www.dianping.com/shopall/" + i + "/10#Top";
			service.crawlPage(url, areaMap, subwayMap);
			count ++;
			System.out.println(count);
		}

		// 将两个Map的内容写到文件里面取
		FileOutputStream areaFile = new FileOutputStream("D:/areaFile.txt", true);
		FileOutputStream subwayFile = new FileOutputStream("D:/subwayFile.txt", true);

		BufferedWriter areaWriter = new BufferedWriter(new OutputStreamWriter(areaFile));
		BufferedWriter subwayWriter = new BufferedWriter(new OutputStreamWriter(subwayFile));

		areaMap.forEach((key, value) -> {
			value.forEach((k, v) -> {
				v.forEach(business -> {
					String result = key + "\t" + k + "\t" + business + "\n";
					try {
						areaWriter.write(result);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			});
		});

		subwayMap.forEach((key, value) -> {
			value.forEach((k, v) -> {
				v.forEach(subway -> {
					String result = key + "\t" + k + "\t" + subway + "\n";
					try {
						subwayWriter.write(result);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			});
		});

		areaWriter.close();
		subwayWriter.close();

	}



	private void crawlPage(String url, Map<String, Map<String, Set<String>>> areaMap,
						   Map<String, Map<String, Set<String>>> subwayMap) throws IOException {
		Document doc = Jsoup.connect(url).get();
//		System.out.println(doc.html());
		String title = Jsoup.parse(doc.html()).select("h1.shopall strong").html();
		String city = null;
		if (!StringUtil.isBlank(title)) {
			city = title.replace("美食地图", "");
		}
		Map<String, Set<String>> cityAreaMap = new HashMap<>();
		Map<String, Set<String>> citySubwayMap = new HashMap<>();
		if (!StringUtil.isBlank(city)) {
			areaMap.put(city, cityAreaMap);
			subwayMap.put(city, citySubwayMap);
		}
		// 每个城市的区
		Elements elements = Jsoup.parse(doc.html()).select("div .content_b div");
		for (Element element : elements) {
			if (null != element.select("h2").html() && "商区".equals(element.select("h2").html().substring(0, 2))) {
				Elements dlElements = element.select("dl");
				for (Element dlelement : dlElements) {
					String area = dlelement.select("dt a").html();
					Elements businesses = dlelement.select("li");
					Set<String> businessSet = businesses.stream().map(e -> e.select("a").html()).collect(Collectors.toSet());
					if (!StringUtil.isBlank(area)) {
//						Set<String> currSet = cityAreaMap.get(area);
//						if (null != currSet && currSet.size() > 0) {
//							businessSet.addAll(currSet);
//						}
						cityAreaMap.put(area, businessSet);
					}
				}
			}
			// 地标，合并到区里面去
			if (null != element.select("h2").html() && "地标".equals(element.select("h2").html().substring(0, 2))) {
				Elements dlElements = element.select("dl");
				for (Element dlelement : dlElements) {
					String area = dlelement.select("dt a").html();
					Elements businesses = dlelement.select("li");
					Set<String> businessSet = businesses.stream().map(e -> e.select("a").html()).collect(Collectors.toSet());
					if (!StringUtil.isBlank(area)) {
						Set<String> currSet = cityAreaMap.get(area);
						if (null != currSet && currSet.size() > 0) {
							businessSet.addAll(currSet);
						}
						cityAreaMap.put(area, businessSet);
					}
				}
			}

			// 地铁沿线
			if (null != element.select("h2").html() && "地铁沿线".equals(element.select("h2").html().substring(0, 4))) {
				Elements dlElements = element.select("dl");
				for (Element dlelement : dlElements) {
					String subway = dlelement.select("dt a").html();
					Elements businesses = dlelement.select("li");
					Set<String> businessSet = businesses.stream().map(e -> e.select("a").html()).collect(Collectors.toSet());
					if (!StringUtil.isBlank(subway)) {
						citySubwayMap.put(subway, businessSet);
					}
				}
			}
		}

//		System.out.println(JSON.toJSONString(areaMap));
//		System.out.println(JSON.toJSONString(subwayMap));
	}
}
