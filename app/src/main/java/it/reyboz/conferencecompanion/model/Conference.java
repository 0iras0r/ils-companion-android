package it.reyboz.conferencecompanion.model;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import it.reyboz.conferencecompanion.utils.StringUtils;

public class Conference {

    private boolean Modified = false; // the "dirty bit", basically.
    private boolean textSizeNeedsRecalculation = true;

    private String EventUrlFormat = "https://linuxdaytorino.org/2016/talk/%1$s"; // TODO: place URL into xml (update Tagliatella and db schema)
    private String PersonUrlFormat = "https://linuxdaytorino.org/2016/user/%1$s";
    private String LongName = null;
    private String ShortName = null;
    private String Hashtag = null;
    private Integer textSize = 11; // leave something as default, here. Don't set to null!
    // TODO: map URL?

    public Conference() {

    }

    public Conference(@NonNull String serialized) {
        deserializeAll(serialized);
    }

    private void deserializeAll(@NonNull String serialized) {
        String[] pieces = serialized.split(",");

        // notice the "breakless" switch
        switch(pieces.length) {
            case 6:
                try {
                    textSize = pieces[5].length() > 0 ? Integer.parseInt(deserialize(pieces[4])) : textSize;
                    textSizeNeedsRecalculation = false;
                } catch(NumberFormatException ignored) {
                    // Leave the default
                }
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
        textSizeNeedsRecalculation = true;
        LongName = longName;
    }

    public String getShortName() {
        return ShortName;
    }

    public void setShortName(@NonNull String shortName) {
        Modified = true;
        textSizeNeedsRecalculation = true;
        ShortName = shortName;

        // Might turn out pointless since the hashtag may be somewhere in the XML, but setting this
        // in a setter makes more sense than in getHashtag()...
        if(getHashtag() == null) {
            setHashtag("#" + shortName.replace(" ", ""));
        }
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
        setShortName(acronym.toUpperCase());
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
        serializeStringInto(serialized, Hashtag) &&
        serializeStringInto(serialized, textSize.toString());

        if(!success) {
            // TODO: do something better.
            throw new RuntimeException("Serialization failed (should never happen)!");
        }

        return serialized.toString();
    }

    private boolean serializeStringInto(ByteArrayOutputStream serialized, @Nullable String s) {
        if(s != null) {
            try {
                serialized.write(Base64.encode(s.getBytes(), Base64.NO_WRAP));
            } catch(IOException e) {
                return false;
            }
        }
        serialized.write(',');
        return true;
    }

    public Boolean getModified() {
        return Modified;
    }

    private void updateTextSize(Typeface typeface, int boxWidth, int boxHeight, int rangeMin, int rangeMax) {
        int result;
        String menuText = getMenuText();

        if(menuText != null) {

            result = StringUtils.textFitsMax(menuText, typeface, boxWidth, boxHeight, rangeMin, rangeMax);
            textSize = result;
        }

        textSizeNeedsRecalculation = false;
    }

    public int getTextSize(Typeface typeface, int boxWidth, int boxHeight, int rangeMin, int rangeMax) {
        if(textSizeNeedsRecalculation) {
            updateTextSize(typeface, boxWidth, boxHeight, rangeMin, rangeMax);
        }

        return textSize;
    }

    /**
     * Exactly what it says on the tin.
     *
     * @return text to be used in main menu (short name or long name), or null
     */
    public String getMenuText() {
        String shortName = getShortName();

        if(shortName == null) {
            return getLongName();
        } else {
            return shortName;
        }
    }

    public void confirmSaved() {
        Modified = false;
    }
}
