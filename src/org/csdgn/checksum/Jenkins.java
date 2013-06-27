package org.csdgn.checksum;

import java.util.zip.Checksum;

public class Jenkins implements Checksum {
	private int hash = 0;
	
	@Override
	public void update(int b) {
		hash += b & 0xFF;
		hash += (hash << 10);
		hash ^= (hash >> 6);
	}

	@Override
	public void update(byte[] b, int off, int len) {
		for(int i = off; i < off+len; ++i) {
			hash += b[i] & 0xFF;
			hash += (hash << 10);
			hash ^= (hash >> 6);
		}
	}

	@Override
	public long getValue() {
		return getIntegerValue() & 0xFFFFFFFFL;
	}
	
	public int getIntegerValue() {
		int hash = this.hash;
		hash += (hash << 3);
	    hash ^= (hash >> 11);
	    hash += (hash << 15);
		return hash;
	}

	@Override
	public void reset() {
		hash = 0;
	}
}
