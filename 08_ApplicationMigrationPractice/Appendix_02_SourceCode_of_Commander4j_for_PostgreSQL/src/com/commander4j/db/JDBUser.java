// $codepro.audit.disable numericLiterals
package com.commander4j.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.commander4j.app.JVersion;
import com.commander4j.sys.Common;
import com.commander4j.util.JUtility;

/**
 */
public class JDBUser
{
	private String dbAccountExpires;
	private Timestamp dbAccountExpiryDate;
	private String dbAccountLocked;
	private int dbBadPasswordAttempts;
	private String dbComment;
	private String dbErrorMessage;
	private String dbLanguage;
	private Timestamp dbLastLogin;
	private boolean dbLoggedIn = false;
	private String dbPasswordChangeAllowed;
	private Timestamp dbPasswordChanged;
	private String dbPasswordCurrent;
	private String dbPasswordExpires;
	private Calendar dbPasswordExpiryCalendar;
	private java.util.Date dbPasswordExpiryDate;
	private String dbPasswordNew;
	private String dbPasswordVerify;
	private String dbUserId;
	private boolean initialised = false;
	public static int field_user_id = 20;
	public static int field_password = 10;
	public static int field_comment = 40;
	private LinkedList<String> allowedModules = new LinkedList<String>();
	private final Logger logger = Logger.getLogger(JDBUser.class);
	private String hostID;
	private String sessionID;
	
	public void setSessionID(String session) {
		sessionID = session;
	}

	public void setHostID(String host) {
		hostID = host;
	}

	private String getSessionID() {
		return sessionID;
	}

	private String getHostID() {
		return hostID;
	}

	public JDBUser(String host, String session)
	{
		setHostID(host);
		setSessionID(session);
	}

	/**
	 * @uml.property name="userGroupMembership"
	 * @uml.associationEnd
	 */
	private JDBUserGroupMembership userGroupMembership;

	private String dbEmailAddress = "";

	public void clear() {
		setAccountExpires("");
		setAccountExpiryDate(null);
		setAccountLocked("");
		setBadPasswordAttempts(0);
		setComment("");
		setLanguage("");
		setLastLogin(null);
		setPasswordChangeAllowed("");
		setPasswordExpires("");
		setPasswordNew("");
		setPasswordVerify("");
	}

	private void init() {
		userGroupMembership = new JDBUserGroupMembership(getHostID(), getSessionID());

		dbPasswordExpiryCalendar = Calendar.getInstance();
		dbPasswordExpiryCalendar.add(Calendar.DATE, -1 * Common.user_password_expiry_days);
		dbPasswordExpiryDate = dbPasswordExpiryCalendar.getTime();

		initialised = true;
	}

	/**
	 * Method getUserIcon.
	 * 
	 * @return Icon
	 */
	public Icon getUserIcon() {
		Icon icon = new ImageIcon();

		try
		{
			if (isAccountLocked() == true)
			{
				icon = Common.imageIconloader.getImageIcon(Common.image_user_locked);
			}
			else
			{
				if (isAccountExpired() == true)
				{
					icon = Common.imageIconloader.getImageIcon(Common.image_user_expired);
				}
				else
				{
					icon = Common.imageIconloader.getImageIcon(Common.image_user);
				}
			}
		}
		catch (Exception e)
		{
		}

		return icon;
	}

	/**
	 * Method addtoGroup.
	 * 
	 * @param lgroup_id
	 *            String
	 * @return boolean
	 */
	public boolean addtoGroup(String lgroup_id) {
		boolean result = false;

		userGroupMembership.setUserId(getUserId());
		userGroupMembership.setGroupId(lgroup_id);
		result = userGroupMembership.addUsertoGroup();

		return result;
	}

