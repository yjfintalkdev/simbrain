package org.simbrain.network.gui.dialogs.neuron.rule_panels;

import org.simbrain.network.core.Neuron;
import org.simbrain.network.core.NeuronUpdateRule;
import org.simbrain.network.gui.NetworkUtils;
import org.simbrain.network.gui.ParameterGetter;
import org.simbrain.network.gui.dialogs.neuron.AbstractNeuronRulePanel;
import org.simbrain.network.neuron_update_rules.MorrisLecarRule;
import org.simbrain.util.LabelledItemPanel;
import org.simbrain.util.SimbrainConstants;
import org.simbrain.util.Utils;
import org.simbrain.util.widgets.TristateDropDown;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

/**
 * @author Amanda Pandey <amanda.pandey@gmail.com>
 */
public class MorrisLecarRulePanel extends AbstractNeuronRulePanel{

    /** Calcium channel conductance (micro Siemens/cm^2). */
    private JTextField tfG_Ca = new JTextField();

    /** Potassium channel conductance (micro Siemens/cm^2). */
    private JTextField tfG_K = new JTextField();

    /** Leak conductance (micro Siemens/cm^2). */
    private JTextField tfG_L = new JTextField();

    /** Resting potential calcium (mV). */
    private JTextField tfVRest_Ca = new JTextField();

    /** Resting potential potassium (mV). */
    private JTextField tfvRest_k = new JTextField();

    /** Resting potential for leak current (mV). */
    private JTextField tfVRest_L = new JTextField();

    /** Membrane capacitance per unit area (micro Farads/cm^2). */
    private JTextField tfCMembrane = new JTextField();

    /** Membrane voltage constant 1. */
    private JTextField tfV_M1 = new JTextField();

    /** Membrane voltage constant 2. */
    private JTextField tfV_M2 = new JTextField();

    /** Potassium channel constant 1. */
    private JTextField tfV_W1 = new JTextField();

    /** Potassium channel constant 2. */
    private JTextField tfV_W2 = new JTextField();

    /** Fraction of open potassium channels. */
    private JTextField tfW_K = new JTextField();

    /** Potassium channel time constant/decay rate (s^-1). */
    private JTextField tfPhi = new JTextField();

    /** Background current (mA). */
    private JTextField tfI_Bg = new JTextField();

    /** Threshold for neurotransmitter release (mV) */
    private JTextField tfThreshold = new JTextField();


    private TristateDropDown isAddNoise = new TristateDropDown();

    private JTabbedPane tabbedPane = new JTabbedPane();

    private LabelledItemPanel mainTab = new LabelledItemPanel();

    /** A reference to the neuron update rule being edited. */
    private static final MorrisLecarRule prototypeRule = new MorrisLecarRule();


    public MorrisLecarRulePanel() {
        super();
        this.add(tabbedPane);
        mainTab.addItem("Calcium Channel Conductance (Micro Siemens/cm^2)", tfG_Ca);
        mainTab.addItem("Potassium channel conductance (micro Siemens/cm^2)", tfG_K);
        mainTab.addItem("Leak conductance (micro Siemens/cm^2)", tfG_L);
        mainTab.addItem("Resting potential calcium (mV)", tfVRest_Ca);
        mainTab.addItem("Resting potential potassium (mV)", tfvRest_k);
        mainTab.addItem("Resting potential for leak current (mV)", tfVRest_L);
        mainTab.addItem("Membrane capacitance per unit area (micro Farads/cm^2)", tfCMembrane);
        mainTab.addItem("Membrane voltage constant 1", tfV_M1);
        mainTab.addItem("Membrane voltage constant 2", tfV_M2);
        mainTab.addItem("Potassium channel constant 1", tfV_W1);
        mainTab.addItem("Potassium channel constant 2", tfV_W2);
        mainTab.addItem("Fraction of open potassium channels", tfW_K);
        mainTab.addItem("Potassium channel time constant/decay rate (s^-1)", tfPhi);
        mainTab.addItem("Background current (mA)", tfI_Bg);
        mainTab.addItem("Threshold for neurotransmitter release (mV)", tfThreshold);
        tabbedPane.add(mainTab, "MorrisLecarFields");
    }

