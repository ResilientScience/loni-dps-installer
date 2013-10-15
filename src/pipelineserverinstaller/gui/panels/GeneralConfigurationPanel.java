
package pipelineserverinstaller.gui.panels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import pipelineserverinstaller.Configuration;
import pipelineserverinstaller.NativeCalls;
import pipelineserverinstaller.gui.ComponentFactory;

/**
 *
 * @author Petros Petrosyan
 */
public class GeneralConfigurationPanel extends AbstractStepPanel {

    // just to make the compiler stop complaining
    static final long serialVersionUID = 1L;

    private String north = SpringLayout.NORTH;
    private String south = SpringLayout.SOUTH;
    private String west = SpringLayout.WEST;
    private String east = SpringLayout.EAST;

    private final int panelMargin = 10;
    private final int dist = 5;
    private final int vDist = 8;

    private JLabel titleLabel;
    private JCheckBox pipelineCheckbox;
    private JCheckBox sgeCheckbox;
    private JCheckBox niToolsCheckbox;
    private JCheckBox biToolsCheckbox;

    private JLabel pipelineDescription;
    private JLabel sgeDescription;
    private JLabel niToolsDescription;
    private JLabel biToolsDescription;

    private JLabel selectComponentsLabel;

    private JLabel fsPathLabel;
    
    private JTextField fsPathField;

    private JButton fsPathBrowseButton;

    private JSeparator topSeparator;
    private JSeparator middleSeparator;
    private JSeparator bottomSeparator;

    private final String SGE_VERSION="8.0.0d";

    Map<String,String> sgeInstallations = new HashMap<String,String>();

    
    /** Creates a new instance of LicensePanel */
    public GeneralConfigurationPanel() {
        initComponents();
        initLayout();
        initListeners();
    }

    private void initComponents() {
        titleLabel = ComponentFactory.label("<html><font size=\"4\"><b>General Configuration</b></font></html>");

        Font titleFont = titleLabel.getFont();
        Font checkboxFont = new Font(titleFont.getName(), titleFont.getStyle(), titleFont.getSize() + 3);

        
        selectComponentsLabel = ComponentFactory.label("<html><font size=\"4\"><b>Please select what to install:</b></font></html>");

        topSeparator = new JSeparator(JSeparator.HORIZONTAL);
        middleSeparator = new JSeparator(JSeparator.HORIZONTAL);
        bottomSeparator = new JSeparator(JSeparator.HORIZONTAL);

        fsPathLabel = ComponentFactory.label("Shared File System Location:");
        fsPathField = ComponentFactory.textfield(17);
        fsPathBrowseButton =  ComponentFactory.button("Browse...");

        String fsHint = "<html><body>Network Shared File System is required when using Grid Engine,<br/>" +
                        "so please make sure to provide path which can be accessed by all<br/>" +
                        "the execution hosts.</body></html>";
        fsPathLabel.setToolTipText(fsHint);
        fsPathField.setToolTipText(fsHint);
        fsPathBrowseButton.setToolTipText(fsHint);

        pipelineCheckbox = ComponentFactory.checkbox("Pipeline Server (" + Configuration.SIZE_PIPELINE + ")");
        pipelineCheckbox.setFont(checkboxFont);
        
        String pipelineHint = "<html><body>Select <b>Pipeline Server</b> if you want to install LONI Pipeline's<br/>" +
                              "server-side application.</body></html>";
        pipelineCheckbox.setToolTipText(pipelineHint);

        
        pipelineDescription = ComponentFactory.label("The LONI Pipeline Server side application.");

        sgeCheckbox = ComponentFactory.checkbox("Grid Engine (" + Configuration.SIZE_SGE + ")");
        sgeCheckbox.setFont(checkboxFont);

        String sgeHint = "<html><body>Select <b>Grid Engine</b> if you want to install job submission<br/>" +
                              "application for cluster with multiple hosts. This is optional<br/>" +
                              "and Pipeline can work without this component.</body></html>";
        sgeCheckbox.setToolTipText(sgeHint);

        sgeDescription = ComponentFactory.label("Grid Engine allows to submit module instances as jobs to existing Grid.");

        niToolsCheckbox = ComponentFactory.checkbox("Neuro Imaging Tools (up to " + Configuration.getToolsSize("neuroimaging") +")");
        niToolsCheckbox.setSelected(true);
        niToolsCheckbox.setFont(checkboxFont);

        biToolsCheckbox = ComponentFactory.checkbox("Bioinformatics Tools (up to " + Configuration.getToolsSize("bioinformatics") +")");
        biToolsCheckbox.setSelected(true);
        biToolsCheckbox.setFont(checkboxFont);
        
        String niToolsHint = "<html><body>Select <b>Neuro Imaging Tools</b> if you want to install external tools<br/>" +
                              "used by the LONI Server. This is optional and the list of the tools<br/>" +
                              "can be customized in step 6 ( NI Tools Configuration ).</body></html>";
        niToolsCheckbox.setToolTipText(niToolsHint);
        
        String biToolsHint = "<html><body>Select <b>Bioinformatics</b> if you want to install external tools<br/>" +
                              "used by the LONI Server. This is optional and the list of the tools<br/>" +
                              "can be customized in step 6 ( Bioinformatics Tools Configuration ).</body></html>";
        biToolsCheckbox.setToolTipText(biToolsHint);

        niToolsDescription = ComponentFactory.label("Most of the neuroimaging tools which are used by the LONI server.");
        biToolsDescription = ComponentFactory.label("Most of the bioinformatics tools which are used by the LONI server.");
        
        this.setPreferredSize(new Dimension(400, 400));
    }


