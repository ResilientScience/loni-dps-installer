
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
public class BioinformaticsToolsConfigurationPanel extends AbstractStepPanel {

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

    private String embossDesc = "A free Open Source software analysis package specially developed for the needs of the molecular biology (e.g. EMBnet) user community.";
    private String picardDesc = "Java-based command-line utilities that manipulate SAM files, and a Java API (SAM-JDK) for creating new programs that read and write SAM files.";
    private String msaDesc = "A universal, platform independent, data analysis tool.";
    private String batwingDesc = "Inferences from DNA data: population histories, evolutionary processes and forensic match probabilities.";
    private String bayesassDesc = "Bayesian Inference of Recent Migration Using Multilocus Genotypes.";
    private String formatomaticDesc = "Creates infiles for several population genetic analysis programs from csv, genepop or convert* (excel) files.";
    private String genepopDesc = "A population genetics software package.";
    private String migrateDesc = "Estimates effective population sizes and past migration rates between n population assuming a migration matrix model with asymmetric migration rates and different subpopulation sizes..";
    private String gwassDesc = "A genotype imputation and phasing program.";
    private String mrfastDesc = "Designed to map short reads generated with the Illumina platform to reference genome assemblies; in a fast and memory-efficient manner.";
    private String bowtieDesc = "An ultrafast, memory-efficient short read aligner.";
    private String samtoolsDesc = "Provides various utilities for manipulating alignments in the SAM format, including sorting, merging, indexing and generating alignments in a per-position format.";
    private String plinkDesc = "A free, open-source whole genome association analysis toolset, designed to perform a range of basic, large-scale analyses in a computationally efficient manner.";
    private String maqDesc = "Builds assembly by mapping short reads to reference sequences.";
    private String miblastDesc = "A tool for efficiently BLASTing a batch of nucleotide sequence queries.";

    private String embossVersion = "6.3.1";
    private String picardVersion = "1.43";
    private String msaVersion = "4.05";
    private String batwingVersion = "0.1";
    private String bayesassVersion = "3.0";
    private String formatomaticVersion = "0.8.1";
    private String genepopVersion = "4.1";
    private String migrateVersion = "3.2.7";
    private String gwassVersion = "2.0";
    private String mrfastVersion = "2.0.0.5";
    private String bowtieVersion = "0.12.7";
    private String samtoolsVersion = "0.1.12a";
    private String plinkVersion = "1.07";
    private String maqVersion = "0.7.1";
    private String miblastVersion = "6.1";
    
    /** Creates a new instance of LicensePanel */
    public BioinformaticsToolsConfigurationPanel() {
        initComponents();
        initLayout();
        initListeners();

        populateData();
    }

