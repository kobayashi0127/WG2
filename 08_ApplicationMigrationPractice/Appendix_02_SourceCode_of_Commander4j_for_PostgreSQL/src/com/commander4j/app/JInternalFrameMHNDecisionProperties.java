package com.commander4j.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBMHNDecisions;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JComboBox4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JTextField4j;
import com.commander4j.sys.Common;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JInternalFrameMHNDecisionProperties extends JInternalFrame
{
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JButton4j jButtonClose;
	private JButton4j jButtonHelp;
	private JButton4j jButtonUpdate;
	private JTextField4j jTextFieldDescription;
	private JLabel4j_std jLabel3;
	private JTextField4j jTextFieldReason;
	private JLabel4j_std jLabel1;
	private JDBMHNDecisions decis = new JDBMHNDecisions(Common.selectedHostID, Common.sessionID);
	private String ltype;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);
	private JButton4j btnForground;
	private JComboBox4j jComboBoxPalletStatus;

	public JInternalFrameMHNDecisionProperties()
	{
		super();
		initGUI();
		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_ADMIN_MHN_DECISION"));

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jTextFieldDescription.requestFocus();
				jTextFieldDescription.setCaretPosition(jTextFieldDescription.getText().length());
				
				JButton4j btnSelect = new JButton4j("Background Color");
				btnSelect.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						 decis.setBackground(JColorChooser.showDialog(Common.mainForm, "Change Background",
								 decis.getBackground()));
						 jTextFieldDescription.setBackground(decis.getBackground());
						 jButtonUpdate.setEnabled(true);
					}
				});
				btnSelect.setBounds(46, 110, 133, 28);
				jDesktopPane1.add(btnSelect);
				{
					btnForground = new JButton4j("Forground Color");
					btnForground.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							 decis.setForeground(JColorChooser.showDialog(Common.mainForm, "Change Foreground",
									 decis.getForeground()));
							 jTextFieldDescription.setForeground(decis.getForeground());
							 jButtonUpdate.setEnabled(true);
							
						}
					});
					btnForground.setBounds(185, 110, 133, 28);
					jDesktopPane1.add(btnForground);
				}
				jTextFieldDescription.setBackground(decis.getBackground());
				jTextFieldDescription.setForeground(decis.getForeground());
				
				JLabel4j_std label = new JLabel4j_std();
				label.setText(lang.get("lbl_Pallet_Status"));
				label.setHorizontalAlignment(SwingConstants.TRAILING);
				label.setBounds(0, 73, 97, 21);
				jDesktopPane1.add(label);

			}
		});
	}

	public JInternalFrameMHNDecisionProperties(String decision)
	{

		this();

		jTextFieldReason.setText(decision);
		setTitle(getTitle() + " - " + decision);
		ltype = decision;

		decis.setDecision(ltype);
		decis.getDecisionProperties();

		jTextFieldReason.setText(decis.getDecision());
		jTextFieldDescription.setText(decis.getDescription());
		
		jComboBoxPalletStatus.setSelectedItem(decis.getStatus());
		jButtonUpdate.setEnabled(false);

	}

	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(389, 143));
			this.setBounds(0, 0, 391+Common.LFAdjustWidth, 226+Common.LFAdjustHeight);
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
					jLabel1.setText(lang.get("lbl_Decision"));
					jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel1.setBounds(0, 7, 98, 21);
				}
				{
					jTextFieldReason = new JTextField4j();
					jDesktopPane1.add(jTextFieldReason, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					jTextFieldReason.setHorizontalAlignment(SwingConstants.LEFT);
					jTextFieldReason.setEditable(false);
					jTextFieldReason.setEnabled(false);
					jTextFieldReason.setBounds(105, 7, 100, 21);
				}
				{
					jLabel3 = new JLabel4j_std();
					jDesktopPane1.add(jLabel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					jLabel3.setText(lang.get("lbl_Description"));
					jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel3.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel3.setBounds(0, 40, 98, 21);
				}
				{
					jTextFieldDescription = new JTextField4j();
					jDesktopPane1.add(jTextFieldDescription, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					jTextFieldDescription.setBounds(105, 40, 238, 21);
					jTextFieldDescription.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonUpdate.setEnabled(true);
						}
					});
					jTextFieldDescription.setFocusCycleRoot(true);
				}
				{
					jButtonUpdate = new JButton4j(Common.icon_update);
					jDesktopPane1.add(jButtonUpdate, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					jButtonUpdate.setEnabled(false);
					jButtonUpdate.setText(lang.get("btn_Save"));
					jButtonUpdate.setMnemonic(lang.getMnemonicChar());
					jButtonUpdate.setHorizontalTextPosition(SwingConstants.RIGHT);
					jButtonUpdate.setBounds(10, 144, 112, 28);
					jButtonUpdate.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							decis.setDescription(jTextFieldDescription.getText());
							decis.setStatus((String) jComboBoxPalletStatus.getSelectedItem());
							decis.update();
							jButtonUpdate.setEnabled(false);
						}
					});
				}
				{
					jButtonHelp = new JButton4j(Common.icon_help);
					jDesktopPane1.add(jButtonHelp);
					jButtonHelp.setText(lang.get("btn_Help"));
					jButtonHelp.setMnemonic(lang.getMnemonicChar());
					jButtonHelp.setBounds(129, 144, 112, 28);
				}
				{
					jButtonClose = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonClose, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					jButtonClose.setText(lang.get("btn_Close"));
					jButtonClose.setMnemonic(lang.getMnemonicChar());
					jButtonClose.setBounds(248, 144, 112, 28);
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							dispose();
						}
					});
				}
				{
					ComboBoxModel jComboBoxDefaultPalletStatusModel = new DefaultComboBoxModel(Common.palletStatusIncBlank);
					jComboBoxPalletStatus = new JComboBox4j();
					jComboBoxPalletStatus.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							jButtonUpdate.setEnabled(true);
						}
					});
					jDesktopPane1.add(jComboBoxPalletStatus);
					jComboBoxPalletStatus.setModel(jComboBoxDefaultPalletStatusModel);
					jComboBoxPalletStatus.setBounds(105, 73, 213, 23);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
