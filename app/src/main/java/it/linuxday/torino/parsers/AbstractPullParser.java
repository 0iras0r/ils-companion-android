/*
 * Original source code from FOSDEM Companion app by Christophe Beyls.
 * Modified by Ludovico Pavesi.
 * Licensed under the Apache License, version 2.0.
 */

package it.linuxday.torino.parsers;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Base class with helper methods for XML pull parsing.
 * 
 * @author Christophe Beyls
 */
public abstract class AbstractPullParser<T> implements Parser<T> {
	private static XmlPullParserFactory factory;

	private static XmlPullParserFactory getFactory() throws XmlPullParserException {
		if (factory == null) {
			factory = XmlPullParserFactory.newInstance();
		}

		return factory;
	}

	private XmlPullParser m_parser;

	/*
	 * Checks if the current event is the end of the document
	 */
	protected boolean isEndDocument() throws XmlPullParserException {
		return (m_parser.getEventType() == XmlPullParser.END_DOCUMENT);
	}

	/*
	 * Checks if the current event is a start tag
	 */
	protected boolean isStartTag() throws XmlPullParserException {
		return (m_parser.getEventType() == XmlPullParser.START_TAG);
	}

	/*
	 * Checks if the current event is a start tag with the specified local name
	 */
	protected boolean isStartTag(String name) throws XmlPullParserException {
		return (m_parser.getEventType() == XmlPullParser.START_TAG) && name.equals(m_parser.getName());
	}

	/*
	 * Go to the next event and check if the current event is an end tag with the specified local name
	 */
	protected boolean isNextEndTag(String name) throws XmlPullParserException, IOException {
		return (m_parser.next() == XmlPullParser.END_TAG) && name.equals(m_parser.getName());
	}

	/*
	 * Skips the start tag and positions the reader on the corresponding end tag
	 */
	protected void skipToEndTag() throws XmlPullParserException, IOException {
		int type;
		while ((type = m_parser.next()) != XmlPullParser.END_TAG) {
			if (type == XmlPullParser.START_TAG)
				skipToEndTag();
		}
	}

	public T parse(InputStream is) throws Exception {
		m_parser = getFactory().newPullParser();
		m_parser.setInput(is, null);

		return parseForReal(m_parser);
	}

	public T parse(XmlPullParser parser) throws Exception {
		m_parser = parser;

		return parseForReal(m_parser);
	}

	/**
	 * If you don't want to raise a NullPointerException just by looking at the code, don't call
	 * this directly, use parse() instead.
	 *
	 * @param parser XmlPullParser which is guaranteed to exist, by now (hopefully)
	 * @return the stuff you've extracted from the xml by the almighty mean of parsing things
	 * @throws Exception I have no idea which exceptions might be raised, but that may happen.
     */
	protected abstract T parseForReal(XmlPullParser parser) throws Exception;
}
