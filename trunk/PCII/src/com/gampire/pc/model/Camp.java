package com.gampire.pc.model;

import java.util.Collections;
import java.util.Vector;

public class Camp {

    final private BattleField battleField;

    final private Alliance alliance;

    private int numPoints;

    final private Vector<Unit> units = new Vector<Unit>();

    final private boolean isLeft;

    public Camp(BattleField battleField, Alliance alliance, int maxPoints, boolean isLeft) {
        this.battleField = battleField;
        this.alliance = alliance;
        numPoints = 0;
        do {
            addUnit();
        } while (numPoints <= maxPoints);
        this.isLeft = isLeft;
    }

    public Camp(Camp oldCamp, BattleField newBattleField) {
        battleField = newBattleField;
        alliance = oldCamp.alliance;
        numPoints = 0;
        for (Unit unit : oldCamp.units) {
            Unit copiedUnit = new Unit(unit, this);
            addUnit(copiedUnit);
        }
        isLeft = oldCamp.isLeft;
    }

    public Alliance getAlliance() {
        return alliance;
    }

    public int getIndex(Unit unit) {
        for (int iUnit = 0; iUnit < units.size(); iUnit++) {
            if (units.get(iUnit) == unit) {
                return iUnit;
            }
        }
        return -1;
    }

    public Vector<Unit> getUnits() {
        return units;
    }

    public void addUnit() {
        UnitType type = UnitType.selectRandomUnitType(alliance);
        Unit unit = new Unit(type, this);
        addUnit(unit);
    }

    public void addUnit(Unit unit) {
        UnitType type = unit.getUnitType();
        units.add(unit);
        numPoints += type.getPoints();
        Collections.sort(units);
        Collections.sort(new Vector<Integer>());
        updateUnitsWithSameName(unit.getUnitType().getShortName());
    }

    public void removeUnit(Unit unit) {
    	Unit unitToRemove=unit;
        // remove last unit if null unit passed
        if (unitToRemove == null) {
            int lastUnit = units.size() - 1;
            if (lastUnit > -1) {
                unitToRemove = units.get(lastUnit);
            }
        }
        if (unitToRemove != null) {
            UnitType type = unitToRemove.getUnitType();
            units.remove(unitToRemove);
            numPoints -= type.getPoints();
            unitToRemove.clearDistanceInfoMap();
        }
    }

    public void changeUnitType(int iUnit, UnitType unitType) {
        Unit unit = units.get(iUnit);
        numPoints -= unit.getUnitType().getPoints();
        unit.setUnitType(unitType);
        numPoints += unitType.getPoints();
        String name = unit.getUnitType().getShortName();
        unit.setName(name);
        Collections.sort(units);
        updateUnitsWithSameName(name);
    }

    public int removeDestroyedUnits() {
        int numRemoved = 0;
        for (;;) {
            Unit destroyed = null;
            for (Unit unit : units) {
                if (unit.isDestroyed()) {
                    destroyed = unit;
                    break;
                }
            }
            if (destroyed != null) {
                removeUnit(destroyed);
                numRemoved++;
            } else {
                break;
            }
        }
        return numRemoved;
    }

    private void updateUnitsWithSameName(String nameWithoutCounter) {

        Vector<Unit> unitsWithSameName = new Vector<Unit>();
        for (Unit unit : units) {
            String shortName = unit.getUnitType().getShortName();
            if (shortName.equals(nameWithoutCounter)) {
                unitsWithSameName.add(unit);
            }
        }
        if (unitsWithSameName.size() > 1) {
            for (int i = 0; i < unitsWithSameName.size(); i++) {
                Unit unit = unitsWithSameName.get(i);
                unit.setName(nameWithoutCounter + " " + Integer.valueOf(i + 1).toString());
            }
        }
    }

    public int getNumPoints() {
        return numPoints;
    }

    public Unit getUnitSelectedForAction() {
        for (Unit unit : units) {
            if (unit.isSelectedForAction()) {
                return unit;
            }
        }
        return null;
    }

    public boolean isSelectedForAction() {
        return getUnitSelectedForAction() != null;
    }

    public boolean isSelectedAsTarget() {
        return getOther().getUnitSelectedForAction() != null;
    }
    
    public boolean isLeft() {
        return isLeft;
    }

    public Camp getOther() {
        if (this == battleField.getCamp1()) {
            return battleField.getCamp2();
        } else {
            if (this != battleField.getCamp2()) {
                throw new RuntimeException("this camp doesn't belong to its battlefield");
            }
            return battleField.getCamp1();
        }
    }
    
    public boolean advanceAnimations() {
        boolean somethingChanged = false;
        for (Unit unit : units) {
            if (unit.getAnimation().advance()) {
                somethingChanged = true;
            }
        }
        return somethingChanged;
    }
}