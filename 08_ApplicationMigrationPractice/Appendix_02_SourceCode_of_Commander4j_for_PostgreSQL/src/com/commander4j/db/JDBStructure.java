package com.commander4j.db;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Collections;

import com.commander4j.sys.Common;
import com.commander4j.util.JFileFilterTXT;
import com.commander4j.util.JUtility;

public class JDBStructure
{
	private String hostID;
	private String sessionID;
	private String db_error_message;
	private LinkedList<String> output = new LinkedList<String>();
	final Logger logger = Logger.getLogger(JDBStructure.class);
	ResultSetMetaData md;

	public JDBStructure(String host, String session)
	{
		setHostID(host);
		setSessionID(session);
	}

	private void setHostID(String host) {
		hostID = host;
	}

	private void setSessionID(String session) {
		sessionID = session;
	}

	private String getHostID() {
		return hostID;
	}

	private String getSessionID() {
		return sessionID;
	}

	public void exportSchema() {

		LinkedList<String> tableNames = new LinkedList<String>();
		String table_name = "";
		String col_name = "";
		String col_type = "";
		String col_size = "";
		String col_precision = "";
		String col_scale = "";
		Boolean includeTable = false;

		String driver = Common.hostList.getHost(getHostID()).getDatabaseParameters().getjdbcDriver();
		String select_prefix = "";
		int col = 0;

		String actual_schema = "";
		String required_schema = Common.hostList.getHost(getHostID()).getDatabaseParameters().getjdbcDatabaseSchema().toLowerCase();

		if (required_schema.length() == 0)
		{
			if (driver.equals("com.microsoft.sqlserver.jdbc.SQLServerDriver"))
			{
				required_schema = "dbo";
			}

			if (driver.equals("oracle.jdbc.driver.OracleDriver"))
			{
				required_schema = Common.hostList.getHost(getHostID()).getDatabaseParameters().getjdbcUsername().toLowerCase();
			}

			if (driver.equals("com.mysql.jdbc.Driver"))
			{
				required_schema = Common.hostList.getHost(getHostID()).getDatabaseParameters().getjdbcDatabase();
			}
			//PG add ->
			if (driver.equals("org.postgresql.Driver"))
			{
				required_schema = "public";
			}
			//PG add end
		}

		required_schema = required_schema.replace(".", "");
		tableNames.clear();
		output.clear();

		try
		{
			Common.hostList.getHost(getHostID()).getConnection(getSessionID()).getMetaData();
			DatabaseMetaData dbm = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).getMetaData();
			String[] types = { "TABLE" };
			ResultSet rs = dbm.getTables(null, null, "%", types);
			while (rs.next())
			{
				includeTable = false;
				table_name = rs.getString("TABLE_NAME").toLowerCase();

				try
				{
					actual_schema = rs.getString("TABLE_SCHEM").toLowerCase();
					if (driver.equals("com.microsoft.sqlserver.jdbc.SQLServerDriver"))
					{

						if (actual_schema.equals(required_schema) == true)
						{
							includeTable = true;
						}
					}

					if (driver.equals("oracle.jdbc.driver.OracleDriver"))
					{
						if (actual_schema.equals(required_schema) == true)
						{
							includeTable = true;
						}
					}
					//PG add ->
					if (driver.equals("org.postgresql.Driver"))
					{
						if (actual_schema.equals(required_schema) == true)
						{
							includeTable = true;
						}
					}
					//PG add end
				}
				catch (Exception ex)
				{
					actual_schema = "";
				}

				try
				{
					actual_schema = rs.getString("TABLE_CAT").toLowerCase();
					if (driver.equals("com.mysql.jdbc.Driver"))
					{
						if (actual_schema.equals(required_schema) == true)
						{
							includeTable = true;
						}
					}
				}
				catch (Exception ex)
				{
					actual_schema = "";
				}

				if (includeTable)
				{
					tableNames.addLast(rs.getString("TABLE_NAME").toLowerCase());
				}
			}
		}
		catch (SQLException s)
		{
			setErrorMessage(s.getMessage());
			logger.debug("No tables found in the database");
			//PG add ->
			String drivername = Common.hostList.getHost(getHostID()).getDatabaseParameters().getjdbcDriver();
			if (drivername.equals("org.postgresql.Driver")){
				try
				{
					Common.hostList.getHost(getHostID()).getConnection(getSessionID()).rollback();
				}
				catch (Exception e2)
				{
					e2.printStackTrace();
				}
			}
			//PG add end
		}

