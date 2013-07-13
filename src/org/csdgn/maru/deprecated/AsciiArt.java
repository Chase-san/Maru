package org.csdgn.maru.deprecated;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;

import javax.imageio.ImageIO;

/**
 * This class was not designed to run in real time, but rather to produce high
 * quality ASCII art.
 * 
 * The algorithm may have trouble with determining the difference between shades
 * of darker colors.
 * 
 * @author Chase
 */
public class AsciiArt {
	private static final Font font = new Font("Consolas", Font.PLAIN, 64);
	@SuppressWarnings("deprecation")
	private static final FontMetrics fm = Toolkit.getDefaultToolkit()
			.getFontMetrics(font);

	/* Lower values makes the image brighter, higher makes it darker */

	/**
	 * This one produces better output then generateDensityAsciiArt, however it
	 * is slower.
	 */
	public static final String generateAreaDensityAsciiArt(BufferedImage img,
			int outputWidth) {
		double[][] glyphDensity = new double[128][];

		// generate density map
		{
			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;
			for (int i = 32; i < 128; ++i) {

				glyphDensity[i] = getGlyphAreaDensity9(i);
				if (glyphDensity[i] == null) {
					continue;
				}

				for (int j = 0; j < glyphDensity[i].length; ++j) {
					if (glyphDensity[i][j] < min) {
						min = glyphDensity[i][j];
					}
					if (glyphDensity[i][j] > max) {
						max = glyphDensity[i][j];
					}
				}
			}

			for (int i = 32; i < 128; ++i) {
				if (glyphDensity[i] == null) {
					continue;
				}
				for (int j = 0; j < glyphDensity[i].length; ++j) {
					glyphDensity[i][j] -= min;
					glyphDensity[i][j] /= max - min;
				}
			}
		}

		BufferedImage img2 = sanitizeImage(img, outputWidth);

		int newWidth = img2.getWidth();
		int newHeight = img2.getHeight();

		double ratio = newWidth / (double) outputWidth;

		// Adjust the ratio by the glyph ratio so we get proportional ascii art
		double yRatio = ratio / (fm.getMaxAdvance() / (double) fm.getHeight());

		// calculate how many pixels across x a glyph covers (xCount)
		// then determine the x start offset (xOff)
		int xCount = (int) ratio;

		// calculate how many pixels across y a glyph covers (yCount)
		// then determine the y start offset (yOff)
		int ySize = (int) (newHeight / yRatio);
		// it likes to cut off the bottom, despite my best efforts :(
		int yCount = (int) yRatio;

		StringBuilder buf = new StringBuilder();
		for (int y = 0; y < ySize; ++y) {
			for (int x = 0; x < outputWidth; ++x) {
				img = img2.getSubimage(x * xCount, y * yCount, xCount, yCount);
				double[] imageDensity = split9(img);

				double nearest = Double.POSITIVE_INFINITY;
				int best = 32;
				// compare to all density
				for (int i = 32; i < 128; ++i) {
					if (glyphDensity[i] == null) {
						continue;
					}

					double avg = 0;
					for (int j = 0; j < imageDensity.length; ++j) {
						double k = glyphDensity[i][j] - imageDensity[j];
						k = k * k;
						k = k * k;
						k = k * k;
						avg += k * k;
					}

					if (avg < nearest) {
						nearest = avg;
						best = i;
					}
				}
				buf.append((char) best);

			}
			buf.append("\r\n");
		}

		return buf.toString();
	}

