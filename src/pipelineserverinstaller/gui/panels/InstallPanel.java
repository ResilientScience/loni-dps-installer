package pipelineserverinstaller.gui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import pipelineserverinstaller.Configuration;
import pipelineserverinstaller.Constants;
import pipelineserverinstaller.NativeCalls;
import pipelineserverinstaller.UserdataUpdater;
import pipelineserverinstaller.gui.ComponentFactory;
import pipelineserverinstaller.Package;

/**
 *
 * @author Petros Petrosyan
 */
public class InstallPanel extends AbstractStepPanel {

    // just to make the compiler stop complaining
    static final long serialVersionUID = 1L;

    private String north = SpringLayout.NORTH;
    private String south = SpringLayout.SOUTH;
    private String west = SpringLayout.WEST;
    private String east = SpringLayout.EAST;

    private SpringLayout layout;

    private final int panelMargin = 20;
    private final int dist = 5;
    private final int vDist = 5;

    private int topMargin = 13;
    
    private JLabel titleLabel;

    private JLabel plTitleLabel;
    private JLabel sgeTitleLabel;
    private JLabel niToolsTitleLabel;
    private JLabel biToolsTitleLabel;
    private JLabel totalTitleLabel;

    private JLabel plActionLabel;
    private JLabel plDetailLabel;

    private JLabel sgeActionLabel;
    private JLabel sgeDetailLabel;
    
    private JLabel niToolsActionLabel;
    private JLabel niToolsDetailLabel;
    private JLabel biToolsActionLabel;
    private JLabel biToolsDetailLabel;
    
    private JLabel logLabel;

    
    private JLabel plPostInstallLabel;
    private JComboBox plPostInstallComboBox;


    private JProgressBar plProgressBar;
    private JProgressBar sgeProgressBar;
    private JProgressBar niToolsProgressBar;
    private JProgressBar biToolsProgressBar;
    private JProgressBar totalProgressBar;


    private JSeparator plSeparator;
    private JSeparator sgeSeparator;
    private JSeparator niToolsSeparator;
    private JSeparator biToolsSeparator;
    private JSeparator totalSeparator;
    private JSeparator bottomSeparator;


    private JTextArea logsDialogTextArea;

    private JButton detailsButton;
    private JButton logsButton;

    private List<String> needsActionQueue;
    
    private boolean needsSGE;
    private boolean needsNITools;
    private boolean needsBITools;
    private boolean needsPipeline;
    private boolean detailedView;
    private int plStepsTotal = 0;
    private int sgeStepsTotal = 0;
    private int niToolsStepsTotal = 0;
    private int biToolsStepsTotal = 0;
    private int totalStepsTotal = plStepsTotal + sgeStepsTotal + niToolsStepsTotal + biToolsStepsTotal;

    private int plStepsPassed;
    private int sgeStepsPassed;
    private int niToolsStepsPassed;
    private int biToolsStepsPassed;
    private int totalStepsPassed;

    private String logFileName;
    
    private boolean headless;

    private boolean isFollowingTheLastLine = true;

    ImageIcon loadingIcon = new ImageIcon(this.getClass().getResource("/graphics/Loading.gif"));

    private ManualActionThread manualActionThread;

    private final int MANUAL_ACTION_NEEDED_FOR_PIPELINE = 1;
    private final int MANUAL_ACTION_NEEDED_FOR_SGE      = 2;
    private final int MANUAL_ACTION_NEEDED_FOR_TOOLS    = 3;

    private final String ACTION_START_SERVER        = "   Start the server   ";
    private final String ACTION_START_CLIENT        = "   Start the client   ";
    private final String ACTION_CONFIGURE_SERVER    = "   Configure the server   ";
    
    /** Creates a new instance of LicensePanel */
    public InstallPanel(boolean headless) {
        this.headless = headless;
    }