    @Override
    public void fillFieldValues(List<NeuronUpdateRule> ruleList) {
        MorrisLecarRule neuronRef = (MorrisLecarRule) ruleList.get(0);

        // Handle Add Noise
        if (!NetworkUtils.isConsistent(ruleList, MorrisLecarRule.class,
                "getAddNoise"))
            isAddNoise.setNull();
        else
            isAddNoise.setSelected(neuronRef.getAddNoise());

        // TODO: Use lambda expressions when we upgrade to be Java 8 compat

        // G_Ca
        ParameterGetter<NeuronUpdateRule, Double> gcGetter =
                new ParameterGetter<NeuronUpdateRule, Double>() {
                    @Override
                    public Double getParameter(NeuronUpdateRule source) {
                        return ((MorrisLecarRule) source).getG_Ca();
                    }
                };
        if (!NetworkUtils.isConsistent(ruleList, gcGetter)) {
            tfG_Ca.setText(SimbrainConstants.NULL_STRING);
        } else {
            tfG_Ca.setText(Double.toString(gcGetter.getParameter(neuronRef)));
        }

        // G_K
        ParameterGetter<NeuronUpdateRule, Double> gkGetter =
                new ParameterGetter<NeuronUpdateRule, Double>() {
                    @Override
                    public Double getParameter(NeuronUpdateRule source) {
                        return ((MorrisLecarRule) source).getG_K();
                    }
                };
        if (!NetworkUtils.isConsistent(ruleList, gkGetter)) {
            tfG_K.setText(SimbrainConstants.NULL_STRING);
        } else {
            tfG_K.setText(Double.toString(gkGetter.getParameter(neuronRef)));
        }

        // G_L
        ParameterGetter<NeuronUpdateRule, Double> glGetter =
                new ParameterGetter<NeuronUpdateRule, Double>() {
                    @Override
                    public Double getParameter(NeuronUpdateRule source) {
                        return ((MorrisLecarRule) source).getG_L();
                    }
                };
        if (!NetworkUtils.isConsistent(ruleList, glGetter)) {
            tfG_L.setText(SimbrainConstants.NULL_STRING);
        } else {
            tfG_L.setText(Double.toString(glGetter.getParameter(neuronRef)));
        }

        // vRest_Ca
        ParameterGetter<NeuronUpdateRule, Double> vrcGetter =
                new ParameterGetter<NeuronUpdateRule, Double>() {
                    @Override
                    public Double getParameter(NeuronUpdateRule source) {
                        return ((MorrisLecarRule) source).getvRest_Ca();
                    }
                };
        if (!NetworkUtils.isConsistent(ruleList, vrcGetter)) {
            tfVRest_Ca.setText(SimbrainConstants.NULL_STRING);
        } else {
            tfVRest_Ca.setText(Double.toString(vrcGetter.getParameter(neuronRef)));
        }

        // vRest_k
        ParameterGetter<NeuronUpdateRule, Double> vrkGetter =
                new ParameterGetter<NeuronUpdateRule, Double>() {
                    @Override
                    public Double getParameter(NeuronUpdateRule source) {
                        return ((MorrisLecarRule) source).getvRest_k();
                    }
                };
        if (!NetworkUtils.isConsistent(ruleList, vrkGetter)) {
            tfvRest_k.setText(SimbrainConstants.NULL_STRING);
        } else {
            tfvRest_k.setText(Double.toString(vrkGetter.getParameter(neuronRef)));
        }

        // vRest_L
        ParameterGetter<NeuronUpdateRule, Double> vrlGetter =
                new ParameterGetter<NeuronUpdateRule, Double>() {
                    @Override
                    public Double getParameter(NeuronUpdateRule source) {
                        return ((MorrisLecarRule) source).getvRest_L();
                    }
                };
        if (!NetworkUtils.isConsistent(ruleList, vrlGetter)) {
            tfVRest_L.setText(SimbrainConstants.NULL_STRING);
        } else {
            tfVRest_L.setText(Double.toString(vrlGetter.getParameter(neuronRef)));
        }

        // cMembrane
        ParameterGetter<NeuronUpdateRule, Double> cmGetter =
                new ParameterGetter<NeuronUpdateRule, Double>() {
                    @Override
                    public Double getParameter(NeuronUpdateRule source) {
                        return ((MorrisLecarRule) source).getcMembrane();
                    }
                };
        if (!NetworkUtils.isConsistent(ruleList, cmGetter)) {
            tfCMembrane.setText(SimbrainConstants.NULL_STRING);
        } else {
            tfCMembrane.setText(Double.toString(cmGetter.getParameter(neuronRef)));
        }

        // v_m1
        ParameterGetter<NeuronUpdateRule, Double> vm1Getter =
                new ParameterGetter<NeuronUpdateRule, Double>() {
                    @Override
                    public Double getParameter(NeuronUpdateRule source) {
                        return ((MorrisLecarRule) source).getV_m1();
                    }
                };
        if (!NetworkUtils.isConsistent(ruleList, vm1Getter)) {
            tfV_M1.setText(SimbrainConstants.NULL_STRING);
        } else {
            tfV_M1.setText(Double.toString(cmGetter.getParameter(neuronRef)));
        }

        // v_m2
        ParameterGetter<NeuronUpdateRule, Double> vm2Getter =
                new ParameterGetter<NeuronUpdateRule, Double>() {
                    @Override
                    public Double getParameter(NeuronUpdateRule source) {
                        return ((MorrisLecarRule) source).getV_m2();
                    }
                };
        if (!NetworkUtils.isConsistent(ruleList, vm2Getter)) {
            tfV_M2.setText(SimbrainConstants.NULL_STRING);
        } else {
            tfV_M2.setText(Double.toString(vm2Getter.getParameter(neuronRef)));
        }

        // v_w1
        ParameterGetter<NeuronUpdateRule, Double> vw1Getter =
                new ParameterGetter<NeuronUpdateRule, Double>() {
                    @Override
                    public Double getParameter(NeuronUpdateRule source) {
                        return ((MorrisLecarRule) source).getV_w1();
                    }
                };
        if (!NetworkUtils.isConsistent(ruleList, vw1Getter)) {
            tfV_W1.setText(SimbrainConstants.NULL_STRING);
        } else {
            tfV_W1.setText(Double.toString(vw1Getter.getParameter(neuronRef)));
        }

        // v_w2
        ParameterGetter<NeuronUpdateRule, Double> vw2Getter =
                new ParameterGetter<NeuronUpdateRule, Double>() {
                    @Override
                    public Double getParameter(NeuronUpdateRule source) {
                        return ((MorrisLecarRule) source).getV_w2();
                    }
                };
        if (!NetworkUtils.isConsistent(ruleList, vw2Getter)) {
            tfV_W2.setText(SimbrainConstants.NULL_STRING);
        } else {
            tfV_W2.setText(Double.toString(vw2Getter.getParameter(neuronRef)));
        }

        // w_k
        ParameterGetter<NeuronUpdateRule, Double> wkGetter =
                new ParameterGetter<NeuronUpdateRule, Double>() {
                    @Override
                    public Double getParameter(NeuronUpdateRule source) {
                        return ((MorrisLecarRule) source).getW_K();
                    }
                };
        if (!NetworkUtils.isConsistent(ruleList, wkGetter)) {
            tfW_K.setText(SimbrainConstants.NULL_STRING);
        } else {
            tfW_K.setText(Double.toString(wkGetter.getParameter(neuronRef)));
        }

        // phi
        ParameterGetter<NeuronUpdateRule, Double> phiGetter =
                new ParameterGetter<NeuronUpdateRule, Double>() {
                    @Override
                    public Double getParameter(NeuronUpdateRule source) {
                        return ((MorrisLecarRule) source).getPhi();
                    }
                };
        if (!NetworkUtils.isConsistent(ruleList, phiGetter)) {
            tfPhi.setText(SimbrainConstants.NULL_STRING);
        } else {
            tfPhi.setText(Double.toString(wkGetter.getParameter(neuronRef)));
        }

        // i_bg
        ParameterGetter<NeuronUpdateRule, Double> ibgGetter =
                new ParameterGetter<NeuronUpdateRule, Double>() {
                    @Override
                    public Double getParameter(NeuronUpdateRule source) {
                        return ((MorrisLecarRule) source).getI_bg();
                    }
                };
        if (!NetworkUtils.isConsistent(ruleList, ibgGetter)) {
            tfI_Bg.setText(SimbrainConstants.NULL_STRING);
        } else {
            tfI_Bg.setText(Double.toString(wkGetter.getParameter(neuronRef)));
        }

        // threshold
        ParameterGetter<NeuronUpdateRule, Double> thrGetter =
                new ParameterGetter<NeuronUpdateRule, Double>() {
                    @Override
                    public Double getParameter(NeuronUpdateRule source) {
                        return ((MorrisLecarRule) source).getThreshold();
                    }
                };
        if (!NetworkUtils.isConsistent(ruleList, thrGetter)) {
            tfThreshold.setText(SimbrainConstants.NULL_STRING);
        } else {
            tfThreshold.setText(Double.toString(thrGetter.getParameter(neuronRef)));
        }
    }

