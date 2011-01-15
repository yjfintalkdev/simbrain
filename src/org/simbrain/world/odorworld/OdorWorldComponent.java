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
package org.simbrain.world.odorworld;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.simbrain.workspace.AttributeType;
import org.simbrain.workspace.PotentialConsumer;
import org.simbrain.workspace.PotentialProducer;
import org.simbrain.workspace.WorkspaceComponent;
import org.simbrain.world.odorworld.effectors.Effector;
import org.simbrain.world.odorworld.entities.OdorWorldEntity;
import org.simbrain.world.odorworld.entities.RotatingEntity;
import org.simbrain.world.odorworld.sensors.Sensor;
import org.simbrain.world.odorworld.sensors.SmellSensor;
import org.simbrain.world.odorworld.sensors.SmellSensor.SmellSensorGetter;

/**
 * <b>WorldPanel</b> is the container for the world component. Handles toolbar
 * buttons, and serializing of world data. The main environment codes is in
 * {@link OdorWorldPanel}.
 */
public class OdorWorldComponent extends WorkspaceComponent {

    /** Reference to model world. */
    private OdorWorld world = new OdorWorld();

    /** Attribute types. */
    AttributeType xLocationType = (new AttributeType(this, "Location", "X", double.class, false));
    AttributeType yLocationType = (new AttributeType(this, "Location", "Y", double.class, false));
    AttributeType leftRotationType = (new AttributeType(this, "Left", double.class, true));
    AttributeType rightRotationType = (new AttributeType(this, "Right", double.class, true));
    AttributeType straightMovementType = (new AttributeType(this, "Straight", double.class, true));
    AttributeType absoluteMovementType = (new AttributeType(this, "Absolute-movement", double.class, false));
    AttributeType smellSensorType = (new AttributeType(this, "Smell-Sensor", "getValue", double.class, true));

    /**
     * Default constructor.
     */
    public OdorWorldComponent(final String name) {
        super(name);
        initializeAttributes();
        addListener();
    }

    /**
     * Constructor used in deserializing.
     *
     * @param name name of world
     * @param world model world
     */
    public OdorWorldComponent(final String name, final OdorWorld world) {
        super(name);
        this.world = world;
        initializeAttributes();
        addListener();
    }

    /**
     * Initialize odor world attributes.
     */
    private void initializeAttributes() {

        addConsumerType(xLocationType);
        addConsumerType(yLocationType);
        addConsumerType(leftRotationType);
        addConsumerType(rightRotationType);
        addConsumerType(straightMovementType);
        addConsumerType(absoluteMovementType);

        addProducerType(xLocationType);
        addProducerType(yLocationType);
        addProducerType(smellSensorType);
    }

    @Override
    public List<PotentialConsumer> getPotentialConsumers() {

        List<PotentialConsumer> returnList = new ArrayList<PotentialConsumer>();

        for (OdorWorldEntity entity : world.getObjectList()) {

            // X, Y Locations
            if (xLocationType.isVisible()) {
                String description = entity.getName() + ":" + xLocationType.getDescription();
                returnList.add(getAttributeManager().createPotentialConsumer(entity, xLocationType, description));
            }
            if (yLocationType.isVisible()) {
                String description = entity.getName() + ":" + yLocationType.getDescription();
                returnList.add(getAttributeManager().createPotentialConsumer(entity, yLocationType, description));
            }

            // Absolute movement
            if (absoluteMovementType.isVisible()) {
                returnList.add(getAttributeManager().createPotentialConsumer(entity, "moveNorth", double.class, entity.getName() + ":goNorth"));
                returnList.add(getAttributeManager().createPotentialConsumer(entity, "moveSouth", double.class, entity.getName() + ":goSouth"));
                returnList.add(getAttributeManager().createPotentialConsumer(entity, "moveEast", double.class, entity.getName() + ":goEast"));
                returnList.add(getAttributeManager().createPotentialConsumer(entity, "moveWest", double.class, entity.getName() + ":goWest"));
            }

            // Turning and Going Straight
            if (entity instanceof RotatingEntity) {
                returnList.add(getAttributeManager().createPotentialConsumer(entity, "turnLeft", double.class, entity.getName() + ":turnLeft"));
                returnList.add(getAttributeManager().createPotentialConsumer(entity, "turnRight", double.class, entity.getName() + ":turnRight"));
                returnList.add(getAttributeManager().createPotentialConsumer(entity, "goStraight", double.class, entity.getName() + ":goStraight"));
            }
        }
        return returnList;
    }

    @Override
    public List<PotentialProducer> getPotentialProducers() {

        List<PotentialProducer> returnList = new ArrayList<PotentialProducer>();

        for (OdorWorldEntity entity : world.getObjectList()) {

            // X, Y Location of entities
            if (xLocationType.isVisible()) {
                String description = entity.getName() + ":" + xLocationType.getDescription();
                returnList.add(getAttributeManager().createPotentialProducer(entity, "DoubleX", double.class, description));
            }
            if (yLocationType.isVisible()) {
                String description = entity.getName() + ":" + yLocationType.getDescription();
                returnList.add(getAttributeManager().createPotentialProducer(entity, "DoubleY", double.class, description));
            }

            // Smell sensor
            if (smellSensorType.isVisible()) {
                for (Sensor sensor : entity.getSensors()) {
                    if (sensor instanceof SmellSensor) {
                        SmellSensor smell = (SmellSensor) sensor;
                        for (int i = 0; i < smell.getCurrentValue().length; i++) {
                            SmellSensorGetter getter =  smell.createGetter(i);
                            String description = smellSensorType.getSimpleDescription(entity
                                    .getName() + ":" + smell.getId() + "[" + i + "]");
                            returnList.add(getAttributeManager().createPotentialProducer(getter, smellSensorType, description));
                        }
                        // TODO: A way of indicating sensor location (relative
                        // location in polar coordinates)
                    }
                }
            }
        }
        return returnList;
    }


