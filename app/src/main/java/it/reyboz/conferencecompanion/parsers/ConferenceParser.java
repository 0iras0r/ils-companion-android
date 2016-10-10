package it.reyboz.conferencecompanion.parsers;

import org.xmlpull.v1.XmlPullParser;

import it.reyboz.conferencecompanion.model.Conference;

public class ConferenceParser extends AbstractPullParser<Conference> {

    @Override
    protected Conference parseForReal(XmlPullParser parser) throws Exception {
        Conference conference = new Conference();
        while (!isNextEndTag("conference")) {
            if (isStartTag()) {

                switch (parser.getName()) {
                    case "title":
                        conference.setLongName(parser.nextText());
                        break;
                    case "acronym":
                        conference.setAcronym(parser.nextText());
                        break;
                    case "hashtag": // XXX: nonstandard tag!
                        conference.setHashtag(parser.nextText());
                        break;
                    case "events_url": // XXX: nonstandard tag!
                        conference.setEventUrlFormat(parser.nextText());
                        break;
                    case "persons_url": // XXX: nonstandard tag!
                        conference.setPersonUrlFormat(parser.nextText());
                        break;
                    default:
                        skipToEndTag();
                        break;
                }
            }
        }
        return conference;
    }
}
