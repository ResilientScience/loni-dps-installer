
package pipelineserverinstaller.gui.panels;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import pipelineserverinstaller.Configuration;
import pipelineserverinstaller.Constants;
import pipelineserverinstaller.Download;
import pipelineserverinstaller.NativeCalls;
import pipelineserverinstaller.gui.ComponentFactory;

/**
 *
 * @author Zhizhong Liu
 */
public class IntroductionPanel extends AbstractStepPanel implements Observer  {

    // just to make the compiler stop complaining
    static final long serialVersionUID = 1L;

    private String north = SpringLayout.NORTH;
    private String south = SpringLayout.SOUTH;
    private String west = SpringLayout.WEST;
    private String east = SpringLayout.EAST;

    private final int panelMargin = 20;
    private final int dist = 5;
    private final int vDist = 8;

    ImageIcon loadingIcon = new ImageIcon(this.getClass().getResource("/graphics/Loading.gif"));

    private JLabel checkingLabel2;
    private JLabel checkingLabel;
    private JLabel introLabel;
    private JTextArea introTextArea;
    private JScrollPane introScrollPane;
    
    public IntroductionPanel() {
        super();

        initComponents();
        initLayout();
        initListeners();
    }

    private void initComponents() {
        introLabel = ComponentFactory.label("<html><font size=\"5\"><b>Introduction</b></font></html>");

        checkingLabel2 = ComponentFactory.label("Please wait...");
        checkingLabel2.setIcon(loadingIcon);
        checkingLabel = ComponentFactory.label();

        StringBuilder descriptionBuilder = new StringBuilder("<html>");


        descriptionBuilder.append("This program will install The LONI Pipeline server, Grid Engine<br/>");
        descriptionBuilder.append("and external Neuro Imaging tools on your system. <br/><br/>");

        descriptionBuilder.append("The LONI Pipeline is a free graphical workflow environment that facilitates<br/>");
        descriptionBuilder.append("the design, execution, validation and dissemination of scientific data analysis<br/>");
        descriptionBuilder.append("protocols utilized in various imaging studies and informatics applications. <br/>");
        descriptionBuilder.append("The Pipeline web-site ");
        descriptionBuilder.append("the design, execution, validation and dissemination of scientific <br/>");


        descriptionBuilder.append("</html>");


        introTextArea = ComponentFactory.textarea(2,2);
        introTextArea.setText("The LONI Pipeline (http://pipeline.loni.ucla.edu/) is a free graphical workflow environment that facilitates the design, execution, validation and dissemination of scientific data analysis protocols utilized in various imaging studies and informatics applications.\n\n" +
                              "This Distributed Pipeline Server installation allows you to build and configure 3 types of resources - backend Grid management resources (e.g., Oracle Grid Engine), the distributed Pipeline server, and a number of computational imaging and informatics software tools.\n\n" +
                              "The specific prerequisites for the Pipeline server installation include:\n\n" +
                              "       - One or more machines running CentOS Linux operating system version 5.8 or above\n" +
                              "       - OpenJDK or Oracle JDK 1.5 or above\n" +
                              "       - NFS V.3 server and network connected nodes with a shared partition\n" +
                              "       - At least 30 GB available space for full installation (including tools and data)\n" +
                              "       - Passwordless Remote Root Access to all SGE execution hosts (when installing SGE)\n" +
                              "       - A user that will run the Pipeline server process (a dedicated user is recommended)\n" +
                              "       - Static IP Addresses for all the hosts, and their hostnames properly configured on DNS\n" +
                              "       - Internet connection during the installation\n" +
                              "Please click Help button on the left bottom corner for more info.\n");


        introTextArea.setLineWrap(true);
        introTextArea.setWrapStyleWord(true);
        introTextArea.setEditable(false);
        introTextArea.setBackground(getBackground());
        introTextArea.setBorder(BorderFactory.createEmptyBorder());

        introScrollPane = new JScrollPane(introTextArea);
        introScrollPane.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        introScrollPane.setPreferredSize(new Dimension(100, 150));


        introLabel.setVisible(false);
        introScrollPane.setVisible(false);


        this.setPreferredSize(new Dimension(400, 400));
    }

    private void initLayout() {
        SpringLayout layout = new SpringLayout();
        setLayout(layout);

        add(introLabel);
        layout.putConstraint(north, introLabel, panelMargin, north, this);
        layout.putConstraint(west, introLabel, panelMargin, west, this);

        add(checkingLabel2);
        layout.putConstraint(north, checkingLabel2, 8 * panelMargin, north, introLabel);
        layout.putConstraint(west, checkingLabel2, 6 * panelMargin, west, this);

        add(checkingLabel);
        layout.putConstraint(north, checkingLabel, 9 * panelMargin, north, introLabel);
        layout.putConstraint(west, checkingLabel, 6 * panelMargin, west, this);

        add(introScrollPane);
        layout.getConstraints(introScrollPane).setConstraint(east, Spring.sum(Spring.constant(-panelMargin), layout.getConstraint(east, this)));
        layout.getConstraints(introScrollPane).setConstraint(south, Spring.sum(Spring.constant(-panelMargin), layout.getConstraint(south, this)));
        layout.putConstraint(north, introScrollPane, vDist, south, introLabel);
        layout.putConstraint(west, introScrollPane, 0, west, introLabel);
    }

