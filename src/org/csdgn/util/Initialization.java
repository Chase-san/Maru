package org.csdgn.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A class for windows like configuration (.ini) files.
 * 
 * TODO Add Write Method
 * 
 * @author Chase
 */
public class Initialization implements Map<String, String> {
	private static final int IO_BUFFER_SIZE = 8192;
	private final HashMap<String, HashMap<String, String>> data;
	private HashMap<String, String> current;

	public Initialization() {
		data = new HashMap<String, HashMap<String, String>>();
		// global section
		current = new HashMap<String, String>();
		data.put(null, current);
	}

	/**
	 * Clears all keys from the current section.
	 */
	@Override
	public void clear() {
		current.clear();
	}

	/**
	 * Determines if the current section contains the given key.
	 */
	@Override
	public boolean containsKey(final Object key) {
		return current.containsKey(key);
	}

	/**
	 * Returns true if this configuration contains a mapping for the specified
	 * section. More formally, returns true if and only if this configuration
	 * contains a mapping for a section <code>s</code> such that (
	 * <code>section==null ? s==null : section.equals(s)</code>). (There can be
	 * at most one such mapping.)
	 * 
	 * @param section
	 *            section whose presence in this configuration is to be tested
	 * @return <code>true</code> if this configuration contains a mapping for
	 *         the specified section
	 */
	public boolean containsSection(final Object section) {
		return data.containsKey(section);
	}

	/**
	 * Determines if the current section contains the given value.
	 */
	@Override
	public boolean containsValue(final Object value) {
		return current.containsValue(value);
	}

	/**
	 * Returns a entry set for the current section.
	 */
	@Override
	public Set<Map.Entry<String, String>> entrySet() {
		return null;
	}

	/**
	 * Gets the value of the given key from the current section.
	 */
	@Override
	public String get(final Object key) {
		return current.get(key);
	}

	/**
	 * Gets the value of the given key from the current section. Returns the
	 * default value if the section does not contain the given key.
	 */
	public String get(final Object key, final String defaultValue) {
		if(current.containsKey(key)) return current.get(key);
		return defaultValue;
	}

	/**
	 * Gets the value of the given key from the current section. Returns null if
	 * the given key does not exist, or if the value is not a valid double.
	 */
	public Double getDouble(final Object key) {
		if(current.containsKey(key)) try {
			return Double.parseDouble(current.get(key));
		}
		catch(final NumberFormatException e) {
		}
		return null;
	}

	/**
	 * Gets the value of the given key from the current section. Returns the
	 * default value if the section does not contain the given key, or the given
	 * value is not a valid double.
	 */
	public double getDouble(final Object key, final double defaultValue) {
		if(current.containsKey(key)) try {
			return Double.parseDouble(current.get(key));
		}
		catch(final NumberFormatException e) {
			e.printStackTrace();
		}
		return defaultValue;
	}

	/**
	 * Gets the value of the given key from the current section. Returns null if
	 * the given key does not exist, or if the value is not a valid integer.
	 */
	public Integer getInteger(final Object key) {
		if(current.containsKey(key)) try {
			return Integer.parseInt(current.get(key));
		}
		catch(final NumberFormatException e) {
		}
		return null;
	}

	/**
	 * Gets the value of the given key from the current section. Returns the
	 * default value if the section does not contain the given key, or the given
	 * value is not a valid integer.
	 */
	public int getInteger(final Object key, final int defaultValue) {
		if(current.containsKey(key)) try {
			return Integer.parseInt(current.get(key));
		}
		catch(final NumberFormatException e) {
		}
		return defaultValue;
	}

	/**
	 * Determines if the current section is empty.
	 */
	@Override
	public boolean isEmpty() {
		return current.isEmpty();
	}

	/**
	 * Returns the key set for the current section.
	 */
	@Override
	public Set<String> keySet() {
		return null;
	}

	public void load(final InputStream stream) throws IOException {
		// if you want to specify the charset, use the load(Reader) method
		load(new InputStreamReader(stream, Charset.defaultCharset()));
	}

	public void load(final Reader reader) throws IOException {
		final IniParser loader = new IniParser();
		// we need to read it quickly, don't keep them waiting
		// with slow reading if they decided to pass us a FileReader
		final char[] buffer = new char[IO_BUFFER_SIZE];
		int r = 0;
		while((r = reader.read(buffer, 0, IO_BUFFER_SIZE)) != -1)
			for(int i = 0; i < r; ++i)
				loader.process(buffer[i]);
		loader.process('\n');
	}

	/**
	 * Puts the given key value pair into the current section.
	 */
	@Override
	public String put(final String key, final String value) {
		return current.put(key, value);
	}

	@Override
	public void putAll(final Map<? extends String, ? extends String> m) {
		current.putAll(m);
	}