	/**
	 * Method changePassword.
	 * 
	 * @return boolean
	 */
	public boolean changePassword() {
		boolean result = false;
		setErrorMessage("");
		logger.debug("changePassword");
		try
		{
			if (isValidUserId() == true)
			{
				if (isValidPassword() == true)
				{
					if (isNewPasswordValid())
					{
						PreparedStatement stmtupdate;
						stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUser.changePassword"));
						stmtupdate.setString(1, getPasswordNew());
						stmtupdate.setTimestamp(2, (Timestamp) JUtility.getSQLDateTime());
						stmtupdate.setString(3, getUserId());
						stmtupdate.execute();
						stmtupdate.clearParameters();
						if (Common.hostList.getHost(getHostID()).getConnection(getSessionID()).getAutoCommit() == false)
						{
							Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
						}
						stmtupdate.close();
						result = true;
					}
				}
				else
				{
					setErrorMessage("Current password is invalid");
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
	 * Method create.
	 * 
	 * @param luser_id
	 *            String
	 * @param lpassword
	 *            String
	 * @param lcomment
	 *            String
	 * @param lpassword_change_allowed
	 *            boolean
	 * @param lpassword_expires
	 *            boolean
	 * @param laccount_locked
	 *            boolean
	 * @param llanguage
	 *            String
	 * @param laccount_expires
	 *            boolean
	 * @return boolean
	 */
	public boolean create(String luser_id, String lpassword, String lcomment, boolean lpassword_change_allowed, boolean lpassword_expires, boolean laccount_locked, String llanguage, boolean laccount_expires, String lemail_address) {
		boolean result = false;
		setErrorMessage("");
		logger.debug("create");

		try
		{
			setUserId(luser_id);
			setPassword("tempcreate");
			setPasswordNew(lpassword);
			setPasswordVerify(lpassword);
			setComment(lcomment);
			setEmailAddress(lemail_address);
			if (lpassword_change_allowed == true)
				setPasswordChangeAllowed("Y");
			else
				setPasswordChangeAllowed("N");

			if (lpassword_expires == true)
				setPasswordExpires("Y");
			else
				setPasswordExpires("N");

			if (laccount_expires == true)
				setAccountExpires("Y");
			else
				setAccountExpires("N");

			if (laccount_locked == true)
				setAccountLocked("Y");
			else
				setAccountLocked("N");

			setLanguage(llanguage);

			if (isValidUserId() == false)
			{
				PreparedStatement stmtupdate;

				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUser.create"));
				stmtupdate.setString(1, getUserId());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				if (Common.hostList.getHost(getHostID()).getConnection(getSessionID()).getAutoCommit() == false)
				{
					Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				}
				stmtupdate.close();
				if (changePassword() == true)
				{
					if (update() == true)
					{
						result = true;
					}
				}
			}
			else
			{
				setErrorMessage("UserId already exists");
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
		logger.debug("delete");

		try
		{
			if (isValidUserId() == true)
			{
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUser.delete"));
				stmtupdate.setString(1, getUserId());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				if (Common.hostList.getHost(getHostID()).getConnection(getSessionID()).getAutoCommit() == false)
				{
					Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				}

				userGroupMembership.setUserId(getUserId());
				result = userGroupMembership.removeAllGroupsfromUser();

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
	 * Method disable.
	 * 
	 * @return boolean
	 */
	public boolean disable() {
		PreparedStatement stmtupdate;
		boolean result = false;
		setErrorMessage("");

		try
		{
			if (isValidUserId() == true)
			{
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUser.disable"));
				stmtupdate.setString(1, getUserId());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				if (Common.hostList.getHost(getHostID()).getConnection(getSessionID()).getAutoCommit() == false)
				{
					Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				}
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
	 * Method enable.
	 * 
	 * @return boolean
	 */
	public boolean enable() {

		PreparedStatement stmtupdate;
		boolean result = false;
		setErrorMessage("");

		try
		{
			if (isValidUserId() == true)
			{
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUser.enable"));
				stmtupdate.setString(1, getUserId());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				if (Common.hostList.getHost(getHostID()).getConnection(getSessionID()).getAutoCommit() == false)
				{
					Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				}
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
	 * Method getAccountExpires.
	 * 
	 * @return String
	 */
	public String getAccountExpires() {
		return dbAccountExpires;
	}

	/**
	 * Method getAccountExpiryDate.
	 * 
	 * @return Timestamp
	 */
	public Timestamp getAccountExpiryDate() {
		return dbAccountExpiryDate;
	}

	/**
	 * Method getAccountLocked.
	 * 
	 * @return String
	 */
	public String getAccountLocked() {
		return dbAccountLocked;
	}

	/**
	 * Method getBadPasswordAttempts.
	 * 
	 * @return int
	 */
	public int getBadPasswordAttempts() {
		return dbBadPasswordAttempts;
	}

	/**
	 * Method getComment.
	 * 
	 * @return String
	 */
	public String getComment() {
		return dbComment;
	}

	public String getEmailAddress() {
		return dbEmailAddress;
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
	 * Method getLanguage.
	 * 
	 * @return String
	 */
	public String getLanguage() {
		return dbLanguage;
	}

	/**
	 * Method getLastLoginTimestamp.
	 * 
	 * @return Timestamp
	 */
	public Timestamp getLastLoginTimestamp() {
		return dbLastLogin;
	}

	/**
	 * Method getPassword.
	 * 
	 * @return String
	 */
	public String getPassword() {
		return dbPasswordCurrent;
	}

	/**
	 * Method getPasswordChangeAllowed.
	 * 
	 * @return String
	 */
	public String getPasswordChangeAllowed() {
		return dbPasswordChangeAllowed;
	}

	/**
	 * Method getPasswordChanged.
	 * 
	 * @return Timestamp
	 */
	public Timestamp getPasswordChanged() {
		return dbPasswordChanged;
	}

	/**
	 * Method getPasswordExpires.
	 * 
	 * @return String
	 */
	public String getPasswordExpires() {
		return dbPasswordExpires;
	}

	/**
	 * Method getPasswordExpiryDate.
	 * 
	 * @return java.util.Date
	 */
	public java.util.Date getPasswordExpiryDate() {
		return dbPasswordExpiryDate;
	}

	/**
	 * Method getPasswordNew.
	 * 
	 * @return String
	 */
	private String getPasswordNew() {
		return dbPasswordNew;
	}

	/**
	 * Method getPasswordVerify.
	 * 
	 * @return String
	 */
	private String getPasswordVerify() {
		return dbPasswordVerify;
	}

	/**
	 * Method getUserGroupsAssigned.
	 * 
	 * @return LinkedList<String>
	 */
	public LinkedList<String> getUserGroupsAssigned() {
		LinkedList<String> groupList = new LinkedList<String>();

		userGroupMembership.setUserId(getUserId());
		groupList = userGroupMembership.getGroupsAssignedtoUser();

		return groupList;
	}

	/**
	 * Method getUserGroupsUnAssigned.
	 * 
	 * @return LinkedList<String>
	 */
	public LinkedList<String> getUserGroupsUnAssigned() {
		LinkedList<String> groupList = new LinkedList<String>();

		PreparedStatement stmt;
		ResultSet rs;
		setErrorMessage("");

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUser.getUserGroupsUnAssigned"));
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
	 * Method getUserId.
	 * 
	 * @return String
	 */
	public String getUserId() {
		return dbUserId;
	}

	public ResultSet getUserDataResultSet() {
		Statement stmt;
		ResultSet rs = null;
		setErrorMessage("");

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).createStatement();
			stmt.setFetchSize(250);
			rs = stmt.executeQuery(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUser.getUserIds"));

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

		return rs;
	}

	/**
	 * Method getUserIds.
	 * 
	 * @return LinkedList<JDBListData>
	 */
	public LinkedList<JDBListData> getUserIds() {
		LinkedList<JDBListData> userList = new LinkedList<JDBListData>();

		Statement stmt;
		ResultSet rs;
		setErrorMessage("");
		Icon icon = new ImageIcon();
		int index = 0;

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).createStatement();
			stmt.setFetchSize(250);
			rs = stmt.executeQuery(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUser.getUserIds"));

			while (rs.next())
			{

				setPassword(rs.getString("password"));
				setComment(rs.getString("user_comment"));
				setLanguage(rs.getString("language_id"));
				setLastLogin(rs.getTimestamp("last_logon"));
				setPasswordExpires(rs.getString("password_expires"));
				setPasswordChanged(rs.getTimestamp("last_password_change"));
				setBadPasswordAttempts(rs.getInt("bad_password_attempts"));
				setAccountLocked(rs.getString("account_locked"));
				setPasswordChangeAllowed(rs.getString("password_change_allowed"));
				setAccountExpires(rs.getString("account_expires"));
				setAccountExpiryDate(rs.getTimestamp("account_expiry_date"));
				icon = getUserIcon();

				JDBListData mld = new JDBListData(icon, index, true, rs.getString("user_id"));
				userList.addLast(mld);
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

		return userList;
	}

	/**
	 * Method getUserProperties.
	 * 
	 * @return boolean
	 */

	public boolean getUserProperties(String userid) {
		setUserId(userid);
		return getUserProperties();
	}

	public boolean getUserProperties() {
		PreparedStatement stmt;
		ResultSet rs;
		boolean result = false;
		setErrorMessage("");

		clear();

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUser.getUserProperties"));
			stmt.setString(1, getUserId());
			stmt.setFetchSize(1);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				// setUserId(rs.getString("user_id"));
				setPassword(rs.getString("password"));
				setComment(rs.getString("user_comment"));
				setEmailAddress(rs.getString("email_address"));
				setLanguage(rs.getString("language_id"));
				setLastLogin(rs.getTimestamp("last_logon"));
				setPasswordExpires(rs.getString("password_expires"));
				setPasswordChanged(rs.getTimestamp("last_password_change"));
				setBadPasswordAttempts(rs.getInt("bad_password_attempts"));
				setAccountLocked(rs.getString("account_locked"));
				setPasswordChangeAllowed(rs.getString("password_change_allowed"));
				setAccountExpires(rs.getString("account_expires"));
				setAccountExpiryDate(rs.getTimestamp("account_expiry_date"));
				result = true;
				rs.close();
				stmt.close();
			}
			else
			{
				setErrorMessage("Invalid UserId [" + getUserId() + "]");
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
	 * Method isAccountExpired.
	 * 
	 * @return boolean
	 */
	public boolean isAccountExpired() {
		boolean result = false;

		if (getAccountExpires() == null)
		{
			setAccountExpires("N");
		}
		if (getAccountExpires().equals("Y"))
		{
			try
			{
				Calendar caldate = Calendar.getInstance();
				java.sql.Date curdate = new java.sql.Date(caldate.getTimeInMillis());
				if (getAccountExpiryDate().after(curdate))
				{
					result = false;
				}
				else
				{
					result = true;
				}
			}
			catch (Exception ex)
			{
				result = true;
			}
		}

		return result;
	}

	/**
	 * Method isAccountExpiring.
	 * 
	 * @return boolean
	 */
	public boolean isAccountExpiring() {
		boolean result = false;

		if (getAccountExpires().equals("Y"))
			result = true;
		else
			result = false;

		return result;
	}

	/**
	 * Method isAccountLocked.
	 * 
	 * @return boolean
	 */
	public boolean isAccountLocked() {
		boolean result = false;
		if (dbAccountLocked != null)
		{
			if (dbAccountLocked.equals("Y") == true)
			{
				result = true;
			}
		}
		return result;
	}

	/**
	 * Method isLoggedIn.
	 * 
	 * @return boolean
	 */
	public boolean isLoggedIn() {
		return dbLoggedIn;
	}

	/**
	 * Method isNewPasswordValid.
	 * 
	 * @return boolean
	 */
	private boolean isNewPasswordValid() {
		boolean result = false;

		try
		{
			if (getPasswordNew().equals(getPasswordVerify()) == true)
			{
				result = true;
			}
			else
			{
				setErrorMessage("New password not verified");
			}
		}
		catch (Exception e)
		{
			setErrorMessage("New password not verified");
		}

		return result;
	}

	/**
	 * Method isPasswordChangeAllowed.
	 * 
	 * @return boolean
	 */
	public boolean isPasswordChangeAllowed() {
		boolean result = false;

		if (dbPasswordChangeAllowed.equals("Y"))
			result = true;
		else
			result = false;

		return result;
	}

	/**
	 * Method isPasswordExpired.
	 * 
	 * @return boolean
	 */
	public boolean isPasswordExpired() {
		boolean result = false;

		if (getPasswordExpires().equals("Y"))
		{
			try
			{
				if (getPasswordChanged().after(getPasswordExpiryDate()))
				{
					result = false;
				}
				else
				{
					result = true;
				}
			}
			catch (Exception ex)
			{
				result = true;
			}
		}

		return result;
	}

	/**
	 * Method isPasswordExpiring.
	 * 
	 * @return boolean
	 */
	public boolean isPasswordExpiring() {
		boolean result = false;

		if (getPasswordExpires().equals("Y"))
			result = true;
		else
			result = false;

		return result;
	}

	/**
	 * Method isValidPassword.
	 * 
	 * @return boolean
	 */
	private boolean isValidPassword() {
		PreparedStatement stmt;
		ResultSet rs;
		boolean result = false;

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUser.isValidPassword"));
			stmt.setString(1, getUserId());
			stmt.setString(2, getPassword());
			stmt.setFetchSize(1);
			rs = stmt.executeQuery();
			if (rs.next())
				result = true;
			else
				setErrorMessage("Invalid password");
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
	 * Method isValidUserId.
	 * 
	 * @return boolean
	 */
	public boolean isValidUserId() {
		PreparedStatement stmt;
		ResultSet rs;
		boolean result = false;

		if (getUserId() == null)
		{
			setUserId("");
		}

		if (getUserId().isEmpty() == false)
		{
			try
			{
				stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUser.isValidUserId"));

				stmt.setString(1, getUserId());
				stmt.setFetchSize(1);
				rs = stmt.executeQuery();

				if (rs.next())
				{
					result = true;
				}
				else
				{
					setErrorMessage("Invalid UserId [" + getUserId() + "]");
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

		}
		// else
		// {
		// setErrorMessage("No User ID Provided");
		// }

		return result;

	}

	public void loadModules() {
		allowedModules.clear();

		PreparedStatement stmt;
		ResultSet rs;
		setErrorMessage("");

		try
		{
			stmt = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUser.loadModules"));
			stmt.setString(1, getUserId());
			rs = stmt.executeQuery();
			while (rs.next())
			{
				allowedModules.addLast(rs.getString("module_id"));
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
	}

	/**
	 * Method isModuleAllowed.
	 * 
	 * @param module_id
	 *            String
	 * @return boolean
	 */
	public boolean isModuleAllowed(String module_id) {
		boolean result = true;

		if (allowedModules.indexOf(module_id) == -1)
		{
			result = false;
		}

		return result;
	}

	/**
	 * Method login.
	 * 
	 * @return boolean
	 */
	public boolean login() {
		boolean result = false;
		setLoggedIn(false);

		try
		{
			if (isValidUserId() == true)
			{
				if (isValidPassword() == true)
				{
					getUserProperties();

					if (isAccountLocked() == false)
					{
						if (isAccountExpired() == false)
						{
							PreparedStatement stmtupdate;
							stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUser.login"));
							stmtupdate.setTimestamp(1, JUtility.getSQLDateTime());
							stmtupdate.setString(2, JVersion.getProgramVersion());
							stmtupdate.setString(3, getUserId());
							stmtupdate.execute();
							stmtupdate.clearParameters();
							if (Common.hostList.getHost(getHostID()).getConnection(getSessionID()).getAutoCommit() == false)
							{
								Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
								//MySQL add ->
								//Common.hostList.getHost(getHostID()).getConnection(getSessionID()).createStatement().execute("start transaction");
								//MySQL add end
							}
							result = true;
							setLoggedIn(true);
							loadModules();
							stmtupdate.close();
						}
						else
						{
							setErrorMessage("Account has expired");
						}
					}
					else
					{
						setErrorMessage("Account is locked");
					}
				}
				else
				{
					setBadPasswordAttempts(getBadPasswordAttempts() + 1);
					PreparedStatement stmtupdate;
					stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUser.login(fail)"));
					stmtupdate.setString(1, getUserId());
					stmtupdate.execute();
					stmtupdate.clearParameters();
					if (Common.hostList.getHost(getHostID()).getConnection(getSessionID()).getAutoCommit() == false)
					{
						Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
					}
					stmtupdate.close();
					if (getBadPasswordAttempts() >= Common.user_max_password_attempts)
					{
						disable();
						setErrorMessage("Account is locked");
					}
				}
			}
		}
		catch (SQLException e)
		{
			logger.error("login :" + e.getMessage());
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

		logger.debug("login :" + result);
		return result;
	}

	public void logout() {
		setLoggedIn(false);
		logger.debug("logout");

	}

	/**
	 * Method removefromGroup.
	 * 
	 * @param lgroup_id
	 *            String
	 * @return boolean
	 */
	public boolean removefromGroup(String lgroup_id) {
		boolean result = false;

		userGroupMembership.setGroupId(lgroup_id);
		userGroupMembership.setUserId(getUserId());
		result = userGroupMembership.removeUserfromGroup();

		return result;
	}

	/**
	 * Method renameTo.
	 * 
	 * @param newUserId
	 *            String
	 * @return boolean
	 */
	public boolean renameTo(String newUserId) {
		PreparedStatement stmtupdate;
		boolean result = false;
		setErrorMessage("");
		logger.debug("renameTo :" + newUserId);

		try
		{
			if (isValidUserId() == true)
			{
				JDBUser newuser = new JDBUser(getHostID(), getSessionID());
				newuser.setUserId(newUserId);
				if (newuser.isValidUserId() == false)
				{

					userGroupMembership.setUserId(getUserId());
					userGroupMembership.renameUserTo(newUserId);

					stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUser.renameTo"));
					stmtupdate.setString(1, newUserId);
					stmtupdate.setString(2, getUserId());
					stmtupdate.execute();
					stmtupdate.clearParameters();
					if (Common.hostList.getHost(getHostID()).getConnection(getSessionID()).getAutoCommit() == false)
					{
						Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
					}
					stmtupdate.close();

					setUserId(newUserId);
					result = true;
				}
				else
				{
					setErrorMessage("New user_id is already in use.");
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
	 * Method setAccountExpires.
	 * 
	 * @param AccountExpires
	 *            String
	 */
	public void setAccountExpires(String AccountExpires) {
		dbAccountExpires = AccountExpires;
	}

	/**
	 * Method setAccountExpiryDate.
	 * 
	 * @param expiryDate
	 *            Timestamp
	 */
	public void setAccountExpiryDate(Timestamp expiryDate) {
		dbAccountExpiryDate = expiryDate;
	}

	/**
	 * Method setAccountLocked.
	 * 
	 * @param AccountLocked
	 *            String
	 */
	public void setAccountLocked(String AccountLocked) {
		dbAccountLocked = AccountLocked;
		if (AccountLocked.equals("Y") == true)
		{
			disable();
		}
		if (AccountLocked.equals("N") == true)
		{
			enable();
		}
	}

	/**
	 * Method setBadPasswordAttempts.
	 * 
	 * @param BadPasswords
	 *            int
	 */
	private void setBadPasswordAttempts(int BadPasswords) {
		dbBadPasswordAttempts = BadPasswords;
	}

	/**
	 * Method setComment.
	 * 
	 * @param Comment
	 *            String
	 */
	public void setComment(String Comment) {
		dbComment = Comment;
	}

	public void setEmailAddress(String email) {
		dbEmailAddress = email;
	}

	/**
	 * Method setErrorMessage.
	 * 
	 * @param ErrorMsg
	 *            String
	 */
	private void setErrorMessage(String ErrorMsg) {
		if (ErrorMsg.isEmpty() == false)
		{
			logger.error(ErrorMsg);
		}
		dbErrorMessage = ErrorMsg;
	}

	/**
	 * Method setLanguage.
	 * 
	 * @param Language
	 *            String
	 */
	public void setLanguage(String Language) {
		dbLanguage = Language;
	}

	/**
	 * Method setLastLogin.
	 * 
	 * @param LastLogin
	 *            java.sql.Timestamp
	 */
	private void setLastLogin(java.sql.Timestamp LastLogin) {
		dbLastLogin = LastLogin;
	}

	/**
	 * Method setLoggedIn.
	 * 
	 * @param flag
	 *            boolean
	 */
	private void setLoggedIn(boolean flag) {
		dbLoggedIn = flag;
	}

	/**
	 * Method setPassword.
	 * 
	 * @param Password
	 *            String
	 */
	public void setPassword(String Password) {
		if (Password == null)
		{
			Password = "*";
		}
		if (Password.equals(""))
		{
			Password = "*";
		}
		dbPasswordCurrent = Password;
	}

	/**
	 * Method setPasswordChangeAllowed.
	 * 
	 * @param PasswordChangeAllowed
	 *            String
	 */
	public void setPasswordChangeAllowed(String PasswordChangeAllowed) {
		dbPasswordChangeAllowed = PasswordChangeAllowed;
	}

	/**
	 * Method setPasswordChanged.
	 * 
	 * @param PasswordChanged
	 *            Timestamp
	 */
	private void setPasswordChanged(Timestamp PasswordChanged) {
		try
		{
			dbPasswordChanged = PasswordChanged;
		}
		catch (Exception ex)
		{
			dbPasswordChanged = null;
		}
	}

	/**
	 * Method setPasswordExpires.
	 * 
	 * @param PasswordExpires
	 *            String
	 */
	public void setPasswordExpires(String PasswordExpires) {
		dbPasswordExpires = PasswordExpires;
	}

	/**
	 * Method setPasswordNew.
	 * 
	 * @param Password
	 *            String
	 */
	public void setPasswordNew(String Password) {
		if (Password == null)
		{
			Password = "*";
		}
		if (Password.equals(""))
		{
			Password = "*";
		}
		dbPasswordNew = Password;
	}

	/**
	 * Method setPasswordVerify.
	 * 
	 * @param Password
	 *            String
	 */
	public void setPasswordVerify(String Password) {
		if (Password == null)
		{
			Password = "*";
		}
		if (Password.equals(""))
		{
			Password = "*";
		}
		dbPasswordVerify = Password;
	}

	/**
	 * Method setUserId.
	 * 
	 * @param UserId
	 *            String
	 */
	public void setUserId(String UserId) {
		if (initialised == false)
		{
			init();
		}
		dbUserId = UserId;
	}

	/**
	 * Method update.
	 * 
	 * @return boolean
	 */
	public boolean update() {
		boolean result = false;
		setErrorMessage("");
		logger.debug("update");

		try
		{
			if (isValidUserId() == true)
			{
				PreparedStatement stmtupdate;
				stmtupdate = Common.hostList.getHost(getHostID()).getConnection(getSessionID()).prepareStatement(Common.hostList.getHost(getHostID()).getSqlstatements().getSQL("JDBUser.update"));
				stmtupdate.setString(1, getPasswordExpires());
				stmtupdate.setString(2, getAccountLocked());
				stmtupdate.setString(3, getPasswordChangeAllowed());
				stmtupdate.setString(4, getLanguage());
				stmtupdate.setString(5, getComment());
				stmtupdate.setString(6, getAccountExpires());
				stmtupdate.setTimestamp(7, getAccountExpiryDate());
				stmtupdate.setString(8, getEmailAddress());
				stmtupdate.setString(9, getUserId());
				stmtupdate.execute();
				stmtupdate.clearParameters();
				if (Common.hostList.getHost(getHostID()).getConnection(getSessionID()).getAutoCommit() == false)
				{
					Common.hostList.getHost(getHostID()).getConnection(getSessionID()).commit();
				}
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
