/**
 * Copyright (c) 2009-2013 Robert Maupin
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
import java.util.HashMap;

/**
 * A simple HTML like table layout for Java. No really!
 * 
 * @author Robert Maupin
 * @version 1.2
 */
public class TableLayout implements LayoutManager, LayoutManager2 {
	private class Cell extends TableLayoutConstraint {
		public Component comp = null;

		public Cell(TableLayoutConstraint stlc, Component comp) {
			super(stlc);
			this.comp = comp;
		}
	}

	private class Table {
		public Cell[] cells = null;
		public int[] rows = null;
		public int[] cols = null;
	}

	private HashMap<Component, TableLayoutConstraint> defined = new HashMap<Component, TableLayoutConstraint>();

	private int hgap = 0;
	private int vgap = 0;
	private boolean fill = true;

	/**
	 * Default arguments. No hgap, no vgap and to fill the cell.
	 */
	public TableLayout() {
	}

	/**
	 * @param fill
	 *            Fill the entire cell?
	 */
	public TableLayout(boolean fill) {
		this.fill = fill;
	}

	/**
	 * @param hgap
	 *            Horizontal Gap between cells
	 * @param vgap
	 *            Vertical Gap between cells
	 */
	public TableLayout(int hgap, int vgap) {
		this.hgap = hgap;
		this.vgap = vgap;
	}

	/**
	 * @param hgap
	 *            Horizontal Gap between cells
	 * @param vgap
	 *            Vertical Gap between cells
	 * @param fill
	 *            Fill the entire cell?
	 */
	public TableLayout(int hgap, int vgap, boolean fill) {
		this.hgap = hgap;
		this.vgap = vgap;
		this.fill = fill;
	}

	@Override
	public void addLayoutComponent(Component comp, Object obj) {
		TableLayoutConstraint cons = null;
		if (obj instanceof TableLayoutConstraint) {
			cons = (TableLayoutConstraint) obj;
		} else if (obj instanceof String) {
			cons = TableLayoutConstraint.createConstraints((String) obj);
		}
		if (cons == null) {
			throw new IllegalArgumentException(
					"Object must be a String or SimpleTableLayoutConstraint.");
		}

		defined.put(comp, cons.clone());
	}

