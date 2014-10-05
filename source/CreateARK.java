package malictus.arkexpander;

import java.io.*;
import java.nio.channels.*;
import java.util.*;
import javax.swing.*;

/**
 * ARK EXPANDER by Jim Halliday
 * Version 1.01
 * malictus@malictus.net
 * 
 * 
 * This class does the job of creating an ARK file from its component files. Implemented as a dialog with a progress bar.
 */
public class CreateARK extends JDialog {
	
	private JPanel jContentPane = null;
	private JLabel lblProg = null;
	private JProgressBar prgProg = null;
	private JButton btnCancel = null;
	
	java.util.Timer theTimer = new java.util.Timer();
	private String status = "";
	private String finishedString = "";
	private int progressCounter = 0;
	private boolean canceled = false;
	Vector allFiles = new Vector();
	
	static final String FINISHED_SUCCESSFULLY = "DONE";
	
	public CreateARK(JFrame parent, final String arkFolderString, final String expandFolderString) {
		super(parent);
		this.setTitle("Creating ARK File...");
        this.setSize(new java.awt.Dimension(412,88));
        this.setLocation(parent.getX() + (parent.getWidth() / 2) - (this.getWidth() / 2), parent.getY() + (parent.getHeight() / 2) - (this.getHeight() / 2));
		initialize(arkFolderString, expandFolderString);
	}
	
	public String getFinishedString() {
		return finishedString;
	}
	
	private void initialize(final String arkFolderString, final String expandFolderString) {
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setModal(true);
        this.setContentPane(getJContentPane());
        this.setResizable(false);
        
		Runnable q = new Runnable() {
            public void run() {
            	doARKCreation(arkFolderString, expandFolderString);
            }
        };
        
        CreateARKTask lTask = new CreateARKTask(theTimer);
        theTimer.schedule(lTask, 0, 200);
        Thread t = new Thread(q);
        t.start();
        this.setVisible(true);
	}
	
	private void doARKCreation(String arkFolderString, String expandFolderString) {
		try {
        	//first verify folders are valid
        	status = "Verifying folders";
    		if ((arkFolderString == null) || (expandFolderString == null)) {
    			finishedString = "Input/output folder strings are null.";
    			return;
    		}
    		if ((arkFolderString.equals("") || (expandFolderString.equals("")))) {
    			finishedString = "Folder locations are not specified.";
    			return;
    		}
    		File arkFolder = new File(arkFolderString);
    		File expandFolder = new File(expandFolderString);
    		if ((arkFolder == null) || (expandFolder == null)) {
    			finishedString = "Error accessing input/output folders.";
    			return;
    		}
    		if ((!arkFolder.exists()) || (!expandFolder.exists())) {
    			finishedString = "The specified folder locations do not exist.";
    			return;
    		}
    		
    		//confirm that folder to compress isn't empty
    		if (expandFolder.listFiles().length < 1) {
    			finishedString = "Folder to compress is empty.";
    			return;
    		}
    		
    		//see if ARK and HDR file already exist
    		String arkFolderDir = arkFolderString;
    		if ( (!arkFolderDir.endsWith("/")) && (!arkFolderDir.endsWith("\\")) ) {
    			arkFolderDir = arkFolderDir + "/";
    		}
    		File arkFile = new File(arkFolderDir + "MAIN_0.ARK");
    		File headerFile = new File(arkFolderDir + "MAIN.HDR");
    		if (arkFile.exists() || headerFile.exists()) {
    			//are existing files read-only?
        		if ( (!arkFile.canWrite()) || (!headerFile.canWrite()) ) {
        			finishedString = "ARK and/or HDR files are read-only.";
        			return;
        		}
    			int response = JOptionPane.showConfirmDialog(this, "WARNING: ARK and/or HDR files already exist in this location. Overwrite?", "Overwrite?", JOptionPane.YES_NO_OPTION);
    			if (response == JOptionPane.NO_OPTION) {
    				finishedString = FINISHED_SUCCESSFULLY;
	        		return;
    			} else {
    				//delete existing ARK and HDR files
    				arkFile.delete();
    				headerFile.delete();
    			}
    		}
    		//create new ARK and HDR files
    		arkFile.createNewFile();
    		headerFile.createNewFile();
    		//copy the entire existing header file to the output
    		File oldheader = new File(expandFolder.getPath() + "/" + "MAIN.HDR");
    		if (!oldheader.exists()) {
    			finishedString = "ERROR: MAIN.HDR file must be present in expanded folder.";
    			return;
    		}
    		FileChannel srcChannel = new FileInputStream(oldheader).getChannel();
    	    FileChannel dstChannel = new FileOutputStream(headerFile).getChannel();
    	    dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
    	    srcChannel.close();
    	    dstChannel.close();
    	    //create HDR information class
    	    HDR_Data headerData = new HDR_Data(headerFile);
    	    //recursively populate the file vector
    		status = "Populating file vector";
    		allFiles.clear();
    		getFilesFor(expandFolder);
    		//output data to ARK file
    		FileOutputStream fos = new FileOutputStream(arkFile);
    		prgProg.setMaximum(allFiles.size());
    		int counter = 0;
    		while (counter < allFiles.size()) {
    			if (canceled) {
    				break;
    			}
    			status = "Adding file " + (counter + 1) + " of " + allFiles.size();
    			progressCounter = counter;
    			File f = (File)allFiles.get(counter);
    			FileInputStream fin = new FileInputStream(f);
    			//copy contents to ARK file
    			byte[] buffer = new byte[1024];
    	        int len;
    	        while ((len = fin.read(buffer)) > 0) {
    	            fos.write(buffer, 0, len);
    	        }
    	        fin.close();
    			counter = counter + 1;
    		}
    		//cleanup
    		fos.close();   		
    		//now, overwrite HDR file contents where necessary
    		status = "Modifying HDR file";
    		headerData.setArkSize((int)arkFile.length());    	
    		counter = 0;
    		int offset = 0;
    		while (counter < allFiles.size()) {
    			File f = (File)allFiles.get(counter);
    			String name = f.getName();
    			String pathName = ((File)allFiles.get(counter)).getPath();
    			pathName = getPathNameFor(pathName, name, expandFolderString);
    			headerData.overwriteSection3Entry(name, pathName, offset, f.length());
    			//increment offset
    			offset = offset + (int)f.length();
    			counter = counter + 1;
    		}    		
    		finishedString = FINISHED_SUCCESSFULLY;
    	} catch (Exception e) {
    		if (e.getMessage().equals("null")) {
    			finishedString = "Error creating files.";
    		} else {
    			finishedString = e.getMessage();
    		}
			return;
    	}
	}
	
