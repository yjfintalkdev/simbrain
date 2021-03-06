/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.simbrain.world.odorworld.effectors;

import org.simbrain.world.odorworld.entities.RotatingEntity;

/**
 * Effector for turning.
 *
 * @author Jeff Yoshimi
 */
public class Turning extends Effector {

    /** Default direction. */
    public static final double DEFAULT_DIRECTION = 0;

    /** Direction value to turn left. */
    public static final double LEFT = 1;

    /** Direction value to turn right. */
    public static final double RIGHT = -1;

    /**
     * Turn by amount times direction, which encodes direction: 1 for left, -1
     * for right.
     */
    private double direction;

    /** Default amount. */
    public static final double DEFAULT_AMOUNT = 0;

    /** Amount to turn in radians. */
    private double amount = 0;

    /** Default label. */
    public static final String DEFAULT_LABEL = "Turn";

    /**
     * Construct a turning effector.
     *
     * @param parent parent entity.
     * @param label descriptive label
     * @param direction amount turn in radians.
     */
    public Turning(RotatingEntity parent, String label, double direction) {
        super(parent, label);
        this.direction = direction;
    }

    @Override
    public void update() {
        ((RotatingEntity) parent).turn(direction * amount);
        this.amount = 0;
    }

    /**
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Add an amount to turning.  Allows for multiple "turns" to be aggregated.
     *
     * @param amount amount to turn.
     */
    //@Consumible(customDescriptionMethod="getMixedId")
    public void addAmount(double amount) {
        this.amount += amount;
    }


    /**
     * @param amount the amount to set
     */
    //@Consumible(customDescriptionMethod="getMixedId")
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * @return the direction
     */
    public double getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(double direction) {
        this.direction = direction;
    }

    @Override
    public String getTypeDescription() {
        return "Turning Movement";
    }

    
}