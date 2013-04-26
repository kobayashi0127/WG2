package com.commander4j.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;

import com.commander4j.db.JDBControl;
import com.commander4j.db.JDBCustomer;
import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBLocation;
import com.commander4j.db.JDBMaterial;
import com.commander4j.db.JDBMaterialUom;
import com.commander4j.db.JDBProcessOrder;
import com.commander4j.db.JDBUom;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JComboBox4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JTextField4j;
import com.commander4j.sys.Common;
import com.commander4j.sys.JLaunchLookup;
import com.commander4j.util.JDateControl;
import com.commander4j.util.JFixedSizeFilter;
import com.commander4j.util.JHelp;
import com.commander4j.util.JQuantityInput;
import com.commander4j.util.JUtility;

public class JInternalFrameProcessOrderProperties extends JInternalFrame
{
	private JTextField4j jTextFieldMaterialDescription;
	private JLabel4j_std jLabel3_1;
	private JLabel4j_std jLabelQuantity;
	private JQuantityInput jFormattedTextFieldRequiredQuantity;
	private JLabel4j_std jLabel6;
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JLabel4j_std jLabel4;
	private JButton4j jButton3;
	private JButton4j jButton1;
	private JButton4j jButtonClose;
	private JButton4j jButtonHelp;
	private JButton4j jButtonSave;
	private JComboBox4j jComboBoxStatus;
	private JComboBox4j jComboBoxPalletStatus = new JComboBox4j();
	private JLabel4j_std jLabel14;
	private JTextField4j jTextFieldLocation;
	private JTextField4j jTextFieldCustomer;
	private JLabel4j_std jLabel2;
	private JDateControl jSpinnerDueDate;
	private JLabel4j_std jLabel7;
	private JTextField4j jTextFieldRecipeID;
	private JTextField4j jTextFieldRequiredResource;
	private JLabel4j_std jLabel12;
	private JTextField4j jTextFieldDescription;
	private JLabel4j_std jLabel3;
	private JTextField4j jTextFieldMaterial;
	private JLabel4j_std jLabel1;
	private JTextField4j jTextFieldProcessOrder;
	private JLabel4j_std lblInspectionID;
	private JTextField4j jTextFieldInspectionID;
	private String lprocessorder;
	private JDBProcessOrder processorder = new JDBProcessOrder(Common.selectedHostID, Common.sessionID);
	private JDBMaterial material = new JDBMaterial(Common.selectedHostID, Common.sessionID);
	private Vector<JDBUom> uomList = new Vector<JDBUom>();
	private JDBMaterialUom materialuom = new JDBMaterialUom(Common.selectedHostID, Common.sessionID);
	private JDBUom paluom = new JDBUom(Common.selectedHostID, Common.sessionID);
	private JComboBox4j jComboBoxRequiredUOM = new JComboBox4j();
	private JDBControl ctrl = new JDBControl(Common.selectedHostID, Common.sessionID);
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);

	/**
	 * Auto-generated main method to display this JInternalFrame inside a new
	 * JFrame.
	 */

	public JInternalFrameProcessOrderProperties()
	{
		super();
		initGUI();

		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_ADMIN_PROCESS_ORDER_EDIT"));
		{
			ComboBoxModel jComboBoxBatchStatusModel = new DefaultComboBoxModel(Common.palletStatusIncBlank);
			jComboBoxPalletStatus.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jButtonSave.setEnabled(true);
				}
			});

			jComboBoxPalletStatus.setModel(jComboBoxBatchStatusModel);
			jComboBoxPalletStatus.setBounds(163, 320, 150, 21);
			jDesktopPane1.add(jComboBoxPalletStatus);
		}
		{
			JLabel4j_std lblBatchStatus = new JLabel4j_std();
			lblBatchStatus.setText(lang.get("lbl_Process_Order_Default_Pallet_Status"));
			lblBatchStatus.setHorizontalAlignment(SwingConstants.TRAILING);
			lblBatchStatus.setBounds(12, 320, 144, 21);
			jDesktopPane1.add(lblBatchStatus);
		}

		{
			JLabel4j_std lblReqdResource = new JLabel4j_std();
			lblReqdResource.setText(lang.get("lbl_Process_Order_Required_Resource"));
			lblReqdResource.setHorizontalAlignment(SwingConstants.TRAILING);
			lblReqdResource.setBounds(12, 353, 144, 21);
			jDesktopPane1.add(lblReqdResource);
		}

		{
			JLabel4j_std lblCustomerID = new JLabel4j_std();
			lblCustomerID.setText(lang.get("lbl_Customer_ID"));
			lblCustomerID.setHorizontalAlignment(SwingConstants.TRAILING);
			lblCustomerID.setBounds(12, 386, 144, 21);
			jDesktopPane1.add(lblCustomerID);
		}		
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jTextFieldDescription.requestFocus();
				jTextFieldDescription.setCaretPosition(jTextFieldDescription.getText().length());
				

			}
		});
	}

	public JInternalFrameProcessOrderProperties(String po)
	{
		this();
		lprocessorder = po;
		jTextFieldProcessOrder.setText(lprocessorder);
		this.setTitle("Process Order Properties");
		processorder.setProcessOrder(lprocessorder);
		if (processorder.getProcessOrderProperties() == true)
		{
			jTextFieldMaterial.setText(processorder.getMaterial());
			jTextFieldDescription.setText(processorder.getDescription());
			jTextFieldRecipeID.setText(processorder.getRecipe());
			jTextFieldLocation.setText(processorder.getLocation());
			jTextFieldCustomer.setText(processorder.getCustomerID());
			jTextFieldInspectionID.setText(processorder.getInspectionID());
			jTextFieldRequiredResource.setText(processorder.getRequiredResource());
			jFormattedTextFieldRequiredQuantity.setValue(processorder.getRequiredQuantity());
			try
			{
				jSpinnerDueDate.setDate(processorder.getDueDate());
			}
			catch (Exception e)
			{

			}
			jComboBoxStatus.setSelectedItem(processorder.getStatus());
			jComboBoxPalletStatus.setSelectedItem(processorder.getDefaultPalletStatus());
		}
		else
		{

			jTextFieldLocation.setText(ctrl.getKeyValue("DEFAULT_LOCATION"));
			try
			{
				jSpinnerDueDate.setDate(new Date());
			}
			catch (Exception e)
			{

			}
		}
		materialChanged();
		jButtonSave.setEnabled(false);
	}

	private void materialChanged() {

		if (material.getMaterialProperties(jTextFieldMaterial.getText()) == true)
		{
			jTextFieldMaterialDescription.setText(material.getDescription());
			getMaterialUoms(jTextFieldMaterial.getText());

		}
		else
		{
			jTextFieldMaterialDescription.setText("");
			processorder.setRequiredUom("");
			processorder.setMaterial("");
			getMaterialUoms("");
		}
	}

	public void getMaterialUoms(String lmaterial) {
		uomList.clear();
		materialuom.setMaterial(lmaterial);
		uomList.addAll(materialuom.getMaterialUoms());
		ComboBoxModel jComboBoxBaseUOMModel = new DefaultComboBoxModel(uomList);
		paluom.getInternalUomProperties(processorder.getRequiredUom());
		jComboBoxBaseUOMModel.setSelectedItem(paluom);
		jComboBoxRequiredUOM.setModel(jComboBoxBaseUOMModel);

	}

	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(448, 289));
			this.setBounds(25, 25, 501, 533);
			setVisible(true);
			this.setClosable(true);
			this.setIconifiable(true);
			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
				jDesktopPane1.setLayout(null);
				{
					jLabel4 = new JLabel4j_std();
					jDesktopPane1.add(jLabel4);
					jLabel4.setText(lang.get("lbl_Process_Order"));
					jLabel4.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel4.setBounds(12, 12, 144, 21);
				}
				{
					jTextFieldProcessOrder = new JTextField4j();
					jDesktopPane1.add(jTextFieldProcessOrder);
					jTextFieldProcessOrder.setEditable(false);
					jTextFieldProcessOrder.setEnabled(false);
					jTextFieldProcessOrder.setBounds(163, 12, 126, 21);
				}
				{
					jLabel1 = new JLabel4j_std();
					jDesktopPane1.add(jLabel1);
					jLabel1.setText(lang.get("lbl_Material"));
					jLabel1.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel1.setBounds(12, 75, 144, 21);
				}
				{
					jTextFieldMaterial = new JTextField4j();
					AbstractDocument doc = (AbstractDocument) jTextFieldMaterial.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(JDBMaterial.field_material));
					jDesktopPane1.add(jTextFieldMaterial);
					jTextFieldMaterial.setEnabled(true);
					jTextFieldMaterial.setBounds(163, 75, 126, 21);
					jTextFieldMaterial.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});
					jTextFieldMaterial.addKeyListener(new KeyAdapter() {
						public void keyReleased(KeyEvent evt) {
							materialChanged();
						}
					});
				}
				{
					jLabel3 = new JLabel4j_std();
					jDesktopPane1.add(jLabel3);
					jLabel3.setText(lang.get("lbl_Description"));
					jLabel3.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel3.setBounds(12, 45, 144, 21);
				}
				{
					jTextFieldDescription = new JTextField4j();
					AbstractDocument doc = (AbstractDocument) jTextFieldDescription.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(JDBProcessOrder.field_description));
					jDesktopPane1.add(jTextFieldDescription);
					jTextFieldDescription.setBounds(163, 45, 301, 21);
					jTextFieldDescription.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});
				}
				{
					jLabel12 = new JLabel4j_std();
					jDesktopPane1.add(jLabel12);
					jLabel12.setText(lang.get("lbl_Process_Order_Recipe"));
					jLabel12.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel12.setBounds(12, 136, 144, 21);
				}
				{
					lblInspectionID = new JLabel4j_std();
					lblInspectionID.setText(lang.get("lbl_Inspection_ID"));
					lblInspectionID.setHorizontalAlignment(SwingConstants.TRAILING);
					lblInspectionID.setBounds(12, 419, 144, 21);
					jDesktopPane1.add(lblInspectionID);
				}
				{
					jTextFieldRecipeID = new JTextField4j();
					AbstractDocument doc = (AbstractDocument) jTextFieldRecipeID.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(JDBProcessOrder.field_recipe_id));
					jDesktopPane1.add(jTextFieldRecipeID);
					jTextFieldRecipeID.setBounds(163, 136, 126, 21);
					jTextFieldRecipeID.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});
				}
				{
					jTextFieldRequiredResource = new JTextField4j();
					AbstractDocument doc = (AbstractDocument) jTextFieldRequiredResource.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(JDBProcessOrder.field_required_resource));
					jDesktopPane1.add(jTextFieldRequiredResource);
					jTextFieldRequiredResource.setBounds(163, 353, 301, 21);
					jTextFieldRequiredResource.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});
				}
				{
					jTextFieldInspectionID = new JTextField4j();
					jTextFieldInspectionID.setBounds(163, 417, 126, 21);
					jTextFieldInspectionID.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});
					jDesktopPane1.add(jTextFieldInspectionID);
				}
				{
					jLabel7 = new JLabel4j_std();
					jDesktopPane1.add(jLabel7);
					jLabel7.setText(lang.get("lbl_Process_Order_Due_Date"));
					jLabel7.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel7.setBounds(12, 229, 144, 21);
				}
				{
					jSpinnerDueDate = new JDateControl();
					jDesktopPane1.add(jSpinnerDueDate);
					jSpinnerDueDate.setBounds(163, 225, 125, 25);
					jSpinnerDueDate.getEditor().setPreferredSize(new java.awt.Dimension(86, 32));
					jSpinnerDueDate.getEditor().addKeyListener(new KeyAdapter() {
						public void keyPressed(KeyEvent e) {
							jButtonSave.setEnabled(true);
						}
					});
					jSpinnerDueDate.addChangeListener(new ChangeListener() {
						public void stateChanged(final ChangeEvent e)

						{
							jButtonSave.setEnabled(true);
						}
					});
				}
				{
					jLabel2 = new JLabel4j_std();
					jDesktopPane1.add(jLabel2);
					jLabel2.setText(lang.get("lbl_Location_ID"));
					jLabel2.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel2.setBounds(12, 258, 144, 21);
				}
				{
					jTextFieldLocation = new JTextField4j();
					AbstractDocument doc = (AbstractDocument) jTextFieldLocation.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(JDBLocation.field_location_id));
					jDesktopPane1.add(jTextFieldLocation);
					jTextFieldLocation.setBounds(163, 258, 126, 21);
					jTextFieldLocation.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});
				}
				{
					jTextFieldCustomer = new JTextField4j();
					AbstractDocument doc = (AbstractDocument) jTextFieldCustomer.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(JDBCustomer.field_customer_id));
					jDesktopPane1.add(jTextFieldCustomer);
					jTextFieldCustomer.setBounds(163, 386, 126, 21);
					jTextFieldCustomer.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});
				}
				{
					jButton3 = new JButton4j(Common.icon_lookup);
					jDesktopPane1.add(jButton3);
					jButton3.setBounds(287, 386, 21, 21);
					jButton3.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							JLaunchLookup.dlgAutoExec = true;
							JLaunchLookup.dlgCriteriaDefault = "";
							if (JLaunchLookup.customers())
							{
								jTextFieldCustomer.setText(JLaunchLookup.dlgResult);
								jButtonSave.setEnabled(true);
							}
						}
					});
				}
				{
					jComboBoxRequiredUOM.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							jButtonSave.setEnabled(true);
						}
					});

					jComboBoxRequiredUOM.setBounds(163, 195, 301, 23);
					jDesktopPane1.add(jComboBoxRequiredUOM);
				}
				{
					jLabel14 = new JLabel4j_std();
					jDesktopPane1.add(jLabel14);
					jLabel14.setText(lang.get("lbl_Process_Order_Status"));
					jLabel14.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel14.setBounds(12, 286, 144, 21);
				}
				{
					ComboBoxModel jComboBoxStatusModel = new DefaultComboBoxModel(Common.processOrderStatus);
					jComboBoxStatus = new JComboBox4j();
					jDesktopPane1.add(jComboBoxStatus);
					jComboBoxStatus.setModel(jComboBoxStatusModel);
					jComboBoxStatus.setBounds(163, 286, 150, 21);
					jComboBoxStatus.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});
				}
				{
					jButtonSave = new JButton4j(Common.icon_save);
					jDesktopPane1.add(jButtonSave);
					jButtonSave.setText(lang.get("btn_Save"));
					jButtonSave.setBounds(62, 453, 112, 28);
					jButtonSave.setEnabled(false);
					jButtonSave.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							processorder.setDescription(jTextFieldDescription.getText());
							processorder.setRecipe(jTextFieldRecipeID.getText());
							processorder.setLocation(jTextFieldLocation.getText());
							processorder.setCustomerID(jTextFieldCustomer.getText());
							processorder.setInspectionID(jTextFieldInspectionID.getText());
							processorder.setMaterial(jTextFieldMaterial.getText());
							processorder.setRequiredQuantity(JUtility.stringToBigDecimal(jFormattedTextFieldRequiredQuantity.getText().toString()));
							processorder.setRequiredUom(((JDBUom) jComboBoxRequiredUOM.getSelectedItem()).getInternalUom());
							processorder.setRequiredResource(jTextFieldRequiredResource.getText());
							try
							{
								processorder.setStatus((String) jComboBoxStatus.getSelectedItem());
							}
							catch (Exception e)
							{
								processorder.setStatus("");
							}

							try
							{
								processorder.setDefaultPalletStatus((String) jComboBoxPalletStatus.getSelectedItem());
							}
							catch (Exception e)
							{
								processorder.setDefaultPalletStatus("");
							}

							Date d = jSpinnerDueDate.getDate();
							processorder.setDueDate(JUtility.getTimestampFromDate(d));
							if (processorder.isValidProcessOrder() == false)
							{
								processorder.create();
							}
							if (processorder.update())
							{
								jButtonSave.setEnabled(false);
							}
							else
							{
								JUtility.errorBeep();
								JOptionPane.showMessageDialog(Common.mainForm, processorder.getErrorMessage(), lang.get("dlg_Error"), JOptionPane.ERROR_MESSAGE);
							}

							jComboBoxPalletStatus.setSelectedItem(processorder.getDefaultPalletStatus());

						}
					});
				}
				{
					jButtonHelp = new JButton4j(Common.icon_help);
					jDesktopPane1.add(jButtonHelp);
					jButtonHelp.setText(lang.get("btn_Help"));
					jButtonHelp.setBounds(181, 453, 112, 28);
				}
				{
					jButtonClose = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonClose);
					jButtonClose.setText(lang.get("btn_Close"));
					jButtonClose.setBounds(300, 453, 112, 28);
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							dispose();
						}
					});
				}
				{
					jButton1 = new JButton4j(Common.icon_lookup);
					jDesktopPane1.add(jButton1);
					jButton1.setBounds(287, 75, 20, 20);
					jButton1.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							JLaunchLookup.dlgAutoExec = false;
							JLaunchLookup.dlgCriteriaDefault = "";
							if (JLaunchLookup.materials())
							{
								jTextFieldMaterial.setText(JLaunchLookup.dlgResult);
								materialChanged();
								jButtonSave.setEnabled(true);
							}
						}
					});
				}
				{
					jButton3 = new JButton4j(Common.icon_lookup);
					jDesktopPane1.add(jButton3);
					jButton3.setBounds(287, 258, 21, 21);
					jButton3.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							JLaunchLookup.dlgAutoExec = true;
							JLaunchLookup.dlgCriteriaDefault = "";
							if (JLaunchLookup.locations())
							{
								jTextFieldLocation.setText(JLaunchLookup.dlgResult);
								jButtonSave.setEnabled(true);
							}
						}
					});
				}

				{
					jLabel6 = new JLabel4j_std();
					jLabel6.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel6.setText(lang.get("lbl_Process_Order_Required_UOM"));
					jLabel6.setBounds(12, 197, 144, 21);
					jDesktopPane1.add(jLabel6);
				}

				{
					jFormattedTextFieldRequiredQuantity = new JQuantityInput(new BigDecimal("0"));
					jFormattedTextFieldRequiredQuantity.setHorizontalAlignment(SwingConstants.TRAILING);
					jFormattedTextFieldRequiredQuantity.setFont(Common.font_std);
					jFormattedTextFieldRequiredQuantity.setBounds(163, 165, 91, 21);
					jFormattedTextFieldRequiredQuantity.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});
					jDesktopPane1.add(jFormattedTextFieldRequiredQuantity);
				}

				{
					jLabelQuantity = new JLabel4j_std();
					jLabelQuantity.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabelQuantity.setText(lang.get("lbl_Process_Order_Required_Quantity"));
					jLabelQuantity.setBounds(12, 165, 144, 21);
					jDesktopPane1.add(jLabelQuantity);
				}

				{
					jLabel3_1 = new JLabel4j_std();
					jLabel3_1.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel3_1.setText(lang.get("lbl_Description"));
					jLabel3_1.setBounds(12, 105, 144, 21);
					jDesktopPane1.add(jLabel3_1);
				}

				{
					jTextFieldMaterialDescription = new JTextField4j();
					jTextFieldMaterialDescription.setBounds(163, 105, 301, 21);
					jTextFieldMaterialDescription.setEnabled(false);
					jDesktopPane1.add(jTextFieldMaterialDescription);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
