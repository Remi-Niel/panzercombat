package com.gampire.pc.model;

public class UnitAnimation {

    private final Unit unit;

    private static final int NUM_BLINKING_STEPS = 45;

    private static final int NUM_MOVEMENT_STEPS = 25;

    // not too many as this is expensive in computing power and slows down the fire
    private static final int NUM_CLOUD_MOVEMENT_STEPS = 6; 
    
    private static final int NUM_EXPLOSION_STEPS = 30;

    private static final int STEP_EXPLOSION_MAX = 4;

    private static final float FINAL_EXPLOSION_PERCENTAGE = 0.65f;

    private boolean blinkOn = false;

    private int blinkingStep = 0;

    private int movementStep = 0;

    private int cloudMovementStep = 0;
    
    private int explosionStep = 0;

    UnitAnimation(Unit unit) {
        this.unit = unit;
    }

    public boolean getBlinkOn() {
        return blinkOn;
    }

    public float getMovementPercentage() {
        return (float) movementStep / NUM_MOVEMENT_STEPS;
    }

    public float getCloudMovementPercentage() {
        return (float) cloudMovementStep / NUM_CLOUD_MOVEMENT_STEPS;
    }
    
    public float getExplosionPercentage() {
        float explosionPercentage;
        if (explosionStep < STEP_EXPLOSION_MAX) {
            explosionPercentage = (float) explosionStep / STEP_EXPLOSION_MAX;
        } else {
            float normalizedStep = (float) (explosionStep - STEP_EXPLOSION_MAX)
                    / (NUM_EXPLOSION_STEPS - STEP_EXPLOSION_MAX);
            explosionPercentage = (1.0f - normalizedStep) * (1.0f - FINAL_EXPLOSION_PERCENTAGE) + FINAL_EXPLOSION_PERCENTAGE;
        }
        return explosionPercentage;
    }

    public boolean advance() {
        boolean somethingChanged = false;
        if (unit.isSelectedForAction()) {
            if (blinkingStep < NUM_BLINKING_STEPS) {
                if (blinkingStep % 5 == 0) {
                    blinkOn = !blinkOn;
                    somethingChanged = true;
                }
                blinkingStep++;
            }
        } else {
            blinkingStep = 0;
            blinkOn = false;
        }
        if (unit.isMoving()) {
            if (movementStep < NUM_MOVEMENT_STEPS) {
                movementStep++;
                somethingChanged = true;
            }
        } else {
            movementStep = 0;
        }
        if (unit.hasFired()) {
            if (cloudMovementStep < NUM_CLOUD_MOVEMENT_STEPS) {
                cloudMovementStep++;
                somethingChanged = true;
            }
        } else {
            cloudMovementStep = 0;
        }
        if (unit.isExploding()) {
            if (explosionStep < NUM_EXPLOSION_STEPS) {
                explosionStep++;
                somethingChanged = true;
            }
        }
        return somethingChanged;
    }

}
