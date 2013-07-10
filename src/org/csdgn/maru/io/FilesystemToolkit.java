package org.csdgn.maru.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;

/**
 * A number of useful static methods to make working with IO much easier.
 * @author Chase
 */
public class FilesystemToolkit {
	private static final int IO_BUFFER_SIZE = 16384;
	
	private FilesystemToolkit() {}
	
	/**
	 * Get an every byte from an input stream and put it into a byte array.
	 * 
	 * @param stream
	 *            Input stream to read from
	 * @return bytes retrieved from stream
	 * @throws IOException
	 *             if an error occurs
	 */
	public static byte[] get(InputStream stream) throws IOException {
		if (stream != null) {
			/*
			 * This is the fastest method I know for reading from an
			 * indeterminate stream. So fast in fact, that it pars fairly well
			 * with FileChannel stuff in test cases.
			 * 
			 * Small and simple enough the JIT can grab hold and optimize it,
			 * but with a large enough buffer to not make reading slow.
			 */
			final BufferedInputStream input = new BufferedInputStream(stream);
			final ByteArrayOutputStream buffer = new ByteArrayOutputStream(); /* magic */
			final byte[] reader = new byte[IO_BUFFER_SIZE];
			int r = 0;
			while ((r = input.read(reader, 0, IO_BUFFER_SIZE)) != -1)
				buffer.write(reader, 0, r);
			buffer.flush();
			return buffer.toByteArray();
		}
		return null;
	}
	
	/**
	 * Get an every byte from an input stream and put it into a byte array. Then
	 * it also closes the stream after it is done.
	 * 
	 * @param stream
	 *            Input stream to read from
	 * @return bytes retrieved from stream
	 * @throws IOException
	 *             if an error occurs
	 */
	public static byte[] getAndClose(InputStream stream) throws IOException {
		if (stream != null) try {
			/*
			 * This is the fastest method I know for reading from an
			 * indeterminate stream. So fast in fact, that it pars fairly well
			 * with FileChannel stuff in test cases.
			 * 
			 * Small and simple enough the JIT can grab hold and optimize it,
			 * but with a large enough buffer to not make reading slow.
			 */
			final BufferedInputStream input = new BufferedInputStream(stream);
			final ByteArrayOutputStream buffer = new ByteArrayOutputStream(); /* magic */
			final byte[] reader = new byte[IO_BUFFER_SIZE];
			int r = 0;
			while ((r = input.read(reader, 0, IO_BUFFER_SIZE)) != -1)
				buffer.write(reader, 0, r);
			buffer.flush();
			return buffer.toByteArray();
		}
		finally {
			stream.close();
		}
		return null;
	}
	
	/**
	 * Writes to the output as it gets data from the input.
	 * 
	 * @param istream
	 *            Input stream to read from
	 * @param ostream
	 *            Output stream to write to
	 * @throws IOException
	 *             if an error occurs
	 */
	public static void pipe(InputStream istream, OutputStream ostream) throws IOException {
		if (ostream != null && istream != null) {
			/*
			 * This is the fastest method I know for reading from an
			 * indeterminate stream. So fast in fact, that it pars fairly well
			 * with FileChannel stuff in test cases.
			 * 
			 * Small and simple enough the JIT can grab hold and optimize it,
			 * but with a large enough buffer to not make reading slow.
			 */
			final BufferedInputStream input = new BufferedInputStream(istream);
			final BufferedOutputStream output = new BufferedOutputStream(ostream);
			// final FileOutputStream buffer = new FileOutputStream(file);
			final byte[] reader = new byte[IO_BUFFER_SIZE];
			int r = 0;
			while ((r = input.read(reader, 0, IO_BUFFER_SIZE)) != -1) {
				output.write(reader, 0, r);
			}
			output.flush();
		}
	}

