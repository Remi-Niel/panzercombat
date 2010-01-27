/**
 * 
 */
package com.gampire.pc.action;

import com.gampire.pc.control.BattleFieldController;
import com.gampire.pc.model.Unit;
import com.gampire.pc.view.panel.BattleFieldControlPanel;

public class MoveAction extends PlayGameAction {
    
    public MoveAction(BattleFieldController battleFieldController, BattleFieldControlPanel battleFieldControlPanel) {
        super("MovingThread",battleFieldController, battleFieldControlPanel);
    }

    @Override
	protected void perform(Unit unitSelectedForAction) {
        battleFieldController.move(unitSelectedForAction, false);
    }
}