package com.commander4j.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;

import com.commander4j.db.JDBListData;
import com.commander4j.gui.JList4j;
import com.commander4j.sys.Common;

/**
 */
public class JDBListRenderer extends JLabel implements ListCellRenderer
{
	/**
	 * Field serialVersionUID. (value is 1) Value: {@value serialVersionUID}
	 */
	private static final long serialVersionUID = 1;
	/**
	 * @uml.property name="defaultRenderer"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	/**
	 * @uml.property name="background"
	 */
	private Color background = Common.color_listBackground;
	/**
	 * @uml.property name="separator"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JSeparator separator;

	public JDBListRenderer()
	{
		separator = new JSeparator(JSeparator.HORIZONTAL);
		setFont(Common.font_list);
	}

	/**
	 * Constructor for JDBListRenderer.
	 * 
	 * @param newBackgroundColor
	 *            Color
	 */
	public JDBListRenderer(Color newBackgroundColor)
	{
		separator = new JSeparator(JSeparator.HORIZONTAL);
		background = newBackgroundColor;
	}

	/**
	 * Method getListCellRendererComponent.
	 * 
	 * @param list
	 *            JList
	 * @param value
	 *            Object
	 * @param index
	 *            int
	 * @param isSelected
	 *            boolean
	 * @param cellHasFocus
	 *            boolean
	 * @return Component
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(JList,
	 *      Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(JList4j list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		Icon theIcon = null;
		String theText = null;

		String str = (value == null) ? "" : value.toString();
		if (str.equals("SEPARATOR"))
		{
			return separator;
		}

		JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		renderer.setFont(Common.font_list);

		if (value instanceof JDBListData)
		{
			JDBListData ldata = (JDBListData) value;
			theIcon = ldata.getIcon();
			theText = ldata.getmData().toString();
		} else
		{
			try
			{
				theText = value.toString();
			} catch (Exception ex)
			{
				theText = "";
			}
		}

		if (!isSelected)
		{
			renderer.setBackground(background);
			renderer.setForeground(Common.color_listFontSelected);
		} else
		{
			renderer.setBackground(Common.color_listHighlighted);
			renderer.setForeground(Common.color_listFontStandard);
		}

		if (theIcon != null)
		{
			renderer.setIcon(theIcon);
		}

		renderer.setText(theText);

		return renderer;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		{
			Icon theIcon = null;
			String theText = null;

			String str = (value == null) ? "" : value.toString();
			if (str.equals("SEPARATOR"))
			{
				return separator;
			}

			JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			renderer.setFont(Common.font_list);

			if (value instanceof JDBListData)
			{
				JDBListData ldata = (JDBListData) value;
				theIcon = ldata.getIcon();
				theText = ldata.getmData().toString();
			} else
			{
				try
				{
					theText = value.toString();
				} catch (Exception ex)
				{
					theText = "";
				}
			}

			if (!isSelected)
			{
				renderer.setBackground(background);
				renderer.setForeground(Common.color_listFontSelected);
			} else
			{
				renderer.setBackground(Common.color_listHighlighted);
				renderer.setForeground(Common.color_listFontStandard);
			}

			if (theIcon != null)
			{
				renderer.setIcon(theIcon);
			}

			renderer.setText(theText);

			return renderer;
		}
	}
}
