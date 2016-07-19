package com.zxm.miaowunovels.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zxm.miaowunovels.DBOpenHelper;
import com.zxm.miaowunovels.R;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;

public class ReadTxtFragment extends Fragment{
	DBOpenHelper dbHepler;
	SQLiteDatabase db;
	WebView webView;
	List<Map<String, String>> data=new ArrayList<Map<String, String>>();//db数据集合
	Map<String, String> dbMap;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view =inflater.inflate(R.layout.readtxt_frg, container,false);
		webView=(WebView) view.findViewById(R.id.webview);
		webView.loadUrl(getArguments().getString("bookurl"));
//		webView.set
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setWebViewClient(new WebViewClient(){
	         @Override
	         public boolean shouldOverrideUrlLoading(WebView view, String url) {
	 
	          view.loadUrl(url);   //在当前的webview中跳转到新的url
	 
	          return true;
	         }
	        });
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
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
        		
        		if (webView.getUrl().contains(".html") && !webView.getUrl().contains("wap")  && webView.getUrl().contains("book/"+data.get(i).get("bookId"))) {
        			flag=true;
        			k=i;
        			break;
				}
        		
			}
        	if (flag) {//有id,自动存入数据库
        		ContentValues values=new ContentValues();
				values.put("bookId", webView.getUrl().split("book/")[1].split("/")[0]);
				values.put("bookName", webView.getTitle().split("_")[1].split("_")[0]);
				values.put("bookNew", webView.getTitle().split("_")[0]);//最新一章
				values.put("bookTime", "");
				values.put("bookUrl", webView.getUrl());
				values.put("bookPic", "");
				db.update("bookInfo", values, "bookId = ? ",new String[]{webView.getUrl().split("book/")[1].split("/")[0]});
				getFragmentManager().popBackStack();
			}
            
        }else {
        	getFragmentManager().popBackStack();
		}
    }
}
