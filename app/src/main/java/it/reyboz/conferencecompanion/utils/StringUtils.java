/*
 * Original source code from FOSDEM Companion app by Christophe Beyls.
 * Modified by Ludovico Pavesi.
 * Licensed under the Apache License, version 2.0.
 */

package it.reyboz.conferencecompanion.utils;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.Locale;

/**
 * Various methods to transform strings
 * 
 * @author Christophe Beyls
 */
public class StringUtils {
	/**
	 * Mirror of the unicode table from 00c0 to 017f without diacritics.
	 */
	private static final String tab00c0 = "AAAAAAACEEEEIIII" + "DNOOOOO\u00d7\u00d8UUUUYI\u00df" + "aaaaaaaceeeeiiii" + "\u00f0nooooo\u00f7\u00f8uuuuy\u00fey"
			+ "AaAaAaCcCcCcCcDd" + "DdEeEeEeEeEeGgGg" + "GgGgHhHhIiIiIiIi" + "IiJjJjKkkLlLlLlL" + "lLlNnNnNnnNnOoOo" + "OoOoRrRrRrSsSsSs" + "SsTtTtTtUuUuUuUu"
			+ "UuUuWwYyYZzZzZzF";

	//private static final String ROOM_DRAWABLE_PREFIX = "room_";

	/**
	 * Returns string without diacritics - 7 bit approximation.
	 * 
	 * @param source
	 *            string to convert
	 * @return corresponding string without diacritics
	 */
	public static String removeDiacritics(String source) {
		final int length = source.length();
		char[] result = new char[length];
		char c;
		for (int i = 0; i < length; i++) {
			c = source.charAt(i);
			if (c >= '\u00c0' && c <= '\u017f') {
				c = tab00c0.charAt((int) c - '\u00c0');
			}
			result[i] = c;
		}
		return new String(result);
	}

	/**
	 * Replaces all groups of non-alphanumeric chars in source with a single replacement char.
	 */
	private static String replaceNonAlphaGroups(String source, char replacement) {
		final int length = source.length();
		char[] result = new char[length];
		char c;
		boolean replaced = false;
		int size = 0;
		for (int i = 0; i < length; i++) {
			c = source.charAt(i);
			if (Character.isLetterOrDigit(c)) {
				result[size++] = c;
				replaced = false;
			} else {
				// Skip quote
				if ((c != '’') && !replaced) {
					result[size++] = replacement;
					replaced = true;
				}
			}
		}
		return new String(result, 0, size);
	}

	/**
	 * Removes all non-alphanumeric chars at the beginning and end of source.
	 */
	private static String trimNonAlpha(String source) {
		int st = 0;
		int len = source.length();

		while ((st < len) && !Character.isLetterOrDigit(source.charAt(st))) {
			st++;
		}
		while ((st < len) && !Character.isLetterOrDigit(source.charAt(len - 1))) {
			len--;
		}
		return ((st > 0) || (len < source.length())) ? source.substring(st, len) : source;
	}

	/**
	 * Transforms a name to a slug identifier to be used in a URL.
	 */
	public static String toSlug(String source) {
		return replaceNonAlphaGroups(trimNonAlpha(removeDiacritics(source)), '_').toLowerCase(Locale.US);
	}

	public static CharSequence trimEnd(CharSequence source) {
		int pos = source.length() - 1;
		while ((pos >= 0) && Character.isWhitespace(source.charAt(pos))) {
			pos--;
		}
		pos++;
		return (pos < source.length()) ? source.subSequence(0, pos) : source;
	}

//	/**
//	 * Converts a room name to a local drawable resource name, by stripping non-alpha chars and converting to lower case. Any letter following a digit will be
//	 * ignored, along with the rest of the string.
//	 *
//	 * @return
//	 */
//	public static String roomNameToResourceName(String roomName) {
//		StringBuilder builder = new StringBuilder(ROOM_DRAWABLE_PREFIX.length() + roomName.length());
//		builder.append(ROOM_DRAWABLE_PREFIX);
//		int size = roomName.length();
//		boolean lastDigit = false;
//		for (int i = 0; i < size; ++i) {
//			char c = roomName.charAt(i);
//			if (Character.isLetter(c)) {
//				if (lastDigit) {
//					break;
//				}
//				builder.append(Character.toLowerCase(c));
//			} else if (Character.isDigit(c)) {
//				builder.append(c);
//				lastDigit = true;
//			}
//		}
//		return builder.toString();
//	}

	/**
	 * Calculate the displayed text height\width in some unknown unit (dp, maybe?) and check if it
	 * fits inside a bounding box.
	 * https://stackoverflow.com/a/26975371
	 */
	public static boolean textFits(String text, int fontSize, Typeface typeface, int maxWidth, int maxHeight) {
		Paint paint = new Paint();
		Rect rectangle = new Rect();

		paint.setTypeface(typeface);
		paint.setTextSize(fontSize);

		paint.getTextBounds(text, 0, text.length(), rectangle);

		return rectangle.width() <= maxWidth && rectangle.height() <= maxHeight;
	}

	/**
	 * Try to fit text inside a bounding box. Complexity should be O(log n), although even O(n^2)
	 * would have been negligible in the real world.
	 *
	 * @see #textFits
	 * @param maxWidth width of the bounding box (in dp?)
	 * @param maxHeight height of the bounding box (in dp?)
	 * @param rangeMin lowest width to try (in whatever unit font sizes are specified)
     * @param rangeMax highest width to try (in whatever unit font sizes are specified)
     * @return maximum text width that fits, or rangeMin if none fits.
     */
	public static int textFitsMax(String text, Typeface typeface, int maxWidth, int maxHeight, int rangeMin, int rangeMax) {
		int dist = rangeMax - rangeMin;
		// rangeMin is assumed to always fit
		if(dist <= 0) {
			return rangeMin;
		} else if(dist == 1) {
			if(textFits(text, rangeMax, typeface, maxWidth, maxHeight)) {
				return rangeMax;
			} else {
				return rangeMin;
			}
		}

		int half = (rangeMin + rangeMax) / 2;

		// Yay recursion!
		if(textFits(text, half, typeface, maxWidth, maxHeight)) {
			return textFitsMax(text, typeface, maxWidth, maxHeight, half, rangeMax);
		} else {
			return textFitsMax(text, typeface, maxWidth, maxHeight, rangeMin, half-1);
		}
	}
}