    private void initComponents() {
        titleLabel = ComponentFactory.label("<html><font size=\"4\"><b>Installation is in progress and can take a while, please wait...</b></font></html>");

        needsSGE = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_SGE));
        needsNITools = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_NI_TOOLS));
        needsBITools = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_BI_TOOLS));
        needsPipeline = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_PIPELINE));

        if ( needsPipeline ) {

            plActionLabel = ComponentFactory.label("");
            plDetailLabel = ComponentFactory.label("");

            plActionLabel.setIcon(loadingIcon);

            plProgressBar = new JProgressBar();
            plProgressBar.setStringPainted(true);

            plTitleLabel = ComponentFactory.label("<html><b>Pipeline Server</b></html>");
            plSeparator = new JSeparator(JSeparator.HORIZONTAL);

            plPostInstallComboBox = ComponentFactory.combobox();
            plPostInstallComboBox.addItem(" Select what to do now or wait until everything is complete ");
            plPostInstallComboBox.addItem(ACTION_CONFIGURE_SERVER);
            plPostInstallComboBox.addItem(ACTION_START_CLIENT);
            plPostInstallLabel = ComponentFactory.label("");

            plPostInstallLabel.setVisible(false);
            plPostInstallComboBox.setVisible(false);

        }


        bottomSeparator = new JSeparator(JSeparator.HORIZONTAL);
        
        if ( needsSGE ) {

            sgeActionLabel = ComponentFactory.label("");
            sgeDetailLabel = ComponentFactory.label("");

            sgeActionLabel.setIcon(loadingIcon);
            
            sgeProgressBar = new JProgressBar();
            sgeProgressBar.setStringPainted(true);
            
            sgeTitleLabel = ComponentFactory.label("<html><b>Grid Engine</b></html>");
            sgeSeparator = new JSeparator(JSeparator.HORIZONTAL);
        }


        if ( needsNITools ) {
            niToolsActionLabel = ComponentFactory.label("");
            niToolsDetailLabel = ComponentFactory.label("");

            niToolsActionLabel.setIcon(loadingIcon);
            
            niToolsProgressBar = new JProgressBar();
            niToolsProgressBar.setStringPainted(true);           
            
            niToolsTitleLabel = ComponentFactory.label("<html><b>Neuro Imaging Tools</b></html>");
            niToolsSeparator = new JSeparator(JSeparator.HORIZONTAL);
        }

        if ( needsBITools ) {
            biToolsActionLabel = ComponentFactory.label("");
            biToolsDetailLabel = ComponentFactory.label("");

            biToolsActionLabel.setIcon(loadingIcon);
            
            biToolsProgressBar = new JProgressBar();
            biToolsProgressBar.setStringPainted(true); 
            
            biToolsTitleLabel = ComponentFactory.label("<html><b>Bioinformatics Tools</b></html>");
            biToolsSeparator = new JSeparator(JSeparator.HORIZONTAL);
        }
        
        totalProgressBar = new JProgressBar();
        totalProgressBar.setStringPainted(true);

        totalTitleLabel = ComponentFactory.label("<html><b>Total Progress</b></html>");
        totalSeparator = new JSeparator(JSeparator.HORIZONTAL);


        detailsButton = ComponentFactory.button("Show details >>");
        logsButton = ComponentFactory.button("Show logs...");

        detailedView = false;

        logLabel = ComponentFactory.label("Log: ");
        
        this.setPreferredSize(new Dimension(400, 400));
    }


    private void initLayout() {
        layout = new SpringLayout();
        setLayout(layout);

        add(titleLabel);
        layout.putConstraint(north, titleLabel, dist, north, this);
        layout.putConstraint(west, titleLabel, panelMargin, west, this);

        if ( needsSGE )
            topMargin-=4;

        if ( needsNITools )
            topMargin-=4;
        
        if ( needsBITools )
            topMargin-=4;
        
        System.out.println("needsSGE=" + needsSGE + " needsNITools=" + needsNITools + " needsBITools=" + needsBITools);

        layoutProgressBars();

        add(bottomSeparator);
        layout.getConstraints(bottomSeparator).setConstraint(east, layout.getConstraint(east, this));
        layout.putConstraint(north, bottomSeparator, -panelMargin, south, this);
        layout.putConstraint(west, bottomSeparator, panelMargin, west, this);


    }

    public void startInstallation() {

        Thread t = new Thread() {
            @Override
            public void run() {
                install();
            }
        };

        t.start();
    }


    private void install() {
        boolean isFullInstall = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALLER_MODE_INSTALL));
        boolean isManualToolInstall = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALLER_MODE_MANUAL_TOOL));
        int manualActionNeededFor = 0;
        String manualActionNeededForString = Configuration.getConfig(Configuration.CONFIG_MANUAL_ACTION_NEEDED_FOR);
        if ( manualActionNeededForString != null){
            manualActionNeededFor = Integer.parseInt(manualActionNeededForString);
        }

        LinkedList<String> arguments = new LinkedList<String>();


        if ( isFullInstall ) {
            boolean installSge = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_SGE));
            boolean installNITools = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_NI_TOOLS));
            boolean installBITools = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_BI_TOOLS));
            boolean installPipeline = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_PIPELINE));

            arguments.add(Configuration.CONFIG_INSTALL_PIPELINE);
            arguments.add(Configuration.CONFIG_INSTALL_SGE);
            arguments.add(Configuration.CONFIG_INSTALL_NI_TOOLS);
            arguments.add(Configuration.CONFIG_INSTALL_BI_TOOLS);


            if ( installPipeline ) {
                arguments.add(Configuration.CONFIG_SHARED_FILESYSTEM_PATH);
                arguments.add(Configuration.CONFIG_INSTALL_SUPERUSER);
                arguments.add(Configuration.CONFIG_SUPERUSER_LIST);
                arguments.add(Configuration.CONFIG_PIPELINE_LOCATION);
                arguments.add(Configuration.CONFIG_PIPELINE_PLUGIN);
                arguments.add(Configuration.CONFIG_PIPELINE_QUEUE);
                arguments.add(Configuration.CONFIG_PIPELINE_HOSTNAME);
                arguments.add(Configuration.CONFIG_PIPELINE_PORT);
                arguments.add(Configuration.CONFIG_PIPELINE_USER);
                arguments.add(Configuration.CONFIG_PIPELINE_TEMPDIR);
                arguments.add(Configuration.CONFIG_PIPELINE_SCRATCHDIR);
                arguments.add(Configuration.CONFIG_PIPELINE_SERVERLIB);
                arguments.add(Configuration.CONFIG_PIPELINE_USEPRIVESC);
                arguments.add(Configuration.CONFIG_PIPELINE_START_ON_STARTUP);
                arguments.add(Configuration.CONFIG_PIPELINE_USER_AUTH);
                arguments.add(Configuration.CONFIG_PIPELINEDB_LOCATION);
                arguments.add(Configuration.CONFIG_JDK_BINARY_LOCATION);
                arguments.add(Configuration.CONFIG_PIPELINE_MEMORY_ALLOCATION);
                arguments.add(Configuration.CONFIG_TOOLS_PATH);
            }

            if ( installSge || installPipeline ) {
                arguments.add(Configuration.CONFIG_SGE_ROOT);
            }

            if ( installSge ) {
                arguments.add(Configuration.CONFIG_SGE_CONFIG_QUEUE);
                arguments.add(Configuration.CONFIG_SGE_QUEUE_NAME);
                arguments.add(Configuration.CONFIG_SGE_QUEUE_HOSTLIST);
                arguments.add(Configuration.CONFIG_SGE_QUEUE_SLOTS);
                arguments.add(Configuration.CONFIG_SGE_CLUSTER);
                arguments.add(Configuration.CONFIG_SGE_SUBMIT_HOSTS);
                arguments.add(Configuration.CONFIG_SGE_EXEC_HOSTS);
                arguments.add(Configuration.CONFIG_SGE_ADMIN_USER);
                arguments.add(Configuration.CONFIG_SGE_ADMIN_HOSTS);
                arguments.add(Configuration.CONFIG_SGE_SPOOL_DIR);
            } 

            if ( installNITools ) {
                arguments.add(Configuration.CONFIG_TOOLS_PATH);
                arguments.add(Configuration.CONFIG_INSTALL_NI_EXECUTABLES);
                arguments.add(Configuration.CONFIG_INSTALL_NI_SERVERLIB);
                arguments.add(Configuration.CONFIG_INSTALL_AFNI);
                arguments.add(Configuration.CONFIG_TOOLS_AFNI_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_AIR);
                arguments.add(Configuration.CONFIG_TOOLS_AIR_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_BRAINSUITE);
                arguments.add(Configuration.CONFIG_TOOLS_BRAINSUITE_VERSION);
                arguments.add(Configuration.CONFIG_BRAINSUITE_ARCHIVE_LOCATION);                
                arguments.add(Configuration.CONFIG_INSTALL_FSL);
                arguments.add(Configuration.CONFIG_TOOLS_FSL_VERSION);
                arguments.add(Configuration.CONFIG_FSL_ARCHIVE_LOCATION);
                arguments.add(Configuration.CONFIG_INSTALL_FREESURFER);
                arguments.add(Configuration.CONFIG_TOOLS_FREESURFER_VERSION);
                arguments.add(Configuration.CONFIG_TOOLS_FREESURFER_VERSION);
                arguments.add(Configuration.CONFIG_FREESURFER_ARCHIVE_LOCATION);                
                arguments.add(Configuration.CONFIG_INSTALL_LONITOOLS);
                arguments.add(Configuration.CONFIG_TOOLS_LONITOOLS_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_MINC);
                arguments.add(Configuration.CONFIG_TOOLS_MINC_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_ITK);
                arguments.add(Configuration.CONFIG_TOOLS_ITK_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_DTK);
                arguments.add(Configuration.CONFIG_TOOLS_DTK_VERSION);
                arguments.add(Configuration.CONFIG_DTK_ARCHIVE_LOCATION);
                arguments.add(Configuration.CONFIG_INSTALL_GAMMA);
                arguments.add(Configuration.CONFIG_TOOLS_GAMMA_VERSION);
                arguments.add(Configuration.CONFIG_PIPELINE_USER);
                arguments.add(Configuration.CONFIG_PIPELINE_SERVERLIB);
                arguments.add(Configuration.CONFIG_PIPELINE_HOSTNAME);
                arguments.add(Configuration.CONFIG_PIPELINE_PORT);
            }
            if ( installBITools ) {
                arguments.add(Configuration.CONFIG_TOOLS_PATH);
                arguments.add(Configuration.CONFIG_INSTALL_BI_EXECUTABLES);
                arguments.add(Configuration.CONFIG_INSTALL_BI_SERVERLIB);
                arguments.add(Configuration.CONFIG_INSTALL_PICARD);
                arguments.add(Configuration.CONFIG_TOOLS_PICARD_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_BATWING);
                arguments.add(Configuration.CONFIG_TOOLS_BATWING_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_BAYESASS);
                arguments.add(Configuration.CONFIG_TOOLS_BAYESASS_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_BOWTIE);
                arguments.add(Configuration.CONFIG_TOOLS_BOWTIE_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_EMBOSS);
                arguments.add(Configuration.CONFIG_TOOLS_EMBOSS_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_FORMATOMATIC);
                arguments.add(Configuration.CONFIG_TOOLS_FORMATOMATIC_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_GENEPOP);
                arguments.add(Configuration.CONFIG_TOOLS_GENEPOP_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_GWASS);
                arguments.add(Configuration.CONFIG_TOOLS_GWASS_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_MAQ);
                arguments.add(Configuration.CONFIG_TOOLS_MAQ_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_MIGRATE);
                arguments.add(Configuration.CONFIG_TOOLS_MIGRATE_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_MRFAST);
                arguments.add(Configuration.CONFIG_TOOLS_MRFAST_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_MSA);
                arguments.add(Configuration.CONFIG_TOOLS_MSA_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_PLINK);
                arguments.add(Configuration.CONFIG_TOOLS_PLINK_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_SAMTOOLS);
                arguments.add(Configuration.CONFIG_TOOLS_SAMTOOLS_VERSION);
                arguments.add(Configuration.CONFIG_INSTALL_MIBLAST);
                arguments.add(Configuration.CONFIG_TOOLS_MIBLAST_VERSION);
                arguments.add(Configuration.CONFIG_PIPELINE_USER);
                arguments.add(Configuration.CONFIG_PIPELINE_SERVERLIB);
                arguments.add(Configuration.CONFIG_PIPELINE_HOSTNAME);
                arguments.add(Configuration.CONFIG_PIPELINE_PORT);
            }
            
        } else if ( isManualToolInstall ) {

            if ( manualActionNeededFor == MANUAL_ACTION_NEEDED_FOR_TOOLS ) {
                arguments.add(Configuration.CONFIG_INSTALL_NI_TOOLS);
                arguments.add(Configuration.CONFIG_TOOLS_PATH);
                arguments.add(Configuration.CONFIG_INSTALL_NI_EXECUTABLES);
                arguments.add(Configuration.CONFIG_INSTALL_NI_SERVERLIB);
                arguments.add(Configuration.CONFIG_PIPELINE_USER);
                arguments.add(Configuration.CONFIG_PIPELINE_SERVERLIB);
                arguments.add(Configuration.CONFIG_PIPELINE_HOSTNAME);
                arguments.add(Configuration.CONFIG_PIPELINE_PORT);

                arguments.add(Configuration.CONFIG_MANUALTOOL_NAME);
                arguments.add(Configuration.CONFIG_MANUALTOOL_VERSION);

            } else if ( manualActionNeededFor == MANUAL_ACTION_NEEDED_FOR_PIPELINE ) {
                arguments.add(Configuration.CONFIG_SHARED_FILESYSTEM_PATH);
                arguments.add(Configuration.CONFIG_INSTALL_PIPELINE);
                arguments.add(Configuration.CONFIG_INSTALL_SUPERUSER);
                arguments.add(Configuration.CONFIG_SUPERUSER_LIST);
                arguments.add(Configuration.CONFIG_PIPELINE_LOCATION);
                arguments.add(Configuration.CONFIG_PIPELINE_PLUGIN);
                arguments.add(Configuration.CONFIG_PIPELINE_QUEUE);
                arguments.add(Configuration.CONFIG_PIPELINE_HOSTNAME);
                arguments.add(Configuration.CONFIG_PIPELINE_PORT);
                arguments.add(Configuration.CONFIG_PIPELINE_USER);
                arguments.add(Configuration.CONFIG_PIPELINE_TEMPDIR);
                arguments.add(Configuration.CONFIG_PIPELINE_SCRATCHDIR);
                arguments.add(Configuration.CONFIG_PIPELINE_SERVERLIB);
                arguments.add(Configuration.CONFIG_PIPELINE_USEPRIVESC);
                arguments.add(Configuration.CONFIG_PIPELINE_START_ON_STARTUP);
                arguments.add(Configuration.CONFIG_PIPELINE_USER_AUTH);
                arguments.add(Configuration.CONFIG_PIPELINEDB_LOCATION);
                arguments.add(Configuration.CONFIG_PIPELINE_MEMORY_ALLOCATION);                                
            }


            arguments.add(Configuration.CONFIG_INSTALLER_MODE_MANUAL_TOOL);
            arguments.add(Configuration.CONFIG_MANUALTOOL_ARCHIVE_PATH);
            arguments.add(Configuration.CONFIG_MANUALTOOL_LICENSE_PATH);

        }

        String path = "install_files/makefile";

        StringBuilder command = new StringBuilder("make -j ");

        command.append(Runtime.getRuntime().availableProcessors() * 4 );
        command.append(" -f ");
        command.append(path);

        LinkedList<String> addedArgs = new LinkedList<String>();

        for (String arg : arguments) {
            String val = Configuration.getConfig(arg);
            if (val == null) {
                continue;
            }

            if (addedArgs.contains(arg)) {
                continue;
            }

            command.append(" ");
            command.append(arg);
            command.append("=");
            if (val.endsWith(File.separator)) {
                command.append(val.substring(0, val.length() - 1));
            } else {
                command.append(val);
            }

            addedArgs.add(arg);
        }

        System.out.println("Command = " + command.toString());


        Process p = null;
        try {
            p = Runtime.getRuntime().exec(command.toString());

            StreamReader isr = new StreamReader(p.getInputStream(), 1);
            StreamReader esr = new StreamReader(p.getErrorStream(), 2);

            isr.start();
            esr.start();


            p.waitFor();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

    }


    private class StreamReader extends Thread {
        InputStream inputStream;
        FileWriter outputStream;
        BufferedWriter out;
        private boolean isErrorStream;
        public StreamReader(InputStream inputStream, int streamID) {
            this.inputStream = inputStream;
            if ( streamID == 2 )
                isErrorStream = true;

            File f = new File("../logs");
            f.mkdirs();

            try{
                // Create file

                  outputStream = new FileWriter(logFileName,true);
                  
                  out = new BufferedWriter(outputStream);

            }catch (Exception e){//Catch exception if any
              System.err.println("Error: " + e.getMessage());
            }


            this.setName(isErrorStream ? "Error" : "Output" + " Stream Reader Thread");
        }

        private String trimSpecialCommands(String string) {
            String ret = string;

            String[] commands = {"-->", "->", "+", "+=", "=>", "@", "!"};
            String[] components = {"pl", "sge", "nitools", "bitools", "license"};

            for (String cmd : commands) {
                if (string.contains(cmd)) {
                    for (String comp : components) {
                        int index = string.indexOf(comp + cmd);
                        if (index >= 0) {
                            String s = string.substring(index);

                            if (s.startsWith("pl-->")) {
                                if (s.equals("pl-->Complete")) {
                                    increaseProgress(1, plStepsTotal - plStepsPassed);
                                }

                                plActionLabel.setText(s.substring(s.indexOf(">") + 1));
                            } else if (s.startsWith("pl->")) {
                                plDetailLabel.setText(s.substring(s.indexOf(">") + 1));
                            } else if (s.startsWith("pl+")) {
                                increaseProgress(1);
                            } else if (s.startsWith("pl=>")) {
                                plStepsTotal = Integer.parseInt(s.substring(s.indexOf(">") + 1));
                                totalStepsTotal = plStepsTotal + sgeStepsTotal + niToolsStepsTotal + biToolsStepsTotal;
                            } else if (s.startsWith("pl@")) {
                                needsActionQueue.add(s);
                                needsAction();
                            } else if (s.startsWith("pl!")) {
                                plActionLabel.setText("ERROR: ");
                                plActionLabel.setForeground(Color.RED);
                                plActionLabel.setIcon(null);
                                plDetailLabel.setText(s.substring(s.indexOf("!") + 1) + ". Check logs for more details");
                                plDetailLabel.setForeground(Color.RED);
                            } else if (s.startsWith("sge-->")) {
                                if (s.equals("sge-->Complete")) {
                                    increaseProgress(2, sgeStepsTotal - sgeStepsPassed);
                                }
                                sgeActionLabel.setText(s.substring(s.indexOf(">") + 1));
                            } else if (s.startsWith("sge->")) {
                                sgeDetailLabel.setText(s.substring(s.indexOf(">") + 1));
                            } else if (s.startsWith("sge+")) {
                                increaseProgress(2);
                            } else if (s.startsWith("sge=>")) {
                                sgeStepsTotal = Integer.parseInt(s.substring(s.indexOf(">") + 1));
                                totalStepsTotal = plStepsTotal + sgeStepsTotal + niToolsStepsTotal + biToolsStepsTotal;
                            } else if (s.startsWith("sge!")) {
                                sgeActionLabel.setText("ERROR: ");
                                sgeActionLabel.setForeground(Color.RED);
                                sgeActionLabel.setIcon(null);
                                sgeDetailLabel.setText(s.substring(s.indexOf("!") + 1) + ". Check logs for more details");
                                sgeDetailLabel.setForeground(Color.RED);
                            } else if (s.startsWith("nitools-->")) {
                                niToolsActionLabel.setText(s.substring(s.indexOf(">") + 1));
                            } else if (s.startsWith("nitools->")) {
                                niToolsDetailLabel.setText(s.substring(s.indexOf(">") + 1));
                            } else if (s.startsWith("nitools+=")) {
                                increaseProgress(3, Integer.valueOf(s.substring(s.indexOf("=") + 1)));
                            } else if (s.startsWith("nitools+")) {
                                increaseProgress(3);
                            } else if (s.startsWith("nitools=>")) {
                                int addedSteps = Integer.parseInt(s.substring(s.indexOf(">") + 1));
                                // If there is nothing to install, the script should report that the total is zero.
                                if (addedSteps == 0) {
                                    niToolsProgressBar.setValue(100);
                                    niToolsActionLabel.setText("Nothing to install");
                                    niToolsActionLabel.setIcon(null);
                                }
                                niToolsStepsTotal += addedSteps;
                                totalStepsTotal = plStepsTotal + sgeStepsTotal + niToolsStepsTotal + biToolsStepsTotal;
                            } else if (s.startsWith("nitools@")) {
                                needsActionQueue.add(s);
                                needsAction();
                            } else if (s.startsWith("nitools!")) {
                                niToolsActionLabel.setText("ERROR: ");
                                niToolsActionLabel.setForeground(Color.RED);
                                niToolsActionLabel.setIcon(null);
                                niToolsActionLabel.setText(s.substring(s.indexOf("!") + 1) + ". Check logs for more details");
                                niToolsActionLabel.setForeground(Color.RED);
                            } else if (s.startsWith("bitools-->")) {
                                biToolsActionLabel.setText(s.substring(s.indexOf(">") + 1));
                            } else if (s.startsWith("bitools->")) {
                                biToolsActionLabel.setText(s.substring(s.indexOf(">") + 1));
                            } else if (s.startsWith("bitools+=")) {
                                increaseProgress(4, Integer.valueOf(s.substring(s.indexOf("=") + 1)));
                            } else if (s.startsWith("bitools+")) {
                                increaseProgress(4);
                            } else if (s.startsWith("bitools=>")) {
                                int addedSteps = Integer.parseInt(s.substring(s.indexOf(">") + 1));
                                // If there is nothing to install, the script should report that the total is zero.
                                if (addedSteps == 0) {
                                    biToolsProgressBar.setValue(100);
                                    biToolsActionLabel.setText("Nothing to install");
                                    biToolsActionLabel.setIcon(null);
                                }
                                biToolsStepsTotal += addedSteps;
                                totalStepsTotal = plStepsTotal + sgeStepsTotal + niToolsStepsTotal + biToolsStepsTotal;
                            } else if (s.startsWith("bitools@")) {
                                needsActionQueue.add(s);
                                needsAction();
                            } else if (s.startsWith("bitools!")) {
                                biToolsActionLabel.setText("ERROR: ");
                                biToolsActionLabel.setForeground(Color.RED);
                                biToolsActionLabel.setIcon(null);
                                biToolsActionLabel.setText(s.substring(s.indexOf("!") + 1) + ". Check logs for more details");
                                biToolsActionLabel.setForeground(Color.RED);
                            }

                            return "";
                        }
                    }


                }
            }

            return ret;
        }
        
        private String trimSpecialCommandsHeadless(String string) {
            String ret = string;

            String[] commands = {"-->", "->", "+", "+=", "=>", "@", "!"};
            String[] components = {"pl", "sge", "nitools", "bitools", "license"};

            for (String cmd : commands) {
                if (string.contains(cmd)) {
                    for (String comp : components) {
                        int index = string.indexOf(comp + cmd);
                        if (index >= 0) {
                            return "";
                        }
                    }


                }
            }

            return ret;
        }        

        public void run() {

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String s = null;
                while ((s = br.readLine()) != null) {

                    String st = "";
                    if (headless){
                        st = trimSpecialCommandsHeadless(s).trim();
                    } else {
                        st = trimSpecialCommands(s).trim();                        
                    }
                    
                    if (st.length() > 0) {

                        out.write(st);
                        out.newLine();
                        out.flush();

                        try {

                            if (logsDialogTextArea != null) {
                                logsDialogTextArea.append(st + "\n");

                                if (isFollowingTheLastLine) {
                                    if (logsDialogTextArea.getLineCount() > 0) {
                                        logsDialogTextArea.setCaretPosition(logsDialogTextArea.getLineEndOffset(logsDialogTextArea.getLineCount() - 1));
                                    }
                                }

                            }
                        } catch (NullPointerException ex) {
                            // do nothing
                            // Sometimes while working with the log dialog, it can be closed by the user and here we can
                            // get a NullPointerException, which has to be ignored
                        }

                        if (st.matches("\\d+[KMG][\\p{Blank}\\.\\,]+\\d{1,3}\\%\\p{Blank}\\d+\\.\\d+[KMG]\\p{Blank}[\\d[hms]]+")) {
                            int index = st.indexOf("%");
                            String p1 = st.substring(0, index + 1);
                            String p2 = st.substring(index);

                            String percent = p1.substring(p1.lastIndexOf(" "));
                            String timeRemaining = p2.substring(p2.lastIndexOf(" ")).replace("m", "m ").replace("h", "h ");

                            if (headless) {
                                System.out.println("Downloaded: " + percent + " ( " + timeRemaining + " remaining )");
                            } else {
                                logLabel.setText("Downloaded: " + percent + " ( " + timeRemaining + " remaining )");
                            }
                            
                        } else if (!st.startsWith("=======|")) {

                            if (st.startsWith("Press Enter to continue")) {
                                st = "Please wait...";
                            }

                            if (st.length() > 47) {
                                st = st.substring(0, 22) + "..." + st.substring(st.length() - 25, st.length());
                            }
                            if (headless) {
                                System.out.println(st);
                            } else {
                                logLabel.setText("Log: " + st);
                            }
                        }
                    }
                }

                inputStream.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }


            if (isErrorStream) {
                System.out.println("[ Error Stream closed ]");
            } else {
                System.out.println("[ Output Stream closed ]");
            }

        }
    }

    private class ManualActionThread extends Thread {

        public ManualActionThread() {
        }

        @Override
        public void run() {

            while (!needsActionQueue.isEmpty()) {
                String s = needsActionQueue.get(0);
                int manualActionNeededFor = 0;

                JLabel actionLabel = null;
                JLabel detailLabel = null;

                boolean license = false;
                boolean licenseOnly = false;

                if (s.startsWith("pl")) {
                    manualActionNeededFor = MANUAL_ACTION_NEEDED_FOR_PIPELINE;
                    actionLabel = plActionLabel;
                    detailLabel = plDetailLabel;
                } else if (s.startsWith("nitools") || s.startsWith("license")) {
                    manualActionNeededFor = MANUAL_ACTION_NEEDED_FOR_TOOLS;
                    actionLabel = niToolsActionLabel;
                    detailLabel = niToolsDetailLabel;
                    if (s.startsWith("license")) {
                        licenseOnly = true;
                    }
                } else if (s.startsWith("sge")) {
                    manualActionNeededFor = MANUAL_ACTION_NEEDED_FOR_SGE;
                    actionLabel = sgeActionLabel;
                    detailLabel = sgeDetailLabel;
                }

                s = s.substring(s.indexOf("@") + 1);

                boolean mandatory = s.startsWith("@");

                // remove the first @ from the string
                if (mandatory) {
                    s = s.substring(1);
                }

                int firstIndex = s.indexOf("@");
                int secondIndex = s.indexOf("@", firstIndex + 1);
                int thirdIndex = s.indexOf("@", secondIndex + 1);
                String licenseUrl = null;
                String licenseInstructions = null;
                if (thirdIndex >= 0) {
                    int fourthIndex = s.indexOf("@", thirdIndex + 1);
                    licenseUrl = s.substring(thirdIndex + 1, fourthIndex);
                    licenseInstructions = s.substring(fourthIndex + 1);
                    license = true;
                }

                if (firstIndex == -1) {
                    System.err.println("Failed to determine the package name of the tool. Wrong format: " + s);
                    continue;
                }

                String packageName = s.substring(0, firstIndex);

                actionLabel.setIcon(null);
                actionLabel.setText("Action needed for " + packageName);
                detailLabel.setText("");


                if (secondIndex == -1) {
                    System.err.println("Failed to determine the package Url of the " + packageName + " tool. " + s);
                    continue;
                }

                String url = s.substring(firstIndex + 1, secondIndex);

                if (url == null) {
                    System.err.println("Failed to parse the package Url of the " + packageName + " tool. " + s);
                    continue;
                }

                if (license) {
                    s = s.substring(secondIndex + 1, thirdIndex);
                } else {
                    s = s.substring(secondIndex + 1);
                }

                //Custom button text
                Object[] options = null;

                String pageType = "download";
                String fileType = "Archive";
                if (licenseOnly) {
                    pageType = "registration";
                    fileType = "License";
                }

                if (mandatory) {
                    options = new Object[]{"Open " + pageType + " page", "I have the " + fileType, "Exit Installer"};
                } else {
                    options = new Object[]{"Open " + pageType + " page", "I have the " + fileType, "Skip for now", "Cancel this tool"};
                }

                StringBuilder msgBuilder = new StringBuilder();
                if (licenseOnly) {
                    msgBuilder = new StringBuilder();
                    msgBuilder.append("<html>To run ");
                    msgBuilder.append(packageName);
                    msgBuilder.append(" binaries, you need to provide a license file.<br><br>");
                    msgBuilder.append("If you have already registered and created the license file<br>");
                    msgBuilder.append("Press \"I have the License\" button and specify its path. Alternatively,<br>");
                    msgBuilder.append("if you don't want to register now, but would like to proceed with installation,<br>");
                    msgBuilder.append("press the \"Register Later\" button; otherwise follow these<br>");
                    msgBuilder.append("instructions after pressing the \"Open registration page\" button:<br><br>");
                    msgBuilder.append(licenseInstructions);
                    msgBuilder.append("<br><br>When you're done with the steps, press \"I have the License\" to continue<br>");
                } else {
                    msgBuilder.append("<html>To install ");
                    msgBuilder.append(packageName);
                    msgBuilder.append(" you need to provide its archive file.<br><br>");
                    msgBuilder.append("If you have already downloaded the archive<br>");
                    msgBuilder.append("Press \"I have the Archive\" button and specify its path,<br>");
                    msgBuilder.append("otherwise follow these instructions after pressing the \"Open download page\" button:<br><br>");
                    msgBuilder.append(s);
                    msgBuilder.append("<br><br>When you're done with the steps, press \"I have the Archive\" to continue<br>");

                    if (!mandatory) {
                        msgBuilder.append("<br>If you started to download the archive and would like to install another tool while<br>");
                        msgBuilder.append("this one is downloading, press \"Skip for now\" to skip to next tool. When there will be no<br>");
                        msgBuilder.append("more tools which need manual action, this message will appear again.<br><br>");
                        msgBuilder.append("Press \"Cancel this tool\" if you don't want to install ");
                        msgBuilder.append(packageName);
                        msgBuilder.append(" anymore.<br></html>");
                    } else {
                        msgBuilder.append("</html>");
                    }
                }

                int choice = -1;

                if (!detailedView) {
                    detailsButtonAction();
                }

                do {

                    choice = JOptionPane.showOptionDialog(sif, msgBuilder.toString(), "Manual Action Needed for " + packageName,
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null, options, options[0]);

                    if (choice == 0) {
                        NativeCalls.openBrowser(url);

                        JOptionPane.showMessageDialog(sif, "Please check your default browser for a newly opened tab or window.\n\n"
                                + "While you follow the instructions, Pipeline installer continues to install\n"
                                + "other components or tools in parallel. If your computer is not fast enough,\n"
                                + "the web page can appear little late, so please be patient.\n\n"
                                + "Press OK to continue reading the instructions about how to install " + packageName,
                                "Web page is being opened",
                                JOptionPane.INFORMATION_MESSAGE);

                        options[0] = "Re-Open " + pageType + " page";
                    }


                } while (choice <= 0);

                String archivePath = packageName;

                while (choice == 1) {

                    File selectedFile = null;

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    if (fileChooser.showOpenDialog(sif) == JFileChooser.APPROVE_OPTION) {
                        selectedFile = fileChooser.getSelectedFile();
                    }

                    if (selectedFile != null) {
                        archivePath = selectedFile.getPath();
                    } else {
                        archivePath = null;
                        break;
                    }


                    File f = new File(archivePath);

                    if (f.exists() && !f.isDirectory()) {
                        if (!license) {
                            JOptionPane.showMessageDialog(sif, "Thank you. Press OK and Pipeline will start installing " + packageName,
                                    "File found", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            String update = needsActionQueue.get(0);
                            update = "license" + update.substring(update.indexOf("@"));
                            needsActionQueue.set(0, update);
                            licenseOnly = true;
                        }
                        actionLabel.setIcon(loadingIcon);
                        actionLabel.setText("");
                        detailLabel.setText("");
                        break;
                    } else {
                        JOptionPane.showMessageDialog(sif, "Specified path " + archivePath + " doesn't exist or is an invalid file. Please try again.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }

                }

                if (archivePath == null) {
                    continue;
                }

                if (choice != 2) {
                    String version = null;
                    boolean updateNeeded = false;
                    final List<String> packagesToUpdate = new LinkedList<String>();
                    
                    if (packageName.equals("AFNI")) {
                        version = Configuration.getConfig(Configuration.CONFIG_TOOLS_AFNI_VERSION);
                    } else if (packageName.equals("AIR")) {
                        version = Configuration.getConfig(Configuration.CONFIG_TOOLS_AIR_VERSION);
                    } else if (packageName.equals("BrainSuite")) {
                        version = Configuration.getConfig(Configuration.CONFIG_TOOLS_BRAINSUITE_VERSION);
                    } else if (packageName.equals("FSL")) {
                        Pattern fslPattern = Pattern.compile("[0-9]\\.[0-9]\\.[0-9]");
                        Matcher fslMatcher = fslPattern.matcher(archivePath);
                        fslMatcher.find();
                        version = fslMatcher.group();
                        if (version == null || version.trim().isEmpty()) {
                            version = Configuration.getConfig(Configuration.CONFIG_TOOLS_FSL_VERSION);
                        } else {
                            Configuration.setConfig(Configuration.CONFIG_TOOLS_FSL_VERSION, version);
                            updateNeeded = true;
                            packagesToUpdate.add("FSL-" + version); 
                        }
                    } else if (packageName.equals("FreeSurfer")) {
                        Pattern freesurferPattern = Pattern.compile("[0-9]\\.[0-9]\\.[0-9]");
                        Matcher freesurferMatcher = freesurferPattern.matcher(archivePath);
                        freesurferMatcher.find();
                        version = freesurferMatcher.group();
                        if (version == null || version.trim().isEmpty()) {
                            version = Configuration.getConfig(Configuration.CONFIG_TOOLS_FREESURFER_VERSION);
                        } else {
                            Configuration.setConfig(Configuration.CONFIG_TOOLS_FREESURFER_VERSION, version);
                            updateNeeded = true;
                            packagesToUpdate.add("FreeSurfer-" + version); 
                        }
                    } else if (packageName.equals("LONI")) {
                        version = Configuration.getConfig(Configuration.CONFIG_TOOLS_LONITOOLS_VERSION);
                    } else if (packageName.equals("MINC")) {
                        version = Configuration.getConfig(Configuration.CONFIG_TOOLS_MINC_VERSION);
                    } else if (packageName.equals("GAMMA")) {
                        version = Configuration.getConfig(Configuration.CONFIG_TOOLS_GAMMA_VERSION);
                    } else if (packageName.equals("ITK")) {
                        version = Configuration.getConfig(Configuration.CONFIG_TOOLS_ITK_VERSION);
                    } else if (packageName.equals("DTK")) {
                        Pattern dtkPattern = Pattern.compile("[0-9]\\.[0-9]\\.[0-9]\\.[0-9]");
                        Matcher dtkMatcher = dtkPattern.matcher(archivePath);
                        dtkMatcher.find();
                        version = dtkMatcher.group();
                        if (version == null || version.trim().isEmpty()) {
                            version = Configuration.getConfig(Configuration.CONFIG_TOOLS_DTK_VERSION);
                        } else {
                            updateNeeded = true;
                            packagesToUpdate.add("DTK-" + version); 
                        }
                    }
                    
                    if (updateNeeded) {
                        // Start the Userdata Updater thread.
                        Thread t = new Thread() {

                            @Override
                            public void run() {
                                UserdataUpdater uu = new UserdataUpdater();

                                List<Package> updatedPackages = new LinkedList<Package>();
                                List<Package> existingPackages = uu.getPackages();
                                for (String packageName : packagesToUpdate) {
                                    String[] tokens = packageName.split("-");
                                    for (Package p : existingPackages) {
                                        if (p.getName().equals(tokens[0])) {
                                            existingPackages.remove(p);
                                            // update version
                                            p.setVersion(tokens[1]);
                                            
                                            updatedPackages.add(p);
                                            
                                            break;
                                        }
                                    }
                                }
                                updatedPackages.addAll(existingPackages);
                                uu.setPackages(updatedPackages);
                                List<Package> allPackages = uu.getPackages();

                                // now write it out to disk
                                uu.flushToDisk();
                            }
                        };
                        t.start();
                    }


                    // When user presses skip, we need to start the installer anyway,
                    // It will not do anything but will increase the progress bar value.

                    Configuration.setConfig(Configuration.CONFIG_INSTALLER_MODE_MANUAL_TOOL, "true");
                    Configuration.setConfig(Configuration.CONFIG_INSTALLER_MODE_INSTALL, "false");

                    Configuration.setConfig(Configuration.CONFIG_MANUALTOOL_NAME, packageName);

                    Configuration.setConfig(Configuration.CONFIG_MANUAL_ACTION_NEEDED_FOR, Integer.toString(manualActionNeededFor));

                    if (version != null) {
                        Configuration.setConfig(Configuration.CONFIG_MANUALTOOL_VERSION, version);
                    }

                    Configuration.setConfig(Configuration.CONFIG_MANUALTOOL_ARCHIVE_PATH, archivePath);


                    // Prompt the user for a license file
                    if (license && choice != 3) {
                        StringBuilder licenseMsgBuilder = new StringBuilder();
                        licenseMsgBuilder.append("<html>To run ");
                        licenseMsgBuilder.append(packageName);
                        licenseMsgBuilder.append(" binaries, you need to provide a license file.<br><br>");
                        licenseMsgBuilder.append("If you have already registered and created the license file<br>");
                        licenseMsgBuilder.append("Press \"I have the License\" button and specify its path. Alternatively,<br>");
                        licenseMsgBuilder.append("if you don't want to register now, but would like to proceed with installation,<br>");
                        licenseMsgBuilder.append("press the \"Register Later\" button; otherwise follow these<br>");
                        licenseMsgBuilder.append("instructions after pressing the \"Open registration page\" button:<br><br>");
                        licenseMsgBuilder.append(licenseInstructions);
                        licenseMsgBuilder.append("<br><br>When you're done with the steps, press \"I have the License\" to continue<br>");

                        Object[] licenseOptions = null;

                        licenseOptions = new Object[]{"Open registration page", "I have the License", "Register Later"};

                        int licenseChoice = -1;

                        if (!detailedView) {
                            detailsButtonAction();
                        }

                        do {

                            licenseChoice = JOptionPane.showOptionDialog(sif, licenseMsgBuilder.toString(), "Manual Action Needed for " + packageName,
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null, licenseOptions, licenseOptions[0]);

                            if (licenseChoice == 0) {
                                NativeCalls.openBrowser(licenseUrl);

                                JOptionPane.showMessageDialog(sif, "Please check your default browser for a newly opened tab or window.\n\n"
                                        + "While you follow the instructions, Pipeline installer continues to install\n"
                                        + "other components or tools in parallel. If your computer is not fast enough,\n"
                                        + "the web page can appear little late, so please be patient.\n\n"
                                        + "Press OK to continue reading the instructions about how to install " + packageName,
                                        "Web page is being opened",
                                        JOptionPane.INFORMATION_MESSAGE);
                                licenseOptions[0] = "Re-Open registration page";
                            }


                        } while (licenseChoice <= 0);

                        String licensePath = ".license";

                        while (licenseChoice == 1) {

                            File selectedFile = null;

                            JFileChooser fileChooser = new JFileChooser();
                            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                            fileChooser.setFileHidingEnabled(false);


                            if (fileChooser.showOpenDialog(sif) == JFileChooser.APPROVE_OPTION) {
                                selectedFile = fileChooser.getSelectedFile();
                            }

                            if (selectedFile != null) {
                                licensePath = selectedFile.getPath();
                            } else {
                                licensePath = null;
                                break;
                            }


                            File f = new File(licensePath);

                            if (f.exists() && !f.isDirectory()) {
                                JOptionPane.showMessageDialog(sif, "Thank you. Press OK and Pipeline will start installing " + packageName,
                                        "File found", JOptionPane.INFORMATION_MESSAGE);
                                actionLabel.setIcon(loadingIcon);
                                actionLabel.setText("");
                                detailLabel.setText("");
                                break;
                            } else {
                                JOptionPane.showMessageDialog(sif, "Specified path " + licensePath + " doesn't exist or is an invalid file. Please try again.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }

                        }

                        if (licensePath == null) {
                            continue;
                        }

                        if (choice == 2) {
                            JOptionPane.showMessageDialog(sif, "After the installation completes and before you run any " + packageName + " executables, make sure to register and obtain a license file for the " + packageName + " package.",
                                    "Reminder", JOptionPane.INFORMATION_MESSAGE);
                        }

                        Configuration.setConfig(Configuration.CONFIG_MANUALTOOL_LICENSE_PATH, licensePath);
                    }
                    startInstallation();
                }

                String str = needsActionQueue.remove(0);


                if (choice == 2) {
                    if (options.length == 3) {
                        System.exit(0); // Exit the installer
                    } else // Skip this for now, remind later.
                    {
                        needsActionQueue.add(str);
                    }
                }
            }

        }
    }

    private void needsAction() {
        if (manualActionThread == null || !manualActionThread.isAlive()) {
            manualActionThread = new ManualActionThread();
            manualActionThread.start();
        }

    }

    private void increaseProgress(int id, int numSteps) {
        for (int i = 0; i < numSteps; i++) {
            increaseProgress(id);
        }
    }

    private void increaseProgress(int id) {
        switch (id) {
            case 1:
                if (needsPipeline) {
                    plStepsPassed++;
                    if (plStepsTotal > 0) {
                        plProgressBar.setValue(plStepsPassed * 100 / plStepsTotal);
                    }


                    if (plProgressBar.getValue() >= 100) {
                        plProgressBar.setValue(100);
                        plActionLabel.setIcon(null);
                        installationFinished(id);
                    }
                }
                break;
            case 2:
                if (needsSGE) {
                    sgeStepsPassed++;
                    if (sgeStepsTotal > 0) {
                        sgeProgressBar.setValue(sgeStepsPassed * 100 / sgeStepsTotal);
                    }

                    if (sgeProgressBar.getValue() >= 100) {
                        sgeProgressBar.setValue(100);
                        sgeActionLabel.setIcon(null);
                        installationFinished(id);
                    }
                }
                break;
            case 3:
                if (needsNITools) {
                    niToolsStepsPassed++;
                    if (niToolsStepsTotal > 0) {
                        niToolsProgressBar.setValue(niToolsStepsPassed * 100 / niToolsStepsTotal);
                        if (niToolsProgressBar.getValue() == 100) {
                            niToolsActionLabel.setText("Complete");
                            niToolsDetailLabel.setText("");
                        }

                        if (niToolsProgressBar.getValue() >= 100) {
                            niToolsActionLabel.setText("Complete");
                            niToolsProgressBar.setValue(100);
                            niToolsActionLabel.setIcon(null);
                        }
                    }
                }
                break;
            case 4:
                if (needsBITools) {
                    biToolsStepsPassed++;
                    if (biToolsStepsTotal > 0) {
                        biToolsProgressBar.setValue(biToolsStepsPassed * 100 / biToolsStepsTotal);
                        if (biToolsProgressBar.getValue() == 100) {
                            biToolsActionLabel.setText("Complete");
                            biToolsDetailLabel.setText("");
                        }

                        if (biToolsProgressBar.getValue() >= 100) {
                            biToolsActionLabel.setText("Complete");                            
                            biToolsProgressBar.setValue(100);
                            biToolsActionLabel.setIcon(null);
                        }
                    }
                }
                break;
            default:
                return;
        }


        totalStepsPassed++;
        if (totalStepsTotal != 0) {
            totalProgressBar.setValue(totalStepsPassed * 100 / totalStepsTotal);
            if (totalStepsPassed >= totalStepsTotal) {
                sif.nextButtonAction();
            }
        }

    }

    private void initListeners() {

        detailsButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                detailsButtonAction();
            }
        });

        logsButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                logsButtonAction();
            }
        });

        if (needsPipeline) {
            plPostInstallComboBox.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.DESELECTED) {
                        return;
                    }

                    if (plPostInstallComboBox.getSelectedIndex() == 0) {
                        return;
                    }

                    String selectedAction = (String) plPostInstallComboBox.getSelectedItem();

                    plPostInstallComboBox.setSelectedIndex(0);

                    if (selectedAction.equals(ACTION_CONFIGURE_SERVER)) {

                        String startServerValue = Configuration.getConfig(Configuration.START_PIPELINE_SERVER);
                        String startClientValue = Configuration.getConfig(Configuration.START_PIPELINE_CLIENT);

                        Configuration.setConfig(Configuration.CONFIGURE_PIPELINE_SERVER, "true");

                        if (startServerValue != null) {
                            Configuration.setConfig(Configuration.START_PIPELINE_SERVER, "false");
                        }
                        if (startClientValue != null) {
                            Configuration.setConfig(Configuration.START_PIPELINE_CLIENT, "false");
                        }

                        sif.postInstall();

                        if (startServerValue != null) {
                            Configuration.setConfig(Configuration.START_PIPELINE_SERVER, String.valueOf(startServerValue));
                        }
                        if (startClientValue != null) {
                            Configuration.setConfig(Configuration.START_PIPELINE_CLIENT, String.valueOf(startClientValue));
                        }

                    } else if (selectedAction.equals(ACTION_START_SERVER)) {
                        int choice = JOptionPane.showConfirmDialog(sif, "If you launch the server now, temporarily you will not be able to access some of the\n"
                                + "services or tools which are currently being installed. You will be offered again to start\n"
                                + "the server when the whole installation will be over.\n\n"
                                + "Are you sure that you want to start the server right now ? ", "Warning", JOptionPane.YES_NO_OPTION);
                        if (choice != JOptionPane.YES_OPTION) {
                            return;
                        }

                        String startClientValue = Configuration.getConfig(Configuration.START_PIPELINE_CLIENT);

                        Configuration.setConfig(Configuration.START_PIPELINE_SERVER, "true");

                        if (startClientValue != null) {
                            Configuration.setConfig(Configuration.START_PIPELINE_CLIENT, "false");
                        }

                        sif.postInstall();

                        if (startClientValue != null) {
                            Configuration.setConfig(Configuration.START_PIPELINE_CLIENT, String.valueOf(startClientValue));
                        }

                        plPostInstallComboBox.removeItem(ACTION_START_SERVER);

                        JOptionPane.showMessageDialog(sif, "Server started.", "Success", JOptionPane.INFORMATION_MESSAGE);

                    } else if (selectedAction.equals(ACTION_START_CLIENT)) {
                        String startServerValue = Configuration.getConfig(Configuration.START_PIPELINE_SERVER);

                        Configuration.setConfig(Configuration.START_PIPELINE_CLIENT, "true");

                        if (startServerValue != null) {
                            Configuration.setConfig(Configuration.START_PIPELINE_SERVER, "false");
                        }

                        sif.postInstall();

                        if (startServerValue != null) {
                            Configuration.setConfig(Configuration.START_PIPELINE_SERVER, String.valueOf(startServerValue));
                        }
                        plPostInstallComboBox.removeItem(ACTION_START_CLIENT);
                    }
                }
            });
        }


    }

    private void installationFinished(int id) {
        switch (id) {
            case 1: // Pipeline installation finished

                if (needsSGE || needsNITools || needsBITools) {
                    plTitleLabel.setText("Pipeline Server Installation Completed");
                    plProgressBar.setVisible(false);
                    plActionLabel.setVisible(false);
                    plDetailLabel.setVisible(false);

                    plPostInstallLabel.setVisible(true);
                    if (!needsSGE || (needsSGE && sgeProgressBar.getValue() >= 100)) {
                        plPostInstallComboBox.addItem(ACTION_START_SERVER);
                    }
                    plPostInstallComboBox.setVisible(true);
                }
                break;
            case 2: // SGE installation finished
                if (needsPipeline) {
                    plPostInstallComboBox.addItem(ACTION_START_SERVER);
                }
            default:
                return;


        }
    }

    private void detailsButtonAction() {
        detailedView = !detailedView;

        if (!detailedView) {
            detailsButton.setText("Show details >>");
        } else {
            detailsButton.setText("<< Hide details");
        }

        layoutProgressBars();
    }

    private void logsButtonAction() {
        final JDialog dialog = new JDialog();
        dialog.setTitle(logFileName);
        dialog.setSize(new Dimension(700, 450));

        JPanel panel = new JPanel();

        logsDialogTextArea = ComponentFactory.textarea();
        logsDialogTextArea.setEditable(false);
        logsDialogTextArea.setLineWrap(true);
        logsDialogTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(logsDialogTextArea);


        JButton closeButton = ComponentFactory.button("Close");
        final JCheckBox checkbox = ComponentFactory.checkbox("Follow the last line");
        checkbox.setSelected(isFollowingTheLastLine);

        checkbox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                isFollowingTheLastLine = checkbox.isSelected();
            }
        });

        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                logsDialogTextArea = null;
            }
        });

        panel.setLayout(layout);

        panel.add(closeButton);

        layout.putConstraint(south, closeButton, -panelMargin, south, panel);
        layout.putConstraint(east, closeButton, -panelMargin, east, panel);

        panel.add(checkbox);

        layout.putConstraint(south, checkbox, -panelMargin, south, panel);
        layout.putConstraint(east, checkbox, -dist, west, closeButton);

        panel.add(scrollPane);

        layout.getConstraints(scrollPane).setConstraint(east, Spring.sum(Spring.constant(-panelMargin), layout.getConstraint(east, panel)));
        layout.getConstraints(scrollPane).setConstraint(south, Spring.sum(Spring.constant(-vDist), layout.getConstraint(north, closeButton)));
        layout.putConstraint(north, scrollPane, panelMargin, north, panel);
        layout.putConstraint(west, scrollPane, panelMargin, west, panel);


        dialog.getContentPane().setLayout(layout);

        dialog.getContentPane().add(panel);
        layout.getConstraints(panel).setConstraint(east, layout.getConstraint(east, dialog.getContentPane()));
        layout.getConstraints(panel).setConstraint(south, layout.getConstraint(south, dialog.getContentPane()));
        layout.putConstraint(north, panel, 0, north, dialog.getContentPane());
        layout.putConstraint(west, panel, 0, west, dialog.getContentPane());


        try {
            FileInputStream fis = new FileInputStream(logFileName);

            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            boolean found = false;

            while ((strLine = br.readLine()) != null) {
                logsDialogTextArea.append(strLine + "\n");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        dialog.setVisible(true);
    }

    private void layoutProgressBars() {
        if (plTitleLabel != null) {
            remove(plTitleLabel);
        }
        if (plSeparator != null) {
            remove(plSeparator);
        }
        if (plProgressBar != null) {
            remove(plProgressBar);
        }
        if (plActionLabel != null) {
            remove(plActionLabel);
        }
        if (plDetailLabel != null) {
            remove(plDetailLabel);
        }
        if (plPostInstallComboBox != null) {
            remove(plPostInstallComboBox);
        }

        if (sgeTitleLabel != null) {
            remove(sgeTitleLabel);
        }
        if (sgeSeparator != null) {
            remove(sgeSeparator);
        }
        if (sgeProgressBar != null) {
            remove(sgeProgressBar);
        }
        if (sgeActionLabel != null) {
            remove(sgeActionLabel);
        }
        if (sgeDetailLabel != null) {
            remove(sgeDetailLabel);
        }

        if (niToolsTitleLabel != null) {
            remove(niToolsTitleLabel);
        }
        if (niToolsSeparator != null) {
            remove(niToolsSeparator);
        }
        if (niToolsProgressBar != null) {
            remove(niToolsProgressBar);
        }
        if (biToolsSeparator != null) {
            remove(biToolsSeparator);
        }
        if (biToolsProgressBar != null) {
            remove(biToolsProgressBar);
        }
        if (niToolsActionLabel != null) {
            remove(niToolsActionLabel);
        }
        if (niToolsDetailLabel != null) {
            remove(niToolsDetailLabel);
        }


        if (logLabel != null) {
            remove(logLabel);
        }
        if (logsButton != null) {
            remove(logsButton);
        }


        if (totalTitleLabel != null) {
            remove(totalTitleLabel);
        }
        if (totalSeparator != null) {
            remove(totalSeparator);
        }
        if (totalProgressBar != null) {
            remove(totalProgressBar);
        }
        if (detailsButton != null) {
            remove(detailsButton);
        }


        int needed = (needsPipeline ? 1 : 0) + (needsSGE ? 1 : 0) + (needsNITools ? 1 : 0) + (needsBITools ? 1 : 0);

        if (detailedView) {
            JLabel previous = titleLabel;

            if (needsPipeline) {
                add(plTitleLabel);
                layout.putConstraint(north, plTitleLabel, topMargin * vDist, south, titleLabel);
                layout.putConstraint(west, plTitleLabel, dist * 15, west, titleLabel);

                add(plSeparator);
                layout.getConstraints(plSeparator).setConstraint(east, Spring.sum(Spring.constant(- 5 * panelMargin), layout.getConstraint(east, this)));
                layout.putConstraint(north, plSeparator, dist, north, plTitleLabel);
                layout.putConstraint(west, plSeparator, dist, east, plTitleLabel);

                add(plProgressBar);
                layout.getConstraints(plProgressBar).setConstraint(east, Spring.sum(Spring.constant(-3 * dist), layout.getConstraint(east, plSeparator)));
                layout.putConstraint(north, plProgressBar, vDist, south, plTitleLabel);
                layout.putConstraint(west, plProgressBar, 4 * dist, west, plTitleLabel);

                add(plActionLabel);
                layout.putConstraint(north, plActionLabel, vDist, south, plProgressBar);
                layout.putConstraint(west, plActionLabel, 2 * dist, west, plProgressBar);

                add(plDetailLabel);
                layout.putConstraint(north, plDetailLabel, 0, north, plActionLabel);
                layout.putConstraint(west, plDetailLabel, 0, east, plActionLabel);
                previous = plActionLabel;

                add(plPostInstallComboBox);
                layout.putConstraint(north, plPostInstallComboBox, vDist, south, plTitleLabel);
                layout.putConstraint(west, plPostInstallComboBox, 4 * dist, west, plTitleLabel);

                add(plPostInstallLabel);
                layout.putConstraint(north, plPostInstallLabel, vDist * 3 / 2, south, plTitleLabel);
                layout.putConstraint(west, plPostInstallLabel, dist, east, plPostInstallComboBox);
            }

            if (needsSGE) {
                add(sgeTitleLabel);
                layout.putConstraint(north, sgeTitleLabel, vDist, south, previous);
                layout.putConstraint(west, sgeTitleLabel, dist * 15, west, titleLabel);

                add(sgeSeparator);
                layout.getConstraints(sgeSeparator).setConstraint(east, Spring.sum(Spring.constant(- 5 * panelMargin), layout.getConstraint(east, this)));
                layout.putConstraint(north, sgeSeparator, dist, north, sgeTitleLabel);
                layout.putConstraint(west, sgeSeparator, dist, east, sgeTitleLabel);

                add(sgeProgressBar);
                layout.getConstraints(sgeProgressBar).setConstraint(east, Spring.sum(Spring.constant(-3 * dist), layout.getConstraint(east, sgeSeparator)));
                layout.putConstraint(north, sgeProgressBar, vDist, south, sgeTitleLabel);
                layout.putConstraint(west, sgeProgressBar, 4 * dist, west, sgeTitleLabel);

                add(sgeActionLabel);
                layout.putConstraint(north, sgeActionLabel, vDist, south, sgeProgressBar);
                layout.putConstraint(west, sgeActionLabel, 2 * dist, west, sgeProgressBar);

                add(sgeDetailLabel);
                layout.putConstraint(north, sgeDetailLabel, 0, north, sgeActionLabel);
                layout.putConstraint(west, sgeDetailLabel, 0, east, sgeActionLabel);

                previous = sgeActionLabel;
            }


            if (needsNITools) {
                add(niToolsTitleLabel);
                layout.putConstraint(north, niToolsTitleLabel, vDist, south, previous);
                layout.putConstraint(west, niToolsTitleLabel, dist * 15, west, titleLabel);


                add(niToolsSeparator);
                layout.getConstraints(niToolsSeparator).setConstraint(east, Spring.sum(Spring.constant(- 5 * panelMargin), layout.getConstraint(east, this)));
                layout.putConstraint(north, niToolsSeparator, dist, north, niToolsTitleLabel);
                layout.putConstraint(west, niToolsSeparator, dist, east, niToolsTitleLabel);

                add(niToolsProgressBar);
                layout.getConstraints(niToolsProgressBar).setConstraint(east, Spring.sum(Spring.constant(-3 * dist), layout.getConstraint(east, niToolsSeparator)));
                layout.putConstraint(north, niToolsProgressBar, vDist, south, niToolsTitleLabel);
                layout.putConstraint(west, niToolsProgressBar, 4 * dist, west, niToolsTitleLabel);

                add(niToolsActionLabel);
                layout.putConstraint(north, niToolsActionLabel, vDist, south, niToolsProgressBar);
                layout.putConstraint(west, niToolsActionLabel, 2 * dist, west, niToolsProgressBar);

                add(niToolsDetailLabel);
                layout.putConstraint(north, niToolsDetailLabel, 0, north, niToolsActionLabel);
                layout.putConstraint(west, niToolsDetailLabel, 0, east, niToolsActionLabel);

                previous = niToolsActionLabel;

            }

            if (needsBITools) {
                add(biToolsTitleLabel);
                layout.putConstraint(north, biToolsTitleLabel, vDist, south, previous);
                layout.putConstraint(west, biToolsTitleLabel, dist * 15, west, titleLabel);


                add(biToolsSeparator);
                layout.getConstraints(biToolsSeparator).setConstraint(east, Spring.sum(Spring.constant(- 5 * panelMargin), layout.getConstraint(east, this)));
                layout.putConstraint(north, biToolsSeparator, dist, north, biToolsTitleLabel);
                layout.putConstraint(west, biToolsSeparator, dist, east, biToolsTitleLabel);

                add(biToolsProgressBar);
                layout.getConstraints(biToolsProgressBar).setConstraint(east, Spring.sum(Spring.constant(-3 * dist), layout.getConstraint(east, biToolsSeparator)));
                layout.putConstraint(north, biToolsProgressBar, vDist, south, biToolsTitleLabel);
                layout.putConstraint(west, biToolsProgressBar, 4 * dist, west, biToolsTitleLabel);

                add(biToolsActionLabel);
                layout.putConstraint(north, biToolsActionLabel, vDist, south, biToolsProgressBar);
                layout.putConstraint(west, biToolsActionLabel, 2 * dist, west, biToolsProgressBar);

                add(biToolsDetailLabel);
                layout.putConstraint(north, biToolsDetailLabel, 0, north, biToolsActionLabel);
                layout.putConstraint(west, biToolsDetailLabel, 0, east, biToolsActionLabel);

                previous = biToolsActionLabel;

            }





            if (needed > 1) {
                add(totalProgressBar);
                layout.getConstraints(totalProgressBar).setConstraint(east, Spring.sum(Spring.constant(-panelMargin), layout.getConstraint(east, this)));
                layout.putConstraint(south, totalProgressBar, -vDist, south, bottomSeparator);
                layout.putConstraint(west, totalProgressBar, 4 * dist, west, totalTitleLabel);

                add(totalTitleLabel);
                layout.putConstraint(south, totalTitleLabel, -vDist, north, totalProgressBar);
                layout.putConstraint(west, totalTitleLabel, panelMargin, west, this);

                add(totalSeparator);
                layout.getConstraints(totalSeparator).setConstraint(east, layout.getConstraint(east, this));
                layout.putConstraint(south, totalSeparator, -vDist, north, totalProgressBar);
                layout.putConstraint(west, totalSeparator, dist, east, totalTitleLabel);

                add(detailsButton);
                layout.putConstraint(south, detailsButton, -vDist, north, totalSeparator);
                layout.putConstraint(east, detailsButton, 0, east, totalProgressBar);

                add(logsButton);
                layout.putConstraint(south, logsButton, -vDist, north, detailsButton);
                layout.putConstraint(east, logsButton, 0, east, detailsButton);

                add(logLabel);
                layout.putConstraint(south, logLabel, -vDist, north, totalTitleLabel);
                layout.putConstraint(west, logLabel, 0, west, totalTitleLabel);

            } else {

                add(detailsButton);
                layout.putConstraint(north, detailsButton, 5 * vDist, south, previous);
                layout.putConstraint(east, detailsButton, -panelMargin, east, this);

                add(logsButton);
                layout.putConstraint(south, logsButton, -vDist, north, detailsButton);
                layout.putConstraint(east, logsButton, 0, east, detailsButton);

                add(logLabel);
                layout.putConstraint(north, logLabel, 2 * vDist, south, previous);
                layout.putConstraint(west, logLabel, panelMargin, west, this);
            }
        } else {
            add(totalTitleLabel);
            layout.putConstraint(north, totalTitleLabel, 5 * vDist, south, titleLabel);
            layout.putConstraint(west, totalTitleLabel, panelMargin, west, this);

            add(totalSeparator);
            layout.getConstraints(totalSeparator).setConstraint(east, layout.getConstraint(east, this));
            layout.putConstraint(north, totalSeparator, dist, north, totalTitleLabel);
            layout.putConstraint(west, totalSeparator, dist, east, totalTitleLabel);

            add(totalProgressBar);
            layout.getConstraints(totalProgressBar).setConstraint(east, Spring.sum(Spring.constant(-panelMargin), layout.getConstraint(east, this)));
            layout.putConstraint(north, totalProgressBar, vDist, south, totalTitleLabel);
            layout.putConstraint(west, totalProgressBar, 4 * dist, west, totalTitleLabel);

            add(detailsButton);
            layout.putConstraint(north, detailsButton, vDist, south, totalProgressBar);
            layout.putConstraint(east, detailsButton, 0, east, totalProgressBar);
        }


        validate();
        repaint();

    }

    public void saveUserInput() {
    }

    public boolean checkUserInput() {

        return true;
    }

    public void panelActivated() {
        needsActionQueue = new LinkedList<String>();

        Configuration.setConfig(Configuration.CONFIG_INSTALLER_MODE_INSTALL, "true");

        File outLogs = new File("out.log");
        File errLogs = new File("err.log");

        if (outLogs.exists()) {
            outLogs.delete();
        }

        if (errLogs.exists()) {
            errLogs.delete();
        }

        if (headless) {
            totalProgressBar = new JProgressBar();
        } else {
            initComponents();
            initLayout();
            initListeners();
            sif.setNextEnabled(true);
        } 

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd___HH_mm_ss");

        String baseDir = Constants.currentDir();
        baseDir = baseDir.substring(0, baseDir.lastIndexOf(File.separator));

        logFileName = baseDir + "/logs/install_" + sdf.format(new Date()) + ".log";

        startInstallation();
    }
}
