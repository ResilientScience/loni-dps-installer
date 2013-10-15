
package pipelineserverinstaller.gui.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.table.TableColumnModel;
import pipelineserverinstaller.Configuration;
import pipelineserverinstaller.Constants;
import pipelineserverinstaller.gui.ComponentFactory;
import pipelineserverinstaller.gui.models.WorkflowsTableModel;

/**
 *
 * @author Petros Petrosyan
 */
public class SummaryPanel extends AbstractStepPanel {

    // just to make the compiler stop complaining
    static final long serialVersionUID = 1L;

    private String north = SpringLayout.NORTH;
    private String south = SpringLayout.SOUTH;
    private String west = SpringLayout.WEST;
    private String east = SpringLayout.EAST;

    private final int panelMargin = 20;
    private final int dist = 5;
    private final int vDist = 8;

    private JLabel titleLabel;
    private JLabel additionalActionsLabel;

    private JLabel testWorkflowsLabel;
    private JTable workflowsTable;
    private WorkflowsTableModel workflowsTableModel;
    private JScrollPane workflowsTableScrollPane;

    private JLabel bottomNoteLabel;
    private JCheckBox startPLServerCheckbox;
    private JCheckBox startPLClientCheckbox;
    private JButton startPLConfigButton;
    
    private JFileChooser fileChooser;

    private final String WORKFLOW_NAME_AFNI         = "AFNI";
    private final String WORKFLOW_NAME_AIR          = "AIR";
    private final String WORKFLOW_NAME_BRAINSUITE   = "BrainSuite";
    private final String WORKFLOW_NAME_FSL          = "FSL";
    private final String WORKFLOW_NAME_FREESURFER   = "FreeSurfer";
    private final String WORKFLOW_NAME_LONI         = "LONI Tools";
    private final String WORKFLOW_NAME_MINC         = "MINC";
    private final String WORKFLOW_NAME_DTK          = "DTK";
    private final String WORKFLOW_NAME_ITK          = "ITK";
    private final String WORKFLOW_NAME_GAMMA        = "GAMMA";
    
    private final String WORKFLOW_NAME_EMBOSS       = "EMBOSS";
    private final String WORKFLOW_NAME_PICARD       = "Picard";
    private final String WORKFLOW_NAME_MSA          = "MSA";
    private final String WORKFLOW_NAME_BATWING      = "BATWING";
    private final String WORKFLOW_NAME_BAYESASS     = "BayesAss";
    private final String WORKFLOW_NAME_FORMATOMATIC = "Formatomatic";
    private final String WORKFLOW_NAME_GENEPOP      = "GENEPOP";
    private final String WORKFLOW_NAME_MIGRATE      = "Migrate";
    private final String WORKFLOW_NAME_GWASS        = "GWASS";
    private final String WORKFLOW_NAME_MRFAST       = "mrFAST";
    private final String WORKFLOW_NAME_BOWTIE       = "Bowtie";
    private final String WORKFLOW_NAME_SAMTOOLS     = "SamTools";
    private final String WORKFLOW_NAME_PLINK        = "PLINK";
    private final String WORKFLOW_NAME_MAQ          = "MAQ";
    private final String WORKFLOW_NAME_MIBLAST      = "miBLAST";

    private final String WORKFLOW_NAME_NONE         = "Sample Workflow";
    
    private final String CONFIGURE_LABEL_ENABLED = "Configure the server with advanced options...";
    private final String CONFIGURE_LABEL_DISABLED = "Configuration utility is now open";
    
    
    /** Creates a new instance of LicensePanel */
    public SummaryPanel() {
        initComponents();
        initLayout();
        initListeners();
    }

    private void initComponents() {
        titleLabel = ComponentFactory.label("<html><font size=\"4\"><b>Installation Complete</b></font></html>");
        additionalActionsLabel = ComponentFactory.label("Please choose what would you like to do before installer exits:");

        bottomNoteLabel = ComponentFactory.label("Thanks for installing. Press \"Finish\" button to exit the installer.");
        startPLServerCheckbox = ComponentFactory.checkbox("Start the LONI Pipeline Server");
        startPLClientCheckbox = ComponentFactory.checkbox("Start the LONI Pipeline Client to validate the installation");
        startPLConfigButton = ComponentFactory.button(CONFIGURE_LABEL_ENABLED);
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        testWorkflowsLabel = ComponentFactory.label("Please select which workflows you would like to test:");
        testWorkflowsLabel.setVisible(false);

        workflowsTableModel = new WorkflowsTableModel();
        workflowsTable = new JTable(workflowsTableModel);
        workflowsTableScrollPane = new JScrollPane(workflowsTable);

        workflowsTableScrollPane.setPreferredSize(new Dimension(300,100));

        workflowsTableScrollPane.setVisible(false);
        
        TableColumnModel tcm = workflowsTable.getColumnModel();

        workflowsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tcm.getColumn(0).setPreferredWidth(60);
        tcm.getColumn(1).setPreferredWidth(240);




        this.setPreferredSize(new Dimension(400, 400));
    }


