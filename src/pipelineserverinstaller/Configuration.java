
package pipelineserverinstaller;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Petros Petrosyan
 */
public class Configuration {
    private static Map<String,String> configMap = new HashMap<String,String>();

    public static void setConfig( String configName, String value ) {
        configMap.put(configName, value);
    }

    public static String getConfig(String configName) {
        return configMap.get(configName);
    }

    public static String hostname;


    public static final String CONFIG_INSTALL_PIPELINE       = "INSTALL_PIPELINE";
    public static final String CONFIG_INSTALL_SGE            = "INSTALL_SGE";
    public static final String CONFIG_INSTALL_NI_TOOLS       = "INSTALL_NI_TOOLS";
    public static final String CONFIG_INSTALL_BI_TOOLS       = "INSTALL_BI_TOOLS";
    public static final String CONFIG_INSTALL_NI_EXECUTABLES = "INSTALL_NI_EXECUTABLES";
    public static final String CONFIG_INSTALL_NI_SERVERLIB   = "INSTALL_NI_SERVERLIB";
    public static final String CONFIG_INSTALL_BI_EXECUTABLES = "INSTALL_BI_EXECUTABLES";
    public static final String CONFIG_INSTALL_BI_SERVERLIB   = "INSTALL_BI_SERVERLIB";
    public static final String CONFIG_INSTALL_SUPERUSER      = "INSTALL_SUPERUSER";

    public static final String CONFIG_INSTALL_AFNI          = "INSTALL_AFNI";
    public static final String CONFIG_INSTALL_AIR           = "INSTALL_AIR";
    public static final String CONFIG_INSTALL_BRAINSUITE    = "INSTALL_BRAINSUITE";
    public static final String CONFIG_INSTALL_FSL           = "INSTALL_FSL";
    public static final String CONFIG_INSTALL_FREESURFER    = "INSTALL_FREESURFER";
    public static final String CONFIG_INSTALL_LONITOOLS     = "INSTALL_LONITOOLS";
    public static final String CONFIG_INSTALL_MINC          = "INSTALL_MINC";
    public static final String CONFIG_INSTALL_ITK           = "INSTALL_ITK";
    public static final String CONFIG_INSTALL_DTK           = "INSTALL_DTK";
    public static final String CONFIG_INSTALL_GAMMA         = "INSTALL_GAMMA";

    public static final String CONFIG_INSTALL_EMBOSS        = "INSTALL_EMBOSS";
    public static final String CONFIG_INSTALL_PICARD        = "INSTALL_PICARD";
    public static final String CONFIG_INSTALL_MSA           = "INSTALL_MSA";
    public static final String CONFIG_INSTALL_BATWING       = "INSTALL_BATWING";
    public static final String CONFIG_INSTALL_BAYESASS      = "INSTALL_BAYESASS";
    public static final String CONFIG_INSTALL_FORMATOMATIC  = "INSTALL_FORMATOMATIC";
    public static final String CONFIG_INSTALL_GENEPOP       = "INSTALL_GENEPOP";
    public static final String CONFIG_INSTALL_MIGRATE       = "INSTALL_MIGRATE";
    public static final String CONFIG_INSTALL_GWASS         = "INSTALL_GWASS";
    public static final String CONFIG_INSTALL_MRFAST        = "INSTALL_MRFAST";
    public static final String CONFIG_INSTALL_BOWTIE        = "INSTALL_BOWTIE";
    public static final String CONFIG_INSTALL_SAMTOOLS      = "INSTALL_SAMTOOLS";
    public static final String CONFIG_INSTALL_PLINK         = "INSTALL_PLINK";
    public static final String CONFIG_INSTALL_MAQ           = "INSTALL_MAQ";
    public static final String CONFIG_INSTALL_MIBLAST       = "INSTALL_MIBLAST";    
    
    public static final String CONFIG_SGE_ROOT              = "SGE_ROOT";
    public static final String CONFIG_SGE_CLUSTER           = "SGE_CLUSTER";
    public static final String CONFIG_SGE_SUBMIT_HOSTS      = "SGE_SUBMITHOSTS";
    public static final String CONFIG_SGE_EXEC_HOSTS        = "SGE_EXECHOSTS";
    public static final String CONFIG_SGE_ADMIN_USER        = "SGE_ADMIN_USER";
    public static final String CONFIG_SGE_ADMIN_HOSTS       = "SGE_ADMINHOSTS";
    public static final String CONFIG_SGE_SPOOL_DIR         = "SGE_SPOOLDIR";
    public static final String CONFIG_SGE_CONFIG_QUEUE      = "SGE_CONFIGQUEUE";
    public static final String CONFIG_SGE_QUEUE_NAME        = "SGE_QUEUENAME";
    public static final String CONFIG_SGE_QUEUE_HOSTLIST    = "SGE_QUEUEHOSTLIST";
    public static final String CONFIG_SGE_QUEUE_SLOTS       = "SGE_QUEUESLOTS";

