package com.wulee.seldistictlibrary.db;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.wulee.seldistictlibrary.entity.SearchComPlace;
import com.wulee.seldistictlibrary.entity.SearchComPlaceResult;
import com.wulee.seldistictlibrary.utils.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 将assets中数据库导入data/data/packagename/databases目录下 并提供接口
 * @author Administrator
 *
 *	note：该数据用完就需要关闭
 */
public class PlaceDao {
	private static final String PLACE_DB_NAME = "place.db";
	private static final String PLACE_DB_NAME_V2 = "place_v6.db";
	private static final int PLACE_DB_VERSION = 2;
	private static String APP_DATA_DIR ;	// "/data/data/packagename"
	
	private Context mContext;
	private PlaceHelper mHelper;
	private SQLiteDatabase mdb;


	public PlaceDao(Context context) {
		// TODO Auto-generated constructor stub
		if(TextUtils.isEmpty(APP_DATA_DIR)) {
			ApplicationInfo ai = context.getApplicationInfo();
			APP_DATA_DIR = ai.dataDir;
		}

		mContext = context;

		String dbDirPath = APP_DATA_DIR + "/databases/";

		Util.delFile(dbDirPath + PLACE_DB_NAME);

		isFolderExists(dbDirPath);

		File file2 = new File(dbDirPath + PLACE_DB_NAME_V2);
		if (file2.exists()) {
			mHelper = new PlaceHelper(mContext);
			mdb = mHelper.getReadableDatabase();
		} else {
			try {
				InputStream is = context.getAssets().open(PLACE_DB_NAME_V2);
				FileOutputStream fos = new FileOutputStream(file2);
				CopyDB(is, fos);
				mdb = SQLiteDatabase.openOrCreateDatabase(file2, null);
				mdb.setVersion(1);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				Log.i("error", "FileNotFoundException");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch blocks
				e.printStackTrace();
				Log.i("error", "IOException");
			}
		}
	}

	boolean isFolderExists(String strFolder) {
		File file = new File(strFolder);
		if (!file.exists()){
			if (file.mkdir()) {
				return true;
			} else
				return false;
		}
		return true;
	}

	private void CopyDB(InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		inputStream.close();
		outputStream.close();
	}

