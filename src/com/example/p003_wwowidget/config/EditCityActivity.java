package com.example.p003_wwowidget.config;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.example.p003_wwowidget.R;
import com.example.p003_wwowidget.R.id;
import com.example.p003_wwowidget.R.layout;
import com.example.p003_wwowidget.R.string;
import com.example.p003_wwowidget.base.DataBase;

public class EditCityActivity extends SherlockActivity implements OnClickListener{

	private EditText edCity, edCountry, edLat, edLon;
	private Button btnEnter;
	private Intent intent;
	private String action;
	
	private String city;
	private String country;
	private float lat;
	private float lon;
	
	private DataBase db;
	
	
	private long checked;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.edit_city);
		
		
		edCity=(EditText) findViewById(R.id.edCtCity);
		edCountry=(EditText) findViewById(R.id.edCtCountry);
		edLat=(EditText) findViewById(R.id.edCtLat);
		edLon=(EditText) findViewById(R.id.edCtLon);
		btnEnter=(Button) findViewById(R.id.btnEdCtEnter);
		btnEnter.setOnClickListener(this);
		db= new DataBase(this);
		db.open();
		intent=getIntent();
		action=intent.getAction();
		if(action.equalsIgnoreCase(CityActivity.CITY_ACTION_EDIT)){
			checked=intent.getLongExtra(CityActivity.CITY_EDIT_ID_KEY,-1);
			if(checked!=-1){
				loadEditData(checked);
			}
		}	
	}
	

	
	public void loadEditData(long id){
		Cursor cursor;
		cursor=db.getRowById(id);
		cursor.moveToFirst();
		
		city=cursor.getString(cursor.getColumnIndex(db.CITY_NAME));
		country=cursor.getString(cursor.getColumnIndex(db.CITY_COUNTRY));
		lat=cursor.getFloat(cursor.getColumnIndex(db.CITY_LATITUDE));
		lon=cursor.getFloat(cursor.getColumnIndex(db.CITY_LONGITUDE));
		
		edCity.setText(city);
		edCountry.setText(country);
		edLat.setText(lat+"");
		edLon.setText(lon+"");
		
	}



	@Override
	public void onClick(View v) {
		try {
			city=edCity.getText().toString();
			country=edCountry.getText().toString();
			lat=Float.parseFloat(edLat.getText().toString());
			lon=Float.parseFloat(edLon.getText().toString());
			
		} catch (Exception e) {
			Toast.makeText(this,getString(R.string.edCityActWrongInput),Toast.LENGTH_SHORT).show();
			return;
		}
		boolean latValidity=(lat>=0 && lat<=90);
		boolean lonValidity=(lon>=0 && lon<=180);
		if( latValidity && lonValidity){
		if(action.equalsIgnoreCase(CityActivity.CITY_ACTION_EDIT)){
		
			
			db.editCity(checked, city, country, lat, lon);
			setResult(RESULT_OK);
			finish();
		}
		if(action.equalsIgnoreCase(CityActivity.CITY_ACTION_CREATE)){

				db.addCity(city, country, lat, lon);
				setResult(RESULT_OK);
				finish();
		}
		}else{
			if(!latValidity) edLat.setText("");
			if(!lonValidity) edLon.setText("");
		}
		}
	
		
	}
