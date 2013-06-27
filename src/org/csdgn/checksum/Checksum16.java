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
package org.csdgn.checksum;

import java.util.zip.Checksum;

/**
 * A simple 16 bit checksum.
 * @author Robert Maupin
 */
public class Checksum16 implements Checksum {
	private long sum = 0;

	@Override
	public void update(int b) {
		sum += b & 0xFFFF;
	}

	@Override
	public void update(byte[] b, int off, int len) {
		for(int i=0;i<len;++i) {
			update(b[i+off]);
		}
	}

	@Override
	public long getValue() {
		return (~(sum & 0xFFFF) + 1) & 0xFFFF;
	}

	@Override
	public void reset() {
		sum = 0;
	}
}
