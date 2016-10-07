/*
 * Original source code from FOSDEM Companion app by Christophe Beyls.
 * Modified by Ludovico Pavesi.
 * Licensed under the Apache License, version 2.0.
 */

package it.reyboz.conferencecompanion.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import it.reyboz.conferencecompanion.R;
import it.reyboz.conferencecompanion.utils.DateUtils;

public class Day implements Parcelable {

	private int index;
	private Date date;

	public Day() {
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getShortName() {
		return DateUtils.getDayName(date);
	}

	@Override
	public String toString() {
		// There are adapter-fragment-loader-list-view things that absolutely need to call this, or
		// a similar method, from a static contest (= forget using getString(), and getting a
		// Context in there apparently causes a billion memory leaks, BECAUSE ANDROID)
		return getShortName();
	}

	@Override
	public int hashCode() {
		return index;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		Day other = (Day) obj;
		return (index == other.index);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(index);
		out.writeLong((date == null) ? 0L : date.getTime());
	}

	public static final Parcelable.Creator<Day> CREATOR = new Parcelable.Creator<Day>() {
		public Day createFromParcel(Parcel in) {
			return new Day(in);
		}

		public Day[] newArray(int size) {
			return new Day[size];
		}
	};

	Day(Parcel in) {
		index = in.readInt();
		long time = in.readLong();
		if (time != 0L) {
			date = new Date(time);
		}
	}
}
