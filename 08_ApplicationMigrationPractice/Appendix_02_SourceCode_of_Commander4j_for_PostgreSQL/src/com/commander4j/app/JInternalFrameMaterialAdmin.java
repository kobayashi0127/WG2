package com.commander4j.app;

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
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBMaterial;
import com.commander4j.db.JDBMaterialType;
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
import com.commander4j.tablemodel.JDBMaterialTableModel;
import com.commander4j.util.JExcel;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JInternalFrameMaterialAdmin extends JInternalFrame
{
	private JButton4j jButtonSearch1;
	private JButton4j jButtonExcel;
	private JLabel4j_std jStatusText;
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JButton4j jButtonSearch;
	private JTextField4j jTextFieldDescription;
	private JLabel4j_std jLabel2;
	private JLabel4j_std jLabel4;
	private JButton4j jButtonClose;
	private JLabel4j_std jLabel8;
	private JToggleButton jToggleButtonSequence;
	private JComboBox4j jComboBoxDefaultBatchStatus;
	private JLabel4j_std jLabel12;
	private JComboBox4j jComboBoxSortBy;
	private JLabel4j_std jLabel10;
	private JLabel4j_std jLabel9;
	private JComboBox4j jComboBoxRoundingRule;
	private JButton4j jButtonPrint;
	private JComboBox4j jComboBoxShelfLifeUOM;
	private JTextField4j jTextFieldShelfLife;
	private JLabel4j_std jLabel7;
	private JButton4j jButtonHelp;
	private JButton4j jButtonDelete;
	private JButton4j jButtonEdit;
	private JButton4j jButtonAdd;
	private JComboBox4j jComboBoxBaseUOM;
	private JLabel4j_std jLabel3;
	private JLabel4j_std jLabel1;
	private JComboBox4j jComboBoxMaterialType;
	private JTextField4j jTextFieldMaterial;
	private JTable jTable1;
	private JScrollPane jScrollPane1;
	private JDBUom uom = new JDBUom(Common.selectedHostID, Common.sessionID);
	private JDBUom baseUom = new JDBUom(Common.selectedHostID, Common.sessionID);
	private JDBMaterialType materialType = new JDBMaterialType(Common.selectedHostID, Common.sessionID);
	private Vector<JDBUom> uomList = new Vector<JDBUom>();
	private Vector<JShelfLifeUom> shelfLifeUomList = new Vector<JShelfLifeUom>();
	private Vector<JShelfLifeRoundingRule> shelfLifeRule = new Vector<JShelfLifeRoundingRule>();
	private Vector<JDBMaterialType> typeList = new Vector<JDBMaterialType>();
	private JShelfLifeUom sluom = new JShelfLifeUom();
	private JShelfLifeRoundingRule slrr = new JShelfLifeRoundingRule();
	private String lmaterial;
	private boolean dlg_sort_descending = false;
	private String schemaName = Common.hostList.getHost(Common.selectedHostID).getDatabaseParameters().getjdbcDatabaseSchema();
	private JDBLanguage lang;
	private PreparedStatement listStatement;
	private JTextField4j textFieldInspectionID;

	private void clearFilter() {

		jTextFieldMaterial.setText("");

		jTextFieldDescription.setText("");

		materialType.setType("");
		materialType.getMaterialTypeProperties();
		jComboBoxMaterialType.getModel().setSelectedItem(materialType);

		baseUom.clear();
		jComboBoxBaseUOM.getModel().setSelectedItem(baseUom);

		jTextFieldShelfLife.setText("");

		sluom.getShelfLifeUomProperties("");
		jComboBoxShelfLifeUOM.getModel().setSelectedItem(sluom);

		slrr.getShelfLifeRuleProperties("");
		jComboBoxRoundingRule.getModel().setSelectedItem(slrr);

		buildSQL();
		populateList();

	}

	private void filterBy(String fieldname) {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{

			if (fieldname.equals(lang.get("lbl_Material")) == true)
			{
				jTextFieldMaterial.setText(jTable1.getValueAt(row, 0).toString());
			}

			if (fieldname.equals(lang.get("lbl_Description")) == true)
			{
				jTextFieldDescription.setText(jTable1.getValueAt(row, 1).toString());
			}

			if (fieldname.equals(lang.get("lbl_Material_Type")) == true)
			{
				materialType.setType(jTable1.getValueAt(row, 2).toString());
				materialType.getMaterialTypeProperties();
				jComboBoxMaterialType.getModel().setSelectedItem(materialType);
			}

			if (fieldname.equals(lang.get("lbl_Material_Base_UOM")) == true)
			{
				baseUom.setInternalUom(jTable1.getValueAt(row, 3).toString());
				baseUom.getInternalUomProperties();
				jComboBoxBaseUOM.getModel().setSelectedItem(baseUom);
			}

			if (fieldname.equals(lang.get("lbl_Material_Shelf_Life")) == true)
			{
				jTextFieldShelfLife.setText(jTable1.getValueAt(row, 4).toString());
			}

			if (fieldname.equals(lang.get("lbl_Material_Shelf_Life_UOM")) == true)
			{
				sluom.getShelfLifeUomProperties(jTable1.getValueAt(row, 5).toString());
				jComboBoxShelfLifeUOM.getModel().setSelectedItem(sluom);
			}

			if (fieldname.equals(lang.get("lbl_Material_Shelf_Life_Rounding_Rule")) == true)
			{
				slrr.getShelfLifeRuleProperties(jTable1.getValueAt(row, 6).toString());
				jComboBoxRoundingRule.getModel().setSelectedItem(slrr);
			}

			buildSQL();
			populateList();

		}
	}

	private void sortBy(String fieldname) {
		jComboBoxSortBy.setSelectedItem(fieldname);
		search();
	}

	private void export() {
		JDBMaterial material = new JDBMaterial(Common.selectedHostID, Common.sessionID);
		JExcel export = new JExcel();
		PreparedStatement temp = buildSQLr();
		export.saveAs("materials.xls", material.getMaterialDataResultSet(temp), Common.mainForm);
	}

	private void print() {
		PreparedStatement temp = buildSQLr();
		JLaunchReport.runReport("RPT_MATERIALS", null, "", temp, "");
	}

	private void search() {
		buildSQL();
		populateList();
	}

	private void deleteRecord() {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{
			lmaterial = jTable1.getValueAt(row, 0).toString();
			int n = JOptionPane.showConfirmDialog(Common.mainForm, "Delete Material " + lmaterial + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
			if (n == 0)
			{
				JDBMaterial m = new JDBMaterial(Common.selectedHostID, Common.sessionID);
				m.setMaterial(lmaterial);
				boolean result = m.delete();
				if (result == false)
				{
					JUtility.errorBeep();
					JOptionPane.showMessageDialog(Common.mainForm, m.getErrorMessage(), "Delete error (" + lmaterial + ")", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					buildSQL();
					populateList();
				}
			}
		}
	}

	private void addRecord() {
		JDBMaterial m = new JDBMaterial(Common.selectedHostID, Common.sessionID);
		lmaterial = JOptionPane.showInputDialog(Common.mainForm, "Enter new material");
		if (lmaterial != null)
		{
			if (lmaterial.equals("") == false)
			{
				lmaterial = lmaterial.toUpperCase();
				m.setMaterial(lmaterial);
				if (m.isValidMaterial() == false)
				{
					JLaunchMenu.runForm("FRM_ADMIN_MATERIAL_EDIT", lmaterial);
				}
				else
				{
					JOptionPane.showMessageDialog(Common.mainForm, "Material [" + lmaterial + "] already exists", "Error", JOptionPane.ERROR_MESSAGE);
				}
				buildSQL();
				populateList();
			}
		}

	}

	private void populateList() {
		JDBMaterial material = new JDBMaterial(Common.selectedHostID, Common.sessionID);

		JDBMaterialTableModel materialtable = new JDBMaterialTableModel(material.getMaterialDataResultSet(listStatement));
		TableRowSorter<JDBMaterialTableModel> sorter = new TableRowSorter<JDBMaterialTableModel>(materialtable);

		jTable1.setRowSorter(sorter);
		jTable1.setModel(materialtable);

		jScrollPane1.setViewportView(jTable1);
		JUtility.scrolltoHomePosition(jScrollPane1);
		jTable1.getTableHeader().setReorderingAllowed(false);
		jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jTable1.setFont(Common.font_list);

		TableColumn col = jTable1.getColumnModel().getColumn(0);
		col.setPreferredWidth(80);
		col = jTable1.getColumnModel().getColumn(1);
		col.setPreferredWidth(460);
		col = jTable1.getColumnModel().getColumn(2);
		col.setPreferredWidth(80);
		col = jTable1.getColumnModel().getColumn(3);
		col.setPreferredWidth(80);
		col = jTable1.getColumnModel().getColumn(4);
		col.setPreferredWidth(80);
		col = jTable1.getColumnModel().getColumn(5);
		col.setPreferredWidth(80);
		col = jTable1.getColumnModel().getColumn(6);
		col.setPreferredWidth(80);
		jScrollPane1.repaint();

		JUtility.setResultRecordCountColour(jStatusText, false, 0, materialtable.getRowCount());
	}

	private void editRecord() {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{
			lmaterial = jTable1.getValueAt(row, 0).toString();
			JLaunchMenu.runForm("FRM_ADMIN_MATERIAL_EDIT", lmaterial);
		}
	}

	private PreparedStatement buildSQLr() {
		
		PreparedStatement result;
		JDBQuery query = new JDBQuery(Common.selectedHostID, Common.sessionID);
		query.clear();
		query.addText(JUtility.substSchemaName(schemaName, "select * from {schema}APP_MATERIAL"));
		query.addParamtoSQL("material=", jTextFieldMaterial.getText());
		query.addParamtoSQL("inspection_id=", textFieldInspectionID.getText());

		if (jTextFieldDescription.getText().equals("") == false)
		{
			query.addParamtoSQL("upper(description) LIKE ", "%" + jTextFieldDescription.getText().toUpperCase() + "%");
		}
		query.addParamtoSQL("base_uom=", ((JDBUom) jComboBoxBaseUOM.getSelectedItem()).getInternalUom());
		query.addParamtoSQL("material_type=", ((JDBMaterialType) jComboBoxMaterialType.getSelectedItem()).getType());
		query.addParamtoSQL("shelf_life_uom=", ((JShelfLifeUom) jComboBoxShelfLifeUOM.getSelectedItem()).getUom());
		query.addParamtoSQL("shelf_life_rule=", ((JShelfLifeRoundingRule) jComboBoxRoundingRule.getSelectedItem()).getRule());
		query.addParamtoSQL("default_batch_status=", ((String) jComboBoxDefaultBatchStatus.getSelectedItem()).toString());

		Integer i;

		try
		{
			i = Integer.valueOf(jTextFieldShelfLife.getText());
			query.addParamtoSQL("shelf_life=", i);
		}
		catch (Exception e)
		{
		}

		query.appendSort(jComboBoxSortBy.getSelectedItem().toString(), jToggleButtonSequence.isSelected());
		query.applyRestriction(false, "none", 0);
		query.bindParams();
		
		result = query.getPreparedStatement();
		return result;
		
	}	
	
	private void buildSQL() {
		
		JDBQuery.closeStatement(listStatement);
		
		listStatement = buildSQLr();
	}

	public JInternalFrameMaterialAdmin()
	{
		super();
		setIconifiable(true);

		lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);

		uomList.add(new JDBUom(Common.selectedHostID, Common.sessionID));
		uomList.addAll(uom.getInternalUoms());
		typeList.add(new JDBMaterialType(Common.selectedHostID, Common.sessionID));
		typeList.addAll(materialType.getMaterialTypes());

		initGUI();

		JDBQuery query = new JDBQuery(Common.selectedHostID, Common.sessionID);
		query.clear();
		query.addText(JUtility.substSchemaName(schemaName, "select * from {schema}APP_MATERIAL where 1=2"));
		query.applyRestriction(false, "none", 0);
		query.bindParams();
		listStatement = query.getPreparedStatement();
		populateList();

		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_ADMIN_MATERIALS"));

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle window = getBounds();
		setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);

		setSequence(dlg_sort_descending);
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

	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(757, 535));
			this.setBounds(0, 0, 994+Common.LFAdjustWidth, 608+Common.LFAdjustHeight);
			setVisible(true);
			this.setTitle("Material Admin");
			this.setClosable(true);
			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				this.getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
				jDesktopPane1.setPreferredSize(new java.awt.Dimension(645, 460));
				{
					jScrollPane1 = new JScrollPane();
					jScrollPane1.getViewport().setBackground(Common.color_tablebackground);
					jDesktopPane1.setLayout(null);
					jDesktopPane1.add(jScrollPane1);
					jScrollPane1.setBounds(0, 167, 963, 374);
					{
						TableModel jTable1Model = new DefaultTableModel(new String[][] { { "One", "Two" }, { "Three", "Four" } }, new String[] { "Column 1", "Column 2" });
						jTable1 = new JTable();
						jTable1.setDefaultRenderer(Object.class, Common.renderer_table);
						jScrollPane1.setViewportView(jTable1);
						jTable1.setModel(jTable1Model);
						jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						jTable1.getTableHeader().setFont(Common.font_table_header);
						jTable1.getTableHeader().setForeground(Common.color_tableHeaderFont);
						jTable1.addMouseListener(new MouseAdapter() {
							public void mouseClicked(MouseEvent evt) {
								if (evt.getClickCount() == 2)
								{
									if (Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MATERIAL_EDIT"))
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
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MATERIAL_ADD"));
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										addRecord();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Add"));
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_edit);
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MATERIAL_EDIT"));
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										editRecord();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Edit"));
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_delete);
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MATERIAL_DELETE"));
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										deleteRecord();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Delete"));
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
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_XLS);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										export();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Excel"));
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
											sortBy("MATERIAL_TYPE");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Material_Type"));
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
											sortBy("BASE_UOM");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Material_Base_UOM"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("SHELF_LIFE");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Material_Shelf_Life"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("SHELF_LIFE_RULE");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Material_Shelf_Life_Rounding_Rule"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("DEFAULT_BATCH_STATUS");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Material_Default_Batch_Status"));
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
											filterBy(newItemMenuItem.getText());
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Material"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy(newItemMenuItem.getText());
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Description"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy(newItemMenuItem.getText());
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Material_Type"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy(newItemMenuItem.getText());
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Material_Base_UOM"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy(newItemMenuItem.getText());
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Material_Shelf_Life"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy(newItemMenuItem.getText());
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Material_Shelf_Life_UOM"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy(newItemMenuItem.getText());
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Material_Shelf_Life_Rounding_Rule"));
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
					jDesktopPane1.add(jButtonSearch);
					jButtonSearch.setText(lang.get("btn_Search"));
					jButtonSearch.setBounds(0, 137, 105, 28);
					jButtonSearch.setMnemonic(lang.getMnemonicChar());
					jButtonSearch.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							search();

						}
					});
				}
				{
					jTextFieldMaterial = new JTextField4j();
					jDesktopPane1.add(jTextFieldMaterial);
					jTextFieldMaterial.setBounds(125, 10, 125, 20);
				}
				{
					JLabel4j_std lblInspectionID = new JLabel4j_std(lang.get("lbl_Inspection_ID"));
					lblInspectionID.setBounds(34, 103, 83, 16);
					jDesktopPane1.add(lblInspectionID);
					lblInspectionID.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					textFieldInspectionID = new JTextField4j();
					textFieldInspectionID.setBounds(126, 98, 117, 28);
					jDesktopPane1.add(textFieldInspectionID);
					textFieldInspectionID.setColumns(10);
				}	
				{
					ComboBoxModel jComboBox1Model = new DefaultComboBoxModel(typeList);
					jComboBoxMaterialType = new JComboBox4j();
					jDesktopPane1.add(jComboBoxMaterialType);
					jComboBoxMaterialType.setModel(jComboBox1Model);
					jComboBoxMaterialType.setBounds(382, 10, 248, 23);
				}
				{
					jTextFieldDescription = new JTextField4j();
					jDesktopPane1.add(jTextFieldDescription);
					jTextFieldDescription.setBounds(126, 41, 506, 20);
				}
				{
					jLabel1 = new JLabel4j_std();
					jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
					jDesktopPane1.add(jLabel1);
					jLabel1.setText(lang.get("lbl_Material"));
					jLabel1.setBounds(12, 10, 105, 20);
				}
				{
					jLabel2 = new JLabel4j_std();
					jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
					jDesktopPane1.add(jLabel2);
					jLabel2.setText(lang.get("lbl_Material_Type"));
					jLabel2.setBounds(260, 10, 115, 20);
				}
				{
					jLabel3 = new JLabel4j_std();
					jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
					jDesktopPane1.add(jLabel3);
					jLabel3.setText(lang.get("lbl_Description"));
					jLabel3.setBounds(12, 41, 106, 20);
				}
				{
					ComboBoxModel jComboBox2Model = new DefaultComboBoxModel(uomList);
					jComboBoxBaseUOM = new JComboBox4j();
					jDesktopPane1.add(jComboBoxBaseUOM);
					jComboBoxBaseUOM.setModel(jComboBox2Model);
					jComboBoxBaseUOM.setBounds(126, 70, 154, 23);
					jComboBoxBaseUOM.setMaximumRowCount(12);
				}
				{
					jLabel4 = new JLabel4j_std();
					jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
					jDesktopPane1.add(jLabel4);
					jLabel4.setText(lang.get("lbl_Base_UOM"));
					jLabel4.setBounds(12, 71, 106, 20);
				}
				{
					jButtonAdd = new JButton4j(Common.icon_add);
					jDesktopPane1.add(jButtonAdd);
					jButtonAdd.setText(lang.get("btn_Add"));
					jButtonAdd.setBounds(214, 137, 105, 28);
					jButtonAdd.setMnemonic(lang.getMnemonicChar());
					jButtonAdd.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MATERIAL_ADD"));
					jButtonAdd.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							addRecord();
						}
					});
				}
				{
					jButtonEdit = new JButton4j(Common.icon_edit);
					jDesktopPane1.add(jButtonEdit);
					jButtonEdit.setText(lang.get("btn_Edit"));
					jButtonEdit.setBounds(321, 137, 105, 28);
					jButtonEdit.setMnemonic(lang.getMnemonicChar());
					jButtonEdit.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MATERIAL_EDIT"));
					jButtonEdit.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							editRecord();
						}
					});
				}
				{
					jButtonDelete = new JButton4j(Common.icon_delete);
					jDesktopPane1.add(jButtonDelete);
					jButtonDelete.setText(lang.get("btn_Delete"));
					jButtonDelete.setBounds(428, 137, 105, 28);
					jButtonDelete.setMnemonic(lang.getMnemonicChar());
					jButtonDelete.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MATERIAL_DELETE"));
					jButtonDelete.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							deleteRecord();
						}
					});
				}
				{
					jButtonHelp = new JButton4j(Common.icon_help);
					jDesktopPane1.add(jButtonHelp);
					jButtonHelp.setText(lang.get("btn_Help"));
					jButtonHelp.setBounds(749, 137, 105, 28);
					jButtonHelp.setMnemonic(lang.getMnemonicChar());
				}
				{
					jLabel7 = new JLabel4j_std();
					jDesktopPane1.add(jLabel7);
					jLabel7.setText(lang.get("lbl_Material_Shelf_Life"));
					jLabel7.setBounds(650, 10, 154, 20);
					jLabel7.setHorizontalAlignment(SwingConstants.RIGHT);
				}
				{
					jTextFieldShelfLife = new JTextField4j();
					jDesktopPane1.add(jTextFieldShelfLife);
					jTextFieldShelfLife.setBounds(812, 10, 125, 20);
				}
				{

					JShelfLifeUom slu = new JShelfLifeUom();
					slu.setUom("");
					slu.setDescription("");
					shelfLifeUomList.add(slu);
					shelfLifeUomList.addAll(slu.getShelfLifeUOMs());
					ComboBoxModel jComboBox5Model = new DefaultComboBoxModel(shelfLifeUomList);
					jComboBoxShelfLifeUOM = new JComboBox4j();
					jDesktopPane1.add(jComboBoxShelfLifeUOM);
					jComboBoxShelfLifeUOM.setModel(jComboBox5Model);
					jComboBoxShelfLifeUOM.setBounds(813, 41, 125, 23);
					jComboBoxShelfLifeUOM.setMaximumRowCount(12);
				}
				{
					jLabel8 = new JLabel4j_std();
					jDesktopPane1.add(jLabel8);
					jLabel8.setText(lang.get("lbl_Material_Shelf_Life_UOM"));
					jLabel8.setBounds(650, 41, 153, 20);
					jLabel8.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jButtonClose = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonClose);
					jButtonClose.setText(lang.get("btn_Close"));
					jButtonClose.setBounds(856, 137, 105, 28);
					jButtonClose.setMnemonic(lang.getMnemonicChar());
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							JDBQuery.closeStatement(listStatement);
							dispose();
						}
					});
				}
				{
					jButtonPrint = new JButton4j(Common.icon_report);
					jDesktopPane1.add(jButtonPrint);
					jButtonPrint.setText(lang.get("btn_Print"));
					jButtonPrint.setBounds(535, 137, 105, 28);
					jButtonPrint.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("RPT_MATERIALS"));
					jButtonPrint.setMnemonic(lang.getMnemonicChar());
					jButtonPrint.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							print();
						}
					});
				}
				{
					JShelfLifeRoundingRule slrr = new JShelfLifeRoundingRule();
					slrr.setRule("");
					slrr.setDescription("");
					shelfLifeRule.add(slrr);
					shelfLifeRule.addAll(slrr.getShelfLifeRoundingRules());
					ComboBoxModel jComboBoxRoundingRuleModel = new DefaultComboBoxModel(shelfLifeRule);
					jComboBoxRoundingRule = new JComboBox4j();
					jDesktopPane1.add(jComboBoxRoundingRule);
					jComboBoxRoundingRule.setModel(jComboBoxRoundingRuleModel);
					jComboBoxRoundingRule.setBounds(814, 71, 125, 23);
				}
				{
					jLabel9 = new JLabel4j_std();
					jDesktopPane1.add(jLabel9);
					jLabel9.setText(lang.get("lbl_Material_Shelf_Life_Rounding_Rule"));
					jLabel9.setBounds(650, 71, 157, 20);
					jLabel9.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jLabel10 = new JLabel4j_std();
					jDesktopPane1.add(jLabel10);
					jLabel10.setText(lang.get("lbl_Sort_By"));
					jLabel10.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel10.setBounds(358, 105, 98, 20);
				}
				{
					ComboBoxModel jComboBoxSortByModel = new DefaultComboBoxModel(new String[] { "MATERIAL", "MATERIAL_TYPE", "DESCRIPTION", "BASE_UOM", "PRODUCTION_UOM", "ISSUE_UOM", "SHELF_LIFE", "SHELF_LIFE_RULE", "DEFAULT_PALLET_STATUS",
							"DEFAULT_BATCH_STATUS" });
					jComboBoxSortBy = new JComboBox4j();
					jDesktopPane1.add(jComboBoxSortBy);
					jComboBoxSortBy.setModel(jComboBoxSortByModel);
					jComboBoxSortBy.setBounds(464, 104, 168, 23);
				}
				{
					jLabel12 = new JLabel4j_std();
					jDesktopPane1.add(jLabel12);
					jLabel12.setText(lang.get("lbl_Material_Default_Batch_Status"));
					jLabel12.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel12.setBounds(383, 71, 112, 20);
				}
				{
					ComboBoxModel jComboBoxDefaultBatchStatusModel = new DefaultComboBoxModel(Common.batchStatusIncBlank);
					jComboBoxDefaultBatchStatus = new JComboBox4j();
					jDesktopPane1.add(jComboBoxDefaultBatchStatus);
					jComboBoxDefaultBatchStatus.setModel(jComboBoxDefaultBatchStatusModel);
					jComboBoxDefaultBatchStatus.setBounds(505, 70, 126, 23);
				}
				{
					jToggleButtonSequence = new JToggleButton();
					jDesktopPane1.add(jToggleButtonSequence);
					jToggleButtonSequence.setBounds(639, 104, 21, 21);
					jToggleButtonSequence.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							setSequence(jToggleButtonSequence.isSelected());
						}
					});
				}

				{
					jStatusText = new JLabel4j_std();
					jStatusText.setForeground(new Color(255, 0, 0));
					jStatusText.setBackground(Color.GRAY);
					jStatusText.setBounds(5, 543, 958, 20);
					jDesktopPane1.add(jStatusText);
				}

				{
					jButtonExcel = new JButton4j(Common.icon_XLS);
					jButtonExcel.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							export();
						}
					});

					jButtonExcel.setText(lang.get("btn_Excel"));
					jButtonExcel.setMnemonic(lang.getMnemonicChar());
					jButtonExcel.setBounds(642, 137, 105, 28);
					jDesktopPane1.add(jButtonExcel);
				}

				{
					jButtonSearch1 = new JButton4j();
					jButtonSearch1.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							clearFilter();
						}
					});
					jButtonSearch1.setText(lang.get("btn_Clear_Filter"));
					jButtonSearch1.setMnemonic(lang.getMnemonicChar());
					jButtonSearch1.setBounds(107, 137, 105, 28);
					jDesktopPane1.add(jButtonSearch1);
				}
				{
					JButton4j btnLookupInspection = new JButton4j("");
					btnLookupInspection.setIcon(Common.icon_lookup);
					btnLookupInspection.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							JLaunchLookup.dlgCriteriaDefault = "";
							JLaunchLookup.dlgAutoExec = true;
							if (JLaunchLookup.qmInspections())
							{
								textFieldInspectionID.setText(JLaunchLookup.dlgResult);
							}
						}
					});
					btnLookupInspection.setBounds(242, 101, 22, 21);
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