    public static final String CONFIG_PIPELINE_PLUGIN           = "PL_PLUGIN";
    public static final String CONFIG_PIPELINE_QUEUE            = "PL_QUEUE";
    public static final String CONFIG_PIPELINE_HOSTNAME         = "PL_HOSTNAME";
    public static final String CONFIG_PIPELINE_PORT             = "PL_PORT";
    public static final String CONFIG_PIPELINE_USER             = "PL_USER";
    public static final String CONFIG_PIPELINE_TEMPDIR          = "PL_TEMPDIR";
    public static final String CONFIG_PIPELINE_SCRATCHDIR       = "PL_SCRATCHDIR";
    public static final String CONFIG_PIPELINE_SERVERLIB        = "PL_SERVERLIB";
    public static final String CONFIG_PIPELINE_USEPRIVESC       = "PL_USEPRIVESC";
    public static final String CONFIG_PIPELINE_LOCATION         = "PL_LOCATION";
    public static final String CONFIG_PIPELINE_START_ON_STARTUP = "PL_START_ON_STARTUP";
    public static final String CONFIG_PIPELINE_USER_AUTH        = "PL_USER_AUTH";
    public static final String CONFIG_PIPELINE_MEMORY_ALLOCATION = "PL_MEMORY_ALLOC";

    public static final String CONFIG_TOOLS_AFNI_VERSION            = "AFNI_VERSION";
    public static final String CONFIG_TOOLS_AIR_VERSION             = "AIR_VERSION";
    public static final String CONFIG_TOOLS_BRAINSUITE_VERSION      = "BRAINSUITE_VERSION";
    public static final String CONFIG_TOOLS_FSL_VERSION             = "FSL_VERSION";
    public static final String CONFIG_TOOLS_FREESURFER_VERSION      = "FREESURFER_VERSION";
    public static final String CONFIG_TOOLS_LONITOOLS_VERSION       = "LONITOOLS_VERSION";
    public static final String CONFIG_TOOLS_MINC_VERSION            = "MINC_VERSION";
    public static final String CONFIG_TOOLS_GAMMA_VERSION           = "GAMMA_VERSION";
    public static final String CONFIG_TOOLS_ITK_VERSION             = "ITK_VERSION";
    public static final String CONFIG_TOOLS_DTK_VERSION             = "DTK_VERSION";

    public static final String CONFIG_TOOLS_EMBOSS_VERSION        = "EMBOSS_VERSION";
    public static final String CONFIG_TOOLS_PICARD_VERSION        = "PICARD_VERSION";
    public static final String CONFIG_TOOLS_MSA_VERSION           = "MSA_VERSION";
    public static final String CONFIG_TOOLS_BATWING_VERSION       = "BATWING_VERSION";
    public static final String CONFIG_TOOLS_BAYESASS_VERSION      = "BAYESASS_VERSION";
    public static final String CONFIG_TOOLS_FORMATOMATIC_VERSION  = "FORMATOMATIC_VERSION";
    public static final String CONFIG_TOOLS_GENEPOP_VERSION       = "GENEPOP_VERSION";
    public static final String CONFIG_TOOLS_MIGRATE_VERSION       = "MIGRATE_VERSION";
    public static final String CONFIG_TOOLS_GWASS_VERSION         = "GWASS_VERSION";
    public static final String CONFIG_TOOLS_MRFAST_VERSION        = "MRFAST_VERSION";
    public static final String CONFIG_TOOLS_BOWTIE_VERSION        = "BOWTIE_VERSION";
    public static final String CONFIG_TOOLS_SAMTOOLS_VERSION      = "SAMTOOLS_VERSION";
    public static final String CONFIG_TOOLS_PLINK_VERSION         = "PLINK_VERSION";
    public static final String CONFIG_TOOLS_MAQ_VERSION           = "MAQ_VERSION";
    public static final String CONFIG_TOOLS_MIBLAST_VERSION       = "MIBLAST_VERSION";
    
    public static final String CONFIG_SUPERUSER_LIST                = "SUPERUSER_LIST";
    
