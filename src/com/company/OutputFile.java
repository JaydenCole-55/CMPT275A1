// Author: Jayden Cole
// Date: Sept 27, 2020

package com.company;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class OutputFile {
    FileWriter myWriter;
    boolean fileCreated = true;

    // Constructor
    public OutputFile(double[] timeIntervals, double[] qVals, Path fulLFilePath) throws IOException {
        // Creates the output file in the constructor
        try {
            // Create file and get a file writer object to it
            File log = new File(String.valueOf(fulLFilePath));
            log.createNewFile();
            myWriter = new FileWriter(log);
        } catch (IOException e) {
            // File was not created
            fileCreated = false;
        }

        // Add q(t) values to file if it was created
        if (fileCreated) {
            // Write header for output file
            myWriter.write("Time (s) : Q Value of Capacitor\n\n");

            // Write data for output file
            for (int i = 0; i < timeIntervals.length; i++) {
                myWriter.write(timeIntervals[i] + ": " + qVals[i] + "\n");
            }
            myWriter.close();
        }
    }

    // Getter to see if the file was created
    public boolean getFileCreated(){
        return fileCreated;
    }
}
