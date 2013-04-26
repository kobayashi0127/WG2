package com.commander4j.thread;

import java.io.File;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.commander4j.sys.Common;
import com.commander4j.util.JFileIO;

public class InboundMessageCollectionThread extends Thread
{
	public boolean allDone = false;
	private String outputFile;
	private String renamedOutputFile;
	private String inputFile;
	private String renamedFile;
	private LinkedList<String> inputPathList = new LinkedList<String>();
	private String inputPath;
	private int inputPathCount = 0;
	private final Logger logger = Logger.getLogger(InboundMessageCollectionThread.class);
	private LinkedList<String> filenames = new LinkedList<String>();
	private String[] chld;
	private String fileName;
	private JFileIO mover = new JFileIO();
	public static boolean recoveringFiles = false;

	public InboundMessageCollectionThread(LinkedList<String> fromPathList, String toPath)
	{
		super();
		inputPathList.clear();
		inputPathList = fromPathList;
		inputPathCount = inputPathList.size();
	}

	private LinkedList<String> getInputFilename(String inputPath)
	{
		LinkedList<String> Result = new LinkedList<String>();
		File dir;

		dir = new File(inputPath);
		chld = dir.list();
		if (chld == null)
		{
			logger.debug("Specified directory does not exist or is not a directory. [" + inputPath + "]");
		}
		else
		{
			for (int i = 0; i < chld.length; i++)
			{
				fileName = chld[i];
				if (fileName.indexOf(".xml") > 0)
				{
					Result.addFirst(fileName);
				}
			}
		}

		return Result;
	}

	public void run()
	{
		while (true)
		{
			try
			{
				sleep(1000);
			}
			catch (InterruptedException e1)
			{
				e1.printStackTrace();
			}

			if (allDone)
			{
				logger.debug("fileCollectorThread closed.");
				return;
			}

			for (int x = 0; x < inputPathCount; x++)
			{

				inputPath = inputPathList.get(x) + java.io.File.separator;
				filenames = getInputFilename(inputPathList.get(x));

				if (filenames.size() > 0)
				{

					recoveringFiles = true;
					for (int i = filenames.size() - 1; i >= 0; i--)
					{
						inputFile = inputPath + filenames.get(i);
						renamedFile = inputPath + filenames.get(i).replaceAll(".xml", ".lmx");

						renamedOutputFile = Common.base_dir + java.io.File.separator + "xml" + java.io.File.separator + "interface" + java.io.File.separator + "recovery" + java.io.File.separator + filenames.get(i).replaceAll(".xml", ".lmx");
						outputFile = Common.base_dir + java.io.File.separator + "xml" + java.io.File.separator + "interface" + java.io.File.separator + "recovery" + java.io.File.separator + filenames.get(i);

						//If file already exists delete it.
						
						mover.move_File(inputFile, renamedFile);
						mover.move_File(renamedFile, renamedOutputFile);
						mover.move_File(renamedOutputFile, outputFile);

						if (allDone)
						{
							logger.debug("fileCollectorThread closed.");
							return;
						}
					}
					recoveringFiles = false;
				}
			}
		}
	}
}
