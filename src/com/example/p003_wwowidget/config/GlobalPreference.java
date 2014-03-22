package com.example.p003_wwowidget.config;

import com.example.p003_wwowidget.R;
import com.example.p003_wwowidget.R.xml;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class GlobalPreference extends PreferenceActivity {
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.global_pref);
		
		
        
	}
	
	
	}
