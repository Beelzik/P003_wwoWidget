package com.example.p003_wwowidget.ui;

import java.security.Provider;
import java.util.concurrent.TimeUnit;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.p003_wwowidget.R;
import com.example.p003_wwowidget.WidgetProvider;
import com.example.p003_wwowidget.base.DataBase;
import com.example.p003_wwowidget.storage.OnWeatherDataStore;
import com.example.p003_wwowidget.storage.WeatherDataCaller;
import com.example.p003_wwowidget.storage.WeatherDataStorage;
import com.example.p003_wwowidget.utils.BindHelper;
import com.example.wwolibrary.LocaleWwoData.Data;
import com.example.wwolibrary.LocaleWwoData.Data.Weather;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;


public class ConfigActivity extends SherlockFragmentActivity 
	implements OnClickListener,OnCheckedChangeListener,OnWeatherDataStore{

	
	
	
	public final static String CONFIG_SP_NAME="con_sp";
	public final static String CONFIG_SP_SEARCH_TYPE="key_auto_search_";
	
	public final static String CONFIG_SP_CITY_LAT="city_lat_";
	public final static String CONFIG_SP_CITY_LON="city_lon_";
	public final static String CONFIG_SP_CITY_SELECT_="city_select_";
	public final static String CONFIG_SP_CITY_NAME_="city_name_";
	public final static String CONFIG_SP_CITY_COUNTRY_="city_country_";
	
	final static int REQUEST_CITY=1;
	WeatherGraphFragment wgFrag;
	LinearLayout layout;
	CheckBox chbMinTemp,chbMaxTemp,chbZeroTemp;
	
	private TextView tvStatus; 
	private CheckBox chbLocationSearch;
	private Button btnSelectCity,btnLocationSetting;
	private SharedPreferences  sp;
	boolean autoSearchOn=false;
	boolean citySelected=false;
	private Intent resultValue;
	int widgetID;
	
	private boolean minCurveOn=true;
	private boolean maxCurveOn=true;
	private boolean zeroTempCurve=true;
	
	private String cityName;
	private String countryName;
	private Float cityLat,cityLon;
	private WeatherDataStorage storage;
	private WeatherDataCaller caller;
	private Data data;
	private Weather[] weathers;
	private LineGraphView graphView;
	private GraphViewSeries seriesMax;
	private GraphViewSeries seriesMin;
	private GraphViewSeries seriesZero;
	private LocationManager locationManager;
	
	BindHelper bindHelper;
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			layout= (LinearLayout) wgFrag.getView().findViewById(R.id.grWth);
			graphView = new LineGraphView(
				     ConfigActivity.this// context
				      , "Weather Graph " // heading
				);
			
			layout.addView(graphView);
			chbMinTemp=(CheckBox) wgFrag.getView().findViewById(R.id.chbWthFrMinTemp);
			chbMaxTemp=(CheckBox) wgFrag.getView().findViewById(R.id.chbWthFrMaxTemp);
			chbZeroTemp=(CheckBox) wgFrag.getView().findViewById(R.id.chbWthFrZeroTemp);
			chbMaxTemp.setOnCheckedChangeListener(ConfigActivity.this);
			chbMinTemp.setOnCheckedChangeListener(ConfigActivity.this);
			chbZeroTemp.setOnCheckedChangeListener(ConfigActivity.this);
			
			
			chbMinTemp.setChecked(true);
			chbZeroTemp.setChecked(true);
			chbMaxTemp.setChecked(true);
			
			graphView.setViewPort(1,4);
			graphView.getGraphViewStyle().setNumHorizontalLabels(5);
			graphView.getGraphViewStyle().setVerticalLabelsAlign(Align.CENTER);
			graphView.setShowLegend(true);
			graphView.getGraphViewStyle().setLegendWidth(300);
			graphView.getGraphViewStyle().setLegendBorder(30);
			graphView.getGraphViewStyle().setLegendSpacing(20);
			graphView.setDrawDataPoints(true);
		
			
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		//Log.d("WWO3","onCreate conf");
		sp= getSharedPreferences(CONFIG_SP_NAME, MODE_PRIVATE);
		
		Intent intent=getIntent();
		Bundle extra=intent.getExtras();
		
		if(extra!=null){
			widgetID=extra.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		if(widgetID==AppWidgetManager.INVALID_APPWIDGET_ID){
			finish();
		}
		
		//данные кривой 0-ой температуры
		GraphViewSeriesStyle seriesStyle= new GraphViewSeriesStyle(Color.CYAN, 2);
		
		
		GraphViewData[] zeroData=new GraphViewData[]{
				 new GraphViewData(1d,0d),
				 new GraphViewData(2,0d),
				 new GraphViewData(3,0d),
				 new GraphViewData(4,0d),
				 new GraphViewData(5,0d),};
		seriesZero=new GraphViewSeries("zero",
				seriesStyle,zeroData		
		);
		
		
		wgFrag=new WeatherGraphFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.ltFrGraph, wgFrag).commit();
		
		storage=(WeatherDataStorage) this.getApplication();
		caller=new WeatherDataCaller(storage);
		storage.setOnWeatherDataStoreListener(this);
		
		
		
		
		// в layout= (LinearLayout) wgFrag.getView().findViewById(R.id.grWth) приходит ноль, поэтому вот
		
		new Thread(new Runnable() {	
			@Override
			public void run() {
				try {
					
						TimeUnit.MILLISECONDS.sleep(50);
					Message message=handler.obtainMessage();
					handler.sendMessage(message);
				} catch (InterruptedException e) {
				}
				
			}
		}).start();
		
		
		autoSearchOn=sp.getBoolean(CONFIG_SP_SEARCH_TYPE+widgetID,false);
		setContentView(R.layout.config);
		tvStatus=(TextView) findViewById(R.id.tvConStatus) ;
		chbLocationSearch= (CheckBox) findViewById(R.id.chbLocationSearch) ;
		chbLocationSearch.setChecked(autoSearchOn);
		chbLocationSearch.setOnCheckedChangeListener(this);
		chbLocationSearch.setEnabled(false);
		
		
		btnSelectCity= (Button) findViewById(R.id.btnSelectCity) ;
		btnLocationSetting= (Button) findViewById(R.id.btnConLocationSetting);
		
		btnLocationSetting.setOnClickListener(this);
		btnSelectCity.setOnClickListener(this);	
		locationManager=(LocationManager) getSystemService(Service.LOCATION_SERVICE);
		
		controlLocationProviderStatuc();
		
	if (autoSearchOn) {
		chbLocationSearch.setChecked(autoSearchOn);
		btnSelectCity.setEnabled(!autoSearchOn);
		tvStatus.setText(R.string.conActLocationAutosearch);
	}else{
		tvStatus.setText(R.string.conActLocationNoData);
	}
	
	if(!sp.getString(CONFIG_SP_CITY_NAME_+widgetID, "no").equalsIgnoreCase("no")){
		String status=this.getString(R.string.conStatusCitySelected);
		status+=" "+sp.getString(CONFIG_SP_CITY_NAME_+widgetID, "no")+", "
		+sp.getString(CONFIG_SP_CITY_COUNTRY_+widgetID, "no");
		tvStatus.setText(status);
	}
	
	ActionBar bar=getSupportActionBar();
	bar.setHomeButtonEnabled(true);
	bar.setDisplayHomeAsUpEnabled(true);
	
	bindHelper= new BindHelper(this);

	data=storage.getWeatherDataById(widgetID);
	if (data!=null) 
	weathers=data.getWeather();
	
	resultValue=new Intent();
	resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetID);
	
	setResult(RESULT_CANCELED, resultValue);
	}

	@Override
	protected void onResume() {
		super.onResume();
		controlLocationProviderStatuc();
	}
	
	public void controlLocationProviderStatuc(){
		boolean isSomeOfLctProviderEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
				locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (isSomeOfLctProviderEnabled) {
			chbLocationSearch.setEnabled(true);
		}else{
			chbLocationSearch.setChecked(false);
			autoSearchOn=false;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.config_setting, menu);
		MenuItem settingItem=menu.findItem(R.id.itSetting);
		settingItem.setIntent(new Intent(this, GlobalPreference.class));
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home){
			if(citySelected || autoSearchOn){
			setResult(RESULT_OK, resultValue);}
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnSelectCity:
			Intent intent= new Intent(this,CityActivity.class);
			startActivityForResult(intent, REQUEST_CITY);	
			break;
		case R.id.btnConLocationSetting:
			startActivity(new Intent(
			        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			
			break;
		default:
			break;
		}
			
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean isChecked) {
		
	
		switch(view.getId()){
		case R.id.chbLocationSearch:
			
			autoSearchOn=isChecked;
			
			
			Editor edit=sp.edit();
			edit.putBoolean(CONFIG_SP_SEARCH_TYPE+widgetID, autoSearchOn);
			edit.commit();
			btnSelectCity.setEnabled(!autoSearchOn);
			
			if (autoSearchOn) {
				caller.callAutoLocateWthData(this, widgetID, 5);
				tvStatus.setText(R.string.conActLocationAutosearch);
				citySelected=false;
				Editor editor=sp.edit();
				editor.remove(CONFIG_SP_CITY_LAT+widgetID);
				editor.remove(CONFIG_SP_CITY_LON+widgetID);
				editor.remove(CONFIG_SP_CITY_SELECT_+widgetID);
				editor.remove(CONFIG_SP_CITY_NAME_+widgetID);
				editor.remove(CONFIG_SP_CITY_COUNTRY_+widgetID);
				editor.commit();
			}else{
				tvStatus.setText(R.string.conActLocationNoData);
				
			}
			edit.commit();
			break;
		case R.id.chbWthFrMinTemp:
			minCurveOn=isChecked;
			
			data=storage.getWeatherDataById(widgetID);
			if(data!=null)
			weathers=data.getWeather();
			
			if (isChecked) {
				if (data!=null) {
				GraphViewData[] graphDataMinTemp=bindHelper.
						makeGraphWeatherData(weathers, BindHelper.GET_MIN_TEMP) ;
				seriesMin=new GraphViewSeries("min temp",
						new GraphViewSeriesStyle(Color.BLUE,5),graphDataMinTemp);
				graphView.addSeries(seriesMin);	}
			}else{
				if(seriesMax!=null)
					graphView.removeSeries(seriesMin);
			}
			break;
		case R.id.chbWthFrMaxTemp:
			
			data=storage.getWeatherDataById(widgetID);
			if(data!=null)
			weathers=data.getWeather();
			
			maxCurveOn=isChecked;
			if (isChecked) {
				if (data!=null) {
				
				GraphViewData[] graphDataMaxTemp=bindHelper
						.makeGraphWeatherData(weathers, BindHelper.GET_MAX_TEMP);
				seriesMax=new GraphViewSeries("max temp",
						new GraphViewSeriesStyle(Color.RED,5),graphDataMaxTemp);
				graphView.addSeries(seriesMax);}
			} else {
				if(seriesMax!=null)
				graphView.removeSeries(seriesMax);
			}
			break;
		case R.id.chbWthFrZeroTemp:
			zeroTempCurve=isChecked;
			if (isChecked) {
		
				graphView.addSeries(seriesZero);
				
			} else {
				if(seriesZero!=null)
				graphView.removeSeries(seriesZero);
			}
			break;
		}
		
		
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_CITY){
			switch(resultCode){
			case RESULT_OK:
				Bundle bdl=data.getExtras();
				cityName=(String) bdl.get(DataBase.CITY_NAME);
				countryName=(String) bdl.get(DataBase.CITY_COUNTRY);
				cityLat=(Float) bdl.get(DataBase.CITY_LATITUDE);
				cityLon=(Float) bdl.get(DataBase.CITY_LONGITUDE);
				String status=this.getString(R.string.conStatusCitySelected);
				status+=" "+cityName+", "
				+countryName;
				tvStatus.setText(status);
				Editor editor=sp.edit();
				editor.putFloat(CONFIG_SP_CITY_LAT+widgetID, cityLat);
				editor.putFloat(CONFIG_SP_CITY_LON+widgetID, cityLon);
				editor.putBoolean(CONFIG_SP_CITY_SELECT_+widgetID, true);
				editor.putString(CONFIG_SP_CITY_NAME_+widgetID, cityName);
				editor.putString(CONFIG_SP_CITY_COUNTRY_+widgetID, countryName);
				editor.commit();
				citySelected=true;
				caller.callCityWthData(this, widgetID, cityLat, cityLon, 5);
				break;
			case RESULT_CANCELED:
				
				break;
				
			}
		}
	}


	@Override
	public void onStoredWthData(int widgetId) {
		if(widgetId==widgetID){
		graphView.removeAllSeries();
		Data data=storage.getWeatherDataById(widgetID);
		weathers=data.getWeather();
		if (minCurveOn) {
			
			GraphViewData[] graphDataMinTemp=bindHelper.
					makeGraphWeatherData(weathers, BindHelper.GET_MIN_TEMP) ;
			seriesMin=new GraphViewSeries("min temp",
					new GraphViewSeriesStyle(Color.BLUE,5),graphDataMinTemp);
			graphView.addSeries(seriesMin);
		}
		if (maxCurveOn) {
			GraphViewData[] graphDataMaxTemp=bindHelper
					.makeGraphWeatherData(weathers, BindHelper.GET_MAX_TEMP);
			seriesMax=new GraphViewSeries("max temp",
					new GraphViewSeriesStyle(Color.RED,5),graphDataMaxTemp);
			graphView.addSeries(seriesMax);
		}
		if(zeroTempCurve){
			graphView.addSeries(seriesZero);
		}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent updateIntent=new Intent(this, WidgetProvider.class);
		updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {widgetID});
		PendingIntent pIntent= PendingIntent.getBroadcast(this, widgetID, updateIntent, 0);
		try {
			pIntent.send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
