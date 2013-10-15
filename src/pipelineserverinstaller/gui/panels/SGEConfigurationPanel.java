
package pipelineserverinstaller.gui.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
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
public class SGEConfigurationPanel extends AbstractStepPanel {

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


    private JLabel sgeLocationLabel;
    private JLabel sgeClusterLabel;
    private JLabel sgeExecHostsLabel;
    private JLabel sgeSpoolDirLabel;

    private JCheckBox sgeAdminCheckbox;
    private JLabel sgeAdminLabel;
    private JTextField sgeAdminField;
    private JTextArea sgeAdminWarningArea;
    
    private JTextField  sgeLocationField;
    private JTextField  sgeClusterField;
    private JTextArea   sgeExecHostsArea;
    private JTextArea   sgeExecHostsExampleArea;
    
    private JTextField  sgeSpoolDirField;
    
    private JScrollPane sgeExecHostsScrollPane;
    private JScrollPane sgeExecHostsExampleScrollPane;
    

    private JSeparator topSeparator;
    private JSeparator middleSeparator;
    private JSeparator sideSeparator;
    private String hostname;

    private boolean hostValidationPassed;
    
    private boolean needsSGE;

    private JLabel validatingHostsLabel;

    
    /** Creates a new instance of LicensePanel */
    public SGEConfigurationPanel() {
        
    }

