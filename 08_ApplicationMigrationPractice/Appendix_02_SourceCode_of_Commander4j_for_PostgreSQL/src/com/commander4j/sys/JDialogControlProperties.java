package com.commander4j.sys;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;

import com.commander4j.db.JDBControl;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JTextField4j;
import com.commander4j.util.JFixedSizeFilter;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JDialogControlProperties extends JDialog
{
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JLabel4j_std jLabelDescription;
	private JButton4j jButtonClose;
	private JButton4j jButtonHelp;
	private JTextField4j jTextFieldDescription;
	private JTextField4j jTextFieldKeyValue;
	private JTextField4j jTextFieldSystemKey;
	private JButton4j jButtonUpdate;
	private JLabel4j_std jLabelKeyValue;
	private JLabel4j_std jLabelSystemKey;
	private String lsystemKey;
	private JDBControl control = new JDBControl(Common.selectedHostID, Common.sessionID);

	/**
	 * Auto-generated main method to display this JInternalFrame inside a new
	 * JFrame.
	 */

	public JDialogControlProperties(JFrame parent, String systemKey)
	{

		super(parent);

		initGUI();

		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_ADMIN_CONTROL_EDIT"));

		jTextFieldSystemKey.setText(systemKey);
		setTitle(getTitle() + " - " + systemKey);
		lsystemKey = systemKey;

		control.setSystemKey(lsystemKey);
		control.getProperties();

		jTextFieldKeyValue.setText(control.getKeyValue());
		jTextFieldDescription.setText(control.getDescription());

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jTextFieldKeyValue.requestFocus();
				jTextFieldKeyValue.setCaretPosition(jTextFieldKeyValue.getText().length());
			}
		});
	}

	private void initGUI() {
		try
		{
			//setDefaultLookAndFeelDecorated(true);
			setPreferredSize(new java.awt.Dimension(460, 163));
			this.setBounds(25, 25, 488, 157);
			setModal(true);
			this.setTitle("Control Properties");
			getContentPane().setLayout(null);

			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBounds(0, 0, 488, 133);
				jDesktopPane1.setBackground(Color.WHITE);
				this.getContentPane().add(jDesktopPane1);
				jDesktopPane1.setPreferredSize(new Dimension(452, 140));
				jDesktopPane1.setLayout(null);
				{
					jLabelSystemKey = new JLabel4j_std();
					jDesktopPane1.add(jLabelSystemKey);
					jLabelSystemKey.setText("Description :");
					jLabelSystemKey.setBounds(9, 65, 87, 19);
					jLabelSystemKey.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabelSystemKey.setHorizontalTextPosition(SwingConstants.RIGHT);
				}
				{
					jLabelKeyValue = new JLabel4j_std();
					jDesktopPane1.add(jLabelKeyValue);
					jLabelKeyValue.setText("System Key :");
					jLabelKeyValue.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabelKeyValue.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabelKeyValue.setBounds(5, 15, 93, 19);
				}
				{
					jLabelDescription = new JLabel4j_std();
					jDesktopPane1.add(jLabelDescription);
					jLabelDescription.setText("Value :");
					jLabelDescription.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabelDescription.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabelDescription.setBounds(10, 39, 88, 19);
				}
				{

					jButtonUpdate = new JButton4j(Common.icon_update);
					jDesktopPane1.add(jButtonUpdate);
					jButtonUpdate.setText("Save");
					jButtonUpdate.setBounds(79, 95, 110, 30);
					jButtonUpdate.setMnemonic(java.awt.event.KeyEvent.VK_S);
					jButtonUpdate.setEnabled(false);
					jButtonUpdate.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							control.setDescription(jTextFieldDescription.getText());
							control.setKeyValue(jTextFieldKeyValue.getText());
							control.update();
							jButtonUpdate.setEnabled(false);
						}
					});
				}
				{
					jButtonClose = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonClose);
					jButtonClose.setText("Close");
					jButtonClose.setBounds(300, 95, 110, 30);
					jButtonClose.setMnemonic(java.awt.event.KeyEvent.VK_C);
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							dispose();
						}
					});
				}
				{
					jTextFieldSystemKey = new JTextField4j();
					jTextFieldSystemKey.setFocusable(false);
					jDesktopPane1.add(jTextFieldSystemKey);
					jTextFieldSystemKey.setHorizontalAlignment(SwingConstants.LEFT);
					jTextFieldSystemKey.setEditable(false);
					jTextFieldSystemKey.setPreferredSize(new java.awt.Dimension(100, 20));
					jTextFieldSystemKey.setBounds(106, 13, 252, 21);
					jTextFieldSystemKey.setEnabled(false);
				}
				{
					jTextFieldKeyValue = new JTextField4j();
					jDesktopPane1.add(jTextFieldKeyValue);
					AbstractDocument doc = (AbstractDocument) jTextFieldKeyValue.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(JDBControl.field_key_value));
					jTextFieldKeyValue.setPreferredSize(new java.awt.Dimension(40, 20));
					jTextFieldKeyValue.setFocusCycleRoot(true);
					jTextFieldKeyValue.setBounds(106, 39, 252, 21);
					jTextFieldKeyValue.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonUpdate.setEnabled(true);
						}
					});

				}
				{
					jTextFieldDescription = new JTextField4j();
					jDesktopPane1.add(jTextFieldDescription);
					AbstractDocument doc = (AbstractDocument) jTextFieldDescription.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(JDBControl.field_description));
					jTextFieldDescription.setPreferredSize(new java.awt.Dimension(40, 20));
					jTextFieldDescription.setFocusCycleRoot(true);
					jTextFieldDescription.setBounds(106, 65, 337, 21);
					jTextFieldDescription.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonUpdate.setEnabled(true);
						}
					});
				}
				{
					jButtonHelp = new JButton4j(Common.icon_help);
					jDesktopPane1.add(jButtonHelp);
					jButtonHelp.setText("Help");
					jButtonHelp.setBounds(194, 95, 100, 30);
					jButtonHelp.setMnemonic(java.awt.event.KeyEvent.VK_H);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
