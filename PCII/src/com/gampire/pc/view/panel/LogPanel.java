package com.gampire.pc.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.gampire.pc.speech.Speaker;
import com.gampire.pc.swing.lookandfeel.PCIITheme;

public class LogPanel extends JPanel {

	private final JTextPane textPane = new JTextPane();

	private final StyledDocument document = (StyledDocument) textPane
			.getDocument();

	protected final Style informStyle = document.addStyle("Inform", null);

	protected final Style alertStyle = document.addStyle("Alert", null);

	public LogPanel() {
		super(new BorderLayout());

		setPreferredSize(new Dimension(800, 80));
		textPane.setEditable(false);
		textPane.setBackground(PCIITheme.GRAY);

		JScrollPane scrollPane = new JScrollPane(textPane);

		add(scrollPane, BorderLayout.CENTER);

		StyleConstants.setFontFamily(informStyle, "Helvetica");
		StyleConstants.setFontFamily(alertStyle, "Helvetica");

		StyleConstants.setForeground(informStyle, PCIITheme.LIGHT_GRAY);
		StyleConstants.setFontSize(informStyle, 18);
		StyleConstants.setForeground(alertStyle, Color.RED);
		StyleConstants.setBold(alertStyle, true);
		StyleConstants.setFontSize(alertStyle, 18);

	}

	protected void insertString(String s, Style style) {
		try {
			document.insertString(document.getLength(), s, style);
		} catch (BadLocationException e) {
			// nothing to do
		}
	}

	public void inform(String message) {
		// run on the AWT thread (otherwise the scrollbar is not correctly
		// positionned)
		try {
			SwingUtilities.invokeAndWait(new InformRunnable(message));
		} catch (InterruptedException e) {
			// nothing to do
		} catch (InvocationTargetException e) {
			// nothing to do
		}

		waitAWhile();

		Speaker.getInstance().speakAndWait(message);
	}

	private void waitAWhile() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// nothing to do
		}
	}

	private class InformRunnable implements Runnable {
		String message;

		InformRunnable(String message) {
			this.message = message;
		}

		public void run() {
			if (message.contains("destroyed")) {
				insertString(message, alertStyle);
			} else {
				insertString(message, informStyle);
			}
		}
	}

}
