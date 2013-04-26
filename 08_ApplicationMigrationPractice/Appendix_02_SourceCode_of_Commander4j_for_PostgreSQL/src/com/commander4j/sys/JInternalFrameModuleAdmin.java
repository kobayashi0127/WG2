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
import java.sql.ResultSet;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.border.BevelBorder;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBListData;
import com.commander4j.db.JDBModule;
import com.commander4j.db.JDBModuleJList;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JMenuItem4j;
import com.commander4j.util.JExcel;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JInternalFrameModuleAdmin extends javax.swing.JInternalFrame
{
	private JButton4j jButtonExcel;
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JButton4j jButtonDelete;
	private JButton4j jButtonEdit;
	private JButton4j jButtonPrint;
	private JButton4j jButtonClose;
	private JRadioButton jRadioButtonFunctions;
	private JRadioButton jRadioButtonExec;
	private JRadioButton jRadioButtonReports;
	private JRadioButton jRadioButtonMenus;
	private JRadioButton jRadioButtonForms;
	private JRadioButton jRadioButtonAll;
	private ButtonGroup buttonGroup1;
	private JButton4j jButtonRename;
	private JButton4j jButton1;
	private JButton4j jButtonHelp;
	private JButton4j jButtonAdd;
	private JDBModuleJList jListModules;
	private JScrollPane jScrollPane1;
	private String lModuleId;
	private String statementText = "";
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);

	public JInternalFrameModuleAdmin()
	{
		super();
		initGUI();

		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_ADMIN_MODULES"));

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle window = getBounds();
		setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);

		populateList("");
	}

	private void add() {
		JDBModule m = new JDBModule(Common.selectedHostID, Common.sessionID);

		lModuleId = JOptionPane.showInputDialog(Common.mainForm, "Enter new module id");
		if (lModuleId != null)
		{
			if (lModuleId.equals("") == false)
			{
				lModuleId = lModuleId.toUpperCase();
				if (m.create(lModuleId, "", "Y", "N", "FORM", "", "") == false)
				{
					JUtility.errorBeep();
					JOptionPane.showMessageDialog(Common.mainForm, m.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
				else
				{

					JLaunchMenu.runForm("FRM_ADMIN_MODULE_EDIT", lModuleId);
				}
				populateList(lModuleId);
			}
		}
	}

	private void delete() {
		if (jListModules.isSelectionEmpty() == false)
		{

			lModuleId = ((JDBListData) jListModules.getSelectedValue()).toString();
			if (lModuleId.equals("root") == false)
			{
				int n = JOptionPane.showConfirmDialog(Common.mainForm, "Delete Module " + lModuleId + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
				if (n == 0)
				{
					JDBModule m = new JDBModule(Common.selectedHostID, Common.sessionID);
					m.setModuleId(lModuleId);
					m.delete();
					populateList("");
				}
			}
			else
			{
				JUtility.errorBeep();
				JOptionPane.showMessageDialog(null, "Cannot delete module " + lModuleId + " !", "Information", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private void populateList(String defaultitem) {
		DefaultComboBoxModel defComboBoxMod = new DefaultComboBoxModel();

		JDBModule tempModule = new JDBModule(Common.selectedHostID, Common.sessionID);

		LinkedList<JDBListData> tempModuleList = new LinkedList<JDBListData>();

		if (jRadioButtonAll.isSelected())
		{
			tempModuleList = tempModule.getModuleIds();
		}
		if (jRadioButtonFunctions.isSelected())
		{
			tempModuleList = tempModule.getModuleIdsByType("FUNCTION");
		}
		if (jRadioButtonForms.isSelected())
		{
			tempModuleList = tempModule.getModuleIdsByType("FORM");
		}
		if (jRadioButtonMenus.isSelected())
		{
			tempModuleList = tempModule.getModuleIdsByType("MENU");
		}
		if (jRadioButtonReports.isSelected())
		{
			tempModuleList = tempModule.getModuleIdsByType("REPORT");
		}
		if (jRadioButtonExec.isSelected())
		{
			tempModuleList = tempModule.getModuleIdsByType("EXEC");
		}
		int sel = -1;
		for (int j = 0; j < tempModuleList.size(); j++)
		{
			defComboBoxMod.addElement(tempModuleList.get(j));
			if (tempModuleList.get(j).toString().equals(defaultitem))
			{
				sel = j;
			}
		}

		ListModel jList1Model = defComboBoxMod;

		jListModules.setModel(jList1Model);
		jListModules.setSelectedIndex(sel);

		jListModules.setCellRenderer(Common.renderer_list);
	}

	private void editRecord() {
		if (jListModules.isSelectionEmpty() == false)
		{
			lModuleId = ((JDBListData) jListModules.getSelectedValue()).toString();
			JLaunchMenu.runForm("FRM_ADMIN_MODULE_EDIT", lModuleId);
			populateList(lModuleId);
		}
	}

	private void rename() {
		if (jListModules.isSelectionEmpty() == false)
		{
			String lmodule_id_from = ((JDBListData) jListModules.getSelectedValue()).toString();
			String lmodule_id_to = new String();
			lmodule_id_to = JOptionPane.showInputDialog(Common.mainForm, "Rename to module id");
			if (lmodule_id_to != null)
			{
				if (lmodule_id_to.equals("") == false)
				{
					lmodule_id_to = lmodule_id_to.toUpperCase();
					JDBModule m = new JDBModule(Common.selectedHostID, Common.sessionID);
					m.setModuleId(lmodule_id_from);
					if (m.renameTo(lmodule_id_to) == false)
					{
						JUtility.errorBeep();
						JOptionPane.showMessageDialog(null, m.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
					populateList(lModuleId);
				}
			}
		}
	}

	private void print() {
		statementText = "select * from " + Common.hostList.getHost(Common.selectedHostID).getDatabaseParameters().getjdbcDatabaseSchema() + "SYS_MODULES order by module_type,module_id";
		JLaunchReport.runReport("RPT_MODULES", null, statementText,null, "");
	}

	private void excel() {
		JDBModule module = new JDBModule(Common.selectedHostID, Common.sessionID);
		ResultSet rs = null;
		String moduleType = "";
		if (jRadioButtonAll.isSelected())
		{
			moduleType = "ALL";
			rs = module.getModuleData();
		}
		if (jRadioButtonFunctions.isSelected())
		{
			moduleType = "FUNCTION";
			rs = module.getModuleDataByType(moduleType);
		}
		if (jRadioButtonForms.isSelected())
		{
			moduleType = "FORM";
			rs = module.getModuleDataByType(moduleType);
		}
		if (jRadioButtonMenus.isSelected())
		{
			moduleType = "MENU";
			rs = module.getModuleDataByType(moduleType);
		}
		if (jRadioButtonReports.isSelected())
		{
			moduleType = "REPORT";
			rs = module.getModuleDataByType(moduleType);
		}
		if (jRadioButtonExec.isSelected())
		{
			moduleType = "EXEC";
			rs = module.getModuleDataByType(moduleType);
		}

		JExcel export = new JExcel();
		export.saveAs("modules_" + moduleType + ".xls", rs, Common.mainForm);
	}

	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(518, 511));
			this.setBounds(0, 0, 530+Common.LFAdjustWidth, 537+Common.LFAdjustHeight);
			setVisible(true);
			this.setTitle("Module Admin");
			this.setClosable(true);
			this.setIconifiable(true);
			{
				{
					buttonGroup1 = new ButtonGroup();

				}
				{
				}
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				this.getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
				jDesktopPane1.setPreferredSize(new java.awt.Dimension(410, 272));
				jDesktopPane1.setLayout(null);
				{
					jScrollPane1 = new JScrollPane();
					jDesktopPane1.add(jScrollPane1);
					jScrollPane1.setBounds(10, 10, 356, 476);
					jScrollPane1.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
					jScrollPane1.setSize(356, 476);
					{
						ListModel jListModulesModel = new DefaultComboBoxModel(new String[] { "Item One", "Item Two" });
						jListModules = new JDBModuleJList(Common.selectedHostID, Common.sessionID);
						jScrollPane1.setViewportView(jListModules);
						jListModules.setModel(jListModulesModel);
						jListModules.addMouseListener(new MouseAdapter() {
							public void mouseClicked(MouseEvent evt) {
								if (evt.getClickCount() == 2)
								{
									if (Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MODULE_EDIT") == true)
									{
										editRecord();
									}
								}
							}
						});

						{
							final JPopupMenu popupMenu = new JPopupMenu();
							addPopup(jListModules, popupMenu);

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_add);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										add();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Add"));
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MODULE_ADD"));
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
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MODULE_DELETE"));
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
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MODULE_EDIT"));
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
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MODULE_RENAME"));
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
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("RPT_MODULES"));
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
					jButtonAdd.setMnemonic(lang.getMnemonicChar());
					jButtonAdd.setBounds(375, 10, 126, 28);
					jButtonAdd.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MODULE_ADD"));
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
					jButtonDelete.setMnemonic(lang.getMnemonicChar());
					jButtonDelete.setBounds(375, 39, 126, 28);
					jButtonDelete.setFocusTraversalKeysEnabled(false);
					jButtonDelete.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MODULE_DELETE"));
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
					jButtonEdit.setBounds(375, 68, 126, 28);
					jButtonEdit.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MODULE_EDIT"));
					jButtonEdit.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							editRecord();
						}
					});
				}
				{
					jButtonPrint = new JButton4j(Common.icon_report);
					jDesktopPane1.add(jButtonPrint);
					jButtonPrint.setText(lang.get("btn_Print"));
					jButtonPrint.setMnemonic(lang.getMnemonicChar());
					jButtonPrint.setBounds(375, 126, 126, 28);
					jButtonPrint.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("RPT_MODULES"));
					jButtonPrint.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							print();
						}
					});
				}
				{
					jButtonClose = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonClose);
					jButtonClose.setText(lang.get("btn_Close"));
					jButtonClose.setMnemonic(lang.getMnemonicChar());
					jButtonClose.setBounds(375, 242, 126, 28);
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							dispose();
						}
					});
				}
				{
					jButtonRename = new JButton4j(Common.icon_rename);
					jDesktopPane1.add(jButtonRename);
					jButtonRename.setText(lang.get("btn_Rename"));
					jButtonRename.setMnemonic(lang.getMnemonicChar());
					jButtonRename.setBounds(375, 97, 126, 28);
					jButtonRename.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MODULE_RENAME"));
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
					jButtonHelp.setBounds(375, 184, 126, 28);
					jButtonHelp.setMnemonic(lang.getMnemonicChar());
				}
				{
					jButton1 = new JButton4j(Common.icon_refresh);
					jDesktopPane1.add(jButton1);
					jButton1.setText(lang.get("btn_Refresh"));
					jButton1.setBounds(375, 213, 126, 28);
					jButton1.setMnemonic(lang.getMnemonicChar());
					jButton1.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							populateList("");
						}
					});
				}
				{
					jRadioButtonAll = new JRadioButton();
					jDesktopPane1.add(jRadioButtonAll);
					jRadioButtonAll.setText(lang.get("lbl_Module_ALL"));
					jRadioButtonAll.setBounds(375, 290, 126, 28);
					jRadioButtonAll.setFont(Common.font_bold);
					buttonGroup1.add(jRadioButtonAll);
					jRadioButtonAll.setBackground(new java.awt.Color(255, 255, 255));
					jRadioButtonAll.setSelected(true);
					jRadioButtonAll.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jRadioButtonActionPerformed(evt);
						}
					});
				}
				{
					jRadioButtonForms = new JRadioButton();
					jDesktopPane1.add(jRadioButtonForms);
					jRadioButtonForms.setText(lang.get("lbl_Module_Form"));
					jRadioButtonForms.setBounds(375, 318, 126, 28);
					jRadioButtonForms.setFont(Common.font_bold);
					buttonGroup1.add(jRadioButtonForms);
					jRadioButtonForms.setBackground(new java.awt.Color(255, 255, 255));
					jRadioButtonForms.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jRadioButtonActionPerformed(evt);
						}
					});
				}
				{
					jRadioButtonFunctions = new JRadioButton();
					jDesktopPane1.add(jRadioButtonFunctions);
					jRadioButtonFunctions.setText(lang.get("lbl_Module_Function"));
					jRadioButtonFunctions.setFont(Common.font_bold);
					jRadioButtonFunctions.setBounds(375, 346, 126, 28);
					buttonGroup1.add(jRadioButtonFunctions);
					jRadioButtonFunctions.setBackground(new java.awt.Color(255, 255, 255));
					jRadioButtonFunctions.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jRadioButtonActionPerformed(evt);
						}
					});
				}
				{
					jRadioButtonMenus = new JRadioButton();
					jDesktopPane1.add(jRadioButtonMenus);
					jRadioButtonMenus.setText(lang.get("lbl_Module_Menu"));
					jRadioButtonMenus.setFont(Common.font_bold);
					jRadioButtonMenus.setBounds(375, 374, 126, 28);
					buttonGroup1.add(jRadioButtonMenus);
					jRadioButtonMenus.setBackground(new java.awt.Color(255, 255, 255));
					jRadioButtonMenus.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jRadioButtonActionPerformed(evt);
						}
					});
				}
				{
					jRadioButtonReports = new JRadioButton();
					jDesktopPane1.add(jRadioButtonReports);
					jRadioButtonReports.setText(lang.get("lbl_Module_Report"));
					jRadioButtonReports.setFont(Common.font_bold);
					jRadioButtonReports.setBounds(375, 402, 126, 28);
					buttonGroup1.add(jRadioButtonReports);
					jRadioButtonReports.setBackground(new java.awt.Color(255, 255, 255));
					jRadioButtonReports.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jRadioButtonActionPerformed(evt);
						}
					});
				}
				{
					jRadioButtonExec = new JRadioButton();
					jDesktopPane1.add(jRadioButtonExec);
					jRadioButtonExec.setText(lang.get("lbl_Module_Executable"));
					jRadioButtonExec.setFont(Common.font_bold);
					jRadioButtonExec.setBackground(new java.awt.Color(255, 255, 255));
					jRadioButtonExec.setBounds(375, 430, 126, 28);
					buttonGroup1.add(jRadioButtonExec);
					jRadioButtonExec.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jRadioButtonActionPerformed(evt);
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
					jButtonExcel.setBounds(375, 155, 126, 28);
					jDesktopPane1.add(jButtonExcel);
				}

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void jRadioButtonActionPerformed(ActionEvent evt) {
		populateList("");
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
