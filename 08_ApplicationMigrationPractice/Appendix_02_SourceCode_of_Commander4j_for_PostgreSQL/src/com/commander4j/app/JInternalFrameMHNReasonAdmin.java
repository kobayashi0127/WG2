package com.commander4j.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBMHNReasons;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JList4j;
import com.commander4j.gui.JMenuItem4j;
import com.commander4j.sys.Common;
import com.commander4j.sys.JLaunchMenu;
import com.commander4j.sys.JLaunchReport;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JInternalFrameMHNReasonAdmin extends JInternalFrame
{
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JButton4j jButtonDelete;
	private JButton4j jButtonEdit;
	private JButton4j jButtonRename;
	private JButton4j jButtonClose;
	private JList4j jListReasons;
	private JScrollPane jScrollPane1;
	private JButton4j jButtonRefresh;
	private JButton4j jButtonHelp;
	private JButton4j jButtonPrint;
	private JButton4j jButtonAdd;
	private String lreason;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);

	private void delete() {
		if (jListReasons.isSelectionEmpty() == false)
		{
			lreason = ((JDBMHNReasons) jListReasons.getSelectedValue()).getReason();
			int question = JOptionPane.showConfirmDialog(Common.mainForm, lang.get("dlg_Reason_Delete") + " " + lreason + " ?", lang.get("dlg_Confirm"), JOptionPane.YES_NO_OPTION);
			if (question == 0)
			{
				JDBMHNReasons u = new JDBMHNReasons(Common.selectedHostID, Common.sessionID);
				u.setReason(lreason);
				u.delete();
				populateList("");
			}
		}
	}

	private void rename() {
		if (jListReasons.isSelectionEmpty() == false)
		{
			String lreason_from = ((JDBMHNReasons) jListReasons.getSelectedValue()).getReason();
			String lreason_to = new String();
			lreason_to = JOptionPane.showInputDialog(Common.mainForm, lang.get("dlg_Reason_Rename"));
			if (lreason_to != null)
			{
				if (lreason_to.equals("") == false)
				{
					lreason_to = lreason_to.toUpperCase();
					JDBMHNReasons u = new JDBMHNReasons(Common.selectedHostID, Common.sessionID);
					u.setReason(lreason_from);
					if (u.renameTo(lreason_to) == false)
					{
						JUtility.errorBeep();
						JOptionPane.showMessageDialog(Common.mainForm, u.getErrorMessage(), lang.get("dlg_Error"), JOptionPane.ERROR_MESSAGE);
					}
					populateList(lreason_to);
				}
			}
		}
	}

	private void create() {
		JDBMHNReasons u = new JDBMHNReasons(Common.selectedHostID, Common.sessionID);
		lreason = JOptionPane.showInputDialog(Common.mainForm, lang.get("dlg_Reason_Add"));
		if (lreason != null)
		{
			if (lreason.equals("") == false)
			{
				lreason = lreason.toUpperCase();
				if (u.create(lreason, "") == false)
				{
					JUtility.errorBeep();
					JOptionPane.showMessageDialog(Common.mainForm, u.getErrorMessage(), lang.get("dlg_Error"), JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					JLaunchMenu.runForm("FRM_ADMIN_MHN_REASON_EDIT", lreason);
				}
				populateList(lreason);
			}
		}
	}

	private void print() {
		JLaunchReport.runReport("RPT_REASONS",null,"",null,"");
	}

	private void populateList(String defaultitem) {

		DefaultComboBoxModel DefComboBoxMod = new DefaultComboBoxModel();

		JDBMHNReasons tempType = new JDBMHNReasons(Common.selectedHostID, Common.sessionID);
		Vector<JDBMHNReasons> tempTypeList = tempType.getReasons();
		int sel = -1;
		for (int j = 0; j < tempTypeList.size(); j++)
		{
			JDBMHNReasons t = (JDBMHNReasons) tempTypeList.get(j);
			DefComboBoxMod.addElement(t);
			if (t.getReason().equals(defaultitem))
			{
				sel = j;
			}
		}

		ListModel jList1Model = DefComboBoxMod;
		jListReasons.setModel(jList1Model);
		jListReasons.setSelectedIndex(sel);
		jListReasons.setCellRenderer(Common.renderer_list);
		jListReasons.ensureIndexIsVisible(sel);
	}

	public JInternalFrameMHNReasonAdmin()
	{
		super();
		initGUI();
		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_ADMIN_MHN_REASON"));
		populateList("");
	}

	private void editRecord() {
		if (jListReasons.isSelectionEmpty() == false)
		{
			lreason = ((JDBMHNReasons) jListReasons.getSelectedValue()).getReason();
			JLaunchMenu.runForm("FRM_ADMIN_MHN_REASON_EDIT", lreason);
		}
	}

	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(375, 402));
			this.setBounds(0, 0, 385+Common.LFAdjustWidth, 418+Common.LFAdjustHeight);
			setVisible(true);
			this.setClosable(true);
			this.setIconifiable(true);
			this.setTitle("Reason Admin");
			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
				{
					jButtonAdd = new JButton4j(Common.icon_add);
					jDesktopPane1.add(jButtonAdd);
					jButtonAdd.setText(lang.get("btn_Add"));
					jButtonAdd.setMnemonic(lang.getMnemonicChar());
					jButtonAdd.setBounds(231, 7, 126, 28);
					jButtonAdd.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MHN_REASON_ADD"));
					jButtonAdd.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							create();

						}
					});
				}
				{
					jButtonDelete = new JButton4j(Common.icon_delete);
					jDesktopPane1.add(jButtonDelete);
					jButtonDelete.setText(lang.get("btn_Delete"));
					jButtonDelete.setMnemonic(lang.getMnemonicChar());
					jButtonDelete.setBounds(231, 36, 126, 28);
					jButtonDelete.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MHN_REASON_DELETE"));
					jButtonDelete.setFocusTraversalKeysEnabled(false);
					jButtonDelete.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							delete();

						}
					});
				}
				{
					jButtonEdit = new JButton4j(Common.icon_edit);
					jDesktopPane1.add(jButtonEdit);
					jButtonEdit.setText(lang.get("btn_Edit"));
					jButtonEdit.setMnemonic(lang.getMnemonicChar());
					jButtonEdit.setBounds(231, 65, 126, 28);
					jButtonEdit.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MHN_REASON_EDIT"));
					jButtonEdit.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							editRecord();
						}
					});
				}
				{
					jButtonRename = new JButton4j(Common.icon_rename);
					jDesktopPane1.add(jButtonRename);
					jButtonRename.setText(lang.get("btn_Rename"));
					jButtonRename.setMnemonic(lang.getMnemonicChar());
					jButtonRename.setBounds(231, 94, 126, 28);
					jButtonRename.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MHN_REASON_RENAME"));
					jButtonRename.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							rename();

						}
					});
				}
				{
					jButtonPrint = new JButton4j(Common.icon_report);
					jDesktopPane1.add(jButtonPrint);
					jButtonPrint.setText(lang.get("btn_Print"));
					jButtonPrint.setMnemonic(lang.getMnemonicChar());
					jButtonPrint.setBounds(231, 123, 126, 28);
					jButtonPrint.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							print();
						}
					});
				}
				{
					jButtonHelp = new JButton4j(Common.icon_help);
					jDesktopPane1.add(jButtonHelp);
					jButtonHelp.setText(lang.get("btn_Help"));
					jButtonHelp.setMnemonic(lang.getMnemonicChar());
					jButtonHelp.setBounds(231, 152, 126, 28);
				}
				{
					jButtonRefresh = new JButton4j(Common.icon_refresh);
					jDesktopPane1.add(jButtonRefresh);
					jButtonRefresh.setText(lang.get("btn_Refresh"));
					jButtonRefresh.setMnemonic(lang.getMnemonicChar());
					jButtonRefresh.setBounds(231, 181, 126, 28);
					jButtonRefresh.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							populateList("");
						}
					});
				}
				{
					jButtonClose = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonClose);
					jButtonClose.setText(lang.get("btn_Close"));
					jButtonClose.setMnemonic(lang.getMnemonicChar());
					jButtonClose.setBounds(231, 210, 126, 28);
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							dispose();
						}
					});
				}
				{
					jScrollPane1 = new JScrollPane();
					jDesktopPane1.add(jScrollPane1);
					jScrollPane1.setBounds(7, 7, 217, 357);
					{
						ListModel jList1Model = new DefaultComboBoxModel(new String[] { "Item One", "Item Two" });
						jListReasons = new JList4j();
						jListReasons.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						jScrollPane1.setViewportView(jListReasons);
						jListReasons.addMouseListener(new MouseAdapter() {
							public void mouseClicked(MouseEvent evt) {
								if (evt.getClickCount() == 2)
								{
									if (Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MHN_REASON_EDIT") == true)
									{
										editRecord();
									}
								}
							}
						});
						jListReasons.setModel(jList1Model);
					}

					{
						final JPopupMenu popupMenu = new JPopupMenu();
						addPopup(jListReasons, popupMenu);

						{
							final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_add);
							newItemMenuItem.addActionListener(new ActionListener() {
								public void actionPerformed(final ActionEvent e) {
									create();
								}
							});
							newItemMenuItem.setText(lang.get("btn_Add"));
							newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MHN_REASON_ADD"));
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
							newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MHN_REASON_DELETE"));
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
							newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MHN_REASON_EDIT"));
							popupMenu.add(newItemMenuItem);
						}

						{
							final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_rename);
							newItemMenuItem.addActionListener(new ActionListener() {
								public void actionPerformed(final ActionEvent e) {
									rename();
								}
							});
							newItemMenuItem.setText(lang.get("btn_Rename"));
							newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MHN_REASON_RENAME"));
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
							newItemMenuItem.setEnabled(true);
							popupMenu.add(newItemMenuItem);
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
