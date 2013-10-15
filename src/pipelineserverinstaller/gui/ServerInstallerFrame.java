package pipelineserverinstaller.gui;

import pipelineserverinstaller.gui.panels.LicensePanel;
import pipelineserverinstaller.gui.panels.AbstractStepPanel;
import pipelineserverinstaller.gui.panels.IntroductionPanel;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import pipelineserverinstaller.Configuration;
import pipelineserverinstaller.Constants;
import pipelineserverinstaller.NativeCalls;
import pipelineserverinstaller.gui.panels.BioinformaticsToolsConfigurationPanel;
import pipelineserverinstaller.gui.panels.InstallPanel;
import pipelineserverinstaller.gui.panels.ServerConfigurationPanel;
import pipelineserverinstaller.gui.panels.GeneralConfigurationPanel;
import pipelineserverinstaller.gui.panels.NIToolsConfigurationPanel;
import pipelineserverinstaller.gui.panels.SGEConfigurationPanel;
import pipelineserverinstaller.gui.panels.SummaryPanel;

/**
 *
 * @author Zhizhong Liu
 */
public class ServerInstallerFrame extends JFrame {
    // just to make the compiler stop complaining
    static final long serialVersionUID = 1L;

    private String north = SpringLayout.NORTH;
    private String south = SpringLayout.SOUTH;
    private String west = SpringLayout.WEST;
    private String east = SpringLayout.EAST;
    
    private final int panelMargin = 20;
    private final int dist = 5;
    private final int vDist = 8;

    public static final int DEFAULT_WIDTH   = 820;
    public static final int DEFAULT_HEIGHT  = 650;

    private JLabel titleIconLabel;
    private JLabel titleLabel;
    private JLabel subTitleLabel;
    private JLabel versionLabel;
    private JLabel stepsLabel;
    private List<String> stepsList;
    private JSeparator topSeparator;
    private JSeparator sideSeparator;

    private List<AbstractStepPanel> stepsPanelList;
    private AbstractStepPanel currentPanel;
    private int currentStepIndex;


    private String lastPressed;

    private JButton helpButton;
    private JButton cancelButton;
    private JButton backButton;
    private JButton nextButton;
    private JButton finishButton;
    
    private JLabel copyrightLabel;

    private SpringLayout layout;
    private static ServerInstallerFrame defaultFrame;

    private JFileChooser fileChooser;
    private FileDialog fileDialogOSX;

    private static ServerInstallerFrame serverInstallerFrame;

    /** Creates a new instance of ServerInstallerFrame */
    public ServerInstallerFrame() {
        super("LONI Pipeline Server Installation");

        serverInstallerFrame = this;

        initComponents();
        initLayout();
        initListeners();

        displaySteps();
    }

    public static ServerInstallerFrame getServerInstallerFrame() {
        return serverInstallerFrame;
    }


    public void redirect() {
        if ( lastPressed == null )
            return;

        if ( lastPressed.equals("back"))
            backButtonAction();
        else if ( lastPressed.equals("next"))
            nextButtonAction();
    }


