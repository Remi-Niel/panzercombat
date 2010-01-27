package com.gampire.pc.action;

import com.gampire.pc.control.BattleFieldController;
import com.gampire.pc.view.panel.BattleFieldControlPanel;

public class StopGameAction extends Thread {

    private final BattleFieldController battleFieldController;
    
    private final BattleFieldControlPanel battleFieldControlPanel;

    public StopGameAction(BattleFieldController battleFieldController, BattleFieldControlPanel battleFieldControlPanel) {
        this.battleFieldController = battleFieldController;
        this.battleFieldControlPanel = battleFieldControlPanel;
    }

    @Override
	public void run() {
        battleFieldControlPanel.updateButtons(false);
        battleFieldController.stopGame();
    }
}