    private void initComponents() {
        titleLabel = ComponentFactory.label("<html><font size=\"4\"><b>SGE Configuration</b></font></html>");

        needsSGE = Boolean.parseBoolean(Configuration.getConfig(Configuration.CONFIG_INSTALL_SGE));

        topSeparator = new JSeparator(JSeparator.HORIZONTAL);
        middleSeparator = new JSeparator(JSeparator.HORIZONTAL);
        sideSeparator = new JSeparator(JSeparator.VERTICAL);


        sgeLocationField        = ComponentFactory.textfield(15);
        sgeLocationLabel        = ComponentFactory.label("SGE Installation Directory");
        sgeClusterLabel         = ComponentFactory.label("SGE Cluster Name");
        sgeSpoolDirLabel        = ComponentFactory.label("SGE Spool Directory");
        sgeExecHostsLabel       = ComponentFactory.label("Execution Hosts (one per line):");

        sgeClusterField         = ComponentFactory.textfield(15);
        sgeExecHostsArea        = ComponentFactory.textarea();
        sgeExecHostsExampleArea = ComponentFactory.textarea();
        sgeSpoolDirField        = ComponentFactory.textfield(15);

        sgeAdminCheckbox        = ComponentFactory.checkbox("Set custom SGE admin");
        sgeAdminLabel           = ComponentFactory.label("SGE Admin username");
        sgeAdminWarningArea     = ComponentFactory.textarea("IMPORTANT: The specified username must exist on all specified execution hosts, otherwise installation may fail.");
        sgeAdminField           = ComponentFactory.textfield(15);


        sgeAdminWarningArea.setBackground(getBackground());
        sgeAdminWarningArea.setWrapStyleWord(true);
        sgeAdminWarningArea.setLineWrap(true);
        sgeAdminWarningArea.setEditable(false);
        sgeAdminWarningArea.setBorder(BorderFactory.createEmptyBorder());

        sgeAdminWarningArea.setVisible(false);
        sgeAdminLabel.setVisible(false);
        sgeAdminField.setVisible(false);

        sgeExecHostsExampleArea.setPreferredSize(new Dimension(100, 60));

        sgeExecHostsExampleArea.setLineWrap(true);
        sgeExecHostsExampleArea.setWrapStyleWord(true);
        sgeExecHostsExampleArea.setEditable(false);
        sgeExecHostsExampleArea.setBackground(getBackground());


        sgeExecHostsArea.setLineWrap(true);
        sgeExecHostsArea.setWrapStyleWord(true);

        sgeExecHostsScrollPane = new JScrollPane(sgeExecHostsArea);
        sgeExecHostsExampleScrollPane = new JScrollPane(sgeExecHostsExampleArea);
        sgeExecHostsExampleScrollPane.setBorder(BorderFactory.createTitledBorder("Example"));

        ImageIcon loadingIcon = new ImageIcon(this.getClass().getResource("/graphics/Loading.gif"));
        validatingHostsLabel = ComponentFactory.label();
        validatingHostsLabel.setIcon(loadingIcon);
        validatingHostsLabel.setVisible(false);


        String sgeLocationHint = "<html><body>The directory where you want the SGE to be installed.<br/>" +
                                 "This needs to be in a Shared Directory which can be accessed by<br/>" +
                                 "all the execution hosts.</body></html>";
        sgeLocationLabel.setToolTipText(sgeLocationHint);
        sgeLocationField.setToolTipText(sgeLocationHint);

        String sgeClusterHint = "<html><body>The name of the SGE cluster. Leave the default if you don't know what it is.</body></html>";
        sgeClusterLabel.setToolTipText(sgeClusterHint);
        sgeClusterField.setToolTipText(sgeClusterHint);

        String sgeSpoolDirHint = "<html><body>The spool directory of SGE.<br/>" +
                                "This needs to be in a Shared Directory which can be accessed by<br/>" +
                                "all the execution hosts.</body></html>";
        sgeSpoolDirLabel.setToolTipText(sgeSpoolDirHint);
        sgeSpoolDirField.setToolTipText(sgeSpoolDirHint);

        String sgeExecHostsHint = "<html><body>IP Addresses or domain names of all the hosts<br/>" +
                                  "where SGE will be installed as an execution host.<br/><br/>" +
                                  "All the hosts are required to allow <b>ssh<br/>" +
                                  "connections without password for root user</b></body></html>";


        sgeExecHostsLabel.setToolTipText(sgeExecHostsHint);
        sgeExecHostsArea.setToolTipText(sgeExecHostsHint);


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

        add(sideSeparator);
        layout.getConstraints(sideSeparator).setConstraint(south, layout.getConstraint(south, this));
        layout.putConstraint(north, sideSeparator, 2 * vDist, south, titleLabel);
        layout.putConstraint(west, sideSeparator, 15 * dist, east, titleLabel);

        add(sgeLocationLabel);
        layout.putConstraint(north, sgeLocationLabel, vDist, north, sideSeparator);
        layout.putConstraint(west, sgeLocationLabel, 4 * dist, west, titleLabel);

        add(sgeLocationField);
        layout.putConstraint(north, sgeLocationField, vDist, south, sgeLocationLabel);
        layout.putConstraint(west, sgeLocationField, 0, west, sgeLocationLabel);

        add(sgeClusterLabel);
        layout.putConstraint(north, sgeClusterLabel, 2 * vDist, south, sgeLocationField);
        layout.putConstraint(west, sgeClusterLabel, 0 , west, sgeLocationLabel);

        add(sgeClusterField);
        layout.putConstraint(north, sgeClusterField, vDist, south, sgeClusterLabel);
        layout.putConstraint(west, sgeClusterField, 0, west, sgeLocationLabel);

        add(sgeSpoolDirLabel);
        layout.putConstraint(north, sgeSpoolDirLabel, 2 * vDist, south, sgeClusterField);
        layout.putConstraint(west, sgeSpoolDirLabel, 0 , west, sgeLocationLabel);

        add(sgeSpoolDirField);
        layout.putConstraint(north, sgeSpoolDirField, vDist, south, sgeSpoolDirLabel);
        layout.putConstraint(west, sgeSpoolDirField, 0, west, sgeLocationLabel);

        add(sgeAdminCheckbox);
        layout.putConstraint(north, sgeAdminCheckbox, vDist, south, sgeSpoolDirField);
        layout.putConstraint(west, sgeAdminCheckbox, 0, west, sgeLocationLabel);

        add(sgeAdminLabel);
        layout.putConstraint(north, sgeAdminLabel, vDist, south, sgeAdminCheckbox);
        layout.putConstraint(west, sgeAdminLabel, 0, west, sgeLocationLabel);

        add(sgeAdminField);
        layout.putConstraint(north, sgeAdminField, vDist, south, sgeAdminLabel);
        layout.putConstraint(west, sgeAdminField, 0, west, sgeLocationLabel);

        add(sgeAdminWarningArea);
        layout.putConstraint(north, sgeAdminWarningArea, vDist / 2, south, sgeAdminField);
        layout.getConstraints(sgeAdminWarningArea).setConstraint(east, Spring.sum(Spring.constant(-dist),layout.getConstraint(west, sideSeparator)));
        layout.putConstraint(west, sgeAdminWarningArea, 0, west, sgeLocationLabel);

        add(sgeExecHostsLabel);
        layout.putConstraint(north, sgeExecHostsLabel, vDist, north, sideSeparator);
        layout.putConstraint(west, sgeExecHostsLabel, 4 * dist , east, sideSeparator);

        add(sgeExecHostsScrollPane);
        layout.getConstraints(sgeExecHostsScrollPane).setConstraint(east, Spring.sum(Spring.constant(-panelMargin),layout.getConstraint(east, this)));
        layout.getConstraints(sgeExecHostsScrollPane).setConstraint(south, Spring.sum(Spring.constant(-vDist),layout.getConstraint(north, sgeExecHostsExampleScrollPane)));
        layout.putConstraint(north, sgeExecHostsScrollPane, vDist, south, sgeExecHostsLabel);
        layout.putConstraint(west, sgeExecHostsScrollPane, 0, west, sgeExecHostsLabel);

        add(sgeExecHostsExampleScrollPane);
        layout.getConstraints(sgeExecHostsExampleScrollPane).setConstraint(east, Spring.sum(Spring.constant(-panelMargin),layout.getConstraint(east, this)));
        layout.getConstraints(sgeExecHostsExampleScrollPane).setConstraint(south, Spring.sum(Spring.constant(-panelMargin),layout.getConstraint(south, this)));
        layout.putConstraint(west, sgeExecHostsExampleScrollPane, 0, west, sgeExecHostsLabel);

        add(validatingHostsLabel);
        layout.putConstraint(north, validatingHostsLabel, vDist, south, sgeExecHostsScrollPane);
        layout.putConstraint(west, validatingHostsLabel, 0, west, sgeExecHostsScrollPane);


    }

