package com.gampire.pc.model;

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
	final private Map<Unit, PositioningInfo> distanceInfoMapToEnemyUnits = new HashMap<Unit, PositioningInfo>();

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

	public PositioningInfo getDistanceInfo(Unit unit) {
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
			int distance = distanceInfoMapToEnemyUnits.get(unit)
					.getMostProbableDistance();
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

	// called when moving
	public void updateDistanceInfoMap(double moveDistance) {
		Set<Map.Entry<Unit, PositioningInfo>> distanceInfoMapToEnemyUnitsEntries = distanceInfoMapToEnemyUnits.entrySet();
		for(Map.Entry<Unit, PositioningInfo> entry : distanceInfoMapToEnemyUnitsEntries) {
			Unit unit=entry.getKey();	
			PositioningInfo info=entry.getValue();
			info.update(moveDistance, true);
			// need to update the info in the map of the related
			// units as these maps are not the same objects anymore 
			// since rear fire has been introduced
			PositioningInfo enemyInfo=unit.distanceInfoMapToEnemyUnits.get(this);
			enemyInfo.update(moveDistance, false);
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

	public String fire(Unit target, FireInfo fireInfo, boolean movePenalty) {
		// if out of LOS
		if (fireInfo.outOfLOSOrToFar()) {
			String result = name + " can't fire on " + target.name + ". ";
			return result + target.name + " is out of LOS.\n";
		}

		// play sound
		UnitAction.FIRE.playSound();

		// the unit has fired
		hasFired = true;

		// convert fireInfo in distance info two way (in order to avoid asking
		// again next time). For the attacking unit :
		distanceInfoMapToEnemyUnits.put(target, new PositioningInfo(fireInfo
				.getDistance(), fireInfo.isRearShot()));

		// ... and for the target unit. The target will not see the attacking at
		// the rear side as units always attach with the front
		target.distanceInfoMapToEnemyUnits.put(this, new PositioningInfo(fireInfo
				.getDistance(), false));

		// compute the range
		int range = target.getUnitType().isArmored() ? unitType
				.getAntiTankRange() : unitType.getAntiPersonnelRange();

		// divide defend value by 2 for rear shot
		int defendValue = target.getUnitType().getDefendValue();
		if (fireInfo.isRearShot()) {
			defendValue /= 2;
		}

		// compute effective defend value of target unit
		int effectiveDefendValue = defendValue
				+ target.getAdditionalDefendPoints();

		// roll dice
		int diceRoll = (int) Math.floor(Math.random() * 6) + 1;

		// compute the effective range
		int effectiveRange = range + diceRoll - effectiveDefendValue;

		// substract move range if there is a move penalty
		String whileMoving = "";
		if (movePenalty) {
			effectiveRange -= getUnitType().getMoveRange();
			whileMoving = " while moving";
		}

		// compute result
		String result = fireInfo.isRearShot() ? name + " fires at the rear of " : name + " fires on ";

		if (fireInfo.getDistance() == 0) {
			result += "adjacent unit " + target.name + whileMoving + ". ";
		} else if (fireInfo.getDistance() == 1) {
			result += target.name + " at one distance unit" + whileMoving
					+ ". ";
		} else {
			result += target.name + " at " + fireInfo.getDistance() + " distance units"
					+ whileMoving + ". ";
		}

		// smaller or equal corresponds to distance within effective range
		if (fireInfo.getDistance() <= effectiveRange) {
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
			if (fireInfo.getDistance() - effectiveRange < 2) {
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

	public UnitAnimation getAnimation() {
		return animation;
	}

}
