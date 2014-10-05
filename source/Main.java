package malictus.arkexpander;

import javax.swing.*;
import java.awt.event.*;

/**
 * ARK EXPANDER by Jim Halliday
 * Version 1.01
 * malictus@malictus.net
 * 
 * This class contains the Java main function that initiates the program, and also
 * draws the GUI.
 */
public class Main extends JFrame {

	private JPanel jContentPane = null;
	private JButton btnAbout = null;
	private JButton btnExpandARK = null;
	private JButton btnCreateARK = null;
	private JLabel lblARKFileLoc= null;
	private JTextField txtfARKFileLoc = null;
	private JButton btnARKFile = null;
	private JLabel lblExpandedFolderLoc= null;
	private JTextField txtfExpandedFolderLoc = null;
	private JButton btnARKExpandedFolder = null;
	
	private static final String VERSION = "1.01";
	
	//we'll keep reusing the same one so that folder references will be remembered
	private JFileChooser jfc = new JFileChooser();
	
	public static void main(String[] args) {
		new Main();
	}	
	
	public Main() {
		super();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception err) {
			System.exit(0);
		}
		initialize();
		this.setVisible(true);
    }
	
	private void showAboutMessage() {
		JOptionPane.showMessageDialog(this, "ARK EXPANDER " + VERSION + "\nby Jim Halliday\nmalictus@malictus.net", "ARK EXPANDER " + VERSION, JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void doExpandARK() {
		String orig = this.txtfARKFileLoc.getText();
		String expand = this.txtfExpandedFolderLoc.getText();
		ExpandARK x = new ExpandARK(this, orig, expand);
		if (!(x.getFinishedString().equals(ExpandARK.FINISHED_SUCCESSFULLY))) {
			JOptionPane.showMessageDialog(this, "ERROR attempting to expand ARK file:\n" + x.getFinishedString());
		}
	}
	
	private void doCreateARK() {
		String orig = this.txtfARKFileLoc.getText();
		String expand = this.txtfExpandedFolderLoc.getText();
		CreateARK x = new CreateARK(this, orig, expand);
		if (!(x.getFinishedString().equals(ExpandARK.FINISHED_SUCCESSFULLY))) {
			JOptionPane.showMessageDialog(this, "ERROR attempting to create ARK file:\n" + x.getFinishedString());
		}
	}
	
	private void browseForARKFolder() {
		int response = jfc.showOpenDialog(this);
		if (response != JFileChooser.CANCEL_OPTION) {
			this.txtfARKFileLoc.setText(jfc.getSelectedFile().getPath());
		}
	}
	
	private void browseForExpandedFolder() {
		int response = jfc.showOpenDialog(this);
		if (response != JFileChooser.CANCEL_OPTION) {
			this.txtfExpandedFolderLoc.setText(jfc.getSelectedFile().getPath());
		}
	}
	
	void this_windowClosing(WindowEvent e) {
        System.exit(0);
    }

	private void initialize() {
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				this_windowClosing(e);
			}
	    });
		this.setSize(new java.awt.Dimension(419,160));
        this.setLocation(5, 5);
        this.setResizable(false);
        this.setTitle("ARK EXPANDER " + VERSION);
        this.setContentPane(getJContentPane());
        jfc.setAcceptAllFileFilterUsed(true);
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}
	
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			int pos = 7;
			
			lblARKFileLoc = new JLabel();
			lblARKFileLoc.setBounds(new java.awt.Rectangle(7,pos+4,104,16));
			lblARKFileLoc.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			lblARKFileLoc.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			lblARKFileLoc.setText("ARK Folder:");
			txtfARKFileLoc = new JTextField();
			txtfARKFileLoc.setBounds(new java.awt.Rectangle(117,pos+4,186,18));
			btnARKFile = new JButton();
			btnARKFile.setBounds(new java.awt.Rectangle(308,pos,74,23));
			btnARKFile.setText("Browse");
			btnARKFile.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					browseForARKFolder();
				}
			});
			
			pos = pos + 30;
			
			lblExpandedFolderLoc = new JLabel();
			lblExpandedFolderLoc.setBounds(new java.awt.Rectangle(7,pos+4,104,16));
			lblExpandedFolderLoc.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			lblExpandedFolderLoc.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			lblExpandedFolderLoc.setText("Expanded Folder:");
			txtfExpandedFolderLoc = new JTextField();
			txtfExpandedFolderLoc.setBounds(new java.awt.Rectangle(117,pos+4,186,18));
			btnARKExpandedFolder = new JButton();
			btnARKExpandedFolder.setBounds(new java.awt.Rectangle(308,pos,74,23));
			btnARKExpandedFolder.setText("Browse");
			btnARKExpandedFolder.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					browseForExpandedFolder();
				}
			});
			
			pos = pos + 40;
			
			btnCreateARK = new JButton();
			btnCreateARK.setBounds(new java.awt.Rectangle(50,pos-2,120,20));
			btnCreateARK.setText("Create ARK");
			btnCreateARK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					doCreateARK();
				}
			});
			
			btnExpandARK = new JButton();
			btnExpandARK.setBounds(new java.awt.Rectangle(230,pos-2,120,20));
			btnExpandARK.setText("Expand ARK");
			btnExpandARK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					doExpandARK();
				}
			});
			
			pos = pos + 30;
			
			btnAbout = new JButton();
			btnAbout.setBounds(new java.awt.Rectangle(150,pos-2,100,20));
			btnAbout.setText("About");
			btnAbout.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showAboutMessage();
				}
			});
			
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(btnCreateARK, null);
			jContentPane.add(btnExpandARK, null);
			jContentPane.add(btnAbout, null);
			jContentPane.add(btnARKExpandedFolder, null);
			jContentPane.add(txtfARKFileLoc, null);
			jContentPane.add(lblARKFileLoc, null);
			jContentPane.add(btnARKFile, null);
			jContentPane.add(txtfExpandedFolderLoc, null);
			jContentPane.add(lblExpandedFolderLoc, null);
		}
		return jContentPane;
	}
}
