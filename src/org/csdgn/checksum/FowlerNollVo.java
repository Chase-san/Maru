package org.csdgn.checksum;

import java.util.zip.Checksum;

public class FowlerNollVo implements Checksum {
	private static final int FNVPrime = 0x01000193;
	private static final int FNVOffsetBasis = 0x811C9DC5;
	private int hash;
	
	public FowlerNollVo() {
		reset();
	}
	
	@Override
	public void update(int b) {
		hash ^= b;
		hash *= FNVPrime;
	}

	@Override
	public void update(byte[] b, int off, int len) {
		for(int i = off; i < off+len; ++i) {
			hash ^= b[i];
			hash *= FNVPrime;
		}
	}

	@Override
	public long getValue() {
		return hash & 0xFFFFFFFF;
	}

	@Override
	public void reset() {
		hash = FNVOffsetBasis;
	}
}