	/**
	 * Writes to the output as it gets data from the input. Finally it closes
	 * both.
	 * 
	 * @param istream
	 *            Input stream to read from
	 * @param ostream
	 *            Output stream to write to
	 * @throws IOException
	 *             if an error occurs
	 */
	public static void pipeAndClose(InputStream istream, OutputStream ostream) throws IOException {
		if (ostream != null && istream != null) try {
			/*
			 * This is the fastest method I know for reading from an
			 * indeterminate stream. So fast in fact, that it pars fairly well
			 * with FileChannel stuff in test cases.
			 * 
			 * Small and simple enough the JIT can grab hold and optimize it,
			 * but with a large enough buffer to not make reading slow.
			 */
			final BufferedInputStream input = new BufferedInputStream(istream);
			final BufferedOutputStream output = new BufferedOutputStream(ostream);
			final byte[] reader = new byte[IO_BUFFER_SIZE];
			int r = 0;
			while ((r = input.read(reader, 0, IO_BUFFER_SIZE)) != -1) {
				output.write(reader, 0, r);
			}
			output.flush();

			input.close();
			output.close();
		}
		finally {
			istream.close();
			ostream.close();
		}
	}
	
	
	/**
	 * Gets the contents of the given file.
	 * @param filename The file's filename.
	 * @return the contents of the file, or <code>null</code> on failure
	 */
	public static byte[] getFileContents(String filename) {
		try {
			return getAndClose(new FileInputStream(filename));
		} catch (IOException e) { }
		return null;
	}
	
	/**
	 * Gets the contents of the given file.
	 * @param file The file.
	 * @return the contents of the file, or <code>null</code> on failure
	 */
	public static byte[] getFileContents(File file) {
		try {
			return getAndClose(new FileInputStream(file));
		} catch (IOException e) { }
		return null;
	}
	
	/**
	 * Sets the contents of the given file.
	 * @param filename The file's filename.
	 * @param contents The contents to store.
	 * @return <code>true</code> on success, <code>false</code> otherwise.
	 */
	public static boolean setFileContents(String filename, byte[] contents) {
		OutputStream fos = null;
		try {
			fos = new BufferedOutputStream(new FileOutputStream(filename));
			fos.write(contents);
			fos.flush();
			return true;
		} catch (IOException e) {
		} finally {
			if(fos != null) try {
				fos.close();
			} catch(IOException e) {}
		}
		return false;
	}

	/**
	 * Sets the contents of the given file.
	 * @param file The file.
	 * @param contents The contents to store.
	 * @return <code>true</code> on success, <code>false</code> otherwise.
	 */
	public static boolean setFileContents(File file, byte[] contents) {
		OutputStream fos = null;
		try {
			fos = new BufferedOutputStream(new FileOutputStream(file));
			fos.write(contents);
			fos.flush();
			return true;
		} catch (IOException e) {
		} finally {
			if(fos != null) try {
				fos.close();
			} catch(IOException e) {}
		}
		return false;
	}
	
