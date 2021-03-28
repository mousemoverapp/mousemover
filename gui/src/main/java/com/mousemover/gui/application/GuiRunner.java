package com.mousemover.gui.application;

import com.formdev.flatlaf.FlatDarkLaf;
import com.mousemover.gui.Gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class GuiRunner {


    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel( new FlatDarkLaf() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }

        final String name = getJarName();

        EventQueue.invokeLater(() -> new Gui(name).setVisible(true));
    }

    private static String getJarName() {
        final String path = new File(GuiRunner.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getPath();

        String name;
        try {
            name = path.substring(0, path.indexOf(".jar"));
            name = name.substring(name.lastIndexOf(File.separator) + 1);

        } catch (Exception e) {
            System.out.println("Error getting jar name from path, using default name " + path + " - " + e.getMessage());
            name = "MouseMover " + path;
//            e.printStackTrace();
        }

        return name;
    }
}
