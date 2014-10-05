package malictus.arkexpander;

/**
 * ARK EXPANDER by Jim Halliday
 * Version 1.01
 * malictus@malictus.net
 * 
 * This object represents a single entry in the section 1 string table of a HDR file.
 */
public class StringTableElement {
	private String theString;	//string value
	private int theOffset;		//byte offset into string table
	
	public StringTableElement(String string, int offset) {
		theString = string;
		theOffset = offset;
	}
	
	public String getString() {
		return theString;
	}
	
	public int getOffset() {
		return theOffset;
	}
}
