package com.netease.crawl;

import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.helper.StringUtil;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @Description: Created with IntelliJ IDEA.
 * @Author: hzzhouwen
 * @Date: 2017/12/14 16:46
 */
public class OnlyOneService {

	private static final String URL_PRE = "http://map.baidu.com/su?cid=179&type=0&newmap=1&b=(12956223.29790208%2C4826291.400174886%3B12965953.376573408%2C4835148.499825235)&t=1513242659774&pc_ver=2";

	public static void main(String[] args) throws Exception {
		// 1. 从本地读取位置 到内存
		List<String> positionList = new ArrayList<>();

//		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:/position.txt"), "utf-8"));
//
//		reader.lines().forEach(line -> {
//			String[] infos = line.split("\t");
//			if (null != infos && infos.length > 0) {
//				positionList.add(infos[0]);
//			}
//		});

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:/result11111.txt")));
		List<Position> result = reader.lines().map(line -> {
			Position position = new Position();
			String[] infos = line.split("\t");
			if (infos.length >= 1) {
				position.setName(infos[0]);
			}
			if (infos.length >= 2) {
				position.setIsUnique(Integer.parseInt(infos[1]));
			}
			if (infos.length >= 3) {
				position.setBelongTo(infos[2]);
			}
			return position;
		}).collect(Collectors.toList());

		List<String> lastList = new ArrayList<>();
//
//		lastList = positionList.parallelStream().filter(str -> !result.contains(str)).collect(Collectors.toList());

		List<Position> needRetryList = result.parallelStream().filter(position -> position.getIsUnique() < 0).collect(Collectors.toList());
		List<Position> noNeedRretryList = result.parallelStream().filter(position -> position.getIsUnique() >= 0).collect(Collectors.toList());
		lastList = needRetryList.parallelStream().map(Position::getName).collect(Collectors.toList());

		Map<String, String> header = new HashMap<>();
		header.put("Host", "map.baidu.com");
		header.put("Pragma", "no-cache");
		header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");

		//存放所有处理后的位置信息
		List<Position> positions = new ArrayList<>();
		positions.addAll(noNeedRretryList);
		AtomicLong count = new AtomicLong(0);//查不到结果的
//		for (String name : positionList) {
		for (String name : lastList) {
			String jsonStr = null;
			try {
				jsonStr = HttpUtils.httpGet(URL_PRE + "&wd=" + name, header);
				Thread.sleep(200);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("再次发送请求， name:" + name);
				jsonStr = HttpUtils.httpGet(URL_PRE + "&wd=" + name, header);
			}
			if (!StringUtil.isBlank(jsonStr)) {
				try {
					Map map = JsonUtil.fromJson(jsonStr, Map.class);
					List<String> results = (List<String>) map.get("s");
					Position position = new Position();
					position.setName(name);
					if (CollectionUtils.isEmpty(results)) {
						position.setIsUnique(-1);
					} else {
						List<String> res = new ArrayList<>();
						res.addAll(results);
						for (Iterator<String> iterator = res.iterator(); iterator.hasNext();) {
							String str = iterator.next();
							str = str.split("\\$")[3];
							if (!str.equals(name) && !str.startsWith(name + "-")) {
								iterator.remove();
							}
						}
						if (res.size() == 0) {
							position.setIsUnique(-1);
						} else if (res.size() == 1) {
							position.setIsUnique(1);
						} else {//大于1的情况要单独判断是不是唯一的
							String city = res.get(0).split("\\$")[0];
							String county = res.get(0).split("\\$")[1];
							int flag = 0;//0表示多次的市和县都是一样的
							for (int i = 1; i < res.size(); i++) {
								String cityTmp = res.get(i).split("\\$")[0];
								String countyTmp = res.get(i).split("\\$")[1];
								if (!city.equals(cityTmp) || !county.equals(countyTmp)) {
									flag = 1;
									break;
								}
							}
							if (flag == 0) {
								position.setIsUnique(1);
							} else {
								position.setIsUnique(0);
							}
						}
					}
					if (position.getIsUnique() == 1) {
						position.setBelongTo(results.get(0).split("\\$\\$")[0]);
					}
					positions.add(position);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Position position = new Position();
				position.setName(name);
				position.setIsUnique(-2);
				count.incrementAndGet();
			}
		}

		System.err.println("查不到结果的条数：" + count.get());

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:/result22222.txt", true)));
		for (Position position : positions) {
			String line = position.getName() + "\t" + position.getIsUnique();
			if (null != position.getBelongTo()) {
				line += ("\t" + position.getBelongTo());
			}
			line += "\n";
			writer.write(line);
		}

		reader.close();
		writer.close();
	}


}
