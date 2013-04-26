package com.commander4j.xml;

//import java.io.File;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.commander4j.sys.Common;
import com.commander4j.sys.JHost;
import com.commander4j.util.JEncryption;
import com.commander4j.util.JUnique;
import com.commander4j.util.JUtility;

;

/**
 */
public class JXMLHost
{
	/**
	 * Method writeHosts.
	 * 
	 * @param hostList
	 *            LinkedList<JHost>
	 */
	private static int iNumberOfHosts;

	public static void writeHosts(LinkedList<JHost> hostList,String splash) {
		final Logger logger = Logger.getLogger(JXMLHost.class);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();

			Document document = builder.newDocument();

			// Create the root element - hosts //
			Element hosts = (Element) document.createElement("Hosts");

			// Number of Hosts //
			Element numberofhosts = (Element) document.createElement("NumberOfSites");
			Text text = document.createTextNode(Integer.toString(hostList.size()));
			numberofhosts.appendChild(text);
			hosts.appendChild(numberofhosts);
			
			// Splash Screen Enabled //
			Element splashScreen = (Element) document.createElement("SplashScreen");
			text = document.createTextNode(splash);
			splashScreen.appendChild(text);
			hosts.appendChild(splashScreen);

			for (int j = 0; j < hostList.size(); j++)
			{
				// Create the 1st level - site //
				Element site = (Element) document.createElement("Site");
				site.setAttribute("Number", hostList.get(j).getSiteNumber());

				Element enabled = (Element) document.createElement("Enabled");
				text = document.createTextNode(hostList.get(j).getEnabled().trim());
				enabled.appendChild(text);

				Element uniqueid = (Element) document.createElement("UniqueID");
				text = document.createTextNode(hostList.get(j).getUniqueID().trim());
				uniqueid.appendChild(text);

				Element description = (Element) document.createElement("Description");
				text = document.createTextNode(hostList.get(j).getSiteDescription().trim());
				description.appendChild(text);

				Element url = (Element) document.createElement("URL");
				text = document.createTextNode(hostList.get(j).getSiteURL().trim());
				url.appendChild(text);

				Element databasedriver = (Element) document.createElement("DatabaseDriver");

				Element jdbcDriver = (Element) document.createElement("jdbcDriver");
				text = document.createTextNode(hostList.get(j).getDatabaseParameters().getjdbcDriver().trim());
				jdbcDriver.appendChild(text);

				Element jdbcConnectString = (Element) document.createElement("jdbcConnectString");
				text = document.createTextNode(hostList.get(j).getDatabaseParameters().getjdbcConnectString().trim());
				jdbcConnectString.appendChild(text);

				Element jdbcDatabaseDateTimeToken = (Element) document.createElement("jdbcDatabaseDateTimeToken");
				text = document.createTextNode(hostList.get(j).getDatabaseParameters().getjdbcDatabaseDateTimeToken().trim());
				jdbcDatabaseDateTimeToken.appendChild(text);

				Element jdbcDatabaseSelectLimit = (Element) document.createElement("jdbcDatabaseSelectLimit");
				text = document.createTextNode(hostList.get(j).getDatabaseParameters().getjdbcDatabaseSelectLimit().trim());
				jdbcDatabaseSelectLimit.appendChild(text);

				Element jdbcDatabaseSchema = (Element) document.createElement("jdbcDatabaseSchema");
				text = document.createTextNode(hostList.get(j).getDatabaseParameters().getjdbcDatabaseSchema().trim());
				jdbcDatabaseSchema.appendChild(text);

				databasedriver.appendChild(jdbcDriver);
				databasedriver.appendChild(jdbcConnectString);
				databasedriver.appendChild(jdbcDatabaseDateTimeToken);
				databasedriver.appendChild(jdbcDatabaseSelectLimit);
				databasedriver.appendChild(jdbcDatabaseSchema);

				Element DatabaseParameters = (Element) document.createElement("DatabaseParameters");

				Element jdbcUsername = (Element) document.createElement("jdbcUsername");
				text = document.createTextNode(hostList.get(j).getDatabaseParameters().getjdbcUsername().trim());
				jdbcUsername.appendChild(text);

				Element jdbcPassword = (Element) document.createElement("jdbcPassword");
				text = document.createTextNode(JEncryption.encrypt(hostList.get(j).getDatabaseParameters().getjdbcPassword().trim()));
				jdbcPassword.appendChild(text);

				Element jdbcServer = (Element) document.createElement("jdbcServer");
				text = document.createTextNode(hostList.get(j).getDatabaseParameters().getjdbcServer().trim());
				jdbcServer.appendChild(text);

				Element jdbcPort = (Element) document.createElement("jdbcPort");
				text = document.createTextNode(hostList.get(j).getDatabaseParameters().getjdbcPort().trim());
				jdbcPort.appendChild(text);

				Element jdbcSID = (Element) document.createElement("jdbcSID");
				text = document.createTextNode(hostList.get(j).getDatabaseParameters().getjdbcSID().trim());
				jdbcSID.appendChild(text);

				Element jdbcDatabase = (Element) document.createElement("jdbcDatabase");
				text = document.createTextNode(hostList.get(j).getDatabaseParameters().getjdbcDatabase().trim());
				jdbcDatabase.appendChild(text);

				DatabaseParameters.appendChild(jdbcUsername);
				DatabaseParameters.appendChild(jdbcPassword);
				DatabaseParameters.appendChild(jdbcServer);
				DatabaseParameters.appendChild(jdbcPort);
				DatabaseParameters.appendChild(jdbcSID);
				DatabaseParameters.appendChild(jdbcDatabase);

				site.appendChild(enabled);
				site.appendChild(uniqueid);
				site.appendChild(description);
				site.appendChild(url);
				site.appendChild(databasedriver);
				site.appendChild(DatabaseParameters);

				hosts.appendChild(site);
			}

			document.appendChild(hosts);

			// File file = new File("xml/hosts.xml");

			XMLSerializer serializer = new XMLSerializer();
			java.io.FileWriter fw = new java.io.FileWriter("xml/hosts/hosts.xml");
			serializer.setOutputCharStream(fw);
			serializer.serialize(document);
			fw.close();

		}
		catch (ParserConfigurationException pce)
		{
			// Parser with specified options can't be built
			pce.printStackTrace();
		}

