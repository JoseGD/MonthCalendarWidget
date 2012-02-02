package com.josegd.monthcalwidget;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class MonthCalWidget extends AppWidgetProvider {
	
	public static String WIDGET_CLICK_NEXT   = "com.josegd.monthcalwidget.ACTION_NEXT_MONTH";
	public static String WIDGET_CLICK_PREV   = "com.josegd.monthcalwidget.ACTION_PREV_MONTH";
	public static String WIDGET_CLICK_MYTV   = "com.josegd.monthcalwidget.ACTION_CURRENT_MONTH";
	public static String WIDGET_NEWDAY_ALARM = "com.josegd.monthcalwidget.ACTION_NEW_DAY";
	public static String WIDGET_DATE_CHANGED = "android.intent.action.DATE_CHANGED";

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		setNewDayAlarm(context);
	}

	@Override
	public void onDisabled(Context context) {
		cancelNewDayAlarm(context);
		super.onDisabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		initService(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String action = intent.getAction();
			if (action.equals(WIDGET_CLICK_NEXT)) {
				MCWUpdateService.nextMonth();
			} else
				if (action.equals(WIDGET_CLICK_PREV)) {
					MCWUpdateService.previousMonth();
				} else
					if (action.equals(WIDGET_CLICK_MYTV) || action.equals(WIDGET_NEWDAY_ALARM) ||	action.equals(WIDGET_DATE_CHANGED)) {
						MCWUpdateService.initMonthDisplayHelper();
					} else {
						super.onReceive(context, intent);
						return;
					  }
			MCWUpdateService.updateCalendar(context);
		} catch (NullPointerException e) {
			initService(context); 
		}
	}

	private void setNewDayAlarm(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Calendar alarmCal = Calendar.getInstance();
		alarmCal.set(Calendar.HOUR_OF_DAY, 0);
		alarmCal.set(Calendar.MINUTE, 0);
		alarmCal.set(Calendar.SECOND, 0);
		alarmManager.setRepeating(AlarmManager.RTC, alarmCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, createNewDayIntent(context));
	}

	private void cancelNewDayAlarm(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(createNewDayIntent(context));
	}

	private PendingIntent createNewDayIntent(Context context) {
		Intent intent = new Intent(context, MonthCalWidget.class);
		intent.setAction(WIDGET_NEWDAY_ALARM);		
		return PendingIntent.getBroadcast(context, 0, intent, 0);
	}

	private void initService(Context context) {
		context.startService(new Intent(context, MCWUpdateService.class));
	}
	
}

