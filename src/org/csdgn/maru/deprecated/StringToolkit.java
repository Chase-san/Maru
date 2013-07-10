/**
 * Copyright (c) 2011-2013 Robert Maupin
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *    1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 
 *    2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 
 *    3. This notice may not be removed or altered from any source
 *    distribution.
 */
package org.csdgn.maru.deprecated;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This package may be removed without warning!
 * 
 * @author Robert Maupin
 */
@Deprecated
public class StringToolkit {
	/**
	 * Whatever style the current system uses.
	 */
	public static final int EOL_SYSTEM = 0;
	/**
	 * Windows/DOS Style.
	 */
	public static final int EOL_WIN = 1;
	/**
	 * Linux/Unix Style.
	 */
	public static final int EOL_NIX = 2;
	/**
	 * Mac Style.
	 */
	public static final int EOL_MAC = 3;

	/**
	 * Changes the EOL of a certain bit of text, even if the endings differ line
	 * to line.
	 * 
	 * @param src
	 *            Source string
	 * @param style
	 *            style to change them to
	 * @return source text with end of lines converted
	 */
	public static final String convertEOL(final String src, final int style) {
		String lineEnding = "";
		switch(style) {
		case EOL_SYSTEM:
			lineEnding = System.getProperty("line.separator");
			break;
		case EOL_WIN:
			lineEnding = "\r\n";
			break;
		case EOL_MAC:
			lineEnding = "\r";
			break;
		case EOL_NIX:
			lineEnding = "\n";
			break;
		default:
			throw new IllegalArgumentException("Unknown line break style: " + style);
		}
		final StringBuilder sb = new StringBuilder();
		final Iterator<Character> it = stringIterator(src);
		while(it.hasNext()) {
			char c = it.next();
			if(c == '\r') {
				sb.append(lineEnding);
				if(it.hasNext() && (c = it.next()) != '\n') sb.append(c);
				continue;
			} else if(c == '\n') {
				sb.append(lineEnding);
				continue;
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	/**
	 * Split CSV data values. (Non-Regex)
	 */
	public static final String[] splitCSV(final String src) {
		Iterator<Character> it = stringIterator(src);
		final ArrayList<String> output = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		boolean qoute = false;
		char last = '\0';
		while(it.hasNext()) {
			char c = it.next();
			if(c == '\r' || c == '\n') { break; }
			if(c == ',' && !qoute) {
				output.add(sb.toString());
				sb.setLength(0);
			} else if(c == '"') {
				//check if double
				if(last == '"' && !qoute) {
					sb.append('"');
				}
				qoute = !qoute;
			} else {
				sb.append(c);
			}
			last = c;
		}
		if(sb.length() > 0) {
			output.add(sb.toString());
		}
		return output.toArray(new String[output.size()]);
	}

	/**
	 * Fast Split. Preserves null sections. "ab..cd" would produce [ab][][cd]
	 * with '.'
	 */
	public static final String[] fastSplit(final String src, final char delim) {
		final ArrayList<String> output = new ArrayList<String>();
		int index = 0;
		int lindex = 0;
		while((index = src.indexOf(delim, lindex)) != -1) {
			output.add(src.substring(lindex, index));
			lindex = index + 1;
		}
		output.add(src.substring(lindex));
		return output.toArray(new String[output.size()]);
	}

	/**
	 * Fast Split. Preserves null sections. "ab..cd" would produce [ab][][cd]
	 * with "."
	 */
	public static final String[] fastSplit(final String src, final String delim) {
		final ArrayList<String> output = new ArrayList<String>();
		final int len = delim.length();
		int index = 0;
		int lindex = 0;
		while((index = src.indexOf(delim, lindex)) != -1) {
			output.add(src.substring(lindex, index));
			lindex = index + len;
		}
		output.add(src.substring(lindex));
		return output.toArray(new String[output.size()]);
	}

	/**
	 * Splits the source on the first occurrence of the delimiter
	 */
	public static final String[] fastSplit1(final String src, final char delim) {
		final int index = src.indexOf(delim);
		return new String[]
			{ src.substring(0, index), src.substring(index + 1) };
	}

	/**
	 * Splits the source on the first occurrence of the delimiter
	 */
	public static final String[] fastSplit1(final String src, final String delim) {
		final int len = delim.length();
		final int index = src.indexOf(delim);
		return new String[]
			{ src.substring(0, index), src.substring(index + len) };
	}

	/**
	 * Splits the source on the first and second occurrence of the delimiter
	 */
	public static final String[] fastSplit2(final String src, final char delim) {
		final int index1 = src.indexOf(delim);
		final int index2 = src.indexOf(delim, index1 + 1);
		return new String[]
			{ src.substring(0, index1), src.substring(index1 + 1, index2), src.substring(index2 + 1) };
	}

	/**
	 * Splits the source on the first and second occurrence of the delimiter
	 */
	public static final String[] fastSplit2(final String src, final String delim) {
		final int len = delim.length();
		final int index1 = src.indexOf(delim);
		final int index2 = src.indexOf(delim, index1 + len);
		return new String[]
			{ src.substring(0, index1), src.substring(index1 + len, index2), src.substring(index2 + len) };
	}

	/**
	 * Fastest Multi Fast Split. "ab..cd" would produce [ab][][cd] with '.' and
	 * 2. It would produce [ab][.cd] with 1.
	 */
	public static final String[] fastSplitN(final String src, final char delim, final int n) {
		if(n < 1) return new String[]
			{ src };
		final String[] output = new String[n + 1];
		int index = 0;
		int lindex = 0;
		for(int i = 0; i < n && index != 0; ++i) {
			index = src.indexOf(delim, lindex);
			output[i] = src.substring(lindex, index);
			lindex = index + 1;
		}
		output[output.length - 1] = src.substring(lindex);
		return output;
	}

	/**
	 * Fastest Multi Fast Split. "ab..cd" would produce [ab][][cd] with '.' and
	 * 2. It would produce [ab][.cd] with 1.
	 */
	public static final String[] fastSplitN(final String src, final String delim, final int n) {
		if(n < 1) return new String[]
			{ src };
		final String[] output = new String[n + 1];
		final int len = delim.length();
		int index = 0;
		int lindex = 0;
		for(int i = 0; i < n; ++i) {
			index = src.indexOf(delim, lindex);
			output[i] = src.substring(lindex, index);
			lindex = index + len;
		}
		output[output.length - 1] = src.substring(lindex);
		return output;
	}

	/**
	 * Smart Fast Split. Removes null sections (length 0). "ab..cd" would
	 * produce [ab][cd] with '.'
	 */
	public static final String[] smartFastSplit(final String src, final char delim) {
		final ArrayList<String> output = new ArrayList<String>();
		String tmp = "";
		int index = 0;
		int lindex = 0;
		while((index = src.indexOf(delim, lindex)) != -1) {
			tmp = src.substring(lindex, index);
			if(tmp.length() > 0) output.add(tmp);
			lindex = index + 1;
		}
		tmp = src.substring(lindex);
		if(tmp.length() > 0) output.add(tmp);
		return output.toArray(new String[output.size()]);
	}

	/**
	 * Smart Fast Split. Removes null sections (length 0). "ab..cd" would
	 * produce [ab][cd] with "."
	 */
	public static final String[] smartFastSplit(final String src, final String delim) {
		final ArrayList<String> output = new ArrayList<String>();
		String tmp = "";
		final int len = delim.length();
		int index = 0;
		int lindex = 0;
		while((index = src.indexOf(delim, lindex)) != -1) {
			tmp = src.substring(lindex, index);
			if(tmp.length() > 0) output.add(tmp);
			lindex = index + len;
		}
		tmp = src.substring(lindex);
		if(tmp.length() > 0) output.add(tmp);
		return output.toArray(new String[output.size()]);
	}

	public static final Iterator<Character> stringIterator(final String string) {
		// Ensure the error is found as soon as possible.
		if(string == null) throw new NullPointerException();
		return new Iterator<Character>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < string.length();
			}

			@Override
			public Character next() {
				/*
				 * Throw NoSuchElementException as defined by the Iterator
				 * contract, not IndexOutOfBoundsException.
				 */
				if(!hasNext()) throw new NoSuchElementException();
				return string.charAt(index++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public static final String unescape(String str) {
		StringBuilder sb = new StringBuilder();
		boolean wasEscape = false;
		for(char c : str.toCharArray()) {
			if(wasEscape) {
				switch(c) {
				case '0':
					sb.append('\0');
					break;
				case 'b':
					sb.append('\b');
					break;
				case 'f':
					sb.append('\f');
					break;
				case 't':
					sb.append('\t');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 'n':
					sb.append('\n');
					break;
				case '\\':
					sb.append('\\');
					break;
				case '\'':
					sb.append('\'');
					break;
				case '"':
					sb.append('"');
					break;
				default:
					sb.append('\\');
					sb.append(c);
				}
				wasEscape = false;
			} else if(c == '\\') {
				wasEscape = true;
			} else {
				sb.append(c);
			}
		}
		if(wasEscape)
			sb.append('\\');
		return sb.toString();
	}

	private StringToolkit() {
	}
}
