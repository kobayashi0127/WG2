package com.commander4j.cfg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.commander4j.app.JVersion;
import com.commander4j.db.JDBControl;
import com.commander4j.db.JDBDDL;
import com.commander4j.db.JDBSchema;
import com.commander4j.db.JDBStructure;
import com.commander4j.db.JDBUpdateRequest;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JComboBox4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JList4j;
import com.commander4j.gui.JTextField4j;
import com.commander4j.sys.Common;
import com.commander4j.sys.JHost;
import com.commander4j.util.JFileIO;
import com.commander4j.util.JUnique;
import com.commander4j.util.JUtility;
import com.commander4j.xml.JXMLHost;
import com.commander4j.xml.JXMLSchema;
import java.awt.Font;

public class JFrameHostAdmin extends JFrame
{
	private JTextField4j jTextFieldURL;
	private static final long serialVersionUID = 1;
	private JDesktopPane desktopPane = new JDesktopPane();
	private JButton4j jButtonApply;
	private JButton4j jButtonDelete;
	private JButton4j jButtonUp;
	private JButton4j jButtonClose;
	private JTextField4j jTextFieldSchema;
	private JLabel4j_std jLabel28;
	private JLabel4j_std jLabel26;
	private JTextField4j jTextFieldSelectLimit;
	private JButton4j jButtonUpdate;
	private JButton4j jButtonTest;
	private JLabel4j_std jLabel24;
	private JTextField4j jTextFieldDatabase;
	private JTextField4j jTextFieldSID;
	private JCheckBox jCheckBoxEnabled;
	private JCheckBox jCheckBoxSplash;
	private JTextField4j jTextFieldDriver;
	private JTextField4j jTextFieldSiteNo;
	private JTextField4j jTextFieldPort;
	private JPasswordField jTextFieldPassword;
	private JTextField4j jTextFieldUsername;
	private JTextField4j jTextFieldDateTime;
	private JLabel4j_std jLabel19;
	private JLabel4j_std jLabel17;
	private JLabel4j_std jLabel15;
	private JLabel4j_std jLabel13;
	private JLabel4j_std jLabel11;
	private JLabel4j_std jLabel9;
	private JLabel4j_std jLabel4;
	private JTextField4j jTextFieldServer;
	private JButton4j jButtonCancel;
	private JLabel4j_std jLabel21;
	private JLabel4j_std jLabel7;
	private JLabel4j_std jLabel1;
	private JTextField4j jTextFieldConnect;
	private JComboBox4j jComboBoxjdbcDriver;
	private JLabel4j_std jLabel2;
	private JTextField4j jTextFieldDescription;
	private JButton4j jButtonSave;
	private JButton4j jButtonUndo;
	private JButton4j jButtonDown;
	private JButton4j jButtonAdd;
	private JButton4j btnSchema;
	private JList4j jListHosts;
	private JScrollPane jScrollPane1;
	private LinkedList<JHost> hostList = new LinkedList<JHost>();
	final Logger logger = Logger.getLogger(JFrameHostAdmin.class);
	private JFrameHostAdmin me;
	private JPanel contentPane;
	private JLabel4j_std jLabel13_2;
	private JTextField4j jTextFieldUniqueID;
	private JLabel4j_std jLabel13_1;
	private JProgressBar progressBar = new JProgressBar();
	private JLabel4j_std labelCommand = new JLabel4j_std("");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					JFrameHostAdmin frame = new JFrameHostAdmin();
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public JFrameHostAdmin() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 937, 511);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		desktopPane.setBounds(0, 0, 937, 491);
		desktopPane.setBackground(Color.WHITE);
		contentPane.add(desktopPane);

		Common.sessionID = JUnique.getUniqueID();
		Common.sd.setData(Common.sessionID, "silentExceptions", "No", true);
		JUtility.initLogging("");
		logger.debug("JFrameHostAdmin starting...");

		initGUI();

		setIconImage(Common.imageIconloader.getImageIcon(Common.image_barcode).getImage());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - getSize().width) / 2, (screenSize.height - getSize().height) / 2);

		getHosts();
		populateList("");
	}

	private void jTextFieldKeyTyped()
	{
		jButtonApply.setEnabled(true);
		jButtonCancel.setEnabled(true);
		jButtonTest.setEnabled(false);
		jButtonUpdate.setEnabled(false);
	}

	private void setSaveButtonState(boolean enabled)
	{
		jButtonSave.setEnabled(enabled);
		jButtonUndo.setEnabled(enabled);
	}

	private void getHosts()
	{
		setEditable(false);
		hostList.clear();
		Common.hostList.loadHosts();
		hostList = Common.hostList.getHosts();
		jCheckBoxSplash.setSelected(Common.displaySplashScreen);
		setButtonState();
	}

	private void renumberHosts()
	{
		for (int j = 0; j < hostList.size(); j++)
		{
			hostList.get(j).setSiteNumber(String.valueOf(j + 1));
		}
	}

	private void populateList(String defaultitem)
	{
		setButtonState();
		int sel = 0;

		DefaultComboBoxModel DefComboBoxMod = new DefaultComboBoxModel();

		for (int j = 0; j < hostList.size(); j++)
		{
			DefComboBoxMod.addElement(hostList.get(j));
			if (hostList.get(j).getSiteDescription().equals(defaultitem))
			{
				sel = j;
			}
		}

		ListModel jList1Model = DefComboBoxMod;
		jListHosts.setModel(jList1Model);
		jListHosts.setCellRenderer(Common.renderer_list);
		jListHosts.setSelectedIndex(sel);
	}

	private void writeBackHosts(LinkedList<JHost> hsts)
	{
		Common.hostList.disconnectAll();
		Common.hostList.clear();
		Common.hostList.addHosts(hsts);

	}

	private void setButtonState()
	{
		if (hostList.size() > 0)
		{
			jButtonUp.setEnabled(true);
			jButtonDown.setEnabled(true);
		}
		else
		{
			jButtonUp.setEnabled(false);
			jButtonDown.setEnabled(false);
		}
	}

	private void getHostData()
	{
		int j = jListHosts.getSelectedIndex();
		if (j >= 0)
		{
			JHost hst = new JHost();
			hst = (JHost) jListHosts.getModel().getElementAt(j);
			jTextFieldDescription.setText(hst.getSiteDescription());
			jTextFieldUniqueID.setText(hst.getUniqueID());
			jTextFieldURL.setText(hst.getSiteURL());
			jTextFieldSiteNo.setText(hst.getSiteNumber());
			jTextFieldDriver.setText(hst.getDatabaseParameters().getjdbcDriver());
			if (hst.getDatabaseParameters().getjdbcDriver().equals(""))
			{
				jComboBoxjdbcDriver.setSelectedIndex(0);
			}

			if (hst.getDatabaseParameters().getjdbcDriver().equals("com.mysql.jdbc.Driver"))
			{
				jComboBoxjdbcDriver.setSelectedIndex(1);
			}
			if (hst.getDatabaseParameters().getjdbcDriver().equals("oracle.jdbc.driver.OracleDriver"))
			{
				jComboBoxjdbcDriver.setSelectedIndex(2);
			}
			if (hst.getDatabaseParameters().getjdbcDriver().equals("com.microsoft.sqlserver.jdbc.SQLServerDriver"))
			{
				jComboBoxjdbcDriver.setSelectedIndex(3);
			}
			if (hst.getDatabaseParameters().getjdbcDriver().equals("http"))
			{
				jComboBoxjdbcDriver.setSelectedIndex(4);
			}
			//PG add ->
			if (hst.getDatabaseParameters().getjdbcDriver().equals("org.postgresql.Driver"))
			{
				jComboBoxjdbcDriver.setSelectedIndex(5);
			}
			//PG add end

			jTextFieldConnect.setText(hst.getDatabaseParameters().getjdbcConnectString());
			jTextFieldDateTime.setText(hst.getDatabaseParameters().getjdbcDatabaseDateTimeToken());
			jTextFieldSelectLimit.setText(hst.getDatabaseParameters().getjdbcDatabaseSelectLimit());
			jTextFieldSchema.setText(hst.getDatabaseParameters().getjdbcDatabaseSchema());
			jTextFieldUsername.setText(hst.getDatabaseParameters().getjdbcUsername());
			jTextFieldPassword.setText(hst.getDatabaseParameters().getjdbcPassword());
			jTextFieldPort.setText(hst.getDatabaseParameters().getjdbcPort());
			jTextFieldSID.setText(hst.getDatabaseParameters().getjdbcSID());
			jTextFieldServer.setText(hst.getDatabaseParameters().getjdbcServer());
			jTextFieldDatabase.setText(hst.getDatabaseParameters().getjdbcDatabase());
			if (hst.getEnabled().equals("Y"))
				jCheckBoxEnabled.setSelected(true);
			else
				jCheckBoxEnabled.setSelected(false);

			setEditable(true);
			jButtonApply.setEnabled(false);
			jButtonCancel.setEnabled(false);
			jButtonTest.setEnabled(true);
			jButtonUpdate.setEnabled(false);
			btnSchema.setEnabled(false);

		}
		else
		{
			jTextFieldDescription.setText("");
			jTextFieldURL.setText("");
			jTextFieldSiteNo.setText("");
			jTextFieldDriver.setText("");
			jTextFieldConnect.setText("");
			jTextFieldDateTime.setText("");
			jTextFieldSelectLimit.setText("");
			jTextFieldUsername.setText("");
			jTextFieldPassword.setText("");
			jTextFieldPort.setText("");
			jTextFieldSID.setText("");
			jTextFieldServer.setText("");
			jTextFieldDatabase.setText("");
			jCheckBoxEnabled.setSelected(false);
			setEditable(false);
			// jButtonTest.setEnabled(false);
		}
	}

	private JHost setHostData()
	{
		int j = jListHosts.getSelectedIndex();
		JHost hst = new JHost();
		if (j > -1)
		{

			hst.setSiteDescription(jTextFieldDescription.getText());
			hst.setSiteURL(jTextFieldURL.getText());
			hst.setSiteNumber(jTextFieldSiteNo.getText());
			hst.getDatabaseParameters().setjdbcDriver(jTextFieldDriver.getText());
			hst.getDatabaseParameters().setjdbcDatabaseDateTimeToken(jTextFieldDateTime.getText());
			hst.getDatabaseParameters().setjdbcDatabaseSelectLimit(jTextFieldSelectLimit.getText());
			hst.getDatabaseParameters().setjdbcDatabaseSchema(jTextFieldSchema.getText());
			hst.getDatabaseParameters().setjdbcUsername(jTextFieldUsername.getText());
			hst.getDatabaseParameters().setjdbcPassword(String.valueOf(jTextFieldPassword.getPassword()));

			hst.getDatabaseParameters().setjdbcPort(jTextFieldPort.getText());
			hst.getDatabaseParameters().setjdbcSID(jTextFieldSID.getText());
			hst.getDatabaseParameters().setjdbcServer(jTextFieldServer.getText());
			hst.getDatabaseParameters().setjdbcDatabase(jTextFieldDatabase.getText());
			hst.setUniqueID(jTextFieldUniqueID.getText());
			hst.getSqlstatements().setjdbcDriver(hst.getDatabaseParameters().getjdbcDriver());
			hst.getSqlstatements().setjdbcDriver(hst.getDatabaseParameters().getjdbcDriver());

			if (jCheckBoxEnabled.isSelected())
				hst.setEnabled("Y");
			else
				hst.setEnabled("N");
			hostList.set(j, hst);
			setEditable(false);
			populateList("");
			jButtonApply.setEnabled(false);
			jButtonCancel.setEnabled(false);
		}
		return hst;
	}

	public static LinkedList<JHost> moveElementDown(LinkedList<JHost> list, JHost element)
	{
		int position;
		int size;

		size = list.size();

		if (size > 0)
		{
			position = list.indexOf(element);
			if (position < (size - 1))
			{
				list.remove(position);
				list.add(position + 1, element);

			}
		}
		return list;
	}

	public static LinkedList<JHost> moveElementUp(LinkedList<JHost> list, JHost element)
	{
		int position;
		int size;

		size = list.size();

		if (size > 0)
		{
			position = list.indexOf(element);
			if (position > 0)
			{
				list.remove(position);
				list.add(position - 1, element);
			}
		}
		return list;
	}

	private void setEditable(boolean edit)
	{
		jTextFieldDescription.setEnabled(edit);
		jTextFieldURL.setEnabled(edit);
		jTextFieldSiteNo.setEnabled(edit);
		// jTextFieldDriver.setEnabled(edit);
		// jTextFieldConnect.setEnabled(edit);
		jTextFieldDateTime.setEnabled(edit);
		jTextFieldSelectLimit.setEnabled(edit);
		jTextFieldSchema.setEnabled(edit);
		jTextFieldUsername.setEnabled(edit);
		jTextFieldPassword.setEnabled(edit);
		jTextFieldPort.setEnabled(edit);
		jTextFieldSID.setEnabled(edit);
		jTextFieldServer.setEnabled(edit);
		jTextFieldDatabase.setEnabled(edit);
		jCheckBoxEnabled.setEnabled(edit);
		jComboBoxjdbcDriver.setEnabled(edit);

	}

	private void initGUI()
	{
		try
		{
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setTitle("Host Administration " + JVersion.getProgramVersion());
			desktopPane.setLayout(null);
			// this.setResizable(false);
			{
				{
					jScrollPane1 = new JScrollPane();
					desktopPane.add(jScrollPane1);
					jScrollPane1.setBounds(14, 37, 259, 377);
					{
						ListModel jListHostsModel = new DefaultComboBoxModel(new String[] { "Item One", "Item Two" });
						jListHosts = new JList4j();
						jScrollPane1.setViewportView(jListHosts);
						jListHosts.setModel(jListHostsModel);
						jListHosts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						jListHosts.addListSelectionListener(new ListSelectionListener()
						{
							public void valueChanged(ListSelectionEvent evt)
							{
								getHostData();
							}
						});
					}
				}
				{
					jButtonAdd = new JButton4j(Common.icon_add);
					desktopPane.add(jButtonAdd);
					jButtonAdd.setText("Add DB Connection");
					jButtonAdd.setBounds(762, 12, 160, 36);
					jButtonAdd.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{

							// Generate Next Site No //
							String siteNo = "";
							int siteNoVal = 0;
							for (int j = 0; j < hostList.size(); j++)
							{
								siteNo = hostList.get(j).getSiteNumber();
								int x = Integer.valueOf(siteNo);
								if (x > siteNoVal)
								{
									siteNoVal = x;
								}
							}
							siteNoVal++;
							siteNo = String.valueOf(siteNoVal);

							// Generate unique description //
							int i = 0;
							// int j = 0;
							boolean found = false;
							String newdesc = "";
							do
							{
								i++;
								newdesc = "New[" + String.valueOf(i) + "]";
								found = false;
								for (int y = 0; y < hostList.size(); y++)
								{
									if (hostList.get(y).getSiteDescription().equals(newdesc))
									{
										found = true;
									}
								}

							}
							while (found);

							JHost hst = new JHost();
							hst.setSiteNumber(siteNo);
							hst.setSiteDescription(newdesc);

							hostList.add(hst);
							setSaveButtonState(true);

							populateList(newdesc);
							getHostData();
						}
					});
				}
				{
					jButtonApply = new JButton4j(Common.icon_ok);
					desktopPane.add(jButtonApply);
					jButtonApply.setText("Confirm Changes");
					jButtonApply.setBounds(762, 160, 160, 36);
					jButtonApply.setEnabled(false);
					jButtonApply.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{
							int j = jListHosts.getSelectedIndex();
							JHost hst = setHostData();

							hostList.set(j, hst);
							setEditable(false);
							populateList("");
							jButtonApply.setEnabled(false);
							jButtonCancel.setEnabled(false);

							setSaveButtonState(true);
							jButtonTest.setEnabled(true);
							jListHosts.setSelectedIndex(j);
						}
					});
				}
				{
					jButtonDelete = new JButton4j(Common.icon_delete);
					desktopPane.add(jButtonDelete);
					jButtonDelete.setText("Delete DB Connection");
					jButtonDelete.setBounds(762, 49, 160, 36);
					jButtonDelete.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{
							if (jListHosts.getSelectedIndex() > -1)
							{
								int j = jListHosts.getSelectedIndex();
								hostList.remove(j);
								renumberHosts();
								populateList("");
								if (hostList.size() > 0)
								{
									if (j > (hostList.size() - 1))
									{
										j--;
									}
									if (j >= 0)
									{
										populateList(hostList.get(j).getSiteDescription());
									}
								}

								getHostData();
								setSaveButtonState(true);
							}
						}
					});
				}
				{
					jButtonUp = new JButton4j(Common.icon_arrow_up);
					desktopPane.add(jButtonUp);
					jButtonUp.setEnabled(false);
					jButtonUp.setBounds(284, 184, 28, 28);
					jButtonUp.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{
							if (jListHosts.getSelectedIndex() > -1)
							{
								int j = jListHosts.getSelectedIndex();
								JHost element = ((JHost) jListHosts.getModel().getElementAt(j));
								hostList = moveElementUp(hostList, element);
								renumberHosts();
								populateList(element.getSiteDescription());
								setSaveButtonState(true);
							}
						}
					});
				}
				{
					jButtonDown = new JButton4j(Common.icon_arrow_down);
					desktopPane.add(jButtonDown);
					jButtonDown.setEnabled(false);
					jButtonDown.setBounds(284, 219, 28, 28);
					jButtonDown.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{
							if (jListHosts.getSelectedIndex() > -1)
							{
								int j = jListHosts.getSelectedIndex();
								JHost element = ((JHost) jListHosts.getModel().getElementAt(j));
								hostList = moveElementDown(hostList, element);
								renumberHosts();
								populateList(element.getSiteDescription());
								setSaveButtonState(true);
							}
						}
					});
				}
				{
					jButtonClose = new JButton4j(Common.icon_close);
					desktopPane.add(jButtonClose);
					jButtonClose.setText("Close");
					jButtonClose.setBounds(762, 382, 160, 36);
					jButtonClose.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{
							logger.debug("JFrameHostAdmin closed");
							dispose();
						}
					});
				}
				{
					jButtonUndo = new JButton4j(Common.icon_undo);
					desktopPane.add(jButtonUndo);
					jButtonUndo.setText("Undo Changes");
					jButtonUndo.setBounds(762, 271, 160, 36);
					jButtonUndo.setEnabled(false);
					jButtonUndo.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{
							// Common.hosts = JXMLHost.loadHosts(false);
							int j = jListHosts.getSelectedIndex();
							String Current = hostList.get(j).getSiteDescription();
							getHosts();
							populateList(Current);
							getHostData();
							setSaveButtonState(false);
							setSaveButtonState(false);
						}
					});
				}
				{

					jButtonSave = new JButton4j(Common.icon_update);
					desktopPane.add(jButtonSave);
					jButtonSave.setText("Save DB Connections");
					jButtonSave.setBounds(762, 197, 160, 36);
					jButtonSave.setEnabled(false);
					jButtonSave.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{
							String splash;
							if (jCheckBoxSplash.isSelected())
							{
								splash = "Y";
							}
							else
							{
								splash = "N";
							}
							
							JXMLHost.writeHosts(hostList,splash);
							jButtonSave.setEnabled(false);
							jButtonUndo.setEnabled(false);
						}
					});
				}
				{
					jTextFieldDescription = new JTextField4j();
					desktopPane.add(jTextFieldDescription);
						jTextFieldDescription.setFocusCycleRoot(true);
					jTextFieldDescription.setBounds(425, 40, 325, 21);
					jTextFieldDescription.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent evt)
						{
							jTextFieldKeyTyped();
						}
					});
				}
				{
					jTextFieldURL = new JTextField4j();
					desktopPane.add(jTextFieldURL);
					jTextFieldURL.setFocusCycleRoot(true);
					jTextFieldURL.setBounds(425, 65, 325, 21);
					jTextFieldURL.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent evt)
						{
							jTextFieldKeyTyped();
						}
					});
				}
				{
					jLabel2 = new JLabel4j_std();
					desktopPane.add(jLabel2);
					jLabel2.setText("Database Type");
					jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel2.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel2.setBounds(291, 91, 127, 21);

				}
				{
					//PG mod ->
					ComboBoxModel jComboBoxjdbcDriverModel = new DefaultComboBoxModel(new String[] { "", "mySQL", "Oracle", "SQL Server","Web URL","PostgreSQL" });
					//PG mod end
					jComboBoxjdbcDriver = new JComboBox4j();
					desktopPane.add(jComboBoxjdbcDriver);
					jComboBoxjdbcDriver.setModel(jComboBoxjdbcDriverModel);
					jComboBoxjdbcDriver.setBounds(425, 90, 164, 21);
					jComboBoxjdbcDriver.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{
							if (jComboBoxjdbcDriver.getSelectedItem().toString().equals("Oracle"))
							{
								jTextFieldDriver.setText("oracle.jdbc.driver.OracleDriver");
								jTextFieldConnect.setText("jdbc:oracle:thin:@jdbcServer:jdbcPort:jdbcSID");
								jTextFieldDateTime.setText("sysdate");
								jTextFieldSelectLimit.setText("rownum");
								jTextFieldServer.setText("localhost");
								jTextFieldPort.setText("1521");
							}
							if (jComboBoxjdbcDriver.getSelectedItem().toString().equals("mySQL"))
							{
								jTextFieldDriver.setText("com.mysql.jdbc.Driver");
								jTextFieldConnect.setText("jdbc:mysql://jdbcServer/jdbcDatabase");
								jTextFieldDateTime.setText("sysdate");
								jTextFieldSelectLimit.setText("limit");
								jTextFieldServer.setText("localhost");
								jTextFieldPort.setText("3306");
							}
							if (jComboBoxjdbcDriver.getSelectedItem().toString().equals("SQL Server"))
							{
								jTextFieldDriver.setText("com.microsoft.sqlserver.jdbc.SQLServerDriver");
								jTextFieldConnect.setText("jdbc:sqlserver://jdbcServer\\jdbcSID");
								jTextFieldDateTime.setText("sysdate");
								jTextFieldSelectLimit.setText("top");
								jTextFieldServer.setText("localhost");
								jTextFieldPort.setText("1433");
							}
							if (jComboBoxjdbcDriver.getSelectedItem().toString().equals("Web URL"))
							{
								jTextFieldDriver.setText("http");
								jTextFieldConnect.setText("");
								jTextFieldDateTime.setText("");
								jTextFieldSelectLimit.setText("");
								jTextFieldServer.setText("");
								jTextFieldPort.setText("");
							}
							if (jComboBoxjdbcDriver.getSelectedItem().toString().equals("PostgreSQL"))
							{
								jTextFieldDriver.setText("org.postgresql.Driver");
								jTextFieldConnect.setText("jdbc:postgresql:postgres");
								jTextFieldDateTime.setText("now()");
								jTextFieldSelectLimit.setText("limit");
								jTextFieldServer.setText("localhost");
								jTextFieldPort.setText("5432");
							}
							jTextFieldKeyTyped();
						}
					});
				}
				{
					jTextFieldConnect = new JTextField4j();
					desktopPane.add(jTextFieldConnect);
					jTextFieldConnect.setFocusCycleRoot(true);
					jTextFieldConnect.setBounds(425, 145, 325, 21);
					jTextFieldConnect.setEditable(false);
					jTextFieldConnect.setEnabled(false);
					jTextFieldConnect.setDisabledTextColor(Common.color_textdisabled);
					jTextFieldConnect.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent evt)
						{
							jTextFieldKeyTyped();
						}
					});
				}
				{
					jLabel1 = new JLabel4j_std();
					desktopPane.add(jLabel1);
					jLabel1.setText("Driver");
					jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel1.setBounds(291, 116, 127, 21);

				}
				{
					jLabel7 = new JLabel4j_std();
					desktopPane.add(jLabel7);
					jLabel7.setText("Database");
					jLabel7.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel7.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel7.setBounds(320, 196, 98, 21);

				}
				{
					jLabel9 = new JLabel4j_std();
					desktopPane.add(jLabel9);
					jLabel9.setText("DB Date Time");
					jLabel9.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel9.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel9.setBounds(291, 321, 127, 21);

				}
				{
					jLabel11 = new JLabel4j_std();
					desktopPane.add(jLabel11);
					jLabel11.setText("Password");
					jLabel11.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel11.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel11.setBounds(291, 296, 127, 21);

				}
				{
					jLabel13 = new JLabel4j_std();
					desktopPane.add(jLabel13);
					jLabel13.setText("Description");
					jLabel13.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel13.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel13.setBounds(291, 41, 127, 21);
				}
				{
					jLabel15 = new JLabel4j_std();
					desktopPane.add(jLabel15);
					jLabel15.setText("Site No");
					jLabel15.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel15.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel15.setBounds(291, 15, 127, 21);

				}
				{
					jLabel17 = new JLabel4j_std();
					desktopPane.add(jLabel17);
					jLabel17.setText("Username");
					jLabel17.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel17.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel17.setBounds(291, 271, 127, 21);

				}
				{
					jLabel19 = new JLabel4j_std();
					desktopPane.add(jLabel19);
					jLabel19.setText("Port");
					jLabel19.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel19.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel19.setBounds(320, 221, 98, 21);

				}
				{
					jTextFieldDateTime = new JTextField4j();
					desktopPane.add(jTextFieldDateTime);
					jTextFieldDateTime.setFocusCycleRoot(true);
					jTextFieldDateTime.setBounds(425, 320, 325, 21);
					jTextFieldDateTime.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent evt)
						{
							jTextFieldKeyTyped();
						}
					});
				}
				{
					jTextFieldUsername = new JTextField4j();
					desktopPane.add(jTextFieldUsername);
					jTextFieldUsername.setFocusCycleRoot(true);
					jTextFieldUsername.setBounds(425, 270, 325, 21);
					jTextFieldUsername.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent evt)
						{
							jTextFieldKeyTyped();
						}
					});
				}
				{
					jTextFieldPassword = new JPasswordField(10);
					desktopPane.add(jTextFieldPassword);
					jTextFieldPassword.setFocusCycleRoot(true);
					jTextFieldPassword.setBounds(425, 295, 325, 21);
					jTextFieldPassword.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent evt)
						{
							jTextFieldKeyTyped();
						}
					});
				}
				{
					jTextFieldPort = new JTextField4j();
					desktopPane.add(jTextFieldPort);
					jTextFieldPort.setFocusCycleRoot(true);
					jTextFieldPort.setBounds(425, 220, 325, 21);
					jTextFieldPort.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent evt)
						{
							jTextFieldKeyTyped();
						}
					});
				}
				{
					jTextFieldSID = new JTextField4j();
					desktopPane.add(jTextFieldSID);
					jTextFieldSID.setFocusCycleRoot(true);
					jTextFieldSID.setBounds(425, 245, 325, 21);
					jTextFieldSID.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent evt)
						{
							jTextFieldKeyTyped();
						}
					});
				}
				{
					jTextFieldSiteNo = new JTextField4j();
					desktopPane.add(jTextFieldSiteNo);
					jTextFieldSiteNo.setFocusCycleRoot(true);
					jTextFieldSiteNo.setBounds(425, 14, 28, 21);
					jTextFieldSiteNo.setHorizontalAlignment(SwingConstants.CENTER);
					jTextFieldSiteNo.setEnabled(false);
					jTextFieldSiteNo.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent evt)
						{
							jTextFieldKeyTyped();
						}
					});
				}
				{
					jTextFieldDriver = new JTextField4j();
					desktopPane.add(jTextFieldDriver);
					jTextFieldDriver.setFocusCycleRoot(true);
					jTextFieldDriver.setBounds(425, 117, 325, 21);
					jTextFieldDriver.setEditable(false);
					jTextFieldDriver.setEnabled(false);
					jTextFieldDriver.setDisabledTextColor(Common.color_textdisabled);
					jTextFieldDriver.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent evt)
						{
							jTextFieldKeyTyped();
						}
					});
				}
				{
					jCheckBoxEnabled = new JCheckBox();
					jCheckBoxEnabled.setFont(Common.font_std);
					desktopPane.add(jCheckBoxEnabled);
					jCheckBoxEnabled.setText("Enabled");
					jCheckBoxEnabled.setBounds(467, 14, 91, 21);
					jCheckBoxEnabled.setBackground(new java.awt.Color(255, 255, 255));
					jCheckBoxEnabled.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{
							jTextFieldKeyTyped();
						}
					});
				}
				{
					jCheckBoxSplash = new JCheckBox();
					jCheckBoxSplash.setSelected(true);
					jCheckBoxSplash.setFont(Common.font_std);
					desktopPane.add(jCheckBoxSplash);
					jCheckBoxSplash.setText("Enable Splash Screen");
					jCheckBoxSplash.setBounds(560, 14, 150, 21);
					jCheckBoxSplash.setBackground(new java.awt.Color(255, 255, 255));
					jCheckBoxSplash.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{
							jTextFieldKeyTyped();
						}
					});
				}
				{
					jLabel21 = new JLabel4j_std();
					desktopPane.add(jLabel21);
					jLabel21.setText("SID");
					jLabel21.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel21.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel21.setBounds(320, 246, 98, 21);
				}
				{
					jButtonCancel = new JButton4j(Common.icon_cancel);
					desktopPane.add(jButtonCancel);
					jButtonCancel.setText("Cancel");
					jButtonCancel.setBounds(762, 345, 160, 36);
					jButtonCancel.setEnabled(false);
					jButtonCancel.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{
							getHostData();
						}
					});
				}
				{
					jTextFieldServer = new JTextField4j();
					desktopPane.add(jTextFieldServer);
					jTextFieldServer.setFocusCycleRoot(true);
					jTextFieldServer.setBounds(425, 170, 325, 21);
					jTextFieldServer.setEnabled(false);
					jTextFieldServer.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent evt)
						{
							jTextFieldKeyTyped();
						}
					});
				}
				{
					jLabel4 = new JLabel4j_std();
					desktopPane.add(jLabel4);
					jLabel4.setText("Connect String");
					jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel4.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel4.setBounds(291, 141, 127, 21);
				}
				{
					jTextFieldDatabase = new JTextField4j();
					desktopPane.add(jTextFieldDatabase);
					jTextFieldDatabase.setEnabled(false);
					jTextFieldDatabase.setFocusCycleRoot(true);
					jTextFieldDatabase.setBounds(425, 195, 325, 21);
					jTextFieldDatabase.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent evt)
						{
							jTextFieldKeyTyped();
						}
					});
				}
				{
					jLabel24 = new JLabel4j_std();
					desktopPane.add(jLabel24);
					jLabel24.setText("Server");
					jLabel24.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel24.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel24.setBounds(320, 171, 98, 21);
				}
				{
					jButtonTest = new JButton4j(Common.icon_connect);
					desktopPane.add(jButtonTest);
					jButtonTest.setText("Connect to DB");
					jButtonTest.setBounds(762, 86, 160, 36);
					jButtonTest.setToolTipText("Connect to selected Host Database");
					jButtonTest.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{

							writeBackHosts(hostList);

							int j = jListHosts.getSelectedIndex();
							JHost hst = (JHost) jListHosts.getModel().getElementAt(j);
							Common.selectedHostID = hst.getSiteNumber();
							if (Common.hostList.getHost(Common.selectedHostID).connect(Common.sessionID, Common.selectedHostID))
							{
								btnSchema.setEnabled(true);
								jButtonUpdate.setEnabled(true);
								Common.hostList.getHost(Common.selectedHostID).disconnect(Common.sessionID);
							}
							else
							{
								jButtonUpdate.setEnabled(false);
								btnSchema.setEnabled(false);
							}
						}
					});
				}
				{
					jButtonUpdate = new JButton4j(Common.icon_update);
					desktopPane.add(jButtonUpdate);
					jButtonUpdate.setText("Create/Update Tables");
					jButtonUpdate.setBounds(762, 123, 160, 36);
					jButtonUpdate.setEnabled(false);
					jButtonUpdate.setToolTipText("Create or Upgrade Application Database Schema");
					jButtonUpdate.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{

							writeBackHosts(hostList);

							int j = jListHosts.getSelectedIndex();
							JHost hst = (JHost) jListHosts.getModel().getElementAt(j);
							Common.selectedHostID = hst.getSiteNumber();

							if (Common.hostList.getHost(Common.selectedHostID).connect(Common.sessionID, Common.selectedHostID))
							{
								JDBSchema schema = new JDBSchema(Common.sessionID, Common.hostList.getHost(Common.selectedHostID));
								JDBUpdateRequest updrst = new JDBUpdateRequest();
								updrst = schema.validate(false);
								//PG add ->
								String driver = Common.hostList.getHost(Common.selectedHostID).getDatabaseParameters().getjdbcDriver();
								if (driver.equals("org.postgresql.Driver")){
									try
									{
										Common.hostList.getHost(Common.selectedHostID).getConnection(Common.sessionID).commit();
									}
									catch (Exception e)
									{
										e.printStackTrace();
									}
								}
								//PG add end

								if (updrst.schema_updateRequired)
								{
									int continueUpdate = JOptionPane.showConfirmDialog(me, "Current Schema Version is " + String.valueOf(updrst.schema_currentVersion) + ", required version is "
											+ String.valueOf(updrst.schema_requiredVersion) + ". Upgrade ?", "Connection to (" + hst.getSiteDescription() + ")", JOptionPane.YES_NO_OPTION);

									if (continueUpdate == 0)
									{

										LinkedList<JDBDDL> cmds = new LinkedList<JDBDDL>();
										cmds.clear();
										cmds = JXMLSchema.loadDDLStatements(jTextFieldDriver.getText(), "xml/schema/"
												+ Common.hostList.getHost(Common.selectedHostID).getDatabaseParameters().getjdbcDriver() + "/");
										boolean updateCtrl = false;
										if (cmds.size() > 0)
										{
											if (schema.executeDDL(cmds, progressBar, labelCommand) == true)
											{
												updateCtrl = true;

											}
											else
											{
												JUtility.errorBeep();
												JDialogDMLErrors dmlerrs = new JDialogDMLErrors(me, cmds);
												dmlerrs.setModal(true);
												int ignoreDDLErrors = JOptionPane.showConfirmDialog(me, "Ignore Errors and set SCHEMA version to " + String.valueOf(updrst.schema_requiredVersion)
														+ " ?", "Connection to (" + hst.getSiteDescription() + ")", JOptionPane.YES_NO_OPTION);

												if (ignoreDDLErrors == 0)
												{
													updateCtrl = true;
												}
											}
										}
										else
										{
											JOptionPane.showMessageDialog(me, "No DDL Commands found", "Connection to (" + hst.getSiteDescription() + ")", JOptionPane.WARNING_MESSAGE);

										}

										if (updateCtrl)
										{
											JDBControl ctrl = new JDBControl(Common.selectedHostID, Common.sessionID);
											if (ctrl.getProperties("SCHEMA VERSION"))
											{
												ctrl.setKeyValue(String.valueOf(updrst.schema_requiredVersion));
												ctrl.update();
											}
											else
											{
												ctrl.create("SCHEMA VERSION", String.valueOf(updrst.schema_requiredVersion), "Schema Version");
											}
											JOptionPane.showMessageDialog(me, "Schema Version now set to " + String.valueOf(JVersion.getSchemaVersion()), "Control Table",
													JOptionPane.INFORMATION_MESSAGE);

										}

									}
								}
								else
								{
									JOptionPane.showMessageDialog(me, "No Schema update Required", "Connection to (" + hst.getSiteDescription() + ")", JOptionPane.INFORMATION_MESSAGE);
								}

								if (updrst.program_updateRequired)
								{
									JDBControl ctrl = new JDBControl(Common.selectedHostID, Common.sessionID);

									if (ctrl.getProperties("PROGRAM VERSION"))
									{
										ctrl.setKeyValue(JVersion.getProgramVersion());
										ctrl.update();
									}
									else
									{
										ctrl.create("PROGRAM VERSION", JVersion.getProgramVersion(), "Program Version");
									}
									JOptionPane.showMessageDialog(me, "Program Version now set to " + JVersion.getProgramVersion(), "Control Table", JOptionPane.INFORMATION_MESSAGE);

								}
								Common.hostList.getHost(Common.selectedHostID).disconnect(Common.sessionID);
							}
						}
					});
				}
				{
					jTextFieldSelectLimit = new JTextField4j();
					desktopPane.add(jTextFieldSelectLimit);
					jTextFieldSelectLimit.setBounds(425, 345, 325, 21);
					jTextFieldSelectLimit.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent evt)
						{
							jTextFieldKeyTyped();
						}
					});
					jTextFieldSelectLimit.setFocusCycleRoot(true);
					jTextFieldSelectLimit.setEnabled(false);
				}
				{
					jLabel26 = new JLabel4j_std();
					desktopPane.add(jLabel26);
					jLabel26.setText("DB Select Limit");
					jLabel26.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel26.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel26.setBounds(291, 346, 127, 21);

				}
				{
					jLabel28 = new JLabel4j_std();
					desktopPane.add(jLabel28);
					jLabel28.setText("DB Schema");
					jLabel28.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel28.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel28.setBounds(291, 370, 127, 21);
				}
				{
					jTextFieldSchema = new JTextField4j();
					desktopPane.add(jTextFieldSchema);
					jTextFieldSchema.setEnabled(false);
					jTextFieldSchema.setBounds(425, 370, 325, 21);
					jTextFieldSchema.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent evt)
						{
							jTextFieldKeyTyped();
						}
					});
					jTextFieldSchema.setFocusCycleRoot(true);
				}

				{
					jLabel13_1 = new JLabel4j_std();
					jLabel13_1.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel13_1.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel13_1.setText("URL");
					jLabel13_1.setBounds(291, 66, 127, 21);
					desktopPane.add(jLabel13_1);
				}

				{
					jTextFieldUniqueID = new JTextField4j();
					jTextFieldUniqueID.addKeyListener(new KeyAdapter()
					{
						@Override
						public void keyTyped(KeyEvent e)
						{
							jTextFieldKeyTyped();
						}
					});
					jTextFieldUniqueID.setEditable(true);
					jTextFieldUniqueID.setBounds(425, 395, 325, 21);
					desktopPane.add(jTextFieldUniqueID);
				}

				{
					jLabel13_2 = new JLabel4j_std();
					jLabel13_2.setText("Unique ID");
					jLabel13_2.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabel13_2.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabel13_2.setBounds(291, 396, 127, 21);
					desktopPane.add(jLabel13_2);
				}
				{
					btnSchema = new JButton4j(Common.icon_report);
					btnSchema.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{

							writeBackHosts(hostList);

							int j = jListHosts.getSelectedIndex();
							JHost hst = (JHost) jListHosts.getModel().getElementAt(j);
							Common.selectedHostID = hst.getSiteNumber();
							if (Common.hostList.getHost(Common.selectedHostID).connect(Common.sessionID, Common.selectedHostID))
							{
								btnSchema.setEnabled(true);
								jButtonUpdate.setEnabled(true);
								JDBStructure struct = new JDBStructure(Common.selectedHostID, Common.sessionID);
								struct.exportSchema();
								struct.saveAs("SCHEMA " + Common.hostList.getHost(Common.selectedHostID).getSiteDescription() + ".txt", Common.mainForm);
								Common.hostList.getHost(Common.selectedHostID).disconnect(Common.sessionID);

							}
							else
							{
								jButtonUpdate.setEnabled(false);
								btnSchema.setEnabled(false);
							}
						}
					});
					btnSchema.setText("DB Schema Report");
					btnSchema.setEnabled(false);
					btnSchema.setBounds(762, 308, 160, 36);
					desktopPane.add(btnSchema);
				}
			}

			progressBar.setBounds(8, 426, 912, 28);
			progressBar.setBackground(Color.WHITE);
			progressBar.setForeground(Color.BLUE);
			desktopPane.add(progressBar);

			labelCommand.setBounds(8, 458, 912, 23);
			labelCommand.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			desktopPane.add(labelCommand);

			JButton4j btnService = new JButton4j(Common.icon_interface);
			btnService.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					//Filename for the template file
					String filenameInput = System.getProperty("user.dir") + File.separator + "conf" + File.separator + "template.conf";
					JFileIO ioInput = new JFileIO();
					
					//Filename for the live file
					String filenameOutput = System.getProperty("user.dir") + File.separator + "conf" + File.separator + "wrapper.conf";
					JFileIO ioOutput = new JFileIO();
					
					//Read input file
					List<String> data = ioInput.readFileLines(filenameInput);
					
					int s = data.size();
					if (s > 0)
					{
						String text = "";
						for (int x=0;x<s;x++)
						{
							text = data.get(x);
							if (text.startsWith("wrapper.app.parameter.2="))
							{
								text = "wrapper.app.parameter.2="+jTextFieldUniqueID.getText();
								data.set(x, text);
							}

							if (text.startsWith("wrapper.displayname="))
							{
								text = "wrapper.displayname="+jTextFieldDescription.getText();
								data.set(x, text);
							}

							if (text.startsWith("wrapper.description="))
							{
								text = "wrapper.description="+jTextFieldDescription.getText();
								data.set(x, text);
							}
						}
						ioOutput.writeFileLines(filenameOutput, data);
					}
				}
			});
			btnService.setToolTipText("Connect to selected Host Database");
			btnService.setText("Assign to Service");
			btnService.setBounds(762, 234,160, 36);
			desktopPane.add(btnService);
			
			JLabel4j_std label4j_std = new JLabel4j_std();
			label4j_std.setFont(new Font("Arial", Font.BOLD, 11));
			label4j_std.setText("Database Connections");
			label4j_std.setHorizontalTextPosition(SwingConstants.LEFT);
			label4j_std.setHorizontalAlignment(SwingConstants.LEFT);
			label4j_std.setBounds(14, 15, 127, 21);
			desktopPane.add(label4j_std);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
