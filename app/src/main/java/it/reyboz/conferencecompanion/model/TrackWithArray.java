package it.reyboz.conferencecompanion.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collection;
import java.util.PriorityQueue;

public class TrackWithArray extends Track {
    // Actually, it's a queue.
    Collection<String> typesArray = new PriorityQueue<>();

    /**
     * Associate a type with a track.
     * Also checks that the type makes sense (not null, not an empty string, not duplicate).
     *
     * @param type String, null, empty string, anything, nothing
     */
    public void addType(String type) {
        if(type != null && !type.equals("") && !typesArray.contains(type)) {
            typesArray.add(type);
            addToList(type);
        }
    }

    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
}
