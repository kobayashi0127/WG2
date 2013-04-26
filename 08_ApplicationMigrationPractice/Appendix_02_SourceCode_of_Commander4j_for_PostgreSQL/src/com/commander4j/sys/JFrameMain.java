package com.commander4j.sys;

/**
 * @author David Garratt
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultDesktopManager;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import com.commander4j.app.JVersion;
import com.commander4j.db.JDBListData;
import com.commander4j.db.JDBModule;
import com.commander4j.util.JHelp;

// import com.commander4j.util.JUtility;

public class JFrameMain extends JFrame implements ComponentListener
{
	private static final long serialVersionUID = 1;
	protected JDesktopPane desktopPane = new JDesktopPane();
	private Container contentPane = getContentPane();
	private JButton btnHome = new JButton(Common.icon_home);
	private JButton btnExit = new JButton(Common.icon_close);
	private JButton btnHelp = new JButton(Common.icon_help);
	private JButton btnCascade = new JButton(Common.icon_cascade);
	private JButton btnMinimize = new JButton(Common.icon_minimize);
	private JButton btnRestore = new JButton(Common.icon_restore);
	private JButton btnExecute = new JButton(Common.icon_execute);
	private JMenuBar menuBar = new JMenuBar();
	private JToolBar jtb = new JToolBar();
	private JMenuToolbarMenu tbm = new JMenuToolbarMenu(Common.selectedHostID, Common.sessionID);
	private JStatusBar jsb = new JStatusBar();
	private JComboBox jcb = new JComboBox();
	private JMenu mFile = new JMenu("File");
	private JMenu mWindow = new JMenu("Window");
	private JMenu mView = new JMenu("View");
	private JMenu mHelp = new JMenu("Help");
	private JMenuItem mExit = new JMenuItem("Exit");
	private JMenuItem mCascade = new JMenuItem("Cascade");
	private JMenuItem mMinimize = new JMenuItem("Minimize");
	private JMenuItem mRestore = new JMenuItem("Restore");
	private JMenuItem mMenu = new JMenuItem("Menu");
	private JMenuItem mHelpContents = new JMenuItem("Contents");
	private JMenuItem mHelpAbout = new JMenuItem("About");
	private JMenuItem mHelpSystemProperties = new JMenuItem("System Info");
	protected JInternalFrameMenuTree treeMenu;
	private DefaultComboBoxModel defComboBoxMod = new DefaultComboBoxModel();
	private JDBModule tempModule = new JDBModule(Common.selectedHostID, Common.sessionID);
	private JMenuOption mo = new JMenuOption(Common.selectedHostID, Common.sessionID);
	private ComboBoxModel comboModel = defComboBoxMod;

	// Class added to reposition minimised windows.
	class AppDesktopManager extends DefaultDesktopManager
	{
		private static final long serialVersionUID = 1;

		public void reIconifyFrame(JInternalFrame jif) {
			super.deiconifyFrame(jif);
			Rectangle rect = getBoundsForIconOf(jif);
			super.iconifyFrame(jif);
			jif.getDesktopIcon().setBounds(rect);
		}
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
		// Next Line added to resize menu
		setTreeSize();

		// Remaining lines added to reposition minimised windows.
		JDesktopPane jdpPane = (JDesktopPane) e.getComponent();
		AppDesktopManager dm = (AppDesktopManager) jdpPane.getDesktopManager();
		JInternalFrame[] jifs = jdpPane.getAllFrames();
		for (int i = 0; i < jifs.length; i++)
		{
			if (jifs[i].isIcon())
			{
				dm.reIconifyFrame(jifs[i]);
			}
		}
	}

	public void componentShown(ComponentEvent e) {
	}

	public void setTreeSize() {
		int Height;
		Height = this.getHeight() - 150;
		if (Height < 100)
			Height = 100;
		treeMenu.setBounds(0, 0, Common.menuTreeWidth+Common.LFTreeMenuAdjustWidth, Height);
	}

	private static void ConfirmExit() {
		int question = JOptionPane.showConfirmDialog(Common.mainForm, "Exit application ?", "Confirm", JOptionPane.YES_NO_OPTION);
		if (question == 0)
		{

			Common.hostList.getHost(Common.selectedHostID).disconnect(Common.sessionID);
			System.exit(0);
		}
	}

	static class WindowListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent e) {
			ConfirmExit();
		}
	}

	public JFrameMain()
	{

		super("Commander4j " + JVersion.getProgramVersion() + " (" + Common.hostList.getHost(Common.selectedHostID).getSiteDescription() + ")");

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize.width, screenSize.height - 50);
		setLocation(0, 0);
		setResizable(true);
		
		setExtendedState(Frame.MAXIMIZED_HORIZ);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowListener());

		desktopPane.setDesktopManager(new AppDesktopManager());
		desktopPane.setBackground(Color.WHITE);
		contentPane.add(desktopPane, BorderLayout.CENTER);
		ButtonHandler buttonhandler = new ButtonHandler();

		menuBar.setFont(Common.font_std);
		setJMenuBar(menuBar);

		mFile.setFont(Common.font_menu);
		mFile.setMnemonic(java.awt.event.KeyEvent.VK_F);

		mExit.setFont(Common.font_menu);
		mExit.setIcon(Common.imageIconloader.getImageIcon(Common.image_close));
		mExit.addActionListener(buttonhandler);

		mWindow.setFont(Common.font_menu);
		mWindow.setMnemonic(java.awt.event.KeyEvent.VK_W);

		mCascade.setFont(Common.font_menu);
		mCascade.addActionListener(buttonhandler);

		mMinimize.setFont(Common.font_menu);
		mMinimize.addActionListener(buttonhandler);

		mRestore.setFont(Common.font_menu);
		mRestore.addActionListener(buttonhandler);

		mView.setFont(Common.font_menu);
		mView.setMnemonic(java.awt.event.KeyEvent.VK_V);

		mMenu.setFont(Common.font_menu);
		mMenu.setIcon(Common.imageIconloader.getImageIcon(Common.image_home));
		mMenu.addActionListener(buttonhandler);

		mHelp.setFont(Common.font_menu);
		mHelp.setMnemonic(java.awt.event.KeyEvent.VK_H);

		mHelpContents.setFont(Common.font_menu);
		mHelpContents.setMnemonic(java.awt.event.KeyEvent.VK_C);
		mHelpContents.addActionListener(buttonhandler);

		mHelpAbout.setFont(Common.font_menu);
		mHelpAbout.setMnemonic(java.awt.event.KeyEvent.VK_A);
		mHelpAbout.addActionListener(buttonhandler);

		mHelpSystemProperties.setFont(Common.font_menu);
		mHelpSystemProperties.setMnemonic(java.awt.event.KeyEvent.VK_S);
		mHelpSystemProperties.addActionListener(buttonhandler);

		mFile.add(mExit);
		mView.add(mMenu);

		mWindow.add(mCascade);
		mWindow.add(mMinimize);
		mWindow.add(mRestore);

		mHelp.add(mHelpContents);
		mHelp.add(mHelpSystemProperties);
		mHelp.add(mHelpAbout);

		jtb.setOrientation(0);
		jtb.setSize(jtb.getSize().width, 40);
		jtb.setPreferredSize(new Dimension(jtb.getSize().width, 40));
		jtb.setFloatable(false);

		jcb.setMaximumSize(new Dimension(300, 26));
		jcb.setMaximumRowCount(30);
		jcb.setToolTipText("Quick Launch Menu");

		contentPane.add(jtb, BorderLayout.NORTH);
		contentPane.add(jsb, BorderLayout.SOUTH);

		btnHome.addActionListener(buttonhandler);
		btnHome.setToolTipText("Display Menu Tree");
		btnHome.setSize(30,30);
		btnHome.setMaximumSize(new Dimension(30,30));
		btnHome.setMinimumSize(new Dimension(30,30));
		btnHome.setPreferredSize(new Dimension(30,30));

		btnMinimize.addActionListener(buttonhandler);
		btnMinimize.setToolTipText("Minimize open windows.");
		btnHome.setSize(30,30);
		btnMinimize.setMaximumSize(new Dimension(30,30));
		btnMinimize.setMinimumSize(new Dimension(30,30));
		btnMinimize.setPreferredSize(new Dimension(30,30));

		btnCascade.addActionListener(buttonhandler);
		btnCascade.setToolTipText("Cascade open windows.");
		btnCascade.setSize(30,30);
		btnCascade.setMaximumSize(new Dimension(30,30));
		btnCascade.setMinimumSize(new Dimension(30,30));
		btnCascade.setPreferredSize(new Dimension(30,30));

		btnRestore.addActionListener(buttonhandler);
		btnRestore.setToolTipText("Restore iconified windows.");
		btnRestore.setSize(30,30);
		btnRestore.setMaximumSize(new Dimension(30,30));
		btnRestore.setMinimumSize(new Dimension(30,30));
		btnRestore.setPreferredSize(new Dimension(30,30));

		btnHelp.addActionListener(buttonhandler);
		btnHelp.setToolTipText("Help");
		btnHelp.setSize(30,30);
		btnHelp.setMaximumSize(new Dimension(30,30));
		btnHelp.setMinimumSize(new Dimension(30,30));
		btnHelp.setPreferredSize(new Dimension(30,30));

		btnExit.addActionListener(buttonhandler);
		btnExit.setToolTipText("Exit application");
		btnExit.setSize(30,30);
		btnExit.setMaximumSize(new Dimension(30,30));
		btnExit.setMinimumSize(new Dimension(30,30));
		btnExit.setPreferredSize(new Dimension(30,30));

		btnExecute.addActionListener(buttonhandler);
		btnExecute.setToolTipText("Execute Quick Menu Option.");
		btnExecute.setSize(30,30);
		btnExecute.setMaximumSize(new Dimension(30,30));
		btnExecute.setMinimumSize(new Dimension(30,30));
		btnExecute.setPreferredSize(new Dimension(30,30));

		// **************** SECURITY **********************************

		menuBar.add(mFile);
		menuBar.add(mView);

		treeMenu = new JInternalFrameMenuTree("root", "Menu", true, false, false, true, menuBar, mView);

		menuBar.add(mWindow);
		menuBar.add(mHelp);

		jtb.add(btnHome);
		jtb.add(new JToolBar.Separator());
		tbm.buildMenu(this.jtb);
		jtb.add(new JToolBar.Separator());
		jtb.add(btnMinimize);
		jtb.add(btnCascade);
		jtb.add(btnRestore);
		jtb.add(btnHelp);
		jtb.add(new JToolBar.Separator());

		LinkedList<JDBListData> tempModuleList = tempModule.getModulesofTypeforUser(Common.selectedHostID, Common.sessionID, "FORM");

		mo.clear();
		defComboBoxMod.removeAllElements();
		defComboBoxMod.addElement(mo);
		for (int j = 0; j < tempModuleList.size(); j++)
		{
			defComboBoxMod.addElement(tempModuleList.get(j));
		}

		tempModuleList = tempModule.getModulesofTypeforUser(Common.selectedHostID, Common.sessionID, "EXEC");

		if (tempModuleList.isEmpty() == false)
		{
			defComboBoxMod.addElement("SEPARATOR");
			for (int j = 0; j < tempModuleList.size(); j++)
			{
				defComboBoxMod.addElement(tempModuleList.get(j));
			}
		}

		jcb.setModel(comboModel);
		jcb.setRenderer(Common.renderer_list);

		jtb.add(jcb);
		jtb.add(btnExecute);
		jtb.add(new JToolBar.Separator());
		jtb.add(btnExit);

		// ***************************************************

		desktopPane.add(treeMenu);

		treeMenu.setVisible(true);
		setTreeSize();

		desktopPane.addComponentListener(this);

		final JHelp help1 = new JHelp();
		help1.enableHelpOnButton(btnHelp, Common.helpURL);

		final JHelp help2 = new JHelp();
		help2.enableHelpOnMenuItem(mHelpContents, Common.helpURL);

	}

	public class ButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == btnHome | event.getSource() == mMenu)
			{
				if (treeMenu.isIcon())
				{
					treeMenu.setVisible(true);

					try
					{
						treeMenu.setIcon(false);
					}
					catch (Exception ex)
					{
						System.out.println("Cannot setIcon on treeMenu");
					}

					try
					{
						treeMenu.setSelected(true);
					}
					catch (Exception ex)
					{
						System.out.println("Cannot setSelected on treeMenu");
					}
				}
				else
				{
					try
					{
						treeMenu.setIcon(true);
					}
					catch (Exception ex)
					{
						System.out.println("Cannot setIcon on treeMenu");
					}
				}
			}

			if (event.getSource() == mHelpAbout)
			{
				JLaunchMenu.runDialog("FRM_ABOUT");
			}

			if (event.getSource() == mHelpSystemProperties)
			{
				JLaunchMenu.runDialog("FRM_SYSTEM_PROPERTIES");
			}

			if (event.getSource() == mCascade)
			{
				JLaunchMenu.cascadeFrames();
			}
			if (event.getSource() == mMinimize)
			{
				JLaunchMenu.minimizeAll();
			}
			if (event.getSource() == mRestore)
			{
				JLaunchMenu.restoreAll();
			}
			if (event.getSource() == btnCascade)
			{
				JLaunchMenu.cascadeFrames();
			}
			if (event.getSource() == btnRestore)
			{
				JLaunchMenu.restoreAll();
			}
			if (event.getSource() == btnMinimize)
			{
				JLaunchMenu.minimizeAll();
			}
			if (event.getSource() == btnExecute)
			{
				try
				{
					JDBListData ld = (JDBListData) jcb.getSelectedItem();
					JMenuOption mo = ((JMenuOption) ld.getObject());
					String x = mo.moduleID;
					if (mo.moduleType.equals("FORM"))
					{
						JLaunchMenu.runForm(x);
					}
					if (mo.moduleType.equals("REPORT"))
					{
						JLaunchReport.runReport(x,null,"",null,"");
					}
					if (mo.moduleType.equals("EXEC"))
					{
						JLaunchExec.runExec(Common.selectedHostID, Common.sessionID, x);
					}
				}
				catch (Exception ex)
				{

				}
			}
			if (event.getSource() == btnExit | event.getSource() == mExit)
			{
				ConfirmExit();
			}
		}
	}
}
