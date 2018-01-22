package com.netease.crawl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.net.URLEncoder;
import java.util.*;

/**
 * 百度地图图片生成
 * @Author: hzzhouwen
 * @Date: 2018/1/17 10:08
 */
public class BaiduPicGenerate {

	private static final String baidu_url = "http://api.map.baidu.com/staticimage/v2?ak=MXdtHkVgvlsaFb6KoD62SaUKDvaQYRSm&width=640&height=480&zoom=9";

	private static final String google_url = "https://maps.googleapis.com/maps/api/staticmap?key=AIzaSyCqFYEDLl5TsfxhbnlFIz7F6MQmLTQrkpU&zoom=9&size=640x480&maptype=terrain";


	//根据经纬度来生成一张百度地图, 标注用M的红点 x：经度 y：纬度
	public void generatePicByXY(double x, double y) {
		String currUrl = baidu_url + "&center=" + x + "," + y + "&markers=" + x + "," + y + "&markerStyles=l,M";
		HttpUtils.getPicByHttp(currUrl, null);
	}

	//根据经纬度来生成一张谷歌地图, 标注用M的红点 x：经度 y：纬度
	public void getGooglePicByXY(double x, double y) {
		String currUrl = google_url + "&center=" + y + "," + x + "&markers=color:red%7Clabel:M%7C" + y + "," + x;
		HttpUtils.getPicByHttp(currUrl, null);
	}