    private void initLayout() {
        SpringLayout layout = new SpringLayout();
        setLayout(layout);

        add(titleLabel);
        layout.putConstraint(north, titleLabel, panelMargin, north, this);
        layout.putConstraint(west, titleLabel, panelMargin, west, this);

        add(additionalActionsLabel);
        layout.putConstraint(north, additionalActionsLabel, vDist * 3, south, titleLabel);
        layout.putConstraint(west, additionalActionsLabel, panelMargin, west, this);

        add(startPLConfigButton);
        layout.putConstraint(north, startPLConfigButton, vDist * 3, south, additionalActionsLabel);
        layout.putConstraint(west, startPLConfigButton, panelMargin * 3, west, this);

        add(startPLServerCheckbox);
        layout.putConstraint(north, startPLServerCheckbox, vDist * 3, south, startPLConfigButton);
        layout.putConstraint(west, startPLServerCheckbox, panelMargin * 3, west, this);

        add(startPLClientCheckbox);
        layout.putConstraint(north, startPLClientCheckbox, vDist, south, startPLServerCheckbox);
        layout.putConstraint(west, startPLClientCheckbox, 0, west, startPLServerCheckbox);

        add(testWorkflowsLabel);
        layout.putConstraint(north, testWorkflowsLabel, vDist, south, startPLClientCheckbox);
        layout.putConstraint(west, testWorkflowsLabel, 5 * dist, west, startPLClientCheckbox);


        add(workflowsTableScrollPane);
        //layout.getConstraints(workflowsTableScrollPane).setConstraint(east, Spring.sum(Spring.constant( -panelMargin ), layout.getConstraint(east, this)));
        layout.getConstraints(workflowsTableScrollPane).setConstraint(south, Spring.sum(Spring.constant( -vDist ), layout.getConstraint(north, bottomNoteLabel)));
        layout.putConstraint(north, workflowsTableScrollPane, vDist, south, testWorkflowsLabel);
        layout.putConstraint(west, workflowsTableScrollPane, 5 * dist, west, testWorkflowsLabel);

        add(bottomNoteLabel);
        layout.putConstraint(north, bottomNoteLabel, -panelMargin, south, this);
        layout.putConstraint(west, bottomNoteLabel, panelMargin, west, this);

    }


