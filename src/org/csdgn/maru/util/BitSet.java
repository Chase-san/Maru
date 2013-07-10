/**
 * Copyright (c) 2013 Robert Maupin
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
package org.csdgn.maru.util;

import java.math.BigInteger;

/**
 * A set of bits.
 * 
 * @author Robert Maupin
 */
public class BitSet {
	public static BitSet wrap(final BigInteger value) {
		final BitSet bitset = new BitSet(value.bitLength());
		final byte[] bytes = value.toByteArray();
		// derp, forgot, java is big endian
		for(int i = 0; i < bytes.length; ++i) {
			final int ti = bytes.length - i - 1;
			final int l = i >> 3;
			final int lb = i - (l << 3);
			bitset.set[l] |= bytes[ti] << (lb << 3);
		}
		return bitset;
	}

	private long mask = 0xFFFFFFFFFFFFFFFFL;
	private final int length;
	private final long[] set;

	public BitSet(final int length) {
		this.length = length;
		int sW = length >> 6;
		if(sW << 6 != length) ++sW;
		set = new long[sW];
		mask = (1L << length) - 1L;
	}

	public void and(final BitSet bs) {
		for(int i = 0; i < set.length && i < bs.set.length; ++i)
			set[i] &= bs.set[i];
		mask();
	}

	public boolean at(final int i) {
		final int s = i >> 6;
		return (set[s] >>> i - (s << 6) & 1) == 1;
	}

	/**
	 * @return true if this bitset is non-zero
	 */
	public boolean combine() {
		for(int i = 0; i < length; ++i)
			if(at(i)) return true;
		return false;
	}

	public int length() {
		return length;
	}

	private void mask() {
		set[set.length - 1] &= mask;
	}

	public void not() {
		for(int i = 0; i < set.length; ++i)
			set[i] = ~set[i];
		mask();
	}

	public void or(final BitSet bs) {
		for(int i = 0; i < set.length && i < bs.set.length; ++i)
			set[i] |= bs.set[i];
		mask();
	}

	public void reset() {
		for(int i = 0; i < set.length; ++i)
			set[i] = 0;
	}

	public void set(final int i) {
		final int s = i >> 6;
		set[s] |= 1L << i - (s << 6);
	}

	public BitSet snip(final int index0, final int index1) {
		if(index1 < index0) return snip(index1, index0);
		mask();
		final BitSet set = new BitSet(index1 - index0 + 1);
		for(int i = index0; i <= index1; ++i) {
			final int n = i - index0;
			if(at(i)) set.set(n);
		}
		return set;
	}

	public BigInteger toBigInteger() {
		mask();
		BigInteger bi = BigInteger.ZERO;
		for(int i = 0; i < set.length << 6; ++i)
			if(at(i)) bi = bi.setBit(i);
		return bi;
	}

	public String toBinaryString() {
		mask();
		final StringBuilder sb = new StringBuilder();
		int c = 0;
		for(int i = 0; i < length; ++i)
			if(at(i)) c = i;
		if(c == 0) return "0";
		for(int i = length; i >= 0; --i)
			sb.append(at(i) ? '1' : '0');
		return sb.toString();
	}

	public String toPaddedBinaryString() {
		mask();
		final StringBuilder sb = new StringBuilder();
		for(int i = length - 1; i >= 0; --i)
			sb.append(at(i) ? '1' : '0');
		return sb.toString();
	}

	@Override
	public String toString() {
		return "BitSet[" + toPaddedBinaryString() + "]";
	}

	public void unset(final int i) {
		final int s = i >> 6;
		set[s] &= ~(1L << i - (s << 6));
	}

	public void xor(final BitSet bs) {
		for(int i = 0; i < set.length && i < bs.set.length; ++i)
			set[i] ^= bs.set[i];
		mask();
	}
}
