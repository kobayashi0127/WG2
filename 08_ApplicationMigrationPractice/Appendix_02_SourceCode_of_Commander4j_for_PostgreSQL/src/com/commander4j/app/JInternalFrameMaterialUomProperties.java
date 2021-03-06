package com.commander4j.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBMaterialUom;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JTextField4j;
import com.commander4j.sys.Common;
import com.commander4j.util.JFixedSizeFilter;

public class JInternalFrameMaterialUomProperties extends JInternalFrame
{
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JButton4j jButtonCancel;
	private JLabel4j_std jLabel2;
	private JLabel4j_std jLabel4;
	private JSpinner jSpinnerNumerator;
	private JSpinner jSpinnerDenominator;
	private JTextField4j jTextFieldVariant;
	private JTextField4j jTextFieldEAN;
	private JTextField4j jTextFieldUOM;
	private JTextField4j jTextFieldMaterial;
	private JLabel4j_std jLabel6;
	private JLabel4j_std jLabel5;
	private JLabel4j_std jLabel3;
	private JLabel4j_std jLabel1;
	private JButton4j jButtonHelp;
	private JButton4j jButtonUpdate;
	private SpinnerNumberModel numeratornumbermodel = new SpinnerNumberModel();
	private SpinnerNumberModel denominatornumbermodel = new SpinnerNumberModel();
	private String lmaterial;
	private String luom;
	private JDBMaterialUom materialuom = new JDBMaterialUom(Common.selectedHostID, Common.sessionID);
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);
	private JLabel4j_std status = new JLabel4j_std();

	public JInternalFrameMaterialUomProperties()
	{
		super();
		initGUI();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				jTextFieldEAN.requestFocus();
				jTextFieldEAN.setCaretPosition(jTextFieldEAN.getText().length());

				status.setForeground(Color.RED);
				status.setBackground(Color.GRAY);
				status.setBounds(0, 196, 385, 21);
				jDesktopPane1.add(status);
			}
		});
	}

	public JInternalFrameMaterialUomProperties(String material, String uom)
	{
		this();
		lmaterial = material;
		luom = uom;
		jTextFieldMaterial.setText(lmaterial);
		jTextFieldUOM.setText(luom);
		materialuom.setMaterial(lmaterial);
		materialuom.setUom(luom);
		materialuom.getMaterialUomProperties();
		jTextFieldEAN.setText(materialuom.getEan());
		jTextFieldVariant.setText(materialuom.getVariant());
		jSpinnerNumerator.setValue(materialuom.getNumerator());
		jSpinnerDenominator.setValue(materialuom.getDenominator());
	}

	private void initGUI()
	{
		try
		{
			this.setPreferredSize(new java.awt.Dimension(376, 234));
			this.setBounds(25, 25, 410, 268);
			setVisible(true);
			this.setIconifiable(true);
			this.setClosable(true);

			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				this.getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
				jDesktopPane1.setPreferredSize(new java.awt.Dimension(390, 208));
				jDesktopPane1.setLayout(null);
				{
					jButtonUpdate = new JButton4j(Common.icon_update);
					jDesktopPane1.add(jButtonUpdate);
					jButtonUpdate.setEnabled(false);
					jButtonUpdate.setText(lang.get("btn_Save"));
					jButtonUpdate.setMnemonic(lang.getMnemonicChar());
					jButtonUpdate.setPreferredSize(new java.awt.Dimension(90, 30));
					jButtonUpdate.setBounds(15, 164, 110, 30);
					jButtonUpdate.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{

							boolean result = true;

							materialuom.setMaterial(lmaterial);
							materialuom.setUom(luom);
							materialuom.setEan(jTextFieldEAN.getText());
							jTextFieldEAN.setText(materialuom.getEan());
							materialuom.setVariant(jTextFieldVariant.getText());
							jTextFieldVariant.setText(materialuom.getVariant());
							materialuom.setNumerator((Integer) jSpinnerNumerator.getValue());
							materialuom.setDenominator((Integer) jSpinnerDenominator.getValue());

							if (materialuom.isValidMaterialUom() == false)
							{
								result = materialuom.create();
							}
							else
							{
								result = materialuom.update();
							}
							
							if (result == false)
							{
								JOptionPane.showMessageDialog(Common.mainForm, materialuom.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
							}
							else
							{
								jButtonUpdate.setEnabled(false);
							}


						}
					});
				}
				{
					jButtonHelp = new JButton4j(Common.icon_help);
					jDesktopPane1.add(jButtonHelp);
					jButtonHelp.setText(lang.get("btn_Help"));
					jButtonHelp.setMnemonic(lang.getMnemonicChar());
					jButtonHelp.setBounds(129, 164, 110, 30);
				}
				{
					jButtonCancel = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonCancel);
					jButtonCancel.setText(lang.get("btn_Close"));
					jButtonCancel.setMnemonic(lang.getMnemonicChar());
					jButtonCancel.setPreferredSize(new java.awt.Dimension(90, 30));
					jButtonCancel.setBounds(242, 164, 110, 30);
					jButtonCancel.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{
							dispose();
						}
					});
				}
				{
					jLabel1 = new JLabel4j_std();
					jDesktopPane1.add(jLabel1);
					jLabel1.setText(lang.get("lbl_Material"));
					jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel1.setBounds(11, 9, 147, 21);
				}
				{
					jLabel2 = new JLabel4j_std();
					jDesktopPane1.add(jLabel2);
					jLabel2.setText(lang.get("lbl_Material_UOM_EAN"));
					jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel2.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel2.setBounds(11, 59, 147, 21);
				}
				{
					jLabel3 = new JLabel4j_std();
					jDesktopPane1.add(jLabel3);
					jLabel3.setText(lang.get("lbl_Material_UOM_Variant"));
					jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel3.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel3.setBounds(11, 83, 147, 21);
				}
				{
					jLabel4 = new JLabel4j_std();
					jDesktopPane1.add(jLabel4);
					jLabel4.setText(lang.get("lbl_Material_UOM_Numerator"));
					jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel4.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel4.setBounds(11, 107, 147, 21);
				}
				{
					jLabel5 = new JLabel4j_std();
					jDesktopPane1.add(jLabel5);
					jLabel5.setText(lang.get("lbl_Material_UOM_Denominator"));
					jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel5.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel5.setBounds(11, 129, 147, 21);
				}
				{
					jLabel6 = new JLabel4j_std();
					jDesktopPane1.add(jLabel6);
					jLabel6.setText(lang.get("lbl_Material_UOM"));
					jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel6.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel6.setBounds(11, 34, 147, 21);
				}
				{
					jTextFieldMaterial = new JTextField4j();
					jDesktopPane1.add(jTextFieldMaterial);
					jTextFieldMaterial.setHorizontalAlignment(SwingConstants.LEFT);
					jTextFieldMaterial.setEditable(false);
					jTextFieldMaterial.setPreferredSize(new java.awt.Dimension(100, 20));
					jTextFieldMaterial.setBounds(165, 9, 113, 21);
					jTextFieldMaterial.setEnabled(false);
				}
				{
					jTextFieldUOM = new JTextField4j();
					jDesktopPane1.add(jTextFieldUOM);
					jTextFieldUOM.setHorizontalAlignment(SwingConstants.LEFT);
					jTextFieldUOM.setEditable(false);
					jTextFieldUOM.setPreferredSize(new java.awt.Dimension(100, 20));
					jTextFieldUOM.setBounds(165, 34, 60, 21);
					jTextFieldUOM.setEnabled(false);
				}
				{
					jTextFieldEAN = new JTextField4j();
					AbstractDocument doc = (AbstractDocument) jTextFieldEAN.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(JDBMaterialUom.field_ean));
					jDesktopPane1.add(jTextFieldEAN);
					jTextFieldEAN.setPreferredSize(new java.awt.Dimension(40, 20));
					jTextFieldEAN.setFocusCycleRoot(true);
					jTextFieldEAN.setBounds(165, 59, 175, 21);
					jTextFieldEAN.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent evt)
						{

							jButtonUpdate.setEnabled(true);
						}
					});
				}
				{
					jTextFieldVariant = new JTextField4j();
					AbstractDocument doc = (AbstractDocument) jTextFieldVariant.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(JDBMaterialUom.field_variant));
					jDesktopPane1.add(jTextFieldVariant);
					jTextFieldVariant.setPreferredSize(new java.awt.Dimension(40, 20));
					jTextFieldVariant.setFocusCycleRoot(true);
					jTextFieldVariant.setBounds(165, 83, 34, 21);
					jTextFieldVariant.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent evt)
						{
							jButtonUpdate.setEnabled(true);
						}
					});
				}
				{

					jSpinnerDenominator = new JSpinner();
					JSpinner.NumberEditor ne2 = new JSpinner.NumberEditor(jSpinnerDenominator);
					ne2.getTextField().setFont(Common.font_std); 
					jSpinnerDenominator.setEditor(ne2);
					jDesktopPane1.add(jSpinnerDenominator);
					jSpinnerDenominator.setModel(denominatornumbermodel);
					jSpinnerDenominator.setBounds(165, 129, 75, 21);
					jSpinnerDenominator.addChangeListener(new ChangeListener()
					{
						public void stateChanged(ChangeEvent evt)
						{
							jButtonUpdate.setEnabled(true);
						}
					});
				}
				{

					jSpinnerNumerator = new JSpinner();
					JSpinner.NumberEditor ne1 = new JSpinner.NumberEditor(jSpinnerNumerator);
					ne1.getTextField().setFont(Common.font_std); 
					jSpinnerNumerator.setEditor(ne1);
					jDesktopPane1.add(jSpinnerNumerator);
					jSpinnerNumerator.setModel(numeratornumbermodel);
					jSpinnerNumerator.setBounds(165, 107, 75, 21);
					jSpinnerNumerator.addChangeListener(new ChangeListener()
					{
						public void stateChanged(ChangeEvent evt)
						{
							jButtonUpdate.setEnabled(true);
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
