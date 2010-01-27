package com.gampire.pc.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.gampire.pc.license.LicenseManager;
import com.gampire.pc.model.BattleField;
import com.gampire.pc.model.Camp;
import com.gampire.pc.model.DistanceInfo;
import com.gampire.pc.model.Unit;
import com.gampire.pc.model.UnitType;
import com.gampire.pc.speech.Speaker;
import com.gampire.pc.util.thread.ThreadUtil;
import com.gampire.pc.view.dialog.DistanceDialog;
import com.gampire.pc.view.frame.BattleFieldFrame;
import com.gampire.pc.view.panel.BattleFieldPanel;

public class BattleFieldController implements ActionListener {

	private final static String SELECT_UNITS = "Please select your units and set them on the gaming terrain. Press start when finished.";

	private final static String RESTORING_BATTLEFIELD = "Restoring battlefield to status at game start.";

	private final static String START_GAME = "Prepare for battle.";

	private final static String STOP_GAME = "The game has stoppped.";

	// control
	private boolean gameRunning = false;

	// model
	protected BattleField battleField;

	private BattleField cloneOfBattleFieldAtPreviousGameStart;

	// view
	protected BattleFieldFrame battleFieldFrame;

	protected BattleFieldPanel battleFieldPanel;

	// animation
	private Timer animationTimer;

	public BattleFieldController(BattleField battleField,
			BattleFieldFrame battleFieldFrame) {
		this.battleField = battleField;
		battleField.setBattleFieldController(this);
		this.battleFieldFrame = battleFieldFrame;
		battleFieldPanel = battleFieldFrame.getPanel();

		// check license file
		if (!LicenseManager.LICENSE_VALID) {
			LicenseManager.showLimitedFunctionalityMessage(battleFieldPanel);
		}

		Speaker.getInstance().speakAndWait(SELECT_UNITS);

		// animation timer
		animationTimer = new Timer(100, this);
		animationTimer.start();
	}

	// game sequence actions : startGame, stopGame, isGameRunning, nextTurn,
	// waitAWhile,

