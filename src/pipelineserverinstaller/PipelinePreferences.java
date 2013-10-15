

package pipelineserverinstaller;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author Petros Petrosyan
 */
public class PipelinePreferences {
    /** Creates a new instance of PipelinePreferences
     * @param preferencesLocation the location of the preferences xml file
     */
    protected PipelinePreferences(String preferencesLocation) {
        if(preferencesLocation == null){
            throw new NullPointerException();
        }

        prefsFile = new File(preferencesLocation);
        if (prefsFile.exists() && prefsFile.isFile()) {
            try {
                SAXBuilder builder = new SAXBuilder();
                document = builder.build(prefsFile);
                root = document.getRootElement();
            } catch (Exception e) {
                root = new Element("preferences");
                document = new Document(root);
            }
        } else
            prefsFile = null;

        // create the default values map
        defaultValues = new HashMap<String, String>();
        // populate it with all the values


        // General
        defaultValues.put(KEY_ServerPort, "");
        defaultValues.put(KEY_Hostname, "");

        // Server Library
        defaultValues.put(KEY_ServerLibraryLocation, "");
        defaultValues.put(KEY_ServerLibrarySameDirMonitor, "");
        defaultValues.put(KEY_ServerLibraryMonitorFile, "");
    }


    /**
     * obtains the preferences singleton, but if it needs to be constructed,
     * it will load it from the preferences file specified as the method argument
     * @param fileLocation the location of the alternative preference file that
     * will be used if the singleton hasn't been instantiated
     * @return the <code>PipelinePreferences</code> singleton
     */
    public static PipelinePreferences getPreferencesFromFile(String fileLocation){
        if(preferences == null){
            synchronized (PipelinePreferences.class){
                if(preferences == null){
                    preferences = new PipelinePreferences(fileLocation);
                    serverPort = preferences.getPref(KEY_ServerPort);
                }
            }
        }

        return preferences;
    }

    public static void releasePreferences() {
        preferences = null;
    }


    /**
     * gets the value of the preference specified by the supplied key. If there is no
     * value specified, the method will return the default value of the specified
     * preference
     * @param prefKey the preference key
     * @return the value of the preference specified by the supplied key
     */
    public String getPref(String prefKey) {
        Element prefElement = root.getChild(prefKey);
        if (prefElement == null)
            return defaultValues.get(prefKey);

        return prefElement.getValue();
    }

    


    public static String getServerPort() {
        return serverPort;
    }

    public static File getPrefsFile() {
        return prefsFile;
    }
    /* the file that the preferences gets written to */
    private static File prefsFile;
    /* the document that holds the XML preferences */
    private Document document;
    /* the root element of the document that holds the XML preferences */
    private Element root;

    /* static stuff */
    private static PipelinePreferences preferences;
    private static String serverPort = Integer.toString(Constants.serverDefaultPort);

    /* preference keys */
    public static String KEY_ServerPort                         =   "ServerPort";
    public static String KEY_Hostname                           =   "Hostname";
    public static String KEY_ServerLibraryLocation              =   "ServerLibraryLocation";
    public static String KEY_ServerLibrarySameDirMonitor        =   "ServerLibrarySameDirMonitor";
    public static String KEY_ServerLibraryMonitorFile           =   "ServerLibraryMonitorFile";

    /* default preference values */
    private Map<String, String> defaultValues;
}
