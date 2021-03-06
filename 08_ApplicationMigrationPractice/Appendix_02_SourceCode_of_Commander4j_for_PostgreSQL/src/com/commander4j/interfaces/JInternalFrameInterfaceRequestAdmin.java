package com.commander4j.interfaces;

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
import java.sql.PreparedStatement;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
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

import com.commander4j.db.JDBInterfaceRequest;
import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBQuery;
import com.commander4j.sys.Common;
import com.commander4j.tablemodel.JDBInterfaceRequestTableModel;
import com.commander4j.util.JExcel;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JInternalFrameInterfaceRequestAdmin extends JInternalFrame
{
	private JButton jButtonDelete;
	private JButton jButtonExcel;
	private JLabel jStatusText;

	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JButton jButtonClose;
	private JToggleButton jToggleButtonSequence;
	private JComboBox comboStatus;
	private JComboBox jComboBoxSortBy;
	private JLabel jLabel10;
	private JLabel jLabel3;
	private JComboBox comboInterfaceType;
	private JLabel jLabel1;
	private JTable jTable1;
	private JButton jButtonHelp;
	private JButton jButtonSearch;
	private JScrollPane jScrollPane1;
	private Long interfaceRequestID;
	private static boolean dlg_sort_descending = false;
	private String schemaName = Common.hostList.getHost(Common.selectedHostID).getDatabaseParameters().getjdbcDatabaseSchema();
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);
	private PreparedStatement listStatement;

	public JInternalFrameInterfaceRequestAdmin()
	{
		super();
		initGUI();

		JDBQuery query = new JDBQuery(Common.selectedHostID, Common.sessionID);
		query.clear();
		query.addText(JUtility.substSchemaName(schemaName, "select * from {schema}SYS_INTERFACE_REQUEST where 1=2"));
		query.bindParams();
		listStatement = query.getPreparedStatement();
		populateList();

		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_ADMIN_INTERFACE_REQUEST"));

		JButton btnReSubmit = new JButton(Common.icon_release);
		btnReSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateRecord("Ready");
				search();
			}
		});
		btnReSubmit.setText(lang.get("btn_Resubmit"));
		btnReSubmit.setFont(Common.font_btn);
		btnReSubmit.setBounds(125, 83, 121, 28);
		jDesktopPane1.add(btnReSubmit);

		JButton buttonHold = new JButton(Common.icon_hold);
		buttonHold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateRecord("Held");
				search();
			}
		});
		buttonHold.setText(lang.get("btn_Hold"));
		buttonHold.setFont(Common.font_btn);
		buttonHold.setBounds(247, 83, 121, 28);
		jDesktopPane1.add(buttonHold);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle window = getBounds();
		setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);

		setSequence(dlg_sort_descending);
	}

	private void clearFilter() {

		comboInterfaceType.setSelectedIndex(-1);

		comboStatus.setSelectedIndex(-1);

		search();

	}

	private void filterBy(String fieldname) {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{

			if (fieldname.equals("INTERFACE_TYPE") == true)
			{
				comboInterfaceType.setSelectedItem(jTable1.getValueAt(row, 2).toString());
			}

			if (fieldname.equals("STATUS") == true)
			{
				comboStatus.setSelectedItem(jTable1.getValueAt(row, 5).toString());
			}

			search();

		}
	}

	private void search() {
		buildSQL();
		populateList();
	}

	private void excel() {
		JDBInterfaceRequest interfaceRequest = new JDBInterfaceRequest(Common.selectedHostID, Common.sessionID);
		JExcel export = new JExcel();
		buildSQL();
		export.saveAs("interface_requests.xls", interfaceRequest.getInterfaceRequestResultSet(listStatement), Common.mainForm);
	}

	private void deleteRecord() {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{
			interfaceRequestID = Long.valueOf(jTable1.getValueAt(row, 1).toString());

			int n = JOptionPane.showConfirmDialog(Common.mainForm, "Delete Interface Request [" + interfaceRequestID.toString() + "] ?", "Confirm", JOptionPane.YES_NO_OPTION);
			if (n == 0)
			{

				JDBInterfaceRequest ir = new JDBInterfaceRequest(Common.selectedHostID, Common.sessionID);
				ir.delete(interfaceRequestID);

			}
		}
	}

	private void sortBy(String fieldname) {
		jComboBoxSortBy.setSelectedItem(fieldname);
		search();
	}

	private void setSequence(boolean descending) {
		jToggleButtonSequence.setSelected(descending);
		if (jToggleButtonSequence.isSelected())
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

	private void buildSQL() {
		
		JDBQuery.closeStatement(listStatement);

		JDBQuery query = new JDBQuery(Common.selectedHostID, Common.sessionID);
		query.clear();

		query.addText(JUtility.substSchemaName(schemaName, "select * from {schema}SYS_INTERFACE_REQUEST"));

		query.addParamtoSQL("interface_type=", (String) comboInterfaceType.getSelectedItem());

		query.addParamtoSQL("status=", (String) comboStatus.getSelectedItem());

		query.appendSort(jComboBoxSortBy.getSelectedItem().toString(), jToggleButtonSequence.isSelected());
		query.applyRestriction(false,"none", 0);


		query.bindParams();
		listStatement = query.getPreparedStatement();
	}

	private void populateList() {
		JDBInterfaceRequest interfaceRequest = new JDBInterfaceRequest(Common.selectedHostID, Common.sessionID);
		JDBInterfaceRequestTableModel interfaceRequestTable = new JDBInterfaceRequestTableModel(interfaceRequest.getInterfaceRequestResultSet(listStatement));
		TableRowSorter<JDBInterfaceRequestTableModel> sorter = new TableRowSorter<JDBInterfaceRequestTableModel>(interfaceRequestTable);

		jTable1.setRowSorter(sorter);
		jTable1.setModel(interfaceRequestTable);

		jScrollPane1.setViewportView(jTable1);

		jTable1.getTableHeader().setReorderingAllowed(false);
		jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jTable1.setFont(Common.font_list);

		jTable1.getColumnModel().getColumn(0).setPreferredWidth(140);
		jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
		jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
		jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
		jTable1.getColumnModel().getColumn(4).setPreferredWidth(100);
		jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
		jTable1.getColumnModel().getColumn(6).setPreferredWidth(160);
		jTable1.getColumnModel().getColumn(7).setPreferredWidth(300);

		jScrollPane1.repaint();

		JUtility.setResultRecordCountColour(jStatusText, false, 0, interfaceRequestTable.getRowCount());
	}

	private void updateRecord(String newStatus) {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{
			interfaceRequestID = Long.valueOf(jTable1.getValueAt(row, 1).toString());
			JDBInterfaceRequest ir = new JDBInterfaceRequest(Common.selectedHostID, Common.sessionID);
			if (ir.getInterfaceRequestProperties(interfaceRequestID))
			{
				ir.setStatus(newStatus);
				ir.update();
			}
		}

	}

	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(497, 522));
			this.setBounds(0, 0, 886+Common.LFAdjustWidth, 527+Common.LFAdjustHeight);
			setVisible(true);
			this.setClosable(true);
			this.setTitle("Interface Requests");
			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
				jDesktopPane1.setPreferredSize(new java.awt.Dimension(483, 266));
				{
					jScrollPane1 = new JScrollPane();
					jScrollPane1.getViewport().setBackground(Common.color_tablebackground);
					jDesktopPane1.setLayout(null);
					jDesktopPane1.add(jScrollPane1);
					jScrollPane1.setBounds(0, 113, 858, 347);
					{
						TableModel jTable1Model = new DefaultTableModel(new String[][] { { "One", "Two" }, { "Three", "Four" } }, new String[] { "Column 1", "Column 2" });
						jTable1 = new JTable();
						jTable1.setDefaultRenderer(Object.class, Common.renderer_table);
						jScrollPane1.setViewportView(jTable1);
						jTable1.setModel(jTable1Model);
						jTable1.getTableHeader().setFont(Common.font_table_header);
						jTable1.getTableHeader().setForeground(Common.color_tableHeaderFont);
						jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

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
								final JMenuItem newItemMenuItem = new JMenuItem(Common.icon_release);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										updateRecord("Ready");
										search();
									}
								});
								newItemMenuItem.setText("Resubmit");
								popupMenu.add(newItemMenuItem);
							}
							{
								final JMenuItem newItemMenuItem = new JMenuItem(Common.icon_hold);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										updateRecord("Held");
										search();
									}
								});
								newItemMenuItem.setText("Hold");
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem newItemMenuItem = new JMenuItem(Common.icon_delete);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										deleteRecord();
									}
								});
								newItemMenuItem.setText("Delete");
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
											sortBy("STATUS");
										}
									});
									newItemMenuItem.setText("Status");
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem newItemMenuItem = new JMenuItem();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("INTERFACE_REQUEST_ID");
										}
									});
									newItemMenuItem.setText("Request ID");
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
											filterBy("STATUS");
										}
									});
									newItemMenuItem.setText("Status");
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
					jButtonSearch = new JButton(Common.icon_search);
					jDesktopPane1.add(jButtonSearch);
					jButtonSearch.setText(lang.get("btn_Search"));
					jButtonSearch.setFont(Common.font_btn);
					jButtonSearch.setBounds(3, 83, 121, 28);
					jButtonSearch.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							search();
						}
					});
				}
				{
					jButtonHelp = new JButton(Common.icon_help);
					jDesktopPane1.add(jButtonHelp);
					jButtonHelp.setText(lang.get("btn_Help"));
					jButtonHelp.setFont(Common.font_btn);
					jButtonHelp.setBounds(613, 83, 121, 28);
				}
				{
					jButtonClose = new JButton(Common.icon_close);
					jDesktopPane1.add(jButtonClose);
					jButtonClose.setText(lang.get("btn_Close"));
					jButtonClose.setFont(Common.font_btn);
					jButtonClose.setBounds(735, 83, 121, 28);
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							JDBQuery.closeStatement(listStatement);
							dispose();
						}
					});
				}
				{
					jLabel1 = new JLabel();
					jDesktopPane1.add(jLabel1);
					jLabel1.setText(lang.get("lbl_Interface_Type"));
					jLabel1.setFont(Common.font_std);
					jLabel1.setBounds(26, 12, 131, 21);
					jLabel1.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					comboInterfaceType = new JComboBox();
					comboInterfaceType.setModel(new DefaultComboBoxModel(new String[] { "", "Production Declaration", "Despatch Confirmation", "Despatch Pre Advice", "Equipment Tracking" }));
					jDesktopPane1.add(comboInterfaceType);
					comboInterfaceType.setFont(Common.font_combo);
					comboInterfaceType.setBounds(165, 12, 207, 23);
				}
				{
					jLabel3 = new JLabel();
					jDesktopPane1.add(jLabel3);
					jLabel3.setText(lang.get("lbl_Message_Status"));
					jLabel3.setFont(Common.font_std);
					jLabel3.setBounds(390, 12, 152, 21);
					jLabel3.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					comboStatus = new JComboBox();
					comboStatus.setModel(new DefaultComboBoxModel(new String[] { "", "Ready", "Held", "Failed" }));
					jDesktopPane1.add(comboStatus);
					comboStatus.setFont(Common.font_combo);
					comboStatus.setBounds(550, 12, 209, 23);
				}
				{
					jLabel10 = new JLabel();
					jDesktopPane1.add(jLabel10);
					jLabel10.setText(lang.get("lbl_Sort_By"));
					jLabel10.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel10.setFont(Common.font_std);
					jLabel10.setBounds(203, 45, 138, 21);
				}
				{
					ComboBoxModel jComboBoxSortByModel = new DefaultComboBoxModel(new String[] { "EVENT_TIME", "INTERFACE_REQUEST_ID", "INTERFACE_TYPE", "STATUS" });
					jComboBoxSortBy = new JComboBox();
					jDesktopPane1.add(jComboBoxSortBy);
					jComboBoxSortBy.setModel(jComboBoxSortByModel);
					jComboBoxSortBy.setFont(Common.font_combo);
					jComboBoxSortBy.setBounds(349, 45, 209, 23);
				}

				{
					jToggleButtonSequence = new JToggleButton();
					jDesktopPane1.add(jToggleButtonSequence);
					jToggleButtonSequence.setBounds(564, 45, 21, 21);
					jToggleButtonSequence.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							setSequence(jToggleButtonSequence.isSelected());
						}
					});
				}

				{
					jStatusText = new JLabel();
					jStatusText.setForeground(new Color(255, 0, 0));
					jStatusText.setBackground(Color.GRAY);
					jStatusText.setBounds(5, 460, 848, 21);
					jDesktopPane1.add(jStatusText);
				}

				{
					jButtonExcel = new JButton(Common.icon_XLS);
					jButtonExcel.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							excel();
						}
					});
					jButtonExcel.setFont(Common.font_btn);
					jButtonExcel.setText(lang.get("btn_Excel"));
					jButtonExcel.setBounds(491, 83, 121, 28);
					jDesktopPane1.add(jButtonExcel);
				}

				{
					jButtonDelete = new JButton(Common.icon_delete);
					jButtonDelete.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							deleteRecord();
							search();
						}
					});
					jButtonDelete.setFont(Common.font_bold);
					jButtonDelete.setText(lang.get("btn_Delete"));
					jButtonDelete.setBounds(369, 83, 121, 28);
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
