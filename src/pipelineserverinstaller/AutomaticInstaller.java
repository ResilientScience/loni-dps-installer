package pipelineserverinstaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import pipelineserverinstaller.gui.panels.InstallPanel;

/**
 *
 * @author azamanyan
 */
public class AutomaticInstaller {

    private Document document;
    private Element root;

    public AutomaticInstaller(String configFileLocation) {
        File configFile = new File(configFileLocation);
        if (configFile.exists() && configFile.isFile()) {
            try {
                SAXBuilder builder = new SAXBuilder();
                document = builder.build(configFile);
                root = document.getRootElement();
            } catch (Exception e) {
                e.printStackTrace();
                document = new Document(root);
            }
        }
    }

    public void run() {
        // extract the configuration preferences from the user-specified file
        extractPreferences();

        // run the installation
        InstallPanel installPanel = new InstallPanel(true);
        installPanel.panelActivated();
    }

    public void extractPreferences() {
        try {
            System.out.println("Checking network configuration");
            Configuration.hostname = InetAddress.getLocalHost().getHostName();
            if (Configuration.hostname.equals("localhost") || Configuration.hostname.startsWith("127.0.")) {
                System.err.println("It is not supported for a Grid Engine installation that the local hostname\n"
                        + "is \"localhost\" and/or the IP address is like \"127.0.x.x\"\n\n"
                        + "The hostname of this computer is " + Configuration.hostname + "\n\n"
                        + "After you fix the hostname make sure to LOG OFF from the system\n"
                        + "and then log back in and start the installer. This will update some\n "
                        + "configurations in the system needed to prevent future errors for \n"
                        + "installing SGE and Pipeline");
                System.exit(6);
            }
        } catch (UnknownHostException ex) {
            System.out.println("Checking network configuration...FAILED");
            System.err.println(ex.getMessage());

            System.err.println("The installer was not able to get current host's hostname.\n"
                    + "Please make sure that the networking of current host is properly configured\n"
                    + "Running \"/etc/init.d/network restart\" may fix the problem.\n\n"
                    + "Press OK to exit the installer");
            System.exit(6);
        }

        List<String> prefList = Arrays.asList(Configuration.AutomaticInstallationConfigurationTags);
        for (String pref : prefList) {
            
            Element configElement = root.getChild(pref);
            if (configElement != null) {
                String configValue = configElement.getValue();

                if (pref.equals(Configuration.SharedFileSystemLocation)) {
                    if (configValue == null || configValue.trim().equals("")) {
                        System.out.println("Shared File System Location is missing; installation will proceed with default value of /usr/local");
                        Configuration.setConfig(Configuration.CONFIG_SHARED_FILESYSTEM_PATH, "/usr/local");
                    }
                    Configuration.setConfig(Configuration.CONFIG_SHARED_FILESYSTEM_PATH, configValue);
                }
                if (pref.equals(Configuration.JDKArchiveLocation)) {
                    if (configValue == null || configValue.trim().equals("")) {
                        System.err.println("Checking Java version");
                        try {
                            Process javaCheckProcess = Runtime.getRuntime().exec("java -version 2>&1 | grep 'Java(TM)' | wc -l");
                            BufferedReader stdInput = new BufferedReader(new InputStreamReader(javaCheckProcess.getInputStream()));
                            String result = stdInput.readLine();
                            if (!result.trim().equals("1")){
                                System.out.println("You need to install Oracle JDK in order to proceed.\n"
                                        + "Please visit http://www.oracle.com/technetwork/java/javase/downloads/index.html"
                                        + "1. Click on Download JDK button."
                                        + "2. Select Platform: Linux x64 or x86 and click Continue ( username, password is optional )"
                                        + "3. Click on jdk-[ver]-linux-[platform].rpm link to begin download.");
                                System.exit(1);
                            }
                            javaCheckProcess.waitFor();
                        } catch (Exception ex) {
                            System.err.println("Checking Java version...FAILED");
                            System.exit(1);
                        }
                    }
                    Configuration.setConfig(Configuration.CONFIG_JDK_BINARY_LOCATION, configValue);
                }
                if (pref.equals(Configuration.PipelineServerConf)) {
                    String enabled = configElement.getAttributeValue(Configuration.enabledAttribute);
                    if (enabled == null || enabled.trim().equals("true")) {
                        Configuration.setConfig(Configuration.CONFIG_INSTALL_PIPELINE, "true");
                        List<String> pipelinePrefList = Arrays.asList(Configuration.PipelineServerConfigurationTags);
                        for (String pipelinePref : pipelinePrefList) {
                            Element pipelineConfigElement = configElement.getChild(pipelinePref);
                            String pipelineConfigValue;
                            if (pipelinePref.equals(Configuration.PipelineInstallationDirectory)) {
                                if (pipelineConfigElement != null) {
                                    pipelineConfigValue = pipelineConfigElement.getValue();
                                    if (pipelineConfigValue == null || pipelineConfigValue.trim().equals("")) {
                                        System.out.println("Pipeline installation directory is missing; installation will proceed with default value of /usr/pipeline");
                                        pipelineConfigValue = "/usr/pipeline";
                                    }
                                } else {
                                    System.out.println("Pipeline installation directory is missing; installation will proceed with default value of /usr/pipeline");
                                    pipelineConfigValue = "/usr/pipeline";
                                }
                                Configuration.setConfig(Configuration.CONFIG_PIPELINE_LOCATION, pipelineConfigValue);
                                Configuration.setConfig(Configuration.CONFIG_PIPELINE_SERVERLIB, pipelineConfigValue + "/serverLibrary");
                            }
                            if (pipelinePref.equals(Configuration.PipelineServerHostname)) {
                                if (pipelineConfigElement != null) {
                                    pipelineConfigValue = pipelineConfigElement.getValue();
                                    if (pipelineConfigValue == null || pipelineConfigValue.trim().equals("")) {
                                        System.out.println("Pipeline server hostname is missing; installation will proceed with value of " + Configuration.hostname);
                                        pipelineConfigValue = Configuration.hostname;
                                    }
                                } else {
                                    System.out.println("Pipeline server hostname is missing; installation will proceed with value of " + Configuration.hostname);
                                    pipelineConfigValue = Configuration.hostname;
                                }
                                Configuration.setConfig(Configuration.CONFIG_PIPELINE_HOSTNAME, pipelineConfigValue);
                            }
                            if (pipelinePref.equals(Configuration.PipelineServerPort)) {
                                if (pipelineConfigElement != null) {
                                    pipelineConfigValue = pipelineConfigElement.getValue();
                                    if (pipelineConfigValue == null || pipelineConfigValue.trim().equals("")) {
                                        System.out.println("Pipeline server port is missing; installation will proceed with default value of 8001");
                                        pipelineConfigValue = "8001";
                                    }
                                } else {
                                    System.out.println("Pipeline server port is missing; installation will proceed with default value of 8001");
                                    pipelineConfigValue = "8001";
                                }
                                Configuration.setConfig(Configuration.CONFIG_PIPELINE_PORT, pipelineConfigValue);
                            }
                            if (pipelinePref.equals(Configuration.PipelineUser)) {
                                if (pipelineConfigElement != null) {
                                    pipelineConfigValue = pipelineConfigElement.getValue();
                                    if (pipelineConfigValue == null || pipelineConfigValue.trim().equals("")) {
                                        System.err.println("Please specify a valid username for the Pipeline user (<Username> tag) and restart the installer");
                                        System.exit(1);
                                    }
                                    // check that the user is not root
                                    if (pipelineConfigValue.trim().equals("root")) {
                                        System.err.println("User root is not allowed. Please try another user.");
                                        System.exit(1);
                                    }

                                    // check that the user exists
                                    Process p = null;
                                    try {
                                        p = Runtime.getRuntime().exec("id " + pipelineConfigValue);
                                        p.waitFor();
                                        int exitValue = p.exitValue();

                                        if (exitValue != 0) {
                                            System.err.println("Invalid Pipeline user - " + pipelineConfigValue + "\nPlease make sure the user exists in the system\nor change the username. Then restart the installer.");
                                            System.exit(1);
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    } finally {
                                        if (p != null) {
                                            NativeCalls.releaseProcess(p);
                                        }
                                    }
                                    Configuration.setConfig(Configuration.CONFIG_PIPELINE_USER, pipelineConfigValue);
                                } else {
                                    System.err.println("Please specify a valid username for the Pipeline user (<Username> tag) and restart the installer");
                                    System.exit(1);
                                }
                            }
                            if (pipelinePref.equals(Configuration.PipelineTempDirectory)) {
                                String defaultCacheDir = Configuration.getConfig(Configuration.CONFIG_SHARED_FILESYSTEM_PATH) + "pipelineCache";
                                if (pipelineConfigElement != null) {
                                    pipelineConfigValue = pipelineConfigElement.getValue();
                                    if (pipelineConfigValue == null || pipelineConfigValue.trim().equals("")) {
                                        System.out.println("Pipeline temp directory is missing; installation will proceed with default value of " + defaultCacheDir);
                                        pipelineConfigValue = defaultCacheDir;
                                    }
                                } else {
                                    System.out.println("Pipeline temp directory is missing; installation will proceed with default value of " + defaultCacheDir);
                                    pipelineConfigValue = defaultCacheDir;
                                }
                                Configuration.setConfig(Configuration.CONFIG_PIPELINE_TEMPDIR, pipelineConfigValue);
                            }
                            if (pipelinePref.equals(Configuration.PipelineScratchDirectory)) {
                                String defaultScratchDir = Configuration.getConfig(Configuration.CONFIG_SHARED_FILESYSTEM_PATH) + "pipelineScratch";
                                if (pipelineConfigElement != null) {
                                    pipelineConfigValue = pipelineConfigElement.getValue();
                                    if (pipelineConfigValue == null || pipelineConfigValue.trim().equals("")) {
                                        System.out.println("Pipeline scratch directory is missing; installation will proceed with default value of " + defaultScratchDir);
                                        pipelineConfigValue = defaultScratchDir;
                                    }
                                } else {
                                    System.out.println("Pipeline scratch directory is missing; installation will proceed with default value of " + defaultScratchDir);
                                    pipelineConfigValue = defaultScratchDir;
                                }
                                Configuration.setConfig(Configuration.CONFIG_PIPELINE_SCRATCHDIR, pipelineConfigValue);
                            }
                            if (pipelinePref.equals(Configuration.GridSubmission)) {
                                String gridSubmissionEnabled = configElement.getAttributeValue(Configuration.enabledAttribute);
                                if (gridSubmissionEnabled == null || gridSubmissionEnabled.trim().equals("true")) {
                                    List<String> gridSubmissionPrefList = Arrays.asList(Configuration.GridSubmissionConfigurationTags);
                                    for (String gridSubmissionPref : gridSubmissionPrefList) {
                                        Element gridSubmissionConfigElement = pipelineConfigElement.getChild(gridSubmissionPref);
                                        String gridSubmissionConfigValue;
                                        if (gridSubmissionPref.equals(Configuration.GridPlugin)) {
                                            if (gridSubmissionConfigElement != null) {
                                                gridSubmissionConfigValue = gridSubmissionConfigElement.getValue();
                                                if (gridSubmissionConfigValue == null || gridSubmissionConfigValue.trim().equals("")) {
                                                    System.out.println("Pipeline grid plugin is missing; installation will proceed with default value of JGDI");
                                                    gridSubmissionConfigValue = "JGDI";
                                                }
                                            } else {
                                                System.out.println("Pipeline grid plugin is missing; installation will proceed with default value of JGDI");
                                                gridSubmissionConfigValue = "JGDI";
                                            }
                                            Configuration.setConfig(Configuration.CONFIG_PIPELINE_PLUGIN, gridSubmissionConfigValue);
                                        }
                                        if (gridSubmissionPref.equals(Configuration.SubmissionQueue)) {
                                            if (gridSubmissionConfigElement != null) {
                                                gridSubmissionConfigValue = gridSubmissionConfigElement.getValue();
                                                if (gridSubmissionConfigValue == null || gridSubmissionConfigValue.trim().equals("")) {
                                                    System.out.println("Pipeline grid submission queue is missing; installation will proceed with default value of pipeline.q");
                                                    gridSubmissionConfigValue = "pipeline.q";
                                                }
                                            } else {
                                                System.out.println("Pipeline grid submission queue is missing; installation will proceed with default value of pipeline.q");
                                                gridSubmissionConfigValue = "pipeline.q";
                                            }
                                            Configuration.setConfig(Configuration.CONFIG_PIPELINE_QUEUE, gridSubmissionConfigValue);
                                        }

                                    }
                                }
                            }
                            if (pipelinePref.equals(Configuration.PrivilegeEscalation)) {
                                if (pipelineConfigElement != null) {
                                    pipelineConfigValue = pipelineConfigElement.getValue();
                                    if (pipelineConfigValue == null || pipelineConfigValue.trim().equals("")) {
                                        System.out.println("Privilege escalation value is missing; installation will proceed with default value of false");
                                        pipelineConfigValue = "false";
                                    }
                                } else {
                                    System.out.println("Privilege escalation value is missing; installation will proceed with default value of false");
                                    pipelineConfigValue = "false";
                                }
                                Configuration.setConfig(Configuration.CONFIG_PIPELINE_USEPRIVESC, pipelineConfigValue);
                            }
                            if (pipelinePref.equals(Configuration.DatabaseLocation)) {
                                if (pipelineConfigElement != null) {
                                    pipelineConfigValue = pipelineConfigElement.getValue();
                                    if (pipelineConfigValue == null || pipelineConfigValue.trim().equals("")) {
                                        System.out.println("Pipeline database location is missing; installation will proceed with default value of /usr/pipeline/db");
                                        pipelineConfigValue = "/usr/pipeline/db";
                                    }
                                } else {
                                    System.out.println("Pipeline database location is missing; installation will proceed with default value of /usr/pipeline/db");
                                    pipelineConfigValue = "/usr/pipeline/db";
                                }
                                if (new File(pipelineConfigValue).exists()) {
                                    System.err.println("Database directory " + pipelineConfigValue + " exists; it will be overwritten");
                                }
                                Configuration.setConfig(Configuration.CONFIG_PIPELINEDB_LOCATION, pipelineConfigValue);
                            }
                            if (pipelinePref.equals(Configuration.LaunchPipelineOnStartup)) {
                                if (pipelineConfigElement != null) {
                                    pipelineConfigValue = pipelineConfigElement.getValue();
                                    if (pipelineConfigValue == null || pipelineConfigValue.trim().equals("")) {
                                        System.out.println("Start Pipeline on system startup value is missing; installation will proceed with default value of false");
                                        pipelineConfigValue = "false";
                                    }
                                } else {
                                    System.out.println("Start Pipeline on system startup value is missing; installation will proceed with default value of false");
                                    pipelineConfigValue = "false";
                                }
                                Configuration.setConfig(Configuration.CONFIG_PIPELINE_START_ON_STARTUP, pipelineConfigValue);
                            }
                            if (pipelinePref.equals(Configuration.PipelineAuthentication)) {
                                if (pipelineConfigElement != null) {
                                    pipelineConfigValue = pipelineConfigElement.getValue();
                                    if (pipelineConfigValue == null || pipelineConfigValue.trim().equals("")) {
                                        System.out.println("Pipeline authentication module is missing; installation will proceed with default value of SSH");
                                        pipelineConfigValue = "SSH";
                                    }
                                } else {
                                    System.out.println("Pipeline authentication module is missing; installation will proceed with default value of SSH");
                                    pipelineConfigValue = "SSH";
                                }
                                Configuration.setConfig(Configuration.CONFIG_PIPELINE_USER_AUTH, pipelineConfigValue);
                            }
                            if (pipelinePref.equals(Configuration.ModifySudoers)) {
                                String modifySudoersEnabled = pipelineConfigElement.getAttributeValue(Configuration.enabledAttribute);
                                if (modifySudoersEnabled == null || modifySudoersEnabled.trim().equals("true")) {
                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_SUPERUSER, "true");
                                    Element sudoersConfigElement = pipelineConfigElement.getChild(Configuration.ModifySudoersConfigurationTag);
                                    String sudoersConfigValue;
                                    if (sudoersConfigElement != null) {
                                        sudoersConfigValue = sudoersConfigElement.getValue();
                                        if (sudoersConfigValue == null || sudoersConfigValue.trim().equals("")) {
                                            System.out.println("Super user list is missing; installation will proceed with default value of root");
                                            sudoersConfigValue = "root";
                                        }
                                    } else {
                                        System.out.println("Super user list is missing; installation will proceed with default value of root");
                                        sudoersConfigValue = "root";
                                    }
                                    Configuration.setConfig(Configuration.CONFIG_SUPERUSER_LIST, sudoersConfigValue);
                                }
                            }
                            if (pipelinePref.equals(Configuration.MemoryAllocation)) {
                                if (pipelineConfigElement != null) {
                                    pipelineConfigValue = pipelineConfigElement.getValue();
                                    if (pipelineConfigValue == null || pipelineConfigValue.trim().equals("")) {
                                        System.out.println("Memory allocation is missing; installation will proceed with default value of 2048");
                                        pipelineConfigValue = "2048";
                                    }
                                } else {
                                    System.out.println("Memory allocation is missing; installation will proceed with default value of 2048");
                                    pipelineConfigValue = "2048";
                                }
                                Configuration.setConfig(Configuration.CONFIG_PIPELINE_MEMORY_ALLOCATION, pipelineConfigValue);
                            }
                        }
                    }
                }
                if (pref.equals(Configuration.GridEngineConf)) {
                    String gridEngineEnabled = configElement.getAttributeValue(Configuration.enabledAttribute);
                    if (gridEngineEnabled == null || gridEngineEnabled.trim().equals("true")) {
                        Configuration.setConfig(Configuration.CONFIG_INSTALL_SGE, "true");
                        List<String> gridPrefList = Arrays.asList(Configuration.GridEngineConfigurationTags);
                        for (String gridPref : gridPrefList) {
                            Element gridConfigElement = configElement.getChild(gridPref);
                            String gridConfigValue;
                            if (gridPref.equals(Configuration.SGERoot)) {
                                if (gridConfigElement != null) {
                                    gridConfigValue = gridConfigElement.getValue();
                                    if (gridConfigValue == null || gridConfigValue.trim().equals("")) {
                                        System.out.println("SGE root is missing; installation will proceed with default value of /usr/local/sge");
                                        gridConfigValue = "/usr/local/sge";
                                    }
                                } else {
                                    System.out.println("SGE root is missing; installation will proceed with default value of /usr/local/sge");
                                    gridConfigValue = "/usr/local/sge";
                                }
                                if (new File(gridConfigValue).exists()) {
                                    System.err.println("SGE directory " + gridConfigValue + " exists; it will be overwritten");
                                }

                                Configuration.setConfig(Configuration.CONFIG_SGE_ROOT, gridConfigValue);
                            }
                            if (gridPref.equals(Configuration.SGECluster)) {
                                if (gridConfigElement != null) {
                                    gridConfigValue = gridConfigElement.getValue();
                                    if (gridConfigValue == null || gridConfigValue.trim().equals("")) {
                                        System.out.println("SGE cluster is missing; installation will proceed with default value of cluster");
                                        gridConfigValue = "cluster";
                                    }
                                } else {
                                    System.out.println("SGE cluster is missing; installation will proceed with default value of cluster");
                                    gridConfigValue = "cluster";
                                }
                                Configuration.setConfig(Configuration.CONFIG_SGE_CLUSTER, gridConfigValue);
                            }
                            if (gridPref.equals(Configuration.SubmitHosts)) {
                                if (gridConfigElement != null) {
                                    List<Element> submitHostElements = gridConfigElement.getChildren(Configuration.Host);
                                    String commaSeparatedSubmitList = "";
                                    for (Element submitHostElement : submitHostElements) {
                                        String submitHost = submitHostElement.getValue();
                                        if (submitHost != null && !submitHost.trim().equals("")) {
                                            commaSeparatedSubmitList = commaSeparatedSubmitList + submitHost + ",";
                                        }
                                    }
                                    if (!commaSeparatedSubmitList.trim().equals("")) {
                                        commaSeparatedSubmitList = commaSeparatedSubmitList.substring(0, commaSeparatedSubmitList.length() - 1);
                                        Configuration.setConfig(Configuration.CONFIG_SGE_SUBMIT_HOSTS, commaSeparatedSubmitList);
                                    } else {
                                        Configuration.setConfig(Configuration.CONFIG_SGE_SUBMIT_HOSTS, Configuration.hostname);
                                    }
                                } else {
                                    Configuration.setConfig(Configuration.CONFIG_SGE_SUBMIT_HOSTS, Configuration.hostname);
                                }
                            }
                            if (gridPref.equals(Configuration.ExecutionHosts)) {
                                if (gridConfigElement != null) {
                                    List<Element> executionHostElements = gridConfigElement.getChildren(Configuration.Host);
                                    String commaSeparatedExecutionList = "";
                                    for (Element executionHostElement : executionHostElements) {
                                        String execHost = executionHostElement.getValue();
                                        if (execHost != null && !execHost.trim().equals("")) {
                                            commaSeparatedExecutionList = commaSeparatedExecutionList + execHost + ",";
                                        }
                                    }
                                    if (!commaSeparatedExecutionList.trim().equals("")) {
                                        commaSeparatedExecutionList = commaSeparatedExecutionList.substring(0, commaSeparatedExecutionList.length() - 1);
                                        Configuration.setConfig(Configuration.CONFIG_SGE_EXEC_HOSTS, commaSeparatedExecutionList);
                                    } else {
                                        Configuration.setConfig(Configuration.CONFIG_SGE_EXEC_HOSTS, Configuration.hostname);
                                    }
                                } else {
                                    Configuration.setConfig(Configuration.CONFIG_SGE_EXEC_HOSTS, Configuration.hostname);
                                }
                            }
                            if (gridPref.equals(Configuration.AdminHosts)) {
                                if (gridConfigElement != null) {
                                    List<Element> adminHostElements = gridConfigElement.getChildren(Configuration.Host);
                                    String commaSeparatedAdminList = "";
                                    for (Element adminHostElement : adminHostElements) {
                                        String adminHost = adminHostElement.getValue();
                                        if (adminHost != null && !adminHost.trim().equals("")) {
                                            commaSeparatedAdminList = commaSeparatedAdminList + adminHost + ",";
                                        }
                                    }
                                    if (!commaSeparatedAdminList.trim().equals("")) {
                                        commaSeparatedAdminList = commaSeparatedAdminList.substring(0, commaSeparatedAdminList.length() - 1);
                                        Configuration.setConfig(Configuration.CONFIG_SGE_ADMIN_HOSTS, commaSeparatedAdminList);
                                    } else {
                                        Configuration.setConfig(Configuration.CONFIG_SGE_ADMIN_HOSTS, Configuration.hostname);
                                    }
                                } else {
                                    Configuration.setConfig(Configuration.CONFIG_SGE_ADMIN_HOSTS, Configuration.hostname);
                                }
                            }
                            if (gridPref.equals(Configuration.AdminUsername)) {
                                if (gridConfigElement != null) {
                                    gridConfigValue = gridConfigElement.getValue();

                                    if (gridConfigValue == null || gridConfigValue.trim().equals("")) {
                                        System.err.println("Please specify a valid username for the Pipeline user (<Username> tag) and restart the installer");
                                        System.exit(1);
                                    }
                                    
                                    // check that the user exists
                                    Process p = null;
                                    try {
                                        p = Runtime.getRuntime().exec("id " + gridConfigValue);
                                        p.waitFor();
                                        int exitValue = p.exitValue();

                                        if (exitValue != 0) {
                                            System.err.println("Invalid SGE admin username - " + gridConfigValue + "\nPlease make sure the user exists in the system\nor change the username. Then restart the installer.");
                                            System.exit(1);
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    } finally {
                                        if (p != null) {
                                            NativeCalls.releaseProcess(p);
                                        }
                                    }
                                    Configuration.setConfig(Configuration.CONFIG_SGE_ADMIN_USER, gridConfigValue);

                                } else {
                                    System.err.println("Please specify a valid username for the Pipeline user (<Username> tag) and restart the installer");
                                    System.exit(1);
                                }
                            }
                            if (gridPref.equals(Configuration.SpoolDir)) {
                                if (gridConfigElement != null) {
                                    gridConfigValue = gridConfigElement.getValue();

                                    if (gridConfigValue == null || gridConfigValue.trim().equals("")) {
                                        System.out.println("SGE spool directory is missing; installation will proceed with default value of /usr/local/sge/spool");
                                        gridConfigValue = "/usr/local/sge/spool";
                                    }
                                } else {
                                    System.out.println("SGE spool directory is missing; installation will proceed with default value of /usr/local/sge/spool");
                                    gridConfigValue = "/usr/local/sge/spool";
                                }
                                if (new File(gridConfigValue).exists()) {
                                    System.err.println("Spool directory " + gridConfigValue + " exists; it will be overwritten");
                                }
                                
                                Configuration.setConfig(Configuration.CONFIG_SGE_SPOOL_DIR, gridConfigValue);
                            }
                            if (gridPref.equals(Configuration.QueueConf)) {
                                String queueConfigure = gridConfigElement.getAttributeValue(Configuration.configuredAttribute);
                                if (queueConfigure == null || queueConfigure.trim().equals("true")) {
                                    Configuration.setConfig(Configuration.CONFIG_SGE_CONFIG_QUEUE, "true");
                                    List<String> queuePrefList = Arrays.asList(Configuration.QueueConfigurationTags);
                                    for (String queuePref : queuePrefList) {
                                        Element queueConfigElement = gridConfigElement.getChild(queuePref);
                                        String queueConfigValue;
                                        if (queuePref.equals(Configuration.QueueName)) {
                                            if (queueConfigElement != null) {
                                                queueConfigValue = queueConfigElement.getValue();
                                                if (queueConfigValue == null || queueConfigValue.trim().equals("")) {
                                                    System.out.println("SGE queue name is missing; installation will proceed with default value of pipeline.q");
                                                    queueConfigValue = "pipeline.q";
                                                }
                                            } else {
                                                System.out.println("SGE queue name is missing; installation will proceed with default value of pipeline.q");
                                                queueConfigValue = "pipeline.q";
                                            }
                                            Configuration.setConfig(Configuration.CONFIG_SGE_QUEUE_NAME, queueConfigValue);
                                        }
                                        if (queuePref.equals(Configuration.QueueHosts)) {
                                            if (queueConfigElement != null) {
                                                List<Element> queueHostElements = queueConfigElement.getChildren(Configuration.Host);
                                                String commaSeparatedQueueHostList = "";
                                                for (Element queueHostElement : queueHostElements) {
                                                    String execHost = queueHostElement.getValue();
                                                    if (execHost != null && !execHost.trim().equals("")) {
                                                        commaSeparatedQueueHostList = commaSeparatedQueueHostList + execHost + ",";
                                                    }
                                                }
                                                if (!commaSeparatedQueueHostList.trim().equals("")) {
                                                    commaSeparatedQueueHostList = commaSeparatedQueueHostList.substring(0, commaSeparatedQueueHostList.length() - 1);
                                                    Configuration.setConfig(Configuration.CONFIG_SGE_QUEUE_HOSTLIST, commaSeparatedQueueHostList);
                                                } else {
                                                    Configuration.setConfig(Configuration.CONFIG_SGE_QUEUE_HOSTLIST, Configuration.hostname);
                                                }
                                            } else {
                                                Configuration.setConfig(Configuration.CONFIG_SGE_QUEUE_HOSTLIST, Configuration.hostname);
                                            }
                                        }
                                        if (queuePref.equals(Configuration.QueueSlots)) {
                                            if (queueConfigElement != null) {
                                                List<Element> queueSlotElements = gridConfigElement.getChildren(Configuration.Host);
                                                String commaSeparatedQueueSlotList = "";
                                                for (Element queueSlotElement : queueSlotElements) {
                                                    String execHost = queueSlotElement.getValue();
                                                    if (execHost != null && !execHost.trim().equals("")) {
                                                        commaSeparatedQueueSlotList = commaSeparatedQueueSlotList + execHost + ",";
                                                    }
                                                }
                                                if (!commaSeparatedQueueSlotList.trim().equals("")) {
                                                    commaSeparatedQueueSlotList = commaSeparatedQueueSlotList.substring(0, commaSeparatedQueueSlotList.length() - 1);
                                                    Configuration.setConfig(Configuration.CONFIG_SGE_QUEUE_SLOTS, commaSeparatedQueueSlotList);
                                                } else {
                                                    Configuration.setConfig(Configuration.CONFIG_SGE_QUEUE_SLOTS, Configuration.hostname);
                                                }
                                            } else {
                                                Configuration.setConfig(Configuration.CONFIG_SGE_QUEUE_SLOTS, Configuration.hostname);
                                            }
                                        }

                                    }
                                }
                            }

                        }
                    }
                }
                if (pref.equals(Configuration.ToolsConf)) {
                    String toolsPath = configElement.getAttributeValue(Configuration.pathAttribute);
                    if (toolsPath == null) {
                        Configuration.setConfig(Configuration.CONFIG_TOOLS_PATH, "/usr/local/tools");
                    } else {
                        Configuration.setConfig(Configuration.CONFIG_TOOLS_PATH, toolsPath);                        
                    }
                    String toolsEnabled = configElement.getAttributeValue(Configuration.enabledAttribute);
                    if (toolsEnabled == null || toolsEnabled.trim().equals("true")) {
                        List<String> toolPrefList = Arrays.asList(Configuration.ToolsConfigurationTags);
                        for (String toolPref : toolPrefList) {
                            Element toolConfigElement = configElement.getChild(toolPref);
                            if (toolPref.equals(Configuration.NeuroImagingToolsConf)) {
                                if (toolConfigElement != null) {
                                    String neuroimagingExecsEnabled = toolConfigElement.getAttributeValue(Configuration.enabledAttribute);
                                    if (neuroimagingExecsEnabled == null || neuroimagingExecsEnabled.trim().equals("true")) {
                                        Configuration.setConfig(Configuration.CONFIG_INSTALL_NI_EXECUTABLES, "true");
                                    } else {
                                        Configuration.setConfig(Configuration.CONFIG_INSTALL_NI_EXECUTABLES, "false");
                                    }
                                    String neuroimagingPipesEnabled = toolConfigElement.getAttributeValue(Configuration.enabledAttribute);
                                    if (neuroimagingPipesEnabled == null || neuroimagingPipesEnabled.trim().equals("true")) {
                                        Configuration.setConfig(Configuration.CONFIG_INSTALL_NI_SERVERLIB, "true");
                                    } else {
                                        Configuration.setConfig(Configuration.CONFIG_INSTALL_NI_SERVERLIB, "false");
                                    }
                                    String neuroimagingToolsEnabled = toolConfigElement.getAttributeValue(Configuration.enabledAttribute);
                                    if (neuroimagingToolsEnabled == null || neuroimagingToolsEnabled.trim().equals("true")) {
                                        Configuration.setConfig(Configuration.CONFIG_INSTALL_NI_TOOLS, "true");
                                        List<String> neuroimagingToolPrefList = Arrays.asList(Configuration.NeuroImagingToolsConfigurationTags);
                                        for (String neuroimagingToolPref : neuroimagingToolPrefList) {
                                            Element neuroimagingToolConfigElement = toolConfigElement.getChild(neuroimagingToolPref);
                                            if (neuroimagingToolConfigElement != null) {
                                                String neuroimagingToolEnabled = toolConfigElement.getAttributeValue(Configuration.enabledAttribute);
                                                if (neuroimagingToolEnabled == null || neuroimagingToolEnabled.trim().equals("true")) {
                                                    neuroimagingToolEnabled = "true";
                                                } else {
                                                    neuroimagingToolEnabled = "false";
                                                }
                                                if (neuroimagingToolPref.equals(Configuration.InstallAFNI)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_AFNI, neuroimagingToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_AFNI_VERSION, CONFIG_TOOLS_AFNI_VERSION);
                                                }
                                                if (neuroimagingToolPref.equals(Configuration.InstallAIR)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_AIR, neuroimagingToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_AIR_VERSION, CONFIG_TOOLS_AIR_VERSION);
                                                }
                                                if (neuroimagingToolPref.equals(Configuration.InstallBrainSuite)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_BRAINSUITE, neuroimagingToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_BRAINSUITE_VERSION, CONFIG_TOOLS_BRAINSUITE_VERSION);
                                                }
                                                if (neuroimagingToolPref.equals(Configuration.InstallFSL)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_FSL, neuroimagingToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_FSL_VERSION, CONFIG_TOOLS_FSL_VERSION);
                                                }
                                                if (neuroimagingToolPref.equals(Configuration.InstallFreeSurfer)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_FREESURFER, neuroimagingToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_FREESURFER_VERSION, CONFIG_TOOLS_FREESURFER_VERSION);
                                                }
                                                if (neuroimagingToolPref.equals(Configuration.InstallLONI)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_LONITOOLS, neuroimagingToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_LONITOOLS_VERSION, CONFIG_TOOLS_LONITOOLS_VERSION);
                                                }
                                                if (neuroimagingToolPref.equals(Configuration.InstallMINC)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_MINC, neuroimagingToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_MINC_VERSION, CONFIG_TOOLS_MINC_VERSION);
                                                }
                                                if (neuroimagingToolPref.equals(Configuration.InstallGAMMA)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_ITK, neuroimagingToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_ITK_VERSION, CONFIG_TOOLS_ITK_VERSION);
                                                }
                                                if (neuroimagingToolPref.equals(Configuration.InstallITK)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_DTK, neuroimagingToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_DTK_VERSION, CONFIG_TOOLS_DTK_VERSION);
                                                }
                                                if (neuroimagingToolPref.equals(Configuration.InstallDTK)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_GAMMA, neuroimagingToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_GAMMA_VERSION, CONFIG_TOOLS_GAMMA_VERSION);
                                                }
                                            }
                                        }
                                    } else {
                                        Configuration.setConfig(Configuration.CONFIG_INSTALL_NI_TOOLS, "false");
                                    }
                                } else {
                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_NI_TOOLS, "false");
                                }
                            }
                            if (toolPref.equals(Configuration.InstallBioinformaticsTools)) {
                                if (toolConfigElement != null) {
                                    String bioinformaticsExecsEnabled = toolConfigElement.getAttributeValue(Configuration.enabledAttribute);
                                    if (bioinformaticsExecsEnabled == null || bioinformaticsExecsEnabled.trim().equals("true")) {
                                        Configuration.setConfig(Configuration.CONFIG_INSTALL_BI_EXECUTABLES, "true");
                                    } else {
                                        Configuration.setConfig(Configuration.CONFIG_INSTALL_BI_EXECUTABLES, "false");
                                    }
                                    String bioinformaticsPipesEnabled = toolConfigElement.getAttributeValue(Configuration.enabledAttribute);
                                    if (bioinformaticsPipesEnabled == null || bioinformaticsPipesEnabled.trim().equals("true")) {
                                        Configuration.setConfig(Configuration.CONFIG_INSTALL_BI_SERVERLIB, "true");
                                    } else {
                                        Configuration.setConfig(Configuration.CONFIG_INSTALL_BI_SERVERLIB, "false");
                                    }
                                    String bioinformaticsToolsEnabled = toolConfigElement.getAttributeValue(Configuration.enabledAttribute);
                                    if (bioinformaticsToolsEnabled == null || bioinformaticsToolsEnabled.trim().equals("true")) {
                                        Configuration.setConfig(Configuration.CONFIG_INSTALL_BI_TOOLS, "true");
                                        List<String> bioinformaticsToolPrefList = Arrays.asList(Configuration.BioinformaticsToolsConfigurationTags);
                                        for (String bioinformaticsToolPref : bioinformaticsToolPrefList) {
                                            Element bioinformaticsToolConfigElement = toolConfigElement.getChild(bioinformaticsToolPref);
                                            if (bioinformaticsToolConfigElement != null) {
                                                String bioinformaticsToolEnabled = toolConfigElement.getAttributeValue(Configuration.enabledAttribute);
                                                if (bioinformaticsToolEnabled == null || bioinformaticsToolEnabled.trim().equals("true")) {
                                                    bioinformaticsToolEnabled = "true";
                                                } else {
                                                    bioinformaticsToolEnabled = "false";
                                                }
                                                if (bioinformaticsToolPref.equals(Configuration.InstallEMBOSS)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_EMBOSS, bioinformaticsToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_EMBOSS_VERSION, CONFIG_TOOLS_EMBOSS_VERSION);
                                                }
                                                if (bioinformaticsToolPref.equals(Configuration.InstallPicard)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_PICARD, bioinformaticsToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_PICARD_VERSION, CONFIG_TOOLS_PICARD_VERSION);
                                                }
                                                if (bioinformaticsToolPref.equals(Configuration.InstallMSA)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_MSA, bioinformaticsToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_MSA_VERSION, CONFIG_TOOLS_MSA_VERSION);
                                                }
                                                if (bioinformaticsToolPref.equals(Configuration.InstallBATWING)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_BATWING, bioinformaticsToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_BATWING_VERSION, CONFIG_TOOLS_BATWING_VERSION);
                                                }
                                                if (bioinformaticsToolPref.equals(Configuration.InstallBayesAss)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_BAYESASS, bioinformaticsToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_BAYESASS_VERSION, CONFIG_TOOLS_BAYESASS_VERSION);
                                                }
                                                if (bioinformaticsToolPref.equals(Configuration.InstallFormatomatics)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_FORMATOMATIC, bioinformaticsToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_FORMATOMATIC_VERSION, CONFIG_TOOLS_FORMATOMATIC_VERSION);
                                                }
                                                if (bioinformaticsToolPref.equals(Configuration.InstallGENEPOP)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_GENEPOP, bioinformaticsToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_GENEPOP_VERSION, CONFIG_TOOLS_GENEPOP_VERSION);
                                                }
                                                if (bioinformaticsToolPref.equals(Configuration.InstallMigrate)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_MIGRATE, bioinformaticsToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_MIGRATE_VERSION, CONFIG_TOOLS_MIGRATE_VERSION);
                                                }
                                                if (bioinformaticsToolPref.equals(Configuration.InstallGWASS)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_GWASS, bioinformaticsToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_GWASS_VERSION, CONFIG_TOOLS_GWASS_VERSION);
                                                }
                                                if (bioinformaticsToolPref.equals(Configuration.InstallMrFAST)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_MRFAST, bioinformaticsToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_MRFAST_VERSION, CONFIG_TOOLS_MRFAST_VERSION);
                                                }
                                                if (bioinformaticsToolPref.equals(Configuration.InstallBowtie)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_BOWTIE, bioinformaticsToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_BOWTIE_VERSION, CONFIG_TOOLS_BOWTIE_VERSION);
                                                }
                                                if (bioinformaticsToolPref.equals(Configuration.InstallSamTools)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_SAMTOOLS, bioinformaticsToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_SAMTOOLS_VERSION, CONFIG_TOOLS_SAMTOOLS_VERSION);
                                                }
                                                if (bioinformaticsToolPref.equals(Configuration.InstallPLINK)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_PLINK, bioinformaticsToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_PLINK_VERSION, CONFIG_TOOLS_PLINK_VERSION);
                                                }
                                                if (bioinformaticsToolPref.equals(Configuration.InstallMAQ)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_MAQ, bioinformaticsToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_MAQ_VERSION, CONFIG_TOOLS_MAQ_VERSION);
                                                }
                                                if (bioinformaticsToolPref.equals(Configuration.InstallMiBLAST)) {
                                                    Configuration.setConfig(Configuration.CONFIG_INSTALL_MIBLAST, bioinformaticsToolEnabled);
                                                    Configuration.setConfig(Configuration.CONFIG_TOOLS_MIBLAST_VERSION, CONFIG_TOOLS_MIBLAST_VERSION);
                                                }
                                            }
                                        }
                                    } else {
                                        Configuration.setConfig(Configuration.CONFIG_INSTALL_BI_TOOLS, "false");
                                    }
                                } else {
                                        Configuration.setConfig(Configuration.CONFIG_INSTALL_BI_TOOLS, "false");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // default values
    private static final String CONFIG_TOOLS_AFNI_VERSION = "2007_05_29_1644";
    private static final String CONFIG_TOOLS_AIR_VERSION = "5.2.5";
    private static final String CONFIG_TOOLS_BRAINSUITE_VERSION = "11a";
    private static final String CONFIG_TOOLS_FSL_VERSION = "4.1.9";
    private static final String CONFIG_TOOLS_FREESURFER_VERSION = "5.1.0";
    private static final String CONFIG_TOOLS_LONITOOLS_VERSION = "-";
    private static final String CONFIG_TOOLS_MINC_VERSION = "01.19.2011";
    private static final String CONFIG_TOOLS_GAMMA_VERSION = "1.1";
    private static final String CONFIG_TOOLS_ITK_VERSION = "3.2.0";
    private static final String CONFIG_TOOLS_DTK_VERSION = "0.6.2.1";
    private static final String CONFIG_TOOLS_EMBOSS_VERSION = "6.3.1";
    private static final String CONFIG_TOOLS_PICARD_VERSION = "1.43";
    private static final String CONFIG_TOOLS_MSA_VERSION = "4.05";
    private static final String CONFIG_TOOLS_BATWING_VERSION = "0.1";
    private static final String CONFIG_TOOLS_BAYESASS_VERSION = "3.0";
    private static final String CONFIG_TOOLS_FORMATOMATIC_VERSION = "0.8.1";
    private static final String CONFIG_TOOLS_GENEPOP_VERSION = "4.1";
    private static final String CONFIG_TOOLS_MIGRATE_VERSION = "3.2.7";
    private static final String CONFIG_TOOLS_GWASS_VERSION = "2.0";
    private static final String CONFIG_TOOLS_MRFAST_VERSION = "2.0.0.5";
    private static final String CONFIG_TOOLS_BOWTIE_VERSION = "0.12.7";
    private static final String CONFIG_TOOLS_SAMTOOLS_VERSION = "0.1.12a";
    private static final String CONFIG_TOOLS_PLINK_VERSION = "1.07";
    private static final String CONFIG_TOOLS_MAQ_VERSION = "0.7.1";
    private static final String CONFIG_TOOLS_MIBLAST_VERSION = "6.1";
}