    private void initComponents() {
        titleLabel = ComponentFactory.label("<html><font size=\"4\"><b>Bioinformatics Tools Configuration</b></font></html>");

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


        totalSizeLabel        = ComponentFactory.label("Total size: " + Configuration.SIZE_BI_TOOLS);
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
        layout.putConstraint(west, serverPrefsLabel, dist * 4, west, serverLibCheckbox);

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

        toolsTableModel.addRow(new Object[]{true, "EMBOSS", embossVersion, Configuration.SIZE_TOOLS_EMBOSS, ToolsTableModel.INST_TYPE_AUTO,
                    embossDesc, Configuration.CONFIG_INSTALL_EMBOSS, Configuration.CONFIG_TOOLS_EMBOSS_VERSION});
        toolsTableModel.addRow(new Object[]{true, "Picard", picardVersion, Configuration.SIZE_TOOLS_PICARD, ToolsTableModel.INST_TYPE_AUTO,
                    picardDesc, Configuration.CONFIG_INSTALL_PICARD, Configuration.CONFIG_TOOLS_PICARD_VERSION});
        toolsTableModel.addRow(new Object[]{true, "MSA", msaVersion, Configuration.SIZE_TOOLS_MSA, ToolsTableModel.INST_TYPE_AUTO,
                    msaDesc, Configuration.CONFIG_INSTALL_MSA, Configuration.CONFIG_TOOLS_MSA_VERSION});
        toolsTableModel.addRow(new Object[]{false, "BATWING", batwingVersion, Configuration.SIZE_TOOLS_BATWING, ToolsTableModel.INST_TYPE_AUTO,
                    batwingDesc, Configuration.CONFIG_INSTALL_BATWING, Configuration.CONFIG_TOOLS_BATWING_VERSION});
        toolsTableModel.addRow(new Object[]{false, "BayesAss", bayesassVersion, Configuration.SIZE_TOOLS_BAYESASS, ToolsTableModel.INST_TYPE_AUTO,
                    bayesassDesc, Configuration.CONFIG_INSTALL_BAYESASS, Configuration.CONFIG_TOOLS_BAYESASS_VERSION});
        toolsTableModel.addRow(new Object[]{false, "Formatomatic", formatomaticVersion, Configuration.SIZE_TOOLS_FORMATOMATIC, ToolsTableModel.INST_TYPE_AUTO,
                    formatomaticDesc, Configuration.CONFIG_INSTALL_FORMATOMATIC, Configuration.CONFIG_TOOLS_FORMATOMATIC_VERSION});
        toolsTableModel.addRow(new Object[]{false, "GENEPOP", genepopVersion, Configuration.SIZE_TOOLS_GENEPOP, ToolsTableModel.INST_TYPE_AUTO,
                    genepopDesc, Configuration.CONFIG_INSTALL_GENEPOP, Configuration.CONFIG_TOOLS_GENEPOP_VERSION});
        toolsTableModel.addRow(new Object[]{false, "Migrate", migrateVersion, Configuration.SIZE_TOOLS_MIGRATE, ToolsTableModel.INST_TYPE_AUTO,
                    migrateDesc, Configuration.CONFIG_INSTALL_MIGRATE, Configuration.CONFIG_TOOLS_MIGRATE_VERSION});
        toolsTableModel.addRow(new Object[]{false, "mrFAST", mrfastVersion, Configuration.SIZE_TOOLS_MRFAST, ToolsTableModel.INST_TYPE_AUTO,
                    mrfastDesc, Configuration.CONFIG_INSTALL_MRFAST, Configuration.CONFIG_TOOLS_MRFAST_VERSION});
        toolsTableModel.addRow(new Object[]{false, "GWASS", gwassVersion, Configuration.SIZE_TOOLS_GWASS, ToolsTableModel.INST_TYPE_AUTO,
                    gwassDesc, Configuration.CONFIG_INSTALL_GWASS, Configuration.CONFIG_TOOLS_GWASS_VERSION});
        toolsTableModel.addRow(new Object[]{false, "Bowtie", bowtieVersion, Configuration.SIZE_TOOLS_BOWTIE, ToolsTableModel.INST_TYPE_AUTO,
                    bowtieDesc, Configuration.CONFIG_INSTALL_BOWTIE, Configuration.CONFIG_TOOLS_BOWTIE_VERSION});
        toolsTableModel.addRow(new Object[]{false, "SamTools", samtoolsVersion, Configuration.SIZE_TOOLS_SAMTOOLS, ToolsTableModel.INST_TYPE_AUTO,
                    samtoolsDesc, Configuration.CONFIG_INSTALL_SAMTOOLS, Configuration.CONFIG_TOOLS_SAMTOOLS_VERSION});
        toolsTableModel.addRow(new Object[]{false, "PLINK", plinkVersion, Configuration.SIZE_TOOLS_PLINK, ToolsTableModel.INST_TYPE_AUTO,
                    plinkDesc, Configuration.CONFIG_INSTALL_PLINK, Configuration.CONFIG_TOOLS_PLINK_VERSION});
        toolsTableModel.addRow(new Object[]{false, "MAQ", maqVersion, Configuration.SIZE_TOOLS_MAQ, ToolsTableModel.INST_TYPE_AUTO,
                    maqDesc, Configuration.CONFIG_INSTALL_MAQ, Configuration.CONFIG_TOOLS_MAQ_VERSION});
        toolsTableModel.addRow(new Object[]{false, "miBLAST", miblastVersion, Configuration.SIZE_TOOLS_MIBLAST, ToolsTableModel.INST_TYPE_AUTO,
                    miblastDesc, Configuration.CONFIG_INSTALL_MIBLAST, Configuration.CONFIG_TOOLS_MIBLAST_VERSION});
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

                if ( installPipeline || !serverLibCheckbox.isSelected() ) {
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
        Configuration.setConfig(Configuration.CONFIG_INSTALL_BI_EXECUTABLES, String.valueOf(executablesCheckbox.isSelected()));
        Configuration.setConfig(Configuration.CONFIG_INSTALL_BI_SERVERLIB, String.valueOf(serverLibCheckbox.isSelected()));
    }


    public boolean checkUserInput() {
        boolean installTools = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_BI_TOOLS));

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
                p = Runtime.getRuntime().exec("ls -l " + serverPrefsLocation );
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
            
            if ( isSelected && !atLeastOneSelected )
                atLeastOneSelected = true;
        }


        if ( !atLeastOneSelected ) {

            if ( installPipeline || installSGE ) {
                int option = JOptionPane.showConfirmDialog(sif, "You haven't selected any tool to install\nAre you sure you want to start the installation ?",
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
                             if ( p.getName().equals(ep.getName()) && p.getVersion().equals(ep.getVersion()) ) {
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

        boolean installTools = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_BI_TOOLS));

        if ( !installTools )
            sif.redirect();

        if ( Configuration.getConfig(Configuration.CONFIG_INSTALL_BI_EXECUTABLES) == null )
            executablesCheckbox.setSelected(true);

        if ( Configuration.getConfig(Configuration.CONFIG_INSTALL_BI_SERVERLIB) == null )
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
                if ( !version.equals("-") )
                    location+="-" + version;
                else
                    version = "*";

                Configuration.setConfig(configfield_name, "true");
                Configuration.setConfig(configfield_version, version);

                Package p = new Package();
                p.setLocation(location);
                p.setVersion(version);
                p.setName(name);
                
                if ( name.equals("EMBOSS") ) {
                    p.addVariable("EMBOSS_ACDROOT", location + "/acd");
                    p.addVariable("PATH", location  + ":${PATH}");
                } else if ( name.equals("Picard") ) {
                    p.addVariable("java_dir", Constants.javaHomeDir());
                    p.addVariable("PICARDPATH", location);
                    String rhome = System.getenv("RHOME");
                    if (rhome == null)
                        rhome = toolsPath + "/LONI/apps/R";
                    p.addVariable("RHOME", rhome);
                    p.addVariable("PATH", rhome + "/bin:${PATH}");
                } else if ( name.equals("MSA")) {
                    p.addVariable("MSA_DIR", location);
                } else if (name.equals("Migrate") ) {
                    p.addVariable("MIGRATE_DIR", location);
                }
                
                packages.add(p);
            } else {
                Configuration.setConfig(configfield_name, "false");
            }

        }
        
        
        return packages;
    }
}

