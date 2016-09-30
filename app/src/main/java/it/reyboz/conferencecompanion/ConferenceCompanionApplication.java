/*
 * Original source code from FOSDEM Companion app by Christophe Beyls.
 * Modified by Ludovico Pavesi.
 * Licensed under the Apache License, version 2.0.
 */

package it.reyboz.conferencecompanion;

import android.app.Application;
import android.preference.PreferenceManager;
import it.reyboz.conferencecompanion.alarms.ConferenceCompanionAlarmManager;
import it.reyboz.conferencecompanion.db.DatabaseManager;

public class ConferenceCompanionApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		DatabaseManager.init(this);
		// Initialize settings
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		// Alarms (requires settings)
		ConferenceCompanionAlarmManager.init(this);
	}
}
