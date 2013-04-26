package com.commander4j.app;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.LinkedList;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.commander4j.db.JDBControl;
import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBProcessOrder;
import com.commander4j.db.JDBQMActivity;
import com.commander4j.db.JDBQMSample;
import com.commander4j.db.JDBQuery;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JComboBox4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JList4j;
import com.commander4j.gui.JTextField4j;
import com.commander4j.sys.Common;
import com.commander4j.sys.JLaunchLookup;
import com.commander4j.sys.JLaunchReport;
import com.commander4j.util.JDateControl;
import com.commander4j.util.JPrint;
import com.commander4j.util.JUtility;
import com.commander4j.util.UppercaseDocumentFilter;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.Timer;

public class JInternalFrameQMSampleLabel extends JInternalFrame
{

	private static final long serialVersionUID = 1L;
	private JTextField4j textFieldProcessOrder;
	private JTextField4j textFieldDescription;
	private JTextField4j textFieldInspectionID;
	private JTextField4j textFieldMaterial;
	private JTextField4j textFieldStatus;
	private JButton4j btnPrint;
	private JButton4j btnClose;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);
	private JDBProcessOrder po = new JDBProcessOrder(Common.selectedHostID, Common.sessionID);
	private JDBControl ctrl = new JDBControl(Common.selectedHostID, Common.sessionID);
	private JDBQMActivity activity = new JDBQMActivity(Common.selectedHostID, Common.sessionID);
	private JDBQMSample sample = new JDBQMSample(Common.selectedHostID, Common.sessionID);
	private JComboBox4j comboBoxPrintQueue = new JComboBox4j();
	private JSpinner spinnerCopies;
	private JList4j listActivities;
	private JTextField4j textFieldResource;
	private JDateControl dueDate;
	private PreparedStatement listStatement;
	private JLabel4j_std lblUserData1;
	private JLabel4j_std lblUserData2;
	private JTextField4j textFieldUserData1;
	private JTextField4j textFieldUserData2;
	private JLabel lblClock;
	private ClockListener clocklistener = new ClockListener();
	private Timer timer = new Timer(1000, clocklistener);
	private JLabel4j_std lblStatusBar;
	private Boolean qmud1 = false;
	private Boolean qmud2 = false;
	private Boolean processOrderValid = false;

	public class ClockListener implements ActionListener
	{
		int hour = 0;
		int min = 0;
		int sec = 0;

		String hours = "";
		String mins = "";
		String secs = "";

		public void actionPerformed(ActionEvent event)
		{
			Calendar rightNow = Calendar.getInstance();

			hour = rightNow.get(Calendar.HOUR_OF_DAY);
			min = rightNow.get(Calendar.MINUTE);
			sec = rightNow.get(Calendar.SECOND);

			hours = JUtility.padString(String.valueOf(hour), false, 2, "0");
			mins = JUtility.padString(String.valueOf(min), false, 2, "0");
			secs = JUtility.padString(String.valueOf(sec), false, 2, "0");

			lblClock.setText(hours + ":" + mins + ":" + secs);
		}
	}

	private void printEnable()
	{
		Boolean result = false;
		Boolean ud1 = false;
		Boolean ud2 = false;
		if (processOrderValid == true)
		{
			if (listActivities.getSelectedIndex() != -1)
			{
				ud1 = false;
				if (qmud1 == true)
				{
					if (textFieldUserData1.getText().equals("") == false)
					{
						ud1 = true;
					}
				} else
				{
					ud1 = true;
				}

				ud2 = false;
				if (qmud2 == true)
				{
					if (textFieldUserData2.getText().equals("") == false)
					{
						ud2 = true;
					}
				} else
				{
					ud2 = true;
				}
				if (ud1 & ud2)
				{
					result = true;
				}
			}
		}
		btnPrint.setEnabled(result);
	}

	private void processOrderChanged(String processOrder)
	{
		if (po.isValidProcessOrder(processOrder))
		{
			po.getProcessOrderProperties(processOrder);
			textFieldProcessOrder.setText(po.getProcessOrder());
			textFieldDescription.setText(po.getDescription());
			textFieldInspectionID.setText(po.getInspectionID());
			textFieldMaterial.setText(po.getMaterial());
			textFieldStatus.setText(po.getStatus());
			textFieldResource.setText(po.getRequiredResource());
			dueDate.setDate(po.getDueDate());
			processOrderValid = true;
			populateActivityList(po.getInspectionID());
		} else
		{
			textFieldDescription.setText("");
			textFieldInspectionID.setText("");
			textFieldMaterial.setText("");
			textFieldStatus.setText("");
			textFieldResource.setText("");
			textFieldUserData1.setText("");
			textFieldUserData2.setText("");
			dueDate.setDate(JUtility.getSQLDate());
			populateActivityList("");
			processOrderValid = false;
		}
		lblStatusBar.setText("");
		printEnable();
	}

	private void populateActivityList(String inspectionid)
	{
		DefaultComboBoxModel defComboBoxMod = new DefaultComboBoxModel();

		LinkedList<JDBQMActivity> tempActivityList = activity.getActivities(inspectionid);

		int sel = -1;
		for (int j = 0; j < tempActivityList.size(); j++)
		{
			defComboBoxMod.addElement(tempActivityList.get(j));
		}

		ListModel jList1Model = defComboBoxMod;
		listActivities.setModel(jList1Model);

		listActivities.setCellRenderer(Common.renderer_list);
		listActivities.ensureIndexIsVisible(sel);
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

	public JInternalFrameQMSampleLabel(String processOrder)
	{
		super();

		processOrderChanged(processOrder);

	}

	/**
	 * Create the frame.
	 */
	public JInternalFrameQMSampleLabel()
	{
		addInternalFrameListener(new InternalFrameAdapter()
		{
			public void internalFrameClosing(InternalFrameEvent e)
			{
				timer.stop();

				while (timer.isRunning())
				{
				}

				timer = null;
			}
		});

		qmud1 = Boolean.valueOf(ctrl.getKeyValueWithDefault("QM_USER_DATA_1_REQD", "true", "QM USER DATA 1 REQD"));
		qmud2 = Boolean.valueOf(ctrl.getKeyValueWithDefault("QM_USER_DATA_2_REQD", "true", "QM USER DATA 2 REQD"));

		setVisible(true);
		this.setClosable(true);
		this.setIconifiable(true);
		setBounds(100, 100, 598, 638);
		getContentPane().setLayout(null);

		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.setBackground(Color.WHITE);
		desktopPane.setBounds(0, 0, 587, 611);
		getContentPane().add(desktopPane);
		desktopPane.setLayout(null);

		JLabel4j_std lblProcessOrder = new JLabel4j_std(lang.get("lbl_Process_Order"));
		lblProcessOrder.setHorizontalAlignment(SwingConstants.TRAILING);
		lblProcessOrder.setBounds(6, 12, 111, 16);
		desktopPane.add(lblProcessOrder);

		JLabel4j_std lblDescription = new JLabel4j_std(lang.get("lbl_Description"));
		lblDescription.setHorizontalAlignment(SwingConstants.TRAILING);
		lblDescription.setBounds(6, 43, 111, 16);
		desktopPane.add(lblDescription);

		textFieldProcessOrder = new JTextField4j();
		textFieldProcessOrder.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent evt)
			{
				processOrderChanged(textFieldProcessOrder.getText());
			}
		});
		textFieldProcessOrder.setBounds(125, 10, 138, 22);
		desktopPane.add(textFieldProcessOrder);
		textFieldProcessOrder.setColumns(10);

		textFieldDescription = new JTextField4j();
		textFieldDescription.setEditable(false);
		textFieldDescription.setBounds(125, 38, 420, 22);
		desktopPane.add(textFieldDescription);
		textFieldDescription.setColumns(10);

		textFieldInspectionID = new JTextField4j();
		textFieldInspectionID.setEditable(false);
		textFieldInspectionID.setBounds(125, 69, 134, 22);
		desktopPane.add(textFieldInspectionID);
		textFieldInspectionID.setColumns(10);

		JLabel4j_std lblInspectionID = new JLabel4j_std(lang.get("lbl_Inspection_ID"));
		lblInspectionID.setHorizontalAlignment(SwingConstants.TRAILING);
		lblInspectionID.setBounds(6, 74, 111, 16);
		desktopPane.add(lblInspectionID);

		JLabel4j_std lblNewLabel_3 = new JLabel4j_std(lang.get("lbl_Activity_ID"));
		lblNewLabel_3.setBounds(30, 199, 111, 16);
		desktopPane.add(lblNewLabel_3);

		btnPrint = new JButton4j(lang.get("btn_Print"));
		btnPrint.setEnabled(false);
		btnPrint.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Long sampleID = sample.generateSampleID();
				String activityID = ((JDBQMActivity) listActivities.getSelectedValue()).getActivityID();
				sample.create(sampleID, po.getInspectionID(), activityID, po.getProcessOrder(), po.getMaterial(), textFieldUserData1.getText(), textFieldUserData2.getText());
				String pq = comboBoxPrintQueue.getSelectedItem().toString();
				buildSQL1Record(sampleID);
				JLaunchReport.runReport("RPT_SAMPLE_LABEL", listStatement, false, pq, Integer.valueOf(spinnerCopies.getValue().toString()), false);
				lblStatusBar.setText(Integer.valueOf(spinnerCopies.getValue().toString()) + " labels printed. " + sample.getSampleDate().toString());
			}
		});
		
		btnPrint.setIcon(Common.icon_print);
		btnPrint.setBounds(167, 514, 117, 29);
		desktopPane.add(btnPrint);

		btnClose = new JButton4j(lang.get("btn_Close"));
		btnClose.setIcon(Common.icon_close);
		btnClose.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		btnClose.setBounds(296, 514, 117, 29);
		desktopPane.add(btnClose);

		comboBoxPrintQueue.setBounds(145, 468, 388, 27);
		desktopPane.add(comboBoxPrintQueue);

		JLabel4j_std lblNewLabel_4 = new JLabel4j_std(lang.get("lbl_Print_Queue"));
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel_4.setBounds(6, 472, 138, 16);
		desktopPane.add(lblNewLabel_4);

		JLabel4j_std lblNewLabel_5 = new JLabel4j_std(lang.get("lbl_Number_Of_Labels"));
		lblNewLabel_5.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel_5.setBounds(6, 442, 138, 16);
		desktopPane.add(lblNewLabel_5);

		spinnerCopies = new JSpinner();
		spinnerCopies.setBounds(149, 432, 37, 28);
		JSpinner.NumberEditor ne = new JSpinner.NumberEditor(spinnerCopies);
		ne.getTextField().setFont(Common.font_std);
		spinnerCopies.setEditor(ne);
		desktopPane.add(spinnerCopies);

		JButton btnProcessOrderLookup = new JButton();
		btnProcessOrderLookup.setIcon(Common.icon_lookup);
		btnProcessOrderLookup.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				JLaunchLookup.dlgCriteriaDefault = "Ready";
				JLaunchLookup.dlgAutoExec = true;
				if (JLaunchLookup.processOrders())
				{
					textFieldProcessOrder.setText(JLaunchLookup.dlgResult);
					processOrderChanged(JLaunchLookup.dlgResult);
				}
			}
		});
		btnProcessOrderLookup.setBounds(261, 10, 21, 22);
		desktopPane.add(btnProcessOrderLookup);

		populatePrinterList(JPrint.getDefaultPrinterQueueName());
		String numberOfLabels = ctrl.getKeyValueWithDefault("QM SAMPLE LABELS", "4", "Number of Labels per Sample");
		spinnerCopies.setValue(Integer.valueOf(numberOfLabels));

		JLabel4j_std lblMaterial = new JLabel4j_std(lang.get("lbl_Material"));
		lblMaterial.setHorizontalAlignment(SwingConstants.TRAILING);
		lblMaterial.setBounds(6, 105, 111, 16);
		desktopPane.add(lblMaterial);

		textFieldMaterial = new JTextField4j();
		textFieldMaterial.setEditable(false);
		textFieldMaterial.setColumns(10);
		textFieldMaterial.setBounds(125, 100, 134, 22);
		desktopPane.add(textFieldMaterial);

		JLabel4j_std lblStatus = new JLabel4j_std(lang.get("lbl_Process_Order_Status"));
		lblStatus.setHorizontalAlignment(SwingConstants.TRAILING);
		lblStatus.setBounds(6, 136, 111, 16);
		desktopPane.add(lblStatus);

		textFieldStatus = new JTextField4j();
		textFieldStatus.setEditable(false);
		textFieldStatus.setColumns(10);
		textFieldStatus.setBounds(125, 131, 134, 22);
		desktopPane.add(textFieldStatus);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(30, 215, 515, 206);
		desktopPane.add(scrollPane);

		listActivities = new JList4j();
		listActivities.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		listActivities.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent arg0)
			{
				printEnable();
			}
		});
		scrollPane.setViewportView(listActivities);

		JLabel4j_std lblResource = new JLabel4j_std(lang.get("lbl_Process_Order_Required_Resource"));
		lblResource.setHorizontalAlignment(SwingConstants.TRAILING);
		lblResource.setBounds(261, 74, 138, 16);
		desktopPane.add(lblResource);

		textFieldResource = new JTextField4j();
		textFieldResource.setEditable(false);
		textFieldResource.setColumns(10);
		textFieldResource.setBounds(411, 69, 134, 22);
		desktopPane.add(textFieldResource);

		dueDate = new JDateControl();
		dueDate.setEnabled(false);
		dueDate.setBounds(411, 103, 134, 28);
		dueDate.getEditor().setPreferredSize(new java.awt.Dimension(87, 19));
		dueDate.getEditor().setSize(87, 21);
		desktopPane.add(dueDate);

		JLabel4j_std lblDueDate = new JLabel4j_std(lang.get("lbl_Process_Order_Due_Date"));
		lblDueDate.setHorizontalAlignment(SwingConstants.TRAILING);
		lblDueDate.setBounds(261, 105, 138, 16);
		desktopPane.add(lblDueDate);

		lblClock = new JLabel("");
		lblClock.setHorizontalAlignment(SwingConstants.RIGHT);
		lblClock.setForeground(Color.RED);
		lblClock.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		lblClock.setBounds(411, 4, 124, 29);
		desktopPane.add(lblClock);

		lblUserData1 = new JLabel4j_std(lang.get("lbl_User_Data1"));
		lblUserData1.setHorizontalAlignment(SwingConstants.TRAILING);
		lblUserData1.setBounds(6, 167, 111, 16);
		desktopPane.add(lblUserData1);

		lblUserData2 = new JLabel4j_std(lang.get("lbl_User_Data2"));
		lblUserData2.setHorizontalAlignment(SwingConstants.TRAILING);
		lblUserData2.setBounds(288, 167, 111, 16);
		desktopPane.add(lblUserData2);

		DocumentFilter filter = new UppercaseDocumentFilter();
		textFieldUserData1 = new JTextField4j();
		textFieldUserData1.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				printEnable();
			}
		});

		((AbstractDocument) textFieldUserData1.getDocument()).setDocumentFilter(filter);
		textFieldUserData1.setColumns(20);
		textFieldUserData1.setBounds(125, 162, 134, 22);
		desktopPane.add(textFieldUserData1);

		textFieldUserData2 = new JTextField4j();
		textFieldUserData2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				printEnable();
			}
		});

		((AbstractDocument) textFieldUserData2.getDocument()).setDocumentFilter(filter);
		textFieldUserData2.setColumns(20);
		textFieldUserData2.setBounds(411, 162, 134, 22);
		desktopPane.add(textFieldUserData2);

		lblStatusBar = new JLabel4j_std();
		lblStatusBar.setForeground(Color.RED);
		lblStatusBar.setBackground(Color.GRAY);
		lblStatusBar.setBounds(0, 555, 575, 21);
		desktopPane.add(lblStatusBar);

		timer.start();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				textFieldProcessOrder.requestFocus();
				textFieldProcessOrder.setCaretPosition(textFieldProcessOrder.getText().length());

				populatePrinterList(JPrint.getDefaultPrinterQueueName());
			}
		});

	}
}
