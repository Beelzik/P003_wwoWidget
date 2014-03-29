package com.example.p003_wwowidget.storage;

import android.content.Context;
import android.location.Location;

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

public class WeatherDataCaller {
	
	WeatherDataStorage storage;
	
	public WeatherDataCaller(WeatherDataStorage storage) {
		this.storage=storage;
	}

	
	public void callAutoLocateWthData(Context ctx,final int widgetId, final int numOfDays){
		
		LocationResult locationResult= new LocationResult() {
			
			@Override
			public void gotLocation(Location location) {
				StringBuilder query= new StringBuilder();
				query.append(location.getLatitude()+","+location.getLongitude());
				
				LocaleWwoApi api= new LocaleWwoApi();
				
				LocaleWeatherParam param=new LocaleWeatherParam.Builder(query.toString(), 
						LocaleWwoApi.API_KEY).setIncludelocation("yes").setNum_of_days(numOfDays).build();
				api.setOnLocaleWWOListener(new MyLocaleWWOListener(widgetId));
				api.callApi(param);
			}
		};
		
		
		MyLocation location= new MyLocation();
		location.getLocation(ctx, locationResult);
	}
	
	
	public void callCityWthData(Context ctx,int widgetId,float lat, float lon,  int numOfDays){
		StringBuilder query= new StringBuilder();
		query.append(lat+","+lon);
	
		
		LocaleWwoApi api= new LocaleWwoApi();
		
		LocaleWeatherParam param=new LocaleWeatherParam.Builder(query.toString(), 
				LocaleWwoApi.API_KEY).setIncludelocation("yes").setNum_of_days(numOfDays).build();
		api.setOnLocaleWWOListener(new MyLocaleWWOListener(widgetId));
		api.callApi(param);
	}
	
	
	
	class MyLocaleWWOListener implements OnLocaleWWOListener{
		int id;
		
		public MyLocaleWWOListener(int id) {
			this.id=id;
		}
		@Override
		public void localeWWOListener(CurrentCondition cc,
				NearestArea nearestArea, Request request, Weather[] weather) {
			if(request.isConnect==true){
			Data data=new Data(cc, nearestArea, weather, request);
			storage.addDataInStorage(data, id);
			}
		}
		
	
		
	}
	
}
