package com.example.p003_wwowidget.locate;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class MyLocation {
    Timer timer1;
    LocationManager lm;
    long timeG,timeN;
    LocationResult locationResult;
    boolean gps_enabled=false;
    boolean network_enabled=false;

    
    public boolean getLocation(Context context, LocationResult result)
    {
    	

        locationResult=result;
        if(lm==null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

     
        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

      
        if(!gps_enabled && !network_enabled)
            return false;

        if(gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        if(network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        timer1=new Timer();
        timer1.schedule(new GetLastLocation(),20000);
      
        return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {
        	timeG=timeG-System.currentTimeMillis();
    		Log.d("myLogs", "Net timer: "+timeG);
        }
        public void onProviderEnabled(String provider) {
        	timeG=System.currentTimeMillis();
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);
        }
        public void onProviderDisabled(String provider) {
        	timeN=timeN-System.currentTimeMillis();
    		Log.d("myLogs", "Net timer: "+timeN);
        }
        public void onProviderEnabled(String provider) { 
        	  timeN=System.currentTimeMillis();
        	cancelTimer();}
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
             lm.removeUpdates(locationListenerGps);
             lm.removeUpdates(locationListenerNetwork);


             Location net_loc=null, gps_loc=null;
             if(gps_enabled){
            	 gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);	 
             }
                
             if(network_enabled){
                 net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
             }
             if(gps_loc!=null && net_loc!=null){
                 if(gps_loc.getTime()>net_loc.getTime())
                     locationResult.gotLocation(gps_loc);
                 else
                     locationResult.gotLocation(net_loc);
                 return;
             }

             if(gps_loc!=null){
       
                 locationResult.gotLocation(gps_loc);
            
                 return;
             }
             if(net_loc!=null){
            	
                 locationResult.gotLocation(net_loc);
                
                 return;
             }
             locationResult.gotLocation(null);
        }
    }

    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }
    
    //This needs to be called onPause() if getLocation is called from onResume, to prevent crash, as stated on the same stackoverflow thread.
    public void cancelTimer() { 
    	if(timer1!=null){
    	timer1.cancel(); 
    	lm.removeUpdates(locationListenerGps); 
    	lm.removeUpdates(locationListenerNetwork);}
    }
}