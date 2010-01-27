package com.gampire.pc.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import com.gampire.pc.swing.renderer.HasImageIcon;

public class Unit implements HasImageIcon, Comparable<Unit> {

	final private Camp camp;

	private UnitType unitType;

	private String name;

	private boolean selectedForAction = false;

	private boolean moving = false;

	private boolean hasFired = false;

	private boolean destroyed = false;

	private boolean exploding = false;

	private int additionalDefendPoints = 0;

	// distance to enemy units
	final private Map<Unit, DistanceInfo> distanceInfoMapToEnemyUnits = new HashMap<Unit, DistanceInfo>();

	final private UnitAnimation animation = new UnitAnimation(this);

	public Unit(UnitType unitType, Camp camp) {
		this.unitType = unitType;
		this.camp = camp;
		name = unitType.getShortName();
	}

	public Unit(Unit oldUnit, Camp newCamp) {
		unitType = oldUnit.unitType;
		camp = newCamp;
		name = oldUnit.getName();
	}

	public int compareTo(Unit unit) {
		// Note: this class has a natural ordering that is inconsistent with
		// equals
		return unitType.compareTo(unit.getUnitType());
	}

	public ImageIcon getImageIcon() {
		return unitType.getImageIcon();
	}

	public boolean isSelectedForAction() {
		return selectedForAction;
	}

