// This code is modified from: https://www.tutorialspoint.com/jfreechart/jfreechart_line_chart.htm
// ALl credit goes to the author of that webpage
// Modified Sept. 27, 2020 by Jayden Cole.

package com.company;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;

// Will create a new line chart in a new window
public class LineChart_AWT extends ApplicationFrame {

    // Store q(t) and time interval values
    private final double[] timeIntervals;
    private final double[] qVals;

    // Constructor makes and displays the graph
    public LineChart_AWT(String applicationTitle , String chartTitle, double[] timeIntervals, double[] qVals ) {
        super(applicationTitle);
        this.timeIntervals = timeIntervals;
        this.qVals = qVals;

        // Create graph
        JFreeChart lineChart = ChartFactory.createLineChart(
                chartTitle,
                "Time","Charge",
                createDataset(),
                PlotOrientation.VERTICAL,
                true,true,false);

        // Make new window for the graph
        ChartPanel chartPanel = new ChartPanel( lineChart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
        setContentPane( chartPanel );
    }

    // Create dataset
    private DefaultCategoryDataset createDataset( ) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
        for(int i = 0; i < timeIntervals.length; i++){
            dataset.addValue(qVals[i], "Power", String.valueOf(timeIntervals[i]));
        }

        return dataset;
    }
}