    private void initListeners() {

    }

    public void saveUserInput() {

    }

    public boolean checkUserInput() {
        return true;
    }

    public void panelActivated() {
        sif.setNextEnabled(false);
        checkInternetConnection();
    }

    private void internetIsOK() {
        checkExistenceOfScripts();
        checkPreviouslyDownloadedFiles();
        checkingLabel.setVisible(false);
        checkingLabel2.setVisible(false);
        introLabel.setVisible(true);
        introScrollPane.setVisible(true);
        sif.setNextEnabled(true);
    }

    private void checkPreviouslyDownloadedFiles() {
        File checksumsFile = new File("checksums");
        File checksumsNewFile = new File("checksumsServer");
        File checksumsOldFile = new File("checksumsOld");

        Map<String, String> oldChecksums = null;
        Map<String, String> checksums = null;

        if ( checksumsOldFile.exists() ) {
            oldChecksums = loadChecksums(checksumsOldFile, Constants.CHECKSUM_ALG_SHA_256);
        }

        checksums = loadChecksums(checksumsNewFile, Constants.CHECKSUM_ALG_SHA_256);

        cleanupOldDownloadedFiles(new File(Constants.currentDir() + File.separator + "install_files"), checksums, oldChecksums);
        
        if ( checksumsFile.exists() )
            checksumsFile.delete();
        
        NativeCalls.copyFile(checksumsNewFile, checksumsFile);

        checksumsNewFile.delete();
        checksumsOldFile.delete();
    }
    
    /* 
     * This function will look and delete those files which have been previously downloaded
     * but at the moment they are old because server has a newer version of the file. It will
     * not touch incomplete files to let them to continue the download.
     */
    private void cleanupOldDownloadedFiles(File dir, Map<String,String> checksums, Map<String,String> oldChecksums) { 
        for ( File f : dir.listFiles() ) 
        {
            if ( f.isDirectory() )
                cleanupOldDownloadedFiles(f,checksums,oldChecksums);
            
            if ( checksums.containsKey(f.getName()) ) {
                checkingLabel.setText("Checking updates on the server for file " + f.getName() + "...");
                String serverChecksum = checksums.get(f.getName());

                if ( oldChecksums == null )
                    f.delete();
                else {
                    // For big files, it takes too long to calculate the checksum
                    // so we will just compare their sizes, and when the checksum value
                    // starts with [s] then its not really a checksum, it is the size of the file.
                    if ( serverChecksum.startsWith("[s]") ) {
                        try {
                            long fileSize = Long.parseLong(serverChecksum.substring(serverChecksum.indexOf("]") + 1));
                            
                            if ( fileSize != f.length() ) {
                                String oldChecksum = oldChecksums.get(f.getName());

                                if ( oldChecksum != null ) {
                                    try {
                                        long oldFileSize = Long.parseLong(oldChecksum.substring(oldChecksum.indexOf("]") + 1));

                                        // If new size is not equal to old size, then file has been changed on the server
                                        // we need to delete the file to restart the download
                                        if ( oldFileSize != fileSize ) {
                                            f.delete();
                                        }

                                    } catch ( Exception ex ) {
                                        ex.printStackTrace();
                                    }
                                } else
                                    f.delete();
                            }
                        } catch (Exception ex ) {
                            ex.printStackTrace();
                        }
                    } else {
                        String currentChecksum = NativeCalls.checksum(f.getAbsolutePath(), Constants.CHECKSUM_ALG_SHA_256);

                        // If checkums are different then either the file has been
                        // changed on the server or the previous download was incomplete.
                        // So we need to check whether the file has changed on the server
                        // by comparing old checksum with new ( if it exists )
                        if ( !serverChecksum.equals(currentChecksum)  ) {
                            String oldChecksum = oldChecksums.get(f.getName());

                            // If new checksum is not equal to old checksum, then file has been changed on the server
                            // we need to delete the file to restart the download
                            if ( oldChecksum != null ) {
                                if ( !serverChecksum.equals(oldChecksum) )
                                    f.delete();
                            }  else {
                                f.delete();
                            }
                        }

                    }
                }

            }
        }
        
    }