    public static final String CONFIG_SHARED_FILESYSTEM_PATH        = "SHARED_FILESYSTEM_PATH";
    public static final String CONFIG_TOOLS_PATH                    = "TOOLS_PATH";

    public static final String CONFIG_INSTALLER_MODE_INSTALL        = "INSTALLER_MODE_INSTALL  ";
    public static final String CONFIG_INSTALLER_MODE_MANUAL_TOOL    = "INSTALLER_MODE_MANUAL_TOOL";
    public static final String CONFIG_MANUAL_ACTION_NEEDED_FOR      = "MANUAL_ACTION_NEEDED_FOR";

    public static final String CONFIG_MANUALTOOL_NAME               = "MANUALTOOL_NAME";
    public static final String CONFIG_MANUALTOOL_VERSION            = "MANUALTOOL_VERSION";
    public static final String CONFIG_MANUALTOOL_ARCHIVE_PATH       = "MANUALTOOL_ARCHIVE_PATH";
    public static final String CONFIG_MANUALTOOL_LICENSE_PATH       = "MANUALTOOL_LICENSE_PATH";

    public static final String CONFIG_JDK_BINARY_LOCATION           = "JDK_BINARY_LOCATION";
    public static final String CONFIG_FSL_ARCHIVE_LOCATION          = "FSL_ARCHIVE_LOCATION";
    public static final String CONFIG_FREESURFER_ARCHIVE_LOCATION   = "FREESURFER_ARCHIVE_LOCATION";
    public static final String CONFIG_FREESURFER_LICENSE_LOCATION   = "FREESURFER_LICENSE_LOCATION";
    public static final String CONFIG_DTK_ARCHIVE_LOCATION          = "DTK_ARCHIVE_LOCATION";
    public static final String CONFIG_BRAINSUITE_ARCHIVE_LOCATION   = "BRAINSUITE_ARCHIVE_LOCATION";
    
    public static final String CONFIG_PIPELINEDB_LOCATION           = "PLDB_LOCATION";

    public static final String CONFIGURE_PIPELINE_SERVER            = "CONFIGURE_PL_SERVER";
    public static final String START_PIPELINE_SERVER                = "START_PL_SERVER";
    public static final String START_PIPELINE_CLIENT                = "START_PL_CLIENT";
    public static final String START_PIPELINE_CLIENT_ARGUMENTS      = "START_PL_CLIENT_ARGUMENTS";

