package com.example.p003_wwowidget.storage;


import java.util.HashMap;
import java.util.Set;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.p003_wwowidget.locate.MyLocation;
import com.example.p003_wwowidget.locate.MyLocation.LocationResult;
import com.example.wwolibrary.LocaleWwoApi;
import com.example.wwolibrary.OnLocaleWWOListener;
import com.example.wwolibrary.LocaleWwoData.Data;
import com.example.wwolibrary.LocaleWwoData.Data.CurrentCondition;
import com.example.wwolibrary.LocaleWwoData.Data.NearestArea;
import com.example.wwolibrary.LocaleWwoData.Data.Request;
import com.example.wwolibrary.LocaleWwoData.Data.Weather;
import com.example.wwolibrary.params.LocaleWeatherParam;

public final class WeatherDataStorage extends Application{
	
private HashMap<Integer, Data> dataMap;
private Context context;

private static String WIDGET_IDS="widget_ids";
	
	OnWeatherDataStore onWeatherDataStore;
	
	@Override
	public void onCreate() {
		super.onCreate();
		dataMap=new HashMap<Integer, Data>();
		context=this;
	}

	

	public void setOnWeatherDataStoreListener(OnWeatherDataStore listener){
		onWeatherDataStore=listener;
	}
	
	public void addDataInStorage(Data data, int widgetId){
		dataMap.put(widgetId, data);
		onWeatherDataStore.onStoredWthData(widgetId);
	}
	
	public Data getWeatherDataById(int id){
		return dataMap.get(id);
	}

	
	
	
	public Set<Integer> getAllKey(){
		 Set<Integer> set= dataMap.keySet();
		 return set;
	}
	
	
	public void removeDataFromStorage(int key){
		dataMap.remove(key);
	}
		
	}
	
	


	