    private Map<String, String> loadChecksums(File file, int algorithm) {
        Map<String, String> checksums = new HashMap<String,String>();

        if ( file.exists() ) {
            try {
                FileInputStream fis =  new FileInputStream(file.getAbsolutePath());

                 // Get the object of DataInputStream
                DataInputStream in = new DataInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;

                while ((strLine = br.readLine()) != null) {
                    String tokens[] = strLine.split(" ");

                    String filename = tokens[0];

                    String checksum = null;

                    switch ( algorithm ) {
                        case Constants.CHECKSUM_ALG_MD_2: break;
                        case Constants.CHECKSUM_ALG_MD_5: checksum = tokens[2];break;
                        case Constants.CHECKSUM_ALG_SHA_1: break;
                        case Constants.CHECKSUM_ALG_SHA_256: checksum = tokens[1]; break;
                        case Constants.CHECKSUM_ALG_SHA_384: break;
                        case Constants.CHECKSUM_ALG_SHA_512: break;

                        default: throw new IllegalArgumentException("Invalid checksum algorithm specified ( " + algorithm + " )");
                    }
                    checksums.put(filename, checksum);
                }
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }
        } else {
            return null;
        }

        return checksums;
    }

    private void checkInternetConnection() {
        checkingLabel.setText("Checking network configuration...");

         try {
                Configuration.hostname = InetAddress.getLocalHost().getHostName();
            } catch ( UnknownHostException ex ) {
                checkingLabel.setText("Checking network configuration...FAILED");
                System.err.println(ex.getMessage());

                JOptionPane.showMessageDialog(sif, "The installer was not able to get current host's hostname.\n" +
                                                   "Please make sure that the networking of current host is properly configured\n" +
                                                   "Running \"/etc/init.d/network restart\" may fix the problem.\n\n" +
                                                   "Press OK to exit the installer", "Error - Failed to get the hostname of this computer", JOptionPane.ERROR_MESSAGE);
                System.exit(6);

            }




        File checksumsFile = new File("checksums");
        File checksumsOldFile = new File("checksumsOld");
        File checksumsNewFile = new File("checksumsServer");

        if ( checksumsFile.exists() ) {
            NativeCalls.copyFile(checksumsFile, checksumsOldFile);
        }

        if ( checksumsNewFile.exists())
            checksumsNewFile.delete();
        

        checkingLabel.setText("Checking internet connection...");

        try {
            Download d = new Download(new URL("http://users.loni.ucla.edu/~pipeline/dps/checksumsServer"));
            d.addObserver(this);

            if ( d.getStatus() == Download.COMPLETE ) {
                System.err.println("Invalid file ");
            }

        } catch ( MalformedURLException ex ) {
            ex.printStackTrace();
        }

    }

    public void update(Observable o, Object arg) {
        Download d = (Download)o;

        if ( d.getStatus() == Download.ERROR ) {
            checkingLabel.setText("Checking internet connection...FAILED");
             JOptionPane.showMessageDialog(sif, "Failed to connect to LONI Web Server\n\n" +
                                               "This installer needs to download some files from LONI Web Server and the\n" +
                                               "attempted connection failed. Please check your internet and relaunch the installer.\n\n" +
                                               "Press OK to exit the installer\n",
                                               "Internet connection failure",
                                               JOptionPane.ERROR_MESSAGE);
            System.exit(6);
        } else if ( d.getStatus() == Download.DOWNLOADING ) {
            checkingLabel.setText("Checking internet connection..." + (int)d.getProgress() + "%");
        } else if ( d.getStatus() == Download.COMPLETE ) {
            checkingLabel.setText("Checking internet connection...OK");
            internetIsOK();
        }

        
    }


    private void checkExistenceOfScripts() {
        checkingLabel.setText("Checking existence of required files...");
        
        String [] filesToCheck = { "install_files",
                                   "install_files" + File.separator + "installSGE.sh",
                                   "install_files" + File.separator + "installPipeline.sh",
                                   "install_files" + File.separator + "installNITools.sh",
                                   "install_files" + File.separator + "makefile",
                                   "install_files" + File.separator + "checkHost.sh",
                                   "install_files" + File.separator + "checkSGE.sh",
                                   "install_files" + File.separator + "postInstall.sh" };


        for ( String relFilePath : filesToCheck ) {
            File file = new File(Constants.currentDir() + File.separator + relFilePath);
            if ( !file.exists() ) {
                checkingLabel.setText("Checking existence of required files...FAILED");
                JOptionPane.showMessageDialog(sif, "Missing " + file.getAbsolutePath() + "\n" +
                                                   "Extracting the installer archive again may fix the problem.\n\n" +
                                                   "Press OK to exit the installer.\n",
                                                   "Installation package is incomplete",
                                                   JOptionPane.ERROR_MESSAGE);
                System.exit(6);
            }

        }

    }

}
