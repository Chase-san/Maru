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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * The RabbitOutputStream is more efficient then the RabbitInputStream. Due to
 * not needing to preserve input arrays.
 * 
 * @author Robert Maupin
 */
public class RabbitOutputStream extends FilterOutputStream {
	public final Rabbit rabbit;

	/**
	 * zero key stream
	 */
	public RabbitOutputStream(OutputStream out) {
		super(out);
		rabbit = new Rabbit();
		rabbit.setupKey(new short[] { 0, 0, 0, 0, 0, 0, 0, 0 });
	}

	/**
	 * @param key
	 *            16 bytes
	 */
	public RabbitOutputStream(OutputStream out, byte[] key) {
		super(out);
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
	public RabbitOutputStream(OutputStream out, byte[] key, byte[] iv) {
		super(out);
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
	public RabbitOutputStream(OutputStream out, short[] key) {
		super(out);
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
	public RabbitOutputStream(OutputStream out, short[] key, short[] iv) {
		super(out);
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

	@Override
	public void write(byte[] b) throws IOException {
		// rabbit alters the array, so we have to make a copy
		byte[] data = b.clone();
		rabbit.crypt(data);
		out.write(data);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		// rabbit alters the array, so we have to make a copy
		byte[] data = Arrays.copyOfRange(b, off, off + len);
		rabbit.crypt(data, 0, data.length);
		out.write(data);
	}

	@Override
	public void write(int b) throws IOException {
		byte[] data = new byte[] { (byte) b };
		rabbit.crypt(data);
		out.write(data[0]);
	}
}
