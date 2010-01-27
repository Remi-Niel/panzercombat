package com.gampire.pc.action;

import com.gampire.pc.control.BattleFieldController;
import com.gampire.pc.model.Unit;
import com.gampire.pc.view.panel.BattleFieldControlPanel;

public class MoveAndFireAction extends PlayGameAction {

	public MoveAndFireAction(BattleFieldController battleFieldController,
			BattleFieldControlPanel battleFieldControlPanel) {
		super("MovingAndFiringThread",battleFieldController, battleFieldControlPanel);
	}

	@Override
	protected void perform(Unit unitSelectedForAction) {
		Unit unitSelectedAsTarget = battleFieldController
				.getUnitSelectedAsTarget();
		if (unitSelectedAsTarget != null) {
			battleFieldController.move(unitSelectedForAction, true);
			battleFieldController.waitAWhile();
			battleFieldController.fire(unitSelectedForAction,
					unitSelectedAsTarget, true);
		}
	}
}