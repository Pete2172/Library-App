package com.company;

import frame.objects.MainWindow;

/***
 * Main class creates a window of programmed application
 */

public class Main {

    public static void main(String[] args) {
	// write your code here
        MainWindow win = new MainWindow();
        win.setResizable(true);
        win.setSize(1300, 700);
        win.setTitle("Library App");
        win.setVisible(true);
        win.setLocationRelativeTo(null);
        win.pack();
    }
}
