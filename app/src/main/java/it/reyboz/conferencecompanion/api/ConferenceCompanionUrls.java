/*
 * Original source code from FOSDEM Companion app by Christophe Beyls.
 * Modified by Ludovico Pavesi.
 * Licensed under the Apache License, version 2.0.
 */

package it.reyboz.conferencecompanion.api;

/**
 * This class contains all URLs
 * 
 * @author Christophe Beyls
 * 
 */
public class ConferenceCompanionUrls {

	private static final String SCHEDULE_URL = "http://10.13.37.2:8000/schedule.xml"; // TODO: replace with public URL

	public static String getSchedule() {
		return SCHEDULE_URL;
	}
	
}
