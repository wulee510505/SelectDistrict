package com.wulee.seldistictlibrary.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wulee.seldistictlibrary.R;
import com.wulee.seldistictlibrary.db.PlaceDao;
import com.wulee.seldistictlibrary.entity.SearchComPlace;
import com.wulee.seldistictlibrary.entity.SearchComPlaceResult;
import com.wulee.seldistictlibrary.ui.adapter.PlaceGrideAdapter;
import com.wulee.seldistictlibrary.utils.Util;
import com.wulee.seldistictlibrary.widget.NoScroViewPager;

import java.util.ArrayList;
import java.util.List;


public class FindPlaceFragment extends Fragment implements OnClickListener, OnPageChangeListener,RadioGroup.OnCheckedChangeListener {
	public final static String INTENT_REQ_LEVEL = "get_pid_cityid_areaid";
	public final static int REQ_LEVEL_1 = 1; // 只请求省id
	public final static int REQ_LEVEL_2 = 2; // 只请求省/市id
	public final static int REQ_LEVEL_3 = 3; // 默认请求省市区id

	public final static String INTENT_PROVINCE_ID = "province_id"; //省id
	public final static String SELECTED_DISTRICT_ID = "selected_district_id"; //选择的地区id

	private TextView mTextView;
	private RadioButton tag1, tag2, tag3;
	private NoScroViewPager mComViewPager;

	private List<View> pagerViews;
	private MyPageAdapter mPageAdapter;

	private int req_Level = REQ_LEVEL_3;
	private String provinceId;
	private String districtId;

	private PlaceDao mPlaceDao = null;;
	private String selProvinceId,selCityId,selAreaId;
	private GridView gridView1,gridView2,gridView3;
	private PlaceGrideAdapter placeGrideAdapter1,placeGrideAdapter2,placeGrideAdapter3;
	private List<SearchComPlace> cityList,areaList;

	private String currSelPId,currSelCId,currSelAId;
	private Context mContext;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		View view  = inflater.inflate(R.layout.search_place_main,container,false);

		Bundle bundle = getArguments();
		req_Level = bundle.getInt(INTENT_REQ_LEVEL, REQ_LEVEL_3);
		provinceId = bundle.getString(INTENT_PROVINCE_ID);
		districtId = bundle.getString(SELECTED_DISTRICT_ID);

