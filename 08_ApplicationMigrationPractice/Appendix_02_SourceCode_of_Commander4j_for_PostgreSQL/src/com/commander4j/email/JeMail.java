package com.commander4j.email;

import javax.mail.MessagingException;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import com.commander4j.db.JDBControl;
import com.commander4j.util.JUtility;

public class JeMail
{
	private static String SMTP_HOST_LOADED = "N/A";
	private static String SMTP_AUTH_REQD;
	private static String SMTP_HOST_NAME;
	private static String SMTP_AUTH_USER;
	private static String SMTP_AUTH_PWD;
	private static String SMTP_FROM_ADRESS;
	private static String MAIL_SMTP_PORT;
	private static String SMTP_TLS_REQD;

	private String hostID;
	private String sessionID;

	public JeMail(String host, String session)
	{
		setHostID(host);
		setSessionID(session);

		JDBControl ctrl = new JDBControl(getHostID(), getSessionID());

		if (SMTP_HOST_LOADED.equals("N/A") == true)
		{
			SMTP_AUTH_REQD = ctrl.getKeyValueWithDefault("MAIL_SMTP_AUTH_REQD", "true", "SMTP Authentication Reqd");
			SMTP_TLS_REQD = ctrl.getKeyValueWithDefault("SMTP_TLS_REQD", "true", "SMTP TLS Reqd");
			SMTP_HOST_NAME = ctrl.getKeyValueWithDefault("MAIL_SMTP_HOST_NAME", "mail.testsmtp.com", "SMTP Server for sending emails");
			SMTP_AUTH_USER = ctrl.getKeyValueWithDefault("MAIL_SMTP_AUTH_USER", "mailUser", "SMTP Username");
			SMTP_AUTH_PWD = ctrl.getKeyValueWithDefault("MAIL_SMTP_AUTH_PWD", "mailPassword", "SMTP Password");
			MAIL_SMTP_PORT = ctrl.getKeyValueWithDefault("MAIL_SMTP_PORT", "25", "SMTP Port");
			SMTP_FROM_ADRESS = ctrl.getKeyValueWithDefault("MAIL_SMTP_FROM_ADRESS", "Commander4j", "From Email");
			SMTP_HOST_LOADED = host;
		}

	}

	private void setSessionID(String session)
	{
		sessionID = session;
	}

	private void setHostID(String host)
	{
		hostID = host;
	}

	private String getSessionID()
	{
		return sessionID;
	}

	private String getHostID()
	{
		return hostID;
	}

	public void setSMTPServer(String hostName, String userName, String password)
	{
		SMTP_HOST_NAME = hostName;
		SMTP_AUTH_USER = userName;
		SMTP_AUTH_PWD = password;
	}

	public void postMail(String recipientsTO[],   String subject, String message, String attachmentFilename, String attachmentLongFilename) throws MessagingException
	{

		//Email email = new SimpleEmail();
		MultiPartEmail email = new MultiPartEmail();
		email.setHostName(SMTP_HOST_NAME);
		email.setSmtpPort(Integer.valueOf(MAIL_SMTP_PORT));

		if (SMTP_AUTH_REQD.toUpperCase().equals("TRUE"))
		{
			email.setAuthenticator(new DefaultAuthenticator(SMTP_AUTH_USER, SMTP_AUTH_PWD));
			email.setTLS(Boolean.parseBoolean(SMTP_TLS_REQD));
		}
		try
		{
			email.setFrom(SMTP_FROM_ADRESS);
			email.setSubject(subject);
			email.setMsg(message+"\n\n");
			for (int x = 1; x <= recipientsTO.length; x++)
			{
				email.addTo(recipientsTO[x - 1]);
			}
			
			if (JUtility.replaceNullStringwithBlank(attachmentFilename).equals("")==false)
			{
				  // Create the attachment
				  EmailAttachment attachment = new EmailAttachment();
				  attachment.setPath(attachmentLongFilename);
				  attachment.setDisposition(EmailAttachment.ATTACHMENT);
				  attachment.setDescription(attachmentFilename);
				  attachment.setName(attachmentFilename);

				  // add the attachment
				  email.attach(attachment);
			}
			
			email.send();
		}
		catch (EmailException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