		catch (Exception ex)
		{
			logger.error("writeHosts : cannot write to file");
		}

	}

	public static int getNumberOfHostsLoaded() {
		return iNumberOfHosts;
	}

	/**
	 * Method loadHosts.
	 * 
	 * @param filename
	 *            String
	 * @param parse
	 *            boolean
	 * @return LinkedList<JHost>
	 */
	public static LinkedList<JHost> loadHosts(String filename, boolean parse) {

		String sNumberOfSites = "";
		String splash = "Y";

		String jdbcDriver = "";
		String jdbcConnectString = "";
		String sitejdbcConnectString = "";
		String jdbcDatabaseDateTimeToken = "";
		String jdbcDatabaseSelectLimit = "";
		String jdbcDatabaseSchema = "";
		String SiteNumber = "";
		String SiteDescription = "";
		String SiteURL = "";
		String jdbcUsername = "";
		String jdbcPassword = "";
		String jdbcServer = "";
		String jdbcPort = "";
		String jdbcSID = "";
		String jdbcDatabase = "";
		String SiteEnabled = "";
		String uniqueid = "";

		LinkedList<JHost> hostList = new LinkedList<JHost>();
		hostList.clear();

		if (filename.isEmpty())
		{
			filename = "xml/hosts/hosts.xml";
		}

		JXMLDocument xmltest = new JXMLDocument(filename);

		sNumberOfSites = xmltest.findXPath("//Hosts/NumberOfSites");
		iNumberOfHosts = Integer.valueOf(sNumberOfSites).intValue();
		splash = xmltest.findXPath("//Hosts/SplashScreen");
		if (splash.equals("N"))
		{
			Common.displaySplashScreen=false;
		}
		else
		{
			Common.displaySplashScreen=true;
		}

		if (iNumberOfHosts > 0)
		{
			for (int i = 1; i <= iNumberOfHosts; i++)
			{
				SiteNumber = Integer.toString(i);

				SiteDescription = xmltest.findXPath("//Hosts/Site[@Number='" + SiteNumber + "']/Description").trim();
				SiteURL = xmltest.findXPath("//Hosts/Site[@Number='" + SiteNumber + "']/URL").trim();
				SiteEnabled = xmltest.findXPath("//Hosts/Site[@Number='" + SiteNumber + "']/Enabled").trim();
				uniqueid = xmltest.findXPath("//Hosts/Site[@Number='" + SiteNumber + "']/UniqueID").trim();
				uniqueid = JUtility.replaceNullStringwithBlank(uniqueid);
				if (uniqueid.length() == 0)
				{
					uniqueid = JUnique.getUniqueID();
				}

				jdbcDriver = xmltest.findXPath("//Hosts/Site[@Number='" + SiteNumber + "']/DatabaseDriver/jdbcDriver").trim();
				jdbcConnectString = xmltest.findXPath("//Hosts/Site[@Number='" + SiteNumber + "']/DatabaseDriver/jdbcConnectString").trim();
				jdbcDatabaseDateTimeToken = xmltest.findXPath("//Hosts/Site[@Number='" + SiteNumber + "']/DatabaseDriver/jdbcDatabaseDateTimeToken").trim();
				jdbcDatabaseSelectLimit = xmltest.findXPath("//Hosts/Site[@Number='" + SiteNumber + "']/DatabaseDriver/jdbcDatabaseSelectLimit").trim();
				jdbcDatabaseSchema = xmltest.findXPath("//Hosts/Site[@Number='" + SiteNumber + "']/DatabaseDriver/jdbcDatabaseSchema").trim();

				jdbcUsername = xmltest.findXPath("//Hosts/Site[@Number='" + SiteNumber + "']/DatabaseParameters/jdbcUsername").trim();
				jdbcPassword = xmltest.findXPath("//Hosts/Site[@Number='" + SiteNumber + "']/DatabaseParameters/jdbcPassword").trim();
				jdbcPassword = JEncryption.decrypt(jdbcPassword);
				jdbcServer = xmltest.findXPath("//Hosts/Site[@Number='" + SiteNumber + "']/DatabaseParameters/jdbcServer").trim();
				jdbcPort = xmltest.findXPath("//Hosts/Site[@Number='" + SiteNumber + "']/DatabaseParameters/jdbcPort").trim();
				jdbcSID = xmltest.findXPath("//Hosts/Site[@Number='" + SiteNumber + "']/DatabaseParameters/jdbcSID").trim();
				jdbcDatabase = xmltest.findXPath("//Hosts/Site[@Number='" + SiteNumber + "']/DatabaseParameters/jdbcDatabase").trim();

				sitejdbcConnectString = jdbcConnectString;
				if (parse)
				{
					sitejdbcConnectString = sitejdbcConnectString.replaceAll("jdbcServer", jdbcServer);
					sitejdbcConnectString = sitejdbcConnectString.replaceAll("jdbcPort", jdbcPort);
					sitejdbcConnectString = sitejdbcConnectString.replaceAll("jdbcSID", jdbcSID);
					sitejdbcConnectString = sitejdbcConnectString.replaceAll("jdbcDatabase", jdbcDatabase);
				}

				JHost host = new JHost();
				host.setSiteNumber(SiteNumber);
				host.setSiteDescription(SiteDescription);
				host.setSiteURL(SiteURL);
				host.setEnabled(SiteEnabled);
				host.setUniqueID(uniqueid);
				host.getDatabaseParameters().setjdbcDriver(jdbcDriver);
				host.getDatabaseParameters().setjdbcDatabaseDateTimeToken(jdbcDatabaseDateTimeToken);
				host.getDatabaseParameters().setjdbcDatabaseSelectLimit(jdbcDatabaseSelectLimit);
				host.getDatabaseParameters().setjdbcDatabaseSchema(jdbcDatabaseSchema);
				host.getDatabaseParameters().setjdbcUsername(jdbcUsername);
				host.getDatabaseParameters().setjdbcPassword(jdbcPassword);
				host.getDatabaseParameters().setjdbcServer(jdbcServer);
				host.getDatabaseParameters().setjdbcPort(jdbcPort);
				host.getDatabaseParameters().setjdbcSID(jdbcSID);
				host.getDatabaseParameters().setjdbcDatabase(jdbcDatabase);

				hostList.addLast(host);

			}
		}
		return hostList;
	}
}
