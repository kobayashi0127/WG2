package com.commander4j.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.commander4j.messages.GenericMessageHeader;
import com.commander4j.sys.Common;
import com.commander4j.util.JUtility;

public class JDBInterfaceRequest
{

	private Timestamp dbEventTime;
	private String dbInterfaceType;
	private Long dbTransactionRef;
	private Long dbInterfaceRequestID;
	private String dbWorkstationID;
	private String dbStatus;
	private String dbFilename;
	private String dbMode;
	private String dbErrorMessage;

	private final Logger logger = Logger.getLogger(JDBInterfaceRequest.class);

	private String hostID;

	private String sessionID;

	public String getStatus() {
		return dbStatus;
	}

	public void setStatus(String status) {
		dbStatus = status;
	}

	public boolean update(Long irid, String status) {
		Boolean result = false;
		setInterfaceRequestID(irid);
		setStatus(status);
		result = update();
		return result;
	}

	public boolean update() {
		boolean result = false;
		try
		{
			PreparedStatement stmtupdate;
			stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBInterfaceRequest.update"));
			stmtupdate.setString(1, getStatus());
			stmtupdate.setLong(2, getInterfaceRequestID());
			stmtupdate.execute();
			stmtupdate.clearParameters();
			if (Common.hostList.getHost(getHostID()).getConnection(getSessionID()).getAutoCommit() == false)
			{
				Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
			}
			stmtupdate.close();
			result = true;
		}
		catch (Exception e)
		{
			setErrorMessage(e.getMessage());
			result = false;
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

	public JDBInterfaceRequest(String host, String session)
	{
		setHostID(host);
		setSessionID(session);
	}

	public void clear() {
		setEventTime(null);
		setInterfaceType("");
		setStatus("");
	}

	public boolean create() {

		logger.debug("create [" + getInterfaceType() + "][" + String.valueOf(getTransactionRef()) + "]");

		boolean result = false;

		try
		{
			PreparedStatement stmtupdate;
			stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBInterfaceRequest.create"));
			stmtupdate.setTimestamp(1, getEventTime());
			stmtupdate.setLong(2, generateNewInterfaceRequestID());
			stmtupdate.setString(3, getInterfaceType());
			stmtupdate.setString(4, JUtility.getClientName());
			stmtupdate.setLong(5, getTransactionRef());
			stmtupdate.setString(6, "Ready");
			stmtupdate.setString(7, getFilename());
			stmtupdate.setString(8, getMode());
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

	public boolean delete() {
		PreparedStatement stmtupdate;
		boolean result = false;
		setErrorMessage("");

		try
		{

			stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBInterfaceRequest.delete"));
			stmtupdate.setLong(1, getInterfaceRequestID());
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

	public boolean delete(Long intReq) {
		setInterfaceRequestID(intReq);
		return delete();
	}

	public long generateNewInterfaceRequestID() {
		long result = 0;
		JDBControl ctrl = new JDBControl(getHostID(), getSessionID());
		String temp = "";
		String interfaceRequestID_str = "1";
		long interfaceRequestID = 1;

		boolean retry = true;
		@SuppressWarnings("unused")
		int counter = 0;

		if (ctrl.getProperties("INTERFACE REQUEST ID") == true)
		{
			do
			{
				if (ctrl.lockRecord("INTERFACE REQUEST ID") == true)
				{
					if (ctrl.getProperties("INTERFACE REQUEST ID") == true)
					{
						interfaceRequestID_str = ctrl.getKeyValue();
						interfaceRequestID = Long.parseLong(interfaceRequestID_str);
						interfaceRequestID++;
						temp = String.valueOf(interfaceRequestID);
						ctrl.setKeyValue(temp);

						if (ctrl.update())
						{
							retry = false;
						}
					}
				}
				else
				{
					logger.debug("Record Locked !");
					retry = true;
					counter++;
				}
			}
			while (retry);
		}
		else
		{
			ctrl.getKeyValueWithDefault("INTERFACE REQUEST ID", "1", "Unique Interface Request ID");
			interfaceRequestID = 1;
		}

		setInterfaceRequestID(interfaceRequestID);
		result = interfaceRequestID;

		logger.debug("InterfaceRequestID :" + result);
		return result;
	}

	public String getErrorMessage() {
		return dbErrorMessage;
	}

	public Timestamp getEventTime() {
		return dbEventTime;
	}

	private String getHostID() {
		return hostID;
	}

	private void setFilename(String filename) {
		dbFilename = filename;
	}

	public String getFilename() {
		return dbFilename;
	}

	private void setMode(String mode) {
		dbMode = mode;
	}

	public String getMode() {
		return dbMode;
	}

	public Long getInterfaceRequestID() {
		return dbInterfaceRequestID;
	}

	public LinkedList<Long> getInterfaceRequestIDs() {
		LinkedList<Long> intList = new LinkedList<Long>();
		PreparedStatement stmt;
		ResultSet rs;
		setErrorMessage("");

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBInterfaceRequest.getInterfaceRequestIDs"));
			stmt.setFetchSize(250);
			stmt.setString(1, "Ready");
			rs = stmt.executeQuery();

			while (rs.next())
			{
				intList.addLast(rs.getLong("interface_request_id"));
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

		return intList;
	}

	public void getPropertiesfromResultSet(ResultSet rs) {
		try
		{
			clear();
			setInterfaceRequestID(rs.getLong("interface_request_id"));
			setEventTime(rs.getTimestamp("event_time"));
			setInterfaceType(rs.getString("interface_type"));
			setWorkstationID(rs.getString("workstation_id"));
			setTransactionRef(rs.getLong("transaction_ref"));
			setStatus(rs.getString("status"));
			setFilename(rs.getString("filename"));
			setMode(rs.getString("request_mode"));
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
	}

	public boolean getInterfaceRequestProperties(Long requestID) {
		setInterfaceRequestID(requestID);
		boolean result = false;
		result = getInterfaceRequestProperties();
		return result;
	}

	public boolean getInterfaceRequestProperties() {
		PreparedStatement stmt;
		ResultSet rs;
		boolean result = false;
		setErrorMessage("");

		clear();

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBInterfaceRequest.getInterfaceRequestProperties"));
			stmt.setLong(1, getInterfaceRequestID());
			stmt.setFetchSize(1);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				getPropertiesfromResultSet(rs);
				result = true;
			}
			else
			{
				setErrorMessage("Invalid Interface Request ID [" + getInterfaceRequestID() + "]");
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

	public ResultSet getInterfaceRequestResultSet(PreparedStatement criteria) {

		ResultSet rs;

		try
		{
			rs = criteria.executeQuery();

		}
		catch (Exception e)
		{
			rs = null;
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

		return rs;
	}

	public String getInterfaceType() {
		return dbInterfaceType;
	}

	private String getSessionID() {
		return sessionID;
	}

	public Long getTransactionRef() {
		return dbTransactionRef;
	}

	public String getWorkstationID() {
		return dbWorkstationID;
	}

	private void setErrorMessage(String err) {
		dbErrorMessage = err;
	}

	public void setEventTime(Timestamp eventTime) {
		dbEventTime = eventTime;
	}

	private void setHostID(String host) {
		hostID = host;
	}

	public void setInterfaceRequestID(Long InterfaceLogID) {
		this.dbInterfaceRequestID = InterfaceLogID;
	}

	public void setInterfaceType(String type) {
		dbInterfaceType = type;
	}

	private void setSessionID(String session) {
		sessionID = session;
	}

	public void setTransactionRef(Long TransactionRef) {
		dbTransactionRef = TransactionRef;
	}

	public void setWorkstationID(String WorkstationID) {
		dbWorkstationID = WorkstationID;
	}

	public void write(GenericMessageHeader gmh, String messageStatus, String messageError, String action) {
		setEventTime(JUtility.getSQLDateTime());
		setInterfaceType(gmh.getInterfaceType());
		setFilename("");
		setMode("Normal");
		create();
	}

	public void write(Long transactionRef, String interfaceType) {
		setEventTime(JUtility.getSQLDateTime());
		setInterfaceType(interfaceType);
		setTransactionRef(transactionRef);
		setFilename("");
		setMode("Normal");
		create();
	}

	public void write(String filename, String interfaceType, String mode) {
		setEventTime(JUtility.getSQLDateTime());
		setInterfaceType(interfaceType);
		setTransactionRef(Long.valueOf(-1));
		setFilename(filename);
		setMode(mode);
		create();
	}

}
