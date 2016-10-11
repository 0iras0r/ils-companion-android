/*
 * Original source code from FOSDEM Companion app by Christophe Beyls.
 * Modified by Ludovico Pavesi.
 * Licensed under the Apache License, version 2.0.
 */

package it.linuxday.torino.receivers;

import it.linuxday.torino.alarms.ConferenceCompanionAlarmManager;
import it.linuxday.torino.services.AlarmIntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Entry point for system-generated events: boot complete and alarms.
 * 
 * @author Christophe Beyls
 * 
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

	public static final String ACTION_NOTIFY_EVENT = "it.linuxday.torino.action.NOTIFY_EVENT";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (ACTION_NOTIFY_EVENT.equals(action)) {

			// Forward the intent to the AlarmIntentService for background processing of the notification
			Intent serviceIntent = new Intent(context, AlarmIntentService.class);
			serviceIntent.setAction(ACTION_NOTIFY_EVENT);
			serviceIntent.setData(intent.getData());
			startWakefulService(context, serviceIntent);

		} else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {

			if (ConferenceCompanionAlarmManager.getInstance().isEnabled()) {
				Intent serviceIntent = new Intent(context, AlarmIntentService.class);
				serviceIntent.setAction(AlarmIntentService.ACTION_UPDATE_ALARMS);
				serviceIntent.putExtra(AlarmIntentService.EXTRA_WITH_WAKE_LOCK, true);
				startWakefulService(context, serviceIntent);
			}
		}
	}

}
