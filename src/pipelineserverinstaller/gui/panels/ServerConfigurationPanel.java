
package pipelineserverinstaller.gui.panels;

import pipelineserverinstaller.Package;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import pipelineserverinstaller.Configuration;
import pipelineserverinstaller.Constants;
import pipelineserverinstaller.NativeCalls;
import pipelineserverinstaller.UserdataUpdater;
import pipelineserverinstaller.gui.ComponentFactory;

/**
 *
 * @author Petros Petrosyan
 */
public class ServerConfigurationPanel extends AbstractStepPanel {

    // just to make the compiler stop complaining
    static final long serialVersionUID = 1L;

    private String north = SpringLayout.NORTH;
    private String south = SpringLayout.SOUTH;
    private String west = SpringLayout.WEST;
    private String east = SpringLayout.EAST;
    
    private SpringLayout layout;

    private final int panelMargin = 20;
    private final int dist = 4;
    private final int vDist = 8;

    private JLabel titleLabel;

    private JLabel addressLabel;
    private JLabel portLabel;
    private JLabel queueLabel;
    private JLabel queueLabel2;
    private JLabel userLabel;
    private JLabel userDescLabel;
    private JLabel serverLocationLabel;
    private JLabel privEscDescLabel;
    private JLabel adminUsersLabel;
    private JLabel userAuthLabel;

    private JTextField addressField;
    private JTextField portField;
    private JTextField userField;
    private JTextField serverLocationField;
    private JTextField adminUsersField;

    private JRadioButton onlyRootRadioButton;
    private JRadioButton notOnlyRootRadioButton;

    private JButton serverLocationBrowseButton;

    private JCheckBox startServerOnSystemStartupCheckbox;
    private JCheckBox usePrivEscCheckbox;
    private JCheckBox editSudoersFileCheckbox;
    private JCheckBox useGridCheckbox;
    private JComboBox gridPluginComboBox;
    private JComboBox queueComboBox;
    private JComboBox userAuthComboBox;

    private JSeparator topSeparator;
    private JSeparator middleSeparator;

    private JFileChooser fileChooser;
    
    private JLabel scratchDirLabel;
    private JTextField scratchDirField;
    private JButton scratchDirBrowseButton;
    
    private JLabel memoryAllocationLabel;
    private JRadioButton automaticMemoryAllocationButton;
    private JRadioButton customMemoryAllocationButton;
    private JTextField memoryAllocationField;
    private JLabel memoryAllocationUnits;
    private ButtonGroup memoryAllocationButtonGroup;

    private boolean needsSGE;

    public static final String USER_AUTH_PAM = "PAM";
    public static final String USER_AUTH_SSH = "SSH";
    public static final String USER_AUTH_NO_AUTH = "NoAuth";
    
    /** Creates a new instance of LicensePanel */
    public ServerConfigurationPanel() {
       initComponents();
       initLayout();

       initListeners();

    }

