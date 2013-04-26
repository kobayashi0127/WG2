package com.commander4j.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.commander4j.messages.OutgoingDespatchConfirmation;
import com.commander4j.messages.OutgoingDespatchPreAdvice;
import com.commander4j.messages.OutgoingEquipmentTracking;
import com.commander4j.sys.Common;
import com.commander4j.thread.DespatchConfirmThread;
import com.commander4j.util.JUtility;

/**
 * @author David
 * @version $Revision: 1.0 $
 */
public class JDBDespatch
{
	/* Locations */
	public static int field_despatch_no = 10;
	public static int field_location_id_from = 15;
	public static int field_status = 20;
	public static int field_total_pallets = 10;
	public static int field_location_id_to = 15;
	public static int field_trailer = 15;
	public static int field_haulier = 15;
	public static int field_load_no = 20;
	public static int field_user_id = 20;
	private String dbDespatchNo;
	private Timestamp dbDespatchDate;
	private String dbLocationIdFrom;
	private String dbLocationIdTo;
	private String dbStatus;
	private String dbTrailer;
	private String dbLoadNo;
	private String dbUserID = "";
	private String dbHaulier;
	private int dbTotalPallets;
	private String dbErrorMessage;
	private Long dbTransactionRef;
	private OutgoingDespatchConfirmation odc;
	private OutgoingDespatchPreAdvice opa;
	private OutgoingEquipmentTracking oet;
	private Logger logger = Logger.getLogger(JDBDespatch.class);
	private String hostID;
	private String sessionID;
	private JDBLocation lf;
	private JDBLocation lt;
	private JDBPallet pal;
	private String last_lf_found = "none";
	private String last_lt_found = "none";
	@SuppressWarnings("unused")
	private String home_location = "";
	private JDBMaterialLocation ml;
	private JDBControl ctrl;
	private Boolean allowDespatchToSelf = false;

	public JDBDespatch(String host, String session)
	{
		super();
		setHostID(host);
		setSessionID(session);
		lf = new JDBLocation(getHostID(), getSessionID());
		lt = new JDBLocation(getHostID(), getSessionID());
		pal = new JDBPallet(getHostID(), getSessionID());
		ml = new JDBMaterialLocation(getHostID(), getSessionID());
		ctrl = new JDBControl(getHostID(), getSessionID());
		ctrl.getProperties("DEFAULT_LOCATION");
		home_location = ctrl.getKeyValue();
		allowDespatchToSelf = Boolean.valueOf(ctrl.getKeyValueWithDefault("DESPATCH_TO_SELF", "false", "Allow despatch to source location"));
	}

	public JDBDespatch(String host, String session, String despatchNo, Timestamp despatchDate, String locationIdFrom, String locationIdTo, String status, int noofpallets, String trailer, String haulier, String loadNo, String userID)
	{
		setHostID(host);
		setSessionID(session);
		lf = new JDBLocation(getHostID(), getSessionID());
		lt = new JDBLocation(getHostID(), getSessionID());
		pal = new JDBPallet(getHostID(), getSessionID());
		ml = new JDBMaterialLocation(getHostID(), getSessionID());
		setDespatchNo(despatchNo);
		setDespatchDate(despatchDate);
		setLocationIDFrom(locationIdFrom);
		setLocationIDTo(locationIdTo);
		setStatus(status);
		setTotalPallets(noofpallets);
		setTrailer(trailer);
		setHaulier(haulier);
		setLoadNo(loadNo);
		setUserID(userID);
		ctrl = new JDBControl(getHostID(), getSessionID());
		ctrl.getProperties("DEFAULT_LOCATION");
		home_location = ctrl.getKeyValue();
		allowDespatchToSelf = Boolean.valueOf(ctrl.getKeyValueWithDefault("DESPATCH_TO_SELF", "false", "Allow despatch to source location"));
	}

