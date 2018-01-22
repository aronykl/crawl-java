package com.netease.crawl;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: Created with IntelliJ IDEA.
 * @Author: hzzhouwen
 * @Date: 2018/1/10 14:11
 */
public class LoginTest {

	public static void main(String[] args) {
		String url = "http://ssp.tuia.cn/account/login";
		Map<String, String> params = new HashMap<>();
		params.put("email", "wangyiyouliao@163.com");
		params.put("password", "hzwangyiyouliao163");
		params.put("remember", "false");

		Map<String, String> header = new HashMap<>();
		header.put("accept", "application/json");
		header.put("Accept-Encoding", "gzip, deflate");
		header.put("Accept-Language", "zh-CN,zh;q=0.8");
		header.put("Cache-Control", "no-cache");
		header.put("Connection", "keep-alive");
		header.put("content-type", "application/json");
		header.put("Cookie", "acw_tc=AQAAAKqUTjEoEQcAj6A6ewIxC42N7lkd; Hm_lvt_15bb94c252785556b43481b3b635df51=1515399174; Hm_lpvt_15bb94c252785556b43481b3b635df51=1515565029");
		header.put("Host", "ssp.tuia.cn");
		header.put("Origin", "http://ssp.tuia.cn");
		header.put("Pragma", "no-cache");
		header.put("Referer", "http://ssp.tuia.cn/index");
		header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
//		header.put("Content-Length", "82");

		String content = HttpUtils.httpPostWithJson(url, params, header);
		System.out.println(content);
	}
}
