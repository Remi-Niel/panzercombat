package com.gampire.pc.action;

import com.gampire.pc.control.BattleFieldController;
import com.gampire.pc.view.panel.BattleFieldControlPanel;

public class StartGameAction extends Thread {

    private final BattleFieldController battleFieldController;

    private final BattleFieldControlPanel battleFieldControlPanel;

    public StartGameAction(BattleFieldController battleFieldController, BattleFieldControlPanel battleFieldControlPanel) {
        this.battleFieldController = battleFieldController;
        this.battleFieldControlPanel = battleFieldControlPanel;
    }

    @Override
	public void run() {
        battleFieldController.startGame();
        // enable buttons
        battleFieldControlPanel.updateButtons(true);
    }
}