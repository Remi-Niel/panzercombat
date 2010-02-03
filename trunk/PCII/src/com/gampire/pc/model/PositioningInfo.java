package com.gampire.pc.model;

public class PositioningInfo {
	PositioningInfo(int distance, boolean rearShot) {
		lastMeasuredDistance = distance;
		this.rearShot = rearShot;
		maxMoveAfterMeasure = 0;
		mostProbableDistance = lastMeasuredDistance;

	}

	void update(double moveDistance, boolean isMoving) {
		maxMoveAfterMeasure += moveDistance;
		// consider that troops advance to each other with an angle of 30 degree
		// degree on the average
		mostProbableDistance = lastMeasuredDistance
				- (int) Math.round(maxMoveAfterMeasure * Math.cos(Math.PI / 6));
		if (mostProbableDistance < 0) {
			mostProbableDistance = 0;
		}
		// the target unit can not shoot at the rear of the attacking unit
		// as the attacking unit is most probably approaching with its front
		if (!isMoving) {
			rearShot = false;
		}
	}

	private int lastMeasuredDistance;

	/**
	 * Maximum distance the unit could have moved after distance got measured.
	 */
	private double maxMoveAfterMeasure;

	/**
	 * Most probable distance where a unit is.
	 */
	private int mostProbableDistance;

	/**
	 * True when the rear of target unit is visible.
	 */
	private boolean rearShot;

	/**
	 * Distance information is certain when the unit could not have moved since
	 * the distance was last measured.
	 */
	public boolean isCertain() {
		return maxMoveAfterMeasure == 0;
	}

	public int getMostProbableDistance() {
		return mostProbableDistance;
	}

	public boolean isRearShot() {
		return rearShot;
	}

}