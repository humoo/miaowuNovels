package com.zxm.miaowunovels.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zxm.miaowunovels.R;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchFragmentAdapter extends BaseAdapter {

	List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	Map<String, String> map;
	Context context;

	public SearchFragmentAdapter(Context context, List<Map<String, String>> list) {

		this.context = context;

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

		View view = LinearLayout.inflate(context, R.layout.search_item, null);

		TextView bookName = (TextView) view.findViewById(R.id.tv_bookname);
		TextView newTime = (TextView) view.findViewById(R.id.tv_newtime);
		TextView newCap = (TextView) view.findViewById(R.id.tv_newCap);
		TextView bookNet = (TextView) view.findViewById(R.id.tv_bookNet);

		map = list.get(position);
		bookName.setText(map.get("bookName"));
		newTime.setText(map.get("newTime"));
		if (map.get("bookUrl").contains("mulu_")) {
			newCap.setVisibility(View.GONE);
			bookNet.setVisibility(View.GONE);
		}else {
			newCap.setVisibility(View.VISIBLE);
			bookNet.setVisibility(View.VISIBLE);
			newCap.setText(map.get("newCap"));
			bookNet.setText(map.get("bookNet"));
		}
		return view;
	}

}
