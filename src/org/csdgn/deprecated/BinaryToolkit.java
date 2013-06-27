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
package org.csdgn.deprecated;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * This package may be removed without warning!
 * 
 * @author Robert Maupin
 * 
 */
@Deprecated
public class BinaryToolkit {
	/**
	 * Returns the index within this string of the first occurrence of the
	 * specified substring. If it is not a substring, return -1.
	 * 
	 * @param haystack
	 *            The string to be scanned
	 * @param needle
	 *            The target string to search
	 * @return The start index of the substring
	 */
	public static int indexOf(final byte[] haystack, final byte[] needle, final int start) {
		if(needle.length == 0) return 0;
		final int charTable[] = makeCharTable(needle);
		final int offsetTable[] = makeOffsetTable(needle);
		for(int i = needle.length - 1, j; i < haystack.length - start;) {
			for(j = needle.length - 1; needle[j] == haystack[i + start]; --i, --j)
				if(j == 0) return i + start;
			// i += needle.length - j; // For naive method
			i += Math.max(offsetTable[needle.length - 1 - j], charTable[haystack[i + start]]);
		}
		return -1;
	}

	/**
	 * Is needle[p:end] a prefix of needle?
	 */
	private static boolean isPrefix(final byte[] needle, final int p) {
		for(int i = p, j = 0; i < needle.length; ++i, ++j)
			if(needle[i] != needle[j]) return false;
		return true;
	}

	/**
	 * Makes the jump table based on the mismatched character information.
	 */
	private static int[] makeCharTable(final byte[] needle) {
		final int ALPHABET_SIZE = 256;
		final int[] table = new int[ALPHABET_SIZE];
		for(int i = 0; i < table.length; ++i)
			table[i] = needle.length;
		for(int i = 0; i < needle.length - 1; ++i)
			table[needle[i]] = needle.length - 1 - i;
		return table;
	}

	/**
	 * Makes the jump table based on the scan offset which mismatch occurs.
	 */
	private static int[] makeOffsetTable(final byte[] needle) {
		final int[] table = new int[needle.length];
		int lastPrefixPosition = needle.length;
		for(int i = needle.length - 1; i >= 0; --i) {
			if(isPrefix(needle, i + 1)) lastPrefixPosition = i + 1;
			table[needle.length - 1 - i] = lastPrefixPosition - i + needle.length - 1;
		}
		for(int i = 0; i < needle.length - 1; ++i) {
			final int slen = suffixLength(needle, i);
			table[slen] = needle.length - 1 - i + slen;
		}
		return table;
	}

	public static long packDoubleToFewerBits(final int maxBits, double maxValue, double value, final boolean hasNegative) {
		final int total_values = (1 << maxBits) - 1;
		if(hasNegative) {
			value += maxValue;
			maxValue *= 2.0D;
		}
		final double divisor = maxValue / total_values;
		final long i = (long) Math.rint(value / divisor);
		return i;
	}

	/**
	 * Byte array based replace.
	 */
	public static byte[] replace(final byte[] haystack, final byte[] needle, final byte[] replacement) {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int start = 0;
		int n = -1;
		try {
			while((n = indexOf(haystack, needle, start)) != -1) {
				baos.write(Arrays.copyOfRange(haystack, start, n));
				baos.write(replacement);
				start = n + needle.length;
			}
			if(start < haystack.length) baos.write(Arrays.copyOfRange(haystack, start, haystack.length));
		}
		catch(final Exception e) {
			// we should never ever get here
			e.printStackTrace();
		}
		return baos.toByteArray();
	}

	/**
	 * Returns the maximum length of the substring ends at p and is a suffix.
	 */
	private static int suffixLength(final byte[] needle, final int p) {
		int len = 0;
		for(int i = p, j = needle.length - 1; i >= 0 && needle[i] == needle[j]; --i, --j)
			len += 1;
		return len;
	}

	public static double unpackDoubleFromFewerBits(final int maxBits, final double maxValue, final long value, final boolean hasNegative) {
		final int total_values = (1 << maxBits) - 1;
		double work = (double) value / (double) total_values;
		if(hasNegative) {
			work *= maxValue * 2.0D;
			work -= maxValue;
		} else work *= maxValue;
		return work;
	}

	private BinaryToolkit() {
	}
}
