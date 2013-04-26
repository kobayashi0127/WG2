package com.commander4j.gui;

import javax.swing.JTextField;
import javax.swing.text.Document;

import com.commander4j.sys.Common;

public class JTextField4j extends JTextField {

	private static final long serialVersionUID = 1L;

	public JTextField4j() {
		setFont(Common.font_input);
		setDisabledTextColor(Common.color_textdisabled);
	}

	public JTextField4j(String text) {
		super(text);
		setFont(Common.font_input);
		setDisabledTextColor(Common.color_textdisabled);
	}

	public JTextField4j(int columns) {
		super(columns);
		setFont(Common.font_input);
		setDisabledTextColor(Common.color_textdisabled);
	}

	public JTextField4j(String text, int columns) {
		super(text, columns);
		setFont(Common.font_input);
		setDisabledTextColor(Common.color_textdisabled);
	}

	public JTextField4j(Document doc, String text, int columns) {
		super(doc, text, columns);
		setFont(Common.font_input);
		setDisabledTextColor(Common.color_textdisabled);
	}

}
