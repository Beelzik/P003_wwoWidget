package com.example.p003_wwowidget.config;

import java.text.MessageFormat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.example.p003_wwowidget.R;
import com.example.p003_wwowidget.R.id;
import com.example.p003_wwowidget.R.layout;
import com.example.p003_wwowidget.R.menu;
import com.example.p003_wwowidget.R.string;
import com.example.p003_wwowidget.base.DataBase;



public class CityActivity extends SherlockFragmentActivity implements
LoaderCallbacks<Cursor>,OnItemClickListener{

	
	ListView lvCity;
	DataBase db;
	//MyCursorAdapter adapter;
	SimpleCursorAdapter adapter;
	SearchView svCity;
	String mFilter;
	ActionMode actionMode;
	public static final String WIDGET_PREF_CITY="pref_city";
	public static final String CITY_ACTION_EDIT="city_action_edit";
	public static final String CITY_ACTION_CREATE="city_action_create";
	
	public static final String CITY_EDIT_ID_KEY="city_edit";
	
	public static final int REQUEST_CITY_EDIT=0;
	public static final int REQUEST_CITY_CREATE=1;
	
	 // формируем столбцы сопоставления
    String[] from = new String[] { DataBase.CITY_NAME, DataBase.CITY_COUNTRY };
   int[] to = new int[] { R.id.tvItemCity, R.id.tvItemCountry };

    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.city);
		db= new DataBase(this);
		db.open();

		adapter= new SimpleCursorAdapter(this, R.layout.item_city, null, from, to, 0);
	lvCity=(ListView) findViewById(R.id.lvCity);
	lvCity.setAdapter(adapter);
	lvCity.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	lvCity.setOnItemClickListener(this);
	
	
	getSupportLoaderManager().initLoader(0,null, this);
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}
	
	@Override
	public void onItemClick(AdapterView<?> data, View view, int position, long id) {
	long[] checked=lvCity.getCheckedItemIds();
	//id=data.getC
	Log.d("WWO3", "id: "+id);
	Log.d("WWO3", "position: "+position);
//	Log.d("WWO3", "checked: "+checked[0]);
	for (long l : checked) {
		Log.d("WWO3", "checked: "+l);
	}
		if(checked.length>0){
			if(actionMode==null){
				actionMode=startActionMode(new CityModeCallback());
			}
			 CharSequence format = getResources().getText(R.string.n_selcted_format);
             actionMode.setTitle(MessageFormat.format(format.toString(), checked.length));
             actionMode.invalidate();
			//actionMode.invalidate();
		}else{
			if(actionMode!=null)
			actionMode.finish();
		}
	}
	
	
	
	public class CityModeCallback implements Callback{
		
		long[] checked;
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.city_mode, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			MenuItem itAdd=menu.findItem(R.id.itCitySelectCity);
			MenuItem itEdit=menu.findItem(R.id.itCityEdit);
			 checked=lvCity.getCheckedItemIds();
			 
		
			boolean onSelected=checked.length==1;
			boolean changed=setVisible(itEdit, onSelected) || setVisible(itAdd, onSelected);
			if (changed) {
				mode.invalidate();
			}
			return changed;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch(item.getItemId()){
			case R.id.itCitySelectCity:
				takeCity(checked[0]);
				
				break;
			case R.id.itCityEdit:
				Intent intent=new Intent(CityActivity.this, EditCityActivity.class);
				intent.setAction(CITY_ACTION_EDIT);
				intent.putExtra(CITY_EDIT_ID_KEY, checked[0]);
				startActivityForResult(intent, REQUEST_CITY_EDIT);
				break;
			case R.id.itCityDelete:
				db.deleteCity(checked);
				getSupportLoaderManager().restartLoader(0, null, CityActivity.this);
				break;
			default:
				break;
			}
			mode.finish();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			for (int i = 0; i < lvCity.getCount(); i++)
                lvCity.setItemChecked(i, false);

            if (mode == actionMode) {
                actionMode = null;
            }
		}
		
	private boolean	setVisible(MenuItem item, boolean visible){
		if(item.isVisible()==visible){
			return false;
		}
		item.setVisible(visible);
		return true;		
	}
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//MenuInflater inflater=getSupportMenuInflater();
		//inflater.inflate(R.menu.city_bar_menu, menu);
		getSupportMenuInflater().inflate(R.menu.city_bar_menu, menu);
		MenuItem item=menu.findItem(R.id.svCity);
		svCity=(SearchView) item.getActionView();
		 if (null != svCity )
	        {
			 svCity.setIconifiedByDefault(false);   
	        }
		if(svCity!=null){
		svCity.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				mFilter=newText;
				getSupportLoaderManager().restartLoader(0, null, CityActivity.this);
				return true;
			}
		});}
		return true; 
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.d("WWO3", "onMenuItemSelected");
		if (item.getItemId()==R.id.itAddNewCity) {
			Intent intent= new Intent(this, EditCityActivity.class);
			intent.setAction(CITY_ACTION_CREATE);
			startActivityForResult(intent, REQUEST_CITY_CREATE);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	

	
	public void takeCity(long id){
		long cityId;
		String city;
		String country;
		Float lat,lon;
		Cursor cursor=db.getCity(id);
		if(cursor.moveToFirst()){
			
		cityId=cursor.getInt(cursor.getColumnIndex(DataBase.CITY_ID));
		city=cursor.getString(cursor.getColumnIndex(DataBase.CITY_NAME));
		country=cursor.getString(cursor.getColumnIndex(DataBase.CITY_COUNTRY));
		lat=cursor.getFloat(cursor.getColumnIndex(DataBase.CITY_LATITUDE));
		lon=cursor.getFloat(cursor.getColumnIndex(DataBase.CITY_LONGITUDE));
		
		Bundle extra= new Bundle();
		
		extra.putString(DataBase.CITY_NAME, city);

		extra.putString(DataBase.CITY_COUNTRY, country);
		extra.putFloat(DataBase.CITY_LATITUDE, lat);
		extra.putFloat(DataBase.CITY_LONGITUDE, lon);
			Intent intent= new Intent().putExtras(extra);
			setResult(RESULT_OK, intent);
			finish();
		}
	}
	

	@Override
	public boolean onSearchRequested() {
		svCity.setIconified(false);
		svCity.requestFocus();
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		String selection=null;
		String selectionArgs[]=null;
		if(!TextUtils.isEmpty(mFilter)){
			String search = "%" + mFilter + "%";
            selection =DataBase.CITY_ASCII_NAME + " like ? or " 
                    + DataBase.CITY_NAME + " like ? or "
                    + DataBase.CITY_COUNTRY + " like ?";
            selectionArgs = new String[] { search, search, search };

		}
		return new MyCursorLoader(this, db,selection,selectionArgs);
	}


	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.d("DBLog","onLoadFinished");
		adapter.swapCursor(cursor);
		
		
	}


	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	
	
	public static class MyCursorLoader extends CursorLoader{
		String selection;
		String selectionArgs[];
		DataBase base;
		
		public MyCursorLoader(Context context,DataBase base,String selection,String selectionArgs[]) {
			super(context);
			this.selection=selection;
			this.selectionArgs=selectionArgs;
			this.base=base;
		}
		
		@Override
		public Cursor loadInBackground() {
			Cursor cursor=base.getData(selection,selectionArgs);
			return cursor;
		}
		
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(resultCode){
		case RESULT_OK:
			getSupportLoaderManager().restartLoader(0,null,this);
			break;
		case RESULT_CANCELED:
			break;
		}
	}




	
	
	}
