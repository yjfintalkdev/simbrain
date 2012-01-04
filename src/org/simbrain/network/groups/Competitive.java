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
package org.simbrain.network.groups;

import java.util.Iterator;

import org.simbrain.network.interfaces.Neuron;
import org.simbrain.network.interfaces.RootNetwork;
import org.simbrain.network.interfaces.Synapse;
import org.simbrain.network.interfaces.SynapseUpdateRule;
import org.simbrain.network.layouts.Layout;
import org.simbrain.network.listeners.NetworkEvent;
import org.simbrain.network.listeners.SynapseListener;
import org.simbrain.network.neurons.LinearNeuron;

/**
 * <b>Competitive</b> implements a simple competitive network (See PDP 1, ch. 151-193.)
 * 
 * TODO: Add "recall" function as with SOM
 *
 * @author Jeff Yoshimi
 */
public class Competitive extends SubnetworkGroup implements UpdatableGroup  {

    /** Learning rate. */
    private double epsilon = .1;

    /** Winner value. */
    private double winValue = 1;

    /** loser value. */
    private double loseValue = 0;

    /** Number of neurons. */
    private int numNeurons = 5;

    /** Normalize inputs boolean. */
    private boolean normalizeInputs = true;

    /** Use leaky learning boolean. */
    private boolean useLeakyLearning = false;

    /** Leaky epsilon value. */
    private double leakyEpsilon = epsilon / 4;

    /** Max, value and activation values. */
    private double max, val, activation;

    /** Winner value. */
    private int winner;

    /**
     * Constructs a competitive network with specified number of neurons.
     *
     * @param numNeurons size of this network in neurons
     * @param layout Defines how neurons are to be layed out
     * @param root reference to RootNetwork.
     */
    public Competitive(final RootNetwork root, final int numNeurons, final Layout layout) {
        super(root, 1, 1);
        for (int i = 0; i < numNeurons; i++) {
            getNeuronGroup().addNeuron(new Neuron(root, new LinearNeuron()));
        }
        layout.layoutNeurons(this.getNeuronGroup().getNeuronList());
        root.addSynapseListener(synapseListener);
        setLabel("Competitive Network");
    }
    
    /**
     * Listen for synapse events and add new synapse when they arrive.
     */
    SynapseListener synapseListener = new SynapseListener() {

        public void synapseRemoved(NetworkEvent<Synapse> networkEvent) {
            // This is handled elsewhere
        }

        public void synapseAdded(NetworkEvent<Synapse> networkEvent) {
            Synapse synapse = networkEvent.getObject();
            if (getNeuronGroup().inFanInOfSomeNode(synapse)) {
                
                // TODO: This is way top much to expect client to know.  Fold in to 
                //  API! And make simpler...
                
                getParentNetwork().deleteSynapseShallow(synapse); // remove from root net
                synapse.setParentGroup(getSynapseGroup());
                getSynapseGroup().getSynapseList().add(synapse); // Add to this list
                
                // Fire Event
                NetworkEvent<Group> event = new NetworkEvent<Group>(
                        getParentNetwork(), Competitive.this, Competitive.this);
                event.setAuxiliaryObject(synapse);
                getParentNetwork().fireGroupChanged(event,"synapseAdded"); 
            }
        }

        public void synapseChanged(NetworkEvent<Synapse> networkEvent) {
        }

        public void synapseTypeChanged(
                NetworkEvent<SynapseUpdateRule> networkEvent) {
        }
        
    };
    
    @Override
    public void delete() {
        super.delete();
        getParentNetwork().removeSynapseListener(synapseListener);
    }
    
    /**
     * Copy constructor.
     *
     * @param newParent new root network
     * @param oldNet old network.
     */
    public Competitive(RootNetwork newRoot, Competitive oldNet) {
        super(newRoot);
        setEpsilon(oldNet.getEpsilon());
        setLeakyEpsilon(oldNet.getLeakyEpsilon());
        setLoseValue(oldNet.getLoseValue());
        setWinValue(oldNet.getWinValue());
        setNormalizeInputs(oldNet.getNormalizeInputs());
    }

    /**
     * {@inheritDoc}
     */
    public void update() {

        getNeuronGroup().updateNeurons();
        max = 0;
        winner = 0;

        // Determine Winner
        for (int i = 0; i < getNeuronGroup().getNeuronList().size(); i++) {
            Neuron n = (Neuron) getNeuronGroup().getNeuronList().get(i);
            n.getAverageInput();
            if (n.getActivation() > max) {
                max = n.getActivation();
                winner = i;
            }
        }

        // Update weights on winning neuron
        for (int i = 0; i < getNeuronGroup().getNeuronList().size(); i++) {
            Neuron neuron = ((Neuron) getNeuronGroup().getNeuronList().get(i));
            double sumOfInputs = neuron.getTotalInput();

            // Don't update weights if no incoming lines have greater than zero
            // activation
            if (neuron.getNumberOfActiveInputs(0) == 0) {
                return;
            }
            if (i == winner) {
                if (!getParentNetwork().getClampNeurons()) {
                    neuron.setActivation(winValue);
                }
                if (!getParentNetwork().getClampWeights()) {

                    // Apply learning rule
                    for (Synapse incoming : neuron.getFanIn()) {
                        activation = incoming.getSource().getActivation();

                        // Normalize the input values
                        if (normalizeInputs) {
                            activation /= sumOfInputs;
                        }

                        val = incoming.getStrength() + epsilon
                                * (activation - incoming.getStrength());
                        incoming.setStrength(val);
                    }
                }
            } else {
                if (!getParentNetwork().getClampNeurons()) {
                    neuron.setActivation(loseValue);
                }
                if ((useLeakyLearning) & (!getParentNetwork().getClampWeights())) {
                    for (Synapse incoming : neuron.getFanIn()) {
                        activation = incoming.getSource().getActivation();
                        if (normalizeInputs) {
                            activation /= sumOfInputs;
                        }
                        val = incoming.getStrength() + leakyEpsilon
                                * (activation - incoming.getStrength());
                        incoming.setStrength(val);
                    }
                }
            }
        }
        //normalizeIncomingWeights();
    }

