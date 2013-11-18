package org.csdgn.maru.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.RepaintManager;
import javax.swing.colorchooser.ColorSelectionModel;

public class HSVColorChooser extends JComponent {
	private static final long serialVersionUID = 8120837343231749556L;
	
	
	

	private static class InnerColorPanel extends JComponent implements MouseListener, MouseMotionListener {
		private static final long serialVersionUID = -2306950947022166145L;
		private static final float TRI_WIDTH_MULT = 0.65f;
		private static final float TRI_HEIGHT_MULT = 0.415f;
		
		private static final Cursor COLOR_SELECT_CURSOR;
		static {
			BufferedImage cImg = new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
			Graphics2D gx = cImg.createGraphics();
			gx.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			gx.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			gx.setColor(Color.BLACK);
			gx.drawOval(0, 0, 14, 14);
			//okay, just drawing another oval is failing badly
			for(int y=0;y<16;++y) {
				int last = 0;
				for(int x=0;x<16;++x) {
					int cur = cImg.getRGB(x, y);
					if(x < 8 && last == 0xFF000000 && cur == 0) {
						cImg.setRGB(x, y, Color.WHITE.getRGB());
					}
					last = cur;
				}
				last = 0;
				for(int x=16;x>=0;--x) {
					int cur = cImg.getRGB(x, y);
					if(x > 7 && last == 0xFF000000 && cur == 0) {
						cImg.setRGB(x, y, Color.WHITE.getRGB());
					}
					last = cur;
				}
			}
			for(int x=0;x<16;++x) {
				int last = 0;
				for(int y=0;y<32;++y) {
					int cur = cImg.getRGB(x, y);
					if(y < 8 && last == 0xFF000000 && cur == 0) {
						cImg.setRGB(x, y, Color.WHITE.getRGB());
					}
					last = cur;
				}
				last = 0;
				for(int y=16;y>=0;--y) {
					int cur = cImg.getRGB(x, y);
					if(y > 7 && last == 0xFF000000 && cur == 0) {
						cImg.setRGB(x, y, Color.WHITE.getRGB());
					}
					last = cur;
				}
			}
			//gx.setColor(Color.WHITE);
			//gx.drawOval(1, 1, 12, 12);
			COLOR_SELECT_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(cImg, new Point(7,7), null);
		}
		
		private Point position;
		private float hue;
		
		private Dimension size;
		private BufferedImage image;
		public InnerColorPanel(Dimension size) {
			this.size = size;
			image = new BufferedImage(size.width,size.height,BufferedImage.TYPE_INT_RGB);
			position = new Point();
			addMouseListener(this);
			addMouseMotionListener(this);
			
			setCursor(COLOR_SELECT_CURSOR);
		}
		public void setHue(float hue) {
			this.hue = hue;
			for(int y=0;y<size.height;++y) {
				for(int x=0;x<size.width;++x) {
					float sat = x/(float)size.width;
					float val = 1-y/(float)size.height;
					image.setRGB(x, y, Color.getHSBColor(hue, sat, val).getRGB());
				}
			}
			markDirty();
		}
		private boolean isCursorBlack(Point p) {
			float width = size.width * TRI_WIDTH_MULT;
			float height = size.height * TRI_HEIGHT_MULT;
			return height * p.x + width * p.y - width * height < 0;
		}
		
		public void setColor(Color color) {
			float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
			setHue(hsv[0]);
			position.x = (int) (hsv[1]*size.width);
			position.y = (int) ((1-hsv[2])*size.height);
		}
		
		/**
		 * If you called setColor this may not return the same color provided.
		 */
		public Color getColor() {
			float sat = position.x/(float)size.width;
			float val = 1-position.y/(float)size.height;
			return Color.getHSBColor(hue, sat, val);
		}
		
		public Dimension getPreferredSize() {
			return size;
		}
		public Dimension getMaximumSize() {
			return size;
		}
		public Dimension getMinimumSize() {
			return size;
		}
		public Dimension getSize() {
			return size;
		}
		public void paintComponent(Graphics g) {
			g.drawImage(image,0,0,null);
			if(isCursorBlack(position)) {
				g.setColor(Color.BLACK);
			} else {
				g.setColor(Color.WHITE);
			}
			boundCursor();
			g.drawOval(position.x - 5, position.y - 5, 10, 10);
		}
		
		private void markDirty() {
			RepaintManager.currentManager(this).markCompletelyDirty(this);
		}
		
		private void boundCursor() {
			position.x = Math.max(0, Math.min(position.x, size.width));
			position.y = Math.max(0, Math.min(position.y, size.height));
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			position = e.getPoint();
			markDirty();
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			position = e.getPoint();
			markDirty();
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			
		}
		@Override
		public void mouseExited(MouseEvent e) {
			
		}
		@Override
		public void mousePressed(MouseEvent e) {
			position = e.getPoint();
			markDirty();
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			
		}
	}
	
	private static class InnerHuePanel extends JComponent {
		private static final long serialVersionUID = 447312803642100019L;
		
		private Dimension size;
		private BufferedImage image;
		
		public InnerHuePanel(Dimension size) {
			this.size = size;
			image = new BufferedImage(size.width,size.height,BufferedImage.TYPE_INT_RGB);
			for(int y=0;y<size.height;++y) {
				float hue = 1-y/(float)size.height;
				for(int x=0;x<size.width;++x) {
					image.setRGB(x, y, Color.getHSBColor(hue, 1, 1).getRGB());
				}
			}
		}
		
		public Dimension getPreferredSize() {
			return size;
		}
		public Dimension getMaximumSize() {
			return size;
		}
		public Dimension getMinimumSize() {
			return size;
		}
		public Dimension getSize() {
			return size;
		}
		public void paintComponent(Graphics g) {
			g.drawImage(image,0,0,null);
		}
	}
	
	public HSVColorChooser() {
		initialize();
	}
	
	public HSVColorChooser(Color initialColor) {
		initialize();
	}
	
	public HSVColorChooser(ColorSelectionModel model) {
		initialize();
	}
	
	private InnerColorPanel cpanel;
	private InnerHuePanel hpanel;
	
	private void initialize() {
		setLayout(new FlowLayout());
		
		cpanel = new InnerColorPanel(new Dimension(256,256));
		cpanel.setColor(Color.RED);
		add(cpanel);
		
		hpanel = new InnerHuePanel(new Dimension(24,256));
		add(hpanel);
		
		
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Test Frame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new HSVColorChooser());
		
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}
}
