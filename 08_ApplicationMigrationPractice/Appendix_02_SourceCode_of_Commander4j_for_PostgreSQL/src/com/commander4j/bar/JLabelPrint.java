package com.commander4j.bar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.commander4j.app.JVersion;
import com.commander4j.db.JDBControl;
import com.commander4j.sys.Common;
import com.commander4j.util.JFileIO;
import com.commander4j.util.JPrint;
import com.commander4j.util.JUtility;

public class JLabelPrint
{
	byte[] v_bytes;
	String v_string = "";
	PrintService v_printService = null;
	DocPrintJob v_job;
	DocFlavor v_flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
	Doc v_doc;
	String v_queuename;
	File v_template;
	String v_company_name = "";
	String v_plant = "";
	String v_header_comment = "";
	String expiry_mode = "";
	PreparedStatement v_ps;
	HashMap<String, String> variables = new HashMap<String, String>();
	HashMap<String, String> expanded_variables = new HashMap<String, String>();
	private final Logger logger = Logger.getLogger(JLabelPrint.class);

	public JLabelPrint(String queuename, String filename, PreparedStatement ps) {

		try {
			v_ps = ps;
			v_queuename = queuename;
			v_printService = JPrint.getPrinterServicebyName(v_queuename);

			v_template = new File("labels/" + filename);
			v_string = getTemplate(v_template);

			JDBControl ctrl = new JDBControl(Common.selectedHostID, Common.sessionID);
			v_company_name = ctrl.getKeyValue("COMPANY NAME");
			v_plant = ctrl.getKeyValue("PLANT");
			v_header_comment = ctrl.getKeyValue("LABEL_HEADER_COMMENT");
			expiry_mode = ctrl.getKeyValue("EXPIRY DATE MODE");
		}
		catch (Exception ex) {
			JUtility.errorBeep();
			JOptionPane.showMessageDialog(null, "Unable to print to selected printer : " + ex.getMessage(), "Printing Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void print(int copiesOfEachLabel, boolean incLabelHeaderText) {

		String all_labels = "";

		try {
			ResultSet rs;
			rs = v_ps.executeQuery();
			String current_label = "";

			while (rs.next()) {

				expanded_variables = expandVariables(rs, variables);
				optimiseEAN128();

				for (int labelNo = 1; labelNo <= copiesOfEachLabel; labelNo++) {
					current_label = v_string;
					current_label = replaceTokens(labelNo, rs, current_label, incLabelHeaderText);
					all_labels = all_labels + current_label;
				}

			}
			rs.close();
			v_ps.close();

			v_job = v_printService.createPrintJob();

			String filename = System.getProperty("java.io.tmpdir") + UUID.randomUUID().toString() + ".cmd4j";

			v_bytes = all_labels.getBytes();

			logger.debug("Writing print job to : " + filename);
			FileOutputStream fos = new FileOutputStream(filename);
			fos.write(v_bytes);
			fos.close();

			DocAttributeSet das = new HashDocAttributeSet();

			try {
				PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
				pras.add(new Copies(1));

				FileInputStream fis = new FileInputStream(filename);
				v_doc = new SimpleDoc(fis, v_flavor, das);
				v_job.print(v_doc, pras);
				logger.debug("Submitting file to spooler : " + filename);
				fis.close();

				JFileIO deleteFile = new JFileIO();
				logger.debug("Deleting file : " + filename);
				deleteFile.deleteFile(filename);
			}
			catch (Exception ex) {

				if (Common.applicationMode.equals("SwingClient")) {
					JUtility.errorBeep();
					JOptionPane.showMessageDialog(null, "Unable to print to selected printer : " + ex.getMessage(), "Printing Error", JOptionPane.ERROR_MESSAGE);
				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			//PG add ->
			String driver = Common.hostList.getHost(Common.selectedHostID).getDatabaseParameters().getjdbcDriver();
			if (driver.equals("org.postgresql.Driver")){
				try
				{
					Common.hostList.getHost(Common.selectedHostID).getConnection(Common.sessionID).rollback();
				}
				catch (Exception e2)
				{
					e2.printStackTrace();
				}
			}
			//PG add end
		}
	}

	public HashMap<String, String> expandVariables(ResultSet rs, HashMap<String, String> hm) {
		HashMap<String, String> result = new HashMap<String, String>();

		Set<Entry<String, String>> set = hm.entrySet();
		Iterator<Entry<String, String>> i = set.iterator();

		while (i.hasNext()) {
			Map.Entry<String, String> me = i.next();

			result.put((String) me.getKey(), replaceTokens(0, rs, (String) me.getValue(), true));
		}

		return result;
	}

	public void optimiseEAN128() {

		Set<Entry<String, String>> set = expanded_variables.entrySet();
		Iterator<Entry<String, String>> i = set.iterator();
		String key = "";
		String originalBarcode = "";
		String optimisedBarcode = "";
		String currentMode = "";
		String newMode = "";
		String initialFNCReqd = "^";
		String pair = "";
		int start = 0;
		int end = 0;
		int size = 0;

		while (i.hasNext()) {
			Map.Entry<String, String> me = i.next();

			key = (String) me.getKey();
			if (key.startsWith("*BARCODE") == true) {
				optimisedBarcode = "";
				currentMode = "";
				originalBarcode = (String) me.getValue();
				size = originalBarcode.length();

				for (int x = 0; x < size; x = x + 2) {
					start = x;
					end = start + 2;
					if (end > size) {
						end = size - 1;
					}
					pair = originalBarcode.substring(start, end);

					if (pair.startsWith("^") == false) {

						if ((pair.length() < 2) | (pair.endsWith("^"))) {
							// Odd (single char at end of string //
							newMode = "alphanumeric";
						}
						else {
							try {
								// Is the pair numeric //
								@SuppressWarnings("unused")
								int test = Integer.valueOf(pair);
								newMode = "numeric";
							}
							catch (Exception ex) {
								// Not numeric //
								newMode = "alphanumeric";
							}

						}
						if (newMode.equals(currentMode) == false) {
							if (optimisedBarcode.equals("")) {
								initialFNCReqd = "^";
							}
							else {
								initialFNCReqd = "";
							}
							if (newMode.equals("numeric")) {
								optimisedBarcode = optimisedBarcode + expanded_variables.get("*CODEC*") + initialFNCReqd;
							}
							if (newMode.equals("alphanumeric")) {
								optimisedBarcode = optimisedBarcode + expanded_variables.get("*CODEB*") + initialFNCReqd;
							}

							currentMode = newMode;
						}
						optimisedBarcode = optimisedBarcode + pair;
					}
					else {
						optimisedBarcode = optimisedBarcode + "^";
						x--;
					}
				}

				optimisedBarcode = optimisedBarcode.replace("^", expanded_variables.get("*FNC1*"));

				expanded_variables.put(key, optimisedBarcode);
			}
		}
	}

	public String substVariables(String line) {
		String result = line;

		Set<Entry<String, String>> set = expanded_variables.entrySet();
		Iterator<Entry<String, String>> i = set.iterator();

		while (i.hasNext()) {
			Map.Entry<String, String> me = i.next();
			result = result.replace((String) me.getKey(), (String) me.getValue());
		}

		return result;
	}

	public String replaceTokens(int labelNo, ResultSet rs, String Line, boolean incHeaderText) {
		String result = Line;
		try {
			
			// 2010-10-08T13:13:52
			String systemTime = JUtility.getISOTimeStampStringFormat(JUtility.getSQLDateTime());
			String yearYYYY = systemTime.substring(0, 4);
			String yearYY = systemTime.substring(2, 4);
			String month = systemTime.substring(5, 7);
			String day = systemTime.substring(8, 10);
			String hour = systemTime.substring(11, 13);
			String minute = systemTime.substring(14, 16);
			String second = systemTime.substring(17, 19);;
			
			if (result.contains("*SYS_YEAR4*")) result = result.replace("*SYS_YEAR4*", yearYYYY);
			if (result.contains("*SYS_YEAR2*")) result = result.replace("*SYS_YEAR2*", yearYY);
			if (result.contains("*SYS_MONTH*")) result = result.replace("*SYS_MONTH*", month);
			if (result.contains("*SYS_DAY*")) result = result.replace("*SYS_DAY*", day);
			if (result.contains("*SYS_HOUR*")) result = result.replace("*SYS_HOUR*", hour);
			if (result.contains("*SYS_MIN*")) result = result.replace("*SYS_MIN*", minute);
			if (result.contains("*SYS_SECOND*")) result = result.replace("*SYS_SECOND*", second);
			
			if (labelNo > 0) {
				result = result.replace("*LABEL_NO*", String.valueOf(labelNo).trim());
			}

			try
			{
			if (JUtility.replaceNullStringwithBlank(rs.getString("PRINT_ON_LABEL")).equals("Y"))
			{
				result = result.replace("*COMPANY_NAME*", JUtility.replaceNullStringwithBlank(rs.getString("CUSTOMER_NAME")));
			}
			else
			{
				result = result.replace("*COMPANY_NAME*", v_company_name);
			}
			}
			catch (Exception ex)
			{
				
			}
			
			result = result.replace("*PLANT*", v_plant);
			if (incHeaderText) {
				result = result.replace("*HEADER_COMMENT*", v_header_comment);
			}
			else {
				result = result.replace("*HEADER_COMMENT*", "");
			}
			if (result.contains("*REQUIRED_RESOURCE*"))
				result = result.replace("*REQUIRED_RESOURCE*", rs.getString("REQUIRED_RESOURCE"));
			
			if (result.contains("*MATERIAL*"))
				result = result.replace("*MATERIAL*", rs.getString("material"));
			
			if (result.contains("*OLD_MATERIAL*"))
				result = result.replace("*OLD_MATERIAL*", rs.getString("old_material"));
			
			result = result.replace("*PRINTED*", "Commander4j " + JVersion.getProgramVersion());

			if (result.contains("*SSCC,1,3*"))
				result = result.replace("*SSCC,1,3*", rs.getString("SSCC").substring(0, 3));
			if (result.contains("*SSCC,4,5*"))
				result = result.replace("*SSCC,4,5*", rs.getString("SSCC").substring(3, 8));
			if (result.contains("*SSCC,9,5*"))
				result = result.replace("*SSCC,9,5*", rs.getString("SSCC").substring(8, 13));
			if (result.contains("*SSCC,14,5*"))
				result = result.replace("*SSCC,14,5*", rs.getString("SSCC").substring(13, 18));
			if (result.contains("*SSCC,8,10*"))
				result = result.replace("*SSCC,8,10*", rs.getString("SSCC").substring(7, 17));
			if (result.contains("*SSCC,9,9*"))
				result = result.replace("*SSCC,9,9*", rs.getString("SSCC").substring(8, 17));
			if (result.contains("*SSCC*"))
				result = result.replace("*SSCC*", rs.getString("SSCC"));

			if (result.contains("*EAN*"))
				result = result.replace("*EAN*", JUtility.padString(rs.getString("EAN"), false, 14, "0"));
			if (result.contains("*VARIANT*"))
				result = result.replace("*VARIANT*", JUtility.padString(rs.getString("VARIANT"), false, 2, "0"));
			if (result.contains("*QUANTITY*"))
				result = result.replace("*QUANTITY*", JUtility.padString(String.valueOf((rs.getBigDecimal("QUANTITY").intValue())), false, 4, "0"));
			if (result.contains("*LAYERS*"))
				result = result.replace("*LAYERS*", rs.getString("LAYERS"));


			if (result.contains("*DESCRIPTION*"))
				result = result.replace("*DESCRIPTION*", rs.getString("DESCRIPTION"));
			if (result.contains("*BATCH_NUMBER*"))
				result = result.replace("*BATCH_NUMBER*", rs.getString("BATCH_NUMBER"));
			if (result.contains("*PROCESS_ORDER*"))
				result = result.replace("*PROCESS_ORDER*", rs.getString("PROCESS_ORDER"));
			
			if (result.contains("*SAMPLE_ID*"))
				result = result.replace("*SAMPLE_ID*", JUtility.padString(String.valueOf(rs.getLong("SAMPLE_ID")), false, 9, "0"));
			
			if (result.contains("*USER_DATA_1*"))
				result = result.replace("*USER_DATA_1*", rs.getString("USER_DATA_1"));

			if (result.contains("*USER_DATA_2*"))
				result = result.replace("*USER_DATA_2*", rs.getString("USER_DATA_2"));

			if (result.contains("*SAMPLE_DATE*")) {
				String newValue = JUtility.getISOTimeStampStringFormat(JUtility.getTimestampFromDate(rs.getTimestamp("SAMPLE_DATE")));
				newValue = newValue.substring(0, 16);
				newValue = newValue.replace("T", " ");
				result = result.replace("*SAMPLE_DATE*",newValue);
			}			
			
			
			if (result.contains("*EXPIRY_")) {
				Calendar caldate = Calendar.getInstance();
				if (expiry_mode.equals("BATCH")) {
					caldate.setTime(rs.getDate("EXPIRY_DATE"));
				}
				else {
					caldate.setTime(rs.getDate("SSCC_EXPIRY_DATE"));
				}
				String expiry_day = JUtility.padString(String.valueOf(caldate.get(Calendar.DATE)), false, 2, "0");
				String expiry_month = JUtility.padString(String.valueOf(caldate.get(Calendar.MONTH) + 1), false, 2, "0");
				String expiry_year4 = JUtility.padString(String.valueOf(caldate.get(Calendar.YEAR)), false, 4, "0");
				String expiry_year2 = expiry_year4.substring(2, 4);

				if (result.contains("*EXPIRY_DAY*"))
					result = result.replace("*EXPIRY_DAY*", expiry_day);
				if (result.contains("*EXPIRY_MONTH*"))
					result = result.replace("*EXPIRY_MONTH*", expiry_month);
				if (result.contains("*EXPIRY_YEAR2*"))
					result = result.replace("*EXPIRY_YEAR2*", expiry_year2);
				if (result.contains("*EXPIRY_YEAR4*"))
					result = result.replace("*EXPIRY_YEAR4*", expiry_year4);
			}

			result = substVariables(result);

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public String getTemplate(File aFile) {

		StringBuilder contents = new StringBuilder();

		try {
			BufferedReader input = new BufferedReader(new FileReader(aFile));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					line = line.trim();
					if ((line.startsWith("/*") == false) & (line.length() > 0)) {
						if (line.startsWith("DEFINE BARCODE ")) {
							String parse = line.substring(15);
							String delims = "[ ]+";
							String[] tokens = parse.split(delims);

							variables.put(tokens[0], tokens[1]);

						}
						else {
							contents.append(line);
							contents.append(System.getProperty("line.separator"));
						}
					}
				}
			}
			finally {
				input.close();
			}
		}
		catch (IOException ex) {
			if (Common.applicationMode.equals("SwingClient")) {
				JUtility.errorBeep();
				JOptionPane.showMessageDialog(null, "Unable to load label template " + aFile.getName(), "Printing Error", JOptionPane.ERROR_MESSAGE);
			}
		}

		return contents.toString();
	}
}
