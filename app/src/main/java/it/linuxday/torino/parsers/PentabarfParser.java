package it.linuxday.torino.parsers;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

import it.linuxday.torino.model.Event;
import it.linuxday.torino.model.Pentabarf;

/**
 * EventsParser returns an array of Event(s), ConferenceParser a Conference, and... PentabarfParser
 * returns a Pentabarf, BECAUSE JAVA. THAT'S WHY.
 */
public class PentabarfParser extends AbstractPullParser<Pentabarf> {

    @Override
    protected Pentabarf parseForReal(XmlPullParser parser) throws Exception {
        Pentabarf wrapper = new Pentabarf();
        ArrayList<Event> EventsFromADay;

        while (!isEndDocument()) {
            if(isStartTag("day")) {
                EventsFromADay = new EventsParser().parse(parser);
                if(wrapper.events == null) {
                    wrapper.events = EventsFromADay;
                } else {
                    wrapper.events.addAll(EventsFromADay);
                }
            } else if(isStartTag("conference")) {
                wrapper.conference = new ConferenceParser().parse(parser);
            }
            parser.next();
        }
        return wrapper;
    }
}
