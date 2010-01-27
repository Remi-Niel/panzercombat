package com.gampire.pc.view.frame;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JScrollPane;

import com.gampire.pc.model.BattleField;
import com.gampire.pc.view.panel.BattleFieldPanel;

public class BattleFieldFrame extends PCFrame {

	private BattleFieldPanel panel;

	public BattleFieldFrame(BattleField battleField) {

		setAndConfigurePanel(new BattleFieldPanel(battleField));

		pack();

		// add 30 to the width after packing to avoid vertical scrollbars
		// appearing when adding units from 3 or less to more induce a
		// horizontal scrollbar
		setSize(getWidth() + 30, getHeight());

		center();

		setVisible(true);
	}

	public BattleFieldPanel getPanel() {
		return panel;
	}

	public void setAndConfigurePanel(BattleFieldPanel panel) {
		this.panel = panel;

		// Create and set up the content pane.
		panel.setOpaque(true);

		JScrollPane scrollPane = new JScrollPane(panel);
		setContentPane(scrollPane);

		// validate the frame
		validate();
	}

	private void center() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - getWidth()) / 2;
		int y = (screenSize.height - getHeight()) / 2;
		setBounds(x, y, getWidth(), getHeight());
	}

}