	public boolean isMoving() {
		return moving;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public boolean isExploding() {
		return exploding;
	}

	public boolean hasFired() {
		return hasFired;
	}

	public void setSelectedForAction(boolean selectedForAction) {
		this.selectedForAction = selectedForAction;
		moving = false;
		hasFired = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public UnitType getUnitType() {
		return unitType;
	}

	public Camp getCamp() {
		return camp;
	}

	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
	}

	public int getAdditionalDefendPoints() {
		return additionalDefendPoints;
	}

	public DistanceInfo getDistanceInfo(Unit unit) {
		if (distanceInfoMapToEnemyUnits.containsKey(unit)) {
			return distanceInfoMapToEnemyUnits.get(unit);
		} else {
			return null;
		}
	}

	public Unit getClosestEnemyUnit() {
		Unit closestUnit = null;
		int closestDistance = Integer.MAX_VALUE;
		Set<Unit> units = distanceInfoMapToEnemyUnits.keySet();
		for (Unit unit : units) {
			int distance = distanceInfoMapToEnemyUnits.get(unit).getMostProbableDistance();
			if (distance < closestDistance) {
				closestDistance = distance;
				closestUnit = unit;
			}
		}
		return closestUnit;
	}

	// call this when removing a unit
	public void clearDistanceInfoMap() {
		Set<Unit> units = distanceInfoMapToEnemyUnits.keySet();
		for (Unit unit : units) {
			unit.distanceInfoMapToEnemyUnits.remove(this);
		}
		// do the following instead of "distanceTo.remove(unit);" in the loop
		// in order to avoid a ConcurrentModificationException
		distanceInfoMapToEnemyUnits.clear();
	}

	// call this when removing a unit
	public void updateDistanceInfoMap(double moveDistance) {
		Collection<DistanceInfo> infos = distanceInfoMapToEnemyUnits.values();
		for (DistanceInfo info : infos) {
			info.update(moveDistance);
			// note: don't need to update the info in the map of the related
			// units as these maps are the same objects
		}
	}

	public void startMoving() {
		// play sound
		UnitAction.MOVE.playSound();

		// the unit is moving
		moving = true;
	}

	public String move(boolean half) {

		int moveDistance = half ? unitType.getMoveRange() / 2 : unitType
				.getMoveRange();
		boolean addHalfToDistance = half && unitType.getMoveRange() % 2 != 0;

		// distances with other units now have changed
		updateDistanceInfoMap(half ? ((double) unitType.getMoveRange()) / 2
				: unitType.getMoveRange());

		String result;
		if (moveDistance == 0 && !addHalfToDistance) {
			result = name + " is immobile.\n";
		} else if (moveDistance == 0 && addHalfToDistance) {
			result = name + " moves over half a distance unit.\n";
		} else {
			result = name + " moves over " + moveDistance;
			if (addHalfToDistance) {
				result += " and a half";
			}
			if (moveDistance == 1 && !addHalfToDistance) {
				result += " distance unit.\n";
			} else {
				result += " distance units.\n";
			}
		}
		if (moveDistance > 0 && additionalDefendPoints != 0) {
			result += "Its defend value drops back to "
					+ unitType.getDefendValue() + ".\n";
			additionalDefendPoints = 0;
		}
		return result;
	}

	public String fire(Unit target, int distance, boolean movePenalty) {
		// if out of LOS
		if (distance == Integer.MAX_VALUE) {
			String result = name + " can't fire on " + target.name + ". ";
			return result + target.name + " is out of LOS.\n";
		}

		// play sound
		UnitAction.FIRE.playSound();

		// the unit has fired
		hasFired = true;

		// set distance two way (in order to avoid asking again next time)
		DistanceInfo distanceInfo = new DistanceInfo(distance);
		distanceInfoMapToEnemyUnits.put(target, distanceInfo);
		target.distanceInfoMapToEnemyUnits.put(this, distanceInfo);

		// compute the range
		int range = target.getUnitType().isArmored() ? unitType
				.getAntiTankRange() : unitType.getAntiPersonnelRange();

		// compute effective defend value of target unit
		int effectiveDefendValue = target.getUnitType().getDefendValue()
				+ target.getAdditionalDefendPoints();

		// roll dice
		int diceRoll = (int) Math.floor(Math.random() * 6) + 1;

		// compute the effective range
		int effectiveRange = range + diceRoll - effectiveDefendValue;

		// substact move range if there is a move penalty
		String whileMoving = "";
		if (movePenalty) {
			effectiveRange -= getUnitType().getMoveRange();
			whileMoving = " while moving";
		}

		// compute result
		String result = name + " fires on ";

		if (distance == 0) {
			result += "adjacent unit " + target.name + whileMoving + ". ";
		} else if (distance == 1) {
			result += target.name + " at one distance unit" + whileMoving
					+ ". ";
		} else {
			result += target.name + " at " + distance + " distance units"
					+ whileMoving + ". ";
		}

		// smaller or equal corresponds to distance within effective range
		if (distance <= effectiveRange) {
			final Camp targetCamp = target.getCamp();
			final int numberOfUnitsLeftInTargetCamp = targetCamp.getUnits()
					.size() - 1;
			final String numberOfUnitsLeftInTargetCampString = numberOfUnitsLeftInTargetCamp > 1 ? numberOfUnitsLeftInTargetCamp
					+ " units"
					: (numberOfUnitsLeftInTargetCamp == 1 ? "only 1 unit"
							: "no more units");
			result += target.getName() + " is destroyed! "
					+ targetCamp.getAlliance().getName() + " camp has "
					+ numberOfUnitsLeftInTargetCampString + " left.\n";
			target.destroyed = true;
		} else {
			if (distance - effectiveRange < 2) {
				result += "It missed very close!\n";
			} else {
				result += "It missed.\n";
			}
		}
		return result;
	}

	public String defend() {
		// play sound
		UnitAction.DEFEND.playSound();

		if (additionalDefendPoints < 3) {
			additionalDefendPoints++;
			return name + " improves its defend value to "
					+ (unitType.getDefendValue() + additionalDefendPoints)
					+ "!\n";
		} else {
			return name + " maintains its defend value at "
					+ (unitType.getDefendValue() + additionalDefendPoints)
					+ ".\n";
		}
	}

	public void explode() {
		// set exploding to true for exploding animation
		exploding = true;

		// play sound
		UnitAction.EXPLODE.playSound();
	}

	// private static final String[] noNames = { "US", "Light", "Medium",
	// "Heavy",
	// "White", "Ausf", "III", "Section", "NbFz" };
	//	
	// public String computeName() {
	// String[] subStrings = unitType.getName().split(" |\\/");
	// int maxLength = 0;
	// String unitName = "";
	// for (int i = 0; i < subStrings.length; i++) {
	// String subString = subStrings[i];
	//
	// // do some replacements
	// if (subString.equals("PzKpfw")) {
	// subString = "Panzer";
	// }
	// if (subString.equals("SdKfz")) {
	// subString = "Fahrzeug";
	// }
	// if (subString.equals("ATG")) {
	// subString = "Anti Tank Gun";
	// }
	//
	// // have Medium Truck instead of Truck
	// if (i > 0 && subString.length() < 10) {
	// if (subString.equals("Truck") || subString.equals("Riffle")
	// || subString.equals("Squad")
	// || subString.equals("Tank") || subString.equals("Team")
	// || subString.equals("Truck")) {
	// unitName = subStrings[i - 1] + " " + subString;
	// if (unitName.length() > 15) {
	// unitName = subStrings[i - 1];
	// }
	// break;
	// }
	// }
	//
	// boolean canBeUsed = true;
	// for (String element : noNames) {
	// if (subString.equals(element)) {
	// canBeUsed = false;
	// }
	// }
	//
	// if (!canBeUsed) {
	// continue;
	// }
	// boolean containsNumerics = false;
	// for (int iChar = 0; iChar < subString.length(); iChar++) {
	// if (Character.digit(subString.charAt(iChar), 10) != -1) {
	// containsNumerics = true;
	// break;
	// }
	// }
	// //if the string contains numerics and is not the last one, take next one
	// if (containsNumerics && i<subStrings.length-1) {
	// continue;
	// }
	// int subStringLength = subString.length();
	// // make sure Tiger is taken in "PzKpfw VI Tiger I"
	// if (subString.equals("Panzer")) {
	// subStringLength = 3;
	// }
	// if (subStringLength >= maxLength) {
	// maxLength = subStringLength;
	// unitName = subString;
	// }
	// }
	//
	// return unitName;
	// }

	public UnitAnimation getAnimation() {
		return animation;
	}

}
