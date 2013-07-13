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
package org.csdgn.maru.crypto;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Robert Maupin
 */
public class RabbitInputStream extends FilterInputStream {
	public final Rabbit rabbit;

	/**
	 * zero key stream
	 */
	public RabbitInputStream(InputStream in) {
		super(in);
		rabbit = new Rabbit();
		rabbit.setupKey(new short[] { 0, 0, 0, 0, 0, 0, 0, 0 });
	}

	/**
	 * @param key
	 *            16 bytes
	 */
	public RabbitInputStream(InputStream in, byte[] key) {
		super(in);
		if (key.length != 16) {
			throw new IllegalArgumentException(
					"A byte array key must have 16 entries.");
		}
		rabbit = new Rabbit();
		rabbit.setupKey(key);
	}

	/**
	 * @param key
	 *            16 bytes
	 * @param iv
	 *            8 bytes
	 */
	public RabbitInputStream(InputStream in, byte[] key, byte[] iv) {
		super(in);
		if (key.length != 16) {
			throw new IllegalArgumentException(
					"A byte array key must have 16 entries.");
		}
		if (iv.length != 8) {
			throw new IllegalArgumentException(
					"A byte array iv must have 8 entries.");
		}
		rabbit = new Rabbit();
		rabbit.setupKey(key);
		rabbit.setupIV(iv);
	}

	/**
	 * @param key
	 *            8 shorts
	 */
	public RabbitInputStream(InputStream in, short[] key) {
		super(in);
		if (key.length != 8) {
			throw new IllegalArgumentException(
					"A short array key must have 8 entries.");
		}
		rabbit = new Rabbit();
		rabbit.setupKey(key);
	}

	/**
	 * @param key
	 *            8 shorts
	 * @param iv
	 *            4 shorts
	 */
	public RabbitInputStream(InputStream in, short[] key, short[] iv) {
		super(in);
		if (key.length != 8) {
			throw new IllegalArgumentException(
					"A short array key must have 8 entries.");
		}
		if (iv.length != 4) {
			throw new IllegalArgumentException(
					"A short array iv must have 4 entries.");
		}
		rabbit = new Rabbit();
		rabbit.setupKey(key);
		rabbit.setupIV(iv);
	}

	/**
	 * RabbitInputStream does not support marks
	 */
	@Override
	public void mark(int readLimit) {
		throw new UnsupportedOperationException(
				"RabbitInputStream does not support mark(int).");
	}

	/**
	 * RabbitInputStream does not support marks
	 */
	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public int read() throws IOException {
		int n = in.read();
		if (n == -1) {
			return -1;
		}
		byte[] data = new byte[] { (byte) n };
		rabbit.crypt(data);
		return data[0];
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		len = in.read(b, off, len);
		rabbit.crypt(b, off, len);
		return len;
	}

	/**
	 * RabbitInputStream does not support marks
	 */
	@Override
	public void reset() {
		throw new UnsupportedOperationException(
				"RabbitInputStream does not support reset().");
	}

	@Override
	public long skip(long n) throws IOException {
		long skipped = in.skip(n);
		// skip this number in Rabbit as well
		rabbit.skip(skipped);
		return skipped;
	}
}
