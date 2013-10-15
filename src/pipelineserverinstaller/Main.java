/*
 * Main.java
 *
 * Created on Oct 6, 2010, 9:35:32 AM
 */
package pipelineserverinstaller;

import pipelineserverinstaller.gui.ServerInstallerFrame;

/**
 *
 * @author Zhizhong Liu
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (Constants.isOSX) {
            // moves the menu bar out of the main frame, and into the top of the screen
            System.setProperty("apple.laf.useScreenMenuBar", "true");

            /* adds a border to the bottom of the screen, so buttons are not covered
             * by the OS X resize graphic at the bottom left of every window */
            System.setProperty("apple.awt.showGrowBox", "true");

            // paints the inside of the window when resizing, instead of just a box
            System.setProperty("com.apple.mrj.application.live-resize", "true");

            // sets the application name for OS X
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "LONI Pipeline Server Install");
        }

        if (args.length > 0) {
            if (args[0].equals("-auto")) {
                // automatic installation mode
                String configFile = args[1];
                AutomaticInstaller autoInstaller = new AutomaticInstaller(configFile);
                autoInstaller.run();
            }
        } else {
            ServerInstallerFrame sif = new ServerInstallerFrame();
            sif.setVisible(true);
        }
    }

}
