package com.commander4j.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBQMDictionary;
import com.commander4j.db.JDBQMSelectList;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JComboBox4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JTextField4j;
import com.commander4j.sys.Common;
import com.commander4j.util.JUtility;

public class JDialogQMDictionaryProperties extends javax.swing.JDialog
{

	private static final long serialVersionUID = 1L;
	private JTextField4j textFieldITestD;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID,Common.sessionID);
	private JDBQMDictionary dict = new JDBQMDictionary(Common.selectedHostID,Common.sessionID);
	private JDBQMSelectList slist = new JDBQMSelectList(Common.selectedHostID,Common.sessionID);
	private JTextField4j textFieldDescription;
	private JButton4j btnSave;
	private JButton4j btnClose;
	private JComboBox4j comboBoxSelectList;
	private JComboBox4j comboBoxDataType;
	private JCheckBox chckbxVisible;
	private JTextField4j textFieldUOM;
	private JSpinner spinnerWidth;
	private String[] fieldAlignment = new String[] { "Left", "Right", "Center" };
	private JComboBox4j comboBoxAlignment;
	private void enableSave()
	{
		if (textFieldITestD.getText().equals("")==false)
		{
			if (textFieldDescription.getText().equals("")==false)
			{
				btnSave.setEnabled(true);
			}
		}
	}
	
	private void save()
	{
		String insp = textFieldITestD.getText();
		String description = textFieldDescription.getText();
		String visible;
		if (chckbxVisible.isSelected())
		{
			visible = "Y";

		}
		else
		{
			visible="N";

		}
		
		if (dict.isValid(insp)==false)
		{
			//dict.create(insp,description);
			dict.create(insp, JLabel.TRAILING, comboBoxDataType.getSelectedItem().toString(), textFieldUOM.getText(), "Y", description, visible, 10);
		}
		else
		{
			dict.setFieldAlignment(comboBoxAlignment.getSelectedItem().toString());
			dict.setDescription(description);
			dict.setDataType(comboBoxDataType.getSelectedItem().toString());
			dict.setSelectListID(comboBoxSelectList.getSelectedItem().toString());
			dict.setVisible(visible);
			dict.setUOM(textFieldUOM.getText());
			dict.setFieldWidth(Integer.valueOf(spinnerWidth.getValue().toString()));
			dict.update();				
		}
	}

	
	public JDialogQMDictionaryProperties(JFrame frame,String testid) {
		
		super(frame,"Dictionary Properties",ModalityType.APPLICATION_MODAL);
		
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle("Dictionary Properties");
		this.setResizable(false);		
		this.setSize(593, 336);

		
		Dimension screensize = Common.mainForm.getSize();

		Dimension formsize = getSize();
		int leftmargin = ((screensize.width - formsize.width) / 2);
		int topmargin = ((screensize.height - formsize.height) / 2);

		setLocation(leftmargin, topmargin);

		getContentPane().setBackground(Color.LIGHT_GRAY);
		getContentPane().setLayout(null);
		
		testid = JUtility.replaceNullStringwithBlank(testid);
		dict.getProperties(testid);
		
		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.setBackground(Color.WHITE);
		desktopPane.setBounds(0, 0, 593, 314);
		getContentPane().add(desktopPane);
		
		JLabel4j_std lblInspectionID = new JLabel4j_std(lang.get("lbl_Test_ID"));
		lblInspectionID.setBounds(8, 27, 92, 16);
		desktopPane.add(lblInspectionID);
		lblInspectionID.setHorizontalAlignment(SwingConstants.TRAILING);
		
		textFieldITestD = new JTextField4j();
		textFieldITestD.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				enableSave();
			}
		});
		textFieldITestD.setEnabled(false);
		textFieldITestD.setBounds(112, 22, 231, 28);
		desktopPane.add(textFieldITestD);
		textFieldITestD.setColumns(10);
		
		
		textFieldDescription = new JTextField4j();
		textFieldDescription.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				enableSave();
			}
		});
		textFieldDescription.setBounds(112, 55, 463, 28);
		desktopPane.add(textFieldDescription);
		textFieldDescription.setColumns(10);
		

		
		btnSave = new JButton4j(lang.get("btn_Save"));
		btnSave.setEnabled(false);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		btnSave.setIcon(Common.icon_save);
		btnSave.setBounds(175, 263, 117, 29);
		desktopPane.add(btnSave);
		
		btnClose = new JButton4j(lang.get("btn_Close"));
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnClose.setIcon(Common.icon_close);
		btnClose.setBounds(304, 263, 117, 29);
		desktopPane.add(btnClose);
		
		JLabel4j_std lblDescription = new JLabel4j_std(lang.get("lbl_Description"));
		lblDescription.setHorizontalAlignment(SwingConstants.TRAILING);
		lblDescription.setBounds(8, 60, 92, 16);
		desktopPane.add(lblDescription);
		
		JLabel4j_std lblDataType = new JLabel4j_std(lang.get("lbl_DataType"));
		lblDataType.setHorizontalAlignment(SwingConstants.TRAILING);
		lblDataType.setBounds(8, 97, 92, 16);
		desktopPane.add(lblDataType);
		
		ComboBoxModel jComboBox1Model = new DefaultComboBoxModel(Common.dataTypes);
		comboBoxDataType = new JComboBox4j();
		comboBoxDataType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				enableSave();
			}
		});
		comboBoxDataType.setModel(jComboBox1Model);
		comboBoxDataType.setBounds(111, 94, 153, 27);
		desktopPane.add(comboBoxDataType);
	
		comboBoxSelectList = new JComboBox4j();
		comboBoxSelectList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enableSave();
			}
		});
		comboBoxSelectList.addItem("");
		LinkedList<String> items = new LinkedList<String>();
		items = slist.getSelectListSummary();
		
		if (dict.getSelectListID().equals("")==false)
		{
			if (items.contains(dict.getSelectListID())==false)
			{
				items.addFirst(dict.getSelectListID());
			}
		}
		
		for (int x=0;x<items.size();x++)
		{
			comboBoxSelectList.addItem(items.get(x));
		}

		comboBoxSelectList.setBounds(112, 133, 153, 27);
		desktopPane.add(comboBoxSelectList);
		
		JLabel4j_std lblSelectListID = new JLabel4j_std(lang.get("lbl_List_ID"));
		lblSelectListID.setHorizontalAlignment(SwingConstants.TRAILING);
		lblSelectListID.setBounds(8, 138, 92, 16);
		desktopPane.add(lblSelectListID);
		
		chckbxVisible = new JCheckBox("");
		chckbxVisible.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enableSave();
			}
		});
		chckbxVisible.setSelected(true);
		chckbxVisible.setBounds(112, 172, 28, 23);
		desktopPane.add(chckbxVisible);
		
		JLabel4j_std lblVisible = new JLabel4j_std(lang.get("lbl_Visible"));
		lblVisible.setHorizontalAlignment(SwingConstants.TRAILING);
		lblVisible.setBounds(8, 179, 92, 16);
		desktopPane.add(lblVisible);
		
		JLabel4j_std lblUOM = new JLabel4j_std(lang.get("lbl_Material_UOM"));
		lblUOM.setHorizontalAlignment(SwingConstants.TRAILING);
		lblUOM.setBounds(8, 213, 92, 16);
		desktopPane.add(lblUOM);
		
		textFieldUOM = new JTextField4j();
		textFieldUOM.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				enableSave();
			}
		});
		textFieldUOM.setText("");
		textFieldUOM.setColumns(10);
		textFieldUOM.setBounds(112, 206, 143, 28);
		desktopPane.add(textFieldUOM);
		
		spinnerWidth = new JSpinner();

		spinnerWidth.setBounds(494, 132, 68, 28);
		JSpinner.NumberEditor ne_spinnerWidth = new JSpinner.NumberEditor(spinnerWidth);
		ne_spinnerWidth.getTextField().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				enableSave();
			}
		});

		ne_spinnerWidth.getTextField().setFont(Common.font_std); 
		spinnerWidth.setEditor(ne_spinnerWidth);
		spinnerWidth.setValue(50);
		spinnerWidth.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				enableSave();
			}
		});
		
		desktopPane.add(spinnerWidth);

		
		JLabel4j_std label4j_std = new JLabel4j_std("Width");
		label4j_std.setHorizontalAlignment(SwingConstants.TRAILING);
		label4j_std.setBounds(392, 136, 92, 16);
		desktopPane.add(label4j_std);
		
		comboBoxAlignment = new JComboBox4j(fieldAlignment);
		comboBoxAlignment.setBounds(464, 94, 98, 27);
		desktopPane.add(comboBoxAlignment);
		
		// ****************** //
		 
		textFieldITestD.setText(testid);

		textFieldDescription.setText(dict.getDescription());
		
		comboBoxDataType.setSelectedItem(dict.getDataType());
		comboBoxSelectList.setSelectedItem(dict.getSelectListID());
		
		if (dict.getVisible().equals("Y"))
			chckbxVisible.setSelected(true);
		else
			chckbxVisible.setSelected(false);
		
		textFieldUOM.setText(dict.getUOM());
		
		spinnerWidth.setValue(dict.getFieldWidth());

		if (testid.equals(""))
		{
			textFieldITestD.setEnabled(true);
			
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					textFieldITestD.requestFocus();
					textFieldITestD.setCaretPosition(textFieldITestD.getText().length());
				}
			});
			

		}
		else
		{
			textFieldITestD.setEnabled(false);
			
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					textFieldDescription.requestFocus();
					textFieldDescription.setCaretPosition(textFieldDescription.getText().length());
				}
			});
			

		}
		
		comboBoxAlignment.setSelectedItem(dict.getFieldAlignment());

		btnSave.setEnabled(false);
		
		JLabel4j_std label4j_std_1 = new JLabel4j_std("Width");
		label4j_std_1.setText("Align");
		label4j_std_1.setHorizontalAlignment(SwingConstants.TRAILING);
		label4j_std_1.setBounds(364, 97, 92, 16);
		desktopPane.add(label4j_std_1);
		

	}
}
