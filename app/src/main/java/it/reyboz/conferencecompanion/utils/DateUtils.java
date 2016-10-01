/*
 * Original source code from FOSDEM Companion app by Christophe Beyls.
 * Modified by Ludovico Pavesi.
 * Licensed under the Apache License, version 2.0.
 */

package it.reyboz.conferencecompanion.utils;

import android.content.Context;

import java.text.DateFormat;
import java.util.TimeZone;

public class DateUtils {

	private static final TimeZone BELGIUM_TIME_ZONE = TimeZone.getTimeZone("GMT+1");

	/**
	 * Don't use this.
	 *
	 * @return Belgium time zone, actually
	 * @deprecated
     */
	public static TimeZone getUTCTimeZone() {
		return BELGIUM_TIME_ZONE;
	}

	/**
	 * @deprecated
	 */
	public static DateFormat withUTCTimeZone(DateFormat format) {
		format.setTimeZone(BELGIUM_TIME_ZONE);
		return format;
	}

	public static DateFormat getTimeDateFormat(Context context) {
		return withUTCTimeZone(android.text.format.DateFormat.getTimeFormat(context));
	}
}
