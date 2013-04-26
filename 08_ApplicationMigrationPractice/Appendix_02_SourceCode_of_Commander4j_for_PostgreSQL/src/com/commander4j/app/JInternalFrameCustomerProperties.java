package com.commander4j.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.commander4j.db.JDBCustomer;
import com.commander4j.db.JDBLanguage;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JTextField4j;
import com.commander4j.sys.Common;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JInternalFrameCustomerProperties extends JInternalFrame
{
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JButton4j jButtonClose;
	private JButton4j jButtonHelp;
	private JButton4j jButtonUpdate;
	private JTextField4j jTextFieldName;
	private JLabel4j_std jLabel3;
	private JTextField4j jTextFieldID;
	private JLabel4j_std jLabel1;
	private JDBCustomer mt = new JDBCustomer(Common.selectedHostID, Common.sessionID);
	private String ltype;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);
	private JLabel4j_std label;
	private JCheckBox checkBoxPrintOnLabel;

	public JInternalFrameCustomerProperties() {
		super();
		initGUI();
		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_ADMIN_CUSTOMER"));

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jTextFieldName.requestFocus();
				jTextFieldName.setCaretPosition(jTextFieldName.getText().length());

			}
		});
	}

	public JInternalFrameCustomerProperties(String typ) {

		this();

		jTextFieldID.setText(typ);
		setTitle(getTitle() + " - " + typ);
		ltype = typ;

		mt.setID(ltype);
		mt.getCustomerProperties();

		jTextFieldID.setText(mt.getID());
		jTextFieldName.setText(mt.getName());
		if (mt.getPrintOnLabel().equals("Y")) {
			checkBoxPrintOnLabel.setSelected(true);
		}
		else {
			checkBoxPrintOnLabel.setSelected(false);
		}
	}

	private void initGUI() {
		try {
			this.setPreferredSize(new java.awt.Dimension(389, 143));
			this.setBounds(0, 0, 408+Common.LFAdjustWidth, 185+Common.LFAdjustHeight);
			setVisible(true);
			this.setIconifiable(true);
			this.setClosable(true);
			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
				{
					jLabel1 = new JLabel4j_std();
					jDesktopPane1.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					jLabel1.setText(lang.get("lbl_Customer_ID"));
					jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel1.setBounds(12, 12, 110, 21);
				}
				{
					jTextFieldID = new JTextField4j();
					jDesktopPane1.add(jTextFieldID, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					jTextFieldID.setHorizontalAlignment(SwingConstants.LEFT);
					jTextFieldID.setEditable(false);
					jTextFieldID.setEnabled(false);
					jTextFieldID.setBounds(129, 12, 141, 21);
				}
				{
					jLabel3 = new JLabel4j_std();
					jDesktopPane1.add(jLabel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					jLabel3.setText(lang.get("lbl_Customer_Name"));
					jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel3.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel3.setBounds(12, 40, 110, 21);
				}

				{
					jTextFieldName = new JTextField4j();
					jDesktopPane1.add(jTextFieldName, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					jTextFieldName.setBounds(129, 40, 238, 21);
					jTextFieldName.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonUpdate.setEnabled(true);
						}
					});
					jTextFieldName.setFocusCycleRoot(true);
				}
				{
					jButtonUpdate = new JButton4j(Common.icon_update);
					jDesktopPane1.add(jButtonUpdate, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					jButtonUpdate.setEnabled(false);
					jButtonUpdate.setText(lang.get("btn_Save"));
					jButtonUpdate.setMnemonic(lang.getMnemonicChar());
					jButtonUpdate.setHorizontalTextPosition(SwingConstants.RIGHT);
					jButtonUpdate.setBounds(12, 100, 112, 28);
					jButtonUpdate.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							mt.setName(jTextFieldName.getText());
							if (checkBoxPrintOnLabel.isSelected()) {
								mt.setPrintOnLabel("Y");
							}
							else {
								mt.setPrintOnLabel("N");
							}
							mt.update();
							jButtonUpdate.setEnabled(false);
						}
					});
				}
				{
					jButtonHelp = new JButton4j(Common.icon_help);
					jDesktopPane1.add(jButtonHelp);
					jButtonHelp.setText(lang.get("btn_Help"));
					jButtonHelp.setMnemonic(lang.getMnemonicChar());
					jButtonHelp.setBounds(131, 100, 112, 28);
				}
				{
					label = new JLabel4j_std();
					label.setText((String) null);
					label.setHorizontalTextPosition(SwingConstants.RIGHT);
					label.setHorizontalAlignment(SwingConstants.RIGHT);
					label.setBounds(12, 67, 110, 21);
					label.setText(lang.get("lbl_Print_Customer_on_Label"));
					jDesktopPane1.add(label);
				}
				{
					checkBoxPrintOnLabel = new JCheckBox();
					checkBoxPrintOnLabel.setSelected(true);
					checkBoxPrintOnLabel.setBackground(Color.WHITE);
					checkBoxPrintOnLabel.setBounds(126, 67, 21, 24);
					checkBoxPrintOnLabel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							jButtonUpdate.setEnabled(true);
						}
					});
					jDesktopPane1.add(checkBoxPrintOnLabel);

				}
				{
					jButtonClose = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonClose, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					jButtonClose.setText(lang.get("btn_Close"));
					jButtonClose.setMnemonic(lang.getMnemonicChar());
					jButtonClose.setBounds(250, 100, 112, 28);
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							dispose();
						}
					});
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