    private void populateData() {

        String sharedDir = Configuration.getConfig(Configuration.CONFIG_SHARED_FILESYSTEM_PATH);
        
        String cluster = Configuration.getConfig(Configuration.CONFIG_SGE_CLUSTER);
        String location = Configuration.getConfig(Configuration.CONFIG_SGE_ROOT);
        String spoolDir = Configuration.getConfig(Configuration.CONFIG_SGE_SPOOL_DIR);
        String hosts = Configuration.getConfig(Configuration.CONFIG_SGE_EXEC_HOSTS);
        String admin = Configuration.getConfig(Configuration.CONFIG_SGE_ADMIN_USER);

        if (cluster == null) {
            cluster = "cluster";
        }

        if (location == null) {
            location = sharedDir + "sge";
        }

        if (spoolDir == null) {
            spoolDir = location + File.separator + "spool";
        }

        if (hosts == null) {
            hosts = "";
        } else {
            hosts = hosts.replaceAll(",", "\n");
        }


        if ( admin != null ) {
            sgeAdminLabel.setVisible(true);
            sgeAdminField.setVisible(true);
            sgeAdminField.setText(admin);
            sgeAdminCheckbox.setSelected(true);
        } else {
            sgeAdminLabel.setVisible(false);
            sgeAdminField.setVisible(false);
            sgeAdminField.setText("");
            sgeAdminCheckbox.setSelected(false);
        }

        sgeClusterField.setText(cluster);
        sgeLocationField.setText(location);
        sgeSpoolDirField.setText(spoolDir);
        sgeExecHostsArea.setText(hosts);


        int index = hostname.indexOf(".");
        if ( index != -1 ) {
            String domainName = hostname.substring(index);

            StringBuilder exampleBuilder = new StringBuilder();

            for ( int i = 1; i <= 3; i++ ) {
                exampleBuilder.append("   host-");
                exampleBuilder.append(i);
                exampleBuilder.append(domainName);
                exampleBuilder.append("\n");
            }

            sgeExecHostsExampleArea.setText(exampleBuilder.toString());
        } else {
            sgeExecHostsExampleArea.setText("   cluster-1.loni.ucla.edu\n   cluster-2.loni.ucla.edu\n   cluster-n.loni.ucla.edu");
        }
        
    }

