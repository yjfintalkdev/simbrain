/*
 * Copyright (C) 2005,2007 The Authors. See http://www.simbrain.net/credits This
 * program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.simbrain.plot.histogram;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.RefineryUtilities;

/**
 * A general purpose histogram utility panel. Made with synapses in particular
 * in mind, but broadly applicable. Supports multiple simultaneous data sets
 * represented by different colors, partially transparent to make overlap
 * visible. The histogram takes the form of a chart supported by a panel which
 * is then placed within the final panel. This panel supports dynamically
 * changing the number of histogram bins graphically, but only logically 
 * supports the altering of data series, relying on other classes for graphical
 * representations of such.
 * 
 * @author Zach Tosi
 *
 */
public class Histogram extends JPanel{

	/** The default number of bins. **/
	private static final int DEFAULT_BINS = 10;
	
	/** 
	 * The default (and maximum, unless otherwise changed) number of colors
	 * available to different data sets. Implicitly this is the default
	 * maximum number of supported data sets. 
	 **/
	private static final int DEFAULT_NUM_DATASETS = 4;
	
	/** The default preferred height. */
	private static final int DEFAULT_PREF_HEIGHT = 300;
	
	/** The default preferred width. */
	private static final int DEFAULT_PREF_WIDTH = 500;
	
	/** The grid width of this panel, for use by possible parent panels. */
	private static final int GRID_WIDTH = 3;
	
	/** The grid height of this panel, for use by possible parent panels. */
	private static final int GRID_HEIGHT = 4;
	
	/** Constant Alpha value governing transparency of histogram colors. */
	private static final byte ALPHA = -0x50; 
	
	/** The preferred dimensions of the histogram. */
	private Dimension dimPref = new Dimension(DEFAULT_PREF_WIDTH, DEFAULT_PREF_HEIGHT);
	
	/** The color pallet, initialized to the default number of data sets. */
	private Color [] pallet = new Color[DEFAULT_NUM_DATASETS];
	
	/**
	 * 	The standard color pallet, Red, Blue, Green, Yellow
	 *	Colors are represented as a single integer where bits 0-7 
	 *	consist of the blue component, bits 8-15 consist of the green
	 *	component, bits 16-23 consist of the red component, and bits 24-31
	 *	consist of the alpha component. Here bits are shifted to remove the
	 *	default alpha value, then shifted back and replaced with ALPHA.
	 */
	{
		pallet[0] = new Color((Color.RED.getRGB() << 8) >>> 8 | ALPHA << 24,
				true);
		pallet[1] = new Color((Color.BLUE.getRGB() << 8) >>> 8 | ALPHA << 24,
				true);
		pallet[2] = new Color((Color.GREEN.getRGB() << 8) >>> 8 | ALPHA << 24,
				true);
		pallet[3] = new Color((Color.YELLOW.getRGB() << 8) >>> 8 | ALPHA << 24,
				true);
	}
	
	/** The main panel supporting the histogram chart. */
	private JPanel mainPanel;
	
	/** The data set used to generate the histogram. */
	private IntervalXYDataset dataSet;
	
	/** X axis label. */
	private String xAxisName = "";
	
	/** Y axis label. */
	private String yAxisName = "";
	
	/** The title of the histogram. */
	private String title = "";
	
	/** Number of bins label. */
	private JLabel numBinLabel = new JLabel("# of Bins: ");
	
	/** A text field for specifying the number of bins. */
	private JTextField numBins = new JTextField(6);
	
	/** A button for updating the histogram for different numbers of bins. */
	private JButton reHistogram = new JButton("Plot");
	
	/** An array containing all the data series to be plotted. */
	private double [][] data;
	
	/** An array containing the names of each of the data series. */
	private String [] dataNames;
	
	
	private int bins;
	
	/** 
	 * This flag is used for safety, minimum and maximum values are
	 * determined assuming a sorted dataset.
	 */
	private boolean sortedFlag;
	
	/**
	 * Creates a blank histogram.
	 */
	public Histogram() {
		this(null, null, DEFAULT_BINS);		
	}
	
	/**
	 * Creates a blank histogram
	 * @param data
	 * @param dataNames
	 * @param bins
	 */
	public Histogram(double [][] data, String [] dataNames, int bins) {
		this(data, dataNames, bins, "", "", "");
	}
	