	///////////////////////////////////////operations/////////////////////////////////////////
	/**
	 * 关闭数据库
	 */
	public void close() {
		try {
			if (mdb != null)
				mdb.close();
			if (mHelper != null)
				mHelper.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取全部省份
	 */
	public List<SearchComPlace> findAllProvince() {
		List<SearchComPlace> ls = new ArrayList<SearchComPlace>();
		Cursor cursor = mdb.rawQuery("select * from province;", null);
		while (cursor.moveToNext()) {
			SearchComPlace place = new SearchComPlace();
			place.setType(0);
			place.setId(cursor.getString(cursor
					.getColumnIndexOrThrow("provinceID")));
			place.setName(cursor.getString(cursor
					.getColumnIndexOrThrow("province")));
			ls.add(place);
		}
		return ls;
	}

	/**
	 * 按照id获取市
	 * 
	 * @param provinceID
	 *            省份的id
	 * @return 返回若为null 此省为直辖市
	 */
	public List<SearchComPlace> findAllCityByProvinceID(String provinceID) {
		List<SearchComPlace> ls = new ArrayList<SearchComPlace>();
		Cursor cursor = mdb.rawQuery("select * from city where father=\""
				+ provinceID + "\";", null);
		while (cursor.moveToNext()) {
			SearchComPlace place = new SearchComPlace();
			place.setType(1);
			place.setId(cursor.getString(cursor.getColumnIndexOrThrow("cityID")));
			place.setName(cursor.getString(cursor.getColumnIndexOrThrow("city")));
			ls.add(place);
		} 

		if (ls.size()>0) {
			return ls;
		}

		return ls;
	}

	/**
	 * 按照id获取区县
	 * 
	 * @param cityID
	 *            城市的id
	 * @return 返回若为null 此市无分区
	 */
	public List<SearchComPlace> findAllAreaByCityID(String cityID) {
		List<SearchComPlace> ls = new ArrayList<SearchComPlace>();
		Cursor cursor = mdb.rawQuery("select * from area where father=\""
				+ cityID + "\";", null);
		while (cursor.moveToNext()) {
			SearchComPlace place = new SearchComPlace();
			place.setName(cursor.getString(cursor.getColumnIndexOrThrow("AREA")));
		    if (place.getName().equalsIgnoreCase("市辖区")
					|| place.getName().equalsIgnoreCase("县"))
				continue;
			place.setType(2);
			place.setId(cursor.getString(cursor.getColumnIndexOrThrow("areaID")));
			ls.add(place);
		}

		if (ls.size() == 0) {
			return null;
		}
		return ls;
	}

	
	/**
	 * 按照id获取省名称
	 * @param provinceID  省份的id
	 */
	public String findProvinceByID(String provinceID) {
		String provinceName = "";
		Cursor cursor = null;
		try {
			cursor = mdb.rawQuery("select * from province where provinceID = "+ provinceID , null);
			if(null!= cursor && cursor.getCount()>0){
			  while (cursor.moveToNext()) {
				provinceName = cursor.getString(cursor.getColumnIndexOrThrow("province")); 
			  }
		  }
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (null != cursor){
			    cursor.close();
			    cursor = null;
			}
		}
		return provinceName;
	}
	
	/**
	 * 按照id获取市名称
	 * @param cityID  市的id
	 */
	public String findCityByID(String cityID) {
		String cityName = "";
		Cursor cursor = null;
		try {
			cursor = mdb.rawQuery("select * from city where cityID ="+ cityID , null);
			if(null!= cursor && cursor.getCount()>0){
			 while (cursor.moveToNext()) {
				cityName = cursor.getString(cursor.getColumnIndexOrThrow("city")); 
			 }
		   }	
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (null != cursor){
			    cursor.close();
			    cursor = null;
			}
		}
		return cityName;
	}
	
	/**
	 * 按照id获取区(县)名称
	 * @param areaID  区(县)的id
	 */
	public String findAreaByID(String areaID) {
		String areaName = "";
		Cursor cursor = null;
		try {
			cursor = mdb.rawQuery("select * from area where areaID = "+ areaID , null);
			if(null!= cursor && cursor.getCount()>0){
			  while (cursor.moveToNext()) {
				areaName = cursor.getString(cursor.getColumnIndexOrThrow("AREA")); 
			 }
			}
			if (null != cursor){
			    cursor.close();
			    cursor = null;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}finally{
			if (null != cursor){
			    cursor.close();
			    cursor = null;
			}
		}
		return areaName;
	}
	
	/**
	 * 按照区(县)id获取区(县)名称
	 */
	public String findAreaIdByArea(String areaName, String father) {
		String areaId = "";
		Cursor cursor = null;
		try {
			cursor = mdb.rawQuery("select * from area where AREA = '"+ areaName+"' and father="+father , null);
			if(null!= cursor && cursor.getCount()>0){
			  while (cursor.moveToNext()) {
				  areaId = cursor.getString(cursor.getColumnIndexOrThrow("areaID")); 
			 }
			}
			if (null != cursor){
			    cursor.close();
			    cursor = null;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}finally{
			if (null != cursor){
			    cursor.close();
			    cursor = null;
			}
		}
		return areaId;
	}
	
	
	/**
	 * 根据区(县)Id获取市Id
	 * @param areaID  市id
	 */
	public String findCityIdByAreaId(String areaID) {
		if(null == areaID || "".equals(areaID)){
			return "";
		}
		String cityId = "";
		Cursor cursor = null;
		try {
			cursor = mdb.rawQuery("select * from area where areaID ="+ areaID , null);
			if(null!= cursor && cursor.getCount()>0){
			  while (cursor.moveToNext()) {
				  try {
					  cityId =  cursor.getString(cursor.getColumnIndexOrThrow("father")); 
				} catch (Exception e) {
					e.printStackTrace();
				}
			 }
			}
			if (null != cursor){
			    cursor.close();
			    cursor = null;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}finally{
			if (null != cursor){
			    cursor.close();
			    cursor = null;
			}
		}
		return cityId;
	}
	
	/**
	 * 根据市Id获取省Id
	 * @param cityID  市id
	 */
	public String findProvinceIdByCityId(String cityID) {
		if(null == cityID || "".equals(cityID)){
			return "";
		}
		String provinceId = "";
		Cursor cursor = null;
		try {
			cursor = mdb.rawQuery("select * from city where cityID ="+ cityID , null);
			if(null!= cursor && cursor.getCount()>0){
			  while (cursor.moveToNext()) {
				  try {
					provinceId =  cursor.getString(cursor.getColumnIndexOrThrow("father")); 
				} catch (Exception e) {
					e.printStackTrace();
				}
			 }
			}
			if (null != cursor){
			    cursor.close();
			    cursor = null;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}finally{
			if (null != cursor){
			    cursor.close();
			    cursor = null;
			}
		}
		return provinceId;
	}
	
	/**
	 * 根据区Id获取省市区详细信息
	 * @param areaId  区id
	 */
	public SearchComPlaceResult getDistrictInfoByAreaId(String areaId) {
		areaId =  areaId.trim();
		String cityId = "";
		String provinceId = "";
		try {
			cityId = findCityIdByAreaId(areaId);
			provinceId = findProvinceIdByCityId(cityId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SearchComPlaceResult retObj = new SearchComPlaceResult();
		int proId = 0;
		try {
			proId = Integer.parseInt(provinceId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if(proId > 0){
			if(Util.isSpeRegion(proId)){
				retObj.setProvinceID(proId);
				retObj.setProvinceName(findProvinceByID(provinceId));
				
				retObj.setCityID(Integer.parseInt(cityId));
				retObj.setCityName("");
				
				retObj.setAreaID(Integer.parseInt(areaId));
				retObj.setAreaName("");
			}else if(Util.isDireGovernment(proId)){
				retObj.setProvinceID(proId);
				retObj.setProvinceName(findProvinceByID(provinceId));
				
				retObj.setCityID(Integer.parseInt(cityId));
				retObj.setCityName("");
				
				retObj.setAreaID(Integer.parseInt(areaId));
				retObj.setAreaName(findAreaByID(areaId));
			}else{
				retObj.setAreaID(Integer.parseInt(areaId));
				retObj.setAreaName(findAreaByID(areaId));
				
				retObj.setCityID(Integer.parseInt(cityId));
				retObj.setCityName(findCityByID(cityId));
				
				retObj.setProvinceID(Integer.parseInt(provinceId));
				retObj.setProvinceName(findProvinceByID(provinceId));
			}
		}
		return retObj;
	}

	public String findProNameByAreaId(String areaId) {
		String cityId = findCityIdByAreaId(areaId);
		String provinceId = findProvinceIdByCityId(cityId);
		return findProvinceByID(provinceId);
	}

	/**
	 * @param id 有可能是省ID,也可能是市ID，也可能是区ID
	 * @return 相应的名称
	 */
	public String findNiceNameById(String id) {
		if ("820000".equals(id) || "810000".equals(id) || "710000".equals(id)) {
			return findProvinceByID(id);
		}
		String name;
		String areaName = findAreaByID(id);
		if (TextUtils.isEmpty(areaName)) {
			//不是区ID，假设是市ID
			String cityName = findCityByID(id);
			if (TextUtils.isEmpty(cityName)) {
				//不是市ID，是省ID
				String proviceName = findProvinceByID(id);
				name = proviceName;
			} else {
				String proviceName = findProvinceByID(findProvinceIdByCityId(id));
				name = proviceName + "-" + cityName ;
			}
		} else {
			String cityName = findCityByID(findCityIdByAreaId(id));
			String proviceName = findProNameByAreaId(id);
			name = proviceName + "-" + cityName + "-" + areaName;
		}
		return name;
	}

	/**
	 * @param id 有可能是省ID,也可能是市ID，也可能是区ID
	 * @return 省的ID
	 */
	public String getProviceId(String id){
		if(TextUtils.isEmpty(id)){
			return "";
		}

		String proviceName = findProvinceByID(id);
		if(!TextUtils.isEmpty(proviceName)){
			//是省ID
			return id;
		}
		//假设是市ID
		String proviceId = findProvinceIdByCityId(id);
		if(!TextUtils.isEmpty(proviceId)){
			return  proviceId;
		}

		//假设是区ID
		proviceId = findProvinceIdByCityId(findCityIdByAreaId(id));
		return proviceId;
	}

	/**
	 *
	 * @param id    有可能是省ID,也可能是市ID，也可能是区ID
	 * @return  市的ID,如果是省的ID,返回""
	 */
	public String getCityId(String id){
		if(TextUtils.isEmpty(id)){
			return "";
		}

		String proviceName = findProvinceByID(id);
		if(!TextUtils.isEmpty(proviceName)){
			//是省ID
			return "";
		}
		//假设是市ID
		String proviceId = findProvinceIdByCityId(id);
		if(!TextUtils.isEmpty(proviceId)){
			return  id;
		}
		//假设是区ID
		String cityId = findCityIdByAreaId(id);
		return cityId;
	}

	/**
	 *
	 * @param id    有可能是省ID,也可能是市ID，也可能是区ID
	 * @return  区的ID,如果是省的ID,返回""
	 *                 如果是市的ID,返回""
	 */
	public String getAreaId (String id){
		if(TextUtils.isEmpty(id)){
			return "";
		}

		String proviceName = findProvinceByID(id);
		if(!TextUtils.isEmpty(proviceName)){
			//是省ID
			return "";
		}
		//假设是市ID
		String proviceId = findProvinceIdByCityId(id);
		if(!TextUtils.isEmpty(proviceId)){
			return  "";
		}

		return id;
	}
	
	/**
	 * 创建内部类DatabaseHelper：创建数据库，穿件表，更新表功能
	 */
	private class PlaceHelper extends SQLiteOpenHelper {
		public PlaceHelper(Context context) {
			super(context, PLACE_DB_NAME_V2, null, PLACE_DB_VERSION);
			// TODO Auto-generated constructor stub
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
