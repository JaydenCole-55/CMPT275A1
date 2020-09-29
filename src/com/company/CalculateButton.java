// Author: Jayden Cole
// Date: Sept 27, 2020

package com.company;

import javax.swing.*;
import java.awt.*;

// Wrote my own calculate button
public class CalculateButton extends JButton {
    // Constructor
    CalculateButton(String text){
        this.setText(text);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        String name = "Go_Button";
    }
}
