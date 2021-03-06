package com.commander4j.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JSpinner;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import com.commander4j.db.JDBControl;
import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBMaterial;
import com.commander4j.db.JDBProcessOrder;
import com.commander4j.db.JDBQMSample;
import com.commander4j.db.JDBQuery;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JComboBox4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JTextField4j;
import com.commander4j.sys.Common;
import com.commander4j.sys.JLaunchReport;
import com.commander4j.util.JDateControl;
import com.commander4j.util.JPrint;
import com.commander4j.util.JUtility;

public class JInternalFrameQMSampleRecord extends javax.swing.JInternalFrame
{
	private JTextField4j 	jTextFieldInspectionID;
	private JLabel4j_std 	lblInspectionID;
	private JTextField4j 	jTextFieldUserData1;
	private static final long serialVersionUID = 1;
	private JDesktopPane 	jDesktopPane1;
	private JButton4j 		jButtonCancel;
	private JLabel4j_std 	jStatusText;
	private JButton4j 		jButtonSave;
	private JTextField4j 	jTextFieldSampleID;
	private JTextField4j 	jTextFieldActivityID;
	private JLabel4j_std 	lblProcessOrderDescription;
	private JTextField4j 	jTextFieldMaterialDescription;
	private JLabel4j_std 	lblMaterialDescription;
	private JTextField4j 	jTextFieldProcessOrderDescription;
	private JLabel4j_std 	lblUserData1;
	private JTextField4j 	jTextFieldUserData2;
	private JTextField4j 	jTextFieldProcessOrder;
	private JLabel4j_std 	lblProcessOrder;
	private JLabel4j_std 	lblActivityID;
	private JTextField4j 	jTextFieldMaterial;
	private JLabel4j_std 	lblMaterial;
	private JLabel4j_std 	lblUserData2;
	private JLabel4j_std 	lblSampleID;
	private JDBQMSample 	sample = new JDBQMSample(Common.selectedHostID, Common.sessionID);
	private Long 			lsampleid;
	private JDBMaterial 	material = new JDBMaterial(Common.selectedHostID, Common.sessionID);
	private JDBProcessOrder processOrder = new JDBProcessOrder(Common.selectedHostID, Common.sessionID);
	private JDBLanguage 	lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);
	private JDateControl    sampleDate;
	private JComboBox4j 	comboBoxPrintQueue = new JComboBox4j();
	private PreparedStatement listStatement;
	private JSpinner		spinnerCopies;
	private JDBControl 		ctrl = new JDBControl(Common.selectedHostID, Common.sessionID);

	
	public JInternalFrameQMSampleRecord(String samp)
	{
		super();
		initGUI();
		blankfields();

		jTextFieldSampleID.setText(samp);
		refresh();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				jTextFieldUserData1.requestFocus();
				jTextFieldUserData1.setCaretPosition(jTextFieldUserData1.getText().length());
				jTextFieldSampleID.setEnabled(false);
				
			}
		});

	}
	
	public JInternalFrameQMSampleRecord()
	{
		super();

		initGUI();
		blankfields();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				jTextFieldSampleID.requestFocus();
				jTextFieldSampleID.setCaretPosition(jTextFieldSampleID.getText().length());
				
			}
		});

	}

	private void updateRecord()
	{
		if (sample.isValidSample(lsampleid))
		{
			sample.getProperties();
			sample.setSampleDate(JUtility.getTimestampFromDate(sampleDate.getDate()));
			sample.setUserData1(jTextFieldUserData1.getText());
			sample.setUserData2(jTextFieldUserData2.getText());
			sample.update();
		}
	}
	
	private void populatePrinterList(String defaultitem)
	{
		DefaultComboBoxModel defComboBoxMod = new DefaultComboBoxModel();

		LinkedList<String> tempPrinterList = JPrint.getPrinterNames();

		for (int j = 0; j < tempPrinterList.size(); j++)
		{
			defComboBoxMod.addElement(tempPrinterList.get(j));
		}

		int sel = defComboBoxMod.getIndexOf(defaultitem);
		ListModel jList1Model = defComboBoxMod;
		comboBoxPrintQueue.setModel((ComboBoxModel) jList1Model);
		comboBoxPrintQueue.setSelectedIndex(sel);

		if (JPrint.getNumberofPrinters() == 0)
		{
			comboBoxPrintQueue.setEnabled(false);
		} else
		{
			comboBoxPrintQueue.setEnabled(true);
		}
	}
	
	private void blankfields()
	{
		jTextFieldProcessOrder.setText("");
		jTextFieldMaterial.setText("");
		jTextFieldMaterialDescription.setText("");
		jTextFieldProcessOrderDescription.setText("");
		jTextFieldInspectionID.setText("");
		jTextFieldActivityID.setText("");
		jTextFieldUserData1.setText("");
		jTextFieldUserData2.setText("");
		sampleDate.setDate(JUtility.getSQLDate());
		jButtonSave.setEnabled(false);
	}

	private void initGUI()
	{
		try
		{
			this.setPreferredSize(new java.awt.Dimension(471, 531));
			this.setBounds(0, 0, 540, 446);
			setVisible(true);
			this.setIconifiable(true);
			this.setClosable(true);
			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				this.getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
				jDesktopPane1.setPreferredSize(new java.awt.Dimension(462, 497));
				{
					jTextFieldSampleID = new JTextField4j();
					jTextFieldSampleID.addKeyListener(new KeyAdapter()
					{
						public void keyReleased(final KeyEvent e)
						{
							refresh();
						}
					});
					jDesktopPane1.setLayout(null);
					jDesktopPane1.add(jTextFieldSampleID);
					jTextFieldSampleID.setEditable(true);
					jTextFieldSampleID.setEnabled(true);
					jTextFieldSampleID.setBounds(147, 12, 77, 21);
				}
				{
					jButtonSave = new JButton4j(Common.icon_save);
					jDesktopPane1.add(jButtonSave);
					jButtonSave.setEnabled(true);
					jButtonSave.setText(lang.get("btn_Save"));
					jButtonSave.setMnemonic(lang.getMnemonicChar());
					jButtonSave.setBounds(111, 333, 100, 28);
					jButtonSave.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{

							updateRecord();
						}
					});
				}
				{
					jButtonCancel = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonCancel);
					jButtonCancel.setText(lang.get("btn_Close"));
					jButtonCancel.setMnemonic(lang.getMnemonicChar());
					jButtonCancel.setBounds(315, 333, 100, 28);
					jButtonCancel.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{
							dispose();
						}
					});
				}

				{
					lblSampleID = new JLabel4j_std();
					jDesktopPane1.add(lblSampleID);
					lblSampleID.setText(lang.get("lbl_SampleID"));
					lblSampleID.setBounds(7, 14, 133, 21);
					lblSampleID.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					lblUserData2 = new JLabel4j_std();
					jDesktopPane1.add(lblUserData2);
					lblUserData2.setBounds(7, 250, 133, 21);
					lblUserData2.setHorizontalAlignment(SwingConstants.TRAILING);
					lblUserData2.setText(lang.get("lbl_User_Data2"));
				}

				{
					lblMaterial = new JLabel4j_std();
					jDesktopPane1.add(lblMaterial);
					lblMaterial.setText(lang.get("lbl_Material"));
					lblMaterial.setHorizontalAlignment(SwingConstants.TRAILING);
					lblMaterial.setBounds(7, 118, 133, 21);
				}
				{
					jTextFieldMaterial = new JTextField4j();
					jDesktopPane1.add(jTextFieldMaterial);
					jTextFieldMaterial.setBounds(147, 116, 119, 21);
					jTextFieldMaterial.setEnabled(false);
				}
				{
					lblActivityID = new JLabel4j_std();
					jDesktopPane1.add(lblActivityID);
					lblActivityID.setText(lang.get("lbl_Activity_ID"));
					lblActivityID.setHorizontalAlignment(SwingConstants.TRAILING);
					lblActivityID.setBounds(7, 196, 133, 21);
				}
				{
					jTextFieldActivityID = new JTextField4j();
					jDesktopPane1.add(jTextFieldActivityID);
					jTextFieldActivityID.setBounds(147, 194, 119, 21);
					jTextFieldActivityID.setEnabled(false);
				}

				{
					lblProcessOrder = new JLabel4j_std();
					jDesktopPane1.add(lblProcessOrder);
					lblProcessOrder.setText(lang.get("lbl_Process_Order"));
					lblProcessOrder.setHorizontalAlignment(SwingConstants.TRAILING);
					lblProcessOrder.setBounds(7, 66, 133, 21);
				}
				{
					jTextFieldProcessOrder = new JTextField4j();
					jDesktopPane1.add(jTextFieldProcessOrder);
					jTextFieldProcessOrder.setBounds(147, 64, 119, 21);
					jTextFieldProcessOrder.setEnabled(false);

				}
				{
					jTextFieldUserData2 = new JTextField4j();
					jDesktopPane1.add(jTextFieldUserData2);
					jTextFieldUserData2.setBounds(147, 246, 119, 21);
					jTextFieldUserData2.setFocusCycleRoot(true);
				}
				{
					lblUserData1 = new JLabel4j_std();
					jDesktopPane1.add(lblUserData1);
					lblUserData1.setText(lang.get("lbl_User_Data1"));
					lblUserData1.setHorizontalAlignment(SwingConstants.TRAILING);
					lblUserData1.setBounds(7, 224, 133, 21);
				}

				{
					jTextFieldProcessOrderDescription = new JTextField4j();
					jDesktopPane1.add(jTextFieldProcessOrderDescription);
					jTextFieldProcessOrderDescription.setBounds(147, 90, 287, 21);
					jTextFieldProcessOrderDescription.setEnabled(false);
				}
				{
					lblProcessOrderDescription = new JLabel4j_std();
					jDesktopPane1.add(lblProcessOrderDescription);
					lblProcessOrderDescription.setText(lang.get("lbl_Description"));
					lblProcessOrderDescription.setHorizontalAlignment(SwingConstants.TRAILING);
					lblProcessOrderDescription.setBounds(7, 92, 133, 21);
				}
				{
					lblMaterialDescription = new JLabel4j_std();
					jDesktopPane1.add(lblMaterialDescription);
					lblMaterialDescription.setText(lang.get("lbl_Description"));
					lblMaterialDescription.setHorizontalAlignment(SwingConstants.TRAILING);
					lblMaterialDescription.setBounds(7, 144, 133, 21);
				}
				{
					jTextFieldMaterialDescription = new JTextField4j();
					jDesktopPane1.add(jTextFieldMaterialDescription);
					jTextFieldMaterialDescription.setBounds(147, 142, 287, 21);
					jTextFieldMaterialDescription.setEnabled(false);

				}
				{
					jStatusText = new JLabel4j_std();
					jDesktopPane1.add(jStatusText);
					jStatusText.setForeground(new java.awt.Color(255, 0, 0));
					jStatusText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
					jStatusText.setBounds(0, 373, 510, 21);
				}

				{
					jTextFieldUserData1 = new JTextField4j();
					jTextFieldUserData1.setFocusCycleRoot(true);
					jTextFieldUserData1.setBounds(147, 220, 119, 21);
					jDesktopPane1.add(jTextFieldUserData1);
				}

				{
					lblInspectionID = new JLabel4j_std();
					lblInspectionID.setHorizontalAlignment(SwingConstants.TRAILING);
					lblInspectionID.setText(lang.get("lbl_Location_ID"));
					lblInspectionID.setBounds(7, 170, 133, 21);
					jDesktopPane1.add(lblInspectionID);
				}

				{
					jTextFieldInspectionID = new JTextField4j();
					jTextFieldInspectionID.setBounds(147, 168, 119, 21);
					jTextFieldInspectionID.setEnabled(false);
					jDesktopPane1.add(jTextFieldInspectionID);
				}
				
				{
					JLabel4j_std lblSampleDate = new JLabel4j_std();
					lblSampleDate.setText(lang.get("lbl_Sample_Date"));
					lblSampleDate.setHorizontalAlignment(SwingConstants.TRAILING);
					lblSampleDate.setBounds(7, 40, 133, 21);
					jDesktopPane1.add(lblSampleDate);
				}
				
				{
					sampleDate = new JDateControl();
					jDesktopPane1.add(sampleDate);
					sampleDate.setBounds(147, 36, 125, 25);
					sampleDate.getEditor().setPreferredSize(new java.awt.Dimension(87, 19));
					sampleDate.getEditor().setSize(87, 21);
				}
				{
					JButton4j btnPrint = new JButton4j(Common.icon_print);
					btnPrint.setText(lang.get("btn_Print"));
					btnPrint.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							updateRecord();
							Long sampleID = sample.generateSampleID();
							String pq = comboBoxPrintQueue.getSelectedItem().toString();
							buildSQL1Record(sampleID);
							JLaunchReport.runReport("RPT_SAMPLE_LABEL", listStatement, false, pq, Integer.valueOf(spinnerCopies.getValue().toString()), false);
							jStatusText.setText(Integer.valueOf(spinnerCopies.getValue().toString()) + " labels printed. " + sample.getSampleDate().toString());
						}
					});
					btnPrint.setMnemonic('0');
					btnPrint.setEnabled(true);
					btnPrint.setBounds(212, 333, 100, 28);
					jDesktopPane1.add(btnPrint);
				}
				
				comboBoxPrintQueue.setBounds(145, 272, 365, 25);
				jDesktopPane1.add(comboBoxPrintQueue);
				
				{
					JLabel4j_std lblNewLabel_5 = new JLabel4j_std(lang.get("lbl_Number_Of_Labels"));
					lblNewLabel_5.setHorizontalAlignment(SwingConstants.TRAILING);
					lblNewLabel_5.setBounds(6, 442, 138, 16);
					jDesktopPane1.add(lblNewLabel_5);
				}
				
				{
					spinnerCopies = new JSpinner();
					spinnerCopies.setBounds(149, 300, 37, 28);
					JSpinner.NumberEditor ne = new JSpinner.NumberEditor(spinnerCopies);
					ne.getTextField().setFont(Common.font_std);
					spinnerCopies.setEditor(ne);
					jDesktopPane1.add(spinnerCopies);					
				}
				
				JLabel4j_std label4j_std = new JLabel4j_std(lang.get("lbl_Print_Queue"));
				label4j_std.setHorizontalAlignment(SwingConstants.TRAILING);
				label4j_std.setBounds(0, 278, 138, 16);
				jDesktopPane1.add(label4j_std);
				
				JLabel4j_std label4j_std_1 = new JLabel4j_std(lang.get("lbl_Number_Of_Labels"));
				label4j_std_1.setHorizontalAlignment(SwingConstants.TRAILING);
				label4j_std_1.setBounds(0, 305, 138, 16);
				jDesktopPane1.add(label4j_std_1);
				
				populatePrinterList(JPrint.getDefaultPrinterQueueName());
				String numberOfLabels = ctrl.getKeyValueWithDefault("QM SAMPLE LABELS", "4", "Number of Labels per Sample");
				spinnerCopies.setValue(Integer.valueOf(numberOfLabels));
				
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void buildSQL1Record(Long sampleID)
	{

		JDBQuery.closeStatement(listStatement);

		String temp = "";

		JDBQuery query = new JDBQuery(Common.selectedHostID, Common.sessionID);
		query.clear();

		temp = Common.hostList.getHost(Common.selectedHostID).getSqlstatements().getSQL("JDBQMSample.selectWithLimit");

		query.addText(temp);

		query.addParamtoSQL("sample_id = ", sampleID);

		query.applyRestriction(false, "none", 0);
		query.bindParams();
		listStatement = query.getPreparedStatement();
	}
	
	private void refresh()
	{
		try
		{
			lsampleid = Long.valueOf(jTextFieldSampleID.getText());
			sample.setSampleID(lsampleid);

			if (sample.isValidSample())
			{
				sample.getProperties();

				jTextFieldProcessOrder.setText(sample.getProcessOrder());
				jTextFieldMaterial.setText(sample.getMaterial());
				jTextFieldActivityID.setText(sample.getActivityID());
				jTextFieldInspectionID.setText(sample.getInspectionID());

				jTextFieldUserData2.setText(sample.getUserData2());
				jTextFieldUserData1.setText(sample.getUserData1());

				processOrder.getProcessOrderProperties(sample.getProcessOrder());
				jTextFieldProcessOrderDescription.setText(processOrder.getDescription());

				material.getMaterialProperties(sample.getMaterial());
				jTextFieldMaterialDescription.setText(material.getDescription());
				
				sampleDate.setDate(sample.getSampleDate());

				jButtonSave.setEnabled(true);
				jStatusText.setText("Sample " + String.valueOf(sample.getSampleID()) + " retrieved.");
			} else
			{
				jStatusText.setText("Sample " + String.valueOf(sample.getSampleID()) + " does not exist.");
				blankfields();
			}
		}
		catch (Exception ex)
		{
			lsampleid = 0l;
			blankfields();
		}
		

	}
}
