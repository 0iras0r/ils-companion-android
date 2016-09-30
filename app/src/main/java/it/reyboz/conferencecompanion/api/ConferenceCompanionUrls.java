/*
 * Original source code from FOSDEM Companion app by Christophe Beyls.
 * Modified by Ludovico Pavesi.
 * Licensed under the Apache License, version 2.0.
 */

package it.reyboz.conferencecompanion.api;

import java.util.Locale;

/**
 * This class contains all URLs
 * 
 * @author Christophe Beyls
 * 
 */
public class ConferenceCompanionUrls {

	private static final String SCHEDULE_URL = "http://localhost:8000/schedule.xml"; // TODO: replace with public URL
	private static final String EVENT_URL_FORMAT = "https://linuxdaytorino.org/%1$d/talk/%2$s/"; // TODO: get URL from xml
	private static final String PERSON_URL_FORMAT = "https://linuxdaytorino.org/%1$d/user/%2$s/"; // TODO: get URL from xml

	public static String getSchedule() {
		return SCHEDULE_URL;
	}

	public static String getEvent(String slug, int year) {
		// TODO: get slug from xml
		return String.format(Locale.US, EVENT_URL_FORMAT, year, slug);
	}

	public static String getPerson(String slug, int year) {
		// TODO: get slug from xml
		return String.format(Locale.US, PERSON_URL_FORMAT, year, slug);
	}
}
