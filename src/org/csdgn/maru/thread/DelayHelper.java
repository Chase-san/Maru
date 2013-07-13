package org.csdgn.maru.thread;

/**
 * Starting FPS is 60.
 * 
 * @author Chase
 */
public class DelayHelper {
	private long delayMS = 16L;
	private int delayNS = 666666;
	private long startTime = 0L;

	public DelayHelper() {
		startTime = System.nanoTime();
	}

	public void delay() throws InterruptedException {
		long off = getTime();
		startTime = System.nanoTime();
		long mso = off / 1000000L;
		long ms = delayMS - mso;
		if (ms < 0L) {
			ms = 0L;
		}
		int nso = (int) (off - (mso * 1000000L));
		int ns = delayNS - nso;
		if (ns < 0) {
			ns = 0;
		}
		Thread.sleep(ms, ns);
	}

	public long getTime() {
		return (System.nanoTime() - startTime) / 1000000L;
	}

	public void setTargetFPS(float fps) {
		double delay = 1000.0D / fps;
		delayMS = ((int) delay);
		delay -= delayMS;
		delayNS = (int) (delay * 1000000.0D);
		System.out.println(delayMS + " " + delayNS);
	}
}
