package com.example.p003_wwowidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.p003_wwowidget.storage.WeatherDataStorage;

public class WidgetProvider extends AppWidgetProvider {
	
	public static final String UPDATE_ALL_WIDGETS="UPDATE_ALL_WIDGETS";
	int wdgUpdateCooldownMls=60000;
	 private PendingIntent pIntent = null; 
	
	 @Override
	  public void onEnabled(Context context) {
	    super.onEnabled(context);

		Intent uIntent = new Intent(context, WidgetProvider.class);
		uIntent.setAction(UPDATE_ALL_WIDGETS);
	    pIntent = PendingIntent.getBroadcast(context, 0, uIntent, 0);
	    AlarmManager alarmManager = (AlarmManager) context
	        .getSystemService(Context.ALARM_SERVICE);
	    alarmManager.set(AlarmManager.RTC, System.currentTimeMillis()+20000, pIntent);
	  }

	  @Override
	  public void onDisabled(Context context) {
	    super.onDisabled(context);
	    final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
	    if (pIntent != null)
        {
            am.cancel(pIntent);
        }
        
	  }

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		 
         wdgUpdateCooldownMls= Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).
				  getString(context.getString(R.string.preKeyUpdate), "299999"));
        
         Intent intent;
			//intent=new Intent("com.example.p003_wwowidget.UpdateService");
			//context.stopService(intent);
			 intent= new Intent(context, UpdateService.class);
			 for (int i : appWidgetIds) {
				Log.d("WWO3","provider id="+i);
			}
			intent.putExtra("appWidgetIds", appWidgetIds);
			context.startService(intent);
				
	}
	
	
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		 if (intent.getAction().equalsIgnoreCase(UPDATE_ALL_WIDGETS)) {
			
		      ComponentName thisAppWidget = new ComponentName(
		          context.getPackageName(), getClass().getName());
		      AppWidgetManager appWidgetManager = AppWidgetManager
		          .getInstance(context);
		      wdgUpdateCooldownMls= Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).
					  getString(context.getString(R.string.preKeyUpdate), "299999"));
		    
				 	Intent uIntent = new Intent(context, WidgetProvider.class);
				uIntent.setAction(UPDATE_ALL_WIDGETS);
			    pIntent = PendingIntent.getBroadcast(context, 0, uIntent, 0);
			    AlarmManager alarmManager = (AlarmManager) context
			        .getSystemService(Context.ALARM_SERVICE);
			    alarmManager.set(AlarmManager.RTC, System.currentTimeMillis()+wdgUpdateCooldownMls, pIntent);
			    
			    
		      int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
		      
		      WeatherDataStorage storage=(WeatherDataStorage) context.getApplicationContext();
		      
		      Log.d("WWO3",storage.getAllKey().toString());
		      
		    //  Intent up=new Intent("com.example.p003_wwowidget.UpdateService");
		  	//context.stopService(up);
		      Intent	 up= new Intent(context, UpdateService.class);
		
			up.setAction(UPDATE_ALL_WIDGETS);
			up.putExtra("appWidgetIds", ids);
			if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("AUTO_UPDATE_WIDGET", true)) {
				context.startService(up);
			}
			
			
		}
	}
	

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		WeatherDataStorage storage=(WeatherDataStorage) context.getApplicationContext();
	     for (int id : appWidgetIds) {
			storage.removeDataFromStorage(id);
		}	
	}
	
}
