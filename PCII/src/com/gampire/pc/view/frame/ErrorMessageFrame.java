package com.gampire.pc.view.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ErrorMessageFrame extends PCFrame {
	JTextArea textArea = new JTextArea();

	public ErrorMessageFrame(String text) {

		// Add a scrolling text area
		textArea.setEditable(false);
		textArea.setFont(new Font("Arial", Font.PLAIN, 14));
		textArea.setColumns(50);
		textArea.setRows(10);
		getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);

		pack();
		setVisible(true);
		textArea.setText(text);

		center();
	}

	private void center() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - getWidth()) / 2;
		int y = (screenSize.height - getHeight()) / 2;
		setBounds(x, y, getWidth(), getHeight());
	}
}
