package com.commander4j.sys;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JDesktopPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;

import com.commander4j.db.JDBGroup;
import com.commander4j.db.JDBLanguage;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JTextField4j;
import com.commander4j.util.JFixedSizeFilter;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JInternalFrameGroupProperties extends javax.swing.JInternalFrame
{
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JTextField4j jTextFieldGroupId;
	private JLabel4j_std jLabel2;
	private JButton4j jButtonClose;
	private JButton4j jButtonHelp;
	private JTextField4j jTextFieldDescription;
	private JButton4j jButtonUpdate;
	private JLabel4j_std jLabel1;
	private String lgroupid;
	private JDBGroup group = new JDBGroup(Common.selectedHostID, Common.sessionID);
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);

	public JInternalFrameGroupProperties()
	{
		super();
		initGUI();

		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_ADMIN_GROUP_EDIT"));

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle window = getBounds();
		setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jTextFieldDescription.requestFocus();
				jTextFieldDescription.setCaretPosition(jTextFieldDescription.getText().length());
			}
		});

	}

	public JInternalFrameGroupProperties(String groupid)
	{

		this();

		setTitle(getTitle() + " - " + groupid);
		lgroupid = groupid;

		group.setGroupId(lgroupid);
		group.getGroupProperties();
		jTextFieldGroupId.setText(lgroupid);
		jTextFieldDescription.setText(group.getDescription());

	}

	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(366, 145));
			this.setBounds(0, 0, 380+Common.LFAdjustWidth, 155+Common.LFAdjustHeight);
			setVisible(true);
			this.setClosable(true);
			this.setTitle("Group Properties");
			this.setIconifiable(true);
			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				this.getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
				jDesktopPane1.setPreferredSize(new java.awt.Dimension(361, 104));

				jDesktopPane1.setLayout(null);
				{
					jTextFieldGroupId = new JTextField4j();
					jDesktopPane1.add(jTextFieldGroupId, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					jTextFieldGroupId.setPreferredSize(new java.awt.Dimension(100, 20));
					jTextFieldGroupId.setHorizontalAlignment(SwingConstants.LEFT);
					jTextFieldGroupId.setBounds(101, 10, 91, 21);
					jTextFieldGroupId.setEditable(false);
					jTextFieldGroupId.setEnabled(false);
					jTextFieldGroupId.setDisabledTextColor(Common.color_textdisabled);

				}
				{
					jLabel1 = new JLabel4j_std();
					jDesktopPane1.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					jLabel1.setText(lang.get("lbl_Group_ID"));
					jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel1.setBounds(5, 10, 83, 21);
				}
				{
					jLabel2 = new JLabel4j_std();
					jDesktopPane1.add(jLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					jLabel2.setText(lang.get("lbl_Description"));
					jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel2.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel2.setBounds(5, 37, 86, 21);
				}
				{

					jButtonUpdate = new JButton4j(Common.icon_update);
					jDesktopPane1.add(jButtonUpdate, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					jButtonUpdate.setText(lang.get("btn_Save"));
					jButtonUpdate.setHorizontalTextPosition(SwingConstants.RIGHT);
					jButtonUpdate.setMnemonic(lang.getMnemonicChar());
					jButtonUpdate.setBounds(5, 70, 110, 30);
					jButtonUpdate.setEnabled(false);
					jButtonUpdate.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							group.setDescription(jTextFieldDescription.getText());
							group.update();
							jButtonUpdate.setEnabled(false);
						}
					});
				}
				{

					jButtonClose = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonClose, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					jButtonClose.setText(lang.get("btn_Close"));
					jButtonClose.setMnemonic(lang.getMnemonicChar());
					jButtonClose.setBounds(239, 70, 110, 30);
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							dispose();
						}
					});
				}
				{
					jTextFieldDescription = new JTextField4j();
					jDesktopPane1.add(jTextFieldDescription, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					AbstractDocument doc = (AbstractDocument) jTextFieldDescription.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(JDBGroup.field_description));
					jTextFieldDescription.setPreferredSize(new java.awt.Dimension(40, 20));
					jTextFieldDescription.setBounds(101, 37, 237, 21);
					jTextFieldDescription.setFocusCycleRoot(true);
					jTextFieldDescription.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonUpdate.setEnabled(true);
						}
					});
				}
				{
					jButtonHelp = new JButton4j(Common.icon_help);
					jDesktopPane1.add(jButtonHelp);
					jButtonHelp.setText(lang.get("btn_Help"));
					jButtonHelp.setBounds(121, 70, 110, 30);
					jButtonHelp.setMnemonic(lang.getMnemonicChar());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