	private String getPathNameFor(String pathName, String fileName, String expandFolderString) {
		//trim off file name itself
		pathName = pathName.substring(0, pathName.length() - fileName.length() - 1);
		//trim off beginning of dir name
		pathName = pathName.substring((int)expandFolderString.length() + 1);
		//change 'dotdot' back to '..'
		pathName = pathName.replace("dotdot", "..");
		//replace folder indicators
		pathName = pathName.replace("\\", "/");
		return pathName;
	}
	
	private Vector getFilesFor(File expandFolder) {
		File[] children = expandFolder.listFiles();
		int counter = 0;
		while (counter < children.length) {
			File child = children[counter];
			if (child.isFile()) {
				//don't include archive file!
				if (!child.getName().equals("MAIN.HDR")) {
					allFiles.add(child);
				}
			} else {
				//recurse
				getFilesFor(child);
			}
			counter = counter + 1;
		}
		return new Vector();
	}
	
	private void doCancel() {
		canceled = true;
	}
	
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			lblProg = new JLabel();
			lblProg.setBounds(new java.awt.Rectangle(7,5,300,16));
			lblProg.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			lblProg.setText("");
			btnCancel = new JButton();
			btnCancel.setBounds(new java.awt.Rectangle(320,28,75,22));
			btnCancel.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			btnCancel.setText("Cancel");
			btnCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					doCancel();
				}
			});
			prgProg = new JProgressBar();
			prgProg.setMinimum(0);
			prgProg.setMaximum(100);
			prgProg.setValue(0);
			prgProg.setBounds(new java.awt.Rectangle(7,28,303,23));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(lblProg, null);
			jContentPane.add(btnCancel, null);
			jContentPane.add(prgProg, null);
		}
		return jContentPane;
	}
	
	private class CreateARKTask extends TimerTask {
        java.util.Timer myTimer = null;
        String currStatus = "";
        
        public CreateARKTask(java.util.Timer aTimer) {
            super();
            myTimer = aTimer;
        }
        
        public void run() {
            if (!finishedString.equals("")) {
            	setVisible(false);
            }
            if (!(currStatus.equals(status))) {
            	currStatus = status;
            	lblProg.setText(status);
            	prgProg.setValue(progressCounter);
            }
        }
	}	
}