	public void startGame() {
		// voice
		Speaker.getInstance().speakAndWait(START_GAME);

		cloneOfBattleFieldAtPreviousGameStart = new BattleField(battleField);
		gameRunning = true;
		// do UI related stuff on the AWT thread
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					battleFieldPanel.setEditable(false);
				}
			});
		} catch (InterruptedException e) {
			// nothing to do
		} catch (InvocationTargetException e) {
			// nothing to do
		}
		nextTurn();
	}

	public void stopGame() {
		gameRunning = false;
		battleField.deselectUnitSelectedForAction();

		// do UI related stuff on the AWT thread
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					battleFieldPanel.repaint();
				}
			});
		} catch (InterruptedException e) {
			// nothing to do
		} catch (InvocationTargetException e) {
			// nothing to do
		}
		Speaker.getInstance().speakAndWait(STOP_GAME);
		Speaker.getInstance().speakAndWait(RESTORING_BATTLEFIELD);

		battleField = cloneOfBattleFieldAtPreviousGameStart;
		battleField.setBattleFieldController(this);
		battleFieldPanel = new BattleFieldPanel(battleField);

		// do UI related stuff on the AWT thread
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					battleFieldFrame.setAndConfigurePanel(battleFieldPanel);
				}
			});
		} catch (InterruptedException e) {
			// nothing to do
		} catch (InvocationTargetException e) {
			// nothing to do
		}
		Speaker.getInstance().speakAndWait(SELECT_UNITS);
	}

	public boolean isGameRunning() {
		return gameRunning;
	}

	public void nextTurn() {

		// do UI related stuff on the AWT thread
		DestroyedUnitsRemover remover = new DestroyedUnitsRemover();
		try {
			SwingUtilities.invokeAndWait(remover);
		} catch (InterruptedException e) {
			// nothing to do
		} catch (InvocationTargetException e) {
			// nothing to do
		}

		Camp campLosingUnits = remover.getCampLosingUnits();
		if (campLosingUnits != null && campLosingUnits.getUnits().size() == 0) {

			Speaker.getInstance().speakAndWait(campLosingUnits.getAlliance().getName()
					+ " camp has lost all its units.");
			Speaker.getInstance().speakAndWait("Victory for "
					+ campLosingUnits.getOther().getAlliance().getName() + ".");
			stopGame();
			return;
		}

		// deselect unit for action
		battleField.deselectUnitSelectedForAction();

		// select unit for action
		battleField.selectUnitForAction();

		// do UI related stuff on the AWT thread
		try {
			SwingUtilities.invokeAndWait(new TargetSelectionnerAndScroller());
		} catch (InterruptedException e) {
			// nothing to do
		} catch (InvocationTargetException e) {
			// nothing to do
		}

		Speaker.getInstance().speak(battleField.getUnitSelectedForAction().getName()
				+ ", it's your turn!");
	}

	public void waitAWhile() {
		ThreadUtil.waitAWhile(1000);
	}

	// game control actions : move, fire, defend

	public void move(Unit unitSelectedForAction, boolean half) {
		// this will play sound and blur the image
		unitSelectedForAction.startMoving();

		// show the image change
		// do not fireTableDataChanged on both table models as this would
		// remove the target selection when move and fire
		// anyway, nothing changes in the target table when moving
		battleFieldPanel.fireTableDataChanged(unitSelectedForAction.getCamp());

		String result = unitSelectedForAction.move(half);

		battleFieldPanel.inform(result);
	}

	public void fire(Unit unitSelectedForAction, Unit unitSelectedAsTarget,
			boolean movePenalty) {
		// scroll to both units on the AWT thread
		try {
			SwingUtilities.invokeAndWait(new CombatScroller(
					unitSelectedForAction, unitSelectedAsTarget));
		} catch (InterruptedException e) {
			// nothing to do
		} catch (InvocationTargetException e) {
			// nothing to do
		}

		// get distance
		int distance = getDistance(unitSelectedForAction, unitSelectedAsTarget);

		// if distance is known or has been entered
		if (distance >= 0) {

			String result = unitSelectedForAction.fire(unitSelectedAsTarget,
					distance, movePenalty);
			if (unitSelectedAsTarget.isDestroyed()) {
				waitAWhile();
				unitSelectedAsTarget.explode();
				// this removes the target selection, so don't if
				// unitSelectedAsTarget is not destroyed as the selection is
				// kept if the camp is unchanged
				battleFieldPanel.fireTableDataChanged(unitSelectedAsTarget
						.getCamp());
			}
			// show the image change
			battleFieldPanel.fireTableDataChanged(unitSelectedForAction
					.getCamp());

			battleFieldPanel.inform(result);
		}
	}

	public void defend(Unit unitSelectedForAction) {
		String result = unitSelectedForAction.defend();
		battleFieldPanel.fireTableDataChanged(unitSelectedForAction.getCamp());
		battleFieldPanel.inform(result);
	}

	// auxiliary functions : getUnitSelectedForAction, getUnitSelectedAsTarget,
	// getDistance, checkNoMoreUnitsLeft

	public Unit getUnitSelectedForAction() {
		return battleField.getUnitSelectedForAction();
	}

	public Unit getUnitSelectedAsTarget() {
		Unit unitSelectedForAction = battleField.getUnitSelectedForAction();

		if (unitSelectedForAction == null) {
			return null;
		}
		Unit unitSelectedAsTarget = battleFieldPanel
				.getSelectedUnit(unitSelectedForAction.getCamp().getOther());
		if (unitSelectedAsTarget == null) {
			unitSelectedAsTarget = unitSelectedForAction.getClosestEnemyUnit();
		}
		return unitSelectedAsTarget;
	}

	public int getDistance(Unit unitSelectedForAction, Unit unitSelectedAsTarget) {

		DistanceInfo distanceInfo = unitSelectedForAction
				.getDistanceInfo(unitSelectedAsTarget);

		if (distanceInfo != null && distanceInfo.isCertain()) {
			return distanceInfo.getMostProbableDistance();
		}

		// get distance information from dialog if none available or uncertain
		UnitType firingUnitType = unitSelectedForAction.getUnitType();
		UnitType targetUnitType = unitSelectedAsTarget.getUnitType();

		String message = "Enter distance from "
				+ unitSelectedForAction.getName() + " to "
				+ unitSelectedAsTarget.getName() + " :";
		Speaker.getInstance().speak(message);

		// number of possible slections is the maximum effective range + next +
		// out of loss
		int numPossibleSelections = firingUnitType
				.getMaximumEffectiveRange(targetUnitType) + 2;

		// set initial value to max half chance to hit distance or most
		// probable distance if distance info available
		int initialSelectionIndex = distanceInfo == null ? firingUnitType
				.getMeanEffectiveRange(targetUnitType) : distanceInfo
				.getMostProbableDistance();

		if (initialSelectionIndex > numPossibleSelections - 1) {
			initialSelectionIndex = numPossibleSelections - 1;
		} else if (initialSelectionIndex < 0) {
			initialSelectionIndex = 0;
		}

		DistanceDialog dialog = new DistanceDialog(battleFieldFrame, message,
				numPossibleSelections, initialSelectionIndex);

		return dialog.getSelectedDistance();

	}

	// AWT thread Runnables : DestroyedUnitsRemover,
	// TargetSelectionnerAndScroller,
	// CombatScroller

	protected class DestroyedUnitsRemover implements Runnable {

		private Camp campLosingUnits;

		public Camp getCampLosingUnits() {
			return campLosingUnits;
		}

		public void run() {
			campLosingUnits = battleField.removeDestroyedUnits();
		}
	}

	protected class TargetSelectionnerAndScroller implements Runnable {

		public void run() {
			Unit unitSelectedForAction = battleField.getUnitSelectedForAction();

			// first update selectable state of tables
			Camp campSelectedForAction = unitSelectedForAction.getCamp();
			battleFieldPanel.setSelectable(campSelectedForAction, false);
			battleFieldPanel.setSelectable(campSelectedForAction.getOther(),
					true);

			// then only select
			Unit unitSelectedAsTarget = battleFieldPanel
					.selectMostProbableTarget(unitSelectedForAction);

			// now scroll to unitSelectedForAction and to targetUnit
			battleFieldPanel.scrollToUnit(unitSelectedForAction,
					battleFieldFrame);
			if (unitSelectedAsTarget != null) {
				battleFieldPanel.scrollToUnit(unitSelectedAsTarget, null);
			}

			// important!
			battleFieldPanel.repaint();
		}
	}

	private class CombatScroller implements Runnable {

		private Unit unitSelectedForAction;

		private Unit unitSelectedAsTarget;

		CombatScroller(Unit unitSelectedForAction, Unit unitSelectedAsTarget) {
			this.unitSelectedForAction = unitSelectedForAction;
			this.unitSelectedAsTarget = unitSelectedAsTarget;
		}

		public void run() {
			// scroll to both units
			battleFieldPanel.scrollToUnit(unitSelectedForAction,
					battleFieldFrame);
			battleFieldPanel.scrollToUnit(unitSelectedAsTarget, null);

			// important!
			battleFieldPanel.repaint();
		}
	}

	// Animation
	public void actionPerformed(ActionEvent e) {
		boolean somethingChanged = battleField.advanceAnimations();
		if (somethingChanged) {
			battleFieldPanel.repaint();
		}
	}

}
