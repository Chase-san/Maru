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
package org.csdgn.maru.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A specialized set that extends both list and set. I don't remember if I
 * actually got this to work.
 * 
 * @author Robert Maupin
 * 
 * @param <E>
 */
public class ListHashSet<E> implements List<E>, Set<E> {
	/**
	 * An optimized version of AbstractList.Itr
	 */
	private class Itr implements Iterator<E> {
		int cursor; // index of next element to return
		int lastRet = -1; // index of last element returned; -1 if no such

		@Override
		public boolean hasNext() {
			return cursor != list.size();
		}

		@Override
		public E next() {
			final int i = cursor;
			if (i >= list.size()) {
				throw new NoSuchElementException();
			}
			cursor = i + 1;
			return ListHashSet.this.get(lastRet = i);
		}

		@Override
		public void remove() {
			if (lastRet < 0) {
				throw new IllegalStateException();
			}
			try {
				ListHashSet.this.remove(lastRet);
				cursor = lastRet;
				lastRet = -1;
			} catch (final IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}
	}

	/**
	 * An optimized version of AbstractList.ListItr
	 */
	private class ListItr extends Itr implements ListIterator<E> {
		ListItr(final int index) {
			super();
			cursor = index;
		}

		@Override
		public void add(final E e) {
			try {
				final int i = cursor;
				ListHashSet.this.add(i, e);
				cursor = i + 1;
				lastRet = -1;
			} catch (final IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}

		@Override
		public boolean hasPrevious() {
			return cursor != 0;
		}

		@Override
		public int nextIndex() {
			return cursor;
		}

		@Override
		public E previous() {
			final int i = cursor - 1;
			if (i < 0) {
				throw new NoSuchElementException();
			}
			cursor = i;
			return ListHashSet.this.get(lastRet = i);
		}

		@Override
		public int previousIndex() {
			return cursor - 1;
		}

		@Override
		public void set(final E e) {
			if (lastRet < 0) {
				throw new IllegalStateException();
			}
			try {
				ListHashSet.this.set(lastRet, e);
			} catch (final IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}
	}

	private final List<E> list = new ArrayList<E>();
	private final Set<E> set = new HashSet<E>();

	public ListHashSet() {
		super();
	}

	@Override
	public boolean add(final E o) {
		if (this.set.add(o)) {
			return this.list.add(o);
		} else {
			return false;
		}
	}

	@Override
	public void add(final int index, final E element) {
		if (this.set.add(element)) {
			list.add(index, element);
		}
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		boolean changed = false;
		final Iterator<? extends E> i = c.iterator();
		while (i.hasNext()) {
			final E element = i.next();
			if (this.add(element)) {
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends E> c) {
		boolean changed = false;
		int insertIndex = index;
		final Iterator<? extends E> i = c.iterator();
		while (i.hasNext()) {
			final E element = i.next();
			if (this.set.add(element)) {
				this.list.add(insertIndex++, element);
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public void clear() {
		this.set.clear();
		this.list.clear();
	}

	@Override
	public boolean contains(final Object o) {
		return this.set.contains(o);
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return this.set.containsAll(c);
	}

	@Override
	public boolean equals(final Object other) {
		return (other instanceof ListHashSet)
				&& this.list.equals(((ListHashSet<?>) other).list);
	}

	@Override
	public E get(final int index) {
		return this.list.get(index);
	}

	@Override
	public int hashCode() {
		return this.list.hashCode();
	}

	@Override
	public int indexOf(final Object o) {
		return this.list.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return this.set.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return new Itr();
	}

	@Override
	public int lastIndexOf(final Object o) {
		return this.list.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return new ListItr(0);
	}

	@Override
	public ListIterator<E> listIterator(final int index) {
		if ((index < 0) || (index > list.size())) {
			throw new IndexOutOfBoundsException("Index: " + index);
		}
		return new ListItr(index);
	}

	@Override
	public E remove(final int index) {
		final E element = this.list.remove(index);
		if (element != null) {
			this.set.remove(element);
		}
		return element;
	}

	@Override
	public boolean remove(final Object o) {
		if (this.set.remove(o)) {
			this.list.remove(o);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		if (this.set.removeAll(c)) {
			this.list.removeAll(c);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		if (this.set.retainAll(c)) {
			this.list.retainAll(c);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public E set(final int index, final E element) {
		this.set.add(element);
		return this.list.set(index, element);
	}

	@Override
	public int size() {
		return this.list.size();
	}

	@Override
	public List<E> subList(final int fromIndex, final int toIndex) {
		return this.list.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return this.list.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return this.list.toArray(a);
	}
}
