package com.netease.crawl;

import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;



import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;

/**
 * @Description: Created with IntelliJ IDEA.
 * @Author: hzzhouwen
 * @Date: 2018/1/17 15:13
 */
public class HttpsUtils {

	public static void getPicByHttps(String url, Map<String, String> header) {
		CloseableHttpClient httpClient = getHttpClient();
		if (null == url || url.length() <= 0)
			throw new RuntimeException("url can not null");
		HttpGet httpGet = new HttpGet(url);
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

	private static CloseableHttpClient getHttpClient() {
		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
		ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
		registryBuilder.register("http", plainSF);
//指定信任密钥存储对象和连接套接字工厂
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			//信任任何链接
			TrustStrategy anyTrustStrategy = new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
					return true;
				}
			};
			SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, anyTrustStrategy).build();
			LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			registryBuilder.register("https", sslSF);
		} catch (KeyStoreException e) {
			throw new RuntimeException(e);
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		Registry<ConnectionSocketFactory> registry = registryBuilder.build();
		//设置连接管理器
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
//      connManager.setDefaultConnectionConfig(connConfig);
//      connManager.setDefaultSocketConfig(socketConfig);
		//构建客户端
		return HttpClientBuilder.create().setConnectionManager(connManager).build();
	}
}
