/*
 * Original source code from FOSDEM Companion app by Christophe Beyls.
 * Modified by Ludovico Pavesi.
 * Licensed under the Apache License, version 2.0.
 */

package it.reyboz.conferencecompanion.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "conferencecompanion.sqlite";
	private static final int DATABASE_VERSION = 3;

	public static final String EVENTS_TABLE_NAME = "events";
	public static final String EVENTS_TITLES_TABLE_NAME = "events_titles";
	private static final String TEMPORARY_UPGRADE_TABLE_BECAUSE_FTS_DOESNT_SUPPORT_ADD_COLUMN_TABLE_NAME = "upgrade_tmp";
	public static final String PERSONS_TABLE_NAME = "persons";
	public static final String EVENTS_PERSONS_TABLE_NAME = "events_persons";
	public static final String LINKS_TABLE_NAME = "links";
	public static final String TRACKS_TABLE_NAME = "tracks";
	public static final String DAYS_TABLE_NAME = "days";
	public static final String BOOKMARKS_TABLE_NAME = "bookmarks";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// Events
		database.execSQL("CREATE TABLE "
				+ EVENTS_TABLE_NAME
				+ " (id INTEGER PRIMARY KEY, day_index INTEGER NOT NULL, start_time INTEGER, end_time INTEGER, room_name TEXT, slug TEXT, track_id INTEGER, abstract TEXT, description TEXT, type TEXT);");
		database.execSQL("CREATE INDEX event_day_index_idx ON " + EVENTS_TABLE_NAME + " (day_index)");
		database.execSQL("CREATE INDEX event_start_time_idx ON " + EVENTS_TABLE_NAME + " (start_time)");
		database.execSQL("CREATE INDEX event_end_time_idx ON " + EVENTS_TABLE_NAME + " (end_time)");
		database.execSQL("CREATE INDEX event_track_id_idx ON " + EVENTS_TABLE_NAME + " (track_id)");
		// Secondary table with fulltext index on the titles
		database.execSQL("CREATE VIRTUAL TABLE " + EVENTS_TITLES_TABLE_NAME + " USING fts3(title TEXT, subtitle TEXT);");

		// Persons
		// indexing "slug" with FTS is a waste of space, but this seems to be the only sane way to
		// add a column (FTS4 isn't supported for API < 11)
		database.execSQL("CREATE VIRTUAL TABLE " + PERSONS_TABLE_NAME + " USING fts3(name TEXT, slug TEXT);");

		// Events-to-Persons
		database.execSQL("CREATE TABLE " + EVENTS_PERSONS_TABLE_NAME
				+ " (event_id INTEGER NOT NULL, person_id INTEGER NOT NULL, PRIMARY KEY(event_id, person_id));");
		database.execSQL("CREATE INDEX event_person_person_id_idx ON " + EVENTS_PERSONS_TABLE_NAME + " (person_id)");

		// Links
		database.execSQL("CREATE TABLE " + LINKS_TABLE_NAME + " (event_id INTEGER NOT NULL, url TEXT NOT NULL, description TEXT);");
		database.execSQL("CREATE INDEX link_event_id_idx ON " + LINKS_TABLE_NAME + " (event_id)");

		// Tracks
		database.execSQL("CREATE TABLE " + TRACKS_TABLE_NAME + " (id INTEGER PRIMARY KEY, name TEXT NOT NULL, type TEXT NOT NULL);");
		database.execSQL("CREATE UNIQUE INDEX track_main_idx ON " + TRACKS_TABLE_NAME + " (name, type)");

		// Days
		database.execSQL("CREATE TABLE " + DAYS_TABLE_NAME + " (_index INTEGER PRIMARY KEY, date INTEGER NOT NULL);");

		// Bookmarks
		database.execSQL("CREATE TABLE " + BOOKMARKS_TABLE_NAME + " (event_id INTEGER PRIMARY KEY);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		if(oldVersion <= 1) {
			database.execSQL("ALTER TABLE " + PERSONS_TABLE_NAME + " RENAME TO " + TEMPORARY_UPGRADE_TABLE_BECAUSE_FTS_DOESNT_SUPPORT_ADD_COLUMN_TABLE_NAME);
			database.execSQL("CREATE VIRTUAL TABLE " + PERSONS_TABLE_NAME + " USING fts3(name TEXT, slug TEXT);");
			database.execSQL("INSERT INTO " + PERSONS_TABLE_NAME + " (name) SELECT name from " + TEMPORARY_UPGRADE_TABLE_BECAUSE_FTS_DOESNT_SUPPORT_ADD_COLUMN_TABLE_NAME + ";");
			database.execSQL("DROP TABLE " + TEMPORARY_UPGRADE_TABLE_BECAUSE_FTS_DOESNT_SUPPORT_ADD_COLUMN_TABLE_NAME + ";");
		}
		if(oldVersion <= 2) {
			// this will leave the new columns unpopulated, but ultimately it's not important, as
			// database version 2 was never publicly released.
			database.execSQL("ALTER TABLE " + EVENTS_TABLE_NAME + " ADD COLUMN type TEXT");
		}
	}
}