    private void initComponents() {
        titleLabel = ComponentFactory.label("<html><font size=\"4\"><b>Pipeline Server Configuration</b></font></html>");

        addressLabel = ComponentFactory.label("Server address:");
        portLabel = ComponentFactory.label("Port:");
        userLabel = ComponentFactory.label("Pipeline user (*):");
        userDescLabel = ComponentFactory.label("* User should exist");
        
        

        queueLabel = ComponentFactory.label("Submission queue:");
        queueLabel2 = ComponentFactory.label(""); // useless, can be used for something in the future
        serverLocationLabel = ComponentFactory.label("Installation directory:");
        
        userAuthLabel = ComponentFactory.label("Users authentication:");

        serverLocationField = ComponentFactory.textfield(22);
        addressField = ComponentFactory.textfield(20);
        portField = ComponentFactory.textfield(4);
        userField = ComponentFactory.textfield(8);
        adminUsersField = ComponentFactory.textfield(10);
        adminUsersField.setVisible(false);
        
        queueComboBox = ComponentFactory.combobox();
        
        serverLocationBrowseButton = ComponentFactory.button("Browse...");

        startServerOnSystemStartupCheckbox = ComponentFactory.checkbox("Start Pipeline server on system startup");
        usePrivEscCheckbox = ComponentFactory.checkbox("Use privilege escalation (sudo as users when submitting jobs)");
        privEscDescLabel = ComponentFactory.label("<html><font size=\"3\"><i>NOTE: If the Pipeline server is configured to use privilege escalation,<br/>" +
                                                  "then the specified user should be able to sudo as any non root user.</i></font></html>");
        editSudoersFileCheckbox = ComponentFactory.checkbox("Modify the sudoers file so the Pipeline User will be able to sudo as any");
        adminUsersLabel = ComponentFactory.label("user except");

        ButtonGroup rootButtonGroup = new ButtonGroup();
        onlyRootRadioButton = ComponentFactory.radiobutton("root");
        onlyRootRadioButton.setSelected(true);
        notOnlyRootRadioButton = ComponentFactory.radiobutton("root and following users (comma separated)");

        rootButtonGroup.add(onlyRootRadioButton);
        rootButtonGroup.add(notOnlyRootRadioButton);

        useGridCheckbox = ComponentFactory.checkbox("Enable Grid submission");
        String gridCheckboxHint = "<html><body>Select this checkbox if you would like to utilize your cluster via grid engine.</body></html>";
        useGridCheckbox.setToolTipText(gridCheckboxHint);
        
        gridPluginComboBox = ComponentFactory.combobox();

        gridPluginComboBox.addItem("Use JGDI Plugin");
        gridPluginComboBox.addItem("Use DRMAA Plugin");

        userAuthComboBox = ComponentFactory.combobox();
        userAuthComboBox.addItem("PAM (PAM required)");
        userAuthComboBox.addItem("SSH Based (sshd required)");
        userAuthComboBox.addItem("No Authentication");

        topSeparator = new JSeparator(JSeparator.HORIZONTAL);
        middleSeparator = new JSeparator(JSeparator.HORIZONTAL);

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


        String plLocationHint = "<html><body>The directory where you want the Pipeline to be installed.</body></html>";

        serverLocationLabel.setToolTipText(plLocationHint);
        serverLocationField.setToolTipText(plLocationHint);
        serverLocationBrowseButton.setToolTipText(plLocationHint);

        String addressHint = "<html><body>The address you want the Pipeline server to use.</body></html>";

        addressLabel.setToolTipText(addressHint);
        addressField.setToolTipText(addressHint);

        String portHint = "<html><body>The network port you want the Pipeline server to use.<br/>" +
                          "The default is 8001 and if you choose other port, Pipeline clients have to<br/>" +
                          "specify the port when connecting to server ( address:port )</body></html>";

        portLabel.setToolTipText(portHint);
        portField.setToolTipText(portHint);
        
        String userHint = "<html><body>The user who will run the Pipeline Server process<br/>"
                           + "You need to specify an existing user. The user root is not allowed.</body></html>";

        userLabel.setToolTipText(userHint);
        userField.setToolTipText(userHint);

        String userAuthHint = "<html>The user authentication method. <br/>"
                + "It is used to authenticate users when they connect to the Pipeline server.</html>";
        userAuthLabel.setToolTipText(userAuthHint);
        userAuthComboBox.setToolTipText(userAuthHint);

        String startOnStartupHint = "<html><body>Check this checkbox if you want Pipeline server to be started <br/>" +
                                "everytime after you reboot the machine <b>(Recommended)</b>.</body></html>";
        startServerOnSystemStartupCheckbox.setToolTipText(startOnStartupHint);

        String usePrivEscHint = "<html><body>Check this checkbox if you want Pipeline server to submit or <br/>" +
                                "run jobs as users who run the workflows <b>(Recommended)</b>.</body></html>";
        usePrivEscCheckbox.setToolTipText(usePrivEscHint);

        String modifySudoersHint = "<html><body>Check this checkbox if you want this installer to make the <br/>" +
                                "Pipeline User ( which you selected above ) a Super user<br/><br/>" +
                                "If you uncheck this checkbox and select to use privilege escalation,<br/>" +
                                "then job submissions can fail, so please make sure to properly edit<br/>" +
                                "the sudoers file and manually give needed permissions to Pipeline User<br/></body></html>";
        editSudoersFileCheckbox.setToolTipText(modifySudoersHint);

        String adminUsersHint = "<html><body>Specify list of users which you want to protect from Pipeline User<br/>" +
                                 "by not allowing to sudo as themselves.</body></html>";
        adminUsersField.setToolTipText(adminUsersHint);

        String pluginHint = "<html><body>JGDI Plugin is recommended for SGE as it is more robust and stable.</body></html>";
        gridPluginComboBox.setToolTipText(pluginHint);

        String queueHint = "<html><body>Select already existing queue where Pipeline will submit its jobs.<br/>" +
                           "Note that Pipeline will not create the queue for you.</body></html>";
        queueLabel.setToolTipText(queueHint);
        queueLabel2.setToolTipText(queueHint);
        queueComboBox.setToolTipText(queueHint);

        scratchDirLabel = ComponentFactory.label("Workflow scratch dir");
        scratchDirField = ComponentFactory.textfield(20);
        scratchDirBrowseButton = ComponentFactory.button("Browse...");
        String scratchDirHint = "<html><body>Select a directory for the Pipeline to write files to. Files in this location<br/>" +
                                "will persist after a workflow has been reset.</body></html>";
        scratchDirLabel.setToolTipText(scratchDirHint);
        scratchDirField.setToolTipText(scratchDirHint);
        scratchDirBrowseButton.setToolTipText(scratchDirHint);
        
        memoryAllocationLabel = ComponentFactory.label("Memory Allocation:");
        automaticMemoryAllocationButton = ComponentFactory.radiobutton("Auto");
        customMemoryAllocationButton = ComponentFactory.radiobutton("Custom");
        memoryAllocationField = ComponentFactory.textfield(4);
        memoryAllocationUnits = ComponentFactory.label("Mb");
        memoryAllocationUnits.setFont(new Font("Courier New", Font.PLAIN, 12));
        String memoryAllocationHint = "<html><body>Choose a mode of memory allocation for the Pipeline server.</body></html>";
        memoryAllocationLabel.setToolTipText(memoryAllocationHint);
        String autoMemoryAllocationHint = "<html><body>Select this option if you want to allow the installer to decide the memory allocation.</body></html>";        
        automaticMemoryAllocationButton.setToolTipText(autoMemoryAllocationHint);
        String customMemoryAllocationHint = "<html><body>Select this option if you want to manually specify the amount of memory to be<br>allocated to the Pipeline server.</body></html>";        
        customMemoryAllocationButton.setToolTipText(customMemoryAllocationHint);
        String customMemoryAllocationFieldHint = "<html><body>Specify the memory allocation in megabytes.</body></html>";        
        memoryAllocationField.setToolTipText(customMemoryAllocationFieldHint);
        
        memoryAllocationButtonGroup = new ButtonGroup();
        memoryAllocationButtonGroup.add(automaticMemoryAllocationButton);
        memoryAllocationButtonGroup.add(customMemoryAllocationButton);    
        automaticMemoryAllocationButton.setSelected(true);
        
        automaticMemoryAllocationButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                memoryAllocationField.setVisible(false);
                memoryAllocationUnits.setVisible(false);
            }
        });
        
        customMemoryAllocationButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                memoryAllocationField.setVisible(true);
                memoryAllocationUnits.setVisible(true);
            }
        });

        this.setPreferredSize(new Dimension(400, 400));
    }

    private void initLayout() {
        layout = new SpringLayout();
        setLayout(layout);

        add(titleLabel);
        layout.putConstraint(north, titleLabel, 0, north, this);
        layout.putConstraint(west, titleLabel, panelMargin, west, this);

        add(serverLocationLabel);
        layout.putConstraint(north, serverLocationLabel, vDist, south, titleLabel);
        layout.putConstraint(west, serverLocationLabel, dist * 4, west, titleLabel);

        add(serverLocationField);
        layout.putConstraint(north, serverLocationField, 0, north, serverLocationLabel);
        layout.putConstraint(west, serverLocationField, dist, east, serverLocationLabel);

        add(serverLocationBrowseButton);
        layout.putConstraint(north, serverLocationBrowseButton, -dist, north, serverLocationLabel);
        layout.putConstraint(west, serverLocationBrowseButton, dist, east, serverLocationField);

        
        add(addressLabel);
        layout.putConstraint(north, addressLabel, vDist / 2, south, serverLocationBrowseButton);
        layout.putConstraint(east, addressLabel, 0, east, serverLocationLabel);

        add(addressField);
        layout.putConstraint(north, addressField, 0, north, addressLabel);
        layout.putConstraint(west, addressField, dist, east, serverLocationLabel);

        add(portLabel);
        layout.putConstraint(north, portLabel, 0, north, addressLabel);
        layout.putConstraint(west, portLabel , dist, east, addressField);

        add(portField);
        layout.putConstraint(north, portField, 0, north, addressField);
        layout.putConstraint(west, portField, dist, east, portLabel);

        add(userLabel);
        layout.putConstraint(north, userLabel, vDist / 2, south, portField);
        layout.putConstraint(east, userLabel, 0, east, serverLocationLabel);

        add(userField);
        layout.putConstraint(north, userField, 0, north, userLabel);
        layout.putConstraint(west, userField, dist, east, serverLocationLabel);

        add(userDescLabel);
        layout.putConstraint(north, userDescLabel, vDist / 2, north, userField);
        layout.putConstraint(west, userDescLabel, dist, east, userField);

        add(userAuthLabel);
        layout.putConstraint(north, userAuthLabel, vDist / 2, south, userField);
        layout.putConstraint(east, userAuthLabel , 0, east, serverLocationLabel);

        add(userAuthComboBox);
        layout.putConstraint(north, userAuthComboBox, -3, north, userAuthLabel);
        layout.putConstraint(west, userAuthComboBox , dist, east, userAuthLabel);

        add(scratchDirLabel);
        layout.putConstraint(north, scratchDirLabel, vDist / 2, south, userAuthComboBox);
        layout.putConstraint(east, scratchDirLabel, 0, east, serverLocationLabel);

        add(scratchDirField);
        layout.putConstraint(north, scratchDirField, 0, north, scratchDirLabel);
        layout.putConstraint(west, scratchDirField, dist, east, serverLocationLabel);

        add(scratchDirBrowseButton);
        layout.putConstraint(north, scratchDirBrowseButton, -3, north, scratchDirLabel);
        layout.putConstraint(west, scratchDirBrowseButton, dist, east, scratchDirField);

        add(middleSeparator);
        layout.getConstraints(middleSeparator).setConstraint(east, Spring.sum(Spring.constant(-panelMargin),layout.getConstraint(east, this)));
        layout.putConstraint(north, middleSeparator, vDist / 2, south, scratchDirBrowseButton);
        layout.putConstraint(west, middleSeparator , panelMargin, west, this);
        
        add(memoryAllocationLabel);
        layout.putConstraint(north, memoryAllocationLabel, dist + 2, south, middleSeparator);
        layout.putConstraint(west, memoryAllocationLabel , 0, west, serverLocationLabel);
        
        add(automaticMemoryAllocationButton);
        layout.putConstraint(north, automaticMemoryAllocationButton, dist, south, middleSeparator);
        layout.putConstraint(west, automaticMemoryAllocationButton , dist, east, memoryAllocationLabel);        
        
        add(customMemoryAllocationButton);
        layout.putConstraint(north, customMemoryAllocationButton, dist, south, middleSeparator);
        layout.putConstraint(west, customMemoryAllocationButton, dist, east, automaticMemoryAllocationButton);

        add(memoryAllocationField);
        layout.putConstraint(north, memoryAllocationField, dist, south, middleSeparator);
        layout.putConstraint(west, memoryAllocationField, dist, east, customMemoryAllocationButton);
        memoryAllocationField.setVisible(false);

        add(memoryAllocationUnits);
        layout.putConstraint(north, memoryAllocationUnits, dist + 2, south, middleSeparator);
        layout.putConstraint(west, memoryAllocationUnits, dist, east, memoryAllocationField);
        memoryAllocationUnits.setVisible(false);

        add(startServerOnSystemStartupCheckbox);
        layout.putConstraint(north, startServerOnSystemStartupCheckbox, dist, south, memoryAllocationLabel);
        layout.putConstraint(west, startServerOnSystemStartupCheckbox , 0, west, serverLocationLabel);

        add(usePrivEscCheckbox);
        layout.putConstraint(north, usePrivEscCheckbox, vDist / 4, south, startServerOnSystemStartupCheckbox);
        layout.putConstraint(west, usePrivEscCheckbox , 0, west, serverLocationLabel);

        add(privEscDescLabel);
        layout.putConstraint(north, privEscDescLabel, vDist / 8, south, usePrivEscCheckbox);
        layout.putConstraint(west, privEscDescLabel , 4 * dist, west, usePrivEscCheckbox);

        add(editSudoersFileCheckbox);
        layout.putConstraint(north, editSudoersFileCheckbox, vDist / 8, south, privEscDescLabel);
        layout.putConstraint(west, editSudoersFileCheckbox , 0, west, privEscDescLabel);

        add(editSudoersFileCheckbox);
        layout.putConstraint(north, editSudoersFileCheckbox, vDist / 8, south, privEscDescLabel);
        layout.putConstraint(west, editSudoersFileCheckbox , 0, west, privEscDescLabel);

        add(adminUsersLabel);
        layout.putConstraint(north, adminUsersLabel, vDist / 8, south, editSudoersFileCheckbox);
        layout.putConstraint(west, adminUsersLabel , 4 * dist, west, editSudoersFileCheckbox);

        add(onlyRootRadioButton);
        layout.putConstraint(north, onlyRootRadioButton, - vDist / 8, north, adminUsersLabel);
        layout.putConstraint(west, onlyRootRadioButton , dist, east, adminUsersLabel);

        add(notOnlyRootRadioButton);
        layout.putConstraint(north, notOnlyRootRadioButton, 0, north, onlyRootRadioButton);
        layout.putConstraint(west, notOnlyRootRadioButton , dist, east, onlyRootRadioButton);

        add(adminUsersField);
        layout.putConstraint(north, adminUsersField, vDist / 4, south, notOnlyRootRadioButton);
        layout.getConstraints(adminUsersField).setConstraint(east, layout.getConstraint(east, editSudoersFileCheckbox));
        layout.putConstraint(west, adminUsersField , 0, west, adminUsersLabel);

        add(useGridCheckbox);
        layout.putConstraint(north, useGridCheckbox, vDist / 4, south, adminUsersField);
        layout.putConstraint(west, useGridCheckbox , 0, west, serverLocationLabel);

        add(gridPluginComboBox);
        layout.putConstraint(north, gridPluginComboBox, 0, north, useGridCheckbox);
        layout.putConstraint(west, gridPluginComboBox , dist, east, useGridCheckbox);

        add(queueLabel);
        layout.putConstraint(north, queueLabel, vDist / 8, south, useGridCheckbox);
        layout.putConstraint(east, queueLabel , 0, east, useGridCheckbox);

        add(queueComboBox);
        layout.putConstraint(north, queueComboBox, 0, north, queueLabel);
        layout.putConstraint(west, queueComboBox , dist, east, useGridCheckbox);

        add(queueLabel2);
        layout.putConstraint(north, queueLabel2, vDist, south, useGridCheckbox);
        layout.putConstraint(west, queueLabel2 , dist, east, queueComboBox);

    }

    private void populateData() {
        // First initialize with configured values
        // if values are not configured, then init with defaults.
        String configServerLocation = Configuration.getConfig(Configuration.CONFIG_PIPELINE_LOCATION);
        String configAddress = Configuration.getConfig(Configuration.CONFIG_PIPELINE_HOSTNAME);
        String configPort = Configuration.getConfig(Configuration.CONFIG_PIPELINE_PORT);
        String configUser = Configuration.getConfig(Configuration.CONFIG_PIPELINE_USER);
        String configUsePrivEsc = Configuration.getConfig(Configuration.CONFIG_PIPELINE_USEPRIVESC);
        String configStartOnStartup = Configuration.getConfig(Configuration.CONFIG_PIPELINE_START_ON_STARTUP);
        String configPlugin = Configuration.getConfig(Configuration.CONFIG_PIPELINE_PLUGIN);
        String configQueue = Configuration.getConfig(Configuration.CONFIG_PIPELINE_QUEUE);
        String configSudo = Configuration.getConfig(Configuration.CONFIG_INSTALL_SUPERUSER);
        String configSGERoot = Configuration.getConfig(Configuration.CONFIG_SGE_ROOT);

        String configUserAuth = Configuration.getConfig(Configuration.CONFIG_PIPELINE_USER_AUTH);
        try{
            if(configUserAuth==null)
                userAuthComboBox.setSelectedIndex(1);
            else if(configUserAuth.equals(USER_AUTH_PAM))
                userAuthComboBox.setSelectedIndex(0);
            else if(configUserAuth.equals(USER_AUTH_SSH))
                userAuthComboBox.setSelectedIndex(1);
            else if(configUserAuth.equals(USER_AUTH_NO_AUTH))
                userAuthComboBox.setSelectedIndex(2);
        } catch(Exception e) { userAuthComboBox.setSelectedIndex(1);}

        if ( configAddress == null ) {
            try {
                configAddress = InetAddress.getLocalHost().getHostName();
            } catch (Exception ex ) {
                ex.printStackTrace();
            }
        }

        if ( !needsSGE && configSGERoot != null )
            populateQueues();

        if ( configPort == null )
            configPort = String.valueOf(Constants.serverDefaultPort);

        if ( configUser == null )
            configUser = "pipeline";

        if ( configServerLocation == null )
            configServerLocation = "/usr/pipeline";

        if ( configUsePrivEsc == null )
            configUsePrivEsc = "true";

        if ( configSudo == null )
            configSudo = "true";

        if ( configStartOnStartup == null )
            configStartOnStartup = "true";


        serverLocationField.setText(configServerLocation);
        addressField.setText(configAddress);
        portField.setText(configPort);
        userField.setText(configUser);

        startServerOnSystemStartupCheckbox.setSelected(Boolean.parseBoolean(configStartOnStartup));
        usePrivEscCheckbox.setSelected(Boolean.parseBoolean(configUsePrivEsc));
        editSudoersFileCheckbox.setSelected(Boolean.parseBoolean(configSudo));
        
        if ( needsSGE ) { 
            useGridCheckbox.setVisible(false);
            queueLabel.setVisible(false);
            queueComboBox.setVisible(false);
            queueLabel2.setVisible(false);
            gridPluginComboBox.setVisible(false);
        } else {
            if ( configSGERoot != null ) {

                if ( queueComboBox.getItemCount() == 1 ) {
                    useGridCheckbox.setVisible(true);
                    useGridCheckbox.setSelected(false);
                    queueLabel.setVisible(false);
                    queueComboBox.setVisible(false);
                    queueLabel2.setVisible(false);
                    gridPluginComboBox.setVisible(false);

                } else {
                    useGridCheckbox.setSelected(true);
                    useGridCheckbox.setVisible(true);
                    queueLabel.setVisible(true);
                    queueComboBox.setVisible(true);
                    queueLabel2.setVisible(true);
                    gridPluginComboBox.setVisible(true);
                }
            } else {
                useGridCheckbox.setVisible(false);
                useGridCheckbox.setSelected(false);
                queueLabel.setVisible(false);
                queueComboBox.setVisible(false);
                queueLabel2.setVisible(false);
                gridPluginComboBox.setVisible(false);
            }

            if ( useGridCheckbox.isVisible() ) {
                
                for ( int i = 0; i < queueComboBox.getItemCount(); i++ ) { 
                    String queue = (String)queueComboBox.getItemAt(i);
                    
                    if ( queue.toLowerCase().equals("pipeline.q") ) { 
                        configQueue = queue;
                        break;
                    }
                }
                
                
                if ( configQueue == null )
                    queueComboBox.setSelectedIndex(0);
                else
                    queueComboBox.setSelectedItem(configQueue);
            }
            
        }

        if (configPlugin == null) {
            gridPluginComboBox.setSelectedIndex(0);
        } else if (!needsSGE) {
            useGridCheckbox.setSelected(true);
        }

        String sharedDir = Configuration.getConfig(Configuration.CONFIG_SHARED_FILESYSTEM_PATH);
        scratchDirField.setText(sharedDir + "pipelineScratch");
    }


    private void populateQueues() {
        String sgeRoot = Configuration.getConfig(Configuration.CONFIG_SGE_ROOT);

        String arch = "";

        Process p = null;
        try {
            p = Runtime.getRuntime().exec(sgeRoot + "/util/arch");
            InputStream is = p.getInputStream();
            byte [] buff = new byte[256];
            if ( is.read(buff) > 0 ) {
                 arch = new String(buff).trim();
            }
        } catch ( Exception ex) {
            ex.printStackTrace();
        } finally {
            NativeCalls.releaseProcess(p);
        }

        StringBuilder cmd = new StringBuilder(sgeRoot);
        cmd.append(File.separator);
        cmd.append("bin");
        cmd.append(File.separator);
        cmd.append(arch);
        cmd.append(File.separator);
        cmd.append("qstat -g c");


        List<String> queues = new LinkedList<String>();
        try {
            p = Runtime.getRuntime().exec( cmd.toString() );
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String s = null;
            int lineNo = 0;
            while ( (s = stdInput.readLine() ) != null ) {
                lineNo++;
                if ( lineNo <= 2)
                    continue;

                String [] tokens = s.split(" ");
                queues.add(tokens[0]);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if ( p != null )
                NativeCalls.releaseProcess(p);
        }


        queueComboBox.removeAllItems();
        queueComboBox.addItem(" Select One ");
        for ( String q : queues )
            queueComboBox.addItem(q);

    }


    private void initListeners() {

        
         useGridCheckbox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if ( !needsSGE ) {
                    if ( useGridCheckbox.isSelected() ) {
                        populateQueues();

                        if ( queueComboBox.getItemCount() <= 1 )
                        {
                            JOptionPane.showMessageDialog(sif, "Cannot enable Grid submission as the installer was not able to detect any SGE queue\n" +
                                                               "Please make sure SGE_ROOT env. variable is defined before launching the installer and\n" +
                                                               "there is at least one installed and available queue. You may need to restart the installer.",
                                                               "Error",
                                                               JOptionPane.ERROR_MESSAGE);
                            useGridCheckbox.setSelected(false);
                            return;
                        }
                        
                        queueLabel.setVisible(true);
                        queueComboBox.setVisible(true);
                        queueLabel2.setVisible(true);
                        gridPluginComboBox.setVisible(true);
                    } else {
                        queueLabel.setVisible(false);
                        queueComboBox.setVisible(false);
                        queueLabel2.setVisible(false);
                        gridPluginComboBox.setVisible(false);
                    }
                }
            }
        });

        usePrivEscCheckbox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if ( usePrivEscCheckbox.isSelected() ) {
                    editSudoersFileCheckbox.setVisible(true);
                    adminUsersLabel.setVisible(true);
                    adminUsersField.setVisible(true);
                    onlyRootRadioButton.setVisible(true);
                    notOnlyRootRadioButton.setVisible(true);
                    privEscDescLabel.setVisible(true);
                    adminUsersField.setVisible(true);
                } else {
                    editSudoersFileCheckbox.setVisible(false);
                    adminUsersLabel.setVisible(false);
                    adminUsersField.setVisible(false);
                    onlyRootRadioButton.setVisible(false);
                    notOnlyRootRadioButton.setVisible(false);
                    privEscDescLabel.setVisible(false);
                    adminUsersField.setVisible(false);
                }
            }
        });


        editSudoersFileCheckbox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if ( editSudoersFileCheckbox.isSelected() ) {
                    onlyRootRadioButton.setEnabled(true);
                    notOnlyRootRadioButton.setEnabled(true);
                    adminUsersField.setEnabled(true);
                } else {
                    onlyRootRadioButton.setEnabled(false);
                    notOnlyRootRadioButton.setEnabled(false);
                    adminUsersField.setEnabled(false);
                }
            }
        });

        onlyRootRadioButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if ( onlyRootRadioButton.isSelected() )
                    adminUsersField.setVisible(false);
                else
                    adminUsersField.setVisible(true);
            }
        });

        notOnlyRootRadioButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if ( onlyRootRadioButton.isSelected() )
                    adminUsersField.setVisible(false);
                else
                    adminUsersField.setVisible(true);
            }
        });
        
        serverLocationBrowseButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                File selectedDir = null;

                String s = serverLocationField.getText();
                File f = new File(s);

                if ( f.exists() )
                    fileChooser.setCurrentDirectory(f);
                
                if (fileChooser.showOpenDialog(sif) == JFileChooser.APPROVE_OPTION)
                    selectedDir = fileChooser.getSelectedFile();

                if (selectedDir != null)
                    serverLocationField.setText(selectedDir.getPath() + File.separator);
            }
        });

        scratchDirBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File selectedFile = null;

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setFileHidingEnabled(false);
                String s = scratchDirField.getText();
                File f = new File(s);

                if (f.exists()) {
                    fileChooser.setCurrentDirectory(f);
                }

                if (fileChooser.showOpenDialog(sif) == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                }

                if (selectedFile != null) {
                    scratchDirField.setText(selectedFile.getPath());
                }
            }
        });
    }

    public void saveUserInput() {
        boolean needsPipeline = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_PIPELINE));

        if ( !needsPipeline )
            return;

        String sharedDir = Configuration.getConfig(Configuration.CONFIG_SHARED_FILESYSTEM_PATH);
        String installDir = serverLocationField.getText().trim();


        if ( !installDir.endsWith(File.separator) )
            installDir = installDir + File.separator;

        Configuration.setConfig(Configuration.CONFIG_PIPELINE_HOSTNAME, addressField.getText().trim());
        Configuration.setConfig(Configuration.CONFIG_PIPELINE_PORT, portField.getText().trim());
        Configuration.setConfig(Configuration.CONFIG_PIPELINE_USER, userField.getText().trim());
        Configuration.setConfig(Configuration.CONFIG_PIPELINE_SCRATCHDIR, scratchDirField.getText().trim());

        Configuration.setConfig(Configuration.CONFIG_PIPELINE_TEMPDIR, sharedDir + "pipelineCache");
        Configuration.setConfig(Configuration.CONFIG_PIPELINE_SERVERLIB, installDir + "serverLibrary");
        Configuration.setConfig(Configuration.CONFIG_PIPELINE_USEPRIVESC, Boolean.toString(usePrivEscCheckbox.isSelected()));
        Configuration.setConfig(Configuration.CONFIG_PIPELINE_START_ON_STARTUP, Boolean.toString(startServerOnSystemStartupCheckbox.isSelected()));
        
        if(userAuthComboBox.getSelectedIndex()==0)
            Configuration.setConfig(Configuration.CONFIG_PIPELINE_USER_AUTH, USER_AUTH_PAM);
        else if(userAuthComboBox.getSelectedIndex() == 1)
            Configuration.setConfig(Configuration.CONFIG_PIPELINE_USER_AUTH, USER_AUTH_SSH);
        else if(userAuthComboBox.getSelectedIndex() == 2)
            Configuration.setConfig(Configuration.CONFIG_PIPELINE_USER_AUTH, USER_AUTH_NO_AUTH);


        if ( !editSudoersFileCheckbox.isSelected() )
            Configuration.setConfig(Configuration.CONFIG_INSTALL_SUPERUSER, "false");
        else
            Configuration.setConfig(Configuration.CONFIG_INSTALL_SUPERUSER, "true");
        
        if ( editSudoersFileCheckbox.isVisible() ) {

            if ( notOnlyRootRadioButton.isSelected() && notOnlyRootRadioButton.isVisible() && notOnlyRootRadioButton.isEnabled() ) {
                String superUsers = adminUsersField.getText().replaceAll(" ", "");

                if ( superUsers.length() > 0 )
                    Configuration.setConfig(Configuration.CONFIG_SUPERUSER_LIST, superUsers.replaceAll(",", ",!"));

            } else {
                Configuration.setConfig(Configuration.CONFIG_SUPERUSER_LIST, null);
            }
        } else
            Configuration.setConfig(Configuration.CONFIG_INSTALL_SUPERUSER, "false");



        Configuration.setConfig(Configuration.CONFIG_PIPELINE_LOCATION, installDir);
        Configuration.setConfig(Configuration.CONFIG_PIPELINEDB_LOCATION, installDir + "db");


        if ( needsSGE ) {
            String pluginName = (String)gridPluginComboBox.getItemAt(0);

            pluginName = pluginName.substring(pluginName.indexOf(" ") + 1, pluginName.lastIndexOf(" "));

            Configuration.setConfig(Configuration.CONFIG_PIPELINE_PLUGIN, pluginName);
            Configuration.setConfig(Configuration.CONFIG_PIPELINE_QUEUE, "pipeline.q");
        } else {
             if ( useGridCheckbox.isSelected() ) {
                String selected = (String)gridPluginComboBox.getSelectedItem();

                String pluginName = selected.substring(selected.indexOf(" ") + 1, selected.lastIndexOf(" "));
                Configuration.setConfig(Configuration.CONFIG_PIPELINE_PLUGIN, pluginName);

                Configuration.setConfig(Configuration.CONFIG_PIPELINE_QUEUE, (String)queueComboBox.getSelectedItem());
            }

        }
    }

    public boolean checkUserInput() {
        boolean needsPipeline = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_PIPELINE));

        if ( !needsPipeline )
            return true;

        
        String installDir = serverLocationField.getText().trim();
        String toolsDir = Configuration.getConfig(Configuration.CONFIG_TOOLS_PATH);


        if ( installDir.length() == 0 ) {
            JOptionPane.showMessageDialog(sif, "Please choose Pipeline installation directory", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if ( addressField.getText().trim().length() == 0 ) {
            JOptionPane.showMessageDialog(sif, "Please specify Pipeline's Server address", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        
        if ( portField.getText().trim().length() == 0 ) {
            JOptionPane.showMessageDialog(sif, "Please specify Pipeline's Server port", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String serverUser = userField.getText().trim();

        if ( serverUser.length() == 0 ) {
            JOptionPane.showMessageDialog(sif, "Please specify Pipeline's Server username", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!checkUserExists(serverUser)) {
            return false;
        }

        if (!installDir.endsWith(File.separator)) {
            installDir = installDir + File.separator;
        }

        if (!toolsDir.endsWith(File.separator)) {
            toolsDir = toolsDir + File.separator;
        }
        
        if ( editSudoersFileCheckbox.isVisible() ) {
            
            if ( notOnlyRootRadioButton.isSelected() && notOnlyRootRadioButton.isVisible() && notOnlyRootRadioButton.isEnabled() ) {
                String superUsers = adminUsersField.getText().replaceAll(" ", "");

                String [] users = superUsers.split(",");

                for ( String user : users ) {
                    if ( !checkUserExists(user) )
                        return false;
                }

                if ( superUsers.length() == 0 ) {
                    JOptionPane.showMessageDialog(sif, "Please specify the list of super users", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

            }
        }

        if ( !needsSGE ) {
             if ( useGridCheckbox.isSelected() ) {
                if ( queueComboBox.getSelectedIndex() == 0 ) {
                    JOptionPane.showMessageDialog(sif, "Please specify Pipeline's submission queue", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

        }

        if (customMemoryAllocationButton.isSelected()) {
            String memoryAllocationString = memoryAllocationField.getText();
            if (memoryAllocationString != null && !memoryAllocationString.trim().isEmpty()) {
                try {
                    int memoryAllocation = Integer.parseInt(memoryAllocationString) / 2;
                    
                    Configuration.setConfig(Configuration.CONFIG_PIPELINE_MEMORY_ALLOCATION, Integer.toString(memoryAllocation));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(sif, "Please enter a valid integer to specify the server memory allocation (in Mb).", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }


        if (new File(installDir).exists()) {
            // check if the current user is the owner of the already-installed Pipeline files
            Process process = null;
            try {
                String username = userField.getText().trim();
                process = Runtime.getRuntime().exec("/usr/bin/stat -c %U " + installDir);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!username.equals(line.trim())) {
                        JOptionPane.showMessageDialog(sif, "The directory " + installDir + " exists and is owned by user " + line + ".\nYou will not be able to overwrite. Please change the value in the\nPipeline user field to " + line + " in order to proceed.", "Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
                process.waitFor();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            int choice = JOptionPane.showConfirmDialog(sif, "The directory " + installDir + " already exists.\nAre you sure that you want to overwrite it with a new installation ? ", "Warning", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) {
                return false;
            }

        }

        // always include package information for the general Pipeline utilities
        // this is added to ensure portability of smartline, IDA, and XNAT modules
        final Package p = new Package("Pipeline Utilities", "*", toolsDir + "PipelineUtilities/");
        final List<Package> packages = new LinkedList<Package>();
        packages.add(p);

        // Start the Userdata Updater thread.
        Thread t = new Thread() {
            public void run() {
                UserdataUpdater uu = new UserdataUpdater();
                uu.setTempDir(scratchDirField.getText().trim());

                List<Package> existingPackages = uu.getPackages();
                if ( existingPackages != null ) {
                    for (Package ep : existingPackages) {
                        if (p.getName().equals(ep.getName()) && p.getVersion().equals(ep.getVersion())) {
                            existingPackages.remove(ep);
                            break;
                        }
                    }

                    packages.addAll(existingPackages);
                }

                uu.setPackages(packages);
                
                // now write it out to disk
                uu.flushToDisk();
            }
        };
        t.start();
        
        return true;
    }


    private boolean checkUserExists(String user) {

        if ( user.equals("root") ) {
            JOptionPane.showMessageDialog(sif, "User root is not allowed.\nTry another user.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check Pipeline user
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("id " + user);
            p.waitFor();
            int exitValue = p.exitValue();

            if ( exitValue != 0 ) {
                JOptionPane.showMessageDialog(sif, "Invalid user - " + user + "\nPlease make sure the user exists in the system.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if ( p != null )
                NativeCalls.releaseProcess(p);
        }

        return true;
    }

    public void panelActivated() {
        needsSGE = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_SGE));
        boolean needsPipeline = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_PIPELINE));
        populateData();

        sif.setNextEnabled(true);

        if ( !needsPipeline )
            sif.redirect();
    }

}

