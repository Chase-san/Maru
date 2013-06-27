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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This package may be removed without warning! 
 * @author Robert Maupin
 */
@Deprecated
public class CryptoToolkit {
	private static final Charset str_charset = Charset.forName("UTF-8");
	private static final String ALGORITHM_MD5 = "MD5";
	private static final String ALGORITHM_SHA1 = "SHA1";
	
	public static MessageDigest getDigest(String type) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(type);
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return md;
	}
	
	public static byte[] md5(byte[] input) {
		MessageDigest md = getDigest(ALGORITHM_MD5);
		md.digest(input);
		return md.digest();
	}
	
	public static byte[] md5(String input) {
		MessageDigest md = getDigest(ALGORITHM_MD5);
		md.digest(input.getBytes(str_charset));
		return md.digest();
	}
	
	public static byte[] md5(InputStream input) throws IOException {
		MessageDigest md = getDigest(ALGORITHM_MD5);
		
		DigestInputStream dis = new DigestInputStream(new BufferedInputStream(input),md);
		dis.on(true);
		
		try {
			byte[] buf = new byte[1024*8];
			while(dis.read(buf) != -1);
		} finally {
			dis.close();
		}
		
		return md.digest();
	}
	
	public static String md5str(byte[] input) {
		MessageDigest md = getDigest(ALGORITHM_MD5);
		md.digest(input);
		return byte2hex(md.digest());
	}
	
	public static String md5str(String input) {
		MessageDigest md = getDigest(ALGORITHM_MD5);
		md.digest(input.getBytes(str_charset));
		return byte2hex(md.digest());
	}
	
	public static String md5str(InputStream input) throws IOException {
		MessageDigest md = getDigest(ALGORITHM_MD5);
		
		DigestInputStream dis = new DigestInputStream(new BufferedInputStream(input),md);
		dis.on(true);
		
		try {
			byte[] buf = new byte[1024*8];
			while(dis.read(buf) != -1);
		} finally {
			dis.close();
		}
		
		return byte2hex(md.digest());
	}
	
	public static byte[] sha1(byte[] input) {
		MessageDigest md = getDigest(ALGORITHM_SHA1);
		md.digest(input);
		return md.digest();
	}
	
	public static byte[] sha1(String input) {
		MessageDigest md = getDigest(ALGORITHM_SHA1);
		md.digest(input.getBytes(str_charset));
		return md.digest();
	}
	
	public static byte[] sha1(InputStream input) throws IOException {
		MessageDigest md = getDigest(ALGORITHM_SHA1);
		
		DigestInputStream dis = new DigestInputStream(new BufferedInputStream(input),md);
		dis.on(true);
		
		try {
			byte[] buf = new byte[1024*8];
			while(dis.read(buf) != -1);
		} finally {
			dis.close();
		}
		
		return md.digest();
	}
	
	public static String sha1str(byte[] input) {
		MessageDigest md = getDigest(ALGORITHM_SHA1);
		md.digest(input);
		return byte2hex(md.digest());
	}
	
	public static String sha1str(String input) {
		MessageDigest md = getDigest(ALGORITHM_SHA1);
		md.digest(input.getBytes(str_charset));
		return byte2hex(md.digest());
	}
	
	public static String sha1str(InputStream input) throws IOException {
		MessageDigest md = getDigest(ALGORITHM_SHA1);
		
		DigestInputStream dis = new DigestInputStream(new BufferedInputStream(input),md);
		dis.on(true);
		
		try {
			byte[] buf = new byte[1024*8];
			while(dis.read(buf) != -1);
		} finally {
			dis.close();
		}
		
		return byte2hex(md.digest());
	}

	public static String byte2hex(byte[] stream) {
		StringWriter writer = new StringWriter();
		for(int i = 0; i < stream.length; ++i) {
			writer.append(String.format("%02x", stream[i]));
		}
		return writer.toString();
	}
}