    private void initLayout() {
        SpringLayout layout = new SpringLayout();
        setLayout(layout);

        add(titleLabel);
        layout.putConstraint(north, titleLabel, panelMargin, north, this);
        layout.putConstraint(west, titleLabel, panelMargin, west, this);

        add(topSeparator);
        layout.getConstraints(topSeparator).setConstraint(east, Spring.sum(Spring.constant(-panelMargin),layout.getConstraint(east, this)));
        layout.putConstraint(north, topSeparator, vDist, south, titleLabel);
        layout.putConstraint(west, topSeparator, panelMargin, west, this);

        add(fsPathLabel);
        layout.putConstraint(north, fsPathLabel, vDist * 3, south, titleLabel);
        layout.putConstraint(west, fsPathLabel, dist * 4, west, titleLabel);
        
        add(fsPathField);
        layout.putConstraint(north, fsPathField, 0, north, fsPathLabel);
        layout.putConstraint(west, fsPathField, dist, east, fsPathLabel);

        add(fsPathBrowseButton);
        layout.putConstraint(north, fsPathBrowseButton, -dist, north, fsPathLabel);
        layout.getConstraints(fsPathBrowseButton).setConstraint(east, Spring.sum(Spring.constant(-panelMargin),layout.getConstraint(east, this)));
        layout.putConstraint(west, fsPathBrowseButton, dist, east, fsPathField);
        
        add(middleSeparator);
        layout.getConstraints(middleSeparator).setConstraint(east, Spring.sum(Spring.constant(-panelMargin),layout.getConstraint(east, this)));
        layout.putConstraint(north, middleSeparator, vDist, south, fsPathBrowseButton);
        layout.putConstraint(west, middleSeparator, panelMargin, west, this);

        add(selectComponentsLabel);
        layout.putConstraint(north, selectComponentsLabel, vDist * 2, south, middleSeparator);
        layout.putConstraint(west, selectComponentsLabel, panelMargin, west, this);

        add(pipelineCheckbox);
        layout.putConstraint(north, pipelineCheckbox, vDist * 2, south, selectComponentsLabel);
        layout.putConstraint(west, pipelineCheckbox, 0, west, fsPathLabel);

        add(pipelineDescription);
        layout.putConstraint(north, pipelineDescription, vDist, south, pipelineCheckbox);
        layout.putConstraint(west, pipelineDescription, dist * 5, west, pipelineCheckbox);

        add(sgeCheckbox);
        layout.putConstraint(north, sgeCheckbox, vDist, south, pipelineDescription);
        layout.putConstraint(west, sgeCheckbox, 0, west, pipelineCheckbox);

        add(sgeDescription);
        layout.putConstraint(north, sgeDescription, vDist, south, sgeCheckbox);
        layout.putConstraint(west, sgeDescription, dist * 5, west, pipelineCheckbox);

        add(niToolsCheckbox);
        layout.putConstraint(north, niToolsCheckbox, vDist, south, sgeDescription);
        layout.putConstraint(west, niToolsCheckbox, 0, west, pipelineCheckbox);

        add(niToolsDescription);
        layout.putConstraint(north, niToolsDescription, vDist, south, niToolsCheckbox);
        layout.putConstraint(west, niToolsDescription, dist * 5, west, pipelineCheckbox);

        add(biToolsCheckbox);
        layout.putConstraint(north, biToolsCheckbox, vDist, south, niToolsDescription);
        layout.putConstraint(west, biToolsCheckbox, 0, west, pipelineCheckbox);

        add(biToolsDescription);
        layout.putConstraint(north, biToolsDescription, vDist, south, biToolsCheckbox);
        layout.putConstraint(west, biToolsDescription, dist * 5, west, pipelineCheckbox);
        
        add(bottomSeparator);
        layout.getConstraints(bottomSeparator).setConstraint(east, Spring.sum(Spring.constant(-panelMargin),layout.getConstraint(east, this)));
        layout.putConstraint(south, bottomSeparator, -panelMargin, south, this);
        layout.putConstraint(west, bottomSeparator, panelMargin, west, this);

        

    }


