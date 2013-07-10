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
package org.csdgn.maru.io;

import java.io.File;

/**
 * 
 * @author Robert Maupin
 *
 */
public class Filename {
	private String dir;
	private String name;
	private String ext;
	
	public Filename(File file) {
		this(file.getAbsolutePath());
	}
	
	public Filename(String file) {
		dir = ext = "";
		name = file.replace('\\', '/');
		
		int sp = name.lastIndexOf('/');
		
		if(sp != -1) {
			dir = name.substring(0,sp+1);
			name = name.substring(sp+1);
		}
		
		int extp = name.lastIndexOf('.');
		
		if(extp != -1) {
			ext = name.substring(extp+1);
			name = name.substring(0,extp);
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setExt(String ext) {
		this.ext = ext;
	}
	
	public String getExt() {
		return ext;
	}
	
	public void setDirectory(String dir) {
		this.dir = dir;
	}
	
	public String getDirectory() {
		return dir;
	}
	
	public void setNameIsDirectory() {
		if(name.length() == 0)
			return;
		dir += name + "/";
		name = "";
	}
	
	public File toFile() {
		return new File(toString());
	}
	
	public String toString() {
		return dir + name + "." + ext;
	}
}
