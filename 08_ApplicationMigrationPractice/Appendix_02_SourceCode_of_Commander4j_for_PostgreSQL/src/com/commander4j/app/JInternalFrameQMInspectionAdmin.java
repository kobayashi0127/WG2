package com.commander4j.app;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBQMActivity;
import com.commander4j.db.JDBQMInspection;
import com.commander4j.db.JDBQMTest;
import com.commander4j.db.JDBQuery;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JList4j;
import com.commander4j.gui.JTextField4j;
import com.commander4j.sys.Common;
import com.commander4j.sys.JLaunchLookup;
import com.commander4j.sys.JLaunchMenu;
import com.commander4j.util.JUtility;

public class JInternalFrameQMInspectionAdmin extends JInternalFrame
{

	private static final long serialVersionUID = 1L;
	private JTextField4j textFieldInspectionID;
	private JTextField4j textFieldDescription;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID,Common.sessionID);
	private JDBQMInspection inspect = new JDBQMInspection(Common.selectedHostID,Common.sessionID);
	private JDBQMActivity activity = new JDBQMActivity(Common.selectedHostID,Common.sessionID);
	private JDBQMTest test = new JDBQMTest(Common.selectedHostID,Common.sessionID);
	private String schemaName = Common.hostList.getHost(Common.selectedHostID).getDatabaseParameters().getjdbcDatabaseSchema();
	private PreparedStatement listStatement;
	private JSpinner spinnerLimit;
	private JCheckBox chckbxLimit;
	private JList4j listInspection;
	private JList4j listActivity;
	private JList4j listTest;
	
	/**
	 * Create the frame.
	 */
	public JInternalFrameQMInspectionAdmin()
	{
		setVisible(true);
		setClosable(true);
		setTitle("Inspection Admin");
		setIconifiable(true);
		setBounds(100, 100, 845, 729);
		getContentPane().setLayout(null);
		
		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.setBackground(Color.WHITE);
		desktopPane.setBounds(0, 0, 833, 713);
		getContentPane().add(desktopPane);
		
		JPanel panelInspection = new JPanel();
		panelInspection.setBackground(Color.WHITE);
		panelInspection.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), lang.get("lbl_Inspection"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelInspection.setBounds(6, 72, 810, 173);
		panelInspection.setFont(Common.font_title);

		desktopPane.add(panelInspection);
		panelInspection.setLayout(null);
		
		JScrollPane scrollPaneInspection = new JScrollPane();
		scrollPaneInspection.setBounds(16, 24, 655, 132);
		panelInspection.add(scrollPaneInspection);
		
	    listInspection = new JList4j();
	    listInspection.addMouseListener(new MouseAdapter() {
	    	@Override
	    	public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2)
				{
					editInspectionRecord();
				}
	    	}
	    });
	    listInspection.addListSelectionListener(new ListSelectionListener() {
	    	public void valueChanged(ListSelectionEvent arg0) {
	    		String selectedItem = "";
	    		if (listInspection.getSelectedIndex() >=0)
	    		{
	    			selectedItem = 	((JDBQMInspection) listInspection.getSelectedValue()).getInspectionID();
	    		}
	    		populateActivityList(selectedItem,""); 
	    	}
	    });
		//listInspection.setLocation(0, 58);
		scrollPaneInspection.setViewportView(listInspection);
		
		JButton4j btnAdd1 = new JButton4j(lang.get("btn_Add"));
		btnAdd1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addInspectionRecord();
			}
		});
		btnAdd1.setIcon(Common.icon_add);
		btnAdd1.setBounds(683, 24, 117, 28);
		btnAdd1.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_QM_INSPECTION"));
		panelInspection.add(btnAdd1);
		
		JButton4j btnDelete1 = new JButton4j(lang.get("btn_Delete"));
		btnDelete1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteInspectionRecord();
			}
		});
		btnDelete1.setBounds(683, 80, 117, 28);
		btnDelete1.setIcon(Common.icon_delete);
		btnDelete1.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_QM_INSPECTION"));
		panelInspection.add(btnDelete1);
		
		JButton4j btnEdit1 = new JButton4j(lang.get("btn_Edit"));
		btnEdit1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editInspectionRecord();
			}
		});
		btnEdit1.setIcon(Common.icon_edit);
		btnEdit1.setBounds(683, 52, 117, 28);
		btnEdit1.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_QM_INSPECTION"));
		panelInspection.add(btnEdit1);
		
		JPanel panelActivity = new JPanel();
		panelActivity.setBackground(Color.WHITE);
		panelActivity.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), lang.get("lbl_Activity"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelActivity.setBounds(6, 245, 810, 184);
		panelActivity.setFont(Common.font_title);
		desktopPane.add(panelActivity);
		panelActivity.setLayout(null);
		
		JScrollPane scrollPaneActivity = new JScrollPane();
		scrollPaneActivity.setBounds(16, 24, 655, 143);
		panelActivity.add(scrollPaneActivity);
		
		listActivity = new JList4j();
		listActivity.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					editActivityRecord();
				}
			}
		});
		listActivity.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
	    		String selectedItemInsp = "";
	    		String selectedItemAct = "";
	    		if (listActivity.getSelectedIndex() >=0)
	    		{
	    			selectedItemInsp = 	((JDBQMActivity) listActivity.getSelectedValue()).getInspectionID();
	    			selectedItemAct = 	((JDBQMActivity) listActivity.getSelectedValue()).getActivityID();
	    		}
	    		populateTestList(selectedItemInsp,selectedItemAct,""); 
			}
		});
		scrollPaneActivity.setViewportView(listActivity);
		
		JButton4j btnDelete2 = new JButton4j(lang.get("btn_Delete"));
		btnDelete2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteActivityRecord();
			}
		});
		btnDelete2.setBounds(683, 80, 117, 28);
		btnDelete2.setIcon(Common.icon_delete);
		btnDelete2.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_QM_ACTIVITY"));
		panelActivity.add(btnDelete2);
		
		JButton4j btnEdit2 = new JButton4j(lang.get("btn_Edit"));
		btnEdit2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editActivityRecord();
			}
		});
		btnEdit2.setIcon(Common.icon_edit);
		btnEdit2.setBounds(683, 52, 117, 28);
		btnEdit2.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_QM_ACTIVITY"));
		panelActivity.add(btnEdit2);
		
		JButton4j btnAdd2 = new JButton4j(lang.get("btn_Add"));
		btnAdd2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addActivityRecord();
			}
		});
		btnAdd2.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_QM_ACTIVITY"));
		btnAdd2.setBounds(683, 24, 117, 28);
		btnAdd2.setIcon(Common.icon_add);
		panelActivity.add(btnAdd2);
		
		JPanel panelTests = new JPanel();
		panelTests.setBackground(Color.WHITE);
		panelTests.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), lang.get("lbl_Test"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelTests.setBounds(6, 426, 810, 238);
		panelTests.setFont(Common.font_title);
		desktopPane.add(panelTests);
		panelTests.setLayout(null);
		
		JScrollPane scrollPaneTests = new JScrollPane();
		scrollPaneTests.setBounds(16, 23, 659, 197);
		panelTests.add(scrollPaneTests);
		
		listTest = new JList4j();
		listTest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					editTestRecord();
				}
			}
		});
		scrollPaneTests.setViewportView(listTest);
		
		JButton4j btnDelete3 = new JButton4j(lang.get("btn_Delete"));
		btnDelete3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteTestRecord();
			}
		});
		btnDelete3.setIcon(Common.icon_delete);
		btnDelete3.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_QM_TEST"));
		btnDelete3.setBounds(687, 80, 117, 28);
		panelTests.add(btnDelete3);
		
		JButton4j btnEdit3 = new JButton4j(lang.get("btn_Edit"));
		btnEdit3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editTestRecord();
			}
		});
		btnEdit3.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_QM_TEST"));
		btnEdit3.setIcon(Common.icon_edit);
		btnEdit3.setBounds(687, 52, 117, 28);
		panelTests.add(btnEdit3);
		
		JButton4j btnAdd3 = new JButton4j(lang.get("btn_Add"));
		btnAdd3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addTestRecord();
			}
		});
		btnAdd3.setIcon(Common.icon_add);
		btnAdd3.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_QM_TEST"));
		btnAdd3.setBounds(687, 24, 117, 28);
		panelTests.add(btnAdd3);
		
		JButton4j btnDictionary = new JButton4j(lang.get("btn_Dictionary"));
		btnDictionary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editDictionaryRecord();
			}
		});
		btnDictionary.setIcon(Common.icon_dictionary);
		btnDictionary.setEnabled(true);
		btnDictionary.setBounds(687, 108, 117, 28);
		panelTests.add(btnDictionary);
		
		JLabel4j_std lblInspectionID = new JLabel4j_std(lang.get("lbl_Inspection_ID"));
		lblInspectionID.setBounds(6, 12, 83, 16);
		desktopPane.add(lblInspectionID);
		lblInspectionID.setHorizontalAlignment(SwingConstants.TRAILING);
		
		textFieldInspectionID = new JTextField4j();
		textFieldInspectionID.setBounds(101, 9, 117, 21);
		desktopPane.add(textFieldInspectionID);
		textFieldInspectionID.setColumns(10);
		
		JLabel4j_std lblDescription = new JLabel4j_std(lang.get("lbl_Description"));
		lblDescription.setBounds(252, 12, 110, 16);
		desktopPane.add(lblDescription);
		lblDescription.setHorizontalAlignment(SwingConstants.TRAILING);
		
		textFieldDescription = new JTextField4j();
		textFieldDescription.setBounds(371, 9, 445, 21);
		desktopPane.add(textFieldDescription);
		textFieldDescription.setColumns(10);
		
		JButton4j btnSearch1 = new JButton4j(lang.get("btn_Search"));
		btnSearch1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buildSQL();
				populateInspectList("");
			}
		});
		btnSearch1.setBounds(262, 43, 117, 28);
		desktopPane.add(btnSearch1);
		btnSearch1.setIcon(Common.icon_search);
		
		JButton4j btnClose1 = new JButton4j(lang.get("btn_Close"));
		btnClose1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnClose1.setBounds(395, 43, 117, 28);
		desktopPane.add(btnClose1);
		btnClose1.setIcon(Common.icon_close);
		
		chckbxLimit = new JCheckBox("");
		chckbxLimit.setBounds(711, 48, 28, 16);
		desktopPane.add(chckbxLimit);
		chckbxLimit.setSelected(true);
		
		spinnerLimit = new JSpinner();
		spinnerLimit.setBounds(740, 43, 76, 28);
		desktopPane.add(spinnerLimit);
		spinnerLimit.setValue(100);
		JSpinner.NumberEditor ne = new JSpinner.NumberEditor(spinnerLimit);
		ne.getTextField().setFont(Common.font_std); 
		spinnerLimit.setEditor(ne);
		
		JLabel4j_std lbl_Limit = new JLabel4j_std(lang.get("lbl_Limit"));
		lbl_Limit.setHorizontalAlignment(SwingConstants.TRAILING);
		lbl_Limit.setBounds(589, 47, 110, 16);
		desktopPane.add(lbl_Limit);
		
		JButton4j btnLookupInspection = new JButton4j("");
		btnLookupInspection.setIcon(Common.icon_lookup);
		btnLookupInspection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JLaunchLookup.dlgAutoExec = true;
				JLaunchLookup.dlgCriteriaDefault = "";
				if (JLaunchLookup.qmInspections())
				{
					textFieldInspectionID.setText(JLaunchLookup.dlgResult);
					buildSQL();
					populateInspectList("");
				}
			}
		});
		btnLookupInspection.setBounds(216, 8, 21, 22);
		desktopPane.add(btnLookupInspection);
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				textFieldInspectionID.requestFocus();
				textFieldInspectionID.setCaretPosition(textFieldInspectionID.getText().length());

			}
		});

	}

	private void addTestRecord()
	{
		if (listActivity.isSelectionEmpty()==false)
		{
			String insp = ((JDBQMInspection) listInspection.getSelectedValue()).getInspectionID();
			String act = ((JDBQMActivity) listActivity.getSelectedValue()).getActivityID();
			JLaunchMenu.runDialog("FRM_QM_TEST", insp,act,"");
			populateTestList(insp, act, "");
		}
	}
	
	private void editTestRecord()
	{
		if (listTest.isSelectionEmpty()==false)
		{
			String insp = ((JDBQMInspection) listInspection.getSelectedValue()).getInspectionID();
			String act = ((JDBQMActivity) listActivity.getSelectedValue()).getActivityID();
			String tst = ((JDBQMTest) listTest.getSelectedValue()).getTestID();
			JLaunchMenu.runDialog("FRM_QM_TEST", insp,act,tst);
			populateTestList(insp, act, tst);
		}
	}
	
	private void editDictionaryRecord()
	{
		if (listTest.isSelectionEmpty()==false)
		{
			String tst = ((JDBQMTest) listTest.getSelectedValue()).getTestID();
			JLaunchMenu.runDialog("FRM_QM_DICTIONARY",tst);
		}
	}
	
	private void editActivityRecord()
	{
		if (listActivity.isSelectionEmpty()==false)
		{
			String insp = ((JDBQMInspection) listInspection.getSelectedValue()).getInspectionID();
			String act = ((JDBQMActivity) listActivity.getSelectedValue()).getActivityID();
			JLaunchMenu.runDialog("FRM_QM_ACTIVITY", insp,act);
			populateActivityList(insp, act);
		}
	}

	private void addActivityRecord()
	{
		if (listInspection.isSelectionEmpty()==false)
		{
			String insp = ((JDBQMInspection) listInspection.getSelectedValue()).getInspectionID();
			JLaunchMenu.runDialog("FRM_QM_ACTIVITY", insp,"");
			populateActivityList(insp, "");
		}
	}

	private void addInspectionRecord()
	{
		JLaunchMenu.runDialog("FRM_QM_INSPECTION", "");
		populateInspectList("");
	}
	
	private void editInspectionRecord()
	{
		if (listInspection.isSelectionEmpty()==false)
		{
			String insp = ((JDBQMInspection) listInspection.getSelectedValue()).getInspectionID();
			JLaunchMenu.runDialog("FRM_QM_INSPECTION",insp);
			populateInspectList(insp);
		}
	}
	
	private void deleteActivityRecord()
	{
		if (listActivity.isSelectionEmpty()==false)
		{
			String insp = ((JDBQMInspection) listInspection.getSelectedValue()).getInspectionID();
			String act = ((JDBQMActivity) listActivity.getSelectedValue()).getActivityID();
			if (activity.isValid(insp, act))
			{
				int question = JOptionPane.showConfirmDialog(Common.mainForm, "Delete Activity [" + act + "] ?", lang.get("dlg_Confirm"), JOptionPane.YES_NO_OPTION);
				if (question == 0)
				{
					activity.delete();
					populateActivityList(insp, "");
				}
			}
		}
	}	
	
	private void deleteInspectionRecord()
	{
		if (listInspection.isSelectionEmpty()==false)
		{
			String insp = ((JDBQMInspection) listInspection.getSelectedValue()).getInspectionID();
			if (inspect.isValid(insp))
			{
				int question = JOptionPane.showConfirmDialog(Common.mainForm, "Delete Inspection [" + insp + "] ?", lang.get("dlg_Confirm"), JOptionPane.YES_NO_OPTION);
				if (question == 0)
				{
					inspect.delete();
					populateInspectList("");
				}
			}
		}
	}
	
	private void deleteTestRecord()
	{
		if (listTest.isSelectionEmpty()==false)
		{
			String insp = ((JDBQMInspection) listInspection.getSelectedValue()).getInspectionID();
			String act = ((JDBQMActivity) listActivity.getSelectedValue()).getActivityID();
			String tst = ((JDBQMTest) listTest.getSelectedValue()).getTestID();
			if (test.isValid(insp, act, tst))
			{
				int question = JOptionPane.showConfirmDialog(Common.mainForm, "Delete Test [" + tst + "] ?", lang.get("dlg_Confirm"), JOptionPane.YES_NO_OPTION);
				if (question == 0)
				{
					test.delete();
					populateTestList(insp, act, "");
				}
			}
		}
	}
	
	private void buildSQL() {
		
		JDBQuery.closeStatement(listStatement);
		JDBQuery query = new JDBQuery(Common.selectedHostID, Common.sessionID);
		query.clear();

		query.addText(JUtility.substSchemaName(schemaName, "select * from {schema}APP_QM_INSPECTION"));
		query.addParamtoSQL("inspection_id =", textFieldInspectionID.getText());
		query.addParamtoSQL("description like ", "%"+textFieldDescription.getText()+"%");

		query.appendSort("inspection_id", true);
		
		query.applyRestriction(chckbxLimit.isSelected(),Common.hostList.getHost(Common.selectedHostID).getDatabaseParameters().getjdbcDatabaseSelectLimit(), spinnerLimit.getValue());
		
		query.bindParams();
		listStatement = query.getPreparedStatement();
	}
	
	private void populateInspectList(String defaultitem) 
	{
		DefaultComboBoxModel DefComboBoxMod = new DefaultComboBoxModel();
		ResultSet rs;
		int sel = -1;
		int x=0;
		try {

			rs = listStatement.executeQuery();

			while (rs.next()) {

				JDBQMInspection mt = new JDBQMInspection("","");
				mt.setInspectionID(rs.getString("inspection_id"));
				mt.setDescription(rs.getString("description"));
				DefComboBoxMod.addElement(mt);
				if (mt.getInspectionID().equals(defaultitem))
				{
					sel = x;
				}
				x++;
			}
			rs.close();
			if (sel==-1) sel=0;
		}
		catch (SQLException e) {
			System.out.print(e.getMessage());
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
		

		ListModel jList1Model = DefComboBoxMod;
		listInspection.setModel(jList1Model);
		listInspection.setSelectedIndex(sel);
		listInspection.setCellRenderer(Common.renderer_list);
		listInspection.ensureIndexIsVisible(sel);

	}
	
	private void populateActivityList(String inspectionid,String activityid) 
	{
		DefaultComboBoxModel DefComboBoxMod = new DefaultComboBoxModel();
		LinkedList<JDBQMActivity> activityList = new LinkedList<JDBQMActivity>();
		
		activityList = activity.getActivities(inspectionid);
		
		int sel = -1;
		for (int j = 0; j < activityList.size(); j++)
		{
			JDBQMActivity t = (JDBQMActivity) activityList.get(j);
			DefComboBoxMod.addElement(t);
			if (t.getActivityID().equals(activityid))
			{
				sel = j;
			}
		}
		if (sel==-1) sel=0;
		ListModel jList1Model = DefComboBoxMod;
		listActivity.setModel(jList1Model);
		listActivity.setSelectedIndex(sel);
		listActivity.setCellRenderer(Common.renderer_list);
		listActivity.ensureIndexIsVisible(sel);
		
	}
	
	private void populateTestList(String inspectionid,String activityid,String testid) 
	{
		DefaultComboBoxModel DefComboBoxMod = new DefaultComboBoxModel();
		LinkedList<JDBQMTest> testList = new LinkedList<JDBQMTest>();
		
		testList = test.getTests(inspectionid,activityid);
		
		int sel = -1;
		for (int j = 0; j < testList.size(); j++)
		{
			JDBQMTest t = (JDBQMTest) testList.get(j);
			DefComboBoxMod.addElement(t);
			if (t.getTestID().equals(testid))
			{
				sel = j;
			}
		}
		if (sel==-1) sel=0;
		ListModel jList1Model = DefComboBoxMod;
		listTest.setModel(jList1Model);
		listTest.setSelectedIndex(sel);
		listTest.setCellRenderer(Common.renderer_list);
		listTest.ensureIndexIsVisible(sel);
		
	}
}