    @Override
    public void fillDefaultValues() {
        tfG_Ca.setText(Double.toString(prototypeRule.getG_Ca()));
        tfG_K.setText(Double.toString(prototypeRule.getG_K()));
        tfG_L.setText(Double.toString(prototypeRule.getG_L()));
        tfVRest_Ca.setText(Double.toString(prototypeRule.getvRest_Ca()));
        tfvRest_k.setText(Double.toString(prototypeRule.getvRest_k()));
        tfVRest_L.setText(Double.toString(prototypeRule.getvRest_L()));
        tfCMembrane.setText(Double.toString(prototypeRule.getcMembrane()));
        tfV_M1.setText(Double.toString(prototypeRule.getV_m1()));
        tfV_M2.setText(Double.toString(prototypeRule.getV_m2()));
        tfV_W1.setText(Double.toString(prototypeRule.getV_w1()));
        tfV_W2.setText(Double.toString(prototypeRule.getV_w2()));
        tfW_K.setText(Double.toString(prototypeRule.getW_K()));
        tfPhi.setText(Double.toString(prototypeRule.getPhi()));
        tfI_Bg.setText(Double.toString(prototypeRule.getI_bg()));
        tfThreshold.setText(Double.toString(prototypeRule.getThreshold()));
    }

    @Override
    public void commitChanges(Neuron neuron) {
        if (!(neuron.getUpdateRule() instanceof MorrisLecarRule)) {
            neuron.setUpdateRule(prototypeRule.deepCopy());
        }

        writeValuesToRules(Collections.singletonList(neuron));
    }

