
package pipelineserverinstaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Holds constants
 * @author Zhizhong Liu
 */
public class Constants {
    
    /**
     * the name of the operating system as returned from the JVM
     */
    public static final String OSName = System.getProperty("os.name");
    /**
     * the version string of the operating system
     */
    public static final String OSVersion = System.getProperty("os.version");
    /**
     * the name of the user
     */
    public static final String UserName = System.getProperty("user.name");
    /**
     * true if the program is running under OS X, false otherwise
     */
    public static final boolean isOSX       = OSName.equalsIgnoreCase("Mac OS X");
    /**
     * true if the program is running under OS X 10.4, false otherwise
     */
    public static final boolean isTiger     = isOSX && OSVersion.startsWith("10.4");
    /**
     * true if the program is running under OS X 10.5 or 10.6, false otherwise
     */
    public static final boolean isLeopard   = isOSX && (OSVersion.startsWith("10.5") || OSVersion.startsWith("10.6"));
    /**
     * true if the program is running under Linux, false otherwise
     */
    public static final boolean isLinux     = OSName.equalsIgnoreCase("Linux");
    /**
     * true if the program is running under Sun Solaris (SunOS), false otherwise
     */
    public static final boolean isSolaris   = OSName.equalsIgnoreCase("SunOS");
    /**
     * true if the program is running under Windows Vista, false otherwise
     */
    public static final boolean isVista     = OSName.equalsIgnoreCase("Windows Vista");

    /**
     * true if the program is running under Windows Seven, false otherwise
     */
    public static final boolean isWinSeven  = OSName.equalsIgnoreCase("Windows 7");
    /**
     * true if the program is running under Windows, false otherwise
     */
    public static final boolean isWindows   = !(isOSX || isLinux || isSolaris);

    public static final String serverJavaPath = "/usr/local/java/bin/java";

    public static final int majorReleaseNumber = 2;
    public static final int minorReleaseNumber = 0;
    public static final int pointReleaseNumber = 2;

    
    /**
     * Installer version string. Update as needed for any new release.
     */
    public static final String installerVersion = majorReleaseNumber+"."+minorReleaseNumber+"."+pointReleaseNumber + "";

    public static final int serverDefaultPort = 8001;

    /**
     * Program copyright text
     */
    public static final String copyrightText = "2012 Laboratory of Neuro Imaging";

    public static String javaHomeDir() {
        return System.getProperty("java.home") + File.separator + "bin";
    }

    public static String currentDir() {
        return System.getProperty("user.dir");
    }

    public static String pipelineHome(String plUser, String plPort){
        StringBuilder homePath = new StringBuilder();

         // By getting the owner of the preferences file we can get the Pipeline user.
        Process p = null;
        try {
            String s = "";

            if ( !plUser.equals("root") ) {
                String [] cmd = {"/bin/sh","-c","echo ~" + plUser};
                p = Runtime.getRuntime().exec(cmd);

                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                String [] tokens = null;
                
                s = stdInput.readLine();

                stdInput.close();
            }
            homePath.append(s);
        } catch ( Exception ex ) {
            ex.printStackTrace();
        } finally {
            if ( p != null )
                NativeCalls.releaseProcess(p);
        }


        homePath.append(File.separator);

        if (isOSX) //In OSX pipeline home directory should be in "~/Library/Preferences/"
            homePath.append("Library" + File.separator + "Preferences");
        else if (isVista || isWinSeven) //In Windows Vista or Seven pipeline home directory should be in "$HOME\AppData\Local\LONI\"
            homePath.append("AppData" + File.separator + "Local" + File.separator + "LONI");
        else if (isWindows) //In pipeline home directory should be in "$HOME\Application Data\LONI\"
            homePath.append("Application Data" + File.separator + "LONI" + File.separator);

        // Unix home directory should be "~/.pipeline/"
        if (isLinux || isSolaris) {
            homePath.append(".pipeline"); // Add dot before pipeline directory name to make it hidden
        }
        else {
            homePath.append(File.separator);
            homePath.append("Pipeline");
        }

        homePath.append(File.separator);
        homePath.append( plPort );


        homePath.append(File.separator);

        return homePath.toString();
    }

    public static final int CHECKSUM_ALG_MD_2       = 20;
    public static final int CHECKSUM_ALG_MD_5       = 21;
    public static final int CHECKSUM_ALG_SHA_1      = 22;
    public static final int CHECKSUM_ALG_SHA_256    = 23;
    public static final int CHECKSUM_ALG_SHA_384    = 24;
    public static final int CHECKSUM_ALG_SHA_512    = 25;


    public static String getHelpURL(Object o) {
        String url = "http://users.loni.ucla.edu/~pipelnv4/help/redirect.php?subject=" + o.getClass().getSimpleName();
        return url;
    }

    public static String getHelpURLString(String s) {
        String url = "http://users.loni.ucla.edu/~pipelnv4/help/redirect.php?subject=" + s;
        return url;
    }
}
