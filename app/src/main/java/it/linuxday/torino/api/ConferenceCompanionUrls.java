/*
 * Original source code from FOSDEM Companion app by Christophe Beyls.
 * Modified by Ludovico Pavesi.
 * Licensed under the Apache License, version 2.0.
 */

package it.linuxday.torino.api;

/**
 * This class contains all URLs
 * 
 * @author Christophe Beyls
 * 
 */
public class ConferenceCompanionUrls {

	private static final String SCHEDULE_URL = "http://linuxdaytorino.org/xml"; // TODO: get from preferences

	public static String getSchedule() {
		return SCHEDULE_URL;
	}

}