    public static final String SharedFileSystemLocation = "SharedFileSystemPath";
    public static final String PipelineServerConf = "PipelineServer";
    public static final String PipelineInstallationDirectory = "InstallLocation";
    public static final String PipelineServerHostname = "Hostname";
    public static final String PipelineServerPort = "Port";
    public static final String PipelineUser = "Username";
    public static final String PipelineTempDirectory = "TempDir";    
    public static final String PipelineScratchDirectory = "ScratchDir";
    public static final String GridSubmission = "GridSubmission";
    public static final String GridPlugin = "GridPlugin";
    public static final String SubmissionQueue = "GridSubmissionQueue";
    public static final String PrivilegeEscalation = "UsePrivilegeEscalation";
    public static final String DatabaseLocation = "DBInstallLocation";    
    public static final String LaunchPipelineOnStartup = "StartPipelineOnSystemStartup";
    public static final String PipelineAuthentication = "AuthenticationModule";
    public static final String ModifySudoers = "ModifySudoers";
    public static final String SudoerExceptions = "SuperUsers";
    public static final String MemoryAllocation = "MemoryAllocation";
    public static final String JDKArchiveLocation = "JDKLocation";
    public static final String GridEngineConf = "SGE";
    public static final String SGERoot = "SGERoot";
    public static final String SGECluster = "SGECluster";
    public static final String SubmitHosts = "SubmitHosts";
    public static final String Host = "Host";
    public static final String ExecutionHosts = "ExecHosts";
    public static final String AdminHosts = "AdminHosts";    
    public static final String AdminUsername = "AdminUsername";    
    public static final String SpoolDir = "SpoolDir";    
    public static final String QueueConf = "Queue";    
    public static final String QueueName = "Name";    
    public static final String QueueHosts = "Hosts";    
    public static final String QueueSlots = "Slots";
    public static final String ToolsConf = "Tools";
    public static final String NeuroImagingToolsConf = "NeuroImagingTools";
    public static final String InstallAFNI = "AFNI";
    public static final String InstallAIR = "AIR";
    public static final String InstallBrainSuite = "BrainSuite";
    public static final String InstallFSL = "FSL";
    public static final String ArchiveLocation = "ArchiveLocation";
    public static final String InstallFreeSurfer = "FreeSurfer";
    public static final String LicenseLocation = "LicenseLocation";    
    public static final String InstallLONI = "LONI";
    public static final String InstallMINC = "MINC";
    public static final String InstallGAMMA = "GAMMA";
    public static final String InstallITK = "ITK";
    public static final String InstallDTK = "DTK";
    public static final String InstallBioinformaticsTools = "BioinformaticsTools";    
    public static final String InstallEMBOSS = "EMBOSS";
    public static final String InstallPicard = "Picard";
    public static final String InstallMSA = "MSA";    
    public static final String InstallBATWING = "BATWING";
    public static final String InstallBayesAss = "BayesAss";
    public static final String InstallFormatomatics = "Formatomatics";
    public static final String InstallGENEPOP = "GENEPOP";
    public static final String InstallMigrate = "Migrate";
    public static final String InstallMrFAST = "MrFAST";
    public static final String InstallGWASS = "GWASS";
    public static final String InstallBowtie = "Bowtie";
    public static final String InstallSamTools = "SamTools";
    public static final String InstallPLINK = "PLINK";
    public static final String InstallMAQ = "MAQ";
    public static final String InstallMiBLAST = "MiBLAST";
    public static final String[] AutomaticInstallationConfigurationTags = {SharedFileSystemLocation,
        PipelineServerConf, JDKArchiveLocation, GridEngineConf, ToolsConf};
    public static final String[] PipelineServerConfigurationTags = {PipelineInstallationDirectory,
        PipelineServerHostname, PipelineServerPort, PipelineUser, PipelineScratchDirectory,
        GridSubmission, PrivilegeEscalation, DatabaseLocation, LaunchPipelineOnStartup, PipelineAuthentication,
        ModifySudoers, MemoryAllocation};
    public static final String[] GridSubmissionConfigurationTags = {GridPlugin, SubmissionQueue};
    public static final String ModifySudoersConfigurationTag = SudoerExceptions;
    public static final String[] GridEngineConfigurationTags = {SGERoot, SGECluster, SubmitHosts,
        ExecutionHosts, AdminHosts, AdminUsername, SpoolDir, QueueConf};
    public static final String HostConfigurationTag = Host;
    public static final String[] QueueConfigurationTags = {QueueName, QueueHosts, QueueSlots};
    public static final String[] ToolsConfigurationTags = {NeuroImagingToolsConf, InstallBioinformaticsTools};
    public static final String[] NeuroImagingToolsConfigurationTags = {InstallAFNI, InstallAIR,
        InstallBrainSuite, InstallFSL, InstallFreeSurfer, InstallLONI, InstallMINC, InstallGAMMA,
        InstallITK, InstallDTK};
    public static final String[] ManualToolConfigurationTags = {ArchiveLocation, LicenseLocation};
    public static final String[] BioinformaticsToolsConfigurationTags = {InstallEMBOSS, InstallPicard, 
        InstallBATWING, InstallBayesAss, InstallFormatomatics, InstallGENEPOP, InstallMigrate, 
        InstallMrFAST, InstallGWASS, InstallBowtie, InstallSamTools, InstallPLINK, InstallMAQ,
        InstallMiBLAST};
    
    public static final String enabledAttribute = "enabled";
    public static final String configuredAttribute = "configure";
    public static final String pathAttribute = "path";
    public static final String executablesAttribute = "executables";
    public static final String serverLibAttribute = "serverLib";

    public static String calculateTotalSize(List<String> sizeList) {
        double sizeKB = 0;

        for ( String strSize : sizeList ) {
            String [] tokens = strSize.split(" ");

            if ( tokens[1].toLowerCase().equals("mb") ) {
                sizeKB+= Double.valueOf(tokens[0]) * 1024.0;
            } else if (tokens[1].toLowerCase().equals("gb")) {
                sizeKB+= Double.valueOf(tokens[0]) * 1024.0 * 1024.0;
            } else if (tokens[1].toLowerCase().equals("kb") ) {
                sizeKB+= Double.valueOf(tokens[0]);
            }
        }

        StringBuilder totalSizeBuilder = new StringBuilder("");

        DecimalFormat df = new DecimalFormat("#.##");

        if ( sizeKB < 1024.0 ) {
            totalSizeBuilder.append(df.format(sizeKB));
            totalSizeBuilder.append(" KB");
        }
        else if ( sizeKB < 1024.0 * 1024.0 ) {
            totalSizeBuilder.append(df.format(sizeKB / 1024.0));
            totalSizeBuilder.append(" MB");
        }
        else {
            totalSizeBuilder.append(df.format(sizeKB / ( 1024.0 * 1024.0)));
            totalSizeBuilder.append(" GB");
        }

        return totalSizeBuilder.toString();
    }

