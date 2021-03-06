package com.commander4j.interfaces;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.commander4j.db.JDBInterface;
import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBQuery;
import com.commander4j.gui.*;
import com.commander4j.sys.Common;
import com.commander4j.sys.JLaunchMenu;
import com.commander4j.tablemodel.JDBInterfaceTableModel;
import com.commander4j.util.JExcel;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JInternalFrameInterfaceAdmin extends JInternalFrame
{
	private JButton4j jButtonDelete;
	private JButton4j jButtonExcel;
	private JLabel4j_std jStatusText;
	private JButton4j jButtonAdd;
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JButton4j jButtonEdit;
	private JButton4j jButtonClose;
	private JToggleButton jToggleButtonSequence;
	private JTextField4j jTextFieldPath;
	private JComboBox4j jComboBoxInterfaceDirection;
	private JLabel4j_std jLabel5;
	private JComboBox4j jComboBoxSortBy;
	private JLabel4j_std jLabel10;
	private JLabel4j_std jLabel3;
	private JTextField4j jTextFieldinterfaceType;
	private JLabel4j_std jLabel1;
	private JTable jTable1;
	private JButton4j jButtonHelp;
	private JButton4j jButtonSearch;
	private JScrollPane jScrollPane1;
	private String ltype;
	private String ldirection;
	private static boolean dlg_sort_descending = false;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);
	String schemaName = Common.hostList.getHost(Common.selectedHostID).getDatabaseParameters().getjdbcDatabaseSchema();
	private PreparedStatement listStatement;

	public JInternalFrameInterfaceAdmin()
	{
		super();
		setIconifiable(true);
		initGUI();

		JDBQuery query = new JDBQuery(Common.selectedHostID, Common.sessionID);
		query.clear();
		query.addText(JUtility.substSchemaName(schemaName, "select * from {schema}SYS_INTERFACE where 1=2"));
		query.bindParams();
		listStatement = query.getPreparedStatement();
		populateList();

		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_ADMIN_INTERFACE"));

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle window = getBounds();
		setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);

		setSequence(dlg_sort_descending);
	}

	private void clearFilter() {

		jTextFieldinterfaceType.setText("");

		jTextFieldPath.setText("");

		jComboBoxInterfaceDirection.setSelectedItem("");

		search();

	}

	private void filterBy(String fieldname) {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{

			if (fieldname.equals("Interface_Type") == true)
			{
				jTextFieldinterfaceType.setText(jTable1.getValueAt(row, 0).toString());
			}

			if (fieldname.equals("Interface_Direction") == true)
			{
				jTextFieldPath.setText(jTable1.getValueAt(row, 1).toString());
			}

			if (fieldname.equals("Path") == true)
			{
				jComboBoxInterfaceDirection.setSelectedItem(jTable1.getValueAt(row, 2).toString());
			}

			search();

		}
	}

	public JInternalFrameInterfaceAdmin(String material)
	{
		this();
		ltype = material;
		jTextFieldinterfaceType.setText(ltype);
		jTextFieldPath.setText(ldirection);
		buildSQL();
		populateList();
	}

	private void search() {
		buildSQL();
		populateList();
	}

	private void excel() {
		JDBInterface interfaceConfig = new JDBInterface(Common.selectedHostID, Common.sessionID);
		JExcel export = new JExcel();
		buildSQL();
		export.saveAs("interface.xls", interfaceConfig.getInterfaceDataResultSet(listStatement), Common.mainForm);
	}

	private void addRecord() {
		String ltype = "";
		String ldirection = "";
		ltype = (String) JOptionPane.showInputDialog(Common.mainForm, "Interface", "Type", JOptionPane.PLAIN_MESSAGE, Common.icon_interface, Common.messageTypesexclBlank, "Interface Definition");

		if (ltype != null)
		{
			if (ltype.equals("") == false)
			{
				Object[] directions = { "Input", "Output" };
				ldirection = (String) JOptionPane.showInputDialog(Common.mainForm, "Interface", "Direction", JOptionPane.PLAIN_MESSAGE, Common.icon_interface, directions, "Output");

				if (ldirection != null)
				{
					if (ldirection.equals("") == false)
					{
						JDBInterface matbat = new JDBInterface(Common.selectedHostID, Common.sessionID);
						if (matbat.isValidInterface(ltype, ldirection) == false)
						{
							JLaunchMenu.runForm("FRM_ADMIN_INTERFACE_EDIT", ltype, ldirection);
						}
						else
						{
							JOptionPane.showMessageDialog(Common.mainForm, "Interface [" + ltype + " / " + ldirection + "] already exists", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		}

	}

	private void deleteRecord() {
		String ltype = "";
		String ldirection = "";
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{
			ltype = jTable1.getValueAt(row, 0).toString();
			ldirection = jTable1.getValueAt(row, 1).toString();
			int n = JOptionPane.showConfirmDialog(Common.mainForm, "Delete Interface Config [" + ltype + "] [" + ldirection + "] ?", "Confirm", JOptionPane.YES_NO_OPTION);
			if (n == 0)
			{
				JDBInterface l = new JDBInterface(Common.selectedHostID, Common.sessionID);
				l.setInterfaceType(ltype);
				l.setInterfaceDirection(ldirection);
				boolean result = l.delete();
				if (result == false)
				{
					JUtility.errorBeep();
					JOptionPane.showMessageDialog(Common.mainForm, l.getErrorMessage(), "Delete error [" + ltype + "] [" + ldirection + "]", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					buildSQL();
					populateList();
				}
			}
		}
	}

	private void sortBy(String fieldname) {
		jComboBoxSortBy.setSelectedItem(fieldname);
		search();
	}

	private void setSequence(boolean descending) {
		jToggleButtonSequence.setSelected(descending);
		if (jToggleButtonSequence.isSelected() == true)
		{
			jToggleButtonSequence.setToolTipText("Descending");
			jToggleButtonSequence.setIcon(Common.icon_descending);
		}
		else
		{
			jToggleButtonSequence.setToolTipText("Ascending");
			jToggleButtonSequence.setIcon(Common.icon_ascending);
		}
	}

	public JInternalFrameInterfaceAdmin(String type, String direction)
	{
		this();
		ltype = type;
		ldirection = direction;
		jTextFieldinterfaceType.setText(ltype);
		jTextFieldPath.setText(ldirection);
		buildSQL();
		populateList();
	}

	private void buildSQL() {

		JDBQuery.closeStatement(listStatement);
		
		JDBQuery query = new JDBQuery(Common.selectedHostID, Common.sessionID);
		query.clear();

		query.addText(JUtility.substSchemaName(schemaName, "select * from {schema}SYS_INTERFACE"));
		query.addParamtoSQL("interface_type=", jTextFieldinterfaceType.getText());
		query.addParamtoSQL("path=", jTextFieldPath.getText());
		query.addParamtoSQL("interface_direction=", jComboBoxInterfaceDirection.getSelectedItem().toString());
		
		query.appendSort(jComboBoxSortBy.getSelectedItem().toString(), jToggleButtonSequence.isSelected());

		query.bindParams();
		listStatement = query.getPreparedStatement();
	}

	private void populateList() {
		JDBInterface interfaceConfig = new JDBInterface(Common.selectedHostID, Common.sessionID);
		JDBInterfaceTableModel interfaceConfigTable = new JDBInterfaceTableModel(interfaceConfig.getInterfaceDataResultSet(listStatement));
		TableRowSorter<JDBInterfaceTableModel> sorter = new TableRowSorter<JDBInterfaceTableModel>(interfaceConfigTable);

		jTable1.setRowSorter(sorter);
		jTable1.setModel(interfaceConfigTable);

		jScrollPane1.setViewportView(jTable1);

		jTable1.getTableHeader().setReorderingAllowed(false);
		jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jTable1.setFont(Common.font_list);

		jTable1.getColumnModel().getColumn(0).setPreferredWidth(200);
		jTable1.getColumnModel().getColumn(1).setPreferredWidth(120);
		jTable1.getColumnModel().getColumn(2).setPreferredWidth(60);
		jTable1.getColumnModel().getColumn(3).setPreferredWidth(60);
		jTable1.getColumnModel().getColumn(4).setPreferredWidth(60);
		jTable1.getColumnModel().getColumn(5).setPreferredWidth(440);

		jScrollPane1.repaint();

		JUtility.setResultRecordCountColour(jStatusText, false, 0, interfaceConfigTable.getRowCount());
	}

	private void editRecord() {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{
			ltype = jTable1.getValueAt(row, 0).toString();
			ldirection = jTable1.getValueAt(row, 1).toString();
			JLaunchMenu.runForm("FRM_ADMIN_INTERFACE_EDIT", ltype, ldirection);
		}

	}

	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(497, 522));
			this.setBounds(0, 0, 976+Common.LFAdjustWidth, 532+Common.LFAdjustHeight);
			setVisible(true);
			this.setClosable(true);
			this.setTitle("Interface Configuration");
			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
				jDesktopPane1.setPreferredSize(new java.awt.Dimension(483, 266));
				{
					jScrollPane1 = new JScrollPane();
					jScrollPane1.setBounds(0, 169, 948, 291);
					jScrollPane1.getViewport().setBackground(Common.color_tablebackground);
					jDesktopPane1.setLayout(null);
					jDesktopPane1.add(jScrollPane1);
					{
						TableModel jTable1Model = new DefaultTableModel(new String[][] { { "One", "Two" }, { "Three", "Four" } }, new String[] { "Column 1", "Column 2" });
						jTable1 = new JTable();
						jTable1.setDefaultRenderer(Object.class, Common.renderer_table);
						jScrollPane1.setViewportView(jTable1);
						jTable1.setModel(jTable1Model);
						jTable1.getTableHeader().setFont(Common.font_table_header);
						jTable1.getTableHeader().setForeground(Common.color_tableHeaderFont);
						jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						jTable1.addMouseListener(new MouseAdapter() {
							public void mouseClicked(MouseEvent evt) {
								if (evt.getClickCount() == 2)
								{
									if (Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_INTERFACE_EDIT"))
									{
										editRecord();
									}
								}
							}
						});

						{
							final JPopupMenu popupMenu = new JPopupMenu();
							addPopup(jTable1, popupMenu);

							{
								final JMenuItem newItemMenuItem = new JMenuItem(Common.icon_search);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										search();
									}
								});
								newItemMenuItem.setText("Search");
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem newItemMenuItem = new JMenuItem(Common.icon_add);
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_INTERFACE_ADD"));
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										addRecord();
									}
								});
								newItemMenuItem.setText("Add");
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem newItemMenuItem = new JMenuItem(Common.icon_delete);
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_INTERFACE_DELETE"));
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										deleteRecord();
									}
								});
								newItemMenuItem.setText("Delete");
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem newItemMenuItem = new JMenuItem(Common.icon_edit);
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_INTERFACE_EDIT"));
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										editRecord();
									}
								});
								newItemMenuItem.setText("Edit");
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem newItemMenuItem = new JMenuItem(Common.icon_XLS);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										excel();
									}
								});
								newItemMenuItem.setText("Excel");
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenu sortByMenu = new JMenu();
								sortByMenu.setText("Sort by");
								popupMenu.add(sortByMenu);

								{
									final JMenuItem newItemMenuItem = new JMenuItem();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("INTERFACE_TYPE");
										}
									});
									newItemMenuItem.setText("Interface Type");
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem newItemMenuItem = new JMenuItem();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("INTERFACE_DIRECTION");
										}
									});
									newItemMenuItem.setText("Interface Direction");
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem newItemMenuItem = new JMenuItem();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("PATH");
										}
									});
									newItemMenuItem.setText("Path");
									sortByMenu.add(newItemMenuItem);
								}

							}

							{
								final JMenu filterByMenu = new JMenu();
								filterByMenu.setText("Filter by");
								popupMenu.add(filterByMenu);

								{
									final JMenuItem newItemMenuItem = new JMenuItem();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("INTERFACE_TYPE");
										}
									});
									newItemMenuItem.setText("Interface Type");
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem newItemMenuItem = new JMenuItem();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("INTERFACE_DIRECTION");
										}
									});
									newItemMenuItem.setText("Interface Direction");
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem newItemMenuItem = new JMenuItem();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("PATH");
										}
									});
									newItemMenuItem.setText("Path");
									filterByMenu.add(newItemMenuItem);
								}

								{
									filterByMenu.addSeparator();
								}

								{
									final JMenuItem newItemMenuItem = new JMenuItem();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											clearFilter();
										}
									});
									newItemMenuItem.setText("Clear Filter");
									filterByMenu.add(newItemMenuItem);
								}
							}
						}
					}
				}
				{
					jButtonSearch = new JButton4j(Common.icon_search);
					jButtonSearch.setBounds(1, 134, 134, 30);
					jDesktopPane1.add(jButtonSearch);
					jButtonSearch.setText(lang.get("btn_Search"));
					jButtonSearch.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							search();

						}
					});
				}
				{
					jButtonEdit = new JButton4j(Common.icon_edit);
					jButtonEdit.setBounds(406, 134, 134, 30);

					jDesktopPane1.add(jButtonEdit);
					jButtonEdit.setText(lang.get("btn_Edit"));
					jButtonEdit.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_INTERFACE_EDIT"));
					jButtonEdit.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							editRecord();
						}
					});
				}
				{
					jButtonHelp = new JButton4j(Common.icon_help);
					jButtonHelp.setBounds(676, 134, 134, 30);
					jDesktopPane1.add(jButtonHelp);
					jButtonHelp.setText(lang.get("btn_Help"));
					jButtonHelp.setMnemonic(java.awt.event.KeyEvent.VK_H);
				}
				{
					jButtonClose = new JButton4j(Common.icon_close);
					jButtonClose.setBounds(811, 134, 134, 30);
					jDesktopPane1.add(jButtonClose);
					jButtonClose.setText(lang.get("btn_Close"));
					jButtonClose.setMnemonic(java.awt.event.KeyEvent.VK_C);
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							JDBQuery.closeStatement(listStatement);
							dispose();
						}
					});
				}
				{
					jLabel1 = new JLabel4j_std();
					jLabel1.setBounds(12, 12, 133, 21);
					jDesktopPane1.add(jLabel1);
					jLabel1.setText(lang.get("lbl_Interface_Type"));
					jLabel1.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jTextFieldinterfaceType = new JTextField4j();
					jTextFieldinterfaceType.setBounds(152, 12, 380, 21);
					jDesktopPane1.add(jTextFieldinterfaceType);
				}
				{
					jLabel3 = new JLabel4j_std();
					jLabel3.setBounds(12, 73, 133, 21);
					jDesktopPane1.add(jLabel3);
					jLabel3.setText(lang.get("lbl_Interface_Path"));
					jLabel3.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jTextFieldPath = new JTextField4j();
					jTextFieldPath.setBounds(152, 72, 694, 21);
					jDesktopPane1.add(jTextFieldPath);
				}
				{
					jLabel10 = new JLabel4j_std();
					jLabel10.setBounds(12, 101, 133, 21);
					jDesktopPane1.add(jLabel10);
					jLabel10.setText(lang.get("lbl_Sort_By"));
					jLabel10.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					ComboBoxModel jComboBoxSortByModel = new DefaultComboBoxModel(new String[] { "Interface_Type", "Interface_Direction", "Path" });
					jComboBoxSortBy = new JComboBox4j();
					jComboBoxSortBy.setBounds(152, 102, 209, 23);
					jDesktopPane1.add(jComboBoxSortBy);
					jComboBoxSortBy.setModel(jComboBoxSortByModel);
				}
				{
					jLabel5 = new JLabel4j_std();
					jLabel5.setBounds(12, 42, 133, 21);
					jDesktopPane1.add(jLabel5);
					jLabel5.setText(lang.get("lbl_Interface_Direction"));
					jLabel5.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					ComboBoxModel jComboBoxStatusModel = new DefaultComboBoxModel(new String[] { "", "Input", "Output" });
					jComboBoxInterfaceDirection = new JComboBox4j();
					jComboBoxInterfaceDirection.setBounds(152, 41, 134, 23);
					jDesktopPane1.add(jComboBoxInterfaceDirection);
					jComboBoxInterfaceDirection.setModel(jComboBoxStatusModel);
				}
				{
					jToggleButtonSequence = new JToggleButton();
					jToggleButtonSequence.setBounds(367, 102, 21, 21);
					jDesktopPane1.add(jToggleButtonSequence);
					jToggleButtonSequence.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							setSequence(jToggleButtonSequence.isSelected());
						}
					});
				}

				{
					jButtonAdd = new JButton4j(Common.icon_add);
					jButtonAdd.setBounds(136, 134, 134, 30);
					jButtonAdd.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							addRecord();
						}
					});
					jButtonAdd.setMnemonic(KeyEvent.VK_A);
					jButtonAdd.setText(lang.get("btn_Add"));
					jButtonAdd.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_INTERFACE_ADD"));
					jDesktopPane1.add(jButtonAdd);
				}

				{
					jStatusText = new JLabel4j_std();
					jStatusText.setBounds(5, 461, 943, 22);
					jStatusText.setForeground(new Color(255, 0, 0));
					jStatusText.setBackground(Color.GRAY);
					jDesktopPane1.add(jStatusText);
				}

				{
					jButtonExcel = new JButton4j(Common.icon_XLS);
					jButtonExcel.setBounds(541, 134, 134, 30);
					jButtonExcel.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							excel();
						}
					});
					jButtonExcel.setMnemonic(KeyEvent.VK_Z);
					jButtonExcel.setText(lang.get("btn_Excel"));
					jDesktopPane1.add(jButtonExcel);
				}

				{
					jButtonDelete = new JButton4j(Common.icon_delete);
					jButtonDelete.setBounds(271, 134, 134, 30);
					jButtonDelete.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							deleteRecord();
						}
					});
					jButtonDelete.setMnemonic(KeyEvent.VK_D);
					jButtonDelete.setText(lang.get("btn_Delete"));
					jButtonDelete.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_INTERFACE_DELETE"));
					jDesktopPane1.add(jButtonDelete);
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
