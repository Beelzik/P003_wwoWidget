package com.example.p003_wwowidget.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DataBase {
	
	private SQLiteDatabase dbLite;
	private DBHelper dbH;
	private Context context;
	int id;
	public static final String DB_NAME="city";
	public static final int DB_VERSION=1;
	
	    private static final String CITY_DATA_CSV = "city_data.csv"; 
	 
	 public static final String CITY_ID = BaseColumns._ID;
     public static final String CITY_NAME = "name";
     public static final String CITY_ASCII_NAME = "asciiname";
     public static final String CITY_LATITUDE = "latitude";
     public static final String CITY_LONGITUDE = "longitude";

     public static final String CITY_COUNTRY = "country";
     public static final String CITY_TIMEZONE_ID = "timezone_id";
     
     
     public static final String TABLE_CITY="cities";
     
	 
	 private static final String DATABASE_CREATE =
	            "create table cities (_id integer primary key, "
	                    + "name text not null, "
	                    + "asciiname text, "
	                    + "latitude real not null, "
	                    + "longitude real not null, "
	                    + "country text not null, "
	                    + "timezone_id text);";
	
	public DataBase(Context ctx) {
		context=ctx;
		dbH=new DBHelper(context, DB_NAME, null, DB_VERSION);
	}
	
	
	public void open(){
		dbLite=dbH.getWritableDatabase();
	}
	
	public void close(){
		if(dbH!=null) dbH.close();
	}
	
	
	public Cursor getData(String selection,String[] selectionArgs){
		Cursor cursor= dbLite.query(TABLE_CITY, null, selection, selectionArgs, null, null, null);
	 return cursor;
	}
	
	public Cursor getCity(long id){
		String selection =CITY_ID+"="+id;
		Cursor cursor= dbLite.query(TABLE_CITY, null, 
				selection, null, null, null, null);
	 return cursor;
	}
	
	
		
	public void addCity(String city, String country, float lat, float lon){
		ContentValues cv= new ContentValues();
		cv.put(CITY_NAME, city);
		cv.put(CITY_COUNTRY, country);
		cv.put(CITY_LATITUDE, lat);
		cv.put(CITY_LONGITUDE, lon);
		dbLite.insert(TABLE_CITY,null, cv);
	}
	
	public long getIdAtPosition(int position){
		Cursor cursor=dbLite.query(TABLE_CITY, null, null,null, null, null, null);
		cursor.moveToPosition(position);
		
		return cursor.getLong(cursor.getColumnIndex(CITY_ID));
		
	}
	public void editCity(long id, String city, String country, float lat, float lon){
		ContentValues cv= new ContentValues();
		cv.put(CITY_NAME, city);
		cv.put(CITY_COUNTRY, country);
		cv.put(CITY_LATITUDE, lat);
		cv.put(CITY_LONGITUDE, lon);
		dbLite.update(TABLE_CITY, cv, CITY_ID+"="+id, null);
	}
	public void deleteCity(long ids[]){
	
		for (long id : ids) {
			dbLite.delete(TABLE_CITY, CITY_ID+"="+id, null);
		}
	}
	
	public Cursor getRowById(long id){
		return dbLite.query(TABLE_CITY, null, CITY_ID+"="+id,null,null,null,null);
	}
	class DBHelper extends SQLiteOpenHelper{

		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			  db.execSQL(DATABASE_CREATE);
			
				insertData(db);
			
		}
		
	
		  private void insertData(SQLiteDatabase db) {
		        Pattern p = Pattern.compile("\t");
		      
		        try {
		        	
		            AssetManager am = context.getAssets();
		            InputStream stream = am.open(CITY_DATA_CSV, AssetManager.ACCESS_STREAMING);
		            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		            String line = in.readLine();
		           
		            while (line != null) {
		            	
		            	ContentValues cv= new ContentValues();
		            	String[] rawValues = p.split(line, -1);
		            	cv.put(CITY_ID, id++);
		            	cv.put(CITY_NAME,  rawValues[0]);
		            	cv.put(CITY_ASCII_NAME,  rawValues[1]);
		            	cv.put(CITY_LATITUDE,  Double.parseDouble(rawValues[2]));
		            	cv.put(CITY_LONGITUDE,  Double.parseDouble(rawValues[3]));
		            	cv.put(CITY_COUNTRY,  rawValues[4]);
		            	cv.put(CITY_TIMEZONE_ID,  rawValues[5]);
		            	
		            	db.insert(TABLE_CITY, null, cv);
		                line = in.readLine();
		            }
		            in.close();
		        } catch (IOException e) {
		            throw new RuntimeException(e.getMessage(), e);
		        }
		   
		    }

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
		
	}
}