    @Override
    public void commitChanges(List<Neuron> neurons) {
        if (isReplace()) {
            NeuronUpdateRule neuronRef = prototypeRule.deepCopy();
            for (Neuron n : neurons) {
                n.setUpdateRule(neuronRef.deepCopy());
            }
        }
        writeValuesToRules(neurons);
    }

    @Override
    protected void writeValuesToRules(List<Neuron> neurons) {
        int numNeurons = neurons.size();
        // G_Ca
        double gCa = Utils.doubleParsable(tfG_Ca);
        if (!Double.isNaN(gCa)) {
            for (int i = 0; i < numNeurons; i++) {
                ((MorrisLecarRule) neurons.get(i).getUpdateRule())
                        .setG_Ca(gCa);
            }
        }
        // G_K
        double gK = Utils.doubleParsable(tfG_K);
        if (!Double.isNaN(gK)) {
            for (int i = 0; i < numNeurons; i++) {
                ((MorrisLecarRule) neurons.get(i).getUpdateRule())
                        .setG_K(gK);
            }
        }

        // G_L
        double gL = Utils.doubleParsable(tfG_L);
        if (!Double.isNaN(gK)) {
            for (int i = 0; i < numNeurons; i++) {
                ((MorrisLecarRule) neurons.get(i).getUpdateRule())
                        .setG_L(gL);
            }
        }

        // vRest_Ca
        double vRC = Utils.doubleParsable(tfVRest_Ca);
        if (!Double.isNaN(vRC)) {
            for (int i = 0; i < numNeurons; i++) {
                ((MorrisLecarRule) neurons.get(i).getUpdateRule())
                        .setvRest_Ca(vRC);
            }
        }

        // vRest_k
        double vRk = Utils.doubleParsable(tfvRest_k);
        if (!Double.isNaN(vRk)) {
            for (int i = 0; i < numNeurons; i++) {
                ((MorrisLecarRule) neurons.get(i).getUpdateRule())
                        .setvRest_k(vRk);
            }
        }

        // vRest_L
        double vRL = Utils.doubleParsable(tfVRest_L);
        if (!Double.isNaN(vRL)) {
            for (int i = 0; i < numNeurons; i++) {
                ((MorrisLecarRule) neurons.get(i).getUpdateRule())
                        .setvRest_L(vRL);
            }
        }

        // cMembrane
        double cM = Utils.doubleParsable(tfCMembrane);
        if (!Double.isNaN(cM)) {
            for (int i = 0; i < numNeurons; i++) {
                ((MorrisLecarRule) neurons.get(i).getUpdateRule())
                        .setcMembrane(cM);
            }
        }

        // v_m1
        double vM1 = Utils.doubleParsable(tfV_M1);
        if (!Double.isNaN(vM1)) {
            for (int i = 0; i < numNeurons; i++) {
                ((MorrisLecarRule) neurons.get(i).getUpdateRule())
                        .setV_m1(vM1);
            }
        }

        // v_m2
        double vM2 = Utils.doubleParsable(tfV_M2);
        if (!Double.isNaN(vM2)) {
            for (int i = 0; i < numNeurons; i++) {
                ((MorrisLecarRule) neurons.get(i).getUpdateRule())
                        .setV_m2(vM2);
            }
        }

        // v_w1
        double vW1 = Utils.doubleParsable(tfV_W1);
        if (!Double.isNaN(vW1)) {
            for (int i = 0; i < numNeurons; i++) {
                ((MorrisLecarRule) neurons.get(i).getUpdateRule())
                        .setV_w1(vW1);
            }
        }

        // v_w2
        double vW2 = Utils.doubleParsable(tfV_W2);
        if (!Double.isNaN(vW2)) {
            for (int i = 0; i < numNeurons; i++) {
                ((MorrisLecarRule) neurons.get(i).getUpdateRule())
                        .setV_w2(vW2);
            }
        }

        // w_k
        double wK = Utils.doubleParsable(tfW_K);
        if (!Double.isNaN(wK)) {
            for (int i = 0; i < numNeurons; i++) {
                ((MorrisLecarRule) neurons.get(i).getUpdateRule())
                        .setW_K(wK);
            }
        }

        // phi
        double phi = Utils.doubleParsable(tfPhi);
        if (!Double.isNaN(phi)) {
            for (int i = 0; i < numNeurons; i++) {
                ((MorrisLecarRule) neurons.get(i).getUpdateRule())
                        .setPhi(phi);
            }
        }

        // i_bg
        double iBG = Utils.doubleParsable(tfI_Bg);
        if (!Double.isNaN(iBG)) {
            for (int i = 0; i < numNeurons; i++) {
                ((MorrisLecarRule) neurons.get(i).getUpdateRule())
                        .setI_bg(iBG);
            }
        }

        // threshold
        double thr = Utils.doubleParsable(tfThreshold);
        if (!Double.isNaN(thr)) {
            for (int i = 0; i < numNeurons; i++) {
                ((MorrisLecarRule) neurons.get(i).getUpdateRule())
                        .setThreshold(thr);
            }
        }
    }

    @Override
    protected NeuronUpdateRule getPrototypeRule() {
        return prototypeRule.deepCopy();
    }
}