    /**
     * Initialize this component.
     */
    private void addListener() {
        world.addListener(new WorldListener() {

            public void updated() {
                fireUpdateEvent();
            }
            public void effectorAdded(final Effector effector) {
                firePotentialAttributesChanged();
            }

            public void effectorRemoved(final Effector effector) {
                fireAttributeObjectRemoved(effector);
                firePotentialAttributesChanged();
            }

            public void entityAdded(final OdorWorldEntity entity) {
                firePotentialAttributesChanged();
            }

            public void entityRemoved(final OdorWorldEntity entity) {
                fireAttributeObjectRemoved(entity);
                firePotentialAttributesChanged();
            }

            public void sensorAdded(final Sensor sensor) {
                firePotentialAttributesChanged();
            }

            public void sensorRemoved(Sensor sensor) {
                // TODO: Go through all smell sensor getters, and if any refer to this, 
                //  then remove that getter
                fireAttributeObjectRemoved(sensor); 
                firePotentialAttributesChanged();
            }
            public void entityChanged(OdorWorldEntity entity) {
            }
            
        });
    }

    /**
     * Return a smell sensor getter, or null if there is no matching object.
     * Helper method for scripts.
     *
     * @param rotatingEntity entity to match
     * @param name name of sensor (left, right, center) to match
     * @param i index of smell vector
     *
     * @return smell sensor getter wrapping this sensor
     */
    public SmellSensorGetter createSmellSensor(RotatingEntity rotatingEntity, String name, int i) {
        for (OdorWorldEntity entity : world.getObjectList()) {
            if (entity == rotatingEntity) {
                for (Sensor sensor : rotatingEntity.getSensors()) {
                    if (sensor instanceof SmellSensor) {
                        SmellSensor smellSensor = (SmellSensor) sensor;
                        if (smellSensor.getId().equalsIgnoreCase(name)) {
                            return smellSensor.createGetter(i);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Recreates an instance of this class from a saved component.
     *
     * @param input
     * @param name
     * @param format
     * @return
     */
    public static OdorWorldComponent open(InputStream input, String name, String format) {
        OdorWorld newWorld = (OdorWorld) OdorWorld.getXStream().fromXML(input);
        return new OdorWorldComponent(name, newWorld);
    }

    @Override
    public String getXML() {
        return OdorWorld.getXStream().toXML(world);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(final OutputStream output, final String format) {
        OdorWorld.getXStream().toXML(world, output);
    }

    @Override
    public String getKeyFromObject(Object object) {
        if (object instanceof OdorWorldEntity) {
            return ((OdorWorldEntity) object).getName();
        } else if (object instanceof Sensor) {
            String entityName = ((Sensor) object).getParent().getName();
            String sensorName = ((Sensor) object).getId();
            return entityName + ":sensor:" + sensorName;
        } else if (object instanceof Effector) {
            String entityName = ((Effector) object).getParent().getName();
            String effectorName = ((Effector) object).getId();
            return entityName + ":effector:" + effectorName;
        } else if (object instanceof SmellSensorGetter) {
            String entityName = ((SmellSensorGetter) object).getParent().getParent().getName();
            String sensorName = ((SmellSensorGetter) object).getParent().getId();
            String index = "" + ((SmellSensorGetter) object).getIndex();
            return entityName + ":smellSensorGetter:" + sensorName + ":" + index; 
        }

        return null;
    }

    @Override
    public Object getObjectFromKey(String objectKey) {
        String[] parsedKey  = objectKey.split(":");
        String entityName = parsedKey[0];
        if (parsedKey.length == 1) {
            return getWorld().getEntityFromKey(entityName);
        } else {
            String secondString = parsedKey[1];
            if (secondString.equalsIgnoreCase("sensor")) {
                return getWorld().getSensorFromKeys(entityName, parsedKey[2]);
            } else if (secondString.equalsIgnoreCase("effector")) {
                return getWorld().getEffectorFromKeys(entityName, parsedKey[2]);
            } else if (secondString.equalsIgnoreCase("smellSensorGetter")) {
                int index = Integer.parseInt(parsedKey[3]);
                return getWorld().getSmellSensorGetter(entityName,
                        parsedKey[2], index);
            }
        }
        return null;
    }

    @Override
    public void closing() {
        // TODO Auto-generated method stub
    }

    @Override
    public void update() {
        world.update();
    }

    @Override
    public void setCurrentDirectory(final String currentDirectory) { 
        super.setCurrentDirectory(currentDirectory);
        OdorWorldPreferences.setCurrentDirectory(currentDirectory);
    }

    @Override
    public String getCurrentDirectory() {
       return OdorWorldPreferences.getCurrentDirectory();
    }

    /**
     * Returns a reference to the odor world.
     *
     * @return the odor world object.
     */
    public OdorWorld getWorld() {
        return world;
    }
}