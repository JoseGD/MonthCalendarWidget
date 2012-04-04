package com.josegd.monthcalwidget;

import java.util.Calendar;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class MCWUpdateService extends IntentService {

	public static int yearNow;
	public static int monthNow;
	public static int today;
	private static MonthDisplayHelper mdh;
	private static CalendarFillingHelper cfh;

	public MCWUpdateService() {
		super("MCWUpdateService");
	}
	
	@Override
	public void onHandleIntent(Intent intent) {
		initMonthDisplayHelper();
		updateCalendar(this, intent.getExtras().getString(MonthCalWidget.CALLING_CLASS_NAME));
	}
	
	public static void initMonthDisplayHelper() {
		Calendar cal = Calendar.getInstance();
		yearNow  = cal.get(Calendar.YEAR);
		monthNow = cal.get(Calendar.MONTH);
		today    = cal.get(Calendar.DATE);
		mdh = new MonthDisplayHelper(yearNow, monthNow);
	}

	public static void nextMonth() {
		mdh.nextMonth();
	}
	
	public static void previousMonth() {
		mdh.previousMonth();
	}
	
	public static void updateCalendar(Context context, String cClass) {
		ComponentName widget = new ComponentName(context, cClass);
		AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		int[] appWidgetIds = mgr.getAppWidgetIds(widget);
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			RemoteViews remViews = buildUpdate(context, mgr.getAppWidgetInfo(appWidgetId).initialLayout, cClass);
			mgr.updateAppWidget(appWidgetId, remViews);
		}
	}

	private static RemoteViews buildUpdate(Context context, int layoutId, String cClass) {
		RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
		try {
			setViewAction(context, views, MonthCalWidget.WIDGET_CLICK_NEXT, R.id.nextmonth, cClass);
			setViewAction(context, views, MonthCalWidget.WIDGET_CLICK_PREV, R.id.prevmonth, cClass);
			setViewAction(context, views, MonthCalWidget.WIDGET_CLICK_MYTV, R.id.monthyear, cClass);
		} catch (ClassNotFoundException e) {
		}
		cfh = new CalendarFillingHelper(mdh);
		cfh.fillCalendar(context, views);
		return views;
	}
	
	private static void setViewAction(Context cont, RemoteViews rv, String action, int idView, String cClass) throws ClassNotFoundException {
		Intent intent = new Intent(cont, Class.forName(cClass));
		intent.setAction(action);		
		rv.setOnClickPendingIntent(idView, PendingIntent.getBroadcast(cont, 0, intent, 0));
	}

}