package com.commander4j.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 */
public class JFileFilterTXT extends FileFilter
{
	/**
	 * Method accept.
	 * 
	 * @param f
	 *            File
	 * @return boolean
	 */
	public boolean accept(File f) {
		if (f.isDirectory())
		{
			return true;
		}

		String extension = JFileFilterXLSTypes.getExtension(f);
		if (extension != null)
		{
			if (extension.equals(JFileFilterTXTTypes.TXT))
			{
				return true;
			}
			else
			{
				return false;
			}
		}

		return false;
	}

	// The description of this filter
	/**
	 * Method getDescription.
	 * 
	 * @return String
	 */
	public String getDescription() {
		return "Text File";
	}

}
