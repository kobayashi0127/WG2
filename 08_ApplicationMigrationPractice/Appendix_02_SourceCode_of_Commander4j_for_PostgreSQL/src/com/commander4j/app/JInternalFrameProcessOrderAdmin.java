package com.commander4j.app;

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
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.commander4j.calendar.JCalendarButton;
import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBProcessOrder;
import com.commander4j.db.JDBQuery;
import com.commander4j.db.JDBUom;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JComboBox4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JMenu4j;
import com.commander4j.gui.JMenuItem4j;
import com.commander4j.gui.JTextField4j;
import com.commander4j.sys.Common;
import com.commander4j.sys.JLaunchLookup;
import com.commander4j.sys.JLaunchMenu;
import com.commander4j.sys.JLaunchReport;
import com.commander4j.tablemodel.JDBProcessOrderTableModel;
import com.commander4j.util.JDateControl;
import com.commander4j.util.JExcel;
import com.commander4j.util.JQuantityInput;
import com.commander4j.util.JUtility;

public class JInternalFrameProcessOrderAdmin extends JInternalFrame
{
	private JButton4j jButtonSearch_1;
	private JComboBox4j jComboBoxUOM;
	private JLabel4j_std jLabel4_1;
	private JLabel4j_std jLabel12;
	private JQuantityInput jFormattedTextFieldQuantity;
	private JCheckBox jCheckBoxQuantity;
	private JLabel4j_std jLabelQuantity;
	private JButton4j jButtonExcel;
	private JLabel4j_std jStatusText;
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JButton4j jButtonEdit;
	private JButton4j jButtonDelete;
	private JButton4j jButtonPrint;
	private JButton4j jButtonClose;
	private JLabel4j_std jLabel4;
	private JLabel4j_std jLabel8;
	private JSpinner jSpinnerLimit;
	private JCheckBox jCheckBoxLimit;
	private JLabel4j_std jLabel10;
	private JButton4j jButtonLocationLookup;
	private JButton4j jButtonCustomerLookup;
	private JButton4j jButtonProcessOrderLookup;
	private JButton4j jButtonMaterialLookuo;
	private JToggleButton jToggleButtonSequence;
	private JComboBox4j jComboBoxSortBy;
	private JComboBox4j jComboBoxStatus;
	private JDateControl dueDateTo;
	private JCheckBox jCheckBoxDueDateTo;
	private JCheckBox jCheckBoxDueDateFrom;
	private JLabel4j_std jLabel7;
	private JDateControl dueDateFrom;
	private JLabel4j_std jLabel6;
	private JTextField4j jTextFieldRecipe;
	private JTextField4j jTextFieldLocation;
	private JLabel4j_std jLabel5;
	private JTextField4j jTextFieldProcessOrder;
	private JLabel4j_std jLabel2;
	private JTextField4j jTextFieldMaterial;
	private JTextField4j jTextFieldCustomer;	
	private JTextField4j jTextFieldDescription;
	private JTextField4j jTextFieldInspectionID;
	private JLabel4j_std jLabel3;
	private JLabel4j_std jLabel1;
	private JTable jTable1;
	private JButton4j jButtonLabel;
	private JButton4j jButtonAdd;
	private JButton4j jButtonSearch;
	private JScrollPane jScrollPane1;
	private String lprocessorder;
	private JDBUom u = new JDBUom(Common.selectedHostID, Common.sessionID);
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);
	private static boolean dlg_sort_descending = false;
	private Vector<JDBUom> uomList = new Vector<JDBUom>();
	String schemaName = Common.hostList.getHost(Common.selectedHostID).getDatabaseParameters().getjdbcDatabaseSchema();
	private JTextField4j jTextFieldRequiredResource;
	private JCalendarButton calendarButtondueDateFrom;
	private JCalendarButton calendarButtondueDateTo;
	private PreparedStatement listStatement;

	public JInternalFrameProcessOrderAdmin()
	{
		super();
		getContentPane().setLayout(null);

		uomList.add(new JDBUom(Common.selectedHostID, Common.sessionID));
		uomList.addAll(u.getInternalUoms());

		initGUI();

		JDBQuery query = new JDBQuery(Common.selectedHostID, Common.sessionID);
		query.clear();
		query.addText(JUtility.substSchemaName(schemaName, "select * from {schema}APP_PROCESS_ORDER where 1=2"));
		query.bindParams();
		listStatement = query.getPreparedStatement();
		populateList();

		JLabel4j_std label = new JLabel4j_std();
		label.setText(lang.get("lbl_Process_Order_Required_Resource"));
		label.setHorizontalAlignment(SwingConstants.TRAILING);
		label.setBounds(10, 106, 131, 21);
		jDesktopPane1.add(label);

		jTextFieldRequiredResource = new JTextField4j();
		jTextFieldRequiredResource.setBounds(148, 106, 117, 21);
		jDesktopPane1.add(jTextFieldRequiredResource);
		
		calendarButtondueDateFrom = new JCalendarButton(dueDateFrom);
		calendarButtondueDateFrom.setEnabled(false);
		calendarButtondueDateFrom.setBounds(300, 72, 21, 21);
		jDesktopPane1.add(calendarButtondueDateFrom);
		
		calendarButtondueDateTo = new JCalendarButton(dueDateTo);
		calendarButtondueDateTo.setEnabled(false);
		calendarButtondueDateTo.setBounds(510, 71, 21, 21);
		jDesktopPane1.add(calendarButtondueDateTo);
		
		jTextFieldInspectionID = new JTextField4j();
		jTextFieldInspectionID.setBounds(383, 104, 117, 21);
		jDesktopPane1.add(jTextFieldInspectionID);
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle window = getBounds();
		setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);

		dlg_sort_descending = true;
		setSequence(dlg_sort_descending);
	}

	private void filterBy(String fieldname) {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{

			if (fieldname.equals("Process Order") == true)
			{
				jTextFieldProcessOrder.setText(jTable1.getValueAt(row, 0).toString());
			}

			if (fieldname.equals("Material") == true)
			{
				jTextFieldMaterial.setText(jTable1.getValueAt(row, 1).toString());
			}

			if (fieldname.equals("Description") == true)
			{
				jTextFieldDescription.setText(jTable1.getValueAt(row, 2).toString());
			}

			if (fieldname.equals("Status") == true)
			{
				jComboBoxStatus.setSelectedItem(jTable1.getValueAt(row, 3).toString());
			}

			if (fieldname.equals("Location") == true)
			{
				jTextFieldLocation.setText(jTable1.getValueAt(row, 4).toString());
			}

			if (fieldname.equals("Recipe") == true)
			{
				jTextFieldRecipe.setText(jTable1.getValueAt(row, 6).toString());
			}
			buildSQL();
			populateList();

		}
	}

	private void clearFilter() {
		jTextFieldMaterial.setText("");
		jTextFieldProcessOrder.setText("");
		jTextFieldDescription.setText("");
		jComboBoxStatus.setSelectedItem("");
		jTextFieldLocation.setText("");
		jTextFieldRecipe.setText("");
		jTextFieldCustomer.setText("");
		jTextFieldInspectionID.setText("");
		search();
	}

	private void sortBy(String orderField) {
		jComboBoxSortBy.setSelectedItem(orderField);
		buildSQL();
		populateList();
	}

	private void excel() {
		JDBProcessOrder processOrder = new JDBProcessOrder(Common.selectedHostID, Common.sessionID);
		JExcel export = new JExcel();
		buildSQL();
		export.saveAs("process_orders.xls", processOrder.getProcessOrderDataResultSet(listStatement), Common.mainForm);
	}

	private void delete() {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{
			lprocessorder = jTable1.getValueAt(row, 0).toString();
			int question = JOptionPane.showConfirmDialog(Common.mainForm, "Delete Process Order " + lprocessorder + " ?", lang.get("dlg_Confirm"), JOptionPane.YES_NO_OPTION);
			if (question == 0)
			{
				JDBProcessOrder p = new JDBProcessOrder(Common.selectedHostID, Common.sessionID);

				p.setProcessOrder(lprocessorder);
				boolean result = p.delete();
				if (result == false)
				{
					JUtility.errorBeep();
					JOptionPane.showMessageDialog(Common.mainForm, p.getErrorMessage(), lang.get("dlg_Delete"), JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					buildSQL();
					populateList();
				}
			}
		}
	}

	private void print() {
		PreparedStatement temp = buildSQLr();
		JLaunchReport.runReport("RPT_PROCESS_ORDERS",null,"", temp,"");
	}

	private void addRecord() {
		JDBProcessOrder p = new JDBProcessOrder(Common.selectedHostID, Common.sessionID);
		lprocessorder = JOptionPane.showInputDialog(Common.mainForm, lang.get("dlg_Process_Order_Add"));
		if (lprocessorder != null)
		{
			if (lprocessorder.equals("") == true)
			{
				lprocessorder = p.generateNewProcessOrderNo();
			}
			else
			{
				lprocessorder = p.formatProcessOrderNo(lprocessorder);
			}
			lprocessorder = lprocessorder.toUpperCase();
			p.setProcessOrder(lprocessorder);
			if (p.isValidProcessOrder() == false)
			{
				JLaunchMenu.runForm("FRM_ADMIN_PROCESS_ORDER_EDIT", lprocessorder);
			}
			else
			{
				JOptionPane.showMessageDialog(Common.mainForm, "Process Order [" + lprocessorder + "] already exists", lang.get("dlg_Error"), JOptionPane.ERROR_MESSAGE);
			}
			buildSQL();
			populateList();
		}
	}

	private void populateList() {
		JDBProcessOrder processorder = new JDBProcessOrder(Common.selectedHostID, Common.sessionID);

		JDBProcessOrderTableModel processordertable = new JDBProcessOrderTableModel(processorder.getProcessOrderDataResultSet(listStatement));
		TableRowSorter<JDBProcessOrderTableModel> sorter = new TableRowSorter<JDBProcessOrderTableModel>(processordertable);

		jTable1.setRowSorter(sorter);
		jTable1.setModel(processordertable);

		jScrollPane1.setViewportView(jTable1);
		JUtility.scrolltoHomePosition(jScrollPane1);
		jTable1.getTableHeader().setReorderingAllowed(false);
		jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jTable1.setFont(Common.font_list);

		TableColumn col = jTable1.getColumnModel().getColumn(0);
		col.setPreferredWidth(90);
		col = jTable1.getColumnModel().getColumn(1);
		col.setPreferredWidth(80);
		col = jTable1.getColumnModel().getColumn(2);
		col.setPreferredWidth(230);
		col = jTable1.getColumnModel().getColumn(3);
		col.setPreferredWidth(80);
		col = jTable1.getColumnModel().getColumn(4);
		col.setPreferredWidth(80);
		col = jTable1.getColumnModel().getColumn(5);
		col.setPreferredWidth(130);
		col = jTable1.getColumnModel().getColumn(6);
		col.setPreferredWidth(135);
		col = jTable1.getColumnModel().getColumn(7);
		col.setPreferredWidth(85);
		col = jTable1.getColumnModel().getColumn(8);
		col.setPreferredWidth(35);
		col = jTable1.getColumnModel().getColumn(9);
		col.setPreferredWidth(120);

		jScrollPane1.repaint();

		JUtility.setResultRecordCountColour(jStatusText, jCheckBoxLimit.isSelected(), Integer.valueOf(jSpinnerLimit.getValue().toString()), processordertable.getRowCount());
	}

	private void search() {
		buildSQL();
		populateList();
	}

	private void editRecord() {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{
			lprocessorder = jTable1.getValueAt(row, 0).toString();
			JLaunchMenu.runForm("FRM_ADMIN_PROCESS_ORDER_EDIT", lprocessorder);
		}
	}

	private void prodDec() {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{
			lprocessorder = jTable1.getValueAt(row, 0).toString();
			JLaunchMenu.runForm("FRM_PAL_PROD_DEC", lprocessorder);
		}
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

	private void printLabels() {

		String lprocessOrder = "";
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{
				lprocessOrder = jTable1.getValueAt(row, 0).toString();
				JLaunchMenu.runForm("FRM_PROCESS_ORDER_LABEL", lprocessOrder);
		}

}
	
	private PreparedStatement buildSQLr() {

		PreparedStatement result;
		
		String temp = "";

		JDBQuery query = new JDBQuery(Common.selectedHostID, Common.sessionID);
		query.clear();

		temp = Common.hostList.getHost(Common.selectedHostID).getSqlstatements().getSQL("JDBProcessOrder.selectWithLimit");

		query.addText(temp);

		query.addParamtoSQL("process_order=", jTextFieldProcessOrder.getText());
		query.addParamtoSQL("material=", jTextFieldMaterial.getText());
		query.addParamtoSQL("status=", jComboBoxStatus.getSelectedItem());
		query.addParamtoSQL("location_id=", jTextFieldLocation.getText());
		query.addParamtoSQL("recipe_id=", jTextFieldRecipe.getText());
		query.addParamtoSQL("description like ", "%" + jTextFieldDescription.getText() + "%");
		query.addParamtoSQL("required_resource = ", jTextFieldRequiredResource.getText());
		query.addParamtoSQL("customer_id=", jTextFieldCustomer.getText());
		query.addParamtoSQL("inspection_id=",jTextFieldInspectionID.getText());
		
		query.addParamtoSQL("required_uom=", ((JDBUom) jComboBoxUOM.getSelectedItem()).getInternalUom());

		if (jCheckBoxDueDateFrom.isSelected())
		{
			query.addParamtoSQL("due_date>=", JUtility.getTimestampFromDate(dueDateFrom.getDate()));
		}
		if (jCheckBoxDueDateTo.isSelected())
		{
			query.addParamtoSQL("due_date<=", JUtility.getTimestampFromDate(dueDateTo.getDate()));
		}


		if (jCheckBoxQuantity.isSelected())
		{
			if (jFormattedTextFieldQuantity.getText().equals("") == false)
			{
				query.addParamtoSQL("required_quantity=", JUtility.stringToBigDecimal(jFormattedTextFieldQuantity.getText().toString()));
			}
		}


		query.appendSort(jComboBoxSortBy.getSelectedItem().toString(), jToggleButtonSequence.isSelected());
		query.applyRestriction(jCheckBoxLimit.isSelected(),Common.hostList.getHost(Common.selectedHostID).getDatabaseParameters().getjdbcDatabaseSelectLimit(), jSpinnerLimit.getValue());
		query.bindParams();
		result = query.getPreparedStatement();
		return result;
	}
	
	private void buildSQL() {

		JDBQuery.closeStatement(listStatement);
		
		listStatement = buildSQLr();
	}

	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(750, 498));
			this.setBounds(0, 0, 1021+Common.LFAdjustWidth, 608+Common.LFAdjustHeight);
			setVisible(true);
			this.setClosable(true);
			this.setIconifiable(true);
			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				jDesktopPane1.setBounds(0, 0, 1006, 654);
				jDesktopPane1.setLayout(null);
				this.getContentPane().add(jDesktopPane1);
				jDesktopPane1.setPreferredSize(new java.awt.Dimension(757, 468));
				{
					jScrollPane1 = new JScrollPane();
					jScrollPane1.getViewport().setBackground(Common.color_tablebackground);
					jScrollPane1.setBounds(0, 171, 991, 368);
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
									if (Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_PROCESS_ORDER_EDIT") == true)
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
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_search);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										search();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Search"));
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_add);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										addRecord();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Add"));
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_PROCESS_ORDER_ADD"));
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
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_PROCESS_ORDER_EDIT"));
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
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_PROCESS_ORDER_DELETE"));
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
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("RPT_PROCESS_ORDERS"));
								popupMenu.add(newItemMenuItem);
							}
							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_print);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										printLabels();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Label"));
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("RPT_PROCESS_ORDER_LABEL"));
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
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_scanner);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										prodDec();
									}
								});
								newItemMenuItem.setText(lang.get("mod_FRM_PAL_PROD_DEC"));
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_PAL_PROD_DEC"));
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenu4j sortByMenu = new JMenu4j();
								sortByMenu.setText(lang.get("lbl_Sort_By"));
								popupMenu.add(sortByMenu);

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("PROCESS_ORDER");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Process_Order"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("MATERIAL");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Material"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("DESCRIPTION");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Description"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("STATUS");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Process_Order_Status"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("LOCATION_ID");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Location_ID"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("DUE_DATE");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Process_Order_Due_Date"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("RECIPE_ID");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Process_Order_Recipe"));
									sortByMenu.add(newItemMenuItem);
								}
							}

							{
								final JMenu4j filterByMenu = new JMenu4j();
								filterByMenu.setText(lang.get("lbl_Filter_By"));
								popupMenu.add(filterByMenu);

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("Process Order");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Process_Order"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("Material");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Material"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("Description");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Description"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("Status");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Process_Order_Status"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("Recipe");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Process_Order_Recipe"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("Location");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Location_ID"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									filterByMenu.addSeparator();
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											clearFilter();
										}
									});
									newItemMenuItem.setText(lang.get("btn_Clear_Filter"));
									filterByMenu.add(newItemMenuItem);
								}
							}
						}
					}
				}
				{
					jButtonSearch = new JButton4j(Common.icon_search);
					jButtonSearch.setBounds(1, 141, 109, 28);
					jDesktopPane1.add(jButtonSearch);
					jButtonSearch.setText(lang.get("btn_Search"));
					jButtonSearch.setMnemonic(lang.getMnemonicChar());
					jButtonSearch.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							search();

						}
					});
				}
				{
					jButtonAdd = new JButton4j(Common.icon_add);
					jButtonAdd.setBounds(221, 141, 109, 28);
					jDesktopPane1.add(jButtonAdd);
					jButtonAdd.setText(lang.get("btn_Add"));
					jButtonAdd.setMnemonic(lang.getMnemonicChar());
					jButtonAdd.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_PROCESS_ORDER_ADD"));
					jButtonAdd.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							addRecord();

						}
					});
				}
				{
					jButtonEdit = new JButton4j(Common.icon_edit);
					jButtonEdit.setBounds(331, 141, 109, 28);
					jDesktopPane1.add(jButtonEdit);
					jButtonEdit.setText(lang.get("btn_Edit"));
					jButtonEdit.setMnemonic(lang.getMnemonicChar());
					jButtonEdit.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_PROCESS_ORDER_EDIT"));
					jButtonEdit.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							editRecord();
						}
					});
				}
				{
					jButtonDelete = new JButton4j(Common.icon_delete);
					jButtonDelete.setBounds(441, 141, 109, 28);
					jDesktopPane1.add(jButtonDelete);
					jButtonDelete.setText(lang.get("btn_Delete"));
					jButtonDelete.setMnemonic(lang.getMnemonicChar());
					jButtonDelete.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_PROCESS_ORDER_DELETE"));
					jButtonDelete.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							delete();
						}
					});
				}
				{
					jButtonPrint = new JButton4j(Common.icon_report);
					jButtonPrint.setBounds(551, 141, 109, 28);
					jDesktopPane1.add(jButtonPrint);
					jButtonPrint.setText(lang.get("btn_Print"));
					jButtonPrint.setMnemonic(lang.getMnemonicChar());
					jButtonPrint.setEnabled(true);
					jButtonPrint.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {

							print();
						}
					});
				}
				{
					jButtonLabel = new JButton4j(Common.icon_report);
					jButtonLabel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							printLabels();
						}
					});
					jButtonLabel.setBounds(661, 141, 109, 28);
					jDesktopPane1.add(jButtonLabel);
					jButtonLabel.setText(lang.get("btn_Label"));
					jButtonLabel.setMnemonic(java.awt.event.KeyEvent.VK_H);
				}
				{
					jButtonClose = new JButton4j(Common.icon_close);
					jButtonClose.setBounds(881, 141, 109, 28);
					jDesktopPane1.add(jButtonClose);
					jButtonClose.setText(lang.get("btn_Close"));
					jButtonClose.setMnemonic(lang.getMnemonicChar());
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							JDBQuery.closeStatement(listStatement);
							dispose();
						}
					});
				}
				{
					jLabel1 = new JLabel4j_std();
					jLabel1.setBounds(275, 38, 98, 22);
					jDesktopPane1.add(jLabel1);
					jLabel1.setText(lang.get("lbl_Material"));
					jLabel1.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jLabel12 = new JLabel4j_std();
					jLabel12.setBounds(275, 7, 98, 22);
					jDesktopPane1.add(jLabel12);
					jLabel12.setText(lang.get("lbl_Customer_ID"));
					jLabel12.setHorizontalAlignment(SwingConstants.TRAILING);
				}				
				{
					jLabel3 = new JLabel4j_std();
					jLabel3.setBounds(521, 7, 98, 22);
					jDesktopPane1.add(jLabel3);
					jLabel3.setText(lang.get("lbl_Description"));
					jLabel3.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jTextFieldDescription = new JTextField4j();
					jTextFieldDescription.setBounds(629, 7, 354, 22);
					jDesktopPane1.add(jTextFieldDescription);
				}
				{
					jTextFieldMaterial = new JTextField4j();
					jTextFieldMaterial.setBounds(383, 38, 98, 22);
					jDesktopPane1.add(jTextFieldMaterial);
				}
				{
					jTextFieldCustomer = new JTextField4j();
					jTextFieldCustomer.setBounds(383, 7, 98, 22);
					jDesktopPane1.add(jTextFieldCustomer);
				}	
				{
					JLabel4j_std label4j_std = new JLabel4j_std();
					label4j_std.setText(lang.get("lbl_Inspection_ID"));
					label4j_std.setHorizontalAlignment(SwingConstants.TRAILING);
					label4j_std.setBounds(275, 106, 98, 21);
					jDesktopPane1.add(label4j_std);
				}
				{
					jLabel2 = new JLabel4j_std();
					jLabel2.setBounds(758, 38, 89, 22);
					jDesktopPane1.add(jLabel2);
					jLabel2.setText(lang.get("lbl_Location_ID"));
					jLabel2.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jTextFieldProcessOrder = new JTextField4j();
					jTextFieldProcessOrder.setBounds(148, 7, 105, 22);
					jDesktopPane1.add(jTextFieldProcessOrder);
				}
				{
					jLabel4 = new JLabel4j_std();
					jLabel4.setBounds(7, 7, 132, 22);
					jDesktopPane1.add(jLabel4);
					jLabel4.setText(lang.get("lbl_Process_Order"));
					jLabel4.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jLabel5 = new JLabel4j_std();
					jLabel5.setBounds(7, 38, 132, 22);
					jDesktopPane1.add(jLabel5);
					jLabel5.setText(lang.get("lbl_Process_Order_Status"));
					jLabel5.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jTextFieldLocation = new JTextField4j();
					jTextFieldLocation.setBounds(857, 38, 105, 22);
					jDesktopPane1.add(jTextFieldLocation);
				}
				{
					jTextFieldRecipe = new JTextField4j();
					jTextFieldRecipe.setBounds(629, 38, 125, 22);
					jDesktopPane1.add(jTextFieldRecipe);
				}
				{
					jLabel6 = new JLabel4j_std();
					jLabel6.setBounds(510, 106, 109, 21);
					jDesktopPane1.add(jLabel6);
					jLabel6.setText(lang.get("lbl_Sort_By"));
					jLabel6.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					dueDateFrom = new JDateControl();
					dueDateFrom.setBounds(172, 69, 120, 22);
					jDesktopPane1.add(dueDateFrom);
					dueDateFrom.setEnabled(false);
					dueDateFrom.getEditor().setPreferredSize(new java.awt.Dimension(86, 32));
				}
				{
					jLabel7 = new JLabel4j_std();
					jLabel7.setBounds(0, 72, 139, 22);
					jDesktopPane1.add(jLabel7);
					jLabel7.setText(lang.get("lbl_Process_Order_Due_Date"));
					jLabel7.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jCheckBoxDueDateFrom = new JCheckBox();
					jCheckBoxDueDateFrom.setBounds(146, 72, 21, 22);
					jDesktopPane1.add(jCheckBoxDueDateFrom);
					jCheckBoxDueDateFrom.setBackground(new java.awt.Color(255, 255, 255));
					jCheckBoxDueDateFrom.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							System.out.println("jCheckBoxDueDateFrom.actionPerformed, event=" + evt);
							if (jCheckBoxDueDateFrom.isSelected())
							{
								dueDateFrom.setEnabled(true);
								calendarButtondueDateFrom.setEnabled(true);
							}
							else
							{
								dueDateFrom.setEnabled(false);
								calendarButtondueDateFrom.setEnabled(false);
							}
						}
					});
				}
				{
					jCheckBoxDueDateTo = new JCheckBox();
					jCheckBoxDueDateTo.setBounds(352, 72, 21, 22);
					jDesktopPane1.add(jCheckBoxDueDateTo);
					jCheckBoxDueDateTo.setBackground(new java.awt.Color(255, 255, 255));
					jCheckBoxDueDateTo.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							System.out.println("jCheckBoxDueDateFrom.actionPerformed, event=" + evt);
							if (jCheckBoxDueDateTo.isSelected())
							{
								dueDateTo.setEnabled(true);
								calendarButtondueDateTo.setEnabled(true);
							}
							else
							{
								dueDateTo.setEnabled(false);
								calendarButtondueDateTo.setEnabled(false);
							}
						}
					});
				}
				{
					dueDateTo = new JDateControl();
					dueDateTo.setBounds(382, 69, 120, 22);
					jDesktopPane1.add(dueDateTo);
					dueDateTo.setEnabled(false);
				}
				{
					ComboBoxModel jComboBoxStatusModel = new DefaultComboBoxModel(Common.processOrderStatusincBlank);
					jComboBoxStatus = new JComboBox4j();
					jComboBoxStatus.setBounds(148, 38, 126, 23);
					jDesktopPane1.add(jComboBoxStatus);
					jComboBoxStatusModel.setSelectedItem("");
					jComboBoxStatus.setModel(jComboBoxStatusModel);
				}
				{
					ComboBoxModel jComboBoxSortByModel = new DefaultComboBoxModel(new String[] { "PROCESS_ORDER", "MATERIAL", "DESCRIPTION", "STATUS", "LOCATION_ID", "DUE_DATE", "RECIPE_ID" });
					jComboBoxSortBy = new JComboBox4j();
					jComboBoxSortBy.setBounds(629, 106, 168, 23);
					jDesktopPane1.add(jComboBoxSortBy);
					jComboBoxSortBy.setModel(jComboBoxSortByModel);
					jComboBoxSortBy.getModel().setSelectedItem("DUE_DATE");
				}
				{
					jLabel8 = new JLabel4j_std();
					jLabel8.setBounds(524, 38, 95, 22);
					jDesktopPane1.add(jLabel8);
					jLabel8.setText(lang.get("lbl_Process_Order_Recipe"));
					jLabel8.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jToggleButtonSequence = new JToggleButton();
					jToggleButtonSequence.setBounds(795, 107, 21, 21);
					jDesktopPane1.add(jToggleButtonSequence);
					jToggleButtonSequence.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							setSequence(jToggleButtonSequence.isSelected());
						}
					});

				}
				{
					jButtonMaterialLookuo = new JButton4j(Common.icon_lookup);
					jButtonMaterialLookuo.setBounds(481, 38, 21, 22);
					jDesktopPane1.add(jButtonMaterialLookuo);
					jButtonMaterialLookuo.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							JLaunchLookup.dlgAutoExec = false;
							JLaunchLookup.dlgCriteriaDefault = "";
							if (JLaunchLookup.materials())
							{
								jTextFieldMaterial.setText(JLaunchLookup.dlgResult);
							}
						}
					});
				}
				{
				jButtonCustomerLookup = new JButton4j(Common.icon_lookup);
				jButtonCustomerLookup.setBounds(481, 7, 21, 22);
				jDesktopPane1.add(jButtonCustomerLookup);
				jButtonCustomerLookup.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						JLaunchLookup.dlgAutoExec = false;
						JLaunchLookup.dlgCriteriaDefault = "";
						if (JLaunchLookup.customers())
						{
							jTextFieldCustomer.setText(JLaunchLookup.dlgResult);
						}
					}
				});
			}				
				{
					jButtonProcessOrderLookup = new JButton4j(Common.icon_lookup);
					jButtonProcessOrderLookup.setBounds(253, 7, 21, 22);
					jDesktopPane1.add(jButtonProcessOrderLookup);
					jButtonProcessOrderLookup.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							JLaunchLookup.dlgCriteriaDefault = "Ready";
							JLaunchLookup.dlgAutoExec = true;
							if (JLaunchLookup.processOrders())
							{
								jTextFieldProcessOrder.setText(JLaunchLookup.dlgResult);
							}
						}
					});
				}
				{
					jButtonLocationLookup = new JButton4j(Common.icon_lookup);
					jButtonLocationLookup.setBounds(962, 38, 21, 22);
					jDesktopPane1.add(jButtonLocationLookup);
					jButtonLocationLookup.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							JLaunchLookup.dlgAutoExec = true;
							JLaunchLookup.dlgCriteriaDefault = "";
							if (JLaunchLookup.locations())
							{
								jTextFieldLocation.setText(JLaunchLookup.dlgResult);
							}
						}
					});
				}
				{
					jLabel10 = new JLabel4j_std();
					jLabel10.setBounds(793, 108, 100, 21);
					jDesktopPane1.add(jLabel10);
					jLabel10.setText(lang.get("lbl_Limit"));
					jLabel10.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jCheckBoxLimit = new JCheckBox();
					jCheckBoxLimit.setBounds(900, 108, 21, 21);
					jDesktopPane1.add(jCheckBoxLimit);
					jCheckBoxLimit.setSelected(true);
					jCheckBoxLimit.setBackground(new java.awt.Color(255, 255, 255));
					jCheckBoxLimit.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							if (jCheckBoxLimit.isSelected())
							{
								jSpinnerLimit.setEnabled(true);
							}
							else
							{
								jSpinnerLimit.setEnabled(false);
							}
						}
					});
				}
				{
					
					SpinnerNumberModel jSpinnerIntModel = new SpinnerNumberModel();
					jSpinnerIntModel.setMinimum(1);
					jSpinnerIntModel.setMaximum(5000);
					jSpinnerIntModel.setStepSize(1);					
					
					jSpinnerLimit = new JSpinner();
					jDesktopPane1.add(jSpinnerLimit);
					
					jSpinnerLimit.setModel(jSpinnerIntModel);
					JSpinner.NumberEditor ne = new JSpinner.NumberEditor(jSpinnerLimit);
					ne.getTextField().setFont(Common.font_std); 
					jSpinnerLimit.setEditor(ne);
					jSpinnerLimit.setBounds(921, 107, 63, 22);
					jSpinnerLimit.setValue(1000);
					jSpinnerLimit.getEditor().setSize(45, 21);
					

				}

				{
					jStatusText = new JLabel4j_std();
					jStatusText.setBounds(0, 540, 979, 21);
					jStatusText.setForeground(new Color(255, 0, 0));
					jStatusText.setBackground(Color.GRAY);
					jDesktopPane1.add(jStatusText);
				}

				{
					jButtonExcel = new JButton4j(Common.icon_XLS);
					jButtonExcel.setBounds(771, 141, 109, 28);
					jButtonExcel.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							excel();
						}
					});
					jButtonExcel.setMnemonic(KeyEvent.VK_H);
					jButtonExcel.setText(lang.get("btn_Excel"));
					jDesktopPane1.add(jButtonExcel);
				}

				{
					jLabelQuantity = new JLabel4j_std();
					jLabelQuantity.setBounds(525, 72, 94, 22);
					jLabelQuantity.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabelQuantity.setText(lang.get("lbl_Process_Order_Required_Quantity"));
					jDesktopPane1.add(jLabelQuantity);
				}

				{
					jCheckBoxQuantity = new JCheckBox();
					jCheckBoxQuantity.setBounds(625, 72, 21, 22);
					jCheckBoxQuantity.setBackground(new Color(255, 255, 255));
					jCheckBoxQuantity.setText("New JCheckBox");

					jCheckBoxQuantity.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							if (jCheckBoxQuantity.isSelected())
							{
								jFormattedTextFieldQuantity.setValue(0);
								jFormattedTextFieldQuantity.setEnabled(true);
							}
							else
							{
								jFormattedTextFieldQuantity.setValue(0);
								jFormattedTextFieldQuantity.setEnabled(false);
							}
						}
					});
					jDesktopPane1.add(jCheckBoxQuantity);
				}

				{
					jFormattedTextFieldQuantity = new JQuantityInput(new BigDecimal("0"));
					jFormattedTextFieldQuantity.setBounds(646, 72, 108, 22);
					jFormattedTextFieldQuantity.setVerifyInputWhenFocusTarget(false);
					jFormattedTextFieldQuantity.setHorizontalAlignment(SwingConstants.TRAILING);
					jFormattedTextFieldQuantity.setFont(Common.font_std);
					jFormattedTextFieldQuantity.setEnabled(false);
					jDesktopPane1.add(jFormattedTextFieldQuantity);
				}

				{
					jLabel4_1 = new JLabel4j_std();
					jLabel4_1.setBounds(768, 72, 79, 22);
					jLabel4_1.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel4_1.setText(lang.get("lbl_Process_Order_Required_UOM"));
					jDesktopPane1.add(jLabel4_1);
				}

				{
					ComboBoxModel jComboBox2Model = new DefaultComboBoxModel(uomList);
					jComboBoxUOM = new JComboBox4j();
					jComboBoxUOM.setBounds(857, 72, 127, 23);
					jComboBoxUOM.setModel(jComboBox2Model);
					jComboBoxUOM.setMaximumRowCount(12);
					jDesktopPane1.add(jComboBoxUOM);
				}

				{
					jButtonSearch_1 = new JButton4j();
					jButtonSearch_1.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							clearFilter();
						}
					});
					jButtonSearch_1.setBounds(111, 141, 109, 28);
					jButtonSearch_1.setText(lang.get("btn_Clear_Filter"));
					jDesktopPane1.add(jButtonSearch_1);
				}
				
				{
					JButton4j btnLookupInspection = new JButton4j("");
					btnLookupInspection.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							JLaunchLookup.dlgAutoExec = true;
							JLaunchLookup.dlgCriteriaDefault = "";
							if (JLaunchLookup.qmInspections())
							{
								jTextFieldInspectionID.setText(JLaunchLookup.dlgResult);
							}
						}
					});
					btnLookupInspection.setBounds(498, 104, 22, 21);
					jDesktopPane1.add(btnLookupInspection);
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
