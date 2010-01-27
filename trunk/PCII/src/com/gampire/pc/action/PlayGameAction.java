package com.gampire.pc.action;

import com.gampire.pc.control.BattleFieldController;
import com.gampire.pc.model.Unit;
import com.gampire.pc.view.panel.BattleFieldControlPanel;

abstract class PlayGameAction extends Thread {

    protected final BattleFieldController battleFieldController;
    
    protected final BattleFieldControlPanel battleFieldControlPanel;

    PlayGameAction(String threadName, BattleFieldController battleFieldController, BattleFieldControlPanel battleFieldControlPanel) {
    	super(threadName);
        this.battleFieldController = battleFieldController; 
        this.battleFieldControlPanel = battleFieldControlPanel;
    }

    abstract protected void perform(Unit unitSelectedForAction);

    @Override
	public void run() {
        // do not allow actions while an action is running
        battleFieldControlPanel.updateButtons(false);

        // the unit selected for action might be removed
        Unit unitSelectedForAction = battleFieldController.getUnitSelectedForAction();
        if (unitSelectedForAction != null) {
            perform(unitSelectedForAction);
            battleFieldController.waitAWhile();
        }

        battleFieldController.nextTurn();

        // enable buttons
        battleFieldControlPanel.updateButtons(true);
    }
}