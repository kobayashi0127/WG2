// $codepro.audit.disable numericLiterals
/*
 * Created on 11-Jan-2005
 *
 */
package com.commander4j.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.commander4j.sys.Common;

/**
 * @author David
 * 
 * @version $Revision: 1.0 $
 */
public class JDBGroup
{
	/**
	 * @uml.property name="dbDescription"
	 */
	private String dbDescription;
	/**
	 * @uml.property name="dbErrorMessage"
	 */
	private String dbErrorMessage;
	/**
	 * @uml.property name="dbGroupId"
	 */
	private String dbGroupId;

	/* Groups */
	/**
	 * Field field_group_id. Value: {@value field_group_id} Value: {@value
	 * field_group_id}
	 */
	public static int field_group_id = 20;
	/**
	 * Field field_description. Value: {@value field_description} Value:
	 * {@value field_description}
	 */
	public static int field_description = 80;

	/**
	 * @uml.property name="groupPermissions"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JDBGroupPermissions groupPermissions;

	/**
	 * @uml.property name="userGroupMembership"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JDBUserGroupMembership userGroupMembership;

	/**
	 * @uml.property name="logger"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private final Logger logger = Logger.getLogger(JDBGroup.class);
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

	public JDBGroup(String host, String session)
	{
		// Get number of days that a password lasts before it needs changing.
		setHostID(host);
		setSessionID(session);
		groupPermissions = new JDBGroupPermissions(getHostID(), getSessionID());
		userGroupMembership = new JDBUserGroupMembership(getHostID(), getSessionID());
	}

	public void clear() {
		setDescription("");
	}

	/**
	 * Method addModule.
	 * 
	 * @param lModuleId
	 *            String
	 * @return boolean
	 */
	public boolean addModule(String lModuleId) {
		boolean result = false;

		if (isValidGroupId() == true)
		{
			result = groupPermissions.create(getGroupId(), lModuleId);
			setErrorMessage(groupPermissions.getErrorMessage());
		}

		return result;
	}

