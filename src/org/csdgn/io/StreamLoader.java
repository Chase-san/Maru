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
package org.csdgn.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Robert Maupin
 */
public abstract class StreamLoader {
	private static final int IO_BUFFER_SIZE = 16384;
	private InputStream input;
	
	/** 
	 * @param input
	 */
	public StreamLoader(InputStream input) {
		this.input = input;
	}
	
	/**
	 * Called when an array of bytes is read.
	 * @param read
	 * @param length
	 */
	public void onByteArrayRead(byte[] read, int length) {
		for(int i=0;i < length; ++i)
			onByteRead(read[i]);
	}
	
	/**
	 * Called when a byte is read.
	 * @param input
	 */
	public abstract void onByteRead(byte input);
	
	/**
	 * @throws IOException 
	 * 
	 */
	public void readAll() throws IOException {
		if (input != null) {
			final BufferedInputStream input = new BufferedInputStream(this.input);
			final byte[] reader = new byte[IO_BUFFER_SIZE];
			int r = 0;
			while ((r = input.read(reader, 0, IO_BUFFER_SIZE)) != -1)
				onByteArrayRead(reader, r);
		} 
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	public void readAllAndClose() throws IOException {
		if (input != null) try {
			final BufferedInputStream input = new BufferedInputStream(this.input);
			final byte[] reader = new byte[IO_BUFFER_SIZE];
			int r = 0;
			while ((r = input.read(reader, 0, IO_BUFFER_SIZE)) != -1)
				onByteArrayRead(reader, r);
		} finally {
			this.input.close();
		}
	}
}
