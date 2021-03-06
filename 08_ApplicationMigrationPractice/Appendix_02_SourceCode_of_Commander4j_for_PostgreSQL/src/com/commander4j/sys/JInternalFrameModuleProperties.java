package com.commander4j.sys;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBModule;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JComboBox4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JTextField4j;
import com.commander4j.util.JFileFilterExecs;
import com.commander4j.util.JFileFilterImages;
import com.commander4j.util.JFileFilterLabels;
import com.commander4j.util.JFileFilterReports;
import com.commander4j.util.JFixedSizeFilter;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JInternalFrameModuleProperties extends javax.swing.JInternalFrame
{
	private JSpinner jSpinnerPrintCopies;
	private JLabel4j_std jLabel3_3;
	private JLabel4j_std jLabel3_2;
	private JLabel4j_std jLabel3_1;
	private JCheckBox jCheckBoxPrintDialog;
	private JCheckBox jCheckBoxPrintPreview;
	private JButton4j jButtonExecDirChooser_1;
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JButton4j jButtonClose;
	private JLabel4j_std jLabel4;
	private JLabel4j_std jLabel8;
	private JButton4j jButtonExecFileChooser;
	private JButton4j jButtonExecDirChooser;
	private JLabel4j_std jLabel10;
	private JLabel4j_std jLabel9;
	private JTextField4j jTextFieldExecFilename;
	private JTextField4j jTextFieldExecDir;
	private JButton4j jButtonReportFileChooser;
	private JTextField4j jTextFieldReportFilename;
	private JButton4j jButtonHelp;
	private JLabel4j_std jLabel7;
	private JTextField4j jTextFieldHelpsetid;
	private JButton4j jButtonIconPreview;
	private JButton4j jButtonIconFileChooser;
	private JTextField4j jTextFieldIconFilename;
	private JLabel4j_std jLabel2;
	private JTextField4j jTextFieldMnemonic;
	private JLabel4j_std jLabelMnemonic;
	private JLabel4j_std jLabel6;
	private JCheckBox jCheckBoxDesktop;
	private JCheckBox jCheckBoxScanner;
	private JComboBox4j jComboBoxType;
	private JLabel4j_std jLabel5;
	private JLabel4j_std jLabel3;
	private JButton4j jButtonUpdate;
	private JTextField4j jTextFieldResourceKey;
	private JTextField4j jTextFieldModuleId;
	private JLabel4j_std jLabel1;
	private JDBModule module = new JDBModule(Common.selectedHostID, Common.sessionID);
	private Object current_type = new Object();
	private Object new_type = new Object();
	private String lmodule_id;
	private JComboBox4j comboBox;
	private JLabel4j_std lblReportType;
	private JTextField4j textFieldTranslatedDescripton;
	private JLabel4j_std lblResourceKey;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);

	public JInternalFrameModuleProperties()
	{
		super();
		initGUI();
		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_ADMIN_MODULE_EDIT"));

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jTextFieldResourceKey.requestFocus();
				jTextFieldResourceKey.setCaretPosition(jTextFieldResourceKey.getText().length());
				{
					lblResourceKey = new JLabel4j_std();
					lblResourceKey.setText(lang.get("lbl_Language_Key"));
					lblResourceKey.setHorizontalTextPosition(SwingConstants.RIGHT);
					lblResourceKey.setHorizontalAlignment(SwingConstants.RIGHT);
					lblResourceKey.setBounds(10, 35, 94, 21);
					jDesktopPane1.add(lblResourceKey);
				}

			}
		});
	}

	public JInternalFrameModuleProperties(String moduleid)
	{

		this();
		lmodule_id = moduleid;
		jTextFieldModuleId.setText(lmodule_id);
		setTitle(getTitle() + " - " + lmodule_id);

		module.setModuleId(lmodule_id);
		module.getModuleProperties();

		jTextFieldResourceKey.setText(module.getResourceKey());
		textFieldTranslatedDescripton.setText(module.getDescription());

		jTextFieldIconFilename.setText(module.getIconFilename());
		jTextFieldReportFilename.setText(module.getReportFilename());
		jTextFieldExecFilename.setText(module.getExecFilename());
		jTextFieldExecDir.setText(module.getExecDir());
		jTextFieldHelpsetid.setText(module.getHelpSetID());

		jComboBoxType.setSelectedItem(module.getType());
		comboBox.setSelectedItem(module.getReportType());

		if (module.isDKModule())
			jCheckBoxDesktop.setSelected(true);
		else
			jCheckBoxDesktop.setSelected(false);

		if (module.isRFModule())
			jCheckBoxScanner.setSelected(true);
		else
			jCheckBoxScanner.setSelected(false);

		if (module.isPrintPreview())
			jCheckBoxPrintPreview.setSelected(true);
		else
			jCheckBoxPrintPreview.setSelected(false);

		if (module.isPrintDialog())
			jCheckBoxPrintDialog.setSelected(true);
		else
			jCheckBoxPrintDialog.setSelected(false);

		jSpinnerPrintCopies.setValue(module.getPrintCopies());

		jButtonIconPreview.setIcon(JDBModule.getModuleIcon(jTextFieldIconFilename.getText(), (String) jComboBoxType.getSelectedItem()));

		reset_changes();
	}

	private void reset_changes() {
		current_type = jComboBoxType.getSelectedItem();
		jButtonUpdate.setEnabled(false);
	}

	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(573, 396));
			this.setBounds(0, 0, 591+Common.LFAdjustWidth, 428+Common.LFAdjustHeight);
			setVisible(true);
			this.setClosable(true);
			this.setTitle("Module Properties");
			this.setIconifiable(true);
			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				this.getContentPane().add(jDesktopPane1, BorderLayout.WEST);
				jDesktopPane1.setPreferredSize(new java.awt.Dimension(577, 280));
				jDesktopPane1.setLayout(null);
				{
					textFieldTranslatedDescripton = new JTextField4j();
					textFieldTranslatedDescripton.setEditable(false);
					textFieldTranslatedDescripton.setFocusCycleRoot(true);
					textFieldTranslatedDescripton.setCaretPosition(0);
					textFieldTranslatedDescripton.setBounds(110, 60, 280, 21);
					jDesktopPane1.add(textFieldTranslatedDescripton);
				}
				{
					jLabel1 = new JLabel4j_std();
					jDesktopPane1.add(jLabel1);
					jLabel1.setText(lang.get("lbl_Module_ID"));
					jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel1.setBounds(0, 10, 104, 21);
				}
				{
					lblReportType = new JLabel4j_std();
					lblReportType.setText(lang.get("lbl_Module_Report_Type"));
					lblReportType.setHorizontalAlignment(SwingConstants.TRAILING);
					lblReportType.setBounds(0, 206, 104, 21);
					jDesktopPane1.add(lblReportType);
				}
				{
					jTextFieldModuleId = new JTextField4j();
					jDesktopPane1.add(jTextFieldModuleId);
					jTextFieldModuleId.setHorizontalAlignment(SwingConstants.LEFT);
					jTextFieldModuleId.setEditable(false);
					jTextFieldModuleId.setBounds(110, 10, 280, 21);
					jTextFieldModuleId.setEnabled(false);
				}
				{
					jTextFieldResourceKey = new JTextField4j();
					AbstractDocument doc = (AbstractDocument) jTextFieldResourceKey.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(JDBModule.field_description));
					jDesktopPane1.add(jTextFieldResourceKey);
					jTextFieldResourceKey.setFocusCycleRoot(true);
					jTextFieldResourceKey.setBounds(110, 35, 280, 21);
					jTextFieldResourceKey.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonUpdate.setEnabled(true);
						}
					});
				}
				{

					jButtonUpdate = new JButton4j(Common.icon_update);
					jDesktopPane1.add(jButtonUpdate);
					jButtonUpdate.setEnabled(false);
					jButtonUpdate.setText(lang.get("btn_Save"));
					jButtonUpdate.setHorizontalTextPosition(SwingConstants.RIGHT);
					jButtonUpdate.setMnemonic(lang.getMnemonicChar());
					jButtonUpdate.setBounds(136, 345, 112, 28);
					jButtonUpdate.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {

							module.setResourceKey(jTextFieldResourceKey.getText());

							module.setIconFilename(jTextFieldIconFilename.getText());
							module.setHelpSetID(jTextFieldHelpsetid.getText());

							module.setType((String) jComboBoxType.getSelectedItem());
							module.setReportType((String) comboBox.getSelectedItem());

							if (jCheckBoxDesktop.isSelected())
								module.setDKActive("Y");
							else
								module.setDKActive("N");

							if (jCheckBoxScanner.isSelected())
								module.setRFActive("Y");
							else
								module.setRFActive("N");

							if (jCheckBoxPrintPreview.isSelected())
								module.setPrintPreview("Y");
							else
								module.setPrintPreview("N");

							if (jCheckBoxPrintDialog.isSelected())
								module.setPrintDialog("Y");
							else
								module.setPrintDialog("N");

							module.setPrintCopies(Integer.valueOf(jSpinnerPrintCopies.getValue().toString()));

							module.setReportFilename(jTextFieldReportFilename.getText());
							module.setExecFilename(jTextFieldExecFilename.getText());
							module.setExecDir(jTextFieldExecDir.getText());

							if (module.update() == false)
							{
								JUtility.errorBeep();
								JOptionPane.showMessageDialog(null, module.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
							}

							jButtonUpdate.setEnabled(false);
						}
					});
				}
				{

					jButtonClose = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonClose);
					jButtonClose.setText(lang.get("btn_Close"));
					jButtonClose.setMnemonic(lang.getMnemonicChar());
					jButtonClose.setBounds(364, 345, 112, 28);
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							dispose();
						}
					});
				}
				{
					jLabel3 = new JLabel4j_std();
					jDesktopPane1.add(jLabel3);
					jLabel3.setText(lang.get("lbl_Module_Type"));
					jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel3.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel3.setBounds(0, 93, 104, 21);
				}
				{
					jLabel4 = new JLabel4j_std();
					jDesktopPane1.add(jLabel4);
					jLabel4.setText(lang.get("lbl_Module_Desktop"));
					jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel4.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel4.setBounds(0, 122, 104, 21);
				}
				{
					jLabel5 = new JLabel4j_std();
					jDesktopPane1.add(jLabel5);
					jLabel5.setText(lang.get("lbl_Description"));
					jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel5.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel5.setBounds(0, 60, 104, 21);
				}
				{
					comboBox = new JComboBox4j();
					comboBox.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							jButtonUpdate.setEnabled(true);
						}
					});
					comboBox.setBounds(110, 206, 180, 23);
					comboBox.addItem("");
					comboBox.addItem("Standard");
					comboBox.addItem("Label");
					jDesktopPane1.add(comboBox);
				}
				{
					jComboBoxType = new JComboBox4j();
					jDesktopPane1.add(jComboBoxType);
					jComboBoxType.setEnabled(true);
					jComboBoxType.setEditable(false);
					jComboBoxType.setLightWeightPopupEnabled(true);
					jComboBoxType.setIgnoreRepaint(false);
					jComboBoxType.setBounds(110, 93, 180, 23);
					jComboBoxType.addItem("EXEC");
					jComboBoxType.addItem("FORM");
					jComboBoxType.addItem("FUNCTION");
					jComboBoxType.addItem("MENU");
					jComboBoxType.addItem("REPORT");
					jComboBoxType.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							new_type = jComboBoxType.getSelectedItem();
							if (new_type != null)
							{
								if (new_type.equals(current_type) != true)
								{
									// module_updated = true;
									jButtonUpdate.setEnabled(true);
									jButtonIconPreview.setIcon(JDBModule.getModuleIcon(jTextFieldIconFilename.getText(), (String) jComboBoxType.getSelectedItem()));
								}

								if (new_type.equals("REPORT"))
								{
									jTextFieldReportFilename.setEnabled(true);
									jButtonReportFileChooser.setEnabled(true);
									jCheckBoxPrintPreview.setEnabled(true);
									jCheckBoxPrintDialog.setEnabled(true);
									jSpinnerPrintCopies.setEnabled(true);
									comboBox.setEnabled(true);
								}
								else
								{
									jTextFieldReportFilename.setEnabled(false);
									jButtonReportFileChooser.setEnabled(false);
									jCheckBoxPrintPreview.setEnabled(false);
									jCheckBoxPrintDialog.setEnabled(false);
									jSpinnerPrintCopies.setEnabled(false);
									comboBox.setEnabled(false);
								}

								if (new_type.equals("EXEC"))
								{
									jTextFieldExecFilename.setEnabled(true);
									jButtonExecFileChooser.setEnabled(true);
									jTextFieldExecDir.setEnabled(true);
									jButtonExecDirChooser.setEnabled(true);
								}
								else
								{
									jTextFieldExecFilename.setEnabled(false);
									jButtonExecFileChooser.setEnabled(false);
									jTextFieldExecDir.setEnabled(false);
									jButtonExecDirChooser.setEnabled(false);
								}
							}
						}
					});
				}
				{
					jCheckBoxScanner = new JCheckBox();
					jDesktopPane1.add(jCheckBoxScanner);
					jCheckBoxScanner.setSelected(true);
					jCheckBoxScanner.setBounds(110, 146, 21, 21);
					jCheckBoxScanner.setBackground(new java.awt.Color(255, 255, 255));
					jCheckBoxScanner.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							// module_updated = true;
							jButtonUpdate.setEnabled(true);
						}
					});
				}
				{
					jCheckBoxDesktop = new JCheckBox();
					jDesktopPane1.add(jCheckBoxDesktop);
					jCheckBoxDesktop.setSelected(true);
					jCheckBoxDesktop.setBounds(110, 122, 21, 21);
					jCheckBoxDesktop.setBackground(new java.awt.Color(255, 255, 255));
					jCheckBoxDesktop.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							// module_updated = true;
							jButtonUpdate.setEnabled(true);
						}
					});
				}
				{
					jLabel6 = new JLabel4j_std();
					jDesktopPane1.add(jLabel6);
					jLabel6.setText(lang.get("lbl_Module_Scanner"));
					jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel6.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel6.setBounds(0, 146, 104, 21);
				}

				{
					jLabelMnemonic = new JLabel4j_std();
					jDesktopPane1.add(jLabelMnemonic);
					jLabelMnemonic.setText(lang.get("lbl_Language_Mnemonic"));
					jLabelMnemonic.setBounds(395, 60, 112, 21);
					jLabelMnemonic.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabelMnemonic.setHorizontalTextPosition(SwingConstants.RIGHT);
				}
				{
					jTextFieldMnemonic = new JTextField4j();
					jTextFieldMnemonic.setEditable(false);
					AbstractDocument doc = (AbstractDocument) jTextFieldMnemonic.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(JDBModule.field_mneumonic));

					jDesktopPane1.add(jTextFieldMnemonic);
					jTextFieldMnemonic.setText("");

					jTextFieldMnemonic.setBounds(512, 60, 42, 21);
					jTextFieldMnemonic.setToolTipText("Character to underline");
					jTextFieldMnemonic.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonUpdate.setEnabled(true);
						}
					});
				}
				{
					jLabel2 = new JLabel4j_std();
					jDesktopPane1.add(jLabel2);
					jLabel2.setText(lang.get("lbl_Module_Executable_Directory"));
					jLabel2.setBounds(0, 290, 104, 21);
					jLabel2.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jTextFieldIconFilename = new JTextField4j();
					jDesktopPane1.add(jTextFieldIconFilename);
					jTextFieldIconFilename.setBounds(110, 177, 163, 22);
					jTextFieldIconFilename.addKeyListener(new KeyAdapter() {
						public void keyReleased(KeyEvent evt) {
							jButtonIconPreview.setIcon(JDBModule.getModuleIcon(jTextFieldIconFilename.getText(), (String) jComboBoxType.getSelectedItem()));
						}

						public void keyTyped(KeyEvent evt) {
							jButtonUpdate.setEnabled(true);
						}
					});
				}
				{
					jButtonIconFileChooser = new JButton4j();
					jDesktopPane1.add(jButtonIconFileChooser);
					jButtonIconFileChooser.setText("..");
					jButtonIconFileChooser.setBounds(273, 177, 17, 21);
					jButtonIconFileChooser.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {

							JFileChooser loadIco = new JFileChooser();

							try
							{
								File f = new File(new File("./images").getCanonicalPath());
								loadIco.setCurrentDirectory(f);
								loadIco.addChoosableFileFilter(new JFileFilterImages());
								loadIco.setSelectedFile(new File(jTextFieldIconFilename.getText()));
							}
							catch (Exception e)
							{
							}

							if (loadIco.showOpenDialog(jButtonIconFileChooser) == JFileChooser.APPROVE_OPTION)
							{
								File selectedFile;
								selectedFile = loadIco.getSelectedFile();

								if (selectedFile != null)
								{
									if (jTextFieldIconFilename.getText().compareTo(selectedFile.getName()) != 0)
									{
										jTextFieldIconFilename.setText(selectedFile.getName());
										jButtonIconPreview.setIcon(JDBModule.getModuleIcon(jTextFieldIconFilename.getText(), (String) jComboBoxType.getSelectedItem()));
										jButtonUpdate.setEnabled(true);
									}
								}
							}
						}
					});
				}
				{
					jButtonIconPreview = new JButton4j();
					jDesktopPane1.add(jButtonIconPreview);
					jButtonIconPreview.setBounds(292, 177, 23, 22);
					jButtonIconPreview.setBorderPainted(false);
					jButtonIconPreview.setContentAreaFilled(false);
					jButtonIconPreview.setRolloverEnabled(false);
					jButtonIconPreview.setRequestFocusEnabled(false);
				}
				{
					jTextFieldHelpsetid = new JTextField4j();
					AbstractDocument doc = (AbstractDocument) jTextFieldHelpsetid.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(JDBModule.field_helpset_id));
					jDesktopPane1.add(jTextFieldHelpsetid);
					jTextFieldHelpsetid.setBounds(110, 317, 427, 21);
					jTextFieldHelpsetid.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {

							jButtonUpdate.setEnabled(true);
						}
					});
				}
				{
					jLabel7 = new JLabel4j_std();
					jDesktopPane1.add(jLabel7);
					jLabel7.setText(lang.get("lbl_Module_Help"));
					jLabel7.setBounds(0, 317, 104, 21);
					jLabel7.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jButtonHelp = new JButton4j(Common.icon_help);
					jDesktopPane1.add(jButtonHelp);
					jButtonHelp.setText(lang.get("btn_Help"));
					jButtonHelp.setBounds(250, 345, 112, 28);
					jButtonHelp.setMnemonic(lang.getMnemonicChar());
				}
				{
					jTextFieldReportFilename = new JTextField4j();
					jDesktopPane1.add(jTextFieldReportFilename);
					jTextFieldReportFilename.setBounds(110, 233, 163, 22);
					jTextFieldReportFilename.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonUpdate.setEnabled(true);
						}

						public void keyReleased(KeyEvent evt) {
							jButtonIconPreview.setIcon(JDBModule.getModuleIcon(jTextFieldIconFilename.getText(), (String) jComboBoxType.getSelectedItem()));
						}
					});
				}
				{
					jLabel8 = new JLabel4j_std();
					jDesktopPane1.add(jLabel8);
					jLabel8.setText(lang.get("lbl_Module_Icon_Filename"));
					jLabel8.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel8.setBounds(0, 178, 104, 21);
				}
				{
					jButtonReportFileChooser = new JButton4j();
					jDesktopPane1.add(jButtonReportFileChooser);
					jButtonReportFileChooser.setText("..");
					jButtonReportFileChooser.setBounds(273, 233, 17, 21);
					jButtonReportFileChooser.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {

							JFileChooser loadRpt = new JFileChooser();

							try
							{
								String path = "";
								if (((String) comboBox.getSelectedItem()).equals("Standard"))
								{
									path = "./reports";
								}
								else
								{
									path = "./labels";
								}
								File f = new File(new File(path).getCanonicalPath());
								loadRpt.setCurrentDirectory(f);

								if (((String) comboBox.getSelectedItem()).equals("Standard"))
								{
									loadRpt.addChoosableFileFilter(new JFileFilterReports());
								}
								else
								{
									loadRpt.addChoosableFileFilter(new JFileFilterLabels());
								}

								loadRpt.setSelectedFile(new File(jTextFieldReportFilename.getText()));
							}
							catch (Exception e)
							{
							}

							if (loadRpt.showOpenDialog(jButtonReportFileChooser) == JFileChooser.APPROVE_OPTION)
							{
								File selectedFile;
								selectedFile = loadRpt.getSelectedFile();

								if (selectedFile != null)
								{
									if (jTextFieldReportFilename.getText().compareTo(selectedFile.getName()) != 0)
									{
										jTextFieldReportFilename.setText(selectedFile.getName());
										jButtonUpdate.setEnabled(true);
									}
								}
							}
						}
					});
				}
				{
					jTextFieldExecDir = new JTextField4j();
					jDesktopPane1.add(jTextFieldExecDir);
					jTextFieldExecDir.setBounds(110, 289, 427, 22);
					jTextFieldExecDir.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonUpdate.setEnabled(true);
						}

						public void keyReleased(KeyEvent evt) {
							jButtonIconPreview.setIcon(JDBModule.getModuleIcon(jTextFieldIconFilename.getText(), (String) jComboBoxType.getSelectedItem()));
						}
					});
				}
				{
					jTextFieldExecFilename = new JTextField4j();
					jDesktopPane1.add(jTextFieldExecFilename);
					jTextFieldExecFilename.setBounds(110, 261, 427, 22);
					jTextFieldExecFilename.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
							jButtonUpdate.setEnabled(true);
						}

						public void keyReleased(KeyEvent evt) {
							jButtonIconPreview.setIcon(JDBModule.getModuleIcon(jTextFieldIconFilename.getText(), (String) jComboBoxType.getSelectedItem()));
						}
					});
				}
				{
					jLabel9 = new JLabel4j_std();
					jDesktopPane1.add(jLabel9);
					jLabel9.setText(lang.get("lbl_Module_Report_Filename"));
					jLabel9.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel9.setBounds(0, 234, 104, 21);
				}
				{
					jLabel10 = new JLabel4j_std();
					jDesktopPane1.add(jLabel10);
					jLabel10.setText(lang.get("lbl_Module_Executable_Filename"));
					jLabel10.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel10.setBounds(0, 262, 104, 21);
				}
				{
					jButtonExecDirChooser = new JButton4j();
					jDesktopPane1.add(jButtonExecDirChooser);
					jButtonExecDirChooser.setText("..");
					jButtonExecDirChooser.setBounds(537, 289, 17, 21);
					jButtonExecDirChooser.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {

							JFileChooser loadDir = new JFileChooser();

							try
							{
								File f = new File(new File("").getCanonicalPath());
								loadDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
								loadDir.setCurrentDirectory(f);
								loadDir.setSelectedFile(new File(jTextFieldExecDir.getText()));

								if (loadDir.showOpenDialog(jButtonExecDirChooser) == JFileChooser.APPROVE_OPTION)
								{
									File selectedFile;
									selectedFile = loadDir.getSelectedFile();

									if (selectedFile != null)
									{
										if (jTextFieldExecDir.getText().compareTo(selectedFile.getCanonicalPath()) != 0)
										{
											jTextFieldExecDir.setText(selectedFile.getCanonicalPath());
											jButtonUpdate.setEnabled(true);
										}
									}
								}

							}
							catch (Exception e)
							{
							}

						}
					});
				}
				{
					jButtonExecFileChooser = new JButton4j();
					jDesktopPane1.add(jButtonExecFileChooser);
					jButtonExecFileChooser.setText("..");
					jButtonExecFileChooser.setBounds(537, 261, 17, 21);
					jButtonExecFileChooser.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {

							JFileChooser loadExec = new JFileChooser();

							try
							{
								File f = new File(new File("").getCanonicalPath());
								loadExec.setCurrentDirectory(f);
								loadExec.addChoosableFileFilter(new JFileFilterExecs());
								loadExec.setSelectedFile(new File(jTextFieldExecFilename.getText()));

								if (loadExec.showOpenDialog(jButtonExecFileChooser) == JFileChooser.APPROVE_OPTION)
								{
									File selectedFile;
									selectedFile = loadExec.getSelectedFile();

									if (selectedFile != null)
									{
										if (jTextFieldExecFilename.getText().compareTo(selectedFile.getName()) != 0)
										{
											jTextFieldExecFilename.setText(selectedFile.getCanonicalPath());
											jButtonUpdate.setEnabled(true);
										}
									}
								}

							}
							catch (Exception e)
							{
							}

						}
					});
				}

				{
					jButtonExecDirChooser_1 = new JButton4j();
					jButtonExecDirChooser_1.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {

							JFileChooser loadWeb = new JFileChooser();

							try
							{
								File f = new File(new File("./help/html/commander4j").getCanonicalPath());
								loadWeb.setCurrentDirectory(f);

								loadWeb.setSelectedFile(new File(jTextFieldHelpsetid.getText()));

								if (loadWeb.showOpenDialog(jButtonExecFileChooser) == JFileChooser.APPROVE_OPTION)
								{
									File selectedFile;
									selectedFile = loadWeb.getSelectedFile();

									String fullpath = selectedFile.getCanonicalPath();
									fullpath = fullpath.replace(Common.base_dir, "{base_dir}");

									if (selectedFile != null)
									{
										if (jTextFieldHelpsetid.getText().compareTo(selectedFile.getName()) != 0)
										{
											jTextFieldHelpsetid.setText(fullpath);
											jButtonUpdate.setEnabled(true);
										}
									}
								}

							}
							catch (Exception ex)
							{
							}

						}
					});
					jButtonExecDirChooser_1.setText("..");
					jButtonExecDirChooser_1.setBounds(537, 317, 17, 21);
					jDesktopPane1.add(jButtonExecDirChooser_1);
				}

				{
					jCheckBoxPrintPreview = new JCheckBox();
					jCheckBoxPrintPreview.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							jButtonUpdate.setEnabled(true);
						}
					});
					jCheckBoxPrintPreview.setSelected(true);
					jCheckBoxPrintPreview.setBackground(new Color(255, 255, 255));
					jCheckBoxPrintPreview.setText("New JCheckBox");
					jCheckBoxPrintPreview.setBounds(453, 178, 21, 21);
					jDesktopPane1.add(jCheckBoxPrintPreview);
				}

				{
					jCheckBoxPrintDialog = new JCheckBox();
					jCheckBoxPrintDialog.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							jButtonUpdate.setEnabled(true);
						}
					});
					jCheckBoxPrintDialog.setSelected(true);
					jCheckBoxPrintDialog.setBackground(new Color(255, 255, 255));
					jCheckBoxPrintDialog.setText("New JCheckBox");
					jCheckBoxPrintDialog.setBounds(453, 206, 21, 21);
					jDesktopPane1.add(jCheckBoxPrintDialog);
				}

				{
					jLabel3_1 = new JLabel4j_std();
					jLabel3_1.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel3_1.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel3_1.setText(lang.get("lbl_Module_Print_Preview"));
					jLabel3_1.setBounds(308, 178, 141, 21);
					jDesktopPane1.add(jLabel3_1);
				}

				{
					jLabel3_2 = new JLabel4j_std();
					jLabel3_2.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel3_2.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel3_2.setText(lang.get("lbl_Module_Print_Dialog"));
					jLabel3_2.setBounds(302, 206, 147, 21);
					jDesktopPane1.add(jLabel3_2);
				}

				{
					jLabel3_3 = new JLabel4j_std();
					jLabel3_3.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel3_3.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel3_3.setText(lang.get("lbl_Module_Print_Copies"));
					jLabel3_3.setBounds(308, 234, 141, 21);
					jDesktopPane1.add(jLabel3_3);
				}

				{
					jSpinnerPrintCopies = new JSpinner();
					jSpinnerPrintCopies.addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent e) {
							jButtonUpdate.setEnabled(true);
						}
					});
					jSpinnerPrintCopies.setFont(Common.font_std);
					jSpinnerPrintCopies.setBounds(453, 234, 42, 21);
					jDesktopPane1.add(jSpinnerPrintCopies);
				}
			}
			postInitGUI();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void postInitGUI() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle window = getBounds();
		setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);

		reset_changes();

	}
}
