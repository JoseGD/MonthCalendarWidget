package com.josegd.monthcalwidget;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmManagerHelper {

	public static String WIDGET_NEWDAY_ALARM = "com.josegd.monthcalwidget.ACTION_NEW_DAY";
	
	private String callingClass;
	private Calendar alarmCal;

	public AlarmManagerHelper(String cClass) {
		callingClass = cClass;
		alarmCal = Calendar.getInstance();
	}
	
	public void setNewDayAlarm(Context context) {
		try {
			alarmCal.set(Calendar.HOUR_OF_DAY, 0);
			alarmCal.set(Calendar.MINUTE, 0);
			alarmCal.set(Calendar.SECOND, 0);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.RTC, alarmCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, createNewDayIntent(context));
		} catch (ClassNotFoundException e) {
		}
	}

	public void cancelNewDayAlarm(Context context) {
		try {
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(createNewDayIntent(context));
		} catch (ClassNotFoundException e) {
		}
	}

	private PendingIntent createNewDayIntent(Context context) throws ClassNotFoundException {
		Intent intent = new Intent(context, Class.forName(callingClass));
		intent.setAction(WIDGET_NEWDAY_ALARM);		
		return PendingIntent.getBroadcast(context, 0, intent, 0);
	}

}
