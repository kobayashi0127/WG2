package com.commander4j.sys;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.border.BevelBorder;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBUser;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JLabel4j_title;
import com.commander4j.gui.JList4j;

public class JInternalFrameUserPermissions extends javax.swing.JInternalFrame
{
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JButton4j jButtonAssign;
	private JList4j jListUnassigned;
	private JButton4j jButtonClose;
	private JButton4j jButtonSave;
	private JButton4j jButtonUndo;
	private JList4j jListAssigned;
	private JScrollPane jScrollPaneAssigned;
	private JScrollPane jScrollPaneUnassigned;
	private JButton4j jButtonUnAssign;
	private JLabel4j_title jLabelAvailable;
	private JLabel4j_title jLabelAssigned;
	private LinkedList<String> assignedUserList = new LinkedList<String>();
	private LinkedList<String> unAssignedUserList = new LinkedList<String>();
	private DefaultComboBoxModel assignedModel = new DefaultComboBoxModel();
	private DefaultComboBoxModel unassignedModel = new DefaultComboBoxModel();
	private String lUserId;
	private JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);

	public void setButtonState() {
		jButtonSave.setEnabled(true);
		jButtonUndo.setEnabled(true);

		jButtonUnAssign.setEnabled(false);
		jButtonAssign.setEnabled(false);

		if (unAssignedUserList.size() > 0)
		{
			jButtonAssign.setEnabled(true);
		}

		if (assignedUserList.size() > 0)
		{
			jButtonUnAssign.setEnabled(true);

		}

	}

	public void addToList(LinkedList<String> list, String newValue) {
		list.add(newValue);
		Collections.sort(list);
	}

	public void removeFromList(LinkedList<String> list, String oldValue) {
		list.remove(list.indexOf(oldValue));
		Collections.sort(list);
	}

	public JInternalFrameUserPermissions(String userId)
	{
		super();

		lUserId = userId;
		initGUI();

		resetLists();

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle window = getBounds();
		setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);

	}

	private void resetLists() {
		JDBUser tempUser = new JDBUser(Common.selectedHostID, Common.sessionID);
		tempUser.setUserId(lUserId);

		assignedUserList.clear();
		assignedUserList = tempUser.getUserGroupsAssigned();
		populateAssignedList();

		unAssignedUserList.clear();
		unAssignedUserList = tempUser.getUserGroupsUnAssigned();
		populateUnAssignedList();

		jButtonUndo.setEnabled(false);
		jButtonSave.setEnabled(false);
	}

	private void populateAssignedList() {
		assignedModel.removeAllElements();

		if (assignedUserList.size() > 0)
		{
			for (int j = 0; j < assignedUserList.size(); j++)
			{
				assignedModel.addElement(assignedUserList.get(j));
			}
			jButtonUnAssign.setEnabled(true);
		}
		else
		{
			jButtonUnAssign.setEnabled(false);
		}

		ListModel jList1Model = assignedModel;
		jListAssigned.setModel(jList1Model);

	}

	private void populateUnAssignedList() {
		unassignedModel.removeAllElements();

		if (unAssignedUserList.size() > 0)
		{
			for (int j = 0; j < unAssignedUserList.size(); j++)
			{
				unassignedModel.addElement(unAssignedUserList.get(j));
			}
			jButtonAssign.setEnabled(true);
		}
		else
		{
			jButtonAssign.setEnabled(false);
		}

		ListModel jList1Model = unassignedModel;
		jListUnassigned.setModel(jList1Model);

	}

	private void initGUI() {
		try
		{

			this.setPreferredSize(new java.awt.Dimension(346, 318));
			this.setBounds(0, 0, 420+Common.LFAdjustWidth, 443+Common.LFAdjustHeight);

			setVisible(true);
			this.setTitle("Permissions - " + lUserId);
			this.setClosable(true);
			this.setIconifiable(true);

			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Color.WHITE);
				this.getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
				jDesktopPane1.setPreferredSize(new java.awt.Dimension(336, 285));
				jDesktopPane1.setLayout(null);
				{
					jLabelAssigned = new JLabel4j_title();
					jDesktopPane1.add(jLabelAssigned);
					jLabelAssigned.setText(lang.get("lbl_Assigned"));
					jLabelAssigned.setBounds(10, 12, 161, 17);
				}
				{
					jLabelAvailable = new JLabel4j_title();
					jDesktopPane1.add(jLabelAvailable);
					jLabelAvailable.setText(lang.get("lbl_Unassigned"));
					jLabelAvailable.setBounds(216, 11, 166, 18);
				}
				{
					jButtonAssign = new JButton4j(Common.icon_arrow_left);
					jDesktopPane1.add(jButtonAssign);
					jButtonAssign.setBounds(183, 139, 26, 24);
					jButtonAssign.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							if (jListUnassigned.getSelectedIndex() > -1)
							{
								for (int j = jListUnassigned.getMaxSelectionIndex(); j >= jListUnassigned.getMinSelectionIndex(); j--)
								{
									if (jListUnassigned.isSelectedIndex(j))
									{
										String item = (String) jListUnassigned.getModel().getElementAt(j);
										addToList(assignedUserList, item);
										removeFromList(unAssignedUserList, item);
									}
								}
								populateAssignedList();
								populateUnAssignedList();
								setButtonState();
							}
						}
					});
				}
				{
					jButtonUnAssign = new JButton4j(Common.icon_arrow_right);
					jDesktopPane1.add(jButtonUnAssign);
					jButtonUnAssign.setBounds(183, 171, 26, 23);
					jButtonUnAssign.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							if (jListAssigned.getSelectedIndex() > -1)
							{
								for (int j = jListAssigned.getMaxSelectionIndex(); j >= jListAssigned.getMinSelectionIndex(); j--)
								{
									if (jListAssigned.isSelectedIndex(j))
									{
										String item = (String) jListAssigned.getModel().getElementAt(j);
										addToList(unAssignedUserList, item);
										removeFromList(assignedUserList, item);
									}
								}
								populateAssignedList();
								populateUnAssignedList();
								setButtonState();
							}
						}
					});
				}
				{
					jScrollPaneUnassigned = new JScrollPane();
					jDesktopPane1.add(jScrollPaneUnassigned);
					jScrollPaneUnassigned.setBounds(221, 30, 161, 315);
					jScrollPaneUnassigned.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
					jScrollPaneUnassigned.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
					{
						ListModel jListUnassignedModel = new DefaultComboBoxModel(new String[] { "Item One", "Item Two" });
						jListUnassigned = new JList4j();
						jScrollPaneUnassigned.setViewportView(jListUnassigned);
						jListUnassigned.setModel(jListUnassignedModel);
						jListUnassigned.setCellRenderer(Common.renderer_list_unassigned);
						jListUnassigned.setBackground(Common.color_list_unassigned);
					}
				}
				{
					jScrollPaneAssigned = new JScrollPane();
					jDesktopPane1.add(jScrollPaneAssigned);
					jScrollPaneAssigned.setBounds(10, 30, 161, 315);
					jScrollPaneAssigned.setFocusable(false);
					jScrollPaneAssigned.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
					jScrollPaneAssigned.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
					{
						ListModel jListAssignedModel = new DefaultComboBoxModel(new String[] { "Item One", "Item Two" });
						jListAssigned = new JList4j();
						jListAssigned.setSize(157, 315);
						jScrollPaneAssigned.setViewportView(jListAssigned);
						jListAssigned.setModel(jListAssignedModel);
						jListAssigned.setForeground(Common.color_listFontStandard);
						jListAssigned.setCellRenderer(Common.renderer_list_assigned);
						jListAssigned.setBackground(Common.color_list_assigned);
					}
				}
				{

					jButtonUndo = new JButton4j(Common.icon_undo);
					jDesktopPane1.add(jButtonUndo);
					jButtonUndo.setText(lang.get("btn_Undo"));
					jButtonUndo.setBounds(42, 357, 105, 30);
					jButtonUndo.setMnemonic(lang.getMnemonicChar());
					jButtonUndo.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							resetLists();
						}
					});
				}
				{
					jButtonSave = new JButton4j(Common.icon_update);
					jDesktopPane1.add(jButtonSave);
					jButtonSave.setText(lang.get("btn_Save"));
					jButtonSave.setBounds(150, 357, 105, 30);
					jButtonSave.setMnemonic(lang.getMnemonicChar());
					jButtonSave.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							JDBUser u = new JDBUser(Common.selectedHostID, Common.sessionID);

							u.setUserId(lUserId);
							for (int j = 0; j < unAssignedUserList.size(); j++)
							{
								u.removefromGroup(unAssignedUserList.get(j).toString());
							}
							for (int j = 0; j < assignedUserList.size(); j++)
							{
								u.addtoGroup(assignedUserList.get(j).toString());
							}
							jButtonUndo.setEnabled(false);
							jButtonSave.setEnabled(false);
						}
					});
				}
				{
					jButtonClose = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonClose);
					jButtonClose.setText(lang.get("btn_Close"));
					jButtonClose.setBounds(257, 357, 105, 30);
					jButtonClose.setMnemonic(lang.getMnemonicChar());
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							dispose();
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