	public Boolean assignSSCC(String sscc)
	{
		Boolean result = false;

		if (getStatus().equals("Unconfirmed"))
		{
			if (pal.getPalletProperties(sscc))
			{
				if (pal.isConfirmed())
				{
					boolean alreadyAssigned = false;
					if (pal.getDespatchNo().equals("") == false)
					{
						JDBDespatch altDesp = new JDBDespatch(getHostID(), getSessionID());
						altDesp.getDespatchProperties(pal.getDespatchNo());
						if (altDesp.getStatus().equals("Unconfirmed"))
						{
							alreadyAssigned = true;
						}
					}

					boolean sourceLocationOK = true;
					if (pal.getLocationID().equals(getLocationIDFrom()) == false)
					{
						sourceLocationOK = false;
					}

					boolean destinationLocationOK = true;

					ml.setMaterial(pal.getMaterial());
					ml.setLocation(getLocationIDTo());
					if (ml.isValidSSCCMaterialLocation() == false)
					{
						destinationLocationOK = false;
					}

					if (sourceLocationOK)
					{
						if (destinationLocationOK)
						{
							if (!alreadyAssigned)
							{
								if (getLocationDBTo().isPalletStatusValidforLocation(pal.getStatus()))
								{
									if (getLocationDBTo().isBatchStatusValidforLocation(pal.getMaterialBatchStatus()))
									{
										pal.setDespatchNo(getDespatchNo());

										if (pal.updateDespatchNo())
										{
											result = true;
											setErrorMessage("");
										} else
										{
											setErrorMessage(pal.getErrorMessage());
										}
									} else
									{
										setErrorMessage("Batch status is " + pal.getMaterialBatchStatus());
									}
								} else
								{
									setErrorMessage("Pallet status is " + pal.getStatus());
								}
							} else
							{
								setErrorMessage("Already Assigned to " + pal.getDespatchNo());
							}
						} else
						{
							setErrorMessage("Material " + pal.getMaterial() + " invalid for " + getLocationIDTo());
						}
					} else
					{
						setErrorMessage("Pallet is not in source location " + getLocationIDFrom());
					}
				} else
				{
					setErrorMessage(sscc + " not confirmed.");
				}
			} else
			{
				setErrorMessage(pal.getErrorMessage());
			}
		} else
		{
			setErrorMessage(getDespatchNo() + " already confirmed.");
		}

		return result;
	}

	public Boolean unassignSSCC(String sscc)
	{
		Boolean result = false;

		if (pal.getPalletProperties(sscc))
		{
			pal.setDespatchNo("");

			if (pal.updateDespatchNo())
			{
				result = true;
			}
		}

		if (result == false)
		{
			setErrorMessage(pal.getErrorMessage());
		}

		return result;
	}

