package com.gampire.pc.view.table.renderer;

import java.awt.Component;

import javax.swing.JList;

import com.gampire.pc.model.Army;
import com.gampire.pc.model.UnitType;
import com.gampire.pc.swing.renderer.HasImageIconListCellRenderer;

public class UnitTypeListCellRenderer extends HasImageIconListCellRenderer {

    @Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {    	    	
    	super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		UnitType unitType = (UnitType) value;
		Army army = unitType.getArmy();
		
		// set color
		setForeground(army.getBrightColor());
		if (!isSelected) {
			setBackground(army.getColor());
		} else {
			setBackground(army.getMiddleBrightColor());
		}
        return this;
    }
}
