package com.gampire.pc.model;


public class UnitTypeBean {

	private String name;

	private String shortName;

	private int moveRange;

	private int antiPersonnelRange;

	private int antiTankRange;

	private int defendValue;

	private boolean armored;

	private String armyName;

	private String image;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public int getMoveRange() {
		return moveRange;
	}

	public void setMoveRange(int moveRange) {
		this.moveRange = moveRange;
	}

	public int getAntiPersonnelRange() {
		return antiPersonnelRange;
	}

	public void setAntiPersonnelRange(int antiPersonnelRange) {
		this.antiPersonnelRange = antiPersonnelRange;
	}

	public int getAntiTankRange() {
		return antiTankRange;
	}

	public void setAntiTankRange(int antiTankRange) {
		this.antiTankRange = antiTankRange;
	}

	public int getDefendValue() {
		return defendValue;
	}

	public void setDefendValue(int defendValue) {
		this.defendValue = defendValue;
	}

	public boolean isArmored() {
		return armored;
	}

	public void setArmored(boolean armored) {
		this.armored = armored;
	}

	public String getArmyName() {
		return armyName;
	}

	public void setArmyName(String armyName) {
		this.armyName = armyName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}



}
