package org.csdgn.maru.thread;

/**
 * Starting FPS is 60.
 * @author Chase
 */
public class DelayHelper {
	private long delayMS = 16L;
	private int delayNS = 666666;
	private long startTime = 0L;

	public DelayHelper() {
		this.startTime = System.nanoTime();
	}

	public void setTargetFPS(float fps) {
		double delay = 1000.0D / (double) fps;
		this.delayMS = (long) ((int) delay);
		delay -= (double) this.delayMS;
		this.delayNS = (int) (delay * 1000000.0D);
		System.out.println(this.delayMS + " " + this.delayNS);
	}

	public long getTime() {
		return (System.nanoTime() - this.startTime) / 1000000L;
	}

	public void delay() throws InterruptedException {
		long off = this.getTime();
		this.startTime = System.nanoTime();
		long mso = off / 1000000L;
		long ms = this.delayMS - mso;
		if (ms < 0L)
			ms = 0L;
		int nso = (int) (off - mso * 1000000L);
		int ns = this.delayNS - nso;
		if (ns < 0)
			ns = 0;
		Thread.sleep(ms, ns);
	}
}
