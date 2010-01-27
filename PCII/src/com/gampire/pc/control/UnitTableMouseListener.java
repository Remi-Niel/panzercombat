package com.gampire.pc.control;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.gampire.pc.model.Camp;
import com.gampire.pc.view.panel.BattleFieldControlPanel;

public class UnitTableMouseListener extends MouseAdapter {

    private Camp camp;
    private BattleFieldControlPanel battlefieldControlPanel;

    public UnitTableMouseListener(Camp camp, BattleFieldControlPanel battlefieldControlPanel) {
        this.camp = camp;
        this.battlefieldControlPanel = battlefieldControlPanel;
    }

    @Override
	public void mouseClicked(MouseEvent e) {
        if (camp.isSelectedForAction()) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                battlefieldControlPanel.getMoveButton().doClick();
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                battlefieldControlPanel.getDefendButton().doClick();
            }
        } else if (camp.isSelectedAsTarget()) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (e.getClickCount() == 2) {
                    battlefieldControlPanel.getFireButton().doClick();
                }
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                battlefieldControlPanel.getMoveAndFireButton().doClick();
            }
        }
    }
}
