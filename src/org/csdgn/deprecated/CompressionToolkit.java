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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

/**
 * This package may be removed without warning!
 * @author Robert Maupin
 *
 */
@Deprecated
public class CompressionToolkit {
	private CompressionToolkit() {}
	private static final int IO_BUFFER_SIZE = 8192;
	
	/**
	 * A one stop byte array gzip method.
	 */
	public static final byte[] gzip(byte[] in) throws IOException {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(buf);
		gzip.write(in);
		gzip.finish();
		gzip.close();
		return buf.toByteArray();
	}
	
	/**
	 * A one stop byte array ungzip method.
	 */
	public static final byte[] ungzip(byte[] in) throws IOException {
		ByteArrayInputStream buf = new ByteArrayInputStream(in);
		GZIPInputStream gzip = new GZIPInputStream(buf);
		ByteArrayOutputStream obuf = new ByteArrayOutputStream();
		byte[] buffer = new byte[IO_BUFFER_SIZE];
		int size = 0;
		while((size=gzip.read(buffer)) != -1) {
			obuf.write(buffer,0,size);
		}
		return obuf.toByteArray();
	}
	
	/**
	 * A one stop byte array deflater method.
	 */
	public static final byte[] deflate(byte[] in, Deflater deflater) throws IOException {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DeflaterOutputStream dbuf = new DeflaterOutputStream(buf,deflater);
		dbuf.write(in);
		dbuf.finish();
		dbuf.close();
		return buf.toByteArray();
	}
	
	/**
	 * A one stop byte array inflater method.
	 */
	public static final byte[] inflate(byte[] in, Inflater inflater) throws IOException {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		InflaterOutputStream ibuf = new InflaterOutputStream(buf,inflater);
		ibuf.write(in);
		ibuf.finish();
		ibuf.close();
		return buf.toByteArray();
	}
}
