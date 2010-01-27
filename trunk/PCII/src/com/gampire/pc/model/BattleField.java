package com.gampire.pc.model;

import com.gampire.pc.control.BattleFieldController;
import com.gampire.pc.view.table.UnitTableModel;

public class BattleField {

	private Camp camp1;

	private Camp camp2;

	private final UnitTableModel unitTableModel1;

	private final UnitTableModel unitTableModel2;

	private Unit unitSelectedForAction = null;

	private BattleFieldController battleFieldController;

	public BattleField(int maxPoints) {
		camp1 = new Camp(this, Alliance.getAll()[0], maxPoints, true);
		camp2 = new Camp(this, Alliance.getAll()[1], maxPoints, false);

		unitTableModel1 = new UnitTableModel(camp1);
		unitTableModel2 = new UnitTableModel(camp2);
	}

	public BattleField(BattleField battlefield) {
		camp1 = new Camp(battlefield.camp1, this);
		camp2 = new Camp(battlefield.camp2, this);

		unitTableModel1 = new UnitTableModel(camp1);
		unitTableModel2 = new UnitTableModel(camp2);
	}

	public Camp getCamp1() {
		return camp1;
	}

	public Camp getCamp2() {
		return camp2;
	}

	public int getNumberOfUnits() {
		return camp1.getUnits().size() + camp2.getUnits().size();
	}

	public void selectUnitForAction() {
		int numUnits1 = camp1.getUnits().size();
		int numUnits2 = camp2.getUnits().size();
		int numUnits = numUnits1 + numUnits2;

		int indexOfUnitSelectedForAction = (int) Math.floor(Math.random()
				* numUnits);

		Camp campSelectedForAction;
		if (indexOfUnitSelectedForAction < numUnits1) {
			campSelectedForAction = camp1;
		} else {
			campSelectedForAction = camp2;
			indexOfUnitSelectedForAction -= numUnits1;
		}
		unitSelectedForAction = campSelectedForAction.getUnits().get(
				indexOfUnitSelectedForAction);
		unitSelectedForAction.setSelectedForAction(true);
	}

	public void deselectUnitSelectedForAction() {
		if (unitSelectedForAction != null) {
			unitSelectedForAction.setSelectedForAction(false);
		}
		unitSelectedForAction = null;
	}

	public Camp removeDestroyedUnits() {
		int numRemoved = unitTableModel1.removeDestroyedUnits();
		if (numRemoved == 0) {
			numRemoved = unitTableModel2.removeDestroyedUnits();
			if (numRemoved != 0) {
				return camp2;
			} else {
				return null;
			}
		} else {
			return camp1;
		}
	}

	public Unit getUnitSelectedForAction() {
		return unitSelectedForAction;
	}

	public BattleFieldController getBattleFieldController() {
		return battleFieldController;
	}

	public void setBattleFieldController(
			BattleFieldController battleFieldController) {
		this.battleFieldController = battleFieldController;
	}

	public UnitTableModel getUnitTableModel1() {
		return unitTableModel1;
	}

	public UnitTableModel getUnitTableModel2() {
		return unitTableModel2;
	}

	public boolean advanceAnimations() {
		boolean somethingChanged1 = camp1.advanceAnimations();
		boolean somethingChanged2 = camp2.advanceAnimations();
		return somethingChanged1 || somethingChanged2;
	}
}
