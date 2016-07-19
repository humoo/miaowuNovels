package com.zxm.miaowunovels.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zxm.miaowunovels.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainAdapter extends BaseAdapter {

	List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	Map<String, String> map;
	Context context;
	File fileDir,file;
	
	public MainAdapter(Context context, List<Map<String, String>> list) {

		this.context = context;
		fileDir=new File(Environment.getExternalStorageDirectory()+"/miaowu/pics");
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		
		if (list != null) {
			this.list = list;
		}
	}

	public void setDataList(List<Map<String, String>> list2) {
		this.list = list2;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		Log.e("loge", "list.size()"+list.size());
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view =LayoutInflater.from(context).inflate(R.layout.main_item, null, false);
		TextView bookName = (TextView) view.findViewById(R.id.tv_bookname);
		TextView bookNew = (TextView) view.findViewById(R.id.tv_booknew);
		ImageView imageView=(ImageView) view.findViewById(R.id.iv_book_pic);
		
		
		map=new HashMap<>();
		map=list.get(position);
		
		file=new File(fileDir,map.get("bookId")+".jpg");
		if (file.exists()&&file.length()>3*1024) {
			Bitmap bitmap=BitmapFactory.decodeFile(file.getAbsolutePath());
			imageView.setImageBitmap(bitmap);
		}
		bookName.setText(map.get("bookName"));
		bookNew.setText(map.get("bookNew"));
		Log.e("loge","新的----"+map.get("bookName")+"--" +map.get("bookNew"));
		return view;
	}

}
