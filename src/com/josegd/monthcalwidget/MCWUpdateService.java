package com.josegd.monthcalwidget;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.widget.RemoteViews;

public class MCWUpdateService extends IntentService {

	private static int yearNow;
	private static int monthNow;
	private static int today;
	private static MonthDisplayHelper mdh;

	public MCWUpdateService() {
		super("MCWUpdateService");
	}
	
	@Override
	public void onHandleIntent(Intent intent) {
		initMonthDisplayHelper();
		updateCalendar(this);
	}
	
	public static void initMonthDisplayHelper() {
		Calendar cal = Calendar.getInstance();
		yearNow = cal.get(Calendar.YEAR);
		monthNow = cal.get(Calendar.MONTH);
		today = cal.get(Calendar.DATE);
		mdh = new MonthDisplayHelper(yearNow, monthNow);
	}

	public static void nextMonth() {
		mdh.nextMonth();
	}
	
	public static void previousMonth() {
		mdh.previousMonth();
	}
	
	public static void updateCalendar(Context context) {
		ComponentName widget = new ComponentName(context, MonthCalWidget.class);
		AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		RemoteViews remViews = buildUpdate(context);
		mgr.updateAppWidget(widget, remViews);
	}

	private static RemoteViews buildUpdate(Context context) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
		setViewAction(context, views, MonthCalWidget.WIDGET_CLICK_NEXT, R.id.nextmonth);
		setViewAction(context, views, MonthCalWidget.WIDGET_CLICK_PREV, R.id.prevmonth);
		setViewAction(context, views, MonthCalWidget.WIDGET_CLICK_MYTV, R.id.monthyear);
		fillCalendar(context, views);
		return views;
	}
	
	private static void setViewAction(Context cont, RemoteViews rv, String action, int idView) {
		Intent intent = new Intent(cont, MonthCalWidget.class);
		intent.setAction(action);		
		rv.setOnClickPendingIntent(idView, PendingIntent.getBroadcast(cont, 0, intent, 0));
	}

	private static void fillCalendar(Context cont, RemoteViews rv) {
		// Month and year (title)
		SimpleDateFormat sdfDate = new SimpleDateFormat("MMMM yyyy");
		Calendar cal = Calendar.getInstance();
		cal.set(mdh.getYear(), mdh.getMonth(), 1);
		rv.setTextViewText( R.id.monthyear, sdfDate.format(cal.getTime()) ); 
		// Dates (grid)
		setWeekDays(cont, rv);
		clearDatesGrid(cont, rv);
		refillDatesGrid(cont, rv);
	}
	
	private static void setWeekDays(Context cont, RemoteViews rv) {
		int identifier;
		DateFormatSymbols weekDays = new DateFormatSymbols();
		for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
			identifier = cont.getResources().getIdentifier("day" + i, "id", cont.getPackageName());
			rv.setTextViewText(identifier, weekDays.getShortWeekdays()[i]);
		}
	}
	
	private static void clearDatesGrid(Context cont, RemoteViews rv) {
		int identifier;
		for (int j = 0; j < 7; j++) {
			for (int i = 0; i < 6; i++) {
				identifier = cont.getResources().getIdentifier("date" + i + j, "id", cont.getPackageName());
				rv.setTextViewText(identifier, "");
			}
		}
	}
	
	private static void refillDatesGrid(Context cont, RemoteViews rv) {
		int dateNumber, nextDateNumber;
		int i, identifier;
		String dateNumberStr;
		for (int j = 0; j < 7; j++) {
			i = 0;
			nextDateNumber = 0;
			do {
				dateNumber = mdh.isWithinCurrentMonth(i, j) ? mdh.getDayAt(i, j) : 0;
				if (dateNumber > 0) {
					identifier = cont.getResources().getIdentifier("date" + i + j, "id", cont.getPackageName());
					dateNumberStr = dateNumber < 10 ? "   " : "  ";
					dateNumberStr += dateNumber + "  ";
					if (mdh.getYear() == yearNow && mdh.getMonth() == monthNow && dateNumber == today) {
						SpannableStringBuilder ssb = new SpannableStringBuilder();
						ssb.append(dateNumberStr);
						ssb.setSpan(new BackgroundColorSpan(cont.getResources().getColor(R.color.today)), 1, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						rv.setTextViewText(identifier, ssb);
					} else {
						rv.setTextViewText(identifier, dateNumberStr);
					}
					nextDateNumber = dateNumber + 7;
				}
				i++;
			} while (nextDateNumber <= mdh.getNumberOfDaysInMonth()); 
		}
	}
	
}