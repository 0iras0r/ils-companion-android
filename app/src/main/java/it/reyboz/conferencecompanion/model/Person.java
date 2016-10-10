/*
 * Original source code from FOSDEM Companion app by Christophe Beyls.
 * Modified by Ludovico Pavesi.
 * Licensed under the Apache License, version 2.0.
 */

package it.reyboz.conferencecompanion.model;

import it.reyboz.conferencecompanion.utils.StringUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Locale;

public class Person implements Parcelable {

	private long id;
	private String name;
	private String slug = null;

	public Person() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl(Conference conference) {
		String urlFormat = conference.getPersonUrlFormat();
		String slug = getSlug();

		if(urlFormat == null || slug == null) {
			return null;
		}

		return String.format(Locale.US, conference.getPersonUrlFormat(), getSlug());
	}

	public String getSlug() {
		if(slug == null) {
			return StringUtils.toSlug(name);
		} else {
			return slug;
		}
	}

	public void setSlug(@Nullable String slug) {
		this.slug = slug;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		return (id == other.id);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(id);
		out.writeString(name);
		if(slug == null) {
			out.writeString("");
		} else {
			out.writeString(slug);
		}
	}

	public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
		public Person createFromParcel(Parcel in) {
			return new Person(in);
		}

		public Person[] newArray(int size) {
			return new Person[size];
		}
	};

	Person(Parcel in) {
		String slugOrEmpty;

		id = in.readLong();
		name = in.readString();
		slugOrEmpty = in.readString();
		if(!slugOrEmpty.equals("")) {
			slug = slugOrEmpty;
		}
	}
}
