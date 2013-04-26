package com.commander4j.util;

import java.awt.print.PrinterJob;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.commander4j.sys.Common;

/**
 */
public class JPrint
{

	private static String errorMessage = "";
	private static String preferredPrinterQueueName = new String();
	private static PrintService preferredPrinterService;
	private static int printerCount = 0;
	private static LinkedList<String> printernames = new LinkedList<String>();
	private static PrintService[] printers;
	private static LinkedList<PrintService> printerservices = new LinkedList<PrintService>();

	public static String getErrorMessage() {
		return errorMessage;
	}

	private static void setErrorMessage(String errorMsg) {
		errorMessage = errorMsg;
	}

	public static Boolean SocketPrint(String ip, int port, String data) {
		Boolean result = true;
		try
		{
			Socket clientSocket = new Socket(ip, port);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(data);
			clientSocket.close();
		}
		catch (Exception ex)
		{
			result = false;
			setErrorMessage(ex.getMessage());
		}
		return result;
	}


	public static String getDefaultPrinterQueueName() {
		final Logger logger = Logger.getLogger(JPrint.class);
		String queuename = new String();
		PrintService printService = getDefaultPrinterService();

		if (printService != null)
		{
			queuename = getPrinterNamebyService(printService);
		}
		logger.debug("getDefaultPrinterQueueName :" + queuename);
		return queuename;
	}


	public static String getPrinterShortName(String queuename) {
		String result = queuename;
		String prefix1 = "Win32 Printer : ";
		result = queuename.replace(prefix1, "");
		return result;
	}


	public static PrintService getDefaultPrinterService() {
		PrintServiceLookup.lookupDefaultPrintService().getName();
		PrinterJob printJob = PrinterJob.getPrinterJob();
		PrintService printService = printJob.getPrintService();

		return printService;
	}


	public static int getNumberofPrinters() {
		return printerCount;
	}


	public static String getPreferredPrinterQueueName() {
		if (preferredPrinterQueueName == null)
		{
			preferredPrinterQueueName = "";
		}

		if (preferredPrinterQueueName.equals(""))
		{
			preferredPrinterQueueName = getDefaultPrinterQueueName();
			preferredPrinterService = getDefaultPrinterService();
		}

		return preferredPrinterQueueName;
	}


	public static PrintService getPreferredPrinterService() {
		return preferredPrinterService;
	}


	public static String getPrinterNamebyService(PrintService printService) {
		String queuename = new String();
		if (printerCount > 0)
		{
			queuename = printService.getName();

		}
		else
		{
			queuename = "";
		}
		return queuename;
	}


	public static LinkedList<String> getPrinterNames() {
		return printernames;
	}
	

	public static PrintService getPrinterServicebyName(String queuename) {
		PrintService printservice = null;

		if (printerCount > 0)
		{
			int idx = getPrintQueueIndex(queuename);

			if (idx >= 0)
			{
				printservice = printerservices.get(idx);
			}
		}

		return printservice;
	}


	public static LinkedList<PrintService> getPrinterServices() {
		return printerservices;
	}


	public static int getPrintQueueIndex(String queuename) {
		int result = -1;

		if (printerCount > 0)
		{
			result = printernames.indexOf(queuename);
		}

		return result;
	}

	public static void init() {
		refresh();
	}


	public static boolean isValidPrintQueueName(String queuename) {
		boolean result = false;

		if (printerCount > 0)
		{
			if (getPrintQueueIndex(queuename) >= 0)
			{
				result = true;
			}
		}

		return result;
	}


	public static boolean isValidPrintService(PrintService printService) {
		boolean result = false;

		if (printerCount > 0)
		{
			int idx = printerservices.indexOf(printService);

			if (idx >= 0)
			{
				result = true;
			}
		}
		return result;
	}

	public static void refresh() {
		final Logger logger = Logger.getLogger(JPrint.class);
		printers = PrinterJob.lookupPrintServices();
		printerCount = printers.length;

		printernames.clear();

		printerservices.clear();

		if (printerCount > 0)
		{
			for (int i = printerCount-1; i >=0; i--)
			//for (int i = 0; i < printerCount; i++)
			{
				if (printers[i].getName().contains("Microsoft XPS Document Writer")==false)
				{
					printers[i].getName();
					printernames.addLast(printers[i].getName());
					logger.debug("refresh : " + printers[i].getName());
					printerservices.addLast(printers[i]);
				}
				else
				{
					printerCount--;
				}
			}
		}
	}


	public static void setPreferredPrinterQueueName(String queuename) {
		if (isValidPrintQueueName(queuename) == true)
		{
			preferredPrinterQueueName = queuename;
			preferredPrinterService = getPrinterServicebyName(queuename);
			setOSDefaultPrinter();
			for (int x = 0; x < 10; x++)
			{
				if (JPrint.getDefaultPrinterQueueName().equals(queuename))
				{
					break;
				}
				else
				{
					JWait.oneSec();
				}
			}
		}
	}


	public static void setPreferredPrinterService(PrintService printService) {
		if (isValidPrintService(printService) == true)
		{
			preferredPrinterService = printService;
			preferredPrinterQueueName = getPrinterNamebyService(printService);
		}
	}

	public static void setOSDefaultPrinter() {
		String param = new String();

		if (OSValidator.isWindows())
		{
			try
			{
				param = preferredPrinterQueueName;

				// Process ls_proc =
				Runtime.getRuntime().exec("RUNDLL32 PRINTUI.DLL,PrintUIEntry /y /n \"" + param + "\"");
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Common.mainForm, e, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (OSValidator.isMac())
		{
			try
			{
				param = JCUPSPrinterAttributes.getName(preferredPrinterQueueName);

				// Process ls_proc =
				Runtime.getRuntime().exec("lpoptions -d " + param);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Common.mainForm, e, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}	
		
		if (OSValidator.isUnix())
		{
			try
			{
				param = JCUPSPrinterAttributes.getName(preferredPrinterQueueName);

				// Process ls_proc =
				Runtime.getRuntime().exec("lpoptions -d " + param);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(Common.mainForm, e, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		refresh();

	}

	public JPrint()
	{
		init();
	}
}
