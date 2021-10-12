package MenuBar.Help; /*******************************************************************************
 * Copyright 2013, Rochester Institute of Technology
 * Interactive Video Vignettes Project
 * Robert B. Teese, Project Director
 * rbtsps@rit.edu
 *
 * This file is part of Vignette Studio.
 * Vignette Studio is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Vignette Studio is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Vignette Studio.
 * If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

import javax.swing.*;
import java.awt.Dialog.ModalityType;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class JavaVersion {
    /**
     * The minimum version of the JRE required for Vignette Studio.
     * Should be set to at least the version of the JDK that was used
     * to build the application.
     */
    public static final double JAVA_MIN_VERSION = 1.7;

    //public static String version = Runtime.class.getPackage().getImplementationVersion();
    public static final String version = System.getProperty("java.version");


    /**
     * Get the version in double format that includes only the
     * major and minor version numbers.
     * @return
     */
    public static double getVersion() {
        String[] ver = version.split("\\.");
        return Double.parseDouble (ver[0]+"." + ver[1]);
    }

    /**
     * Get the full Java version string
     * @return
     */
    public static String getFullVersion() {
        return version;
    }

    /**
     * Check that the detected version of Java is at least JAVA_MIN_VERSION
     */
    public static void checkVersion() {
        double version = JavaVersion.getVersion();
        System.out.println("Java version: " + version);
        if (version < JAVA_MIN_VERSION) {
            String message =
                    "<html>Vignette Studio requires Java version 7 or newer. Your machine appears " +
                            "to have an earlier version. If you experience any problems,\n" +
                            "please update your Java platform to the newest version. " +
                            "Go to <a href=\"https://www.java.com/en/download/\">https://www.java.com/en/download/</a> " +
                            "for more details.</html>";
            final JDialog versionDialog = new JDialog(null, "Java Version Requirement", ModalityType.APPLICATION_MODAL);
            versionDialog.setLayout(new GridBagLayout());
            versionDialog.setSize(new Dimension(400, 250));
            versionDialog.setLocationRelativeTo(null);
            versionDialog.setResizable(true);
            JLabel label = new JLabel(message);
            label.setSize(350, 200);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10,10,10,10);
            gbc.anchor = GridBagConstraints.CENTER;
            versionDialog.add(label, gbc);

            JButton btnClose = new JButton("OK");
            btnClose.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    versionDialog.setVisible(false);
                }

            });
            gbc.gridy = 3;
            gbc.insets = new Insets(5, 0, 5, 0);
            versionDialog.add(btnClose, gbc);
            versionDialog.setVisible(true);
        }
    }

}