	/**
	 * Older (but refactored) version of the AAG Ascii Art Algorithm.
	 */
	public static final String generateDensityAsciiArt(BufferedImage img,
			int outputWidth) {
		double[] glyphDensity = new double[128];

		// generate density map
		{

			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;

			// generate density map
			for (int i = 32; i < 128; ++i) {
				glyphDensity[i] = getGlyphDensity(i);
				if (glyphDensity[i] == Double.NaN) {
					continue;
				}
				if (glyphDensity[i] < min) {
					min = glyphDensity[i];
				}
				if (glyphDensity[i] > max) {
					max = glyphDensity[i];
				}
			}
			for (int i = 32; i < 128; ++i) {
				if (glyphDensity[i] == Double.NaN) {
					continue;
				}
				glyphDensity[i] -= min;
				glyphDensity[i] /= max - min;
			}
		}

		BufferedImage img2 = sanitizeImage(img, outputWidth);

		int newWidth = img2.getWidth();
		int newHeight = img2.getHeight();

		double ratio = newWidth / (double) outputWidth;

		// Adjust the ratio by the glyph ratio so we get proportional ascii art
		double yRatio = ratio / (fm.getMaxAdvance() / (double) fm.getHeight());

		// calculate how many pixels across x a glyph covers (xCount)
		// then determine the x start offset (xOff)
		int xCount = (int) ratio;

		// calculate how many pixels across y a glyph covers (yCount)
		// then determine the y start offset (yOff)
		int ySize = (int) (newHeight / yRatio);
		// it likes to cut off the bottom, despite my best efforts :(
		int yCount = (int) yRatio;

		StringBuilder buf = new StringBuilder();
		for (int y = 0; y < ySize; ++y) {
			for (int x = 0; x < outputWidth; ++x) {
				img = img2.getSubimage(x * xCount, y * yCount, xCount, yCount);

				int count = 0;
				int[] data = img.getRaster().getSamples(0, 0, xCount, yCount,
						0, (int[]) null);
				for (int d : data) {
					count += d;
				}
				double imageDensity = count / (data.length * 255.0);

				double nearest = Double.POSITIVE_INFINITY;
				int best = 32;
				// compare to all density
				for (int i = 32; i < 128; ++i) {
					if (glyphDensity[i] == Double.NaN) {
						continue;
					}

					double dist = Math.abs(glyphDensity[i] - imageDensity);
					if (dist < nearest) {
						nearest = dist;
						best = i;
					}
				}
				buf.append((char) best);
			}
			buf.append("\r\n");
		}

		return buf.toString();
	}

	public static final String generateHybridAsciiArt(BufferedImage img,
			int outputWidth) {
		double[][] glyphDensity = new double[128][];
		double[] glyphDensitySimple = new double[128];

		// generate density map
		{

			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;
			for (int i = 32; i < 128; ++i) {

				glyphDensity[i] = getGlyphAreaDensity9(i);
				if (glyphDensity[i] == null) {
					continue;
				}

				for (int j = 0; j < glyphDensity[i].length; ++j) {
					if (glyphDensity[i][j] < min) {
						min = glyphDensity[i][j];
					}
					if (glyphDensity[i][j] > max) {
						max = glyphDensity[i][j];
					}
				}
			}
			for (int i = 32; i < 128; ++i) {
				if (glyphDensity[i] == null) {
					continue;
				}
				for (int j = 0; j < glyphDensity[i].length; ++j) {
					glyphDensity[i][j] -= min;
					glyphDensity[i][j] /= max - min;
				}
			}

			min = Double.POSITIVE_INFINITY;
			max = Double.NEGATIVE_INFINITY;

			// generate density map
			for (int i = 32; i < 128; ++i) {
				glyphDensitySimple[i] = getGlyphDensity(i);
				if (glyphDensitySimple[i] == Double.NaN) {
					continue;
				}
				if (glyphDensitySimple[i] < min) {
					min = glyphDensitySimple[i];
				}
				if (glyphDensitySimple[i] > max) {
					max = glyphDensitySimple[i];
				}
			}
			for (int i = 32; i < 128; ++i) {
				if (glyphDensitySimple[i] == Double.NaN) {
					continue;
				}
				glyphDensitySimple[i] -= min;
				glyphDensitySimple[i] /= max - min;
			}
		}

		BufferedImage img2 = sanitizeImage(img, outputWidth);

		int newWidth = img2.getWidth();
		int newHeight = img2.getHeight();

		double ratio = newWidth / (double) outputWidth;

		// Adjust the ratio by the glyph ratio so we get proportional ascii art
		double yRatio = ratio / (fm.getMaxAdvance() / (double) fm.getHeight());

		// calculate how many pixels across x a glyph covers (xCount)
		// then determine the x start offset (xOff)
		int xCount = (int) ratio;

		// calculate how many pixels across y a glyph covers (yCount)
		// then determine the y start offset (yOff)
		int ySize = (int) (newHeight / yRatio);
		// it likes to cut off the bottom, despite my best efforts :(
		int yCount = (int) yRatio;

		StringBuilder buf = new StringBuilder();
		for (int y = 0; y < ySize; ++y) {
			for (int x = 0; x < outputWidth; ++x) {
				img = img2.getSubimage(x * xCount, y * yCount, xCount, yCount);
				double[] imageDensity = split9(img);

				int count = 0;
				int[] data = img.getRaster().getSamples(0, 0, xCount, yCount,
						0, (int[]) null);
				for (int d : data) {
					count += d;
				}
				double simpleImageDensity = count / (data.length * 255.0);

				double nearest = Double.POSITIVE_INFINITY;
				int best = 32;
				// compare to all density
				for (int i = 32; i < 128; ++i) {
					if (glyphDensity[i] == null) {
						continue;
					}

					// glyphDensitySimple

					double avg = 0;
					for (int j = 0; j < imageDensity.length; ++j) {
						avg += Math.abs(glyphDensity[i][j] - imageDensity[j]);
					}
					avg /= imageDensity.length;
					avg += 0.5 * Math.abs(glyphDensitySimple[i]
							- simpleImageDensity);

					if (avg < nearest) {
						nearest = avg;
						best = i;
					}
				}
				buf.append((char) best);

			}
			buf.append("\r\n");
		}

		return buf.toString();
	}