    /**
     * Normalize  weights coming in to this network, separately for each neuron.
     */
    public void normalizeIncomingWeights() {

        for (Iterator i = getNeuronGroup().getNeuronList().iterator(); i.hasNext(); ) {
            Neuron n = (Neuron) i.next();
            double normFactor = n.getSummedIncomingWeights();
            for (Synapse s : n.getFanIn()) {
                s.setStrength(s.getStrength() / normFactor);
            }
        }
    }

    /**
     * Normalize all weights coming in to this network.
     */
    public void normalizeAllIncomingWeights() {

        double normFactor = getSummedIncomingWeights();
        for (Iterator i = getNeuronGroup().getNeuronList().iterator(); i.hasNext(); ) {
            Neuron n = (Neuron) i.next();
            for (Synapse s : n.getFanIn()) {
                s.setStrength(s.getStrength() / normFactor);
            }
        }
    }

    /**
     * Randomize all weights coming in to this network.
     */
    public void randomizeIncomingWeights() {

        for (Iterator i = getNeuronGroup().getNeuronList().iterator(); i.hasNext(); ) {
            Neuron n = (Neuron) i.next();
            for (Synapse s : n.getFanIn()) {
                s.randomize();
            }
        }
    }

    /**
     * Returns the sum of all incoming weights to this network.
     *
     * @return the sum of all incoming weights to this network.
     */
    private double getSummedIncomingWeights() {
        double ret = 0;
        for (Iterator i = getNeuronGroup().getNeuronList().iterator(); i.hasNext(); ) {
            Neuron n = (Neuron) i.next();
            ret += n.getSummedIncomingWeights();
        }
        return ret;
    }

    /**
     * Randomize and normalize weights.
     */
    public void randomize() {
        randomizeIncomingWeights();
        normalizeIncomingWeights();
    }

    /**
     * Return the epsilon.
     *
     * @return the epsilon value.
     */
    public double getEpsilon() {
        return epsilon;
    }

    /**
     * Sets epsilon.
     *
     * @param epsilon The new epsilon value.
     */
    public void setEpsilon(final double epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * Return the loser value.
     *
     * @return the loser Value
     */
    public final double getLoseValue() {
        return loseValue;
    }

    /**
     * Sets the loser value.
     *
     * @param loseValue The new loser value
     */
    public final void setLoseValue(final double loseValue) {
        this.loseValue = loseValue;
    }

    /**
     * Return the winner value.
     *
     * @return the winner value
     */
    public final double getWinValue() {
        return winValue;
    }

    /**
     * Sets the winner value.
     *
     * @param winValue The new winner value
     */
    public final void setWinValue(final double winValue) {
        this.winValue = winValue;
    }

    /**
     * @return The initial number of neurons.
     */
    public int getNumNeurons() {
        return numNeurons;
    }

    /**
     * Return leaky epsilon value.
     *
     * @return Leaky epsilon value
     */
    public double getLeakyEpsilon() {
        return leakyEpsilon;
    }

    /**
     * Sets the leaky epsilon value.
     *
     * @param leakyEpsilon Leaky epsilon value to set
     */
    public void setLeakyEpsilon(final double leakyEpsilon) {
        this.leakyEpsilon = leakyEpsilon;
    }

    /**
     * Return the normalize inputs value.
     *
     * @return the normailize inputs value
     */
    public boolean getNormalizeInputs() {
        return normalizeInputs;
    }

    /**
     * Sets the normalize inputs value.
     *
     * @param normalizeInputs Normalize inputs value to set
     */
    public void setNormalizeInputs(final boolean normalizeInputs) {
        this.normalizeInputs = normalizeInputs;
    }

    /**
     * Return the leaky learning value.
     *
     * @return the leaky learning value
     */
    public boolean getUseLeakyLearning() {
        return useLeakyLearning;
    }

    /**
     * Sets the leaky learning value.
     *
     * @param useLeakyLearning The leaky learning value to set
     */
    public void setUseLeakyLearning(final boolean useLeakyLearning) {
        this.useLeakyLearning = useLeakyLearning;
    }

    public boolean getEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setEnabled(boolean enabled) {
        // TODO Auto-generated method stub
        
    }

}