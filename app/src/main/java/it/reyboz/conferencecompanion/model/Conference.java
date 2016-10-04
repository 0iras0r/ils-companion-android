package it.reyboz.conferencecompanion.model;

import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Conference {

    private Boolean Modified = false; // the "dirty bit", basically.
    private String EventUrlFormat = "https://linuxdaytorino.org/2016/talk/%1$s/"; // TODO: place URL into xml (update Tagliatella and db schema)
    private String PersonUrlFormat = "https://linuxdaytorino.org/2016/user/%1$s/";
    private String LongName = null;
    private String ShortName = null;
    private String Hashtag = null;
    // TODO: map URL?

    public Conference() {

    }

    public Conference(@NonNull String serialized) {
        deserializeAll(serialized);
    }

    private void deserializeAll(@NonNull String serialized) {
        String[] pieces = serialized.split(",");
        switch(pieces.length) {
            case 5:
                Hashtag = pieces[4].length() > 0 ? deserialize(pieces[4]) : Hashtag;
            case 4:
                ShortName = pieces[3].length() > 0 ? deserialize(pieces[3]) : ShortName;
            case 3:
                LongName = pieces[2].length() > 0 ? deserialize(pieces[2]) : LongName;
            case 2:
                PersonUrlFormat = pieces[1].length() > 0 ? deserialize(pieces[1]) : PersonUrlFormat;
            case 1:
                EventUrlFormat = pieces[0].length() > 0 ? deserialize(pieces[0]) : EventUrlFormat;
        }
    }

    private String deserialize(String base64) {
        return new String(Base64.decode(base64, Base64.DEFAULT));
    }

    public String getEventUrlFormat() {
        return EventUrlFormat;
    }

    public void setEventUrlFormat(String format) {
        Modified = true;
        EventUrlFormat = format;
    }

    public String getPersonUrlFormat() {
        return PersonUrlFormat;
    }

    public void setPersonUrlFormat(String format) {
        Modified = true;
        PersonUrlFormat = format;
    }

    public String getLongName() {
        return LongName;
    }

    public void setLongName(String longName) {
        Modified = true;
        LongName = longName;
    }

    public String getShortName() {
        return ShortName;
    }

    public void setShortName(String shortName) {
        Modified = true;
        ShortName = shortName;
    }

    /**
     * This probably violates every single best practice in existence, since this is actually a
     * setter for ShortName.
     *
     * frab (and possibly the Pentabarf software itself, which seems to have disappeared from the
     * internet) suggests to set a lowercase acronym because it's used in URLs (IIRC), but uppercase
     * looks nicer where we're using it, so we apply a toUpperCase().
     *
     * @param acronym the acronym field from the "Pentabarf" XML file
     */
    public void setAcronym(String acronym) {
        Modified = true;
        ShortName = acronym.toUpperCase();
    }

    public String getHashtag() {
        return Hashtag;
    }

    public void setHashtag(String hashtag) {
        Modified = true;
        Hashtag = hashtag;
    }

    public @NonNull String serialize() {
        ByteArrayOutputStream serialized = new ByteArrayOutputStream();

        boolean success =
        serializeStringInto(serialized, EventUrlFormat) &&
        serializeStringInto(serialized, PersonUrlFormat) &&
        serializeStringInto(serialized, LongName) &&
        serializeStringInto(serialized, ShortName) &&
        serializeStringInto(serialized, Hashtag);

        if(!success) {
            // TODO: do something better.
            throw new RuntimeException("Serialization failed (should never happen)!");
        }

        return serialized.toString();
    }

    private boolean serializeStringInto(ByteArrayOutputStream serialized, @NonNull String s) {
        try {
            serialized.write(Base64.encode(s.getBytes(), Base64.NO_WRAP));
        } catch(IOException e) {
            return false;
        }
        serialized.write(',');
        return true;
    }

    public Boolean getModified() {
        return Modified;
    }

    public void confirmSaved() {
        Modified = false;
    }
}
