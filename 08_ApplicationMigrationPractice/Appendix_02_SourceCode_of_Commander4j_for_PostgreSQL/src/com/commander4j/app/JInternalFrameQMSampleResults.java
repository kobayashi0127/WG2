package com.commander4j.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBProcessOrder;
import com.commander4j.db.JDBQMActivity;
import com.commander4j.db.JDBQMInspection;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JComboBox4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JTextField4j;
import com.commander4j.sys.Common;
import com.commander4j.sys.JLaunchLookup;
import com.commander4j.sys.JLaunchMenu;
import com.commander4j.table.JDBQMResultTable;
import com.commander4j.tablemodel.JDBQMResultTableModelData;
import com.commander4j.tablemodel.JDBQMResultTableModelIndex;
import com.commander4j.util.JUtility;

public class JInternalFrameQMSampleResults extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField4j textFieldProcessOrder;
	private JButton4j btnClose;
	private JButton4j btnEdit;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);
	private JDBProcessOrder po = new JDBProcessOrder(Common.selectedHostID, Common.sessionID);
	private JDBQMActivity activity = new JDBQMActivity(Common.selectedHostID,Common.sessionID);
	private JDBQMInspection insp = new JDBQMInspection(Common.selectedHostID, Common.sessionID);
	private JComboBox4j comboboxActivities;
	private JLabel4j_std lblStatusBar;
	private JDBQMResultTable jTableIndex;
	private JDBQMResultTable jTableData;
	private JViewport viewport;
	private JDBQMResultTableModelIndex indexTableModel;
	private JDBQMResultTableModelData dataTableModel;
	private ListSelectionModel model;
	private Dimension indexSize;
	private JTextField4j textFieldDescription ;
	private JTextField4j textField4Material;
	private JLabel4j_std lbl_inspection;
	private JTextField4j textField4InspectionID;
	private JTextField4j textField4jInspectionDescription;

	private JScrollPane jScrollPane1;

	
	private void processOrderChanged(String processOrder)
	{
		if (po.isValidProcessOrder(processOrder))
		{
			po.getProcessOrderProperties(processOrder);
			textFieldProcessOrder.setText(po.getProcessOrder());
			textField4Material.setText(po.getMaterial());
			textFieldDescription.setText(po.getDescription());
			textField4InspectionID.setText(po.getInspectionID());
			insp.getProperties(po.getInspectionID());
			textField4jInspectionDescription.setText(insp.getDescription());
			populateActivityList(po.getInspectionID());
		}
		else
		{
			populateActivityList("");
			textField4Material.setText("");
			textFieldDescription.setText("");
		}
		populateTable();
		lblStatusBar.setText("");
	}
	
	private void populateActivityList(String inspectionid) {
		DefaultComboBoxModel defComboBoxMod = new DefaultComboBoxModel();
		defComboBoxMod.addElement(new JDBQMActivity(Common.selectedHostID, Common.sessionID));
		LinkedList<JDBQMActivity> tempActivityList = activity.getActivities(inspectionid);
		
		for (int j = 0; j < tempActivityList.size(); j++)
		{
			defComboBoxMod.addElement(tempActivityList.get(j));
		}
	
		comboboxActivities.setModel(defComboBoxMod);

	}	
	
	private void populateTable()
	{

		initializeTable( po.getInspectionID(), ((JDBQMActivity) comboboxActivities.getSelectedItem()).getActivityID());
		
		indexTableModel = new JDBQMResultTableModelIndex(Common.selectedHostID,Common.sessionID,po.getProcessOrder(),po.getInspectionID(),((JDBQMActivity) comboboxActivities.getSelectedItem()).getActivityID());
		jTableIndex.setModel(indexTableModel);
		jTableIndex.setCellRenderers(po.getProcessOrder(), po.getInspectionID(), ((JDBQMActivity) comboboxActivities.getSelectedItem()).getActivityID(),"index");
		jTableIndex.setColumnWidths();
		
		dataTableModel = new JDBQMResultTableModelData(Common.selectedHostID,Common.sessionID,po.getProcessOrder(),po.getInspectionID(),((JDBQMActivity) comboboxActivities.getSelectedItem()).getActivityID());
		jTableData.setModel(dataTableModel);
		jTableData.setCellRenderers(po.getProcessOrder(), po.getInspectionID(), ((JDBQMActivity) comboboxActivities.getSelectedItem()).getActivityID(),"data");
		jTableData.setColumnEditors(po.getProcessOrder(), po.getInspectionID(), ((JDBQMActivity) comboboxActivities.getSelectedItem()).getActivityID());
		jTableData.setColumnWidths();
		
		model = jTableIndex.getSelectionModel();
		jTableData.setSelectionModel(model);
			
		indexSize = jTableIndex.getPreferredSize();
		viewport = new JViewport();
		viewport.setBackground(Common.color_tablebackground);
		viewport.setView(jTableIndex);
		viewport.setPreferredSize(indexSize);
		viewport.setMaximumSize(indexSize);
		jScrollPane1.setCorner(JScrollPane.UPPER_LEFT_CORNER, jTableIndex.getTableHeader());
		jScrollPane1.setRowHeaderView(viewport);
		
		jScrollPane1.setViewportView(jTableData);
		
		JUtility.scrolltoHomePosition(jScrollPane1);
		jTableIndex.repaint();
		jTableData.repaint();
		jScrollPane1.repaint();
		
		JUtility.setResultRecordCountColour(lblStatusBar, false, 99999, jTableData.getRowCount());
	}
	
	private void editRecord()
	{
		int row = jTableIndex.getSelectedRow();
		if (row >= 0)
		{
			String temp = jTableIndex.getValueAt(row, 0).toString();
			JLaunchMenu.runForm("FRM_QM_SAMPLE_EDIT", temp);
		}
	}
	
	public JInternalFrameQMSampleResults(String processOrder) 
	{
		super();
		
		processOrderChanged(processOrder);
		
	}
	
	/**
	 * Create the frame.
	 */
	public JInternalFrameQMSampleResults() {
		
		setVisible(true);
		this.setClosable(true);
		this.setIconifiable(true);
		setBounds(100, 100, 1036, 693);
		getContentPane().setLayout(null);
		
		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.setBounds(0, 0, 1023, 666);
		desktopPane.setBackground(Color.WHITE);
		getContentPane().add(desktopPane);
		desktopPane.setLayout(null);
		
		JLabel4j_std lblProcessOrder = new JLabel4j_std(lang.get("lbl_Process_Order"));
		lblProcessOrder.setBounds(6, 18, 111, 16);
		lblProcessOrder.setHorizontalAlignment(SwingConstants.TRAILING);
		desktopPane.add(lblProcessOrder);
		
		textFieldProcessOrder = new JTextField4j();
		textFieldProcessOrder.setBounds(125, 15, 138, 22);
		textFieldProcessOrder.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent evt)
			{
				processOrderChanged(textFieldProcessOrder.getText());
			}
		});
		desktopPane.add(textFieldProcessOrder);
		textFieldProcessOrder.setColumns(10);
		
		JLabel4j_std lblNewLabel_3 = new JLabel4j_std(lang.get("lbl_Activity_ID"));
		lblNewLabel_3.setBounds(6, 98, 111, 16);
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.TRAILING);
		desktopPane.add(lblNewLabel_3);
		
		btnClose = new JButton4j(lang.get("btn_Close"));
		btnClose.setBounds(877, 92, 117, 29);
		btnClose.setIcon(Common.icon_close);
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		desktopPane.add(btnClose);
		
		btnEdit = new JButton4j(lang.get("btn_Edit"));
		btnEdit.setBounds(748, 92, 117, 29);
		btnEdit.setIcon(Common.icon_edit);
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editRecord();
			}
		});
		desktopPane.add(btnEdit);
		
		JButton4j btnRefresh = new JButton4j(lang.get("btn_Refresh"));
		btnRefresh.setBounds(619, 92, 117, 29);
		btnRefresh.setIcon(Common.icon_refresh);
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				populateTable();
			}
		});
		desktopPane.add(btnRefresh);
		
		JButton btnProcessOrderLookup = new JButton();
		btnProcessOrderLookup.setIcon(Common.icon_lookup);
		btnProcessOrderLookup.setBounds(261, 15, 21, 22);
		btnProcessOrderLookup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JLaunchLookup.dlgCriteriaDefault = "Ready";
				JLaunchLookup.dlgAutoExec = true;
				if (JLaunchLookup.processOrders())
				{
					textFieldProcessOrder.setText(JLaunchLookup.dlgResult);
					processOrderChanged(JLaunchLookup.dlgResult);
				}
			}
		});
		desktopPane.add(btnProcessOrderLookup);
		
		comboboxActivities = new JComboBox4j();
		comboboxActivities.setBounds(125, 93, 470, 28);
		comboboxActivities.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				populateTable();
			}
		});
		desktopPane.add(comboboxActivities);
		
		lblStatusBar = new JLabel4j_std();
		lblStatusBar.setBounds(8, 615, 990, 21);
		lblStatusBar.setForeground(Color.RED);
		lblStatusBar.setBackground(Color.GRAY);
		desktopPane.add(lblStatusBar);
		
		jScrollPane1 = new JScrollPane();
		jScrollPane1.setBounds(8, 139, 995, 470);
		jScrollPane1.getViewport().setBackground(Common.color_tablebackground);
		desktopPane.setLayout(null);
		desktopPane.add(jScrollPane1);

		
		initializeTable("","");
		
		textField4Material = new JTextField4j();
		textField4Material.setEnabled(false);
		textField4Material.setColumns(10);
		textField4Material.setBounds(413, 15, 138, 22);
		desktopPane.add(textField4Material);
		
		textFieldDescription = new JTextField4j();
		textFieldDescription.setEnabled(false);
		textFieldDescription.setColumns(10);
		textFieldDescription.setBounds(563, 15, 431, 22);
		desktopPane.add(textFieldDescription);
		
		JLabel4j_std lbl_material = new JLabel4j_std(lang.get("lbl_Material"));
		lbl_material.setHorizontalAlignment(SwingConstants.TRAILING);
		lbl_material.setBounds(294, 18, 111, 16);
		desktopPane.add(lbl_material);
		
		textField4InspectionID = new JTextField4j();
		textField4InspectionID.setEnabled(false);
		textField4InspectionID.setColumns(10);
		textField4InspectionID.setBounds(125, 52, 138, 22);
		desktopPane.add(textField4InspectionID);
		
		lbl_inspection = new JLabel4j_std(lang.get("lbl_Inspection_ID"));
		lbl_inspection.setHorizontalAlignment(SwingConstants.TRAILING);
		lbl_inspection.setBounds(6, 56, 111, 16);
		desktopPane.add(lbl_inspection);
		
		textField4jInspectionDescription = new JTextField4j();
		textField4jInspectionDescription.setEnabled(false);
		textField4jInspectionDescription.setColumns(10);
		textField4jInspectionDescription.setBounds(271, 52, 723, 22);
		desktopPane.add(textField4jInspectionDescription);
		
		processOrderChanged("");
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				textFieldProcessOrder.requestFocus();
				textFieldProcessOrder.setCaretPosition(textFieldProcessOrder.getText().length());

			}
		});
		
	}
	
	private void initializeTable(String insp,String act)
	{		
		jTableIndex = new JDBQMResultTable(Common.selectedHostID,Common.sessionID,insp,act,"index");
		jTableIndex.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jTableData = new JDBQMResultTable(Common.selectedHostID,Common.sessionID,insp,act,"data");	
		jTableData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
}
