package com.gampire.pc.action;

import com.gampire.pc.control.BattleFieldController;
import com.gampire.pc.model.Unit;
import com.gampire.pc.view.panel.BattleFieldControlPanel;

public class FireAction extends PlayGameAction {

	public FireAction(BattleFieldController battleFieldController,
			BattleFieldControlPanel battleFieldControlPanel) {
		super("FiringThread",battleFieldController, battleFieldControlPanel);
	}

	@Override
	protected void perform(Unit unitSelectedForAction) {
		Unit unitSelectedAsTarget = battleFieldController
				.getUnitSelectedAsTarget();
		if (unitSelectedAsTarget != null) {
			battleFieldController.fire(unitSelectedForAction,
					unitSelectedAsTarget, false);
		}
	}
}