package com.netease.crawl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: Created with IntelliJ IDEA.
 * @Author: hzzhouwen
 * @Date: 2017/11/21 19:29
 */
public class HttpUtils {

	/**
	 * 发送一个post请求
	 * @param url 请求url
	 * @param params 请求参数
	 * @return
	 */
	public static String httpPost(String url, Map<String, String> params, Map<String, String> header) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		if (null == url || url.length() <= 0)
			throw new RuntimeException("url can not null");
		HttpPost httpPost = new HttpPost(url);
		if (MapUtils.isNotEmpty(header)) {
			header.forEach(httpPost::addHeader);
		}
		List<NameValuePair> nameValuePairs = new ArrayList<>();
		if (MapUtils.isNotEmpty(params)) {
			params.forEach((key, value) -> nameValuePairs.add(new BasicNameValuePair(key, value)));
		}
		CloseableHttpResponse response = null;
		String content = null;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//			httpPost.setEntity();
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				content = "http return code is not 200 : " + response.getStatusLine().getStatusCode();
				System.err.println(content);
				return content;
			}
			HttpEntity httpEntity = response.getEntity();
			if (null == httpEntity) {
				content = "http response entity is null.";
				return content;
			}
			content = new BufferedReader(new InputStreamReader(httpEntity.getContent())).lines().collect(Collectors.joining());
		} catch (IOException e) {
			e.printStackTrace();
			content = "http post error.";
		} finally {
			try {
				if (null != response) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}

	public static String httpGet(String url, Map<String, String> header) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		if (null == url || url.length() <= 0)
			throw new RuntimeException("url can not null");
		HttpGet httpGet = new HttpGet(url);
		if (MapUtils.isNotEmpty(header)) {
			header.forEach(httpGet::setHeader);
		}
		CloseableHttpResponse response = null;
		String content = null;
		try {
			response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				content = "http return code is not 200 : " + response.getStatusLine().getStatusCode();
				System.err.println(content);
				throw new Exception(content);
//				return content;
			}
			HttpEntity httpEntity = response.getEntity();
			if (null == httpEntity) {
				content = "http response entity is null.";
				System.err.println(content);
				return content;
			}
			content = new BufferedReader(new InputStreamReader(httpEntity.getContent())).lines().collect(Collectors.joining());
		} catch (Exception e) {
			e.printStackTrace();
			content = "http post error.";
			System.out.println(url);
			throw e;
		} finally {
			try {
				if (null != response) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}

	public static String httpPostWithJson(String url, Map<String, String> params, Map<String, String> header) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		if (null == url || url.length() <= 0)
			throw new RuntimeException("url can not null");
		HttpPost httpPost = new HttpPost(url);
		if (MapUtils.isNotEmpty(header)) {
			header.forEach(httpPost::addHeader);
		}
//		List<NameValuePair> nameValuePairs = new ArrayList<>();
//		if (MapUtils.isNotEmpty(params)) {
//			params.forEach((key, value) -> nameValuePairs.add(new BasicNameValuePair(key, value)));
//		}
		CloseableHttpResponse response = null;
		String content = null;
		try {
//			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httpPost.setEntity(new StringEntity(JSON.toJSONString(params), ContentType.APPLICATION_JSON));
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				content = "http return code is not 200 : " + response.getStatusLine().getStatusCode();
				System.err.println(content);
				return content;
			}
			HttpEntity httpEntity = response.getEntity();
			if (null == httpEntity) {
				content = "http response entity is null.";
				return content;
			}
			content = new BufferedReader(new InputStreamReader(httpEntity.getContent())).lines().collect(Collectors.joining());
		} catch (IOException e) {
			e.printStackTrace();
			content = "http post error.";
		} finally {
			try {
				if (null != response) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}

	public static void getPicByHttp(String url, Map<String, String> header) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		if (null == url || url.length() <= 0)
			throw new RuntimeException("url can not null");
		URI uri = null;
		try {
			URL url1 = new URL(url);
			uri = new URI(url1.getProtocol(), url1.getHost(), url1.getPath(), url1.getQuery(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpGet httpGet = new HttpGet(uri);
		if (MapUtils.isNotEmpty(header)) {
			header.forEach(httpGet::setHeader);
		}
		CloseableHttpResponse response = null;
		String content = null;
		String filePathName = "";
		try {
			response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				content = "http return code is not 200 : " + response.getStatusLine().getStatusCode();
				System.err.println(content);
				throw new Exception(content);
//				return content;
			}
			HttpEntity httpEntity = response.getEntity();
			if (null == httpEntity) {
				content = "http response entity is null.";
				System.err.println(content);
			}
			String date = DateUtil.DateToString(new Date());
			date = date.substring(0, date.indexOf(" "));
			String rootDirectory = "/home/commonrec/gatherhot/pic/";
			if(OSUtils.isWin()){
				rootDirectory = "D:/pic/";
			}
			rootDirectory = rootDirectory + date;

			File file = new File(rootDirectory);
			if(!file.exists()){
				file.mkdirs();
			}
			filePathName = rootDirectory + "/" + new Date().getTime() + ".png";
			File storeFile = new File(filePathName);
			FileOutputStream output = new FileOutputStream(storeFile);

			InputStream instream = httpEntity.getContent();

			byte b[] = new byte[1024];
			int j = 0;
			while( (j = instream.read(b))!=-1){
				output.write(b,0,j);
			}
			output.flush();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
//			content = "http post error.";
			System.out.println(url);
		} finally {
			try {
				if (null != response) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