	/**
	 * Reads a serialized object from the given file.
	 * @param filename The file.
	 * @return Object read, or null on failure.
	 */
	public static Object getFileObject(String filename) {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
			return ois.readObject();
		} catch(IOException e) {
		} catch(ClassNotFoundException e) {
		} finally {
			if(ois != null) try {
				ois.close();
			} catch(IOException e) {}
		}
		return null;
	}
	
	/**
	 * Reads a serialized object from the given file.
	 * @param file The file.
	 * @return Object read, or null on failure.
	 */
	public static Object getFileObject(File file) {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			return ois.readObject();
		} catch(IOException e) {
		} catch(ClassNotFoundException e) {
		} finally {
			if(ois != null) try {
				ois.close();
			} catch(IOException e) {}
		}
		return null;
	}
	
	/**
	 * Sets the object to the given file.
	 * @param filename The file.
	 * @param obj The object to store.
	 * @return <code>true</code> on success, <code>false</code> otherwise.
	 */
	public static boolean setFileObject(String filename, Object obj) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
			oos.writeObject(obj);
			oos.flush();
			return true;
		} catch(IOException e) {
		} finally {
			if(oos != null) try {
				oos.close();
			} catch(IOException e) {}
		}
		return false;
	}
	
	/**
	 * Sets the object to the given file.
	 * @param file The file.
	 * @param obj The object to store.
	 * @return <code>true</code> on success, <code>false</code> otherwise.
	 */
	public static boolean setFileObject(File file, Object obj) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			oos.writeObject(obj);
			oos.flush();
			return true;
		} catch(IOException e) {
		} finally {
			if(oos != null) try {
				oos.close();
			} catch(IOException e) {}
		}
		return false;
	}
	
	/**
	 * Get an every byte from an input stream and put it into a byte array.
	 * 
	 * @param stream
	 *            Input stream to read from
	 * @return bytes retrieved from stream
	 * @throws IOException
	 *             if an error occurs
	 */
	public static final String get(Reader reader) throws IOException {
		if (reader != null) {
			/*
			 * This is the fastest method I know for reading from an
			 * indeterminate reader. So fast in fact, that it pars fairly well
			 * with FileChannel stuff in test cases.
			 * 
			 * Small and simple enough the JIT can grab hold and optimize it,
			 * but with a large enough buffer to not make reading slow.
			 */

			final BufferedReader input = new BufferedReader(reader);
			final StringBuilder buffer = new StringBuilder();

			final char[] read = new char[IO_BUFFER_SIZE];
			int r = 0;
			while ((r = input.read(read, 0, IO_BUFFER_SIZE)) != -1)
				buffer.append(read, 0, r);

			return buffer.toString();
		}
		return null;
	}

	/**
	 * Get an every byte from an input stream and put it into a byte array. Then
	 * it also closes the stream after it is done.
	 * 
	 * @param stream
	 *            Input stream to read from
	 * @return bytes retrieved from stream
	 * @throws IOException
	 *             if an error occurs
	 */
	public static final String getAndClose(Reader reader) throws IOException {
		if (reader != null) try {
			/*
			 * This is the fastest method I know for reading from an
			 * indeterminate reader. So fast in fact, that it pars fairly well
			 * with FileChannel stuff in test cases.
			 * 
			 * Small and simple enough the JIT can grab hold and optimize it,
			 * but with a large enough buffer to not make reading slow.
			 */
			final BufferedReader input = new BufferedReader(reader);
			final StringBuilder buffer = new StringBuilder();

			final char[] read = new char[IO_BUFFER_SIZE];
			int r = 0;
			while ((r = input.read(read, 0, IO_BUFFER_SIZE)) != -1)
				buffer.append(read, 0, r);

			return buffer.toString();
		}
		finally {
			reader.close();
		}
		return null;
	}
	
	/**
	 * Gets the contents of the given file.
	 * @param filename The file's filename.
	 * @return the contents of the file, or <code>null</code> on failure
	 */
	public static String getFileStringContents(String filename) {
		try {
			return getAndClose(new FileReader(filename));
		} catch (IOException e) { }
		return null;
	}
	
	/**
	 * Gets the contents of the given file.
	 * @param file The file.
	 * @return the contents of the file, or <code>null</code> on failure
	 */
	public static String getFileStringContents(File file) {
		try {
			return getAndClose(new FileReader(file));
		} catch (IOException e) { }
		return null;
	}
	
	/**
	 * Sets the contents of the given file.
	 * @param filename The file's filename.
	 * @param contents The contents to store.
	 * @return <code>true</code> on success, <code>false</code> otherwise.
	 */
	public static boolean setFileContents(String filename, String contents) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(filename);
			fw.write(contents);
			return true;
		} catch (IOException e) {
		} finally {
			if(fw != null) try {
				fw.close();
			} catch(IOException e) {}
		}
		return false;
	}
	

	/**
	 * Sets the contents of the given file.
	 * @param file The file.
	 * @param contents The contents to store.
	 * @return <code>true</code> on success, <code>false</code> otherwise.
	 */
	public static boolean setFileContents(File file, String contents) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(contents);
			return true;
		} catch (IOException e) {
		} finally {
			if(fw != null) try {
				fw.close();
			} catch(IOException e) {}
		}
		return false;
	}
}
