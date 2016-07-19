package com.zxm.miaowunovels.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class GetMoreNameThread extends Thread {
	String bookName;
	String TAG = "";
	List<Map<String, String>> list=new ArrayList<Map<String, String>>();
	Map<String,String> map;
	String bookUrl;
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			Log.e("loge", "ThreadHandler");

		};
	};
	public GetMoreNameThread(String bookUrl, String bookName,String TAG,  Handler handler) {
		this.bookUrl=bookUrl;
		this.bookName = bookName;
		this.TAG = TAG;
		this.handler=handler;
	}

	@Override
	public void run() {
		try {
			if (TAG.equals("TAG_MORE")) {
				
				list.clear();
				
				URL url;
				bookName=new String(bookName.getBytes("utf-8"),"ISO-8859-1");
				url = new URL("http://www.sodu.cc/result.html?searchstr="+bookName);
				Log.e("loge","bookName:"+bookName+"url:"+url.toString());
				
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				// 设置通用属性
				conn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				//conn.setRequestProperty("accept", "*/*");
				conn.setRequestProperty("connection", "Keep-Alive");
				conn.setRequestProperty("Content-Type","text/html;charset=utf-8"); 
				conn.setRequestProperty("user-agent", "NOKIA5700/ UCWEB7.0.2.37/28/999");
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				//conn.connect();

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(conn.getInputStream(), "utf-8"));
				StringBuilder builder = new StringBuilder();
				String line = "";
				while ((line = reader.readLine()) != null) {
					builder.append(line + "\n");
				}

				reader.close();
				conn.disconnect();
				String html = new String(builder.toString().getBytes("UTF-8"),
						"UTF-8");
				System.out.println(html);

				String[] htmlArr = html.split("\n");

				for (int i = 0; i < htmlArr.length; i++) {
					if (htmlArr[i].contains("style=\"width:188px;float:left")) {
						// System.out.println(htmlArr[i]);
						// System.out.println(htmlArr[i+1]);
						// System.out.println(htmlArr[i+2]);

						System.out.println("网址："
								+ htmlArr[i].split(">")[1].substring(9,
										htmlArr[i].split(">")[1].length() - 1));
						System.out.println("书名："
								+ htmlArr[i].split(">")[2].substring(0,
										htmlArr[i].split(">")[2].length() - 3));
						System.out.println("最新章："
								+ htmlArr[i + 1].split(">")[2]
										.substring(0,
												htmlArr[i + 1].split(">")[2]
														.length() - 3));
						System.out.println("最新时间："
								+ htmlArr[i + 2].split(">")[1]
										.substring(0,
												htmlArr[i + 2].split(">")[1]
														.length() - 5));

						Log.e("loge","书名："
								+ htmlArr[i].split(">")[2].substring(0,
										htmlArr[i].split(">")[2].length() - 3));
						
						map=new HashMap<String,String>();
						map.put("bookName", htmlArr[i].split(">")[2].substring(0,
										htmlArr[i].split(">")[2].length() - 3));
						map.put("bookUrl", htmlArr[i].split(">")[1].substring(9,
										htmlArr[i].split(">")[1].length() - 1));
						map.put("newCap", htmlArr[i + 1].split(">")[2]
								.substring(0,htmlArr[i + 1].split(">")[2].length() - 3));
						map.put("newTime",htmlArr[i + 2].split(">")[1]
								.substring(0,htmlArr[i + 2].split(">")[1].length() - 5));
						map.put("bookNet","");
						list.add(map);
						
					}
				}

			} else if (TAG.equals("TAG_CUNRRENT")) {

				URL url=new URL(bookUrl);
				//url=new URL(new String(url.toString().getBytes("UTF-8"),"gbk"));
				System.out.println(url.toString());
				HttpURLConnection conn= (HttpURLConnection) url.openConnection();
				
				 // 设置通用的请求属性
				conn.setRequestProperty("connection", "Keep-Alive");
				conn.setRequestProperty("Content-Type","text/html;charset=utf-8"); 
				conn.setRequestProperty("user-agent", "NOKIA5700/ UCWEB7.0.2.37/28/999");
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				//conn.connect();
				
				BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
				StringBuilder builder=new StringBuilder();
				String line="";
				while ((line=reader.readLine())!=null) {
					builder.append(line+"\n");
				}	
				
				reader.close();
				conn.disconnect();
				String html=new String(builder.toString().getBytes("UTF-8"),"UTF-8");
				System.out.println(html);

				String[] htmlArr=html.split("\n");
				//String[] detailArr;
				
				for (int i = 0; i < htmlArr.length; i++) {
					if (htmlArr[i].contains("style=\"width:560px;float:left;\"><a href")) {
//						System.out.println(htmlArr[i]);
//						System.out.println(htmlArr[i+1]);
//						System.out.println(htmlArr[i+2]);
						
						Log.d("logd","网址："+htmlArr[i].split("chapterurl=")[1].split("\" alt=\"")[0]);
						Log.d("logd","最新章："+htmlArr[i].split("chapterurl=")[1].split("\" alt=\"")[1].split("\" onclick=\"")[0]);
						Log.d("logd","网站名称："+htmlArr[i+1].split("class=\"tl\">")[1].split("</a>")[0]);
						Log.d("logd","最新时间："+htmlArr[i+2].split("class=\"xt1\">")[1].split("</div>")[0]);
						
						map=new HashMap<String,String>();
						map.put("bookName", bookName);
						map.put("bookUrl", htmlArr[i].split("chapterurl=")[1].split("\" alt=\"")[0]);
						map.put("newCap", htmlArr[i].split("chapterurl=")[1].split("\" alt=\"")[1].split("\" onclick=\"")[0]);
						map.put("newTime",htmlArr[i+2].split("class=\"xt1\">")[1].split("</div>")[0]);
						map.put("bookNet",htmlArr[i+1].split("class=\"tl\">")[1].split("</a>")[0]);//网站名称
						list.add(map);
					}
					
					
				}
				
				
			}

			handler.sendEmptyMessage(0);
			
		} catch (MalformedURLException e) {
			Log.d("loge", "url错误");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			Log.d("loge", "不支持的编码格式");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("loge", "io错误");
			e.printStackTrace();
		}

	}
	
	public List<Map<String, String>> getDataList() {
		
		return list;
		
	}
	  
}
