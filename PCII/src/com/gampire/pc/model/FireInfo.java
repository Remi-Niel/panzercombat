package com.gampire.pc.model;

public class FireInfo {
    public FireInfo(int distance, boolean rearShot) {
        this.distance=distance;
        this.rearShot=rearShot;
    }

    /**
     * Distance at which the target resides (-1 if unknown, MAX_INT if too far or out of line of sight).
     */
    private int distance;

    /**
     * Boolean indicating if it is a rear shot.
     */
    private boolean rearShot;

	public int getDistance() {
		return distance;
	}

	public boolean isRearShot() {
		return rearShot;
	}
	
	public boolean distanceKnown() {
		return distance>=0;
	}
	
	public boolean outOfLOSOrToFar() {
		return distance==Integer.MAX_VALUE;
	}

}