	/**
	 * Method create.
	 * 
	 * @param lGroupId
	 *            String
	 * @param ldescription
	 *            String
	 * @return boolean
	 */
	public boolean create(String lGroupId, String ldescription) {
		boolean result = false;
		setErrorMessage("");

		try
		{
			setGroupId(lGroupId);
			setDescription(ldescription);

			if (isValidGroupId() == false)
			{
				PreparedStatement stmtupdate;
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBGroup.create"));
				stmtupdate.setString(1, getGroupId());
				stmtupdate.setString(2, getDescription());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				stmtupdate.close();
				result = true;
			}
			else
			{
				setErrorMessage("Group Id already exists");
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
	 * @return boolean
	 */
	public boolean delete() {
		PreparedStatement stmtupdate;
		boolean result = false;
		setErrorMessage("");

		try
		{
			if (isValidGroupId() == true)
			{

				userGroupMembership.setGroupId(getGroupId());
				userGroupMembership.removeAllUsersfromGroup();

				groupPermissions.setGroupId(getGroupId());
				groupPermissions.removeModulesfromGroup();

				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBGroup.delete"));
				stmtupdate.setString(1, getGroupId());
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
	 * Method getDescription.
	 * 
	 * @return String
	 */
	public String getDescription() {
		return dbDescription;
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
	 * Method getGroupIds.
	 * 
	 * @return LinkedList<String>
	 */
	public LinkedList<String> getGroupIds() {
		LinkedList<String> groupList = new LinkedList<String>();

		Statement stmt;
		ResultSet rs;
		setErrorMessage("");

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).createStatement();
			stmt.setFetchSize(50);
			rs = stmt.executeQuery(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBGroup.getGroupIds"));

			while (rs.next())
			{
				groupList.addLast(rs.getString("group_id"));
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

		return groupList;
	}

	/**
	 * Method getGroupProperties.
	 * 
	 * @return boolean
	 */
	public boolean getGroupProperties() {
		PreparedStatement stmt;
		ResultSet rs;
		boolean result = false;
		setErrorMessage("");

		clear();

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBGroup.getGroupProperties"));
			stmt.setString(1, getGroupId());
			stmt.setFetchSize(1);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				setDescription(rs.getString("description"));
				result = true;
			}
			else
			{
				setErrorMessage("Invalid GroupId");
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

		return result;
	}

	/**
	 * Method getModulesAssigned.
	 * 
	 * @return LinkedList<JDBListData>
	 */
	public LinkedList<JDBListData> getModulesAssigned() {
		LinkedList<JDBListData> moduleList = new LinkedList<JDBListData>();

		groupPermissions.setGroupId(getGroupId());
		moduleList = groupPermissions.getModulesAssigned(getHostID(), getSessionID());

		return moduleList;
	}

	/**
	 * Method getModulesUnAssigned.
	 * 
	 * @return LinkedList<JDBListData>
	 */
	public LinkedList<JDBListData> getModulesUnAssigned() {
		LinkedList<JDBListData> moduleList = new LinkedList<JDBListData>();

		groupPermissions.setGroupId(getGroupId());
		moduleList = groupPermissions.getModulesUnAssigned(getHostID(), getSessionID());

		return moduleList;
	}

	/**
	 * Method isValidGroupId.
	 * 
	 * @return boolean
	 */
	public boolean isValidGroupId() {
		PreparedStatement stmt;
		ResultSet rs;
		boolean result = false;

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBGroup.isValidGroupId"));
			stmt.setFetchSize(1);
			stmt.setString(1, getGroupId());
			rs = stmt.executeQuery();

			if (rs.next())
			{
				result = true;
			}
			else
			{
				setErrorMessage("Invalid GroupId");
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
	 * Method removeModule.
	 * 
	 * @param lModuleId
	 *            String
	 * @return boolean
	 */
	public boolean removeModule(String lModuleId) {
		boolean result = false;

		if (isValidGroupId() == true)
		{
			groupPermissions.setGroupId(getGroupId());
			groupPermissions.setModuleId(lModuleId);
			result = groupPermissions.delete();
			setErrorMessage(groupPermissions.getErrorMessage());
		}

		return result;
	}

	/**
	 * Method renameTo.
	 * 
	 * @param newGroupId
	 *            String
	 * @return boolean
	 */
	public boolean renameTo(String newGroupId) {
		PreparedStatement stmtupdate;
		boolean result = false;
		setErrorMessage("");
		try
		{
			if (isValidGroupId() == true)
			{
				JDBGroup newgroup = new JDBGroup(getHostID(), getSessionID());
				newgroup.setGroupId(newGroupId);
				if (newgroup.isValidGroupId() == false)
				{
					groupPermissions.setGroupId(getGroupId());
					groupPermissions.renameGroupTo(newGroupId);

					userGroupMembership.setGroupId(getGroupId());
					userGroupMembership.renameGroupTo(newGroupId);

					stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBGroup.renameTo"));
					stmtupdate.setString(1, newGroupId);
					stmtupdate.setString(2, getGroupId());
					stmtupdate.execute();
					stmtupdate.clearParameters();

					Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
					stmtupdate.close();

					setGroupId(newGroupId);
					result = true;
				}
				else
				{
					setErrorMessage("New group_id is already in use.");
				}
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
	 * Method setDescription.
	 * 
	 * @param description
	 *            String
	 */
	public void setDescription(String description) {
		dbDescription = description;
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
	 * Method update.
	 * 
	 * @return boolean
	 */
	public boolean update() {
		boolean result = false;
		setErrorMessage("");

		try
		{
			if (isValidGroupId() == true)
			{
				PreparedStatement stmtupdate;
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBGroup.update"));
				stmtupdate.setString(1, getDescription());
				stmtupdate.setString(2, getGroupId());
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

}
