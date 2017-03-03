package com.wulee.seldistictlibrary.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wulee.seldistictlibrary.R;
import com.wulee.seldistictlibrary.entity.SearchComPlace;

import java.util.ArrayList;
import java.util.List;


public class PlaceGrideAdapter extends BaseAdapter {
	private List<SearchComPlace> mList;
	private Context mContext;
	private int mPosition = -1 ;

	private int colorSelItemBg;
	private int colorDefItemBg;

	public void setPlaceData(List<SearchComPlace> list) {
		if (list != null) this.mList = list;
		else {
			this.mList = new ArrayList<SearchComPlace>();
		}
	}

	public void setSelectPosition(int position) {
		this.mPosition = position;
		notifyDataSetChanged();
	}
	
	
	public PlaceGrideAdapter(Context context, List<SearchComPlace> list,int bgDefColor,int bgSelColor) {
		this.setPlaceData(list);
		mContext = context;
		colorDefItemBg = bgDefColor;
		colorSelItemBg = bgSelColor;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}

	public List<SearchComPlace> getmList() {
		return mList;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemView itemView = null;
		if (null == convertView) {
			convertView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.search_place_gridview_item, null);
			itemView = new ItemView(convertView);
			convertView.setTag(itemView);
		}else{
			itemView = (ItemView) convertView.getTag();
		}
		
		if(mPosition == position){
			itemView.mNameTV.setBackgroundColor(colorSelItemBg);
			itemView.mNameTV.setTextColor(Color.WHITE);
		}else{
			itemView.mNameTV.setBackgroundColor(colorDefItemBg);
			itemView.mNameTV.setTextColor(ContextCompat.getColor(mContext,R.color.ctv_black2));
		}
		SearchComPlace place = (SearchComPlace) getItem(position);
	    itemView.mNameTV.setText(place.getName());
 		return convertView;
	}
	class ItemView {
		TextView mNameTV;
		public ItemView(View convertView){
			mNameTV = (TextView)convertView.findViewById(R.id.tv_place_item);
		}
	}
}
