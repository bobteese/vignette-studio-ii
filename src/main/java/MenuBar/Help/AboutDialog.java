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


import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;


public class AboutDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final JDialog dialog = this;
    private static final String text = "<html><div style=\"width:350px\">Vignette Studio was created by the Vignette Dreamers as an " +
            "undergraduate senior project at Rochester Institute of Technology. Vignette Studio was created for the " +
            "<a href=\"http://livephoto.rit.edu/\">LivePhoto Physics</a> project. Dr. Robert Teese and Professor Tom Reichlmayr " +
            "sponsored the project, and Dr. Scott Hawker coached the team. Contributors include:<br><br><p>The Vignette Dreamers:<br>Peter-John Rowe, " +
            "Jake Juby, Monir Hossain, Thomas Connors, and Samuel Nelson</p> <br>Additional Developers:<br>Bradley Bensch, " +
            "Nick Fuschino, Rohit Garg, Peter Gyory, Chad Koppes, Trevor Koppes, Nicholas Krzysiak, Joseph Ksiazek, Jen Lamere, Cailin Li, " +
            "Robert Liedka, Nicolas McCurdy, Hector Pieiro II, Chirag Chandrakant Salian, Angel Shiwakoti, Nils Sohn, Brian Soulliard, " +
            "Juntian Tao, Gordon Toth, Devin Warren, Alexander Wilczek, Todd Williams, Brian Wyant, Asmita Hari, Jiwoo Baik and Felix Brink.<br><br>Vignette Studio " +
            "is &copy; 2014-2018, the LivePhoto Physics Project at Rochester Institute of Technology. Vignette Studio is licensed to you under the terms of the GNU General Public License (GPL). " +
            "The terms of the license can be found at <a href=\"http://www.gnu.org/licenses/gpl.html\">http://www.gnu.org/licenses/gpl.html</a></div></html>";

    public AboutDialog(Frame owner) {
        super(owner, "About Vignette Studio", ModalityType.APPLICATION_MODAL);
        setLayout(new GridBagLayout());
        setSize(new Dimension(600, 520));
        setLocationRelativeTo(null);
        setResizable(false);
        JLabel label = new JLabel(text);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(40, 40, 40, 40);
        gbc.anchor = GridBagConstraints.CENTER;
        add(label, gbc);

        String appVersion = "Vignette Studio version 1.0" ;
        JLabel lblAppVersion = new JLabel(appVersion);
        gbc.gridy = 1;
        gbc.insets = new Insets(0,0,0,0);
        add(lblAppVersion, gbc);

        String javaVersion = "Java version " + JavaVersion.getFullVersion();
        JLabel lblJavaVersion = new JLabel(javaVersion);
        gbc.gridy = 2;
        add(lblJavaVersion, gbc);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                dialog.setVisible(false);
            }

        });
        gbc.gridy = 3;
        gbc.insets = new Insets(20,0,40,0);
        add(btnClose, gbc);
    }
}
