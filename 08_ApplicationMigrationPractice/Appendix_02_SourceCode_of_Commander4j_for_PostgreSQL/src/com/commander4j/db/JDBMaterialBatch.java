// $codepro.audit.disable numericLiterals
package com.commander4j.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.commander4j.sys.Common;
import com.commander4j.util.JUtility;

public class JDBMaterialBatch
{
	public static int field_batch_number = 12;
	private String dbErrorMessage;
	private String dbMaterial;
	private String dbMaterialBatch;
	private Timestamp dbMaterialBatchExpiry;
	private String dbMaterialStatus;
	private final Logger logger = Logger.getLogger(JDBMaterialBatch.class);
	private String hostID;
	private String sessionID;
	private JDBMaterial mat;
	private JDBControl ctrl;
	private String batchREGEX;
	private String expiryMode;

	public JDBMaterialBatch(ResultSet rs)
	{
		getPropertiesfromResultSet(rs);
	}

	public JDBMaterialBatch(String host, String session)
	{
		// Get number of days that a password lasts before it needs changing.
		setHostID(host);
		setSessionID(session);
		mat = new JDBMaterial(getHostID(), getSessionID());
		ctrl = new JDBControl(getHostID(), getSessionID());

		batchREGEX = ctrl.getKeyValue("BATCH REGEX");
		expiryMode = ctrl.getKeyValue("EXPIRY DATE MODE");
	}

	public JDBMaterialBatch(String material, String batch, String status, Timestamp expiry)
	{
		setMaterial(material);
		setBatch(batch);
		setStatus(status);
		setExpiryDate(expiry);
	}

	public JDBMaterialBatch(String material, String batch, Timestamp expiry, String status)
	{
		setMaterial(material);
		setBatch(batch);
		setExpiryDate(expiry);
		setStatus(status);
	}

	public Boolean autoCreateMaterialBatch(String material, String batch, Timestamp expiryDate, String status) {

		Timestamp expiry;

		if (expiryMode.equals("SSCC"))
		{
			expiry = new Timestamp(0);
		}
		else
		{
			expiry = expiryDate;
		}

		setMaterial(material);
		setBatch(batch);

		Boolean result = false;

		int t1 = JUtility.replaceNullStringwithBlank(getMaterial()).length();
		int t2 = JUtility.replaceNullStringwithBlank(getBatch()).length();

		if ((t1 > 0) & (t2 > 0))
		{
			if (JUtility.isStringPatternValid(batchREGEX, getBatch()))
			{
				if (getMaterialBatchProperties() == false)
				{

					if (expiry != null)
					{
						setExpiryDate(expiry);
					}
					else
					{
						if (mat.getMaterialProperties(getMaterial()) == true)
						{
							setExpiryDate(expiry);
						}
						else
						{
							setErrorMessage("Unknown Material " + getMaterial());
						}
					}

					if (status.length() > 0)
					{
						setStatus(status);
					}
					else
					{
						setStatus(mat.getDefaultBatchStatus());
					}

					if (getStatus().equalsIgnoreCase(""))
					{
						setStatus(ctrl.getKeyValueWithDefault("DEFAULT BATCH STATUS", "Restricted", "Default Batch Status"));
					}

					if (create())
					{
						result = true;

					}

				}
				else
				{
					if (getExpiryDate() != null)
					{
						if (expiry != null)
						{
							if (expiry.equals(getExpiryDate()) == false)
							{
								setErrorMessage("Cannot override batch expiry date. Batch ["+getMaterial()+"/"+getBatch()+"] already exists with expiry date of " + getExpiryDate().toString());
							}
							else
							{
								result = true;
							}
						}
						else
						{
							result = true;
						}
					}
					else
					{
						result = true;
					}
				}
			}
			else
			{
				setErrorMessage("The format of the batch number ["+getBatch()+"] is not valid.\nFormat of batch number is defined in control key BATCH REGEX.");
			}
		}
		else
		{
			result = true;
		}

		return result;
	}