		mPlaceDao = new PlaceDao(mContext);
		initView(view);
		initData();
		return view;
	}


	private void initData() {
		if(null != provinceId && !provinceId.equals("")){
			selProvinceId = provinceId;
			String provinceName = mPlaceDao.findProvinceByID(selProvinceId);

			mTextView.setText(provinceName);

			mComViewPager.setCurrentItem(1);
			cityList.clear();
			cityList = mPlaceDao.findAllCityByProvinceID(provinceId);

			placeGrideAdapter2 = new PlaceGrideAdapter(mContext,cityList);
			gridView2.setAdapter(placeGrideAdapter2);
			placeGrideAdapter2.notifyDataSetChanged();
			mPageAdapter.notifyDataSetChanged();
		}

		if(!TextUtils.isEmpty(districtId)){
			SearchComPlaceResult scp = mPlaceDao.getDistrictInfoByAreaId(districtId);
			if(scp != null){
				try {
					currSelPId = String.valueOf(scp.getProvinceID());
					currSelCId  = String.valueOf(scp.getCityID());
					currSelAId = String.valueOf(scp.getAreaID());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void initView(View view) {
		mTextView = (TextView)view.findViewById(R.id.sg_tv_findplace);
		mComViewPager = (NoScroViewPager)view. findViewById(R.id.place_viewpager);
		RadioGroup rg = (RadioGroup)view. findViewById(R.id.radioGroup);
		rg.setOnCheckedChangeListener(this);
		tag1 = (RadioButton)view. findViewById(R.id.tag1);
		tag2 = (RadioButton)view. findViewById(R.id.tag2);
		tag3 = (RadioButton)view. findViewById(R.id.tag3);

		initTagButton();

		tag1.setOnClickListener(this);
		tag2.setOnClickListener(this);
		tag3.setOnClickListener(this);

		pagerViews = new ArrayList<View>();
		View view1 = LayoutInflater.from(mContext).inflate(R.layout.search_place_gridview_main,null);
		View view2 = LayoutInflater.from(mContext).inflate(R.layout.search_place_gridview_main,null);
		View view3 = LayoutInflater.from(mContext).inflate(R.layout.search_place_gridview_main,null);
		gridView1 = (GridView) view1.findViewById(R.id.gridview_place);
		gridView2 = (GridView) view2.findViewById(R.id.gridview_place);
		gridView3 = (GridView) view3.findViewById(R.id.gridview_place);
		final List<SearchComPlace> provinceList  = mPlaceDao.findAllProvince();
		cityList = new ArrayList<SearchComPlace>();
		areaList = new ArrayList<SearchComPlace>();

		final String[] provinceName = new String[1];
		final String[] cityName = new String[1];

		placeGrideAdapter1 = new PlaceGrideAdapter(mContext,provinceList);
		gridView1.setAdapter(placeGrideAdapter1);


		gridView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
				SearchComPlace place = provinceList.get(position);
				if(null == place)
					return;
				selProvinceId = place.getId();
				selCityId = "";
				selAreaId = "";
				cityList.clear();
				areaList.clear();

				provinceName[0] = place.getName();
				mTextView.setText(provinceName[0]);

				placeGrideAdapter1.setSelectPosition(position);

				if(req_Level == REQ_LEVEL_2){
					try {
						if(Util.isSpeRegion(Integer.valueOf(selProvinceId)) || Util.isDireGovernment(Integer.valueOf(selProvinceId))){
							return;
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					mComViewPager.setCurrentItem(1);
					List<SearchComPlace> dataList2 = mPlaceDao.findAllCityByProvinceID(selProvinceId);
					if(null != dataList2 && dataList2.size() >0){
						cityList.addAll(dataList2);
						placeGrideAdapter2 = new PlaceGrideAdapter(mContext,cityList);
						gridView2.setAdapter(placeGrideAdapter2);
						placeGrideAdapter2.notifyDataSetChanged();
						mPageAdapter.notifyDataSetChanged();
					}
				}else if(req_Level == REQ_LEVEL_3){
					try {
						if(Util.isSpeRegion(Integer.valueOf(selProvinceId))){
							return;
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					mComViewPager.setCurrentItem(1);
					List<SearchComPlace> dataList2 = mPlaceDao.findAllCityByProvinceID(selProvinceId);
					if(null != dataList2 && dataList2.size() >0){
						cityList.addAll(dataList2);
						placeGrideAdapter2 = new PlaceGrideAdapter(mContext,cityList);
						gridView2.setAdapter(placeGrideAdapter2);
						placeGrideAdapter2.notifyDataSetChanged();
						mPageAdapter.notifyDataSetChanged();
					}
				}
			}
		});

		gridView2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
				SearchComPlace place = cityList.get(position);
				if(null == place)
					return;
				selCityId = place.getId();
				selAreaId = "";
				areaList.clear();


				String proId = mPlaceDao.findProvinceIdByCityId(selCityId);
				String provinceName = mPlaceDao.findProvinceByID(proId);
				if(Util.isDireGovernment(Integer.parseInt(selProvinceId))){
					cityName[0] = "";
					mTextView.setText(provinceName);
				}else{
					cityName[0] = place.getName();
					mTextView.setText(provinceName + "-" + cityName[0]);
				}

				placeGrideAdapter2.setSelectPosition(position);

				if(req_Level == REQ_LEVEL_3){
					mComViewPager.setCurrentItem(2);
					List<SearchComPlace> dataList3 = mPlaceDao.findAllAreaByCityID(selCityId);
					if(null != dataList3 && dataList3.size() >0){
						areaList.addAll(dataList3);
						placeGrideAdapter3 = new PlaceGrideAdapter(mContext,areaList);
						gridView3.setAdapter(placeGrideAdapter3);
						placeGrideAdapter3.notifyDataSetChanged();
						mPageAdapter.notifyDataSetChanged();
					}
				}
			}
		});
		gridView3.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
				SearchComPlace place = areaList.get(position);
				if(null == place)
					return;
				selAreaId = place.getId();
				placeGrideAdapter3.setSelectPosition(position);

				SearchComPlaceResult placeResult = mPlaceDao.getDistrictInfoByAreaId(selAreaId);
				if(Util.isDireGovernment(Integer.parseInt(selProvinceId))){
					mTextView.setText(placeResult.getProvinceName()+"-"+placeResult.getAreaName());
				}else{
					mTextView.setText(placeResult.getProvinceName()+"-"+placeResult.getCityName()+"-"+placeResult.getAreaName());
				}
			}
		});

		pagerViews.add(view1);
		pagerViews.add(view2);
		pagerViews.add(view3);

		mComViewPager.setOnPageChangeListener(this);
		mPageAdapter = new MyPageAdapter(mContext, pagerViews);
		mComViewPager.setAdapter(mPageAdapter);
	}


	public SearchComPlaceResult getSelectDistrictInfo(){

		StringBuilder IdSB = new StringBuilder();
		StringBuilder NameSB = new StringBuilder();

		SearchComPlaceResult result = new SearchComPlaceResult();
		String provinceName = "";
		String cityName = "";

		switch (req_Level) {
			case REQ_LEVEL_1:
				if(TextUtils.isEmpty(selProvinceId)){
					Toast.makeText(mContext, "请选择省份", Toast.LENGTH_SHORT).show();
					return result;
				}
				IdSB.append(selProvinceId);
				provinceName = mPlaceDao.findProvinceByID(selProvinceId);
				NameSB.append(provinceName);

				result.setProvinceID(Integer.parseInt(selProvinceId));
				result.setProvinceName(provinceName);
				mTextView.setText(NameSB.toString());
				break;
			case REQ_LEVEL_2:
				if(TextUtils.isEmpty(selProvinceId)){
					Toast.makeText(mContext, "请选择省份", Toast.LENGTH_SHORT).show();
					return result;
				}else{
					if(Util.isSpeRegion(Integer.parseInt(selProvinceId))){
						IdSB.append(selProvinceId).append("-").append(selProvinceId).append("-").append(selProvinceId);
						provinceName = mPlaceDao.findProvinceByID(selProvinceId);

						NameSB.append(provinceName);

						result.setProvinceID(Integer.parseInt(selProvinceId));
						result.setCityID(Integer.parseInt(selProvinceId));
						result.setAreaID(Integer.parseInt(selProvinceId));
						result.setProvinceName(provinceName);
						result.setCityName("");
						result.setAreaName("");
					}else if(Util.isDireGovernment(Integer.parseInt(selProvinceId))){
						selCityId = selProvinceId;
						IdSB.append(selProvinceId).append("-").append(selCityId);

						provinceName = mPlaceDao.findProvinceByID(selProvinceId);
						cityName = "";
						NameSB.append(provinceName);

						result.setProvinceID(Integer.parseInt(selProvinceId));
						result.setProvinceName(provinceName);
						result.setCityID(Integer.parseInt(selProvinceId));
						result.setCityName(cityName);
					}else{
						if(TextUtils.isEmpty(selCityId)){
							Toast.makeText(mContext, "请选择市", Toast.LENGTH_SHORT).show();
							return result;
						}else{
							IdSB.append(selProvinceId).append("-").append(selCityId);

							provinceName = mPlaceDao.findProvinceByID(selProvinceId);
							cityName = mPlaceDao.findCityByID(selCityId);
							NameSB.append(provinceName).append("-").append(cityName);

							result.setProvinceID(Integer.parseInt(selProvinceId));
							result.setProvinceName(provinceName);
							result.setCityID(Integer.parseInt(selCityId));
							result.setCityName(cityName);
						}
					}
				}
				mTextView.setText(NameSB.toString());
				break;
			case REQ_LEVEL_3:
				if(TextUtils.isEmpty(selProvinceId)){
					Toast.makeText(mContext, "请选择省份", Toast.LENGTH_SHORT).show();
					return result;
				}else{
					if(Util.isSpeRegion(Integer.parseInt(selProvinceId))){
						IdSB.append(selProvinceId).append("-").append(selProvinceId).append("-").append(selProvinceId);
						provinceName = mPlaceDao.findProvinceByID(selProvinceId);

						NameSB.append(provinceName);

						result.setProvinceID(Integer.parseInt(selProvinceId));
						result.setCityID(Integer.parseInt(selProvinceId));
						result.setAreaID(Integer.parseInt(selProvinceId));
						result.setProvinceName(provinceName);
						result.setCityName("");
						result.setAreaName("");
					}else{
						if(TextUtils.isEmpty(selCityId)){
							Toast.makeText(mContext, "请选择市", Toast.LENGTH_SHORT).show();
							return result;
						}else{
							if(TextUtils.isEmpty(selAreaId)){
								Toast.makeText(mContext, "请选择区/县", Toast.LENGTH_SHORT).show();
								return result;
							}else{
								IdSB.append(selProvinceId).append("-").append(selCityId).append("-").append(selAreaId);

								SearchComPlaceResult placeResult = mPlaceDao.getDistrictInfoByAreaId(selAreaId);
								if(Util.isDireGovernment(Integer.parseInt(selProvinceId))){
									NameSB.append(placeResult.getProvinceName()).append("-").append(placeResult.getAreaName());
								}else{
									NameSB.append(placeResult.getProvinceName()).append("-").append(placeResult.getCityName()).append("-").append(placeResult.getAreaName());
								}
								result.setProvinceID(Integer.parseInt(selProvinceId));
								result.setProvinceName(placeResult.getProvinceName());
								result.setCityID(Integer.parseInt(selCityId));
								result.setCityName(placeResult.getCityName());
								result.setAreaID(Integer.parseInt(selAreaId));
								result.setAreaName(placeResult.getAreaName());
							}
						}
					}
				}
				mTextView.setText(NameSB.toString());
				break;
			default:
				break;
		}
		return result;
	}



	private void initTagButton() {
		switch (req_Level) {
		case REQ_LEVEL_1:
		  	tag1.setTextColor(getResources().getColor(R.color.ctv_white));
    		tag1.setBackgroundResource(R.drawable.com_btn_bg_only_one);
			tag1.setChecked(true);
    		tag2.setVisibility(View.GONE);
    		tag3.setVisibility(View.GONE);
			break;
        case REQ_LEVEL_2:
          	tag1.setTextColor(getResources().getColor(R.color.ctv_white));
    		tag2.setTextColor(getResources().getColor(R.color.colorPrimary));
    		tag1.setChecked(true);
			tag2.setBackgroundResource(R.drawable.com_btn_bg_right);
			tag2.setChecked(false);
			tag3.setVisibility(View.GONE);
			break;
        case REQ_LEVEL_3:
        	tag1.setTextColor(getResources().getColor(R.color.ctv_white));
    		tag2.setTextColor(getResources().getColor(R.color.colorPrimary));
    		tag3.setTextColor(getResources().getColor(R.color.colorPrimary));
    		tag1.setChecked(true);
    		tag2.setChecked(false);
			tag3.setChecked(false);
			break;
			default:
				break;
		}

	}


	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.tag1){
			mComViewPager.setCurrentItem(0);
		}else if(v.getId() == R.id.tag2){
			if(TextUtils.isEmpty(selProvinceId)){
				Toast.makeText(mContext, "请选择省份", Toast.LENGTH_SHORT).show();
				return;
			}
			mComViewPager.setCurrentItem(1);
		}else if(v.getId() == R.id.tag3){
			if(TextUtils.isEmpty(selCityId)){
				Toast.makeText(mContext, "请选择市", Toast.LENGTH_SHORT).show();
				return;
			}
			mComViewPager.setCurrentItem(2);
		}
	}


	class MyPageAdapter extends PagerAdapter {
		private Context mContext;
		private List<View> mPageViews;;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mPageViews.size();
		}

		public MyPageAdapter(Context context, List<View> pageViews) {
			this.mContext = context;
			this.mPageViews = pageViews;
		}

		@Override
		public Object instantiateItem(ViewGroup arg0, int position) {
			arg0.addView(mPageViews.get(position));
			return mPageViews.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// 销毁一个页面之后会调用这个方法
			((ViewPager) arg0).removeView((View) arg2);
		}
	}


	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (req_Level) {
			case REQ_LEVEL_1:
				if (checkedId == R.id.tag1){
					tag1.setTextColor(getResources().getColor(R.color.ctv_white));
					tag1.setBackgroundResource(R.drawable.com_btn_bg_only_one);
				}
				break;
			case REQ_LEVEL_2:
				if (checkedId == R.id.tag1){
					tag1.setChecked(true);
					tag2.setChecked(false);
					tag1.setTextColor(getResources().getColor(R.color.ctv_white));
					tag2.setTextColor(getResources().getColor(R.color.colorPrimary));
				}else if(checkedId == R.id.tag2){
					tag1.setChecked(false);
					tag2.setChecked(true);
					tag1.setTextColor(getResources().getColor(R.color.colorPrimary));
					tag2.setTextColor(getResources().getColor(R.color.ctv_white));
				}
				break;
			case REQ_LEVEL_3:
				if (checkedId == R.id.tag1){
					tag1.setTextColor(getResources().getColor( R.color.ctv_white));
					tag2.setTextColor(getResources().getColor( R.color.colorPrimary));
					tag3.setTextColor(getResources().getColor(R.color.colorPrimary));
					tag1.setChecked(true);
					tag2.setChecked(false);
					tag3.setChecked(false);
				}else if(checkedId == R.id.tag2){
					tag1.setTextColor(getResources().getColor( R.color.colorPrimary));
					tag2.setTextColor(getResources().getColor( R.color.ctv_white));
					tag3.setTextColor(getResources().getColor(R.color.colorPrimary));
					tag1.setChecked(false);
					tag2.setChecked(true);
					tag3.setChecked(false);
				}else if(checkedId == R.id.tag3){
					tag1.setTextColor(getResources().getColor( R.color.colorPrimary));
					tag2.setTextColor(getResources().getColor( R.color.colorPrimary));
					tag3.setTextColor(getResources().getColor(R.color.ctv_white));
					tag1.setChecked(false);
					tag2.setChecked(false);
					tag3.setChecked(true);
				}
				break;
			default:
				break;
		}
	}


	@Override
	public void onPageSelected(int arg0) {
		switch (req_Level) {
		case REQ_LEVEL_1:
			switch (arg0) {
			case 0:
				tag1.setTextColor(getResources().getColor(R.color.ctv_white));
				tag1.setBackgroundResource(R.drawable.com_btn_bg_only_one);
				break;
			}
			break;
		case REQ_LEVEL_2:
			switch (arg0) {
			case 0:
				tag1.setChecked(true);
				tag2.setChecked(false);
				tag1.setTextColor(getResources().getColor(R.color.ctv_white));
				tag2.setTextColor(getResources().getColor(R.color.colorPrimary));
				break;
			case 1:
				tag1.setChecked(false);
				tag2.setChecked(true);
				tag1.setTextColor(getResources().getColor(R.color.colorPrimary));
				tag2.setTextColor(getResources().getColor(R.color.ctv_white));
				break;
			default:
				break;
			}
			break;
		case REQ_LEVEL_3:
			switch (arg0) {
			case 0:
				tag1.setTextColor(getResources().getColor( R.color.ctv_white));
				tag2.setTextColor(getResources().getColor( R.color.colorPrimary));
				tag3.setTextColor(getResources().getColor(R.color.colorPrimary));
				tag1.setChecked(true);
				tag2.setChecked(false);
				tag3.setChecked(false);
				break;
				case 1:
					tag1.setTextColor(getResources().getColor( R.color.colorPrimary));
				tag2.setTextColor(getResources().getColor( R.color.ctv_white));
				tag3.setTextColor(getResources().getColor(R.color.colorPrimary));
				tag1.setChecked(false);
				tag2.setChecked(true);
				tag3.setChecked(false);
				break;
			case 2:
				tag1.setTextColor(getResources().getColor( R.color.colorPrimary));
				tag2.setTextColor(getResources().getColor( R.color.colorPrimary));
				tag3.setTextColor(getResources().getColor(R.color.ctv_white));
				tag1.setChecked(false);
				tag2.setChecked(false);
				tag3.setChecked(true);
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}
}
