package com.josegd.monthcalwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class MonthCalWidget extends AppWidgetProvider {
	
	public static String CALLING_CLASS_NAME   = "MCW_Descendant";
	public static String WIDGET_CLICK_NEXT    = "com.josegd.monthcalwidget.ACTION_NEXT_MONTH";
	public static String WIDGET_CLICK_PREV    = "com.josegd.monthcalwidget.ACTION_PREV_MONTH";
	public static String WIDGET_CLICK_MYTV    = "com.josegd.monthcalwidget.ACTION_CURRENT_MONTH";
	private static String WIDGET_DATE_CHANGED = "android.intent.action.DATE_CHANGED";
	
	private AlarmManagerHelper alh;
	private String mcwClassName = this.getClass().getName();
	
	@Override
	public void onDisabled(Context context) {
		alh.cancelNewDayAlarm(context);
		super.onDisabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		alh = new AlarmManagerHelper(mcwClassName);
		alh.setNewDayAlarm(context);
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
					if (action.equals(WIDGET_CLICK_MYTV) || 
							action.equals(AlarmManagerHelper.WIDGET_NEWDAY_ALARM) || action.equals(WIDGET_DATE_CHANGED)) {
						MCWUpdateService.initMonthDisplayHelper(context);
					} else
						if (action.equals(MCWUpdateService.UPD_AFTER_SETTINGS)) {
							initService(context);
						} else {
						   super.onReceive(context, intent);
						   return;
						}
			MCWUpdateService.updateCalendar(context, mcwClassName);
		} catch (NullPointerException e) {
			initService(context); 
		}
	}

	private void initService(Context context) {
		Intent intent = new Intent(context, MCWUpdateService.class);
		intent.putExtra(CALLING_CLASS_NAME, mcwClassName);
		context.startService(intent);
	}
	
}
