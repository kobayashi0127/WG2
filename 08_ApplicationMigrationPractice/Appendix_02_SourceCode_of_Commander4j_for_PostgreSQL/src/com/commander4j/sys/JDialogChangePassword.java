/*
 * Created on 31-Jul-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.commander4j.sys;


import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.AbstractDocument;

import com.commander4j.db.JDBUser;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JTextField4j;
import com.commander4j.util.JFixedSizeFilter;
import com.commander4j.util.JUtility;

public class JDialogChangePassword extends JDialog
{
	private static final long serialVersionUID = 1;
	public boolean validated = false;
	public String password;
	public String username;

	private JButton4j btn_change;
	private JButton4j btn_cancel;

	private JTextField4j fld_userName;
	private JPasswordField fld_password;
	private JPasswordField fld_new_password;
	private JPasswordField fld_verify_new_password;

	final int screenWidth = 260;
	final int screenHeight = 190;
	private JLabel4j_std label;
	private JLabel4j_std label_1;
	private JLabel4j_std lblNewPassword;
	private JLabel4j_std lblVerifyPassword;

	public JDialogChangePassword(Frame parent, String DefaultUserName, String DefaultPassword)
	{
		super(parent, "Change Password", true);
		setTitle("Change Password (" + Common.hostList.getHost(Common.selectedHostID).getSiteDescription() + ")");

		fld_userName = new JTextField4j(20);
		fld_userName.setEnabled(false);
		fld_userName.setSize(128, 22);
		fld_userName.setLocation(137, 12);
		fld_userName.setDisabledTextColor(Common.color_textdisabled);
		fld_password = new JPasswordField(10);
		fld_password.setEnabled(false);
		fld_password.setSize(128, 22);
		fld_password.setLocation(137, 46);
		fld_password.setDisabledTextColor(Common.color_textdisabled);
		fld_new_password =  new JPasswordField(10);
		fld_new_password.setBounds(137, 80, 128, 22);
		
		AbstractDocument doc1 = (AbstractDocument) fld_new_password.getDocument();
		doc1.setDocumentFilter(new JFixedSizeFilter(JDBUser.field_password));
		
		fld_verify_new_password = new JPasswordField(10);
		fld_verify_new_password.setSize(128, 22);
		fld_verify_new_password.setLocation(137, 114);
		
		AbstractDocument doc2 = (AbstractDocument) fld_verify_new_password.getDocument();
		doc2.setDocumentFilter(new JFixedSizeFilter(JDBUser.field_password));

		if (DefaultUserName.equals("") == false)
		{
			fld_userName.setText(DefaultUserName);
			fld_userName.setEnabled(false);
		}

		if (DefaultPassword.equals("") == false)
		{
			fld_password.setText(DefaultPassword);
			fld_password.setEnabled(false);
		}

		/* Create button objects */
		btn_change = new JButton4j("Change", Common.icon_ok);
		btn_change.setSize(111, 30);
		btn_change.setLocation(18, 148);
		btn_change.setMnemonic('L');
		btn_change.setToolTipText("Confirm password change.");
		btn_cancel = new JButton4j("Cancel", Common.icon_cancel);
		btn_cancel.setLocation(147, 148);
		btn_cancel.setSize(111, 30);
		btn_cancel.setMnemonic('C');
		btn_cancel.setToolTipText("Cancel password change.");

		ButtonHandler buttonhandler = new ButtonHandler();

		btn_change.addActionListener(buttonhandler);
		btn_cancel.addActionListener(buttonhandler);

		KeyboardHandler keyboardhandler = new KeyboardHandler();

		btn_change.addKeyListener(keyboardhandler);
		btn_cancel.addKeyListener(keyboardhandler);
		fld_userName.addKeyListener(keyboardhandler);
		fld_password.addKeyListener(keyboardhandler);
		fld_new_password.addKeyListener(keyboardhandler);
		fld_verify_new_password.addKeyListener(keyboardhandler);

		addKeyListener(keyboardhandler);

		fld_userName.setEditable(false);
		fld_password.setEditable(false);

		getRootPane().setDefaultButton(btn_change);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		this.setSize(284, 213);
		setLocation((screenSize.width - screenWidth) / 2, (screenSize.height - screenHeight) / 2);
		setResizable(false);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		getContentPane().add(fld_userName);
		getContentPane().add(fld_password);
		getContentPane().add(fld_new_password);
		getContentPane().add(fld_verify_new_password);
		getContentPane().add(btn_change);
		getContentPane().add(btn_cancel);
		
		label = new JLabel4j_std("Username :");
		label.setHorizontalAlignment(SwingConstants.TRAILING);
		label.setBounds(12, 12, 111, 22);
		getContentPane().add(label);
		
		label_1 = new JLabel4j_std("Password :");
		label_1.setHorizontalAlignment(SwingConstants.TRAILING);
		label_1.setBounds(12, 46, 111, 22);
		getContentPane().add(label_1);
		
		lblNewPassword = new JLabel4j_std("New Password :");
		lblNewPassword.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewPassword.setBounds(12, 80, 111, 22);
		getContentPane().add(lblNewPassword);
		
		lblVerifyPassword = new JLabel4j_std("Verify Password :");
		lblVerifyPassword.setHorizontalAlignment(SwingConstants.TRAILING);
		lblVerifyPassword.setBounds(12, 114, 111, 22);
		getContentPane().add(lblVerifyPassword);
		setVisible(true);
		
		SwingUtilities.invokeLater(new Runnable()
		{
			@SuppressWarnings("deprecation")
			public void run()
			{
				fld_new_password.requestFocus();
				fld_new_password.setCaretPosition(fld_new_password.getText().length());
			}
		});
	}

	private class KeyboardHandler implements KeyListener
	{
		// handle press of any key
		public void keyPressed(KeyEvent event) {
			if (event.getKeyCode() == 27)
			{
				validated = false;
				username = "";
				password = "";
				dispose();
			}
		}

		// handle release of any key
		public void keyReleased(KeyEvent event) {
		}

		// handle press of an action key
		public void keyTyped(KeyEvent event) {
		}
	}

	private class ButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == btn_change)
			{
				password = new String(fld_password.getPassword());
				username = fld_userName.getText().toUpperCase();
				String pass1 = new String(fld_new_password.getPassword());
				String pass2 = new String(fld_verify_new_password.getPassword());
				JDBUser user = new JDBUser(Common.selectedHostID, Common.sessionID);

				user.setUserId(username);
				user.setPassword(password);
				user.setPasswordNew(pass1);
				user.setPasswordVerify(pass2);
				if (user.changePassword())
				{
					validated = true;
					Common.userList.addUser(Common.sessionID, user);
					dispose();
				}
				else
				{
					validated = false;
					fld_new_password.setText("");
					fld_verify_new_password.setText("");
					fld_new_password.requestFocus();
					JUtility.errorBeep();
					JOptionPane.showMessageDialog(null, user.getErrorMessage());
				}
			}

			if (event.getSource() == btn_cancel)
			{
				validated = false;
				username = "";
				password = "";
				dispose();
			}
		}
	}
}
