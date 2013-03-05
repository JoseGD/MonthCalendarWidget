package com.josegd.monthcalwidget;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;

public class MCWSettings extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	private ListPreference mFDWListPreference;
	private Preference mEmailDevPreference;
	private Preference mDonationsPreference;
	private Preference mWebsitePreference;
	
	private static String FDW_PREFERENCE     = "first_day_week";
	private static String EMAIL_PREFERENCE   = "email_developer";
	private static String DONATE_PREFERENCE  = "donate";
	private static String WEBSITE_PREFERENCE = "website";
	private static String MAILTO_DEVELOPER   = "mailto:jgonzalezdamico@gmail.com";
	private static String SUBJECT_MAILDEV    = "[Month Calendar Widget feedback]";
	private static String DONATIONS_URL 	  = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=G2KKES2AJ79AC";
	private static String WEBSITE_URL 		  = "http://mobiledevjourney.blogspot.com";
	private static String CONFIGURE_ACTION   = "android.appwidget.action.APPWIDGET_CONFIGURE";
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.mcw_settings);

		mFDWListPreference   = (ListPreference) getPreferenceScreen().findPreference(FDW_PREFERENCE);
		mEmailDevPreference  = (Preference) getPreferenceScreen().findPreference(EMAIL_PREFERENCE);
		mDonationsPreference = (Preference) getPreferenceScreen().findPreference(DONATE_PREFERENCE);
		mWebsitePreference   = (Preference) getPreferenceScreen().findPreference(WEBSITE_PREFERENCE);
		
		mEmailDevPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		   public boolean onPreferenceClick(Preference preference) {
		   	Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(MAILTO_DEVELOPER));
		   	intent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT_MAILDEV);
				startActivity(intent);
			   return true;
			}
		});		
		mDonationsPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		   public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DONATIONS_URL)));
			   return true;
			}
		});		
		mWebsitePreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		   public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(WEBSITE_URL)));
			   return true;
			}
		});		
	}
	
	@SuppressWarnings("deprecation")
	@Override
   protected void onResume() {
       super.onResume();
       mFDWListPreference.setSummary(mFDWListPreference.getEntry()); 
       getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
   }
	
	@SuppressWarnings("deprecation")
	@Override
   protected void onPause() {
       super.onPause();
       getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
   }

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      if (key.equals(FDW_PREFERENCE)) {
      	mFDWListPreference.setSummary(mFDWListPreference.getEntry()); 
      }
   }
	
	@SuppressLint("NewApi")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && Build.VERSION.SDK_INT < 5) {
			onBackPressed();
		}
		return(super.onKeyDown(keyCode, event));
	}
	
	@Override
	public void onBackPressed() {
		if (CONFIGURE_ACTION.equals(getIntent().getAction())) {
			Intent intent = getIntent();
			Bundle extras = intent.getExtras();
			if (extras != null) {
				int id = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
				Intent result = new Intent();
				result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
				setResult(RESULT_OK, result);	// This will NOT trigger ACTION_APPWIDGET_UPDATE, better send a separate broadcast
				this.getApplicationContext().sendBroadcast(new Intent(MCWUpdateService.UPD_AFTER_SETTINGS));
			}
		}
		finish();
		//super.onBackPressed();  // Fails on SDK < 5 (2.0)
	} 
	
}
