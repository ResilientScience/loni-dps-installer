
package pipelineserverinstaller.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import javax.swing.event.TableModelEvent;
import pipelineserverinstaller.Package;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import pipelineserverinstaller.Configuration;
import pipelineserverinstaller.Constants;
import pipelineserverinstaller.NativeCalls;
import pipelineserverinstaller.PipelinePreferences;
import pipelineserverinstaller.UserdataUpdater;
import pipelineserverinstaller.gui.ComponentFactory;
import pipelineserverinstaller.gui.listeners.ToolsTableListener;
import pipelineserverinstaller.gui.models.ToolsTableModel;

/**
 *
 * @author Petros Petrosyan
 */
public class NIToolsConfigurationPanel extends AbstractStepPanel {

    // just to make the compiler stop complaining
    static final long serialVersionUID = 1L;

    private String north = SpringLayout.NORTH;
    private String south = SpringLayout.SOUTH;
    private String west = SpringLayout.WEST;
    private String east = SpringLayout.EAST;

    private final int panelMargin = 20;
    private final int dist = 5;
    private final int vDist = 7;

    private JLabel titleLabel;
    private JLabel toolsDescLabel;

    private JCheckBox executablesCheckbox;
    private JCheckBox serverLibCheckbox;

    private JLabel serverPrefsLabel;
    private JTextField serverPrefsField;
    private JButton serverPrefsBrowseButton;

    private JLabel totalSizeLabel;
    private JLabel toolLocationLabel;

    private JTable toolsTable;
    private ToolsTableModel toolsTableModel;
    private JScrollPane toolsTableScrollPane;


    private JTextArea descriptionTextArea;

    private String toolsPath;

    private String afniDesc = "Set of tools for processing, analyzing, and displaying functional MRI (fMRI) data.";
    private String airDesc = "Suite of tools for automated registration of 2D/3D images within and across subjects, populations and modalities.";
    private String brainSuiteDesc = "A comprehensive collection of tools designed for manual and automated tissue classification, surface modeling and image processing.";
    private String fslDesc = "A library of tools for analysis of structural and functional MRI and DTI imaging data.";
    private String freeSurferDesc = "A set of tools for reconstruction, modeling, analysis and visualization of the cortical surface using structural MRI data.";
    private String mincDesc = "A collection of advanced image processing and statistical analysis tools.";
    private String loniDesc = "A large suite of tools for computational neuroscience, neuroimaging genetics, image analysis, brain mapping, and atlasing using heterogeneous data format.";
    private String gammaDesc = "An open-source cross-platform data mining software package designed to analyze neuroimaging data.";
    private String itkDesc = "An an open-source software toolkit for performing registration and segmentation.";
    private String dtkDesc = "A software tool that can analyze fiber track data from diffusion MR imaging (DTI/DSI/HARDI/Q-Ball) tractography.";

    private String afniVersion = "2007_05_29_1644";
    private String airVersion = "5.2.5";
    private String brainSuiteVersion = "11a";
    private String fslVersion = "4.1.9";
    private String freeSurferVersion = "5.1.0";
    private String mincVersion = "01.19.2011";
    private String loniVersion = "-";
    private String gammaVersion = "1.1";
    private String itkVersion = "3.2.0";
    private String dtkVersion = "0.6.2.1";

    private boolean airSelected;
    private boolean brainSuiteSelected;
    private boolean fslSelected;
    private boolean freeSurferSelected;
    private boolean mincSelected;
    private boolean loniSelected;
    private boolean itkSelected;
    
    /** Creates a new instance of LicensePanel */
    public NIToolsConfigurationPanel() {
        initComponents();
        initLayout();
        initListeners();

        populateData();
    }

    private void initComponents() {
        titleLabel = ComponentFactory.label("<html><font size=\"4\"><b>Neuro Imaging Tools Configuration</b></font></html>");

        toolsDescLabel = ComponentFactory.label("Please select tools you want to install: ");

        toolsTableModel = new ToolsTableModel();
        toolsTable = new JTable(toolsTableModel);
        toolsTableScrollPane = new JScrollPane(toolsTable);

        TableCellRenderer leftJustifiedRenderer = null;
        TableCellRenderer centerJustifiedRenderer = null;
        TableCellRenderer rightJustifiedRenderer = null;

        leftJustifiedRenderer = new DefaultTableCellRenderer();

        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(SwingConstants.CENTER);
        centerJustifiedRenderer = tcr;

        tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(SwingConstants.RIGHT);
        rightJustifiedRenderer = tcr;

        TableColumnModel tcm = toolsTable.getColumnModel();

        toolsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        tcm.getColumn(0).setPreferredWidth(60);
        tcm.getColumn(1).setPreferredWidth(100);
        tcm.getColumn(2).setPreferredWidth(160);
        tcm.getColumn(3).setPreferredWidth(70);
        tcm.getColumn(4).setPreferredWidth(200);

        tcm.getColumn(3).setCellRenderer(centerJustifiedRenderer);
        tcm.getColumn(4).setCellRenderer(centerJustifiedRenderer);

        descriptionTextArea = ComponentFactory.textarea();
        descriptionTextArea.setBackground(getBackground());
        descriptionTextArea.setEditable(false);

        descriptionTextArea.setPreferredSize(new Dimension(100,45));
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setLineWrap(true);

        executablesCheckbox = ComponentFactory.checkbox("Install the executables and libraries for the selected packages");
        serverLibCheckbox = ComponentFactory.checkbox("Install Pipeline server library and package mapping for selected tools");
        serverPrefsField = ComponentFactory.textfield(18);
        serverPrefsField.setText("/usr/pipeline");
        
        serverPrefsLabel = ComponentFactory.label("Server Preferences File location:");
        serverPrefsBrowseButton = ComponentFactory.button("Browse...");


        totalSizeLabel        = ComponentFactory.label("Total size: " + Configuration.SIZE_NI_TOOLS);
        toolLocationLabel     = ComponentFactory.label("");


        this.setPreferredSize(new Dimension(400, 400));
    }


