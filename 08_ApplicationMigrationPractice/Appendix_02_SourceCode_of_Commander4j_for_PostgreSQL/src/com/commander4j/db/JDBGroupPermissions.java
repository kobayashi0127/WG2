// $codepro.audit.disable numericLiterals
/*
 * Created on 15-Jun-2005
 *
 */
package com.commander4j.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.swing.Icon;

import org.apache.log4j.Logger;

import com.commander4j.sys.Common;

/**
 * @author David
 * 
 * @version $Revision: 1.0 $
 */
public class JDBGroupPermissions
{
	/**
	 * @uml.property name="dbErrorMessage"
	 */
	private String dbErrorMessage;

	/**
	 * @uml.property name="dbGroupId"
	 */
	private String dbGroupId;

	/**
	 * @uml.property name="dbModuleId"
	 */
	private String dbModuleId;

	/**
	 * @uml.property name="logger"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private Logger logger = Logger.getLogger(JDBGroupPermissions.class);
	private String hostID;
	private String sessionID;

	private void setSessionID(String session) {
		sessionID = session;
	}

	private void setHostID(String host) {
		hostID = host;
	}

	private String getSessionID() {
		return sessionID;
	}

	private String getHostID() {
		return hostID;
	}

	public JDBGroupPermissions(String host, String session)
	{
		// Get number of days that a password lasts before it needs changing.
		setHostID(host);
		setSessionID(session);
	}

	/**
	 * Method create.
	 * 
	 * @param lGroupId
	 *            String
	 * @param lModuleId
	 *            String
	 * @return boolean
	 */
	public boolean create(String lGroupId, String lModuleId) {
		boolean result = false;
		setErrorMessage("");

		try
		{
			setGroupId(lGroupId);
			setModuleId(lModuleId);

			if (isValidGroupPermission() == false)
			{
				PreparedStatement stmtupdate;
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBGroupPermissions.create"));
				stmtupdate.setString(1, getGroupId());
				stmtupdate.setString(2, getModuleId());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				stmtupdate.close();
				result = true;
			}
			else
			{
				setErrorMessage("Group Permission already exists");
			}
		}
		catch (SQLException e)
		{
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

	/**
	 * Method delete.
	 * 
	 * @param groupid
	 *            String
	 * @param moduleid
	 *            String
	 * @return boolean
	 */
	public boolean delete(String groupid, String moduleid) {
		setGroupId(groupid);
		setModuleId(moduleid);
		return delete();
	}

	/**
	 * Method deletePermissionsForModule.
	 * 
	 * @param moduleid
	 *            String
	 * @return boolean
	 */
	public boolean deletePermissionsForModule(String moduleid) {
		setModuleId(moduleid);
		return deletePermissionsForModule();
	}

	/**
	 * Method deletePermissionsForModule.
	 * 
	 * @return boolean
	 */
	public boolean deletePermissionsForModule() {
		PreparedStatement stmtupdate;
		boolean result = false;
		setErrorMessage("");

		try
		{

			stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBGroupPermissions.deletePermissionsForModule"));
			stmtupdate.setString(1, getModuleId());
			stmtupdate.execute();
			stmtupdate.clearParameters();
			Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
			stmtupdate.close();
			result = true;

		}
		catch (SQLException e)
		{
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

	/**
	 * Method delete.
	 * 
	 * @return boolean
	 */
	public boolean delete() {
		PreparedStatement stmtupdate;
		boolean result = false;
		setErrorMessage("");

		try
		{
			if (isValidGroupPermission() == true)
			{
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBGroupPermissions.delete"));
				stmtupdate.setString(1, getGroupId());
				stmtupdate.setString(2, getModuleId());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				stmtupdate.close();
				result = true;
			}
		}
		catch (SQLException e)
		{
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

	/**
	 * Method getErrorMessage.
	 * 
	 * @return String
	 */
	public String getErrorMessage() {
		return dbErrorMessage;
	}

	/**
	 * Method getGroupId.
	 * 
	 * @return String
	 */
	public String getGroupId() {
		return dbGroupId;
	}

	/**
	 * Method getModuleId.
	 * 
	 * @return String
	 */
	public String getModuleId() {
		return dbModuleId;
	}

	/**
	 * Method getModulesAssigned.
	 * 
	 * @return LinkedList<JDBListData>
	 */
	public LinkedList<JDBListData> getModulesAssigned(String host, String session) {
		LinkedList<JDBListData> moduleList = new LinkedList<JDBListData>();
		Icon icon;

		PreparedStatement stmt;
		ResultSet rs;
		setHostID(host);
		setSessionID(session);
		setErrorMessage("");

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBGroupPermissions.getModulesAssigned"));
			stmt.setString(1, getGroupId());
			rs = stmt.executeQuery();
			while (rs.next())
			{
				icon = JDBModule.getModuleIcon(rs.getString("icon_filename"), rs.getString("module_type"));
				JDBListData mld = new JDBListData(icon, 0, true, rs.getString("module_id"));
				moduleList.addLast(mld);
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{
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

		return moduleList;
	}

	/**
	 * Method getModulesUnAssigned.
	 * 
	 * @return LinkedList<JDBListData>
	 */
	public LinkedList<JDBListData> getModulesUnAssigned(String host, String session) {
		LinkedList<JDBListData> moduleList = new LinkedList<JDBListData>();
		Icon icon;
		PreparedStatement stmt;
		ResultSet rs;
		setHostID(host);
		setSessionID(session);
		setErrorMessage("");

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBGroupPermissions.getModulesUnAssigned"));
			stmt.setString(1, getGroupId());
			rs = stmt.executeQuery();
			while (rs.next())
			{
				icon = JDBModule.getModuleIcon(rs.getString("icon_filename"), rs.getString("module_type"));
				JDBListData mld = new JDBListData(icon, 0, true, rs.getString("module_id"));
				moduleList.addLast(mld);
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{
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
		return moduleList;
	}

	/**
	 * Method isValidGroupPermission.
	 * 
	 * @return boolean
	 */
	public boolean isValidGroupPermission() {
		PreparedStatement stmt;
		ResultSet rs;
		boolean result = false;

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBGroupPermissions.isValidGroupPermission"));
			stmt.setString(1, getGroupId());
			stmt.setString(2, getModuleId());
			stmt.setFetchSize(1);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				result = true;
			}
			else
			{
				setErrorMessage("Invalid Group Permission");
			}
			stmt.close();
			rs.close();

		}
		catch (SQLException e)
		{
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

	/**
	 * Method removeModulesfromGroup.
	 * 
	 * @return boolean
	 */
	public boolean removeModulesfromGroup() {
		PreparedStatement stmtupdate;
		boolean result = false;
		setErrorMessage("");

		try
		{
			stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBGroupPermissions.removeModulesfromGroup"));
			stmtupdate.setString(1, getGroupId());
			stmtupdate.execute();
			stmtupdate.clearParameters();
			Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
			stmtupdate.close();
			result = true;
		}
		catch (SQLException e)
		{
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

	/**
	 * Method renameGroupTo.
	 * 
	 * @param lGroupId
	 *            String
	 * @return boolean
	 */
	public boolean renameGroupTo(String lGroupId) {
		boolean result = false;

		try
		{
			PreparedStatement stmtupdate;
			stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBGroupPermissions.renameGroupTo"));
			stmtupdate.setString(1, lGroupId);
			stmtupdate.setString(2, getGroupId());
			stmtupdate.execute();
			stmtupdate.clearParameters();
			Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
			stmtupdate.close();
			result = true;
		}
		catch (SQLException e)
		{
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

	/**
	 * Method renameModuleTo.
	 * 
	 * @param lModuleId
	 *            String
	 * @return boolean
	 */
	public boolean renameModuleTo(String lModuleId) {
		boolean result = false;

		try
		{
			PreparedStatement stmtupdate;
			stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBGroupPermissions.renameModuleTo"));
			stmtupdate.setString(1, lModuleId);
			stmtupdate.setString(2, getModuleId());
			stmtupdate.execute();
			stmtupdate.clearParameters();
			Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
			stmtupdate.close();
			result = true;
		}
		catch (SQLException e)
		{
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

	/**
	 * Method setErrorMessage.
	 * 
	 * @param errorMsg
	 *            String
	 */
	private void setErrorMessage(String errorMsg) {
		if (errorMsg.isEmpty() == false)
		{
			logger.error(errorMsg);
		}
		dbErrorMessage = errorMsg;
	}

	/**
	 * Method setGroupId.
	 * 
	 * @param groupId
	 *            String
	 */
	public void setGroupId(String groupId) {
		dbGroupId = groupId;
	}

	/**
	 * Method setModuleId.
	 * 
	 * @param moduleId
	 *            String
	 */
	public void setModuleId(String moduleId) {
		dbModuleId = moduleId;
	}
}
