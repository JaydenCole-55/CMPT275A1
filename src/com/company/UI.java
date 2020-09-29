// Author: Jayden Cole
// Date: Sept 27, 2020

package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.Math.*;

public class UI extends Frame implements ActionListener {
    private
    // Table headers and variables
    final String[] colNames = {"Component", "Units", "Min value", "Max Value", "Your Value (change this!)"};
    final String[] variables = {"V", "C", "L", "R", "t_end", "t_step"};

    // Table row and column values
    final int userCol = 4;
    final int voltageRow = 1;
    final int capacitanceRow = 2;
    final int inductanceRow = 3;
    final int resistanceRow = 4;
    final int tStopRow = 5;
    final int tStepRow = 6;

    // Set limits for parameters
    final double Vmin = 4.0;
    final double Vmax = 15.0;
    final double Cmin = 1e-9;
    final double Cmax = 1e-7;
    final double Lmin = 1e-3;
    final double Lmax = 1e-1;
    final double Rmin = 5;
    final double Rmax = 10;
    final double tEndMin = 0;
    final double tEndMax = 1.23456e300;
    final double tStepMin = 0;
    final double tStepMax = 1.23456e300;

    // Initialize example user values, will intialize the user table column
    String VuserInit = "10";
    String CuserInit = "1e-8";
    String LuserInit = "1e-2";
    String RuserInit = "7.5";
    String tEndUserInit = "0.001";
    String tStepUserInit = "0.000001";

    // These will store the actual values the user inputs
    double VuserInput;
    double CuserInput;
    double LuserInput;
    double RuserInput;
    double tEndUserInput;
    double tStepUserInput;

    // Circuit image variables
    boolean imageLoaded = true;
    BufferedImage circuitPNG;

    // Create UI elements
    private JTable table;
    JLabel errorMes;

    // Store pathing for logging file
    String cwd = System.getProperty("user.dir");
    JTextArea userPath;
    JTextArea userFile;
    String userFileStr;
    String userPathStr;
    OutputFile log;

    // Store time intervals and q(t) values for the circuit
    double[] timeIntervals;
    double[] qVals;

    // Constructor, sets up the UI
    public UI() {
        // Set frame details
        setSize(600, 600);
        setUpDisplay();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setVisible(true);
    }

