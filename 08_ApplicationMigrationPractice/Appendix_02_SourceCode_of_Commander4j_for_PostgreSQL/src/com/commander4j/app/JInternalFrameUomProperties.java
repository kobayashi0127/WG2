package com.commander4j.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBUom;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JTextField4j;
import com.commander4j.sys.Common;
import com.commander4j.util.JFixedSizeFilter;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JInternalFrameUomProperties extends JInternalFrame
{
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JTextField4j jTextFieldUOM;
	private JLabel4j_std jLabel2;
	private JButton4j jButtonClose;
	private JTextField4j jTextFieldDescription;
	private JLabel4j_std jLabel3;
	private JButton4j jButtonHelp;
	private JButton4j jButtonUpdate;
	private JTextField4j jTextFieldISO_UOM;
	private JLabel4j_std jLabel1;
	private JDBUom uom = new JDBUom(Common.selectedHostID, Common.sessionID);
	private String luomid;
	private JTextField4j jTextFieldLocal_UOM;
	private JLabel4j_std lblLocalUom;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);

	/**
	 * Auto-generated main method to display this JInternalFrame inside a new
	 * JFrame.
	 */

	public JInternalFrameUomProperties()
	{
		super();
		initGUI();
		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_ADMIN_UOM_ADD"));

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jTextFieldISO_UOM.requestFocus();
				jTextFieldISO_UOM.setCaretPosition(jTextFieldISO_UOM.getText().length());
			}
		});
	}

	public JInternalFrameUomProperties(String uomid)
	{

		this();

		jTextFieldUOM.setText(uomid);
		setTitle(getTitle() + " - " + uomid);
		luomid = uomid;

		uom.setInternalUom(luomid);
		uom.getInternalUomProperties();

		jTextFieldUOM.setText(uom.getInternalUom());
		jTextFieldISO_UOM.setText(uom.getIsoUom());
		jTextFieldLocal_UOM.setText(uom.getLocalUom());
		jTextFieldDescription.setText(uom.getDescription());
	}

	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(387, 165));
			this.setBounds(25, 25, 424+Common.LFAdjustWidth, 232+Common.LFAdjustHeight);
			setVisible(true);
			this.setTitle("UOM Properties");
			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				this.getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
				jDesktopPane1.setLayout(null);
				{
					jLabel1 = new JLabel4j_std();
					jDesktopPane1.add(jLabel1);
					jLabel1.setText(lang.get("lbl_UOM_Internal"));
					jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel1.setBounds(0, 10, 149, 21);
				}
				{
					jTextFieldUOM = new JTextField4j();
					jDesktopPane1.add(jTextFieldUOM);
					jTextFieldUOM.setHorizontalAlignment(SwingConstants.LEFT);
					jTextFieldUOM.setEditable(false);
					jTextFieldUOM.setPreferredSize(new java.awt.Dimension(100, 20));
					jTextFieldUOM.setBounds(155, 10, 51, 21);
					jTextFieldUOM.setEnabled(false);
				}
				{
					jLabel2 = new JLabel4j_std();
					jDesktopPane1.add(jLabel2);
					jLabel2.setText(lang.get("lbl_UOM_ISO"));
					jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel2.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel2.setBounds(0, 43, 149, 21);
				}
				{
					jTextFieldISO_UOM = new JTextField4j();
					AbstractDocument doc = (AbstractDocument) jTextFieldISO_UOM.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(JDBUom.field_uom));
					jDesktopPane1.add(jTextFieldISO_UOM);
					jTextFieldISO_UOM.setPreferredSize(new java.awt.Dimension(40, 20));
					jTextFieldISO_UOM.setFocusCycleRoot(true);
					jTextFieldISO_UOM.setBounds(155, 43, 53, 21);
					jTextFieldISO_UOM.addKeyListener(new KeyAdapter() {
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
					jButtonUpdate.setMnemonic(lang.getMnemonicChar());
					jButtonUpdate.setHorizontalTextPosition(SwingConstants.RIGHT);
					jButtonUpdate.setBounds(45, 142, 110, 30);
					jButtonUpdate.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							uom.setIsoUom(jTextFieldISO_UOM.getText().toUpperCase());
							uom.setDescription(jTextFieldDescription.getText().toUpperCase());
							uom.setLocalUom(jTextFieldLocal_UOM.getText().toUpperCase());
							uom.update();
							jButtonUpdate.setEnabled(false);
						}
					});
				}
				{
					jButtonHelp = new JButton4j(Common.icon_help);
					jDesktopPane1.add(jButtonHelp);
					jButtonHelp.setText(lang.get("btn_Help"));
					jButtonHelp.setMnemonic(lang.getMnemonicChar());
					jButtonHelp.setBounds(157, 142, 110, 30);
				}
				{
					jButtonClose = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonClose);
					jButtonClose.setText(lang.get("btn_Close"));
					jButtonClose.setMnemonic(lang.getMnemonicChar());
					jButtonClose.setBounds(269, 142, 110, 30);
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							dispose();
						}
					});
				}
				{
					jLabel3 = new JLabel4j_std();
					jDesktopPane1.add(jLabel3);
					jLabel3.setText(lang.get("lbl_Description"));
					jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel3.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel3.setBounds(0, 109, 149, 21);
				}
				{
					jTextFieldLocal_UOM = new JTextField4j();
					jTextFieldLocal_UOM.addKeyListener(new KeyAdapter() {
						@Override
						public void keyTyped(KeyEvent e) {
							jButtonUpdate.setEnabled(true);
						}
					});
					jTextFieldLocal_UOM.setPreferredSize(new Dimension(40, 20));
					jTextFieldLocal_UOM.setFocusCycleRoot(true);
					jTextFieldLocal_UOM.setCaretPosition(0);
					jTextFieldLocal_UOM.setBounds(155, 76, 53, 21);
					jDesktopPane1.add(jTextFieldLocal_UOM);
				}
				{
					lblLocalUom = new JLabel4j_std();
					lblLocalUom.setText(lang.get("lbl_UOM_Local"));
					lblLocalUom.setHorizontalTextPosition(SwingConstants.RIGHT);
					lblLocalUom.setHorizontalAlignment(SwingConstants.RIGHT);
					lblLocalUom.setBounds(0, 76, 149, 21);
					jDesktopPane1.add(lblLocalUom);
				}

				{
					jTextFieldDescription = new JTextField4j();
					AbstractDocument doc = (AbstractDocument) jTextFieldDescription.getDocument();
					doc.setDocumentFilter(new JFixedSizeFilter(JDBUom.field_description));
					jDesktopPane1.add(jTextFieldDescription);
					jTextFieldDescription.setPreferredSize(new java.awt.Dimension(40, 20));
					jTextFieldDescription.setFocusCycleRoot(true);
					jTextFieldDescription.setBounds(155, 109, 237, 21);
					jTextFieldDescription.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent evt) {
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