	public void clear() {
		/*
		 * setMaterial(""); setBatch("");
		 */
		setExpiryDate(null);
		setStatus("");
	}

	public boolean create() {

		logger.debug("create [" + getMaterial() + "][" + getBatch() + "]");

		boolean result = false;

		if (isValid() == true)
		{
			try
			{
				PreparedStatement stmtupdate;
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBMaterialBatch.create"));
				stmtupdate.setString(1, getMaterial());
				stmtupdate.setString(2, getBatch());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				stmtupdate.close();
				update();
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
		}

		return result;
	}

	public boolean delete() {
		PreparedStatement stmtupdate;
		boolean result = false;
		setErrorMessage("");

		try
		{
			stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBMaterialBatch.delete"));
			stmtupdate.setString(1, getMaterial());
			stmtupdate.setString(2, getBatch());
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

	public boolean delete(String material, String batch) {
		boolean result = false;
		setMaterial(material);
		setBatch(batch);
		result = delete();

		return result;
	}

	public String getBatch() {
		return JUtility.replaceNullStringwithBlank(dbMaterialBatch);
	}

	public String getDefaultBatchNumber(String batchFormat, Calendar caldate, JDBProcessOrder po) {
		String result = "";

		result = batchFormat;

		if (result.indexOf("{PLANT}") >= 0)
		{
			String plant = "";
			ctrl.getProperties("PLANT");
			plant = ctrl.getKeyValue();
			result = result.replaceAll("\\{PLANT\\}", plant);
		}

		if (result.indexOf("{PROCESS_ORDER}") >= 0)
		{
			result = result.replaceAll("\\{PROCESS_ORDER\\}", po.getProcessOrder());
		}

		if (result.indexOf("{YEAR1}") >= 0)
		{
			String curdate = JUtility.getSQLDate(caldate).toString().substring(3, 4);
			result = result.replaceAll("\\{YEAR1\\}", curdate);
		}

		if (result.indexOf("{YEAR2}") >= 0)
		{
			String curdate = JUtility.getSQLDate(caldate).toString().substring(2, 4);
			result = result.replaceAll("\\{YEAR2\\}", curdate);
		}

		if (result.indexOf("{YEAR4}") >= 0)
		{
			String curdate = JUtility.getSQLDate(caldate).toString().substring(0, 4);
			result = result.replaceAll("\\{YEAR4\\}", curdate);
		}

		if (result.indexOf("{MONTH}") >= 0)
		{
			String curdate = JUtility.getSQLDate(caldate).toString().substring(5, 7);
			result = result.replaceAll("\\{MONTH\\}", curdate);
		}

		if (result.indexOf("{DAY}") >= 0)
		{
			String curdate = JUtility.getSQLDate(caldate).toString().substring(8, 10);
			result = result.replaceAll("\\{DAY\\}", curdate);
		}

		if (result.indexOf("{JULIAN_DAY}") >= 0)
		{
			long jd = JUtility.getJulianDay(caldate);
			String jds = Long.toString(jd).trim();
			jds = JUtility.padString(jds, false, 3, "0");
			result = result.replaceAll("\\{JULIAN_DAY\\}", jds);
		}

		return result;
	}

	public String getErrorMessage() {
		return dbErrorMessage;
	}

	public Timestamp getExpiryDate() {
		return dbMaterialBatchExpiry;
	}

	private String getHostID() {
		return hostID;
	}

	public String getMaterial() {
		return dbMaterial;
	}

	public Vector<JDBMaterialBatch> getMaterialBatchData(PreparedStatement criteria) {
		ResultSet rs;
		Vector<JDBMaterialBatch> result = new Vector<JDBMaterialBatch>();

		// logger.debug("getControlData");
		if (Common.hostList.getHost(getHostID()).toString().equals(null))
		{
			result.addElement(new JDBMaterialBatch("material", "batch", "status", JUtility.getSQLDateTime()));
		}
		else
		{
			try
			{
				rs = criteria.executeQuery();

				while (rs.next())
				{
					result.addElement(new JDBMaterialBatch(rs.getString("material"), rs.getString("batch_number"), rs.getString("status"), rs.getTimestamp("expiry_date")));
				}

				rs.close();
			}
			catch (Exception e)
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

		return result;
	}

	public ResultSet getMaterialBatchDataResultSet(PreparedStatement criteria) {
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

	public boolean getMaterialBatchProperties() {
		boolean result = false;

		PreparedStatement stmt;
		ResultSet rs;
		setErrorMessage("");

		//logger.debug("getMaterialBatchProperties material=["+getMaterial()+ "] batch=["+getBatch()+"]");
		clear();

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBMaterialBatch.getMaterialBatchProperties"));
			stmt.setFetchSize(1);
			stmt.setString(1, getMaterial());
			stmt.setString(2, getBatch());
			rs = stmt.executeQuery();

			if (rs.next())
			{
				getPropertiesfromResultSet(rs);
				result = true;
			}
			else
			{
				setErrorMessage("Unknown Material Batch [" + getMaterial() + " " + getBatch() + "]");
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

	public boolean getMaterialBatchProperties(String material, String batch) {
		setMaterial(material);
		setBatch(batch);

		return getMaterialBatchProperties();
	}

	public void getPropertiesfromResultSet(ResultSet rs) {
		try
		{
			clear();
			setMaterial(rs.getString("material"));
			setBatch(rs.getString("batch_number"));
			setStatus(rs.getString("status"));
			setExpiryDate(rs.getTimestamp("expiry_date"));
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

	private String getSessionID() {
		return sessionID;
	}

	public String getStatus() {
		return JUtility.replaceNullStringwithBlank(dbMaterialStatus);
	}

	public boolean isValid() {
		boolean result = true;

		/* Check Material */
		if (JUtility.isNullORBlank(getMaterial()) == true)
		{
			setErrorMessage("MATERIAL code cannot be null");
			result = false;
		}

		/* Check Base UOM */
		if (result == true)
		{
			if (JUtility.isNullORBlank(getBatch()) == true)
			{
				setErrorMessage("BATCH code cannot be null");
				result = false;
			}
		}

		return result;
	}

	public boolean isValidMaterialBatch() {

		PreparedStatement stmt;
		ResultSet rs;
		boolean result = false;

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBMaterialBatch.isValidMaterialBatch"));
			stmt.setString(1, getMaterial());
			stmt.setString(2, getBatch());
			stmt.setFetchSize(1);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				result = true;
			}
			else
			{
				setErrorMessage("Invalid Material / Batch");
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

	public boolean isValidMaterialBatch(String material, String batch) {
		setMaterial(material);
		setBatch(batch);

		return isValidMaterialBatch();
	}

	public void setBatch(String batch) {
		dbMaterialBatch = batch;
		//.toUpperCase();
	}

	private void setErrorMessage(String errorMsg) {
		dbErrorMessage = errorMsg;
	}

	public void setExpiryDate(Timestamp expiryDate) {
		dbMaterialBatchExpiry = expiryDate;
	}

	private void setHostID(String host) {
		hostID = host;
	}

	public void setMaterial(String material) {
		dbMaterial = material.toUpperCase();
	}

	private void setSessionID(String session) {
		sessionID = session;
	}

	public void setStatus(String status) {
		dbMaterialStatus = status;
	}

	public boolean update() {
		boolean result = false;

		if (isValid() == true)
		{
			try
			{
				PreparedStatement stmtupdate;
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBMaterialBatch.update"));

				stmtupdate.setString(1, getStatus());
				stmtupdate.setTimestamp(2, getExpiryDate());

				stmtupdate.setString(3, getMaterial());
				stmtupdate.setString(4, getBatch());

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
		}

		return result;
	}
}
