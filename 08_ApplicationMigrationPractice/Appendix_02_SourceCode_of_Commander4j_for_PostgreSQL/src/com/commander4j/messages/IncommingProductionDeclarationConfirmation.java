package com.commander4j.messages;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.commander4j.db.JDBMaterial;
import com.commander4j.db.JDBPallet;
import com.commander4j.util.JUtility;

public class IncommingProductionDeclarationConfirmation
{

	private String hostID;
	private String sessionID;
	private String errorMessage;
	private String confirmed;

	public String getErrorMessage()
	{
		return errorMessage;
	}

	private void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public String getHostID()
	{
		return hostID;
	}

	public void setHostID(String hostID)
	{
		this.hostID = hostID;
	}

	public String getSessionID()
	{
		return sessionID;
	}

	public void setSessionID(String sessionID)
	{
		this.sessionID = sessionID;
	}

	public IncommingProductionDeclarationConfirmation(String host, String session) {
		setSessionID(session);
		setHostID(host);

	}

	public Boolean processMessage(GenericMessageHeader gmh)
	{
		Boolean result = false;

		JDBPallet pal = new JDBPallet(getHostID(), getSessionID());
		JDBMaterial mat = new JDBMaterial(getHostID(), getSessionID());

		String sscc = gmh.getXMLDocument().findXPath("//message/messageData/productionDeclaration/SSCC").trim();
		confirmed = gmh.getXMLDocument().findXPath("//message/messageData/productionDeclaration/confirmed").trim().toUpperCase();

		if (pal.getPalletProperties(sscc) == false)
		{
			pal.setSSCC(sscc);
			pal.setProcessOrder(gmh.getXMLDocument().findXPath("//message/messageData/productionDeclaration/processOrder").trim());
			pal.setUom(gmh.getXMLDocument().findXPath("//message/messageData/productionDeclaration/productionUOM").trim());
			if (pal.populateFromProcessOrder() == true)
			{

				pal.setBatchNumber(gmh.getXMLDocument().findXPath("//message/messageData/productionDeclaration/batch").trim());
				pal.setQuantity(new BigDecimal(gmh.getXMLDocument().findXPath("//message/messageData/productionDeclaration/productionQuantity").trim()));

				String prodDateString = gmh.getXMLDocument().findXPath("//message/messageData/productionDeclaration/productionDate").trim();
				Timestamp prodDateTime = JUtility.getTimeStampFromISOString(prodDateString);

				String expireString = gmh.getXMLDocument().findXPath("//message/messageData/productionDeclaration/expiryDate").trim();
				Timestamp expireTime = JUtility.getTimeStampFromISOString(expireString);

				if (expireTime == null)
				{
					result = false;
					setErrorMessage("SSCC " + pal.getSSCC() + " Expiry Date in wrong format (" + expireString + ") yyyy-mm-ddThh:mm:ss");
				}
				else
				{

					pal.setBatchExpiry(JUtility.getTimestampFromDate(mat.getRoundedExpiryTime(expireTime)));

					if (pal.getBatchExpiry().before(prodDateTime))
					{
						result = false;
						setErrorMessage("SSCC " + pal.getSSCC() + " has Expiry Date before Production Date.");
					}
					else
					{

						if (pal.create("PROD DEC", "CREATE") == false)
						{
							result = false;
							setErrorMessage("SSCC " + pal.getSSCC() + " "+pal.getErrorMessage());
						}
						else
						{
							result = true;
							setErrorMessage("SSCC " + pal.getSSCC() + " created.");
						}
					}
				}

			}
			else
			{
				result = false;
				setErrorMessage("SSCC " + pal.getSSCC() + " " +pal.getErrorMessage());
			}

		}
		else
		{
			result = true;
			// setErrorMessage("SSCC " + pal.getSSCC() + " already exists.");
		}

		if (result == true)
		{
			if (confirmed.equals("Y"))
			{
				if (pal.isConfirmed() == false)
				{

					String prodDateString = gmh.getXMLDocument().findXPath("//message/messageData/productionDeclaration/productionDate").trim();
					Timestamp prodDateTime = JUtility.getTimeStampFromISOString(prodDateString);

					pal.setDateOfManufacture(prodDateTime);

					if (pal.confirm() == false)
					{
						result = false;
						setErrorMessage(pal.getErrorMessage());
					}
					else
					{
						setErrorMessage("SSCC " + pal.getSSCC() + " confirmed.");
					}
				}
				else
				{
					setErrorMessage("SSCC " + pal.getSSCC() + " already confirmed.");
					result = false;
				}
			}
		}
		pal = null;

		return result;
	}
}
