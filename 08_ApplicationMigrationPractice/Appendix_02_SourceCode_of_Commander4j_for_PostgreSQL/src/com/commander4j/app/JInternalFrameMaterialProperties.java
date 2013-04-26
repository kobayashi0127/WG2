package com.commander4j.app;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;

import com.commander4j.db.JDBControl;
import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBMaterial;
import com.commander4j.db.JDBMaterialType;
import com.commander4j.db.JDBUom;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JComboBox4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JTextField4j;
import com.commander4j.sys.Common;
import com.commander4j.sys.JLaunchLookup;
import com.commander4j.sys.JLaunchMenu;
import com.commander4j.util.JFixedSizeFilter;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JInternalFrameMaterialProperties extends javax.swing.JInternalFrame
{
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JComboBox4j jComboBoxRoundingRule;
	private JLabel4j_std jLabel14;
	private JComboBox4j jComboBoxShelfLifeUOM;
	private JButton4j jButtonCancel;
	private JComboBox4j jComboBoxDefaultBatchStatus;
	private JLabel4j_std jLabel11;
	private JButton4j jButtonBatches;
	private JButton4j jButtonUOMs;
	private JButton4j jButtonLocations;
	private JSpinner jSpinnerNetWeight;
	private JSpinner jSpinnerGrossWeight;
	private JButton4j jButtonHelp;
	private JButton4j jButtonSave;
	private JTextField4j jTextFieldDescription;
	private JTextField4j jTextFieldMaterial;
	private JLabel4j_std jLabel13;
	private JLabel4j_std jLabel12;
	private JLabel4j_std jLabel4;
	private JLabel4j_std jLabel8;
	private JSpinner jSpinnerShelfLife;
	private JComboBox4j jComboBoxWeightUOM;
	private JTextField4j jTextFieldLegacyCode;
	private JLabel4j_std jLabel10;
	private JLabel4j_std jLabel9;
	private JLabel4j_std jLabel2;
	private JLabel4j_std jLabel5;
	private JLabel4j_std jLabel3;
	private JLabel4j_std jLabel1;
	private JComboBox4j jComboBoxMaterialType;
	private JComboBox4j jComboBoxBaseUOM;
	private JDBUom uom = new JDBUom(Common.selectedHostID, Common.sessionID);
	private JDBMaterial material = new JDBMaterial(Common.selectedHostID, Common.sessionID);
	private JDBMaterialType materialtype = new JDBMaterialType(Common.selectedHostID, Common.sessionID);;
	private JDBUom baseuom = new JDBUom(Common.selectedHostID, Common.sessionID);
	private JDBUom weightuom = new JDBUom(Common.selectedHostID, Common.sessionID);
	private JShelfLifeUom sluom = new JShelfLifeUom();
	private JShelfLifeRoundingRule slrr = new JShelfLifeRoundingRule();
	private Vector<JDBUom> uomList = new Vector<JDBUom>();
	private Vector<JShelfLifeUom> shelfLifeUomList = new Vector<JShelfLifeUom>();
	private Vector<JShelfLifeRoundingRule> shelfLifeRule = new Vector<JShelfLifeRoundingRule>();
	private Vector<JDBMaterialType> typeList = new Vector<JDBMaterialType>();
	private String lmaterial;
	private SpinnerNumberModel shelflifenumbermodel = new SpinnerNumberModel();
	private SpinnerNumberModel grossweightnumbermodel = new SpinnerNumberModel((float) 0, null, null, 1);
	private SpinnerNumberModel netweightnumbermodel = new SpinnerNumberModel((double) 0, null, null, 1);
	private JLabel4j_std lblEquipment;
	private JLabel4j_std lblInspectionID;
	private JTextField4j jTextFieldEquipmentType;
	private JTextField4j jTextFieldInspectionID;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);

	public JInternalFrameMaterialProperties()
	{
		super();
		uomList.add(new JDBUom("", ""));
		uomList.addAll(uom.getInternalUoms());
		typeList.add(new JDBMaterialType(Common.selectedHostID, Common.sessionID));
		typeList.addAll(materialtype.getMaterialTypes());
		JShelfLifeUom slu = new JShelfLifeUom();
		slu.setUom("");
		slu.setDescription("");
		shelfLifeUomList.add(slu);
		shelfLifeUomList.addAll(slu.getShelfLifeUOMs());
		JShelfLifeRoundingRule slrr1 = new JShelfLifeRoundingRule();
		slrr1.setRule("");
		slrr1.setDescription("");
		shelfLifeRule.add(slrr1);
		shelfLifeRule.addAll(slrr1.getShelfLifeRoundingRules());
		initGUI();

		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_ADMIN_MATERIAL_EDIT"));
		{
			lblEquipment = new JLabel4j_std();
			lblEquipment.setText(lang.get("lbl_Material_Equipment_Type"));
			lblEquipment.setHorizontalAlignment(SwingConstants.TRAILING);
			lblEquipment.setBounds(7, 361, 133, 21);
			jDesktopPane1.add(lblEquipment);
		}
		{
			lblInspectionID = new JLabel4j_std();
			lblInspectionID.setText(lang.get("lbl_Inspection_ID"));
			lblInspectionID.setHorizontalAlignment(SwingConstants.TRAILING);
			lblInspectionID.setBounds(7, 390, 133, 21);
			jDesktopPane1.add(lblInspectionID);
		}		
		{
			jTextFieldEquipmentType = new JTextField4j();
			jTextFieldEquipmentType.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					jButtonSave.setEnabled(true);
				}
			});
			jTextFieldEquipmentType.setBounds(147, 362, 85, 21);
			jDesktopPane1.add(jTextFieldEquipmentType);
		}
		{
			jTextFieldInspectionID = new JTextField4j();
			jTextFieldInspectionID.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					jButtonSave.setEnabled(true);
				}
			});
			jTextFieldInspectionID.setBounds(147, 391, 85, 21);
			jDesktopPane1.add(jTextFieldInspectionID);
		}		

		{
			JButton4j btnLookupInspection = new JButton4j("");
			btnLookupInspection.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JLaunchLookup.dlgAutoExec = true;
					if (JLaunchLookup.qmInspections())
					{
						jTextFieldInspectionID.setText(JLaunchLookup.dlgResult);
					}
				}
			});
			btnLookupInspection.setBounds(230, 391, 22, 21);
			jDesktopPane1.add(btnLookupInspection);
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jTextFieldDescription.requestFocus();
				jTextFieldDescription.setCaretPosition(jTextFieldDescription.getText().length());
				
			}
		});
	}

	public JInternalFrameMaterialProperties(String mat)
	{
		this();
		lmaterial = mat;
		jTextFieldMaterial.setText(lmaterial);
		setTitle(getTitle() + " - " + lmaterial);

		material.setMaterial(lmaterial);

		if (material.isValidMaterial())
		{
			material.getMaterialProperties(lmaterial);

			materialtype.setType(material.getMaterialType());
			materialtype.getMaterialTypeProperties();

			baseuom.setInternalUom(material.getBaseUom());
			baseuom.getInternalUomProperties();

			weightuom.setInternalUom(material.getWeightUom());
			weightuom.getInternalUomProperties();

			sluom.getShelfLifeUomProperties(material.getShelfLifeUom());
			slrr.getShelfLifeRuleProperties(material.getShelfLifeRule());

			jTextFieldDescription.setText(material.getDescription());

			jTextFieldEquipmentType.setText(material.getEquipmentType());
			
			jTextFieldInspectionID.setText(material.getInspectionID());

			jSpinnerShelfLife.setValue((Number) material.getShelfLife());

			jSpinnerGrossWeight.setValue((BigDecimal) material.getGrossWeight());
			jSpinnerNetWeight.setValue((BigDecimal) material.getNetWeight());

			jTextFieldLegacyCode.setText(material.getOldMaterial());

			jComboBoxDefaultBatchStatus.setSelectedItem(material.getDefaultBatchStatus());

			jButtonSave.setEnabled(false);
		}
	}

	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(422, 483));
			this.setBounds(0, 0, 475, 514);
			setVisible(true);
			this.setIconifiable(true);
			this.setClosable(true);
			getContentPane().setLayout(null);
			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBounds(0, 0, 453, 468);
				jDesktopPane1.setBackground(Color.WHITE);
				this.getContentPane().add(jDesktopPane1);
				jDesktopPane1.setPreferredSize(new java.awt.Dimension(447, 385));
				jDesktopPane1.setLayout(null);
				{
					jTextFieldMaterial = new JTextField4j();
					jDesktopPane1.add(jTextFieldMaterial);
					jTextFieldMaterial.setEditable(false);
					jTextFieldMaterial.setEnabled(false);
					jTextFieldMaterial.setBounds(147, 9, 146, 21);
				}
				{
					jTextFieldDescription = new JTextField4j();
					AbstractDocument doc = (AbstractDocument) jTextFieldDescription.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(80));
					jDesktopPane1.add(jTextFieldDescription);
					jTextFieldDescription.setBounds(147, 38, 288, 21);
					jTextFieldDescription.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});
				}
				{
					jButtonSave = new JButton4j(Common.icon_save);
					jDesktopPane1.add(jButtonSave);
					jButtonSave.setEnabled(false);
					jButtonSave.setText(lang.get("btn_Save"));
					jButtonSave.setMnemonic(lang.getMnemonicChar());
					jButtonSave.setBounds(58, 428, 112, 28);
					jButtonSave.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							boolean result = true;

							material.setDescription(jTextFieldDescription.getText());
							material.setMaterialType(((JDBMaterialType) jComboBoxMaterialType.getSelectedItem()).getType());
							material.setBaseUom(((JDBUom) jComboBoxBaseUOM.getSelectedItem()).getInternalUom());

							material.setShelfLife((Integer) jSpinnerShelfLife.getValue());
							material.setShelfLifeUom(((JShelfLifeUom) jComboBoxShelfLifeUOM.getSelectedItem()).getUom());
							material.setShelfLifeRule(((JShelfLifeRoundingRule) jComboBoxRoundingRule.getSelectedItem()).getRule());

							material.setDefaultBatchStatus((String) jComboBoxDefaultBatchStatus.getSelectedItem());

							BigDecimal bd = new BigDecimal(0).setScale(3, BigDecimal.ROUND_HALF_UP);
							bd = BigDecimal.valueOf(grossweightnumbermodel.getNumber().doubleValue()).setScale(3, BigDecimal.ROUND_HALF_UP);
							material.setGrossWeight(bd);
							bd = BigDecimal.valueOf(netweightnumbermodel.getNumber().doubleValue()).setScale(3, BigDecimal.ROUND_HALF_UP);
							material.setNetWeight(bd);

							try
							{
								material.setWeightUom(((JDBUom) jComboBoxWeightUOM.getSelectedItem()).getInternalUom());
							}
							catch (Exception e)
							{
								material.setWeightUom("");
							}

							material.setOldMaterial(jTextFieldLegacyCode.getText());

							material.setEquipmentType(jTextFieldEquipmentType.getText());
							
							material.setInspectionID(jTextFieldInspectionID.getText());

							if (material.isValidMaterial() == false)
							{
								result = material.create();
								if (result == true)
								{
									result = material.update();
								}
							}
							else
							{
								result = material.update();
							}
							if (result == false)
							{
								JOptionPane.showMessageDialog(Common.mainForm, material.getErrorMessage(), lang.get("dlg_Error"), JOptionPane.ERROR_MESSAGE);
							}
							else
							{
								jButtonSave.setEnabled(false);
							}

						}
					});
				}
				{
					jButtonHelp = new JButton4j(Common.icon_help);
					jDesktopPane1.add(jButtonHelp);
					jButtonHelp.setText(lang.get("btn_Help"));
					jButtonHelp.setMnemonic(lang.getMnemonicChar());
					jButtonHelp.setBounds(170, 428, 112, 28);
				}
				{
					jButtonCancel = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonCancel);
					jButtonCancel.setText(lang.get("btn_Close"));
					jButtonCancel.setMnemonic(lang.getMnemonicChar());
					jButtonCancel.setBounds(282, 428, 112, 28);
					jButtonCancel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							dispose();
						}
					});
				}
				{
					ComboBoxModel jComboBoxBaseUOMModel = new DefaultComboBoxModel(uomList);
					jComboBoxBaseUOM = new JComboBox4j();
					jDesktopPane1.add(jComboBoxBaseUOM);
					jComboBoxBaseUOM.setModel(jComboBoxBaseUOMModel);
					jComboBoxBaseUOM.setMaximumRowCount(12);
					jComboBoxBaseUOM.setBounds(147, 97, 248, 23);
					jComboBoxBaseUOM.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});

					jComboBoxBaseUOMModel.setSelectedItem(baseuom);

				}
				{
					ComboBoxModel jComboBoxMaterialTypeModel = new DefaultComboBoxModel(typeList);
					jComboBoxMaterialType = new JComboBox4j();
					jDesktopPane1.add(jComboBoxMaterialType);
					jComboBoxMaterialType.setModel(jComboBoxMaterialTypeModel);
					jComboBoxMaterialType.setBounds(147, 68, 248, 23);
					jComboBoxMaterialType.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});

					jComboBoxMaterialTypeModel.setSelectedItem(materialtype);
				}
				{
					jLabel12 = new JLabel4j_std();
					jDesktopPane1.add(jLabel12);
					jLabel12.setText(lang.get("lbl_Material_Default_Batch_Status"));
					jLabel12.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel12.setBounds(7, 332, 133, 21);
				}
				{
					jLabel13 = new JLabel4j_std();
					jDesktopPane1.add(jLabel13);
					jLabel13.setText(lang.get("lbl_Material_Shelf_Life_UOM"));
					jLabel13.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel13.setBounds(7, 155, 133, 21);
				}
				{
					ComboBoxModel jComboBoxShelfLifeUOMModel = new DefaultComboBoxModel(shelfLifeUomList);
					jComboBoxShelfLifeUOM = new JComboBox4j();
					jDesktopPane1.add(jComboBoxShelfLifeUOM);
					jComboBoxShelfLifeUOM.setModel(jComboBoxShelfLifeUOMModel);
					jComboBoxShelfLifeUOM.setMaximumRowCount(12);
					jComboBoxShelfLifeUOM.setBounds(147, 156, 165, 21);
					jComboBoxShelfLifeUOM.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});

					jComboBoxShelfLifeUOMModel.setSelectedItem(sluom);
				}
				{
					jLabel14 = new JLabel4j_std();
					jDesktopPane1.add(jLabel14);
					jLabel14.setText(lang.get("lbl_Material_Shelf_Life_Rounding_Rule"));
					jLabel14.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel14.setBounds(7, 184, 133, 21);
				}
				{
					ComboBoxModel jComboBoxRoundingRuleModel = new DefaultComboBoxModel(shelfLifeRule);
					jComboBoxRoundingRule = new JComboBox4j();
					jDesktopPane1.add(jComboBoxRoundingRule);
					jComboBoxRoundingRule.setModel(jComboBoxRoundingRuleModel);
					jComboBoxRoundingRule.setBounds(147, 185, 165, 21);
					jComboBoxRoundingRule.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});

					jComboBoxRoundingRuleModel.setSelectedItem(slrr);
				}
				{
					jLabel1 = new JLabel4j_std();
					jDesktopPane1.add(jLabel1);
					jLabel1.setText(lang.get("lbl_Material"));
					jLabel1.setBounds(7, 8, 133, 21);
					jLabel1.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jLabel3 = new JLabel4j_std();
					jDesktopPane1.add(jLabel3);
					jLabel3.setText(lang.get("lbl_Description"));
					jLabel3.setBounds(7, 38, 133, 21);
					jLabel3.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jLabel5 = new JLabel4j_std();
					jDesktopPane1.add(jLabel5);
					jLabel5.setText(lang.get("lbl_Material_Base_UOM"));
					jLabel5.setBounds(7, 96, 133, 21);
					jLabel5.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jLabel2 = new JLabel4j_std();
					jDesktopPane1.add(jLabel2);
					jLabel2.setText(lang.get("lbl_Material_Type"));
					jLabel2.setBounds(7, 67, 133, 21);
					jLabel2.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jLabel4 = new JLabel4j_std();
					jDesktopPane1.add(jLabel4);
					jLabel4.setText(lang.get("lbl_Material_Shelf_Life"));
					jLabel4.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel4.setBounds(7, 126, 133, 21);
				}
				{
					jLabel8 = new JLabel4j_std();
					jDesktopPane1.add(jLabel8);
					jLabel8.setText(lang.get("lbl_Material_Gross_Weight"));
					jLabel8.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel8.setBounds(7, 214, 133, 21);
					jLabel8.setFocusTraversalPolicyProvider(true);
				}
				{
					jLabel9 = new JLabel4j_std();
					jDesktopPane1.add(jLabel9);
					jLabel9.setText(lang.get("lbl_Material_Net_Weight"));
					jLabel9.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel9.setBounds(7, 244, 133, 21);
				}
				{
					jLabel10 = new JLabel4j_std();
					jDesktopPane1.add(jLabel10);
					jLabel10.setText(lang.get("lbl_Material_Weight_UOM"));
					jLabel10.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel10.setBounds(7, 273, 133, 21);
				}
				{
					jTextFieldLegacyCode = new JTextField4j();
					AbstractDocument doc = (AbstractDocument) jTextFieldLegacyCode.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(20));
					jDesktopPane1.add(jTextFieldLegacyCode);
					jTextFieldLegacyCode.setBounds(147, 303, 125, 21);
					jTextFieldLegacyCode.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});
				}
				{
					ComboBoxModel jComboBox1Model = new DefaultComboBoxModel(uomList);
					jComboBoxWeightUOM = new JComboBox4j();
					jDesktopPane1.add(jComboBoxWeightUOM);
					jComboBoxWeightUOM.setModel(jComboBox1Model);
					jComboBoxWeightUOM.setBounds(147, 274, 248, 23);
					jComboBoxWeightUOM.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});
					jComboBox1Model.setSelectedItem(weightuom);
				}
				{
					jSpinnerShelfLife = new JSpinner();
					jDesktopPane1.add(jSpinnerShelfLife);
					jSpinnerShelfLife.setModel(shelflifenumbermodel);
					jSpinnerShelfLife.setBounds(147, 126, 60, 21);
					JSpinner.NumberEditor nec2 = new JSpinner.NumberEditor(jSpinnerShelfLife);
					nec2.getTextField().setFont(Common.font_std); 
					jSpinnerShelfLife.setEditor(nec2);
					jSpinnerShelfLife.addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});
				}
				{
					jSpinnerGrossWeight = new JSpinner();

					jDesktopPane1.add(jSpinnerGrossWeight);
					jSpinnerGrossWeight.setModel(grossweightnumbermodel);

					jSpinnerGrossWeight.setBounds(147, 214, 95, 21);
					JSpinner.NumberEditor nec2 = new JSpinner.NumberEditor(jSpinnerGrossWeight);
					nec2.getTextField().setFont(Common.font_std); 
					jSpinnerGrossWeight.setEditor(nec2);
					jSpinnerGrossWeight.addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});
				}
				{
					jSpinnerNetWeight = new JSpinner();
					jDesktopPane1.add(jSpinnerNetWeight);
					jSpinnerNetWeight.setModel(netweightnumbermodel);
					JSpinner.NumberEditor nec2 = new JSpinner.NumberEditor(jSpinnerNetWeight);
					nec2.getTextField().setFont(Common.font_std); 
					jSpinnerNetWeight.setEditor(nec2);
					jSpinnerNetWeight.setBounds(147, 244, 95, 21);
					jSpinnerNetWeight.addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});
				}
				{
					jButtonUOMs = new JButton4j();
					jDesktopPane1.add(jButtonUOMs);
					jButtonUOMs.setText(lang.get("btn_Material_UOM_Conversions"));
					jButtonUOMs.setBounds(255, 240, 140, 25);
					jButtonUOMs.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MATERIAL_UOM"));
					jButtonUOMs.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							String base = ((JDBUom) jComboBoxBaseUOM.getSelectedItem()).getInternalUom();
							JLaunchMenu.runForm("FRM_ADMIN_MATERIAL_UOM", lmaterial, base);
						}
					});
				}
				{
					JDBControl ctrl = new JDBControl(Common.selectedHostID, Common.sessionID);
					String value = ctrl.getKeyValue("SSCC_LOCATION_VALIDATION");
					Boolean enabled = Boolean.valueOf(value);
					jButtonLocations = new JButton4j();
					jDesktopPane1.add(jButtonLocations);
					jButtonLocations.setText(lang.get("btn_Material_Locations"));
					jButtonLocations.setEnabled(enabled);
					jButtonLocations.setBounds(220, 124, 175, 25);
					if (enabled)
					{
						jButtonLocations.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MATERIAL_LOCATION"));
					}
					jButtonLocations.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {

							JLaunchMenu.runForm("FRM_ADMIN_MATERIAL_LOCATION", lmaterial);
						}
					});
					
				}
				{
					jButtonBatches = new JButton4j();
					jDesktopPane1.add(jButtonBatches);
					jButtonBatches.setText(lang.get("btn_Material_Batches"));
					jButtonBatches.setBounds(255, 210, 140, 25);
					jButtonBatches.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_MATERIAL_BATCH"));
					jButtonBatches.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							JLaunchMenu.runForm("FRM_ADMIN_MATERIAL_BATCH", lmaterial);
						}
					});
				}
				{
					jLabel11 = new JLabel4j_std();
					jDesktopPane1.add(jLabel11);
					jLabel11.setText(lang.get("lbl_Material_Legacy_Code"));
					jLabel11.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel11.setBounds(7, 302, 133, 21);
				}
				{
					ComboBoxModel jComboBoxDefaultBatchStatusModel = new DefaultComboBoxModel(Common.batchStatusIncBlank);
					jComboBoxDefaultBatchStatus = new JComboBox4j();
					jDesktopPane1.add(jComboBoxDefaultBatchStatus);
					jComboBoxDefaultBatchStatus.setModel(jComboBoxDefaultBatchStatusModel);
					jComboBoxDefaultBatchStatus.setBounds(148, 332, 164, 23);
					jComboBoxDefaultBatchStatus.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jButtonSave.setEnabled(true);
						}
					});
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
