package org.csdgn.util;

import java.util.ArrayList;

/**
 * Some basic methods to allow easy escaping and unescaping of strings. 
 * @author Chase
 */
public class StringUtils {
	/**
	 * This method unescapes special characters in the given string.
	 * @param string The string to unescape.
	 * @return A string with special characters unescaped.
	 */
	public static final String unescape(String string) {
		StringBuilder sb = new StringBuilder();
		boolean wasEscape = false;
		boolean unicode = false;
		int count = 0;
		for(char c : string.toCharArray()) {
			if(unicode) {
				sb.append(c);
				if(++count == 4) {
					int s = sb.length()-4;
					String code = sb.substring(s);
					sb.setLength(s);
					try {
						int ncode = Integer.parseInt(code, 16);
						sb.append((char)ncode);
					} catch(NumberFormatException e) {
						sb.append("\\u");
						sb.append(code);
					}
					unicode = false;
				}
			} else if(wasEscape) {
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
				case 'u':
					unicode = true;
					count = 0;
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
		if(unicode) {
			int s = sb.length()-count;
			String code = sb.substring(s);
			sb.setLength(s);
			sb.append("\\u");
			sb.append(code);
		}
		if(wasEscape)
			sb.append('\\');
		return sb.toString();
	}
	
	/**
	 * This method escapes special characters in the given string.
	 * @param string The string to escape.
	 * @return A string with special characters escaped.
	 */
	public static final String escape(String string) {
		return escape(string,true);
	}
	
	/**
	 * This method escapes special characters in the given string.
	 * @param string The string to escape.
	 * @param quotes If quotes should be escaped.
	 * @return A string with special characters escaped.
	 */
	public static final String escape(String string, boolean quotes) {
		StringBuilder sb = new StringBuilder();
		for(char c : string.toCharArray()) {
			switch(c) {
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\0':
				sb.append("\\0");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\"':
				if(quotes) {
					sb.append("\\\"");
					break;
				}
				sb.append(c);
				break;
			case '\'':
				if(quotes) {
					sb.append("\\'");
					break;
				}
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Preserves null sections. "ab..cd" would produce [ab][][cd] with '.'
	 * @param src The source string
	 * @param delim The delimiter string to split by
	 * @return An array of strings.
	 */
	public static final String[] split(final String src, final char delim) {
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
	 * Preserves null sections. "ab..cd" would produce [ab][][cd] with "."
	 * @param src The source string
	 * @param delim The delimiter string to split by
	 * @return An array of strings.
	 */
	public static final String[] split(final String src, final String delim) {
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
}
