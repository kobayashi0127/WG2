package com.commander4j.gui;

import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;

import com.commander4j.sys.Common;

public class JList4j extends JList
{

	private static final long serialVersionUID = 1L;

	public JList4j()
	{
		super();
		setFont(Common.font_list);
	}
	
	public JList4j(Object[] param1)
	{
		super(param1);
		setFont(Common.font_list);
	}

	public JList4j(@SuppressWarnings("rawtypes") Vector[] param1)
	{
		super(param1);
		setFont(Common.font_list);
	}
	
	public JList4j(ListModel param1)
	{
		super(param1);
		setFont(Common.font_list);
	}
		
}
