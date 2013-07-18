package org.csdgn.maru.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ByteArrayUtils {
	public static byte[] add(byte[] array, byte element) {
		if (array == null) {
			return null;
		}
		if (array.length == 0) {
			return new byte[0];
		}
		final byte[] array2 = new byte[array.length + 1];
		System.arraycopy(array, 0, array2, 0, array.length);
		array2[array.length] = element;
		return array2;
	}

	public static final Iterator<Byte> byteArrayIterator(final byte[] array) {
		// Ensure the error is found as soon as possible.
		if (array == null) {
			return null;
		}
		return new Iterator<Byte>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < array.length;
			}

			@Override
			public Byte next() {
				/*
				 * Throw NoSuchElementException as defined by the Iterator
				 * contract, not IndexOutOfBoundsException.
				 */
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return array[index++];
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public static byte[] clone(byte[] array) {
		if (array == null) {
			return null;
		}
		if (array.length == 0) {
			return new byte[0];
		}
		final byte[] array2 = new byte[array.length];
		System.arraycopy(array, 0, array2, 0, array.length);
		return array2;
	}

	public static byte[] concat(byte[] array1, byte[] array2) {
		if (array1 == null) {
			return clone(array2);
		}
		if (array1.length == 0) {
			return clone(array2);
		}
		if (array2 == null) {
			return clone(array1);
		}
		if (array2.length == 0) {
			return clone(array1);
		}
		final byte[] array3 = new byte[array1.length + array2.length];
		System.arraycopy(array1, 0, array3, 0, array1.length);
		System.arraycopy(array2, 0, array3, array1.length, array2.length);
		return array3;
	}

	public static boolean contains(byte[] array, byte value) {
		return indexOf(array, value) != -1;
	}

	public static boolean contains(byte[] array, byte[] value) {
		return indexOf(array, value) != -1;
	}

	public static int indexOf(byte[] array, byte value) {
		return indexOf(array, value, 0);
	}

	public static int indexOf(byte[] array, byte value, int start) {
		if (array == null) {
			return -1;
		}
		if (array.length == 0) {
			return -1;
		}
		if (start < 0) {
			start = 0;
		}
		for (int i = start; i < array.length; ++i) {
			if (value == array[i]) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(byte[] array, byte[] value) {
		return indexOf(array, value, 0);
	}

	public static int indexOf(byte[] array, byte[] value, int start) {
		if (array == null || value == null) {
			return -1;
		}
		if (array.length == 0 || value.length == 0) {
			return -1;
		}
		if (start < 0) {
			start = 0;
		}

		loop: for (int i = start; i < array.length - value.length + 1; ++i) {
			for (int j = 0; j < value.length; j++) {
				if (array[i + j] != value[j]) {
					continue loop;
				}
			}
			return i;
		}
		return -1;
	}

	public static int lastIndexOf(byte[] array, byte value) {
		return lastIndexOf(array, value, 0);
	}

	public static int lastIndexOf(byte[] array, byte value, int start) {
		if (array == null) {
			return -1;
		}
		if (array.length == 0) {
			return -1;
		}
		if (start < 0) {
			start = 0;
		}
		for (int i = array.length - 1; i >= start; --i) {
			if (value == array[i]) {
				return i;
			}
		}
		return -1;
	}

	public static int lastIndexOf(byte[] array, byte[] value) {
		return lastIndexOf(array, value, 0);
	}

	public static int lastIndexOf(byte[] array, byte[] value, int start) {
		if (array == null || value == null) {
			return -1;
		}
		if (array.length == 0 || value.length == 0) {
			return -1;
		}
		if (start < 0) {
			start = 0;
		}

		loop: for (int i = array.length - value.length + 1; i >= start; --i) {
			for (int j = 0; j < value.length; j++) {
				if (array[i + j] != value[j]) {
					continue loop;
				}
			}
			return i;
		}

		return -1;
	}

	public static byte[] push(byte[] array, byte element) {
		if (array == null) {
			return null;
		}
		final byte[] array2 = new byte[array.length + 1];
		System.arraycopy(array, 0, array2, 1, array.length);
		array2[0] = element;
		return array2;
	}

	public static byte[] reverse(byte[] array) {
		final byte[] array2 = new byte[array.length];
		int index = 0;
		for (int i = array.length - 1; i >= 0; --i) {
			array2[index++] = array[i];
		}
		return array2;
	}

	public static byte[] subarray(byte[] array, int start) {
		if (array == null) {
			return null;
		}
		return subarray(array, start, array.length);
	}

	/**
	 * [start,end)
	 * 
	 * @param start
	 *            Inclusive
	 * @param end
	 *            Exclusive
	 */
	public static byte[] subarray(byte[] array, int start, int end) {
		if (array == null) {
			return null;
		}
		if (start < 0) {
			start = 0;
		}
		if (end > array.length) {
			end = array.length;
		}
		final int newSize = end - start;
		if (newSize <= 0) {
			return new byte[0];
		}
		final byte[] subarray = new byte[newSize];
		System.arraycopy(array, start, subarray, 0, newSize);
		return subarray;
	}

	public static byte[] wrap(byte element) {
		return new byte[] { element };
	}
}
