package com.commander4j.messages;

import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.commander4j.db.JDBControl;
import com.commander4j.db.JDBInterface;
import com.commander4j.db.JDBInterfaceLog;
import com.commander4j.db.JDBInterfaceRequest;
import com.commander4j.db.JDBMaterial;
import com.commander4j.db.JDBPalletHistory;
import com.commander4j.db.JDBUom;
import com.commander4j.email.JeMailOutGoingMessage;
import com.commander4j.sys.Common;
import com.commander4j.util.JFileIO;
import com.commander4j.util.JUtility;
import com.commander4j.xml.JXMLDocument;

public class OutgoingProductionDeclarationConfirmation
{
	private String hostID;
	private String sessionID;
	final Logger logger = Logger.getLogger(OutgoingProductionDeclarationConfirmation.class);
	private JeMailOutGoingMessage ogm;

	private String errorMessage;
	private JFileIO fio = new JFileIO();

	public OutgoingProductionDeclarationConfirmation(String host, String session)
	{
		setHostID(host);
		setSessionID(session);
	}

	public Element addElement(Document doc, String name, String value) {
		Element temp = (Element) doc.createElement(name);
		Text temp_value = doc.createTextNode(value);
		temp.appendChild(temp_value);
		return temp;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getHostID() {
		return hostID;
	}

	public String getSessionID() {
		return sessionID;
	}

	public Boolean processMessage(Long transactionRef)

	{
		Boolean result = false;
		String path = "";
		JDBInterfaceLog il = new JDBInterfaceLog(getHostID(), getSessionID());
		GenericMessageHeader gmh = new GenericMessageHeader();
		JDBInterface inter = new JDBInterface(getHostID(), getSessionID());
		JDBUom uom = new JDBUom(getHostID(), getSessionID());
		JDBMaterial mat = new JDBMaterial(getHostID(), getSessionID());
		JDBControl ctrl = new JDBControl(getHostID(), getSessionID());

		String expiryMode;
		expiryMode = ctrl.getKeyValue("EXPIRY DATE MODE");

		inter.getInterfaceProperties("Production Declaration", "Output");
		String device = inter.getDevice();

		JDBPalletHistory palhist = new JDBPalletHistory(getHostID(), getSessionID());
		ResultSet rs = palhist.getInterfacingData(transactionRef, "PROD DEC", "CONFIRM", Long.valueOf(1), "SSCC", "asc");
		try
		{
			if (rs.next())
			{
				palhist.getPropertiesfromResultSet(rs);

				try
				{
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();

					Document document = builder.newDocument();

					Element message = (Element) document.createElement("message");

					Element hostUniqueID = addElement(document, "hostRef", Common.hostList.getHost(getHostID()).getUniqueID());
					message.appendChild(hostUniqueID);

					Element messageRef = addElement(document, "messageRef", String.valueOf(transactionRef));
					message.appendChild(messageRef);

					Element messageType = addElement(document, "interfaceType", "Production Declaration");
					message.appendChild(messageType);

					Element messageInformation = addElement(document, "messageInformation", "SSCC=" + palhist.getPallet().getSSCC());
					message.appendChild(messageInformation);

					Element messageDirection = addElement(document, "interfaceDirection", "Output");
					message.appendChild(messageDirection);

					Element messageDate = addElement(document, "messageDate", JUtility.getISOTimeStampStringFormat(JUtility.getSQLDateTime()));
					message.appendChild(messageDate);

					Element productionDeclaration = (Element) document.createElement("productionDeclaration");

					Element sscc = addElement(document, "SSCC", palhist.getPallet().getSSCC());
					productionDeclaration.appendChild(sscc);

					Element processOrder = addElement(document, "processOrder", palhist.getPallet().getProcessOrder());
					productionDeclaration.appendChild(processOrder);

					Element recipe = addElement(document, "recipe", palhist.getPallet().getProcessOrderObj(false).getRecipe());
					productionDeclaration.appendChild(recipe);

					Element required_resource = addElement(document, "requiredResource", palhist.getPallet().getProcessOrderObj(false).getRequiredResource());
					productionDeclaration.appendChild(required_resource);
					
					Element material = addElement(document, "material", palhist.getPallet().getMaterial());
					productionDeclaration.appendChild(material);

					if (mat.getMaterialProperties(palhist.getPallet().getMaterial()) == true)
					{
						Element description = addElement(document, "description", mat.getDescription());
						productionDeclaration.appendChild(description);
						Element old_code = addElement(document,"old_code",mat.getOldMaterial());
						productionDeclaration.appendChild(old_code);
					}
					else
					{
						Element description = addElement(document, "description", "");
						productionDeclaration.appendChild(description);
						Element old_code = addElement(document,"old_code","");
						productionDeclaration.appendChild(old_code);
					}

					Element ean = addElement(document, "ean", palhist.getPallet().getEAN());
					productionDeclaration.appendChild(ean);

					Element variant = addElement(document, "variant", palhist.getPallet().getVariant());
					productionDeclaration.appendChild(variant);

					Element status = addElement(document, "status", palhist.getPallet().getStatus());
					productionDeclaration.appendChild(status);

					Element batch = addElement(document, "batch", palhist.getPallet().getBatchNumber());
					productionDeclaration.appendChild(batch);

					Element batchStatus = addElement(document, "batchStatus", palhist.getPallet().getMaterialBatchStatus());
					productionDeclaration.appendChild(batchStatus);

					Element expiryDateMode = addElement(document, "expiry_Mode", expiryMode);
					productionDeclaration.appendChild(expiryDateMode);

					if (expiryMode.equals("BATCH") == true)
					{
						Element expiryDate = addElement(document, "expiryDate", JUtility.getISOTimeStampStringFormat(palhist.getPallet().getMaterialBatchExpiryDate()));
						productionDeclaration.appendChild(expiryDate);
					}
					else
					{
						Element expiryDate = addElement(document, "expiryDate", JUtility.getISOTimeStampStringFormat(palhist.getPallet().getBatchExpiry()));
						productionDeclaration.appendChild(expiryDate);
					}

					Element location = addElement(document, "location", palhist.getPallet().getLocationID());
					productionDeclaration.appendChild(location);

					Element name = addElement(document, "name", palhist.getPallet().getLocationObj().getDescription());
					productionDeclaration.appendChild(name);

					Element gln = addElement(document, "gln", palhist.getPallet().getLocationObj().getGLN());
					productionDeclaration.appendChild(gln);

					Element plant = addElement(document, "plant", palhist.getPallet().getLocationObj().getPlant());
					productionDeclaration.appendChild(plant);

					Element warehouse = addElement(document, "warehouse", palhist.getPallet().getLocationObj().getWarehouse());
					productionDeclaration.appendChild(warehouse);

					Element storageLocation = addElement(document, "storageLocation", palhist.getPallet().getLocationObj().getStorageLocation());
					productionDeclaration.appendChild(storageLocation);

					Element storageSection = addElement(document, "storageSection", palhist.getPallet().getLocationObj().getStorageSection());
					productionDeclaration.appendChild(storageSection);

					Element storageBin = addElement(document, "storageBin", palhist.getPallet().getLocationObj().getStorageBin());
					productionDeclaration.appendChild(storageBin);

					Element storageType = addElement(document, "storageType", palhist.getPallet().getLocationObj().getStorageType());
					productionDeclaration.appendChild(storageType);

					Element productionQuantity = addElement(document, "productionQuantity", String.valueOf(palhist.getPallet().getQuantity()));
					productionDeclaration.appendChild(productionQuantity);

					String paluom = palhist.getPallet().getUom();
					paluom = uom.convertUom(inter.getUOMConversion(), paluom);

					Element productionUOM = addElement(document, "productionUOM", paluom);
					productionDeclaration.appendChild(productionUOM);

					Element productionConfirmed = addElement(document, "confirmed", palhist.getPallet().getConfirmed());
					productionDeclaration.appendChild(productionConfirmed);

					Element productionDate = addElement(document, "productionDate", JUtility.getISOTimeStampStringFormat(palhist.getPallet().getDateOfManufacture()));
					productionDeclaration.appendChild(productionDate);

					Element messageData = (Element) document.createElement("messageData");
					messageData.appendChild(productionDeclaration);

					message.appendChild(messageData);

					document.appendChild(message);

					OutputFormat format = new OutputFormat(document);
					StringWriter stringOut = new StringWriter();
					XMLSerializer serial = new XMLSerializer(stringOut, format);
					serial.serialize(document);
					logger.debug(stringOut.toString());

					JXMLDocument xmld = new JXMLDocument();
					xmld.setDocument(document);
					gmh.decodeHeader(xmld);

					if (device.equals("Disk") | device.equals("Email"))
					{

						path = inter.getRealPath();
						if (fio.writeToDisk(path, document, transactionRef,"_"+ palhist.getPallet().getLocationID().replace(" ", "_")+"_ProductionDeclaration.xml") == true)
						{
							result = true;
							il.write(gmh, GenericMessageHeader.msgStatusSuccess, "Processed OK", "File Write", fio.getFilename());
							setErrorMessage("");
							
							if (device.equals("Email"))
							{
								ogm = new JeMailOutGoingMessage(inter,transactionRef,fio);
								ogm.sendEmail();
							}
						}
						else
						{
							result = false;
							il.write(gmh, GenericMessageHeader.msgStatusError, fio.getErrorMessage(), "File Write", fio.getFilename());
							setErrorMessage(fio.getErrorMessage());
						}
					}

				}

				catch (Exception ex)
				{
					logger.error("Error sending message. " + ex.getMessage());
					ex.printStackTrace();

				}
			}
			else
			{
				logger.debug("Could not find Pallet History Interfacing Data for Transaction Ref  " + String.valueOf(transactionRef));
			}
			rs.close();
		}
		catch (SQLException e)
		{
			logger.debug("Error finding Pallet History Interfacing Data for Transaction Ref  " + String.valueOf(transactionRef) + " " + e.getMessage());
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

	private void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setHostID(String host) {
		hostID = host;
	}

	public void setSessionID(String session) {
		sessionID = session;
	}

	public void submit(long dbTransactionRef) {
		JDBInterface inter = new JDBInterface(getHostID(), getSessionID());
		inter.getInterfaceProperties("Production Declaration", "Output");
		if (inter.isEnabled() == true)
		{
			JDBInterfaceRequest ir = new JDBInterfaceRequest(getHostID(), getSessionID());
			ir.write(dbTransactionRef, "Production Declaration");
		}
		else
		{
			logger.debug("Interface Production Declaration - Output is DISABLED");
		}

	}

}
