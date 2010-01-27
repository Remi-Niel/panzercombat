package com.gampire.pc.model;

import java.applet.AudioClip;

import com.gampire.pc.sound.AudioClipUtil;

public enum UnitAction {

	MOVE("move", "move.wav"), FIRE("fire", "fire.wav"), MOVE_AND_FIRE(
			"move and fire", null), DEFEND("defend", "defend.wav"), EXPLODE(
			"exlode", "explode.wav");

	private final String name;
	private final AudioClip clip;

	private UnitAction(String name, String soundFileName) {
		this.name = name;
		if (soundFileName != null) {
			clip = AudioClipUtil.getAudioClip(soundFileName);
		} else {
			clip = null;
		}
	}

	@Override
	public String toString() {
		return name;
	}

	public void playSound() {
		if (clip != null) {
			clip.play();
		}
	}
}
