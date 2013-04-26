package com.commander4j.sys;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBSchema;
import com.commander4j.util.JPrint;
import com.commander4j.util.JSplashScreenUtils;
import com.commander4j.util.JUnique;

import com.commander4j.util.JUtility;

public class Start
{
	public static void main(String[] args)
	{
		final Logger logger = Logger.getLogger(Start.class);

		Thread t = Thread.currentThread();
		t.setName("C4J Main");

		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Commander4j");
		Common.base_dir = System.getProperty("user.dir");
		Common.sessionID = JUnique.getUniqueID();
		Common.sd.setData(Common.sessionID, "silentExceptions", "No", true);
		Common.applicationMode = "SwingClient";
		
		JUtility.adjustForLookandFeel();
		
		JUtility.initLogging("");
		Common.hostList.loadHosts();
		
		JSplashScreenUtils.create();
		JSplashScreenUtils.updateProgress(0, "Initialising");
		JSplashScreenUtils.updateProgress(5, "Starting Logger");

		JSplashScreenUtils.updateProgress(10, "Checking Printers");

		JPrint.init();

		if (JUtility.isValidJavaVersion(Common.requiredJavaVersion) == false)
		{
			JUtility.errorBeep();
			JOptionPane.showMessageDialog(null, "This application requires java version " + Common.requiredJavaVersion + " or higher.\n Detected version is " + System.getProperty("java.version"), "Information", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		JSplashScreenUtils.updateProgress(15, "Setting Look and Feel");

		logger.info("Application starting");

		JSplashScreenUtils.updateProgress(20, "Preparing for Logon");

		JSplashScreenUtils.hide();

		//Common.hostList.loadHosts();

		final JDialogHosts hsts = new JDialogHosts(null);
		hsts.setVisible(false);
		hsts.dispose();

		if (Common.selectedHostID.equals("Cancel") == false)
		{
			if (Common.displaySplashScreen)
			{
				JSplashScreenUtils.show();

				JSplashScreenUtils.updateProgress(25, "Initialising....");
				JSplashScreenUtils.updateProgress(30, "Open SQL Library....");
				JSplashScreenUtils.updateProgress(35, "Building jdbc connection string....");
				JSplashScreenUtils.updateProgress(40, "Loading database SQL statements...");
				JSplashScreenUtils.updateProgress(45, "Open Virtual Views....");
				JSplashScreenUtils.updateProgress(55, "Building jdbc connection string....");
				JSplashScreenUtils.updateProgress(60, "Loading database view statements...");
				JSplashScreenUtils.updateProgress(65, "Loading database view statements...");
				JSplashScreenUtils.updateProgress(70, "Connecting to Host database...");
			}

			if (Common.hostList.getHost(Common.selectedHostID).connect(Common.sessionID, Common.selectedHostID) == true)
			{
				JSplashScreenUtils.updateProgress(75, "Validating Schema Version...");

				JDBSchema schema = new JDBSchema(Common.sessionID, Common.hostList.getHost(Common.selectedHostID));

				schema.validate(true);

				JSplashScreenUtils.updateProgress(80, "Initialising Help Subsystem...");

				JSplashScreenUtils.updateProgress(85, "Loading EAN Barcode definitions...");

				JUtility.initEANBarcode();

				JSplashScreenUtils.updateProgress(90, "Initialising Reporting system...");

				JLaunchReport.init();

				JSplashScreenUtils.updateProgress(95, "Initialising Common values...");

				Common.init();

				JSplashScreenUtils.updateProgress(100, "Loading Logon dialog...");

				final JDialogLogin lg;

				JSplashScreenUtils.hide();
				JSplashScreenUtils.remove();

				lg = new JDialogLogin(null);

				if (lg.validated)
				{
					JDBLanguage lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);
					lang.preLoad("%");
					Common.mainForm = new JFrameMain();
					Common.mainForm.setIconImage(Common.imageIconloader.getImageIcon(Common.image_barcode).getImage());
					Common.mainForm.setVisible(true);
				}
				else
				{
					Common.hostList.getHost(Common.selectedHostID).disconnect(Common.sessionID);
					System.exit(0);
				}
			}
			else
			{
				System.exit(0);
			}
		}
		else
		{
			System.exit(0);
		}
	}
}
