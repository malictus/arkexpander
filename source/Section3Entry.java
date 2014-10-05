package malictus.arkexpander;

/**
 * ARK EXPANDER by Jim Halliday
 * Version 1.01
 * malictus@malictus.net
 * 
 * This object represents a single entry in the section 3 (file entry) table of a HDR file.
 */
public class Section3Entry {
	private long fileOffset;
	private long fileID;
	private long directoryID;
	private long fileSize;
	private String fileName;
	private String directoryName;
	private long offsetIntoHDR;
	
	public Section3Entry(long fileoffset, long fileid, long directoryid, long filesize, String filename, String directoryname, long offsetintohdr) {
		fileOffset = fileoffset;
		fileID = fileid;
		directoryID = directoryid;
		fileSize = filesize;
		fileName = filename;
		directoryName = directoryname;
		offsetIntoHDR = offsetintohdr;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getDirectoryName() {
		return directoryName;
	}
	
	public long getFileOffset() {
		return fileOffset;
	}
	
	public void setFileOffset(long newOffset) {
		fileOffset = newOffset;
	}
	
	public long getFileID() {
		return fileID;
	}
	
	public long getDirectoryID() {
		return directoryID;
	}
	
	public long getFileSize() {
		return fileSize;
	}
	
	public void setFileSize(long newFileSize) {
		fileSize = newFileSize;
	}
	
	public long getOffsetIntoHDR() {
		return offsetIntoHDR;
	}
}