package com.commander4j.db;

import java.awt.event.MouseEvent;

import javax.swing.JList;

import com.commander4j.sys.Common;
import com.commander4j.util.JUtility;

/**
 */
public class JDBModuleJList extends JList
{

	/**
	 * Field serialVersionUID. (value is 1) Value: {@value serialVersionUID}
	 */
	private static final long serialVersionUID = 1;
	/**
	 * @uml.property name="key"
	 */
	private String key;
	/**
	 * @uml.property name="tooltip"
	 */
	private String tooltip;
	/**
	 * @uml.property name="mod"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JDBModule mod;

	/**
	 * Method getToolTipText.
	 * 
	 * @param e
	 *            MouseEvent
	 * @return String
	 */
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

	public JDBModuleJList()
	{
		setFont(Common.font_list);
	}

	public JDBModuleJList(String host, String session)
	{
		setHostID(host);
		setSessionID(session);
		mod = new JDBModule(getHostID(), getSessionID());
		setFont(Common.font_list);
	}

	public String getToolTipText(MouseEvent e) {

		int index = locationToIndex(e.getPoint());
		if (index > -1)
		{
			try
			{
				key = ((JDBListData) getModel().getElementAt(index)).toString();

				mod.setModuleId(key);
				if (mod.getModuleProperties() == true)
				{
					tooltip = mod.getType() + " : " + JUtility.replaceNullStringwithBlank(mod.getDescription());
				}
				else
				{
					tooltip = key;
				}
			}
			catch (Exception ex)
			{
				return null;
			}
			return tooltip;
		}
		else
		{
			return null;
		}
	}

}
