package com.commander4j.sys;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.text.AbstractDocument;

import com.commander4j.db.JDBUser;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JTextField4j;
import com.commander4j.util.JFixedSizeFilter;
import com.commander4j.util.JUtility;

public class JDialogLogin extends JDialog
{
	private class ButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			final JDialogChangePassword chg;

			if (event.getSource() == btn_login)
			{
				password = new String(fld_password.getPassword());
				username = fld_userName.getText().toUpperCase();
				validated = false;

				user.setUserId(username);
				user.setPassword(password);
				if (user.login())
				{
					// Common.userList.addUser(Common.sessionID, user);
					if ((chgPassword.isSelected() == true) || (user.isPasswordExpired() == true))
					{
						if (user.isPasswordChangeAllowed() == true)
						{
							chg = new JDialogChangePassword(null, username, password);
							if (chg.validated == true)
							{
								password = chg.password;
							}
						}
						else
						{
							JUtility.errorBeep();
							JOptionPane.showMessageDialog(null, "Your user profile does not permit password to be changed.", "Login Error", JOptionPane.WARNING_MESSAGE);
						}
					}
					validated = true;
					Common.userList.addUser(Common.sessionID, user);
					dispose();
				}
				else
				{
					JUtility.errorBeep();
					JOptionPane.showMessageDialog(null, user.getErrorMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
				}

			}

			if (event.getSource() == btn_close)
			{
				validated = false;
				username = "";
				password = "";
				dispose();
			}
		}
	}

	private class KeyboardHandler implements KeyListener
	{
		// handle press of any key
		public void keyPressed(KeyEvent event)
		{
			if (event.getKeyCode() == 27)
			{
				validated = false;
				username = "";
				password = "";
				dispose();
			}
		}

		// handle release of any key
		public void keyReleased(KeyEvent event)
		{
		}

		// handle press of an action key
		public void keyTyped(KeyEvent event)
		{
		}
	}

	private static final long serialVersionUID = 1;
	public boolean validated = false;

	// private boolean change_password = false;

	public String password;
	public String username;

	private JButton4j btn_login;
	private JButton4j btn_close;
	private JTextField4j fld_userName;

	private JPasswordField fld_password;

	// private JDBUser user = new JDBUser();

	private JDBUser user;
	private JCheckBox chgPassword;

	final int screenWidth = 250;

	final int screenHeight = 150;

	public JDialogLogin(Frame parent) {

		super(parent, "Login", true);
		user = new JDBUser(Common.selectedHostID, Common.sessionID);

		setTitle("Login (" + Common.hostList.getHost(Common.selectedHostID).getSiteDescription() + ")");

		JLabel lname = new JLabel("User name");
		lname.setFont(Common.font_std);
		JLabel lpassword = new JLabel("Password");
		lpassword.setFont(Common.font_std);

		ButtonHandler buttonhandler = new ButtonHandler();

		KeyboardHandler keyboardhandler = new KeyboardHandler();
		getContentPane().setLayout(null);
		fld_userName = new JTextField4j(20);
		fld_userName.setBounds(103, 12, 150, 22);
		getContentPane().add(fld_userName);
		AbstractDocument doc1 = (AbstractDocument) fld_userName.getDocument();
		doc1.setDocumentFilter(new JFixedSizeFilter(JDBUser.field_user_id));

		fld_userName.setFont(Common.font_std);
		fld_userName.setText(System.getProperty("user.name"));
		fld_userName.setCaretPosition(fld_userName.getText().length());
		btn_close = new JButton4j("Close", Common.icon_cancel);
		btn_close.setBounds(142, 101, 104, 25);
		getContentPane().add(btn_close);
		btn_close.setMnemonic('C');
		btn_close.setToolTipText("Click to cancel logon.");
		btn_login = new JButton4j("Login", Common.icon_ok);
		btn_login.setBounds(19, 101, 104, 25);
		getContentPane().add(btn_login);
		btn_login.setMnemonic('L');
		btn_login.setToolTipText("Click to validate logon.");
		btn_login.addActionListener(buttonhandler);

		btn_login.addKeyListener(keyboardhandler);

		getRootPane().setDefaultButton(btn_login);
		chgPassword = new JCheckBox("Change Password");
		chgPassword.setBounds(81, 70, 133, 20);
		getContentPane().add(chgPassword);
		chgPassword.setFont(Common.font_std);
		chgPassword.setMnemonic('P');
		chgPassword.setToolTipText("Check to change password.");
		fld_password = new JPasswordField(10);
		fld_password.setBounds(103, 40, 150, 22);
		getContentPane().add(fld_password);
		AbstractDocument doc2 = (AbstractDocument) fld_password.getDocument();
		doc2.setDocumentFilter(new JFixedSizeFilter(JDBUser.field_password));
		fld_password.setFont(Common.font_std);

		JLabel lblUsername = new JLabel("Username :");
		lblUsername.setHorizontalAlignment(SwingConstants.TRAILING);
		lblUsername.setBounds(12, 14, 83, 16);
		getContentPane().add(lblUsername);

		JLabel lblPassword = new JLabel("Password :");
		lblPassword.setHorizontalAlignment(SwingConstants.TRAILING);
		lblPassword.setBounds(12, 46, 83, 16);
		getContentPane().add(lblPassword);
		fld_password.addKeyListener(keyboardhandler);
		chgPassword.addKeyListener(keyboardhandler);
		btn_close.addActionListener(buttonhandler);
		btn_close.addKeyListener(keyboardhandler);
		fld_userName.addKeyListener(keyboardhandler);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		this.setSize(271, 166);
		setLocation((screenSize.width - screenWidth) / 2, (screenSize.height - screenHeight) / 2);
		setResizable(false);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);

	}
}
