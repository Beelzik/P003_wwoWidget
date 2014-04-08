package com.example.p003_wwowidget.ui;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

import com.example.p003_wwowidget.R;
import com.example.p003_wwowidget.storage.WeatherDataStorage;
import com.example.p003_wwowidget.utils.BindHelper;
import com.example.wwolibrary.LocaleWwoData.Data;
import com.example.wwolibrary.LocaleWwoData.Data.Weather;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

public class WeatherGraphFragment extends Fragment implements OnCheckedChangeListener {

	private LineGraphView graphView;
	private GraphViewSeries seriesMax;
	private GraphViewSeries seriesMin;
	private GraphViewSeries seriesZero;
	
	private boolean minCurveOn=true;
	private boolean maxCurveOn=true;
	private boolean zeroTempCurve=true;
	
	private Data fragmentData;
	
	
	BindHelper bindHelper;
	
	private Weather[] weathers;
	int widgetID;
	
	LinearLayout layout;
	CheckBox chbMinTemp,chbMaxTemp,chbZeroTemp;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view =inflater.inflate(R.layout.weather_graph_fragment, null);
		layout= (LinearLayout) view.findViewById(R.id.grWth);
		graphView = new LineGraphView(
			     getActivity()// context
			      , "Weather Graph " // heading
			);
		
		
		chbMinTemp=(CheckBox)view.findViewById(R.id.chbWthFrMinTemp);
		chbMaxTemp=(CheckBox) view.findViewById(R.id.chbWthFrMaxTemp);
		chbZeroTemp=(CheckBox) view.findViewById(R.id.chbWthFrZeroTemp);
		chbMaxTemp.setOnCheckedChangeListener(this);
		chbMinTemp.setOnCheckedChangeListener(this);
		chbZeroTemp.setOnCheckedChangeListener(this);
		
		
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
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
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
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		widgetID=getArguments().getInt(ConfigActivity.KEY_WIDGET_ID_FOR_FRAGMENT);
		layout.addView(graphView);
	
		bindHelper= new BindHelper(getActivity());

		
				
				if (fragmentData!=null) 
				weathers=fragmentData.getWeather();
	}


	@Override
	public void onCheckedChanged(CompoundButton view, boolean isChecked) {
		
		switch(view.getId()){
		
		case R.id.chbWthFrMinTemp:
			minCurveOn=isChecked;
			
			if(fragmentData!=null)
			weathers=fragmentData.getWeather();
			
			if (isChecked) {
				if (fragmentData!=null) {
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
			
			if(fragmentData!=null)
			weathers=fragmentData.getWeather();
			
			maxCurveOn=isChecked;
			if (isChecked) {
				if (fragmentData!=null) {
				
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
	
	
	public void setChangeData(Data data, int widgetId){
		fragmentData=data;
		if(widgetId==widgetID){
			graphView.removeAllSeries();
			weathers=fragmentData.getWeather();
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
	

}
