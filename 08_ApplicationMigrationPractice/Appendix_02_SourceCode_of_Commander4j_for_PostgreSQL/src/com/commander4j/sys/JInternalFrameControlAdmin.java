package com.commander4j.sys;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import com.commander4j.db.JDBControl;
import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBQuery;
import com.commander4j.gui.JButton4j;
import com.commander4j.tablemodel.JDBControlTableModel;
import com.commander4j.util.JExcel;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JInternalFrameControlAdmin extends javax.swing.JInternalFrame
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
	private JTable jTable1;
	private String systemKey;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);
	private PreparedStatement listStatement;

	/**
	 * Auto-generated main method to display this JInternalFrame inside a new
	 * JFrame.
	 */

	public JInternalFrameControlAdmin()
	{
		super();
		initGUI();

		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_SYSTEM_KEYS"));

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle window = getBounds();
		setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);

		{
			jButtonExcel = new JButton4j(Common.icon_XLS);
			jButtonExcel.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					JDBControl control = new JDBControl(Common.selectedHostID, Common.sessionID);

					JExcel export = new JExcel();
					buildSQL();
					export.saveAs("control.xls", control.getControlDataResultSet(listStatement), Common.mainForm);
				}
			});
			jButtonExcel.setText(lang.get("btn_Excel"));
			jButtonExcel.setMnemonic(lang.getMnemonicChar());
			jButtonExcel.setBounds(331, 275, 106, 30);
			getContentPane().add(jButtonExcel);
		}

	}

	private void buildSQL() {
		
		JDBQuery.closeStatement(listStatement);
		JDBQuery query = new JDBQuery(Common.selectedHostID, Common.sessionID);

		query.clear();
		String schemaName = Common.hostList.getHost(Common.selectedHostID).getDatabaseParameters().getjdbcDatabaseSchema();
		query.addText(JUtility.substSchemaName(schemaName, "select * from {schema}SYS_CONTROL order by system_key"));
		query.applyRestriction(false, "none", 0);
		query.bindParams();

		listStatement = query.getPreparedStatement();
	}

	private void populateList(String defaultitem) {

		JDBControl ctrl = new JDBControl(Common.selectedHostID, Common.sessionID);
		buildSQL();
		JDBControlTableModel controltable = new JDBControlTableModel(ctrl.getControlDataResultSet(listStatement));
		TableRowSorter<JDBControlTableModel> sorter = new TableRowSorter<JDBControlTableModel>(controltable);

		jTable1.setRowSorter(sorter);
		jTable1.setModel(controltable);

		jScrollPane1.setViewportView(jTable1);
		jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		jTable1.getTableHeader().setReorderingAllowed(false);
		jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jTable1.setFont(Common.font_list);

		TableColumn col = jTable1.getColumnModel().getColumn(0);
		col.setPreferredWidth(200);
		col = jTable1.getColumnModel().getColumn(1);
		col.setPreferredWidth(150);
		col = jTable1.getColumnModel().getColumn(2);
		col.setPreferredWidth(300);
		jScrollPane1.repaint();
	}

	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(669, 341));
			this.setBounds(0, 0, 793+Common.LFAdjustWidth, 357+Common.LFAdjustHeight);
			setVisible(true);
			this.setClosable(true);
			this.getContentPane().setLayout(null);
			this.setIconifiable(true);

			{
				jScrollPane1 = new JScrollPane();
				jScrollPane1.getViewport().setBackground(Common.color_tablebackground);
				this.getContentPane().add(jScrollPane1);
				jScrollPane1.setBounds(3, 3, 765, 267);
				jScrollPane1.setFont(Common.font_std);
				{
					jTable1 = new JTable();
					jTable1.getTableHeader().setBounds(0, 0, 629, 16);

					jTable1.getTableHeader().setFont(Common.font_table_header);
					jTable1.getTableHeader().setForeground(Common.color_tableHeaderFont);
					jTable1.setDefaultRenderer(Object.class, Common.renderer_table);
					jTable1.setFont(Common.font_list);
					jTable1.setForeground(Common.color_listFontStandard);
					jTable1.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent evt) {
							if (evt.getClickCount() == 2)
							{
								if (Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_SYSTEM_KEYS_EDIT") == true)
								{
									editRecord();
								}
							}
						}
					});
					populateList("");

				}

			}
			{
				jButtonEdit = new JButton4j(Common.icon_edit);
				this.getContentPane().add(jButtonEdit);
				jButtonEdit.setText(lang.get("btn_Edit"));
				jButtonEdit.setBounds(115, 275, 106, 30);
				jButtonEdit.setMnemonic(lang.getMnemonicChar());
				jButtonEdit.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_SYSTEM_KEYS_EDIT"));
				jButtonEdit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						editRecord();
					}
				});
			}
			{
				jButtonClose = new JButton4j(Common.icon_close);
				this.getContentPane().add(jButtonClose);
				jButtonClose.setText(lang.get("btn_Close"));
				jButtonClose.setBounds(655, 275, 106, 30);
				jButtonClose.setMnemonic(lang.getMnemonicChar());
				jButtonClose.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						jButtonCloseActionPerformed(evt);
					}
				});
			}
			{

				jButtonAdd = new JButton4j(Common.icon_add);
				this.getContentPane().add(jButtonAdd);
				jButtonAdd.setText(lang.get("btn_Add"));
				jButtonAdd.setBounds(7, 275, 106, 30);
				jButtonAdd.setMnemonic(lang.getMnemonicChar());
				jButtonAdd.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_SYSTEM_KEYS_ADD"));
				jButtonAdd.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						JDBControl ctl = new JDBControl(Common.selectedHostID, Common.sessionID);

						systemKey = JOptionPane.showInputDialog(Common.mainForm, "Enter new System Key");
						if (systemKey != null)
						{
							if (systemKey.equals("") == false)
							{
								systemKey = systemKey.toUpperCase();
								if (ctl.create(systemKey, "", "") == false)
								{
									JUtility.errorBeep();
									JOptionPane.showMessageDialog(Common.mainForm, ctl.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
								}
								else
								{

									populateList("");
									JLaunchMenu.runDialog("FRM_ADMIN_CONTROL_EDIT", systemKey);
									populateList("");
								}
							}
						}

					}
				});
			}
			{
				jButtonDelete = new JButton4j(Common.icon_delete);
				this.getContentPane().add(jButtonDelete);
				jButtonDelete.setText(lang.get("btn_Delete"));
				jButtonDelete.setBounds(223, 275, 106, 30);
				jButtonDelete.setMnemonic(lang.getMnemonicChar());
				jButtonDelete.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_SYSTEM_KEYS_DELETE"));
				jButtonDelete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						int row = jTable1.getSelectedRow();
						if (row >= 0)
						{

							systemKey = jTable1.getValueAt(row, 0).toString();

							int n = JOptionPane.showConfirmDialog(Common.mainForm, "Delete System Key " + systemKey + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
							if (n == 0)
							{
								JDBControl c = new JDBControl(Common.selectedHostID, Common.sessionID);
								c.setSystemKey(systemKey);
								c.delete();
								populateList("");
							}
						}
					}
				});
			}
			{
				jButtonPrint = new JButton4j(Common.icon_report);
				this.getContentPane().add(jButtonPrint);
				jButtonPrint.setText(lang.get("btn_Print"));
				jButtonPrint.setBounds(439, 275, 106, 30);
				jButtonPrint.setMnemonic(lang.getMnemonicChar());
				jButtonPrint.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("RPT_CONTROL"));
				jButtonPrint.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						JLaunchReport.runReport("RPT_CONTROL",null,"",null,"");
					}
				});
			}
			{
				jButtonHelp = new JButton4j(Common.icon_help);
				this.getContentPane().add(jButtonHelp);
				jButtonHelp.setText(lang.get("btn_Help"));
				jButtonHelp.setBounds(547, 275, 106, 30);
				jButtonHelp.setMnemonic(lang.getMnemonicChar());

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void editRecord() {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{

			systemKey = jTable1.getValueAt(row, 0).toString();

			JLaunchMenu.runDialog("FRM_ADMIN_CONTROL_EDIT", systemKey);
			populateList(systemKey);
		}
	}

	private void jButtonCloseActionPerformed(ActionEvent evt) {
		JDBQuery.closeStatement(listStatement);
		dispose();
	}

}
