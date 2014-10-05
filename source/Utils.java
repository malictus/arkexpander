package malictus.arkexpander;

import java.io.RandomAccessFile;

/**
 * ARK EXPANDER by Jim Halliday
 * Version 1.01
 * malictus@malictus.net
 * 
 * A collection of utils for the other classes
 */
public class Utils {
	
	private Utils() {}
	
	/**
	 * Read a single null-terminated ASCII string from a file
	 */
	static public String readNullTerminatedString(RandomAccessFile raf) throws Exception {
		String x = "";
		byte[] w = new byte[1];
		byte next = raf.readByte();
		while (next != 0) {
			w[0] = next;
			x = x + new String(w);
			next = raf.readByte();
		}
		return x;
	}
	
	/**
	 * Write a null-terminated ASCII string to a file
	 */
	static public void writeNullTerminatedString(RandomAccessFile raf, String input) throws Exception {
		byte[] buf = input.getBytes();	
		raf.write(buf);
		byte x = 0;
		raf.write(x);
	}
	
	/**
	 * Do necessary conversions to read 32-bit little endian ints (also convert from unsigned to signed)
	 */
	static public long readNumber(int inputNumber) {
		inputNumber = Integer.reverseBytes(inputNumber);
		long output = inputNumber & 0xffffffffL;
		return output;
	}
	
	/**
	 * Do necessary conversions to write 32-bit little endian ints
	 * Returns a byte array that represents the int
	 */
	static public byte[] writeNumber(int inputNumber) {
		byte[] array = new byte[4];
		array[0] = (byte) (inputNumber & 0x00FF);
		array[1] = (byte) ((inputNumber >> 8) & 0x000000FF);
		array[2] = (byte) ((inputNumber >> 16) & 0x000000FF);
		array[3] = (byte) ((inputNumber >> 24) & 0x000000FF);
		return array;
	}

}
