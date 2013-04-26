package com.commander4j.db;

import java.awt.Color;
import java.awt.Rectangle;
import java.sql.Statement;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import com.commander4j.app.JVersion;
import com.commander4j.sys.Common;
import com.commander4j.sys.JHost;
import com.commander4j.util.JSplashScreenUtils;
import com.commander4j.util.JUtility;
import com.commander4j.xml.JXMLSchema;


/**
 */
public class JDBSchema
{

	private JHost hst = new JHost();
	private String dbErrorMessage;
	private String sessionID;

	private void setSessionID(String session) {
		sessionID = session;
	}

	private String getSessionID() {
		return sessionID;
	}

	public JDBSchema(String session, JHost host)
	{
		setSessionID(session);
		setHost(host);
	}


	private void setErrorMessage(String errorMsg) {
		if (errorMsg.isEmpty() == false)
		{
		}
		dbErrorMessage = errorMsg;
	}


	public String getErrorMessage() {
		return dbErrorMessage;
	}


	public String getSchemaName() {
		String result = "";
		result = com.commander4j.util.JUtility.replaceNullStringwithBlank(hst.getDatabaseParameters().getjdbcDatabaseSchema());
		if (result.equals("") == false)
		{
			result = result + ".";
		}
		return result;
	}

	public JDBUpdateRequest validate(boolean showWarnings) {
		JDBUpdateRequest result = new JDBUpdateRequest();

		JDBTable table = new JDBTable(hst.getSiteNumber(), getSessionID(), hst.getDatabaseParameters().getjdbcDatabaseSchema() + "SYS_CONTROL");
		if (table.isValidTable() == false)
		{
			hst.setSchemaVersion(-1);
			result.schema_currentVersion = -1;
			result.schema_requiredVersion = JVersion.getSchemaVersion();
			result.schema_updateRequired = true;

			result.program_currentVersion = new Double("-1");
			result.program_requiredVersion = JVersion.getProgramVersionValue();
			result.program_updateRequired = true;

			if (showWarnings)
			{
				JUtility.errorBeep();
				JSplashScreenUtils.hide();
				JOptionPane.showMessageDialog(null, "One or more missing application tables, please run Commander4j Setup", "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		}
		else
		{
			JDBControl ctrl = new JDBControl(hst.getSiteNumber(), getSessionID());
			if (ctrl.getProperties("SCHEMA VERSION") == false)
			{
				ctrl.create("SCHEMA VERSION", "0", "Database Schema Version");
			}

			hst.setSchemaVersion(Integer.valueOf(ctrl.getKeyValue()));

			int newVersion = JXMLSchema.getSchemaVersion();
	
			if (hst.getSchemaVersion() != newVersion)
			{
				result.schema_currentVersion = Integer.valueOf(ctrl.getKeyValue());
				result.schema_requiredVersion = JVersion.getSchemaVersion();
				result.schema_updateRequired = true;
				if (showWarnings)
				{
					JUtility.errorBeep();
					
					JSplashScreenUtils.hide();
					
					int answer = JOptionPane.showConfirmDialog(null, "Database Schema is out of date.\nPlease use Commander4j Setup program to upgrade.\nDetected version ["
							+ String.valueOf(Common.hostList.getHost(hst.getSiteNumber()).getSchemaVersion()) + "], Expected version [" + String.valueOf(newVersion) + "]\nContinue with application logon ?", "Database Schema",
							JOptionPane.YES_NO_OPTION);

					if (answer == JOptionPane.NO_OPTION)
					{
						System.exit(0);
					}
					else
					{
						JSplashScreenUtils.show();
					}
				}
			}

			if (ctrl.getProperties("PROGRAM VERSION") == false)
			{
				ctrl.create("PROGRAM VERSION", JVersion.getProgramVersion(), "Application Version");
			}

			result.program_requiredVersion = Double.valueOf(ctrl.getKeyValue("PROGRAM VERSION"));
			result.program_currentVersion = Double.valueOf(JVersion.getProgramVersion());

			if (result.program_requiredVersion != result.program_currentVersion)
			{
				result.program_updateRequired = true;
				if (showWarnings)
				{
					if (result.program_currentVersion > result.program_requiredVersion)
					{

						JSplashScreenUtils.hide();
						
						int answer = JOptionPane.showConfirmDialog(null, "Your database is configured to expect program version " + String.valueOf(result.program_requiredVersion) + ". You appear to be running version "
								+ String.valueOf(result.program_currentVersion) + " of the application.\nPlease run the Setup4j Configuration to update your database." + "]\nContinue with application logon ?", "Program Version",
								JOptionPane.YES_NO_OPTION);
						if (answer == JOptionPane.NO_OPTION)
						{
							System.exit(0);
						}
						else
						{
							JSplashScreenUtils.show();
						}
					}
					if (result.program_currentVersion < result.program_requiredVersion)
					{
						JSplashScreenUtils.hide();
						
						int answer = JOptionPane.showConfirmDialog(null, "You appear to be running an old version of the application.\nPlease install Commander4j " + String.valueOf(result.program_requiredVersion) + " or later.\nDetected version ["
								+ JVersion.getProgramVersion() + "]\nContinue with application logon ?", "Program Version", JOptionPane.WARNING_MESSAGE);
						if (answer == JOptionPane.NO_OPTION)
						{
							System.exit(0);
						}
						else
						{
							JSplashScreenUtils.show();
						}
					}
				}
			}
			else
			{
				result.program_updateRequired = false;
			}
		}
		return result;
	}

	public boolean executeDDL(LinkedList<JDBDDL> ddl,JProgressBar progress,JLabel commandLabel) {
		Statement stmtupdate;
		boolean result = true;
		int sze = ddl.size();
		String command = "";

		if (sze > 0)
		{

			progress.setMinimum(0);
			progress.setMaximum(sze-1);
			progress.setBackground(Color.WHITE);
			progress.setForeground(Color.BLUE);
			progress.setStringPainted(true);
			try
			{
				stmtupdate = hst.getConnection(getSessionID()).createStatement();

				for (int l = 0; l < sze; l++)
				{
					try
					{
						command = (ddl.get(l).getText().trim());
						progress.setValue(l);
						commandLabel.setText(command.toLowerCase());
						Rectangle progressRect = progress.getBounds();  
						progressRect.x = 0;  
						progressRect.y = 0;  
						progress.paintImmediately( progressRect );
						progressRect = commandLabel.getBounds();  
						progressRect.x = 0;  
						progressRect.y = 0;  
						commandLabel.paintImmediately( progressRect );						
						try
						{
							Thread.sleep(10);
						}
						catch (InterruptedException ie)
						{
						}
						
						if (command.equalsIgnoreCase("null") == false)
						{
							stmtupdate.executeUpdate(ddl.get(l).getText());
							hst.getConnection(getSessionID()).commit();
						}
						ddl.get(l).setError("Success");
					}
					catch (Exception ex)
					{
						result = false;
						String err = ex.getMessage();
						ddl.get(l).setError(err);
						ddl.get(l).setErrorCount(ddl.get(l).getErrorCount()+1);
					}
				}

			}
			catch (Exception ex)
			{
				setErrorMessage(ex.getMessage());
			}
		}
		commandLabel.setText("");
		return result;
	}

	public void setHost(JHost host) {
		hst = host;
	}

}
