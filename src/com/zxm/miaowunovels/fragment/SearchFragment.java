package com.zxm.miaowunovels.fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.zxm.miaowunovels.DBOpenHelper;
import com.zxm.miaowunovels.R;
import com.zxm.miaowunovels.adapter.SearchFragmentAdapter;
import com.zxm.miaowunovels.utils.GetMoreNameThread;

public class SearchFragment extends Fragment{
	LinearLayout ll_search,ll_back;
	private EditText editText;
	GetMoreNameThread getMoreNameThread;
	String TAG_MORE="TAG_MORE";//模糊书名查询
	String TAG_CUNRRENT="TAG_CUNRRENT";//精准书名查询
	List<Map<String, String>> list=new ArrayList<Map<String, String>>();
	List<Map<String, String>> data=new ArrayList<Map<String, String>>();//db数据集合
	Map<String, String> map,dbMap;
	SearchFragmentAdapter adapter;
	ListView listView;
	ReadTxtFragment readTxtFragment;
	WebView webView;
	DBOpenHelper dbHepler;
	SQLiteDatabase db;
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			list=getMoreNameThread.getDataList();
			adapter.setDataList(list);
			
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view =inflater.inflate(R.layout.search_frg, container,false);
		ll_back=(LinearLayout) view.findViewById(R.id.ll_back);
		ll_search=(LinearLayout) view.findViewById(R.id.ll_search);
		editText=(EditText) view.findViewById(R.id.ed_text);
		listView=(ListView) view.findViewById(R.id.listView);
		listView.setVisibility(View.INVISIBLE);
		webView=(WebView) view.findViewById(R.id.webview);
		webView.loadUrl("http://m.biquge.la/");
		webView.getSettings().setJavaScriptEnabled(false); 		//不允许js
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setWebViewClient(new WebViewClient(){
	         @Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

	        	if (url.contains("http://so.biquge.la/cse/search?")) {
	        		 webView.getSettings().setJavaScriptEnabled(true); 		//允许js
				}else {
					webView.getSettings().setJavaScriptEnabled(false); 		//不允许js
				}
				view.loadUrl(url); // 在当前的webview中跳转到新的url

				return true;
			}

	         
		});
		
		
		ll_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStack();		
			}
		});
		
		ll_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//输入框不为空
				if (!editText.getText().toString().trim().equals("")) {
						getMoreNameThread=new GetMoreNameThread("",editText.getText().toString().trim(),TAG_MORE,handler);
						getMoreNameThread.start();
						listView.setVisibility(View.VISIBLE);
				}
				
			}
		});
		
		
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				map=list.get(position);
				if (map.get("bookNet").equals("")) {
					getMoreNameThread=new GetMoreNameThread(map.get("bookUrl"),map.get("bookName"),TAG_CUNRRENT,handler);
					getMoreNameThread.start();
				}else {
					readTxtFragment = new ReadTxtFragment();
					Bundle bundle = new Bundle();  
					bundle.putString("bookurl", map.get("bookUrl"));  
					
					readTxtFragment.setArguments(bundle);
					getFragmentManager().beginTransaction()
					
							.setTransition(
									FragmentTransaction.TRANSIT_FRAGMENT_FADE)
							.replace(R.id.main_layout, readTxtFragment)
							.addToBackStack(null)
							.commit();
					//getFragmentManager().popBackStack();		
				}
				

				
			}
		});
		
		return view;
	}
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		adapter=new SearchFragmentAdapter(getActivity(),list);
		dbHepler=new DBOpenHelper(getActivity());
		db=dbHepler.getWritableDatabase();
		
		
		
		super.onCreate(savedInstanceState);
	}
	
	private void queryFromDB() {
		
		Cursor cursor=db.rawQuery("select * from bookInfo ", null);
		if (cursor.moveToFirst()) {
			do{
				dbMap=new HashMap<String, String>();
//				Log.d("logd", cursor.getString(cursor.getColumnIndex("songName")));
				dbMap.put("bookId", cursor.getString(cursor.getColumnIndex("bookId")));
				dbMap.put("bookName", cursor.getString(cursor.getColumnIndex("bookName")));
				dbMap.put("bookNew", cursor.getString(cursor.getColumnIndex("bookNew")));
				dbMap.put("bookTime", cursor.getString(cursor.getColumnIndex("bookTime")));
				dbMap.put("bookUrl", cursor.getString(cursor.getColumnIndex("bookUrl")));
				dbMap.put("bookPic", cursor.getString(cursor.getColumnIndex("bookPic")));
				data.add(dbMap);
				
			}while(cursor.moveToNext());
		}
		
		
	}




	public boolean canGoBack() {
        return webView != null && webView.canGoBack();
    }
	
	
	int k=0;//有id时下脚标
    public void goBack() {
        if (webView != null) {
        	//读取数据库
    		queryFromDB();
        	//判断数据库是否存在该书//book ID
        	//有的话自动保存进度
        	boolean flag=false;
           
        	for (int i = 0; i < data.size(); i++) {
        		
        		if (webView.getUrl().contains(".html") 
        				&& !webView.getUrl().contains("wap")
        				&& webView.getUrl().contains("book/"+data.get(i).get("bookId")+"/")) {
        			Log.d("logd", "1:"+webView.getUrl().contains(".html"));
        			Log.d("logd", "2:"+!webView.getUrl().contains("wap"));
        			Log.d("logd", "3:"+webView.getUrl().contains(data.get(i).get("bookId")));
        			
        			flag=true;
        			k=i;
        			break;
				}
        		
			}
        	Log.d("logd", "webView.getUrl():"+webView.getUrl());
        	//当数据库没有此id，并且页面含html
        	if (webView.getUrl().contains(".html") 
        			&& !webView.getUrl().contains("wap")
        			&& !flag) {
        		Log.d("logd", "提示");
				//提示是否加入数据库
        		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        		builder.setMessage("\n是否将其加入阅读列表？\n");
        		builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//操作数据库
						ContentValues values=new ContentValues();
						values.put("bookId", webView.getUrl().split("book/")[1].split("/")[0]);
						values.put("bookName", webView.getTitle().split("_")[1].split("_")[0]);
						values.put("bookNew", webView.getTitle().split("_")[0]);//最新一章
						values.put("bookTime", "");
						values.put("bookUrl", webView.getUrl());
						values.put("bookPic", "");
						db.insert("bookInfo", null, values);
						

						// 在书的主页，开始下载书的封面
						final String urlString=webView.getUrl().substring(0, webView.getUrl().lastIndexOf("/"))+"/";
						/**
				          * 下载封面
				          * @param url
				          */
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									URL url=new URL(urlString);
									Log.e("loge", "下载图片url:"+url);
									
									HttpURLConnection connection=(HttpURLConnection) url.openConnection();
									connection.setRequestMethod("GET");
									connection.setConnectTimeout(5000);
									
									BufferedReader reader = new BufferedReader(
											new InputStreamReader(connection.getInputStream()));
									StringBuilder builder = new StringBuilder();
									String line = "";
									while ((line = reader.readLine()) != null) {
										builder.append(line + "\n");
									}
									reader.close();
									
									String[] htmlArr = builder.toString().split("\n");
									String picUrl="";
									for (int i = 0; i < htmlArr.length; i++) {
										if (htmlArr[i].contains("http://www.biquge.la/files/article/image")) {
											
											//Log.e("loge", "jpg:"+htmlArr[i].split("\"")[1].split("\"")[0]);
											picUrl=htmlArr[i].split("\"")[1].split("\"")[0];
										}
									}
									
									URL url2=new URL(picUrl);
									Log.e("loge", "下载图片url2:"+url2);
									
									HttpURLConnection connection2=(HttpURLConnection) url2.openConnection();
									connection2.setRequestMethod("GET");
									connection2.setConnectTimeout(5000);
									
									if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
										File fileDir=new File(Environment.getExternalStorageDirectory()+"/miaowu/pics");
										if (!fileDir.exists()) {
											fileDir.mkdirs();
										}
										File file=new File(fileDir,
												urlString.split("book/")[1]
														.substring(0,urlString.split("book/")[1].length()-1)+".jpg");
										Log.e("loge", "下载图片储存地址:"+file.getAbsolutePath());
										
										FileOutputStream fos=new FileOutputStream(file);
										InputStream in=connection2.getInputStream();
										
										int len=-1;
										byte[] buffer=new byte[256];
										while ((len=in.read(buffer))!=-1) {
											fos.write(buffer,0,len);
										}
										
										fos.close();
										in.close();
										
										
									}
									
									
									
									
								} catch (MalformedURLException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}).start();

					
						webView.goBack();
					}
				});
        		builder.setNegativeButton("不要",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						webView.goBack();
					}
				} );
        		builder.create().show();
        		
			}
        	
        	if (flag) {//有id,自动存入数据库
        		Log.d("logd", "自动存入数据库");

        		ContentValues values=new ContentValues();
				values.put("bookId", webView.getUrl().split("book/")[1].split("/")[0]);
				values.put("bookName", webView.getTitle().split("_")[1].split("_")[0]);
				values.put("bookNew", webView.getTitle().split("_")[0]);//最新一章
				values.put("bookTime", "");
				values.put("bookUrl", webView.getUrl());
				values.put("bookPic", "");
				db.update("bookInfo", values, "bookId = ? ",new String[]{webView.getUrl().split("book/")[1].split("/")[0]});
        		webView.goBack();
			}

        	Log.e("loge",webView.getTitle() + webView.getUrl()+"   ---"+webView.getContext() );
        	
        	if (!webView.getUrl().contains(".html")||webView.getUrl().contains("wap")) {
        		Log.d("logd", "webView.goBack();"+webView.getUrl());
        		Log.d("logd", "webView.getUrl().contains(\"wap\"):"+webView.getUrl().contains("wap"));
        		Log.d("logd", "!webView.getUrl().contains(\".html\"):"+!webView.getUrl().contains(".html"));
        		webView.goBack();
			}

            
        }
    }
	
}
