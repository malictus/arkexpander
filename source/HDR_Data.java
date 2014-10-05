package malictus.arkexpander;

import java.io.*;
import java.util.*;

/**
 * ARK EXPANDER by Jim Halliday
 * Version 1.01
 * malictus@malictus.net
 * 
 * This object stores all information about an ARK's HDR file, so that actual file lookup only has to happen once.
 */
public class HDR_Data {
	
	private File theHeaderFile;
	private long arkSize;
	private long section1Size;
	private long section2Size;
	private long section3Size;
	private long section1Start;
	private long section2Start;
	private long section3Start;
	private Vector stringTableElements = new Vector();
	private Vector section2Offsets = new Vector();
	private Vector section3Entries = new Vector();
	
	public HDR_Data(File headerFile) throws Exception {
		theHeaderFile = headerFile;
		RandomAccessFile raf = new RandomAccessFile(theHeaderFile, "r");
		//skip to the part that tells us the size of the ARK file
		raf.seek(12);
		arkSize = Utils.readNumber(raf.readInt());		
		//size of the string table (section 1) in bytes
		section1Size = Utils.readNumber(raf.readInt());	
		//skip to the part of the file that tells us the number of 32-bit string offsets in section 2
		raf.seek(20 + section1Size);
		//number of 32-bit string offsets in section 2
		section2Size = Utils.readNumber(raf.readInt());
		//skip to the part of the file that tells us the number of file entries in section 3
		raf.seek(24 + section1Size + (section2Size * 4));
		section3Size = Utils.readNumber(raf.readInt());    		
		section1Start = 20;
		section2Start = 24 + section1Size;
		section3Start = 28 + section1Size + (section2Size * 4);
		//now begin populating vectors, starting with String table vector
		raf.seek(section1Start + 1);	//skip over first character, which is null
		int offset = 1;
		while (raf.getFilePointer() < (section2Start - 4)) {
			String string = Utils.readNullTerminatedString(raf);
			stringTableElements.add(new StringTableElement(string, offset));
			offset = offset + string.length() + 1;	//add null char
		}
		//now populate vector of section 2 offsets
		raf.seek(section2Start);
		while (raf.getFilePointer() < (section3Start - 4)) {
			section2Offsets.add(new Long(Utils.readNumber(raf.readInt())));
		}
		//lastly, populate the file vector
		raf.seek(section3Start);
		while (raf.getFilePointer() < raf.length() - 1) {
			long offsetIntoHDR = raf.getFilePointer();
			long fileOffset = Utils.readNumber(raf.readInt());
			long filenameStringID = Utils.readNumber(raf.readInt());
			long directoryStringID = Utils.readNumber(raf.readInt());
			long fileSize = Utils.readNumber(raf.readInt());
			long shouldBeZero = Utils.readNumber(raf.readInt());
			if (shouldBeZero != 0) {
				throw new Exception("Error parsing HDR file.");
			}
			String fileName = getFilenameFor((int)filenameStringID);
			String directoryName = getFilenameFor((int)directoryStringID);
			Section3Entry s3e = new Section3Entry(fileOffset, filenameStringID, directoryStringID, fileSize, fileName, directoryName, offsetIntoHDR);
			section3Entries.add(s3e);
		}
		raf.close();
	}
	
	/**
	 * Overwrite a single value in section 3 of the table
	 */
	public void overwriteSection3Entry(String name, String pathName, int offset, long length) throws Exception {
		//first, find the Section3Entry that corresponds to this name/pathName
		Section3Entry s = getSection3EntryFor(name, pathName);
		//now, overwrite existing values in array
		s.setFileOffset(offset);
		s.setFileSize(length);
		//also, overwrite actual values in HDR file
		RandomAccessFile raf = new RandomAccessFile(theHeaderFile, "rw");
		raf.seek(s.getOffsetIntoHDR());
		raf.write(Utils.writeNumber(offset));
		raf.seek(s.getOffsetIntoHDR() + 12);
		raf.write(Utils.writeNumber((int)length));
		raf.close();
	}
	
	private Section3Entry getSection3EntryFor(String name, String pathName) throws Exception{
		int counter = 0;
		while (counter < section3Entries.size()) {
			Section3Entry s = (Section3Entry)section3Entries.get(counter);
			if ( (s.getFileName().equals(name)) && (s.getDirectoryName().equals(pathName))) {
				return s;
			}
			counter = counter + 1;
		}
		throw new Exception("File name not found in HDR file.");
	}
	
	/**
	 * Do string table lookup
	 */
	private String getFilenameFor(int fileID) throws Exception {
		long offset = ((Long)section2Offsets.get(fileID)).longValue();
		int counter = 0;
		while (counter < stringTableElements.size()) {
			StringTableElement ste = (StringTableElement)stringTableElements.get(counter);
			if (ste.getOffset() == offset) {
				return ste.getString();
			}
			counter = counter + 1;
		}
		throw new Exception("String not found in string table");
	}
	
	public long getSection1Start() {
		return section1Start;
	}
	
	public long getSection2Start() {
		return section2Start;
	}
	
	public long getSection3Start() {
		return section3Start;
	}
	
	public long getSection1Size() {
		return section1Size;
	}
	
	public long getSection2Size() {
		return section2Size;
	}
	
	public long getSection3Size() {
		return section3Size;
	}
	
	public File getHeaderFile() {
		return theHeaderFile;
	}
	
	public long getArkSize() {
		return arkSize;
	}
	
	public void setArkSize(int newArkSize) throws Exception {
		arkSize = newArkSize;
		RandomAccessFile raf = new RandomAccessFile(theHeaderFile, "rw");
		raf.seek(12);
		raf.write(Utils.writeNumber(newArkSize));
		raf.close();
	}
	
	public Vector getSection3Entries() {
		return section3Entries;
	}

}