	/**
	 * Adds the given section to this configuration if it doesn't already exist
	 * and sets it as the active section.
	 * 
	 * @param sectionName
	 */
	public void putSection(final String sectionName) {
		current = data.get(sectionName);
		if(current == null) {
			current = new HashMap<String, String>();
			data.put(sectionName, current);
		}
	}

	/**
	 * Removes the given key from the current section.
	 */
	@Override
	public String remove(final Object key) {
		return current.remove(key);
	}

	/**
	 * Removes the given section from this configuration.
	 * 
	 * @param sectionName
	 *            The name of the section to remove.
	 * @return true if the section existed.
	 */
	public boolean removeSection(final String sectionName) {
		final boolean retValue = data.containsKey(sectionName);
		if(retValue) data.remove(sectionName);
		return retValue;
	}

	/**
	 * Resets the section to the default global one.
	 */
	public void resetSection() {
		putSection(null);
	}

	/**
	 * Returns the total number of sections. A new configuration starts with
	 * just the global section.
	 * 
	 * @return The total number of sections.
	 */
	public int sections() {
		return data.size();
	}

	/**
	 * Returns a set of the sections.
	 * 
	 * @return
	 */
	public Set<String> sectionSet() {
		return data.keySet();
	}

	/**
	 * Returns the total number of keys in the current section.
	 */
	@Override
	public int size() {
		if(current == null) return 0;
		return current.size();
	}

	/**
	 * Returns the value set for the current section.
	 */
	@Override
	public Collection<String> values() {
		return null;
	}
	
	private enum IniParserMode {
		UNKNOWN,
		KEY,
		VALUE,
		COMMENT,
		SECTION,
		QUOTED_VALUE
	};
	
	/**
	 * Supports multiline sections names. Supports escaped [ and ] in section names. 
	 * Uses = and : as key/value seperators. Supports escaped = and : in keys. 
	 * It trims the start and ends of section names, keys and values. However it does support quoted values.
	 * It supports ; and # as comments, they must be on their own line, otherwise treated as part of whatever (key/value/section).
	 * Blank lines are not an issue and are ignored, as is all other whitespace outside of quoted values.
	 * Supports all unicode characters in keys, sections and values. Supports standard Java escape sequences.
	 * @author Chase
	 *
	 */
	private class IniParser {
		StringBuilder sb = new StringBuilder();
		IniParserMode mode = IniParserMode.UNKNOWN;
		char qoute = '\0';
		String key = null;

		private void process(final char chr) {
			// lexer + parser
			switch(mode) {
			case UNKNOWN:
				// skip whitespace till we find something
				if(Character.isWhitespace(chr)) break;
				// section
				if(chr == '[') mode = IniParserMode.SECTION;
				else if(chr == ';' || chr == '#') mode = IniParserMode.COMMENT;
				else {
					sb.append(chr);
					mode = IniParserMode.KEY;
				}
				break;
			case KEY:
				if(chr == '\n' || chr == '\r') {
					// blah, we have no choice but to ignore it
					sb.setLength(0);
					mode = IniParserMode.UNKNOWN;
					break;
				}
				if(chr == '=' || chr == ':') {
					if(sb.charAt(sb.length() - 1) == '\\') {
						sb.setCharAt(sb.length() - 1, chr);
						break;
					}
					key = StringUtils.unescape(sb.toString().trim());
					sb.setLength(0);
					mode = IniParserMode.VALUE;
					break;
				}
				sb.append(chr);
				break;
			case QUOTED_VALUE:
				if(chr == qoute) {
					if(sb.charAt(sb.length() - 1) == '\\') {
						sb.setCharAt(sb.length() - 1, chr);
						break;
					}
					put(key, StringUtils.unescape(sb.toString()));
					sb.setLength(0);
					mode = IniParserMode.UNKNOWN;
					break;
				}
				sb.append(chr);
				break;
			case VALUE:
				if(chr == '\n' || chr == '\r') {
					put(key, StringUtils.unescape(sb.toString().trim()));
					sb.setLength(0);
					mode = IniParserMode.UNKNOWN;
					break;
				}
				if(chr == '\'' || chr == '"') {
					sb.setLength(0);
					qoute = chr;
					mode = IniParserMode.QUOTED_VALUE;
					break;
				}
				sb.append(chr);
				break;
			case COMMENT:
				if(chr == '\n' || chr == '\r') mode = IniParserMode.UNKNOWN;
				break;
			case SECTION:
				if(chr == '[' && sb.charAt(sb.length() - 1) == '\\') {
					sb.setCharAt(sb.length() - 1, chr);
					break;
				}
				if(chr == ']') {
					if(sb.charAt(sb.length() - 1) == '\\') {
						sb.setCharAt(sb.length() - 1, chr);
						break;
					}
					putSection(StringUtils.unescape(sb.toString().trim()));
					sb.setLength(0);
					mode = IniParserMode.UNKNOWN;
					break;
				}
				sb.append(chr);
				break;
			}
		}
	}
}
