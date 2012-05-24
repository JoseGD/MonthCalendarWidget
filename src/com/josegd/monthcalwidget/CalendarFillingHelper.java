package com.josegd.monthcalwidget;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.widget.RemoteViews;

public class CalendarFillingHelper {
	
	private MonthDisplayHelper mdh;

	public CalendarFillingHelper(MonthDisplayHelper month) {
		mdh = month;
	}
	
	private String getMonthYearString() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("MMMM yyyy");
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
	
	public void fillCalendar(Context cont, RemoteViews rv) {
		// Month and year (title)
		rv.setTextViewText(R.id.monthyear, getMonthYearString()); 
		// Dates (grid)
		setWeekDays(cont, rv);
		clearDatesGrid(cont, rv);
		refillDatesGrid(cont, rv);
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
		for (int j = 0; j < 7; j++) {
			for (int i = 0; i < 6; i++) {
				identifier = cont.getResources().getIdentifier("date" + i + j, "id", cont.getPackageName());
				rv.setTextViewText(identifier, "");
			}
		}
	}
	
	private void refillDatesGrid(Context cont, RemoteViews rv) {
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
					if (mdh.getYear() == MCWUpdateService.yearNow && mdh.getMonth() == MCWUpdateService.monthNow && dateNumber == MCWUpdateService.today) {
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
