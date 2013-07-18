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
package org.csdgn.maru.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.CRC32;

import javax.imageio.ImageIO;

/**
 * This class builds the frames of a APNG image. Needs to be optimized...
 * 
 * @author Robert Maupin
 * 
 */
public class APNGFrameBuilder {
	private static class Frame {
		BufferedImage image;
		int width;
		int height;
		int x_offset;
		int y_offset;
		long delay_ms;
		byte dispose_op;
		byte blend_op;

		protected Frame() {}

		public Frame(byte[] data) throws IOException {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
					data));
			dis.skip(4);
			image = null;
			width = dis.readInt();
			height = dis.readInt();
			x_offset = dis.readInt();
			y_offset = dis.readInt();
			int delay_num = dis.readShort() & 0xFFFF;
			int delay_den = dis.readShort() & 0xFFFF;

			if (delay_den == 0) {
				delay_den = 100;
			}
			if (delay_num == 0) {
				delay_ms = 1;
			} else {
				double fraction = delay_num / (double) delay_den;
				delay_ms = (long) (fraction * 1000L);
			}

			dispose_op = (byte) dis.read();
			blend_op = (byte) dis.read();
			dis.close();
		}
	}

	private static BufferedImage copy(BufferedImage bufImg) {
		ColorModel cm = bufImg.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bufImg.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	private InputStream imgStream;
	private BufferedImage[] frames;
	private long[] durations;
	private int width;
	private int height;
	private boolean isAnimated;
	private Frame[] bFrames;

	/**
	 * @param stream
	 *            the ImageInputStream describing a possible Animated PNG.
	 */
	public APNGFrameBuilder(InputStream stream) throws IOException {
		imgStream = stream;
		durations = null;
		frames = null;
	}

	/**
	 * @param stream
	 *            the ImageInputStream describing a possible Animated PNG.
	 * @param process
	 *            if true, it will process() the stream now.
	 * @throws IOException
	 *             If the processing throws an exception.
	 */
	public APNGFrameBuilder(InputStream stream, boolean process)
			throws IOException {
		imgStream = stream;
		durations = null;
		frames = null;

		if (process) {
			process();
		}
	}

	private byte[] buildIHDR(int width, int height, byte[] extra)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeInt(width);
		dos.writeInt(height);
		dos.write(extra);
		return baos.toByteArray();
	}

	private CRC32 crc = new CRC32();
	private byte[] crcChunk(byte[] type, byte[] data) throws IOException {
		crc.reset();
		crc.update(type);
		if (data != null) {
			crc.update(data);
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeInt((int) crc.getValue());
		dos.close();
		return baos.toByteArray();
	}

	private void doDecode(InputStream input) throws IOException {
		byte[] header = new byte[] { (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a,
				0x1a, 0x0a };
		DataInputStream stream = new DataInputStream(input);
		{
			// Check Header
			byte[] buffer = new byte[header.length];
			stream.read(buffer);
			for (int i = 0; i < header.length; ++i) {
				if (buffer[i] != header[i]) {
					return;
				}
			}
		}

		ByteArrayOutputStream bRawChunks = new ByteArrayOutputStream();
		DataOutputStream bRawChunksDS = new DataOutputStream(bRawChunks);

		byte[] typeBuffer = new byte[4];
		byte[] IHDRStf = new byte[5];

		// OTHER
		boolean mainImageIsFrame0 = false;
		boolean hasIDAT = false;

		int num_frames = 1;

		// Unfortunately required :(
		// TODO replace this with something faster (somehow)!
		ArrayList<ArrayList<byte[]>> fBytes = new ArrayList<ArrayList<byte[]>>();

		int fcTLIndex = 0;

		loop: while (true) {
			int length = stream.readInt();
			stream.read(typeBuffer);
			String type = new String(typeBuffer, StandardCharsets.US_ASCII);
			byte[] data = new byte[length];
			stream.read(data);
			// Skip CRC
			stream.skip(4);
			if (isImportantChunk(type)) {
				bRawChunksDS.writeInt(length);
				bRawChunksDS.writeBytes(type);
				bRawChunksDS.write(data);
				bRawChunksDS.write(typeBuffer);
			}
			switch (type) {
			case "acTL":
				if (!hasIDAT) {
					isAnimated = true;

					DataInputStream dis = new DataInputStream(
							new ByteArrayInputStream(data));
					num_frames = dis.readInt();

					// TODO num_plays = dis.readInt();
					dis.close();

					bFrames = new Frame[num_frames];
				}
				break;
			case "fcTL":
				if (isAnimated) {
					if (!hasIDAT) {
						mainImageIsFrame0 = true;
					}
					bFrames[fcTLIndex] = new Frame(data);

					++fcTLIndex;
					break;
				}
			case "IDAT":
				hasIDAT = true;
				if (mainImageIsFrame0 || !isAnimated) {
					ArrayList<byte[]> list;
					if (fBytes.size() > 0) {
						list = fBytes.get(0);
					} else {
						fBytes.add(list = new ArrayList<byte[]>());
					}
					list.add(data);
				}
				break;
			case "IHDR": {
				DataInputStream dis = new DataInputStream(
						new ByteArrayInputStream(data));
				width = dis.readInt();
				height = dis.readInt();
				dis.read(IHDRStf);
				dis.close();
				break;
			}
			case "fdAT": {
				DataInputStream dis = new DataInputStream(
						new ByteArrayInputStream(data));

				dis.skip(4);

				byte[] IDAT = new byte[data.length - 4];
				dis.read(IDAT);

				//TODO see if we actually have to do this...
				ArrayList<byte[]> list;
				if (fBytes.size() > fcTLIndex - 1) {
					list = fBytes.get(fcTLIndex - 1);
				} else {
					fBytes.add(list = new ArrayList<byte[]>());
				}
				list.add(IDAT);

				dis.close();
				break;
			}
			case "IEND":
				break loop;
			}
		}
		byte[] chunks = bRawChunks.toByteArray();

		if (isAnimated) {

			for (int i = 0; i < num_frames; ++i) {
				bRawChunks.reset();
				bRawChunksDS.write(header);

				bRawChunksDS.writeInt(13);
				byte[] type = getTypeBytes("IHDR");
				bRawChunksDS.write(type);

				byte[] data = buildIHDR(bFrames[i].width, bFrames[i].height,
						IHDRStf);
				bRawChunksDS.write(data);
				bRawChunksDS.write(crcChunk(type, data));

				ArrayList<byte[]> idats = fBytes.get(i);

				// IDAT
				for (byte[] IDAT : idats) {
					bRawChunksDS.writeInt(IDAT.length);
					type = getTypeBytes("IDAT");
					bRawChunksDS.write(type);
					bRawChunksDS.write(IDAT);
					bRawChunksDS.write(crcChunk(type, IDAT));
				}

				idats.clear();

				// All other important chunks
				bRawChunksDS.write(chunks);

				// IEND
				bRawChunksDS.writeInt(0);
				type = getTypeBytes("IEND");
				bRawChunksDS.write(type);
				bRawChunksDS.write(crcChunk(type, null));

				bRawChunksDS.flush();

				// Finally decode this with the java decoder.
				bFrames[i].image = ImageIO.read(new ByteArrayInputStream(
						bRawChunks.toByteArray()));
			}
		} else {
			bRawChunks.reset();
			bRawChunksDS.write(header);

			// IHDR
			// length
			byte[] data = buildIHDR(width, height, IHDRStf);

			bRawChunksDS.writeInt(data.length);
			// type
			byte[] type = getTypeBytes("IHDR");
			bRawChunksDS.write(type);
			// data
			bRawChunksDS.write(data);
			// crc
			bRawChunksDS.write(crcChunk(type, data));

			ArrayList<byte[]> idats = fBytes.get(0);

			// IDAT
			for (byte[] IDAT : idats) {
				bRawChunksDS.writeInt(IDAT.length);
				type = getTypeBytes("IDAT");
				bRawChunksDS.write(type);
				bRawChunksDS.write(IDAT);
				bRawChunksDS.write(crcChunk(type, IDAT));
			}

			idats.clear();

			// All other important chunks
			bRawChunksDS.write(chunks);

			// IEND
			// length
			bRawChunksDS.writeInt(0);
			// type
			type = getTypeBytes("IEND");
			bRawChunksDS.write(type);
			// CRC
			bRawChunksDS.write(crcChunk(type, null));

			bRawChunksDS.flush();

			// Finally decode this with the java decoder.
			bFrames = new Frame[1];
			bFrames[0] = new Frame();
			ByteArrayInputStream bais = new ByteArrayInputStream(
					bRawChunks.toByteArray());
			bFrames[0].image = ImageIO.read(bais);
			bais.close();
		}

		// Okay! THE HARD PART IS DONE NOW!
	}

	private void doProcess() {
		if (!isAnimated) {
			frames = new BufferedImage[] { bFrames[0].image };
			durations = new long[] { 0 };
			return;
		}

		frames = new BufferedImage[bFrames.length];
		durations = new long[bFrames.length];
		BufferedImage buffer = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB_PRE);

		Color transparent = new Color(0, 0, 0, 0);

		for (int i = 0; i < bFrames.length; ++i) {
			Frame f = bFrames[i];
			Graphics2D g = buffer.createGraphics();

			// Blend Operation
			switch (f.blend_op) {
			case 0: // OP Source
				g.setComposite(AlphaComposite.Src);
				break;
			case 1: // OP Over
				g.setComposite(AlphaComposite.DstOver);
				break;
			}

			g.drawImage(f.image, f.x_offset, f.y_offset, null);

			frames[i] = copy(buffer);
			durations[i] = f.delay_ms;

			// DISPOSE
			switch (f.dispose_op) {
			case 0: // no disposal
				break;
			case 2: // previous. Revert to previous image.
				if (i != 0) {
					for (int pIndex = i - 1; pIndex >= 0; --pIndex) {
						if (bFrames[pIndex].dispose_op <= 1) {
							buffer = copy(frames[pIndex]);
							break;
						}
					}
					break;
				}
				// use background if first image
			case 1: // background, clear frame bounds to transparent
				g.setBackground(transparent);
				g.clearRect(f.x_offset, f.y_offset, f.width, f.height);
				break;
			}
		}
	}

	public long[] getDurations() {
		return durations;
	}

	public int getFrameCount() {
		return frames.length;
	}

	public BufferedImage[] getFrames() {
		return frames;
	}

	private byte[] getTypeBytes(String str) {
		return str.getBytes(StandardCharsets.US_ASCII);
	}

	private boolean isImportantChunk(String string) {
		switch (string) {
		case "PLTE":
		case "bKGD":
		case "sPLT":
		case "tRNS":
			return true;
		}
		return false;
	}

	/**
	 * Call to process the ImageInputStream, if it has not already been
	 * processed.
	 */
	public void process() throws IOException {
		if (imgStream != null) {
			doDecode(imgStream);
			doProcess();
		}
		// Cleanup
		imgStream = null;
		for (Frame f : bFrames) {
			f.image = null;
		}
		bFrames = null;
		width = height = 0;
	}
}