    // Implement the UI
    public void setUpDisplay() {
        // Set the title
        JLabel title = new JLabel("Simulate this Electric Circuit!");
        title.setFont(new Font("Verdana", Font.PLAIN, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);

        // Try to read in circuit image
        try {
            circuitPNG = ImageIO.read(this.getClass().getResource("Circuit.png"));
        } catch (IOException e) {
            imageLoaded = false;
        }
        if (imageLoaded) {
            JLabel picLabel = new JLabel(new ImageIcon(circuitPNG));
            picLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(picLabel);
        }

        // Create table to show parameters
        Object[][] data = {
                colNames,
                {"V", "Volts", Vmin, Vmax, VuserInit},
                {"C", "Farads", Cmin, Cmax, CuserInit},
                {"L", "Henrys", Lmin, Lmax, LuserInit},
                {"R", "Ohms", Rmin, Rmax, RuserInit},
                {"t_end", "Seconds", tEndMin, tEndMax, tEndUserInit},
                {"t_step", "Seconds", tStepMin, tStepMax, tStepUserInit}
        };
        this.table = new JTable(data, colNames);
        add(table);

        // Add button for user to push to execute the calculations of q(t)
        CalculateButton calculateButton = new CalculateButton("Calculate");
        calculateButton.addActionListener(this);
        add(calculateButton);

        // Add a Label saying where the user can save their data
        JLabel filePathInfo = new JLabel("Enter on the next line where you would like to store your data file. " +
                "Ensure it is a directory that already exists");
        filePathInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(filePathInfo);

        // User types in where they need to store their file
        userPath = new JTextArea(cwd);
        userPath.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(userPath);

        // Label to ask for their specific file name
        JLabel fileName = new JLabel("Enter on the next line the specific file name you would like for your data file, ensure it ends in .txt");
        fileName.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(fileName);

        // This is where the user types in what file name they want
        userFile = new JTextArea("datafile.txt");
        userFile.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(userFile);
    }

    // Actions when "Calculate Button" is clicked
    @Override
    public void actionPerformed(ActionEvent e) {

        // Check user value inputs
        boolean[] goodInput = {false, false, false, false, false, false};
        int counter = 0;
        boolean sendErrMes = false;

        for (String userVariable : variables) {
            if (inUserRange(userVariable)) {
                goodInput[counter] = true;
            } else {
                sendErrMes = true;
            }
            counter++;
        }

        // Send error message for bad inputs
        if (sendErrMes) {
            inputErrorMes(goodInput, variables);
        }

        // Now calculate q(t)
        qVals = calculateQ();

        // Get the path and file name for the output file
        userPathStr = userPath.getText();
        userFileStr = userFile.getText();

        Path fullUserPath = Paths.get(userPathStr, userFileStr);

        try {
            log = new OutputFile(this.timeIntervals, this.qVals, fullUserPath);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        if( !log.getFileCreated() ){
            boolean[] fileCreated = {false};
            String[] badInput = {"input file"};
            inputErrorMes(fileCreated, badInput);
            sendErrMes = true; // Detect that something is wrong with the inputs
        }

        // Now graph q(t) as long as no errors were detected
        if(!sendErrMes) {
            plotQ();
        }
    }

    // A user value is outside of bounds, send the error message
    public void inputErrorMes(boolean[] goodInputs, String[] variableNames) {
        String errorString = "";
        int counter = 0;

        for (String variable : variableNames) {
            if (!goodInputs[counter]) {
                errorString += "Error in input: " + variable + ". Please enter a valid input.\n ";
            }
            counter++;
        }

        errorMes = new JLabel(errorString);
        errorMes.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(errorMes);
        setVisible(true);
    }

    // Check user values are in range
    public boolean inUserRange(String userVariable) {
        if (userVariable == "V") {
            // Get Vuser from table
            VuserInput = Double.parseDouble((String) this.table.getModel().getValueAt(voltageRow, userCol));
            return (VuserInput >= Vmin && VuserInput <= Vmax);
        } else if (userVariable == "C") {
            // Get Cuser from table
            CuserInput = Double.parseDouble((String) this.table.getModel().getValueAt(capacitanceRow, userCol));
            return (CuserInput >= Cmin && CuserInput <= Cmax);
        } else if (userVariable == "L") {
            // Get Luser from table
            LuserInput = Double.parseDouble((String) this.table.getModel().getValueAt(inductanceRow, userCol));
            return (LuserInput >= Lmin && LuserInput <= Lmax);
        } else if (userVariable == "R") {
            // Get Ruser from table
            RuserInput = Double.parseDouble((String) this.table.getModel().getValueAt(resistanceRow, userCol));
            return (RuserInput >= Rmin && RuserInput <= Rmax);
        } else if (userVariable == "t_end") {
            // Get User defined t_end from table
            tEndUserInput = Double.parseDouble((String) this.table.getModel().getValueAt(tStopRow, userCol));
            return (tEndUserInput > tEndMin && tEndUserInput <= tEndMax);
        } else if (userVariable == "t_step") {
            // Get User defined t_end from table
            tStepUserInput = Double.parseDouble((String) this.table.getModel().getValueAt(tStepRow, userCol));
            return (tStepUserInput > tStepMin && tStepUserInput <= tStepMax);
        } else {
            // Error, should never be here. Print to system output
            System.out.println("Error: user input check failed");
            return (false);
        }
    }

    // Calculates all the values of q(t)
    public double[] calculateQ(){
        // Initialize array to hold q(t) values
        int timeIntervals  = (int) (ceil( tEndUserInput / tStepUserInput ) + 1);
        this.timeIntervals = new double[timeIntervals];
        double[] qVals     = new double[timeIntervals];

        // Split the calculation into multiple steps
        double coeff;
        double innerCos;

        // Rename variables for clarity in the equation
        double V = VuserInput;
        double C = CuserInput;
        double L = LuserInput;
        double R = RuserInput;

        // Iterate all t iterations until finished
        double t = 0.0;
        for(int i = 0; i < timeIntervals; t += tStepUserInput, i++){
            coeff = V * C * exp( ( -1 )*R /( 2*L ) * t );
            innerCos   = t * sqrt( (1/(L*C)) - Math.pow(R/(2*L), 2) );

            qVals[i] = coeff * cos(innerCos);
            this.timeIntervals[i] = t;
        }

        return qVals;
    }

    // Plots Q(t) over time
    public void plotQ(){
        LineChart_AWT chart = new LineChart_AWT(
                "Plotted values of Q(t) over time",
                "Electric Circuit Simulation",
                this.timeIntervals,
                this.qVals);

        chart.pack( );
        chart.setVisible( true );
    }
}