	/**
	 * 
	 * @param data
	 * @param dataNames
	 * @param bins
	 * @param title
	 * @param xAxisName
	 * @param yAxisName
	 */
	public Histogram(double [][] data, String [] dataNames, int bins,
			String title, String xAxisName, String yAxisName) {
		this.data = data;
		this.dataNames = dataNames;
		this.bins = bins;
		this.title = title;
		this.xAxisName = xAxisName;
		this.yAxisName = yAxisName;
		numBins.setText(Integer.toString(bins));
		mainPanel = new JPanel();
		JPanel cPanel = new ChartPanel(createHistogram(data, dataNames, bins));
		cPanel.setPreferredSize(dimPref);
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.weightx = 1.0;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.BOTH;
		mainPanel.add(cPanel, g);
		initializeLayout();		
		initializeListeners();
	}
	
	/**
	 * Draws the core histogram implemented as a JFreeChart. In addition to
	 * reflecting the specified inputs, the created histogram will reflect any
	 * changes to the titles of the histogram, x, and y axis. 
	 * @param data the data series being represented, each row is interpreted
	 *  as a distinct series.
	 * @param dataNames the names of each of the data series
	 * @param bins the number of bins to be used for the histogram
	 * @return the core histogram represented as a JFreeChart
	 */
	private JFreeChart createHistogram(double [][] data, String[] dataNames, int bins) {
		
		JFreeChart mainChart = null;
		
		try {		
			dataSet = new HistogramDataset();
			if(data != null){
				
				sanityCheck(bins, data.length);					
				
				for(int i = 0, n = data.length; i < n; i++) {
					sort(data[i]);
					double min = getMinValue(data[i]);
					double max = getMaxValue(data[i]);
					sortedFlag = false;
					((HistogramDataset) dataSet).addSeries(dataNames[i], data[i], bins,
							min, max);
				}	
						
				mainChart = ChartFactory.createHistogram(
						 title, xAxisName, yAxisName, dataSet,
						 PlotOrientation.VERTICAL, true, true, false);		
				XYPlot plot = (XYPlot) mainChart.getPlot();
				plot.setForegroundAlpha(0.75F);
				XYBarRenderer renderer = (XYBarRenderer)plot.getRenderer(); 
				renderer.setDrawBarOutline(false);	
				renderer.setShadowVisible(false);
	
				for(int i = 0, n = data.length; i < n; i++) {
					renderer.setSeriesPaint(i, pallet[i], true);
				}
			} else {
				mainChart = ChartFactory.createHistogram(
						 title, xAxisName, yAxisName, dataSet,
						 PlotOrientation.VERTICAL, true, true, false);	
			}
			
		} catch (IllegalArgumentException iaEx) {	
			iaEx.printStackTrace();
			JOptionPane.showMessageDialog(getParent(), iaEx.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
			
		} catch (IllegalStateException isEx) {
			isEx.printStackTrace();
			JOptionPane.showMessageDialog(getParent(), isEx.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		} 
		
		return mainChart;	
		
	}
	
	/**
	 * Initializes the layout of the histogram panel, including the main
	 * histogram chart, a text field for the number of bins, and a button to
	 * refresh the histogram.
	 */
	private void initializeLayout() {
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = GRID_HEIGHT - 1;
		gbc.gridwidth = GRID_WIDTH;
		
		this.add(mainPanel, gbc);
		
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		
		JPanel binPanel = new JPanel();
		binPanel.setLayout(new FlowLayout());
		binPanel.add(numBinLabel);
		binPanel.add(numBins);
		
		this.add(binPanel, gbc);
		
		gbc.gridx = 2;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		
		this.add(reHistogram, gbc);
		
	}
	
	/**
	 * Initializes all listeners. At present this includes only the plot 
	 * button which redraws the histogram. 
	 */
	private void initializeListeners(){
		
		reHistogram.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					bins = Integer.parseInt(numBins.getText());
					reDraw();			
				} catch (NumberFormatException nfe) {
					nfe.printStackTrace();
					JOptionPane.showMessageDialog(getParent(),
							"Non-Integer number of bins.", "Error",
							JOptionPane.ERROR);
				}
				
			}
			
		});
	
	}
	
	/**
	 * Used to re-draw the histogram based on any changes in bin size, names,
	 * or data series. New series can be dynamically added or removed by
	 * changing those values via the provided setter-functions and then calling
	 * this function.
	 */
	public void reDraw() {
		JPanel cPanel = new ChartPanel(createHistogram(data, dataNames,
				bins));
		cPanel.setPreferredSize(dimPref);
		mainPanel.removeAll();
		GridBagConstraints g = new GridBagConstraints();
		g.weightx = 1.0;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.BOTH;
		mainPanel.add(cPanel, g);
		mainPanel.revalidate();
		mainPanel.repaint();
		getParent().revalidate();
		getParent().repaint();
	}
	
	/**
	 * 
	 * @param bins
	 * @param numDataSeries
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	private void sanityCheck(int bins, int numDataSeries) throws
		IllegalArgumentException, IllegalStateException {
		
		if(numDataSeries > pallet.length) {
			throw new IllegalStateException("Quantity of data sets exceeds" +
					" specified number of available pallet colors.");
		}
		if(bins >= dimPref.getWidth()){
			throw new IllegalArgumentException("Number of bins exceeds" +
					" available pixels.");
		}
		
	}		
	
	/**
	 * Sorts the data set. This must be used before getMaxValue(...) or
	 * getMinValue(...) is used.
	 * @param dataSet
	 */
	private void sort(double dataSet []) {
		Arrays.sort(dataSet);
		sortedFlag = true;
	}
	
	/**
	 * Returns the max value from the data set by returning the last element 
	 * of the sorted data set.
	 * @param dataset
	 * @return
	 */
	private double getMaxValue(double [] dataset) {
		if(sortedFlag) {
			return dataset[dataset.length-1];
		} else {
			throw new IllegalStateException("Unsorted Dataset Exception");
		}
	}
	
	/**
	 * Returns the min value from the data set by returning the first element
	 * of the sorted data set.
	 * @param dataset
	 * @return
	 */
	private double getMinValue(double [] dataset) {
		if(sortedFlag) {
			return dataset[0];
		} else {
			throw new IllegalStateException("Unsorted Dataset Exception");
		}
	}
	
	/****************************************************************
	 					Getters and Setters
	 ****************************************************************/

	public Dimension getDimPref() {
		return dimPref;
	}

	public void setDimPref(Dimension dimPref) {
		this.dimPref = dimPref;
	}

	public IntervalXYDataset getDataSet() {
		return dataSet;
	}

	public void setDataSet(HistogramDataset dataSet) {
		this.dataSet = dataSet;
	}

	public Color[] getPallet() {
		return pallet;
	}

	public void setPallet(Color[] pallet) {
		this.pallet = pallet;
	}

	public String getxAxisName() {
		return xAxisName;
	}

	public void setxAxisName(String xAxisName) {
		this.xAxisName = xAxisName;
	}

	public String getyAxisName() {
		return yAxisName;
	}

	public void setyAxisName(String yAxisName) {
		this.yAxisName = yAxisName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double[][] getData() {
		return data;
	}

	public void setData(double[][] data) {
		this.data = data;
	}

	public String[] getDataNames() {
		return dataNames;
	}

	public void setDataNames(String[] dataNames) {
		this.dataNames = dataNames;
	}
	
	
	public int getBins() {
		return bins;
	}

	/**
	 * Sets the number of bins. Also automatically updates the number of bins
	 * text field, but does NOT redraw the histogram.
	 * @param bins the new number of bins
	 */
	public void setBins(int bins) {
		this.bins = bins;
		numBins.setText(Integer.toString(bins));
	}

	public static int getGridWidth() {
		return GRID_WIDTH;
	}

	public static int getGridHeight() {
		return GRID_HEIGHT;
	}
	
	/****************************************************************
	 							Test Main
	 ****************************************************************/
	public static void main(String[] args) {
		
		   double[][]  r = new double[2][1000];
           Random random = new Random(3141592L);
           for (int i = 0; i < 1000; i++)
                   r[0][i] = random.nextGaussian() + 100;
           for (int i = 0; i < 1000; i++)
               r[1][i] = random.nextGaussian() + 102;
           
           String [] name = new String[2];
           name[0] = "John";
           name[1] = "Jane";
           
           JFrame bob = new JFrame();
           
           Histogram h = new Histogram(r, name, 50);
   		 
           
           bob.setContentPane(h);
           bob.pack();
           RefineryUtilities.centerFrameOnScreen(bob);
           bob.setVisible(true);         

	}

}