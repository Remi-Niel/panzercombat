package com.gampire.pc.view.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicBorders;

import com.gampire.pc.action.DefendAction;
import com.gampire.pc.action.FireAction;
import com.gampire.pc.action.MoveAction;
import com.gampire.pc.action.MoveAndFireAction;
import com.gampire.pc.action.StartGameAction;
import com.gampire.pc.action.StopGameAction;
import com.gampire.pc.model.BattleField;
import com.gampire.pc.model.Unit;
import com.gampire.pc.model.UnitAction;
import com.gampire.pc.swing.lookandfeel.PCIITheme;
import com.gampire.pc.util.image.ImageUtil;
import com.gampire.pc.web.BareBonesBrowserLaunch;

public class BattleFieldControlPanel extends JPanel implements
		TableModelListener {
	// buttons
	protected final JButton startStopButton = new JButton();

	private final JButton moveButton = new JButton();

	private final JButton fireButton = new JButton();

	private final JButton moveAndFireButton = new JButton();

	private final JButton defendButton = new JButton();

	private final JButton helpButton = new JButton();

	// button tooltips
	static private final String TOOLTIP_START = "start game";

	static private final String TOOLTIP_STOP = "stop game";

	static private final String TOOLTIP_MOVE = UnitAction.MOVE.toString()
			+ " (click on unit)";

	static private final String TOOLTIP_FIRE = UnitAction.FIRE.toString()
			+ " (double-click on target)";

	static private final String TOOLTIP_FIRE_NO_TARGET = UnitAction.FIRE
			.toString()
			+ " (select target first)";

	static private final String TOOLTIP_MOVE_AND_FIRE = UnitAction.MOVE_AND_FIRE
			.toString()
			+ " (right-click on target)";

	static private final String TOOLTIP_MOVE_AND_FIRE_NO_TARGET = UnitAction.MOVE_AND_FIRE
			.toString()
			+ " (select target first)";

	static private final String TOOLTIP_DEFEND = UnitAction.DEFEND.toString()
			+ " (right-click on unit)";

	static private final String TOOLTIP_HELP = "about Panzer Combat II";

	// icons
	private final static int ICON_SIZE = 80;

	private final static int INSET_HORIZONTAL_SMALL = 40;

	private final static int INSET_HORIZONTAL_BIG = 80;

	protected final BattleField battleField;

	private boolean playGameButtonsEnabled = false;

	public BattleFieldControlPanel(BattleField field) {
		super();

		// model
		battleField = field;

		// when the models are updated, buttons should be as well
		battleField.getUnitTableModel1().addTableModelListener(this);
		battleField.getUnitTableModel2().addTableModelListener(this);

		// layout
		GridBagConstraints c = new GridBagConstraints();
		setLayout(new GridBagLayout());

		// not opaque
		setOpaque(false);

		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(10, INSET_HORIZONTAL_SMALL, 10,
				INSET_HORIZONTAL_BIG);
		add(startStopButton, c);
		c.gridx++;
		c.insets = new Insets(10, 0, 10, INSET_HORIZONTAL_SMALL);
		add(moveButton, c);
		c.gridx++;
		add(fireButton, c);
		c.gridx++;
		add(moveAndFireButton, c);
		c.gridx++;
		add(defendButton, c);
		c.gridx++;
		c.insets = new Insets(10, INSET_HORIZONTAL_BIG, 10,
				INSET_HORIZONTAL_SMALL);
		add(helpButton, c);

		// format buttons
		formatButton(startStopButton, "start.jpg", TOOLTIP_START);
		formatButton(moveButton, "move.jpg", null);
		formatButton(fireButton, "fire.jpg", null);
		formatButton(moveAndFireButton, "moveAndFire.jpg", null);
		formatButton(defendButton, "defend.jpg", null);
		formatButton(helpButton, "help.jpg", TOOLTIP_HELP);

		// set initial status of play game buttons to disable
		updateButtons();

		// add action listener to buttons
		startStopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				boolean gameIsRunning = battleField.getBattleFieldController()
						.isGameRunning();

				if (!gameIsRunning) {
					formatButton(startStopButton, "stop.jpg", TOOLTIP_STOP);
					new StartGameAction(battleField.getBattleFieldController(),
							BattleFieldControlPanel.this).start();
				} else {
					formatButton(startStopButton, "start.jpg", TOOLTIP_START);
					new StopGameAction(battleField.getBattleFieldController(),
							BattleFieldControlPanel.this).start();
				}
			}
		});

		moveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// do not run on the AWT thread
				new MoveAction(battleField.getBattleFieldController(),
						BattleFieldControlPanel.this).start();
			}
		});

		fireButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// do not run on the AWT thread
				new FireAction(battleField.getBattleFieldController(),
						BattleFieldControlPanel.this).start();
			}
		});

		moveAndFireButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// do not run on the AWT thread
				new MoveAndFireAction(battleField.getBattleFieldController(),
						BattleFieldControlPanel.this).start();
			}
		});

		defendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// do not run on the AWT thread
				new DefendAction(battleField.getBattleFieldController(),
						BattleFieldControlPanel.this).start();
			}
		});

		helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				BareBonesBrowserLaunch
						.openURL("http://panzercombat.sourceforge.net");
			}

		});

	}

	public void formatButton(JButton button, String fileName,
			String toolTipText) {
		button.setIcon(new ImageIcon(ImageUtil.getScaledImage(fileName, ICON_SIZE)));
		button.setToolTipText(toolTipText);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setBackground(Color.BLACK);
		button.setForeground(PCIITheme.LIGHT_GRAY);
		button.setBorder(BasicBorders.getButtonBorder());
	}

	// when a unit is added or removed, the buttons should be updated,
	// as no selection change event occurs when one unit is added or removed,
	// but the target is known when there is only one unit
	public void tableChanged(TableModelEvent e) {
		updateButtons();
	}

	public void updateButtons(boolean enablePlayGameButtons) {
		this.playGameButtonsEnabled = enablePlayGameButtons;
		updateButtons();
	}

	public void updateButtons() {
		if (SwingUtilities.isEventDispatchThread()) {
			if (!playGameButtonsEnabled) {
				moveButton.setEnabled(false);
				fireButton.setEnabled(false);
				moveAndFireButton.setEnabled(false);
				defendButton.setEnabled(false);
				moveButton.setToolTipText(null);
				fireButton.setToolTipText(null);
				moveAndFireButton.setToolTipText(null);
				defendButton.setToolTipText(null);
			} else {
				moveButton.setEnabled(true);
				defendButton.setEnabled(true);
				moveButton.setToolTipText(TOOLTIP_MOVE);
				defendButton.setToolTipText(TOOLTIP_DEFEND);

				// get unit selected as target
				Unit unitSelectedAsTarget = battleField
						.getBattleFieldController().getUnitSelectedAsTarget();
				if (unitSelectedAsTarget == null) {
					fireButton.setEnabled(false);
					moveAndFireButton.setEnabled(false);
					fireButton.setToolTipText(TOOLTIP_FIRE_NO_TARGET);
					moveAndFireButton
							.setToolTipText(TOOLTIP_MOVE_AND_FIRE_NO_TARGET);
				} else {
					fireButton.setEnabled(true);
					moveAndFireButton.setEnabled(true);
					fireButton.setToolTipText(TOOLTIP_FIRE);
					moveAndFireButton.setToolTipText(TOOLTIP_MOVE_AND_FIRE);
				}
			}
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					updateButtons();
				}
			});
		}
	}

	public JButton getDefendButton() {
		return defendButton;
	}

	public JButton getFireButton() {
		return fireButton;
	}

	public JButton getMoveAndFireButton() {
		return moveAndFireButton;
	}

	public JButton getMoveButton() {
		return moveButton;
	}
}