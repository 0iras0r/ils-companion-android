/*
 * Original source code from FOSDEM Companion app by Christophe Beyls.
 * Modified by Ludovico Pavesi.
 * Licensed under the Apache License, version 2.0.
 */

package it.reyboz.conferencecompanion.parsers;

import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it.reyboz.conferencecompanion.model.Day;
import it.reyboz.conferencecompanion.model.Event;
import it.reyboz.conferencecompanion.model.Link;
import it.reyboz.conferencecompanion.model.Person;
import it.reyboz.conferencecompanion.model.Track;
import it.reyboz.conferencecompanion.utils.DateUtils;

/**
 * Main parser for FOSDEM schedule data in pentabarf XML format.
 *
 * @author Christophe Beyls
 */
public class EventsParser extends AbstractPullParser<ArrayList<Event>> {

	private final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	// Calendar used to compute the events time
	private final Calendar calendar = Calendar.getInstance(DateUtils.getUTCTimeZone(), Locale.US);

	private Day currentDay;
	private String currentRoom;
	private Track currentTrack;

	/**
	 * Returns the hours portion of a time string in the "hh:mm" format, without allocating objects.
	 *
	 * @param time string in the "hh:mm" format
	 * @return hours
	 */
	private static int getHours(String time) {
		return (Character.getNumericValue(time.charAt(0)) * 10) + Character.getNumericValue(time.charAt(1));
	}

	/**
	 * Returns the minutes portion of a time string in the "hh:mm" format, without allocating objects.
	 *
	 * @param time string in the "hh:mm" format
	 * @return minutes
	 */
	private static int getMinutes(String time) {
		return (Character.getNumericValue(time.charAt(3)) * 10) + Character.getNumericValue(time.charAt(4));
	}

	@Override
	protected ArrayList<Event> parseForReal(XmlPullParser parser) throws Exception {
		ArrayList<Event> events = new ArrayList<>();

		// parseForReal() is called when a start tag for <day> has been found
		currentDay = new Day();
		currentDay.setIndex(Integer.parseInt(parser.getAttributeValue(null, "index")));
		currentDay.setDate(DATE_FORMAT.parse(parser.getAttributeValue(null, "date")));

		while (!isNextEndTag("day")) {
			if (isStartTag()) {

				switch (parser.getName()) {
					case "room":
						currentRoom = parser.getAttributeValue(null, "name");
						break;
					case "event":
						Event event = new Event();
						event.setId(Long.parseLong(parser.getAttributeValue(null, "id")));
						event.setDay(currentDay);
						event.setRoomName(currentRoom);
						// Initialize empty lists
						List<Person> persons = new ArrayList<>();
						event.setPersons(persons);
						List<Link> links = new ArrayList<>();
						event.setLinks(links);

						String duration = null;
						String trackName = "";
						Track.Type trackType = Track.Type.other;

						while (!isNextEndTag("event")) {
							if (isStartTag()) {

								switch (parser.getName()) {
									// TODO: handle standard format with time zone from frab
									case "start":
										String time = parser.nextText();
										if (!TextUtils.isEmpty(time)) {
											calendar.setTime(currentDay.getDate());
											calendar.set(Calendar.HOUR_OF_DAY, getHours(time));
											calendar.set(Calendar.MINUTE, getMinutes(time));
											event.setStartTime(calendar.getTime());
										}
										break;
									case "duration":
										duration = parser.nextText();
										break;
									case "slug":
										event.setSlug(parser.nextText());
										break;
									case "title":
										event.setTitle(parser.nextText());
										break;
									case "subtitle":
										event.setSubTitle(parser.nextText());
										break;
									case "track":
										trackName = parser.nextText();
										break;
									case "type":
										try {
											trackType = Enum.valueOf(Track.Type.class, parser.nextText());
										} catch (Exception e) {
											// trackType will be "other"
										}
										break;
									case "abstract":
										event.setAbstractText(parser.nextText());
										break;
									case "description":
										event.setDescription(parser.nextText());
										break;
									case "persons":
										while (!isNextEndTag("persons")) {
											if (isStartTag("person")) {
												Person person = new Person();
												person.setId(Long.parseLong(parser.getAttributeValue(null, "id")));
												person.setSlug(parser.getAttributeValue(null, "slug"));
												person.setName(parser.nextText());

												persons.add(person);
											}
										}
										break;
									case "links":
										while (!isNextEndTag("links")) {
											if (isStartTag("link")) {
												Link link = new Link();
												link.setUrl(parser.getAttributeValue(null, "href"));
												link.setDescription(parser.nextText());

												links.add(link);
											}
										}
										break;
									default:
										skipToEndTag();
										break;
								}
							}
						}

						if ((event.getStartTime() != null) && !TextUtils.isEmpty(duration)) {
							calendar.add(Calendar.HOUR_OF_DAY, getHours(duration));
							calendar.add(Calendar.MINUTE, getMinutes(duration));
							event.setEndTime(calendar.getTime());
						}

						if ((currentTrack == null) || !trackName.equals(currentTrack.getName()) || (trackType != currentTrack.getType())) {
							currentTrack = new Track(trackName, trackType);
						}
						event.setTrack(currentTrack);

						events.add(event);
					default:
						skipToEndTag();
						break;
				}
			}
		}

		return events;
	}
}
