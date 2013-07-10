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
package org.csdgn.maru.deprecated;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.UUID;
import java.util.zip.*;

import org.csdgn.maru.io.FilesystemToolkit;
import org.csdgn.maru.io.ReaderLoader;
import org.csdgn.maru.io.StreamLoader;

/**
 * This package may be removed without warning!
 * @author Robert Maupin
 */
@Deprecated
public class ZipToolkit {
	public static final void append(ZipOutputStream zip, String name, File input) throws IOException {
		append(zip,name,new FileInputStream(input));
	}
	public static final void append(ZipOutputStream zip, String name, InputStream input) throws IOException {
		ZipEntry e = new ZipEntry(name);
		zip.putNextEntry(e);
		FilesystemToolkit.pipeAndClose(input, zip);
		zip.closeEntry();
	}
	public static final ZipFile getAsReadable(File file) throws IOException {
		return new ZipFile(file);
	}
	public static final InputStream getStream(ZipFile zip, String entry) throws IOException {
		return zip.getInputStream(zip.getEntry(entry));
	}
	public static final byte[] getBytes(ZipFile zip, String entry) throws IOException {
		return FilesystemToolkit.getAndClose(getStream(zip,entry));
	}
	public static final String getString(ZipFile zip, String entry) throws IOException {
		return FilesystemToolkit.getAndClose(new InputStreamReader(getStream(zip,entry)));
	}
	public static final String getString(ZipFile zip, String entry, String charset) throws IOException {
		return FilesystemToolkit.getAndClose(new InputStreamReader(getStream(zip,entry),charset));
	}
	public static final ZipOutputStream getAsWritable(File file) throws IOException {
		if(!file.exists()) {
			//new empty zip file :) 
			return new ZipOutputStream(new FileOutputStream(file)); 
		}
		
		//copy all the stuff from the old one to the new one... :(
		File tmp = File.createTempFile(UUID.randomUUID().toString(), null);
		
		copy(file, tmp);
		
		file.delete();
		
		ZipFile input = new ZipFile(tmp);
        ZipOutputStream output = new ZipOutputStream(new FileOutputStream(file));
        
        // first, copy contents from existing war
        Enumeration<? extends ZipEntry> entries = input.entries();
        while (entries.hasMoreElements()) {
            ZipEntry e = entries.nextElement();
            output.putNextEntry(e);
            if (!e.isDirectory()) {
            	//fastest pipe in the west
            	FilesystemToolkit.pipeAndClose(input.getInputStream(e), output);
            }
            output.closeEntry();
        }
        
        input.close();
        tmp.delete();
		
        return output;
	}
	
	@SuppressWarnings("resource")
	public static void copy(File src, File dest) throws IOException {
	    if(!dest.exists()) {
	        dest.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;
	    try {
	        source = new FileInputStream(src).getChannel();
	        destination = new FileOutputStream(dest).getChannel();

	        // previous code: destination.transferFrom(source, 0, source.size());
	        // to avoid infinite loops, should be:
	        long count = 0;
	        long size = source.size();              
	        while((count += destination.transferFrom(source, count, size-count))<size);
	    } finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
}
