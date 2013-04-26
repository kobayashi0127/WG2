package com.commander4j.messages;

import com.commander4j.db.JDBQMResult;
import com.commander4j.db.JDBQMSample;
import com.commander4j.db.JDBQMTest;
import com.commander4j.sys.Common;
import com.commander4j.util.JUtility;

public class IncommingInspectionResult
{

	private String hostID;
	private String sessionID;
	private String errorMessage;

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

	public IncommingInspectionResult(String host, String session)
	{
		setSessionID(session);
		setHostID(host);
	}

	public Boolean processMessage(GenericMessageHeader gmh)
	{
		Boolean result = true;
		int updated = 0;
		int created = 0;
		int errors = 0;

		JDBQMResult res = new JDBQMResult(getHostID(), getSessionID());
		JDBQMSample samp = new JDBQMSample(getHostID(), getSessionID());
		JDBQMTest test = new JDBQMTest(getHostID(), getSessionID());

		String sampleIDs = "1";
		Long sampleID = 1L;
		int sampleOccur = 1;

		String inspectionID;
		String activityID;

		while (sampleIDs.length() > 0)
		{
			sampleIDs = JUtility.replaceNullStringwithBlank(gmh.getXMLDocument().findXPath("//message/messageData/sample[" + String.valueOf(sampleOccur) + "]/id").trim());

			try
			{
				sampleID = Long.valueOf(sampleIDs);
			} catch (Exception ex)
			{
				sampleID = 0L;
			}

			if (sampleID > 0)
			{

				if (samp.isValidSample(sampleID))
				{
					samp.getProperties();
					inspectionID = samp.getInspectionID();
					activityID = samp.getActivityID();

					String testId = "1";
					int testOccur = 1;

					while (testId.length() > 0)
					{
						testId = JUtility.replaceNullStringwithBlank(gmh.getXMLDocument().findXPath("//message/messageData/sample[" + String.valueOf(sampleOccur) + "]/test[" + String.valueOf(testOccur) + "]/id").trim());

						if (testId.length() > 0)
						{

							if (test.isValid(inspectionID, activityID, testId))
							{

								String resultData = JUtility.replaceNullStringwithBlank(gmh.getXMLDocument().findXPath("//message/messageData/sample[" + String.valueOf(sampleOccur) + "]/test[" + String.valueOf(testOccur) + "]/data").trim());
								if (res.isValidResult(sampleID, testId) == true)
								{
									res.setValue(resultData);
									res.update();
									updated++;
								} else
								{
									res.create(sampleID, testId, resultData, "Created", Common.userList.getUser(getSessionID()).getUserId());
									created++;
								}
							}
							else
							{
								result = false;
								setErrorMessage("Invalid Test ID [" + testId+"] for Sample ID " + String.valueOf(sampleID));
								errors++;
							}

						}

						testOccur++;
					}
				} else
				{
					result = false;
					setErrorMessage("Invalid Sample ID " + String.valueOf(sampleID));
					errors++;
				}

			}
			sampleOccur++;
		}

		res = null;
		samp = null;
		test = null;

		if (errors == 0)
		{
			setErrorMessage(String.valueOf(created) + " Created, " + String.valueOf(updated) + " Updated.");
		}
		return result;

	}
}