	public LinkedList<JDBDespatch> browseDespatchData(String status, int limit)
	{
		String temp = "";
		Boolean top = false;
		PreparedStatement stmt = null;
		LinkedList<JDBDespatch> result = new LinkedList<JDBDespatch>();
		ResultSet rs;

		result.clear();

		try
		{
			temp = Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBDespatch.browse");

			if (temp.indexOf("[top]") >= 0)
			{
				top = true;
				temp = temp.replace("[top]", "top " + String.valueOf(limit));
			}

			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(temp);
			stmt.setFetchSize(100);
			stmt.setString(1, status);

			if (top == false)
			{
				stmt.setInt(2, limit);
			}

			rs = stmt.executeQuery();

			while (rs.next())
			{
				result.addLast(new JDBDespatch(getHostID(), getSessionID(), rs.getString("despatch_no"), rs.getTimestamp("despatch_date"), rs.getString("location_id_from"), rs.getString("location_id_to"), rs.getString("status"), rs
						.getInt("total_pallets"), rs.getString("trailer"), rs.getString("haulier"), rs.getString("load_no"), rs.getString("user_id")));
			}

			rs.close();

			stmt.close();
		} catch (SQLException e)
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

	public void clear()
	{
		setDespatchDate(null);
		setLocationIDFrom("");
		setLocationIDTo("");
		setStatus("");
		setTotalPallets(0);
	}

	public boolean confirm()
	{
		boolean result = false;
		LinkedList<String> assignedList = new LinkedList<String>();

		JDBPallet pal = new JDBPallet(getHostID(), getSessionID());
		JDBPalletHistory ph = new JDBPalletHistory(getHostID(), getSessionID());
		long txn = 0;

		logger.debug("confirm [" + getDespatchNo() + "]");

		if (getStatus().equals("Confirmed") == true)
		{
			setErrorMessage("Despatch already confirmed.");
		} else
		{
			if (isValid() == true)
			{
				assignedList.clear();
				assignedList.addAll(getAssignedSSCCs());

				if (assignedList.size() > 0)
				{
					setStatus("Confirmed");
					setDespatchDate(com.commander4j.util.JUtility.getSQLDateTime());
					setTotalPallets(assignedList.size());

					while (txn == 0)
					{
						txn = ph.generateNewTransactionRef();
						if (txn == 0)
						{
							logger.error("Unable to get Transaction Number - retrying");
						}
					}

					setTransactionRef(txn);
					update();

					String mode = ctrl.getKeyValueWithDefault("DESPATCH CONFIRM THREAD", "True", "Use background thread to confirm").toUpperCase();
					Boolean threadmode = Boolean.valueOf(mode);

					if (threadmode)
					{
						DespatchConfirmThread despconfirmThread = new DespatchConfirmThread(getHostID(), getSessionID(), getDespatchNo());
						despconfirmThread.setName("C4J Despatch Confirm Thread");
						despconfirmThread.start();
					} else
					{

						for (int j = 0; j < assignedList.size(); j++)
						{
							if (pal.getPalletProperties(assignedList.get(j)))
							{
								pal.writePalletHistory(txn, "DESPATCH", "FROM");
								pal.setLocationID(getLocationIDTo());
								pal.updateLocationID();
								pal.writePalletHistory(txn, "DESPATCH", "TO");
							}
						}

						if (getLocationDBTo().isDespatchConfirmationMessageRequired())
						{
							odc = new OutgoingDespatchConfirmation(getHostID(), getSessionID());
							odc.submit(txn);
						}

						if (getLocationDBTo().isDespatchEquipmentTrackingMessageRequired())
						{
							oet = new OutgoingEquipmentTracking(getHostID(), getSessionID());
							oet.submit(txn);
						}

						if (getLocationDBTo().isDespatchPreAdviceMessageRequired())
						{
							opa = new OutgoingDespatchPreAdvice(getHostID(), getSessionID());
							opa.submit(txn);
						}
					}
					setErrorMessage("");
					result = true;
				} else
				{
					setErrorMessage("Cannot confirm an empty despatch");
				}
			}
		}

		return result;
	}

	public boolean create()
	{
		logger.debug("create [" + getDespatchNo() + "]");

		boolean result = false;

		if (isValid() == true)
		{
			try
			{
				PreparedStatement stmtupdate;
				setStatus("Unconfirmed");
				setTotalPallets(0);
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBDespatch.create"));
				stmtupdate.setString(1, getDespatchNo());
				stmtupdate.setString(2, getStatus());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				stmtupdate.close();
				Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();

				JDBControl ctrl = new JDBControl(getHostID(), getSessionID());

				if (ctrl.getProperties("DEFAULT_LOCATION") == true)
				{
					String locn = JUtility.replaceNullStringwithBlank(ctrl.getKeyValue());

					if (locn.length() > 0)
					{
						JDBLocation loc = new JDBLocation(getHostID(), getSessionID());

						if (loc.getLocationProperties(locn))
						{
							setLocationIDFrom(locn);
						}
					}
				}

				update();
				result = true;
			} catch (SQLException e)
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

	public boolean create(String despatchNo)
	{
		boolean result = false;
		setDespatchNo(despatchNo);
		result = create();

		return result;
	}

	public boolean delete()
	{
		PreparedStatement stmtupdate;
		boolean result = false;
		setErrorMessage("");
		logger.debug("delete [" + getDespatchNo() + "]");

		LinkedList<String> assignedList = new LinkedList<String>();

		JDBPallet pal = new JDBPallet(getHostID(), getSessionID());

		if (isValid() == true)
		{
			assignedList.clear();
			assignedList.addAll(getAssignedSSCCs());

			if (assignedList.size() > 0)
			{
				for (int j = 0; j < assignedList.size(); j++)
				{
					if (pal.getPalletProperties(assignedList.get(j)))
					{
						pal.setDespatchNo("");
						pal.update();
					}
				}
			}

			try
			{
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBDespatch.delete"));
				stmtupdate.setString(1, getDespatchNo());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();

				stmtupdate.close();
				result = true;
			} catch (SQLException e)
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

	public boolean delete(String despatchNo)
	{
		boolean result = false;
		setDespatchNo(despatchNo);
		result = delete();

		return result;
	}

	public String formatDespatchNo(String despatchNo)
	{
		String result = "error";
		JDBControl ctrl = new JDBControl(getHostID(), getSessionID());
		String processOrderNoFormat = "XX{NNNNNN}";

		if (ctrl.getProperties("DESPATCH NUMBER FORMAT") == true)
		{
			processOrderNoFormat = ctrl.getKeyValue();
		}

		result = JUtility.formatNumber(despatchNo, processOrderNoFormat);

		return result;
	}

	public String generateNewDespatchNo()
	{
		String result = "error";
		JDBControl ctrl = new JDBControl(getHostID(), getSessionID());
		String despatchNo = "1";
		int SeqNumber = 0;

		if (ctrl.lockRecord("DESPATCH NUMBER") == true)
		{
			if (ctrl.getProperties("DESPATCH NUMBER") == true)
			{
				despatchNo = ctrl.getKeyValue();
				SeqNumber = Integer.parseInt(despatchNo);

				result = formatDespatchNo(despatchNo);
				setDespatchNo(result);

				SeqNumber++;
				despatchNo = String.valueOf(SeqNumber);
				ctrl.setKeyValue(despatchNo);
				ctrl.update();
			} else
			{
				result = "";
				setErrorMessage(ctrl.getErrorMessage());
			}
		} else
		{
			result = "";
			setErrorMessage(ctrl.getErrorMessage());
		}

		return result;
	}

	public LinkedList<String> getAssignedSSCCs()
	{
		return getAssignedSSCCs(getDespatchNo());
	}

	public LinkedList<String> getAssignedSSCCs(String despatchNo)
	{
		PreparedStatement stmt = null;
		LinkedList<String> result = new LinkedList<String>();
		ResultSet rs;

		result.clear();

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBDespatch.getAssignedSSCCs"));
			stmt.setFetchSize(50);
			stmt.setString(1, despatchNo);

			rs = stmt.executeQuery();

			while (rs.next())
			{
				result.addLast(rs.getString("SSCC"));
			}

			rs.close();

			stmt.close();
		} catch (SQLException e)
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

	public Vector<JDBDespatch> getDespatchData(PreparedStatement criteria)
	{
		ResultSet rs;
		Vector<JDBDespatch> result = new Vector<JDBDespatch>();

		if (Common.hostList.getHost(getHostID()).toString().equals(null))
		{
			result.addElement(new JDBDespatch(getHostID(), getSessionID(), "despatch_no", null, "location_id_from", "location_id_to", "status", 0, "trailer", "haulier", "load_no", "user_id"));
		} else
		{
			try
			{
				rs = criteria.executeQuery();

				while (rs.next())
				{
					result.addElement(new JDBDespatch(getHostID(), getSessionID(), rs.getString("despatch_no"), rs.getTimestamp("despatch_date"), rs.getString("location_id_from"), rs.getString("location_id_to"), rs.getString("status"), rs
							.getInt("total_pallets"), rs.getString("trailer"), rs.getString("haulier"), rs.getString("load_no"), rs.getString("user_id")));
				}

				rs.close();
			} catch (Exception e)
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

	public Timestamp getDespatchDate()
	{
		return dbDespatchDate;
	}

	public String getDespatchNo()
	{
		return JUtility.replaceNullStringwithBlank(dbDespatchNo);
	}

	public int getDespatchPalletCount()
	{
		int result = 0;

		setTotalPallets(0);

		PreparedStatement stmt;
		ResultSet rs;
		setErrorMessage("");
		logger.debug("getDespatchPalletCount");

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBDespatch.count"));
			stmt.setFetchSize(50);
			stmt.setString(1, getDespatchNo());
			rs = stmt.executeQuery();

			if (rs.next())
			{
				result = rs.getInt("pallet_count");
				setTotalPallets(result);
			} else
			{
				result = -1;
				setErrorMessage("Invalid Despatch No");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e)
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

	public boolean getDespatchProperties()
	{
		boolean result = false;

		PreparedStatement stmt;
		ResultSet rs;
		setErrorMessage("");

		clear();

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBDespatch.getDespatchProperties"));
			stmt.setFetchSize(1);
			stmt.setString(1, getDespatchNo());
			rs = stmt.executeQuery();

			if (rs.next())
			{
				getPropertiesfromResultSet(rs);
				result = true;
			} else
			{
				setErrorMessage("Invalid Despatch No");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e)
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

	public boolean getDespatchProperties(String despatchNo)
	{
		setDespatchNo(despatchNo);

		return getDespatchProperties();
	}

	public boolean getDespatchPropertiesFromTransactionRef()
	{
		boolean result = false;

		PreparedStatement stmt;
		ResultSet rs;
		setErrorMessage("");

		clear();

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBDespatch.getDespatchPropertiesFromTransactionRef"));
			stmt.setFetchSize(1);
			stmt.setLong(1, getTransactionRef());
			rs = stmt.executeQuery();

			if (rs.next())
			{
				getPropertiesfromResultSet(rs);
				result = true;
			} else
			{
				setErrorMessage("Invalid Despatch No");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e)
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

	public LinkedList<JDBEquipmentList> getEquipment()
	{
		LinkedList<JDBEquipmentList> result = new LinkedList<JDBEquipmentList>();
		PreparedStatement stmt = null;
		ResultSet rs;
		String temp = "";

		try
		{
			temp = Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBDespatch.equipment");

			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(temp);
			stmt.setFetchSize(25);
			stmt.setString(1, getDespatchNo());

			rs = stmt.executeQuery();
			result.clear();

			while (rs.next())
			{
				result.addLast(new JDBEquipmentList(rs.getString("equipment_type"), rs.getInt("total")));
			}

			rs.close();
			stmt.close();
		} catch (SQLException e)
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

	public String getErrorMessage()
	{
		return dbErrorMessage;
	}

	public String getHaulier()
	{
		return dbHaulier;
	}

	private String getHostID()
	{
		return hostID;
	}

	public JDBLocation getLocationDBFrom()
	{
		if (getLocationIDFrom().equals(last_lf_found) == false)
		{
			lf.getLocationProperties(getLocationIDFrom());
			last_lf_found = getLocationIDFrom();
		}

		return lf;
	}

	public JDBLocation getLocationDBTo()
	{
		if (getLocationIDTo().equals(last_lt_found) == false)
		{
			lt.getLocationProperties(getLocationIDTo());
			last_lt_found = getLocationIDTo();
		}

		return lt;
	}

	public String getLocationIDFrom()
	{
		return JUtility.replaceNullStringwithBlank(dbLocationIdFrom);
	}

	public String getLocationIDTo()
	{
		return JUtility.replaceNullStringwithBlank(dbLocationIdTo);
	}

	public void getPropertiesfromResultSet(ResultSet rs)
	{
		try
		{
			clear();

			setDespatchNo(rs.getString("despatch_no"));
			setDespatchDate(rs.getTimestamp("despatch_date"));
			setLocationIDFrom(rs.getString("location_id_from"));
			setLocationIDTo(rs.getString("location_id_to"));
			setStatus(rs.getString("status"));
			setTrailer(rs.getString("trailer"));
			setLoadNo(rs.getString("load_no"));
			setHaulier(rs.getString("haulier"));
			setTotalPallets(rs.getInt("total_pallets"));
			setTransactionRef(rs.getLong("transaction_ref"));
			setUserID(rs.getString("user_id"));
		} catch (SQLException e)
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

	private String getSessionID()
	{
		return sessionID;
	}

	public String getStatus()
	{
		return dbStatus;
	}

	public int getTotalPallets()
	{
		return dbTotalPallets;
	}

	public String getTrailer()
	{
		return dbTrailer;
	}

	public String getLoadNo()
	{
		return dbLoadNo;
	}

	public String getUserID()
	{
		return JUtility.replaceNullStringwithBlank(dbUserID);
	}

	public Long getTransactionRef()
	{
		if (dbTransactionRef == null)
		{
			dbTransactionRef = Long.valueOf(0);
		}

		return dbTransactionRef;
	}

	public boolean isValid()
	{
		boolean result = true;

		if (JUtility.isNullORBlank(dbDespatchNo) == true)
		{
			setErrorMessage("Despatch No cannot be null");
			result = false;
		}

		JDBLocation locn = new JDBLocation(getHostID(), getSessionID());

		if (JUtility.isNullORBlank(getLocationIDFrom()) == false)
		{
			if (locn.isValidLocation(getLocationIDFrom()) == false)
			{
				setErrorMessage("Invalid FROM Location ID");
				result = false;
			}
		}

		if (JUtility.isNullORBlank(getLocationIDTo()) == false)
		{
			if (locn.isValidLocation(getLocationIDTo()) == false)
			{
				setErrorMessage("Invalid TO Location ID");
				result = false;
			}
		}

		if (allowDespatchToSelf == false)
		{
			if (JUtility.isNullORBlank(getLocationIDFrom()) == false)
			{
				if (JUtility.isNullORBlank(getLocationIDTo()) == false)
				{
					if (getLocationIDFrom().equals(getLocationIDTo()))
					{
						setErrorMessage("FROM & TO Locations cannot be same.");
						result = false;
					}
				}
			}
		}

		return result;
	}

	public boolean isValidDespatchNo()
	{

		PreparedStatement stmt;
		ResultSet rs;
		boolean result = false;

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBDespatch.isValidDespatchNo"));
			stmt.setString(1, getDespatchNo());
			stmt.setFetchSize(1);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				result = true;
			} else
			{
				setErrorMessage("Invalid Despatch No [" + getDespatchNo() + "]");
			}

			rs.close();
			stmt.close();
		} catch (SQLException e)
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

	public boolean isValidDespatchNo(String despatchNo)
	{
		setDespatchNo(despatchNo);

		return isValidDespatchNo();
	}

	public void setDespatchDate(Timestamp despatchDate)
	{
		dbDespatchDate = despatchDate;
	}

	public void setDespatchNo(String despatchNo)
	{
		dbDespatchNo = despatchNo;
	}

	private void setErrorMessage(String ErrorMsg)
	{
		if (ErrorMsg.isEmpty() == false)
		{
			logger.error(ErrorMsg);
		}

		dbErrorMessage = ErrorMsg;
	}

	public void setHaulier(String haulier)
	{
		dbHaulier = haulier;
	}

	private void setHostID(String host)
	{
		hostID = host;
	}

	public void setLocationIDFrom(String locationid)
	{
		dbLocationIdFrom = locationid;
		getLocationDBFrom();
	}

	public void setLocationIDTo(String locationid)
	{
		dbLocationIdTo = locationid;
		getLocationDBTo();
	}

	private void setSessionID(String session)
	{
		sessionID = session;
	}

	public void setStatus(String status)
	{
		dbStatus = status;
	}

	public void setTotalPallets(int total_pallets)
	{
		dbTotalPallets = total_pallets;
	}

	public void setTrailer(String trailer)
	{
		dbTrailer = trailer;
	}

	public void setLoadNo(String loadNo)
	{
		dbLoadNo = loadNo;
	}

	public void setUserID(String userID)
	{
		dbUserID = userID;
	}

	public void setTransactionRef(Long txn)
	{
		dbTransactionRef = txn;
	}

	public String toString()
	{
		return getDespatchNo();
	}

	public boolean update()
	{
		boolean result = false;

		logger.debug("update [" + getDespatchNo() + "]");

		if (isValid() == true)
		{
			try
			{
				PreparedStatement stmtupdate;
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBDespatch.update"));

				stmtupdate.setTimestamp(1, getDespatchDate());
				stmtupdate.setString(2, getLocationIDFrom());
				stmtupdate.setString(3, getStatus());
				stmtupdate.setString(4, getTrailer());
				stmtupdate.setString(5, getHaulier());
				stmtupdate.setInt(6, getTotalPallets());
				stmtupdate.setString(7, getLocationIDTo());
				stmtupdate.setLong(8, getTransactionRef());
				stmtupdate.setString(9, getLoadNo());
				stmtupdate.setString(10, getDespatchNo());

				stmtupdate.execute();

				stmtupdate.clearParameters();
				Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				stmtupdate.close();
				result = true;
			} catch (SQLException e)
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

	public boolean updateUserID(String despatchNO, String userID)
	{
		boolean result = false;

		setDespatchNo(despatchNO);
		setUserID(userID);

		logger.debug("updateUserID [" + getDespatchNo() + "] [" + getUserID() + "]");

		if (isValid() == true)
		{
			try
			{
				PreparedStatement stmtupdate;
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBDespatch.setUserID"));

				stmtupdate.setString(1, getUserID());
				stmtupdate.setString(2, getDespatchNo());

				stmtupdate.execute();

				stmtupdate.clearParameters();
				Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				stmtupdate.close();
				result = true;
			} catch (SQLException e)
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
