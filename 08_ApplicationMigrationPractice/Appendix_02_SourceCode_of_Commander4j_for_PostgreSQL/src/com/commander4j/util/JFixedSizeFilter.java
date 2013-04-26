package com.commander4j.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 */
public class JFixedSizeFilter extends DocumentFilter
{
	private int maxSize;

	// limit is the maximum number of characters allowed.
	/**
	 * Constructor for JFixedSizeFilter.
	 * 
	 * @param limit
	 *            int
	 */
	public JFixedSizeFilter(int limit)
	{
		maxSize = limit;
	}

	// This method is called when characters are inserted into the document
	/**
	 * Method insertString.
	 * 
	 * @param fb
	 *            DocumentFilter.FilterBypass
	 * @param offset
	 *            int
	 * @param str
	 *            String
	 * @param attr
	 *            AttributeSet
	 * @throws BadLocationException
	 */
	public void insertString(DocumentFilter.FilterBypass fb, int offset, String str, AttributeSet attr) throws BadLocationException {
		replace(fb, offset, 0, str, attr);
	}

	// This method is called when characters in the document are replace with
	// other characters
	/**
	 * Method replace.
	 * 
	 * @param fb
	 *            DocumentFilter.FilterBypass
	 * @param offset
	 *            int
	 * @param length
	 *            int
	 * @param str
	 *            String
	 * @param attrs
	 *            AttributeSet
	 * @throws BadLocationException
	 */
	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String str, AttributeSet attrs) throws BadLocationException {
		int newLength = fb.getDocument().getLength() - length + str.length();
		if (newLength <= maxSize)
		{
			fb.replace(offset, length, str, attrs);
		}
		else
		{
			throw new BadLocationException("New characters exceeds max size of document", offset);
		}
	}
}
