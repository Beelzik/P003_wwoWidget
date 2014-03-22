package com.example.p003_wwowidget.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.p003_wwowidget.R;
import com.example.p003_wwowidget.R.string;
import com.example.wwolibrary.LocaleWwoData.Data;
import com.example.wwolibrary.LocaleWwoData.Data.CurrentCondition;
import com.example.wwolibrary.LocaleWwoData.Data.Weather;
import com.jjoe64.graphview.GraphView.GraphViewData;

public class BindHelper {

	private final String GRADUS_FARENHEIT="°F";
	private final String GRADUS_CELSIUM="°C";
	
	public static final int GET_MAX_TEMP=1;
	public static final int GET_MIN_TEMP=0;
	public static final int GET_CUR_TEMP=2;
	
	private SharedPreferences sp;
	private Context ctx;
	
	public BindHelper(Context c) {
		ctx=c;
		sp=PreferenceManager.getDefaultSharedPreferences(ctx);
	}
	
	public GraphViewData[] makeGraphWeatherData(Weather[] weathers, int wutTemp){
		GraphViewData[] data=new GraphViewData[weathers.length];
		String format=sp.getString(ctx.getString(R.string.preKeyGradus),"°C");
		
		switch(wutTemp){
		case GET_MAX_TEMP:
			if(format.equalsIgnoreCase(GRADUS_CELSIUM)){
				for (int i = 0; i < weathers.length; i++) {
					data[i]= new GraphViewData(i+1, weathers[i].getTempMaxC());
				}}
			if(format.equalsIgnoreCase(GRADUS_FARENHEIT)){
					for (int i = 0; i < weathers.length; i++) {
						data[i]= new GraphViewData(i+1, weathers[i].getTempMaxF());
					}	
				}
			break;
		case GET_MIN_TEMP:
			if(format.equalsIgnoreCase(GRADUS_CELSIUM)){
				for (int i = 0; i < weathers.length; i++) {
					data[i]= new GraphViewData(i+1, weathers[i].getTempMinC());
				}}
			if(format.equalsIgnoreCase(GRADUS_FARENHEIT)){
					for (int i = 0; i < weathers.length; i++) {
						data[i]= new GraphViewData(i+1, weathers[i].getTempMinF());
					}	
				}
			break;
		}
		return data;
		
	}
	
	public String makeFormmtedWthText(Data data,int wutIs){
		Weather[] weather;
		String answer=null;
		String format=sp.getString(ctx.getString(R.string.preKeyGradus),"°C");
		switch(wutIs){
		case GET_CUR_TEMP:
			CurrentCondition cc=data.getCurrent_condition();
			if (format.equalsIgnoreCase("°C")) {
				answer=cc.getTemp_C()+" "+format;
			} else {
				answer=cc.getTemp_F()+" "+format;
			}
			break;
		case GET_MAX_TEMP:
			weather=data.getWeather();
			if (format.equalsIgnoreCase("°C")) {
				answer=weather[0].getTempMaxC()+" "+format;
			} else {
				answer=weather[0].getTempMaxF()+" "+format;
			}
			
			break;
		case GET_MIN_TEMP:
			weather=data.getWeather();
			if (format.equalsIgnoreCase("°C")) {
				answer=weather[0].getTempMinC()+" "+format;
			} else {
				answer=weather[0].getTempMinF()+" "+format;
			}
			
			break;
		}
		
		return answer;
		
	}

}
