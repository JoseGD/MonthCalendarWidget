package com.josegd.monthcalwidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesHelper {

	private SharedPreferences prefs;
	private String FDW_PREFERENCE   = "first_day_week";
	private String SDPNM_PREFERENCE = "show_days_prevnext";
	private String WW_PREFERENCE    = "stretch_widget";
	private String CAL_SUNDAY 		  = "1";
	
	public PreferencesHelper(Context ctx) {
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
	}
	
	public int firstDayOfWeek() {
		 // Cannot use getInt because of unresolved issue 2096 (http://code.google.com/p/android/issues/detail?id=2096)
		return Integer.parseInt(prefs.getString(FDW_PREFERENCE, CAL_SUNDAY));
	}
	
	public boolean showDaysPrevNextMonths() {
		return prefs.getBoolean(SDPNM_PREFERENCE, false);
	}
	
	public boolean stretchedWidgetPreferred() {
		return prefs.getBoolean(WW_PREFERENCE, false);
	}
	
}
