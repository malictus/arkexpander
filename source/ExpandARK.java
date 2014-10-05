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
 * This class does the job of expanding an ARK file into its component files. Implemented as a dialog with a progress bar.
 */
public class ExpandARK extends JDialog {

	private JPanel jContentPane = null;
	private JLabel lblProg = null;
	private JProgressBar prgProg = null;
	private JButton btnCancel = null;

	java.util.Timer theTimer = new java.util.Timer();
	private String status = "";
	private String finishedString = "";
	private int progressCounter = 0;
	private boolean canceled = false;

	private HDR_Data headerData;

	static final String FINISHED_SUCCESSFULLY = "DONE";

	public ExpandARK(JFrame parent, final String arkFolderString, final String expandFolderString) {
		super(parent);
		this.setTitle("Expanding ARK File...");
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
            	doARKExpansion(arkFolderString, expandFolderString);
            }
        };
        ExpandARKTask lTask = new ExpandARKTask(theTimer);
        theTimer.schedule(lTask, 0, 200);
        Thread t = new Thread(q);
        t.start();
        this.setVisible(true);
	}

	private void doARKExpansion(String arkFolderString, String expandFolderString) {
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
    		File inputFolder = new File(arkFolderString);
    		File outputFolder = new File(expandFolderString);
    		if ((inputFolder == null) || (outputFolder == null)) {
    			finishedString = "Error accessing input/output folders.";
    			return;
    		}
    		if ((!inputFolder.exists()) || (!outputFolder.exists())) {
    			finishedString = "The specified folder locations do not exist.";
    			return;
    		}
    		if ((!inputFolder.isDirectory() || (!outputFolder.isDirectory()))) {
    			finishedString = "You must specify a directory, not a file.";
    			return;
    		}

    		//needed for later
    		String outputFolderDir = expandFolderString;
    		if ( (!outputFolderDir.endsWith("/")) && (!outputFolderDir.endsWith("\\")) ) {
    			outputFolderDir = outputFolderDir + "/";
    		}

    		//now verify ARK file and HDR file exist
    		File[] inputFiles = inputFolder.listFiles();
    		File headerFile = null;
    		File arkFile = null;
    		int counter = 0;
    		while (counter < inputFiles.length) {
    			File x = inputFiles[counter];
    			if (x.getName().toUpperCase().equals("MAIN.HDR")) {
    				headerFile = x;
    			} else if (x.getName().toUpperCase().equals("MAIN_0.ARK")) {
    				arkFile = x;
    			}
    			counter = counter + 1;
    		}
    		if ((headerFile == null) || (arkFile == null)) {
    			finishedString = "ARK file or HDR file not found in specified location.";
    			return;
    		}

    		//verify expansion folder location is empty
    		File[] outputFiles = outputFolder.listFiles();
    		if (outputFiles.length > 0) {
    			finishedString = "Expansion folder must be empty.";
    			return;

    		}

    		//copy the entire header file to the output, since we'll need it to rebuild later
    		status = "Copying HDR file";
    		File fil = new File(outputFolderDir + "MAIN.HDR");
			fil.createNewFile();
    		FileChannel srcChannel = new FileInputStream(headerFile).getChannel();
    	    FileChannel dstChannel = new FileOutputStream(fil).getChannel();
    	    dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
    	    srcChannel.close();
    	    dstChannel.close();

    		//read the HDR file into a HDR_Data object, so we only have to read it once
    	    status = "Reading HDR data";
    	    headerData = new HDR_Data(headerFile);

    		counter = 0;
    		prgProg.setMaximum((int)headerData.getSection3Size() + 1);
    		Vector section3Entries = headerData.getSection3Entries();
    		while (counter < headerData.getSection3Size()) {
    			if (canceled) {
    				break;
    			}
    			status = "Creating file " + (counter + 1) + " of " + headerData.getSection3Size();
    			progressCounter = counter;

    			//read a single file entry
    			Section3Entry s = (Section3Entry)section3Entries.get(counter);

    			//parse directory string and create directories if necessary
    			String[] folders = s.getDirectoryName().split("/");
    			int innercounter = 0;
    			String fullPath = outputFolderDir;
    			while (innercounter < folders.length) {
    				//apply substitution for folders named '..'
    				if (folders[innercounter].equals("..")) {
    					folders[innercounter] = "dotdot";
    				}
    				fullPath = fullPath + folders[innercounter] + "/";
    				innercounter = innercounter + 1;
    			}

    			File f = new File(fullPath);
				if (!(f.exists())) {
					f.mkdirs();
				}

				//create files
				fullPath = fullPath + s.getFileName();
				File outfile = new File(fullPath);
				outfile.createNewFile();
				FileInputStream inARK = new FileInputStream(arkFile);
				FileOutputStream fos = new FileOutputStream(outfile);
				inARK.skip(s.getFileOffset());
		        //transfer bytes
		        long filecounter = 0;
				while ((filecounter + 1024) < s.getFileSize()) {
					if (canceled) {
        				break;
        			}
					byte[] buf = new byte[1024];
					inARK.read(buf);
					fos.write(buf);
					filecounter = filecounter + 1024;
				}
				if (canceled) {
					inARK.close();
					fos.close();
	        		finishedString = FINISHED_SUCCESSFULLY;
	        		return;
    			}
				//finish up the remaining bytes
				byte[] buf = new byte[(int)(s.getFileSize() - filecounter)];
				inARK.read(buf);
				fos.write(buf);
				inARK.close();
				fos.flush();
				fos.close();
    			counter = counter + 1;
    		}

    		finishedString = FINISHED_SUCCESSFULLY;
    	} catch (Exception e) {
    		if (e.getMessage() == null) {
    			e.printStackTrace();
    			finishedString = "Error creating files.";
    		} else if (e.getMessage().equals("null")) {
    			finishedString = "Error creating files.";
    		} else {
    			finishedString = e.getMessage();
    		}
			return;
    	}
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

	private class ExpandARKTask extends TimerTask {
        java.util.Timer myTimer = null;
        String currStatus = "";

        public ExpandARKTask(java.util.Timer aTimer) {
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