	//根据经纬度找到附近的村镇，并划线显示距离  x：经度 y：纬度
	public void getTownPic(double x, double y) {
		//20公里以内的乡镇
		String townUrl = "http://api.map.baidu.com/place/v2/search?query=%E4%B9%A1%E9%95%87&location=" + y + "," + x + "&radius=20000&output=json&ak=MXdtHkVgvlsaFb6KoD62SaUKDvaQYRSm";
		//5公里以内的村庄
		String villageUrl = "http://api.map.baidu.com/place/v2/search?query=%E6%9D%91%E5%BA%84&location=" + y + "," + x + "&radius=5000&output=json&ak=MXdtHkVgvlsaFb6KoD62SaUKDvaQYRSm";

		List<Pos> posList = new ArrayList<>();
		Map<String, String> header = new HashMap<>();
		header.put("Host", "api.map.baidu.com");
		header.put("Cookie", "BAIDUID=62F0476AE5654185DAA8272493A2AE40:FG=1; FP_UID=190e77c572efeafc93b5b38a46bdeb23; BDUSS=HZXYmxVV3M0YmF3b2I1dmNGY1BqS1cwT0lOazZNSFdYT29lWnhvM0FpaEtLNFZhQVFBQUFBJCQAAAAAAAAAAAEAAABNpUARencxOTkxMDkyNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEqeXVpKnl1aR; BAIDU_SSP_lcr=https://www.google.com.hk/; MCITY=-%3A; pgv_pvi=712968448; pgv_si=s4923105280");
		header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
		try {
			String townData = HttpUtils.httpGet(townUrl, header);
			Map townMap = JsonUtil.fromJson(townData, Map.class);
			if (MapUtils.isNotEmpty(townMap)) {
				List<Map> maps = (List<Map>) townMap.get("results");
				if (CollectionUtils.isNotEmpty(maps)) {
					for (Map map : maps) {
						if (map.get("name").toString().endsWith("人民政府"))
							continue;
						Pos pos = new Pos();
						pos.setName(map.get("name").toString());
						Map location = (Map) map.get("location");
						pos.setLat(Double.parseDouble(location.get("lat").toString()));
						pos.setLng(Double.parseDouble(location.get("lng").toString()));
						pos.setDistance(getDistance(y, x, pos.getLat(), pos.getLng()));
						posList.add(pos);
					}
				}
			}

			// 根据查询到的乡镇坐标  来计算距离，划线画图
			// 划线的URl
			String url = "http://api.map.baidu.com/staticimage/v2?ak=MXdtHkVgvlsaFb6KoD62SaUKDvaQYRSm&center=" + x + "," + y + "&width=640&height=480&zoom=11&markers="
					+ x + "," + y + "&markerStyles=,,&paths=";
			for (int i = 0; i < posList.size(); i++) {
				if (i == posList.size() - 1) {
					url = url + x + "," + y + ";" + posList.get(i).getLng() + "," + posList.get(i).getLat();
				} else {
					url = url + x + "," + y + ";" + posList.get(i).getLng() + "," + posList.get(i).getLat() + "|";
				}
			}

			url += "&pathStyles=0x000fff,1,1&labels=";

			for (int i = 0; i < posList.size(); i++) {
				double midLng = (x + posList.get(i).getLng()) / 2;
				double midLat = (y + posList.get(i).getLat()) / 2;
				if (i == posList.size() - 1) {
					url = url + midLng + "," + midLat;
				} else {
					url = url + midLng + "," + midLat + "|";
				}
			}

			url += "&labelStyles=";

			for (int i = 0; i < posList.size(); i++) {
				if (i == posList.size() - 1) {
					url = url + posList.get(i).getDistance() + "公里" + ",1,15,0xFF0000,0xffffff,1";
				} else {
					url = url + posList.get(i).getDistance() + "公里" + ",1,15,0xFF0000,0xffffff,1" + "|";
				}
			}

			HttpUtils.getPicByHttp(url, header);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 根据经纬度找到附近的区县，并划线显示距离  x：经度 y：纬度
	public void getAreaPic(double x, double y) {
		// 获取以指定地点为中心的正方形区域的四个角的位置
		List<Pos> list = getNeighPosition(x, y, 100d);
		list.add(new Pos(x, y));
		// 获取这5个点的圆形区域的区县信息
		String preUrl = "http://api.map.baidu.com/place/v2/search?query=区县&radius=200000&output=json&ak=MXdtHkVgvlsaFb6KoD62SaUKDvaQYRSm&location=";
		// 根据名字去重
		Set<String> areaSet = new HashSet<>();
		//存放查到的县 以及其坐标
		List<Pos> areaPoses = new ArrayList<>();

		Map<String, String> header = new HashMap<>();
		header.put("Host", "api.map.baidu.com");
		header.put("Cookie", "BAIDUID=62F0476AE5654185DAA8272493A2AE40:FG=1; FP_UID=190e77c572efeafc93b5b38a46bdeb23; BDUSS=HZXYmxVV3M0YmF3b2I1dmNGY1BqS1cwT0lOazZNSFdYT29lWnhvM0FpaEtLNFZhQVFBQUFBJCQAAAAAAAAAAAEAAABNpUARencxOTkxMDkyNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEqeXVpKnl1aR; BAIDU_SSP_lcr=https://www.google.com.hk/; MCITY=-%3A; pgv_pvi=712968448; pgv_si=s4923105280");
		header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");

		try {
			for (Pos pos : list) {
				int pageNum = 0;//分页的页数，从0开始
				int totalCount = 0;
				boolean flag = true;
				while (flag) {
					String areaUrl = preUrl + pos.getLat() + "," + pos.getLng() + "&page_num=" + pageNum;
					String data = HttpUtils.httpGet(areaUrl, header);
					Thread.sleep(1000);
					Map map = JsonUtil.fromJson(data, Map.class);
					if (MapUtils.isNotEmpty(map)) {
						List<Map> posList = (List<Map>) map.get("results");
						if (CollectionUtils.isNotEmpty(posList)) {
							for (Map posMap : posList) {
								if (null != posMap.get("area")) {
									areaSet.add(posMap.get("area").toString());
									Pos pos1 = new Pos();
									pos1.setName(posMap.get("name").toString());
									pos1.setLat(Double.parseDouble(((Map)posMap.get("location")).get("lat").toString()));
									pos1.setLng(Double.parseDouble(((Map)posMap.get("location")).get("lng").toString()));
									pos1.setArea(posMap.get("area").toString());
									areaPoses.add(pos1);
								}
								totalCount++;
							}
							if (totalCount >= Integer.parseInt(map.get("total").toString())) {
								break;
							}
						} else {
							flag = false;
						}
					} else {
						flag = false;
					}

					pageNum++;
				}

			}

			List<Pos> resultPosList = new ArrayList<>();

			// 每个县只保留一个地点
			for (String area : areaSet) {
				Iterator<Pos> iterator = areaPoses.iterator();
				int count = 0;
				while (iterator.hasNext()) {
					if (count > 0)
						break;
					Pos pos = iterator.next();
					if (area.equals(pos.getArea()) && pos.getName().contains(area) && !pos.getName().contains("镇")) {
						count++;
						resultPosList.add(pos);
					}
				}
			}

//			// 根据区县名称查询其经纬度
//			String posPreUrl = "http://api.map.baidu.com/geocoder/v2/?output=json&ak=MXdtHkVgvlsaFb6KoD62SaUKDvaQYRSm&address=";
//			for (String name : areaSet) {
//				String url = posPreUrl + name + "政府";
//				String data = HttpUtils.httpGet(url, null);
//				Map map = JsonUtil.fromJson(data, Map.class);
//				if (MapUtils.isNotEmpty(map)) {
//					Map resultMap = (Map) map.get("result");
//					if (MapUtils.isNotEmpty(resultMap)) {
//						Map locationMap = (Map) resultMap.get("location");
//						if (MapUtils.isNotEmpty(locationMap)) {
//							Pos pos = new Pos();
//							pos.setName(name);
//							pos.setLng(Double.parseDouble(locationMap.get("lng").toString()));
//							pos.setLat(Double.parseDouble(locationMap.get("lat").toString()));
//							areaPoses.add(pos);
//						}
//					}
//				}
//			}

			// 根据查询到的坐标 计算距离  不在100公里以内的全部删除
			for (Iterator<Pos> iterator = resultPosList.iterator(); iterator.hasNext();) {
				Pos pos = iterator.next();
				if (getDistance(y, x, pos.getLat(), pos.getLng()) > 100) {
					iterator.remove();
				}
			}

			System.out.println(resultPosList.size());

			//计算出距离设置进去
			for (Pos pos : resultPosList) {
				pos.setDistance(getDistance(y, x, pos.getLat(), pos.getLng()));
			}

			//(x, y) 跟剩下的这些点  进行标注划线
			String picUrl = "http://api.map.baidu.com/staticimage/v2?ak=MXdtHkVgvlsaFb6KoD62SaUKDvaQYRSm&width=640&height=480&zoom=9&center=" + x + "," + y + "&markers=" + x + "," + y;

			for (Pos pos : resultPosList) {
				picUrl = picUrl + "|" + pos.getLng() + "," + pos.getLat();
			}

			picUrl += "&markerStyles=,,&paths=";

			for (Pos pos : resultPosList) {
				picUrl = picUrl + x + "," + y + ";" + pos.getLng() + "," + pos.getLat() + "|";
			}

			if (CollectionUtils.isNotEmpty(resultPosList)) {
				picUrl = picUrl.substring(0, picUrl.length() - 1);
			}

			picUrl += "&pathStyles=0x000fff,1,1&labels=";

			for (int i = 0; i < resultPosList.size(); i++) {
				double midLng = (x + resultPosList.get(i).getLng()) / 2;
				double midLat = (y + resultPosList.get(i).getLat()) / 2;
				if (i == resultPosList.size() - 1) {
					picUrl = picUrl + midLng + "," + midLat;
				} else {
					picUrl = picUrl + midLng + "," + midLat + "|";
				}
			}

			picUrl += "&labelStyles=";

			for (int i = 0; i < resultPosList.size(); i++) {
				if (i == resultPosList.size() - 1) {
					picUrl = picUrl + resultPosList.get(i).getDistance() + "公里" + ",1,15,0xFF0000,0xffffff,1";
				} else {
					picUrl = picUrl + resultPosList.get(i).getDistance() + "公里" + ",1,15,0xFF0000,0xffffff,1" + "|";
				}
			}

//			picUrl = URLEncoder.encode(picUrl, "utf-8");

			HttpUtils.getPicByHttp(picUrl, header);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取历史地震的图片  （x1, y1）震源 （x2, y2）历史最大地震 posList:历史地震
	public void getHistoryPic(double x1, double y1, double x2, double y2, List<Pos> posList) {

		Map<String, String> header = new HashMap<>();
		header.put("Host", "api.map.baidu.com");
		header.put("Cookie", "BAIDUID=62F0476AE5654185DAA8272493A2AE40:FG=1; FP_UID=190e77c572efeafc93b5b38a46bdeb23; BDUSS=HZXYmxVV3M0YmF3b2I1dmNGY1BqS1cwT0lOazZNSFdYT29lWnhvM0FpaEtLNFZhQVFBQUFBJCQAAAAAAAAAAAEAAABNpUARencxOTkxMDkyNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEqeXVpKnl1aR; BAIDU_SSP_lcr=https://www.google.com.hk/; MCITY=-%3A; pgv_pvi=712968448; pgv_si=s4923105280");
		header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");

		StringBuffer url = new StringBuffer("http://api.map.baidu.com/staticimage/v2?ak=MXdtHkVgvlsaFb6KoD62SaUKDvaQYRSm&width=640&height=480&zoom=7&center=")
				.append(x1).append(",").append(y1).append("&markers=").append(x1).append(",").append(y1).append("|").append(x2).append(",").append(y2);

		for (Pos pos : posList) {
			url.append("|").append(pos.getLng()).append(",").append(pos.getLat());
		}

//		Pos rightDownPos = getRightDownPicPosition(x1, y1);

//		url.append("|").append(rightDownPos.getLng()).append(",").append(rightDownPos.getLat());

		url.append("&markerStyles=l,,0xFF0000|-1,http://youliao-item.nos-eastchina1.126.net/writing/earthquake/img/middledot.png,-1");

		for (Pos pos : posList) {
			url.append("|-1,http://youliao-item.nos-eastchina1.126.net/writing/earthquake/img/smalldot.png,-1");
		}

//		url.append("|-1,http://youliao-item.nos-eastchina1.126.net/writing/earthquake/img/instruction.png,-1");

		// 获取200公里周围的的四个点
		List<Pos> fourPos = getNeighPosition(x1, y1, 200);
		url.append("&paths=").append(fourPos.get(0).getLng()).append(",").append(fourPos.get(0).getLat()).append(";")
				.append(fourPos.get(1).getLng()).append(",").append(fourPos.get(1).getLat()).append(";")
				.append(fourPos.get(2).getLng()).append(",").append(fourPos.get(2).getLat()).append(";")
				.append(fourPos.get(3).getLng()).append(",").append(fourPos.get(3).getLat())
				.append("&pathStyles=0x00BFFF,3,0.1,0x87CEFA");

		HttpUtils.getPicByHttp(url.toString(), header);
	}

	public static void main(String[] args) {
		BaiduPicGenerate generate = new BaiduPicGenerate();
//		generate.generatePicByXY(103.82d, 33.20d);

//		generate.getGooglePicByXY(103.82d, 33.20d);
//
//		System.out.println(getDistance(33.20d, 103.82d, 33.295003d, 103.882408d));

//		generate.getTownPic(103.82d, 33.20d);
//		generate.getAreaPic(103.82d, 33.20d);
//		System.out.println(getDistance(33.20d, 103.82d, 33.63481041973872, 104.32632271287576));

		List<Pos> list = getNeighPosition(103.82d, 33.20d, 200);
		System.out.println(list.get(0).getLng() + "," + list.get(0).getLat());
		System.out.println(list.get(1).getLng() + "," + list.get(1).getLat());
		System.out.println(list.get(2).getLng() + "," + list.get(2).getLat());
		System.out.println(list.get(3).getLng() + "," + list.get(3).getLat());

		List<Pos> posList = new ArrayList<>();
		posList.add(new Pos(102.70, 32.50));
		posList.add(new Pos(102.80, 32.50));
		posList.add(new Pos(102.80, 32.60));
		posList.add(new Pos(102d, 32d));
		posList.add(new Pos(103d, 32d));
		posList.add(new Pos(103, 33));
//
		generate.getHistoryPic(103.82d, 33.20d, 104.5, 33.5, posList);
	}



	private static final double EARTH_RADIUS = 6378.137;
	private static double rad(double d)
	{
		return d * Math.PI / 180.0;
	}

	public static int getDistance(double lat1, double lng1, double lat2, double lng2)
	{
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
				Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return new Double(s).intValue();
	}

	// 获取以指定地点为中心的正方形区域的四个角的位置
	private static List<Pos> getNeighPosition(double longitude, double latitude, double dis) {

		List<Pos> list = new ArrayList<>();

		//先计算查询点的经纬度范围
		double r = 6371;//地球半径千米
//		double dis = 100;//100千米距离
		double dlng =  2*Math.asin(Math.sin(dis/(2*r))/Math.cos(latitude*Math.PI/180));
		dlng = dlng*180/Math.PI;//角度转为弧度
		double dlat = dis/r;
		dlat = dlat*180/Math.PI;
		double minlat =latitude-dlat;
		double maxlat = latitude+dlat;
		double minlng = longitude -dlng;
		double maxlng = longitude + dlng;

		list.add(new Pos(minlng, minlat));
		list.add(new Pos(minlng, maxlat));
		list.add(new Pos(maxlng, maxlat));
		list.add(new Pos(maxlng, minlat));

		return list;
	}


	// 获取指定经纬度，右下角图片的位置
	public static Pos getRightDownPicPosition(double longitude, double latitude) {
		Pos pos = new Pos();

		double r = 6371;//地球半径千米
		double xDis = 400;//经度距离 440Km
		double yDis = 300;//纬度距离 320KM

		double dlng =  2*Math.asin(Math.sin(xDis/(2*r))/Math.cos(latitude*Math.PI/180));
		dlng = dlng*180/Math.PI;//角度转为弧度

		double dlat = yDis/r;
		dlat = dlat*180/Math.PI;

		pos.setLng(longitude + dlng);
		pos.setLat(latitude - dlat);

		return pos;
	}



}
