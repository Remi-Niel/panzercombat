package com.gampire.pc.action;

import com.gampire.pc.control.BattleFieldController;
import com.gampire.pc.model.Unit;
import com.gampire.pc.view.panel.BattleFieldControlPanel;

public class DefendAction extends PlayGameAction {
    public DefendAction(BattleFieldController battleFieldController, BattleFieldControlPanel battleFieldControlPanel) {
    	super("DefendingThread",battleFieldController, battleFieldControlPanel);
    }

    @Override
	protected void perform(Unit unitSelectedForAction) {
        battleFieldController.defend(unitSelectedForAction);
    }
}