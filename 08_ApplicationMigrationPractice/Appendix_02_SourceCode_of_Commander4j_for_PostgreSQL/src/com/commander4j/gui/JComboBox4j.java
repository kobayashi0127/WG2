package com.commander4j.gui;

import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import com.commander4j.sys.Common;

public class JComboBox4j extends JComboBox {

	private static final long serialVersionUID = 1L;

	public JComboBox4j() {
		setFont(Common.font_combo);
	}

	public JComboBox4j(ComboBoxModel arg0) {
		super(arg0);
		setFont(Common.font_combo);
	}

	public JComboBox4j(Object[] arg0) {
		super(arg0);
		setFont(Common.font_combo);
	}

	public JComboBox4j(Vector<?> arg0) {
		super(arg0);
		setFont(Common.font_combo);
	}

}
