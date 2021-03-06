package com.commander4j.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.commander4j.sys.Common;

public class StringRenderer implements TableCellRenderer
{
	private TableCellRenderer delegate;

	public StringRenderer(TableCellRenderer defaultRenderer)
	{
		this.delegate = defaultRenderer;

	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Component c = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		String s = (String) value;
		if (c instanceof JLabel)
		{
			((JLabel) c).setText(s);
			// /((JLabel) c).setHorizontalAlignment(Alignment);
		}

		Color foreground, background;

			if (isSelected)
			{
				foreground = Common.color_listFontSelected;
				background = Common.color_listHighlighted;
			} else
			{
				if (row % 2 == 0)
				{
					foreground = Common.color_listFontStandard;
					background = Common.color_tablerow1;
				} else
				{
					foreground = Common.color_listFontStandard;
					background = Common.color_tablerow2;
				}
			}

			((JLabel) c).setHorizontalAlignment(JLabel.LEFT);


		c.setForeground(foreground);
		c.setBackground(background);

		return c;
	}
}