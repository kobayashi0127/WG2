package com.commander4j.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBQMActivity;
import com.commander4j.db.JDBQMInspection;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JTextField4j;
import com.commander4j.sys.Common;
import com.commander4j.util.JUtility;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class JDialogQMActivityProperties extends javax.swing.JDialog
{

	private static final long serialVersionUID = 1L;
	private JTextField4j textFieldInspectionID;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID,Common.sessionID);
	private JDBQMInspection inspect = new JDBQMInspection(Common.selectedHostID,Common.sessionID);
	private JDBQMActivity active = new JDBQMActivity(Common.selectedHostID,Common.sessionID);
	private JTextField4j textFieldInspectionDescription;
	private JTextField4j textFieldActivityDescription;
	private JTextField4j textFieldActivityID;
	private JButton4j btnSave;
	private JButton4j btnClose;
	
	
	private void enableSave()
	{
		if (textFieldActivityID.getText().equals("")==false)
		{
			if (textFieldActivityDescription.getText().equals("")==false)
			{
				btnSave.setEnabled(true);
			}
		}
	}
	
	private void save()
	{
		String insp = textFieldInspectionID.getText();
		String act = textFieldActivityID.getText();
		String description = textFieldActivityDescription.getText();
		
		if (active.isValid(insp, act)==false)
		{
			active.create(insp, act,description);
		}
		else
		{
			active.setDescription(textFieldActivityDescription.getText());
			active.update();				
		}
	}
	
	
	
	public JDialogQMActivityProperties(JFrame frame,String inspectionid,String activityid) {
		
		super(frame,"Activity Properties",ModalityType.APPLICATION_MODAL);
		
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle("Activity Properties");
		this.setResizable(false);		
		this.setSize(855, 184);

		
		Dimension screensize = Common.mainForm.getSize();

		Dimension formsize = getSize();
		int leftmargin = ((screensize.width - formsize.width) / 2);
		int topmargin = ((screensize.height - formsize.height) / 2);

		setLocation(leftmargin, topmargin);

		getContentPane().setBackground(Color.LIGHT_GRAY);
		getContentPane().setLayout(null);
		
		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.setBackground(Color.WHITE);
		desktopPane.setBounds(0, 0, 855, 162);
		getContentPane().add(desktopPane);
		
		JLabel4j_std lblInspectionID = new JLabel4j_std(lang.get("lbl_Inspection_ID"));
		lblInspectionID.setBounds(8, 27, 87, 16);
		desktopPane.add(lblInspectionID);
		lblInspectionID.setHorizontalAlignment(SwingConstants.TRAILING);
		
		JLabel4j_std lblActivityID = new JLabel4j_std(lang.get("lbl_Activity_ID"));
		lblActivityID.setBounds(6, 67, 89, 16);
		desktopPane.add(lblActivityID);
		lblActivityID.setHorizontalAlignment(SwingConstants.TRAILING);
		
		textFieldInspectionID = new JTextField4j();
		textFieldInspectionID.setEnabled(false);
		textFieldInspectionID.setBounds(108, 22, 153, 28);
		desktopPane.add(textFieldInspectionID);
		textFieldInspectionID.setColumns(10);
		
		
		textFieldInspectionDescription = new JTextField4j();
		textFieldInspectionDescription.setEnabled(false);
		textFieldInspectionDescription.setBounds(377, 22, 463, 28);
		desktopPane.add(textFieldInspectionDescription);
		textFieldInspectionDescription.setColumns(10);
		

		
		btnSave = new JButton4j(lang.get("btn_Save"));
		btnSave.setEnabled(false);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		btnSave.setIcon(Common.icon_save);
		btnSave.setBounds(292, 113, 117, 29);
		desktopPane.add(btnSave);
		
		btnClose = new JButton4j(lang.get("btn_Close"));
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnClose.setIcon(Common.icon_close);
		btnClose.setBounds(413, 113, 117, 29);
		desktopPane.add(btnClose);
		
		JLabel4j_std label4j_std_1 = new JLabel4j_std(lang.get("lbl_Description"));
		label4j_std_1.setHorizontalAlignment(SwingConstants.TRAILING);
		label4j_std_1.setBounds(273, 27, 92, 16);
		desktopPane.add(label4j_std_1);
		
		textFieldActivityID = new JTextField4j();
		textFieldActivityID.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				enableSave();
			}
		});
		textFieldActivityID.setEnabled(false);
		textFieldActivityID.setText("<dynamic>");
		textFieldActivityID.setColumns(10);
		textFieldActivityID.setBounds(108, 62, 153, 28);
		desktopPane.add(textFieldActivityID);
		
		JLabel4j_std label4j_std_2 = new JLabel4j_std(lang.get("lbl_Description"));
		label4j_std_2.setHorizontalAlignment(SwingConstants.TRAILING);
		label4j_std_2.setBounds(267, 67, 98, 16);
		desktopPane.add(label4j_std_2);
		
		textFieldActivityDescription = new JTextField4j();
		textFieldActivityDescription.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				enableSave();
			}
		});
		textFieldActivityDescription.setColumns(10);
		textFieldActivityDescription.setBounds(377, 63, 463, 28);
		desktopPane.add(textFieldActivityDescription);
		
		inspectionid = JUtility.replaceNullStringwithBlank(inspectionid);
		activityid = JUtility.replaceNullStringwithBlank(activityid);

		
		textFieldInspectionID.setText(inspectionid);
		inspect.getProperties(inspectionid);
		textFieldInspectionDescription.setText(inspect.getDescription());
		
		textFieldActivityID.setText(activityid);
		active.getProperties(inspectionid,activityid);
		textFieldActivityDescription.setText(active.getDescription());
		

		if (activityid.equals(""))
		{
			textFieldActivityID.setEnabled(true);
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					textFieldActivityID.requestFocus();
					textFieldActivityID.setCaretPosition(textFieldActivityID.getText().length());
				}
			});
		}
		else
		{
			textFieldActivityID.setEnabled(false);
			
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					textFieldActivityDescription.requestFocus();
					textFieldActivityDescription.setCaretPosition(textFieldActivityDescription.getText().length());
				}
			});
		}

	}
}
