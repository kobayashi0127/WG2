package com.commander4j.sys;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBListData;
import com.commander4j.db.JDBUser;
import com.commander4j.db.JDBUserJList;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JMenu4j;
import com.commander4j.gui.JMenuItem4j;
import com.commander4j.util.JExcel;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JInternalFrameUserAdmin extends javax.swing.JInternalFrame
{
	private JButton4j jButtonExcel;
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JDBUserJList jListUsers;
	private JButton4j jButtonRefresh;
	private JButton4j jButtonHelp;
	private JButton4j jButtonRename;
	private JButton4j jButtonPrint;
	private JButton4j jButtonPermissions;
	private JButton4j jButtonAdd;
	private JButton4j jButtonDelete;
	private JButton4j jButtonClose;
	private JButton4j jButtonUpdate;
	private JScrollPane jScrollPane1;
	private String luser_id;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);

	public JInternalFrameUserAdmin()
	{
		super();

		initGUI();

		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_ADMIN_USERS"));

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle window = getBounds();
		setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);

		populateList("");
	}

	private void add() {
		JDBUser u = new JDBUser(Common.selectedHostID, Common.sessionID);

		luser_id = JOptionPane.showInputDialog(Common.mainForm, "Enter new user id");
		if (luser_id != null)
		{
			if (luser_id.equals("") == false)
			{
				luser_id = luser_id.toUpperCase();
				if (u.create(luser_id, "password", "", true, true, false, "EN", false, "") == false)
				{
					JUtility.errorBeep();
					JOptionPane.showMessageDialog(Common.mainForm, u.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
				else
				{

					JLaunchMenu.runForm("FRM_ADMIN_USER_EDIT", luser_id);
				}
				populateList(luser_id);
			}
		}
	}

	private void delete() {
		if (jListUsers.isSelectionEmpty() == false)
		{
			// String luser_id = new String();
			luser_id = ((JDBListData) jListUsers.getSelectedValue()).toString();
			if (luser_id.equals(Common.userList.getUser(Common.sessionID).getUserId()) == false)
			{
				int n = JOptionPane.showConfirmDialog(Common.mainForm, "Delete User " + luser_id + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
				if (n == 0)
				{
					JDBUser u = new JDBUser(Common.selectedHostID, Common.sessionID);
					u.setUserId(luser_id);
					u.delete();
					populateList("");
				}
			}
			else
			{
				JUtility.errorBeep();
				JOptionPane.showMessageDialog(null, "Cannot delete current user " + luser_id + " !", "Information", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private void permissions() {
		if (jListUsers.isSelectionEmpty() == false)
		{
			luser_id = ((JDBListData) jListUsers.getSelectedValue()).toString();
			JLaunchMenu.runForm("FRM_ADMIN_USER_PERM", luser_id);
		}
	}

	private void rename() {
		if (jListUsers.isSelectionEmpty() == false)
		{
			String luser_id_from = ((JDBListData) jListUsers.getSelectedValue()).toString();
			String luser_id_to = new String();
			luser_id_to = JOptionPane.showInputDialog(Common.mainForm, "Rename to user id");
			if (luser_id_to != null)
			{
				if (luser_id_to.equals("") == false)
				{
					luser_id_to = luser_id_to.toUpperCase();
					JDBUser u = new JDBUser(Common.selectedHostID, Common.sessionID);
					u.setUserId(luser_id_from);
					if (u.renameTo(luser_id_to) == false)
					{
						JUtility.errorBeep();
						JOptionPane.showMessageDialog(Common.mainForm, u.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
					populateList(luser_id_to);
				}
			}
		}
	}

	private void populateList(String defaultitem) {
		DefaultComboBoxModel DefComboBoxMod = new DefaultComboBoxModel();

		JDBUser tempUser = new JDBUser(Common.selectedHostID, Common.sessionID);
		LinkedList<JDBListData> tempUserList = tempUser.getUserIds();

		int sel = -1;
		for (int j = 0; j < tempUserList.size(); j++)
		{
			DefComboBoxMod.addElement(tempUserList.get(j));
			if (tempUserList.get(j).toString().equals(defaultitem))
			{
				sel = j;
			}
		}
		// int sel = DefComboBoxMod.getIndexOf(defaultitem);
		ListModel jList1Model = DefComboBoxMod;
		jListUsers.setModel(jList1Model);
		jListUsers.setSelectedIndex(sel);
		jListUsers.setCellRenderer(Common.renderer_list);
		jListUsers.ensureIndexIsVisible(sel);
	}

	private void print() {
		JLaunchReport.runReport("RPT_USERS",null,"",null,"");
	}

	private void lock() {
		luser_id = ((JDBListData) jListUsers.getSelectedValue()).toString();
		JDBUser user = new JDBUser(Common.selectedHostID, Common.sessionID);
		user.getUserProperties(luser_id);
		user.setAccountLocked("Y");
		user.update();
		populateList(luser_id);
	}

	private void unlock() {
		luser_id = ((JDBListData) jListUsers.getSelectedValue()).toString();
		JDBUser user = new JDBUser(Common.selectedHostID, Common.sessionID);
		user.getUserProperties(luser_id);
		user.setAccountLocked("N");
		user.update();
		populateList(luser_id);
	}

	private void editRecord() {
		if (jListUsers.isSelectionEmpty() == false)
		{
			luser_id = ((JDBListData) jListUsers.getSelectedValue()).toString();
			JLaunchMenu.runForm("FRM_ADMIN_USER_EDIT", luser_id);
		}
	}

	private void excel() {
		JDBUser user = new JDBUser(Common.selectedHostID, Common.sessionID);
		JExcel export = new JExcel();
		export.saveAs("users.xls", user.getUserDataResultSet(), Common.mainForm);
	}

	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(273, 474));
			this.setBounds(0, 0, 368+Common.LFAdjustWidth, 574+Common.LFAdjustHeight);
			setVisible(true);
			this.setClosable(true);
			this.setTitle("User Admin");
			this.setIconifiable(true);
			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				this.getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
				jDesktopPane1.setPreferredSize(new java.awt.Dimension(235, 269));
				jDesktopPane1.setLayout(null);
				{
					jScrollPane1 = new JScrollPane();
					jDesktopPane1.add(jScrollPane1);
					jScrollPane1.setBounds(10, 10, 190, 503);
					jScrollPane1.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
					{
						ListModel jList1Model = new DefaultComboBoxModel(new String[] { "Item One", "Item Two" });
						jListUsers = new JDBUserJList(Common.selectedHostID, Common.sessionID);
						jScrollPane1.setViewportView(jListUsers);
						jListUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						jListUsers.addMouseListener(new MouseAdapter() {
							public void mouseClicked(MouseEvent evt) {
								if (evt.getClickCount() == 2)
								{
									if (Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_USER_EDIT") == true)
									{
										editRecord();
									}
								}
							}
						});
						jListUsers.setModel(jList1Model);
						jListUsers.setFont(Common.font_list);

						{
							final JPopupMenu popupMenu = new JPopupMenu();
							addPopup(jListUsers, popupMenu);

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_add);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										add();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Add"));
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_USER_ADD"));
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_delete);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										delete();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Delete"));
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_USER_DELETE"));
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_edit);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										editRecord();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Edit"));
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_USER_EDIT"));
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_permissions);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										permissions();
									}
								});
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_USER_PERM"));
								newItemMenuItem.setText(lang.get("btn_Permissions"));
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_rename);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										rename();
									}
								});
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_USER_RENAME"));
								newItemMenuItem.setText(lang.get("btn_Rename"));
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_print);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										print();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Print"));
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("RPT_USERS"));
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_XLS);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										excel();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Excel"));
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenu4j accountMenu = new JMenu4j();
								accountMenu.setText(lang.get("lbl_User_Account"));
								accountMenu.setIcon(Common.icon_user);
								popupMenu.add(accountMenu);

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											lock();
										}
									});
									newItemMenuItem.setText(lang.get("lbl_User_Account_Lock"));
									accountMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											unlock();
										}
									});
									newItemMenuItem.setText(lang.get("lbl_User_Account_Unlock"));
									accountMenu.add(newItemMenuItem);
								}
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_refresh);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										populateList("");
									}
								});
								newItemMenuItem.setText(lang.get("btn_Refresh"));
								popupMenu.add(newItemMenuItem);
							}
						}
					}
				}
				{
					jButtonAdd = new JButton4j(Common.icon_add);
					jDesktopPane1.add(jButtonAdd);
					jButtonAdd.setText(lang.get("btn_Add"));
					jButtonAdd.setBounds(212, 10, 125, 30);
					jButtonAdd.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_USER_ADD"));
					jButtonAdd.setMnemonic(lang.getMnemonicChar());
					jButtonAdd.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							add();
						}
					});
				}
				{

					jButtonDelete = new JButton4j(Common.icon_delete);
					jDesktopPane1.add(jButtonDelete);
					jButtonDelete.setText(lang.get("btn_Delete"));
					jButtonDelete.setBounds(212, 41, 125, 30);
					jButtonDelete.setMnemonic(lang.getMnemonicChar());
					jButtonDelete.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_USER_DELETE"));
					jButtonDelete.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							delete();

						}
					});
				}
				{
					jButtonUpdate = new JButton4j(Common.icon_edit);
					jDesktopPane1.add(jButtonUpdate);
					jButtonUpdate.setText(lang.get("btn_Edit"));
					jButtonUpdate.setBounds(212, 72, 125, 30);
					jButtonUpdate.setMnemonic(lang.getMnemonicChar());
					jButtonUpdate.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_USER_EDIT"));
					jButtonUpdate.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							editRecord();
						}
					});
				}
				{
					jButtonClose = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonClose);
					jButtonClose.setText(lang.get("btn_Close"));
					jButtonClose.setBounds(212, 289, 125, 30);
					jButtonClose.setMnemonic(lang.getMnemonicChar());
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							dispose();
						}
					});
				}
				{
					jButtonPermissions = new JButton4j(Common.icon_permissions);
					jDesktopPane1.add(jButtonPermissions);
					jButtonPermissions.setText(lang.get("btn_Permissions"));
					jButtonPermissions.setBounds(212, 103, 125, 30);
					jButtonPermissions.setMnemonic(lang.getMnemonicChar());
					jButtonPermissions.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_USER_PERM"));
					jButtonPermissions.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							permissions();
						}
					});
				}
				{

					jButtonPrint = new JButton4j(Common.icon_report);
					jDesktopPane1.add(jButtonPrint);
					jButtonPrint.setText(lang.get("btn_Print"));
					jButtonPrint.setBounds(212, 165, 125, 30);
					jButtonPrint.setMnemonic(lang.getMnemonicChar());
					jButtonPrint.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("RPT_USERS"));
					jButtonPrint.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							print();
						}
					});
				}
				{
					jButtonRename = new JButton4j(Common.icon_rename);
					jDesktopPane1.add(jButtonRename);
					jButtonRename.setText(lang.get("btn_Rename"));
					jButtonRename.setMnemonic(lang.getMnemonicChar());
					jButtonRename.setBounds(212, 134, 125, 30);
					jButtonRename.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_USER_RENAME"));
					jButtonRename.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							rename();
						}
					});
				}
				{
					jButtonHelp = new JButton4j(Common.icon_help);
					jDesktopPane1.add(jButtonHelp);
					jButtonHelp.setText(lang.get("btn_Help"));
					jButtonHelp.setBounds(212, 258, 125, 30);
					jButtonHelp.setMnemonic(lang.getMnemonicChar());
				}
				{
					jButtonRefresh = new JButton4j(Common.icon_refresh);
					jDesktopPane1.add(jButtonRefresh);
					jButtonRefresh.setText(lang.get("btn_Refresh"));
					jButtonRefresh.setBounds(212, 196, 125, 30);
					jButtonRefresh.setMnemonic(lang.getMnemonicChar());
					jButtonRefresh.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							populateList("");
						}
					});
				}

				{
					jButtonExcel = new JButton4j(Common.icon_XLS);
					jButtonExcel.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							excel();

						}
					});

					jButtonExcel.setText(lang.get("btn_Excel"));
					jButtonExcel.setMnemonic(lang.getMnemonicChar());
					jButtonExcel.setBounds(212, 227, 125, 30);
					jDesktopPane1.add(jButtonExcel);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * WindowBuilder generated method.<br>
	 * Please don't remove this method or its invocations.<br>
	 * It used by WindowBuilder to associate the {@link javax.swing.JPopupMenu}
	 * with parent.
	 */
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
