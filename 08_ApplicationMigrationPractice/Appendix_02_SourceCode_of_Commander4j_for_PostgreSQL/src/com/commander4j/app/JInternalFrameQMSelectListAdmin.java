package com.commander4j.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBQMSelectList;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JList4j;
import com.commander4j.gui.JMenuItem4j;
import com.commander4j.sys.Common;
import com.commander4j.sys.JLaunchMenu;
import com.commander4j.util.JExcel;

public class JInternalFrameQMSelectListAdmin extends javax.swing.JInternalFrame
{
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JList4j   jListSelectLists;
	private JButton4j jButtonClose;
	private JButton4j jButtonRefresh;
	private JButton4j jButtonEdit;
	private JButton4j jButtonDelete;
	private JButton4j jButtonAdd;
	private JScrollPane jScrollPane1;
	private String llistid;
	private String llistval;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);


	public JInternalFrameQMSelectListAdmin()
	{
		super();
		initGUI();
		populateList("","");
	}
	
	private void editRecord()
	{
		if (jListSelectLists.isSelectionEmpty()==false)
		{
			String id = ((JDBQMSelectList) jListSelectLists.getSelectedValue()).getSelectListID();
			String val = ((JDBQMSelectList) jListSelectLists.getSelectedValue()).getValue();
			JLaunchMenu.runDialog("FRM_QM_SELECTLIST", id,val);
		}
	}

	private void addRecord()
	{
		JLaunchMenu.runDialog("FRM_QM_SELECTLIST", "","");
	}
	
	private void populateList(String defaultid,String defaultval) {

		DefaultComboBoxModel DefComboBoxMod = new DefaultComboBoxModel();

		JDBQMSelectList tempSelectList = new JDBQMSelectList(Common.selectedHostID, Common.sessionID);

		tempSelectList.setDisplayModeLong(true);
		LinkedList<JDBQMSelectList> tempList = tempSelectList.getSelectLists();
		int sel = -1;
		for (int j = 0; j < tempList.size(); j++)
		{
			JDBQMSelectList t = (JDBQMSelectList) tempList.get(j);
			DefComboBoxMod.addElement(t);
			if (t.getSelectListID().equals(defaultid) & (t.getSelectListID().equals(defaultval)))
			{
				sel = j;
			}
		}

		ListModel jList1Model = DefComboBoxMod;
		jListSelectLists.setModel(jList1Model);
		jListSelectLists.setSelectedIndex(sel);
		jListSelectLists.setCellRenderer(Common.renderer_list);
		jListSelectLists.ensureIndexIsVisible(sel);
	}

	private void deleteRecord() {
		if (jListSelectLists.isSelectionEmpty() == false)
		{
			llistid = ((JDBQMSelectList) jListSelectLists.getSelectedValue()).getSelectListID();
			llistval = ((JDBQMSelectList) jListSelectLists.getSelectedValue()).getValue();
			int question = JOptionPane.showConfirmDialog(Common.mainForm, lang.get("dlg_SelectList_Delete") + " " + llistid + "/" + llistval +" ?", lang.get("dlg_Confirm"), JOptionPane.YES_NO_OPTION);
			if (question == 0)
			{
				JDBQMSelectList u = new JDBQMSelectList(Common.selectedHostID, Common.sessionID);
				u.setSelectListID(llistid);
				u.setValue(llistval);
				u.delete();
				populateList("","");
			}
		}
	}


	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(455, 518));
			this.setBounds(0, 0, 547, 541);
			setVisible(true);
			this.setClosable(true);
			this.setIconifiable(true);
			this.setTitle("Select List Admin");
			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				this.getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
				jDesktopPane1.setLayout(null);
				{
					jScrollPane1 = new JScrollPane();
					jDesktopPane1.add(jScrollPane1);
					jScrollPane1.setBounds(10, 10, 358, 476);
					{
						ListModel jList1Model = new DefaultComboBoxModel(new String[] { "Item One", "Item Two" });
						jListSelectLists = new JList4j();
						jScrollPane1.setViewportView(jListSelectLists);
						jListSelectLists.addMouseListener(new MouseAdapter() {
							public void mouseClicked(MouseEvent evt) {
								if (evt.getClickCount() == 2)
								{
									editRecord();
								}
							}
						});
						jListSelectLists.setModel(jList1Model);

						{
							final JPopupMenu popupMenu = new JPopupMenu();
							addPopup(jListSelectLists, popupMenu);

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_add);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										addRecord();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Add"));
								newItemMenuItem.setEnabled(true);
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_delete);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										deleteRecord();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Delete"));
								newItemMenuItem.setEnabled(true);
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
								newItemMenuItem.setEnabled(true);
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_refresh);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										populateList("","");
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
					jButtonAdd.setBounds(380, 10, 125, 30);
					jButtonAdd.setEnabled(true);
					jButtonAdd.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							addRecord();

						}
					});
				}
				{
					jButtonDelete = new JButton4j(Common.icon_delete);
					jDesktopPane1.add(jButtonDelete);
					jButtonDelete.setText(lang.get("btn_Delete"));
					jButtonDelete.setMnemonic(lang.getMnemonicChar());
					jButtonDelete.setBounds(380, 41, 125, 30);
					jButtonDelete.setEnabled(true);
					jButtonDelete.setFocusTraversalKeysEnabled(false);
					jButtonDelete.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							deleteRecord();

						}
					});
				}
				{
					jButtonEdit = new JButton4j(Common.icon_edit);
					jDesktopPane1.add(jButtonEdit);
					jButtonEdit.setText(lang.get("btn_Edit"));
					jButtonEdit.setMnemonic(lang.getMnemonicChar());
					jButtonEdit.setBounds(380, 72, 125, 30);
					jButtonEdit.setEnabled(true);
					jButtonEdit.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							editRecord();
						}
					});
				}

				{
					jButtonRefresh = new JButton4j(Common.icon_refresh);
					jDesktopPane1.add(jButtonRefresh);
					jButtonRefresh.setText(lang.get("btn_Refresh"));
					jButtonRefresh.setMnemonic(lang.getMnemonicChar());
					jButtonRefresh.setBounds(380, 102, 125, 30);
					jButtonRefresh.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							populateList("","");
						}
					});
				}
				{
					jButtonClose = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonClose);
					jButtonClose.setText(lang.get("btn_Close"));
					jButtonClose.setMnemonic(lang.getMnemonicChar());
					jButtonClose.setBounds(380, 162, 125, 30);
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							dispose();
						}
					});
					
					JButton4j btnExcel = new JButton4j(Common.icon_XLS);
					btnExcel.setText(lang.get("btn_Excel"));
					btnExcel.setBounds(380, 132, 125, 30);
					btnExcel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							excel();
						}
					});
					jDesktopPane1.add(btnExcel);

				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void excel()
	{
		JDBQMSelectList slist = new JDBQMSelectList(Common.selectedHostID, Common.sessionID);
		JExcel export = new JExcel();
		PreparedStatement ps = slist.getSelectListPreparedStatement();
		export.saveAs("qm_selectlist.xls", slist.getQMSelectListResultSet(ps), Common.mainForm);
		try
		{
			ps.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
			//PG add ->
			String driver = Common.hostList.getHost(Common.selectedHostID).getDatabaseParameters().getjdbcDriver();
			if (driver.equals("org.postgresql.Driver")){
				try
				{
					Common.hostList.getHost(Common.selectedHostID).getConnection(Common.sessionID).rollback();
				}
				catch (Exception e2)
				{
					e2.printStackTrace();
				}
			}
			//PG add end
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
