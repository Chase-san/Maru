package org.csdgn.maru.util;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class QuadTree<K> {
	private static final int BUCKET_SIZE = 16;
	Rectangle2D bounds;
	Point2D[] points = new Point2D[BUCKET_SIZE];
	Object[] data = new Object[BUCKET_SIZE];
	int count = 0;
	
	QuadTree<K> nW;
	QuadTree<K> nE;
	QuadTree<K> sW;
	QuadTree<K> sE;
	
	public QuadTree(Rectangle2D bounds) {
		this.bounds = bounds;
	}
	
	public boolean add(double x, double y, K value) {
		return add(new Point2D.Double(x,y),value);
	}
	
	public boolean add(Point2D p, K value) {
		if(!bounds.contains(p))
			return false;
		if(count < points.length) {
			data[count] = value;
			points[count++] = p;
			return true;
		}
		if(nW == null)
			subdivide();
		
		if(nW.add(p,value))
			return true;
		if(nE.add(p,value))
			return true;
		if(sW.add(p,value))
			return true;
		if(sE.add(p,value))
			return true;
		
		return false;
	}
	
	public K get(double x, double y) {
		return get(new Point2D.Double(x,y));
	}
	
	@SuppressWarnings("unchecked")
	public K get(Point2D p) {
		if(!bounds.contains(p))
			return null;
		for(int i=0;i<count;++i) {
			Point2D q = points[i];
			if(p.equals(q)) {
				return (K)data[i];
			}
		}
		if(nW != null) {
			K k = nW.get(p);
			if(k != null)
				return k;
			k = nE.get(p);
			if(k != null)
				return k;
			k = sW.get(p);
			if(k != null)
				return k;
			k = sE.get(p);
			if(k != null)
				return k;
		}
		return null;
	}
	
	public ArrayList<Point2D> range(double x, double y, double w, double h) {
		return range(new Rectangle2D.Double(x,y,w,h));
	}
	
	public ArrayList<Point2D> range(Rectangle2D range) {
		ArrayList<Point2D> list = new ArrayList<Point2D>();
		
		if(!bounds.intersects(range))
			return list;
		
		for(int i = 0; i < count; ++i) {
			Point2D p = points[i];
			if(range.contains(p)) {
				list.add(p);
			}
		}
		
		if(nW == null)
			return list;
		
		list.addAll(nW.range(range));
		list.addAll(nE.range(range));
		list.addAll(sW.range(range));
		list.addAll(sE.range(range));
		
		return list;
	}
	
	private void subdivide() {
		//cut it into 4 parts
		nW = new QuadTree<K>(new Rectangle2D.Double(bounds.getX(),bounds.getY(),bounds.getWidth()/2,bounds.getHeight()/2));
		nE = new QuadTree<K>(new Rectangle2D.Double(bounds.getCenterX(),bounds.getY(),bounds.getWidth()/2,bounds.getHeight()/2));
		sW = new QuadTree<K>(new Rectangle2D.Double(bounds.getX(),bounds.getCenterY(),bounds.getWidth()/2,bounds.getHeight()/2));
		sE = new QuadTree<K>(new Rectangle2D.Double(bounds.getCenterX(),bounds.getCenterY(),bounds.getWidth()/2,bounds.getHeight()/2));
	}
}
