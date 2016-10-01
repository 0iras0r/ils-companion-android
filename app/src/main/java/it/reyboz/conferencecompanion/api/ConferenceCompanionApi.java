/*
 * Original source code from FOSDEM Companion app by Christophe Beyls.
 * Modified by Ludovico Pavesi.
 * Licensed under the Apache License, version 2.0.
 */

package it.reyboz.conferencecompanion.api;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import it.reyboz.conferencecompanion.db.DatabaseManager;
import it.reyboz.conferencecompanion.model.Event;
import it.reyboz.conferencecompanion.model.Pentabarf;
import it.reyboz.conferencecompanion.parsers.EventsParser;
import it.reyboz.conferencecompanion.parsers.PentabarfParser;
import it.reyboz.conferencecompanion.utils.HttpUtils;

/**
 * Main API entry point.
 * 
 * @author Christophe Beyls
 * 
 */
public class ConferenceCompanionApi {

	// Local broadcasts parameters
	public static final String ACTION_DOWNLOAD_SCHEDULE_PROGRESS = "it.reyboz.conferencecompanion.action.DOWNLOAD_SCHEDULE_PROGRESS";
	public static final String EXTRA_PROGRESS = "PROGRESS";
	public static final String ACTION_DOWNLOAD_SCHEDULE_RESULT = "it.reyboz.conferencecompanion.action.DOWNLOAD_SCHEDULE_RESULT";
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
				// Nothing to parseForReal, the result is up-to-date.
				result = RESULT_UP_TO_DATE;
				return;
			}

			try {
				// TODO: determine whether a miracle happens and this works, or not.
				Pentabarf pentabarf = new PentabarfParser().parse(httpResult.inputStream);
				//Iterable<Event> events = new EventsParser().parseForReal(httpResult.inputStream);
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
