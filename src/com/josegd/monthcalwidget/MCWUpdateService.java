package com.josegd.monthcalwidget;

import java.util.Calendar;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class MCWUpdateService extends IntentService {

	public static String UPD_AFTER_SETTINGS = "com.josegd.monthcalwidget.APPWIDGET_UPDATE_AFTER_SETTINGS";
	
	private static String CLASS_NAME_3X2_WIDGET = "com.josegd.monthcalwidget.MonthCalWidget3x2";
	private static String CLASS_NAME_SETTINGS   = "com.josegd.monthcalwidget.MCWSettings";
	
	public static int yearNow;
	public static int monthNow;
	public static int today;
	private static MonthDisplayHelper mdh;
	private static CalendarFillingHelper cfh;
	private static PreferencesHelper ph;

	public MCWUpdateService() {
		super("MCWUpdateService");
	}
	
	@Override
	public void onHandleIntent(Intent intent) {
		ph = new PreferencesHelper(this);
		initMonthDisplayHelper(this);
		updateCalendar(this, intent.getExtras().getString(MonthCalWidget.CALLING_CLASS_NAME));
	}
	
	public static void initMonthDisplayHelper(Context ctx) {
		Calendar cal = Calendar.getInstance();
		yearNow  = cal.get(Calendar.YEAR);
		monthNow = cal.get(Calendar.MONTH);
		today    = cal.get(Calendar.DATE);
		mdh = new MonthDisplayHelper(yearNow, monthNow, ph.firstDayOfWeek());
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
		int wideLayout = 0;
		if (ph.stretchedWidgetPreferred())
			wideLayout = cClass.equals(CLASS_NAME_3X2_WIDGET) ? R.layout.main_3x2_wide : R.layout.main_4x3_wide;
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			AppWidgetProviderInfo awpi = mgr.getAppWidgetInfo(appWidgetId);
			int layout = ph.stretchedWidgetPreferred() ? wideLayout : awpi.initialLayout;
			if (awpi != null) {
				RemoteViews remViews = buildUpdate(context, layout, cClass, appWidgetId);
				mgr.updateAppWidget(appWidgetId, remViews);
			}
		}
	}

	private static RemoteViews buildUpdate(Context context, int layoutId, String cClass, int awId) {
		RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
		try {
			setViewAction(context, views, MonthCalWidget.WIDGET_CLICK_NEXT, R.id.nextmonth, cClass);
			setViewAction(context, views, MonthCalWidget.WIDGET_CLICK_PREV, R.id.prevmonth, cClass);
			setViewAction(context, views, MonthCalWidget.WIDGET_CLICK_MYTV, R.id.monthyear, cClass);
			setViewActivityAction(context, views, R.id.settings, CLASS_NAME_SETTINGS, awId);
		} catch (ClassNotFoundException e) {
		}
		cfh = new CalendarFillingHelper(mdh, ph);
		cfh.fillCalendar(context, views);	
		return views;
	}
	
	private static void setViewAction(Context ctx, RemoteViews rv,
											    String action, int idView, String cClass) throws ClassNotFoundException {
		Intent intent = new Intent(ctx, Class.forName(cClass));
		intent.setAction(action);
		rv.setOnClickPendingIntent(idView, PendingIntent.getBroadcast(ctx, 0, intent, 0));
	}

	private static void setViewActivityAction(Context ctx, RemoteViews rv,
															int idView, String cClass, int awId) throws ClassNotFoundException {
		Intent intent = new Intent(ctx, Class.forName(cClass));
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, awId);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	   rv.setOnClickPendingIntent(idView, PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
	}
		
}


