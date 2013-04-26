package com.commander4j.sys;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;

import com.commander4j.calendar.JCalendarButton;
import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBUser;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JComboBox4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JTextField4j;
import com.commander4j.util.JDateControl;
import com.commander4j.util.JFixedSizeFilter;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JInternalFrameUserProperties extends javax.swing.JInternalFrame
{
	private JLabel4j_std jLabel2_1;
	private JTextField4j jTextFieldEmailAddress;
	private static final long serialVersionUID = 1;
	private JButton4j jButtonCancel;
	private JButton4j jButtonSave;
	private JCheckBox jCheckBoxPasswordChangeAllowed;
	private JCheckBox jCheckBoxPasswordExpires;
	private JCheckBox jCheckBoxAccountLocked;
	private JLabel4j_std jLabel11;
	private JTextField4j jTextFieldBadPasswords;
	private JLabel4j_std jLabel10;
	private JLabel4j_std jLabel9;
	private JLabel4j_std jLabel8;
	private JLabel4j_std jLabel7;
	private JLabel4j_std jLabel6;
	private JLabel4j_std jLabel5;
	private JTextField4j jTextFieldLastPasswordChange;
	private JTextField4j jTextFieldLastLogon;
	private JComboBox4j jComboBoxLanguage;
	private JPasswordField jPasswordField2;
	private JPasswordField jPasswordField1;
	private JTextField4j jTextFieldComment;
	private JTextField4j jTextFieldUserID;
	private JLabel4j_std jLabel4;
	private JLabel4j_std jLabelAccountExpiryDate;
	private JLabel4j_std jLabelAccountExpires;
	private JCheckBox jCheckBoxAccountExpires;
	private JButton4j jButtonHelp;
	private JLabel4j_std jLabel3;
	private JLabel4j_std jLabel2;
	private JLabel4j_std jLabel1;
	private JDesktopPane jDesktopPane1;
	private String luserid;
	private JDBUser user = new JDBUser(Common.selectedHostID, Common.sessionID);
	private boolean userUpdated;
	private boolean userPasswordUpdated;
	private Object currentLanguage = new Object();
	private Object newLanguage = new Object();
	private JDateControl accountExpiryDate = new JDateControl();
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);
	private JCalendarButton calendarButton;

	public JInternalFrameUserProperties()
	{
		initGUI();

		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_ADMIN_USER_EDIT"));

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jTextFieldComment.requestFocus();
				jTextFieldComment.setCaretPosition(jTextFieldComment.getText().length());

			}
		});
	}

	public JInternalFrameUserProperties(String userid)
	{

		this();

		jTextFieldUserID.setText(userid);
		setTitle(getTitle() + " - " + userid);
		luserid = userid;

		user.setUserId(luserid);
		user.getUserProperties();

		jTextFieldComment.setText(user.getComment());
		jTextFieldEmailAddress.setText(user.getEmailAddress());
		jPasswordField1.setText(user.getPassword());
		jPasswordField2.setText(user.getPassword());
		jComboBoxLanguage.setSelectedItem(user.getLanguage());

		try
		{
			jTextFieldLastLogon.setText(DateFormat.getDateTimeInstance().format(user.getLastLoginTimestamp()));
		}
		catch (Exception e)
		{
			jTextFieldLastLogon.setText("Never");
		}

		try
		{
			jTextFieldLastPasswordChange.setText(DateFormat.getDateTimeInstance().format(user.getPasswordChanged()));
		}
		catch (Exception e)
		{
			jTextFieldLastLogon.setText("Never");
		}

		if (user.isAccountLocked())
			jCheckBoxAccountLocked.setSelected(true);
		else
			jCheckBoxAccountLocked.setSelected(false);

		if (user.isAccountExpiring())
			jCheckBoxAccountExpires.setSelected(true);
		else
			jCheckBoxAccountExpires.setSelected(false);

		setExpiryDateVisibility();

		if (user.isPasswordChangeAllowed())
			jCheckBoxPasswordChangeAllowed.setSelected(true);
		else
			jCheckBoxPasswordChangeAllowed.setSelected(false);

		if (user.isPasswordExpiring())
			jCheckBoxPasswordExpires.setSelected(true);
		else
			jCheckBoxPasswordExpires.setSelected(false);

		jTextFieldBadPasswords.setText("" + user.getBadPasswordAttempts());

		try
		{
			accountExpiryDate.setDate(user.getAccountExpiryDate());
		}
		catch (Exception e)
		{

		}

		resetChanges();

	}

	private void resetChanges() {
		userUpdated = false;
		userPasswordUpdated = false;
		currentLanguage = jComboBoxLanguage.getSelectedItem();
		jButtonSave.setEnabled(false);
	}

	private void setExpiryDateVisibility() {
		if (jCheckBoxAccountExpires.isSelected())
		{
			calendarButton.setEnabled(true);
			accountExpiryDate.setEnabled(true);
		}
		else
		{
			accountExpiryDate.setEnabled(false);
			calendarButton.setEnabled(false);
		}
	}

	public void initGUI() {
		try
		{
			preInitGUI();

			jDesktopPane1 = new JDesktopPane();
			jDesktopPane1.setBackground(Color.WHITE);
			jLabel2 = new JLabel4j_std();
			jLabel3 = new JLabel4j_std();
			jLabel1 = new JLabel4j_std();
			jLabel4 = new JLabel4j_std();
			jTextFieldUserID = new JTextField4j();
			jTextFieldComment = new JTextField4j();
			jPasswordField1 = new JPasswordField();
			jPasswordField2 = new JPasswordField();
			jComboBoxLanguage = new JComboBox4j();
			jTextFieldLastLogon = new JTextField4j();
			jTextFieldLastPasswordChange = new JTextField4j();
			jLabel5 = new JLabel4j_std();
			jLabel6 = new JLabel4j_std();
			jLabel7 = new JLabel4j_std();
			jLabel8 = new JLabel4j_std();
			jLabel9 = new JLabel4j_std();
			jLabel10 = new JLabel4j_std();
			jTextFieldBadPasswords = new JTextField4j();
			jLabel11 = new JLabel4j_std();
			jCheckBoxAccountLocked = new JCheckBox();
			jCheckBoxPasswordExpires = new JCheckBox();
			jCheckBoxPasswordChangeAllowed = new JCheckBox();

			jButtonSave = new JButton4j(Common.icon_update);
			jButtonCancel = new JButton4j(Common.icon_close);

			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setTitle("User Properties");
			this.setClosable(true);
			this.setVisible(true);
			this.setPreferredSize(new java.awt.Dimension(417, 432));
			this.setBounds(0, 0, 430+Common.LFAdjustWidth, 479+Common.LFAdjustHeight);
			this.setIconifiable(true);

			jDesktopPane1.setPreferredSize(new java.awt.Dimension(364, 329));
			jDesktopPane1.setOpaque(true);
			this.getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
			jDesktopPane1.setLayout(null);

			jLabel2.setText(lang.get("lbl_Description"));
			jDesktopPane1.add(jLabel2);
			jLabel2.setBounds(0, 35, 158, 20);
			jLabel2.setHorizontalAlignment(SwingConstants.TRAILING);

			jLabel3.setText(lang.get("lbl_User_Account_Password"));
			jDesktopPane1.add(jLabel3);
			jLabel3.setBounds(0, 62, 158, 20);
			jLabel3.setHorizontalAlignment(SwingConstants.TRAILING);

			jLabel1.setText(lang.get("lbl_User_ID"));

			jDesktopPane1.add(jLabel1);
			jLabel1.setFocusTraversalKeysEnabled(false);
			jLabel1.setBounds(0, 7, 158, 20);
			jLabel1.setHorizontalAlignment(SwingConstants.TRAILING);

			jLabel4.setText(lang.get("lbl_User_Account_Password_Verify"));
			jDesktopPane1.add(jLabel4);
			jLabel4.setBounds(0, 89, 158, 20);
			jLabel4.setHorizontalAlignment(SwingConstants.TRAILING);

			jTextFieldUserID.setEditable(false);
			jTextFieldUserID.setEnabled(false);
			jTextFieldUserID.setPreferredSize(new java.awt.Dimension(100, 20));
			jTextFieldUserID.setDisabledTextColor(Common.color_textdisabled);
			jDesktopPane1.add(jTextFieldUserID);
			jTextFieldUserID.setBounds(172, 7, 100, 20);

			AbstractDocument doc = (AbstractDocument) jTextFieldComment.getDocument();
			doc.setDocumentFilter(new JFixedSizeFilter(JDBUser.field_comment));
			jDesktopPane1.add(jTextFieldComment);
			jTextFieldComment.setBounds(172, 34, 217, 21);
			jTextFieldComment.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent evt) {
					jTextFieldCommentKeyTyped(evt);
				}
			});

			jPasswordField1.setPreferredSize(new java.awt.Dimension(100, 20));
			AbstractDocument doc1 = (AbstractDocument) jPasswordField1.getDocument();
			doc1.setDocumentFilter(new JFixedSizeFilter(JDBUser.field_password));
			jDesktopPane1.add(jPasswordField1);
			jPasswordField1.setBounds(172, 62, 100, 20);
			jPasswordField1.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent evt) {
					jPasswordField1KeyTyped(evt);
				}
			});

			jPasswordField2.setPreferredSize(new java.awt.Dimension(100, 20));
			AbstractDocument doc2 = (AbstractDocument) jPasswordField2.getDocument();
			doc2.setDocumentFilter(new JFixedSizeFilter(JDBUser.field_password));
			jDesktopPane1.add(jPasswordField2);
			jPasswordField2.setBounds(172, 89, 100, 20);
			jPasswordField2.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent evt) {
					jPasswordField2KeyTyped(evt);
				}
			});

			jComboBoxLanguage.setEnabled(true);
			jComboBoxLanguage.setEditable(false);
			jComboBoxLanguage.setLightWeightPopupEnabled(true);
			jComboBoxLanguage.setPreferredSize(new java.awt.Dimension(45, 21));
			jComboBoxLanguage.setIgnoreRepaint(false);
			jDesktopPane1.add(jComboBoxLanguage);
			jComboBoxLanguage.setBounds(172, 116, 69, 21);
			jComboBoxLanguage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jComboBoxLanguageActionPerformed(evt);
				}
			});

			jTextFieldLastLogon.setEditable(false);
			jTextFieldLastLogon.setPreferredSize(new java.awt.Dimension(150, 20));
			jDesktopPane1.add(jTextFieldLastLogon);
			jTextFieldLastLogon.setBounds(172, 144, 150, 20);
			jTextFieldLastLogon.setEnabled(false);
			jTextFieldLastLogon.setDisabledTextColor(Common.color_textdisabled);

			jTextFieldLastPasswordChange.setEditable(false);
			jTextFieldLastPasswordChange.setPreferredSize(new java.awt.Dimension(150, 20));
			jDesktopPane1.add(jTextFieldLastPasswordChange);
			jTextFieldLastPasswordChange.setBounds(172, 171, 150, 20);
			jTextFieldLastPasswordChange.setEnabled(false);
			jTextFieldLastPasswordChange.setDisabledTextColor(Common.color_textdisabled);

			jLabel5.setText(lang.get("lbl_Language"));
			jDesktopPane1.add(jLabel5);
			jLabel5.setBounds(0, 117, 158, 20);
			jLabel5.setHorizontalAlignment(SwingConstants.TRAILING);

			jLabel6.setText(lang.get("lbl_User_Account_Last_Logon"));
			jDesktopPane1.add(jLabel6);
			jLabel6.setBounds(0, 144, 158, 20);
			jLabel6.setHorizontalAlignment(SwingConstants.TRAILING);

			jLabel7.setText(lang.get("lbl_User_Account_Last_Password_Change"));
			jDesktopPane1.add(jLabel7);
			jLabel7.setBounds(0, 171, 158, 20);
			jLabel7.setHorizontalAlignment(SwingConstants.TRAILING);

			jLabel8.setText(lang.get("lbl_User_Account_Locked"));
			jDesktopPane1.add(jLabel8);
			jLabel8.setBounds(0, 199, 158, 20);
			jLabel8.setHorizontalAlignment(SwingConstants.TRAILING);

			jLabel9.setText(lang.get("lbl_User_Account_Password_Expires"));
			jDesktopPane1.add(jLabel9);
			jLabel9.setBounds(0, 227, 158, 20);
			jLabel9.setHorizontalAlignment(SwingConstants.TRAILING);

			jLabel10.setText(lang.get("lbl_User_Account_Bad_Passwords"));
			jDesktopPane1.add(jLabel10);
			jLabel10.setFocusTraversalKeysEnabled(false);
			jLabel10.setBounds(0, 282, 158, 20);
			jLabel10.setHorizontalAlignment(SwingConstants.TRAILING);

			jTextFieldBadPasswords.setEditable(false);
			jTextFieldBadPasswords.setPreferredSize(new java.awt.Dimension(20, 20));
			jDesktopPane1.add(jTextFieldBadPasswords);
			jTextFieldBadPasswords.setBounds(172, 282, 20, 20);
			jTextFieldBadPasswords.setEnabled(false);
			jTextFieldBadPasswords.setDisabledTextColor(Common.color_textdisabled);

			jLabel11.setText(lang.get("lbl_User_Account_Password_Change_Allowed"));
			jDesktopPane1.add(jLabel11);
			jLabel11.setBounds(0, 255, 158, 20);
			jLabel11.setHorizontalAlignment(SwingConstants.TRAILING);

			jDesktopPane1.add(jCheckBoxAccountLocked);
			jCheckBoxAccountLocked.setBounds(170, 198, 21, 21);
			jCheckBoxAccountLocked.setBackground(new java.awt.Color(255, 255, 255));
			jCheckBoxAccountLocked.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jCheckBoxAccountLockedActionPerformed(evt);
				}
			});

			jDesktopPane1.add(jCheckBoxPasswordExpires);
			jCheckBoxPasswordExpires.setBounds(169, 226, 21, 21);
			jCheckBoxPasswordExpires.setFont(Common.font_std);
			jCheckBoxPasswordExpires.setBackground(new java.awt.Color(255, 255, 255));
			jCheckBoxPasswordExpires.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jCheckBoxPasswordExpiresActionPerformed(evt);
				}
			});

			jDesktopPane1.add(jCheckBoxPasswordChangeAllowed);
			jCheckBoxPasswordChangeAllowed.setBounds(169, 254, 21, 21);
			jCheckBoxPasswordChangeAllowed.setBackground(new java.awt.Color(255, 255, 255));
			jCheckBoxPasswordChangeAllowed.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jCheckBoxPasswordChangeAllowedActionPerformed(evt);
				}
			});

			jButtonSave.setEnabled(false);
			jButtonSave.setText(lang.get("btn_Save"));
			jButtonSave.setMnemonic(lang.getMnemonicChar());
			jDesktopPane1.add(jButtonSave);
			jButtonSave.setBounds(39, 394, 112, 28);
			jButtonSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonUpdateActionPerformed(evt);
				}
			});

			jButtonCancel.setText(lang.get("btn_Close"));
			jButtonCancel.setMnemonic(lang.getMnemonicChar());
			jDesktopPane1.add(jButtonCancel);
			jButtonCancel.setBounds(277, 394, 112, 28);
			{
				jButtonHelp = new JButton4j(Common.icon_help);
				jDesktopPane1.add(jButtonHelp);
				jButtonHelp.setText(lang.get("btn_Help"));
				jButtonHelp.setBounds(158, 394, 112, 28);
				jButtonHelp.setMnemonic(lang.getMnemonicChar());
			}
			{
				jCheckBoxAccountExpires = new JCheckBox();
				jDesktopPane1.add(jCheckBoxAccountExpires);
				jCheckBoxAccountExpires.setBounds(169, 309, 22, 21);
				jCheckBoxAccountExpires.setBackground(new java.awt.Color(255, 255, 255));
				jCheckBoxAccountExpires.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						jCheckBoxAccountExpiresActionPerformed(evt);
					}
				});
			}
			{
				jLabelAccountExpires = new JLabel4j_std();
				jDesktopPane1.add(jLabelAccountExpires);
				jLabelAccountExpires.setText(lang.get("lbl_User_Account_Expires"));
				jLabelAccountExpires.setBounds(0, 310, 158, 20);
				jLabelAccountExpires.setHorizontalAlignment(SwingConstants.TRAILING);
			}
			{
				jLabelAccountExpiryDate = new JLabel4j_std();
				jDesktopPane1.add(jLabelAccountExpiryDate);
				jLabelAccountExpiryDate.setText(lang.get("lbl_User_Account_Expiry_Date"));
				jLabelAccountExpiryDate.setBounds(0, 339, 158, 20);
				jLabelAccountExpiryDate.setHorizontalAlignment(SwingConstants.TRAILING);
			}
			jButtonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed(evt);
				}
			});

			accountExpiryDate.getEditor().addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					userUpdated = true;
					jButtonSave.setEnabled(true);
				}
			});
			accountExpiryDate.addChangeListener(new ChangeListener() {
				public void stateChanged(final ChangeEvent e)

				{
					userUpdated = true;
					jButtonSave.setEnabled(true);
				}
			});

			accountExpiryDate.setBounds(172, 334, 125, 25);
			jDesktopPane1.add(accountExpiryDate);

			jTextFieldEmailAddress = new JTextField4j();
			jTextFieldEmailAddress.addKeyListener(new KeyAdapter() {
				public void keyTyped(final KeyEvent e) {
					userUpdated = true;
					jButtonSave.setEnabled(true);
				}
			});
			jTextFieldEmailAddress.setBounds(172, 366, 217, 21);
			jDesktopPane1.add(jTextFieldEmailAddress);

			jLabel2_1 = new JLabel4j_std();
			jLabel2_1.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabel2_1.setText(lang.get("lbl_User_Account_Email"));
			jLabel2_1.setBounds(0, 367, 158, 20);
			jDesktopPane1.add(jLabel2_1);
			
			calendarButton = new JCalendarButton(accountExpiryDate);
			calendarButton.setEnabled(false);
			calendarButton.setBounds(300, 336, 21, 21);
			jDesktopPane1.add(calendarButton);
			
			postInitGUI();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void preInitGUI() {
	}


	public void postInitGUI() {
		jComboBoxLanguage.addItem("EN");
		jComboBoxLanguage.addItem("DE");
		jComboBoxLanguage.addItem("FR");
		jComboBoxLanguage.addItem("HU");
		jComboBoxLanguage.addItem("IT");
		jComboBoxLanguage.addItem("NL");

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle window = getBounds();
		setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);

		resetChanges();

	}


	protected void jButtonUpdateActionPerformed(ActionEvent evt)

	{
		// try {
		if (userUpdated == true)
		{
			user.setComment(jTextFieldComment.getText());
			user.setEmailAddress(jTextFieldEmailAddress.getText());

			if (jCheckBoxPasswordChangeAllowed.isSelected())
				user.setPasswordChangeAllowed("Y");
			else
				user.setPasswordChangeAllowed("N");

			if (jCheckBoxPasswordExpires.isSelected())
				user.setPasswordExpires("Y");
			else
				user.setPasswordExpires("N");

			if (jCheckBoxAccountExpires.isSelected())
				user.setAccountExpires("Y");
			else
				user.setAccountExpires("N");

			if (jCheckBoxAccountLocked.isSelected())
				user.setAccountLocked("Y");
			else
				user.setAccountLocked("N");

			user.setLanguage((String) jComboBoxLanguage.getSelectedItem());

			Date d = accountExpiryDate.getDate();
			user.setAccountExpiryDate(JUtility.getTimestampFromDate(d));

			if (user.update() == true)
			{
				String pass1 = new String(jPasswordField1.getPassword());
				String pass2 = new String(jPasswordField2.getPassword());
				user.setPasswordNew(pass1);
				user.setPasswordVerify(pass2);
				if (user.changePassword() == false)
				{
					JUtility.errorBeep();
					JOptionPane.showMessageDialog(null, user.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
				userUpdated = false;
				resetChanges();
			}
			else
			{
				JUtility.errorBeep();
				JOptionPane.showMessageDialog(null, user.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}

		}

		if (userPasswordUpdated == true)
		{
			String pass1 = new String(jPasswordField1.getPassword());
			String pass2 = new String(jPasswordField2.getPassword());
			user.setPasswordNew(pass1);
			user.setPasswordVerify(pass2);
			if (user.changePassword() == true)
			{
				userPasswordUpdated = false;
				resetChanges();
			}
			else
			{
				JUtility.errorBeep();
				JOptionPane.showMessageDialog(null, user.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	/** Auto-generated event handler method */
	protected void jButtonCancelActionPerformed(ActionEvent evt) {
		dispose();
	}

	/** Auto-generated event handler method */
	protected void jTextFieldCommentKeyTyped(KeyEvent evt) {
		userUpdated = true;
		jButtonSave.setEnabled(true);

	}

	/** Auto-generated event handler method */
	protected void jPasswordField1KeyTyped(KeyEvent evt) {
		userPasswordUpdated = true;
		jButtonSave.setEnabled(true);
	}

	/** Auto-generated event handler method */
	protected void jPasswordField2KeyTyped(KeyEvent evt) {
		userPasswordUpdated = true;
		jButtonSave.setEnabled(true);
	}

	/** Auto-generated event handler method */
	protected void jComboBoxLanguageActionPerformed(ActionEvent evt) {
		newLanguage = jComboBoxLanguage.getSelectedItem();
		if (newLanguage.equals(currentLanguage) != true)
		{
			userUpdated = true;
			jButtonSave.setEnabled(true);
		}
	}

	/** Auto-generated event handler method */
	protected void jCheckBoxAccountLockedActionPerformed(ActionEvent evt) {
		userUpdated = true;
		jButtonSave.setEnabled(true);
	}

	/** Auto-generated event handler method */
	protected void jCheckBoxPasswordExpiresActionPerformed(ActionEvent evt) {
		userUpdated = true;
		jButtonSave.setEnabled(true);
	}

	/** Auto-generated event handler method */
	protected void jCheckBoxPasswordChangeAllowedActionPerformed(ActionEvent evt) {
		userUpdated = true;
		if (jCheckBoxPasswordChangeAllowed.isSelected() == false)
		{
			jCheckBoxPasswordExpires.setSelected(false);
		}
		jButtonSave.setEnabled(true);
	}

	private void jCheckBoxAccountExpiresActionPerformed(ActionEvent evt) {
		userUpdated = true;
		jButtonSave.setEnabled(true);
		setExpiryDateVisibility();
	}
}
