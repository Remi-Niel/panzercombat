package com.gampire.pc.swing.model;

import javax.swing.DefaultListSelectionModel;

public class FreezeableListSelectionModel extends DefaultListSelectionModel {

	private boolean freezed = false;

	public void setFreezed(boolean freezed) {
		this.freezed = freezed;
	}

	@Override
	public void setSelectionInterval(int index0, int index1) {
		if (freezed) {
			return;
		} else {
			super.setSelectionInterval(index0, index1);
		}
	}
}