	private static final double[] getGlyphAreaDensity9(int glyph) {
		if (!font.canDisplay(glyph)) {
			return null;
		}

		int w = fm.getMaxAdvance();
		int h = fm.getHeight();

		BufferedImage img = new BufferedImage(w, h,
				BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D gx = img.createGraphics();
		gx.setBackground(Color.WHITE);
		gx.clearRect(0, 0, w, h);
		gx.setColor(Color.BLACK);

		gx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		// create
		GlyphVector gv = font.createGlyphVector(gx.getFontRenderContext(),
				new char[] { (char) glyph });
		Rectangle2D lb = gv.getLogicalBounds();

		gx.drawGlyphVector(gv, (float) -lb.getX(), fm.getAscent());

		double[] split = split9(img);

		img.flush();

		return split;
	}

	private static final double getGlyphDensity(int glyph) {
		if (!font.canDisplay(glyph)) {
			return Double.NaN;
		}

		int w = fm.getMaxAdvance();
		int h = fm.getHeight();

		BufferedImage img = new BufferedImage(w, h,
				BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D gx = img.createGraphics();
		gx.setBackground(Color.WHITE);
		gx.clearRect(0, 0, w, h);
		gx.setColor(Color.BLACK);

		gx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		// create
		GlyphVector gv = font.createGlyphVector(gx.getFontRenderContext(),
				new char[] { (char) glyph });
		Rectangle2D lb = gv.getLogicalBounds();

		gx.drawGlyphVector(gv, (float) -lb.getX(), fm.getAscent());

		double data = 0;
		for (int d : img.getRaster().getSamples(0, 0, w, h, 0, (int[]) null)) {
			data += d;
		}

		data /= w * h * 255;

		img.flush();

		return data;
	}

	public static void main(String[] args) throws Exception {
		FileWriter out;

		BufferedImage img = ImageIO.read(new File("test.png"));

		out = new FileWriter("g0.txt");
		out.write(generateDensityAsciiArt(img, 80));
		out.close();

		System.out.println("A");

		out = new FileWriter("g1.txt");
		out.write(generateAreaDensityAsciiArt(img, 80));
		out.close();

		System.out.println("B");

		out = new FileWriter("g2.txt");
		out.write(generateHybridAsciiArt(img, 80));
		out.close();

		System.out.println("C");
	}

	/**
	 * TODO refactor this method
	 * 
	 * @param image
	 * @param outputWidth
	 * @return
	 */
	private static final BufferedImage sanitizeImage(BufferedImage image,
			int outputWidth) {
		int fontHeight = fm.getHeight();
		int fontAdv = fm.getMaxAdvance();

		int newWidth = fontAdv * outputWidth;
		int newHeight = 0;
		double tmpHeight = image.getHeight()
				* (newWidth / (double) image.getWidth());
		double bestDistance = Double.POSITIVE_INFINITY;
		for (double ys = 0; ys < (tmpHeight * 2); ys += fontHeight) {
			double dist = Math.abs(tmpHeight - ys);
			if (dist < bestDistance) {
				bestDistance = dist;
				newHeight = (int) (ys + 0.5);
			}
		}

		BufferedImage reImage = new BufferedImage(newWidth, newHeight,
				BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D gx = reImage.createGraphics();
		gx.setBackground(Color.WHITE);
		gx.clearRect(0, 0, newWidth, newHeight);
		gx.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		gx.drawImage(image, 0, 0, newWidth, newHeight, null);

		image.flush();

		return reImage;
	}

	private static final double[] split9(BufferedImage img) {
		double[] data = new double[9];

		int w = img.getWidth();
		int h = img.getHeight();
		double ws = w / 3.0;
		double hs = h / 3.0;

		int[] samples = img.getRaster().getSamples(0, 0, w, h, 0, (int[]) null);
		int[] count = new int[9];

		for (int i = 0; i < samples.length; ++i) {
			int index = ((int) ((i / w) / hs) * 3) + ((int) ((i % w) / ws) % 3);
			data[index] += samples[i];
			count[index] += 255;
		}

		for (int i = 0; i < data.length; ++i) {
			data[i] /= count[i];
		}

		return data;
	}
}