	/**
	 * @param name
	 *            Text to use to set the constraints.
	 * @param comp
	 *            The component to set the contraints on.
	 * @see TableLayoutConstraint#createConstraints(String)
	 */
	@Override
	public void addLayoutComponent(String name, Component comp) {
		/*
		 * Easy enough!
		 */
		addLayoutComponent(comp, TableLayoutConstraint.createConstraints(name));
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override
	public void invalidateLayout(Container target) {
		// TODO
	}

	@Override
	public void layoutContainer(Container parent) {
		Insets insets = parent.getInsets();

		Table table = layoutTable(parent);

		/*
		 * Layout the components using the cell and row/col data
		 */
		for (Cell cell : table.cells) {
			/*
			 * Determine the size and offset of the cell!
			 */
			int x = insets.left + cell.x * hgap;
			int y = insets.top + cell.y * vgap;
			int width = 0;
			int height = 0;

			for (int i = 0; i < cell.x; ++i) {
				x += table.cols[i];
			}

			for (int i = 0; i < cell.y; ++i) {
				y += table.rows[i];
			}

			if (cell.colspan != 1) {
				for (int i = 0; i < cell.colspan; ++i) {
					width += table.cols[cell.x + i];
				}
				width += (cell.colspan - 1) * hgap;
			} else {
				width = table.cols[cell.x];
			}

			if (cell.rowspan != 1) {
				for (int i = 0; i < cell.rowspan; ++i) {
					height += table.rows[cell.y + i];
				}
				height += (cell.rowspan - 1) * vgap;
			} else {
				height = table.rows[cell.y];
			}

			if (fill) {
				cell.comp.setBounds(x, y, width, height);
			} else {
				/*
				 * Align the element inside the cell according to its alignment
				 * x/y and size
				 */
				Dimension dim = cell.comp.getPreferredSize();

				float xalign = cell.comp.getAlignmentX();
				float yalign = cell.comp.getAlignmentY();

				width -= dim.width;
				height -= dim.height;

				x += width * xalign;
				y += height * yalign;

				cell.comp.setBounds(x, y, dim.width, dim.height);
			}
		}
	}

	/**
	 * Do the actual calculation of the table size and all!
	 * 
	 * 
	 */
	private Table layoutTable(Container parent) {
		Table table = new Table();
		ArrayList<Cell> cells = new ArrayList<Cell>();
		Component[] cs = parent.getComponents();

		int maxX = 0;
		int maxY = 0;
		Cell last = null;
		for (Component comp : cs) {
			Cell cell = new Cell(defined.get(comp), comp);

			/*
			 * the only thing we touch here is the x/y (ergo col/row)
			 */
			if (cell.x == -1) {
				cell.x = 0;
				if (last != null) {
					cell.x = last.x + last.colspan - 1 + cell.offsetX;
				}
			}

			if (cell.y == -1) {
				cell.y = 0;
				if (last != null) {
					cell.y = last.y + last.rowspan - 1 + cell.offsetY;
				}
			}

			/*
			 * This is used to determine the number of columns and rows we will
			 * have in the end.
			 */
			if (cell.x + cell.colspan - 1 > maxX) {
				maxX = cell.x + cell.colspan - 1;
			}

			if (cell.y + cell.rowspan - 1 > maxY) {
				maxY = cell.y + cell.rowspan - 1;
			}

			cells.add(last = cell);
		}

		/*
		 * Go through our cells and determine the size of each row and column.
		 */
		int[] rows = new int[maxY + 1];
		for (int i = 0; i < rows.length; ++i) {
			rows[i] = 0;
		}

		int[] cols = new int[maxX + 1];
		for (int i = 0; i < cols.length; ++i) {
			cols[i] = 0;
		}

		/*
		 * Use the even distributed span cell to get this in one go.
		 * 
		 * If the total size of the two combined rows/columns can already
		 * accommodate it, do not increase the size of any of them!
		 * 
		 * XXX: This bit of code is a can of worms really. There is no perfect
		 * way to do it really, so I tried to imitate HTML tables as close as I
		 * care to. Unfortunately those documents say it can be done any number
		 * of ways, so WTH right?
		 */
		for (Cell cell : cells) {
			Dimension dim = cell.comp.getPreferredSize();
			if (cell.colspan != 1) {
				// Total size of the columns spanned
				int total = 0;
				for (int i = 0; i < cell.colspan; ++i) {
					total += cols[cell.x + i];
				}
				if (total < dim.width) {
					// Even distribute
					int width = dim.width / cell.colspan;
					for (int i = 0; i < cell.colspan; ++i) {
						if (cols[cell.x + i] < width) {
							cols[cell.x + i] = width;
						}
					}
				}
			} else {
				if (cols[cell.x] < dim.width) {
					cols[cell.x] = dim.width;
				}
			}

			if (cell.rowspan != 1) {
				// Total size of the rows spanned
				int total = 0;
				for (int i = 0; i < cell.rowspan; ++i) {
					total += rows[cell.y + i];
				}
				if (total < dim.height) {
					// Even distribute
					int height = dim.height / cell.rowspan;
					for (int i = 0; i < cell.rowspan; ++i) {
						if (rows[cell.y + i] < height) {
							rows[cell.y + i] = height;
						}
					}
				}
			} else {
				if (rows[cell.y] < dim.height) {
					rows[cell.y] = dim.height;
				}
			}
		}

		table.rows = rows;
		table.cols = cols;
		table.cells = cells.toArray(new Cell[cells.size()]);
		return table;
	}

	@Override
	public Dimension maximumLayoutSize(Container parent) {
		return parent.getMaximumSize();
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Insets insets = parent.getInsets();

		Table table = layoutTable(parent);

		int width = hgap * (table.cols.length - 1) + insets.left
				+ insets.right;
		int height = vgap * (table.rows.length - 1) + insets.top
				+ insets.bottom;

		/*
		 * Add up the size of the columns and rows
		 */
		for (int col : table.cols) {
			width += col;
		}

		for (int row : table.rows) {
			height += row;
		}

		return new Dimension(width, height);
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		defined.remove(comp);
	}
}
