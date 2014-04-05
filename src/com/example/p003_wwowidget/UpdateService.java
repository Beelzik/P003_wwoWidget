package com.example.p003_wwowidget;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.p003_wwowidget.storage.OnWeatherDataStore;
import com.example.p003_wwowidget.storage.WeatherDataCaller;
import com.example.p003_wwowidget.storage.WeatherDataStorage;
import com.example.p003_wwowidget.ui.ConfigActivity;
import com.example.p003_wwowidget.utils.BindHelper;
import com.example.wwolibrary.OnLocaleWWOListener;
import com.example.wwolibrary.LocaleWwoData.Data;
import com.example.wwolibrary.LocaleWwoData.Data.CurrentCondition;
import com.example.wwolibrary.LocaleWwoData.Data.NearestArea;
import com.example.wwolibrary.LocaleWwoData.Data.Request;
import com.example.wwolibrary.LocaleWwoData.Data.Weather;

public class UpdateService extends Service implements OnWeatherDataStore{

	private static WeatherDataStorage storage;
	private WeatherDataCaller caller;
	int[] ids;
	
	RemoteViews rViews;
	
	boolean gotLastUpdate=false;

	
	
HashMap<Integer, Bitmap> bitMap = new HashMap<Integer, Bitmap>();
	
	
	public static final int HANDLE_BITMAP=2;
	
	Handler handler= new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case HANDLE_BITMAP:
				int id=msg.arg1;
					updateWidget(UpdateService.this, id);
					stopSelf();
				break;
			}
			
		}
	};
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		storage =(WeatherDataStorage) this.getApplicationContext();
		if(intent.getIntArrayExtra("appWidgetIds")!=null){
		ids=intent.getIntArrayExtra("appWidgetIds");
		}else{
			stopSelf();
		}
			for (int id :ids) {
				Log.d("WWO3"," service id="+id);
				buildUpdate(this.getBaseContext(),id);
			}	
		return START_STICKY;
	}
	
	public void buildUpdate(Context context,int id){
		
		rViews= new RemoteViews(context.getPackageName(), R.layout.widget);
		
		prepareActionItem(context, rViews, id);
		
		
		storage= (WeatherDataStorage) context.getApplicationContext();
		caller= new WeatherDataCaller(storage);
		SharedPreferences sp=context.getSharedPreferences(ConfigActivity.CONFIG_SP_NAME, MODE_PRIVATE);
		if(sp.getBoolean(ConfigActivity.CONFIG_SP_SEARCH_TYPE+id, false)){
		caller.callAutoLocateWthData(context, id, 5);

		}else{
			float lat, lon;
			lat=sp.getFloat(ConfigActivity.CONFIG_SP_CITY_LAT+id, 0);
			lon=sp.getFloat(ConfigActivity.CONFIG_SP_CITY_LON+id,0);
			caller.callCityWthData(context, id, lat,lon, 5);
		}
		storage.setOnWeatherDataStoreListener(this);	
	}
	
	public void prepareActionItem(Context context, RemoteViews rViews, int id){
		Intent updateIntent=new Intent(context, WidgetProvider.class);
		updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {id});
		PendingIntent pIntent= PendingIntent.getBroadcast(context, id, updateIntent, 0);
		rViews.setOnClickPendingIntent(R.id.imBtnUpdateWeather, pIntent);
		
		
		Intent configIntent= new Intent(context, ConfigActivity.class);
		configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
		configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
		pIntent=PendingIntent.getActivity(context, id, configIntent, 0);
		rViews.setOnClickPendingIntent(R.id.laWdgMain, pIntent);
		
		AppWidgetManager manager= AppWidgetManager.getInstance(context);	
		manager.updateAppWidget(id, rViews);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public void updateWidget(Context context,final int id){
		
		Data data=storage.getWeatherDataById(id);
		BindHelper bindHelper=new BindHelper(context);
		CurrentCondition cc=data.getCurrent_condition();
		NearestArea nearestArea=data.getNearest_area();
		
		
		String  format="EEE dd.mm.yyyy";
		SimpleDateFormat dateFormat= new SimpleDateFormat(format);
		dateFormat.format(new Date(System.currentTimeMillis()));
		
		
		
		
		rViews.setTextViewText(R.id.tvWidDate, dateFormat.
				format(new Date(System.currentTimeMillis())));
		
		if (gotLastUpdate) {
			rViews.setTextViewText(R.id.tvCurTemp, 
					bindHelper.makeFormmtedWthText(data,BindHelper.GET_CUR_TEMP));
			rViews.setTextViewText(R.id.tvWidToday, cc.getWeatherDesc());
			rViews.setTextViewText(R.id.tvMaxTemp,
					bindHelper.makeFormmtedWthText(data,BindHelper.GET_MAX_TEMP));
			rViews.setTextViewText(R.id.tvMinTemp, 
					bindHelper.makeFormmtedWthText(data,BindHelper.GET_MIN_TEMP));
			if(nearestArea!=null){
			rViews.setTextViewText(R.id.tvWidCountry, nearestArea.getCountry());
			rViews.setTextViewText(R.id.tvWidCity, nearestArea.getRegion());}
			Bitmap btm=bitMap.get(id);
			if (btm!=null) {
				rViews.setImageViewBitmap(R.id.imBtnUpdateWeather, btm);
			}	
		}
		
		
		
		
		
		AppWidgetManager manager= AppWidgetManager.getInstance(context);
		
		
		
		
		manager.updateAppWidget(id, rViews);
	}

	@Override
	public void onStoredWthData(final int widgetId) {
		Data data=storage.getWeatherDataById(widgetId);
		
		if(data!=null){
		final String weatherIconURL=data.getCurrent_condition().getWeatherIconUrl();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					
					Bitmap bitmap=BitmapFactory.decodeStream(new URL(weatherIconURL).openStream());
					bitMap.put(widgetId, bitmap);
					Message message=handler.obtainMessage(HANDLE_BITMAP, widgetId, 0, bitmap);
					handler.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				} 		
			}
		}).start();
		
		gotLastUpdate=true;
		}else{
			gotLastUpdate=false;
		}
	}
	
	@Override
	public void onDestroy() {
		Log.d("WWO3","Service onDestroy()");
		super.onDestroy();
	}
}