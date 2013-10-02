package com.josegd.monthcalwidget;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.RemoteViews;

public class CalendarFillingHelper {
	
	private static String DATE_IN_THE_CORNER = "date67";
	private static String SETTINGS_BUTTON    = "settings";
	
	private MonthDisplayHelper mdh;
	private PreferencesHelper ph;

	public CalendarFillingHelper(MonthDisplayHelper month, PreferencesHelper prefs) {
		mdh = month;
		ph = prefs;
	}
	
	public void fillCalendar(Context cont, RemoteViews rv) {
		// Month and year (title)
		rv.setTextViewText(R.id.monthyear, getMonthYearString()); 
		// Dates (grid)
		defineCornerViewVisibility(cont, rv);
		setWeekDays(cont, rv);
		clearDatesGrid(cont, rv);
		refillDatesGrid(cont, rv);
	}
	
	private void defineCornerViewVisibility(Context cont, RemoteViews rv) {
		int idSettings = cont.getResources().getIdentifier(SETTINGS_BUTTON, "id", cont.getPackageName());
		int idDateCorner = cont.getResources().getIdentifier(DATE_IN_THE_CORNER, "id", cont.getPackageName());
		if (ph.showSettingsButton()) {
			rv.setViewVisibility(idSettings, View.VISIBLE);
			rv.setViewVisibility(idDateCorner, View.GONE);
		} else {
			rv.setViewVisibility(idDateCorner, View.VISIBLE);
			rv.setViewVisibility(idSettings, View.GONE);
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private String getMonthYearString() {
		String fmt = Build.VERSION.SDK_INT <= 8 ? "MMMM yyyy" : "LLLL yyyy";   // MMMM not the correct format for slavic languages / LLLL only available on 2.3+
		SimpleDateFormat sdfDate = new SimpleDateFormat(fmt);
		Calendar cal = Calendar.getInstance();
		cal.set(mdh.getYear(), mdh.getMonth(), 1);
		return sdfDate.format(cal.getTime());
	}
	
	private String getWeekDayShortName(int index) {
		int firstDay = mdh.getWeekStartDay();
		DateFormatSymbols weekDays = new DateFormatSymbols();
		String strWeekDay = "";
		switch (firstDay) {
			case Calendar.SUNDAY:
				strWeekDay = weekDays.getShortWeekdays()[index];
				break;
			case Calendar.MONDAY:
				strWeekDay = index != 7 ? weekDays.getShortWeekdays()[index+1] : weekDays.getShortWeekdays()[1];
				break;
			case Calendar.SATURDAY:
				strWeekDay = index != 1 ? weekDays.getShortWeekdays()[index-1] : weekDays.getShortWeekdays()[7];
				break;
			default:
				break;
		}
		return strWeekDay;
	}
	
	private void setWeekDays(Context cont, RemoteViews rv) {
		int identifier;
		for (int i = 1; i <= 7; i++) {
			identifier = cont.getResources().getIdentifier("day" + i, "id", cont.getPackageName());
			rv.setTextViewText(identifier, getWeekDayShortName(i));
		}
	}
	
	private void clearDatesGrid(Context cont, RemoteViews rv) {
		int identifier;
		for (int i = 1; i <= 6; i++) {
			for (int j = 1; j <= 7; j++) {
				if (!(i == 6 && j == 7) || !ph.showSettingsButton()) {  // No TextView at (6,7) if Settings icon visible
					identifier = cont.getResources().getIdentifier("date" + i + j, "id", cont.getPackageName());
					rv.setTextViewText(identifier, " "); // Empty string caused layout problems in tablets
				}
			}
		}
	}

	private void refillDatesGrid(Context cont, RemoteViews rv) {
		int dateNumber;
		int identifier;
		boolean isToday;
		for (int i = 1; i <= 6; i++)
			for (int j = 1; j <= 7; j++) {
				if (!(i == 6 && j == 7) || !ph.showSettingsButton()) {  // No TextView at (6,7) if Settings icon visible
					dateNumber = mdh.getDayAt(i-1, j-1);
					identifier = cont.getResources().getIdentifier("date" + i + j, "id", cont.getPackageName());
					isToday = mdh.getYear() == MCWUpdateService.yearNow &&
								 mdh.getMonth() == MCWUpdateService.monthNow &&
								 dateNumber == MCWUpdateService.today && mdh.isWithinCurrentMonth(i-1, j-1);
					if (Build.VERSION.SDK_INT >= 8) {
						rv.setInt(identifier, "setBackgroundResource", isToday ? R.color.today : android.R.color.transparent);
						rv.setTextViewText(identifier, dateNumber + "");
					} else {
						String dateNumberStr = dateNumber + "";
						SpannableStringBuilder ssb = new SpannableStringBuilder();
						ssb.append(dateNumberStr);
						if (isToday)
							ssb.setSpan(new BackgroundColorSpan(cont.getResources().getColor(R.color.today)), 0, dateNumberStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						rv.setTextViewText(identifier, ssb);
					}
					if (mdh.isWithinCurrentMonth(i-1, j-1)) {
						rv.setTextColor(identifier, cont.getResources().getColor(R.color.white));
						rv.setViewVisibility(identifier, View.VISIBLE);
					} else {
						rv.setTextColor(identifier, cont.getResources().getColor(R.color.gray));
						rv.setViewVisibility(identifier, ph.showDaysPrevNextMonths() ? View.VISIBLE : View.INVISIBLE);
					}
				}
			}
	}
	
}