    private void initLayout() {
        SpringLayout layout = new SpringLayout();
        setLayout(layout);


        add(titleLabel);
        layout.putConstraint(north, titleLabel, 0, north, this);
        layout.putConstraint(west, titleLabel, panelMargin, west, this);
        
        add(executablesCheckbox);
        layout.putConstraint(north, executablesCheckbox, vDist * 2, south, titleLabel);
        layout.putConstraint(west, executablesCheckbox, panelMargin, west, this);

        add(serverLibCheckbox);
        layout.putConstraint(north, serverLibCheckbox, vDist, south, executablesCheckbox);
        layout.putConstraint(west, serverLibCheckbox, panelMargin, west, this);

        add(serverPrefsLabel);
        layout.putConstraint(north, serverPrefsLabel, vDist, south, serverLibCheckbox);
        layout.putConstraint(west, serverPrefsLabel, panelMargin, west, this);

        add(serverPrefsField);
        layout.putConstraint(north, serverPrefsField, vDist, south, serverLibCheckbox);
        layout.putConstraint(west, serverPrefsField, dist, east, serverPrefsLabel);

        add(serverPrefsBrowseButton);
        layout.putConstraint(north, serverPrefsBrowseButton, vDist, south, serverLibCheckbox);
        layout.putConstraint(west, serverPrefsBrowseButton, dist, east, serverPrefsField);

        add(toolsDescLabel);
        layout.putConstraint(north, toolsDescLabel, 0, south, serverPrefsBrowseButton);
        layout.putConstraint(west, toolsDescLabel, panelMargin, west, this);

        add(toolsTableScrollPane);
        layout.getConstraints(toolsTableScrollPane).setConstraint(east, Spring.sum(Spring.constant( -panelMargin ), layout.getConstraint(east, this)));
        layout.getConstraints(toolsTableScrollPane).setConstraint(south, Spring.sum(Spring.constant( -vDist ), layout.getConstraint(north, totalSizeLabel)));
        layout.putConstraint(north, toolsTableScrollPane, vDist, south, toolsDescLabel);
        layout.putConstraint(west, toolsTableScrollPane, 0, west, toolsDescLabel);

        add(totalSizeLabel);
        layout.putConstraint(south, totalSizeLabel, -vDist, north, descriptionTextArea);
        layout.putConstraint(east, totalSizeLabel, 0, east, toolsTableScrollPane);

        add(descriptionTextArea);
        layout.getConstraints(descriptionTextArea).setConstraint(east, Spring.sum(Spring.constant( -panelMargin ), layout.getConstraint(east, this)));
        layout.putConstraint(south, descriptionTextArea, -vDist, north, toolLocationLabel);
        layout.putConstraint(west, descriptionTextArea, 0, west, toolsTableScrollPane);

        add(toolLocationLabel);
        layout.putConstraint(south, toolLocationLabel, -panelMargin, south, this);
        layout.putConstraint(west, toolLocationLabel, panelMargin, west, this);


      
    }

