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
package org.csdgn.maru.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.util.ArrayList;

/**
 * A layout manager that handles all children as though they were the CENTER in
 * a BorderLayout. This is mainly only useful for JLayeredPanes.
 * 
 * @author Robert Maupin
 */
public class StackedLayout implements LayoutManager, LayoutManager2 {
	private ArrayList<Component> components = new ArrayList<Component>();

	@Override
	public void addLayoutComponent(String name, Component comp) {
		synchronized (comp.getTreeLock()) {
			components.add(comp);
		}
	}

	@Override
	public void layoutContainer(Container target) {
		synchronized (target.getTreeLock()) {
			Insets insets = target.getInsets();
			int top = insets.top;
			int bottom = target.getHeight() - insets.bottom;
			int left = insets.left;
			int right = target.getWidth() - insets.right;

			for (Component c : components) {
				c.setBounds(left, top, right - left, bottom - top);
			}
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container target) {
		synchronized (target.getTreeLock()) {
			Dimension dim = new Dimension(0, 0);
			for (Component c : components) {
				Dimension d = c.getMinimumSize();
				dim.width = Math.max(d.width, dim.width);
				dim.height = Math.max(d.height, dim.height);
			}
			Insets insets = target.getInsets();
			dim.width += insets.left + insets.right;
			dim.height += insets.top + insets.bottom;
			return dim;
		}
	}

	@Override
	public Dimension preferredLayoutSize(Container target) {
		synchronized (target.getTreeLock()) {
			Dimension dim = new Dimension(0, 0);
			for (Component c : components) {
				Dimension d = c.getPreferredSize();
				dim.width = Math.max(d.width, dim.width);
				dim.height = Math.max(d.height, dim.height);
			}
			Insets insets = target.getInsets();
			dim.width += insets.left + insets.right;
			dim.height += insets.top + insets.bottom;
			return dim;
		}
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		synchronized (comp.getTreeLock()) {
			components.remove(comp);
		}
	}

	@Override
	public void addLayoutComponent(Component c, Object o) {
		//we honestly don't care what was passed
		addLayoutComponent((String)null,c);
	}

	@Override
	public float getLayoutAlignmentX(Container c) {
		return 0.5f;
	}

	@Override
	public float getLayoutAlignmentY(Container c) {
		return 0.5f;
	}

	@Override
	public void invalidateLayout(Container c) {
		//done
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		synchronized (target.getTreeLock()) {
			Dimension dim = new Dimension(0, 0);
			for (Component c : components) {
				Dimension d = c.getMaximumSize();
				dim.width = Math.max(d.width, dim.width);
				dim.height = Math.max(d.height, dim.height);
			}
			Insets insets = target.getInsets();
			dim.width += insets.left + insets.right;
			dim.height += insets.top + insets.bottom;
			return dim;
		}
	}
}
