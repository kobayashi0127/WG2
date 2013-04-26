// $codepro.audit.disable numericLiterals
package com.commander4j.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.commander4j.sys.Common;
import com.commander4j.util.JUtility;

/**
 */
public class JDBMaterialType
{
	/**
	 * @uml.property name="dbErrorMessage"
	 */
	private String dbErrorMessage;
	/**
	 * @uml.property name="dbMaterialDescription"
	 */
	private String dbMaterialDescription;
	/**
	 * @uml.property name="dbMaterialType"
	 */
	private String dbMaterialType;

	/* Material Type */
	/**
	 * Field field_material_type. Value: {@value field_material_type}
	 */
	public static int field_material_type = 5;
	/**
	 * Field field_description. Value: {@value field_description}
	 */
	public static int field_description = 80;

	/**
	 * @uml.property name="logger"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private final Logger logger = Logger.getLogger(JDBMaterialType.class);
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

	public JDBMaterialType(String host, String session)
	{
		setHostID(host);
		setSessionID(session);

	}

	/**
	 * Method create.
	 * 
	 * @param ltype
	 *            String
	 * @param ldescription
	 *            String
	 * @return boolean
	 */
	public boolean create(String ltype, String ldescription) {
		boolean result = false;
		setErrorMessage("");

		try
		{
			setType(ltype);

			setDescription(ldescription);

			if (isValidMaterialType() == false)
			{
				PreparedStatement stmtupdate;
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBMaterialType.create"));
				stmtupdate.setString(1, getType());
				stmtupdate.setString(2, getDescription());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				stmtupdate.close();
				result = true;
			}
			else
			{
				setErrorMessage("Type already exists");
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
	 * Method update.
	 * 
	 * @return boolean
	 */
	public boolean update() {
		boolean result = false;
		setErrorMessage("");

		try
		{
			if (isValidMaterialType() == true)
			{
				PreparedStatement stmtupdate;
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBMaterialType.update"));
				stmtupdate.setString(1, getDescription());
				stmtupdate.setString(2, getType());
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
			if (isValidMaterialType() == true)
			{
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBMaterialType.delete"));
				stmtupdate.setString(1, getType());
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
	 * Method renameTo.
	 * 
	 * @param newType
	 *            String
	 * @return boolean
	 */
	public boolean renameTo(String newType) {
		PreparedStatement stmtupdate;
		boolean result = false;
		setErrorMessage("");
		try
		{
			if (isValidMaterialType() == true)
			{
				JDBMaterialType mattype = new JDBMaterialType(getHostID(), getSessionID());
				mattype.setType(newType);
				if (mattype.isValidMaterialType() == false)
				{
					stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBMaterialType.renameTo"));
					stmtupdate.setString(1, newType);
					stmtupdate.setString(2, getType());
					stmtupdate.execute();
					stmtupdate.clearParameters();

					Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
					stmtupdate.close();

					setType(newType);
					result = true;
				}
				else
				{
					setErrorMessage("New Type is already in use.");
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
	 * Method isValidMaterialType.
	 * 
	 * @return boolean
	 */
	public boolean isValidMaterialType() {
		PreparedStatement stmt;
		ResultSet rs;
		boolean result = false;

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBMaterialType.isValidMaterialType"));
			stmt.setString(1, getType());
			stmt.setFetchSize(1);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				result = true;
			}
			else
			{
				setErrorMessage("Invalid Type [" + getType() + "]");
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
	
	public boolean isValidMaterialType(String type)
	{
		setType(type);
		return isValidMaterialType();
	}

	/**
	 * Constructor for JDBMaterialType.
	 * 
	 * @param type
	 *            String
	 * @param description
	 *            String
	 */
	public JDBMaterialType(String host, String session, String type, String description)
	{
		setHostID(host);
		setSessionID(session);
		setType(type);
		setDescription(description);
	}

	/**
	 * Method getDescription.
	 * 
	 * @return String
	 */
	public String getDescription() {
		String result = "";
		if (dbMaterialDescription != null)
			result = dbMaterialDescription;
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

	public void clear() {
		// setType("");
		setDescription("");
		setErrorMessage("");
	}

	/**
	 * Method getMaterialTypeProperties.
	 * 
	 * @return boolean
	 */
	public boolean getMaterialTypeProperties() {
		boolean result = false;

		PreparedStatement stmt;
		ResultSet rs;
		setErrorMessage("");
		logger.debug("getMaterialTypeProperties");

		clear();

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBMaterialType.getMaterialTypeProperties"));
			stmt.setString(1, getType());
			stmt.setFetchSize(1);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				setDescription(rs.getString("description"));
				result = true;
			}
			else
			{
				setErrorMessage("Invalid Material Type");
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
	 * Method getMaterialTypes.
	 * 
	 * @return Vector<JDBMaterialType>
	 */
	public Vector<JDBMaterialType> getMaterialTypes() {
		Vector<JDBMaterialType> typeList = new Vector<JDBMaterialType>();
		PreparedStatement stmt;
		ResultSet rs;
		setErrorMessage("");

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBMaterialType.getMaterialTypes"));
			stmt.setFetchSize(25);
			rs = stmt.executeQuery();

			while (rs.next())
			{
				JDBMaterialType mt = new JDBMaterialType(getHostID(), getSessionID());
				mt.setType(rs.getString("material_type"));
				mt.setDescription(rs.getString("description"));
				typeList.add(mt);
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

		return typeList;
	}

	/**
	 * Method getType.
	 * 
	 * @return String
	 */
	public String getType() {
		String result = "";
		if (dbMaterialType != null)
			result = dbMaterialType;
		return result;
	}

	/**
	 * Method setDescription.
	 * 
	 * @param description
	 *            String
	 */
	public void setDescription(String description) {
		dbMaterialDescription = description;
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
	 * Method setType.
	 * 
	 * @param type
	 *            String
	 */
	public void setType(String type) {
		dbMaterialType = type;
	}

	/**
	 * Method toString.
	 * 
	 * @return String
	 */
	public String toString() {
		String result = "";
		if (getType().equals("") == false)
		{
			result = JUtility.padString(getType(), true, field_material_type, " ") + " - " + getDescription();
		}
		else
		{
			result = "";
		}

		return result;
	}
}
