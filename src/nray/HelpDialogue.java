/*
 *  Copyright 2014 Derek Alexander
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/*
 * HelpDialogue.java
 *
 * A simple help dialog which is probably never up to date with the current options available to the keyListener
 * probably should expose the options as a collection of sliders and radio buttons in a separate frame rather than
 * arcane keybindings.
 */

package nray;

/**
 *
 * @author dalexander
 */
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class HelpDialogue {
    
    /** Creates a new instance of HelpDialogue */
    public HelpDialogue() {
    }

    public static Frame frame = null;

    public static void setFrame(Frame f){
        frame = f;
    }
    
    public static void display(){
            //Thank you Sun for the following code from the dialogue demo found at:
            //http://java.sun.com/docs/books/tutorial/uiswing/components/examples/DialogDemo.java

            if(frame == null) return;

            //Create the dialog.
            final JDialog dialog = new JDialog(frame,
                                               "Command help");

            //Add contents to it. It must have a close button,
            //since some L&Fs (notably Java/Metal) don't provide one
            //in the window decorations for dialogs.
            JLabel label = new JLabel("<html><p align=center>"
                + "?: Bring up this help box.<br>"
                + "Arrow keys: move camera around.<br>" +
                  "Hold down the left mouse button " +
                  "and move the mouse to look around<br>" +
                    "R toggles raycast mode<br>" +
                    "B renders bounding volumes<br>" +
                    "p display photon hits<br>" +
                    "P toggles kd-tree photon gathers<br>" +
                    "c and x display intersection heatmaps<br>" +
                    "u displays intersected UVs<br>" +
                    "n displays normals<br>" +
                    "o displays time per ray<br>" +
                    "r enables reflections" +
                    "M toggles multi-threaded/single-threaded tracing" +
                    "L toggles nieve specularless phong and photon lighting" +
                    "1 toggles rotating the light around the origin" +
                    "2 toggles forcing the light to follow the camera");
            label.setHorizontalAlignment(JLabel.CENTER);
            Font font = label.getFont();
            label.setFont(label.getFont().deriveFont(font.PLAIN,
                                                     14.0f));

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                    dialog.dispose();
                }
            });
            JPanel closePanel = new JPanel();
            closePanel.setLayout(new BoxLayout(closePanel,
                                               BoxLayout.LINE_AXIS));
            closePanel.add(Box.createHorizontalGlue());
            closePanel.add(closeButton);
            closePanel.setBorder(BorderFactory.
                createEmptyBorder(0,0,5,5));

            JPanel contentPane = new JPanel(new BorderLayout());
            contentPane.add(label, BorderLayout.CENTER);
            contentPane.add(closePanel, BorderLayout.PAGE_END);
            contentPane.setOpaque(true);
            dialog.setContentPane(contentPane);

            //Show it.
            dialog.setSize(new Dimension(400, 350));
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
    }
    
}
