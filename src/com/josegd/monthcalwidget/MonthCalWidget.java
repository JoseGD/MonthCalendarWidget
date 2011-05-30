package com.josegd.monthcalwidget;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.widget.RemoteViews;

public class MonthCalWidget extends AppWidgetProvider {
	
	public static String WIDGET_CLICK_NEXT = "com.josegd.monthcalwidget.ACTION_NEXT_MONTH";
	public static String WIDGET_CLICK_PREV = "com.josegd.monthcalwidget.ACTION_PREV_MONTH";
	public static String WIDGET_CLICK_MYTV = "com.josegd.monthcalwidget.ACTION_CURRENT_MONTH";

	private static Calendar cal = Calendar.getInstance();
	private static int yearNow  = cal.get(Calendar.YEAR);
	private static int monthNow = cal.get(Calendar.MONTH);
	private static int today 	 = cal.get(Calendar.DATE);
	private static MonthDisplayHelper mdh = new MonthDisplayHelper(yearNow, monthNow);
	private SimpleDateFormat cmyDate = new SimpleDateFormat("MMMM yyyy");
	private DateFormatSymbols weekDays = new DateFormatSymbols();

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		RemoteViews remViews = new RemoteViews(context.getPackageName(), R.layout.main);
		setViewAction(context, remViews, WIDGET_CLICK_NEXT, R.id.nextmonth);
		setViewAction(context, remViews, WIDGET_CLICK_PREV, R.id.prevmonth);
		setViewAction(context, remViews, WIDGET_CLICK_MYTV, R.id.monthyear);
		updateCalendar(context, remViews, new ComponentName(context, MonthCalWidget.class));
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.main);
		ComponentName me = new ComponentName(context, MonthCalWidget.class);
		String action = intent.getAction();
		if (action.equals(WIDGET_CLICK_NEXT)) {
			mdh.nextMonth();
		} else
			if (action.equals(WIDGET_CLICK_PREV)) {
				mdh.previousMonth();
			} else
				if (action.equals(WIDGET_CLICK_MYTV)) {
					mdh = new MonthDisplayHelper(yearNow, monthNow);
				} else {
					super.onReceive(context, intent);
					return;
				  }
		updateCalendar(context, rv, me);
	}
	
	private void setViewAction(Context cont, RemoteViews rv, String action, int idView) {
		Intent intent = new Intent(cont, MonthCalWidget.class);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(cont, 0, intent, 0);
		rv.setOnClickPendingIntent(idView, pendingIntent);
	}

	private void updateCalendar(Context cont, RemoteViews rv, ComponentName cn) {
		rv.setTextViewText(R.id.monthyear, cmyDate.format(mdh.getTime())); // Month and year (title) 
		setWeekDays(cont, rv);
		refillDatesGrid(cont, rv);														 // Dates (grid)
		AppWidgetManager.getInstance(cont).updateAppWidget(cn, rv);
	}
	
	private void setWeekDays(Context cont, RemoteViews rv) {
		int identifier;
		for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
			identifier = cont.getResources().getIdentifier("day" + i, "id", cont.getPackageName());
			rv.setTextViewText(identifier, weekDays.getShortWeekdays()[i]);
		}
	}
	
	private void refillDatesGrid(Context cont, RemoteViews rv) {
		int dateNumber, nextDateNumber;
		int i, j, identifier;
		String dateNumberStr;
		for (j = 0; j < 7; j++) {
			for (i = 0; i < 6; i++) {
				identifier = cont.getResources().getIdentifier("date" + i + j, "id", cont.getPackageName());
				rv.setTextViewText(identifier, "");
			}
		}
		for (j = 0; j < 7; j++) {
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

