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
import java.sql.PreparedStatement;

import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBQuery;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JComboBox4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JMenuItem4j;
import com.commander4j.gui.JTextField4j;
import com.commander4j.tablemodel.JDBLanguageTableModel;
import com.commander4j.util.JExcel;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JInternalFrameLanguageAdmin extends javax.swing.JInternalFrame
{
	private JButton4j jButtonExcel;
	private static final long serialVersionUID = 1;
	private JScrollPane jScrollPane1;
	private JButton4j jButtonEdit;
	private JButton4j jButtonClose;
	private JButton4j jButtonHelp;
	private JButton4j jButtonPrint;
	private JButton4j jButtonDelete;
	private JButton4j jButtonAdd;
	private String resourceKey;
	private JDesktopPane jDesktopPane1;
	private String languageID;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);
	private JTextField4j textFieldText;
	private JTextField4j textFieldResourceKey;
	private JTextField4j textFieldMnemonic;
	private JComboBox4j comboBoxLanguageID;
	private String schemaName = Common.hostList.getHost(Common.selectedHostID).getDatabaseParameters().getjdbcDatabaseSchema();
	private JTable jTable1;
	private PreparedStatement listStatement;

	public JInternalFrameLanguageAdmin()
	{
		super();
		setIconifiable(true);
		getContentPane().setBackground(Color.WHITE);
		initGUI();

		JDBQuery query = new JDBQuery(Common.selectedHostID, Common.sessionID);
		query.clear();
		query.addText(JUtility.substSchemaName(schemaName, "select * from {schema}SYS_LANGUAGE where 1=2"));
		query.bindParams();
		listStatement = query.getPreparedStatement();
		populateList();

		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_LANGUAGE_ADMIN"));

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle window = getBounds();
		setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);

	}

	private void buildSQL() {

		JDBQuery.closeStatement(listStatement);
		
		String temp = "";

		JDBQuery query = new JDBQuery(Common.selectedHostID, Common.sessionID);
		query.clear();

		temp = "select * from {schema}SYS_LANGUAGE";
		query.addText(JUtility.substSchemaName(schemaName, temp));

		if (textFieldText.getText().equals("") == false)
		{
			query.addParamtoSQL("text like ", "%" + textFieldText.getText() + "%");
		}

		if (textFieldResourceKey.getText().equals("") == false)
		{
			query.addParamtoSQL("resource_key like ", "%" + textFieldResourceKey.getText() + "%");
		}

		if (textFieldMnemonic.getText().equals("") == false)
		{
			query.addParamtoSQL("mnemonic = ", textFieldMnemonic.getText());
		}

		query.addParamtoSQL("language_id = ", ((String) comboBoxLanguageID.getSelectedItem()).toString());

		query.appendSort("resource_key,language_id", "asc");

		query.bindParams();
		listStatement = query.getPreparedStatement();
	}

	private void populateList() {

		JDBLanguage lang1 = new JDBLanguage(Common.selectedHostID, Common.sessionID);

		JDBLanguageTableModel languagetable = new JDBLanguageTableModel(lang1.getLanguageDataResultSet(listStatement));
		TableRowSorter<JDBLanguageTableModel> sorter = new TableRowSorter<JDBLanguageTableModel>(languagetable);

		jTable1.setRowSorter(sorter);
		jTable1.setModel(languagetable);

		jScrollPane1.setViewportView(jTable1);
		jTable1.getTableHeader().setReorderingAllowed(false);
		jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jTable1.setFont(Common.font_list);

		TableColumn col = jTable1.getColumnModel().getColumn(0);
		col.setPreferredWidth(270);
		col = jTable1.getColumnModel().getColumn(1);
		col.setPreferredWidth(80);
		col = jTable1.getColumnModel().getColumn(2);
		col.setPreferredWidth(520);
		col = jTable1.getColumnModel().getColumn(3);
		col.setPreferredWidth(80);
		jScrollPane1.repaint();
	}

	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(979, 535));
			this.setBounds(0, 0, 1005+Common.LFAdjustWidth, 618+Common.LFAdjustHeight);
			setVisible(true);
			this.setClosable(true);
			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				this.getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
				jDesktopPane1.setPreferredSize(new java.awt.Dimension(917, 504));
				jDesktopPane1.setLayout(null);
				{
					jScrollPane1 = new JScrollPane();
					jScrollPane1.getViewport().setBackground(Common.color_tablebackground);
					jDesktopPane1.add(jScrollPane1);
					jScrollPane1.setBounds(3, 113, 973, 435);
					{
						TableModel jTable1Model = new DefaultTableModel(new String[][] { { "One", "Two" }, { "Three", "Four" } }, new String[] { "Column 1", "Column 2" });
						jTable1 = new JTable();
						jTable1.setDefaultRenderer(Object.class, Common.renderer_table);

						JPopupMenu popupMenu = new JPopupMenu();
						addPopup(jTable1, popupMenu);

						{
							final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_report);
							newItemMenuItem.addActionListener(new ActionListener() {
								public void actionPerformed(final ActionEvent e) {
									JLaunchReport.runReport("RPT_LANGUAGE",null,"",null,"");
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
							final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_add);
							newItemMenuItem.addActionListener(new ActionListener() {
								public void actionPerformed(final ActionEvent e) {
								add();
								}
							});
							newItemMenuItem.setText(lang.get("btn_Add"));
							newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_LANGUAGE_ADD"));
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
							newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_LANGUAGE_EDIT"));
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
							newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_LANGUAGE_DELETE"));
							popupMenu.add(newItemMenuItem);
						}

						jScrollPane1.setViewportView(jTable1);
						jTable1.setModel(jTable1Model);
						jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						jTable1.getTableHeader().setFont(Common.font_table_header);
						jTable1.getTableHeader().setForeground(Common.color_tableHeaderFont);
						jTable1.addMouseListener(new MouseAdapter() {
							public void mouseClicked(MouseEvent evt) {
								if (evt.getClickCount() == 2)
								{
									if (Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MATERIAL_EDIT") == true)
									{
										editRecord();
									}
								}
							}
						});

					}
					{
						jButtonEdit = new JButton4j(Common.icon_edit);
						jDesktopPane1.add(jButtonEdit);
						jButtonEdit.setText(lang.get("btn_Edit"));
						jButtonEdit.setBounds(246, 78, 120, 30);
						jButtonEdit.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_LANGUAGE_EDIT"));
						jButtonEdit.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								editRecord();
							}
						});
					}
					{
						jButtonClose = new JButton4j(Common.icon_close);
						jDesktopPane1.add(jButtonClose);
						jButtonClose.setText(lang.get("btn_Close"));
						jButtonClose.setBounds(856, 78, 120, 30);
						jButtonClose.setMnemonic(lang.getMnemonicChar());
						jButtonClose.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								jButtonCloseActionPerformed(evt);
							}
						});
					}
					{

						jButtonAdd = new JButton4j(Common.icon_add);
						jDesktopPane1.add(jButtonAdd);
						jButtonAdd.setText(lang.get("btn_Add"));
						jButtonAdd.setBounds(124, 78, 120, 30);
						jButtonAdd.setMnemonic(lang.getMnemonicChar());
						jButtonAdd.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_LANGUAGE_ADD"));
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
						jButtonDelete.setBounds(368, 78, 120, 30);
						jButtonDelete.setMnemonic(lang.getMnemonicChar());
						jButtonDelete.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_LANGUAGE_DELETE"));
						jButtonDelete.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								delete();
							}
						});
					}
					{
						jButtonPrint = new JButton4j(Common.icon_report);
						jDesktopPane1.add(jButtonPrint);
						jButtonPrint.setText(lang.get("btn_Print"));
						jButtonPrint.setBounds(612, 78, 120, 30);
						jButtonPrint.setMnemonic(lang.getMnemonicChar());
						jButtonPrint.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								JLaunchReport.runReport("RPT_LANGUAGE",null,"",null,"");
							}
						});
					}
					{
						jButtonHelp = new JButton4j(Common.icon_help);
						jDesktopPane1.add(jButtonHelp);
						jButtonHelp.setText(lang.get("btn_Help"));
						jButtonHelp.setBounds(734, 78, 120, 30);
						jButtonHelp.setMnemonic(lang.getMnemonicChar());
					}
					{
						textFieldText = new JTextField4j();
						textFieldText.setBounds(141, 45, 414, 20);
						jDesktopPane1.add(textFieldText);
						textFieldText.setColumns(10);

						textFieldResourceKey = new JTextField4j();
						textFieldResourceKey.setBounds(685, 12, 280, 20);
						jDesktopPane1.add(textFieldResourceKey);
						textFieldResourceKey.setColumns(10);

						textFieldMnemonic = new JTextField4j();
						textFieldMnemonic.setBounds(685, 45, 35, 20);
						jDesktopPane1.add(textFieldMnemonic);
						textFieldMnemonic.setColumns(10);
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
						jButtonExcel.setBounds(490, 78, 120, 30);
						jDesktopPane1.add(jButtonExcel);
					}

					JButton4j button = new JButton4j(Common.icon_search);
					button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							buildSQL();
							populateList();
						}
					});
					button.setText(lang.get("btn_Search"));
					button.setMnemonic(lang.getMnemonicChar());
					button.setBounds(3, 78, 120, 30);
					jDesktopPane1.add(button);

					JLabel4j_std label = new JLabel4j_std();
					label.setForeground(Color.RED);
					label.setBackground(Color.GRAY);
					label.setBounds(0, 550, 976, 21);
					jDesktopPane1.add(label);

					JLabel4j_std label_1 = new JLabel4j_std();
					label_1.setHorizontalTextPosition(SwingConstants.RIGHT);
					label_1.setHorizontalAlignment(SwingConstants.RIGHT);
					label_1.setText(lang.get("lbl_Text"));
					label_1.setBounds(8, 45, 115, 21);
					jDesktopPane1.add(label_1);

					JLabel4j_std label_2 = new JLabel4j_std();
					label_2.setHorizontalTextPosition(SwingConstants.RIGHT);
					label_2.setHorizontalAlignment(SwingConstants.RIGHT);
					label_2.setBounds(557, 12, 115, 21);
					label_2.setText(lang.get("lbl_Resource_Key"));
					jDesktopPane1.add(label_2);

					JLabel4j_std label_3 = new JLabel4j_std();
					label_3.setHorizontalTextPosition(SwingConstants.RIGHT);
					label_3.setHorizontalAlignment(SwingConstants.RIGHT);
					label_3.setText(lang.get("lbl_Language_ID"));
					label_3.setBounds(8, 12, 115, 21);
					jDesktopPane1.add(label_3);

					JLabel4j_std label_4 = new JLabel4j_std();
					label_4.setText(lang.get("lbl_Mnemonic"));
					label_4.setHorizontalTextPosition(SwingConstants.RIGHT);
					label_4.setHorizontalAlignment(SwingConstants.RIGHT);
					label_4.setBounds(557, 45, 115, 21);
					jDesktopPane1.add(label_4);

					comboBoxLanguageID = new JComboBox4j();
					comboBoxLanguageID.setPreferredSize(new Dimension(45, 21));
					comboBoxLanguageID.setLightWeightPopupEnabled(true);
					comboBoxLanguageID.setIgnoreRepaint(false);
					comboBoxLanguageID.setEnabled(true);
					comboBoxLanguageID.setEditable(false);
					comboBoxLanguageID.setBounds(141, 12, 72, 23);
					comboBoxLanguageID.addItem("");
					comboBoxLanguageID.addItem("EN");
					comboBoxLanguageID.addItem("DE");
					comboBoxLanguageID.addItem("FR");
					comboBoxLanguageID.addItem("HU");
					comboBoxLanguageID.addItem("IT");
					comboBoxLanguageID.addItem("NL");
					jDesktopPane1.add(comboBoxLanguageID);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void delete() {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{

			resourceKey = jTable1.getValueAt(row, 0).toString();
			languageID = jTable1.getValueAt(row, 1).toString();

			int n = JOptionPane.showConfirmDialog(Common.mainForm, "Delete Resource Key " + resourceKey + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
			if (n == 0)
			{
				JDBLanguage c = new JDBLanguage(Common.selectedHostID, Common.sessionID);

				c.setKey(resourceKey);
				c.setLanguage(languageID);
				c.delete();
				buildSQL();
				populateList();
			}
		}
	}

	private void export() {
		JDBLanguage language = new JDBLanguage(Common.selectedHostID, Common.sessionID);

		JExcel export = new JExcel();
		buildSQL();
		export.saveAs("language.xls", language.getLanguageDataResultSet(listStatement), Common.mainForm);
	}

	private void add() {
		JDBLanguage ctl = new JDBLanguage(Common.selectedHostID, Common.sessionID);

		resourceKey = JUtility.replaceNullStringwithBlank(JOptionPane.showInputDialog(Common.mainForm, "Enter new Resource Key"));

		if (resourceKey.equals("") == false)
		{
			languageID = JUtility.replaceNullStringwithBlank(JOptionPane.showInputDialog(Common.mainForm, "Enter new Language ID"));
			if (languageID.equals("") == false)
			{
				languageID = languageID.toUpperCase();
				if (ctl.create(resourceKey, languageID, "", "") == false)
				{
					JUtility.errorBeep();
					JOptionPane.showMessageDialog(Common.mainForm, ctl.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					buildSQL();
					populateList();
					JLaunchMenu.runForm("FRM_ADMIN_LANGUAGE_EDIT", resourceKey, languageID);
					buildSQL();
					populateList();
				}
			}
		}

	}

	private void editRecord() {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{

			resourceKey = jTable1.getValueAt(row, 0).toString();
			languageID = jTable1.getValueAt(row, 1).toString();

			JLaunchMenu.runForm("FRM_ADMIN_LANGUAGE_EDIT", resourceKey, languageID);

			buildSQL();
			populateList();
		}
	}

	private void jButtonCloseActionPerformed(ActionEvent evt) {
		JDBQuery.closeStatement(listStatement);
		dispose();
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger())
				{
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
				{
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
