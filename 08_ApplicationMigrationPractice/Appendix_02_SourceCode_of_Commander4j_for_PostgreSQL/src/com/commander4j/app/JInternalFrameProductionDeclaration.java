package com.commander4j.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.ListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.AbstractDocument;

import org.apache.log4j.Logger;

import com.commander4j.bar.JEANBarcode;
import com.commander4j.calendar.JCalendarButton;
import com.commander4j.db.JDBControl;
import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBMaterial;
import com.commander4j.db.JDBMaterialBatch;
import com.commander4j.db.JDBMaterialUom;
import com.commander4j.db.JDBModule;
import com.commander4j.db.JDBPallet;
import com.commander4j.db.JDBProcessOrder;
import com.commander4j.db.JDBQuery;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JComboBox4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JTextField4j;
import com.commander4j.sys.Common;
import com.commander4j.sys.JLaunchLookup;
import com.commander4j.sys.JLaunchReport;
import com.commander4j.util.JDateControl;
import com.commander4j.util.JFixedSizeFilter;
import com.commander4j.util.JHelp;
import com.commander4j.util.JPrint;
import com.commander4j.util.JQuantityInput;
import com.commander4j.util.JUtility;

public class JInternalFrameProductionDeclaration extends JInternalFrame
{
	private static final long serialVersionUID = 1;
	private JCheckBox jCheckBoxAutoConfirm;
	private JLabel4j_std jLabelPrintLabel_2;
	private JSpinner jSpinnerQuantity;
	private JSpinner jSpinnerCopies;
	private JLabel4j_std jLabelQuantity_1;
	private JCheckBox jCheckBoxAutoPreview;
	private JLabel4j_std jLabelPrintLabel_1;
	private JButton4j jButtonReprint;
	private JCheckBox jCheckBoxAutoPrint;
	private JLabel4j_std jLabelPrintLabel;
	private JCheckBox jCheckBoxAutoSSCC;
	private JButton4j jButtonHelp;
	private JDateControl jSpinnerDueDate;
	private JLabel4j_std jLabelDueDate;
	private JTextField4j jTextFieldRecipe;
	private JLabel4j_std jLabelRecipe;
	private JTextField4j jTextFieldShelfLifeRoundingRule;
	private JLabel4j_std jLabelRounding;
	private JTextField4j jTextFieldProcessOrderDescription;
	private JLabel4j_std jLabelOrderDescription;
	private JTextField4j jTextFieldMaterial;
	private JLabel4j_std jLabelMaterial;
	private JTextField4j jTextFieldProcessOrder;
	private JLabel4j_std jLabelProcessOrder;
	private JTextField4j jTextFieldMaterialDescription;
	private JLabel4j_std jLabelDescription;
	private JTextField4j jTextFieldSSCC;
	private JLabel4j_std jLabelQuantity;
	private JButton4j jButtonClose;
	private JTextField4j jTextFieldProductionUom;
	private JCheckBox jCheckBoxBatch;
	private JTextField4j jTextFieldBaseUOM;
	private JLabel4j_std jLabel3;
	private JLabel4j_std jLabel2;
	private JCheckBox jCheckBoxExpiry;
	private JButton4j jButtonPOLookup;
	private JLabel4j_std jLabelSSCC;
	private JQuantityInput jFormattedTextFieldProdQuantity;
	private JLabel4j_std jLabelProductionEAN;
	private JLabel4j_std jLabelProductionDate;
	private JDateControl jSpinnerProductionDate;
	private JPanel jPanelBatch;
	private JPanel jPanelPallet;
	private JTextField4j jTextFieldVariant;
	private JLabel4j_std jLabelVariant;
	private JTextField4j jTextFieldEAN;
	private JLabel4j_std jLabelEAN;
	private JTextField4j jTextFieldLegacyCode;
	private JLabel4j_std jLabelLegacyCode;
	private JTextField4j jTextFieldShelfLifeUOM;
	private JLabel4j_std jLabel1ShelfLifeUOM;
	private JCheckBox jCheckBoxProductionDate;
	private JQuantityInput jFormattedTextFieldBaseQuantity;
	private JLabel4j_std jStatusText;
	private JLabel4j_std jLabelShelfLife;
	private JTextField4j jTextFieldProcessOrderStatus;
	private JPanel jPanelMaterial;
	private JPanel jPanelProcessOrder;
	private JLabel4j_std jLabelOrderStatus;
	private JDateControl jSpinnerExpiryDate;
	private JLabel4j_std jLabelBatchExpiry;
	private JTextField4j jTextFieldBatch;
	private JLabel4j_std jLabelBatch;
	private JSpinner jSpinnerShelfLife;
	private JTextField4j jTextFieldLocation;
	private JLabel4j_std jLabelLocation;
	private JButton4j jButtonSave;
	private JDesktopPane jDesktopPane1;
	private SpinnerNumberModel shelflifenumbermodel = new SpinnerNumberModel();
	private SpinnerNumberModel quantitynumbermodel = new SpinnerNumberModel();
	private SpinnerNumberModel copiesnumbermodel;
	private JDBProcessOrder processorder = new JDBProcessOrder(Common.selectedHostID, Common.sessionID);
	private JDBMaterial material = new JDBMaterial(Common.selectedHostID, Common.sessionID);
	private JDBMaterialUom materialuom = new JDBMaterialUom(Common.selectedHostID, Common.sessionID);
	private JDBMaterialBatch materialbatch = new JDBMaterialBatch(Common.selectedHostID, Common.sessionID);
	private JShelfLifeUom shelflifeuom = new JShelfLifeUom();
	private JDBPallet pallet = new JDBPallet(Common.selectedHostID, Common.sessionID);
	private JShelfLifeRoundingRule shelfliferoundingrule = new JShelfLifeRoundingRule();
	final 	Logger logger = Logger.getLogger(JInternalFrameProductionDeclaration.class);
	private ClockListener clocklistener = new ClockListener();
	private JEANBarcode bc = new JEANBarcode(Common.selectedHostID, Common.sessionID);
	private Timer timer = new Timer(1000, clocklistener);
	private String ssccList = "";
	private LinkedList<String> ssccItems = new LinkedList<String>();
	private JDBModule mod = new JDBModule(Common.selectedHostID, Common.sessionID);
	private JComboBox4j comboBoxPrintQueue;
	private JLabel4j_std lblPrintQueueFor;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);
	private String batchFormat = "";
	private JDBControl ctrl = new JDBControl(Common.selectedHostID, Common.sessionID);
	private String expiryMode = "";
	private JLabel4j_std label_1;
	private JCheckBox checkBoxIncHeaderText;
	private JTextField4j textFieldBatchExtension;
	private JCalendarButton calendarButtonjSpinnerProductionDate;
	private JCalendarButton calendarButtonjSpinnerExpiryDate;
	private BigDecimal fullPalletDefaultQuantity;
	private PreparedStatement listStatement;
	private JLabel4j_std labelCopies = new JLabel4j_std();

	public JInternalFrameProductionDeclaration(String procOrder) {
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
		
		int copies = Integer.valueOf(ctrl.getKeyValueWithDefault("DEFAULT_LABELS_TO_PRINT", "2", "Default No of Labels to print"));
		copiesnumbermodel = new SpinnerNumberModel(copies, 1, 100, 1);
		initGUI();
		clearFields();

		timer.start();

		jTextFieldBatch.setText("");
		textFieldBatchExtension.setText("");

		ctrl.getProperties("BATCH FORMAT");
		batchFormat = ctrl.getKeyValue();
		expiryMode = ctrl.getKeyValue("EXPIRY DATE MODE");

		calcBBEBatch();

		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_PAL_PROD_DEC"));

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				jTextFieldProcessOrder.requestFocus();
				jTextFieldProcessOrder.setCaretPosition(jTextFieldProcessOrder.getText().length());
				mod.setModuleId("RPT_PALLET_LABEL");
				mod.getModuleProperties();

				if (mod.getReportType().equals("Label"))
				{
					jCheckBoxAutoPreview.setSelected(false);
					jCheckBoxAutoPreview.setEnabled(false);
					lblPrintQueueFor = new JLabel4j_std(lang.get("lbl_Print_Queue"));
					lblPrintQueueFor.setHorizontalAlignment(SwingConstants.TRAILING);
					lblPrintQueueFor.setBounds(7, 461, 102, 16);
					jDesktopPane1.add(lblPrintQueueFor);
				}
				else
				{ 
					jSpinnerCopies.setVisible(false);
					labelCopies.setVisible(false);
				}

				populatePrinterList(JPrint.getDefaultPrinterQueueName());
			}
		});

		procOrder = JUtility.replaceNullStringwithBlank(procOrder);

		if (procOrder.equals("") == false)
		{
			processOrderChanged(procOrder);
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
		}
		else
		{
			comboBoxPrintQueue.setEnabled(true);
		}
	}

	private void buildSQL(String mList, LinkedList<String> mItems)
	{
		JDBQuery.closeStatement(listStatement);
		
		String temp = "";

		JDBQuery query = new JDBQuery(Common.selectedHostID, Common.sessionID);
		query.clear();

		temp = Common.hostList.getHost(Common.selectedHostID).getSqlstatements().getSQL("JDBPallet.selectWithExpiry");

		query.addText(temp);

		query.addText(" where sscc in (" + mList + ")");

		for (int t = 0; t <= (mItems.size() - 1); t++)
		{
			query.addParameter(mItems.get(t));
		}

		query.appendSort("sscc", "asc");
		query.applyRestriction(false, "none", 0);
		query.bindParams();

		listStatement = query.getPreparedStatement();
	}

	private void processOrderChanged(String po)
	{
		clearFields();
		checkSSCC();
		jButtonReprint.setEnabled(false);
		jTextFieldProcessOrder.setText(po);
		processorder.setProcessOrder(po);

		if (processorder.getProcessOrderProperties() == true)
		{
			pallet.setProcessOrder(po);
			pallet.populateFromProcessOrder();

			jTextFieldProcessOrderDescription.setText(processorder.getDescription());
			jTextFieldMaterial.setText(processorder.getMaterial());

			jTextFieldProcessOrderStatus.setText(processorder.getStatus());
			jTextFieldRecipe.setText(processorder.getRecipe());
			jTextFieldLocation.setText(processorder.getLocation());

			try
			{
				jSpinnerDueDate.setDate(processorder.getDueDate());
			}
			catch (Exception e)
			{
			}

			material.getMaterialProperties(processorder.getMaterial());
			jTextFieldMaterialDescription.setText(material.getDescription());
			jSpinnerShelfLife.setValue((Number) material.getShelfLife());
			shelflifeuom.getShelfLifeUomProperties(material.getShelfLifeUom());
			jTextFieldShelfLifeUOM.setText(shelflifeuom.toString());
			shelfliferoundingrule.getShelfLifeRuleProperties(material.getShelfLifeRule());
			jTextFieldShelfLifeRoundingRule.setText(shelfliferoundingrule.toString());
			jFormattedTextFieldProdQuantity.setValue(JUtility.stringToBigDecimal(processorder.getProductionQuantity()));
			fullPalletDefaultQuantity = JUtility.stringToBigDecimal(processorder.getProductionQuantity());
			jTextFieldLegacyCode.setText(material.getOldMaterial());
			jTextFieldProductionUom.setText(processorder.getRequiredUom());
			jTextFieldBaseUOM.setText(material.getBaseUom());
			materialuom.getMaterialUomProperties(material.getMaterial(), processorder.getRequiredUom());
			jTextFieldEAN.setText(materialuom.getEan());
			jTextFieldVariant.setText(materialuom.getVariant());

			pallet.setBatchNumber(jTextFieldBatch.getText() + textFieldBatchExtension.getText());
			pallet.setQuantity(JUtility.stringToBigDecimal(jFormattedTextFieldProdQuantity.getText().toString()));

			jFormattedTextFieldBaseQuantity.setText(pallet.getBaseQuantityAsString());

			calcBBEBatch();
		}
	}

	private void enableField(JComponent field, Boolean allowed)
	{
		if (allowed == true)
		{
			field.setEnabled(true);
		}
		else
		{
			field.setEnabled(false);
		}
	}

	private void clearFields()
	{
		// previousDateString = "";
		jTextFieldProcessOrderDescription.setText("");
		jTextFieldProcessOrderStatus.setText("");
		jTextFieldRecipe.setText("");
		jTextFieldLocation.setText("");
		jTextFieldMaterial.setText("");
		jTextFieldMaterialDescription.setText("");
		jTextFieldShelfLifeUOM.setText("");
		jTextFieldShelfLifeRoundingRule.setText("");
		jTextFieldLegacyCode.setText("");
		jTextFieldBatch.setText("");
		textFieldBatchExtension.setText("");
		jTextFieldProductionUom.setText("");
		jTextFieldBaseUOM.setText("");
		jTextFieldEAN.setText("");
		jTextFieldVariant.setText("");
		jTextFieldSSCC.setText("");

		jCheckBoxProductionDate.setSelected(false);
		enableField(jSpinnerProductionDate, jCheckBoxProductionDate.isSelected());

		jCheckBoxExpiry.setSelected(false);
		enableField(jSpinnerExpiryDate, jCheckBoxExpiry.isSelected());

		jCheckBoxBatch.setSelected(false);
		enableField(jTextFieldBatch, jCheckBoxBatch.isSelected());

		jFormattedTextFieldProdQuantity.setValue(0);
		jFormattedTextFieldBaseQuantity.setValue(0);
	}

	private void checkSSCC()
	{
		boolean valid = false;
		jStatusText.setText("");
		jButtonReprint.setEnabled(false);

		if (jCheckBoxAutoSSCC.isSelected() == true)
		{
			jTextFieldSSCC.setEnabled(false);
			valid = true;
		}
		else
		{
			jTextFieldSSCC.setEnabled(true);

			if (jTextFieldSSCC.getText().length() == 18)
			{
				if (Common.barcode.validateCheckdigit(jTextFieldSSCC.getText()))
				{
					valid = true;
				}
				else
				{
					jStatusText.setText("Invalid SSCC check digit");
				}
			}
		}

		if (valid)
		{
			jTextFieldSSCC.setForeground(new java.awt.Color(51, 51, 51));
			jButtonSave.setEnabled(true);

			jFormattedTextFieldProdQuantity.setEnabled(true);

			jCheckBoxProductionDate.setEnabled(true);
			jCheckBoxExpiry.setEnabled(true);
			jCheckBoxBatch.setEnabled(true);
			enableField(jSpinnerProductionDate, jCheckBoxProductionDate.isSelected());
			enableField(jSpinnerExpiryDate, jCheckBoxExpiry.isSelected());
			enableField(jTextFieldBatch, jCheckBoxBatch.isSelected());
			textFieldBatchExtension.setEnabled(true);
		}
		else
		{
			jTextFieldSSCC.setForeground(new java.awt.Color(255, 0, 0));
			jButtonSave.setEnabled(false);

			jFormattedTextFieldProdQuantity.setEnabled(false);

			jSpinnerProductionDate.setEnabled(false);
			jCheckBoxProductionDate.setSelected(false);
			jCheckBoxProductionDate.setEnabled(false);

			jSpinnerExpiryDate.setEnabled(false);
			jCheckBoxExpiry.setSelected(false);
			jCheckBoxExpiry.setEnabled(false);

			jTextFieldBatch.setEnabled(false);
			textFieldBatchExtension.setEnabled(false);
			jCheckBoxBatch.setSelected(false);
			jCheckBoxBatch.setEnabled(false);
		}
	}

	private void calcBBEBatch()
	{
		try
		{
			String temp = "";
			Date d = jSpinnerProductionDate.getDate();
			Calendar caldate = Calendar.getInstance();
			caldate.setTime(d);

			JDBMaterialBatch mb = new JDBMaterialBatch(Common.selectedHostID, Common.sessionID);
			temp = mb.getDefaultBatchNumber(batchFormat, caldate, processorder);

			if (temp.equalsIgnoreCase(jTextFieldBatch.getText()) == false)
			{
				if (jCheckBoxBatch.isSelected() == false)
				{
					jTextFieldBatch.setText(mb.getDefaultBatchNumber(batchFormat, caldate, processorder));
				}
			}

			if (jCheckBoxExpiry.isSelected() == false)
			{
				jSpinnerExpiryDate.setEnabled(false);

				if (material.getMaterial().length() > 0)
				{
					if (expiryMode.equals("BATCH"))
					{
						if (materialbatch.getMaterialBatchProperties(material.getMaterial(), jTextFieldBatch.getText() + textFieldBatchExtension.getText()) == true)
						{
							try
							{
								jSpinnerExpiryDate.setDate(materialbatch.getExpiryDate());
							}
							catch (Exception e)
							{
							}
						}
						else
						{
							Date de = jSpinnerProductionDate.getDate();

							try
							{
								jSpinnerExpiryDate.setDate(material.calcBBE(de, material.getShelfLife(), material.getShelfLifeUom(), material.getShelfLifeRule()));
							}
							catch (Exception e)
							{
							}
						}
					}
					else
					{
						Date de = jSpinnerProductionDate.getDate();

						try
						{
							jSpinnerExpiryDate.setDate(material.calcBBE(de, material.getShelfLife(), material.getShelfLifeUom(), material.getShelfLifeRule()));
						}
						catch (Exception e)
						{
						}
					}
				}
			}

			jSpinnerExpiryDate.setDate(material.getRoundedExpiryDate(jSpinnerExpiryDate.getDate()));
		}
		catch (Exception e)
		{
			//logger.debug(e.getMessage());
		}
	}

	private void initGUI()
	{
		try
		{
			this.setPreferredSize(new java.awt.Dimension(674, 474));
			this.setBounds(0, 0, 785+Common.LFAdjustWidth, 603+Common.LFAdjustHeight);
			setVisible(true);
			this.setClosable(true);
			this.setIconifiable(true);
			jDesktopPane1 = new JDesktopPane();
			getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
			jDesktopPane1.setPreferredSize(new java.awt.Dimension(665, 490));
			jDesktopPane1.setBackground(new java.awt.Color(255, 255, 255));
			jDesktopPane1.setLayout(null);
			jButtonSave = new JButton4j(Common.icon_scanner);
			jDesktopPane1.add(jButtonSave);
			jButtonSave.setEnabled(false);
			jButtonSave.setText(lang.get("btn_Confirm"));
			jButtonSave.setMnemonic(lang.getMnemonicChar());
			jButtonSave.setBounds(116, 492, 126, 28);
			jButtonSave.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					int noOfSSCCs = 0;
					int ssccCount = 0;
					int printCount = 0;
					int confirmCount = 0;
					int createCount = 0;
					LinkedList<String> ssccItems = new LinkedList<String>();
					ssccList = "";
					ssccItems.clear();

					if (jCheckBoxAutoSSCC.isSelected() == false)
					{
						// If manual SSCC mode selected we can only print 1 label for the SSCC input
						jSpinnerQuantity.setValue(1);
					}

					noOfSSCCs = Integer.valueOf(jSpinnerQuantity.getValue().toString());
					
					if (jCheckBoxAutoSSCC.isSelected() == false)
					{
						ssccItems.clear();
						ssccItems.add(jTextFieldSSCC.getText());
					}
					else
					{
						ssccItems = bc.generateNewSSCCs(noOfSSCCs);
					}
					

					Boolean confirmQuantity = true;
					BigDecimal a = jFormattedTextFieldProdQuantity.getQuantity();

					if (fullPalletDefaultQuantity.compareTo(new BigDecimal("0")) > 0)
					{
						if (a.compareTo(fullPalletDefaultQuantity) > 0)
						{
							if (JOptionPane.showConfirmDialog(Common.mainForm, lang.get("dlg_Quantity_Confirm"),  lang.get("dlg_Confirm"),	JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
							{
								confirmQuantity = true;
							}
							else
							{
								confirmQuantity = false;
							}
						}
					}

					if (confirmQuantity == true)
					{

						for (int loop = 1; loop <= noOfSSCCs; loop++)
						{
							// Get or Generate the SSCC
							String tempSSCC = jTextFieldSSCC.getText();

							if (jCheckBoxAutoSSCC.isSelected() == true)
							{
								tempSSCC = ssccItems.get(loop - 1);
								jTextFieldSSCC.setText(tempSSCC);
								jTextFieldSSCC.repaint();
							}
							else
							{
								tempSSCC = jTextFieldSSCC.getText();
							}

							// Assign SSCC to Pallet
							pallet.clear();
							pallet.setSSCC(tempSSCC);

							// Validate that the SSCC is well formed.
							if (pallet.isValidPallet() == false)
							{
								// Assign critical fields.
								pallet.setProcessOrder(jTextFieldProcessOrder.getText());
								// Get data from the Process Order
								pallet.populateFromProcessOrder();

								pallet.setBatchNumber(jTextFieldBatch.getText() + textFieldBatchExtension.getText());
								pallet.setQuantity(jFormattedTextFieldProdQuantity.getQuantity());
								pallet.setBatchExpiry(JUtility.getTimestampFromDate(jSpinnerExpiryDate.getDate()));

								jFormattedTextFieldBaseQuantity.setText(pallet.getBaseQuantityAsString());

								pallet.setTransactionRef(0);

								if (pallet.create("PROD DEC", "CREATE") == false)
								{
									JUtility.errorBeep();
									JOptionPane.showMessageDialog(Common.mainForm, pallet.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);

									break;
								}
								else
								{
									createCount++;
									jStatusText.setText("SSCC " + tempSSCC + " created.");

									if (ssccList.isEmpty())
									{
										ssccList = ssccList + '?';
									}
									else
									{
										ssccList = ssccList + ",?";
									}

									ssccCount++;

									if (jCheckBoxAutoPrint.isSelected() == true)
									{
										pallet.writePalletHistory(pallet.getTransactionRef(), "PROD DEC", "PRINT");
										printCount++;
									}

									if (jCheckBoxAutoConfirm.isSelected() == true)
									{
										Date dom = jSpinnerProductionDate.getDate();
										pallet.setDateOfManufacture(JUtility.getTimestampFromDate(dom));

										if (pallet.confirm() == true)
										{
											jStatusText.setText("SSCC " + tempSSCC + " created and confirmed.");
											confirmCount++;
										}
										else
										{
											jStatusText.setText(pallet.getErrorMessage());
										}
									}
								}
							}
							else
							{
								// SSCC already exists
								JUtility.errorBeep();
								JOptionPane.showMessageDialog(Common.mainForm, "SSCC [" + pallet.getSSCC() + "] already exists", "Error", JOptionPane.ERROR_MESSAGE);
							}
						}

						if (ssccCount > 0)
						{
							jButtonReprint.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_PRODDEC_REPRINT"));

							if (jCheckBoxAutoPrint.isSelected())
							{
								String pq = comboBoxPrintQueue.getSelectedItem().toString();
								buildSQL(ssccList, ssccItems);
								JLaunchReport.runReport("RPT_PALLET_LABEL", listStatement, jCheckBoxAutoPreview.isSelected(), pq,
										Integer.valueOf(jSpinnerCopies.getValue().toString()), checkBoxIncHeaderText.isSelected());
							}
							JOptionPane.showMessageDialog(Common.mainForm, "SSCC's created : " + String.valueOf(createCount) + "\nSSCC's printed : " + String.valueOf(printCount)
									+ "\nSSCC's confirmed : " + String.valueOf(confirmCount), "Information", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			});
			jButtonClose = new JButton4j(Common.icon_close);
			jDesktopPane1.add(jButtonClose);
			jButtonClose.setText(lang.get("btn_Close"));
			jButtonClose.setMnemonic(lang.getMnemonicChar());
			jButtonClose.setBounds(513, 492, 126, 28);
			jButtonClose.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					timer.stop();

					while (timer.isRunning())
					{
					}

					timer = null;
					dispose();
				}
			});
			jButtonHelp = new JButton4j(Common.icon_help);
			jDesktopPane1.add(jButtonHelp);
			jButtonHelp.setText(lang.get("btn_Help"));
			jButtonHelp.setMnemonic(lang.getMnemonicChar());
			jButtonHelp.setBounds(380, 492, 126, 28);
			jPanelProcessOrder = new JPanel();
			jPanelProcessOrder.setFont(Common.font_title);
			jDesktopPane1.add(jPanelProcessOrder);
			jPanelProcessOrder.setBounds(7, 7, 748, 112);
			jPanelProcessOrder.setBorder(BorderFactory.createTitledBorder(lang.get("lbl_Process_Order")));
			jPanelProcessOrder.setLayout(null);
			jPanelProcessOrder.setBackground(new java.awt.Color(255, 255, 255));

			{
				jTextFieldProcessOrder = new JTextField4j();
				jPanelProcessOrder.add(jTextFieldProcessOrder);

				AbstractDocument doc = (AbstractDocument) jTextFieldProcessOrder.getDocument();
				doc.setDocumentFilter(new JFixedSizeFilter(JDBProcessOrder.field_process_order));
				jTextFieldProcessOrder.setBounds(161, 21, 119, 21);
				jTextFieldProcessOrder.addKeyListener(new KeyAdapter()
				{
					public void keyReleased(KeyEvent evt)
					{
						processOrderChanged(jTextFieldProcessOrder.getText());
					}
				});
			}

			jLabelProcessOrder = new JLabel4j_std();
			jPanelProcessOrder.add(jLabelProcessOrder);
			jLabelProcessOrder.setText(lang.get("lbl_Process_Order"));
			jLabelProcessOrder.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelProcessOrder.setBounds(12, 21, 142, 21);
			jTextFieldProcessOrderDescription = new JTextField4j();
			jPanelProcessOrder.add(jTextFieldProcessOrderDescription);
			jTextFieldProcessOrderDescription.setBounds(449, 21, 287, 21);
			jTextFieldProcessOrderDescription.setEditable(false);
			jTextFieldProcessOrderDescription.setEnabled(false);
			jLabelOrderDescription = new JLabel4j_std();
			jPanelProcessOrder.add(jLabelOrderDescription);
			jLabelOrderDescription.setText(lang.get("lbl_Description"));
			jLabelOrderDescription.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelOrderDescription.setBounds(306, 21, 136, 21);
			jLabelOrderStatus = new JLabel4j_std();
			jPanelProcessOrder.add(jLabelOrderStatus);
			jLabelOrderStatus.setText(lang.get("lbl_Process_Order_Status"));
			jLabelOrderStatus.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelOrderStatus.setBounds(290, 49, 152, 21);
			jSpinnerDueDate = new JDateControl();
			jPanelProcessOrder.add(jSpinnerDueDate);
			jSpinnerDueDate.setEnabled(false);
			jSpinnerDueDate.setBounds(161, 49, 137, 21);
			jSpinnerDueDate.getEditor().setOpaque(true);
			jSpinnerDueDate.setForeground(new java.awt.Color(238, 238, 238));
			jLabelDueDate = new JLabel4j_std();
			jPanelProcessOrder.add(jLabelDueDate);
			jLabelDueDate.setText(lang.get("lbl_Process_Order_Due_Date"));
			jLabelDueDate.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelDueDate.setBounds(12, 49, 142, 21);
			jTextFieldProcessOrderStatus = new JTextField4j();
			jPanelProcessOrder.add(jTextFieldProcessOrderStatus);
			jTextFieldProcessOrderStatus.setBounds(449, 49, 126, 21);
			jTextFieldProcessOrderStatus.setEditable(false);
			jTextFieldProcessOrderStatus.setEnabled(false);
			jTextFieldRecipe = new JTextField4j();
			jPanelProcessOrder.add(jTextFieldRecipe);
			jTextFieldRecipe.setBounds(161, 77, 139, 21);
			jTextFieldRecipe.setEditable(false);
			jTextFieldRecipe.setEnabled(false);
			jLabelRecipe = new JLabel4j_std();
			jPanelProcessOrder.add(jLabelRecipe);
			jLabelRecipe.setText(lang.get("lbl_Process_Order_Recipe"));
			jLabelRecipe.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelRecipe.setBounds(12, 77, 142, 21);
			jTextFieldLocation = new JTextField4j();
			jPanelProcessOrder.add(jTextFieldLocation);
			jTextFieldLocation.setBounds(449, 77, 126, 21);
			jTextFieldLocation.setEditable(false);
			jTextFieldLocation.setEnabled(false);
			jLabelLocation = new JLabel4j_std();
			jPanelProcessOrder.add(jLabelLocation);
			jLabelLocation.setText(lang.get("lbl_Location_ID"));
			jLabelLocation.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelLocation.setBounds(290, 77, 152, 21);
			jButtonPOLookup = new JButton4j(Common.icon_lookup);
			jPanelProcessOrder.add(jButtonPOLookup);
			jButtonPOLookup.setBounds(280, 21, 20, 20);
			jButtonPOLookup.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					JLaunchLookup.dlgCriteriaDefault = "Ready";
					JLaunchLookup.dlgAutoExec = true;

					if (JLaunchLookup.processOrders())
					{
						String po = JLaunchLookup.dlgResult;
						processOrderChanged(po);
					}
				}
			});
			jPanelMaterial = new JPanel();
			jDesktopPane1.add(jPanelMaterial);
			jPanelMaterial.setBounds(7, 119, 748, 112);
			jPanelMaterial.setBorder(BorderFactory.createTitledBorder(lang.get("lbl_Material")));
			jPanelMaterial.setLayout(null);
			jPanelMaterial.setFont(Common.font_title);
			jPanelMaterial.setBackground(new java.awt.Color(255, 255, 255));
			jTextFieldMaterial = new JTextField4j();
			jPanelMaterial.add(jTextFieldMaterial);
			jTextFieldMaterial.setBounds(161, 21, 128, 21);
			jTextFieldMaterial.setEditable(false);
			jTextFieldMaterial.setEnabled(false);

			jLabelMaterial = new JLabel4j_std();
			jPanelMaterial.add(jLabelMaterial);
			jLabelMaterial.setText(lang.get("lbl_Material"));
			jLabelMaterial.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelMaterial.setBounds(12, 21, 142, 21);
			jTextFieldMaterialDescription = new JTextField4j();
			jPanelMaterial.add(jTextFieldMaterialDescription);
			jTextFieldMaterialDescription.setBounds(449, 21, 287, 21);
			jTextFieldMaterialDescription.setEditable(false);
			jTextFieldMaterialDescription.setEnabled(false);
			jLabelDescription = new JLabel4j_std();
			jPanelMaterial.add(jLabelDescription);
			jLabelDescription.setText(lang.get("lbl_Description"));
			jLabelDescription.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelDescription.setBounds(291, 21, 151, 21);
			jSpinnerShelfLife = new JSpinner();
			jPanelMaterial.add(jSpinnerShelfLife);
			jSpinnerShelfLife.setModel(shelflifenumbermodel);			
			JSpinner.NumberEditor nec = new JSpinner.NumberEditor(jSpinnerShelfLife);
			nec.getTextField().setFont(Common.font_std); 
			jSpinnerShelfLife.setEditor(nec);
			jSpinnerShelfLife.setBounds(161, 49, 63, 21);
			jSpinnerShelfLife.setEnabled(false);
			jTextFieldShelfLifeRoundingRule = new JTextField4j();
			jPanelMaterial.add(jTextFieldShelfLifeRoundingRule);
			jTextFieldShelfLifeRoundingRule.setBounds(638, 49, 98, 21);
			jTextFieldShelfLifeRoundingRule.setEditable(false);
			jTextFieldShelfLifeRoundingRule.setEnabled(false);
			jLabelRounding = new JLabel4j_std();
			jPanelMaterial.add(jLabelRounding);
			jLabelRounding.setText(lang.get("lbl_Material_Shelf_Life_Rounding_Rule"));
			jLabelRounding.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelRounding.setBounds(546, 49, 85, 21);
			jTextFieldShelfLifeUOM = new JTextField4j();
			jPanelMaterial.add(jTextFieldShelfLifeUOM);
			jTextFieldShelfLifeUOM.setBounds(449, 49, 91, 21);
			jTextFieldShelfLifeUOM.setEditable(false);
			jTextFieldShelfLifeUOM.setEnabled(false);
			jLabel1ShelfLifeUOM = new JLabel4j_std();
			jPanelMaterial.add(jLabel1ShelfLifeUOM);
			jLabel1ShelfLifeUOM.setText(lang.get("lbl_Material_Shelf_Life_UOM"));
			jLabel1ShelfLifeUOM.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabel1ShelfLifeUOM.setBounds(281, 49, 161, 21);
			jLabelShelfLife = new JLabel4j_std();
			jPanelMaterial.add(jLabelShelfLife);
			jLabelShelfLife.setText(lang.get("lbl_Material_Shelf_Life"));
			jLabelShelfLife.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelShelfLife.setBounds(12, 49, 142, 21);
			jTextFieldLegacyCode = new JTextField4j();
			jPanelMaterial.add(jTextFieldLegacyCode);
			jTextFieldLegacyCode.setBounds(161, 77, 128, 21);
			jTextFieldLegacyCode.setEditable(false);
			jTextFieldLegacyCode.setEnabled(false);
			jLabelLegacyCode = new JLabel4j_std();
			jPanelMaterial.add(jLabelLegacyCode);
			jLabelLegacyCode.setText(lang.get("lbl_Material_Legacy_Code"));
			jLabelLegacyCode.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelLegacyCode.setBounds(12, 77, 142, 21);
			jLabelEAN = new JLabel4j_std();
			jPanelMaterial.add(jLabelEAN);
			jLabelEAN.setText(lang.get("lbl_Material_UOM_EAN"));
			jLabelEAN.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelEAN.setHorizontalTextPosition(SwingConstants.RIGHT);
			jLabelEAN.setBounds(291, 77, 151, 21);
			jTextFieldEAN = new JTextField4j();
			jPanelMaterial.add(jTextFieldEAN);
			jTextFieldEAN.setBounds(449, 77, 126, 21);
			jTextFieldEAN.setFocusCycleRoot(true);
			jTextFieldEAN.setEditable(false);
			jTextFieldEAN.setEnabled(false);
			jLabelVariant = new JLabel4j_std();
			jPanelMaterial.add(jLabelVariant);
			jLabelVariant.setText(lang.get("lbl_Material_UOM_Variant"));
			jLabelVariant.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelVariant.setHorizontalTextPosition(SwingConstants.RIGHT);
			jLabelVariant.setBounds(579, 77, 110, 21);
			jTextFieldVariant = new JTextField4j();
			jPanelMaterial.add(jTextFieldVariant);
			jTextFieldVariant.setBounds(701, 77, 35, 21);
			jTextFieldVariant.setFocusCycleRoot(true);
			jTextFieldVariant.setEditable(false);
			jTextFieldVariant.setEnabled(false);
			jPanelPallet = new JPanel();
			jDesktopPane1.add(jPanelPallet);
			jPanelPallet.setBounds(7, 231, 748, 84);
			jPanelPallet.setBorder(BorderFactory.createTitledBorder(null, lang.get("lbl_Pallet"), TitledBorder.LEADING, TitledBorder.TOP));
			jPanelPallet.setLayout(null);
			jPanelPallet.setFont(Common.font_std);
			jPanelPallet.setBackground(new java.awt.Color(255, 255, 255));
			jLabelQuantity = new JLabel4j_std();
			jPanelPallet.add(jLabelQuantity);
			jLabelQuantity.setText(lang.get("lbl_Pallet_Quantity"));
			jLabelQuantity.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelQuantity.setBounds(323, 21, 119, 21);
			comboBoxPrintQueue = new JComboBox4j();
			comboBoxPrintQueue.setBounds(116, 455, 621, 23);
			jDesktopPane1.add(comboBoxPrintQueue);

			{
				jTextFieldSSCC = new JTextField4j();
				jPanelPallet.add(jTextFieldSSCC);
				AbstractDocument doc = (AbstractDocument) jTextFieldSSCC.getDocument();
				doc.setDocumentFilter(new JFixedSizeFilter(JDBPallet.field_sscc));
				jTextFieldSSCC.setBounds(161, 18, 128, 21);
				jTextFieldSSCC.setEnabled(false);
				jTextFieldSSCC.addKeyListener(new KeyAdapter()
				{
					public void keyReleased(KeyEvent evt)
					{
						checkSSCC();
					}
				});
			}

			jSpinnerProductionDate = new JDateControl();
			jPanelPallet.add(jSpinnerProductionDate);
			jSpinnerProductionDate.setFont(new java.awt.Font("Dialog", 0, 12));
			jSpinnerProductionDate.setBounds(161, 45, 136, 21);
			jSpinnerProductionDate.getEditor().addKeyListener(new KeyAdapter()
			{
				public void keyPressed(KeyEvent e)
				{
					calcBBEBatch();
				}
			});
			jSpinnerProductionDate.addChangeListener(new ChangeListener()
			{
				public void stateChanged(final ChangeEvent e)
				{
					calcBBEBatch();
				}
			});

			try
			{
				jSpinnerProductionDate.setDate(JUtility.getSQLDate());
				jSpinnerProductionDate.setEnabled(false);
			}
			catch (Exception e)
			{
			}

			jLabelProductionDate = new JLabel4j_std();
			jPanelPallet.add(jLabelProductionDate);
			jLabelProductionDate.setText(lang.get("lbl_Pallet_DOM"));
			jLabelProductionDate.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelProductionDate.setBounds(12, 46, 122, 21);
			jFormattedTextFieldProdQuantity = new JQuantityInput(new BigDecimal("0"));
			jPanelPallet.add(jFormattedTextFieldProdQuantity);
			jFormattedTextFieldProdQuantity.setFont(Common.font_std);
			jFormattedTextFieldProdQuantity.setBounds(449, 21, 91, 21);
			jFormattedTextFieldProdQuantity.setValue(0);
			jFormattedTextFieldProdQuantity.setEnabled(false);
			jFormattedTextFieldProdQuantity.setHorizontalAlignment(SwingConstants.TRAILING);
			jFormattedTextFieldProdQuantity.addKeyListener(new KeyAdapter()
			{
				public void keyReleased(KeyEvent evt)
				{
					pallet.setUom(jTextFieldProductionUom.getText());
					pallet.setQuantity(JUtility.stringToBigDecimal(jFormattedTextFieldProdQuantity.getText().toString()));
					jFormattedTextFieldBaseQuantity.setText(pallet.getBaseQuantityAsString());
				}
			});
			jLabelSSCC = new JLabel4j_std();
			jPanelPallet.add(jLabelSSCC);
			jLabelSSCC.setText(lang.get("lbl_Pallet_SSCC"));
			jLabelSSCC.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelSSCC.setBounds(12, 18, 122, 21);
			jTextFieldProductionUom = new JTextField4j();
			jPanelPallet.add(jTextFieldProductionUom);
			jTextFieldProductionUom.setBounds(666, 21, 70, 21);
			jTextFieldProductionUom.setEditable(false);
			jTextFieldProductionUom.setEnabled(false);
			jLabelProductionEAN = new JLabel4j_std();
			jPanelPallet.add(jLabelProductionEAN);
			jLabelProductionEAN.setText(lang.get("lbl_Pallet_UOM"));
			jLabelProductionEAN.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelProductionEAN.setBounds(546, 21, 113, 21);
			jLabel2 = new JLabel4j_std();
			jPanelPallet.add(jLabel2);
			jLabel2.setText(lang.get("lbl_Pallet_Base_Quantity"));
			jLabel2.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabel2.setBounds(323, 49, 119, 21);
			jLabel3 = new JLabel4j_std();
			jPanelPallet.add(jLabel3);
			jLabel3.setText(lang.get("lbl_Pallet_Base_UOM"));
			jLabel3.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabel3.setBounds(546, 49, 113, 21);
			jTextFieldBaseUOM = new JTextField4j();
			jPanelPallet.add(jTextFieldBaseUOM);
			jTextFieldBaseUOM.setEditable(false);
			jTextFieldBaseUOM.setEnabled(false);
			jTextFieldBaseUOM.setBounds(666, 49, 70, 21);
			jFormattedTextFieldBaseQuantity = new JQuantityInput(new BigDecimal("0"));
			jPanelPallet.add(jFormattedTextFieldBaseQuantity);
			jFormattedTextFieldBaseQuantity.setFont(Common.font_std);
			jFormattedTextFieldBaseQuantity.setHorizontalAlignment(SwingConstants.TRAILING);
			jFormattedTextFieldBaseQuantity.setText("0.000");
			jFormattedTextFieldBaseQuantity.setEnabled(false);
			jFormattedTextFieldBaseQuantity.setBounds(449, 49, 91, 21);
			jCheckBoxProductionDate = new JCheckBox();
			jPanelPallet.add(jCheckBoxProductionDate);
			jCheckBoxProductionDate.setBackground(new java.awt.Color(255, 255, 255));
			jCheckBoxProductionDate.setBounds(139, 45, 21, 21);
			jCheckBoxProductionDate.setEnabled(false);
			jCheckBoxProductionDate.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					enableField(jSpinnerProductionDate, jCheckBoxProductionDate.isSelected());
					enableField(calendarButtonjSpinnerProductionDate, jCheckBoxProductionDate.isSelected());
				}
			});
			jCheckBoxAutoSSCC = new JCheckBox();
			jCheckBoxAutoSSCC.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					checkSSCC();
				}
			});
			jCheckBoxAutoSSCC.setSelected(true);
			jCheckBoxAutoSSCC.setToolTipText("Auto SSCC");
			jCheckBoxAutoSSCC.setBackground(new Color(255, 255, 255));
			jCheckBoxAutoSSCC.setBounds(139, 18, 21, 21);
			jPanelPallet.add(jCheckBoxAutoSSCC);

			calendarButtonjSpinnerProductionDate = new JCalendarButton(jSpinnerProductionDate);
			calendarButtonjSpinnerProductionDate.setEnabled(false);
			calendarButtonjSpinnerProductionDate.setBounds(290, 47, 21, 21);
			jPanelPallet.add(calendarButtonjSpinnerProductionDate);
			jPanelBatch = new JPanel();
			jDesktopPane1.add(jPanelBatch);
			jPanelBatch.setBounds(7, 315, 748, 56);
			jPanelBatch.setLayout(null);
			jPanelBatch.setFont(Common.font_std);
			jPanelBatch.setBorder(BorderFactory.createTitledBorder(lang.get("lbl_Material_Batch")));
			jPanelBatch.setBackground(new java.awt.Color(255, 255, 255));
			jLabelBatch = new JLabel4j_std();
			jPanelBatch.add(jLabelBatch);
			jLabelBatch.setText(lang.get("lbl_Material_Batch"));
			jLabelBatch.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelBatch.setBounds(320, 21, 96, 21);

			{
				jTextFieldBatch = new JTextField4j();
				jPanelBatch.add(jTextFieldBatch);
				AbstractDocument doc = (AbstractDocument) jTextFieldBatch.getDocument();
				doc.setDocumentFilter(new JFixedSizeFilter(JDBMaterialBatch.field_batch_number));
				jTextFieldBatch.setBounds(449, 21, 108, 21);
				jTextFieldBatch.setEnabled(false);
			}

			jSpinnerExpiryDate = new JDateControl();
			jSpinnerExpiryDate.addFocusListener(new FocusAdapter() {
				@Override     
				public void focusLost(FocusEvent arg0) {
					jSpinnerExpiryDate.setDate(material.getRoundedExpiryDate(jSpinnerExpiryDate.getDate()));
				}
			});
			jPanelBatch.add(jSpinnerExpiryDate);
			jSpinnerExpiryDate.setFont(Common.font_std);
			jSpinnerExpiryDate.setBounds(161, 17, 135, 25);
			jSpinnerExpiryDate.getEditor().setPreferredSize(new java.awt.Dimension(87, 19));
			jSpinnerExpiryDate.getEditor().setSize(87, 21);
			jSpinnerExpiryDate.setEnabled(false);
			jLabelBatchExpiry = new JLabel4j_std();
			jPanelBatch.add(jLabelBatchExpiry);
			jLabelBatchExpiry.setText(lang.get("lbl_Material_Batch_Expiry_Date"));
			jLabelBatchExpiry.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelBatchExpiry.setBounds(12, 17, 122, 21);
			jCheckBoxExpiry = new JCheckBox();
			jPanelBatch.add(jCheckBoxExpiry);
			jCheckBoxExpiry.setBounds(139, 17, 21, 21);
			jCheckBoxExpiry.setBackground(new java.awt.Color(255, 255, 255));
			jCheckBoxExpiry.setEnabled(false);
			jCheckBoxExpiry.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					enableField(jSpinnerExpiryDate, jCheckBoxExpiry.isSelected());
					enableField(calendarButtonjSpinnerExpiryDate, jCheckBoxExpiry.isSelected());
					calcBBEBatch();
				}
			});
			jCheckBoxBatch = new JCheckBox();
			jPanelBatch.add(jCheckBoxBatch);
			jCheckBoxBatch.setEnabled(false);
			jCheckBoxBatch.setBackground(new java.awt.Color(255, 255, 255));
			jCheckBoxBatch.setBounds(422, 21, 21, 21);
			jCheckBoxBatch.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					enableField(jTextFieldBatch, jCheckBoxBatch.isSelected());
					calcBBEBatch();
				}
			});
			jStatusText = new JLabel4j_std();
			jDesktopPane1.add(jStatusText);
			jStatusText.setForeground(new java.awt.Color(255, 0, 0));
			jStatusText.setBounds(0, 532, 761, 21);
			jStatusText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			jButtonReprint = new JButton4j(Common.icon_report);
			jButtonReprint.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					buildSQL(ssccList, ssccItems);
					JLaunchReport.runReport("RPT_PALLET_LABEL", null, "", listStatement, "");
				}
			});
			jButtonReprint.setMnemonic(KeyEvent.VK_C);
			jButtonReprint.setText(lang.get("btn_Re_Print"));
			jButtonReprint.setBounds(248, 492, 126, 28);
			jButtonReprint.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_PRODDEC_REPRINT"));
			jDesktopPane1.add(jButtonReprint);

			{
				final JPanel panel = new JPanel();
				panel.setBorder(new TitledBorder(null, lang.get("lbl_Options"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
				panel.setBackground(Color.WHITE);
				panel.setFont(Common.font_title);
				panel.setLayout(null);
				panel.setBounds(7, 374, 748, 73);
				jDesktopPane1.add(panel);
				jLabelPrintLabel = new JLabel4j_std();
				jLabelPrintLabel.setBounds(0, 15, 129, 21);
				panel.add(jLabelPrintLabel);
				jLabelPrintLabel.setHorizontalAlignment(SwingConstants.TRAILING);
				jLabelPrintLabel.setText(lang.get("lbl_Print"));
				jCheckBoxAutoPrint = new JCheckBox();
				jCheckBoxAutoPrint.setBounds(139, 15, 21, 21);
				panel.add(jCheckBoxAutoPrint);
				jCheckBoxAutoPrint.setToolTipText("Auto SSCC");
				jCheckBoxAutoPrint.setSelected(true);
				jCheckBoxAutoPrint.setBackground(new Color(255, 255, 255));
				jCheckBoxAutoPrint.setText("New JCheckBox");
				jCheckBoxAutoPrint.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_PRODDEC_PRINT"));
				jLabelPrintLabel_1 = new JLabel4j_std();
				jLabelPrintLabel_1.setBounds(282, 15, 138, 21);
				panel.add(jLabelPrintLabel_1);
				jLabelPrintLabel_1.setHorizontalTextPosition(SwingConstants.CENTER);
				jLabelPrintLabel_1.setHorizontalAlignment(SwingConstants.TRAILING);
				jLabelPrintLabel_1.setText(lang.get("lbl_Preview"));
				jCheckBoxAutoPreview = new JCheckBox();
				jCheckBoxAutoPreview.setBounds(424, 15, 21, 21);
				panel.add(jCheckBoxAutoPreview);
				jCheckBoxAutoPreview.setToolTipText("Auto SSCC");
				jCheckBoxAutoPreview.setSelected(true);
				jCheckBoxAutoPreview.setBackground(new Color(255, 255, 255));
				jCheckBoxAutoPreview.setText("New JCheckBox");
				jCheckBoxAutoPreview.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_PRODDEC_PREVIEW"));
				jLabelPrintLabel_2 = new JLabel4j_std();
				jLabelPrintLabel_2.setHorizontalAlignment(SwingConstants.TRAILING);
				jLabelPrintLabel_2.setText(lang.get("lbl_Label_Header_Text"));
				jLabelPrintLabel_2.setBounds(10, 44, 119, 21);
				panel.add(jLabelPrintLabel_2);
				jCheckBoxAutoConfirm = new JCheckBox();
				jCheckBoxAutoConfirm.setBackground(Color.WHITE);
				jCheckBoxAutoConfirm.setText("New JCheckBox");
				jCheckBoxAutoConfirm.setBounds(424, 44, 21, 21);
				panel.add(jCheckBoxAutoConfirm);
				jCheckBoxAutoConfirm.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_PRODDEC_CONFIRM"));
				jLabelQuantity_1 = new JLabel4j_std();
				jLabelQuantity_1.setBounds(474, 15, 201, 21);
				panel.add(jLabelQuantity_1);
				jLabelQuantity_1.setHorizontalAlignment(SwingConstants.RIGHT);
				jLabelQuantity_1.setText(lang.get("lbl_Number_of_SSCCs"));

				
				jSpinnerQuantity = new JSpinner();
				jSpinnerQuantity.addChangeListener(new ChangeListener()
				{
					public void stateChanged(final ChangeEvent e)
					{
						int t = Integer.valueOf(jSpinnerQuantity.getValue().toString());

						if (t <= 0)
						{
							jSpinnerQuantity.setValue(1);
						}

						if (t > 50)
						{
							jSpinnerQuantity.setValue(50);
						}
					}
				});
				jSpinnerQuantity.setBounds(687, 15, 49, 21);
				jSpinnerQuantity.setInputVerifier(null);
				jSpinnerQuantity.setModel(quantitynumbermodel);
				jSpinnerQuantity.setValue(1);
				JSpinner.NumberEditor ne = new JSpinner.NumberEditor(jSpinnerQuantity);
				ne.getTextField().setFont(Common.font_std); 
				jSpinnerQuantity.setEditor(ne);
				panel.add(jSpinnerQuantity);

				jSpinnerCopies = new JSpinner();
				jSpinnerCopies.setBounds(687, 44, 49, 21);
				jSpinnerCopies.setInputVerifier(null);
				jSpinnerCopies.setModel(copiesnumbermodel);
				JSpinner.NumberEditor nec2 = new JSpinner.NumberEditor(jSpinnerCopies);
				nec2.getTextField().setFont(Common.font_std); 
				jSpinnerCopies.setEditor(nec2);
				panel.add(jSpinnerCopies);

					labelCopies.setText((String) null);
					labelCopies.setHorizontalAlignment(SwingConstants.RIGHT);
					labelCopies.setBounds(474, 44, 201, 21);
					labelCopies.setText(lang.get("lbl_Labels_Per_SSCC"));
					panel.add(labelCopies);


				label_1 = new JLabel4j_std();
				label_1.setHorizontalAlignment(SwingConstants.TRAILING);
				label_1.setText(lang.get("lbl_Confirmed"));
				label_1.setBounds(292, 44, 129, 21);
				panel.add(label_1);

				checkBoxIncHeaderText = new JCheckBox();
				checkBoxIncHeaderText.setSelected(true);
				checkBoxIncHeaderText.setText("New JCheckBox");
				checkBoxIncHeaderText.setBackground(Color.WHITE);
				checkBoxIncHeaderText.setBounds(139, 44, 21, 21);
				panel.add(checkBoxIncHeaderText);

				textFieldBatchExtension = new JTextField4j();
				textFieldBatchExtension.setText("");
				textFieldBatchExtension.setBounds(561, 21, 39, 21);
				jPanelBatch.add(textFieldBatchExtension);

				calendarButtonjSpinnerExpiryDate = new JCalendarButton(jSpinnerExpiryDate);
				calendarButtonjSpinnerExpiryDate.setEnabled(false);
				calendarButtonjSpinnerExpiryDate.setBounds(290, 18, 21, 21);
				jPanelBatch.add(calendarButtonjSpinnerExpiryDate);

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public class ClockListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			Calendar rightNow = Calendar.getInstance();
			Date d = rightNow.getTime();

			try
			{
				if (jCheckBoxProductionDate.isSelected() == false)
				{
					jSpinnerProductionDate.setDate(d);
				}
				else
				{
					calcBBEBatch();
				}

			}
			catch (Exception e)
			{
			}
		}
	}
}