		select_prefix = required_schema + ".";

		Collections.sort(tableNames);
		int tableCount = tableNames.size();

		if (tableCount > 0)
		{
			try
			{

				for (int x = 0; x < tableCount; x++)
				{
					table_name = tableNames.get(x).toLowerCase();

					try
					{
						Statement st = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).createStatement();
						ResultSet rs = st.executeQuery("select * from " + select_prefix + table_name);
						md = rs.getMetaData();
						col = md.getColumnCount();
						output.addLast("");
						output.addLast(table_name);
						output.addLast(JUtility.padString(table_name.length(), "="));
						output.addLast("");
						output.addLast("Column Name                      Type               Size  Precision     Scale");
						output.addLast("-------------------------------  ------------ ----------  ---------  --------");
					}
					catch (Exception ex)
					{
						col = 0;
					}

					for (int i = 1; i <= col; i++)
					{
						table_name = JUtility.padString(table_name, true, 32, " ");
						col_name = JUtility.padString(md.getColumnName(i).toLowerCase(), true, 32, " ");
						col_type = JUtility.padString(md.getColumnTypeName(i).toLowerCase(), true, 20, " ");
						col_size = JUtility.padString(String.valueOf(md.getColumnDisplaySize(i)), true, 10, " ");
						if ((col_type.contains("char") == true) || (col_type.contains("date") == true))
						{
							col_precision = "";
							col_scale = "";
						}
						else
						{
							col_precision = JUtility.padString(String.valueOf(md.getPrecision(i)), true, 10, " ");
							col_scale = JUtility.padString(String.valueOf(md.getScale(i)), true, 10, " ");
							if ((col_scale.contains("-") == true))
							{
								col_scale = "";
							}
							if ((col_precision.trim().equals("0") == true))
							{
								col_precision = "";
							}
						}
						output.addLast(col_name + " " + col_type + " " + String.valueOf(col_size) + " " + String.valueOf(col_precision) + " " + String.valueOf(col_scale));
					}
				}
			}
			catch (SQLException s)
			{
				setErrorMessage(s.getMessage());
				System.out.println("SQL statement is not executed!");
				//PG add ->
				String drivername = Common.hostList.getHost(getHostID()).getDatabaseParameters().getjdbcDriver();
				if (drivername.equals("org.postgresql.Driver")){
					try
					{
						Common.hostList.getHost(getHostID()).getConnection(getSessionID()).rollback();
					}
					catch (Exception e2)
					{
						e2.printStackTrace();
					}
				}
				//PG add end
			}
		}

	}

	public String getErrorMessage() {
		return db_error_message;
	}

	private void setErrorMessage(String ErrorMsg) {
		db_error_message = ErrorMsg;
	}

	public void saveAs(String defaultFilename, Component parent) {

		if (output.size() > 0)
		{
			JFileChooser saveTXT = new JFileChooser();

			try
			{
				File f = new File(new File(System.getProperty("user.home")).getCanonicalPath());
				saveTXT.setCurrentDirectory(f);
				saveTXT.addChoosableFileFilter(new JFileFilterTXT());
				saveTXT.setSelectedFile(new File(defaultFilename));
			}
			catch (Exception ex)
			{
			}

			int result = saveTXT.showSaveDialog(parent);
			if (result == 0)
			{
				File selectedFile;
				selectedFile = saveTXT.getSelectedFile();
				if (selectedFile != null)
				{
					String filename = selectedFile.getAbsolutePath();
					BufferedWriter fw = null;

					try
					{
						fw = new BufferedWriter(new FileWriter(filename));

						fw.write("Schema Report");
						fw.newLine();
						for (int x = 0; x < output.size(); x++)
						{
							fw.write(output.get(x));
							fw.newLine();
						}
						fw.newLine();
						fw.append("End of Report.");
						fw.newLine();
						fw.flush();
						fw.close();
					}
					catch (IOException e)
					{
						JUtility.errorBeep();
						JOptionPane.showMessageDialog(Common.mainForm, e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
		else
		{
			JUtility.errorBeep();
			JOptionPane.showMessageDialog(Common.mainForm, "No tables in selected schema", "Export Error", JOptionPane.WARNING_MESSAGE);
		}
	}
}