    private void initListeners() {
        sgeAdminCheckbox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if ( sgeAdminCheckbox.isSelected() ) {
                    sgeAdminLabel.setVisible(true);
                    sgeAdminField.setVisible(true);
                    sgeAdminWarningArea.setVisible(true);
                } else {
                    sgeAdminLabel.setVisible(false);
                    sgeAdminField.setVisible(false);
                    sgeAdminWarningArea.setVisible(false);
                }
            }

        });

    }

    public void saveUserInput() {
        if ( !needsSGE )
            return;

         String execHosts = sgeExecHostsArea.getText().trim().replaceAll("\n", ",");

         String admin = null;
         if ( sgeAdminCheckbox.isSelected() ) {
            admin = sgeAdminField.getText().trim();
            if (admin.equals("root")) {
                admin = null;
            }
        }

        Configuration.setConfig(Configuration.CONFIG_SGE_ROOT, sgeLocationField.getText().trim());
        Configuration.setConfig(Configuration.CONFIG_SGE_SPOOL_DIR, sgeSpoolDirField.getText().trim());
        Configuration.setConfig(Configuration.CONFIG_SGE_CLUSTER, sgeClusterField.getText().trim());
        Configuration.setConfig(Configuration.CONFIG_SGE_SUBMIT_HOSTS, hostname);
        Configuration.setConfig(Configuration.CONFIG_SGE_EXEC_HOSTS, execHosts);
        Configuration.setConfig(Configuration.CONFIG_SGE_ADMIN_HOSTS, hostname);
        Configuration.setConfig(Configuration.CONFIG_SGE_CONFIG_QUEUE, "true");
        Configuration.setConfig(Configuration.CONFIG_SGE_ADMIN_USER, admin);

        Configuration.setConfig(Configuration.CONFIG_SGE_QUEUE_NAME, "pipeline.q");
        Configuration.setConfig(Configuration.CONFIG_SGE_QUEUE_HOSTLIST, "@pipeline");
        Configuration.setConfig(Configuration.CONFIG_SGE_QUEUE_SLOTS, execHosts);

    }

    public boolean checkUserInput() {
        if ( !needsSGE || hostValidationPassed )
            return true;

        String sharedDir = Configuration.getConfig(Configuration.CONFIG_SHARED_FILESYSTEM_PATH);
        String location = sgeLocationField.getText().trim();

        if ( location.length() == 0 ) {
            JOptionPane.showMessageDialog(sif, "Please choose SGE installation directory", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if ( !location.startsWith(sharedDir) ) {
            int choice = JOptionPane.showConfirmDialog(sif, "Seems you have changed the SGE location.\nNote: SGE has to be installed in a shared directory\nwhich can be accessed by all the nodes.\n\nAre you sure you want to set SGE directory to " + location + " ?"  , "Warning", JOptionPane.YES_NO_OPTION);
            if ( choice != JOptionPane.YES_OPTION )
                return false;
        }

        String cluster = sgeClusterField.getText().trim();

        if ( cluster.length() == 0 ) {
            JOptionPane.showMessageDialog(sif, "Please choose a SGE cluster name", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String spoolDir = sgeSpoolDirField.getText().trim();

        if ( spoolDir.length() == 0 ) {
            JOptionPane.showMessageDialog(sif, "Please choose SGE Spool directory", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String admin = null;
        if ( sgeAdminCheckbox.isSelected() ) {
            admin = sgeAdminField.getText().trim();

            if ( admin.length() == 0 ) {
                JOptionPane.showMessageDialog(sif, "The \"SGE Admin username\" field is required when installing SGE with custom admin", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if ( !checkUserExists(admin) )
                return false;

            if ( admin.equals("root") )
                admin = null;
        }


        if ( new File(location).exists() ) {
            int choice = JOptionPane.showConfirmDialog(sif, "The directory " + location + " already exists.\nAre you sure that you want to overwrite it with a new installation ? " , "Warning", JOptionPane.YES_NO_OPTION);
            if ( choice != JOptionPane.YES_OPTION )
                return false;
        }

        if ( new File(spoolDir).exists() ) {
            int choice = JOptionPane.showConfirmDialog(sif, "The directory " + spoolDir + " already exists.\nAre you sure that you want to overwrite it with a new installation ? " , "Warning", JOptionPane.YES_NO_OPTION);
            if ( choice != JOptionPane.YES_OPTION )
                return false;
        }
        
        if ( !spoolDir.startsWith(sharedDir) ) {
            int choice = JOptionPane.showConfirmDialog(sif, "Seems you have changed the SGE Spool Directory location.\nNote: SGE Spool Directory should be in a shared directory\nwhich can be accessed by all the nodes.\n\nAre you sure you want to set SGE Spool directory to " + spoolDir + " ?"  , "Warning", JOptionPane.YES_NO_OPTION);
            if ( choice != JOptionPane.YES_OPTION )
                return false;
        }

        String execHosts = sgeExecHostsArea.getText().trim();

        if ( execHosts.length() == 0 ) {
            int option = JOptionPane.showConfirmDialog(sif, "No executable hosts have been specified.\nExecution hosts are required for SGE.\n\nDo you want SGE to be configured to use only the local host\nas its single execution host ? ", "Error", JOptionPane.YES_NO_OPTION);
            
            if ( option == JOptionPane.YES_OPTION ) { 
                execHosts = hostname;
                sgeExecHostsArea.setText(hostname);
            } else {
                return false;
            }
        }
        
        HostValidator hv = new HostValidator(execHosts);
        hv.start();

        return false;
    }

    private boolean checkUserExists(String user) {
        // Check SGE user
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

        hostname = Configuration.hostname;

        for ( Component c : getComponents())
            remove(c);
        initComponents();
        initLayout();
        initListeners();

        if ( !needsSGE )
           sif.redirect();
        
        populateData();

        sif.setNextEnabled(true);

    }


    private class HostValidator extends Thread {
        String execHosts;

        public HostValidator(String execHosts) {
            this.execHosts = execHosts;
        }


        private void finished(String msg, String title) {
            sgeExecHostsExampleScrollPane.setVisible(true);
            validatingHostsLabel.setText("");
            validatingHostsLabel.setVisible(false);
            sif.setNextEnabled(true);
            sif.setBackEnabled(true);

            if ( msg != null && title != null )
                JOptionPane.showMessageDialog(sif, msg, title, JOptionPane.ERROR_MESSAGE);

        }

        @Override
        public void run() {
         String [] hosts = execHosts.split(",");

         int numHosts = hosts.length;

         int currentHost = 0;

         sgeExecHostsExampleScrollPane.setVisible(false);
         validatingHostsLabel.setVisible(true);
         sif.setNextEnabled(false);
         sif.setBackEnabled(false);

         List<String> checkedHosts = new LinkedList<String>();
         for ( String host : hosts ) {
            host = host.trim();
            
            if ( checkedHosts.contains(host) ) {
                finished("The host " + host + " has been entered more than\n" +
                         "once. Each entry line has to be unique.\n", "Duplicate execution hosts found");
                return;
            }

            if ( host.equals(hostname)) {
                checkedHosts.add(host);
                continue;
             }


            validatingHostsLabel.setText("Validating hosts..." + (currentHost * 100 / numHosts) + "%");

            Process p = null;
            try {
                p = Runtime.getRuntime().exec("install_files/checkHost.sh " + host);

                p.waitFor();

                if ( p.exitValue() != 0 ) {
                    finished("Please make sure that the host " + host + "\n" +
                             "      - exists AND\n" +
                             "      - accepts ssh connections AND\n" +
                             "      - allows this host to log in without specifying password.",
                             "Invalid host found " + host);
                    return;
                }

            } catch ( Exception ex ) {
                ex.printStackTrace();
            } finally{
                if ( p != null )
                    NativeCalls.releaseProcess(p);
            }

            checkedHosts.add(host);
            currentHost++;
            validatingHostsLabel.setText("Validating hosts..." + (currentHost * 100 / numHosts) + "%");

         }


         sgeExecHostsExampleScrollPane.setVisible(true);
         validatingHostsLabel.setText("");
         validatingHostsLabel.setVisible(false);
         sif.setNextEnabled(true);
         sif.setBackEnabled(true);

         hostValidationPassed = true;
         sif.nextButtonAction();

        }



    }

}