    public static String getToolsSize(String domain) {
        List<String> sizeList = new LinkedList<String>();

        // so far, the two domains are 'neuroimaging' and 'bioinformatics'
        if (domain.equals("neuroimaging")) {
            sizeList.add(SIZE_TOOLS_AFNI);
            sizeList.add(SIZE_TOOLS_AIR);
            sizeList.add(SIZE_TOOLS_BRAINSUITE);
            sizeList.add(SIZE_TOOLS_FSL);
            sizeList.add(SIZE_TOOLS_FREESURFER);
            sizeList.add(SIZE_TOOLS_LONI);
            sizeList.add(SIZE_TOOLS_MINC);
            sizeList.add(SIZE_TOOLS_GAMMA);
            sizeList.add(SIZE_TOOLS_ITK);
            sizeList.add(SIZE_TOOLS_DTK);
        } else if (domain.equals("bioinformatics")){
            sizeList.add(SIZE_TOOLS_PICARD);
            sizeList.add(SIZE_TOOLS_BATWING);
            sizeList.add(SIZE_TOOLS_BAYESASS);
            sizeList.add(SIZE_TOOLS_BOWTIE);
            sizeList.add(SIZE_TOOLS_EMBOSS);
            sizeList.add(SIZE_TOOLS_FORMATOMATIC);
            sizeList.add(SIZE_TOOLS_GENEPOP);
            sizeList.add(SIZE_TOOLS_GWASS);
            sizeList.add(SIZE_TOOLS_MAQ);
            sizeList.add(SIZE_TOOLS_MIGRATE);
            sizeList.add(SIZE_TOOLS_MRFAST);
            sizeList.add(SIZE_TOOLS_MSA);
            sizeList.add(SIZE_TOOLS_PLINK);            
            sizeList.add(SIZE_TOOLS_SAMTOOLS);            
            sizeList.add(SIZE_TOOLS_MIBLAST);            
        }

        return calculateTotalSize(sizeList);
    }
    

    public static final String SIZE_SGE                = "20 MB";
    public static final String SIZE_PIPELINE           = "32.3 MB";
    public static final String SIZE_NI_TOOLS           = "5.88 GB";
    public static final String SIZE_BI_TOOLS           = "430 MB";

    public static final String SIZE_TOOLS_AFNI         = "221.4 MB";
    public static final String SIZE_TOOLS_AIR          = "32.8 MB";
    public static final String SIZE_TOOLS_BRAINSUITE   = "41.7 MB";
    public static final String SIZE_TOOLS_FSL          = "1.32 GB";
    public static final String SIZE_TOOLS_FREESURFER   = "2 GB";
    public static final String SIZE_TOOLS_LONI         = "1.9 GB";
    public static final String SIZE_TOOLS_MINC         = "102 MB";
    public static final String SIZE_TOOLS_GAMMA        = "3.9 MB";
    public static final String SIZE_TOOLS_ITK          = "107 MB";
    public static final String SIZE_TOOLS_DTK          = "54.9 MB";
    
    public static final String SIZE_TOOLS_PICARD        = "9.6 MB";
    public static final String SIZE_TOOLS_BATWING       = "0.4 MB";
    public static final String SIZE_TOOLS_BAYESASS      = "1.7 MB";
    public static final String SIZE_TOOLS_BOWTIE        = "76 MB";
    public static final String SIZE_TOOLS_EMBOSS        = "4.0 MB";
    public static final String SIZE_TOOLS_FORMATOMATIC  = "0.1 MB";
    public static final String SIZE_TOOLS_GENEPOP       = "0.2 MB";
    public static final String SIZE_TOOLS_GWASS         = "1.2 MB";
    public static final String SIZE_TOOLS_MAQ           = "174 MB";
    public static final String SIZE_TOOLS_MIGRATE       = "1.0 MB";
    public static final String SIZE_TOOLS_MRFAST        = "0.5 MB";
    public static final String SIZE_TOOLS_MSA           = "0.5 MB";
    public static final String SIZE_TOOLS_PLINK         = "12.8 MB";
    public static final String SIZE_TOOLS_SAMTOOLS      = "0.4 MB";
    public static final String SIZE_TOOLS_MIBLAST       = "192 MB";    

}
