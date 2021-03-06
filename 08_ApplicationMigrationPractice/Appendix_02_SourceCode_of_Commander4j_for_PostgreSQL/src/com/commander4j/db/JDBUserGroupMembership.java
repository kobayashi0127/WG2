/*
 * Created on 16-Jun-2005
 *
 */
package com.commander4j.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.commander4j.sys.Common;

/**
 * @author David
 * 
 * @version $Revision: 1.0 $
 */
public class JDBUserGroupMembership
{
	/**
	 * @uml.property name="db_error_message"
	 */
	private String db_error_message;
	/**
	 * @uml.property name="db_exception"
	 */
	private Exception db_exception;
	/**
	 * @uml.property name="db_group_id"
	 */
	private String db_group_id;
	/**
	 * @uml.property name="db_user_id"
	 */
	private String db_user_id;
	/**
	 * @uml.property name="logger"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	final Logger logger = Logger.getLogger(JDBUserGroupMembership.class);
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

	public JDBUserGroupMembership(String host, String session)
	{
		setHostID(host);
		setSessionID(session);
	}

	/**
	 * Method addUsertoGroup.
	 * 
	 * @return boolean
	 */
	public boolean addUsertoGroup() {
		boolean result = false;

		if (isValidUserGroupMembership() == true)
		{
			result = true;
		}
		else
		{
			try
			{
				logger.debug("addUsertoGroup User [" + getUserId() + "] Group [" + getGroupId() + "]");
				PreparedStatement stmtupdate;

				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUserGroupMembership.addUsertoGroup"));
				stmtupdate.setString(1, getUserId());
				stmtupdate.setString(2, getGroupId());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				stmtupdate.close();
				result = true;
			}
			catch (SQLException e)
			{
				setError(e);
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
		}
		return result;
	}

	/**
	 * Method create.
	 * 
	 * @param luser_id
	 *            String
	 * @param lgroup_id
	 *            String
	 * @return boolean
	 */
	public boolean create(String luser_id, String lgroup_id) {
		boolean result = false;
		setErrorMessage("");

		try
		{
			setGroupId(lgroup_id);
			setUserId(luser_id);

			if (isValidUserGroupMembership() == false)
			{
				PreparedStatement stmtupdate;
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUserGroupMembership.create"));
				stmtupdate.setString(1, getUserId());
				stmtupdate.setString(2, getGroupId());
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
			setError(e);
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
			if (isValidUserGroupMembership() == true)
			{
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUserGroupMembership.delete"));
				stmtupdate.setString(1, getUserId());
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
			setError(e);
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
	 * Method getGroupId.
	 * 
	 * @return String
	 */
	public String getGroupId() {
		return db_group_id;
	}

	/**
	 * Method getGroupsAssignedtoUser.
	 * 
	 * @return LinkedList<String>
	 */
	public LinkedList<String> getGroupsAssignedtoUser() {
		// int tempCount = 0;
		LinkedList<String> groupList = new LinkedList<String>();

		PreparedStatement stmt;
		ResultSet rs;
		// boolean result = false;
		setErrorMessage("");

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUserGroupMembership.getGroupsAssignedtoUser"));
			stmt.setString(1, getUserId());
			rs = stmt.executeQuery();
			while (rs.next())
			{
				groupList.addLast(rs.getString("group_id"));
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			setError(e);
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
	 * Method getUserId.
	 * 
	 * @return String
	 */
	public String getUserId() {
		return db_user_id;
	}

	/**
	 * Method isValidUserGroupMembership.
	 * 
	 * @return boolean
	 */
	public boolean isValidUserGroupMembership() {
		PreparedStatement stmt;
		ResultSet rs;
		boolean result = false;
		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUserGroupMembership.isValidUserGroupMembership"));
			stmt.setString(1, getUserId());
			stmt.setString(2, getGroupId());
			stmt.setFetchSize(1);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				result = true;
			}
			else
			{
				setErrorMessage("Invalid User Group Membership");
			}
			stmt.close();

		}
		catch (SQLException e)
		{
			setError(e);
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
	 * Method removeAllGroupsfromUser.
	 * 
	 * @return boolean
	 */
	public boolean removeAllGroupsfromUser() {
		PreparedStatement stmtupdate;
		boolean result = false;
		setErrorMessage("");
		logger.debug("removeAllGroupsfromUser User [" + getUserId() + "]");
		try
		{
			stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUserGroupMembership.removeAllGroupsfromUser"));
			stmtupdate.setString(1, getUserId());
			stmtupdate.execute();
			stmtupdate.clearParameters();
			Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
			stmtupdate.close();
			result = true;
		}
		catch (SQLException e)
		{
			setError(e);
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
	 * Method removeAllUsersfromGroup.
	 * 
	 * @return boolean
	 */
	public boolean removeAllUsersfromGroup() {
		PreparedStatement stmtupdate;
		boolean result = false;
		setErrorMessage("");

		try
		{
			stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUserGroupMembership.removeAllUsersfromGroup"));
			stmtupdate.setString(1, getGroupId());
			stmtupdate.execute();
			stmtupdate.clearParameters();
			Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
			stmtupdate.close();
			result = true;
		}
		catch (SQLException e)
		{
			setError(e);
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
	 * Method removeGroupfromUser.
	 * 
	 * @return boolean
	 */
	public boolean removeGroupfromUser() {
		boolean result = false;

		if (isValidUserGroupMembership() == false)
		{
			result = true;
		}
		else
		{
			try
			{
				logger.debug("removeGroupfromUser User [" + getUserId() + "] Group [" + getGroupId() + "]");
				PreparedStatement stmtupdate;

				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUserGroupMembership.removeGroupfromUser"));
				stmtupdate.setString(1, getUserId());
				stmtupdate.setString(2, getGroupId());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				stmtupdate.close();
				result = true;
			}
			catch (SQLException e)
			{
				setError(e);
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
		}
		return result;
	}

	/**
	 * Method removeUserfromGroup.
	 * 
	 * @return boolean
	 */
	public boolean removeUserfromGroup() {
		boolean result = false;

		// setUserId(luser_id);

		if (isValidUserGroupMembership() == false)
		{
			result = true;
		}
		else
		{
			try
			{
				logger.debug("removeUserfromGroup User [" + getUserId() + "] Group [" + getGroupId() + "]");
				PreparedStatement stmtupdate;

				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUserGroupMembership.removeUserfromGroup"));
				stmtupdate.setString(1, getUserId());
				stmtupdate.setString(2, getGroupId());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				stmtupdate.close();
				result = true;
			}
			catch (SQLException e)
			{
				setError(e);
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
		}
		return result;
	}

	/**
	 * Method renameGroupTo.
	 * 
	 * @param lgroup_id
	 *            String
	 * @return boolean
	 */
	public boolean renameGroupTo(String lgroup_id) {
		boolean result = false;

		try
		{
			PreparedStatement stmtupdate;
			stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUserGroupMembership.renameGroupTo"));
			stmtupdate.setString(1, lgroup_id);
			stmtupdate.setString(2, getGroupId());
			stmtupdate.execute();
			stmtupdate.clearParameters();
			Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
			stmtupdate.close();
			result = true;
		}
		catch (SQLException e)
		{
			setError(e);
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
	 * Method renameUserTo.
	 * 
	 * @param luser_id
	 *            String
	 * @return boolean
	 */
	public boolean renameUserTo(String luser_id) {
		boolean result = false;

		try
		{
			PreparedStatement stmtupdate;
			stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUserGroupMembership.renameUserTo"));
			stmtupdate.setString(1, luser_id);
			stmtupdate.setString(2, getUserId());
			stmtupdate.execute();
			stmtupdate.clearParameters();
			Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
			stmtupdate.close();
			result = true;
		}
		catch (SQLException e)
		{
			setError(e);
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
	 * Method setError.
	 * 
	 * @param e
	 *            Exception
	 */
	private void setError(Exception e) {
		logger.error(e);
		db_exception = e;
		setErrorMessage(e.getMessage());
	}

	/**
	 * Method getException.
	 * 
	 * @return Exception
	 */
	public Exception getException() {
		return db_exception;
	}

	/**
	 * Method setErrorMessage.
	 * 
	 * @param ErrorMsg
	 *            String
	 */
	private void setErrorMessage(String ErrorMsg) {
		db_error_message = ErrorMsg;
	}

	/**
	 * Method getErrorMessage.
	 * 
	 * @return String
	 */
	public String getErrorMessage() {
		return db_error_message;
	}

	/**
	 * Method setGroupId.
	 * 
	 * @param GroupId
	 *            String
	 */
	public void setGroupId(String GroupId) {
		db_group_id = GroupId;
	}

	/**
	 * Method setUserId.
	 * 
	 * @param UserId
	 *            String
	 */
	public void setUserId(String UserId) {
		db_user_id = UserId;
	}
}