    private void populateData() {
        
        toolsTableModel.addRow(new Object[] { true, "AFNI", afniVersion, Configuration.SIZE_TOOLS_AFNI, ToolsTableModel.INST_TYPE_AUTO,
                                afniDesc, Configuration.CONFIG_INSTALL_AFNI, Configuration.CONFIG_TOOLS_AFNI_VERSION});
        toolsTableModel.addRow(new Object[] { true, "AIR",  airVersion, Configuration.SIZE_TOOLS_AIR, ToolsTableModel.INST_TYPE_AUTO,
                                airDesc, Configuration.CONFIG_INSTALL_AIR, Configuration.CONFIG_TOOLS_AIR_VERSION});
        toolsTableModel.addRow(new Object[] { false, "BrainSuite", brainSuiteVersion, Configuration.SIZE_TOOLS_BRAINSUITE,ToolsTableModel.INST_TYPE_MANUAL,
                                brainSuiteDesc, Configuration.CONFIG_INSTALL_BRAINSUITE, Configuration.CONFIG_TOOLS_BRAINSUITE_VERSION});
        toolsTableModel.addRow(new Object[] { false, "FSL", fslVersion, Configuration.SIZE_TOOLS_FSL, ToolsTableModel.INST_TYPE_MANUAL,
                                fslDesc, Configuration.CONFIG_INSTALL_FSL, Configuration.CONFIG_TOOLS_FSL_VERSION});
        toolsTableModel.addRow(new Object[] { false, "FreeSurfer", freeSurferVersion, Configuration.SIZE_TOOLS_FREESURFER, ToolsTableModel.INST_TYPE_MANUAL,
                                freeSurferDesc, Configuration.CONFIG_INSTALL_FREESURFER, Configuration.CONFIG_TOOLS_FREESURFER_VERSION});
        toolsTableModel.addRow(new Object[] { false, "LONI", loniVersion, Configuration.SIZE_TOOLS_LONI, ToolsTableModel.INST_TYPE_AUTO,
                                loniDesc, Configuration.CONFIG_INSTALL_LONITOOLS, Configuration.CONFIG_TOOLS_LONITOOLS_VERSION});
        toolsTableModel.addRow(new Object[] { false, "MINC", mincVersion, Configuration.SIZE_TOOLS_MINC, ToolsTableModel.INST_TYPE_AUTO,
                                mincDesc, Configuration.CONFIG_INSTALL_MINC, Configuration.CONFIG_TOOLS_MINC_VERSION});
        toolsTableModel.addRow(new Object[] { false, "GAMMA", gammaVersion, Configuration.SIZE_TOOLS_GAMMA, ToolsTableModel.INST_TYPE_AUTO,
                                gammaDesc, Configuration.CONFIG_INSTALL_GAMMA, Configuration.CONFIG_TOOLS_GAMMA_VERSION});
        toolsTableModel.addRow(new Object[] { false, "ITK", itkVersion, Configuration.SIZE_TOOLS_ITK, ToolsTableModel.INST_TYPE_AUTO,
                                itkDesc, Configuration.CONFIG_INSTALL_ITK, Configuration.CONFIG_TOOLS_ITK_VERSION});
        toolsTableModel.addRow(new Object[] { false, "DTK", dtkVersion, Configuration.SIZE_TOOLS_DTK, ToolsTableModel.INST_TYPE_AUTO,
                                dtkDesc, Configuration.CONFIG_INSTALL_DTK, Configuration.CONFIG_TOOLS_DTK_VERSION});        
    }