    private void initListeners() {

        fsPathBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File selectedDir = null;

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                String s = fsPathField.getText();
                File f = new File(s);

                if ( f.exists() )
                    fileChooser.setCurrentDirectory(f);


                if (fileChooser.showOpenDialog(sif) == JFileChooser.APPROVE_OPTION)
                    selectedDir = fileChooser.getSelectedFile();

                if (selectedDir != null)
                    fsPathField.setText(selectedDir.getPath() + File.separator);
            }

        });

        sgeCheckbox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if ( sgeCheckbox.isSelected() ) {
                    boolean ok = doSGECheckings(true);

                    if (!ok) {
                        sgeCheckbox.setSelected(false);
                    }
                    
                } else if ( Configuration.getConfig(Configuration.CONFIG_PIPELINE_PLUGIN) != null ) {
                    Configuration.setConfig(Configuration.CONFIG_PIPELINE_PLUGIN, null);
                }
            }

        });
      
    }


    public void saveUserInput() {
        Configuration.setConfig(Configuration.CONFIG_INSTALL_PIPELINE, String.valueOf(pipelineCheckbox.isSelected()));
        Configuration.setConfig(Configuration.CONFIG_INSTALL_SGE, String.valueOf(sgeCheckbox.isSelected()));
        Configuration.setConfig(Configuration.CONFIG_INSTALL_NI_TOOLS, String.valueOf(niToolsCheckbox.isSelected()));
        Configuration.setConfig(Configuration.CONFIG_INSTALL_BI_TOOLS, String.valueOf(biToolsCheckbox.isSelected()));

        String sharedPath = fsPathField.getText();

        if (!sharedPath.endsWith(File.separator)) {
            sharedPath = sharedPath + File.separator;
        }

        Configuration.setConfig(Configuration.CONFIG_SHARED_FILESYSTEM_PATH, sharedPath);
        Configuration.setConfig(Configuration.CONFIG_TOOLS_PATH, sharedPath + "tools" + File.separator);
    }

    public boolean checkUserInput() {

        if ( !pipelineCheckbox.isSelected() && !sgeCheckbox.isSelected() && !niToolsCheckbox.isSelected() && !biToolsCheckbox.isSelected()) {
            JOptionPane.showMessageDialog(sif, "At least one component must be selected",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String sharedPath = fsPathField.getText();

        if ( sharedPath.trim().length() == 0 ) {
            JOptionPane.showMessageDialog(sif, "Invalid path specified for shared file system.",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        File sharedPathFile = new File(sharedPath);

        if ( !sharedPathFile.exists() ) {
             JOptionPane.showMessageDialog(sif, "Specified directory " + sharedPath + " doesn't exist.",
                                          "Error", JOptionPane.ERROR_MESSAGE);
             return false;
        }

        if ( !sharedPathFile.isDirectory() ) {
             JOptionPane.showMessageDialog(sif, "Specified path " + sharedPath + " is not a directory",
                                          "Error", JOptionPane.ERROR_MESSAGE);
             return false;
        }

        if ( !sharedPath.endsWith(File.separator) )
            sharedPath = sharedPath + File.separator;

        Process p = null;
        try {
            p = Runtime.getRuntime().exec("stat -f -c %T " + sharedPath);
            InputStream is = p.getInputStream();
            byte [] buff = new byte[128];
            if ( is.read(buff) > 0 ) {
                 String t = new String(buff);
                 if ( !t.trim().equals("nfs") ) {
                    String exportsFilePath = "/etc/exports";
                    boolean found = false;

                    if ( new File(exportsFilePath).exists() ) {
                        // Also check /etc/exports file because this host can be the NFS server
                        FileInputStream fis =  new FileInputStream(exportsFilePath);

                         // Get the object of DataInputStream
                        DataInputStream in = new DataInputStream(fis);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String strLine;



                        while ((strLine = br.readLine()) != null) {
                            // with or without file separator
                            if ( strLine.contains(sharedPath + " ") ||
                                 strLine.contains(sharedPath.substring(0, sharedPath.length() - 1) + " ")) {
                                found =true;
                                break;
                            }
                        }
                     }
                    
                    if ( !found && JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(sif,"The provided directory seems not to be a network file shared (NFS) directory.\nNFS Directory is REQUIRED and installed files may not properly work without it.\n\n\tAre you sure you want to continue ?", "Non NFS Directory detected", JOptionPane.YES_NO_OPTION)) {
                         return false;
                    }
                 }
            }
        } catch ( Exception ex) {
            ex.printStackTrace();
        } finally {
            NativeCalls.releaseProcess(p);
        }

        // If there is already SGE installed, and user wants to install SGE, warn about it.
        if ( !sgeInstallations.isEmpty() && sgeCheckbox.isSelected() ) {
            StringBuilder sb = new StringBuilder();
            for ( String path : sgeInstallations.keySet() ) {
                sb.append("      ");
                sb.append(path);
                sb.append(" ( version ");
                sb.append(sgeInstallations.get(path));
                sb.append(" )\n");
            } 


            if ( JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(sif,"The installer found that there is already SGE installed at\n\n" +
                                                                            sb.toString() + 
                                                                            "\nThis installer will install SGE version " + SGE_VERSION + "\n" +
                                                                            "\nDo you want to continue ?", "Found Installed SGE", JOptionPane.YES_NO_OPTION)) {
                return false;
            }
        }

        // If Pipeline is selected but SGE is not and if there is SGE installed on the system, then we need to define the SGE_ROOT
        if ( pipelineCheckbox.isSelected() && !sgeCheckbox.isSelected() ) {
            String sgeRoot = null;
            if ( sgeInstallations.size() > 1 ) {
                sgeRoot = (String)JOptionPane.showInputDialog(
                                    sif,
                                    "  Please select which SGE installation you want Pipeline to use:   ",
                                    "Multiple SGE Installations detected",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    sgeInstallations.keySet().toArray(),
                                    "ham");


            } else if (!sgeInstallations.isEmpty()) {
                sgeRoot = sgeInstallations.keySet().iterator().next();
            }

            String configInstallSGE = Configuration.getConfig(Configuration.CONFIG_INSTALL_SGE);
            if (configInstallSGE == null) {
                if (sgeRoot != null) {
                    Configuration.setConfig(Configuration.CONFIG_SGE_ROOT, sgeRoot);
                } else {
                    return false;
                }
            }

        }

        return true;
    }




    private boolean doSGECheckings(boolean reportSGECheckErrors) {
        String hostname = Configuration.hostname;
        boolean success = true;

        try {
            if ( hostname.equals("localhost") || hostname.startsWith("127.0.") ) {
                if ( reportSGECheckErrors ) {
                  JOptionPane.showMessageDialog(sif, "It is not supported for a Grid Engine installation that the local hostname\n" +
                                   "is \"localhost\" and/or the IP address is like \"127.0.x.x\"\n\n" +
                                   "The hostname of this computer is " + hostname + "\n\n" +
                                   "After you fix the hostname make sure to LOG OFF from the system\n" +
                                   "and then log back in and start the installer. This will update some\n " +
                                   "configurations in the system needed to prevent future errors for \n" +
                                   "installing SGE and Pipeline", "Error", JOptionPane.ERROR_MESSAGE);

                }
                success = false;
            }

            if ( success ) {

                FileInputStream is =  new FileInputStream("/etc/hosts");

                 // Get the object of DataInputStream
                DataInputStream in = new DataInputStream(is);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;

                int lineNo = 0;
                while ((strLine = br.readLine()) != null) {
                    lineNo++;
                    if ( strLine.contains(hostname) ) {
                        if ( strLine.contains("localhost") || strLine.contains("127.0") ) {
                            if ( reportSGECheckErrors ) {
                                JOptionPane.showMessageDialog(sif, "It is not supported for a Grid Engine installation that the local hostname\n" +
                                                               "contains the hostname \"localhost\" and/or the IP address \"127.0.x.x\" of the\n"+
                                                               "loopback interface.\n" +
                                                               "The \"localhost\" hostname should be reserved for the loopback interface\n"+
                                                               "(\"127.0.0.1\") and the real hostname should be assigned to one of the\n" +
                                                               "physical or logical network interfaces of this machine.\n\n" +
                                                               "Please edit your /etc/hosts file's line "+ lineNo + " and try to check the checkbox again.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            success = false;
                            break;
                        }
                    }

                }
            }

        } catch ( Exception ex ) {
            ex.printStackTrace();
        }


        return success;

    }


    private void populateData() {

        String configFSLocation = Configuration.getConfig(Configuration.CONFIG_SHARED_FILESYSTEM_PATH);
        String configInstallSGE = Configuration.getConfig(Configuration.CONFIG_INSTALL_SGE);
        String configInstallPipeline = Configuration.getConfig(Configuration.CONFIG_INSTALL_PIPELINE);
        String configInstallNITools = Configuration.getConfig(Configuration.CONFIG_INSTALL_NI_TOOLS);
        String configInstallBITools = Configuration.getConfig(Configuration.CONFIG_INSTALL_BI_TOOLS);


        if ( configFSLocation == null )
            configFSLocation = "/usr/local";

        if ( configInstallSGE == null ) {
            sgeInstallations.clear();
            Process p = null;
            try {
                p = Runtime.getRuntime().exec("install_files/checkSGE.sh");
                InputStream is = p.getInputStream();
                byte [] buff = new byte[128];
                HashSet<String> counted = new HashSet<String>();
                while ( is.read(buff) > 0 ) {
                    String t = new String(buff);
                    if (t.startsWith("SGE")) {
                        String[] tokens = t.split("\\s");
                        String path = tokens[1];
                        if (path.endsWith(File.separator)) {
                            path = path.substring(0, path.length() - 1);
                        }
                        if (!counted.contains(path)) {
                            sgeInstallations.put(path, tokens[2]);
                            counted.add(path);
                        }
                    }
                }

                Arrays.fill(buff, (byte)0);
            } catch ( Exception ex) {
                ex.printStackTrace();
            } finally {
                if ( p != null )
                    NativeCalls.releaseProcess(p);
            }

            if (sgeInstallations.isEmpty()) {
                sgeCheckbox.setSelected(true);
            } else {
                sgeCheckbox.setSelected(false);
            }

            if (!doSGECheckings(false) && sgeCheckbox.isSelected()) {
                sgeCheckbox.setSelected(false);
            }

        } else {
            sgeCheckbox.setSelected(Boolean.parseBoolean(configInstallSGE));
        }

        if ( configInstallPipeline == null )
            pipelineCheckbox.setSelected(true);
        else
            pipelineCheckbox.setSelected(Boolean.parseBoolean(configInstallPipeline));


        if ( configInstallNITools == null )
            niToolsCheckbox.setSelected(true);
        else
            niToolsCheckbox.setSelected(Boolean.parseBoolean(configInstallNITools));
        
        if ( configInstallBITools == null )
            biToolsCheckbox.setSelected(true);
        else
            biToolsCheckbox.setSelected(Boolean.parseBoolean(configInstallBITools));
        
        fsPathField.setText(configFSLocation);

    }

    public void panelActivated() {

        populateData();
        sif.setNextEnabled(true);
    }

}

