// $codepro.audit.disable numericLiterals
package com.commander4j.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.commander4j.sys.Common;
import com.commander4j.util.JUtility;


/**
 */
public class JDBQMInspection
{
	private Long dbExtensionID;
	private String dbDescription;
	private String dbInspectionID;
	private String dbErrorMessage;
	public static int field_activity_id = 20;
	private final Logger logger = Logger.getLogger(JDBQMInspection.class);
	private String hostID;
	private String sessionID;
	private JDBQMExtension extension;
	
	/*
	 * 
			Table APP_QM_INSPECTION
			=======================
			INSPECTION_ID
			varchar(20) PK
			DESCRIPTION
			varchar(50)
			EXTENSION_ID
			int(11)

	 * 
	 */
	
	public String toString()
	{
		String result = "";
		if (getInspectionID().equals("") == false) {
			result = JUtility.padString(getInspectionID(), true, field_activity_id, " ") + " - " + getDescription();
		}
		else {
			result = "";
		}

		return result;
	}

	public JDBQMInspection(String host, String session) {
		setHostID(host);
		setSessionID(session);
		extension = new JDBQMExtension(host,session);
	}

	public JDBQMInspection(String host, String session, String inpectionid, String description, Long extensionid) {
		setHostID(host);
		setSessionID(session);
		setInspectionID(inpectionid);
		setDescription(description);
		setExtensionID(extensionid);
	}

	public void clear() {
		setDescription("");
		setExtensionID((long) -1);
	}

	public boolean create(String inspectionid, String description) {
		boolean result = false;
		setErrorMessage("");

		try {

			setInspectionID(inspectionid);
			setDescription(description);

			if (isValid() == false) {
				PreparedStatement stmtupdate;
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBQMInspection.create"));
				stmtupdate.setString(1, getInspectionID());
				stmtupdate.setString(2, getDescription());
				setExtensionID(extension.generateExtensionID());
				stmtupdate.setLong(3,getExtensionID());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				stmtupdate.close();
				result = true;
			}
			else {
				setErrorMessage("QMInspection item already exists");
			}
		}
		catch (SQLException e) {
			setErrorMessage(e.getMessage());
			//PG add ->
			String driver = Common.hostList.getHost(getHostID()).getDatabaseParameters().getjdbcDriver();
			if (driver.equals("org.postgresql.Driver")){
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

		return result;
	}

	public boolean delete() {
		PreparedStatement stmtupdate;
		boolean result = false;
		setErrorMessage("");

		try {
				if (isValid() == true) {
					stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBQMInspection.delete"));
					stmtupdate.setString(1, getInspectionID());
					stmtupdate.execute();
					stmtupdate.clearParameters();
					Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
					stmtupdate.close();
					result = true;
				}
		}
		catch (Exception e) {
			setErrorMessage(e.getMessage());
			//PG add ->
			String driver = Common.hostList.getHost(getHostID()).getDatabaseParameters().getjdbcDriver();
			if (driver.equals("org.postgresql.Driver")){
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

		return result;
	}

	public boolean getProperties(String inspectionID) {
		setInspectionID(inspectionID);
		return getProperties();
	}

	public String getErrorMessage() {
		return dbErrorMessage;
	}

	public String getDescription() {
		String result = "";
		if (dbDescription != null)
			result = dbDescription;
		return result;
	}

	private String getHostID() {
		return hostID;
	}


	public String getInspectionID() {
		String result = "";
		if (dbInspectionID != null)
			result = dbInspectionID;
		return result;
	}

	public boolean getProperties() {
		boolean result = false;

		PreparedStatement stmt;
		ResultSet rs;
		setErrorMessage("");
		logger.debug("JDBQMInspection getProperties Inspection ["+getInspectionID()+"]");

		clear();

		try {
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBQMInspection.getProperties"));
			stmt.setString(1,getInspectionID());
			stmt.setFetchSize(1);
			rs = stmt.executeQuery();

			if (rs.next()) {
				setDescription(rs.getString("description"));
				setExtensionID(rs.getLong("extension_id"));
				result = true;
				rs.close();
				stmt.close();
			}
			else {
				setErrorMessage("Invalid Inspection ID ["+getInspectionID()+"]");
			}
		}
		catch (SQLException e) {
			setErrorMessage(e.getMessage());
			//PG add ->
			String driver = Common.hostList.getHost(getHostID()).getDatabaseParameters().getjdbcDriver();
			if (driver.equals("org.postgresql.Driver")){
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
		return result;
	}
	
	
	private String getSessionID() {
		return sessionID;
	}	

	public Long getExtensionID() {
		Long result = (long) -1;
		if (dbExtensionID != null)
			result = dbExtensionID;
		return result;
	}

	public boolean isValid(String inspect)
	{
		setInspectionID(inspect);
		return isValid();
	}
	
	public boolean isValid() {
		PreparedStatement stmt;
		ResultSet rs;
		boolean result = false;

		try {
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBQMInspection.isValid"));
			stmt.setString(1, getInspectionID());
			stmt.setFetchSize(1);
			rs = stmt.executeQuery();

			if (rs.next()) {
				result = true;
			}
			else {
				setErrorMessage("Invalid Inspection [" + getInspectionID().toString() + "]");
			}
			rs.close();
			stmt.close();

		}
		catch (SQLException e) {
			setErrorMessage(e.getMessage());
			//PG add ->
			String driver = Common.hostList.getHost(getHostID()).getDatabaseParameters().getjdbcDriver();
			if (driver.equals("org.postgresql.Driver")){
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

		return result;

	}

	private void setErrorMessage(String errorMsg) {
		if (errorMsg.isEmpty() == false) {
			logger.error(errorMsg);
		}
		dbErrorMessage = errorMsg;
	}

	public void setDescription(String description) {
		dbDescription = description;
	}

	private void setHostID(String host) {
		hostID = host;
	}
	
	public void setInspectionID(String inpectionid) {
		dbInspectionID = inpectionid;
	}

	private void setSessionID(String session) {
		sessionID = session;
	}

	public void setExtensionID(Long extensionid) {
		dbExtensionID = extensionid;
	}

	public boolean update() {
		boolean result = false;
		setErrorMessage("");

		try {
			if (isValid() == true) {
				PreparedStatement stmtupdate;
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBQMInspection.update"));
				stmtupdate.setString(1, getDescription());
				stmtupdate.setString(2, getInspectionID());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				stmtupdate.close();
				result = true;
			}
		}
		catch (SQLException e) {
			setErrorMessage(e.getMessage());
			//PG add ->
			String driver = Common.hostList.getHost(getHostID()).getDatabaseParameters().getjdbcDriver();
			if (driver.equals("org.postgresql.Driver")){
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

		return result;
	}
}