    private void initListeners() {
        startPLServerCheckbox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if ( startPLServerCheckbox.isSelected() ) {
                    startPLClientCheckbox.setEnabled(true);
                } else {
                    startPLClientCheckbox.setEnabled(false);
                    startPLClientCheckbox.setSelected(false);
                    workflowsTableScrollPane.setVisible(false);
                    testWorkflowsLabel.setVisible(false);
                }
            }

        });

        startPLConfigButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        startPLConfigButton.setEnabled(false);
                        startPLConfigButton.setText(CONFIGURE_LABEL_DISABLED);
                        Configuration.setConfig(Configuration.CONFIGURE_PIPELINE_SERVER, "true");
                        sif.postInstall();
                        startPLConfigButton.setText(CONFIGURE_LABEL_ENABLED);
                        startPLConfigButton.setEnabled(true);
                    }
                };
                t.start();
            }

        });

        startPLClientCheckbox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if ( startPLClientCheckbox.isSelected() ) {
                    testWorkflowsLabel.setVisible(true);
                    workflowsTableScrollPane.setVisible(true);
                } else {
                    testWorkflowsLabel.setVisible(false);
                    workflowsTableScrollPane.setVisible(false);
                }
            }

        });
        
    }

    public void saveUserInput() {
        // there is no back button for this panel, so no need to split checkUserInput and saveUserInput
        // All the functionality should be in checkUserInput method.
    }

    public boolean checkUserInput() {
        if (!startPLServerCheckbox.isVisible()) {
            Configuration.setConfig(Configuration.START_PIPELINE_SERVER, "false");
        } else {
            if (startPLServerCheckbox.isSelected()) {
                Configuration.setConfig(Configuration.START_PIPELINE_SERVER, "true");
            } else {
                Configuration.setConfig(Configuration.START_PIPELINE_SERVER, "false");
            }
        }

        if (!startPLClientCheckbox.isVisible()) {
            Configuration.setConfig(Configuration.START_PIPELINE_CLIENT, "false");
        } else {
            if (startPLClientCheckbox.isSelected()) {
                Configuration.setConfig(Configuration.START_PIPELINE_CLIENT, "true");

                StringBuilder sb = new StringBuilder();

                boolean fsl = false;
                boolean freesurfer = false;
                boolean dtk = false;
                boolean brainsuite = false;
                for (int i = 0; i < workflowsTable.getRowCount(); i++) {

                    Boolean isSelected = (Boolean) workflowsTable.getValueAt(i, WorkflowsTableModel.COLUMN_SELECTED);

                    if (!isSelected) {
                        continue;
                    }

                    if (sb.length() == 0) {
                        sb.append("-url ");
                    } else {
                        sb.append(" ");
                    }

                    String name = (String) workflowsTable.getValueAt(i, WorkflowsTableModel.COLUMN_NAME);

                    if (name.equals(WORKFLOW_NAME_AFNI)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/AFNI.pipe");
                    } else if (name.equals(WORKFLOW_NAME_AIR)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/AIR.pipe");
                    } else if (name.equals(WORKFLOW_NAME_BRAINSUITE)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/BrainSuite.pipe");
                        brainsuite = true;
                    } else if (name.equals(WORKFLOW_NAME_FSL)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/FSL.pipe");
                        fsl = true;
                    } else if (name.equals(WORKFLOW_NAME_FREESURFER)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/FreeSurfer.pipe");
                        freesurfer = true;
                    } else if (name.equals(WORKFLOW_NAME_LONI)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/LONI.pipe");
                    } else if (name.equals(WORKFLOW_NAME_MINC)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/MINC.pipe");
                    } else if (name.equals(WORKFLOW_NAME_DTK)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/DTK.pipe");
                        dtk = true;
                    } else if (name.equals(WORKFLOW_NAME_ITK)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/ITK.pipe");
                    } else if (name.equals(WORKFLOW_NAME_GAMMA)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/GAMMA.pipe");
                    } else if (name.equals(WORKFLOW_NAME_EMBOSS)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/EMBOSS.pipe");
                    } else if (name.equals(WORKFLOW_NAME_MSA)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/MSA.pipe");
                    } else if (name.equals(WORKFLOW_NAME_BATWING)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/BATWING.pipe");
                    } else if (name.equals(WORKFLOW_NAME_FORMATOMATIC)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/Formatomatic.pipe");
                    } else if (name.equals(WORKFLOW_NAME_GENEPOP)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/GENEPOP.pipe");
                    } else if (name.equals(WORKFLOW_NAME_MIGRATE)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/Migrate.pipe");
                    } else if (name.equals(WORKFLOW_NAME_GWASS)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/GWASS.pipe");
                    } else if (name.equals(WORKFLOW_NAME_MRFAST)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/mrFast.pipe");
                    } else if (name.equals(WORKFLOW_NAME_BOWTIE)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/Bowtie.pipe");
                    } else if (name.equals(WORKFLOW_NAME_PLINK)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/PLINK.pipe");
                    } else if (name.equals(WORKFLOW_NAME_MAQ)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/MAQ.pipe");
                    } else if (name.equals(WORKFLOW_NAME_MIBLAST)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/miBLAST.pipe");
                    } else if (name.equals(WORKFLOW_NAME_NONE)) {
                        sb.append("http://users.loni.ucla.edu/~pipeline/dps/testWorkflows/TestWorkflow.pipe");
                    }
                }

                if (sb.length() > 0) {
                    sb.append(" -execute -replaceServer=");
                    sb.append(Configuration.getConfig(Configuration.CONFIG_PIPELINE_HOSTNAME));

                    if (!Configuration.getConfig(Configuration.CONFIG_PIPELINE_PORT).equals(String.valueOf(Constants.serverDefaultPort))) {
                        sb.append(":");
                        sb.append(Configuration.getConfig(Configuration.CONFIG_PIPELINE_PORT));
                    }

                    sb.append(" -username=");
                    sb.append(Configuration.getConfig(Configuration.CONFIG_PIPELINE_USER));

                    sb.append(" {testdata}=");
                    sb.append(Constants.currentDir());
                    sb.append(File.separator);
                    sb.append("install_files");
                    sb.append(File.separator);
                    sb.append("pipeline");

                    if (fsl) {
                        sb.append(" {FSLVERSION}=");
                        sb.append(Configuration.getConfig(Configuration.CONFIG_TOOLS_FSL_VERSION));
                    }

                    if (freesurfer) {
                        sb.append(" {FREESURFERVERSION}=");
                        sb.append(Configuration.getConfig(Configuration.CONFIG_TOOLS_FREESURFER_VERSION));
                    }
                    
                    if (dtk) {
                        sb.append(" {DTKVERSION}=");
                        sb.append(Configuration.getConfig(Configuration.CONFIG_TOOLS_DTK_VERSION));
                    }
                    
                    if (brainsuite) {
                        sb.append(" {BRAINSUITEVERSION}=");
                        sb.append(Configuration.getConfig(Configuration.CONFIG_TOOLS_BRAINSUITE_VERSION));
                    }

                    Configuration.setConfig(Configuration.START_PIPELINE_CLIENT_ARGUMENTS, sb.toString());
                }
            } else {
                Configuration.setConfig(Configuration.START_PIPELINE_CLIENT, "false");
            }
        }


        return true;
    }
    
    public void panelActivated() {
        boolean installPipeline = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_PIPELINE));

        if ( !installPipeline ) {
            additionalActionsLabel.setVisible(false);
            startPLConfigButton.setVisible(false);
            startPLServerCheckbox.setVisible(false);
            startPLServerCheckbox.setSelected(false);

            startPLClientCheckbox.setVisible(false);
            startPLClientCheckbox.setSelected(false);
        }
        else {
            additionalActionsLabel.setVisible(true);
            startPLConfigButton.setVisible(true);
            startPLServerCheckbox.setVisible(true);

            boolean serverAlreadyStarted = Boolean.parseBoolean(Configuration.getConfig(Configuration.START_PIPELINE_SERVER));
            boolean clientAlreadyStarted = Boolean.parseBoolean(Configuration.getConfig(Configuration.START_PIPELINE_CLIENT));

            if ( serverAlreadyStarted ) {
                Configuration.setConfig(Configuration.START_PIPELINE_SERVER,"true");
                startPLServerCheckbox.setSelected(true);
                startPLServerCheckbox.setEnabled(false);
                startPLServerCheckbox.setText("Server already started");
            } else {
                startPLServerCheckbox.setSelected(true);
            }

            if ( clientAlreadyStarted )
                startPLClientCheckbox.setText("Start another Pipeline Client to validate the installation");

            startPLClientCheckbox.setVisible(true);
            startPLClientCheckbox.setSelected(false);

            boolean installNITools = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_NI_TOOLS));
            boolean installBITools = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_BI_TOOLS));

            if ( installNITools ) {
                boolean installAFNI = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_AFNI));
                boolean installAIR = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_AIR));
                boolean installBrainSuite = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_BRAINSUITE));
                boolean installFSL = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_FSL));
                boolean installFreeSurfer = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_FREESURFER));
                boolean installLONI = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_LONITOOLS));
                boolean installMINC = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_MINC));
                boolean installDTK = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_DTK));
                boolean installGAMMA = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_GAMMA));
                boolean installITK = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_ITK));

                if (installAFNI) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_AFNI});
                }
                if (installAIR) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_AIR});
                }
                if (installBrainSuite) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_BRAINSUITE});
                }
                if (installFSL) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_FSL});
                }
                if (installFreeSurfer) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_FREESURFER});
                }
                if (installLONI) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_LONI});
                }
                if (installMINC) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_MINC});
                }
                if (installDTK) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_DTK});
                }
                if (installGAMMA) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_GAMMA});
                }
                if (installITK) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_ITK});
                }            
            }

            if ( installBITools ) {
                boolean installEmboss = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_EMBOSS));
                boolean installMsa = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_MSA));
                boolean installBatwing = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_BATWING));
                boolean installFormatomatic = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_FORMATOMATIC));
                boolean installGenepop = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_GENEPOP));
                boolean installMigrate = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_MIGRATE));
                boolean installGwass = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_GWASS));
                boolean installMrfast = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_MRFAST));
                boolean installBowtie = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_BOWTIE));
                boolean installPlink = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_PLINK));
                boolean installMaq = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_MAQ));
                boolean installMiblast = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_MIBLAST));

                if (installEmboss) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_EMBOSS});
                }
                if (installMsa) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_MSA});
                }
                if (installBatwing) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_BATWING});
                }
                if (installFormatomatic) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_FORMATOMATIC});
                }
                if (installGenepop) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_GENEPOP});
                }
                if (installMigrate) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_MIGRATE});
                }
                if (installGwass) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_GWASS});
                }
                if (installMrfast) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_MRFAST});
                }
                if (installBowtie) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_BOWTIE});
                }
                if (installPlink) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_PLINK});
                }
                if (installMaq) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_MAQ});
                }
                if (installMiblast) {
                    workflowsTableModel.addRow(new Object[]{true, WORKFLOW_NAME_MIBLAST});
                }
            }
            
            if ( workflowsTableModel.getRowCount() == 0 ) {
                workflowsTableModel.addRow(new Object[] {true, WORKFLOW_NAME_NONE});
            }
            
        }
    }

}

