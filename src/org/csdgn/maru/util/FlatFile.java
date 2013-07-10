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
package org.csdgn.maru.util;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for a simple Flat File Database format.
 * The FlatFile class should not be used for serialization,
 * and only primitive or simple types should be used with it.
 * 
 * 
 * @requires {@link StringUtils}
 * @author Robert Maupin
 */
public class FlatFile {
	public static class Row {
		private final Object[] data;
		private Row(int columns) {
			data = new Object[columns];
		}
		private void setAll(Object[] data) {
			for(int i = 0; i < data.length && i < this.data.length; ++i)
				this.data[i] = data[i];
		}
		public int getSize() {
			return data.length;
		}
		public void set(int index, Object value) {
			data[index] = value;
		}
		public Object get(int index) {
			return data[index];
		}
		public String getString(int index) {
			if(data[index] == null)
				return "null";
			return data[index].toString();
		}
		public int getInteger(int index) throws NumberFormatException {
			return Integer.parseInt(getString(index));
		}
		public int getInteger(int index, int radix) throws NumberFormatException {
			return Integer.parseInt(getString(index),radix);
		}
		public double getDouble(int index) throws NumberFormatException {
			return Double.parseDouble(getString(index));
		}
	}
	
	private final int columns;
	private final List<Row> rows;
	
	public FlatFile(int columns) {
		this.columns = columns;
		rows = new ArrayList<Row>();
	}
	
	/**
	 * Any value you add will be converted to a string by the getString() method, if written to file.
	 * @param row
	 */
	public void addRow(Object[] row) {
		Row nr = new Row(columns);
		nr.setAll(row);
		rows.add(nr);
	}
	
	public Row removeRow(int index) {
		return rows.remove(index);
	}
	
	public Row getRow(int index) {
		return rows.get(index);
	}
	
	public Object getValue(int row, int column) {
		return rows.get(row).get(column);
	}
	
	public String getString(int row, int column) {
		return rows.get(row).getString(column);
	}
	public int getInteger(int row, int column) throws NumberFormatException {
		return rows.get(row).getInteger(column);
	}
	public int getInteger(int row, int column, int radix) throws NumberFormatException {
		return rows.get(row).getInteger(column, radix);
	}
	public double getDouble(int row, int column) throws NumberFormatException {
		return rows.get(row).getDouble(column);
	}
	
	/**
	 * This method returns the number of rows.
	 * @return
	 */
	public int getSize() {
		return rows.size();
	}
	
	/**
	 * This method does not flush or close the stream.
	 */
	public void write(Writer writer) {
		PrintWriter pw = new PrintWriter(writer);
		//they can put their own header on it if they want
		//pretty much all flat file DB are the same anyway
		//which is pretty surprising since there is no standard
		//pw.print("CFFDB 1.0\n");
		
		//write the number of rows
		pw.print(getSize());
		pw.print('\t');
		pw.print(columns);
		pw.print('\n');
		
		{
			String tmp;
			for(Row row : rows) {
				boolean first = true;
				for(Object obj : row.data) {
					if(!first)
						pw.print('\t');
					tmp = StringUtils.escape(String.valueOf(obj), false);
					pw.print(tmp);
					first = false;
				}
				pw.print('\n');
			}
		}
	}
	
	/**
	 * Loads a flat file database.
	 */
	public static FlatFile load(Reader reader) throws IOException {
		//header stuff
		int rows = Integer.parseInt(readToken(reader));
		int columns = Integer.parseInt(readToken(reader));
		
		FlatFile ff = new FlatFile(columns);
		Object[] data = new Object[columns];
		
		//first row
		for(int r = 0; r < rows; ++r) {
			for(int c = 0; c < columns; ++c)
				data[c] = StringUtils.unescape(readToken(reader));
			ff.addRow(data);
		}
		
		return ff;
	}
	
	private static final String readToken(Reader reader) throws IOException {
		//why use a StreamTokenizer when I don't need to do any COMPLEX tokenization
		StringBuilder sb = new StringBuilder();
		int r = -1;
		while((r = reader.read()) != -1) {
			if(r == '\t' || r == '\n')
				break;
			sb.append((char)r);
		}
		
		return sb.toString();
	}
}