    private void initListeners() {

        ToolsTableListener listener = new ToolsTableListener(toolsTable, toolsTableModel, this);
        toolsTable.getSelectionModel().addListSelectionListener(listener);
        toolsTable.addMouseListener(listener);
        toolsTable.addMouseMotionListener(listener);


        serverPrefsBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File selectedFile = null;

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setFileHidingEnabled(false);
                String s = serverPrefsField.getText();
                File f = new File(s);

                if ( f.exists() )
                    fileChooser.setCurrentDirectory(f);
                
                if (fileChooser.showOpenDialog(sif) == JFileChooser.APPROVE_OPTION)
                    selectedFile = fileChooser.getSelectedFile();

                if (selectedFile != null)
                    serverPrefsField.setText(selectedFile.getPath());
            }

        });


        serverLibCheckbox.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                boolean installPipeline = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_PIPELINE));

                if (installPipeline || !serverLibCheckbox.isSelected()) {
                    serverPrefsLabel.setVisible(false);
                    serverPrefsField.setVisible(false);
                    serverPrefsBrowseButton.setVisible(false);
                } else {
                    serverPrefsLabel.setVisible(true);
                    serverPrefsField.setVisible(true);
                    serverPrefsBrowseButton.setVisible(true);
                }
            }

        });

        toolsTableModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                List<String> sizeList = new LinkedList<String>();

                for ( int i = 0; i < toolsTable.getRowCount(); i++ ) {

                    Boolean isSelected = (Boolean)toolsTable.getValueAt(i, ToolsTableModel.COLUMN_SELECTED);

                    if ( !isSelected )
                        continue;

                    sizeList.add((String)toolsTable.getValueAt(i, ToolsTableModel.COLUMN_SIZE));
                }
                String totalSize = Configuration.calculateTotalSize(sizeList);
                totalSizeLabel.setText("Total size: " + totalSize);
            }

        });



    }

    public void saveUserInput() {
        Configuration.setConfig(Configuration.CONFIG_INSTALL_NI_EXECUTABLES, String.valueOf(executablesCheckbox.isSelected()));
        Configuration.setConfig(Configuration.CONFIG_INSTALL_NI_SERVERLIB, String.valueOf(serverLibCheckbox.isSelected()));
    }


    public boolean checkUserInput() {
        boolean installTools = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_NI_TOOLS));

        if ( !installTools )
            return true;

        boolean installPipeline = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_PIPELINE));
        boolean installSGE = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_SGE));

        if ( !installPipeline && serverLibCheckbox.isSelected() ) {
            String serverPrefsLocation = serverPrefsField.getText().trim();

            if (serverPrefsLocation.length() == 0) {
                JOptionPane.showMessageDialog(sif, "Please provide the server preferences location.\n" +
                                                   "If you don't have a Pipeline server or don't want module/workfow definitions of\n" +
                                                   "selected tools to be installed, you can uncheck the checkbox but note that server\n" +
                                                   "library files and package mapping will not be installed for selected tools.\n\n" +
                                                   "If you have Pipeline Server already installed then please provide its preferences.xml\n" +
                                                   "file location. It is needed because installer needs to know Pipeline's server library\n" +
                                                   "location, username and port number to properly install packages in Pipeline.",
                                                   "Error - Preferences file location is required",JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if ( new File(serverPrefsLocation).isDirectory() ) {
                if ( !serverPrefsLocation.endsWith(File.separator) )
                    serverPrefsLocation = serverPrefsLocation + File.separator;

                serverPrefsLocation = serverPrefsLocation + "preferences.xml";
            }


            File prefsLocationFile = new File(serverPrefsLocation);

            if ( !prefsLocationFile.exists() ) { 
                JOptionPane.showMessageDialog(sif, "Please provide existing server preferences location",
                                                   "Error - Preferences file location was invalid",JOptionPane.ERROR_MESSAGE);
                return false;
            }


            PipelinePreferences.releasePreferences();
            PipelinePreferences prefs = PipelinePreferences.getPreferencesFromFile(serverPrefsLocation);


            if ( PipelinePreferences.getPrefsFile() == null ) {
                 JOptionPane.showMessageDialog(sif, "Please provide existing server preferences location",
                                                   "Error - Preferences file location was invalid",JOptionPane.ERROR_MESSAGE);
                 return false;
            }

            String serverLibLocation = prefs.getPref(PipelinePreferences.KEY_ServerLibraryLocation);
            String serverPort = prefs.getPref(PipelinePreferences.KEY_ServerPort);
            String hostname = prefs.getPref(PipelinePreferences.KEY_Hostname);
            String pipelineUser = null;

            // By getting the owner of the preferences file we can get the Pipeline user.
            Process p = null;
            try {
                p = Runtime.getRuntime().exec("ls -l " + serverPrefsLocation);
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                String s = stdInput.readLine();
                
                if ( s != null ) {
                    String [] tokens = s.split(" ");

                    pipelineUser = tokens[2];
                } else { 
                    System.err.println("Failed to get Pipeline user from file " + serverPrefsLocation);
                }

                stdInput.close();
            } catch ( Exception ex ) {
                ex.printStackTrace();
            } finally {
                if ( p != null )
                    NativeCalls.releaseProcess(p);
            }

            if ( serverPort.trim().length() == 0 && serverLibLocation.trim().length() == 0 ) {
                JOptionPane.showMessageDialog(sif, "Please provide valid server preferences file",
                                                   "Error - Preferences file was invalid",JOptionPane.ERROR_MESSAGE);
                return false;
            }


            System.out.println("Preferences file: " + serverPrefsLocation);
            System.out.println("Preferences Owner: " + pipelineUser);
            System.out.println("ServerLib Location: " + serverLibLocation);
            System.out.println("Server Hostname: " + hostname);
            System.out.println("Server Port: " + serverPort);
            
            Configuration.setConfig(Configuration.CONFIG_PIPELINE_HOSTNAME, hostname);
            Configuration.setConfig(Configuration.CONFIG_PIPELINE_PORT, serverPort);
            Configuration.setConfig(Configuration.CONFIG_PIPELINE_SERVERLIB, serverLibLocation);
            Configuration.setConfig(Configuration.CONFIG_PIPELINE_USER, pipelineUser);
        }


        boolean atLeastOneSelected = false;

        for ( int i = 0; i < toolsTableModel.getRowCount(); i++ ) {
            Boolean isSelected = (Boolean)toolsTableModel.getValueAt(i, ToolsTableModel.COLUMN_SELECTED);
            String name =  (String)toolsTableModel.getValueAt(i, ToolsTableModel.COLUMN_NAME);

            if ( name.equals("AIR") )
                airSelected = isSelected;
            else if ( name.equals("BrainSuite") )
                brainSuiteSelected = isSelected;
            else if ( name.equals("FSL") )
                fslSelected = isSelected;
            else if ( name.equals("FreeSurfer"))
                freeSurferSelected = isSelected;
            else if ( name.equals("LONI"))
                loniSelected = isSelected;
            else if ( name.equals("MINC"))
                mincSelected = isSelected;
            else if ( name.equals("ITK"))
                itkSelected = isSelected;

            if ( isSelected && !atLeastOneSelected )
                atLeastOneSelected = true;
        }


        if ( loniSelected )  {
            StringBuilder notSelectedToolsBuilder = new StringBuilder();

            if ( !airSelected )
                notSelectedToolsBuilder.append("     AIR\n");
            if ( !brainSuiteSelected )
                notSelectedToolsBuilder.append("     BrainSuite\n");
            if ( !fslSelected )
                notSelectedToolsBuilder.append("     FSL\n");
            if ( !freeSurferSelected )
                notSelectedToolsBuilder.append("     FreeSurfer\n");
            if ( !mincSelected )
                notSelectedToolsBuilder.append("     MINC\n");
            if ( !itkSelected )
                notSelectedToolsBuilder.append("     ITK\n");            

            if ( notSelectedToolsBuilder.length() > 0 ) {
                int option = JOptionPane.showConfirmDialog(sif, "You have selected to install LONI Tools but did not select following tool(s):\n" +
                                                    notSelectedToolsBuilder.toString() +
                                                    "LONI Tools Package contains workflows which are using these tools. If you install\n" +
                                                    "LONI Tools without these tools, some workflows will give validation errors.\n\n" +
                                                    "It is recommended to select ALL these tools if you need to install LONI Tools.\n\n" +
                                                    "Are you sure that you want to continue without installing tools listed above ?",
                                                    "WARNING",JOptionPane.YES_NO_OPTION);

                if ( option != JOptionPane.YES_OPTION )
                    return false;
            }
        }


        if ( !atLeastOneSelected ) {

            if ( installPipeline || installSGE ) {
                int option = JOptionPane.showConfirmDialog(sif, "You haven't selected any tool to install\nAre you sure you want to proceed ?",
                                                   "None of the tools is selected",JOptionPane.YES_NO_OPTION);

                if ( option != JOptionPane.YES_OPTION )
                    return false;
            } else {
                JOptionPane.showMessageDialog(sif, "You haven't selected anything to install. Cannot continue the installation.\n" +
                                                    "If you wish to cancel the installation, please Press Cancel button or\n" +
                                                    "select something to install.",
                                                    "ERROR - Nothing is selected for installation",JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        // This function needs to be called not matter if packages will be used or not
        // because it sets Configuration of which tools are needed to be installed
        final List<Package> packages = getPackages();

        // Configure package mapping only when needed.
        if ( !serverLibCheckbox.isSelected() )
            return true;

        // Start the Userdata Updater thread.
        Thread t = new Thread() {
            public void run() {
                UserdataUpdater uu = new UserdataUpdater();

                List<Package> existingPackages = uu.getPackages();
                if ( existingPackages != null ) {
                     for ( Package p : packages ) {
                         for ( Package ep : existingPackages ) {
                             if ( p.getName().equals(ep.getName()) ) {
                                 existingPackages.remove(ep);
                                 break;
                             }
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


        if ( executablesCheckbox.isSelected() && loniSelected ) {
            JOptionPane.showMessageDialog(sif, "You have selected to install LONI Tools, which is very big in size. It can take long\n" +
                                               "time to download and install all the selected tools (" + totalSizeLabel.getText() +").\n\n" +
                                               "Please be patient during the installation and wait until it is complete.",
                                               "Message",JOptionPane.INFORMATION_MESSAGE);

        }
        
        return true;
    }


    public void tableRowChanged() {
        int row = toolsTable.getSelectedRow();
        if ( row == -1 )
            return ;

        String name =  (String)toolsTableModel.getValueAt(row, ToolsTableModel.COLUMN_NAME);
        String version =  (String)toolsTableModel.getValueAt(row, ToolsTableModel.COLUMN_VERSION);

        descriptionTextArea.setText((String)toolsTableModel.getValueAt(row, ToolsTableModel.COLUMN_DESCRIPTION));

        String location = toolsPath + name;
        if ( !version.equals("-") )
            location+="_" + version;

        toolLocationLabel.setText("Installation directory for " + name + ": " + location);
    }

    public void panelActivated() {
        sif.setNextEnabled(true);

        boolean installTools = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_NI_TOOLS));

        if ( !installTools )
            sif.redirect();

        if ( Configuration.getConfig(Configuration.CONFIG_INSTALL_NI_EXECUTABLES) == null )
            executablesCheckbox.setSelected(true);

        if ( Configuration.getConfig(Configuration.CONFIG_INSTALL_NI_SERVERLIB) == null )
            serverLibCheckbox.setSelected(true);
        
        String fsPath = Configuration.getConfig(Configuration.CONFIG_SHARED_FILESYSTEM_PATH);

        toolsPath = fsPath + "tools" + File.separator;

        Configuration.setConfig(Configuration.CONFIG_TOOLS_PATH, toolsPath);


    }
    
    
    private List<Package> getPackages() { 
        List<Package> packages = new LinkedList<Package>();
         
        for ( int i = 0; i < toolsTableModel.getRowCount(); i++ ) {
            Boolean isSelected = (Boolean)toolsTableModel.getValueAt(i, ToolsTableModel.COLUMN_SELECTED);
            String name =  (String)toolsTableModel.getValueAt(i, ToolsTableModel.COLUMN_NAME);
            String configfield_name =(String)toolsTableModel.getValueAt(i, ToolsTableModel.COLUMN_CONFIGFIELD_NAME);

            if ( isSelected ) {
                String version =  (String)toolsTableModel.getValueAt(i, ToolsTableModel.COLUMN_VERSION);
                String configfield_version =(String)toolsTableModel.getValueAt(i, ToolsTableModel.COLUMN_CONFIGFIELD_VERSION);

                String location = toolsPath + name;
                if (!version.equals("-")) {
                    if (!name.equals("FSL") && !name.equals("FreeSurfer") && !name.equals("DTK")) {
                        location += "-" + version;
                    }
                } else {
                    version = "*";
                }

                Configuration.setConfig(configfield_name, "true");
                Configuration.setConfig(configfield_version, version);
       

                if (name.equals("AIR")) {

                    Package p = new Package();
                    p.setLocation(location + File.separator + "8bit");
                    p.setVersion(version + "_8");
                    p.addVariable("AIR_DIR_8", location + File.separator + "8bit/bin");
                    p.setName(name);

                    packages.add(p);

                    Package p2 = new Package();
                    p2.setLocation(location + File.separator + "16bit");
                    p2.setVersion(version + "_16");
                    p.addVariable("AIR_DIR_16", location + File.separator + "16bit/bin");
                    p2.setName(name);
                    
                    packages.add(p2);

                } else if (name.equals("AFNI")) {
                    Package p = new Package();
                    p.setName("AFNI");
                    p.setLocation(location);
                    p.setVersion(version);
                    p.addVariable("AFNI_DIR", location);
                    packages.add(p);
                } else if (name.equals("MINC")) {
                    Package p = new Package();
                    p.setLocation(location);
                    p.setVersion(version);
                    p.setName("MNI");   // For backwards compatibilty we add both names MNI and MINC
                    p.addVariable("LD_LIBRARY_PATH", location + "/lib:" + location + "/lib64:${LD_LIBRARY_PATH}");
                    p.addVariable("PATH", location + "/bin:${PATH}");
                    p.addVariable("PERL5LIB", location + "/lib/perl5/5.8.8:${PERL5LIB}");

                    packages.add(p);

                    Package p2 = new Package();
                    p2.setLocation(location);
                    p2.setVersion(version);
                    p2.setName(name);
                    p2.addVariable("LD_LIBRARY_PATH", location + "/lib:" + location + "/lib64:${LD_LIBRARY_PATH}");
                    p2.addVariable("PATH", location + "/bin:${PATH}");
                    p2.addVariable("PERL5LIB", location + "/lib/perl5/5.8.8:${PERL5LIB}");

                    packages.add(p2);

                } else if ( name.equals("BrainSuite")) {
                    Package p = new Package();
                    p.setLocation(location);
                    p.setVersion(version);
                    p.setName("BrainSuite11a"); // For backwards compatibilty we add both names BrainSuite11a and BrainSuite

                    packages.add(p);

                    Package p2 = new Package();
                    p2.setLocation(location);
                    p2.setVersion(version);
                    p2.setName(name);

                    packages.add(p2);
                } else if (name.equals("GAMMA")){
                    Package p = new Package();
                    p.setLocation(location);
                    p.setVersion(version);
                    p.setName("GAMMA");
                    p.addVariable("GAMMA_Suite", location);
                    packages.add(p);
                } else if (name.equals("ITK")){
                    Package p = new Package();
                    p.setLocation(location);
                    p.setVersion(version);
                    p.setName("ITK");
                    p.addVariable("LD_LIBRARY_PATH", location + "/lib/InsightToolkit:${LD_LIBRARY_PATH}");
                    packages.add(p);
                } else if (name.equals("LONI") ) {
                    
                    List<Package> loniPackages = new LinkedList<Package>();
                      
                    // LONI Tools
                    Package pLoni = new Package(name,version,toolsPath);
                    pLoni.addVariable("ieckstei",toolsPath + "LONI/apps/ieckstei");                    
                    pLoni.addVariable("DCM2RAWPATH",toolsPath + "LONI/apps/shattuck/dcm2raw");                    
                    pLoni.addVariable("LONIAPPS",toolsPath + "LONI/apps");
                    pLoni.addVariable("FSLDIR", toolsPath + "FSL-" + fslVersion);
                    String serverPrefsLocation = serverPrefsField.getText().trim();
                    if (!serverPrefsLocation.endsWith(File.separator)) {
                        serverPrefsLocation = serverPrefsLocation + File.separator;
                    }
                    serverPrefsLocation = serverPrefsLocation + "preferences.xml";
                    if (new File(serverPrefsLocation).exists()) {
                        PipelinePreferences prefs = PipelinePreferences.getPreferencesFromFile(serverPrefsLocation);
                        String smartlineDir = new File(prefs.getPref(PipelinePreferences.KEY_ServerLibraryLocation)).getParent() + "/utilities/smartline";
                        pLoni.addVariable("SMARTLINE_DIR", smartlineDir);
                    }
                    loniPackages.add(pLoni);
                    
                    // AAL data
                    Package pAAL = new Package("AAL", "*", toolsPath + "LONI/data/Atlases/AAL");
                    loniPackages.add(pAAL);

                    // AD data
                    Package pAD = new Package("AD", "*", toolsPath + "LONI/data/Atlases/AD_Atlas");
                    loniPackages.add(pAD);
                    
                    // ANTs tools
                    Package pANTs = new Package("ANTs", "*", toolsPath + "LONI/workflows/ANTS");
                    pANTs.addVariable("ANTSPATH",toolsPath + "LONI/workflows/ANTS");
                    loniPackages.add(pANTs);
                    
                    // DHM tools
                    Package pDHM = new Package("DHM", "*", toolsPath + "LONI/apps/yshi");
                    pDHM.addVariable("DHMDIR",toolsPath + "LONI/apps/yshi");
                    loniPackages.add(pDHM);
                    
                    // ESW tools
                    Package pESW = new Package("Elastic Surface Warp", "*", toolsPath + "LONI/apps/rcabeen/ESW");
                    loniPackages.add(pESW);
                    
                    // JHU DTI data
                    Package pJHUDTI = new Package("JHU DTI", "*", toolsPath + "LONI/data/Atlases/JHU_DTI_based");
                    loniPackages.add(pJHUDTI);
                    
                    // JHU DTI data
                    Package pJHUWM = new Package("JHU WM Segments", "*", toolsPath + "LONI/data/Atlases/JHU_WMSegments");
                    loniPackages.add(pJHUWM);
                    
                    // MiND tools
                    Package pMiND = new Package("MiND", "*", toolsPath + "LONI/apps/vpatel/bin");
                    loniPackages.add(pMiND);
                    
                    // Mouse Atlas data
                    Package pMouseAtlas = new Package("MouseAtlas", "*", toolsPath + "LONI/data/ccb");
                    loniPackages.add(pMouseAtlas);
                    
                    // MDT tools
                    Package pMDT = new Package("Minimum Distance Template", "*", toolsPath + "LONI/workflows/MinimumDistanceTemplate");
                    pMDT.addVariable("MDT_DIR", toolsPath + "LONI/workflows/MinimumDistanceTemplate");
                    loniPackages.add(pMDT);
                    
                    // Postmortem data
                    Package pPostmortem = new Package("Postmortem", "*", toolsPath + "LONI/data/Atlases");
                    loniPackages.add(pPostmortem);                     
                    
                    // Automatic Registration Toolbox version 1.0
                    Package pAutoRToolbox = new Package("Automatic Registration Toolbox", "1.0",toolsPath + "LONI/apps/gprasad/art");
                    loniPackages.add(pAutoRToolbox);
                    
                    // DIRAC version 2.2.0
                    Package pDirac = new Package("DIRAC", "2.2.0", toolsPath + "LONI/apps/vpatel");
                    pDirac.addVariable("DIRAC_DIR", toolsPath + "LONI/apps/vpatel/bin");
                    pDirac.addVariable("FSLPATH", toolsPath + "FSL-" + fslVersion);
                    pDirac.addVariable("PYTHONPATH", toolsPath + "LONI/apps/vpatel/lib:${PYTHONPATH}");
                    loniPackages.add(pDirac);
                    
                    // IRMA version 1.0	
                    Package pIRMA = new Package("IRMA", "1.0", toolsPath + "LONI/apps/kleung/irma");
                    loniPackages.add(pIRMA);
                    
                    // SSMA version 1.0.1
                    Package pSSMA = new Package("SSMA", "1.0.1",toolsPath + "LONI/apps/kleung");
                    pSSMA.addVariable("ssma_root", toolsPath + "LONI/apps/kleung");
                    pSSMA.addVariable("FREESURFER_HOME", toolsPath + "FreeSurfer-" + fslVersion);
                    pSSMA.addVariable("AIR16", toolsPath + "AIR-" + airVersion + "_16");
                    pSSMA.addVariable("AIR8", toolsPath + "AIR-" + airVersion + "_8");
                    pSSMA.addVariable("FSL", toolsPath + "FSL-" + fslVersion);
                    pSSMA.addVariable("BRAINSUITE", toolsPath + "BrainSuite-" + fslVersion);
                    pSSMA.addVariable("SSMADIR", toolsPath + "LONI/apps/kleung");
                    loniPackages.add(pSSMA);
                    
                    // LONI Statistics version 2.6
                    Package pLoniStats = new Package("LONI Statistics","*",toolsPath + "LONI/scripts/loniStats");
                    pLoniStats.addVariable("JAVABINARY", Constants.javaHomeDir() + "/java");
                    pLoniStats.addVariable("SOCR", toolsPath + "LONI/jars/SOCR_Statistics");
                    pLoniStats.addVariable("ieckstei", toolsPath + "LONI/apps/ieckstei");
                    loniPackages.add(pLoniStats); 
                    
                    // CCB version 1.0
                    Package pCCB = new Package("CCB","1.0",toolsPath);
                    loniPackages.add(pCCB); 
                    
                    // SPH surface analysis version 1.0
                    Package pSPHSA = new Package("SPH surface analysis","*",toolsPath + "LONI/workflows/LocalShapeAnalysis");
                    pSPHSA.addVariable("SPH_DIR", toolsPath + "LONI/workflows/LocalShapeAnalysis");                    
                    loniPackages.add(pSPHSA); 
                    
                    // ITK version 3.2.0
                    Package pITK = new Package("ITK","3.2.0",toolsPath);
                    loniPackages.add(pITK); 
                    
                    // ShapeTools version 1.3.2		/usr/local/tools/LONI/jars/ShapeTools
                    Package pShapeTools = new Package("ShapeTools","*", toolsPath + "LONI/jars/ShapeTools");
                    pShapeTools.addVariable("JAVA_BINARY", Constants.javaHomeDir() + "/java");
                    pShapeTools.addVariable("SHAPETOOLS_DIR", toolsPath + "LONI/jars/ShapeTools");
                    loniPackages.add(pShapeTools);
                    
                    // R version 2.4.1
                    Package pR = new Package("R","2.4.1", toolsPath + "LONI/apps");
                    loniPackages.add(pR);
                    
                    // LONI DTI Suite version 1.0
                    Package pDTISuite = new Package("LONI DTI Suite","*",toolsPath + "LONI/apps/kclark");
                    pDTISuite.addVariable("MYPATH", toolsPath + "LONI/apps/kclark");
                    loniPackages.add(pDTISuite); 
                    
                    // PCA Registration version 1.0
                    Package pPCAR = new Package("PCA Registration","*", toolsPath + "LONI/apps/gprasad/");
                    loniPackages.add(pPCAR);
                    
                    // SVPASEG version 2.0		
                    Package pSVPASEG = new Package("SVPASEG","*", toolsPath + "LONI/apps/svpaseg");
                    pSVPASEG.addVariable("SVPASEG_PATH", toolsPath + "LONI/apps/svpaseg");
                    loniPackages.add(pSVPASEG);
                    
                    // Utilities version 1.0
                    Package pUtils = new Package("Utilities","1.0",toolsPath);
                    loniPackages.add(pUtils);
                    
                    // WAIR version 2.0
                    Package pWAIR = new Package("WAIR","2.0",toolsPath + "LONI/apps/dinov");
                    loniPackages.add(pWAIR);
                    
                    // fMRI version 1.0
                    Package pfMRI = new Package("fMRI","1.0",location + "/data");
                    loniPackages.add(pfMRI);
                    
                    // ICBM version 1.0		/usr/local/tools/LONI/data
                    Package pICBM = new Package("ICBM","1.0",location + "/data/Atlases/ICBM");
                    loniPackages.add(pICBM);
                    
                    // Training version 1.0
                    Package pTraining = new Package("Training","1.0",location + "/data/Pipeline_Workflow_Test_Data/Training");
                    loniPackages.add(pTraining);
                    
                    // FDG version 1.0
                    Package pFDG = new Package("FDG","*",toolsPath + "LONI/data/Pipeline_Workflow_Test_Data/FDG");
                    loniPackages.add(pFDG);
                    
                    // LPBA version 1.0
                    Package pLPBA = new Package("LPBA","*",toolsPath + "LONI/data");
                    loniPackages.add(pLPBA);
                    
                    // LONI TDA version 1.0
                    Package pLONI_TDA = new Package("LONI TDA","1.0",toolsPath);
                    loniPackages.add(pLONI_TDA);
                    
                    // RAT version 1.0
                    Package pRAT = new Package("RAT","1.0",location + "/data");
                    loniPackages.add(pRAT);
                    
                    // Java
                    Package pJava = new Package("Java", "*", Constants.javaHomeDir());
                    loniPackages.add(pJava);
                    
                    // add LONI Packages to all packages list
                    packages.addAll(loniPackages);
                    
                } else  {

                    Package p = new Package();
                    p.setLocation(location);
                    p.setVersion(version);
                    p.setName(name);


                    if ( name.equals("FSL") ) {
                        p.addVariable("FSLDIR", location);
                        p.addSource("$FSLDIR/etc/fslconf/fsl.sh");
                    } else if ( name.equals("FreeSurfer") ){
                        p.addVariable("TOOLSDIR", toolsPath);
                        p.addVariable("FREESURFER_HOME", location);
                    } else if ( name.equals("DTK")){
                        p.addVariable("DIFFTK_DIR", location);
                        p.addVariable("DSI_PATH", location + "/matrices");                        
                    }
                    
                    packages.add(p);
                }
            } else {
                Configuration.setConfig(configfield_name, "false");
            }

        }
        
        return packages;
    }

}