    private void initComponents() {
        defaultFrame = this;

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        Dimension screenSize = getToolkit().getScreenSize();
        setLocation((screenSize.width  - DEFAULT_WIDTH) / 2, (screenSize.height - DEFAULT_HEIGHT)/ 2 - 15);

        ImageIcon titleIcon = new ImageIcon(this.getClass().getResource("/graphics/faucet_only.png"));
        titleIconLabel = new JLabel(titleIcon);
        titleLabel = ComponentFactory.label("LONI Pipeline");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));

        subTitleLabel = ComponentFactory.label("Server Installer");
        subTitleLabel.setFont(new Font("Serif", Font.BOLD, 14));

        versionLabel = ComponentFactory.label("version " + Constants.installerVersion);
        versionLabel.setForeground(Color.DARK_GRAY);

        topSeparator = new JSeparator();
        topSeparator.setMaximumSize(new Dimension(Short.MAX_VALUE, 10));
        sideSeparator = new JSeparator(JSeparator.VERTICAL);
        sideSeparator.setMaximumSize(new Dimension(10, Short.MAX_VALUE));

        stepsList = new LinkedList<String>();
        stepsList.add("Introduction");
        stepsList.add("License");
        stepsList.add("General Configuration");
        stepsList.add("SGE Configuration");
        stepsList.add("Server Configuration");
        stepsList.add("NI Tools Configuration");
        stepsList.add("BI Tools Configuration");
        stepsList.add("Install");
        stepsList.add("Summary");

        stepsLabel = new JLabel("");

        stepsPanelList = new LinkedList<AbstractStepPanel>();
        currentStepIndex = 1;

        stepsPanelList.add(new IntroductionPanel());
        stepsPanelList.add(new LicensePanel());
        stepsPanelList.add(new GeneralConfigurationPanel());
        stepsPanelList.add(new SGEConfigurationPanel());
        stepsPanelList.add(new ServerConfigurationPanel());
        stepsPanelList.add(new NIToolsConfigurationPanel());
        stepsPanelList.add(new BioinformaticsToolsConfigurationPanel());
        stepsPanelList.add(new InstallPanel(false));
        stepsPanelList.add(new SummaryPanel());

        helpButton = ComponentFactory.button("Help");
        cancelButton = ComponentFactory.button("Cancel");
        backButton = ComponentFactory.button("< Back");
        nextButton = ComponentFactory.button("Next >");
        finishButton = ComponentFactory.button("Finish");
        backButton.setEnabled(false);
        finishButton.setEnabled(false);
        
        
        StringBuilder sb  =  new StringBuilder("<html>");
        
        sb.append("<font color=\"gray\" size=\"3\">");
        sb.append("&copy; ");
        sb.append(Constants.copyrightText);
        sb.append("</font>");
        
        copyrightLabel = ComponentFactory.label(sb.toString());
    }

    private void displaySteps() {
        setNextEnabled(false);
        StringBuilder sb = new StringBuilder("<html><br><br>");
        int counter = 1;
        for(String s : stepsList) {
            sb.append("<font color=\"");
            
            if(counter < currentStepIndex) {
                if (stepsPanelList.get(counter - 1) instanceof ServerConfigurationPanel) {
                   if (!Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_PIPELINE)) )
                       sb.append("gray");
                } else if  (stepsPanelList.get(counter - 1) instanceof SGEConfigurationPanel ) {
                    if (!Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_SGE)) )
                       sb.append("gray");
                } else if  (stepsPanelList.get(counter - 1) instanceof NIToolsConfigurationPanel ) {
                    if (!Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_NI_TOOLS)) )
                       sb.append("gray");
                } else if  (stepsPanelList.get(counter - 1) instanceof BioinformaticsToolsConfigurationPanel ) {
                    if (!Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_BI_TOOLS)) )
                       sb.append("gray");
                } else sb.append("darkGray");
            } else if ( counter == currentStepIndex ) {
                sb.append("#273791");
            } else {
                sb.append("gray");
            }
            sb.append("\">");

            if ( counter == currentStepIndex )
                sb.append("<b>");

            sb.append(counter);
            sb.append(". ");
            sb.append(s);

            if ( counter == currentStepIndex )
                sb.append("</b>");

            sb.append("</font><br><br>");

            counter++;
        }
        sb.append("</html>");
        stepsLabel.setText(sb.toString());

        if(currentPanel!=null)
            remove(currentPanel);
        AbstractStepPanel panel = stepsPanelList.get(currentStepIndex-1);
        add(panel);

        

        layout.getConstraints(panel).setConstraint(south, Spring.sum(Spring.constant(-vDist), layout.getConstraint(north, helpButton)));
        layout.getConstraints(panel).setConstraint(east, Spring.sum(Spring.constant(-panelMargin), layout.getConstraint(east, getContentPane())));
        layout.putConstraint(north, panel, vDist, south, topSeparator);
        layout.putConstraint(west, panel, dist, east, sideSeparator);
        currentPanel = panel;

        panel.panelActivated();
        validate();
        repaint();
    }


    private void initLayout() {
        layout = new SpringLayout();
        this.setLayout(layout);
        Container pane = this.getContentPane();

        add(titleIconLabel);
        layout.putConstraint(north, titleIconLabel, panelMargin, north, pane);
        layout.putConstraint(west, titleIconLabel, panelMargin*2, west, pane);

        add(titleLabel);
        layout.putConstraint(south, titleLabel, -2 * vDist, south, titleIconLabel);
        layout.putConstraint(west, titleLabel, dist, west, topSeparator);

        add(subTitleLabel);
        layout.putConstraint(south, subTitleLabel, 0, south, versionLabel);
        layout.putConstraint(east, subTitleLabel, -dist * 2, west, versionLabel);

        add(versionLabel);
        layout.putConstraint(south, versionLabel, -dist, north, topSeparator);
        layout.putConstraint(east, versionLabel, -panelMargin, east, pane);

        add(topSeparator);
        layout.getConstraints(topSeparator).setConstraint(east, Spring.sum(Spring.constant(-panelMargin), layout.getConstraint(east, pane)));
        layout.putConstraint(north, topSeparator, vDist, south, titleLabel);
        layout.putConstraint(west, topSeparator, 0, west, sideSeparator);

        add(stepsLabel);
        layout.putConstraint(north, stepsLabel, vDist, south, topSeparator);
        layout.putConstraint(west, stepsLabel, panelMargin, west, pane);

        add(sideSeparator);
        layout.getConstraints(sideSeparator).setConstraint(south, Spring.sum(Spring.constant(-vDist), layout.getConstraint(north, helpButton)));
        layout.putConstraint(north, sideSeparator, vDist, south, titleLabel);
        layout.putConstraint(west, sideSeparator, dist, east, stepsLabel);
        
        add(copyrightLabel);
        layout.putConstraint(south, copyrightLabel, -5, south, pane);
        layout.putConstraint(west, copyrightLabel, dist, west, pane);

        add(helpButton);
        layout.putConstraint(south, helpButton, -panelMargin, north, copyrightLabel);
        layout.putConstraint(west, helpButton, panelMargin*2, west, pane);

        add(cancelButton);
        layout.putConstraint(south, cancelButton, 0, south, helpButton);
        layout.putConstraint(east, cancelButton, -panelMargin*2, west, backButton);

        add(finishButton);
        layout.putConstraint(south, finishButton, 0, south, helpButton);
        layout.putConstraint(east, finishButton, -panelMargin*2, east, pane);

        add(nextButton);
        layout.putConstraint(south, nextButton, 0, south, helpButton);
        layout.putConstraint(east, nextButton, -dist*2, west, finishButton);

        add(backButton);
        layout.putConstraint(south, backButton, 0, south, helpButton);
        layout.putConstraint(east, backButton, -dist*2, west, nextButton);
        

    }


    private void initListeners() {
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                helpButtonAction();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                backButtonAction();
            }
        });

        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nextButtonAction();
            }
        });

        finishButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                finishButtonAction();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelButtonAction();
            }
        });
    }

    private void helpButtonAction() {
        AbstractStepPanel panel = stepsPanelList.get(currentStepIndex-1);
        NativeCalls.openBrowser(Constants.getHelpURLString("DPSInstaller"+panel.getClass().getSimpleName()));
    }

    private void backButtonAction() {
        // check if it's ok
        if(currentStepIndex<=1 || currentPanel==null)
            return;

        currentPanel.saveUserInput();

        lastPressed = "back";
        currentStepIndex--;
        displaySteps();


        // update button status
        if(!nextButton.isEnabled())
            nextButton.setEnabled(true);
        if(finishButton.isEnabled())
            finishButton.setEnabled(false);
        if(currentStepIndex<=1)
            backButton.setEnabled(false);

        if ( currentStepIndex < stepsPanelList.size() && stepsPanelList.get(currentStepIndex) instanceof InstallPanel )
            nextButton.setText("Install");
        else
            nextButton.setText("Next >");
    }

    public void nextButtonAction() {
        // check if it's ok
        if(currentStepIndex>=stepsPanelList.size() || currentPanel==null
                || !currentPanel.checkUserInput())
            return;

        currentPanel.saveUserInput();

        lastPressed = "next";
        currentStepIndex++;
        displaySteps();
        
        // update button status
        if(!backButton.isEnabled())
            backButton.setEnabled(true);
        
        if(currentStepIndex>=stepsPanelList.size() - 1) {
            nextButton.setEnabled(false);
            backButton.setEnabled(false);
            if ( currentStepIndex>=stepsPanelList.size() ) {
                cancelButton.setEnabled(false);
                finishButton.setEnabled(true);
            }
        } else {
            if ( stepsPanelList.get(currentStepIndex) instanceof InstallPanel )
                nextButton.setText("Install");
            else if ( stepsPanelList.get(currentStepIndex) instanceof ServerConfigurationPanel ) {
                if ( !Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_PIPELINE)) )
                    if ( !Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_NI_TOOLS)) && !Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_BI_TOOLS)))
                        nextButton.setText("Install");
                    else
                        nextButton.setText("Next >");
            } else if ( stepsPanelList.get(currentStepIndex) instanceof NIToolsConfigurationPanel ) {
                if ( !Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_NI_TOOLS)) && !Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_BI_TOOLS)))
                    nextButton.setText("Install");
                else
                    nextButton.setText("Next >");
            }
            else
                nextButton.setText("Next >");
        }
    }


    public void setNextEnabled(boolean enabled) {
        nextButton.setEnabled(enabled);
    }
    
    public void setBackEnabled(boolean enabled) {
        backButton.setEnabled(enabled);
    }


    public void postInstall() {
        String plUser = Configuration.getConfig(Configuration.CONFIG_PIPELINE_USER);
        String startServer = Configuration.getConfig(Configuration.START_PIPELINE_SERVER);
        String configureServer = Configuration.getConfig(Configuration.CONFIGURE_PIPELINE_SERVER);
        String startClient = Configuration.getConfig(Configuration.START_PIPELINE_CLIENT);

        boolean needsServerStart = startServer != null ? Boolean.parseBoolean(startServer) : false;
        boolean needsConfigureServer = configureServer != null ? Boolean.parseBoolean(configureServer) : false;
        boolean needsClientStart = startClient != null ? Boolean.parseBoolean(startClient) : false;

        if ( needsClientStart || needsConfigureServer || needsServerStart ) {
            StringBuilder cmd = new StringBuilder(System.getProperty("user.dir"));
            cmd.append("/install_files/postInstall.sh ");
            cmd.append(plUser);
            cmd.append(" ");
            cmd.append(Configuration.getConfig(Configuration.CONFIG_PIPELINE_LOCATION));
            cmd.append(" ");
            cmd.append(String.valueOf(needsServerStart));
            cmd.append(" ");
            cmd.append(String.valueOf(needsConfigureServer));
            cmd.append(" ");
            cmd.append(String.valueOf(needsClientStart));

            if ( Configuration.getConfig(Configuration.START_PIPELINE_CLIENT_ARGUMENTS) != null ) {
                cmd.append(" ");
                cmd.append(Configuration.getConfig(Configuration.START_PIPELINE_CLIENT_ARGUMENTS));
            }

            Process p = null;
            try {
                p = Runtime.getRuntime().exec(cmd.toString());
                
                if ( !needsConfigureServer ) {
                    StreamReader isr = new StreamReader(p.getInputStream(), 1);
                    StreamReader esr = new StreamReader(p.getErrorStream(), 2);

                    isr.start();
                    esr.start();

                    p.waitFor();
                }

            } catch ( Exception ex ) {
                ex.printStackTrace();
            } finally {
                if ( p != null && !needsConfigureServer )
                    NativeCalls.releaseProcess(p);

                if ( needsConfigureServer )
                    Configuration.setConfig(Configuration.CONFIGURE_PIPELINE_SERVER, "false");
            }
        }

    }
    
    public void finishButtonAction() {
        // check if it's ok
        if(currentPanel==null || !currentPanel.checkUserInput())
            return;
        
        postInstall();

        System.exit(0);
    }


     private class StreamReader extends Thread {
        InputStream inputStream;
        private boolean isErrorStream;
        public StreamReader(InputStream inputStream, int streamID) {
            this.inputStream = inputStream;
            if ( streamID == 2 )
                isErrorStream = true;
            this.setName("StreamReader");
        }

        public void run() {

            byte [] buff = new byte[1024];
            try {
                while ( inputStream.read(buff) > 0 ) {
                    String t = new String(buff);

                    String [] tokens = t.split("\n");


                    for ( String s : tokens ) {


                        //else {
                            if ( isErrorStream )
                                System.out.println("ERROR-STREAM: " + s);
                            else
                                System.out.println(s);
                        //}
                    }
                    Arrays.fill(buff, (byte)0);
                }

                inputStream.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }


            if (isErrorStream)
                System.out.println("Output Stream finished.");
            else
                System.out.println("Error Stream finished.");

        }
    }


      
    private void cancelButtonAction() {
         if ( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this,"Are you sure that you want to cancel the installation ?", "Cancel Installation", JOptionPane.YES_NO_OPTION)) {
            System.exit(0);
         }
    }
    
}
