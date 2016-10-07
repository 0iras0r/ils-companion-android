/*
 * Original source code from FOSDEM Companion app by Christophe Beyls.
 * Modified by Ludovico Pavesi.
 * Licensed under the Apache License, version 2.0.
 */

package it.reyboz.conferencecompanion.utils;

import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

	private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
	private static final DateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private static final DateFormat longFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:MM:ssZZZZZ", Locale.US);
	private static final DateFormat dayName = new SimpleDateFormat("EEEE", Locale.getDefault());

	public static @Nullable	Date parseShortFormatFromTimezone(String date, TimeZone tz) {
		try {
			shortFormat.setTimeZone(tz);
			return shortFormat.parse(date);
		} catch(ParseException e) {
			return null;
		}
	}

	public static Date parseShortFormatUTC(String date) {
		return parseShortFormatFromTimezone(date, UTC_TIME_ZONE);
	}

	public static Date parseShortFormatLocal(String date) {
		return parseShortFormatFromTimezone(date, TimeZone.getDefault());
	}

	public static @Nullable Date parseLongFormat(String date) {
		if(date == null) {
			return null;
		}
		try {
			return longFormat.parse(date);
		} catch(ParseException e) {
			return null;
		}
	}

	public static String getDayName(Date date) {
		// replace with this in year 2162, when we will finally be able to target APIs in the
		// two-digits range (18 in this case):
		//return android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEE")
		return dayName.format(date);
	}
}
