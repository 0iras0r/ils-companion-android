/*
 * Original source code from FOSDEM Companion app by Christophe Beyls.
 * Modified by Ludovico Pavesi.
 * Licensed under the Apache License, version 2.0.
 */

package it.linuxday.torino.api;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import it.linuxday.torino.db.DatabaseManager;
import it.linuxday.torino.model.Event;
import it.linuxday.torino.model.Pentabarf;
import it.linuxday.torino.parsers.EventsParser;
import it.linuxday.torino.parsers.PentabarfParser;
import it.linuxday.torino.utils.HttpUtils;

/**
 * Main API entry point.
 * 
 * @author Christophe Beyls
 * 
 */
public class ConferenceCompanionApi {

	// Local broadcasts parameters
	public static final String ACTION_DOWNLOAD_SCHEDULE_PROGRESS = "it.linuxday.torino.action.DOWNLOAD_SCHEDULE_PROGRESS";
	public static final String EXTRA_PROGRESS = "PROGRESS";
	public static final String ACTION_DOWNLOAD_SCHEDULE_RESULT = "it.linuxday.torino.action.DOWNLOAD_SCHEDULE_RESULT";
	public static final String EXTRA_RESULT = "RESULT";

	public static final int RESULT_ERROR = -1;
	public static final int RESULT_UP_TO_DATE = -2;

	private static final Lock scheduleLock = new ReentrantLock();

	/**
	 * Download & store the schedule to the database. Only one thread at a time will perform the actual action, the other ones will return immediately. The
	 * result will be sent back in the form of a local broadcast with an ACTION_DOWNLOAD_SCHEDULE_RESULT action.
	 * 
	 */
	public static void downloadSchedule(Context context) {
		if (!scheduleLock.tryLock()) {
			// If a download is already in progress, return immediately
			return;
		}

		int result = RESULT_ERROR;
		try {
			DatabaseManager dbManager = DatabaseManager.getInstance();
			HttpUtils.HttpResult httpResult = HttpUtils.get(
					context,
					ConferenceCompanionUrls.getSchedule(),
					dbManager.getLastModifiedTag(),
					ACTION_DOWNLOAD_SCHEDULE_PROGRESS,
					EXTRA_PROGRESS);
			if (httpResult.inputStream == null) {
				// Nothing to parse, the result is up-to-date.
				result = RESULT_UP_TO_DATE;
				return;
			}

			try {
				Pentabarf pentabarf = new PentabarfParser().parse(httpResult.inputStream);
				dbManager.storeConference(pentabarf.conference);
				result = dbManager.storeSchedule(pentabarf.events, httpResult.lastModified);
			} finally {
				try {
					httpResult.inputStream.close();
				} catch (Exception ignored) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_DOWNLOAD_SCHEDULE_RESULT).putExtra(EXTRA_RESULT, result));
			scheduleLock.unlock();
		}
	}
}
