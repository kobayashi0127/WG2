package com.commander4j.thread;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.commander4j.db.JDBDespatch;
import com.commander4j.db.JDBPallet;
import com.commander4j.messages.OutgoingDespatchConfirmation;
import com.commander4j.messages.OutgoingDespatchPreAdvice;
import com.commander4j.messages.OutgoingEquipmentTracking;


public class DespatchConfirmThread extends Thread
{
	public boolean allDone = false;
	private final Logger logger = Logger.getLogger(DespatchConfirmThread.class);
	private String session;
	private String host;
	private String despatch;

	public DespatchConfirmThread(String host,String session,String despatchNO)
	{
		super();
		setHostID(host);
		setSessonID(session);
		setDespatchNo(despatchNO);
	}

	private String getDespatchNo()
	{
		return despatch;
	}
	
	private void setDespatchNo(String desp)
	{
		despatch = desp;
	}
	
	private void setHostID(String hst)
	{
		host = hst;
	}
	
	private void setSessonID(String sess)
	{
		session = sess;
	}
	
	private String getHost()
	{
		return host;
	}
	
	private String getSession()
	{
		return session;
	}

	public void run()
	{
		logger.debug("Background confirmation started.");
		
		JDBDespatch desp = new JDBDespatch(getHost(),getSession());
		
		if (desp.getDespatchProperties(getDespatchNo()))
		{
			Long txn = desp.getTransactionRef();
			LinkedList<String> assignedList = new LinkedList<String>();
			JDBPallet pal = new JDBPallet(getHost(),getSession());
			OutgoingDespatchConfirmation odc;
			OutgoingDespatchPreAdvice opa;
			OutgoingEquipmentTracking oet;
			
			assignedList.clear();
			assignedList.addAll(desp.getAssignedSSCCs());
			
			for (int j = 0; j < assignedList.size(); j++)
			{
				if (pal.getPalletProperties(assignedList.get(j)))
				{
					logger.debug("Background confirmation processing SSCC="+assignedList.get(j));
					pal.writePalletHistory(txn, "DESPATCH", "FROM");
					pal.setLocationID(desp.getLocationIDTo());
					pal.updateLocationID();
					pal.writePalletHistory(txn, "DESPATCH", "TO");
				}
			}
			
			pal = null;

			if (desp.getLocationDBTo().isDespatchConfirmationMessageRequired())
			{
				logger.debug("Background confirmation requesting Despatch Confirmation.");
				odc = new OutgoingDespatchConfirmation(getHost(),getSession());
				odc.submit(txn);
				odc = null;
			}

			if (desp.getLocationDBTo().isDespatchEquipmentTrackingMessageRequired())
			{
				logger.debug("Background confirmation requesting Equipment Tracking.");
				oet = new OutgoingEquipmentTracking(getHost(),getSession());
				oet.submit(txn);
				oet = null;
			}

			if (desp.getLocationDBTo().isDespatchPreAdviceMessageRequired())
			{
				logger.debug("Background confirmation requesting Pre Advice.");
				opa = new OutgoingDespatchPreAdvice(getHost(),getSession());
				opa.submit(txn);
				opa = null;
			}
			
		}
		logger.debug("Background confirmation finished.");
	}
}
