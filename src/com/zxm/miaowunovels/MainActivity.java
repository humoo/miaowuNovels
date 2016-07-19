package com.zxm.miaowunovels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.zxm.miaowunovels.adapter.MainAdapter;
import com.zxm.miaowunovels.fragment.ReadTxtFragment;
import com.zxm.miaowunovels.fragment.SearchFragment;

public class MainActivity extends FragmentActivity {

	private LinearLayout ll_search, ll_more;
	private SearchFragment searchFragment=null;
	private FragmentManager manager;
	ReadTxtFragment readTxtFragment=null;
	private SwipeMenuListView listView;
	List<Map<String, String>> data=new ArrayList<Map<String, String>>();//db数据集合
	Map<String, String> dbMap;
	DBOpenHelper dbHepler;
	SQLiteDatabase db;
	MainAdapter adapter;
	
	SwipeMenuCreator creator = new SwipeMenuCreator() {
		@Override
		public void create(SwipeMenu menu) {
			// create "open" item
			SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());

			SwipeMenuItem deleteItem = new SwipeMenuItem(
					getApplicationContext());
			// set item background
			deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F,
					0x25)));
			// set item width
			deleteItem.setWidth(180);
			// set a icon
			//deleteItem.setIcon(R.drawable.search);
			deleteItem.setTitle("Delete");
			deleteItem.setTitleColor(Color.WHITE);
			deleteItem.setTitleSize(18);
			// add to menu
			menu.addMenuItem(deleteItem);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		manager = getSupportFragmentManager();
		init();
		 
		dbHepler=new DBOpenHelper(this);
		db=dbHepler.getWritableDatabase();
		queryFromDB();
		
		adapter=new MainAdapter(getApplicationContext(), data);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				readTxtFragment = new ReadTxtFragment();
				Bundle bundle = new Bundle();  
				bundle.putString("bookurl", data.get(position).get("bookUrl"));  
				readTxtFragment.setArguments(bundle);
				manager.beginTransaction()
				
						.setTransition(
								FragmentTransaction.TRANSIT_FRAGMENT_FADE)
						.replace(R.id.main_layout, readTxtFragment)
						.addToBackStack("").commit();
				
			}
		});
	}

	private void init() {

		ll_search = (LinearLayout) findViewById(R.id.ll_search);
		ll_more = (LinearLayout) findViewById(R.id.ll_more);
		listView = (SwipeMenuListView) findViewById(R.id.listView);
		listView.setMenuCreator(creator);
		listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					// delete 将纪录从数据库删除，有本地文件提示删除
					
					break;
				}
			}

		});
		ll_search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				searchFragment = new SearchFragment();
				manager.beginTransaction()
						.setTransition(
								FragmentTransaction.TRANSIT_FRAGMENT_FADE)
						.replace(R.id.main_layout, searchFragment)
						.addToBackStack("").commit();
			}
		});
	}
	
	@Override
    public void onBackPressed() {
        if (searchFragment != null && searchFragment.canGoBack()) {
        	searchFragment.goBack();
        }else {
        	super.onBackPressed();
        	Log.e("loge", "onResume()");
        	queryFromDB();
        	adapter.setDataList(data);
		}
        
        if (readTxtFragment != null && readTxtFragment.canGoBack()) {
        	readTxtFragment.goBack();
        	readTxtFragment=null;
        	Log.e("loge", "onResume()222");
        	  queryFromDB();
        	  listView.setAdapter(adapter);
          	adapter.setDataList(data);        

        }
      
        
//        super.onBackPressed();
    }
	
	/**
	 * 查询数据库
	 */
	private void queryFromDB() {
		Cursor cursor=db.rawQuery("select * from bookInfo ", null);
		data.clear();
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
	
}
