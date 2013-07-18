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

import java.awt.Point;
import java.util.HashSet;

/**
 * A map of bits.
 * 
 * @author Robert Maupin
 */
public class BitMap {
	private final BitSet[] map;

	private BitMap(final int height) {
		map = new BitSet[height];
	}

	public BitMap(final int width, final int height) {
		map = new BitSet[height];
		for (int i = 0; i < height; ++i) {
			map[i] = new BitSet(width);
		}
	}

	public void and(final BitMap bm) {
		for (int i = 0; i < map.length && i < bm.map.length; ++i) {
			map[i].and(bm.map[i]);
		}
	}

	public boolean at(final int x, final int y) {
		if (!valid(x, y)) {
			return false;
		}
		return map[y].at(x);
	}

	/**
	 * Runs a (slow) path finding routine to see if you can get to the second
	 * set of coordinates from the first. Only works if both are spots are 'on'.
	 */
	public boolean canPathFind(final int x0, final int y0, final int x1,
			final int y1) {
		if (!at(x0, y0) || !at(x1, y1)) {
			return false;
		}
		final HashSet<Point> open = new HashSet<Point>();
		final HashSet<Point> closed = new HashSet<Point>();
		open.add(new Point(x0, y0));
		while (true) {
			Point b = null;
			int v = Integer.MAX_VALUE;
			for (final Point p : open) {
				final int h = (x1 - p.x) * (x1 - p.x)
						+ (y1 - p.y) * (y1 - p.y);
				if (h < v) {
					b = p;
					v = h;
				}
			}
			if (b == null) {
				return false;
			}
			if (b.x == x1 && b.y == y1) {
				return true;
			}
			for (final Point p : pathN(b)) {
				if (!at(p.x, p.y)) {
					continue;
				}
				if (!closed.contains(p) && !open.contains(p)) {
					open.add(p);
				}
			}
			open.remove(b);
			closed.add(b);
		}
	}

	/**
	 * ORs all rows into a single BitSet.
	 */
	public BitSet combine() {
		final BitSet set = new BitSet(map[0].length());
		for (final BitSet element : map) {
			set.or(element);
		}
		return set;
	}

	/**
	 * @return number of its 4 neighbors are active.
	 */
	public int edges(final int x, final int y) {
		return (at(x - 1, y) ? 1 : 0) + (at(x + 1, y) ? 1 : 0)
				+ (at(x, y - 1) ? 1 : 0) + (at(x, y + 1) ? 1 : 0);
	}

	public int height() {
		return map.length;
	}

	public void or(final BitMap bm) {
		for (int i = 0; i < map.length && i < bm.map.length; ++i) {
			map[i].or(bm.map[i]);
		}
	}

	private Point[] pathN(final Point p) {
		return new Point[] { new Point(p.x - 1, p.y), new Point(p.x + 1, p.y),
				new Point(p.x, p.y - 1), new Point(p.x, p.y + 1) };
	}

	public void set(final int x, final int y) {
		map[y].set(x);
	}

	public BitMap snip(final int x0, final int y0, final int x1, final int y1) {
		if (x0 > x1) {
			if (y0 > y1) {
				return snip(x1, y1, x0, y0);
			} else {
				return snip(x1, y0, x0, y1);
			}
		}
		final BitMap map = new BitMap(y1 - y0 + 1);
		for (int y = y0; y <= y1; ++y) {
			final int y2 = y - y0;
			map.map[y2] = this.map[y].snip(x0, x1);
		}
		return map;
	}

	public String toBinaryString() {
		final StringBuilder sb = new StringBuilder();
		for (final BitSet element : map) {
			sb.append(element.toPaddedBinaryString());
			sb.append('\n');
		}
		return sb.toString();
	}

	public void unset(final int x, final int y) {
		map[y].unset(x);
	}

	public boolean valid(final int x, final int y) {
		if (x < 0 || y < 0) {
			return false;
		}
		if (y >= map.length || x >= map[y].length()) {
			return false;
		}
		return true;
	}

	public int width() {
		return map[0].length();
	}

	public void xor(final BitMap bm) {
		for (int i = 0; i < map.length && i < bm.map.length; ++i) {
			map[i].xor(bm.map[i]);
		}
	}
}
